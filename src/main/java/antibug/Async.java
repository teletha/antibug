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

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import kiss.I;

/**
 * <p>
 * Asynchronous utility.
 * </p>
 * 
 * @version 2014/01/10 19:23:57
 */
public class Async extends ScheduledThreadPoolExecutor {

    /** The singleton. */
    private static final Async singleton = new Async();

    /** A number of running taks. */
    private volatile AtomicInteger tasks = new AtomicInteger();

    /**
     * @param corePoolSize
     */
    private Async() {
        super(2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void afterExecute(Runnable runnable, Throwable error) {
        tasks.decrementAndGet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task) {
        tasks.incrementAndGet();
        return task;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> callable, RunnableScheduledFuture<V> task) {
        tasks.incrementAndGet();
        return task;
    }

    /**
     * <p>
     * Use thread pool.
     * </p>
     * 
     * @return
     */
    public static ScheduledThreadPoolExecutor use() {
        return singleton;
    }

    /**
     * <p>
     * Wait all task executions.
     * </p>
     */
    public static void awaitTasks() {
        AtomicInteger counter = singleton.tasks;
        long start = System.currentTimeMillis();

        try {
            while (0 < counter.get()) {
                wait(2);

                long end = System.currentTimeMillis();

                if (start + 200 < end) {
                    throw new Error("Task can't exceed 200ms. Remaining tasks are " + counter + ".");
                }
            }
        } finally {
            counter.set(0);
        }
    }

    /**
     * <p>
     * Wait thread execution.
     * </p>
     * 
     * @param millseconds
     */
    public static void wait(int millseconds) {
        try {
            long start = System.currentTimeMillis();
            Thread.sleep(millseconds);
            long end = System.currentTimeMillis();

            if (end - start < millseconds) {
                wait((int) (millseconds - end + start));
            }
        } catch (InterruptedException e) {
            throw I.quiet(e);
        }
    }
}
