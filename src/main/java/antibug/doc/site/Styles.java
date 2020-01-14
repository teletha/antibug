/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.doc.site;

import stylist.AbstractStyleDSL;
import stylist.Style;
import stylist.value.Color;
import stylist.value.Numeric;

/**
 * 
 */
public class Styles extends AbstractStyleDSL implements BaseStyle {

    private static final double LineHeight = 1.5;

    private static final Numeric LeftNavigationWidth = Numeric.of(15, vw);

    // =====================================================
    // HTML Elements
    // =====================================================

    public static Style HTML = Style.named("html", () -> {
        font.size(13, px);
        scroll.smooth().padding.top(HeaderHeight);
        text.wordBreak.breakAll();

        $.select("*[class|=el]", () -> {
            font.size(1, rem);
        });
    });

    public static Style HTMLLevel2 = Style.named("h2", () -> {
        font.size(1.8, rem).family(fonts.title);
    });

    public static Style HTMLItalic = Style.named("i", () -> {
        font.style.normal();
    });

    public static Style HTMLAnchor = Style.named("a", () -> {
        font.color(Color.Inherit);
        text.decoration.none();
        cursor.pointer();

        $.hover(() -> {
            text.decoration.underline();
            text.decorationColor.color(palette.font.opacify(-0.5));
            text.underlineOffset.length(4, px);
            text.underlinePosition.under();
        });
    });

    /**
     * Define block-like.
     * 
     * @param color
     */
    public static void block(Color color, boolean paintBackground) {
        margin.left(0, px);
        padding.vertical(BaseStyle.BlockVerticalGap).horizontal(BaseStyle.BlockHorizontalGap);
        border.left.width(BaseStyle.BlockBorderWidth).solid().color(color);
        line.height(LineHeight);
        font.family(fonts.base);
        if (paintBackground) background.color(color.opacify(-0.8d));
    }

    /**
     * Define block-like.
     * 
     * @param color
     */
    public static void block2(Color color, boolean paintBackground) {
        padding.vertical(BaseStyle.BlockVerticalGap).horizontal(BaseStyle.BlockHorizontalGap);
        border.left.width(BaseStyle.BlockBorderWidth).solid().color(color);
        border.radius(2, px);
        line.height(LineHeight);
        font.family(fonts.base);
        position.relative();
        if (paintBackground) background.color(color.opacify(-0.8d));

        $.before(() -> {
            position.absolute();
            content.text("Signature");
            font.color(color.opacify(-0.9d)).size(1.6, rem);
        });
    }

    public static final Style SignatureParameterPart = () -> {
        display.opacity(0.8);
        padding.left(0.08, em);
    };

    public static final Style HTMLClassParameters = Style.named(".parameters", () -> {
        $.before(() -> {
            content.text("<");
        });
        $.after(() -> {
            content.text(">");
        });
    });

    public static final Style HTMLClassExtends = Style.named(".extends", () -> {

        $.before(() -> {
            content.text(" extends ");
        });
    });

    public static final Style HTMLClassSuper = Style.named(".super", () -> {

        $.before(() -> {
            content.text(" super ");
        });
    });

    public static final Style HTMLClassPackage = Style.named(".package", () -> {
        font.weight.bold();
    });

    public static final Style HTMLClassTypeInterface = Style.named(".Interface", () -> {
        buidlMark("\\e88f", Color.rgb(128, 88, 165));
    });

    public static final Style HTMLClassTypeFunctionalInterface = Style.named(".Functional", () -> {
        buidlMark("\\e88e", Color.rgb(128, 88, 165));
    });

    private static final Style HTMLClassTypeAbstractClass = Style.named(".AbstractClass", () -> {
        buidlMark("\\e90c", Color.of("#5eb95e").saturate(-30));
    });

    public static final Style HTMLClassTypeClass = Style.named(".Class", () -> {
        buidlMark("\\e90c", Color.of("#5eb95e"));
    });

    public static final Style HTMLClassTypeEnum = Style.named(".Enum", () -> {
        buidlMark("\\e01e", Color.of("#5eb95e").saturate(-35));
    });

    public static final Style HTMLClassTypeAnnotation = Style.named(".Annotation", () -> {
        buidlMark("\\e167", Color.of("#5eb95e").saturate(-35));
    });

    public static final Style HTMLClassTypeException = Style.named(".Exception", () -> {
        buidlMark("\\e031", Color.rgb(243, 123, 29));
    });

    private static void buidlMark(String mark, Color color) {
        position.relative();
        $.before(() -> {
            font.family(fonts.icon).color(color).size(1, rem);
            content.text(mark);
            padding.right(0.6, rem);
            text.verticalAlign.middle();
        });
    }

    public static final Style HTMLClassModifierPublic = Style.named(".PUBLIC", () -> {
        setMarkColor(Color.of("#5eb95e"));
    });

