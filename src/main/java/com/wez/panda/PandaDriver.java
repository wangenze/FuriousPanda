package com.wez.panda;

import com.wez.panda.servo.Servo;
import com.wez.panda.servo.ServoDriver;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor
public class PandaDriver {

    public static final List<Servo> DEFAULT_SERVOS = Arrays.asList(
            Servo.of(1, "S1", "servo1.csv"),
            Servo.of(2, "S2", "servo2.csv")
    );

    @Getter
    private List<Servo> servos = DEFAULT_SERVOS;

    private ExecutorService executorService = null;
    private Set<ServoDriver> drivers = new HashSet<>();

    public PandaDriver(List<Servo> servos) {
        this.servos = servos;
    }

    public synchronized void start() {
        synchronized (this) {
            if (executorService != null) {
                return;
            }
            executorService = Executors.newFixedThreadPool(servos.size());
            drivers.clear();
            for (Servo servo : servos) {
                ServoDriver servoDriver = new ServoDriver(servo);
                drivers.add(servoDriver);
                executorService.submit(servoDriver);
            }
        }
    }

    public synchronized void stop() {
        synchronized (this) {
            if (executorService == null) {
                return;
            }
            for (ServoDriver driver : drivers) {
                try {
                    driver.terminate();
                } catch (Exception e) {
                    e.printStackTrace(); //NOSONAR
                }
            }
            try {
                executorService.shutdown();
                if (executorService.awaitTermination(1L, TimeUnit.MINUTES)) {
                    executorService = null;
                } else {
                    throw new IllegalStateException("Timeout waiting for threads to terminate");
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
