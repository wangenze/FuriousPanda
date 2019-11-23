package com.wez.panda.servo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import processing.serial.Serial;

@Data
@AllArgsConstructor(staticName = "of")
@RequiredArgsConstructor(staticName = "of")
public class Servo {
    private final String name;
    private final String dataFilePath;

    private double offsetDegrees = 0d;
    private Serial serial = null;
}
