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

import stylist.Style;
import stylist.StyleDSL;
import stylist.value.Color;
import stylist.value.Font;
import stylist.value.Numeric;

/**
 * 
 */
interface Styles extends StyleDSL {

    // color palette - https://coolors.co/e63946-f1faee-a8dadc-457b9d-1d3557
    Color BackColor = Color.rgb(241, 250, 238);

    Color SecondColor = Color.rgb(168, 218, 220);

    Color AccentColor = Color.rgb(69, 123, 157);

    Color DarkColor = Color.rgb(29, 53, 87);

    Color FontColor = Color.rgb(94, 109, 130);

    Color ParagraphColor = Color.rgb(40, 165, 245);

    Color ListColor = Color.rgb(250, 210, 50);

    Color SignatureColor = Color.rgb(221, 81, 76);

    Color CodeColor = Color.rgb(94, 185, 94);

    Font HeadFont = Font.fromGoogle("Oswald");

    Numeric FontSize = Numeric.of(14, px);

    Numeric HeaderHeight = Numeric.of(80, px);

    Numeric MaxWidth = Numeric.of(1200, px);

    Numeric LeftNaviWidth = Numeric.of(240, px);

    Style workbench = () -> {
        font.size(FontSize).family("Segoe UI", Font.SansSerif).color(FontColor);
        line.height(1.6);
        display.maxWidth(MaxWidth);
        margin.auto();
    };

    Style header = () -> {
        background.color(Color.White);
        position.sticky().top(0, rem);
        display.maxWidth(MaxWidth).height(HeaderHeight).zIndex(10);
        margin.auto();
        border.bottom.color(ParagraphColor).width(1, px).solid();
    };

    Style productTitle = () -> {
        font.size(1.5, rem).family(HeadFont).weight.normal().color(ParagraphColor);
    };

    Style main = () -> {
        display.maxWidth(MaxWidth).flex().direction.row();
        margin.auto();
    };

    Style article = () -> {
        flexItem.grow(3);
        margin.auto();
        padding.horizontal(20, px);
    };

    Style type = Style.named("type", () -> {
        cursor.pointer();
        font.color(FontColor);
    });

    Style nav = () -> {
        display.width(LeftNaviWidth).flex().direction.column();

        $.select(type, () -> {
            display.block();
            text.decoration.none();
        });
    };

    Style selector = () -> {
        display.block();
    };

    Style parameters = Style.named("parameters", () -> {

        $.before(() -> {
            content.text("<");
        });
        $.after(() -> {
            content.text(">");
        });
    });

    Style lowerBound = Style.named("extends", () -> {

        $.before(() -> {
            content.text(" extends ");
        });
    });

    Style upperBound = Style.named("super", () -> {

        $.before(() -> {
            content.text(" super ");
        });
    });
}