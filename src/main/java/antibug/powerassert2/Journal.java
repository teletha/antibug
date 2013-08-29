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

/**
 * @version 2013/08/29 16:06:50
 */
public interface Journal {

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public <T> T log(T value, String code);

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public boolean log(boolean value, String code);

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public byte log(byte value, String code);

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public short log(short value, String code);

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public int log(int value, String code);

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public long log(long value, String code);

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public float log(float value, String code);

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public double log(double value, String code);

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public <T> T log(T value, int methodId, int localId);

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public boolean log(boolean value, int methodId, int localId);

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public byte log(byte value, int methodId, int localId);

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public short log(short value, int methodId, int localId);

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public int log(int value, int methodId, int localId);

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public long log(long value, int methodId, int localId);

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public float log(float value, int methodId, int localId);

    /**
     * <p>
     * Log value and code.
     * </p>
     * 
     * @param value A current value.
     * @param code A current code.
     * @return A input value.
     */
    public double log(double value, int methodId, int localId);
}
