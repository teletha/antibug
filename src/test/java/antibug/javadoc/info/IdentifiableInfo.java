/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.javadoc.info;


/**
 * @version 2014/07/29 15:38:20
 */
public abstract class IdentifiableInfo {

    /** The id. */
    private Identifier id;

    /**
     * Get the id property of this {@link IdentifiableInfo}.
     * 
     * @return The id property.
     */
    public Identifier getId() {
        if (id == null) {
            id = computeId();
        }
        return id;
    }

    /**
     * <p>
     * Compute {@link Identifier}.
     * </p>
     * 
     * @return
     */
    protected abstract Identifier computeId();
}
