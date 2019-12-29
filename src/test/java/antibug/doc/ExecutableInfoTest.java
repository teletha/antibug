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

public class ExecutableInfoTest extends JavadocTestSupport {

    @Test
    void parameter0() {
        ExecutableInfo info = method("noparam");
        assert info.name.equals("noparam");
        assert info.params.size() == 0;
    }

    public void noparam() {
    }

    @Test
    void parameter1() {
        ExecutableInfo info = method("param1");
        assert info.name.equals("param1");
        assert info.params.size() == 1;
        assert info.params.get(0).ⅰ.equals("value");
        assert sameXML(info.params.get(0).ⅱ, "<");
    }

    public void param1(int value) {
    }
}
