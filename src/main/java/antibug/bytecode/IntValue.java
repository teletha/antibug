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
 * @version 2012/01/18 9:51:34
 */
public class IntValue extends Bytecode<IntValue> {

    /** The opration code. */
    public int opcode;

    /** The value. */
    public int operand;

    /**
     * @param opcode
     * @param operand
     */
    public IntValue(int opcode, int operand) {
        this.opcode = opcode;
        this.operand = operand;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(MethodVisitor visitor, boolean isNonPrimitive) {
        visitor.visitIntInsn(opcode, operand);
        wrap(visitor, INT_TYPE);
    }
}