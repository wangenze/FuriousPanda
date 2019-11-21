package com.wez.panda;

import com.wez.panda.servo.Servo;
import lombok.experimental.UtilityClass;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@UtilityClass
public class StatusHolder {
    public static final ConcurrentMap<Servo, Double> STATUS = new ConcurrentHashMap<>();
}
