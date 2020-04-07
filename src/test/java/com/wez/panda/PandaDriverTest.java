package com.wez.panda;

import com.wez.panda.servo.Servo;
import com.wez.panda.servo.driver.AServoDriver;
import com.wez.panda.servo.driver.IServoDriver;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class PandaDriverTest {

    @Test
    public void testPandaDriver() throws Exception {
        PandaDriver pandaDriver = PandaDriver.builder()
                .servos(Resources.getAllServosForTesting())
                .servoDriverFactory(MockingServoDriver::new)
                .build();
        // Start
        pandaDriver.start();
        TimeUnit.SECONDS.sleep(2);

        // Stop
        pandaDriver.stop();
        TimeUnit.SECONDS.sleep(2);

        // Restart
        pandaDriver.start();
        TimeUnit.SECONDS.sleep(2);

        // Stop
        pandaDriver.stop();
    }

    @Test
    public void testSingleServo() throws Exception {
        PandaDriver pandaDriver = PandaDriver.builder()
                .servos(Collections.singletonList(Servo.builder().name("LB_KN").dataFilePath("LB_KN.csv").build()))
                .servoDriverFactory(IServoDriver.EmptyDriver::new)
                .build();
        // Start
        pandaDriver.start();
        TimeUnit.SECONDS.sleep(5);
        pandaDriver.stop();
    }

    static class MockingServoDriver extends AServoDriver {
        public MockingServoDriver(Servo servo, StopWatch stopWatch) {
            super(servo, stopWatch);
        }

        @Override
        protected void operate(double timeSec) throws InterruptedException {
            System.out.println(String.format("Servo %s operating to position %.3f", getServo().getName(), getPosAfterApplyingOffset(timeSec)));
            TimeUnit.MILLISECONDS.sleep(100);
        }
    }
}