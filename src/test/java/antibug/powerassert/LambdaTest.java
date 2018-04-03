/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug.powerassert;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * @version 2018/03/31 16:31:21
 */
class LambdaTest {

    @RegisterExtension
    static PowerAssertTester test = new PowerAssertTester();

    @Test
    void runnable() {
        test.willCapture("runnable(() -> { ... })", false);
        assert runnable(() -> new Object());
    }

    private boolean runnable(Runnable run) {
        return false;
    }

    @Test
    void runnableMethodReference() {
        List list = new ArrayList();

        test.willCapture("runnable(list::clear)", false);
        assert runnable(list::clear);
    }

    @Test
    void consumer() {
        test.willCapture("consumer(p1 -> { ... })", false);
        assert consumer(v -> v.toString());
    }

    private boolean consumer(Consumer<String> value) {
        return false;
    }

    @Test
    void consumerMethodReference() {
        test.willCapture("consumer(this::consumerRef)", false);
        assert consumer(this::consumerRef);
    }

    private void consumerRef(String value) {
    }

    @Test
    void bifunction() {
        test.willCapture("bifunction((p1, p2) -> { ... })", false);
        assert bifunction((context, value) -> "");
    }

    private boolean bifunction(BiFunction<String, String, String> run) {
        return false;
    }

    @Test
    void bifunctionMethodReference() {
        test.willCapture("bifunction(String::concat)", false);
        assert bifunction(String::concat);
    }

    @Test
    void supplier() {
        String value = "outer";

        test.willCapture("supplier(() -> { ... })", false);
        assert supplier(() -> value);
    }

    private boolean supplier(Supplier<String> supplier) {
        return false;
    }
}
