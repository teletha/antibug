/*
 * Copyright (C) 2025 The ANTIBUG Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug.bytecode;

import static net.bytebuddy.jar.asm.Type.*;

import net.bytebuddy.jar.asm.MethodVisitor;

/**
 * @version 2012/01/18 9:56:27
 */
public class Constant extends Bytecode<Constant> {

    /** The constant value. */
    private Object value;

    /**
     * @param value
     */
    public Constant(Object value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(MethodVisitor visitor, boolean requireNonPrimitive) {
        visitor.visitLdcInsn(value);

        if (value instanceof Integer) {
            wrap(visitor, INT_TYPE);
        } else if (value instanceof Long) {
            wrap(visitor, LONG_TYPE);
        } else if (value instanceof Float) {
            wrap(visitor, FLOAT_TYPE);
        } else if (value instanceof Double) {
            wrap(visitor, DOUBLE_TYPE);
        }
    }
}