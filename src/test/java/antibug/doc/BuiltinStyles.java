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

import kiss.WiseFunction;
import stylist.Style;
import stylist.StyleDSL;
import stylist.Stylist;
import stylist.value.Color;
import stylist.value.Font;
import stylist.value.Numeric;

public interface BuiltinStyles extends StyleDSL {

    Font IconFont = Font.fromGoogle("Material Icons");

    Font HeadFont = Font.fromGoogle("Oswald");

    Font BodyFont = new Font("Yu Gothic UI", "");

    Numeric BlockBorderWidth = Numeric.of(3, px);

    Numeric BlockVerticalGap = Numeric.of(6, px);

    Numeric BlockHorizontalGap = Numeric.of(10, px);

    Numeric BlockInterval = Numeric.of(2, px);

    Numeric HeadSize = Numeric.of(18, px);

    Numeric HeadTopGap = BlockVerticalGap.multiply(4);

    Numeric HeadBottomGap = BlockVerticalGap.multiply(2);

    double LineHeight = 1.5;

    Color ParagraphColor = Color.rgb(31, 141, 214);

    Color ListColor = Color.rgb(250, 210, 50);

    Color SignatureColor = Color.rgb(221, 81, 76);

    Color CodeColor = Color.rgb(94, 185, 94);

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

    Numeric LeftGap = Numeric.of(20, px);

    Numeric BottomGap = Numeric.of(10, px);

    Numeric SmallGap = Numeric.of(2, px);

    Style heading = () -> {
        font.family(HeadFont, Font.SansSerif).size(HeadSize).weight.normal();
        margin.top(HeadTopGap).bottom(HeadBottomGap);
        display.block();
    };

    Style body = () -> {
        padding.vertical(5, px).horizontal(LeftGap);
        background.color(Color.White);
        font.size(12, px).family(BodyFont, "Noto Sans", Font.SansSerif);
    };

    Style p = () -> {
        $.not($.attr("class").exist(), () -> {
            block(ParagraphColor, false);
        });
    };

    Style list = () -> {
        block(ListColor, true);
    };

    /**
     * <h2>OK Title is Good</h2>
     * <p>
     * Locate a present resource file which is assured that the spcified file exists. adqw aar aw ar
     * r r aeawew adqw aar aw ar r r aeawewadqw aar aw ar r r aeawewadqw aar aw ar r r aeawew adqw
     * aar aw ar r r aeawew
     * </p>
     * <p>
     * Locate a present resource file which is assured that the spcified file exists. adqw aar aw
     * arli r r aeawew adqw aar aw ar r r aeawewadqw aar aw ar r r aeawewadqw aar aw ar r r aeawew
     * adqw aar aw ar r r aeawew
     * </p>
     * <h>This is test title. </h>
     * <dl>
     * <dt>Action set</dt>
     * <dd>This is my test action.aaaaaaasda ads asda ds ad aweaafgafa a</dd>
     * <dt>Action set</dt>
     * <dd>This is my test action. adqw aar aw ar r r aeawewaearatararara</dd>
     * <dt>Action set</dt>
     * <dd>This is my test action.asdaweawe adqw aar aw ar r r aeawew adqw aar aw ar r r aeawew adqw
     * aar aw ar r r aeawew adqw aar aw ar r r aeawew</dd>
     * </dl>
     * <h3>This is test title.</h3>
     * <ul>
     * <li>Create item and test. Create item and test. Create item and test. Create item and test.
     * Create item and test.</li>
     * <li>Create item and test. Create item and test. Create item and test. Create item and test.
     * Create item and test.</li>
     * <li>Create item and test. Create item and test. Create item and test. Create item and test.
     * Create item and test.</li>
     * <li>Create item and test. Create item and test. Create item and test. Create item and test.
     * Create item and test.</li>
     * </ul>
     * <p>
     * {@link #map(WiseFunction)} preassign context.
     * </p>
     * <pre>
     * ───①───②───③───④───⑤──┼
     *    ↓   ↓   ↓   ↓   ↓
     *  ┌────────────────────┐
     *   map ○→●
     *  └────────────────────┘
     *    ↓   ↓   ↓   ↓   ↓  ↓
     * ───❶───❷───❸───❹───❺──┼
     * </pre> <link rel="stylesheet" href= "../../../../../docs/javadoc.css"/>
     * 
     * @param <T> A intext.
     * @param name A file name. asd aoijsouh ara@8shou:psdus: iha@daiagp9i 0qaeiaoudalsdaasu
     *            iayiudaidydsiusad uh ara@8shou:psdus: iha@daiagp9i 0qaeiaoudalsdaasu
     *            iayiudaidydsiu uh ara@8shou:psdus: iha@daiagp9i 0qaeiaoudalsdaasu iayiudaidydsiu
     *            uh ara@8shou:psdus: iha@daiagp9i 0qaeiaoudalsdaasu iayiudaidydsiu
     * @param modified A last modified time. uh ara@8shou:psdus: iha@daiagp9i 0qaeiaoudalsdaasu
     *            iayiudaidydsiu uh ara@8shou:psdus: iha@daiagp9i 0qaeiaoudalsdaasu iayiudaidydsiu
     * @return A located present file. uh ara@8shou:psdus: iha@daiagp9i 0qaeiaoudalsdaasu uh
     *         ara@8shou:psdus: iha@daiagp9i 0qaeiaoudalsdaasu iayiudaidydsiu iayiudaidydsiu
     * 
     * @see String
     */
    Style dl = () -> {
        $.not($.attr("class").exist(), list);

        $.lastChild(() -> {
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
        });
    };

