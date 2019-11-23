package com.wez.panda;

import com.wez.panda.servo.Servo;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class Resources {
    public static List<Servo> getAllServosForTesting() {
        return Arrays.asList(
                Servo.builder().name("LB_KN").dataFilePath("LB_KN.csv").offsetDegrees(90d).build(),
                Servo.builder().name("LB_SD").dataFilePath("LB_SD.csv").build(),
                Servo.builder().name("LF_KN").dataFilePath("LF_KN.csv").build(),
                Servo.builder().name("LF_SD").dataFilePath("LF_SD.csv").build(),
                Servo.builder().name("RB_KN").dataFilePath("RB_KN.csv").offsetDegrees(-90d).build(),
                Servo.builder().name("RB_SD").dataFilePath("RB_SD.csv").build(),
                Servo.builder().name("RF_KN").dataFilePath("RF_KN.csv").build(),
                Servo.builder().name("RF_SD").dataFilePath("RF_SD.csv").build()
        );
    }
}
