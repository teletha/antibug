/*
 * Copyright (C) 2025 The ANTIBUG Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug.profiler;

import java.io.Serializable;
import java.util.function.DoubleConsumer;

/**
 * The Statistics class collects statistical data such as count, sum, min, max,
 * mean, variance, and standard deviation for a stream of double values.
 * This class implements {@link DoubleConsumer}, allowing it to be used in
 * conjunction with streams or other APIs that accept double values.
 * It is also {@link Serializable} to enable object serialization.
 */
@SuppressWarnings("serial")
class Statistics implements DoubleConsumer, Serializable {

    /** The count of values processed. */
    private long count;

    /** The sum of all values. */
    private double sum;

    /** The minimum value observed. Initialized to positive infinity. */
    private double min = Double.POSITIVE_INFINITY;

    /** The maximum value observed. Initialized to negative infinity. */
    private double max = Double.NEGATIVE_INFINITY;

    /** The sum of squares of the differences from the mean. */
    private double sumOfSquares;

    /** The mean (average) of the values processed. */
    private double mean;

    /**
     * Accepts a new double value and updates the statistics.
     * 
     * @param value the value to be added to the statistics.
     */
    @Override
    public void accept(double value) {
        count++;
        sum += value;
        min = Math.min(min, value);
        max = Math.max(max, value);

        double delta = value - mean;
        mean += delta / count;
        sumOfSquares += delta * (value - mean);
    }

    /**
     * Returns the count of values processed.
     * 
     * @return the count of values.
     */
    public long getCount() {
        return count;
    }

    /**
     * Returns the sum of all values.
     * 
     * @return the sum of values.
     */
    public double getSum() {
        return sum;
    }

    /**
     * Returns the minimum value observed.
     * 
     * @return the minimum value, or 0.0 if no values have been processed.
     */
    public double getMin() {
        return count > 0 ? min : 0.0;
    }

    /**
     * Returns the maximum value observed.
     * 
     * @return the maximum value, or 0.0 if no values have been processed.
     */
    public double getMax() {
        return count > 0 ? max : 0.0;
    }

    /**
     * Returns the mean (average) of the values.
     * 
     * @return the mean value, or 0.0 if no values have been processed.
     */
    public double getMean() {
        return count > 0 ? sum / count : 0.0;
    }

    /**
     * Returns the variance of the values.
     * 
     * @return the variance, or 0.0 if fewer than two values have been processed.
     */
    public double getVariance() {
        return count > 1 ? sumOfSquares / count : 0.0;
    }

    /**
     * Returns the standard deviation of the values.
     * 
     * @return the standard deviation, calculated as the square root of the variance.
     */
    public double getStandardDeviation() {
        return Math.sqrt(getVariance());
    }
}