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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class SVG {

    static void write(String fileName, Inspection[] visualize, List<MeasurableCode> results) {
        // resort by the first inspection item
        Collections.sort(results, Comparator.comparingDouble(o -> visualize[0].calculate(o) * (visualize[0].ascending ? 1 : -1)));

        int barHeight = 12 * visualize.length;
        int barHeightGap = 20;
        int width = 625;
        int height = (barHeight + barHeightGap) * results.size() + barHeightGap;

        StringBuilder svg = new StringBuilder();
        try {
            svg.append("""
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 625 %d">
                      <style>
                          text {
                              font-family: Arial;
                              font-size: 13px;
                              fill: #787878;
                          }

                          .desc {
                              font-size: 9px;
                          }

                          .vline {
                              fill: #acacac;
                              stroke: none;
                              stroke-width: 0;
                          }

                          .subline {
                              fill: #bbb;
                              stroke: none;
                              stroke-width: 0;
                          }

                          .bar {
                              stroke-linejoin: round;
                              height: 12px;
                          }
                      </style>
                      <rect x="175" y="0" width="1" height="%d" class="vline"/>
                      <rect x="275" y="0" width="1" height="%d" class="vline"/>
                      <rect x="375" y="0" width="1" height="%d" class="vline"/>
                      <rect x="475" y="0" width="1" height="%d" class="vline"/>
                      <rect x="575" y="0" width="1" height="%d" class="vline"/>
                      <rect x="225" y="0" width="1" height="%d" class="subline"/>
                      <rect x="325" y="0" width="1" height="%d" class="subline"/>
                      <rect x="425" y="0" width="1" height="%d" class="subline"/>
                      <rect x="525" y="0" width="1" height="%d" class="subline"/>
                      <rect x="175" y="%d" width="450" height="1" class="subline"/>

                     """.formatted(height + 50, height, height, height, height, height, height, height, height, height, height));

            int name = 175;
            int margin = 30;
            int itemWidth = (width - name - margin * 2) / visualize.length;
            for (int i = 0; i < visualize.length; i++) {
                Inspection item = visualize[i];
                int x = name + margin + i * itemWidth;
                svg.append("""
                          <rect x="%d" y="%d" width="30" fill="%s" class="bar"/>
                          <text x="%d" y="%d">%s</text>
                        """.formatted(x, height + 8, item.color, x + 40, height + barHeightGap, item.label));
            }

            for (int i = 0; i < results.size(); i++) {
                MeasurableCode code = results.get(i);
                int y = (barHeight + barHeightGap) * i + barHeightGap;

                svg.append("""
                          <text x="160" y="%d" text-anchor="end">%s</text>
                          <text x="160" y="%d" text-anchor="end" class="desc">%s</text>
                        """.formatted(y + 17, code.name, y + 27, code.version));

                for (int j = 0; j < visualize.length; j++) {
                    Inspection item = visualize[j];
                    long value = Math.round(item.calculate(code));
                    double max = results.stream().mapToDouble(item::calculate).max().getAsDouble();
                    double barWidth = 350 * item.barRatio / max * value;

                    svg.append("""
                              <rect x="%d" y="%d" width="%.2f" rx="2" ry="2" fill="%s" class="bar"/>
                              <text x="%.2f" y="%d" class="desc">%d</text>
                            """.formatted(name, y + j * 12, barWidth, item.color, name + barWidth + 7, y + 10 + j * 12, value));
                }
            }

            Runtime runtime = Runtime.getRuntime();
            int infoY = height + barHeightGap * 2;
            svg.append("""
                      <text x="175" y="%d" class="desc">Java: %s</text>
                      <text x="245" y="%d" class="desc">Memory: %sMB</text>
                      <text x="345" y="%d" class="desc">CPU: %s</text>
                    """.formatted(infoY, Runtime.version().feature(), infoY, runtime.maxMemory() / 1024 / 1024, infoY, Benchmark
                    .getCPUInfo()));
            svg.append("</svg>").append(System.lineSeparator());
        } catch (Throwable e) {
            e.printStackTrace(Benchmark.origina);
        }

        try {
            Path file = Path.of("benchmark/" + fileName + ".svg");
            Files.createDirectories(file.getParent());
            Files.writeString(file, svg, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new Error(e);
        }
    }

}