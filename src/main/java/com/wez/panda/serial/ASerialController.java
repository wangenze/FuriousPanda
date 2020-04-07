package com.wez.panda.serial;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import processing.serial.Serial;

import java.util.concurrent.TimeUnit;

@Log4j2
@AllArgsConstructor
public abstract class ASerialController {

    @NonNull
    private final Serial serial;

    public void send(int signal) {
        synchronized (serial) {
            sendSignal(signal);
        }
    }

    public int receive() {
        synchronized (serial) {
            return receiveSignal();
        }
    }

    protected int receiveSignal() {
        return 0;
    }

    protected abstract void sendSignal(int signal);

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

    void delay() {
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace(); //NOSONAR
            Thread.currentThread().interrupt();
        }
    }
}
