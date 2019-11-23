package com.wez.panda;

import com.wez.panda.servo.Servo;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class Resources {
    public static List<Servo> getAllServosForTesting() {
        return Arrays.asList(
                Servo.of("LB_KN", "LB_KN.csv", 90d, null),
                Servo.of("LB_SD", "LB_SD.csv"),
                Servo.of("LF_KN", "LF_KN.csv"),
                Servo.of("LF_SD", "LF_SD.csv"),
                Servo.of("RB_KN", "RB_KN.csv", -90d, null),
                Servo.of("RB_SD", "RB_SD.csv"),
                Servo.of("RF_KN", "RF_KN.csv"),
                Servo.of("RF_SD", "RF_SD.csv")
        );
    }
}
