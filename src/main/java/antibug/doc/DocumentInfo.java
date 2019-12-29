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
import java.util.List;

import javax.lang.model.element.Element;

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
     * @param docs Documents.
     * @return
     */
    private XML xml(List<? extends DocTree> docs) {
        return new XMLBuilder().parse(docs).build();
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
    private class XMLBuilder extends SimpleDocTreeVisitor<XMLBuilder, XMLBuilder> {

        private StringBuilder text = new StringBuilder();

        /**
         * Parse documetation.
         * 
         * @param docs
         * @return
         */
        private XMLBuilder parse(List<? extends DocTree> docs) {
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
        public XMLBuilder visitAttribute(AttributeTree node, XMLBuilder p) {
            text.append(' ').append(node.getName()).append("=\"");
            node.getValue().forEach(n -> n.accept(this, this));
            text.append("\"");
            return p;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public XMLBuilder visitComment(CommentTree node, XMLBuilder p) {
            return super.visitComment(node, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public XMLBuilder visitDocRoot(DocRootTree node, XMLBuilder p) {
            return super.visitDocRoot(node, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public XMLBuilder visitDocType(DocTypeTree node, XMLBuilder p) {
            return super.visitDocType(node, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public XMLBuilder visitEndElement(EndElementTree node, XMLBuilder p) {
            text.append("</").append(node.getName()).append('>');
            return p;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public XMLBuilder visitEntity(EntityTree node, XMLBuilder p) {
            return super.visitEntity(node, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public XMLBuilder visitErroneous(ErroneousTree node, XMLBuilder p) {
            return super.visitErroneous(node, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public XMLBuilder visitIdentifier(IdentifierTree node, XMLBuilder p) {
            return super.visitIdentifier(node, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public XMLBuilder visitIndex(IndexTree node, XMLBuilder p) {
            return super.visitIndex(node, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public XMLBuilder visitInheritDoc(InheritDocTree node, XMLBuilder p) {
            return super.visitInheritDoc(node, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public XMLBuilder visitLink(LinkTree node, XMLBuilder p) {
            return super.visitLink(node, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public XMLBuilder visitLiteral(LiteralTree node, XMLBuilder p) {
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
        public XMLBuilder visitStartElement(StartElementTree node, XMLBuilder p) {
            text.append("<").append(node.getName());
            node.getAttributes().forEach(attr -> attr.accept(this, this));
            text.append(node.isSelfClosing() ? "/>" : ">");
            return p;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public XMLBuilder visitSummary(SummaryTree node, XMLBuilder p) {
            return super.visitSummary(node, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public XMLBuilder visitSystemProperty(SystemPropertyTree node, XMLBuilder p) {
            return super.visitSystemProperty(node, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public XMLBuilder visitText(TextTree node, XMLBuilder p) {
            text.append(node.getBody());
            return p;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public XMLBuilder visitUnknownInlineTag(UnknownInlineTagTree node, XMLBuilder p) {
            return super.visitUnknownInlineTag(node, p);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public XMLBuilder visitValue(ValueTree node, XMLBuilder p) {
            return super.visitValue(node, p);
        }
    }
}
