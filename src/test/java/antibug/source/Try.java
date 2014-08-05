/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.source;

import java.io.Closeable;
import java.io.IOException;

/**
 * @version 2014/08/04 11:51:07
 */
public class Try {

    int TryCatch() {
        try {
            return 10;
        } catch (Exception e) {
            return 20;
        }
    }

    int TryMultiCatch() {
        try {
            return 10;
        } catch (Exception | IllegalAccessError e) {
            return 20;
        }
    }

    int TryCatches() {
        try {
            return 10;
        } catch (IllegalAccessError e) {
            return 20;
        } catch (Exception e) {
            return 30;
        }
    }

    int TryCatcheFinally(int value) {
        try {
            value++;
        } catch (IllegalAccessError e) {
            value++;
        } finally {
            value++;
        }
        return value;
    }

    int TryFinally(int value) {
        try {
            value++;
        } finally {
            value--;
        }
        return value;
    }

    int TryWithResource(Closeable resource) {
        try (final Closeable close = resource) {
            return 1;
        } catch (IOException e) {
            return 2;
        }
    }

    int TryWithResource2(Closeable resource) throws Exception {
        try (final Closeable close1 = resource; final Closeable clsoe2 = resource) {
            return 1;
        }
    }
}
