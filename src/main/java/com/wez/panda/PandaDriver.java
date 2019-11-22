package com.wez.panda;

import com.wez.panda.servo.IServoDriver;
import com.wez.panda.servo.SerialServoDriver;
import com.wez.panda.servo.Servo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.wez.panda.Resources.DEFAULT_SERVOS;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PandaDriver {

    @Getter
    @NonNull
    private List<Servo> servos;
    @NonNull
    private Function<Servo, IServoDriver> servoDriverFactory;

    private ExecutorService executorService = null;
    private Set<IServoDriver> drivers = new HashSet<>();

    public static Builder builder() {
        return new Builder().servos(DEFAULT_SERVOS).servoDriverFactory(SerialServoDriver::new);
    }

    public synchronized void start() {
        synchronized (this) {
            if (executorService != null) {
                return;
            }
            executorService = Executors.newFixedThreadPool(servos.size());
            drivers.clear();
            for (Servo servo : servos) {
                IServoDriver servoDriver = servoDriverFactory.apply(servo);
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
            for (IServoDriver driver : drivers) {
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

    public static class Builder {
        private Function<Servo, IServoDriver> servoDriverFactory;
        private List<Servo> servos;

        public Builder servoDriverFactory(Function<Servo, IServoDriver> servoDriverFactory) {
            this.servoDriverFactory = servoDriverFactory;
            return this;
        }

        public Builder servos(List<Servo> servos) {
            this.servos = servos;
            return this;
        }

        public PandaDriver build() {
            return new PandaDriver(servos, servoDriverFactory);
        }
    }
}
