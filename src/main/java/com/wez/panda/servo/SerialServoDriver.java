package com.wez.panda.servo;

import java.util.concurrent.TimeUnit;

public class SerialServoDriver extends AServoDriver {

    public SerialServoDriver(Servo servo) {
        super(servo);
    }

    @Override
    protected void operate(double pos) throws InterruptedException {
        System.out.println(String.format("Servo %s operating to position %.3f", getServo().getName(), pos));
        TimeUnit.MILLISECONDS.sleep(100);
    }
}
