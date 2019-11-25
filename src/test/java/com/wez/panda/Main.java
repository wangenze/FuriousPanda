package com.wez.panda;

import com.wez.panda.servo.ControlMode;
import com.wez.panda.servo.Servo;
import com.wez.panda.servo.driver.DriverParameters;
import processing.core.PApplet;
import processing.serial.Serial;

import java.util.Arrays;
import java.util.List;

public class Main extends PApplet {

    public static void main(String[] args) {
        PApplet.main(new String[]{Main.class.getName()});
    }

    @Override
    public void settings() {
        Servo servo1 = Servo.builder()
                .name("RF_SD")
                .dataFilePath("C:\\Users\\wange\\IdeaProjects\\FuriousPanda\\src\\test\\resources\\RF_SD.csv")
                .offsetDegrees(0d)
                .transmissionRatio(1)
                .serial(new Serial(this, "COM1"))
                .controlMode(ControlMode.ABSOLUTE)
                .build();

        List<Servo> servos = Arrays.asList(servo1);
        DriverParameters parameters = DriverParameters.builder()
                .delayAfterInitialization(5_000L)
                .minInterval(100L)
                .build();
        PandaDriver pandaDriver = PandaDriver.builder().servos(servos).driverParameters(parameters).build();
        pandaDriver.start();
    }

    @Override
    public void setup() {
    }

    @Override
    public void draw() {
        delay(5000);
    }
}
