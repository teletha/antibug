/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.powerassert2;

/**
 * <p>
 * This is internal class for enhanced error.
 * </p>
 * 
 * @version 2012/01/24 13:14:52
 */
@SuppressWarnings("serial")
public class PowerAssertionError extends AssertionError {

    /** The related variables holder. */
    final PowerAssertContext context;

    /**
     * <p>
     * This is internal constructor. Don't use it.
     * </p>
     */
    public PowerAssertionError() {
        super("");

        this.context = PowerAssertContext.current;
    }

    /**
     * <p>
     * This is internal constructor. Don't use it.
     * </p>
     */
    public PowerAssertionError(boolean detailMessage, PowerAssertContext context) {
        super(detailMessage);

        this.context = PowerAssertContext.current;
    }

    /**
     * <p>
     * This is internal constructor. Don't use it.
     * </p>
     */
    public PowerAssertionError(char detailMessage) {
        super(detailMessage);

        this.context = PowerAssertContext.current;
    }

    /**
     * <p>
     * This is internal constructor. Don't use it.
     * </p>
     */
    public PowerAssertionError(double detailMessage) {
        super(detailMessage);

        this.context = PowerAssertContext.current;
    }

    /**
     * <p>
     * This is internal constructor. Don't use it.
     * </p>
     */
    public PowerAssertionError(float detailMessage) {
        super(detailMessage);

        this.context = PowerAssertContext.current;
    }

    /**
     * <p>
     * This is internal constructor. Don't use it.
     * </p>
     */
    public PowerAssertionError(int detailMessage) {
        super(detailMessage);

        this.context = PowerAssertContext.current;
    }

    /**
     * <p>
     * This is internal constructor. Don't use it.
     * </p>
     */
    public PowerAssertionError(long detailMessage) {
        super(detailMessage);

        this.context = PowerAssertContext.current;
    }

    /**
     * <p>
     * This is internal constructor. Don't use it.
     * </p>
     */
    public PowerAssertionError(Object detailMessage) {
        super(detailMessage);

        this.context = PowerAssertContext.current;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return super.getMessage() + "\n" + context;
    }
}
