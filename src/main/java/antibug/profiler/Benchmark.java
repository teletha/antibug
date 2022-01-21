/*
 * Copyright (C) 2021 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug.profiler;

import static java.math.BigInteger.*;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class Benchmark {

    /** 2 */
    private static final BigInteger TWO = new BigInteger("2");

    /** 1,000 */
    private static final BigInteger K = new BigInteger("1000");

    /** 1,000,000 */
    private static final BigInteger M = new BigInteger("1000000");

    /** 1,000,000,000 */
    private static final BigInteger G = new BigInteger("1000000000");

    /** Reusable */
    private static final List<GarbageCollectorMXBean> Garbages = ManagementFactory.getGarbageCollectorMXBeans();

    /** The number of iteration. */
    private int trials = 5;

    /** The duration of single trial. */
    private Duration duration = Duration.ofSeconds(1);

    /** The realtime reporter. */
    private Consumer<String> reporter = System.out::println;

    /** The target codes. */
    private final List<MeasurableCode> codes = new ArrayList();

    /**
     * Create new benchmark process.
     */
    public Benchmark() {
    }

    /**
     * Configure the number of trial.
     * 
     * @param trials A number of trials. (3 <= trials <= 30)
     * @return Chainable configuration.
     */
    public Benchmark trial(int trials) {
        if (trials < 3) {
            throw new AssertionError("There is too few trial number of times. (minimus is 3)");
        }

        if (30 < trials) {
            throw new AssertionError("There is too many trial number of times. (maximum is 30)");
        }
        this.trials = trials;

        return this;
    }

    /**
     * Configure the duration of trial.
     * 
     * @param time A duration of trial.
     * @param unit A duration unit of trial.
     * @return Chainable configuration.
     */
    public Benchmark duration(int time, TimeUnit unit) {
        return duration(Duration.of(time, unit.toChronoUnit()));
    }

    /**
     * Configure the duration of trial.
     * 
     * @param time A duration of trial.
     * @param unit A duration unit of trial.
     * @return Chainable configuration.
     */
    public Benchmark duration(int time, ChronoUnit unit) {
        return duration(Duration.of(time, unit));
    }

    /**
     * Configure the duration of trial.
     * 
     * @param duration A duration of trial.
     * @return Chainable configuration.
     */
    public Benchmark duration(Duration duration) {
        if (duration == null) {
            throw new AssertionError("You must specify duration of trial.");
        }

        long mills = duration.toMillis();

        if (mills < 100) {
            throw new AssertionError("There is too short trial duration. (minimus is 100ms)");
        }

        if (3 * 60 * 1000 < mills) {
            throw new AssertionError("There is too long  trial duration. (maximum is 3min)");
        }
        this.duration = duration;

        return this;
    }

    /**
     * Configure the progress reporter.
     * 
     * @param reporter
     * @return
     */
    public Benchmark progress(Consumer<String> reporter) {
        if (reporter != null) {
            this.reporter = reporter;
        }
        return this;
    }

    /**
     * Discard any message on the system output.
     * 
     * @return
     */
    public Benchmark discardSystemOutput() {
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        System.setErr(new PrintStream(OutputStream.nullOutputStream()));
        return this;
    }

    /**
     * Measure an execution speed of the specified code fragment.
     * 
     * @param code A code to be measured.
     */
    public Benchmark measure(String name, Callable code) {
        codes.add(new MeasurableCode(name, null, code));

        return this;
    }

    /**
     * Measure an execution speed of the specified code fragment.
     * 
     * @param code A code to be measured.
     */
    public Benchmark measure(String name, Runnable setup, Callable code) {
        codes.add(new MeasurableCode(name, setup, code));

        return this;
    }

    /**
     * Perform this benchmark and show its result.
     */
    public List<MeasurableCode> perform() {
        // Pre execution to avoid callee optimization
        for (MeasurableCode code : codes) {
            code.measure(TEN);
        }

        for (MeasurableCode code : codes) {
            code.perform();
        }

        Collections.sort(codes, Comparator.comparing(o -> o.arithmeticMean));

        int maxName = 0;
        for (MeasurableCode code : codes) {
            maxName = Math.max(maxName, code.name.length());
        }

        DecimalFormat format = new DecimalFormat();
        for (MeasurableCode code : codes) {
            reporter.accept(format(maxName, code.name) + "\tMean : " + format
                    .format(code.arithmeticMean) + "ns/call \tMemory : " + (code.memory / 1024 / 1024) + "MB \tGC : " + code.countGC);
        }
        reporter.accept("");
        reporter.accept(getPlatformInfo());

        return codes;
    }

    /**
     * Format.
     * 
     * @param min
     * @param text
     * @return
     */
    private static String format(int min, String text) {
        int length = text.length();
        if (min < length) {
            return text;
        } else {
            return text + " ".repeat(min - text.length());
        }
    }

    public static final String getPlatformInfo() {
        Runtime runtime = Runtime.getRuntime();

        StringBuilder builder = new StringBuilder();
        builder.append("Java \t")
                .append(System.getProperty("java.vendor"))
                .append(" ")
                .append(Runtime.version())
                .append("@")
                .append(System.getProperty("java.class.version"))
                .append("\n");

        builder.append("OS \t")
                .append(System.getProperty("os.name"))
                .append(" ")
                .append(System.getProperty("os.arch"))
                .append(" ")
                .append(System.getProperty("os.version"))
                .append("\n");

        builder.append("PC \tCPU Core Size: ")
                .append(runtime.availableProcessors())
                .append(" ")
                .append("Memory: ")
                .append(runtime.maxMemory() / 1024 / 1024)
                .append("MB\n");

        return builder.toString();
    }

    /**
     * 
     */
    public class MeasurableCode {

        /** The code name. */
        public final String name;

        /** The setup. */
        private final Runnable setup;

        /** The code to measure. */
        private final Callable<Object> code;

        /** The sample set. */
        private final List<Sample> samples = new ArrayList();

        /** The summary statistic. */
        private BigInteger arithmeticMean;

        /** The summary statistic. */
        private BigInteger variance;

        /** The summary statistic. */
        private double standardDeviation;

        /** The summary statistic. */
        private BigInteger median;

        private long memory;

        /** The number of garbage collections. */
        private long countGC;

        /** The duration of garbage collections. */
        private long timeGC;

        /**
         * @param name
         * @param setup
         * @param code
         */
        private MeasurableCode(String name, Runnable setup, Callable code) {
            this.name = Objects.requireNonNull(name);
            this.setup = setup;
            this.code = Objects.requireNonNull(code);
        }

        /**
         * Perform code profiling.
         */
        private void perform() {
            write("Warming up ", name);

            if (setup != null) setup.run();

            Sample first = measure(ONE);

            if (first.hash == 0) {
                throw new Error("Benckmark task must return not null but something.");
            }

            int firstTestCount = 0;
            BigInteger threshold = new BigInteger(String.valueOf(duration.toNanos()));

            while (first.time.compareTo(threshold) != -1) {
                if (5 <= firstTestCount++) {
                    throw new Error("Benchmark task must be able to execute within " + duration + ".");
                }
                first = measure(ONE);
            }

            // warmup JVM and decided the number of executions
            BigInteger frequency = ONE;

            while (true) {
                Sample result = measure(frequency);

                if (result.time.compareTo(threshold) == -1) {
                    frequency = frequency.multiply(TWO);
                } else {
                    frequency = frequency.multiply(threshold).divide(result.time);
                    break;
                }
            }

            Runtime.getRuntime().gc();

            // measure actually
            DecimalFormat counterFormat = new DecimalFormat("00");

            for (int i = 0; i < trials; i++) {
                Sample result = measure(frequency);
                samples.add(result);

                // display for user
                write(counterFormat.format(i + 1), " : ", result);
            }
            write("");

            analyze();
        }

        /**
         * Measures the execution time of <code>frequency</code> calls of the specified task.
         */
        private Sample measure(BigInteger frequency) {
            int hash = 0;

            try {
                measureMemory();
                long[] startGC = measureGarbageCollection();
                long freq = frequency.longValue();
                long outer = 5000 <= freq ? 50 : 1000 <= freq ? 20 : 100 <= freq ? 10 : 1;
                long inner = Math.round(freq / outer);
                long count = 0;
                resetMemory();

                // measure actually
                long start = System.nanoTime();
                long expectedEnd = start + duration.toNanos();
                for (; (count < outer && System.nanoTime() <= expectedEnd); count++) {
                    for (long j = 0; j < inner; j++) {
                        hash ^= code.call().hashCode(); // prevent dead-code-elimination
                    }
                }
                long end = System.nanoTime();
                long[] endGC = measureGarbageCollection();
                long memory = measureMemory();

                // calculate execution time
                return new Sample(BigInteger
                        .valueOf(count * inner), end - start, hash, endGC[0] - startGC[0], endGC[1] - startGC[1], memory);
            } catch (Throwable e) {
                throw new Error(e);
            }
        }

        /**
         * Analyze result.
         */
        private void analyze() {
            // Prepare
            BigInteger size = BigInteger.valueOf(samples.size());

            // Arithmetic Mean
            BigInteger sum = ZERO;

            for (Sample sample : samples) {
                sum = sum.add(sample.timesPerExecution);
            }
            arithmeticMean = sum.divide(size);

            // Variance and Standard Deviation
            sum = ZERO;

            for (Sample sample : samples) {
                sum = sum.add(sample.timesPerExecution.subtract(arithmeticMean).pow(2));
            }
            variance = sum.divide(size);
            standardDeviation = Math.sqrt(sum.divide(size).subtract(ONE).doubleValue());

            // Find outlier and remove it
            Iterator<Sample> iterator = samples.iterator();

            while (iterator.hasNext()) {
                Sample sample = iterator.next();
                sample.isOutlier = 3 < Math.abs(sample.timesPerExecution.subtract(arithmeticMean).doubleValue() / standardDeviation);

                if (sample.isOutlier) {
                    iterator.remove();
                }
            }

            // Arithmetic Mean (re-calculate)
            sum = ZERO;

            for (Sample sample : samples) {
                sum = sum.add(sample.timesPerExecution);
            }
            arithmeticMean = sum.divide(size);

            // Median
            int newSize = samples.size();

            if (newSize % 2 == 1) {
                median = samples.get((newSize + 1) / 2).timesPerExecution;
            } else {
                BigInteger one = samples.get(newSize / 2).timesPerExecution;
                BigInteger other = samples.get(newSize / 2 + 1).timesPerExecution;

                median = one.add(other).divide(TWO);
            }

            for (Sample sample : samples) {
                memory = Math.max(sample.memory, memory);
                countGC += sample.countGC;
            }
        }

        /**
         * Helper method to write conosle message.
         * 
         * @param messages
         */
        private void write(Object... messages) {
            StringBuilder builder = new StringBuilder();

            for (Object message : messages) {
                builder.append(message);
            }

            reporter.accept(builder.toString());
        }

        /**
         * Analyze garbage collection.
         * 
         * @return
         */
        private long[] measureGarbageCollection() {
            long count = 0;
            long time = 0;

            for (int i = 0, size = Garbages.size(); i < size; i++) {
                GarbageCollectorMXBean garbage = Garbages.get(i);
                count += garbage.getCollectionCount();
                time += garbage.getCollectionTime();
            }
            return new long[] {count, time};
        }

        private long measureMemory() {
            long size = 0;

            for (MemoryPoolMXBean memory : ManagementFactory.getMemoryPoolMXBeans()) {
                if (memory.getType() == MemoryType.HEAP) {
                    size += memory.getPeakUsage().getCommitted();
                }
            }
            return size;
        }

        private void resetMemory() {
            for (MemoryPoolMXBean memory : ManagementFactory.getMemoryPoolMXBeans()) {
                memory.resetPeakUsage();
            }
        }

        /**
         * The summary statistic.
         * 
         * @return
         */
        public int getMean() {
            return arithmeticMean.intValueExact();
        }

        /**
         * The summary statistic.
         * 
         * @return
         */
        public int getVariance() {
            return variance.intValueExact();
        }

        /**
         * The summary statistic.
         * 
         * @return
         */
        public double getStandardDeviation() {
            return standardDeviation;
        }

        /**
         * The summary statistic.
         * 
         * @return
         */
        public int getMedian() {
            return median.intValueExact();
        }
    }

    /**
     * 
     */
    private static class Sample implements Comparable<Sample> {

        /** The execution count. */
        private final BigInteger frequency;

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
        private final long memory;

        /***
         * Create MeasurementResult instance.
         * 
         * @param frequency
         * @param time
         */
        Sample(BigInteger frequency, long time, int hash, long countGC, long timeGC, long memory) {
            this(frequency, new BigInteger(String.valueOf(time)), hash, countGC, timeGC, memory);
        }

        /**
         * Create MeasurementResult instance.
         * 
         * @param frequency
         * @param time
         */
        Sample(BigInteger frequency, BigInteger time, int hash, long countGC, long timeGC, long memory) {
            this.frequency = frequency;
            this.time = time;
            this.hash = hash;
            this.timesPerExecution = (frequency.equals(ZERO)) ? ZERO : time.divide(frequency);
            this.executionsPerSecond = (time.equals(ZERO)) ? ZERO : frequency.multiply(Benchmark.G).divide(time);
            this.countGC = countGC;
            this.timeGC = timeGC;
            this.memory = memory;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(Sample o) {
            return timesPerExecution.compareTo(o.timesPerExecution);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            DecimalFormat format = new DecimalFormat();

            StringBuilder builder = new StringBuilder();
            builder.append(format(7, format.format(time.divide(Benchmark.M)) + "ms"));
            builder.append("   ");
            builder.append(format.format(executionsPerSecond));
            builder.append("call/s   ");
            builder.append(format.format(timesPerExecution));
            builder.append("ns/call   ");
            builder.append(Math.round(memory / 1024)).append("KB");
            builder.append("  ").append(countGC);

            if (isOutlier) {
                builder.append("   ☠");
            }

            return builder.toString();
        }
    }
}