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

import static java.math.BigInteger.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * {@code MeasurableCode} is a class that represents a unit of code whose performance and memory
 * usage are measured and benchmarked. It collects statistics such as execution time, garbage
 * collection (GC) counts, peak memory usage, and variance. It is designed to benchmark code
 * execution over a series of trials and to provide statistical analysis of the results.
 *
 * <p>Fields such as {@code throughputMean}, {@code arithmeticMean}, {@code variance}, and
 * {@code standardDeviation} store summary statistics after the benchmark is completed.</p>
 *
 * <p>This class also handles memory measurement and garbage collection monitoring. It is designed
 * to allow code execution profiling while avoiding dead-code elimination by ensuring that code
 * return values are utilized.</p>
 */
@SuppressWarnings("serial")
public class MeasurableCode implements Serializable {

    /** 1,000,000 */
    private static final BigInteger M = new BigInteger("1000000");

    /** 1,000,000,000 */
    private static final BigInteger G = new BigInteger("1000000000");

    /** 2 */
    private static final BigInteger TWO = new BigInteger("2");

    /** Reusable */
    private static final List<GarbageCollectorMXBean> Garbages = ManagementFactory.getGarbageCollectorMXBeans();

    /** The code name. */
    public final String name;

    /** The detected code version. */
    transient String version;

    /** The setup. */
    private transient final Runnable setup;

    /** The code to measure. */
    private transient final Callable<Object> code;

    /** The duration of single trial. */
    private transient final Benchmark bench;

    /** The sample set. */
    private transient final List<MeasurableCode.Sample> samples = new ArrayList();

    /** The environment. */
    final Environment env;

    /** The summary statistics. */
    BigInteger throughputMean;

    /** The summary statistics. */
    BigInteger arithmeticMean;

    /** The summary statistics. */
    private BigInteger variance;

    /** The summary statistics. */
    private double standardDeviation;

    /** The memory statistics. */
    final Statistics peakMemory = new Statistics();

    /** The number of garbage collections. */
    final Statistics countGC = new Statistics();

    /** The duration of garbage collections. */
    final Statistics timeGC = new Statistics();

    /**
     * @param name
     * @param setup
     * @param code
     */
    MeasurableCode(String name, Runnable setup, Callable code, Benchmark bench, Environment env) {
        this.name = Objects.requireNonNull(name);
        this.setup = setup;
        this.code = Objects.requireNonNull(code);
        this.bench = bench;
        this.env = Objects.requireNonNull(env);
    }

    /**
     * Performs the benchmarking of the specified code block.
     * This method handles warm-up, measures execution time and other statistics,
     * and analyzes the results.
     *
     * <p>It first warms up the JVM, then decides on the number of executions for each trial,
     * and finally runs the benchmark, collecting data such as execution time, throughput,
     * memory usage, and garbage collection stats.</p>
     */
    void perform() {
        write("Warming up ", name, " [", bench.caller.getSimpleName(), "]");

        if (setup != null) setup.run();

        MeasurableCode.Sample first = measure(ONE);

        if (first.hash == 0) {
            throw new Error("Benckmark task must return not null but something.");
        }

        int firstTestCount = 0;
        BigInteger threshold = new BigInteger(String.valueOf(env.duration.toNanos()));

        while (first.time.compareTo(threshold) != -1) {
            if (5 <= firstTestCount++) {
                throw new Error("Benchmark task must be able to execute within " + env.duration + ".");
            }
            first = measure(ONE);
        }

        // warmup JVM and decided the number of executions
        BigInteger frequency = ONE;

        if (env.limit != 0) {
            frequency = BigInteger.valueOf(env.limit);
            for (int i = 0; i < 5; i++) {
                measure(frequency);
            }
        } else {
            while (true) {
                MeasurableCode.Sample result = measure(frequency);

                if (result.time.compareTo(threshold) == -1) {
                    frequency = frequency.multiply(TWO);
                } else {
                    frequency = frequency.multiply(threshold).divide(result.time);
                    break;
                }
            }
        }

        // measure actually
        DecimalFormat counterFormat = new DecimalFormat("00");
        bench.reporter.accept("     Time\t\tThroughput\t\tAverage\t\tMemory\tGC");

        for (int i = 0; i < env.trials; i++) {
            performGC();

            MeasurableCode.Sample result = measure(frequency);
            samples.add(result);

            // display for user
            write(counterFormat.format(i + 1), " : ", result);
        }
        write("");

        analyze();
        serialize(this);
    }

