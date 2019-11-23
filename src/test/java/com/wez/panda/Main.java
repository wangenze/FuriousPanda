package com.wez.panda;

import com.wez.panda.servo.Servo;
import processing.core.PApplet;
import processing.serial.Serial;

import java.util.Arrays;
import java.util.List;

public class Main extends PApplet {

    public static void main(String[] args) {
        PApplet.main(new String[]{Main.class.getName()});
    }

    private PandaDriver pandaDriver;

    @Override
    public void settings() {
        Servo servo1 = Servo.of("LB_KN", "C:\\Users\\wange\\IdeaProjects\\FuriousPanda\\src\\test\\resources\\LB_KN.csv", 10d,  new Serial(this, "COM1"));

        List<Servo> servos = Arrays.asList(servo1);
        pandaDriver = PandaDriver.builder().servos(servos).build();
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
