/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.doc;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import kiss.I;

public class ExecutableInfoTest extends JavadocTestSupport {

    @Test
    void parameter0() {
        ExecutableInfo info = currentMethod();
        assert info.name.text().equals("parameter0");
        assert info.params.size() == 0;
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    void parameter1(String value) {
        assert checkParamName(currentMethod(), "value");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    void parameter2(String value, String text) {
        assert checkParamName(currentMethod(), "value", "text");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    void parameter3(String value, String text, Object context) {
        assert checkParamName(currentMethod(), "value", "text", "context");
    }

    /**
     * Shortcut method.
     * 
     * @param info
     * @param expected
     * @return
     */
    private boolean checkParamName(ExecutableInfo info, String... expected) {
        assert info.params.stream().map(v -> v.ⅰ).collect(Collectors.toList()).containsAll(I.set(expected));
        return true;
    }
}
