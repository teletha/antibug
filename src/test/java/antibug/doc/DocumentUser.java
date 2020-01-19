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

import java.io.IOException;

import antibug.doc.AnotherDoclet.Builder;

public class DocumentUser {

    public static void main(String[] args) throws IOException {
        Builder builder = new AnotherDoclet.Builder();
        builder.sources.add("src/main/java");
        builder.output = "docs/api";
        builder.build();
    }
}
