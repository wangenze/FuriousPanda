package com.wez.panda.math;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.util.MathUtils;

@AllArgsConstructor(staticName = "wrap")
public class RepeatingSeriesInterpolator implements UnivariateInterpolator {

    private static final double DELTA = 1e-6;

    private final UnivariateInterpolator interpolator;

    public static RepeatingSeriesInterpolator linearInterpolator() {
        return RepeatingSeriesInterpolator.wrap(new LinearInterpolator());
    }

    @Override
    public UnivariateFunction interpolate(double[] xval, double[] yval) {
        double xMin = xval[0];
        double xMax = xval[xval.length - 1];
        double range = xMax - xMin;
        Validate.isTrue(Math.abs(yval[0] - yval[yval.length - 1]) < DELTA);

        UnivariateFunction interpolate = interpolator.interpolate(xval, yval);
        return x -> interpolate.value(MathUtils.reduce(x, range, xMin) + xMin);
    }
}
