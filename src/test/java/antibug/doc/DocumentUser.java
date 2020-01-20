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

public class DocumentUser {

    public static void main(String[] args) throws IOException {
        Javadoc javadoc = new Javadoc();
        javadoc.productName = "Antibug";

        ModernDoclet.with.sources("src/main/java").output("docs/api").processor(javadoc).build();
    }
}
