/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.doc.site;

import antibug.doc.ClassInfo;
import antibug.doc.Javadoc;

public final class TypeHTML extends BaseHTML {

    /**
     * @param info
     */
    public TypeHTML(Javadoc javadoc, ClassInfo info) {
        super(javadoc, info);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void main() {
        $(new MainArea(info));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void aside() {
        $(new RightNavigationArea(info));
    }
}