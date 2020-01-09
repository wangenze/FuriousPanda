package com.wez.panda.serial;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import processing.serial.Serial;

import static org.mockito.Mockito.*;

public class DisplacementControllerTest {

    private Serial serial;

    private DisplacementController controller;

    @Before
    public void setUp() throws Exception {
        serial = mock(Serial.class);
        controller = new DisplacementController(serial);
    }

    @Test
    public void send() {
        doAnswer(invocation -> {
            int val = invocation.getArgument(0);
            System.out.println("Write to serial:\t" + toBinaryString(val));
            return null;
        }).when(serial).write(anyInt());
        int message = 4321;
        System.out.println("Sending message:\t" + toBinaryString(message));
        controller.send(message);
    }

    @Test
    public void checksum() {
        Assert.assertEquals(0x12, controller.checksum(0x05, 0x00, 0x0d));
    }

    private String toBinaryString(int i) {
        return "0x" + StringUtils.leftPad(StringUtils.upperCase(Integer.toHexString(i)), 8, '0');
    }
}
