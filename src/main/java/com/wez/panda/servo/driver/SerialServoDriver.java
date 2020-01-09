package com.wez.panda.servo.driver;

import com.wez.panda.serial.ASerialController;
import com.wez.panda.servo.Position;
import com.wez.panda.servo.Servo;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static com.wez.panda.math.RotatingAngleInterpolator.HALF_PERIOD;
import static com.wez.panda.math.RotatingAngleInterpolator.PERIOD;

public class SerialServoDriver extends AServoDriver {

    private static final double SERVO_SIGNAL_RANGE = 5000;

    private final ASerialController controller;
    private final DriverParameters parameters;
    private final double conversionRatio;
    private final Function<Position, Integer> position2Signal;

    public SerialServoDriver(Servo servo, StopWatch stopWatch, DriverParameters parameters) {
        super(servo, stopWatch);
        this.controller = servo.getSerialMode().getController(servo.getSerial());
        this.parameters = parameters;
        this.conversionRatio = servo.getTransmissionRatio() * SERVO_SIGNAL_RANGE;
        this.position2Signal = servo.getControlMode().getPosition2SignalConverter();
    }

    public static Builder builder() {
        return new Builder();
    }

    private AtomicReference<Position> current = new AtomicReference<>(new Position(0, 0));

    @Override
    public void initialize() {
        super.initialize();
        delay(parameters.getDelayAfterInitialization());
    }

    @Override
    protected void operate(double posWithOffset) throws InterruptedException {
        Position position = calculatePosition(posWithOffset);
        if (position.getStep() != 0) {
            int signal = position2Signal.apply(position);
            controller.send(signal);
        }
        delay(parameters.getMinInterval());
    }

    private Position calculatePosition(final double targetAngle) {
        return current.updateAndGet(currentPos -> getNewPosition(targetAngle, currentPos));
    }

    private Position getNewPosition(double targetAngle, Position currentPos) {
        double oriAngle = MathUtils.reduce(currentPos.getPos() * PERIOD / conversionRatio, PERIOD, 0d);
        double newAngle = MathUtils.reduce(targetAngle, PERIOD, 0d);
        double angleStep = MathUtils.reduce(newAngle - oriAngle, PERIOD, HALF_PERIOD) - HALF_PERIOD;

        int step = (int) FastMath.round(angleStep * conversionRatio / PERIOD);
        int newPos = currentPos.getPos() + step;

        return new Position(newPos, step);
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

        public SerialServoDriver build() {
            Validate.notNull(servo);
            Validate.notNull(parameters);
            return new SerialServoDriver(servo, stopWatch == null ? new StopWatch() : stopWatch, parameters);
        }
    }
}
