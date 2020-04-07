package com.wez.panda.servo.driver;

import com.wez.panda.data.DataFileLoader;
import com.wez.panda.data.ServoDataConverter;
import com.wez.panda.servo.Servo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.Pair;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@AllArgsConstructor
@RequiredArgsConstructor
public abstract class AServoDriver implements IServoDriver {

    private static final double NANOS_PER_SEC = (double) TimeUnit.SECONDS.toNanos(1L);

    @Getter
    private final Servo servo;

    private final StopWatch stopWatch;

    @Getter(lazy = true, value = AccessLevel.PACKAGE)
    private final UnivariateFunction data = loadData();

    private final AtomicBoolean terminated = new AtomicBoolean(false);

    private DataFileLoader dataFileLoader = new DataFileLoader();
    private ServoDataConverter dataConverter = new ServoDataConverter();

    @Override
    public void initialize() {
        try {
            syncOperate(0.0);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        // Starting from initial position
        while (!terminated.get() && !Thread.currentThread().isInterrupted()) {
            try {
                syncOperate(now());
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void terminate() {
        terminated.set(true);
    }

    protected abstract void operate(double timeSec) throws InterruptedException;

    protected double getPosAfterApplyingOffset(double timeSec) {
        return getData().value(timeSec) + servo.getOffsetDegrees();
    }

    private synchronized void syncOperate(double timeSec) throws InterruptedException {
        this.operate(timeSec);
    }

    private UnivariateFunction loadData() {
        String dataFilePath = getServo().getDataFilePath();
        List<Pair<Double, Double>> rawData = dataFileLoader.loadRawData(dataFilePath);
        return dataConverter.convert(rawData);
    }

    protected double now() {
        return stopWatch.getNanoTime() / NANOS_PER_SEC;
    }
}
