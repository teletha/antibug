/*
 * Copyright (C) 2025 The ANTIBUG Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug.profiler;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The {@code Libraries} class is responsible for scanning and identifying libraries
 * from the classpath and the current project's target directory. It collects the libraries
 * and their respective versions, allowing version detection based on input queries.
 * <p>
 * The class scans JAR files, ignoring Javadoc and source JARs, and compares the versions
 * of the libraries based on their weights, retaining only the highest version for each library.
 */
class Libraries {

    /**
     * The {@code Version} record stores the version ID and a calculated weight
     * for version comparison.
     */
    private record Version(String id, long weight) {
    }

    /**
     * A map that holds the libraries found during the scan,
     * where the key is the library name and the value is the {@code Version}.
     */
    private final Map<String, Version> scanned = new HashMap();

    /**
     * Initializes the {@code Libraries} instance by scanning the system's classpath
     * and the current project's target directory for libraries (JAR files).
     * It skips Javadoc and source JAR files and calculates version weights to retain
     * the highest version of each library.
     */
    Libraries() {
        try {
            // scan classpath
            for (String path : System.getProperty("java.class.path").split(File.pathSeparator)) {
                scan(path);
            }

            // scan current project
            Files.newDirectoryStream(Path.of("target")).forEach(path -> {
                scan(path.toString());
            });
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    /**
     * Scans a given path for JAR files, extracts the library name and version,
     * and calculates the version's weight based on numerical segments in the version string.
     * <p>
     * If a version of the library is already present in the {@code scanned} map,
     * it compares the versions and keeps the higher version.
     *
     * @param path the path to scan for JAR files
     */
    private void scan(String path) {
        if (path.endsWith(".jar") && !path.contains("-javadoc") && !path.contains("-sources")) {
            path = path.substring(path.lastIndexOf(File.separator) + 1, path.length() - 4);

            int index = path.lastIndexOf("-");
            while (Character.isDigit(path.charAt(index - 1))) {
                int newIndex = path.lastIndexOf("-", index - 1);
                if (newIndex == -1) {
                    break;
                } else {
                    index = newIndex;
                }
            }

            String name = path.substring(0, index).toLowerCase();
            String version = path.substring(index + 1);

            // compare version
            long weight = 0;
            long multiplier = 10000000000000L;
            for (String num : version.split("\\D+")) {
                weight += Long.parseLong(num) * multiplier;
                multiplier /= 100;
            }

            Version previous = scanned.get(name);
            if (previous != null && previous.weight >= weight) {
                return;
            }
            scanned.put(name, new Version(version, weight));
        }
    }

    /**
     * Detects the version of a library based on an input string.
     * The input string is split into lowercase tokens, and each token
     * is compared to the library names in the {@code scanned} map.
     * If a match is found, the corresponding version is returned.
     *
     * @param input the input string to search for a library version
     * @return the version of the matching library, or an empty string if no match is found
     */
    String detect(String input) {
        for (Entry<String, Version> entry : scanned.entrySet()) {
            String names = entry.getKey();
            String version = entry.getValue().id;

            for (String in : input.toLowerCase().split("[ -]")) {
                if (names.contains(in)) {
                    return version;
                }
            }
        }
        return "";
    }
}