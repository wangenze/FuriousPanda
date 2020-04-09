package com.wez.panda.serial;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.Validate;
import processing.serial.Serial;

import java.util.concurrent.TimeUnit;

@Log4j2
@AllArgsConstructor
public abstract class ASerialController {

    private static final int SERIAL_READ_TRIES = 20;

    @NonNull
    private final Serial serial;

    public void send(int signal) {
        synchronized (serial) {
            sendSignal(signal);
        }
    }

    public int readPositionFeedback() {
        synchronized (serial) {
            // request
            sequentialWrite(0x75, 0x00, 0x00);

            // header
            int m1 = tryRead();
            int m2 = tryRead();
            for (int i = 0; !(m1 == 0x75 && m2 == 0x75) && i < SERIAL_READ_TRIES; i++) {
                m1 = (m2 == 0x75) ? m2 : tryRead();
                m2 = tryRead();
            }
            Validate.validState(m1 == 0x75 && m2 == 0x75, "Unable to read serial signal header");

            // data
            int dataH = readDataExpectAddress(0xE8);
            int dataL = readDataExpectAddress(0xE9);
            return ((dataH & 0xFFFF) << 16) | (dataL & 0xFFFF);
        }
    }

    private int readDataExpectAddress(int expectedAddress) {
        expectedAddress &= 0xFF;
        int address = tryRead();
        Validate.validState(address == expectedAddress, "Unable to find data address %02x", expectedAddress);
        int data1 = tryRead();
        Validate.validState(data1 >= 0, "Unable to find data byte 1 on address %02x", expectedAddress);
        int data2 = tryRead();
        Validate.validState(data2 >= 0, "Unable to find data byte 2 on address %02x", expectedAddress);
        int sum = tryRead();
        Validate.validState(sum == checksum(address, data1, data2), "Unable to verify checksum on address %02x", expectedAddress);
        return ((data1 & 0xFF) << 8) | (data2 & 0xFF);
    }

    protected abstract void sendSignal(int signal);

    int tryRead() {
        for (int i = 0; serial.available() <= 0 && i < SERIAL_READ_TRIES; i++) {
            delay();
        }
        return serial.read();
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

    void delay() {
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace(); //NOSONAR
            Thread.currentThread().interrupt();
        }
    }
}
