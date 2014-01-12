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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
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

    /** The non-executed tasks. */
    private final Deque<Runnable> nonexecuted = new ArrayDeque();

    /** The flag for task manager. */
    private volatile AtomicBoolean waitingTasks = new AtomicBoolean();

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
    protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task) {
        return decorateTask(Executors.callable(runnable, null), task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> callable, RunnableScheduledFuture<V> task) {
        tasks.incrementAndGet();
        return new AwaitableTask(task);
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
        Async async = Async.singleton;
        AtomicInteger counter = singleton.tasks;
        async.waitingTasks.set(true);

        try {
            long start = System.currentTimeMillis();

            while (!async.nonexecuted.isEmpty()) {
                async.nonexecuted.pollFirst().run();
            }

            while (0 < counter.get()) {
                wait(10);

                long end = System.currentTimeMillis();

                if (start + 200 < end) {
                    throw new Error("Task can't exceed 200ms. Remaining tasks are " + counter + ".");
                }
            }
        } finally {
            async.waitingTasks.set(false);
            async.nonexecuted.clear();
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

    /**
     * @version 2014/01/12 18:10:39
     */
    private class AwaitableTask implements RunnableScheduledFuture {

        /** The actual task. */
        private final RunnableScheduledFuture delegator;

        /**
         * @param delegator
         */
        private AwaitableTask(RunnableScheduledFuture delegator) {
            this.delegator = delegator;
        }

        /**
         * {@inheritDoc}
         */
        public long getDelay(TimeUnit unit) {
            return delegator.getDelay(unit);
        }

        /**
         * {@inheritDoc}
         */
        public void run() {
            if (waitingTasks.get()) {
                tasks.decrementAndGet();
                delegator.run();
            } else {
                nonexecuted.add(this);
            }
        }

        /**
         * {@inheritDoc}
         */
        public boolean isPeriodic() {
            return delegator.isPeriodic();
        }

        /**
         * {@inheritDoc}
         */
        public boolean cancel(boolean mayInterruptIfRunning) {
            return delegator.cancel(mayInterruptIfRunning);
        }

        /**
         * {@inheritDoc}
         */
        public int compareTo(Delayed o) {
            return delegator.compareTo(o);
        }

        /**
         * {@inheritDoc}
         */
        public boolean isCancelled() {
            return delegator.isCancelled();
        }

        /**
         * {@inheritDoc}
         */
        public boolean isDone() {
            return delegator.isDone();
        }

        /**
         * {@inheritDoc}
         */
        public Object get() throws InterruptedException, ExecutionException {
            return delegator.get();
        }

        /**
         * {@inheritDoc}
         */
        public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
                TimeoutException {
            return delegator.get(timeout, unit);
        }
    }
}
