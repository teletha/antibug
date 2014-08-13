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

/**
 * @version 2014/08/04 12:08:13
 */
public abstract strictfp class Modifier {

    protected static final int value1 = 0;

    private static final int value2 = 0;

    transient volatile float transientValue;

    native void nativeMethod();

    public abstract void abstractMethod();

    private static final synchronized void synchronizedMethod() {
    }
}