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

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.UnionType;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.SimpleTypeVisitor9;

import com.sun.source.doctree.AttributeTree;
import com.sun.source.doctree.CommentTree;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocRootTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocTypeTree;
import com.sun.source.doctree.EndElementTree;
import com.sun.source.doctree.EntityTree;
import com.sun.source.doctree.ErroneousTree;
import com.sun.source.doctree.IdentifierTree;
import com.sun.source.doctree.IndexTree;
import com.sun.source.doctree.InheritDocTree;
import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.LiteralTree;
import com.sun.source.doctree.ParamTree;
import com.sun.source.doctree.ReturnTree;
import com.sun.source.doctree.StartElementTree;
import com.sun.source.doctree.SummaryTree;
import com.sun.source.doctree.SystemPropertyTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.doctree.UnknownInlineTagTree;
import com.sun.source.doctree.ValueTree;
import com.sun.source.util.SimpleDocTreeVisitor;

import kiss.I;
import kiss.Variable;
import kiss.XML;
import kiss.Ⅱ;

public class DocumentInfo {

    public final Variable<XML> comment = Variable.empty();

    /** Tag info. */
    public final List<Ⅱ<String, XML>> typeParameterTags = new ArrayList();

    /** Tag info. */
    public final List<Ⅱ<String, XML>> paramTags = new ArrayList();

    /** Tag info. */
    public final Variable<XML> returnTag = Variable.empty();

    protected DocumentInfo(Element e) {
        try {
            DocCommentTree docs = AntibugDoclet.TreeScanner.getDocCommentTree(e);
            if (docs != null) {
                comment.set(xml(docs.getFullBody()));
                docs.getBlockTags().forEach(tag -> tag.accept(new TagScanner(), this));
            }
        } catch (Throwable error) {
            System.err.println(e);
            error.printStackTrace();
        }
    }

    /**
     * Parse {@link TypeMirror} and build its XML expression.
     * 
     * @param type A target type.
     * @return New XML expression.
     */
    protected final XML parseTypeAsXML(TypeMirror type) {
        return new TypeXMLBuilder().parse(type).root;
    }

    /**
     * @param docs Documents.
     * @return
     */
    private XML xml(List<? extends DocTree> docs) {
        return new DocumentXMLBuilder().parse(docs).build();
    }

    /**
     * Create empty node.
     * 
     * @return
     */
    private XML emptyXML() {
        return I.xml("<span/>");
    }

    /**
     * 
     */
    private class TagScanner extends SimpleDocTreeVisitor<DocumentInfo, DocumentInfo> {

        /**
         * {@inheritDoc}
         */
        @Override
        public DocumentInfo visitParam(ParamTree node, DocumentInfo p) {
            Ⅱ<String, XML> pair = I.pair(node.getName().toString(), xml(node.getDescription()));

            if (node.isTypeParameter()) {
                typeParameterTags.add(pair);
            } else {
                paramTags.add(pair);
            }
            return p;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DocumentInfo visitReturn(ReturnTree node, DocumentInfo p) {
            returnTag.set(xml(node.getDescription()));
            return p;
        }
    }

    /**
     * 
     */
    private class DocumentXMLBuilder extends SimpleDocTreeVisitor<DocumentXMLBuilder, DocumentXMLBuilder> {

        private StringBuilder text = new StringBuilder();

        /**
         * Parse documetation.
         * 
         * @param docs
         * @return
         */
        private DocumentXMLBuilder parse(List<? extends DocTree> docs) {
            for (DocTree doc : docs) {
                doc.accept(this, this);
            }
            return this;
        }

