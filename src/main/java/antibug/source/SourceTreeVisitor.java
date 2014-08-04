/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.source;

import static com.sun.tools.javac.code.Flags.*;
import static com.sun.tools.javac.tree.JCTree.Tag.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;

import kiss.XML;

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
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.UnionTypeTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.tree.WildcardTree;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;

/**
 * @version 2014/07/31 21:39:41
 */
class SourceTreeVisitor implements TreeVisitor<SourceXML, SourceXML> {

    private static final Modifier[] MODIFIE_ORDER = {Modifier.PUBLIC, Modifier.PROTECTED, Modifier.PRIVATE,
            Modifier.ABSTRACT, Modifier.STATIC, Modifier.FINAL, Modifier.DEFAULT};

    /** The root xml. */
    private final XML root;

    /** The latest line xml. */
    private SourceXML latestLine;

    /** The actual line mapper. */
    private final SourceMapper mapper;

    /** The current AST line number. */
    private int logicalLine = 1;

    /** The current AST indent size. */
    private int indentLevel = 0;

    /** The indent pattern. */
    private String indent = "    ";

    /** The enclosing class name stack. */
    private Deque<String> classNames = new ArrayDeque();

    /** The statement level manager. */
    private Statement statement = new Statement();

    /**
     * @param mapper
     */
    SourceTreeVisitor(XML xml, SourceMapper mapper) {
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
            annotation.accept(this, context).space();
        }
        type.getUnderlyingType().accept(this, context);

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitAnnotation(AnnotationTree annotation, SourceXML context) {
        context = traceLine(annotation, context);

        return context.child("annotation")
                .text("@" + annotation.getAnnotationType())
                .join("(", annotation.getArguments(), ")");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitArrayAccess(ArrayAccessTree arg0, SourceXML context) {
        System.out.println("visitArrayAccess");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitArrayType(ArrayTypeTree array, SourceXML context) {
        traceLine(array, context);

        array.getType().accept(this, context).text("[]");

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitAssert(AssertTree arg0, SourceXML context) {
        System.out.println("visitAssert");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitAssignment(AssignmentTree assign, SourceXML context) {
        statement.start();
        context.variable(assign.getVariable().toString()).space().text("=").space().visit(assign.getExpression());
        statement.end(true);

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitBinary(BinaryTree binary, SourceXML context) {
        traceLine(binary, context);

        String operator = "";

        switch (binary.getKind()) {
        case LESS_THAN:
            operator = "<";
            break;

        case LESS_THAN_EQUAL:
            operator = "<=";
            break;

        case GREATER_THAN:
            operator = ">";
            break;

        case GREATER_THAN_EQUAL:
            operator = ">=";
            break;

        case EQUAL_TO:
            operator = "==";
            break;

        case NOT_EQUAL_TO:
            operator = "!=";
            break;

        case AND:
            operator = "&";
            break;

        case OR:
            operator = "|";
            break;

        case PLUS:
            operator = "+";
            break;

        case MINUS:
            operator = "-";
            break;

        case MULTIPLY:
            operator = "*";
            break;

        case DIVIDE:
            operator = "/";
            break;

        case REMAINDER:
            operator = "%";
            break;

        case CONDITIONAL_OR:
            operator = "||";
            break;

        case CONDITIONAL_AND:
            operator = "&&";
            break;

        case XOR:
            operator = "^";
            break;

        case LEFT_SHIFT:
            operator = "<<";
            break;

        case RIGHT_SHIFT:
            operator = ">>";
            break;

        case UNSIGNED_RIGHT_SHIFT:
            operator = ">>>";
            break;

        default:
            throw new Error(binary.getKind().toString());
        }

        binary.getLeftOperand().accept(this, context);
        context.space().text(operator).space();
        binary.getRightOperand().accept(this, context);

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitBlock(BlockTree block, SourceXML context) {
        context = traceLine(block, context);

        if (block.isStatic()) {
            context.reserved("static");
        }

        return writeBlock(block.getStatements(), context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitBreak(BreakTree arg0, SourceXML context) {
        System.out.println("visitCase");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitCase(CaseTree arg0, SourceXML context) {
        System.out.println("visitCase");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitCatch(CatchTree arg0, SourceXML context) {
        System.out.println("visitCatch");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitClass(ClassTree clazz, SourceXML context) {
        context = traceLine(clazz, context);

        // ===========================================
        // Annotations and Modifiers
        // ===========================================
        context = clazz.getModifiers().accept(this, context);

        // ===========================================
        // Type Declaration
        // ===========================================
        switch (clazz.getKind()) {
        case CLASS:
            latestLine.reserved("class").space();
            break;

        case INTERFACE:
            latestLine.reserved("interface").space();
            break;

        case ANNOTATION_TYPE:
            latestLine.reserved("@interface").space();
            break;

        case ENUM:
            latestLine.reserved("enum").space();
            break;

        default:
            throw new Error(clazz.getKind().toString());
        }

        classNames.add(clazz.getSimpleName().toString());
        latestLine.type(classNames.peekLast());

        // ===========================================
        // Type Parameters
        // ===========================================
        latestLine.typeParams(clazz.getTypeParameters(), false);

        // ===========================================
        // Extends
        // ===========================================
        Tree extend = clazz.getExtendsClause();

        if (extend != null) {
            latestLine.space().reserved("extends").space();
            extend.accept(this, latestLine);
        }

        // ===========================================
        // Implements
        // ===========================================
        List<? extends Tree> implement = clazz.getImplementsClause();

        if (!implement.isEmpty()) {
            latestLine.space().reserved("implements").space().join(implement);
        }

        latestLine.space().text("{");
        indentLevel++;

        // ===========================================
        // Members
        // ===========================================
        EnumConstantsInfo info = new EnumConstantsInfo(clazz.getKind());

        for (Tree tree : clazz.getMembers()) {
            info.processNonFirstConstant(tree);
            context = tree.accept(this, context);
            info.completeIfAllConstantsDeclared(tree);
        }
        info.complete();

        indentLevel--;
        classNames.pollLast();
        return startNewLine().text("}");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitCompilationUnit(CompilationUnitTree unit, SourceXML context) {
        context = traceLine(unit, context);

        context.reserved("package").space().text(unit.getPackageName()).semiColon();

        for (ImportTree tree : unit.getImports()) {
            context = visitImport(tree, context);
        }

        for (Tree tree : unit.getTypeDecls()) {
            tree.accept(this, context);
        }
        return startNewLine();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitCompoundAssignment(CompoundAssignmentTree assign, SourceXML context) {
        String operator = "";

        switch (assign.getKind()) {
        case PLUS_ASSIGNMENT:
            operator = "+=";
            break;

        case MINUS_ASSIGNMENT:
            operator = "-=";
            break;

        case MULTIPLY_ASSIGNMENT:
            operator = "*=";
            break;

        case DIVIDE_ASSIGNMENT:
            operator = "/=";
            break;

        case REMAINDER_ASSIGNMENT:
            operator = "%=";
            break;

        case OR_ASSIGNMENT:
            operator = "|=";
            break;

        case AND_ASSIGNMENT:
            operator = "&=";
            break;

        case XOR_ASSIGNMENT:
            operator = "^=";
            break;

        case LEFT_SHIFT_ASSIGNMENT:
            operator = "<<=";
            break;

        case RIGHT_SHIFT_ASSIGNMENT:
            operator = ">>=";
            break;

        case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
            operator = ">>>=";
            break;

        default:
            throw new Error(assign.getKind().toString());
        }

        statement.start();
        context = context.visit(assign.getVariable()).space().text(operator).space().visit(assign.getExpression());
        statement.end(true);

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitConditionalExpression(ConditionalExpressionTree ternary, SourceXML context) {
        context = traceLine(ternary, context);

        statement.start();
        context = context.visit(ternary.getCondition())
                .space()
                .text("?")
                .space()
                .visit(ternary.getTrueExpression())
                .space()
                .text(":")
                .space()
                .visit(ternary.getFalseExpression());
        statement.end(true);

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitContinue(ContinueTree arg0, SourceXML context) {
        System.out.println("visitContinue");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitDoWhileLoop(DoWhileLoopTree loop, SourceXML context) {
        context = traceLine(loop, context);

        context = context.reserved("do").visit(loop.getStatement());

        statement.start();
        context.space().reserved("while").space().visit(loop.getCondition()).semiColon();
        statement.end(false);

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitEmptyStatement(EmptyStatementTree arg0, SourceXML context) {
        System.out.println("visitEmptyStatement");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitEnhancedForLoop(EnhancedForLoopTree loop, SourceXML context) {
        context = traceLine(loop, context);

        latestLine.reserved("for").space().text("(");
        loop.getVariable().accept(this, latestLine);
        latestLine.space().text(":").space();
        loop.getExpression().accept(this, latestLine);
        latestLine.text(")");
        loop.getStatement().accept(this, latestLine);

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitErroneous(ErroneousTree arg0, SourceXML context) {
        System.out.println("visitErroneous");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitExpressionStatement(ExpressionStatementTree expression, SourceXML context) {
        context = traceLine(expression, context);

        expression.getExpression().accept(this, context);

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitForLoop(ForLoopTree loop, SourceXML context) {
        context = traceLine(loop, context);

        statement.start();
        context.reserved("for").space().text("(").join(loop.getInitializer()).semiColon().space();
        loop.getCondition().accept(this, context).semiColon().join(" ", loop.getUpdate(), ",", null).text(")");
        statement.end(false);

        context.visit(loop.getStatement());

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitIdentifier(IdentifierTree identifier, SourceXML context) {
        context.type(identifier.getName().toString());

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitIf(IfTree tree, SourceXML context) {
        traceLine(tree, context);

        // Condition
        statement.start();
        latestLine.reserved("if").space();
        tree.getCondition().accept(this, latestLine);
        statement.end(false);

        // Then
        tree.getThenStatement().accept(this, latestLine);

        StatementTree elseStatement = tree.getElseStatement();

        if (elseStatement != null) {
            latestLine.space().reserved("else").space();
            elseStatement.accept(this, latestLine);
        }

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitImport(ImportTree tree, SourceXML context) {
        context = traceLine(tree, context);

        latestLine.reserved("import").space();
        if (tree.isStatic()) latestLine.reserved("static").space();
        latestLine.text(tree.getQualifiedIdentifier()).semiColon();
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitInstanceOf(InstanceOfTree instanceOf, SourceXML context) {
        context = traceLine(instanceOf, context);

        statement.start();
        context = context.visit(instanceOf.getExpression())
                .space()
                .reserved("instanceof")
                .space()
                .visit(instanceOf.getType());
        statement.end(true);

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitIntersectionType(IntersectionTypeTree intersection, SourceXML context) {
        context = traceLine(intersection, context);

        return context.join(null, intersection.getBounds(), " &", null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitLabeledStatement(LabeledStatementTree arg0, SourceXML context) {
        System.out.println("visitLabeledStatement");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitLambdaExpression(LambdaExpressionTree arg0, SourceXML context) {
        System.out.println("visitLambdaExpression");
        return context;
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

        case LONG_LITERAL:
            context.number(value.toString() + "L");
            break;

        case NULL_LITERAL:
        case BOOLEAN_LITERAL:
            context.reserved(String.valueOf(value));
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
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitMemberReference(MemberReferenceTree arg0, SourceXML context) {
        System.out.println("visitMemberReference");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitMemberSelect(MemberSelectTree select, SourceXML context) {
        context = traceLine(select, context);

        select.getExpression().accept(this, context);
        context.text(".").memberAccess(select.getIdentifier().toString());

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitMethod(MethodTree executor, SourceXML context) {
        traceLine(executor, context);

        // ===========================================
        // Annotations and Modifiers
        // ===========================================
        visitModifiers(executor.getModifiers(), latestLine);

        // ===========================================
        // Type Parameter Declarations
        // ===========================================
        latestLine.typeParams(executor.getTypeParameters(), true);

        // ===========================================
        // Return Type
        // ===========================================
        Tree returnType = executor.getReturnType();

        if (returnType != null) {
            returnType.accept(this, latestLine).space();
        }

        // ===========================================
        // Name And Parameters
        // ===========================================
        String name = executor.getName().toString();

        if (name.equals("<init>")) {
            name = classNames.peekLast();
        }

        statement.start();
        latestLine.memberDeclare(name).text("(").join(executor.getParameters()).text(")");
        statement.end(false);

        // ===========================================
        // Throws
        // ===========================================
        List<? extends ExpressionTree> exceptions = executor.getThrows();

        if (!exceptions.isEmpty()) {
            latestLine.space().reserved("throws").space().join(exceptions);
        }

        // ===========================================
        // Default Value for Annotation
        // ===========================================
        Tree defaultValue = executor.getDefaultValue();

        if (defaultValue != null) {
            latestLine.space().reserved("default").space();
            defaultValue.accept(this, latestLine);
        }

        // ===========================================
        // Body
        // ===========================================
        BlockTree body = executor.getBody();

        if (body == null) {
            // abstract
            latestLine.semiColon();
        } else {
            // concreat
            body.accept(this, latestLine);
        }

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitMethodInvocation(MethodInvocationTree invoke, SourceXML context) {
        statement.start();
        traceLine(invoke, context);

        JCMethodInvocation tree = (JCMethodInvocation) invoke;
        List<? extends Tree> types = invoke.getTypeArguments();

        if (!types.isEmpty()) {
            if (tree.meth.hasTag(SELECT)) {
                JCFieldAccess left = (JCFieldAccess) tree.meth;
                context = left.selected.accept(this, context);
                context.text(".").typeParams(types, true).text(left.name.toString());
            } else {
                context.typeParams(types, true).visit(tree.meth);
            }
        } else {
            tree.meth.accept(this, context);
        }

        context.text("(").join(invoke.getArguments()).text(")");

        statement.end(true);
        return context;
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
        List<? extends AnnotationTree> annotations = modifiers.getAnnotations();

        if (!annotations.isEmpty()) {
            for (AnnotationTree tree : annotations) {
                context = visitAnnotation(tree, context);

                if (1 < statement.expressionNestLevel) {
                    context.space();
                }
            }

            if (statement.expressionNestLevel <= 1) {
                context = startNewLine();
            }
        }

        // ===========================================
        // Modifiers
        // ===========================================
        Set<Modifier> flags = modifiers.getFlags();

        for (Modifier modifier : MODIFIE_ORDER) {
            if (flags.contains(modifier)) {
                context.reserved(modifier.name().toLowerCase()).space();
            }
        }

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitNewArray(NewArrayTree array, SourceXML context) {
        statement.start();
        traceLine(array, context);

        context.reserved("new").space();
        array.getType().accept(this, context);
        context.text("[").join(array.getDimensions()).text("]");

        statement.end(true);
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitNewClass(NewClassTree clazz, SourceXML context) {
        statement.start();
        traceLine(clazz, context);

        // check enclosing class
        ExpressionTree enclosing = clazz.getEnclosingExpression();

        if (enclosing != null) {
            enclosing.accept(this, context);
            context.text(".");
        }

        context.reserved("new")
                .space()
                .typeParams(clazz.getTypeArguments(), true)
                .visit(clazz.getIdentifier())
                .text("(")
                .join(clazz.getArguments())
                .text(")");

        ClassTree body = clazz.getClassBody();

        if (body != null) {
            body.accept(this, context);
        }

        statement.end(true);
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitOther(Tree arg0, SourceXML context) {
        System.out.println("visitOther");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitParameterizedType(ParameterizedTypeTree type, SourceXML context) {
        traceLine(type, context);

        context.visit(type.getType()).typeParams(type.getTypeArguments(), true);

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitParenthesized(ParenthesizedTree tree, SourceXML context) {
        traceLine(tree, context);

        latestLine.text("(").visit(tree.getExpression()).text(")");

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitPrimitiveType(PrimitiveTypeTree primitive, SourceXML context) {
        traceLine(primitive, context);

        TypeKind kind = primitive.getPrimitiveTypeKind();

        switch (kind) {
        case BOOLEAN:
        case BYTE:
        case CHAR:
        case DOUBLE:
        case FLOAT:
        case INT:
        case LONG:
        case NULL:
        case SHORT:
        case VOID:
            context.reserved(kind.name().toLowerCase());
            break;

        default:
            throw new Error(kind.toString());
        }

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitReturn(ReturnTree tree, SourceXML context) {
        statement.start();
        context = traceLine(tree, context);

        context.reserved("return").space();
        tree.getExpression().accept(this, context);

        statement.end(true);
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitSwitch(SwitchTree arg0, SourceXML context) {
        System.out.println("visitSwitch");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitSynchronized(SynchronizedTree arg0, SourceXML context) {
        System.out.println("visitSynchronized");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitThrow(ThrowTree tree, SourceXML context) {
        traceLine(tree, context);

        statement.start();
        latestLine.reserved("throw").space().visit(tree.getExpression());
        statement.end(true);

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitTry(TryTree tree, SourceXML context) {
        traceLine(tree, context);

        latestLine.reserved("try").visit(tree.getBlock());

        for (CatchTree catchTree : tree.getCatches()) {
            statement.start();
            latestLine.space().reserved("catch").space().text("(").visit(catchTree.getParameter()).text(")");
            statement.end(false);

            catchTree.getBlock().accept(this, latestLine);
        }

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitTypeCast(TypeCastTree cast, SourceXML context) {
        context = traceLine(cast, context);

        return context.text("(").visit(cast.getType()).text(")").space().visit(cast.getExpression());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitTypeParameter(TypeParameterTree param, SourceXML context) {
        context = traceLine(param, context);

        context.join(null, param.getAnnotations(), null, " ").type(param.getName().toString());

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitUnary(UnaryTree unary, SourceXML context) {
        statement.start();
        ExpressionTree expression = unary.getExpression();

        switch (unary.getKind()) {
        case POSTFIX_INCREMENT:
            expression.accept(this, context).text("++");
            break;

        case POSTFIX_DECREMENT:
            expression.accept(this, context).text("--");
            break;

        case PREFIX_INCREMENT:
            expression.accept(this, context.text("++"));
            break;

        case PREFIX_DECREMENT:
            expression.accept(this, context.text("--"));
            break;

        case LOGICAL_COMPLEMENT:
            expression.accept(this, context.text("!"));
            break;

        case BITWISE_COMPLEMENT:
            expression.accept(this, context.text("~"));
            break;

        case UNARY_PLUS:
            expression.accept(this, context.text("+"));
            break;

        case UNARY_MINUS:
            expression.accept(this, context.text("-"));
            break;

        default:
            throw new Error(unary.getKind().toString());
        }

        statement.end(true);
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitUnionType(UnionTypeTree arg0, SourceXML context) {
        System.out.println("visitUnionType");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitVariable(VariableTree variable, SourceXML context) {
        context = traceLine(variable, context);

        if (isEnum(variable)) {
            latestLine.variable(variable.getName().toString());
            NewClassTree constructor = (NewClassTree) variable.getInitializer();
            List<? extends ExpressionTree> arguments = constructor.getArguments();

            if (!arguments.isEmpty()) {
                context.text("(").join(arguments).text(")");
            }

            NewClassTree initializer = (NewClassTree) variable.getInitializer();
            ClassTree body = initializer.getClassBody();

            if (body != null) {
                writeBlock(body.getMembers(), context);
            }
        } else {
            statement.start();

            // Annotations and Modifiers
            visitModifiers(variable.getModifiers(), context);

            // Type
            variable.getType().accept(this, latestLine).space();

            // Name
            latestLine.variable(variable.getName().toString());

            // Value
            ExpressionTree initializer = variable.getInitializer();

            if (initializer != null) {
                latestLine.space().text("=").space();
                initializer.accept(this, latestLine);
            }

            statement.end(true);
        }

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitWhileLoop(WhileLoopTree loop, SourceXML context) {
        context = traceLine(loop, context);

        statement.start();
        context.reserved("while").space().visit(loop.getCondition());
        statement.end(false);

        context.visit(loop.getStatement());

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitWildcard(WildcardTree arg0, SourceXML context) {
        System.out.println("visitWildcard");
        return context;
    }

    /**
     * <p>
     * Create new line.
     * </p>
     */
    private SourceXML startNewLine() {
        SourceXML newLine = new SourceXML(root.child("line").attr("n", logicalLine++), this);

        for (int i = 0; i < indentLevel; i++) {
            newLine.text(indent);
        }
        return this.latestLine = newLine;
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
            startNewLine().text(mapper.readLineFrom(logicalLine) + "☆");
        }
        return startNewLine();
    }

    private SourceXML writeBlock(List<? extends Tree> trees, SourceXML context) {
        context.space().text("{");
        indentLevel++;

        for (Tree tree : trees) {
            context = tree.accept(this, context);
        }
        indentLevel--;
        return startNewLine().text("}");
    }

    /**
     * Helper method to check enum.
     * 
     * @param tree
     * @return
     */
    private boolean isEnum(Tree tree) {
        return tree instanceof JCVariableDecl && (((JCVariableDecl) tree).mods.flags & ENUM) != 0;
    }

    /**
     * @version 2014/08/02 23:27:08
     */
    private class EnumConstantsInfo {

        /** The flag whether this class is enum or not. */
        private final boolean inEnum;

        /** The flag whether fisrt enum constant was declared or not. */
        private boolean constatntDeclared;

        /** The flag whether constants related process was completed or not. */
        private boolean completed;

        /** The latest declared line number. */
        private int line;

        /** The latest declared line. */
        private SourceXML lineXML;

        /**
         * @param kind
         */
        private EnumConstantsInfo(Kind kind) {
            inEnum = kind == Kind.ENUM;
        }

        /**
         * <p>
         * Process constant location.
         * </p>
         * 
         * @param tree
         * @return
         */
        private void processNonFirstConstant(Tree tree) {
            if (!inEnum || !isEnum(tree)) {
                return;
            }

            lineXML = latestLine;

            if (!constatntDeclared) {
                constatntDeclared = true;
                return;
            }

            // write separator
            latestLine.text(",");

            if (line == mapper.getLine(tree)) {
                latestLine.space();
            }
        }

        /**
         * 
         */
        private void complete() {
            if (inEnum && constatntDeclared && !completed) {
                completed = true;

                lineXML.semiColon();
            }
        }

        /**
         * <p>
         * Check constant location.
         * </p>
         * 
         * @param tree
         * @return
         */
        private void completeIfAllConstantsDeclared(Tree tree) {
            if (!inEnum) {
                return;
            }

            if (!constatntDeclared) {
                return;
            }

            if (!isEnum(tree)) {
                complete();
            } else {
                line = mapper.getLine(tree);
                lineXML = latestLine;
            }
        }
    }

    /**
     * @version 2014/08/03 11:59:03
     */
    private class Statement {

        /** The current nest level. */
        private int expressionNestLevel = 0;

        /**
         * <p>
         * Mark start of statment.
         * </p>
         */
        private void start() {
            expressionNestLevel++;
        }

        /**
         * <p>
         * Write end of statment symbol if needed.
         * </p>
         */
        private void end(boolean writeSemicolon) {
            if (writeSemicolon && expressionNestLevel == 1) {
                latestLine.semiColon();
            }
            expressionNestLevel--;
        }
    }
}
