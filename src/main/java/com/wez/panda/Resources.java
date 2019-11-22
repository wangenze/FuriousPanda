package com.wez.panda;

import com.wez.panda.servo.Servo;
import lombok.experimental.UtilityClass;
import processing.core.PApplet;
import processing.serial.Serial;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class Resources {
    public static List<Servo> getAllServosForTesting() {
        return Arrays.asList(
                Servo.of("LB_KN", "LB_KN.csv"),
                Servo.of("LB_SD", "LB_SD.csv"),
                Servo.of("LF_KN", "LF_KN.csv"),
                Servo.of("LF_SD", "LF_SD.csv"),
                Servo.of("RB_KN", "RB_KN.csv"),
                Servo.of("RB_SD", "RB_SD.csv"),
                Servo.of("RF_KN", "RF_KN.csv"),
                Servo.of("RF_SD", "RF_SD.csv")
        );
    }

    public static List<Servo> getAllServos(PApplet pApplet) {
        String[] serials = Serial.list();
        return Arrays.asList(
                Servo.of("LB_KN", "LB_KN.csv", new Serial(pApplet, serials[0], 9600), 0d),
                Servo.of("LB_SD", "LB_SD.csv", new Serial(pApplet, serials[1], 9600), 0d),
                Servo.of("LF_KN", "LF_KN.csv", new Serial(pApplet, serials[2], 9600), 0d),
                Servo.of("LF_SD", "LF_SD.csv", new Serial(pApplet, serials[3], 9600), 0d),
                Servo.of("RB_KN", "RB_KN.csv", new Serial(pApplet, serials[4], 9600), 0d),
                Servo.of("RB_SD", "RB_SD.csv", new Serial(pApplet, serials[5], 9600), 0d),
                Servo.of("RF_KN", "RF_KN.csv", new Serial(pApplet, serials[6], 9600), 0d),
                Servo.of("RF_SD", "RF_SD.csv", new Serial(pApplet, serials[7], 9600), 0d)
        );
    }
}
