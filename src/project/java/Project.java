/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
public class Project extends bee.api.Project {

    String JunitVersion = "5.2.0";

    String BuddyVersion = "[1.8.20,)";

    {
        product("com.github.teletha", "antibug", "0.6");
        producer("Nameless Production Committee");
        describe("JUnit Extension Tools.");

        require("com.github.teletha", "sinobu", "1.0");
        require("com.github.teletha", "filer", "0.5");
        require("org.junit.jupiter", "junit-jupiter-api", JunitVersion);
        require("org.junit.jupiter", "junit-jupiter-engine", JunitVersion);
        require("org.junit.platform", "junit-platform-launcher", "1.2.0");
        require("net.bytebuddy", "byte-buddy", BuddyVersion);
        require("net.bytebuddy", "byte-buddy-agent", BuddyVersion);

        versionControlSystem("https://github.com/teletha/antiBug");
    }
}
