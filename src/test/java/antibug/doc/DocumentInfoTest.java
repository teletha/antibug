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
    public void text() {
        ExecutableInfo info = currentMethod();
        assert sameXML(info.comment, "<section>Text</section>");
    }

    /**
     * <p>
     * Text
     * </p>
     */
    @Test
    public void element() {
        ExecutableInfo info = currentMethod();
        assert sameXML(info.comment, "<section><p>Text</p></section>");
    }

    /**
     * <a type="stylesheet" href="test.css"/>
     */
    @Test
    public void attribute() {
        ExecutableInfo info = currentMethod();
        assert sameXML(info.comment, "<section><a href=\"test.css\" type=\"stylesheet\"/></section>");
    }

    /**
     * @param name1 Description.
     * @param name2 This is <em>NOT</em> error.
     */
    @Test
    public void paramTag() {
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
    public void parameterTypeTag() {
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
    public void returnTag() {
        ExecutableInfo info = currentMethod();
        XML description = info.returnTag.exact();
        assert sameXML(description, "<section>description</section>");
    }

    /**
     * {@literal 0 < i}
     */
    @Test
    public void literalTag() {
        ExecutableInfo info = currentMethod();
        assert sameXML(info.comment, "<section>0 &amp;lt; i</section>");
    }

    /**
     * {@link DocumentInfoTest}
     */
    @Test
    public void linkTagInternalType() {
        ExecutableInfo info = currentMethod();
        assert sameXML(info.comment, "<section><a href='/types/antibug.doc.DocumentInfoTest.html'>DocumentInfoTest</a></section>");
    }

    /**
     * {@link #linkTagInternalMethod()}
     */
    @Test
    public void linkTagInternalMethod() {
        ExecutableInfo info = currentMethod();
        assert sameXML(info.comment, "<section><a href='/types/antibug.doc.DocumentInfoTest.html#linkTagInternalMethod()'>#linkTagInternalMethod()</a></section>");
    }

    /**
     * {@link DocumentInfoTest#linkTagInternalTypeAndMethod()}
     */
    @Test
    public void linkTagInternalTypeAndMethod() {
        ExecutableInfo info = currentMethod();
        assert sameXML(info.comment, "<section><a href='/types/antibug.doc.DocumentInfoTest.html#linkTagInternalTypeAndMethod()'>DocumentInfoTest#linkTagInternalTypeAndMethod()</a></section>");
    }

    /**
     * {@link String}
     */
    @Test
    public void linkTagUnregisteredExternalType() {
        ExecutableInfo info = currentMethod();
        assert sameXML(info.comment, "<section>String</section>");
    }

    /**
     * {@link String#chars()}
     */
    @Test
    public void linkTagUnregisteredExternalTypeAndMethod() {
        ExecutableInfo info = currentMethod();
        assert sameXML(info.comment, "<section>String#chars()</section>");
    }

    /**
     * @see Text
     */
    @Test
    public void seeTagText() {
        ExecutableInfo info = currentMethod();
        assert sameXML(info.seeTags.get(0), "<section>Text</section>");
    }
}
