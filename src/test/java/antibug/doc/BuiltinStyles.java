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

    Font MaterialIcon = new Font("Material Icons", "https://fonts.googleapis.com/icon?family=Material+Icons");

    Font Oswald = new Font("Oswald", "https://fonts.googleapis.com/css?family=Oswald:300");

    Numeric BlockBorderWidth = Numeric.of(3, px);

    Numeric BlockVerticalGap = Numeric.of(6, px);

    Numeric BlockHorizontalGap = Numeric.of(10, px);

    Numeric BlockInterval = Numeric.of(2, px);

    Numeric HeadSize = Numeric.of(18, px);

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
        if (paintBackground) background.color(color.opacify(-0.9d));
    }

    Numeric LeftGap = Numeric.of(20, px);

    Numeric BottomGap = Numeric.of(6, px);

    Style all = () -> {
        margin.size(0, px);
        padding.size(0, px);
        border.width(0, px);
        font.size(12, px).family("Yu Gothic UI", Font.SansSerif);
        line.height(1.35);
    };

    Style body = () -> {
        padding.vertical(5, px).horizontal(LeftGap);
        background.color(Color.White);
    };

    Style p = () -> {
        $.not($.attr("class").exist(), () -> {
            block(ParagraphColor, false);
        });
    };

    Style list = () -> {
        block(ListColor, true);
    };

    Style dl = () -> {
        $.not($.attr("class").exist(), list);

        $.lastChild(() -> {
            block(SignatureColor, false);
            background.color(Color.Transparent);
            margin.vertical(58, px);
            position.relative();

            $.before(() -> {
                font.size(HeadSize).family(Oswald, Font.SansSerif);
                display.block();
                content.text("Signature");
                position.absolute().top(-36, px).left(-3, px);
            });

            $.select("dt", () -> {
                display.block().width(70, px).floatLeft();
                font.size(9, px).weight.bold().color(SignatureColor);
                padding.top(2, px);
                border.bottom.none();
                text.transform.capitalize();
            });

            $.select("dd", () -> {
                margin.bottom(15, px).left(70, px);
            });

            $.select("b", () -> {
                display.block();
                border.bottom.width(1, px).solid().color(Color.WhiteGray);
                padding.bottom(3, px);
                margin.bottom(3, px);
                font.weight.bold();
                text.transform.capitalize();
            });
        });
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

    Style ul = () -> {
        $.not($.attr("class").exist(), () -> {
            list.style();

            $.select("li", () -> {
                listStyle.none();
                padding.bottom(BottomGap);

                $.before(() -> {
                    font.family(MaterialIcon).size(11, px).color(ListColor.saturate(-20));
                    content.text("\\e876");
                    display.block();
                    text.verticalAlign.bottom();
                    padding.right(5, px);
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
            font.family("Yu Gothic UI", Font.SansSerif);
        });
    };

    Style a = () -> {
        $.not($.attr("class").exist(), () -> {
            text.decoration.none();
        });
    };

    Style heading = () -> {
        font.family(Oswald, Font.SansSerif).size(HeadSize).weight.normal();
        padding.vertical(BlockVerticalGap);
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
        String formatted = Stylist.pretty().format(BuiltinStyles.class);
        System.out.println(formatted);
        formatted = formatted.replaceAll(".+#([^\\s\\*]+) \\*/\\.[a-zA-Z]+", "$1").replaceFirst("all", "*");
        System.out.println(formatted);

        Files.writeString(Path.of("docs/javadoc.css"), formatted, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
    }
}
