/*
 * Copyright (C) 2018 Nameless Production Committee
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

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

/**
 * <p>
 * This is pseudo character-based user.
 * </p>
 * 
 * @version 2018/03/31 3:08:40
 */
public class CommandLineUser implements BeforeEachCallback, AfterEachCallback, TestExecutionExceptionHandler {

    /** The mock system input. */
    private MockInputStream input;

    /** The original system output. */
    private MockOutputStream output;

    /** The original system error. */
    private MockOutputStream error;

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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        // clear message
        messages.clear();

        // swap
        System.setIn(input = new MockInputStream());

        if (!ignore) {
            System.setOut(output = new MockOutputStream(false));
            System.setErr(error = new MockOutputStream(true));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        // restore original
        System.setIn(input.original);

        if (!ignore) {
            System.setOut(output.original);
            System.setErr(error.original);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        // display buffered message
        for (Runnable message : messages) {
            message.run();
        }
        throw throwable;
    }

    /**
     * @param value
     */
    public void willInput(String... values) {
        for (String value : values) {
            input.deque.add(new UserInput(value.concat("\r\n")));
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
        return output.text.indexOf(valus) != -1;
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
        return error.text.indexOf(valus) != -1;
    }

    /**
     * <p>
     * Test whether this user outputed the specified value or not.
     * </p>
     * 
     * @param valus An outputed value to test.
     * @return A result.
     */
    public boolean receive(String valus) {
        return receiveOutput(valus) || receiveError(valus);
    }

    /**
     * <p>
     * Clear buffered output message.
     * </p>
     * 
     * @return
     */
    public void clearOutput() {
        output.text = new StringBuilder();
    }

    /**
     * <p>
     * Clear buffered error message.
     * </p>
     * 
     * @return
     */
    public void clearError() {
        error.text = new StringBuilder();
    }

    /**
     * <p>
     * Clear buffered output and error message.
     * </p>
     * 
     * @return
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

        /** The output result. */
        private StringBuilder text = new StringBuilder();

        /**
         * @param original
         */
        private MockOutputStream(boolean error) {
            super(error ? System.err : System.out);

            this.original = (PrintStream) out;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void write(byte[] buf, int off, int len) {
            String message = new String(buf, off, len);

            text.append(message);

            messages.add(() -> {
                original.append(message);
            });
        }
    }

    /**
     * @version 2014/07/14 22:20:51
     */
    private class MockInputStream extends InputStream {

        /** The original system input. */
        private final InputStream original = System.in;

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
