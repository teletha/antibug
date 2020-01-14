/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.doc.site;

import java.util.List;

import antibug.doc.ClassInfo;
import antibug.doc.ExecutableInfo;
import antibug.doc.FieldInfo;
import antibug.doc.MemberInfo;
import antibug.doc.MethodInfo;
import antibug.doc.builder.HTML;
import stylist.Style;

/**
 * 
 */
class SubNavigationView extends HTML {

    private final ClassInfo info;

    /**
     * @param info
     */
    public SubNavigationView(ClassInfo info) {
        this.info = info;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void declare() {
        members("Constructors", info.constructors());
        members("Static Fields", info.staticFields());
        members("Fields", info.nonStaticFields());
        members("Static Methods", info.staticMethods());
        members("Methods", info.nonStaticMethods());
    }

    private void members(String title, List<? extends MemberInfo> members) {
        if (members.size() != 0) {
            $("h5", style.RNaviTitle, text(title));
            $("ul", foŕ(members, m -> {
                $("li", () -> {
                    $(m.createModifier());
                    $(m.createName());

                    if (m instanceof ExecutableInfo) {
                        ExecutableInfo e = (ExecutableInfo) m;
                        $(e.createParameter());
                    }

                    if (m instanceof MethodInfo) {
                        $("i", style.RNaviReturn, ((MethodInfo) m).createReturnType());
                    }

                    if (m instanceof FieldInfo) {
                        $(((FieldInfo) m).createType());
                    }
                });
            }));
        }
    }

    /**
     * 
     */
    private static class style extends BaseStyle {

        public static final Style RNaviReturn = () -> {
            font.color(palette.secondary.saturate(-40));

            $.before(() -> {
                content.text(":");
                padding.horizontal(0.3, em);
            });
        };

        public static final Style RNaviTitle = () -> {
            margin.top(0.9, rem);
            font.weight.bold().size(1, rem);
        };
    }
}