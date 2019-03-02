/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
public class Project extends bee.api.Project {

    String JunitVersion = "LATEST";

    {
        product("com.github.teletha", "antibug", "0.7");
        producer("Nameless Production Committee");
        describe("Bug Detection Tools. (including PowerAssert");

        require("org.junit.jupiter", "junit-jupiter-api", JunitVersion);
        require("org.junit.jupiter", "junit-jupiter-engine", JunitVersion);
        require("org.junit.jupiter", "junit-jupiter-params", JunitVersion);
        require("net.bytebuddy", "byte-buddy");
        require("net.bytebuddy", "byte-buddy-agent");

        versionControlSystem("https://github.com/teletha/antiBug");
    }
}
