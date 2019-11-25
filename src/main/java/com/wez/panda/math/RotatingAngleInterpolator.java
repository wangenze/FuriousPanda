package com.wez.panda.math;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.util.MathUtils;

@AllArgsConstructor(staticName = "wrap")
public class RotatingAngleInterpolator implements UnivariateInterpolator {

    public static final double PERIOD = 360d;
    public static final double HALF_PERIOD = 180d;

    private static final double DELTA = 1e-6;

    private final UnivariateInterpolator interpolator;

    public static RotatingAngleInterpolator linearInterpolator() {
        return RotatingAngleInterpolator.wrap(new LinearInterpolator());
    }

    @Override
    public UnivariateFunction interpolate(double[] x, double[] y) {
        // Number of intervals.  The number of data points is n + 1.
        int n = x.length - 1;

        Validate.isTrue(x.length == y.length);
        Validate.isTrue(Math.abs(y[0] - y[n]) < DELTA);

        // Slopes
        double[] m = new double[n];
        // Corrected y values
        double[] yCorrected = new double[n + 1];
        yCorrected[0] = y[0];
        for (int i = 0; i < n; i++) {
            double d = MathUtils.reduce(y[i + 1] - y[i], PERIOD, HALF_PERIOD) - HALF_PERIOD;
            m[i] = d / (x[i + 1] - x[i]);
            yCorrected[i + 1] = y[i] + d;
        }
        final PolynomialFunction[] polynomials = new PolynomialFunction[n];
        final double[] coefficients = new double[2];
        for (int i = 0; i < n; i++) {
            coefficients[0] = yCorrected[i];
            coefficients[1] = m[i];
            polynomials[i] = new PolynomialFunction(coefficients);
        }

        UnivariateFunction interpolate = new PolynomialSplineFunction(x, polynomials);

        double xMin = x[0];
        double xMax = x[n];
        double range = xMax - xMin;

        return in -> interpolate.value(MathUtils.reduce(in, range, xMin) + xMin);
    }
}
