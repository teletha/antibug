/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.javasource;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @version 2014/07/31 16:22:55
 */
public class LineWriter extends Writer implements Iterable<StringBuilder> {

    /** The line code. */
    private static final String LINE = System.getProperty("line.separator");

    /** The line manager. */
    private final List<StringBuilder> lines = new ArrayList();

    /** The current line. */
    private StringBuilder current = new StringBuilder();

    /**
     * 
     */
    public LineWriter() {
        lines.add(current);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(String value) throws IOException {
        // use idetical equality
        if (value == LINE) {
            // update line
            current = new StringBuilder();
            lines.add(current);
        } else {
            super.write(value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        current.append(cbuf, off, len);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() throws IOException {
        // ignore
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        // ignore
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<StringBuilder> iterator() {
        return lines.iterator();
    }
}
