/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import filer.Filer;
import kiss.I;
import kiss.WiseRunnable;

/**
 * <p>
 * Antibug is highly testable utility, which can manipulate tester objects as a extremely-condensed
 * facade.
 * </p>
 * 
 * @version 2012/02/16 15:56:29
 */
public class AntiBug {

    /**
     * <p>
     * Create temporary file with contents.
     * </p>
     * 
     * @param contents A text contents.
     * @return A created temporary file.
     */
    public static final Path memo(String... contents) {
        try {
            StringBuilder builder = new StringBuilder();
            for (String content : contents) {
                builder.append(content);
            }

            Path temporary = Filer.locateTemporary();
            Files.createFile(temporary);
            Files.write(temporary, builder.toString().getBytes(StandardCharsets.UTF_8));

            assert Files.exists(temporary);
            assert Files.size(temporary) != 0;

            return temporary;
        } catch (IOException e) {
            throw I.quiet(e);
        }
    }

    /**
     * Helper method to catch error.
     * 
     * @param code
     * @return
     */
    public static final Throwable willCatch(WiseRunnable code) {
        try {
            code.run();
            return null;
        } catch (Throwable e) {
            return e;
        }
    }
}
