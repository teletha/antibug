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

    Font BodyFont = new Font("Yu Gothic UI", "");

    Font HeadFont = Font.fromGoogle("Oswald");

    Numeric FontSize = Numeric.of(14, px);

    Numeric HeaderHeight = Numeric.of(80, px);

    Numeric MaxWidth = Numeric.of(1200, px);

    Numeric LeftNaviWidth = Numeric.of(240, px);

    double LineHeight = 1.5;

    Style workbench = () -> {
        font.size(FontSize).family("Segoe UI", Font.SansSerif).color(FontColor);
        line.height(LineHeight);
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

    Numeric BlockBorderWidth = Numeric.of(3, px);

    Numeric BlockVerticalGap = Numeric.of(6, px);

    Numeric BlockHorizontalGap = Numeric.of(10, px);

    Numeric BlockInterval = Numeric.of(2, px);

    Numeric HeadSize = Numeric.of(18, px);

    Numeric HeadTopGap = BlockVerticalGap.multiply(4);

    Numeric HeadBottomGap = BlockVerticalGap.multiply(2);

    Style heading = () -> {
        font.family(HeadFont, Font.SansSerif).size(HeadSize).weight.normal();
        margin.top(HeadTopGap).bottom(HeadBottomGap);
        display.block();
    };

    Style section = () -> {
        margin.bottom(BlockVerticalGap);
    };

    /**
     * Define block-like.
     * 
     * @param color
     */
    private static void block(Color color, boolean paintBackground) {
        margin.vertical(BlockInterval).left(0, px);
        padding.vertical(BlockVerticalGap).horizontal(BlockHorizontalGap);
        border.left.width(BlockBorderWidth).solid().color(color);
        line.height(LineHeight);
        font.family(BodyFont, Font.SansSerif);
        if (paintBackground) background.color(color.opacify(-0.8d));
    }

    Style list = () -> {
        block(ListColor, true);
    };

    Numeric LeftGap = Numeric.of(20, px);

    Numeric BottomGap = Numeric.of(10, px);

    Numeric SmallGap = Numeric.of(2, px);

    Style dl = () -> {
        list.style();

        block(SignatureColor, false);
        background.color(Color.Transparent);
        margin.top(BlockInterval.add(HeadSize).add(HeadBottomGap).add(HeadTopGap));
        position.relative();

        $.before(() -> {
            display.block();
            font.family(HeadFont, Font.SansSerif).size(HeadSize).weight.normal();
            content.text("Signature");
            position.absolute().top(BlockVerticalGap.add(HeadSize).add(HeadBottomGap).negate()).left(BlockBorderWidth.negate());
        });

        $.select("dt", () -> {
            display.block().width(70, px).floatLeft();
            font.size(9, px).weight.bold().color(SignatureColor);
            padding.top(SmallGap);
            border.bottom.none();
            text.transform.capitalize();
        });

        $.select("dd", () -> {
            margin.bottom(15, px).left(70, px);
        });

        $.select("b", () -> {
            display.block();
            border.bottom.width(1, px).solid().color(Color.WhiteGray);
            padding.bottom(SmallGap);
            margin.bottom(SmallGap);
            font.weight.bold();
            text.transform.capitalize();
        });
    };

    Style p = () -> {
        block(ParagraphColor, false);
    };
}