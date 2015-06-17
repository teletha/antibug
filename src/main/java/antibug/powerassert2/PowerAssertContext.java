/*
 * Copyright (C) 2015 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.powerassert2;

/**
 * @version 2013/08/29 8:12:07
 */
public class PowerAssertContext {

    /** The current context. */
    static PowerAssertContext current;

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public static <T> T log(T value, boolean init, String code) {
        record(value, init, code);

        return value;
    }

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public static boolean log(boolean value, boolean init, String code) {
        record(Boolean.valueOf(value), init, code);

        return value;
    }

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public static byte log(byte value, boolean init, String code) {
        record(Byte.valueOf(value), init, code);

        return value;
    }

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public static short log(short value, boolean init, String code) {
        record(Short.valueOf(value), init, code);

        return value;
    }

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public static int log(int value, boolean init, String code) {
        record(Integer.valueOf(value), init, code);

        return value;
    }

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public static long log(long value, boolean init, String code) {
        record(Long.valueOf(value), init, code);

        return value;
    }

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public static float log(float value, boolean init, String code) {
        record(Float.valueOf(value), init, code);

        return value;
    }

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public static double log(double value, boolean init, String code) {
        record(Double.valueOf(value), init, code);

        return value;
    }

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    private static void record(Object value, boolean init, String code) {
        if (init) {
            current = new PowerAssertContext();
        }

        System.out.println(code + "  :  " + value);
    }
}
