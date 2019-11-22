package com.wez.panda.servo;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ServoDriverTest {

    @Test
    public void testServoDriver() throws Exception {
        IServoDriver driver = new AServoDriver(Servo.of("LB_KN", "LB_KN.csv")) {
            @Override
            protected void operate(double pos) throws InterruptedException {
                System.out.println(String.format("Servo %s operating to position %.3f", getServo().getName(), pos));
                TimeUnit.MILLISECONDS.sleep(100);
            }
        };

        Thread thread = new Thread(driver);
        thread.start();
        TimeUnit.SECONDS.sleep(10);
        driver.terminate();
        TimeUnit.SECONDS.sleep(1);
        Assert.assertFalse(thread.isAlive());
    }

}