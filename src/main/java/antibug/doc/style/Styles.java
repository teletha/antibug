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
    private Style HTMLLevel2 = Style.named("h2", () -> {
        font.size(1.8, rem).family(fonts.title);
    });

    @SuppressWarnings("unused")
    private Style HTMLItalic = Style.named("i", () -> {
        font.style.normal();
    });

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
        block2(palette.accent, true);

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

    /**
     * Define block-like.
     * 
     * @param color
     */
    private void block2(Color color, boolean paintBackground) {
        padding.vertical(BlockVerticalGap).horizontal(BlockHorizontalGap);
        border.left.width(BlockBorderWidth).solid().color(color);
        border.radius(2, px);
        line.height(LineHeight);
        font.family(fonts.base, Font.SansSerif);
        position.relative();
        if (paintBackground) background.color(color.opacify(-0.8d));

        $.before(() -> {
            position.absolute();
            content.text("Signature");
            font.color(color.opacify(-0.9d)).size(1.6, rem);
        });
    }

    @SuppressWarnings("unused")
    private final Style HTMLClassParameters = Style.named(".parameters", () -> {
        $.before(() -> {
            content.text("<");
        });
        $.after(() -> {
            content.text(">");
        });

        $.child().not($.lastChild(), () -> {
            $.after(() -> {
                content.text(", ");
            });
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

    @SuppressWarnings("unused")
    private final Style HTMLClassPackage = Style.named(".package", () -> {
        font.weight.bold();
    });

    @SuppressWarnings("unused")
    private final Style HTMLClassTypeInterface = Style.named(".Interface", () -> {
        buidlMark("\\e88f", Color.rgb(128, 88, 165));
    });

    @SuppressWarnings("unused")
    private final Style HTMLClassTypeFunctionalInterface = Style.named(".Functional", () -> {
        buidlMark("\\e88e", Color.rgb(128, 88, 165));
    });

    @SuppressWarnings("unused")
    private final Style HTMLClassTypeAbstractClass = Style.named(".AbstractClass", () -> {
        buidlMark("\\e90c", Color.of("#5eb95e").saturate(-30));
    });

    @SuppressWarnings("unused")
    private final Style HTMLClassTypeClass = Style.named(".Class", () -> {
        buidlMark("\\e90c", Color.of("#5eb95e"));
    });

    @SuppressWarnings("unused")
    private final Style HTMLClassTypeEnum = Style.named(".Enum", () -> {
        buidlMark("\\e01e", Color.of("#5eb95e").saturate(-35));
    });

    @SuppressWarnings("unused")
    private final Style HTMLClassTypeAnnotation = Style.named(".Annotation", () -> {
        buidlMark("\\e167", Color.of("#5eb95e").saturate(-35));
    });

    @SuppressWarnings("unused")
    private final Style HTMLClassTypeException = Style.named(".Exception", () -> {
        buidlMark("\\e031", Color.rgb(243, 123, 29));
    });

    @SuppressWarnings("unused")
    private final Style HTMLClassModifierPublic = Style.named(".PUBLIC", () -> {
        buidlMark("\\e57b", Color.of("#5eb95e"));
    });

    @SuppressWarnings("unused")
    private final Style HTMLClassModifierProtected = Style.named(".PROTECTED", () -> {
        buidlMark("\\e57b", palette.secondary);
    });

    @SuppressWarnings("unused")
    private final Style HTMLClassModifierPackagePrivate = Style.named(".PACKAGEPRIVATE", () -> {
        buidlMark("\\e8fb", palette.primary);
    });

    @SuppressWarnings("unused")
    private final Style HTMLClassModifierPrivate = Style.named(".PRIVATE", () -> {
        buidlMark("\\e3c6", palette.accent);
    });

    @SuppressWarnings("unused")
    private final Style HTMLClassModifierAbstract = Style.named(".ABSTRACT", () -> {
        buidlMark("\\e57b", Color.of("#5eb95e"));
    });

    @SuppressWarnings("unused")
    private final Style HTMLClassModifierDefault = Style.named(".DEFAULT", () -> {
        buidlMark("\\e57b", Color.of("#5eb95e"));
    });

    @SuppressWarnings("unused")
    private final Style HTMLClassModifierSTATIC = Style.named(".STATIC", () -> {
        buidlMark("\\e57b", Color.of("#5eb95e"));
    });

    @SuppressWarnings("unused")
    private final Style HTMLClassModifierFinal = Style.named(".FINAL", () -> {
        buidlMark("\\e57b", Color.of("#5eb95e"));
    });

    @SuppressWarnings("unused")
    private final Style HTMLClassModifierSynchronized = Style.named(".SYNCHRONIZED", () -> {
        buidlMark("\\e57b", Color.of("#5eb95e"));
    });

    @SuppressWarnings("unused")
    private final Style HTMLClassModifierNative = Style.named(".NATIVE", () -> {
        buidlMark("\\e57b", Color.of("#5eb95e"));
    });

    @SuppressWarnings("unused")
    private final Style HTMLClassModifierTransient = Style.named(".TRANSIENT", () -> {
        buidlMark("\\e57b", Color.of("#5eb95e"));
    });

    @SuppressWarnings("unused")
    private final Style HTMLClassModifierVolatile = Style.named(".VOLATILE", () -> {
        buidlMark("\\e57b", Color.of("#5eb95e"));
    });

    @SuppressWarnings("unused")
    private final Style HTMLClassModifierStrict = Style.named(".STRICTFP", () -> {
        buidlMark("\\e57b", Color.of("#5eb95e"));
    });

    private void buidlMark(String mark, Color color) {
        $.before(() -> {
            font.family(fonts.icon).color(color).size.smaller();
            content.text(mark);
            padding.right(0.6, rem);
        });
    }

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