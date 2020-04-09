package com.wez.panda.servo.driver;

import com.wez.panda.serial.VelocityController;
import com.wez.panda.servo.Servo;
import com.wez.panda.servo.Snapshot;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.MathUtils;

import java.util.concurrent.atomic.AtomicReference;

import static com.wez.panda.math.RotatingAngleInterpolator.HALF_PERIOD;
import static com.wez.panda.math.RotatingAngleInterpolator.PERIOD;
import static org.apache.commons.math3.util.FastMath.abs;
import static org.apache.commons.math3.util.FastMath.exp;
import static org.apache.commons.math3.util.FastMath.log;
import static org.apache.commons.math3.util.FastMath.max;
import static org.apache.commons.math3.util.FastMath.min;
import static org.apache.commons.math3.util.FastMath.round;
import static org.apache.commons.math3.util.FastMath.signum;

@Log4j2
public class VelocityServoDriver extends AServoDriver {

    private static final double FORWARD_LOOKING_TIME_SEC = 0.03;
    private static final double ADJUST_KD_COEFFICIENT = 1.0; // not bigger than 1.0
    private static final double ADJUST_KI_COEFFICIENT = 0.1; // not bigger than 1.0
    private static final double MAX_ACCUMULATED_ERROR = 1.0; // not bigger than 1.0

    private static final int MOVING_AVG_WINDOW = 50;
    private static final double SERVO_SIGNAL_RANGE = 5000;
    private static final int SEC_PER_MIN = 60;

    private final VelocityController controller;
    private final DriverParameters parameters;
    private final double transmissionRatio;

    private AtomicReference<Snapshot> actualAngle = new AtomicReference<>();
    private AtomicReference<Double> outputVa = new AtomicReference<>(0.0);
    private AtomicReference<Double> accumulatedAngleError = new AtomicReference<>(0.0);
    private DescriptiveStatistics kVas = new DescriptiveStatistics(MOVING_AVG_WINDOW);


    public VelocityServoDriver(Servo servo, StopWatch stopWatch, DriverParameters parameters) {
        super(servo, stopWatch);
        this.controller = new VelocityController(servo.getSerial());
        this.parameters = parameters;
        this.transmissionRatio = servo.getTransmissionRatio();
    }

    @Override
    public void initialize() {
        log.info("Initializing Servo: {}", getServo().getName());
        actualAngle.set(getActualAngle(0.0));
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < parameters.getDelayAfterInitialization()) {
            super.initialize();
        }
        log.info("Servo initialized: {}", getServo().getName());
    }

    @Override
    protected void operate(double timeSec) throws InterruptedException {
        try {
            double va = outputVa.updateAndGet(lastOutputVa -> calculateOutputVa(lastOutputVa, timeSec));
            int signal = (int) round(va * SEC_PER_MIN / PERIOD);
            controller.send(signal);
        } catch (Exception ex) {
            log.error("Servo " + getServo().getName() + " operation failed", ex);
        }
        delay(parameters.getMinInterval());
    }

    @Override
    protected double getPosAfterApplyingOffset(double timeSec) {
        return super.getPosAfterApplyingOffset(timeSec * transmissionRatio);
    }

    private double calculateOutputVa(double lastOutputVa, double timeSec) {
        Snapshot s0 = actualAngle.get();
        Snapshot s1 = actualAngle.updateAndGet(currentPos -> getActualAngle(timeSec));

        double t0 = s0.getTimeSec();
        double t1 = s1.getTimeSec();

        double a0 = s0.getValue();
        double a1 = s1.getValue();

        // Proportional
        double t2 = t1 + parameters.getMinInterval() / 1000.0 + FORWARD_LOOKING_TIME_SEC;
        double a2 = MathUtils.reduce(getPosAfterApplyingOffset(t2), PERIOD, 0d);
        double d12 = subtractAngle(a1, a2);
        double t12 = t2 - t1;
        double idealVa12 = d12 / t12;
        log.debug("t1={}, t2={}, t12={};    a1={}, a2={}, d12={};   idealVa12={}", t1, t2, t12, a1, a2, d12, idealVa12);

        // Derivative
        double t01 = t1 - t0;
        double d01 = subtractAngle(a0, a1);
        double actualVa = d01 / t01;

        double kVa = Double.isFinite(lastOutputVa) && Double.isFinite(actualVa) && signum(lastOutputVa) * signum(actualVa) > 0 ?
                max(abs(lastOutputVa), 1.0) / max(abs(actualVa), 1.0) : 1.0;
        kVas.addValue(kVa);
        double adjustedKVa = exp(log(kVas.getMean()) * ADJUST_KD_COEFFICIENT * kVas.getN() / kVas.getWindowSize());
        log.debug("t0={}, t1={}, t01={};    a0={}, a1={}, d01={};   actualVa={}, kVa={}, adjustedKVa={}", t0, t1, t01, a0, a1, d01, actualVa, kVa, adjustedKVa);

        // Integral
        double i1 = getPosAfterApplyingOffset(t1);
        double e1 = subtractAngle(a1, i1);
        double kI = accumulatedAngleError.updateAndGet(error -> min(MAX_ACCUMULATED_ERROR, max(error + e1 * t01, -MAX_ACCUMULATED_ERROR)));
        double adjustedKI = exp(kI * ADJUST_KI_COEFFICIENT);

        log.debug("a1={}, i1={}, e1={};   kI={}, adjustedKI={}", a1, i1, e1, kI, adjustedKI);

        double va = idealVa12 * adjustedKVa * adjustedKI;

        log.debug("Time(sec): {}, ideal angle(degree): {}, actual angle(degree): {}, output speed(degree/sec): {}",
                t1, i1, a1, va);

        return va;
    }

    private double subtractAngle(double a1, double a2) {
        return MathUtils.reduce(a2 - a1, PERIOD, HALF_PERIOD) - HALF_PERIOD;
    }

    private Snapshot getActualAngle(double timeSec) {
        double angle = MathUtils.reduce(controller.readPositionFeedback() * PERIOD / SERVO_SIGNAL_RANGE, PERIOD, 0d);
        return new Snapshot(angle, timeSec);
    }
}
