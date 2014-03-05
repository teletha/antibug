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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import kiss.I;

/**
 * @version 2014/03/05 22:06:45
 */
public class Awaitable {

    /** The flag for task manager. */
    private static volatile AtomicBoolean awaiting = new AtomicBoolean();

    /** The non-executed tasks. */
    private static final Set remaining = new HashSet();

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
                Async.wait(10);

                long end = System.currentTimeMillis();

                if (start + 200 < end) {
                    throw new Error("Task can't exceed 200ms. Remaining tasks are " + remaining + ".");
                }
            }
        } finally {
            awaiting.set(false);
            remaining.clear();
        }
    }

    /**
     * <p>
     * Wrap task.
     * </p>
     * 
     * @param task
     * @return
     */
    protected final Runnable wrap(Runnable task) {
        return new Task(task);
    }

    /**
     * <p>
     * Wrap task.
     * </p>
     * 
     * @param task
     * @return
     */
    protected final <T> Callable<T> wrap(Callable<T> task) {
        return new Task(task);
    }

    /**
     * <p>
     * Wrap task.
     * </p>
     * 
     * @param task
     * @return
     */
    protected final <T> List<Callable<T>> wrap(Collection<? extends Callable<T>> tasks) {
        ArrayList<Callable<T>> list = new ArrayList();

        for (Callable<T> callable : tasks) {
            list.add(wrap(callable));
        }
        return list;
    }

    /**
     * <p>
     * <em>This is internal API.</em>
     * </p>
     */
    public static ExecutorService wrap(ExecutorService service) {
        if (service == null) {
            return null;
        } else if (service instanceof AwaitableExecutorService) {
            return service;
        } else {
            return new AwaitableExecutorService(service);
        }
    }

    /**
     * <p>
     * <em>This is internal API.</em>
     * </p>
     */
    public static ExecutorService newCachedThreadPool() {
        return new AwaitableExecutorService(Executors.newCachedThreadPool());
    }

    /**
     * <p>
     * <em>This is internal API.</em>
     * </p>
     */
    public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
        return new AwaitableExecutorService(Executors.newCachedThreadPool(threadFactory));
    }

    /**
     * @version 2014/03/05 23:43:32
     */
    private class Task implements Callable, Runnable, Future {

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

            System.out.println("add task " + this);
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
                throw I.quiet(e);
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
                System.out.println("done task " + this);
                remaining.remove(this);
            }
        }

        private Future future;

        /**
         * <p>
         * Delegate {@link Future} fuature.
         * </p>
         * 
         * @param future
         * @return
         */
        private Task connect(Future future) {
            this.future = future;

            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            boolean cancel = future.cancel(mayInterruptIfRunning);
            System.out.println("try cancel " + cancel);
            if (cancel) {
                if (remaining.remove(this)) {
                    System.out.println("cancel task " + this + "   remaining " + remaining.size());
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
        public Object get() throws InterruptedException, ExecutionException {
            return future.get();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
                TimeoutException {
            return future.get(timeout, unit);
        }
    }

    /**
     * @version 2014/03/05 22:34:27
     */
    private static class AwaitableExecutorService extends Awaitable implements ExecutorService {

        /** The actual service. */
        private ExecutorService service;

        /**
         * @param service
         */
        private AwaitableExecutorService(ExecutorService service) {
            this.service = service;
        }

        /**
         * {@inheritDoc}
         */
        public void execute(Runnable command) {
            service.execute(wrap(command));
        }

        /**
         * {@inheritDoc}
         */
        public void shutdown() {
            service.shutdown();
        }

        /**
         * {@inheritDoc}
         */
        public List<Runnable> shutdownNow() {
            return service.shutdownNow();
        }

        /**
         * {@inheritDoc}
         */
        public boolean isShutdown() {
            return service.isShutdown();
        }

        /**
         * {@inheritDoc}
         */
        public boolean isTerminated() {
            return service.isTerminated();
        }

        /**
         * {@inheritDoc}
         */
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return service.awaitTermination(timeout, unit);
        }

        /**
         * {@inheritDoc}
         */
        public <T> Future<T> submit(Callable<T> command) {
            Task task = new Task(command);

            return task.connect(service.submit((Callable) task));
        }

        /**
         * {@inheritDoc}
         */
        public <T> Future<T> submit(Runnable command, T result) {
            Task task = new Task(command, result);

            return task.connect(service.submit((Callable) task));
        }

        /**
         * {@inheritDoc}
         */
        public Future<?> submit(Runnable command) {
            Task task = new Task(command);

            return task.connect(service.submit((Callable) task));
        }

        /**
         * {@inheritDoc}
         */
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        /**
         * {@inheritDoc}
         */
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
                throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        /**
         * {@inheritDoc}
         */
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
            throw new UnsupportedOperationException();
        }

        /**
         * {@inheritDoc}
         */
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
                throws InterruptedException, ExecutionException, TimeoutException {
            throw new UnsupportedOperationException();
        }
    }
}
