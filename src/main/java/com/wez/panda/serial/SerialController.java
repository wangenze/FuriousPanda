package com.wez.panda.serial;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import processing.serial.Serial;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class SerialController {

    @NonNull
    private final Serial serial;

    public synchronized void send(int message) {
        serial.write(0x50);
        serial.write((message >> 24) & 0xFF);
        serial.write((message >> 16) & 0xFF);
        serial.write(checksum(0x50, (message >> 24) & 0xFF, (message >> 16) & 0xFF));
        delay(10L);

        serial.write(0x05);
        serial.write((message >> 8) & 0xFF);
        serial.write(message & 0xFF);
        serial.write(checksum(0x50, (message >> 8) & 0xFF, message & 0xFF));
        delay(10L);
    }

    private int checksum(int a, int b, int c) {
        return (a + b + c) & 0xFF;
    }

    private void delay(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace(); //NOSONAR
            Thread.currentThread().interrupt();
        }
    }
}
