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
 * @version 2014/07/30 11:50:47
 */
public class ExternalPackageInfo extends PackageInfo {

    /**
     * <p>
     * External package info.
     * </p>
     * 
     * @param id
     */
    public ExternalPackageInfo(Identifier id) {
        this.id = id;
    }
}
