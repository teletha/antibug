/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.powerassert2;

import java.util.Arrays;

import org.objectweb.asm.Type;

/**
 * @version 2012/01/20 1:03:27
 */
class Operand {

    /** The unknown type. */
    private static final Type Unknown = Type.VOID_TYPE;

    /** The human redable expression. */
    final String name;

    /** The actual value. */
    final Object value;

    /** The type inference. */
    final Type inference;

    /**
     * @param name
     * @param value
     */
    Operand(String name, Object value) {
        this.name = name;
        this.value = value;
        this.inference = null;
    }

    /**
     * @param name
     * @param value
     * @param inference
     */
    Operand(String name, Object value, Type inference) {
        this.name = name;
        this.value = value;
        this.inference = inference;
    }

    /**
     * <p>
     * Decide that this operand has a value to display to user.
     * </p>
     * 
     * @return A result.
     */
    boolean isVariableHolder() {
        return true;
    }

    /**
     * <p>
     * Decide the type of this operand's value.
     * </p>
     * 
     * @return
     */
    Type getType() {
        return value == null ? inference == null ? Unknown : inference : Type.getType(value.getClass());
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        Class type = getClass();
        Class otherType = obj.getClass();

        if (!Operand.class.isAssignableFrom(type) || !Operand.class.isAssignableFrom(otherType)) {
            return false;
        }

        Operand other = (Operand) obj;
        String name = toString();
        String otherName = other.toString();

        // check name
        if (name == null) {
            if (otherName != null) {
                return false;
            }
        } else if (!name.equals(otherName)) {
            return false;
        }

        // check value
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            type = value.getClass();

            if (!type.isArray()) {
                return false;
            } else {
                type = type.getComponentType();

                if (type == int.class) {
                    return Arrays.equals((int[]) value, (int[]) other.value);
                }

                if (type == long.class) {
                    return Arrays.equals((long[]) value, (long[]) other.value);
                }

                if (type == float.class) {
                    return Arrays.equals((float[]) value, (float[]) other.value);
                }

                if (type == double.class) {
                    return Arrays.equals((double[]) value, (double[]) other.value);
                }

                if (type == char.class) {
                    return Arrays.equals((char[]) value, (char[]) other.value);
                }

                if (type == boolean.class) {
                    return Arrays.equals((boolean[]) value, (boolean[]) other.value);
                }

                if (type == short.class) {
                    return Arrays.equals((short[]) value, (short[]) other.value);
                }

                if (type == byte.class) {
                    return Arrays.equals((byte[]) value, (byte[]) other.value);
                }
                return Arrays.deepEquals((Object[]) value, (Object[]) other.value);
            }
        }
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return name;
    }
}