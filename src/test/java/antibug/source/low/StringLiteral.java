/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.source.low;

/**
 * @version 2014/08/05 10:51:27
 */
public class StringLiteral {

    String normal = "normal";

    String nonAscii = "(´・ω・`)";

    String escape = "\b\t\n\r\f\'\"\\";

    // @TODO how to test
    // String unicode = "\u3042\u3044\u3046\u3048\u304A";

    char c = 'c';

    char nonAsciiChar = '熊';

    char backspace = '\b';

    char tab = '\t';

    char nobreak = '\n';

    char carriageReturn = '\r';

    char lineFeed = '\f';

    char singleQuote = '\'';

    char doubleQuote = '\"';

    char escapeChar = '\\';
}
