/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.javadoc.info;

/**
 * @version 2014/07/30 11:59:17
 */
public class ExternalTypeInfo extends TypeInfo {

    /**
     * @param id
     */
    public ExternalTypeInfo(Identifier id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return id.toString();
    }
}