    /**
     * Measures the execution time of a specified number of code executions (frequency).
     * 
     * @param frequency the number of executions to measure
     * @return a {@code Sample} containing the measurement results
     */
    private Sample measure(BigInteger frequency) {
        int hash = 0;

        try {
            long[] startGC = measureGC();
            long freq = frequency.longValue();
            long outer = 5000 <= freq ? 100 : 1000 <= freq ? 50 : 100 <= freq ? 25 : 1;
            long inner = Math.round(freq / outer);
            long count = 0;
            resetMemory();

            // measure actually
            long startTime = System.nanoTime();
            long expectedEndTime = startTime + env.duration.toNanos();
            while (count < outer && System.nanoTime() <= expectedEndTime) {
                for (long j = 0; j < inner; j++) {
                    hash ^= code.call().hashCode(); // prevent dead-code-elimination
                }
                count++;
            }
            long endTime = System.nanoTime();
            long[] memory = measureMemory();
            long[] endGC = measureGC();

            // calculate execution time
            return new Sample(BigInteger
                    .valueOf(count * inner), endTime - startTime, hash, endGC[0] - startGC[0], endGC[1] - startGC[1], memory);
        } catch (OutOfMemoryError e) {
            throw e;
        } catch (Throwable e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Analyzes the results of the benchmark, computing statistics such as arithmetic mean,
     * variance, and standard deviation. Also detects and removes outliers from the samples.
     */
    private void analyze() {
        // Prepare
        BigInteger size = BigInteger.valueOf(samples.size());

        // Arithmetic Mean
        BigInteger sum = ZERO;

        for (MeasurableCode.Sample sample : samples) {
            sum = sum.add(sample.timesPerExecution);
        }
        arithmeticMean = sum.divide(size);

        // Variance and Standard Deviation
        sum = ZERO;

        for (MeasurableCode.Sample sample : samples) {
            sum = sum.add(sample.timesPerExecution.subtract(arithmeticMean).pow(2));
        }
        variance = sum.divide(size);
        standardDeviation = Math.sqrt(sum.divide(size).subtract(ONE).doubleValue());

        // Find outlier and remove it
        Iterator<MeasurableCode.Sample> iterator = samples.iterator();

        while (iterator.hasNext()) {
            MeasurableCode.Sample sample = iterator.next();
            sample.isOutlier = 3 < Math.abs(sample.timesPerExecution.subtract(arithmeticMean).doubleValue() / standardDeviation);

            if (sample.isOutlier) {
                iterator.remove();
            }
        }

        // Arithmetic Mean (re-calculate)
        sum = ZERO;
        for (MeasurableCode.Sample sample : samples) {
            sum = sum.add(sample.timesPerExecution);
        }
        arithmeticMean = sum.divide(size);

        // Arithmetic Mean (re-calculate)
        sum = ZERO;
        for (MeasurableCode.Sample sample : samples) {
            sum = sum.add(sample.executionsPerSecond);
        }
        throughputMean = sum.divide(size);

        for (MeasurableCode.Sample sample : samples) {
            peakMemory.accept(sample.peakMemory);
            countGC.accept(sample.countGC);
            timeGC.accept(sample.timeGC);
        }
    }

    /**
     * Serializes this {@code MeasurableCode} instance to a file.
     * 
     * @param obj the object to serialize
     */
    private void serialize(Serializable obj) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(Benchmark.FILE)))) {
            out.writeObject(obj);
            out.flush();
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    /**
     * Writes messages to the console or report.
     * 
     * @param messages the messages to write
     */
    private void write(Object... messages) {
        StringBuilder builder = new StringBuilder();

        for (Object message : messages) {
            builder.append(message);
        }

        bench.reporter.accept(builder.toString());
    }

    /**
     * Forces garbage collection to occur and waits for completion.
     */
    private void performGC() {
        long beforeGC = measureGC()[0];

        System.gc();

        do {
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } while (measureGC()[0] < beforeGC + 1);
    }

    /**
     * Measures the current state of garbage collection.
     * 
     * @return an array containing the number of GCs and the total GC time
     */
    private long[] measureGC() {
        long count = 0;
        long time = 0;
        for (int i = 0, size = Garbages.size(); i < size; i++) {
            GarbageCollectorMXBean garbage = Garbages.get(i);
            count += garbage.getCollectionCount();
            time += garbage.getCollectionTime();
        }
        return new long[] {count, time};
    }

    /**
     * Measures the current memory usage, including peak and used memory.
     * 
     * @return an array containing the peak and used memory values
     */
    private long[] measureMemory() {
        long peak = 0;
        long used = 0;

        for (MemoryPoolMXBean memory : ManagementFactory.getMemoryPoolMXBeans()) {
            if (memory.getType() == MemoryType.HEAP) {
                peak += memory.getPeakUsage().getUsed();
                used += memory.getUsage().getUsed();
            }
        }
        return new long[] {peak, used};
    }

    /**
     * Resets the memory usage statistics to prepare for a new measurement.
     */
    private void resetMemory() {
        for (MemoryPoolMXBean memory : ManagementFactory.getMemoryPoolMXBeans()) {
            memory.resetPeakUsage();
        }
    }

    /**
     * Returns the arithmetic mean of the benchmark results.
     * 
     * @return the arithmetic mean
     */
    public int getMean() {
        return arithmeticMean.intValueExact();
    }

    /**
     * Returns the variance of the benchmark results.
     * 
     * @return the variance
     */
    public int getVariance() {
        return variance.intValueExact();
    }

    /**
     * Returns the standard deviation of the benchmark results.
     * 
     * @return the standard deviation
     */
    public double getStandardDeviation() {
        return standardDeviation;
    }

    /**
     * {@code Sample} is a nested class that represents a single trial of code measurement,
     * storing results such as execution time, memory usage, and GC statistics.
     */
    private static class Sample implements Comparable<MeasurableCode.Sample> {

        /** The measurement time. */
        private final BigInteger time;

        /** The measurement time per one execution of the specified task. */
        private final BigInteger timesPerExecution;

        /** The number of task executions per one second. */
        private final BigInteger executionsPerSecond;

        /** The check sum. */
        private final int hash;

        /** The state. */
        private boolean isOutlier = false;

        /** The number of garbage collections. */
        private final long countGC;

        /** The duration of garbage collections. */
        private final long timeGC;

        /** The peak memory usage. */
        private final double peakMemory;

        /**
         * Constructs a new {@code Sample} with the given measurement results.
         * 
         * @param frequency the number of task executions
         * @param time the total time for the executions, in nanoseconds
         * @param hash the hash code result to prevent dead-code elimination
         * @param countGC the number of GCs that occurred during this sample
         * @param timeGC the total time spent in GC during this sample
         * @param memory the memory usage data
         */
        private Sample(BigInteger frequency, long time, int hash, long countGC, long timeGC, long[] memory) {
            this.time = new BigInteger(String.valueOf(time));
            this.hash = hash;
            this.timesPerExecution = (frequency.equals(ZERO)) ? ZERO : this.time.divide(frequency);
            this.executionsPerSecond = (this.time.equals(ZERO)) ? ZERO : frequency.multiply(G).divide(this.time);
            this.countGC = countGC;
            this.timeGC = timeGC;
            this.peakMemory = memory[0] / 1024d / 1024d;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(MeasurableCode.Sample o) {
            return timesPerExecution.compareTo(o.timesPerExecution);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return String.format("%,dms  \t%,-6dcall/s \t%,-6dns/call \t%.2fMB \t%d(%dms)", time
                    .divide(M), executionsPerSecond, timesPerExecution, peakMemory, countGC, timeGC);
        }
    }
}