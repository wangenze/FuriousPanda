package com.wez.panda.servo;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "of")
public class Servo {
    private int id;
    private String name;
    private String dataFilePath;
}
