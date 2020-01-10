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

    {
        product("com.github.teletha", "antibug", "0.7");
        producer("Nameless Production Committee");
        describe("Bug Detection Tool Set. (including PowerAssert");

        require("org.junit.jupiter", "junit-jupiter-api", "5.5.2");
        require("org.junit.jupiter", "junit-jupiter-engine", "5.5.2");
        require("org.junit.jupiter", "junit-jupiter-params", "5.5.2");
        require("org.junit.platform", "junit-platform-launcher", "1.5.2");
        require("net.bytebuddy", "byte-buddy");
        require("net.bytebuddy", "byte-buddy-agent");
        require("com.github.teletha", "sinobu");
        require("com.github.teletha", "stylist");
        require("com.github.teletha", "psychopath");

        versionControlSystem("https://github.com/teletha/antibug");
    }
}
