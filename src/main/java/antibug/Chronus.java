/*
 * Copyright (C) 2025 The ANTIBUG Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug;

import static java.util.concurrent.TimeUnit.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Chronus implements ScheduledExecutorService {

    /** The flag for task manager. */
    private transient volatile AtomicBoolean awaiting = new AtomicBoolean();

    /** The non-executed tasks. */
    private volatile CopyOnWriteArraySet remaining = new CopyOnWriteArraySet();

    /** The lazy service initializer. */
    private final Supplier<ScheduledExecutorService> builder;

    /** The service holder. */
    private final AtomicReference<ScheduledExecutorService> holder = new AtomicReference();

    /**
     * By {@link Executors#newCachedThreadPool()}.
     */
    public Chronus() {
        this(() -> Executors.newScheduledThreadPool(ForkJoinPool.getCommonPoolParallelism()));
    }

    /**
     * By your {@link ExecutorService}.
     * 
     * @param builder Your {@link ExecutorService}.
     */
    public Chronus(Supplier<ScheduledExecutorService> builder) {
        this.builder = Objects.requireNonNull(builder);
    }

    /**
     * Retrieve {@link ExecutorService} by lazy initialization.
     * 
     * @return
     */
    private ScheduledExecutorService executor() {
        return holder.updateAndGet(e -> e != null ? e : builder.get());
    }

    /**
     * Create delayed {@link Executors} in the specified duration.
     * 
     * @param time A delay time.
     * @param unit A time unit.
     * @return A delayed {@link Executor}.
     */
    public Executor in(long time, TimeUnit unit) {
        return task -> schedule(task, time, unit);
    }

    /**
     * Config the limit size of execution threads.
     * 
     * @param size
     * @return
     */
    public Chronus configExecutionLimit(int size) {
        ScheduledExecutorService executor = executor();

        if (executor instanceof ThreadPoolExecutor) {
            ((ThreadPoolExecutor) executor).setCorePoolSize(size);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void execute(Runnable command) {
        executor().execute(new Task(command));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdown() {
        executor().shutdown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Runnable> shutdownNow() {
        return executor().shutdownNow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isShutdown() {
        return executor().isShutdown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTerminated() {
        return executor().isTerminated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executor().awaitTermination(timeout, unit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Future<T> submit(Callable<T> command) {
        Task task = new Task(command);

        return task.connect(executor().submit((Callable) task));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Future<T> submit(Runnable command, T result) {
        Task task = new Task(command, result);

        return task.connect(executor().submit((Callable) task));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<?> submit(Runnable command) {
        Task task = new Task(command);

        return task.connect(executor().submit((Callable) task));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        List<Task<T>> collect = tasks.stream().map(task -> new Task<T>(task)).collect(Collectors.toList());

        return executor().invokeAll(collect);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        List<Task<T>> collect = tasks.stream().map(task -> new Task<T>(task)).collect(Collectors.toList());

        return executor().invokeAll(collect, timeout, unit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        List<Task<T>> collect = tasks.stream().map(task -> new Task<T>(task)).collect(Collectors.toList());

        return executor().invokeAny(collect);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        List<Task<T>> collect = tasks.stream().map(task -> new Task<T>(task)).collect(Collectors.toList());

        return executor().invokeAny(collect, timeout, unit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        if (delay <= 0) {
            return immediately(Executors.callable(command));
        }

        Task task = new Task(command);
        return task.connect(executor().schedule((Callable) task, delay, unit));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        if (delay <= 0) {
            return immediately(callable);
        }

        Task task = new Task(callable);
        return task.connect(executor().schedule((Callable) task, delay, unit));
    }

    private ScheduledFuture immediately(Callable callable) {
        try {
            Task task = new Task(callable);
            task.connect(CompletableFuture.completedFuture(task.call()));
            return task;
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long interval, TimeUnit unit) {
        long intervalMills = unit.toMillis(interval);
        long[] next = {System.currentTimeMillis() + unit.toMillis(initialDelay) + intervalMills};
        Runnable[] task = new Runnable[1];
        task[0] = () -> {
            next[0] = next[0] + intervalMills;
            command.run();
            schedule(task[0], next[0] - System.currentTimeMillis(), unit);
        };
        return schedule(task[0], initialDelay, unit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long interval, TimeUnit unit) {
        Runnable[] task = new Runnable[1];
        task[0] = () -> {
            command.run();
            schedule(task[0], interval, unit);
        };
        return schedule(task[0], initialDelay, unit);
    }

    private long marked;

    /**
     * Record the current time. Hereafter, it is used as the start time when using
     * {@link #elapse(int, TimeUnit)} or {@link #within(int, TimeUnit, Runnable)}.
     */
    public final Chronus mark() {
        marked = System.nanoTime();

        return this;
    }

    /**
     * <p>
     * Waits for the specified time from the marked time. It does not wait if it has already passed.
     * </p>
     * <pre>
     * chronus.mark();
     * asynchronous.process();
     * 
     * chronus.elapse(100, TimeUnit.MILLSECONDS);
     * assert validation.code();
     * </pre>
     * 
     * @param amount Time amount.
     * @param unit Time unit.
     */
    public final Chronus elapse(int amount, TimeUnit unit) {
        long startTime = marked + unit.toNanos(amount);

        await(startTime - System.nanoTime(), NANOSECONDS);

        return this;
    }

    /**
     * <p>
     * Performs the specified operation if the specified time has not yet elapsed since the marked
     * time. If it has already passed, do nothing.
     * </p>
     * <pre>
     * chronus.mark();
     * synchronous.process();
     * 
     * chronus.within(100, TimeUnit.MILLSECONDS, () -> {
     *      assert validation.code();
     * });
     * </pre>
     * 
     * @param amount Time amount.
     * @param unit Time unit.
     * @param within Your process.
     */
    public final Chronus within(int amount, TimeUnit unit, Runnable within) {
        if (within != null && System.nanoTime() < marked + unit.toNanos(amount)) {
            within.run();
        }
        return this;
    }

    /**
     * Wait all task executions.
     */
    @SuppressWarnings("resource")
    public void await() {
        awaiting.set(true);

        try {
            long start = System.currentTimeMillis();

            while (!remaining.isEmpty()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new Error(e);
                }

                long end = System.currentTimeMillis();

                if (start + 3000 < end && !remaining.isEmpty()) {
                    throw new Error("Task can't exceed 3000ms. Remaining tasks are " + remaining + ".\r\n" + executor());
                }
            }
        } finally {
            awaiting.set(false);
            remaining.clear();
        }
    }

    /**
     * Freeze process.
     */
    public void await(long time, TimeUnit unit) {
        freezeNano(unit.toNanos(time));
    }

    /**
     * <p>
     * Freeze process.
     * </p>
     * 
     * @param time A nano time to freeze.
     */
    private void freezeNano(long time) {
        try {
            long start = System.nanoTime();
            NANOSECONDS.sleep(time);
            long end = System.nanoTime();

            long remaining = start + time - end;

            if (0 < remaining) {
                freezeNano(remaining);
            }
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }

    /**
     * 
     */
    protected class Task<V> implements Callable<V>, Runnable, Future<V>, ScheduledFuture<V> {

        /** The actual task. */
        private final Callable<V> callable;

        /**
         * @param task
         */
        Task(Runnable task) {
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
        Task(Callable task) {
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
            } catch (Throwable e) {
                throw new Error(e);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public V call() throws Exception {
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
        Task connect(Future<V> future) {
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
            try {
                return get(1000, MILLISECONDS);
            } catch (TimeoutException e) {
                throw new Error(e);
            }
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
}