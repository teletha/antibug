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
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version 2014/03/05 12:26:10
 */
public class ChronoTrigger extends ScheduledThreadPoolExecutor {

    /** The non-executed tasks. */
    private static final Deque<Runnable> nonexecuted = new ArrayDeque();

    /** The flag for task manager. */
    private static volatile AtomicBoolean waitingTasks = new AtomicBoolean();

    /** A number of running taks. */
    private static volatile AtomicInteger tasks = new AtomicInteger();

    /**
     * @param corePoolSize
     */
    protected ChronoTrigger(int corePoolSize) {
        super(corePoolSize);
    }

    /**
     * @param corePoolSize
     * @param threadFactory
     */
    protected ChronoTrigger(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }

    /**
     * @param corePoolSize
     * @param handler
     */
    protected ChronoTrigger(int corePoolSize, RejectedExecutionHandler handler) {
        super(corePoolSize, handler);
    }

    /**
     * @param corePoolSize
     * @param threadFactory
     * @param handler
     */
    protected ChronoTrigger(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, threadFactory, handler);
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
     * Wait all task executions.
     * </p>
     */
    public static void await() {
        waitingTasks.set(true);

        try {
            long start = System.currentTimeMillis();

            while (!nonexecuted.isEmpty()) {
                nonexecuted.pollFirst().run();
            }

            while (0 < tasks.get()) {
                Async.wait(10);

                long end = System.currentTimeMillis();

                if (start + 200 < end) {
                    throw new Error("Task can't exceed 200ms. Remaining tasks are " + tasks + ".");
                }
            }
        } finally {
            waitingTasks.set(false);
            nonexecuted.clear();
            tasks.set(0);
        }
    }

    /**
     * @version 2014/03/05 13:02:11
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
                try {
                    delegator.run();
                } finally {
                    tasks.decrementAndGet();
                }
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
            boolean cancel = delegator.cancel(mayInterruptIfRunning);

            if (cancel) {
                if (nonexecuted.remove(this)) {
                    tasks.decrementAndGet();
                }
            }
            return cancel;
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
