/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.benchmark;

import static java.math.BigInteger.*;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.text.DecimalFormat;

import kiss.I;
import antibug.ReusableRule;

/**
 * @version 2011/02/08 22:48:23
 */
public class Benchmark extends ReusableRule {

    /** 2 */
    static final BigInteger TWO = new BigInteger("2");

    /** 1,000 */
    static final BigInteger K = new BigInteger("1000");

    /** 1,000,000 */
    static final BigInteger M = new BigInteger("1000000");

    /** 1,000,000,000 */
    static final BigInteger G = new BigInteger("1000000000");

    /** The number of trial. */
    private final int trials;

    /** The threshold of measurement time. (unit: ns) */
    private final BigInteger threshold = new BigInteger("1").multiply(G);

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
     * <p>
     * Measure an execution speed of the specified code fragment.
     * </p>
     * 
     * @param measuredCode A code to be measured.
     */
    public void measure(Code measuredCode) {
        Sample first = measure(measuredCode, ONE);

        assert first.hash != 0 : "Benckmark task must return not null but something.";
        assert first.time.compareTo(threshold) == -1 : "Benchmark task must be able to execute within 1 second.";

        write("Warmup JVM");

        // warmup JVM and decided the number of executions
        BigInteger frequency = ONE;

        while (true) {
            Sample result = measure(measuredCode, frequency);

            if (result.time.compareTo(threshold) == -1) {
                frequency = frequency.multiply(TWO);
            } else {
                frequency = frequency.multiply(G).divide(result.time);
                break;
            }
            write("..");
        }
        write("\r");

        // measure actually
        Statistics statistics = new Statistics();
        DecimalFormat counterFormat = new DecimalFormat("00");

        for (int i = 0; i < this.trials; i++) {
            Sample result = measure(measuredCode, frequency);

            // save
            statistics.addSample(result);

            // display for user
            write(counterFormat.format(i + 1), " : ", result, "\n");
        }

        // report
        throw statistics;
    }

    /**
     * Measures the execution time of <code>frequency</code> calls of the specified task.
     */
    private Sample measure(Code code, BigInteger frequency) {
        int hash = 0;

        try {
            // measure actually
            long start = System.nanoTime();
            for (long i = frequency.longValue(); 0 < i; i--) {
                hash ^= code.measure().hashCode(); // prevent dead-code-elimination
            }
            long end = System.nanoTime();

            // calculate execution time
            return new Sample(frequency, end - start, hash);
        } catch (Throwable e) {
            throw I.quiet(e);
        }
    }

    /**
     * @see antibug.ReusableRule#before(java.lang.reflect.Method)
     */
    @Override
    protected void before(Method method) throws Exception {
        write("<<<<<<<<<<  ", method.getName(), "  >>>>>>>>>>\n");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean skip(Method method) {
        return isSuite;
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

    /**
     * <p>
     * </p>
     * 
     * @version 2012/01/30 10:56:30
     */
    public static interface Code {

        /**
         * <p>
         * Write micro benchmark code.
         * </p>
         * 
         * @return
         * @throws Throwable
         */
        Object measure() throws Throwable;
    }
}
