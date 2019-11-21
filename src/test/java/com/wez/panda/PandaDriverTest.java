package com.wez.panda;

import com.wez.panda.servo.Servo;
import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class PandaDriverTest {

    @Test
    public void testPandaDriver() throws Exception {
        PandaDriver pandaDriver = new PandaDriver();
        // Start
        pandaDriver.start();
        TimeUnit.SECONDS.sleep(5);

        // Stop
        pandaDriver.stop();
        TimeUnit.SECONDS.sleep(5);

        // Restart
        pandaDriver.start();
        TimeUnit.SECONDS.sleep(5);

        // Stop
        pandaDriver.stop();
    }

    @Test
    public void testSingleServo() throws Exception {
        PandaDriver pandaDriver = new PandaDriver(Collections.singletonList(Servo.of(1, "S1", "servo1.csv")));
        // Start
        pandaDriver.start();
        TimeUnit.SECONDS.sleep(15);
        pandaDriver.stop();
    }
}