package com.wez.panda;

import com.wez.panda.serial.SerialServoDriver;
import com.wez.panda.servo.Servo;
import processing.core.PApplet;
import processing.serial.Serial;

import java.util.Collections;

public class Main extends PApplet {

    public static void main(String[] args) {
        PApplet.main(new String[]{Main.class.getName()});
    }

    private PandaDriver pandaDriver;

    @Override
    public void settings() {
        size(1000, 800);
        pandaDriver = PandaDriver.builder()
                //.servos(Resources.getAllServos(this))
                .servos(Collections.singletonList(
                        Servo.of("LB_KN", "LB_KN.csv", new Serial(this), 0d)))
                .servoDriverFactory(SerialServoDriver::new)
                .build();
        pandaDriver.start();
    }

    @Override
    public void setup() {
    }

    @Override
    public void draw() {
        pandaDriver.getServos().forEach(servo -> {
            System.out.println(servo.getName());
        });
        delay(5000);
    }
}
