package com.wez.panda;

import com.wez.panda.servo.ControlMode;
import com.wez.panda.servo.Servo;
import com.wez.panda.servo.driver.DriverParameters;
import org.mockito.Mockito;
import processing.core.PApplet;
import processing.serial.Serial;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;

public class SerialSimulation extends PApplet {

    private PandaDriver pandaDriver;

    public static void main(String[] args) {
        PApplet.main(new String[]{SerialSimulation.class.getName()});
    }

    @Override
    public void settings() {
        size(200, 200);

        Serial mockSerial = Mockito.mock(Serial.class);
        doAnswer(invocation -> {
            int val = invocation.getArgument(0);
            System.out.println("Write to serial:\t" + val);
            return null;
        }).when(mockSerial).write(anyInt());

        Servo servo1 = Servo.builder()
                .name("RF_SD")
                .dataFilePath("C:\\Users\\wange\\IdeaProjects\\FuriousPanda\\src\\test\\resources\\RF_SD.csv")
                .offsetDegrees(0d)
                .transmissionRatio(1)
                .serial(mockSerial)
                .controlMode(ControlMode.ABSOLUTE)
                .build();

        List<Servo> servos = Arrays.asList(servo1);
        DriverParameters parameters = DriverParameters.builder()
                .delayAfterInitialization(5_000L)
                .minInterval(100L)
                .build();
        pandaDriver = PandaDriver.builder().servos(servos).driverParameters(parameters).build();
        pandaDriver.start();
    }

    @Override
    public void setup() {
    }

    @Override
    public void draw() {
        background(0);
    }

    @Override
    public void mousePressed() {
        if (pandaDriver.isPaused()) {
            pandaDriver.resume();
        } else {
            pandaDriver.pause();
        }
    }
}
