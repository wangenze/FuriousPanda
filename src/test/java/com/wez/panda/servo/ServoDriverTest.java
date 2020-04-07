package com.wez.panda.servo;

import com.wez.panda.servo.driver.AServoDriver;
import com.wez.panda.servo.driver.IServoDriver;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ServoDriverTest {

    @Test
    public void testServoDriver() throws Exception {
        IServoDriver driver = new AServoDriver(Servo.builder().name("LB_KN").dataFilePath("LB_KN.csv").build(), StopWatch.createStarted()) {
            @Override
            protected void operate(double timeSec) throws InterruptedException {
                System.out.println(String.format("Servo %s operating to position %.3f", getServo().getName(), getPosAfterApplyingOffset(timeSec)));
                TimeUnit.MILLISECONDS.sleep(100);
            }
        };

        Thread thread = new Thread(driver);
        thread.start();
        TimeUnit.SECONDS.sleep(5);
        driver.terminate();
        TimeUnit.SECONDS.sleep(1);
        Assert.assertFalse(thread.isAlive());
    }

}