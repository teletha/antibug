/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug;

import static java.util.concurrent.TimeUnit.*;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kiss.I;

/**
 * @version 2018/03/31 3:10:33
 */
public class ChronusTest {

    public static final Chronus chronus = new Chronus(ChronusTest.class);

    /**
     * @version 2014/03/06 11:54:38
     */
    private static class ExternalService {

        private static ExecutorService service() {
            return Executors.newCachedThreadPool();
        }
    }

    /** The test result. */
    private static boolean done = false;

    /** The test result. */
    private static AtomicInteger value = new AtomicInteger();

    @BeforeEach
    public void reset() {
        done = false;
        value.set(0);
    }

    @Test
    public void scheduledThreadPoolExecutorInt() throws Exception {
        execute(3, () -> {
            ScheduledExecutorService service = new ScheduledThreadPoolExecutor(2);
            service.schedule(createTask(), 10, MILLISECONDS);
            service.schedule(createTask(), 20, MILLISECONDS);
            service.schedule(createTask(), 30, MILLISECONDS);
        });
    }

    @Test
    public void newCachedThreadPool() throws Exception {
        execute(2, () -> {
            ExecutorService service = Executors.newCachedThreadPool();
            service.submit(createDelayedTask());
            service.submit(createDelayedTask());
        });
    }

    @Test
    public void newCachedThreadPoolThreadFactory() throws Exception {
        execute(1, () -> {
            ExecutorService service = Executors.newCachedThreadPool(runnable -> {
                return new Thread(runnable);
            });
            service.submit(createDelayedTask());
        });
    }

    @Test
    public void newWorkStealingPool() throws Exception {
        execute(1, () -> {
            ExecutorService service = Executors.newWorkStealingPool();
            service.submit(createDelayedTask());
        });
    }

    private static ExecutorService staticService = ExternalService.service();

    @Test
    public void staticField() throws Exception {
        execute(1, () -> {
            staticService.submit(createDelayedTask());
        });
    }

    private ExecutorService service = ExternalService.service();

    @Test
    public void field() throws Exception {
        execute(1, () -> {
            service.submit(createDelayedTask());
        });
    }

    @Test
    public void cancel() throws Exception {
        executeWithCancel(() -> {
            ExecutorService service = Executors.newCachedThreadPool(runnable -> {
                return new Thread(runnable);
            });
            return service.submit(createDelayedTask());
        });
    }

    /**
     * <p>
     * Create task.
     * </p>
     * 
     * @return
     */
    private static final Runnable createTask() {
        return () -> {
            done = true;
            value.incrementAndGet();
        };
    }

    /**
     * <p>
     * Create delayed task.
     * </p>
     * 
     * @return
     */
    private static final Runnable createDelayedTask() {
        return () -> {
            try {
                Thread.sleep(50);
                done = true;
                value.incrementAndGet();
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
    private final void execute(int expectedValue, Runnable task) {
        task.run();
        assert value.get() == 0;
        chronus.await();
        assert value.get() == expectedValue;
    }

    /**
     * <p>
     * Helper method to test.
     * </p>
     * 
     * @param task
     * @throws Exception
     */
    private final void executeWithCancel(Callable<Future> task) throws Exception {
        Future result = task.call();
        assert done == false;
        assert result.isCancelled() == false;

        result.cancel(true);
        chronus.await();

        assert done == false;
        assert result.isCancelled() == true;
    }
}
