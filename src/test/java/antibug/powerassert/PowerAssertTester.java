/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug.powerassert;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @version 2018/04/04 2:19:05
 */
public class PowerAssertTester implements BeforeAllCallback, BeforeEachCallback {

    /** For self test. */
    private final List<Operand> expecteds = new ArrayList();

    /** For self test. */
    private final List<String> operators = new ArrayList();

    /** The expected error message. */
    private String message;

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        PowerAssert.errorCapture = this::validate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        expecteds.clear();
        operators.clear();
        message = null;
    }

    /**
     * <p>
     * Validate error message.
     * </p>
     * 
     * @param context
     */
    void validate(PowerAssertionError e) {
        PowerAssertContext context = e.context;

        if (context.stack.size() != 1) {
            throw new AssertionError("Stack size is not 1. \n" + context.stack);
        }

        String code = context.stack.peek().toString();

        for (Operand expected : expecteds) {
            if (!context.operands.contains(expected)) {
                throw new AssertionError("Can't capture the below operand.\r\nExpect  : " + expected
                        .toString() + "\r\nValue : " + expected.value + "\r\nActual : " + context.operands + "\r\n");
            }
        }

        for (String operator : operators) {
            if (code.indexOf(operator) == -1) {
                throw new AssertionError("Can't capture the below code.\r\nExpect  : " + operator + "\r\nCode : " + code);
            }
        }

        if (message != null) {
            if (!e.getMessage().startsWith(message)) {
                throw new AssertionError("Actual message is [" + e.getMessage() + "].");
            }
        }
    }

    /**
     * @param name
     * @param value
     */
    void willCapture(String name, Object value) {
        expecteds.add(new Operand(name, value));
    }

    /**
     * @param operator
     */
    void willUse(String operator) {
        operators.add(operator);
    }

    /**
     * @param string
     */
    void willMessage(String message) {
        this.message = message;
    }
}
