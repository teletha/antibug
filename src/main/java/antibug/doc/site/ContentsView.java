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

import java.util.stream.IntStream;

import antibug.doc.ClassInfo;
import antibug.doc.ExecutableInfo;
import antibug.doc.MethodInfo;
import antibug.doc.builder.HTML;
import kiss.XML;
import stylist.Style;
import stylist.StyleDSL;
import stylist.value.Numeric;

/**
 * 
 */
class ContentsView extends HTML {

    private final ClassInfo info;

    /**
     * @param info
     */
    public ContentsView(ClassInfo info) {
        this.info = info;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void declare() {
        $("section", () -> {
            $("h2", () -> {
                $(info.createModifier(), info.createName());
            });
            $(info.createComment());

            for (ExecutableInfo constructor : info.constructors()) {
                writeMember(constructor);
            }

            for (MethodInfo method : info.methods()) {
                writeMember(method);
            }
        });
    }

    /**
     * Write HTML for each members.
     * 
     * @param member
     */
    private void writeMember(ExecutableInfo member) {
        $("section", style.Section, () -> {
            $("h2", attr("id", member.id()), style.Title, () -> {
                XML type = member.createReturnType();

                $(member.createModifier());
                if (type != null) $("i", style.ReturnOnTitle, type);
                $(member.createName());
                $(member.createParameter());
            });

            int types = member.numberOfTypeVariables();
            int params = member.numberOfParameters();
            int returns = member.returnVoid() ? 0 : 1;
            int exceptions = member.numberOfExceptions();

            if (0 < types + params + returns + exceptions) {
                $("table", style.SignatureTable, () -> {
                    IntStream.range(0, types).forEach(i -> {
                        $("tr", style.SignatureTypeVariable, () -> {
                            $("td", member.createTypeVariable(i));
                            $("td", member.createTypeVariableComment(i));
                        });
                    });

                    IntStream.range(0, params).forEach(i -> {
                        $("tr", style.SignatureParameter, () -> {
                            $("td", member.createParameter(i), text(" "), member.createParameterName(i));
                            $("td", member.createParameterComment(i));
                        });
                    });

                    if (0 < returns) {
                        $("tr", style.SignatureReturn, () -> {
                            $("td", member.createReturnType());
                            $("td", member.createReturnComment());
                        });
                    }

                    IntStream.range(0, exceptions).forEach(i -> {
                        $("tr", style.SignatureException, () -> {
                            $("td", member.createException(i));
                            $("td", member.createExceptionComment(i));
                        });
                    });
                });
            }
        });

        $(member.createComment());
    }

    /**
     * Style definition.
     */
    private interface style extends StyleDSL, BaseStyle {

        Numeric signatureLabelWidth = Numeric.of(2.5, rem);

        Style Section = () -> {
            margin.top(BlockVerticalGap.multiply(8));
        };

        Style Title = () -> {
            font.family(RobotoMono).size(1, rem).weight.normal();
            display.block();
        };

        Style ReturnOnTitle = () -> {
            margin.right(0.5, rem);
        };

        Style SignatureTable = () -> {
            padding.left(signatureLabelWidth);
            margin.top(0.1, rem).bottom(0.6, rem);

            $.select("td", () -> {
                padding.right(0.8, rem);
                text.verticalAlign.top().overflow.ellipsis();
                line.height(1.3);

                $.not($.lastChild(), () -> {
                    text.whiteSpace.nowrap();
                });
            });
        };

        Style SignatureDefinition = () -> {
            position.relative();

            $.before(() -> {
                position.absolute().top(0.1, rem);
                display.inlineBlock().width(signatureLabelWidth);
                margin.left(signatureLabelWidth.negate());
                font.size(0.8, rem).color(palette.accent().opacify(-0.4)).family(RobotoMono);
            });
        };

        Style SignatureTypeVariable = () -> {
            SignatureDefinition.style();

            $.before(() -> {
                content.text("Type");
            });
        };

        Style SignatureParameter = () -> {
            SignatureDefinition.style();

            $.before(() -> {
                content.text("Param");
            });
        };

        Style SignatureReturn = () -> {
            SignatureDefinition.style();

            $.before(() -> {
                content.text("Return");
            });
        };

        Style SignatureException = () -> {
            SignatureDefinition.style();

            $.before(() -> {
                content.text("Throw");
            });
        };
    }
}