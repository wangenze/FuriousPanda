package com.wez.panda.servo.driver;

import com.wez.panda.serial.SerialController;
import com.wez.panda.servo.Servo;
import lombok.Value;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class SerialServoDriver extends AServoDriver {

    // Angle should be between 0 and 360
    private static final int PERIOD = 360;
    private static final int HALF_PERIOD = 180;

    private static final double SERVO_SIGNAL_RANGE = 5000;

    private final SerialController controller;
    private final DriverParameters parameters;
    private final double conversionRatio;

    public SerialServoDriver(Servo servo, DriverParameters parameters) {
        super(servo);
        this.controller = new SerialController(servo.getSerial());
        this.parameters = parameters;
        this.conversionRatio = servo.getTransmissionRatio() * SERVO_SIGNAL_RANGE;
    }

    private AtomicReference<Position> current = new AtomicReference<>(new Position(0, 0));

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

    private int calculateStep(final double targetAngle) {
        Position position = current.updateAndGet(currentPos -> {
            double oriAngle = MathUtils.reduce(currentPos.getPos() * PERIOD / conversionRatio, PERIOD, 0d);
            double newAngle = MathUtils.reduce(targetAngle, PERIOD, 0d);
            double angleStep = newAngle - oriAngle;
            if (angleStep <= -HALF_PERIOD) {
                angleStep += PERIOD;
            } else if (angleStep > HALF_PERIOD) {
                angleStep -= PERIOD;
            }
            int step = (int) FastMath.round(angleStep * conversionRatio / PERIOD);
            int newPos = currentPos.getPos() + step;

//            System.out.println("Ori angle: " + oriAngle);
//            System.out.println("New angle: " + newAngle);
//            System.out.println("Angle Step: " + angleStep);
//
//            System.out.println("Ori pos: " + currentPos.getPos());
//            System.out.println("New pos: " + newPos);
//
//            System.out.println("Step: " + step);
            return new Position(newPos, step);
        });
        return position.step;
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
    private static class Position {
        private int pos;
        private int step;
    }
}
