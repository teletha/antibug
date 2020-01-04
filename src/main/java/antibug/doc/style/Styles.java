/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.doc.style;

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
public class Styles extends AbstractStyleDSL {

    private final ColorPalette palette = ColorPalette.with.primary($.rgb(40, 165, 245))
            .secondary($.rgb(250, 210, 50))
            .accent($.rgb(221, 81, 76))
            .background($.rgb(241, 250, 238))
            .font($.rgb(94, 109, 130));

    private final FontPalette fonts = FontPalette.with.base(new Font("Yu Gothic UI", "")).title(Font.fromGoogle("Oswald"));

    // color palette - https://coolors.co/e63946-f1faee-a8dadc-457b9d-1d3557

    private final Numeric FontSize = Numeric.of(13, px);

    private final Numeric MaxWidth = Numeric.of(85, vw);

    private final double LineHeight = 1.5;

    // =====================================================
    // HTML Elements
    // =====================================================
    private final Numeric SmallGap = Numeric.of(1, px);

    private Numeric BlockVerticalGap = Numeric.of(0.5, rem);

    private Numeric BlockBorderWidth = Numeric.of(3, px);

    private Numeric BlockHorizontalGap = Numeric.of(10, px);

    @SuppressWarnings("unused")
    private Style HTMLAnchor = Style.named("a", () -> {
        font.color(palette.font);
        text.decoration.none();

        $.hover(() -> {
            text.decoration.underline();
            text.decorationColor.color(palette.font.opacify(-0.5));
            text.underlineOffset.length(2, px);
            text.underlinePosition.under();
        });
    });

    @SuppressWarnings("unused")
    private Style HTMLSection = Style.named("section", () -> {
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

    @SuppressWarnings("unused")
    private Style HTMLDifinitionList = Style.named("dl", () -> {
        block(palette.accent, false);
        background.color(Color.Transparent);

        $.select("dt", () -> {
            display.block().width(70, px).floatLeft();
            font.size(9, px).weight.bold().color(palette.accent);
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
        });
    });

    /**
     * Define block-like.
     * 
     * @param color
     */
    private void block(Color color, boolean paintBackground) {
        margin.left(0, px);
        padding.vertical(BlockVerticalGap).horizontal(BlockHorizontalGap);
        border.left.width(BlockBorderWidth).solid().color(color);
        line.height(LineHeight);
        font.family(fonts.base, Font.SansSerif);
        if (paintBackground) background.color(color.opacify(-0.8d));
    }

    private final Style HTMLClassType = Style.named(".type", () -> {
        cursor.pointer();
        font.color(palette.font);
    });

    @SuppressWarnings("unused")
    private final Style HTMLClassParameters = Style.named(".parameters", () -> {

        $.before(() -> {
            content.text("<");
        });
        $.after(() -> {
            content.text(">");
        });
    });

    @SuppressWarnings("unused")
    private final Style HTMLClassExtends = Style.named(".extends", () -> {

        $.before(() -> {
            content.text(" extends ");
        });
    });

    @SuppressWarnings("unused")
    private final Style HTMLClassSuper = Style.named(".super", () -> {

        $.before(() -> {
            content.text(" super ");
        });
    });

    public final Style workbench = () -> {
        font.size(FontSize).family("Segoe UI", Font.SansSerif).color(palette.font);
        line.height(LineHeight);
        display.width(100, vw);
    };

    public final Style productTitle = () -> {
        font.size(1.5, rem).family(fonts.title).weight.normal().color(palette.primary);
    };

    private final Numeric HeaderHeight = Numeric.of(80, px);

    private final Numeric NavigationWidth = Numeric.of(15, vw);

    /** Header Area */
    public final Style HeaderArea = () -> {
        background.color(Color.White);
        position.sticky().top(0, rem);
        display.width(MaxWidth).height(HeaderHeight).zIndex(10);
        margin.auto();
        border.bottom.color(palette.primary).width(1, px).solid();
    };

    public final Style MainArea = Style.named("main", () -> {
        display.width(MaxWidth).flex().direction.row();
        margin.auto();
    });

    /** Left Side Navigation */
    public final Style TypeNavigation = () -> {
        flexItem.basis(NavigationWidth).shrink(0);

        $.child(() -> {
            position.sticky().top(HeaderHeight);

            $.child(() -> {
                margin.top(BlockVerticalGap);
            });
        });

        $.select(HTMLClassType, () -> {
            display.block().height(1.5, rem);
            text.whiteSpace.nowrap();
        });

        $.select(".el-select", () -> {
            display.width(100, percent);
        });

        $.select(".el-checkbox", () -> {
            display.block();
        });

        $.select("#AllTypes", () -> {
            overflow.hidden().scrollbar.thin();
            display.height(60, vh);

            $.hover(() -> {
                overflow.y.auto();
            });
        });
    };

    /** Main Contents */
    public final Style contents = () -> {
        flexItem.grow(1);
        margin.left(5, rem).right(1.5, rem);
    };

    /** Right Side Navigation */
    public final Style navigationRight = () -> {
        flexItem.basis(NavigationWidth).shrink(0);

        $.child(() -> {
            position.sticky().top(HeaderHeight);
        });
    };

    public final Style heading = () -> {
        font.family(fonts.title, Font.SansSerif).size(18, px).weight.normal();
        margin.top(BlockVerticalGap.multiply(4)).bottom(BlockVerticalGap.multiply(2));
        display.block();
    };
}