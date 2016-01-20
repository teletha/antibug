/*
 * Copyright (C) 2015 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.javadoc.info;

import kiss.Decoder;
import kiss.Encoder;

/**
 * @version 2014/07/29 15:49:05
 */
public class IdentifierCodec implements Decoder<Identifier>, Encoder<Identifier> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Identifier decode(String value) {
        return Identifier.of(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encode(Identifier value) {
        return String.valueOf(value);
    }
}
