package com.wez.panda.servo.driver;

public interface IServoDriver extends Runnable {

    default void initialize() {
        // do nothing by default
    }

    void terminate();

    class EmptyDriver implements IServoDriver {

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
