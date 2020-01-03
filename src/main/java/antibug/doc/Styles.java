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

import stylist.AbstractStyleDSL;
import stylist.Style;
import stylist.value.Color;
import stylist.value.ColorPalette;
import stylist.value.Font;
import stylist.value.FontPalette;
import stylist.value.Numeric;

/**
 * 
 */
class Styles extends AbstractStyleDSL {

    private static final ColorPalette palette = ColorPalette.with.primary($.rgb(40, 165, 245))
            .secondary($.rgb(250, 210, 50))
            .accent($.rgb(221, 81, 76))
            .background($.rgb(241, 250, 238))
            .font($.rgb(94, 109, 130));

    private static final FontPalette fonts = FontPalette.with.base(new Font("Yu Gothic UI", "")).title(Font.fromGoogle("Oswald"));

    // color palette - https://coolors.co/e63946-f1faee-a8dadc-457b9d-1d3557

    Numeric FontSize = Numeric.of(13, px);

    Numeric HeaderHeight = Numeric.of(80, px);

    Numeric MaxWidth = Numeric.of(85, vw);

    Numeric LeftNaviWidth = Numeric.of(190, px);

    double LineHeight = 1.5;

    Style workbench = () -> {
        font.size(FontSize).family("Segoe UI", Font.SansSerif).color(palette.font);
        line.height(LineHeight);
        display.width(100, vw);
    };

    Style header = () -> {
        background.color(Color.White);
        position.sticky().top(0, rem);
        display.width(MaxWidth).height(HeaderHeight).zIndex(10);
        margin.auto();
        border.bottom.color(palette.primary).width(1, px).solid();
    };

    Style productTitle = () -> {
        font.size(1.5, rem).family(fonts.title).weight.normal().color(palette.primary);
    };

    Style main = () -> {
        display.width(MaxWidth).flex().direction.row();
        margin.auto();
    };

    Style article = () -> {
        flexItem.grow(1);
    };

    Style type = Style.named(".type", () -> {
        cursor.pointer();
        font.color(palette.font);
    });

    Style nav = () -> {
        display.flex().direction.column();
        flexItem.basis(LeftNaviWidth).shrink(0);

        $.select(type, () -> {
            display.block();
            text.decoration.none();
        });
    };

    Style contents = () -> {
        flexItem.basis(640, px).shrink(1);
    };

    Style toc = () -> {
        flexItem.basis(240, px).shrink(0);
    };

    Style selector = () -> {
        display.block();
    };

    Style parameters = Style.named(".parameters", () -> {

        $.before(() -> {
            content.text("<");
        });
        $.after(() -> {
            content.text(">");
        });
    });

    Style lowerBound = Style.named(".extends", () -> {

        $.before(() -> {
            content.text(" extends ");
        });
    });

    Style upperBound = Style.named(".super", () -> {

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
        font.family(fonts.title, Font.SansSerif).size(HeadSize).weight.normal();
        margin.top(HeadTopGap).bottom(HeadBottomGap);
        display.block();
    };

    Style section = Style.named("section", () -> {
        margin.bottom(BlockVerticalGap);

        $.select("> p", () -> {
            block(palette.primary, false);
        });

        $.select("> pre", () -> {
            block(palette.secondary, false);
        });

        $.select("> ul", () -> {
            block(palette.secondary, false);
        });

        $.select("> ol", () -> {
            block(palette.secondary, false);
        });
    });

    /**
     * Define block-like.
     * 
     * @param color
     */
    private void block(Color color, boolean paintBackground) {
        margin.vertical(BlockInterval).left(0, px);
        padding.vertical(BlockVerticalGap).horizontal(BlockHorizontalGap);
        border.left.width(BlockBorderWidth).solid().color(color);
        line.height(LineHeight);
        font.family(fonts.base, Font.SansSerif);
        if (paintBackground) background.color(color.opacify(-0.8d));
    }

    Style list = () -> {
        block(palette.secondary, true);
    };

    Numeric LeftGap = Numeric.of(20, px);

    Numeric BottomGap = Numeric.of(10, px);

    Numeric SmallGap = Numeric.of(2, px);

    Style dl = Style.named("dl", () -> {
        list.style();

        block(palette.accent, false);
        background.color(Color.Transparent);

        $.select("dt", () -> {
            display.block().width(70, px).floatLeft();
            font.size(9, px).weight.bold().color(palette.accent);
            padding.top(SmallGap.multiply(2));
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
        });
    });
}