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

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;

import antibug.benchmark.Benchmark.Code;

/**
 * @version 2012/01/30 10:48:09
 */
public class BenchmarkSample {

    @Rule
    public static final Benchmark benchmark = new Benchmark();

    private Path path = Paths.get(new File("").getAbsolutePath());

    @Test
    public void system() throws Exception {
        benchmark.measure(new Code() {

            private PathMatcher wildcard = FileSystems.getDefault().getPathMatcher("glob:*.java");

            @Override
            public Object measure() throws Throwable {
                return wildcard.matches(path);
            }
        });
    }

}
