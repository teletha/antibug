/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.dummy;

import java.util.ArrayList;
import java.util.List;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * @param <P> First item
 * @param <Q> Second bounded item
 */
@API(status = Status.EXPERIMENTAL)
public abstract class Parameterized<P, Q extends Parameterized & AutoCloseable> extends ArrayList<Q>
        implements InterfaceA, InterfaceB, InterfaceC<Q>, AutoCloseable {

    /** The typed param. */
    public P fisrt;

    /**
     * Calculate values.
     * 
     * @param <R> A return type.
     * @param first A first type.
     * @param second A second type.
     * @return The calculated value.
     */
    public <R extends List<Q> & Cloneable> R calculate(Class<? extends List<P>> first, List<? super Q> second) {
        return null;
    }
}
