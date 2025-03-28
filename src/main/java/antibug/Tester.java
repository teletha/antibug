/*
 * Copyright (C) 2025 The ANTIBUG Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug;

import java.util.List;
import java.util.Objects;

public class Tester {
    /**
     * Test {@link List} items.
     * 
     * @param <T>
     * @param expected
     * @param actual
     * @return
     */
    public static <T> boolean same(List<T> expected, List<T> actual) {
        assert expected != null;
        assert actual != null;

        if (expected.size() != actual.size()) {
            return false;
        }
        for (int i = 0; i < expected.size(); i++) {
            if (!Objects.equals(expected.get(i), actual.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Test {@link List} items.
     * 
     * @param <T>
     * @param expected
     * @param actual
     * @return
     */
    public static <T> boolean different(List<T> expected, List<T> actual) {
        return !same(expected, actual);
    }
}