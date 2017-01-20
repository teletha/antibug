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
        product("npc", "antibug", "0.3");
        producer("Nameless Production Committee");
        describe("JUnit Extension Tools.");

        require("npc", "sinobu", "1.0");
        require("junit", "junit", "4.12");
        require("sun.jdk", "tools", "8.0");

        versionControlSystem("https://github.com/Teletha/AntiBug");
    }
}
