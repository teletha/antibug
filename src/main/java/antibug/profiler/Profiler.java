/*
 * Copyright (C) 2025 The ANTIBUG Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug.profiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class Profiler<K, E, Y> {

    /** Flag for profiling execution. */
    public boolean execute = true;

    /** The profiling manager. */
    private final ConcurrentHashMap<String, Result> results = new ConcurrentHashMap();

    /** The latest profiling. */
    private Result latest = new Result(null, null, null);

    /**
     * 
     */
    public Profiler() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            show();
        }));
    }

    /**
     * Create the group name by keys.
     * 
     * @param key1
     * @param key2
     * @param key3
     * @return
     */
    protected String name(K key1, E key2, Y key3) {
        return String.valueOf(key1);
    }

    /**
     * Create the grouping key.
     * 
     * @param key1
     * @param key2
     * @param key3
     * @return
     */
    protected Object group(K key1, E key2, Y key3) {
        return Objects.hash(key1, key2, key3);
    }

    /**
     * Start profiling phase with the specified grouping key.
     * 
     * @param key1
     */
    public final AutoCloseable of(K key1) {
        return of(key1, null);
    }

    /**
     * Start profiling phase with the specified grouping key.
     * 
     * @param key1
     */
    public final AutoCloseable of(K key1, E key2) {
        return of(key1, key2, null);
    }

    /**
     * Start profiling phase with the specified grouping key.
     * 
     * @param key1
     */
    public final AutoCloseable of(K key1, E key2, Y key3) {
        start(key1, key2, key3);

        return latest;
    }

    /**
     * Start profiling phase with the specified grouping key.
     * 
     * @param key1
     */
    public final void start(K key1) {
        start(key1, (E) null, (Y) null);
    }

    /**
     * Start profiling phase with the specified grouping key.
     * 
     * @param key1
     */
    public final void start(K key1, E key2) {
        start(key1, key2, (Y) null);
    }

    /**
     * Start profiling phase with the specified grouping key.
     * 
     * @param key1
     */
    public final void start(K key1, E key2, Y key3) {
        if (execute) {
            latest.stop();

            String key = String.valueOf(key1).concat(String.valueOf(key2)).concat(String.valueOf(key3));
            Result now = results.computeIfAbsent(key, name -> new Result(key1, key2, key3));
            now.previous = latest;
            now.count++;
            latest = now;
            now.start();
        }
    }

    /**
     * Start profiling phase with the specified grouping key.
     * 
     * @param key1
     */
    public final void start(K key1, Runnable process) {
        start(key1, null, null, process);
    }

    /**
     * Start profiling phase with the specified grouping key.
     * 
     * @param key1
     */
    public final void start(K key1, E key2, Runnable process) {
        start(key1, key2, null, process);
    }

    /**
     * Start profiling phase with the specified grouping key.
     * 
     * @param key1
     */
    public final void start(K key1, E key2, Y key3, Runnable process) {
        start(key1, key2, key3);
        try {
            process.run();
        } finally {
            stop();
        }
    }

    /**
     * Start profiling phase with the specified grouping key.
     * 
     * @param key1
     */
    public final <T> T start(K key1, Supplier<T> process) {
        return start(key1, null, null, process);
    }

    /**
     * Start profiling phase with the specified grouping key.
     * 
     * @param key1
     */
    public final <T> T start(K key1, E key2, Supplier<T> process) {
        return start(key1, key2, null, process);
    }

    /**
     * Start profiling phase with the specified grouping key.
     * 
     * @param key1
     */
    public final <T> T start(K key1, E key2, Y key3, Supplier<T> process) {
        start(key1, key2, key3);
        try {
            return process.get();
        } finally {
            stop();
        }
    }

    /**
     * Stop profiling phase.
     */
    public final void stop() {
        if (execute) {
            latest.stop();
            latest = latest.previous;
            latest.start();
        }
    }

    /**
     * Show profiling result.
     */
    public final void show() {
        if (execute) {
            long total = 0;
            Map<Object, Result> grouped = new HashMap();

            for (Result result : results.values()) {
                Object key = group(result.key1, result.key2, result.key3);

                Result computed = grouped.computeIfAbsent(key, name -> new Result(name(result.key1, result.key2, result.key3)));
                computed.elapsed += result.elapsed;
                computed.count += result.count;

                total += result.elapsed;
            }

            List<Result> list = new ArrayList(grouped.values());
            Collections.sort(list, Comparator.<Result> comparingDouble(item -> item.elapsed).reversed());

            show(total, list);
        }
    }

    /**
     * Show profiling result.
     * 
     * @param results
     */
    protected void show(double total, List<Result> results) {
        System.out.format("Total Profiled Time: %-6.0fms%n", total / 1000000);

        int size = Math.min(15, results.size());

        String name = max(results.subList(0, size), v -> v.name, true);
        String time = max(results.subList(0, size), v -> String.valueOf(v.elapsed), false);
        String count = max(results.subList(0, size), v -> String.valueOf(v.count), false);

        for (int i = 0; i < size; i++) {
            Result result = results.get(i);

            if (result.elapsed != 0) {
                String format = name + "  " + time + "ms  %2.0f%%  " + count + "count%n";

                System.out.format(format, result.name, result.elapsed / 1000000, result.elapsed / total * 100, result.count);
            }
        }
    }

    /**
     * Helper method to build formatter.
     * 
     * @param list
     * @param value
     * @param left
     * @return
     */
    private String max(List<Result> list, Function<Result, String> value, boolean left) {
        return "%" + (left ? "-" : "") + list.stream().map(value).max(Comparator.comparingInt(v -> v.length())).get().length() + "s";
    }

    /**
     * @version 2015/08/02 20:31:19
     */
    protected class Result implements AutoCloseable {

        protected String name;

        /** The group key. */
        private K key1;

        /** The group key. */
        private E key2;

        /** The group key. */
        private Y key3;

        /** The previous profiling. */
        private Result previous;

        /** The latest recorded time. */
        private long latest;

        /** The elapsed time of the specified phase. */
        protected long elapsed;

        /** The start time of the specified phase. */
        private long start;

        /** The end time of the specified phase. */
        private long end;

        /** The call count. */
        protected long count;

        /**
         * @param name
         */
        private Result(String name) {
            this.name = name;
        }

        /**
         * @param name
         * @param group
         */
        private Result(K key1, E key2, Y key3) {
            this.key1 = key1;
            this.key2 = key2;
            this.key3 = key3;
        }

        /**
         * 
         */
        private void start() {
            long now = System.nanoTime();

            if (start == 0) {
                start = now;
            }
            latest = now;
        }

        /**
         * 
         */
        private void stop() {
            end = System.nanoTime();
            elapsed += end - latest;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void close() throws Exception {
            Profiler.this.stop();
        }
    }
}