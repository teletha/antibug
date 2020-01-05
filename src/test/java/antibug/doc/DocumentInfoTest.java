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
        ExecutableInfo info = currentMethod();
        assert sameXML(info.comment, "<section>Text</section>");
    }

    /**
     * <p>
     * Text
     * </p>
     */
    @Test
    void element() {
        ExecutableInfo info = currentMethod();
        assert sameXML(info.comment, "<section><p>Text</p></section>");
    }

    /**
     * <a type="stylesheet" href="test.css"/>
     */
    @Test
    void attribute() {
        ExecutableInfo info = currentMethod();
        assert sameXML(info.comment, "<section><a href=\"test.css\" type=\"stylesheet\"/></section>");
    }

    /**
     * @param name1 Description.
     * @param name2 This is <em>NOT</em> error.
     */
    @Test
    void paramTag() {
        ExecutableInfo info = currentMethod();
        Ⅱ<String, XML> param = info.paramTags.get(0);
        assert param.ⅰ.equals("name1");
        assert sameXML(param.ⅱ, "<section>Description.</section>");

        param = info.paramTags.get(1);
        assert param.ⅰ.equals("name2");
        assert sameXML(param.ⅱ, "<section>This is <em>NOT</em> error.</section>");
    }

    /**
     * @param <T> Description.
     * @param <NEXT> This is <em>NOT</em> error.
     */
    @Test
    void parameterTypeTag() {
        ExecutableInfo info = currentMethod();
        Ⅱ<String, XML> param = info.typeParameterTags.get(0);
        assert param.ⅰ.equals("T");
        assert sameXML(param.ⅱ, "<section>Description.</section>");

        param = info.typeParameterTags.get(1);
        assert param.ⅰ.equals("NEXT");
        assert sameXML(param.ⅱ, "<section>This is <em>NOT</em> error.</section>");
    }

    /**
     * @return description
     */
    @Test
    void returnTag() {
        ExecutableInfo info = currentMethod();
        XML description = info.returnTag.exact();
        assert sameXML(description, "<section>description</section>");
    }

    /**
     * {@literal 0 < i}
     */
    @Test
    void literalTag() {
        ExecutableInfo info = currentMethod();
        assert sameXML(info.comment, "<section>0 &amp;lt; i</section>");
    }

    /**
     * @see Text
     */
    @Test
    void seeTagText() {
        ExecutableInfo info = currentMethod();
        assert sameXML(info.seeTags.get(0), "<section>Text</section>");
    }
}
