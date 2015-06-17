/*
 * Copyright (C) 2015 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.source.low;

/**
 * @version 2014/08/04 12:31:24
 */
public abstract class MethodCall {

    void main() {
        empty();
        param(20);
        params(10, "C.C");
        varargs(1, 2, 3);
        params(nest(2), generic("String"));

        // with this
        this.param(20);
        this.<String> generic("value");

        // static
        staticEmpty();
        MethodCall.staticEmpty();
        MethodCall.<String> staticGeneric("C.C");
    }

    abstract void empty();

    abstract void param(int value);

    abstract void params(int value, String name);

    abstract void varargs(int... values);

    abstract <T> T generic(T value);

    abstract int nest(int value);

    static void staticEmpty() {
    }

    static <T> T staticGeneric(T value) {
        return value;
    }
}
