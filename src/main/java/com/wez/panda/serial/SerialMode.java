package com.wez.panda.serial;

import lombok.AllArgsConstructor;
import processing.serial.Serial;

import java.util.function.Function;

@AllArgsConstructor
public enum SerialMode {

    DISPLACEMENT(DisplacementController::new),
    VELOCITY(VelocityController::new);

    private final Function<Serial, ASerialController> serialControllerBuilder;

    public ASerialController getController(Serial serial) {
        return serialControllerBuilder.apply(serial);
    }
}
