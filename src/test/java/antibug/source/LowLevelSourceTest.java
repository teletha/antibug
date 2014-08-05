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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import kiss.I;
import kiss.XML;

import org.junit.Test;
import org.xml.sax.SAXException;

import antibug.powerassert.PowerAssertOff;
import antibug.xml.XMLFormatter;

/**
 * @version 2014/08/01 22:59:27
 */
@PowerAssertOff
public class LowLevelSourceTest {

    @Test
    public void annotation() throws Exception {
        assertSourceAsText(AnnotationUse.class);
    }

    @Test
    public void annotationTypeUse() throws Exception {
        assertSourceAsText(AnnotationTypeUse.class);
    }

    @Test
    public void annotationWithDefaultValue() throws Exception {
        assertSourceAsText(Annotation.class);
    }

    @Test
    public void interfaceWithStaticAndDefaultMethod() throws Exception {
        assertSourceAsText(Interface.class);
    }

    @Test
    public void innerClass() throws Exception {
        assertSourceAsText(InnerClass.class);
    }

    @Test
    public void enumClass() throws Exception {
        assertSourceAsText(EnumClass.class);
    }

    @Test
    public void cast() throws Exception {
        assertSourceAsText(Cast.class);
    }

    @Test
    public void statement() throws Exception {
        assertSourceAsText(Statement.class);
    }

    @Test
    public void operator() throws Exception {
        assertSourceAsText(Operator.class);
    }

    @Test
    public void typeParameter() throws Exception {
        assertSourceAsText(TypeParameter.class);
    }

    @Test
    public void tryCatchFinally() throws Exception {
        assertSourceAsText(Try.class);
    }

    @Test
    public void constructor() throws Exception {
        XML xml = assertSourceAsText(Constructor.class);
        assert xml.find("reserved:contains(this)").size() == 1;
        assert xml.find("reserved:contains(super)").size() == 1;
    }

    @Test
    public void modifier() throws Exception {
        assertSourceAsText(Modifier.class);
    }

    @Test
    public void methodCall() throws Exception {
        assertSourceAsText(MethodCall.class);
    }

    @Test
    public void primitive() throws Exception {
        assertSourceAsText(Primitive.class);
    }

    @Test
    public void array() throws Exception {
        assertSourceAsText(Array.class);
    }

    @Test
    public void lambda() throws Exception {
        assertSourceAsText(Lambda.class);
    }

    @Test
    public void methodReference() throws Exception {
        assertSourceAsText(MethodReference.class);
    }

    @Test
    public void assertion() throws Exception {
        assertSourceAsText(Assert.class);
    }

    @Test
    public void classLiteral() throws Exception {
        XML xml = assertSourceAsText(ClassLiteral.class);

        assert xml.find("reserved:contains(class)").size() == 2;
    }

    @Test
    public void sample() throws Exception {
        assertSourceAsText(LowLevelSourceTest.class);
    }

    /**
     * <p>
     * Assertion helper.
     * </p>
     * 
     * @param target A target class to test.
     */
    private XML assertSourceAsText(Class target) {
        String separator = System.getProperty("line.separator");

        try {
            Path source = I.locate("src/test/java").resolve(target.getName().replace(".", "/") + ".java");
            XML xml = SourceParser.parse(source);

            // convert xml to text
            List<String> lines = new ArrayList();

            for (XML line : xml.find("line")) {
                lines.add(line.text());
            }
            removeTailWhitespaceLine(lines);

            // diff
            List<String> originals = Files.readAllLines(source);

            // check size
            int size = lines.size();

            if (originals.size() != size) {
                StringBuilder message = new StringBuilder();
                message.append("Line ").append(size).append(" is missing.").append(separator);
                message.append("【").append(originals.get(size)).append("】");

                throw new AssertionError(message.toString());
            }

            for (int i = 0; i < size; i++) {
                String line = lines.get(i);
                String original = originals.get(i);

                // remove debug code
                if (line.endsWith("☆")) {
                    line = line.substring(0, line.length() - 1);
                }

                // ignore whitespace only line
                if (isWhitespaceLine(line)) {
                    continue;
                }

                // remove comment at the end of a sentence
                // int index = original.indexOf(" //");
                //
                // if (index != -1) {
                // original = original.substring(0, index);
                // }

                // check equality
                if (!line.equals(original)) {
                    // build error message
                    StringBuilder message = new StringBuilder();
                    message.append("Wrong code at line ").append(i + 1).append(separator);
                    message.append("◯【").append(original).append("】").append(separator);
                    message.append("×【").append(line).append("】");
                    xml.find("line[n=\"" + (i + 1) + "\"]").to(new Formatter(message));

                    xml.to(new Formatter(System.out));
                    System.out.println(separator + separator + message);

                    // build error
                    Error cause = new Error(message.toString());
                    cause.setStackTrace(new StackTraceElement[] {new StackTraceElement(target.getName(), "$", target.getSimpleName() + ".java", i + 1)});

                    throw new AssertionError(cause);
                }
            }

            return xml;
        } catch (IOException e) {
            throw I.quiet(e);
        }
    }

    /**
     * <p>
     * Revemo tailing whitespace line.
     * </p>
     * 
     * @param lines
     */
    private void removeTailWhitespaceLine(List<String> lines) {
        for (int i = lines.size() - 1; 0 < i; i--) {
            if (isWhitespaceLine(lines.get(i))) {
                lines.remove(i);
            } else {
                break;
            }
        }
    }

    /**
     * @param line
     * @return
     */
    private boolean isWhitespaceLine(String line) {
        for (int i = 0; i < line.length(); i++) {
            if (!Character.isWhitespace(line.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * @version 2014/08/02 0:23:35
     */
    private static class Formatter extends XMLFormatter {

        /**
         * @param writer
         */
        private Formatter(Appendable out) {
            super(out);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void startDocument() throws SAXException {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected boolean asCharacter(String uri, String local) {
            return !local.equals("line") && !local.equals("source");
        }
    }
}
