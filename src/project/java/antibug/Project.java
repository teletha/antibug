/*
 * Copyright (C) 2021 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug;

import static bee.api.License.*;

import javax.lang.model.SourceVersion;

public class Project extends bee.api.Project {

    String JunitVersion = "5.8.2";

    {
        product("com.github.teletha", "antibug", ref("version.txt"));
        license(MIT);
        describe("Bug Detection Tool Set. (including PowerAssert");

        require(SourceVersion.RELEASE_16);

        require("org.junit.jupiter", "junit-jupiter-api", JunitVersion);
        require("org.junit.jupiter", "junit-jupiter-engine", JunitVersion);
        require("org.junit.jupiter", "junit-jupiter-params", JunitVersion);
        require("org.junit.platform", "junit-platform-launcher");
        require("net.bytebuddy", "byte-buddy");
        require("net.bytebuddy", "byte-buddy-agent");

        versionControlSystem("https://github.com/teletha/antibug");
    }
}