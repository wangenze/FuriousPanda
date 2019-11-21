package com.wez.panda.servo;

import com.wez.panda.StatusHolder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ServoOperator {

    private final Servo servo;

    public synchronized void operate(double pos) throws InterruptedException {
        System.out.println(String.format("Servo %s operating to position %.3f", servo.getId(), pos));
        StatusHolder.STATUS.put(servo, pos);
//        TimeUnit.MILLISECONDS.sleep(100);
    }
}
