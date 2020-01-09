package com.wez.panda.serial;

import lombok.AllArgsConstructor;
import processing.serial.Serial;

import java.util.function.Function;

@AllArgsConstructor
public enum SerialMode implements Function<Serial, ASerialController> {

    DISPLACEMENT(DisplacementController::new),
    VELOCITY(VelocityController::new);

    private final Function<Serial, ASerialController> innerFunction;

    @Override
    public ASerialController apply(Serial serial) {
        return innerFunction.apply(serial);
    }
}
