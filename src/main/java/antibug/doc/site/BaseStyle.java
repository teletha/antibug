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
import stylist.value.ColorPalette;
import stylist.value.Font;
import stylist.value.FontPalette;
import stylist.value.Numeric;

public class BaseStyle extends AbstractStyleDSL {

    protected static final ColorPalette palette = ColorPalette.with.primary($.rgb(40, 165, 245))
            .secondary($.rgb(250, 210, 50))
            .accent($.rgb(221, 81, 76))
            .background($.rgb(241, 250, 238))
            .font($.rgb(94, 109, 130));

    protected static final Font RobotoMono = Font.fromGoogle("Roboto Condensed");

    protected static final FontPalette fonts = FontPalette.with.base(RobotoMono).title(Font.fromGoogle("Ubuntu")).monoBySystem();

    protected static final Numeric FontSize = Numeric.of(13, px);

    protected static final Numeric MaxWidth = Numeric.of(85, vw);

    protected static Numeric SmallGap = Numeric.of(1, px);

    protected static Numeric BlockVerticalGap = Numeric.of(0.5, rem);

    protected static Numeric BlockBorderWidth = Numeric.of(3, px);

    protected static Numeric BlockHorizontalGap = Numeric.of(10, px);

    protected static final Numeric HeaderHeight = Numeric.of(80, px);

    protected static final Numeric RightNavigationWidth = Numeric.of(20, vw);
}