    Style dt = () -> {
        $.not($.attr("class").exist(), () -> {
            border.bottom.width(1, px).solid().color("#ddd");
            padding.bottom(SmallGap);
            margin.bottom(SmallGap);
        });
    };

    Style dd = () -> {
        $.not($.attr("class").exist(), () -> {
            margin.bottom(20, px);
        });
    };

    /**
     * <ul>
     * <li>Create item and test. Create item and test. Create item and test. Create item and test.
     * Create item and test.</li>
     * <li>Create item and test. Create item and test. Create item and test. Create item and test.
     * Create item and test.</li>
     * <li>Create item and test. Create item and test. Create item and test. Create item and test.
     * Create item and test.</li>
     * <li>Create item and test. Create item and test. Create item and test. Create item and test.
     * Create item and test.</li>
     * </ul>
     * <link rel="stylesheet" href= "../../../../../docs/javadoc.css"/>
     */
    Style ul = () -> {
        $.not($.attr("class").exist(), () -> {
            list.style();

            $.select("li", () -> {
                listStyle.none();
                padding.bottom(BottomGap).left(1, em);
                text.indent(Numeric.of(-1.5, em));

                $.before(() -> {
                    font.family(IconFont).color(ListColor.saturate(-20));
                    content.text("\\e876");
                    text.verticalAlign.bottom();
                    padding.right(0.5, em);
                });
            });
        });
    };

    Style ol = () -> {
        $.not($.attr("class").exist(), list);
    };

    Style pre = () -> {
        $.not($.attr("class").exist(), () -> {
            block(CodeColor, true);
        });
    };

    Style a = () -> {
        $.not($.attr("class").exist(), () -> {
            font.family(BodyFont);
            text.decoration.none();
        });
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
        $.not($.attr("class").exist(), () -> {
            display.none();
        });
    };

    /**
     * a
     * <h2>OK Title is Good</h2>
     * <p>
     * Locate a present resource file which is assured that the spcified file exists. adqw aar aw ar
     * r r aeawew adqw aar aw ar r r aeawewadqw aar aw ar r r aeawewadqw aar aw ar r r aeawew adqw
     * aar aw ar r r aeawew
     * </p>
     * <p>
     * Locate a present resource file which is assured that the spcified file exists. adqw aar aw
     * arli r r aeawew adqw aar aw ar r r aeawewadqw aar aw ar r r aeawewadqw aar aw ar r r aeawew
     * adqw aar aw ar r r aeawew
     * </p>
     * <h>This is test title. </h>
     * <dl>
     * <dt>Action set</dt>
     * <dd>This is my test action.aaaaaaasda ads asda ds ad aweaafgafa a</dd>
     * <dt>Action set</dt>
     * <dd>This is my test action. adqw aar aw ar r r aeawewaearatararara</dd>
     * <dt>Action set</dt>
     * <dd>This is my test action.asdaweawe adqw aar aw ar r r aeawew adqw aar aw ar r r aeawew adqw
     * aar aw ar r r aeawew adqw aar aw ar r r aeawew</dd>
     * </dl>
     * <h3>This is test title.</h3>
     * <ul>
     * <li>Create item and test. Create item and test. Create item and test. Create item and test.
     * Create item and test.</li>
     * <li>Create item and test. Create item and test. Create item and test. Create item and test.
     * Create item and test.</li>
     * <li>Create item and test. Create item and test. Create item and test. Create item and test.
     * Create item and test.</li>
     * <li>Create item and test. Create item and test. Create item and test. Create item and test.
     * Create item and test.</li>
     * </ul>
     * <p>
     * {@link #map(WiseFunction)} preassign context.
     * </p>
     * <pre>
     * ───①───②───③───④───⑤──┼
     *    ↓   ↓   ↓   ↓   ↓
     *  ┌────────────────────┐
     *   map ○→●
     *  └────────────────────┘
     *    ↓   ↓   ↓   ↓   ↓  ↓
     * ───❶───❷───❸───❹───❺──┼
     * </pre> <link rel="stylesheet" href= "../../../../../docs/javadoc.css"/>
     * 
     * @param <T> A intext.
     * @param name A file name. asd aoijsouh ara@8shou:psdus: iha@daiagp9i 0qaeiaoudalsdaasu
     *            iayiudaidydsiusad uh ara@8shou:psdus: iha@daiagp9i 0qaeiaoudalsdaasu
     *            iayiudaidydsiu uh ara@8shou:psdus: iha@daiagp9i 0qaeiaoudalsdaasu iayiudaidydsiu
     *            uh ara@8shou:psdus: iha@daiagp9i 0qaeiaoudalsdaasu iayiudaidydsiu
     * @param modified A last modified time. uh ara@8shou:psdus: iha@daiagp9i 0qaeiaoudalsdaasu
     *            iayiudaidydsiu uh ara@8shou:psdus: iha@daiagp9i 0qaeiaoudalsdaasu iayiudaidydsiu
     * @return A located present file. uh ara@8shou:psdus: iha@daiagp9i 0qaeiaoudalsdaasu uh
     *         ara@8shou:psdus: iha@daiagp9i 0qaeiaoudalsdaasu iayiudaidydsiu iayiudaidydsiu
     * 
     * @see String
     */
    public static void main(String[] args) throws IOException {
        String formatted = Stylist.pretty().importNormalizeStyle().format(BuiltinStyles.class);
        System.out.println(formatted);
        formatted = formatted.replaceAll(".+#([^\\s\\*]+) \\*/\\.[a-zA-Z]+", "$1");
        System.out.println(formatted);

        Files.writeString(Path.of("docs/javadoc.css"), formatted, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
    }
}