        /**
         * Build XML fragmentation.
         * 
         * @return
         */
        private XML build() {
            try {
                if (text.length() == 0) {
                    return emptyXML();
                } else {
                    if (text.charAt(0) != '<') text.insert(0, "<span>").append("</span>");
                    return I.xml(text);
                }
            } catch (Exception e) {
                throw new Error(e.getMessage() + " [" + text.toString() + "]", e);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DocumentXMLBuilder visitAttribute(AttributeTree node, DocumentXMLBuilder p) {
            text.append(' ').append(node.getName()).append("=\"");
            node.getValue().forEach(n -> n.accept(this, this));
            text.append("\"");
            return p;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DocumentXMLBuilder visitComment(CommentTree node, DocumentXMLBuilder p) {
            return super.visitComment(node, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DocumentXMLBuilder visitDocRoot(DocRootTree node, DocumentXMLBuilder p) {
            return super.visitDocRoot(node, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DocumentXMLBuilder visitDocType(DocTypeTree node, DocumentXMLBuilder p) {
            return super.visitDocType(node, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DocumentXMLBuilder visitEndElement(EndElementTree node, DocumentXMLBuilder p) {
            text.append("</").append(node.getName()).append('>');
            return p;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DocumentXMLBuilder visitEntity(EntityTree node, DocumentXMLBuilder p) {
            return super.visitEntity(node, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DocumentXMLBuilder visitErroneous(ErroneousTree node, DocumentXMLBuilder p) {
            return super.visitErroneous(node, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DocumentXMLBuilder visitIdentifier(IdentifierTree node, DocumentXMLBuilder p) {
            return super.visitIdentifier(node, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DocumentXMLBuilder visitIndex(IndexTree node, DocumentXMLBuilder p) {
            return super.visitIndex(node, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DocumentXMLBuilder visitInheritDoc(InheritDocTree node, DocumentXMLBuilder p) {
            return super.visitInheritDoc(node, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DocumentXMLBuilder visitLink(LinkTree node, DocumentXMLBuilder p) {
            return super.visitLink(node, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DocumentXMLBuilder visitLiteral(LiteralTree node, DocumentXMLBuilder p) {
            text.append(escape(node.getBody().getBody()));
            return p;
        }

        /**
         * Escape text for XML.
         * 
         * @param text
         * @return
         */
        private String escape(String text) {
            StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                switch (c) {
                case '<':
                    buffer.append("&lt;");
                    break;
                case '>':
                    buffer.append("&gt;");
                    break;
                case '\"':
                    buffer.append("&quot;");
                    break;
                case '&':
                    buffer.append("&amp;");
                    break;
                case '\'':
                    buffer.append("&apos;");
                    break;
                default:
                    if (c > 0x7e) {
                        buffer.append("&#" + ((int) c) + ";");
                    } else
                        buffer.append(c);
                }
            }
            return buffer.toString();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DocumentXMLBuilder visitStartElement(StartElementTree node, DocumentXMLBuilder p) {
            text.append("<").append(node.getName());
            node.getAttributes().forEach(attr -> attr.accept(this, this));
            text.append(node.isSelfClosing() ? "/>" : ">");
            return p;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DocumentXMLBuilder visitSummary(SummaryTree node, DocumentXMLBuilder p) {
            return super.visitSummary(node, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DocumentXMLBuilder visitSystemProperty(SystemPropertyTree node, DocumentXMLBuilder p) {
            return super.visitSystemProperty(node, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DocumentXMLBuilder visitText(TextTree node, DocumentXMLBuilder p) {
            text.append(node.getBody());
            return p;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DocumentXMLBuilder visitUnknownInlineTag(UnknownInlineTagTree node, DocumentXMLBuilder p) {
            return super.visitUnknownInlineTag(node, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DocumentXMLBuilder visitValue(ValueTree node, DocumentXMLBuilder p) {
            return super.visitValue(node, p);
        }
    }

    /**
     * 
     */
    private class TypeXMLBuilder extends SimpleTypeVisitor9<TypeXMLBuilder, TypeXMLBuilder> {

        private final XML root = I.xml("<type/>");

        /**
         * Parse documetation.
         * 
         * @param docs
         * @return
         */
        private TypeXMLBuilder parse(TypeMirror type) {
            type.accept(this, this);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TypeXMLBuilder visitIntersection(IntersectionType t, TypeXMLBuilder p) {
            return super.visitIntersection(t, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TypeXMLBuilder visitUnion(UnionType t, TypeXMLBuilder p) {
            return super.visitUnion(t, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TypeXMLBuilder visitPrimitive(PrimitiveType primitive, TypeXMLBuilder p) {
            root.text(primitive.toString());
            return p;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TypeXMLBuilder visitNull(NullType t, TypeXMLBuilder p) {
            return super.visitNull(t, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TypeXMLBuilder visitArray(ArrayType array, TypeXMLBuilder p) {
            root.attr("array", "fix");
            array.getComponentType().accept(this, p);
            return p;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TypeXMLBuilder visitDeclared(DeclaredType declared, TypeXMLBuilder p) {
            // type
            TypeElement e = (TypeElement) declared.asElement();
            root.text(e.getSimpleName().toString());

            // enclosing
            Deque<String> enclosings = new LinkedList();
            Element enclosing = e.getEnclosingElement();
            while (enclosing.getKind() != ElementKind.PACKAGE) {
                enclosings.addFirst(((TypeElement) enclosing).getSimpleName().toString());
                enclosing = enclosing.getEnclosingElement();
            }
            if (enclosings.isEmpty() == false) root.attr("enclosing", I.join(".", enclosings));

            // pacakage
            root.attr("package", enclosing.toString());

            List<? extends TypeMirror> paramTypes = declared.getTypeArguments();
            if (paramTypes.isEmpty() == false) {
                XML parameters = root.after("parameters").next();
                for (TypeMirror paramType : paramTypes) {
                    parameters.append(parseTypeAsXML(paramType));
                }
            }

            return p;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TypeXMLBuilder visitError(ErrorType t, TypeXMLBuilder p) {
            return super.visitError(t, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TypeXMLBuilder visitTypeVariable(TypeVariable variable, TypeXMLBuilder p) {
            root.text(variable.toString());
            return p;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TypeXMLBuilder visitWildcard(WildcardType wildcard, TypeXMLBuilder p) {
            TypeMirror bounded = wildcard.getExtendsBound();
            if (bounded != null) {
                root.attr("bounded", "extends");
                bounded.accept(this, this);
                return p;
            }

            bounded = wildcard.getSuperBound();
            if (bounded != null) {
                root.attr("bounded", "super");
                bounded.accept(this, this);
                return p;
            }

            root.text("?");
            return p;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TypeXMLBuilder visitExecutable(ExecutableType t, TypeXMLBuilder p) {
            return super.visitExecutable(t, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TypeXMLBuilder visitNoType(NoType no, TypeXMLBuilder p) {
            switch (no.getKind()) {
            case VOID:
                root.text("void");
                break;

            default:
            }
            return p;
        }
    }
}
