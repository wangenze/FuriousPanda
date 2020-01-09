package com.wez.panda.serial;

import lombok.NonNull;
import processing.serial.Serial;

public class DisplacementController extends ASerialController {

    public DisplacementController(@NonNull Serial serial) {
        super(serial);
    }

    @Override
    protected void sendSignal(int signal) {
        sequentialWrite(0x06, (signal >> 8) & 0xFF, signal & 0xFF);
        delay(10L);
    }
}
