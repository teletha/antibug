/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.source.low;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @version 2014/08/04 13:43:04
 */
@SuppressWarnings("unused")
public class Lambda {

    void main() {
        Supplier<String> run = () -> {
            return "value";
        };

        Consumer<String> consumer = value -> {
        };

        BiConsumer<String, String> biConsumer = (one, two) -> {
        };

        biConsumer = (String one, String two) -> {
        };
    }
}
