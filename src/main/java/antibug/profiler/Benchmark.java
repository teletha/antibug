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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.StackWalker.Option;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * The Benchmark class provides methods for measuring and reporting
 * the performance of code execution in a benchmarking process.
 * It supports visualization and configurable reporting options.
 */
@SuppressWarnings("serial")
public final class Benchmark extends Environment<Benchmark> {

    /** The original stream. */
    static final PrintStream origina = System.out;

    /** The benchmark file specifier. */
    static final String FILE_KEY = "antibug.benchmark.file";

    /** The benchmark file specifier. */
    static final String FILE = System.getProperty(FILE_KEY);

    /** The benchmark target specifier. */
    static final String TARGET_KEY = "antibug.benchmark.target";

    /** The benchmark target code name. */
    static final String TARGET = System.getProperty(TARGET_KEY);

    /** The main class. */
    final Class caller;

    /** The report option. */
    private Inspection[] visualize = new Inspection[0];

    /** The realtime reporter. */
    Consumer<String> reporter = System.out::println;

    /** The target codes. */
    private final List<MeasurableCode> codes = new ArrayList();

    /**
     * Create new benchmark process.
     */
    public Benchmark() {
        caller = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE).getCallerClass();
    }

    /**
     * Configures the progress reporter for output messages.
     * 
     * @param reporter a Consumer that accepts a String message to report progress.
     * @return the current Benchmark instance for method chaining.
     */
    public Benchmark progress(Consumer<String> reporter) {
        if (reporter != null) {
            this.reporter = reporter;
        }
        return this;
    }

    /**
     * Discards any output messages sent to the system output.
     * 
     * @return the current Benchmark instance for method chaining.
     */
    public Benchmark discardSystemOutput() {
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        System.setErr(new PrintStream(OutputStream.nullOutputStream()));
        return this;
    }

    /**
     * Configures the report visualization with default inspection items.
     * 
     * @return the current Benchmark instance for method chaining.
     */
    public Benchmark visualize() {
        return visualize(Inspection.TimePerCall, Inspection.GC);
    }

    /**
     * Configures the report visualization with specified inspection items.
     * 
     * @param items an array of Inspection items to visualize.
     * @return the current Benchmark instance for method chaining.
     */
    public Benchmark visualize(Inspection... items) {
        this.visualize = items;
        return this;
    }

    /**
     * Measures the execution speed of the specified code fragment.
     * 
     * @param name the name of the code block to be measured.
     * @param code the Callable code to be measured.
     * @return the current Benchmark instance for method chaining.
     */
    public Benchmark measure(String name, Callable code) {
        return measure(name, (Runnable) null, code);
    }

    /**
     * Measures the execution speed of the specified code fragment with a given environment.
     * 
     * @param name the name of the code block to be measured.
     * @param environment a UnaryOperator to modify the benchmark environment.
     * @param code the Callable code to be measured.
     * @return the current Benchmark instance for method chaining.
     */
    public Benchmark measure(String name, UnaryOperator<Environment> environment, Callable code) {
        return measure(name, environment, null, code);
    }

    /**
     * Measures the execution speed of the specified code fragment with a setup action.
     * 
     * @param name the name of the code block to be measured.
     * @param setup a Runnable action to set up the environment before measuring.
     * @param code the Callable code to be measured.
     * @return the current Benchmark instance for method chaining.
     */
    public Benchmark measure(String name, Runnable setup, Callable code) {
        return measure(name, UnaryOperator.identity(), setup, code);
    }

    /**
     * Measures the execution speed of the specified code fragment with setup and environment.
     * 
     * @param name the name of the code block to be measured.
     * @param environment a UnaryOperator to modify the benchmark environment.
     * @param setup a Runnable action to set up the environment before measuring.
     * @param code the Callable code to be measured.
     * @return the current Benchmark instance for method chaining.
     */
    public Benchmark measure(String name, UnaryOperator<Environment> environment, Runnable setup, Callable code) {
        codes.add(new MeasurableCode(name, setup, code, this, environment.apply(snapshot())));
        return this;
    }

    /**
     * Performs the benchmark and shows the results.
     * 
     * @return a list of MeasurableCode instances representing the measured codes.
     */
    public List<MeasurableCode> perform() {
        if (TARGET == null) {
            List<MeasurableCode> results = new ArrayList();
            for (MeasurableCode code : codes) {
                try {
                    Path file = Files.createTempFile(null, null).toAbsolutePath();

                    List<String> command = new ArrayList();
                    command.add("java");
                    command.add("-Xmx" + code.env.memory);
                    command.add("-Xms" + code.env.memory);
                    command.add("-D" + FILE_KEY + "=" + file.toString());
                    command.add("-D" + TARGET_KEY + "=" + code.name);
                    command.add("-Dfile.encoding=UTF-8");
                    command.addAll(List.of("-cp", System.getProperty("java.class.path")));
                    command.add(caller.getName());

                    int result = new ProcessBuilder(command).inheritIO().start().waitFor();
                    if (result != 0) {
                        reporter.accept("Stop benchmark by fatal error in forked JVM.\n");
                        continue;
                    }

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

            Libraries names = new Libraries();
            int maxName = 0;
            for (MeasurableCode code : results) {
                maxName = Math.max(maxName, code.name.length());
                code.version = names.detect(code.name);
            }

            try {
                reporter.accept(String.format("%" + maxName + "s\tThroughput\t\tAverage\t\tPeakMemory\tTotalGC", "\t"));
                for (MeasurableCode code : results) {
                    reporter.accept(String
                            .format("%-" + maxName + "s\t%,dcall/s \t%,-6dns/call \t%.2f\t\t%.0f(%.0fms)", code.name, code.throughputMean, code.arithmeticMean, code.peakMemory
                                    .getMean(), code.countGC.getMean(), code.timeGC.getMean()));
                }
                reporter.accept("");
                reporter.accept(getPlatformInfo());

                if (visualize.length != 0) {
                    SVG.write(caller.getSimpleName(), visualize, results);
                }
            } catch (Throwable e) {
                e.printStackTrace(origina);
            }
        } else {
            MeasurableCode code = codes.stream().filter(c -> c.name.equals(TARGET)).findFirst().get();
            code.perform();
        }
        return codes;
    }

    /**
     * Retrieves information about the current platform, including Java and OS details.
     * 
     * @return a formatted string containing platform information.
     */
    static final String getPlatformInfo() {
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

        builder.append("CPU \t")
                .append(getCPUInfo())
                .append("\n")
                .append("Mem \t")
                .append(runtime.maxMemory() / 1024 / 1024)
                .append("MB\n");

        return builder.toString();
    }

    /**
     * Retrieves information about the CPU, including its name and clock speed.
     * 
     * @return a string containing the CPU name and clock speed.
     */
    static final String getCPUInfo() {
        try {
            Process process = new ProcessBuilder("wmic", "cpu", "list", "full").redirectErrorStream(true).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            Map<String, String> info = new HashMap();
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    int index = line.indexOf("=");
                    if (index != -1) {
                        info.put(line.substring(0, index), line.substring(index + 1));
                    }
                }
            }
            return info.get("Name") + " " + (Double.parseDouble(info.get("MaxClockSpeed")) / 1000) + "GHz";
        } catch (IOException e) {
            return "";
        }
    }
}