package com.wez.panda.data;

import com.wez.panda.math.RotatingAngleInterpolator;
import one.util.streamex.StreamEx;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.Pair;

import java.util.List;

public class ServoDataConverter {

    private RotatingAngleInterpolator interpolator = RotatingAngleInterpolator.linearInterpolator();

    public UnivariateFunction convert(List<Pair<Double, Double>> rawData) {
        double[] xVal = StreamEx.of(rawData).mapToDouble(Pair::getFirst).toArray();
        double[] yVal = StreamEx.of(rawData).mapToDouble(Pair::getSecond).toArray();

        return interpolator.interpolate(xVal, yVal);
    }
}
