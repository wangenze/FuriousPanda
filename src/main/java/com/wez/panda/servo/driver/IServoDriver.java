package com.wez.panda.servo.driver;

import com.wez.panda.servo.Servo;

public interface IServoDriver extends Runnable {

    default void initialize() {
        // do nothing by default
    }

    void terminate();

    class EmptyDriver implements IServoDriver {

        public EmptyDriver(Servo servo) {
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
