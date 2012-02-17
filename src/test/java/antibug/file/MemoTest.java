/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.file;

import java.nio.file.Files;

import org.junit.Test;

/**
 * @version 2012/02/17 16:04:03
 */
public class MemoTest {

    @Test
    public void testname() throws Exception {
        Memo memo = new Memo("memo");
        System.out.println(new String(Files.readAllBytes(memo)));

        Files.write(memo, "write".getBytes());
        System.out.println(new String(Files.readAllBytes(memo)));
        System.out.println(memo);
    }
}
