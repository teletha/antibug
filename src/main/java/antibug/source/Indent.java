/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.source;

/**
 * @version 2014/08/05 15:41:00
 */
class Indent {

    /** The current AST indent size. */
    private int size = 0;

    /** The indent pattern. */
    private String indent = "    ";

    /**
     * <p>
     * Increase indent size.
     * </p>
     */
    void increase() {
        increase(1);
    }

    /**
     * <p>
     * Increase indent size.
     * </p>
     */
    void increase(int size) {
        this.size += size;
    }

    /**
     * <p>
     * Decrease indent size.
     * </p>
     */
    void decrease() {
        decrease(1);
    }

    /**
     * <p>
     * Decrease indent size.
     * </p>
     */
    void decrease(int size) {
        this.size -= size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < size; i++) {
            builder.append(indent);
        }
        return builder.toString();
    }
}