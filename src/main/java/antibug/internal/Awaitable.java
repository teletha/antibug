/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug.internal;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 * This is internal API.
 * </p>
 * 
 * @version 2014/03/05 22:06:45
 */
public class Awaitable {

    /** The flag for task manager. */
    private static volatile AtomicBoolean awaiting = new AtomicBoolean();

    /** The non-executed tasks. */
    private static final CopyOnWriteArraySet remaining = new CopyOnWriteArraySet();

    /**
     * <p>
     * Wait all task executions.
     * </p>
     */
    public static void await() {
        awaiting.set(true);

        try {
            long start = System.currentTimeMillis();

            while (!remaining.isEmpty()) {
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    throw new Error(e);
                }

                long end = System.currentTimeMillis();

                if (start + 1000 < end && !remaining.isEmpty()) {
                    throw new Error("Task can't exceed 1000ms. Remaining tasks are " + remaining + ".");
                }
            }
        } finally {
            awaiting.set(false);
            remaining.clear();
        }
    }

    /**
     * <p>
     * <em>This is internal API.</em>
     * </p>
     */
    public static <T extends ExecutorService> T wrap(T service) {
        if (service == null) {
            return null;
        } else if (service instanceof Executor) {
            return service;
        } else if (service instanceof ScheduledExecutorService) {
            return (T) new ScheduledExecutor((ScheduledExecutorService) service);
        } else {
            return (T) new Executor(service);
        }
    }

    /**
     * @version 2014/03/05 23:43:32
     */
    private static class Task<V> implements Callable, Runnable, Future<V>, ScheduledFuture<V> {

        /** The actual task. */
        private final Callable callable;

        /**
         * @param task
         */
        private Task(Runnable task) {
            this(Executors.callable(task));
        }

        /**
         * @param task
         */
        private Task(Runnable task, Object result) {
            this(Executors.callable(task, result));
        }

        /**
         * @param task
         */
        private Task(Callable task) {
            this.callable = task;

            // System.out.println("add task " + this);
            remaining.add(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {
                call();
            } catch (Exception e) {
                throw new Error(e);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object call() throws Exception {
            try {
                return callable.call();
            } finally {
                if (remaining.remove(this)) {
                    // System.out.println("remove task " + this);
                }
            }
        }

        private Future<V> future;

        /**
         * <p>
         * Delegate {@link Future} fuature.
         * </p>
         * 
         * @param future
         * @return
         */
        private Task connect(Future<V> future) {
            this.future = future;

            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            boolean cancel = future.cancel(mayInterruptIfRunning);

            if (cancel) {
                if (remaining.remove(this)) {
                    // System.out.println("cancel task " + this);
                }
            }
            return cancel;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isCancelled() {
            return future.isCancelled();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isDone() {
            return future.isDone();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public V get() throws InterruptedException, ExecutionException {
            return future.get();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return future.get(timeout, unit);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getDelay(TimeUnit unit) {
            if (future instanceof ScheduledFuture) {
                return ((ScheduledFuture) future).getDelay(unit);
            } else {
                return 0;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(Delayed o) {
            if (future instanceof ScheduledFuture) {
                return ((ScheduledFuture) future).compareTo(o);
            } else {
                return 0;
            }
        }
    }

    /**
     * @version 2014/03/05 22:34:27
     */
    private static class Executor extends Awaitable implements ExecutorService {

        /** The actual service. */
        private ExecutorService service;

        /**
         * @param service
         */
        private Executor(ExecutorService service) {
            this.service = service;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void execute(Runnable command) {
            service.execute(new Task(command));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void shutdown() {
            service.shutdown();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<Runnable> shutdownNow() {
            return service.shutdownNow();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isShutdown() {
            return service.isShutdown();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isTerminated() {
            return service.isTerminated();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return service.awaitTermination(timeout, unit);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T> Future<T> submit(Callable<T> command) {
            Task task = new Task(command);

            return task.connect(service.submit((Callable) task));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T> Future<T> submit(Runnable command, T result) {
            Task task = new Task(command, result);

            return task.connect(service.submit((Callable) task));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Future<?> submit(Runnable command) {
            Task task = new Task(command);

            return task.connect(service.submit((Callable) task));
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
            throw new UnsupportedOperationException();
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
            throw new UnsupportedOperationException();
        }
    }

    /**
     * @version 2014/03/06 10:12:19
     */
    private static class ScheduledExecutor extends Executor implements ScheduledExecutorService {

        /** The actual service. */
        private final ScheduledExecutorService service;

        /**
         * @param service
         */
        private ScheduledExecutor(ScheduledExecutorService service) {
            super(service);

            this.service = service;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
            Task task = new Task(command);

            return task.connect(service.schedule((Callable) task, delay, unit));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
            Task task = new Task(callable);

            return task.connect(service.schedule((Callable) task, delay, unit));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
            return service.scheduleAtFixedRate(command, initialDelay, period, unit);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
            return service.scheduleWithFixedDelay(command, initialDelay, delay, unit);
        }
    }
}
