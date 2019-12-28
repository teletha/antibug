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

import kiss.XML;
import kiss.Ⅱ;

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
    void element() {
        MethodInfo info = currentMethod();
        assert sameXML(info.comment, "<p>Text</p>");
    }

    /**
     * <link type="stylesheet" href="test.css"/>
     */
    @Test
    void attribute() {
        MethodInfo info = currentMethod();
        assert sameXML(info.comment, "<link href=\"test.css\" type=\"stylesheet\"/>");
    }

    /**
     * @param name1 Description.
     * @param name2 This is <em>NOT</em> error.
     */
    @Test
    void paramTag() {
        MethodInfo info = currentMethod();
        Ⅱ<String, XML> param = info.paramTags.get(0);
        assert param.ⅰ.equals("name1");
        assert sameXML(param.ⅱ, "<span>Description.</span>");

        param = info.paramTags.get(1);
        assert param.ⅰ.equals("name2");
        assert sameXML(param.ⅱ, "<span>This is <em>NOT</em> error.</span>");
    }

    /**
     * @param <T> Description.
     * @param <NEXT> This is <em>NOT</em> error.
     */
    @Test
    void parameterTypeTag() {
        MethodInfo info = currentMethod();
        Ⅱ<String, XML> param = info.typeParameterTags.get(0);
        assert param.ⅰ.equals("T");
        assert sameXML(param.ⅱ, "<span>Description.</span>");

        param = info.typeParameterTags.get(1);
        assert param.ⅰ.equals("NEXT");
        assert sameXML(param.ⅱ, "<span>This is <em>NOT</em> error.</span>");
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
