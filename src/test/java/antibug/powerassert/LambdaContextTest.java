/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.powerassert;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class LambdaContextTest {

    @RegisterExtension
    static PowerAssertTester test = new PowerAssertTester();

    private String instanceAccess = "instance access";

    @Test
    void param() {
        test.willCapture("v.equals(\"ng\")", false);
        run("ok", v -> {
            assert v.equals("ng");
        });
    }

    @Test
    void paramInNestedNoParameterLambda() {
        test.willCapture("v.equals(\"use in nested runnable\")", false);
        run("ok", v -> {
            run(() -> {
                assert v.equals("use in nested runnable");
            });
        });
    }

    @Test
    void paramInNestedParameterLambda() {
        test.willCapture("v.equals(\"use in nested consumer\")", false);
        run("ok", v -> {
            run("ignored", x -> {
                assert v.equals("use in nested consumer");
            });
        });
    }

    @Test
    void paramInNestedInstantAccessibleNoParameterLambda() {
        test.willCapture("v.equals(instanceAccess)", false);
        run("ok", v -> {
            run(() -> {
                assert v.equals(instanceAccess);
            });
        });
    }

    @Test
    void paramInNestedInstantAccessibleParameterLambda() {
        test.willCapture("v.equals(instanceAccess)", false);
        run("ok", v -> {
            run("ignored", x -> {
                assert v.equals(instanceAccess);
            });
        });
    }

    @Test
    void paramInNestedLocalAccessibleNoParameterLambda() {
        CharSequence local = "local";

        test.willCapture("v.equals(local)", false);
        run("ok", v -> {
            run(() -> {
                assert v.equals(local);
            });
        });
    }

    private void run(String v, Consumer<String> con) {
        con.accept(v);
    }

    private void run(Runnable run) {
        run.run();
    }
}
