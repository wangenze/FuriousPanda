package com.wez.panda.serial;

import lombok.NonNull;
import processing.serial.Serial;

public class VelocityController extends ASerialController {

    public VelocityController(@NonNull Serial serial) {
        super(serial);
    }

    @Override
    protected void sendSignal(int signal) {
        sequentialWrite(0x06, (signal >> 8) & 0xFF, signal & 0xFF);
        delay();
    }
}
