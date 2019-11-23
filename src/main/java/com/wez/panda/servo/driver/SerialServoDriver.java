package com.wez.panda.servo.driver;

import com.wez.panda.servo.Servo;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import processing.serial.Serial;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SerialServoDriver extends AServoDriver {
    // Angle should be between 0 and 360
    private static final int PERIOD = 360;

    private static final int SERVO_SIGNAL_RANGE = 5000;
    private static final int SERVO_SIGNAL_HALF_RANGE = SERVO_SIGNAL_RANGE / 2;

    public SerialServoDriver(Servo servo) {
        super(servo);
    }

    private AtomicInteger current = new AtomicInteger(0);

    @Override
    protected void operate(double posWithOffset) throws InterruptedException {
        int step = calculateStep(posWithOffset);
        if (step != 0) {
            drive(step);
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

    private void drive(int step) {
        Serial ser = getServo().getSerial();

        ser.write(0x50);
        ser.write((step >> 24) & 0xFF);
        ser.write((step >> 16) & 0xFF);
        ser.write(checksum(0x50, (step >> 28) & 0xFF, (step >> 24) & 0xFF));
        delay(10L);

        ser.write(0x05);
        ser.write((step >> 8) & 0xFF);
        ser.write(step & 0xFF);
        ser.write(checksum(0x50, (step >> 8) & 0xFF, step & 0xFF));
        delay(10);
    }

    private int checksum(int a, int b, int c) {
        return (a + b + c) & 0xFF;
    }

    private void delay(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace(); //NOSONAR
            Thread.currentThread().interrupt();
        }
    }
}
