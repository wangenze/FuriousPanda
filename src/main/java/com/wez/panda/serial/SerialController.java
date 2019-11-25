package com.wez.panda.serial;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import processing.serial.Serial;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class SerialController {

    @NonNull
    private final Serial serial;

    public synchronized void send(int signal) {
//        System.out.println("Writing signal: " + signal);
        sequentialWrite(0x50, (signal >> 24) & 0xFF, (signal >> 16) & 0xFF);
        delay(10L);

        sequentialWrite(0x05, (signal >> 8) & 0xFF, signal & 0xFF);
        delay(10L);
    }

    void sequentialWrite(int a, int b, int c) {
        serial.write(a);
        serial.write(b);
        serial.write(c);
        serial.write(checksum(a, b, c));
    }

    int checksum(int a, int b, int c) {
        long sum = 0L;
        sum += a;
        sum += b;
        sum += c;
        return (int) (sum & 0xFF);
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
