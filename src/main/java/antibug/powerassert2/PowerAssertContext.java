/*
 * Copyright (C) 2013 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.powerassert2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 2013/08/29 8:12:07
 */
@SuppressWarnings("serial")
public class PowerAssertContext implements Journal {

    /** The current context. */
    private static PowerAssertContext current;;

    /** The local variable name mapping. */
    private static final Map<Integer, List<String[]>> locals = new ConcurrentHashMap();

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public <T> T log(T value, String code) {
        record(value, code);

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
    public boolean log(boolean value, String code) {
        record(Boolean.valueOf(value), code);

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
    public byte log(byte value, String code) {
        record(Byte.valueOf(value), code);

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
    public short log(short value, String code) {
        record(Short.valueOf(value), code);

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
    public int log(int value, String code) {
        record(Integer.valueOf(value), code);

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
    public long log(long value, String code) {
        record(Long.valueOf(value), code);

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
    public float log(float value, String code) {
        record(Float.valueOf(value), code);

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
    public double log(double value, String code) {
        record(Double.valueOf(value), code);

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
    private void record(Object value, String code) {
        System.out.println(code + "  :  " + value);
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
    public <T> T log(T value, int methodId, int localId) {
        record(value, methodId, localId);

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
    public boolean log(boolean value, int methodId, int localId) {
        record(Boolean.valueOf(value), methodId, localId);

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
    public byte log(byte value, int methodId, int localId) {
        record(Byte.valueOf(value), methodId, localId);

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
    public short log(short value, int methodId, int localId) {
        record(Short.valueOf(value), methodId, localId);

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
    public int log(int value, int methodId, int localId) {
        record(Integer.valueOf(value), methodId, localId);

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
    public long log(long value, int methodId, int localId) {
        record(Long.valueOf(value), methodId, localId);

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
    public float log(float value, int methodId, int localId) {
        record(Float.valueOf(value), methodId, localId);

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
    public double log(double value, int methodId, int localId) {
        record(Double.valueOf(value), methodId, localId);

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
    private void record(Object value, int methodId, int localId) {
        String[] local = locals.get(methodId).get(localId);

        record(value, local[0]);
    }

    //
    // /**
    // * <p>
    // * Initialize power assert context and log value and code.
    // * </p>
    // *
    // * @param value A current value.
    // * @param code A current code.
    // * @return A input value.
    // */
    // public static <T> T init(T value, String code) {
    // initialize();
    //
    // return log(value, code);
    // }
    //
    // /**
    // * <p>
    // * Initialize power assert context and log value and code.
    // * </p>
    // *
    // * @param value A current value.
    // * @param code A current code.
    // * @return A input value.
    // */
    // public static boolean init(boolean value, String code) {
    // initialize();
    //
    // return log(value, code);
    // }
    //
    // /**
    // * <p>
    // * Initialize power assert context and log value and code.
    // * </p>
    // *
    // * @param value A current value.
    // * @param code A current code.
    // * @return A input value.
    // */
    // public static byte init(byte value, String code) {
    // initialize();
    //
    // return log(value, code);
    // }
    //
    // /**
    // * <p>
    // * Initialize power assert context and log value and code.
    // * </p>
    // *
    // * @param value A current value.
    // * @param code A current code.
    // * @return A input value.
    // */
    // public static short init(short value, String code) {
    // initialize();
    //
    // return log(value, code);
    // }
    //
    // /**
    // * <p>
    // * Initialize power assert context and log value and code.
    // * </p>
    // *
    // * @param value A current value.
    // * @param code A current code.
    // * @return A input value.
    // */
    // public static int init(int value, String code) {
    // initialize();
    //
    // return log(value, code);
    // }
    //
    // /**
    // * <p>
    // * Initialize power assert context and log value and code.
    // * </p>
    // *
    // * @param value A current value.
    // * @param code A current code.
    // * @return A input value.
    // */
    // public static long init(long value, String code) {
    // initialize();
    //
    // return log(value, code);
    // }
    //
    // /**
    // * <p>
    // * Initialize power assert context and log value and code.
    // * </p>
    // *
    // * @param value A current value.
    // * @param code A current code.
    // * @return A input value.
    // */
    // public static float init(float value, String code) {
    // initialize();
    //
    // return log(value, code);
    // }
    //
    // /**
    // * <p>
    // * Initialize power assert context and log value and code.
    // * </p>
    // *
    // * @param value A current value.
    // * @param code A current code.
    // * @return A input value.
    // */
    // public static double init(double value, String code) {
    // initialize();
    //
    // return log(value, code);
    // }

    /**
     * <p>
     * Initialize power assert context.
     * </p>
     */
    public static PowerAssertContext initialize() {
        current = new PowerAssertContext();

        return get();
    }

    /**
     * <p>
     * Initialize power assert context.
     * </p>
     */
    public static PowerAssertContext get() {
        return current;
    }

    /**
     * <p>
     * Register local variable.
     * </p>
     * 
     * @param methodId
     * @param name
     * @param description
     */
    public static void registerLocalVariable(int methodId, String name, String description, int index) {
        List<String[]> local = locals.get(methodId);

        if (local == null) {
            local = new ArrayList();

            locals.put(methodId, local);
        }

        // ensure size
        for (int i = local.size(); i < index + 1; i++) {
            local.add(null);
        }
        local.set(index, new String[] {name, description});
    }
}
