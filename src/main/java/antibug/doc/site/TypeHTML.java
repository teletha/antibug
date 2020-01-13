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
import kiss.XML;

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
    }

    /**
     * Write HTML for each members.
     * 
     * @param member
     */
    private void writeMember(ExecutableInfo member) {
        $("section", styles.MainSection, () -> {
            $("h2", id(member.id()), styles.MainTitle, () -> {
                XML type = member.createReturnType();

                $(member.createModifier());
                if (type != null) $("i", styles.MainTitleReturn, type);
                $(member.createName());
                $(member.createParameter());
            });

            int types = member.numberOfTypeVariables();
            int params = member.numberOfParameters();
            int returns = member.returnVoid() ? 0 : 1;
            int exceptions = member.numberOfExceptions();

            if (0 < types + params + returns + exceptions) {
                $("section", styles.MainSignature, () -> {
                    $("table", styles.SignatureTable, () -> {
                        IntStream.range(0, types).forEach(i -> {
                            $("tr", styles.SignatureTypeVariable, () -> {
                                $("td", member.createTypeVariable(i));
                                $("td", member.createTypeVariableComment(i));
                            });
                        });

                        IntStream.range(0, params).forEach(i -> {
                            $("tr", styles.SignatureParameter, () -> {
                                $("td", member.createParameter(i), text(" "), member.createParameterName(i));
                                $("td", member.createParameterComment(i));
                            });
                        });

                        if (0 < returns) {
                            $("tr", styles.SignatureReturn, () -> {
                                $("td", member.createReturnType());
                                $("td", member.createReturnComment());
                            });
                        }

                        IntStream.range(0, exceptions).forEach(i -> {
                            $("tr", styles.SignatureException, () -> {
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
            $("h5", styles.RNaviTitle, text(title));
            $("ul", foŕ(members, m -> {
                $("li", () -> {
                    $(m.createModifier());
                    $(m.createName());

                    if (m instanceof ExecutableInfo) {
                        ExecutableInfo e = (ExecutableInfo) m;
                        $(e.createParameter());
                    }

                    if (m instanceof MethodInfo) {
                        $("i", styles.RNaviReturn, ((MethodInfo) m).createReturnType());
                    }

                    if (m instanceof FieldInfo) {
                        $(((FieldInfo) m).createType());
                    }
                });
            }));
        }
    }
}