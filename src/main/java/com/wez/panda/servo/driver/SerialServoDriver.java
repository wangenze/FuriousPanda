package com.wez.panda.servo.driver;

import com.wez.panda.serial.SerialController;
import com.wez.panda.servo.Servo;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

import java.util.concurrent.atomic.AtomicInteger;

public class SerialServoDriver extends AServoDriver {
    // Angle should be between 0 and 360
    private static final int PERIOD = 360;

    private static final int SERVO_SIGNAL_RANGE = 5000;
    private static final int SERVO_SIGNAL_HALF_RANGE = SERVO_SIGNAL_RANGE / 2;

    private final SerialController controller;

    public SerialServoDriver(Servo servo) {
        super(servo);
        this.controller = new SerialController(servo.getSerial());
    }

    private AtomicInteger current = new AtomicInteger(0);

    @Override
    protected void operate(double posWithOffset) throws InterruptedException {
        int step = calculateStep(posWithOffset);
        if (step != 0) {
            controller.send(step);
        }
    }

    private int calculateStep(double posWithOffset) {
        double angleInDegrees = MathUtils.reduce(posWithOffset, PERIOD, 0d);
        int newActual = (int) FastMath.round(angleInDegrees * SERVO_SIGNAL_RANGE / PERIOD);
        int oriActual = current.getAndSet(newActual);
        int step = newActual - oriActual;

        return -SERVO_SIGNAL_HALF_RANGE < step && step <= SERVO_SIGNAL_HALF_RANGE ?
                step : SERVO_SIGNAL_HALF_RANGE - step;
    }
}
