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

/**
 * @version 2014/08/02 18:33:46
 */
public class EnumClass {

    enum Single {
        A;
    }

    enum MultipleInOneLine {
        A, B, C;
    }

    enum MultipleInMultipleLines {
        /** Fisrt */
        A,

        /** Second */
        B,

        /** Last */
        C;
    }

    enum ConstructorAndMethod {
        A("aaa");

        String name;

        ConstructorAndMethod(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }
    }

    enum AbstractMethod {
        A("aaa") {

            /**
             * {@inheritDoc}
             */
            @Override
            String getName() {
                return name;
            }
        };

        String name;

        AbstractMethod(String name) {
            this.name = name;
        }

        abstract String getName();
    }
}
