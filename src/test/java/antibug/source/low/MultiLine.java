/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.source.low;

import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.IllegalChannelGroupException;

import javax.crypto.IllegalBlockSizeException;

/**
 * @version 2014/08/05 12:27:18
 */
public abstract class MultiLine
        implements java.util.function.IntBinaryOperator, java.util.function.IntConsumer,
        com.sun.tools.doclets.internal.toolkit.taglets.InheritableTaglet,
        com.sun.org.apache.xml.internal.dtm.ref.IncrementalSAXSource {

    String[] array = {"looooooooooooooooooooooooooooooooooooooooooong", "short", "looooooooooooooooooooooooooooooong",
            "short", "looooooooooooooooooooooooooooooong", "short", "log", "short",
            "looooooooooooooooooooooooooooooooooooooooooong"};

    int[][] nestedArray = {
            {123456789, 123456789, 123456789, 123456789, 123456789, 123456789, 123456789, 123456789, 123456789,
                    123456789, 123456789, 123456789, 123456789, 123456789, 123456789, 123456789, 123456789, 123456789,
                    123456789, 123456789, 123456789, 123456789},
            {123456789, 123456789, 123456789, 123456789, 123456789, 123456789, 123456789, 123456789, 123456789,
                    123456789, 123456789, 123456789, 123456789, 123456789}};

    StringBuilder chain = new StringBuilder().append("0000000000000000000000000000")
            .append("111111111111111111111111111111")
            .append("2222222222222222222222222222")
            .append("3333333333333333333333333333");

    abstract void throwMultiLines(int p) throws IllegalAccessError, IllegalAccessException, IllegalArgumentException,
            IllegalMonitorStateException, IllegalStateException, IllegalThreadStateException,
            IllegalBlockingModeException, IllegalBlockSizeException, IllegalChannelGroupException;

    abstract void throwNextLine(int parameter1, int parameter2, int parameter3, int parameter4, int parameter5, int parameter6, int parameter7, int parameter8, int parameter9, int parameter10)
            throws IllegalAccessError, IllegalAccessException, IllegalArgumentException, IllegalMonitorStateException,
            IllegalStateException, IllegalThreadStateException, IllegalBlockingModeException,
            IllegalBlockSizeException, IllegalChannelGroupException;
}
