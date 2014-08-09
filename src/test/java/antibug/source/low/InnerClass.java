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

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @version 2014/08/02 17:00:08
 */
public class InnerClass {

    /**
     * @version 2014/08/02 17:37:56
     */
    public static class StaticInnerClass<T> {

        protected final class Nested {
        }

        @interface NestedAnnotation {
        }

        @SuppressWarnings("unused")
        private enum NestedEnum {
        }
    }

    <T> Consumer<T> anonymous() {
        return new Consumer<T>() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void accept(Object t) {
            }
        };
    }

    Supplier<String> local() {
        class Local implements Supplier<String> {

            /**
             * {@inheritDoc}
             */
            @Override
            public String get() {
                return null;
            }
        }

        return new Local();
    }
}
