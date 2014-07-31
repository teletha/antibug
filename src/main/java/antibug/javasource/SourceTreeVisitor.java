/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.javasource;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;

import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.AssertTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EmptyStatementTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ErroneousTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.IntersectionTypeTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.UnionTypeTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.tree.WildcardTree;

/**
 * @version 2014/07/31 21:39:41
 */
class SourceTreeVisitor implements TreeVisitor<Object, SourceXML> {

    /** The root xml. */
    private final SourceXML root;

    /** The current line xml. */
    private SourceXML xml;

    /** The actual line mapper. */
    private final SourceMapper mapper;

    /** The current AST line number. */
    private int logicalLine = 0;

    /** The current AST indent size. */
    private int indent = 0;

    /**
     * @param mapper
     */
    SourceTreeVisitor(SourceXML xml, SourceMapper mapper) {
        this.root = xml;
        this.mapper = mapper;

        startNewLine();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitAnnotatedType(AnnotatedTypeTree arg0, SourceXML context) {
        System.out.println("visitAnnotatedType");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitAnnotation(AnnotationTree annotation, SourceXML context) {
        traceLine(annotation);

        SourceXML anno = xml.child("annotation").text("@" + annotation.getAnnotationType());

        List<? extends ExpressionTree> values = annotation.getArguments();

        if (!values.isEmpty()) {
            anno.text("(");
            for (int i = 0, size = values.size(); i < size; i++) {
                values.get(i).accept(this, anno);

                if (i < size - 1) {
                    anno.text(",").space();
                }
            }
            anno.text(")");
        }
        return startNewLine();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitArrayAccess(ArrayAccessTree arg0, SourceXML context) {
        System.out.println("visitArrayAccess");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitArrayType(ArrayTypeTree arg0, SourceXML context) {
        System.out.println("visitArrayType");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitAssert(AssertTree arg0, SourceXML context) {
        System.out.println("visitAssert");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitAssignment(AssignmentTree assign, SourceXML context) {
        context.variable(assign.getVariable().toString()).space().text("=").space();
        assign.getExpression().accept(this, context);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitBinary(BinaryTree arg0, SourceXML context) {
        System.out.println("visitBinary");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitBlock(BlockTree arg0, SourceXML context) {
        System.out.println("visitBlock");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitBreak(BreakTree arg0, SourceXML context) {
        System.out.println("visitCase");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitCase(CaseTree arg0, SourceXML context) {
        System.out.println("visitCase");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitCatch(CatchTree arg0, SourceXML context) {
        System.out.println("visitCatch");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitClass(ClassTree clazz, SourceXML context) {
        // ===========================================
        // Annotations and Modifiers
        // ===========================================
        visitModifiers(clazz.getModifiers(), context);

        // ===========================================
        // Type Declaration
        // ===========================================
        switch (clazz.getKind()) {
        case CLASS:
            xml.reserved("class").space();
            break;

        case INTERFACE:
            xml.reserved("interface").space();
            break;

        case ANNOTATION:
            xml.reserved("@interface").space();
            break;

        case ENUM:
            xml.reserved("enum").space();
            break;
        }
        xml.type(clazz.getSimpleName().toString());

        // ===========================================
        // Type Parameters
        // ===========================================
        List<? extends TypeParameterTree> parameters = clazz.getTypeParameters();

        if (!parameters.isEmpty()) {
            SourceXML param = xml.child("typeParam").text("<");

            for (int i = 0, size = parameters.size(); i < size; i++) {
                parameters.get(i).accept(this, param);

                if (i < size - 1) {
                    param.text(",").space();
                }
            }
            param.text(">");
        }
        xml.space();

        // ===========================================
        // Extends
        // ===========================================
        Tree extend = clazz.getExtendsClause();

        if (extend != null) {
            xml.reserved("extends").space();
            extend.accept(this, xml);
        }

        // ===========================================
        // Implements
        // ===========================================
        List<? extends Tree> implement = clazz.getImplementsClause();

        if (!implement.isEmpty()) {
            xml.reserved("implements").space();

            for (int i = 0, size = implement.size(); i < size; i++) {
                implement.get(i).accept(this, xml);

                if (i < size - 1) {
                    xml.text(",").space();
                }
            }
        }
        xml.text("{");
        startNewLine();

        // ===========================================
        // Members
        // ===========================================
        for (Tree tree : clazz.getMembers()) {
            tree.accept(this, context);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitCompilationUnit(CompilationUnitTree unit, SourceXML context) {
        traceLine(unit);

        xml.reserved("package").space().text(unit.getPackageName()).semiColon().line();

        for (ImportTree tree : unit.getImports()) {
            visitImport(tree, context);
        }

        for (Tree tree : unit.getTypeDecls()) {
            tree.accept(this, context);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitCompoundAssignment(CompoundAssignmentTree arg0, SourceXML context) {
        System.out.println("visitCompoundAssignment");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitConditionalExpression(ConditionalExpressionTree arg0, SourceXML context) {
        System.out.println("visitConditionalExpression");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitContinue(ContinueTree arg0, SourceXML context) {
        System.out.println("visitContinue");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitDoWhileLoop(DoWhileLoopTree arg0, SourceXML context) {
        System.out.println("visitDoWhileLoop");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitEmptyStatement(EmptyStatementTree arg0, SourceXML context) {
        System.out.println("visitEmptyStatement");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitEnhancedForLoop(EnhancedForLoopTree arg0, SourceXML context) {
        System.out.println("visitEnhancedForLoop");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitErroneous(ErroneousTree arg0, SourceXML context) {
        System.out.println("visitErroneous");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitExpressionStatement(ExpressionStatementTree arg0, SourceXML context) {
        System.out.println("visitExpressionStatement");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitForLoop(ForLoopTree arg0, SourceXML context) {
        System.out.println("visitForLoop");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitIdentifier(IdentifierTree identifier, SourceXML context) {
        context.type(identifier.getName().toString());

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitIf(IfTree arg0, SourceXML context) {
        System.out.println("visitIf");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitImport(ImportTree tree, SourceXML context) {
        traceLine(tree);

        xml.reserved("import").space();
        if (tree.isStatic()) xml.reserved("static").space();
        xml.text(tree.getQualifiedIdentifier()).semiColon().line();
        return startNewLine();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitInstanceOf(InstanceOfTree arg0, SourceXML context) {
        System.out.println("visitInstanceOf");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitIntersectionType(IntersectionTypeTree arg0, SourceXML context) {
        System.out.println("visitIntersectionType");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitLabeledStatement(LabeledStatementTree arg0, SourceXML context) {
        System.out.println("visitLabeledStatement");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitLambdaExpression(LambdaExpressionTree arg0, SourceXML context) {
        System.out.println("visitLambdaExpression");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitLiteral(LiteralTree literal, SourceXML context) {
        traceLine(literal);

        Object value = literal.getValue();

        switch (literal.getKind()) {
        case STRING_LITERAL:
            context.string(value.toString());
            break;

        case INT_LITERAL:
            context.number(value.toString());
            break;

        case BOOLEAN_LITERAL:
            context.reserved(value.toString());
            break;

        default:
            throw new Error(literal.getKind().toString());
        }

        // try {
        // switch (tree.typetag) {
        // case INT:
        // print(tree.value.toString());
        // break;
        // case LONG:
        // print(tree.value + "L");
        // break;
        // case FLOAT:
        // print(tree.value + "F");
        // break;
        // case DOUBLE:
        // print(tree.value.toString());
        // break;
        // case CHAR:
        // print("\'" + Convert.quote(String.valueOf((char) ((Number) tree.value).intValue())) +
        // "\'");
        // break;
        // case BOOLEAN:
        // print(((Number) tree.value).intValue() == 1 ? "true" : "false");
        // break;
        // case BOT:
        // print("null");
        // break;
        // default:
        // print("\"" + Convert.quote(tree.value.toString()) + "\"");
        // break;
        // }
        // } catch (IOException e) {
        // throw new UncheckedIOException(e);
        // }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitMemberReference(MemberReferenceTree arg0, SourceXML context) {
        System.out.println("visitMemberReference");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitMemberSelect(MemberSelectTree arg0, SourceXML context) {
        System.out.println("visitMemberSelect");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitMethod(MethodTree arg0, SourceXML context) {
        System.out.println("visitMethod");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitMethodInvocation(MethodInvocationTree arg0, SourceXML context) {
        System.out.println("visitMethodInvocation");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitModifiers(ModifiersTree modifiers, SourceXML context) {
        // ===========================================
        // Annotations
        // ===========================================
        for (AnnotationTree tree : modifiers.getAnnotations()) {
            visitAnnotation(tree, context);
        }

        // ===========================================
        // Modifiers
        // ===========================================
        visitModifier(modifiers.getFlags(), xml);

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitNewArray(NewArrayTree arg0, SourceXML context) {
        System.out.println("visitNewArray");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitNewClass(NewClassTree arg0, SourceXML context) {
        System.out.println("visitNewClass");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitOther(Tree arg0, SourceXML context) {
        System.out.println("visitOther");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitParameterizedType(ParameterizedTypeTree type, SourceXML context) {
        context.type(type.getType().toString());

        List<? extends Tree> arguments = type.getTypeArguments();

        if (!arguments.isEmpty()) {
            SourceXML params = context.child("typeParam").text("<");

            for (int i = 0, size = arguments.size(); i < size; i++) {
                arguments.get(i).accept(this, params);

                if (i < size - 1) {
                    params.text(",").space();
                }
            }
            params.text(">");
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitParenthesized(ParenthesizedTree arg0, SourceXML context) {
        System.out.println("visitParenthesized");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitPrimitiveType(PrimitiveTypeTree arg0, SourceXML context) {
        System.out.println("visitPrimitiveType");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitReturn(ReturnTree arg0, SourceXML context) {
        System.out.println("visitReturn");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitSwitch(SwitchTree arg0, SourceXML context) {
        System.out.println("visitSwitch");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitSynchronized(SynchronizedTree arg0, SourceXML context) {
        System.out.println("visitSynchronized");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitThrow(ThrowTree arg0, SourceXML context) {
        System.out.println("visitThrow");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitTry(TryTree arg0, SourceXML context) {
        System.out.println("visitTry");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitTypeCast(TypeCastTree arg0, SourceXML context) {
        System.out.println("visitTypeCast");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitTypeParameter(TypeParameterTree param, SourceXML context) {
        context.type(param.toString());
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitUnary(UnaryTree arg0, SourceXML context) {
        System.out.println("visitUnary");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitUnionType(UnionTypeTree arg0, SourceXML context) {
        System.out.println("visitUnionType");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitVariable(VariableTree variable, SourceXML context) {
        traceLine(variable);

        // Annotations and Modifiers
        visitModifiers(variable.getModifiers(), xml);

        // Type
        variable.getType().accept(this, xml);

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitWhileLoop(WhileLoopTree arg0, SourceXML context) {
        System.out.println("visitWhileLoop");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitWildcard(WildcardTree arg0, SourceXML context) {
        System.out.println("visitWildcard");
        return null;
    }

    /**
     * <p>
     * Create new line.
     * </p>
     */
    private SourceXML startNewLine() {
        return this.xml = root.child("line").attr("n", ++logicalLine);
    }

    /**
     * <p>
     * Trace line.
     * </p>
     * 
     * @param current
     */
    private void traceLine(Tree current) {
        int actualLine = mapper.getLine(current);

        while (logicalLine < actualLine) {
            xml.text(mapper.readLineFrom(logicalLine) + "☆");
            startNewLine();
        }
    }

    private static final Modifier[] MODIFIE_ORDER = {Modifier.PUBLIC, Modifier.PROTECTED, Modifier.PRIVATE,
            Modifier.STATIC, Modifier.FINAL};

    /**
     * <p>
     * Write modifiers.
     * </p>
     */
    private void visitModifier(Set<Modifier> modifiers, SourceXML context) {
        for (Modifier modifier : MODIFIE_ORDER) {
            if (modifiers.contains(modifier)) {
                context.reserved(modifier.name().toLowerCase()).space();
            }
        }
    }
}
