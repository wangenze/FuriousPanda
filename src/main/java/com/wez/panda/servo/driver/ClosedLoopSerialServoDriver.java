package com.wez.panda.servo.driver;

import com.wez.panda.serial.VelocityController;
import com.wez.panda.servo.Servo;
import com.wez.panda.servo.Snapshot;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.MathUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.wez.panda.math.RotatingAngleInterpolator.HALF_PERIOD;
import static com.wez.panda.math.RotatingAngleInterpolator.PERIOD;
import static org.apache.commons.math3.util.FastMath.exp;
import static org.apache.commons.math3.util.FastMath.log;
import static org.apache.commons.math3.util.FastMath.max;
import static org.apache.commons.math3.util.FastMath.min;
import static org.apache.commons.math3.util.FastMath.round;

public class ClosedLoopSerialServoDriver extends AServoDriver {

    private static final double FORWARD_LOOKING_TIME_SEC = 0.5;
    private static final double ADJUST_KD_COEFFICIENT = 1.0; // not bigger than 1.0
    private static final double ADJUST_KI_COEFFICIENT = 1.0; // not bigger than 1.0
    private static final double MAX_ACCUMULATED_ERROR = 1.0; // not bigger than 1.0

    private static final int MOVING_AVG_WINDOW = 10;
    private static final double SERVO_SIGNAL_RANGE = 5000;

    private final VelocityController controller;
    private final DriverParameters parameters;
    private final double conversionRatio;

    private AtomicReference<Snapshot> actualAngle = new AtomicReference<>();
    private AtomicReference<Double> outputVa = new AtomicReference<>(0.0);
    private AtomicReference<Double> accumulatedAngleError = new AtomicReference<>(0.0);
    private DescriptiveStatistics kVas = new DescriptiveStatistics(MOVING_AVG_WINDOW);


    public ClosedLoopSerialServoDriver(Servo servo, StopWatch stopWatch, DriverParameters parameters) {
        super(servo, stopWatch);
        this.controller = new VelocityController(servo.getSerial());
        this.parameters = parameters;
        this.conversionRatio = servo.getTransmissionRatio() * SERVO_SIGNAL_RANGE;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void initialize() {
        actualAngle.set(getActualAngle());
        super.initialize();
        delay(parameters.getDelayAfterInitialization());
    }

    @Override
    protected void operate(double timeSec) throws InterruptedException {
        double va = outputVa.updateAndGet(this::calculateOutputVa);
        int signal = (int) round(va * conversionRatio / PERIOD);
        controller.send(signal);
        delay(parameters.getMinInterval());
    }

    private double calculateOutputVa(double lastOutputVa) {
        Snapshot s0 = actualAngle.get();
        Snapshot s1 = actualAngle.updateAndGet(currentPos -> getActualAngle());

        double t0 = s0.getTimeSec();
        double t1 = s1.getTimeSec();

        double a0 = s0.getValue();
        double a1 = s1.getValue();

        // Proportional
        double t2 = t1 + FORWARD_LOOKING_TIME_SEC;
        double a2 = MathUtils.reduce(getPosAfterApplyingOffset(t2), PERIOD, 0d);
        double d12 = subtractAngle(a1, a2);
        double t12 = t2 - t1;
        double idealVa12 = d12 / t12;

        // Derivative
        double t01 = t1 - t0;
        double d01 = subtractAngle(a0, a1);
        double actualVa = d01 / t01;

        double kVa = lastOutputVa / actualVa;
        kVas.addValue(Double.isFinite(kVa) ? kVa : 1.0);
        double adjustedKVa = exp(log(kVas.getMean()) * ADJUST_KD_COEFFICIENT * kVas.getN() / kVas.getWindowSize());

        // Integral
        double i1 = getPosAfterApplyingOffset(t1);
        double e1 = subtractAngle(a1, i1);
        double kI = accumulatedAngleError.updateAndGet(error -> min(MAX_ACCUMULATED_ERROR, max(error + e1 * t01, -MAX_ACCUMULATED_ERROR)));
        double adjustedKI = exp(kI * ADJUST_KI_COEFFICIENT);

        return idealVa12 * adjustedKVa * adjustedKI;
    }

    private double subtractAngle(double a1, double a2) {
        return MathUtils.reduce(a2 - a1, PERIOD, HALF_PERIOD) - HALF_PERIOD;
    }

    private Snapshot getActualAngle() {
        double timeSec = now();
        double angle = MathUtils.reduce(controller.receive() * PERIOD / conversionRatio, PERIOD, 0d);
        return new Snapshot(angle, timeSec);
    }

    private void delay(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace(); //NOSONAR
            Thread.currentThread().interrupt();
        }
    }


    public static final class Builder {

        private Servo servo;
        private StopWatch stopWatch;
        private DriverParameters parameters;


        private Builder() {
        }

        public Builder servo(Servo servo) {
            this.servo = servo;
            return this;
        }

        public Builder stopWatch(StopWatch stopWatch) {
            this.stopWatch = stopWatch;
            return this;
        }

        public Builder parameters(DriverParameters parameters) {
            this.parameters = parameters;
            return this;
        }

        public ClosedLoopSerialServoDriver build() {
            Validate.notNull(servo);
            Validate.notNull(parameters);
            return new ClosedLoopSerialServoDriver(servo, stopWatch == null ? new StopWatch() : stopWatch, parameters);
        }
    }
}
