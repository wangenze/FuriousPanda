package com.wez.panda.servo.driver;

import com.wez.panda.serial.SerialController;
import com.wez.panda.servo.Servo;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SerialServoDriver extends AServoDriver {

    public static final DriverParameters DEFAULT_PARAMETERS = DriverParameters.builder()
            .delayAfterInitialization(3_000L)
            .minInterval(100L)
            .build();

    // Angle should be between 0 and 360
    private static final int PERIOD = 360;

    private static final int SERVO_SIGNAL_RANGE = 5000;
    private static final int SERVO_SIGNAL_HALF_RANGE = SERVO_SIGNAL_RANGE / 2;

    private final SerialController controller;
    private final DriverParameters parameters;

    public SerialServoDriver(Servo servo, DriverParameters parameters) {
        super(servo);
        this.controller = new SerialController(servo.getSerial());
        this.parameters = parameters;
    }

    private AtomicInteger current = new AtomicInteger(0);

    @Override
    public void initialize() {
        super.initialize();
        delay(parameters.getDelayAfterInitialization());
    }

    @Override
    protected void operate(double posWithOffset) throws InterruptedException {
        int step = calculateStep(posWithOffset);
        if (step != 0) {
            controller.send(step);
        }
        delay(parameters.getMinInterval());
    }

    private int calculateStep(double posWithOffset) {
        double angleInDegrees = MathUtils.reduce(posWithOffset, PERIOD, 0d);
        int newActual = (int) FastMath.round(angleInDegrees * SERVO_SIGNAL_RANGE / PERIOD);
        int oriActual = current.getAndSet(newActual);
        int step = newActual - oriActual;

        return -SERVO_SIGNAL_HALF_RANGE < step && step <= SERVO_SIGNAL_HALF_RANGE ?
                step : SERVO_SIGNAL_HALF_RANGE - step;
    }

    private void delay(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace(); //NOSONAR
            Thread.currentThread().interrupt();
        }
    }

    @Value
    @Builder
    public static class DriverParameters {
        private final long delayAfterInitialization;
        private final long minInterval;
    }
}
