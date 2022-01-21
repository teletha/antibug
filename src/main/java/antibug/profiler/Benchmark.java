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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.StackWalker.Option;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
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

    /** The benchmark file specifier. */
    private static final String FILE_KEY = "antibug.benchmark.file";

    /** The benchmark file specifier. */
    private static final String FILE = System.getProperty(FILE_KEY);

    /** The benchmark target specifier. */
    private static final String TARGET_KEY = "antibug.benchmark.target";

    /** The benchmark target code name. */
    private static final String TARGET = System.getProperty(TARGET_KEY);

    /** 2 */
    private static final BigInteger TWO = new BigInteger("2");

    /** 1,000,000 */
    private static final BigInteger M = new BigInteger("1000000");

    /** 1,000,000,000 */
    private static final BigInteger G = new BigInteger("1000000000");

    /** Reusable */
    private static final List<GarbageCollectorMXBean> Garbages = ManagementFactory.getGarbageCollectorMXBeans();

    /** The main class. */
    private final Class caller;

    /** The number of iteration. */
    private int trials = 5;

    /** The duration of single trial. */
    private Duration duration = Duration.ofSeconds(1);

    /** The max memory size. */
    private String memory = "48m";

    /** The realtime reporter. */
    private Consumer<String> reporter = System.out::println;

    /** The target codes. */
    private final List<MeasurableCode> codes = new ArrayList();

    /**
     * Create new benchmark process.
     */
    public Benchmark() {
        caller = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE).getCallerClass();
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

    public Benchmark memory(String max) {
        this.memory = max;

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
        return measure(name, null, code);
    }

    /**
     * Measure an execution speed of the specified code fragment.
     * 
     * @param code A code to be measured.
     */
    public Benchmark measure(String name, Runnable setup, Callable code) {
        codes.add(new MeasurableCode(name, setup, code, this));

        return this;
    }

    /**
     * Perform this benchmark and show its result.
     */
    public List<MeasurableCode> perform() {
        if (TARGET == null) {
            List<MeasurableCode> results = new ArrayList();
            for (MeasurableCode code : codes) {
                try {
                    Path file = Files.createTempFile(null, null).toAbsolutePath();

                    List<String> command = new ArrayList();
                    command.add("java");
                    command.add("-Xmx" + memory);
                    command.add("-Xms" + memory);
                    command.add("-D" + FILE_KEY + "=" + file.toString());
                    command.add("-D" + TARGET_KEY + "=" + code.name);
                    command.add("-Dfile.encoding=UTF-8");
                    command.addAll(List.of("-cp", System.getProperty("java.class.path")));
                    command.add(caller.getName());

                    new ProcessBuilder(command).inheritIO().start().waitFor();

                    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file.toFile()))) {
                        results.add((MeasurableCode) in.readObject());
                    } finally {
                        Files.delete(file);
                    }
                } catch (Exception e) {
                    throw new Error(e);
                }
            }

            Collections.sort(results, Comparator.comparing(o -> o.arithmeticMean));

            int maxName = 0;
            for (MeasurableCode code : results) {
                maxName = Math.max(maxName, code.name.length());
            }

            reporter.accept(String.format("%" + maxName + "s\tAverage\t\tPeakMemory\tTotalGC", "\t"));
            for (MeasurableCode code : results) {
                reporter.accept(String
                        .format("%-" + maxName + "s\t%,-6dns/call \t%.2fMB\t\t%d(%dms)", code.name, code.arithmeticMean, code.peakMemory / 1024f / 1024f, code.countGC, code.timeGC));
            }
            reporter.accept("");
            reporter.accept(getPlatformInfo());
        } else {
            MeasurableCode code = codes.stream().filter(c -> c.name.equals(TARGET)).findFirst().get();
            code.perform();
        }
        return codes;
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
    @SuppressWarnings("serial")
    public static class MeasurableCode implements Serializable {

        /** The code name. */
        public final String name;

        /** The setup. */
        private transient final Runnable setup;

        /** The code to measure. */
        private transient final Callable<Object> code;

        /** The duration of single trial. */
        private transient final Benchmark bench;

        /** The sample set. */
        private transient final List<Sample> samples = new ArrayList();

        /** The summary statistics. */
        private BigInteger arithmeticMean;

        /** The summary statistics. */
        private BigInteger variance;

        /** The summary statistics. */
        private double standardDeviation;

        /** The summary statistics. */
        private BigInteger median;

        /** The memory statistics. */
        private long peakMemory;

        /** The number of garbage collections. */
        private long countGC;

        /** The duration of garbage collections. */
        private long timeGC;

        /**
         * @param name
         * @param setup
         * @param code
         */
        private MeasurableCode(String name, Runnable setup, Callable code, Benchmark bench) {
            this.name = Objects.requireNonNull(name);
            this.setup = setup;
            this.code = Objects.requireNonNull(code);
            this.bench = bench;
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
            BigInteger threshold = new BigInteger(String.valueOf(bench.duration.toNanos()));

            while (first.time.compareTo(threshold) != -1) {
                if (5 <= firstTestCount++) {
                    throw new Error("Benchmark task must be able to execute within " + bench.duration + ".");
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

            // measure actually
            DecimalFormat counterFormat = new DecimalFormat("00");
            bench.reporter.accept("     Time\t\tThroughput\t\tAverage\t\tMemory\tGC");

            for (int i = 0; i < bench.trials; i++) {
                performGC();

                Sample result = measure(frequency);
                samples.add(result);

                // display for user
                write(counterFormat.format(i + 1), " : ", result);
            }
            write("");

            analyze();
            serialize();
        }

        /**
         * Measures the execution time of <code>frequency</code> calls of the specified task.
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
                long expectedEndTime = startTime + bench.duration.toNanos();
                while (count < outer && System.nanoTime() <= expectedEndTime) {
                    for (long j = 0; j < inner; j++) {
                        hash ^= code.call().hashCode(); // prevent dead-code-elimination
                    }
                    count++;
                }
                long endTime = System.nanoTime();
                long[] endGC = measureGC();
                long[] memory = measureMemory();

                // calculate execution time
                return new Sample(BigInteger
                        .valueOf(count * inner), endTime - startTime, hash, endGC[0] - startGC[0], endGC[1] - startGC[1], memory);
            } catch (Throwable e) {
                throw new AssertionError(e);
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
                peakMemory = Math.max(sample.peakMemory, peakMemory);
                countGC += sample.countGC;
                timeGC += sample.timeGC;
            }
        }

        /**
         * Serialize this object.
         */
        private void serialize() {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(FILE)))) {
                out.writeObject(this);
                out.flush();
            } catch (Exception e) {
                throw new Error(e);
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

            bench.reporter.accept(builder.toString());
        }

        /**
         * Perform GC absolutely.
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
         * Analyze garbage collection.
         * 
         * @return
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
         * Analyze memory usage.
         * 
         * @return
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
         * Clean up memory analyzer.
         */
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
        private final long peakMemory;

        /***
         * Create MeasurementResult instance.
         * 
         * @param frequency
         * @param time
         */
        Sample(BigInteger frequency, long time, int hash, long countGC, long timeGC, long[] memory) {
            this(frequency, new BigInteger(String.valueOf(time)), hash, countGC, timeGC, memory);
        }

        /**
         * Create MeasurementResult instance.
         * 
         * @param frequency
         * @param time
         */
        Sample(BigInteger frequency, BigInteger time, int hash, long countGC, long timeGC, long[] memory) {
            this.time = time;
            this.hash = hash;
            this.timesPerExecution = (frequency.equals(ZERO)) ? ZERO : time.divide(frequency);
            this.executionsPerSecond = (time.equals(ZERO)) ? ZERO : frequency.multiply(Benchmark.G).divide(time);
            this.countGC = countGC;
            this.timeGC = timeGC;
            this.peakMemory = memory[0];
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
            return String.format("%,dms  \t%,-6dcall/s \t%,-6dns/call \t%.2fMB \t%d(%dms)", time
                    .divide(M), executionsPerSecond, timesPerExecution, peakMemory / 1024f / 1024f, countGC, timeGC);
        }
    }
}