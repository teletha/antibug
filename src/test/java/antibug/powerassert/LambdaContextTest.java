/*
 * Copyright (C) 2021 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
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
    void noParameter() {
        test.willCapture("v.equals(\"use in nested runnable\")", false);
        run("ok", v -> {
            run(() -> {
                assert v.equals("use in nested runnable");
            });
        });
    }

    @Test
    void parameter() {
        test.willCapture("v.equals(\"use in nested consumer\")", false);
        run("ok", v -> {
            run("ignored", x -> {
                assert v.equals("use in nested consumer");
            });
        });
    }

    @Test
    void instantAccessibleNoParameter() {
        test.willCapture("v.equals(instanceAccess)", false);
        run("ok", v -> {
            run(() -> {
                assert v.equals(instanceAccess);
            });
        });
    }

    @Test
    void instantAccessibleParameter() {
        test.willCapture("v.equals(instanceAccess)", false);
        run("ok", v -> {
            run("ignored", x -> {
                assert v.equals(instanceAccess);
            });
        });
    }

    @Test
    void localAccessibleNoParameter() {
        CharSequence local = "local";

        test.willCapture("v.equals(local)", false);
        run("ok", v -> {
            run(() -> {
                assert v.equals(local);
            });
        });
    }

    @Test
    void localAccessibleNoParameterOrderShuffle1() {
        String x = "use";
        String y = "local";

        test.willCapture("v.equals(x.concat(y))", false);
        run("ok", v -> {
            run(() -> {
                assert v.equals(x.concat(y));
            });
        });
    }

    @Test
    void localAccessibleNoParameterOrderShuffle2() {
        String x = "use";
        String y = "local";

        test.willCapture("v.equals(y.concat(x))", false);
        run("ok", v -> {
            run(() -> {
                assert v.equals(y.concat(x));
            });
        });
    }

    @Test
    void localAccessibleNoParameterOrderShuffle3() {
        String x = "use";
        String y = "local";

        test.willCapture("x.equals(y.concat(v))", false);
        run("ok", v -> {
            run(() -> {
                assert x.equals(y.concat(v));
            });
        });
    }

    @Test
    void localAccessibleNoParameterOrderShuffle4() {
        String p = "use";
        String q = "local";

        test.willCapture("q.equals(p.concat(r))", false);
        run("ok", r -> {
            run(() -> {
                assert q.equals(p.concat(r));
            });
        });
    }

    @Test
    void localAccessibleNoParameterOrderShuffle5() {
        String x = "use";
        String y = "local";

        test.willCapture("y.equals(v.concat(x))", false);
        run("oka", v -> {
            run(() -> {
                assert y.equals(v.concat(x));
            });
        });
    }

    private <V> void run(V v, Consumer<V> con) {
        con.accept(v);
    }

    private void run(Runnable run) {
        run.run();
    }
}