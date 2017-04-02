/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
public class Project extends bee.api.Project {

    {
        product("com.github.teletha", "antibug", "0.3");
        producer("Nameless Production Committee");
        describe("JUnit Extension Tools.");

        require("org.ow2.asm", "asm", "5.2");
        require("com.github.teletha", "sinobu", "1.0");
        require("junit", "junit", "4.12");
        requireJavaTools();

        versionControlSystem("https://github.com/teletha/antiBug");
    }
}
