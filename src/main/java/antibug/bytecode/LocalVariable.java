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
import net.bytebuddy.jar.asm.Type;

/**
 * @version 2012/01/18 10:12:18
 */
public class LocalVariable extends Bytecode<LocalVariable> {

    /** The operation code. */
    public int opcode;

    /** The local variable index. */
    public int index;

    /** The variable type. */
    public Type type;

    /** The delegator. */
    private MethodVisitor visitor;

    /**
     * @param opcode
     * @param index
     */
    public LocalVariable(int opcode, int index) {
        this.opcode = opcode;
        this.index = index;

        switch (opcode) {
        case ISTORE:
        case ILOAD:
            type = INT_TYPE;
            break;

        case LSTORE:
        case LLOAD:
            type = LONG_TYPE;
            break;

        case FSTORE:
        case FLOAD:
            type = FLOAT_TYPE;
            break;

        case DSTORE:
        case DLOAD:
            type = DOUBLE_TYPE;
            break;

        case ASTORE:
        case ALOAD:
            type = OBJECT_TYPE;
            break;
        }
    }

    /**
     * @param type
     * @param sorter
     */
    LocalVariable(Type type, LocalVariablesSorter sorter) {
        this.type = type;
        this.index = sorter.newLocal(type);
        this.opcode = type.getOpcode(ILOAD);
        this.visitor = sorter;
    }

    /**
     * 
     * 
     */
    public void store() {
        visitor.visitVarInsn(type.getOpcode(ISTORE), index);
    }

    /**
     * 
     * 
     */
    public void load() {
        visitor.visitVarInsn(type.getOpcode(ILOAD), index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(MethodVisitor visitor, boolean isNonPrimitive) {
        visitor.visitVarInsn(opcode, index);

        if (isNonPrimitive) {
            wrap(visitor, type);
        }
    }
}