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

import static net.bytebuddy.jar.asm.Opcodes.*;
import static net.bytebuddy.jar.asm.Type.*;

import net.bytebuddy.jar.asm.MethodVisitor;

/**
 * @version 2012/01/18 8:37:28
 */
public class Instruction extends Bytecode<Instruction> {

    /** The operation code. */
    public int opcode;

    /**
     * @param opcode
     */
    public Instruction(int opcode) {
        this.opcode = opcode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(MethodVisitor visitor, boolean requireNonPrimitive) {
        visitor.visitInsn(opcode);

        if (requireNonPrimitive) {
            switch (opcode) {
            case ICONST_0:
            case ICONST_1:
            case ICONST_2:
            case ICONST_3:
            case ICONST_4:
            case ICONST_5:
            case ICONST_M1:
                wrap(visitor, INT_TYPE);
                break;

            case LCONST_0:
            case LCONST_1:
                wrap(visitor, LONG_TYPE);
                break;

            case FCONST_0:
            case FCONST_1:
            case FCONST_2:
                wrap(visitor, FLOAT_TYPE);
                break;

            case DCONST_0:
            case DCONST_1:
                wrap(visitor, DOUBLE_TYPE);
                break;
            }
        }
    }
}