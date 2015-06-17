/*
 * Copyright (C) 2015 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.powerassert;

/**
 * @version 2014/03/09 10:23:06
 */
public class PowerAssertOffError extends Error {

    /**
     * 
     */
    private static final long serialVersionUID = 4563142651759605792L;

    /**
     * @param cause
     */
    public PowerAssertOffError(Throwable cause) {
        super(cause);
    }
}
