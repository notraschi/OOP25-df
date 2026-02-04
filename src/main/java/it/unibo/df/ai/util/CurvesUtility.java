package it.unibo.df.ai.util;

public class CurvesUtility {

    /**
     * Linear curve.
     * @param x input value (0.0 to 1.0)
     * @return x
     */
    public static double linear(double x) {
        return clamp(x);
    }

    /**
     * Inverse linear curve.
     * 
     * @param x input value (0.0 to 1.0)
     * @return 1 - x
     */
    public static double inverse(double x) {
        return clamp(1.0 - x);
    }

    /**
     * Exponential curve, grows slowly at first, then rapidly.
     * 
     * @param x input value
     * @param exponent power (e.g., 2 for quadratic)
     * @return x^exponent
     */
    public static double exponential(double x, double exponent) {
        return clamp(Math.pow(x, exponent));
    }

    /**
     * Logistic (Sigmoid-like) curve.
     * 
     * @param x input value
     * @param slope steepness of the curve
     * @param midpoint x-value where y is 0.5
     * @return utility value
     */
    public static double logistic(double x, double slope, double midpoint) {
        double val = 1.0 / (1.0 + Math.exp(-slope * (x - midpoint)));
        return clamp(val);
    }

    /**
     * Gaussian (Bell) curve.
     * Useful when there is a specific "sweet spot" value.
     * Returns 1.0 when x == target, and drops to 0.0 as x moves away.
     *
     * @param x the input value
     * @param target the optimal value where utility is 1.0 (peak of the bell)
     * @param width controls how wide the sweet spot is
     * @return utility value between 0.0 and 1.0
     */
    public static double gaussian(double x, double target, double width) {
        // Formula: e^(-(x - target)^2 / (2 * width^2))
        double exponent = -Math.pow(x - target, 2) / (2 * Math.pow(width, 2));
        return clamp(Math.exp(exponent));
    }

    /**
     * Helper to clamp values between 0.0 and 1.0.
     */
    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
