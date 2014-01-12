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

import java.util.concurrent.ScheduledExecutorService;

import kiss.I;

/**
 * <p>
 * Asynchronous utility.
 * </p>
 * 
 * @version 2014/01/10 19:23:57
 */
public class Async {

    /** The singleton. */
    private static final Asynchronous async = new Asynchronous();

    /** The singleton instance. */
    private static final Synchronous sync = new Synchronous();

    /** The current scheduler. */
    private static Scheduler scheduler;

    /**
     * <p>
     * Use thread pool.
     * </p>
     * 
     * @return
     */
    public static ScheduledExecutorService use() {
        return scheduler = async;
    }

    /**
     * <p>
     * Use fake thread pool.
     * </p>
     * 
     * @return
     */
    public static ScheduledExecutorService sync() {
        return scheduler = sync;
    }

    /**
     * <p>
     * Wait all task executions.
     * </p>
     */
    public static void awaitTasks() {
        scheduler.awaitTasks();
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
