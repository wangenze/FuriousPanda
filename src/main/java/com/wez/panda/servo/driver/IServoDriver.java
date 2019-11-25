package com.wez.panda.servo.driver;

import com.wez.panda.servo.Servo;
import org.apache.commons.lang3.time.StopWatch;

public interface IServoDriver extends Runnable {

    default void initialize() {
        // do nothing by default
    }

    void terminate();

    class EmptyDriver implements IServoDriver {

        public EmptyDriver(Servo servo, StopWatch stopWatch) {
        }

        @Override
        public void terminate() {
            // do nothing
        }

        @Override
        public void run() {
            // do nothing
        }
    }
}
