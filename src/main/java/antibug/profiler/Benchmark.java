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
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
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

    /** The threshold of measurement time. (unit: ns) */
    private static final BigInteger threshold = new BigInteger("1").multiply(G);

    /** The number of trials. */
    private int trials = 5;

    /** The realtime reporter. */
    private Consumer<String> reporter = System.out::println;

    /** The target codes. */
    private final List<MeasurableCode> codes = new ArrayList();

    /**
     * Create Benchmark instance.
     */
    public Benchmark() {
    }

    /**
     * Configure the number of trials.
     * 
     * @param trials
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

        // API definition
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
            reporter.accept(format(maxName, code.name) + "\tMean : " + format.format(code.arithmeticMean) + "ns/call");
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

            if (first.hash == 0) throw new Error("Benckmark task must return not null but something.");
            if (first.time.compareTo(threshold) != -1) throw new Error("Benchmark task must be able to execute within 1 second.");

            // warmup JVM and decided the number of executions
            BigInteger frequency = ONE;

            while (true) {
                Sample result = measure(frequency);

                if (result.time.compareTo(threshold) == -1) {
                    frequency = frequency.multiply(TWO);
                } else {
                    frequency = frequency.multiply(G).divide(result.time);
                    break;
                }
            }

            Runtime.getRuntime().gc();

            // measure actually
            DecimalFormat counterFormat = new DecimalFormat("00");

            MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
            System.out.println(memory.getHeapMemoryUsage().getUsed() + memory.getNonHeapMemoryUsage().getUsed());
            System.out.println(ManagementFactory.getGarbageCollectorMXBeans().stream().mapToLong(x -> x.getCollectionCount()).count());

            for (int i = 0; i < trials; i++) {
                Sample result = measure(frequency);
                samples.add(result);

                // display for user
                write(counterFormat.format(i + 1), " : ", result);
            }
            System.out.println(memory.getHeapMemoryUsage().getUsed() + memory.getNonHeapMemoryUsage().getUsed());
            System.out.println(ManagementFactory.getGarbageCollectorMXBeans().stream().mapToLong(x -> x.getCollectionCount()).count());
            write("");

            analyze();
        }

        /**
         * Measures the execution time of <code>frequency</code> calls of the specified task.
         */
        private Sample measure(BigInteger frequency) {
            int hash = 0;

            try {
                long freq = frequency.longValue();
                long outer = 5000 <= freq ? 50 : 1000 <= freq ? 20 : 100 <= freq ? 10 : 1;
                long inner = Math.round(freq / outer);
                long count = 0;

                // measure actually
                long start = System.nanoTime();
                for (; (count < outer && System.nanoTime() - start <= 1000000000); count++) {
                    for (long j = 0; j < inner; j++) {
                        hash ^= code.call().hashCode(); // prevent dead-code-elimination
                    }
                }
                long end = System.nanoTime();

                // calculate execution time
                return new Sample(BigInteger.valueOf(count * inner), end - start, hash);
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

        /***
         * Create MeasurementResult instance.
         * 
         * @param frequency
         * @param time
         */
        Sample(BigInteger frequency, long time, int hash) {
            this(frequency, new BigInteger(String.valueOf(time)), hash);
        }

        /**
         * Create MeasurementResult instance.
         * 
         * @param frequency
         * @param time
         */
        Sample(BigInteger frequency, BigInteger time, int hash) {
            this.time = time;
            this.hash = hash;
            this.timesPerExecution = (frequency.equals(ZERO)) ? ZERO : time.divide(frequency);
            this.executionsPerSecond = (time.equals(ZERO)) ? ZERO : frequency.multiply(Benchmark.G).divide(time);
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
            builder.append("ns/call");

            if (isOutlier) {
                builder.append("   ☠");
            }

            return builder.toString();
        }
    }
}