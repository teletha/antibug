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

import java.util.function.ToDoubleFunction;

/**
 * This {@code Inspection} enum represents different benchmark inspection items.
 * Each item defines a measurement index (such as speed, memory usage, GC count, etc.)
 * and a function to extract the relevant metric from a {@code MeasurableCode} object.
 */
public enum Inspection {

    /**
     * Time per call, measured in nanoseconds per call.
     * Represents the speed index of the benchmark.
     */
    TimePerCall("ns / call", "#4886CD", 1, code -> code.arithmeticMean.doubleValue()),

    /**
     * Calls per time unit, representing how many calls are made in a given time.
     * Another measure of the speed index.
     */
    CallPerTime("call / time", "#4886CD", 1, code -> code.throughputMean.doubleValue()),

    /**
     * Garbage collection count.
     * Represents a weight index related to the garbage collector activity.
     */
    GC("GC", "#4FB84B", 0.4, code -> code.countGC.getMean()),

    /**
     * Peak memory usage in bytes.
     * Represents a weight index measuring the memory usage during peak execution.
     */
    PeakMemory("Memory", "#BF4F4B", 0.6, code -> code.peakMemory.getMean()),

    /**
     * Peak memory usage as a percentage of the total available memory.
     * Represents a weight index measuring how much of the environment's memory was used during peak
     * execution.
     */
    PeakMemoryRatio("Memory", "#BF4F4B", 0.6, code -> (code.peakMemory.getMean() / code.env.memory()) * 100d);

    /** A label describing the inspection item. */
    final String label;

    /** The color associated with the inspection item, typically used in visualizations. */
    final String color;

    /** The bar ratio representing the visual impact or weight of this inspection item in charts. */
    final double barRatio;

    /**
     * A function that extracts the relevant measurement value from a {@code MeasurableCode}
     * instance.
     */
    private final ToDoubleFunction<MeasurableCode> extractor;

    /**
     * Constructs an {@code Inspection} item with the given label, color, bar ratio, and extractor
     * function.
     *
     * @param label a descriptive label for the inspection item
     * @param color the color to associate with this inspection item (used for visualization)
     * @param barRatio the bar ratio representing this inspection's importance or weight
     * @param extractor a function that extracts a double value from a {@code MeasurableCode} for
     *            this inspection
     */
    private Inspection(String label, String color, double barRatio, ToDoubleFunction<MeasurableCode> extractor) {
        this.label = label;
        this.color = color;
        this.barRatio = barRatio;
        this.extractor = extractor;
    }

    /**
     * Applies the extractor function to obtain the relevant measurement value from the given
     * {@code MeasurableCode}.
     *
     * @param code the measurable code instance
     * @return the extracted measurement value
     */
    public double calculate(MeasurableCode code) {
        return extractor.applyAsDouble(code);
    }
}