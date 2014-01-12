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

import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import kiss.I;

/**
 * <p>
 * Synchronous {@link ScheduledExecutorService}.
 * </p>
 * 
 * @version 2014/01/12 21:20:17
 */
class Synchronous implements Scheduler {

    /** The task queue. */
    private final PriorityQueue<Task> queue = new PriorityQueue();

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdown() {
        queue.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Runnable> shutdownNow() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isShutdown() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTerminated() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return new Task(task, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return submit(Executors.callable(task, result));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<?> submit(Runnable task) {
        return submit(Executors.callable(task));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException {
        return invokeAll(tasks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return invokeAny(tasks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Runnable command) {
        submit(command);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return schedule(Executors.callable(command), delay, unit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return new Task(callable, unit.toMillis(delay));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void awaitTasks() {
        while (!queue.isEmpty()) {
            queue.poll().run();
        }
    }

    /**
     * @version 2014/01/12 21:25:52
     */
    private class Task<V> implements Runnable, ScheduledFuture<V> {

        /** The actual task. */
        private final Callable<V> task;

        /** The priority. */
        private final long time;

        /** The state. */
        private boolean canceled;

        /** The state. */
        private boolean done;

        /** The result object. */
        private V result;

        /**
         * @param task
         * @param time
         */
        private Task(Callable task, long time) {
            this.task = task;
            this.time = time;

            queue.add(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {
                result = task.call();
            } catch (Exception e) {
                throw I.quiet(e);
            } finally {
                done = true;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            if (done) {
                return false;
            }
            return canceled = queue.remove(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isCancelled() {
            return canceled;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isDone() {
            return done;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public V get() throws InterruptedException, ExecutionException {
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return get();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(time, TimeUnit.MILLISECONDS);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(Delayed task) {
            long other = ((Task) task).time;

            if (time == other) {
                return 0;
            }
            return time < other ? 1 : -1;
        }
    }
}
