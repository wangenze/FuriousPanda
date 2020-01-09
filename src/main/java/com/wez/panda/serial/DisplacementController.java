package com.wez.panda.serial;

import lombok.NonNull;
import processing.serial.Serial;

public class DisplacementController extends ASerialController {

    public DisplacementController(@NonNull Serial serial) {
        super(serial);
    }

    @Override
    protected void sendSignal(int signal) {
        sequentialWrite(0x50, (signal >> 24) & 0xFF, (signal >> 16) & 0xFF);
        delay(10L);

        sequentialWrite(0x05, (signal >> 8) & 0xFF, signal & 0xFF);
        delay(10L);
    }
}
