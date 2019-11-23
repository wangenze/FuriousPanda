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
            System.out.println("Write to serial:\t" + toBinaryString(val));
            return null;
        }).when(serial).write(anyInt());
        SerialController serialController = new SerialController(serial);

        int message = 4321;
        System.out.println("Sending message:\t" + toBinaryString(message));
        serialController.send(message);
    }

    private String toBinaryString(int i) {
        return "0x" + StringUtils.leftPad(StringUtils.upperCase(Integer.toHexString(i)), 8, '0');
    }
}