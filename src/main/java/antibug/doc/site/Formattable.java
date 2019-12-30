/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.doc.site;

import java.io.IOException;

/**
 * This is an interface that shows that you can format and output beautifully.
 */
public interface Formattable {

    /** End of line code. */
    String EOL = "\r\n";

    /**
     * Write contents out to the specified output.
     * 
     * @param output
     * @param depth A depth of indent.
     * @param prevBlock TODO
     * @throws IOException
     */
    boolean format(Appendable output, int depth, boolean prevBlock) throws IOException;

    /**
     * Utility to write indent block.
     * 
     * @param depth
     * @return
     */
    default String indent(int depth) {
        return "\t".repeat(depth);
    }
}