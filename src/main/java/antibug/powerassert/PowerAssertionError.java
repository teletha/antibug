/*
 * Copyright (C) 2024 The ANTIBUG Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug.powerassert;

/**
 * This is internal class for enhanced error.
 */
@SuppressWarnings("serial")
public class PowerAssertionError extends AssertionError {

    /** The related variables holder. */
    final PowerAssertContext context;

    /**
     * This is internal constructor. Don't use it.
     */
    public PowerAssertionError(PowerAssertContext context) {
        super("");

        this.context = context;
    }

    /**
     * This is internal constructor. Don't use it.
     */
    public PowerAssertionError(boolean detailMessage, PowerAssertContext context) {
        super(detailMessage);

        this.context = context;
    }

    /**
     * This is internal constructor. Don't use it.
     */
    public PowerAssertionError(char detailMessage, PowerAssertContext context) {
        super(detailMessage);

        this.context = context;
    }

    /**
     * This is internal constructor. Don't use it.
     */
    public PowerAssertionError(double detailMessage, PowerAssertContext context) {
        super(detailMessage);

        this.context = context;
    }

    /**
     * This is internal constructor. Don't use it.
     */
    public PowerAssertionError(float detailMessage, PowerAssertContext context) {
        super(detailMessage);

        this.context = context;
    }

    /**
     * This is internal constructor. Don't use it.
     */
    public PowerAssertionError(int detailMessage, PowerAssertContext context) {
        super(detailMessage);

        this.context = context;
    }

    /**
     * This is internal constructor. Don't use it.
     */
    public PowerAssertionError(long detailMessage, PowerAssertContext context) {
        super(detailMessage);

        this.context = context;
    }

    /**
     * This is internal constructor. Don't use it.
     */
    public PowerAssertionError(Object detailMessage, PowerAssertContext context) {
        super(detailMessage);

        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return super.getMessage() + "\n" + context;
    }
}