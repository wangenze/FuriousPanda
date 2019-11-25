package com.wez.panda;

import com.wez.panda.servo.Servo;
import com.wez.panda.servo.driver.DriverParameters;
import com.wez.panda.servo.driver.IServoDriver;
import com.wez.panda.servo.driver.SerialServoDriver;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.StopWatch;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

import static com.wez.panda.servo.driver.DriverParameters.DEFAULT_PARAMETERS;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PandaDriver {

    @Getter
    @NonNull
    private List<Servo> servos;
    @NonNull
    private BiFunction<Servo, StopWatch, IServoDriver> servoDriverFactory;

    private ExecutorService executorService = null;
    private Set<IServoDriver> drivers = new HashSet<>();
    private StopWatch stopWatch = new StopWatch();

    public static Builder builder() {
        return new Builder();
    }

    public boolean isAlive() {
        return executorService != null;
    }

    public boolean isPaused() {
        return stopWatch.isSuspended();
    }

    public synchronized void start() {
        synchronized (this) {
            if (isAlive()) {
                return;
            }
            stopWatch.reset();
            drivers.clear();
            for (Servo servo : servos) {
                IServoDriver servoDriver = servoDriverFactory.apply(servo, stopWatch);
                drivers.add(servoDriver);
            }
            drivers.stream().parallel().forEach(IServoDriver::initialize);

            executorService = Executors.newFixedThreadPool(servos.size());
            drivers.forEach(executorService::submit);

            stopWatch.start();
        }
    }

    public synchronized void pause() {
        synchronized (this) {
            stopWatch.suspend();
        }
    }

    public synchronized void resume() {
        synchronized (this) {
            stopWatch.resume();
        }
    }

    public synchronized void stop() {
        synchronized (this) {
            if (!isAlive()) {
                return;
            }
            stopWatch.stop();
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
        private DriverParameters driverParameters = DEFAULT_PARAMETERS;
        private BiFunction<Servo, StopWatch, IServoDriver> servoDriverFactory =
                (servo, stopWatch) -> SerialServoDriver.builder()
                        .servo(servo)
                        .stopWatch(stopWatch)
                        .parameters(driverParameters)
                        .build();
        private List<Servo> servos;

        public Builder driverParameters(DriverParameters driverParameters) {
            this.driverParameters = driverParameters;
            return this;
        }

        public Builder servoDriverFactory(BiFunction<Servo, StopWatch, IServoDriver> servoDriverFactory) {
            this.servoDriverFactory = servoDriverFactory;
            return this;
        }

        public Builder servos(List<Servo> servos) {
            this.servos = servos;
            return this;
        }

        public PandaDriver build() {
            Validate.notEmpty(servos);
            return new PandaDriver(servos, servoDriverFactory);
        }
    }
}
