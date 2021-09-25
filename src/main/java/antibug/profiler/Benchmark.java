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

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

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

    /** The number of trial. */
    private final int trials;

    /** The target codes. */
    private final List<MeasurableCode> codes = new ArrayList();

    /**
     * Create Benchmark instance.
     */
    public Benchmark() {
        this(10);
    }

    /**
     * Create Benchmark instance.
     * 
     * @param trials A number of trial.
     */
    public Benchmark(int trials) {
        this.trials = trials;

        if (trials < 10) {
            throw new AssertionError("There is too few trial number of times. (minimus is 10)");
        }

        if (60 < trials) {
            throw new AssertionError("There is too many trial number of times. (maximum is 60)");
        }
    }

    /**
     * Measure an execution speed of the specified code fragment.
     * 
     * @param code A code to be measured.
     */
    public void measure(String name, Callable code) {
        codes.add(new MeasurableCode(name, null, code));
    }

    /**
     * Measure an execution speed of the specified code fragment.
     * 
     * @param code A code to be measured.
     */
    public void measure(String name, Runnable setup, Callable code) {
        codes.add(new MeasurableCode(name, setup, code));
    }

    /**
     * Perform this benchmark and show its result.
     */
    public void perform() {
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
            System.out.println(format(maxName, code.name) + "\tMean : " + format.format(code.arithmeticMean) + "ns/call");
        }
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

    /**
     * 
     */
    private class MeasurableCode {

        /** The code name. */
        private final String name;

        /** The setup. */
        private final Runnable setup;

        /** The code to measure. */
        private final Callable<Object> code;

        /** The result set. */
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
            Runtime.getRuntime().gc();

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
            write("\n");

            // measure actually
            DecimalFormat counterFormat = new DecimalFormat("00");

            for (int i = 0; i < trials; i++) {
                Sample result = measure(frequency);
                samples.add(result);

                // display for user
                write(counterFormat.format(i + 1), " : ", result, "\n");
            }
            write("\n");

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
                long inner = freq / outer;
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
         * <p>
         * Helper method to write conosle message.
         * </p>
         * 
         * @param messages
         */
        private void write(Object... messages) {
            StringBuilder builder = new StringBuilder();

            for (Object message : messages) {
                builder.append(message);
            }

            System.out.print(builder);
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