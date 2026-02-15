package it.unibo.df.ai.util;

/**
 * Utility class that gives me some curves to model the utility curves.
 */
public final class CurvesUtility {

    private CurvesUtility() { }

    /**
     * Linear curve.
     * 
     * @param x input value (0.0 to 1.0)
     * @return x
     */
    public static double linear(final double x) {
        return clamp(x);
    }

    /**
     * Inverse linear curve.
     * 
     * @param x input value (0.0 to 1.0)
     * @return 1 - x
     */
    public static double inverse(final double x) {
        return clamp(1.0 - x);
    }

    /**
     * Exponential curve.
     * 
     * @param x input value
     * @param exponent power
     * @return x^exponent
     */
    public static double exponential(final double x, final double exponent) {
        return clamp(Math.pow(x, exponent));
    }

    /**
     * Sigmoid-like curve.
     * 
     * @param x input value
     * @param slope steepness of the curve
     * @param midpoint x-value where y is 0.5
     * @return utility value
     */
    public static double logistic(final double x, final double slope, final double midpoint) {
        final double val = 1.0 / (1.0 + Math.exp(-slope * (x - midpoint)));
        return clamp(val);
    }

    /**
     * Gaussian curve.
     *
     * @param x the input value
     * @param target the optimal value where utility is 1.0 (peak of the bell)
     * @param width controls how wide the sweet spot is
     * @return utility value between 0.0 and 1.0
     */
    public static double gaussian(final double x, final double target, final double width) {
        final double exponent = -Math.pow(x - target, 2) / (2 * Math.pow(width, 2));
        return clamp(Math.exp(exponent));
    }

    /**
     * Clamping the value beetween 0.0 e 1.0.
     * 
     * @param value input
     * @return the input value otherwise 0.0 or 1.0
     */
    public static double clamp(final double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
