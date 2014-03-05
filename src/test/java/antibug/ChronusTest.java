/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug;

import static java.util.concurrent.TimeUnit.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import kiss.I;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @version 2014/03/05 9:21:24
 */
public class ChronusTest {

    @Rule
    public static final Chronus chronus = new Chronus(ChronusTest.class);

    /** The test result. */
    private boolean done = false;

    @Before
    public void reset() {
        done = false;
    }

    @Test
    public void scheduledThreadPoolExecutorInt() throws Exception {
        execute(() -> {
            ScheduledExecutorService service = new ScheduledThreadPoolExecutor(2);
            service.schedule(createTask(), 10, MILLISECONDS);
        });
    }

    @Test
    public void newCachedThreadPoolThreadFactory() throws Exception {
        execute(() -> {
            ExecutorService service = Executors.newCachedThreadPool(runnable -> {
                return new Thread(runnable);
            });
            service.submit(createDelayedTask());
        });
    }

    /**
     * <p>
     * Create task.
     * </p>
     * 
     * @return
     */
    private final Runnable createTask() {
        return () -> {
            done = true;
        };
    }

    /**
     * <p>
     * Create delayed task.
     * </p>
     * 
     * @return
     */
    private final Runnable createDelayedTask() {
        return () -> {
            try {
                Thread.sleep(10);
                done = true;
            } catch (Exception e) {
                throw I.quiet(e);
            }
        };
    }

    /**
     * <p>
     * Helper method to test.
     * </p>
     * 
     * @param task
     */
    private final void execute(Runnable task) {
        task.run();
        assert done == false;
        chronus.await();
        assert done == true;
    }
}
