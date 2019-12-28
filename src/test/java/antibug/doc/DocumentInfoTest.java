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

import org.junit.jupiter.api.Test;

public class DocumentInfoTest extends JavadocTestSupport {

    /**
     * Text
     */
    @Test
    void text() {
        MethodInfo info = currentMethod();
        assert sameXML(info.comment, "<span>Text</span>");
    }

    /**
     * <p>
     * Text
     * </p>
     */
    @Test
    void markuped() {
        MethodInfo info = currentMethod();
        assert sameXML(info.comment, "<p>Text</p>");
    }

    /**
     * {@literal 0 < i}
     */
    @Test
    void literalTag() {
        MethodInfo info = currentMethod();
        assert sameXML(info.comment, "<span>0 &lt; i</span>");
    }
}
