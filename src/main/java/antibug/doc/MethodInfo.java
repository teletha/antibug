/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.doc;

import javax.lang.model.element.ExecutableElement;

public class MethodInfo extends DocumentInfo {

    public final String name;

    /**
     * @param e
     */
    MethodInfo(ExecutableElement e) {
        super(e);

        this.name = e.getSimpleName().toString();
    }

}
