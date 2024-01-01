/*
 * Copyright (C) 2024 The ANTIBUG Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ChronusTest {

    public final Chronus chronus = new Chronus();

    /** The test result. */
    private static AtomicInteger value = new AtomicInteger();

    @BeforeEach
    void reset() {
        value.set(0);
    }

    @Test
    void schedule() {
        execute(3, () -> {
            chronus.schedule(createTask(), 10, MILLISECONDS);
            chronus.schedule(createTask(), 20, MILLISECONDS);
            chronus.schedule(createTask(), 30, MILLISECONDS);
        });
    }

    @Test
    void submit() {
        execute(2, () -> {
            chronus.submit(createDelayedTask());
            chronus.submit(createDelayedTask());
        });
    }

    /**
     * Create task.
     * 
     * @return
     */
    private static final Runnable createTask() {
        return () -> {
            value.incrementAndGet();
        };
    }

    /**
     * Create delayed task.
     * 
     * @return
     */
    private static final Runnable createDelayedTask() {
        return () -> {
            try {
                Thread.sleep(50);
                value.incrementAndGet();
            } catch (Exception e) {
                throw new Error(e);
            }
        };
    }

    /**
     * Helper method to test.
     * 
     * @param task
     */
    private final void execute(int expectedValue, Runnable task) {
        task.run();
        assert value.get() == 0;
        chronus.await();
        assert value.get() == expectedValue;
    }

    @Test
    void elapse() {
        long start = System.currentTimeMillis();
        chronus.mark();
        chronus.elapse(100, TimeUnit.MILLISECONDS);
        long end = System.currentTimeMillis();

        assert 100 <= end - start;
    }
}