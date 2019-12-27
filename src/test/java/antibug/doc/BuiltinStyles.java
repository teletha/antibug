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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import stylist.Style;
import stylist.StyleDSL;
import stylist.Stylist;
import stylist.value.Color;
import stylist.value.Font;
import stylist.value.Numeric;

public interface BuiltinStyles extends StyleDSL {

    Font MaterialIcon = new Font("MaterialIcon", "https://fonts.googleapis.com/icon?family=Material+Icons");

    Font Oswald = new Font("Oswald", "https://fonts.googleapis.com/css?family=Oswald:300");

    Numeric BlockBorderWidth = new Numeric(3, px);

    Style all = () -> {
        margin.size(0, px);
        padding.size(0, px);
        border.width(0, px);
        font.size(12, px).family("Yu Gothic UI", Font.SansSerif);
        line.height(1.35);
    };

    Style body = () -> {
        padding.vertical(5, px).horizontal(10, px);
        background.color(Color.White);
    };

    Style p = () -> {
        $.not($.attr("class").exist(), () -> {
            padding.vertical(6, px).horizontal(10, px);
        });
    };

    Color ColorList = Color.rgb(250, 210, 50);

    Style list = () -> {
        margin.vertical(2, px);
        padding.vertical(6, px).horizontal(20, px);
        border.left.width(3, px).solid().color(ColorList);
        background.color(ColorList.opacify(-0.9d));
    };

    Numeric HeadSize = new Numeric(18, px);

    Color SignatureColor = Color.of("#dd514c");

    Style dl = () -> {
        $.not($.attr("class").exist(), list);

        $.lastChild(() -> {
            margin.vertical(58, px);
            padding.top(12, px).bottom(5, px).left(10, px);
            border.left.width(BlockBorderWidth).solid().color(SignatureColor);
            background.color(Color.Transparent);
            position.relative();
        });

        $.lastChild().before(() -> {
            font.size(HeadSize).family(Oswald, Font.SansSerif);
            display.block();
            content.text("Signature");
            position.absolute().top(-36, px).left(-3, px);
        });

        $.lastChild().select("dt", () -> {
            display.block().width(70, px);
            font.size(9, px).weight.bold().color(SignatureColor);
            padding.top(2, px);
            border.bottom.none();
        });
        $.lastChild().select("dd", () -> {
            margin.bottom(15, px).left(70, px);
        });

        $.lastChild().select("b", () -> {
            display.block();
            border.bottom.width(1, px).solid().color(Color.WhiteGray);
            padding.bottom(3, px);
            margin.bottom(3, px);
            font.weight.bold();
        });
    };

    Style ul = () -> {
        $.not($.attr("class").exist(), list);
        $.not($.attr("class").exist()).select("li", () -> {
            listStyle.none();
            padding.vertical(6, px);
        });
        $.not($.attr("class").exist()).select("li").before(() -> {
            font.family(MaterialIcon).size(11, px).color(ColorList.saturate(-20));
            content.text("\\e876");
            margin.right(-10, px).left(4, px);
            display.inlineBlock();
            text.verticalAlign.bottom();
        });
    };

    Style ol = () -> {
        $.not($.attr("class").exist(), list);
    };

    Style dt = () -> {
        $.not($.attr("class").exist(), () -> {
            border.bottom.width(1, px).solid().color("#ddd");
            padding.bottom(3, px);
            margin.bottom(3, px);
        });
    };

    Style dd = () -> {
        $.not($.attr("class").exist(), () -> {
            margin.bottom(20, px);
        });
    };

    Style pre = () -> {
        $.not($.attr("class").exist(), list);
    };

    Style a = () -> {
        $.not($.attr("class").exist(), () -> {
            text.decoration.none();
        });
    };

    Style heading = () -> {
        font.size(HeadSize);
        font.family(Oswald, Font.SansSerif);
        padding.top(20, px).bottom(10, px);
        display.block();
    };

    Style h = () -> {
        heading.style();
    };

    Style h1 = () -> {
        heading.style();
    };

    Style h2 = () -> {
        heading.style();
    };

    Style h3 = () -> {
        heading.style();
    };

    Style h4 = () -> {
        heading.style();
    };

    Style h5 = () -> {
        heading.style();
    };

    public static void main(String[] args) throws IOException {
        String formatted = Stylist.pretty().format(BuiltinStyles.class);
        formatted = formatted.replaceAll(".+#([^\\s\\*]+) \\*/\\.[a-zA-Z]+", "$1").replaceFirst("all", "*");
        System.out.println(formatted);

        Files.writeString(Path.of("docs/javadoc.css"), formatted, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
    }
}
