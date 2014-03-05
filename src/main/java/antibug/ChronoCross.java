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
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version 2014/03/05 16:31:20
 */
public class ChronoCross extends ThreadPoolExecutor {

    /** A number of running taks. */
    private static volatile AtomicInteger tasks = new AtomicInteger();

    /**
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param workQueue
     * @param handler
     */
    protected ChronoCross(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    /**
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param workQueue
     * @param threadFactory
     * @param handler
     */
    protected ChronoCross(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    /**
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param workQueue
     * @param threadFactory
     */
    protected ChronoCross(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    /**
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param workQueue
     */
    protected ChronoCross(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void afterExecute(Runnable task, Throwable error) {
        tasks.decrementAndGet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Runnable command) {
        tasks.incrementAndGet();
        super.execute(command);
    }

    /**
     * <p>
     * Wait all task executions.
     * </p>
     */
    public static void await() {
        try {
            long start = System.currentTimeMillis();

            while (0 < tasks.get()) {
                Async.wait(10);

                long end = System.currentTimeMillis();

                if (start + 200 < end) {
                    throw new Error("Task can't exceed 200ms. Remaining tasks are " + tasks + ".");
                }
            }
        } finally {
            tasks.set(0);
        }
    }

    public static ExecutorService field(ExecutorService service) {
        if (service instanceof ChronoCross || service instanceof Wrapper) {
            return service;
        } else {
            return new Wrapper(service);
        }
    }

    /**
     * Creates a thread pool that creates new threads as needed, but will reuse previously
     * constructed threads when they are available. These pools will typically improve the
     * performance of programs that execute many short-lived asynchronous tasks. Calls to
     * {@code execute} will reuse previously constructed threads if available. If no existing thread
     * is available, a new thread will be created and added to the pool. Threads that have not been
     * used for sixty seconds are terminated and removed from the cache. Thus, a pool that remains
     * idle for long enough will not consume any resources. Note that pools with similar properties
     * but different details (for example, timeout parameters) may be created using
     * {@link ThreadPoolExecutor} constructors.
     *
     * @return the newly created thread pool
     */
    public static ExecutorService newCachedThreadPool() {
        return new ChronoCross(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    }

    /**
     * Creates a thread pool that creates new threads as needed, but will reuse previously
     * constructed threads when they are available, and uses the provided ThreadFactory to create
     * new threads when needed.
     * 
     * @param threadFactory the factory to use when creating new threads
     * @return the newly created thread pool
     * @throws NullPointerException if threadFactory is null
     */
    public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
        return new ChronoCross(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), threadFactory);
    }

    /**
     * @version 2014/03/05 18:20:52
     */
    private static class Wrapper implements ExecutorService {

        /** The actual service. */
        private ExecutorService service;

        /**
         * @param service
         */
        private Wrapper(ExecutorService service) {
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
        public <T> Future<T> submit(Callable<T> task) {
            return service.submit(wrap(task));
        }

        /**
         * {@inheritDoc}
         */
        public <T> Future<T> submit(Runnable task, T result) {
            return service.submit(wrap(task), result);
        }

        /**
         * {@inheritDoc}
         */
        public Future<?> submit(Runnable task) {
            return service.submit(wrap(task));
        }

        /**
         * {@inheritDoc}
         */
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
            return service.invokeAll(wrap(tasks));
        }

        /**
         * {@inheritDoc}
         */
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
                throws InterruptedException {
            return service.invokeAll(wrap(tasks), timeout, unit);
        }

        /**
         * {@inheritDoc}
         */
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
            return service.invokeAny(wrap(tasks));
        }

        /**
         * {@inheritDoc}
         */
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
                throws InterruptedException, ExecutionException, TimeoutException {
            return service.invokeAny(wrap(tasks), timeout, unit);
        }

        /**
         * <p>
         * Wrap task.
         * </p>
         * 
         * @param task
         * @return
         */
        private Runnable wrap(Runnable task) {
            return () -> {
                tasks.incrementAndGet();
                try {
                    task.run();
                } finally {
                    tasks.decrementAndGet();
                }
            };
        }

        /**
         * <p>
         * Wrap task.
         * </p>
         * 
         * @param task
         * @return
         */
        private <T> Callable<T> wrap(Callable<T> task) {
            return () -> {
                tasks.incrementAndGet();
                try {
                    return task.call();
                } finally {
                    tasks.decrementAndGet();
                }
            };
        }

        /**
         * <p>
         * Wrap task.
         * </p>
         * 
         * @param task
         * @return
         */
        private <T> List<Callable<T>> wrap(Collection<? extends Callable<T>> tasks) {
            ArrayList<Callable<T>> list = new ArrayList();

            for (Callable<T> callable : tasks) {
                list.add(wrap(callable));
            }
            return list;
        }
    }
}
