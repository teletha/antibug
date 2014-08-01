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
class SourceTreeVisitor implements TreeVisitor<SourceXML, SourceXML> {

    /** The root xml. */
    private final SourceXML root;

    /** The current line xml. */
    private SourceXML xml;

    /** The actual line mapper. */
    private final SourceMapper mapper;

    /** The current AST line number. */
    private int logicalLine = 1;

    /** The current AST indent size. */
    private int indent = 0;

    /**
     * @param mapper
     */
    SourceTreeVisitor(SourceXML xml, SourceMapper mapper) {
        this.root = xml;
        this.mapper = mapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitAnnotatedType(AnnotatedTypeTree type, SourceXML context) {
        traceLine(type, context);

        for (AnnotationTree annotation : type.getAnnotations()) {
            annotation.accept(this, context);
        }
        type.getUnderlyingType().accept(this, context);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitAnnotation(AnnotationTree annotation, SourceXML context) {
        context = traceLine(annotation, context);

        SourceXML anno = context.child("annotation").text("@" + annotation.getAnnotationType());
        anno.join("(", ")", annotation.getArguments(), tree -> tree.accept(this, anno));

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitArrayAccess(ArrayAccessTree arg0, SourceXML context) {
        System.out.println("visitArrayAccess");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitArrayType(ArrayTypeTree arg0, SourceXML context) {
        System.out.println("visitArrayType");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitAssert(AssertTree arg0, SourceXML context) {
        System.out.println("visitAssert");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitAssignment(AssignmentTree assign, SourceXML context) {
        context.variable(assign.getVariable().toString()).space().text("=").space();
        assign.getExpression().accept(this, context);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitBinary(BinaryTree arg0, SourceXML context) {
        System.out.println("visitBinary");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitBlock(BlockTree arg0, SourceXML context) {
        System.out.println("visitBlock");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitBreak(BreakTree arg0, SourceXML context) {
        System.out.println("visitCase");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitCase(CaseTree arg0, SourceXML context) {
        System.out.println("visitCase");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitCatch(CatchTree arg0, SourceXML context) {
        System.out.println("visitCatch");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitClass(ClassTree clazz, SourceXML context) {
        // ===========================================
        // Annotations and Modifiers
        // ===========================================
        context = clazz.getModifiers().accept(this, context);

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
        xml.children("typeParam", "<", ">", clazz.getTypeParameters(), (tree, xml) -> tree.accept(this, xml)).space();

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
            xml.reserved("implements").space().join(implement, tree -> tree.accept(this, xml));
        }
        xml.text("{");
        startNewLine();

        // ===========================================
        // Members
        // ===========================================
        for (Tree tree : clazz.getMembers()) {
            tree.accept(this, context);
        }
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitCompilationUnit(CompilationUnitTree unit, SourceXML context) {
        context = traceLine(unit, context);

        context.reserved("package").space().text(unit.getPackageName()).semiColon().line();

        for (ImportTree tree : unit.getImports()) {
            context = visitImport(tree, context);
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
    public SourceXML visitCompoundAssignment(CompoundAssignmentTree arg0, SourceXML context) {
        System.out.println("visitCompoundAssignment");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitConditionalExpression(ConditionalExpressionTree arg0, SourceXML context) {
        System.out.println("visitConditionalExpression");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitContinue(ContinueTree arg0, SourceXML context) {
        System.out.println("visitContinue");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitDoWhileLoop(DoWhileLoopTree arg0, SourceXML context) {
        System.out.println("visitDoWhileLoop");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitEmptyStatement(EmptyStatementTree arg0, SourceXML context) {
        System.out.println("visitEmptyStatement");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitEnhancedForLoop(EnhancedForLoopTree arg0, SourceXML context) {
        System.out.println("visitEnhancedForLoop");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitErroneous(ErroneousTree arg0, SourceXML context) {
        System.out.println("visitErroneous");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitExpressionStatement(ExpressionStatementTree arg0, SourceXML context) {
        System.out.println("visitExpressionStatement");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitForLoop(ForLoopTree arg0, SourceXML context) {
        System.out.println("visitForLoop");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitIdentifier(IdentifierTree identifier, SourceXML context) {
        context.type(identifier.getName().toString());

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitIf(IfTree arg0, SourceXML context) {
        System.out.println("visitIf");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitImport(ImportTree tree, SourceXML context) {
        context = traceLine(tree, context);

        xml.reserved("import").space();
        if (tree.isStatic()) xml.reserved("static").space();
        xml.text(tree.getQualifiedIdentifier()).semiColon().line();
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitInstanceOf(InstanceOfTree arg0, SourceXML context) {
        System.out.println("visitInstanceOf");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitIntersectionType(IntersectionTypeTree arg0, SourceXML context) {
        System.out.println("visitIntersectionType");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitLabeledStatement(LabeledStatementTree arg0, SourceXML context) {
        System.out.println("visitLabeledStatement");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitLambdaExpression(LambdaExpressionTree arg0, SourceXML context) {
        System.out.println("visitLambdaExpression");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitLiteral(LiteralTree literal, SourceXML context) {
        traceLine(literal, context);

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
    public SourceXML visitMemberReference(MemberReferenceTree arg0, SourceXML context) {
        System.out.println("visitMemberReference");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitMemberSelect(MemberSelectTree arg0, SourceXML context) {
        System.out.println("visitMemberSelect");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitMethod(MethodTree method, SourceXML context) {
        traceLine(method, context);

        visitModifiers(method.getModifiers(), xml);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitMethodInvocation(MethodInvocationTree arg0, SourceXML context) {
        System.out.println("visitMethodInvocation");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitModifiers(ModifiersTree modifiers, SourceXML context) {
        context = traceLine(modifiers, context);

        // ===========================================
        // Annotations
        // ===========================================
        for (AnnotationTree tree : modifiers.getAnnotations()) {
            context = visitAnnotation(tree, context);
        }

        context = startNewLine();

        // ===========================================
        // Modifiers
        // ===========================================
        context = visitModifier(modifiers.getFlags(), context);

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitNewArray(NewArrayTree arg0, SourceXML context) {
        System.out.println("visitNewArray");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitNewClass(NewClassTree clazz, SourceXML context) {
        // check enclosing class
        ExpressionTree enclosing = clazz.getEnclosingExpression();

        if (enclosing != null) {
            enclosing.accept(this, context);
            context.text(".");
        }

        context.reserved("new").space();
        visitTypeParameters(clazz.getTypeArguments(), context);
        clazz.getIdentifier().accept(this, context);
        context.text("(");
        context.join(clazz.getArguments(), item -> item.accept(this, context));
        context.text(")");

        ClassTree body = clazz.getClassBody();

        if (body != null) {
            body.accept(this, context);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitOther(Tree arg0, SourceXML context) {
        System.out.println("visitOther");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitParameterizedType(ParameterizedTypeTree type, SourceXML context) {
        traceLine(type, context);

        type.getType().accept(this, context);

        context.children("typeParam", "<", ">", type.getTypeArguments(), (tree, xml) -> {
            tree.accept(this, xml);
        });
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitParenthesized(ParenthesizedTree arg0, SourceXML context) {
        System.out.println("visitParenthesized");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitPrimitiveType(PrimitiveTypeTree arg0, SourceXML context) {
        System.out.println("visitPrimitiveType");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitReturn(ReturnTree arg0, SourceXML context) {
        System.out.println("visitReturn");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitSwitch(SwitchTree arg0, SourceXML context) {
        System.out.println("visitSwitch");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitSynchronized(SynchronizedTree arg0, SourceXML context) {
        System.out.println("visitSynchronized");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitThrow(ThrowTree arg0, SourceXML context) {
        System.out.println("visitThrow");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitTry(TryTree arg0, SourceXML context) {
        System.out.println("visitTry");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitTypeCast(TypeCastTree arg0, SourceXML context) {
        System.out.println("visitTypeCast");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitTypeParameter(TypeParameterTree param, SourceXML context) {
        context.type(param.toString());
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitUnary(UnaryTree arg0, SourceXML context) {
        System.out.println("visitUnary");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitUnionType(UnionTypeTree arg0, SourceXML context) {
        System.out.println("visitUnionType");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitVariable(VariableTree variable, SourceXML context) {
        context = traceLine(variable, context);

        // Annotations and Modifiers
        visitModifiers(variable.getModifiers(), context);

        // Type
        variable.getType().accept(this, xml);

        // Name
        xml.variable(variable.getName().toString()).space().text("=").space();

        // Value
        variable.getInitializer().accept(this, xml);

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitWhileLoop(WhileLoopTree arg0, SourceXML context) {
        System.out.println("visitWhileLoop");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitWildcard(WildcardTree arg0, SourceXML context) {
        System.out.println("visitWildcard");
        return null;
    }

    /**
     * <p>
     * Create new line.
     * </p>
     */
    private SourceXML startNewLine() {
        return this.xml = root.child("line").attr("n", logicalLine++);
    }

    /**
     * <p>
     * Trace line.
     * </p>
     * 
     * @param current
     * @param context TODO
     */
    private SourceXML traceLine(Tree current, SourceXML context) {
        int actualLine = mapper.getLine(current);

        if (actualLine < logicalLine) {
            return context;
        }

        while (logicalLine < actualLine) {
            SourceXML line = startNewLine();
            line.text(mapper.readLineFrom(logicalLine) + "☆");
        }
        return startNewLine();
    }

    private static final Modifier[] MODIFIE_ORDER = {Modifier.PUBLIC, Modifier.PROTECTED, Modifier.PRIVATE,
            Modifier.STATIC, Modifier.FINAL};

    /**
     * <p>
     * Write modifiers.
     * </p>
     */
    private SourceXML visitModifier(Set<Modifier> modifiers, SourceXML context) {
        for (Modifier modifier : MODIFIE_ORDER) {
            if (modifiers.contains(modifier)) {
                context.reserved(modifier.name().toLowerCase()).space();
            }
        }
        return context;
    }

    /**
     * <p>
     * Write type parameters.
     * </p>
     * 
     * @param list
     * @param context
     */
    private void visitTypeParameters(List<? extends Tree> list, SourceXML context) {
        context.children("typeParam", "<", ">", list, (tree, xml) -> tree.accept(this, xml)).space();
    }
}
