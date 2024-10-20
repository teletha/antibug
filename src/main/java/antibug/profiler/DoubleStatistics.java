/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.profiler;

import java.io.Serializable;
import java.util.function.DoubleConsumer;

/**
 * Serializable statistics.
 */
@SuppressWarnings("serial") class DoubleStatistics implements DoubleConsumer, Serializable {
    private long count;

    private double sum;

    private double min = Double.POSITIVE_INFINITY;

    private double max = Double.NEGATIVE_INFINITY;

    // 分散と標準偏差計算用の変数
    private double sumOfSquares;

    private double mean;

    @Override
    public void accept(double value) {
        count++;
        sum += value;
        min = Math.min(min, value);
        max = Math.max(max, value);

        // 更新された平均値を基に平方和を計算
        double delta = value - mean;
        mean += delta / count;
        sumOfSquares += delta * (value - mean); // 累積平方和を更新
    }

    public long getCount() {
        return count;
    }

    public double getSum() {
        return sum;
    }

    public double getMin() {
        return count > 0 ? min : 0.0;
    }

    public double getMax() {
        return count > 0 ? max : 0.0;
    }

    public double getMean() {
        return count > 0 ? sum / count : 0.0;
    }

    public double getVariance() {
        return count > 1 ? sumOfSquares / count : 0.0;
    }

    public double getStandardDeviation() {
        return Math.sqrt(getVariance());
    }

}