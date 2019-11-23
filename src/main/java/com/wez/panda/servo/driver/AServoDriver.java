package com.wez.panda.servo.driver;

import com.wez.panda.data.DataFileLoader;
import com.wez.panda.data.ServoDataConverter;
import com.wez.panda.servo.Servo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.Pair;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@AllArgsConstructor
@RequiredArgsConstructor
public abstract class AServoDriver implements IServoDriver {

    private static final double MILLIS_PER_SEC = (double) TimeUnit.SECONDS.toMillis(1);

    @Getter
    private final Servo servo;

    @Getter(lazy = true, value = AccessLevel.PACKAGE)
    private final UnivariateFunction data = loadData();

    private final AtomicBoolean terminated = new AtomicBoolean(false);

    private DataFileLoader dataFileLoader = new DataFileLoader();
    private ServoDataConverter dataConverter = new ServoDataConverter();

    @Override
    public void initialize() {
        try {
            syncOperate(getData().value(0));
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {

        final double offset = now();

        // Starting from initial position
        while (!terminated.get() && !Thread.currentThread().isInterrupted()) {
            try {
                syncOperate(getData().value(now() - offset));
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void terminate() {
        terminated.set(true);
    }

    protected abstract void operate(double posAfterApplyingOffset) throws InterruptedException;

    private synchronized void syncOperate(double pos) throws InterruptedException {
        this.operate(pos + servo.getOffsetDegrees());
    }

    private UnivariateFunction loadData() {
        String dataFilePath = getServo().getDataFilePath();
        List<Pair<Double, Double>> rawData = dataFileLoader.loadRawData(dataFilePath);
        return dataConverter.convert(rawData);
    }

    private double now() {
        return System.currentTimeMillis() / MILLIS_PER_SEC;
    }
}
