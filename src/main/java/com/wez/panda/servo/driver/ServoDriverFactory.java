package com.wez.panda.servo.driver;

import com.wez.panda.servo.Servo;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.time.StopWatch;

public class ServoDriverFactory {

    public IServoDriver getServoDriver(Servo servo, StopWatch sw, DriverParameters parameters) {
        StopWatch stopWatch = sw == null ? new StopWatch() : sw;
        switch (servo.getSerialMode()) {
            case DISPLACEMENT:
                return new DisplacementServoDriver(servo, stopWatch, parameters);
            case VELOCITY:
                return new VelocityServoDriver(servo, stopWatch, parameters);
            default:
                throw new NotImplementedException("Mode not supported: " + servo.getSerialMode());
        }
    }
}
