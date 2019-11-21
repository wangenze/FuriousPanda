package com.wez.panda.servo;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ServoDriverTest {

    @Test
    @Ignore
    public void testServoDriver() throws Exception {
        ServoDriver driver = new ServoDriver(Servo.of(1, "S1", "servo1.csv"));
//        for (int i = 0; i < 7000; i++) {
//            System.out.println(String.format("%5d, %.3f", i, driver.getData().value(i / 1000.0)));
//        }

        Thread thread = new Thread(driver);
        thread.start();
        TimeUnit.SECONDS.sleep(10);
        driver.terminate();
        TimeUnit.SECONDS.sleep(1);
        Assert.assertFalse(thread.isAlive());
    }

}