package com.wez.panda.servo;

import lombok.*;
import processing.serial.Serial;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor(staticName = "of")
@RequiredArgsConstructor(staticName = "of")
public class Servo {
    private final String name;
    private final String dataFilePath;

    private double offsetDegrees = 0d;
    private Serial serial = null;
}
