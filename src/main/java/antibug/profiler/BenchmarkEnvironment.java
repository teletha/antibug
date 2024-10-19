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
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("serial")
public class BenchmarkEnvironment<Self extends BenchmarkEnvironment> implements Serializable {

    /** The number of iteration. */
    int trials = 5;

    /** The limit of calls. */
    long limit;

    /** The duration of single trial. */
    Duration duration = Duration.ofSeconds(1);

    /** The max memory size. */
    String memory = "512m";

    private boolean configurable = true;

    /**
     * Clone this environment.
     * 
     * @return
     */
    BenchmarkEnvironment snapshot() {
        return new BenchmarkEnvironment().trial(trials).memory(memory).limit(limit).duration(duration).when(configurable);
    }

    long memory() {
        long multiplier;
        String unit = memory.substring(memory.length() - 1).toUpperCase();
        long value = Long.parseLong(memory.substring(0, memory.length() - 1));

        switch (unit) {
        case "G":
            multiplier = 1024L * 1024 * 1024; // GB to bytes
            break;
        case "M":
            multiplier = 1024L * 1024; // MB to bytes
            break;
        case "K":
            multiplier = 1024L; // KB to bytes
            break;
        default:
            throw new IllegalArgumentException("Unsupported unit: " + unit);
        }
        return value * multiplier;
    }

    /**
     * Configure the number of trial.
     * 
     * @param trials A number of trials. (3 <= trials <= 30)
     * @return Chainable configuration.
     */
    public Self trial(int trials) {
        if (configurable) {
            if (trials < 3) {
                throw new AssertionError("There is too few trial number of times. (minimus is 3)");
            }

            if (30 < trials) {
                throw new AssertionError("There is too many trial number of times. (maximum is 30)");
            }
            this.trials = trials;
        }
        return (Self) this;
    }

    /**
     * Configure the memory size of forked JVM.
     * 
     * @param max
     * @return
     */
    public Self memory(String max) {
        if (configurable && max != null) {
            this.memory = max;
        }
        return (Self) this;
    }

    /**
     * Configure the duration of trial.
     * 
     * @param limit A caount of trial.
     * @return Chainable configuration.
     */
    public Self limit(long limit) {
        if (configurable && 0 < limit) {
            this.limit = limit;
        }
        return (Self) this;
    }

    /**
     * Configure the duration of trial.
     * 
     * @param time A duration of trial.
     * @param unit A duration unit of trial.
     * @return Chainable configuration.
     */
    public Self duration(int time, TimeUnit unit) {
        return duration(Duration.of(time, unit.toChronoUnit()));
    }

    /**
     * Configure the duration of trial.
     * 
     * @param time A duration of trial.
     * @param unit A duration unit of trial.
     * @return Chainable configuration.
     */
    public Self duration(int time, ChronoUnit unit) {
        return duration(Duration.of(time, unit));
    }

    /**
     * Configure the duration of trial.
     * 
     * @param duration A duration of trial.
     * @return Chainable configuration.
     */
    public Self duration(Duration duration) {
        if (configurable) {
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
        }
        return (Self) this;
    }

    /**
     * Subsequent configurations are applied only if the given condition is true.
     * 
     * @param condition
     * @return
     */
    public Self when(boolean condition) {
        this.configurable = condition;
        return (Self) this;
    }
}