    public static final Style HTMLClassModifierProtected = Style.named(".PROTECTED", () -> {
        setMarkColor(palette.secondary);
    });

    public static final Style HTMLClassModifierPackagePrivate = Style.named(".PACKAGEPRIVATE", () -> {
        setMarkColor(palette.primary);
    });

    public static final Style HTMLClassModifierPrivate = Style.named(".PRIVATE", () -> {
        setMarkColor(palette.accent);
    });

    /** The circle icon. */
    private static final String circleStroked = "\\e836";

    /** The circle icon. */
    private static final String circleFilled = "\\e061"; // cirle filled big e3fa small e061

    /** The circle icon. */
    private static final String circlePointed = "\\e837"; // cirle filled big e3fa small e061

    /**
     * Assign mark color.
     * 
     * @param color
     */
    private static void setMarkColor(Color color) {
        position.relative();

        $.before(() -> {
            padding.right(0.6, rem);
            font.color(color).family(fonts.icon).size(0.8, em);
            content.text(circleFilled);

            $.with(".OVERRIDE", () -> {
                content.text(circlePointed);
            });

            $.with(".FIELD", () -> {
                content.text(circleStroked);
            });
        });
    }

    public static final Style HTMLClassModifierDefault = Style.named(".DEFAULT", () -> {
    });

    public static final Style HTMLClassModifierOverride = Style.named(".OVERRIDE", () -> {
    });

    public static final Style HTMLClassModifierSynchronized = Style.named(".SYNCHRONIZED", () -> {
        overlayIconRightBottom("\\e8ae");
    });

    public static final Style HTMLClassModifierTransient = Style.named(".TRANSIENT", () -> {
        overlayIconRightBottom("\\e14c");
    });

    public static final Style HTMLClassModifierAbstract = Style.named(".ABSTRACT", () -> {
        overlayAlphabetRightTop("A");
    });

    public static final Style HTMLClassModifierFinal = Style.named(".FINAL", () -> {
        overlayAlphabetRightTop("F");
    });

    public static final Style HTMLClassModifierVolatile = Style.named(".VOLATILE", () -> {
        overlayAlphabetRightTop("V");
    });

    /**
     * Create alphabetical mark.
     * 
     * @param mark
     */
    private static void overlayAlphabetRightTop(String mark) {
        position.relative();
        $.before(() -> {
            font.color(palette.primary).size(0.6, em).family(RobotoMono);
            content.text(mark);
            position.absolute().top(0, em).left(1.4, em);
        });
    }

    /**
     * Create icon mark.
     * 
     * @param mark
     */
    private static void overlayIconRightBottom(String mark) {
        position.relative();
        $.after(() -> {
            font.color(palette.primary).size(0.8, em).family(fonts.icon);
            content.text(mark);
            position.absolute().top(0.8, em).left(0.8, em);
        });
    }

    public static final Style workbench = () -> {
        font.size(FontSize).family(fonts.base).color(palette.font);
        line.height(LineHeight);
        display.width(100, vw);
    };

    // ==================================================================
    // Header
    // ==================================================================
    public static final Style HeaderArea = () -> {
        background.color(Color.White);
        position.sticky().top(0, rem);
        display.width(MaxWidth).height(HeaderHeight).zIndex(10).flex();
        margin.auto();
        border.bottom.color(palette.primary).width(1, px).solid();
    };

    public static final Style HeaderTitle = () -> {
        font.size(2.5, rem).family(fonts.title).weight.normal().color(palette.primary);
        flexItem.alignSelf.center();
    };

    // ==================================================================
    // Main
    // ==================================================================

    // ==================================================================
    // Left Side Navigation
    // ==================================================================
    public static final Style TypeNavigation = () -> {
        flexItem.basis(LeftNavigationWidth).shrink(0);

        $.child(() -> {
            position.sticky().top(HeaderHeight);

            $.child(() -> {
                margin.top(BaseStyle.BlockVerticalGap);
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
    public static final Style contents = () -> {
        flexItem.grow(1);
        margin.left(5, rem).right(1.5, rem);
    };

    public static final Style MainArea = () -> {
        display.width(MaxWidth).flex().direction.row();
        margin.auto();
    };

    public static final Style RNavi = () -> {
        flexItem.basis(RightNavigationWidth).shrink(0);
    };

    public static final Style RNaviStickyBlock = () -> {
        position.sticky().top(HeaderHeight);
        display.block().height(Numeric.of(80, vh).subtract(HeaderHeight)).maxWidth(RightNavigationWidth);
        overflow.auto().scrollbar.thin();
        text.whiteSpace.nowrap();

        $.hover(() -> {
            overflow.y.auto();
        });

        $.child().child(() -> {
            padding.vertical(0.15, em);
        });
    };
}