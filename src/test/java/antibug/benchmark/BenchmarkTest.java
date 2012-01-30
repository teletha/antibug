/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.benchmark;

import org.junit.Rule;
import org.junit.Test;

import antibug.benchmark.Benchmark.Code;

/**
 * @version 2012/01/30 10:48:09
 */
public class BenchmarkTest {

    @Rule
    public static final Benchmark benchmark = new Benchmark();

    @Test
    public void parseInt() throws Exception {
        benchmark.measure(new Code() {

            @Override
            public Object call() throws Throwable {
                return null;
            }
        });
    }

    @Test
    public void parseIn2t() throws Exception {
        benchmark.measure(new Code() {

            @Override
            public Object call() throws Throwable {
                return null;
            }
        });
    }
}
