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

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;

/**
 * @version 2014/08/04 14:19:32
 */
@SuppressWarnings("unused")
public class MethodReference {

    /**
     * 
     */
    public MethodReference(String value) {
        use(this::consume);
        use(MethodReference::new);
        use(MethodReference::<String> generic);

        IntSupplier supplier = value::length;
        ToIntFunction<String> intFunction = String::length;

        Consumer<String> consumer = System.out::println;
        Function<String, String> function = System::clearProperty;
        IntBinaryOperator op = Integer::sum;
    }

    private void use(Consumer<String> consumer) {
    }

    private void consume(String value) {
    }

    private static <T> void generic(T value) {
    }
}
