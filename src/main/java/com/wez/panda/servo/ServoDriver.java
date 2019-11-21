package com.wez.panda.servo;

import com.wez.panda.data.DataFileLoader;
import com.wez.panda.data.ServoDataConverter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.Pair;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@AllArgsConstructor
@RequiredArgsConstructor
public class ServoDriver implements Runnable {

    private static final double MILLIS_PER_SEC = (double) TimeUnit.SECONDS.toMillis(1);

    @Getter
    private final Servo servo;

    @Getter(lazy = true, value = AccessLevel.PACKAGE)
    private final UnivariateFunction data = loadData();

    private final AtomicBoolean terminated = new AtomicBoolean(false);

    private DataFileLoader dataFileLoader = new DataFileLoader();
    private ServoDataConverter dataConverter = new ServoDataConverter();

    @Override
    public void run() {
        ServoOperator operator = new ServoOperator(getServo());

        // Resetting position
        try {
            operator.operate(getData().value(0));
            // TODO: Blocking or non-blocking?
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        final double offset = now();

        // Starting from initial position
        while (!terminated.get() && !Thread.currentThread().isInterrupted()) {
            try {
                operator.operate(getData().value(now() - offset));
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void terminate() {
        terminated.set(true);
    }

    private UnivariateFunction loadData() {
        String dataFilePath = getServo().getDataFilePath();
        List<Pair<Double, Double>> rawData = dataFileLoader.loadRawData(dataFilePath);
        return dataConverter.convert(rawData);
    }

    private double now() {
        return Calendar.getInstance().getTimeInMillis() / MILLIS_PER_SEC;
    }
}
