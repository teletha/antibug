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
 * @version 2014/07/31 9:29:12
 */
public class MarkupValue {

    /** The decoded html. */
    private String html;

    /**
     * @param html
     */
    public MarkupValue(String html) {
        this.html = html;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return html;
    }

    /**
     * @version 2014/07/31 9:30:53
     */
    @SuppressWarnings("unused")
    private static class Codec implements kiss.Codec<MarkupValue> {

        /**
         * {@inheritDoc}
         */
        @Override
        public MarkupValue decode(String value) {
            return new MarkupValue(value.replaceAll("\\\"", "\""));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String encode(MarkupValue value) {
            return value.html.replaceAll("\"", "\\\"");
        }
    }
}