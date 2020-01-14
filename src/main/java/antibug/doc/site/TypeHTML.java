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

import java.util.List;
import java.util.stream.IntStream;

import antibug.doc.ClassInfo;
import antibug.doc.ExecutableInfo;
import antibug.doc.FieldInfo;
import antibug.doc.Javadoc;
import antibug.doc.MemberInfo;
import antibug.doc.MethodInfo;
import antibug.doc.builder.HTML;
import kiss.XML;
import stylist.Style;
import stylist.value.Numeric;

public final class TypeHTML extends BaseHTML {

    /**
     * @param info
     */
    public TypeHTML(Javadoc javadoc, ClassInfo info) {
        super(javadoc, info);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void main() {
        $(new MainHTML());
    }

    /**
     * 
     */
    private class MainHTML extends HTML {

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
    }

    /**
     * Write HTML for each members.
     * 
     * @param member
     */
    private void writeMember(ExecutableInfo member) {
        $("section", main.MainSection, () -> {
            $("h2", attr("id", member.id()), main.MainTitle, () -> {
                XML type = member.createReturnType();

                $(member.createModifier());
                if (type != null) $("i", main.MainTitleReturn, type);
                $(member.createName());
                $(member.createParameter());
            });

            int types = member.numberOfTypeVariables();
            int params = member.numberOfParameters();
            int returns = member.returnVoid() ? 0 : 1;
            int exceptions = member.numberOfExceptions();

            if (0 < types + params + returns + exceptions) {
                $("section", main.MainSignature, () -> {
                    $("table", main.SignatureTable, () -> {
                        IntStream.range(0, types).forEach(i -> {
                            $("tr", main.SignatureTypeVariable, () -> {
                                $("td", member.createTypeVariable(i));
                                $("td", member.createTypeVariableComment(i));
                            });
                        });

                        IntStream.range(0, params).forEach(i -> {
                            $("tr", main.SignatureParameter, () -> {
                                $("td", member.createParameter(i), text(" "), member.createParameterName(i));
                                $("td", member.createParameterComment(i));
                            });
                        });

                        if (0 < returns) {
                            $("tr", main.SignatureReturn, () -> {
                                $("td", member.createReturnType());
                                $("td", member.createReturnComment());
                            });
                        }

                        IntStream.range(0, exceptions).forEach(i -> {
                            $("tr", main.SignatureException, () -> {
                                $("td", member.createException(i));
                                $("td", member.createExceptionComment(i));
                            });
                        });
                    });
                });
            }
        });

        $(member.createComment());
    }

    /**
     * 
     */
    private static class main extends BaseStyle {

        private static final Numeric signatureLabelWidth = Numeric.of(2.5, rem);

        public static Style MainSignature = () -> {
            padding.left(signatureLabelWidth);
        };

        public static Style MainSection = () -> {
            margin.top(BlockVerticalGap.multiply(8));
        };

        public static Style MainTitle = () -> {
            font.family(RobotoMono).size(1, rem).weight.normal();
            display.block();
        };

        public static Style MainTitleReturn = () -> {
            margin.right(0.5, rem);
        };

        public static final Style SignatureTable = () -> {
            $.select("td", () -> {
                padding.right(0.8, rem);
                text.verticalAlign.top().overflow.ellipsis();

                $.not($.lastChild(), () -> {
                    text.whiteSpace.nowrap();
                });
            });
        };

        public static final Style SignatureDefinition = () -> {
            position.relative();

            $.before(() -> {
                position.absolute().top(0.2, rem);
                display.inlineBlock().width(signatureLabelWidth);
                margin.left(signatureLabelWidth.negate());
                font.size(0.8, rem).color(palette.accent().opacify(-0.4)).family(RobotoMono);
            });
        };

        public static final Style SignatureTypeVariable = () -> {
            SignatureDefinition.style();

            $.before(() -> {
                content.text("Type");
            });
        };

        public static final Style SignatureParameter = () -> {
            SignatureDefinition.style();

            $.before(() -> {
                content.text("Param");
            });
        };

        public static final Style SignatureReturn = () -> {
            SignatureDefinition.style();

            $.before(() -> {
                content.text("Return");
            });
        };

        public static final Style SignatureException = () -> {
            SignatureDefinition.style();

            $.before(() -> {
                content.text("Throw");
            });
        };

        public static final Style SignatureName = () -> {
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void aside() {
        members("Constructors", info.constructors());
        members("Static Fields", info.staticFields());
        members("Fields", info.nonStaticFields());
        members("Static Methods", info.staticMethods());
        members("Methods", info.nonStaticMethods());
    }

    private void members(String title, List<? extends MemberInfo> members) {
        if (members.size() != 0) {
            $("h5", navi.RNaviTitle, text(title));
            $("ul", foŕ(members, m -> {
                $("li", () -> {
                    $(m.createModifier());
                    $(m.createName());

                    if (m instanceof ExecutableInfo) {
                        ExecutableInfo e = (ExecutableInfo) m;
                        $(e.createParameter());
                    }

                    if (m instanceof MethodInfo) {
                        $("i", navi.RNaviReturn, ((MethodInfo) m).createReturnType());
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
    private static class navi extends BaseStyle {

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