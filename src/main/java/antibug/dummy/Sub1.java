/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.dummy;

public class Sub1<P, Q extends Sub1<P, Q>> extends Parameterized<P, Q> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws Exception {
    }
}
