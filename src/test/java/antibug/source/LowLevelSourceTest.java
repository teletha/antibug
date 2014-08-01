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
    public void testname() throws Exception {
        assertSourceAsText(Sample.class);
    }

    /**
     * <p>
     * Assertion helper.
     * </p>
     * 
     * @param target A target class to test.
     */
    private void assertSourceAsText(Class target) {
        try {
            Path source = I.locate("src/test/java").resolve(target.getName().replace(".", "/") + ".java");
            XML xml = SourceParser.parse(source);

            // convert xml to text
            List<String> lines = new ArrayList();

            for (XML line : xml.find("line")) {
                lines.add(line.text());
            }

            // diff
            List<String> originals = Files.readAllLines(source);

            for (int i = 0; i < lines.size(); i++) {
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
                int index = original.indexOf(" //");

                if (index != -1) {
                    original = original.substring(0, index);
                }

                // check equality
                if (!line.equals(original)) {
                    String separator = System.getProperty("line.separator");

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
        } catch (IOException e) {
            throw I.quiet(e);
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
            // ignore xml declaration
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
