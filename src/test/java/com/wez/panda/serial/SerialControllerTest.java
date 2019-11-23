package com.wez.panda.serial;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import processing.serial.Serial;

import static org.mockito.Mockito.*;

public class SerialControllerTest {

    @Test
    public void send() {
        Serial serial = mock(Serial.class);
        doAnswer(invocation -> {
            int val = invocation.getArgument(0);
            System.out.println("Serial write: " + toBinaryString(val));
            return null;
        }).when(serial).write(anyInt());
        SerialController serialController = new SerialController(serial);

        int message = 1000;
        System.out.println("Sending message: " + toBinaryString(message));
        serialController.send(1000);
    }

    private String toBinaryString(int i) {
        return StringUtils.leftPad(Integer.toBinaryString(i), 8, '0');
    }
}