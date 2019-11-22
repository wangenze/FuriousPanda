package com.wez.panda.servo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor(staticName = "of")
@RequiredArgsConstructor(staticName = "of")
public class Servo {
    private final String name;
    private final String dataFilePath;

    private double offset = 0d;
}
