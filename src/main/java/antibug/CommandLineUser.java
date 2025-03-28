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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * This is pseudo character-based user.
 */
public class CommandLineUser {

    /** The mock system input. */
    public final InputStream input;

    /** Internal API */
    private final MockInputStream mockInput;

    /** The original. */
    private InputStream originalInput = System.in;

    /** The original system output. */
    public final PrintStream output;

    /** Internal API */
    private final MockOutputStream mockOutput;

    /** The original. */
    private PrintStream originalOutput = System.out;

    /** The original system error. */
    public final PrintStream error;

    /** Internal API */
    private final MockOutputStream mockError;

    /** The original. */
    private PrintStream originalError = System.err;

    /** The ignore system output. */
    private boolean ignore;

    /** The message buffer. */
    private List<Runnable> messages = new ArrayList();

    /**
     * 
     */
    public CommandLineUser() {
        this(false);
    }

    /**
     * @param ignoreOutput
     */
    public CommandLineUser(boolean ignoreOutput) {
        this.ignore = ignoreOutput;

        // swap
        input = mockInput = new MockInputStream();
        output = mockOutput = new MockOutputStream(false, ignore);
        error = mockError = new MockOutputStream(true, ignore);
    }

    /**
     * Mock standard interface.
     * 
     * @return
     */
    public CommandLineUser mockSystem() {
        originalInput = System.in;
        originalOutput = System.out;
        originalError = System.err;

        System.setIn(mockInput);
        System.setOut(mockOutput);
        System.setErr(mockError);

        return this;
    }

    /**
     * Revert to standard interface.
     * 
     * @return
     */
    public CommandLineUser unmockSystem() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
        System.setErr(originalError);

        return this;
    }

    /**
     * @param values
     */
    public void willInput(String... values) {
        for (String value : values) {
            mockInput.deque.add(new UserInput(value.concat("\r\n")));
        }
    }

    /**
     * <p>
     * Test whether this user outputed the specified value or not.
     * </p>
     * 
     * @param valus An outputed value to test.
     * @return A result.
     */
    public boolean receiveOutput(String valus) {
        return mockOutput.text.indexOf(valus) != -1;
    }

    /**
     * <p>
     * Test whether this user outputed the specified value or not.
     * </p>
     * 
     * @param valus An outputed value to test.
     * @return A result.
     */
    public boolean receiveError(String valus) {
        return mockError.text.indexOf(valus) != -1;
    }

    /**
     * Test whether this user outputed the specified value or not.
     * 
     * @param valus An outputed value to test.
     * @return A result.
     */
    public boolean receive(String valus) {
        return receiveOutput(valus) || receiveError(valus);
    }

    /**
     * Clear buffered output message.
     */
    public void clearOutput() {
        mockOutput.text = new StringBuilder();
    }

    /**
     * Clear buffered error message.
     */
    public void clearError() {
        mockError.text = new StringBuilder();
    }

    /**
     * Clear buffered output and error message.
     */
    public void clear() {
        clearOutput();
        clearError();
    }

    /**
     * @version 2014/07/14 22:20:48
     */
    private class MockOutputStream extends PrintStream {

        /** The original. */
        private final PrintStream original;

        private final boolean discard;

        /** The output result. */
        private StringBuilder text = new StringBuilder();

        /**
         * @param original
         */
        private MockOutputStream(boolean error, boolean discard) {
            super(error ? System.err : System.out);

            this.original = (PrintStream) out;
            this.discard = discard;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void write(byte[] buf, int off, int len) {
            String message = new String(buf, off, len);
            text.append(message);
            messages.add(() -> {
                if (!discard) {
                    original.append(message);
                }
            });
        }
    }

    /**
     * @version 2014/07/14 22:20:51
     */
    private class MockInputStream extends InputStream {

        /** The user input. */
        private final Deque<UserInput> deque = new ArrayDeque();

        /**
         * {@inheritDoc}
         */
        @Override
        public int read() throws IOException {
            UserInput input = deque.peekFirst();

            if (input == null) {
                return -1;
            } else {
                int i = input.read();

                if (i == -1) {
                    deque.pollFirst();

                    return -1;
                }
                return i;
            }
        }
    }

    /**
     * @version 2014/07/14 22:20:58
     */
    private static class UserInput {

        /** The user input value. */
        private final String input;

        /** The current input index. */
        private int index = 0;

        /**
         * @param input
         */
        private UserInput(String input) {
            this.input = input;
        }

        /**
         * <p>
         * Read next input character.
         * </p>
         * 
         * @return A next input character.
         */
        private int read() {
            return index == input.length() ? -1 : input.charAt(index++);
        }
    }
}