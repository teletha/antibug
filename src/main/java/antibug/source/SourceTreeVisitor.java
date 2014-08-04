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
import javax.lang.model.element.Name;
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
            Modifier.ABSTRACT, Modifier.STATIC, Modifier.FINAL, Modifier.STRICTFP, Modifier.DEFAULT,
            Modifier.TRANSIENT, Modifier.VOLATILE, Modifier.SYNCHRONIZED, Modifier.NATIVE};

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
    public SourceXML visitArrayAccess(ArrayAccessTree array, SourceXML context) {
        context = traceLine(array, context);

        statement.start();
        context = context.visit(array.getExpression()).text("[").visit(array.getIndex()).text("]");
        statement.end(true);

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitArrayType(ArrayTypeTree array, SourceXML context) {
        traceLine(array, context);

        context.visit(array.getType()).text("[]");

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitAssert(AssertTree assertion, SourceXML context) {
        context = traceLine(assertion, context);

        statement.start();
        context.reserved("assert").space().visit(assertion.getCondition());

        ExpressionTree detail = assertion.getDetail();

        if (detail != null) {
            context.space().text(":").space().visit(detail);
        }
        statement.end(false);

        context.semiColon();

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
    public SourceXML visitBreak(BreakTree tree, SourceXML context) {
        context = traceLine(tree, context);

        context.reserved("break");

        Name label = tree.getLabel();

        if (label != null) {
            context.space().text(label.toString());
        }
        context.semiColon();

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitCase(CaseTree tree, SourceXML context) {
        indentLevel--;
        context = traceLine(tree, context);

        ExpressionTree value = tree.getExpression();

        if (value == null) {
            // default
            context = context.reserved("default").text(":");
        } else {
            // case
            context = context.reserved("case").space().visit(tree.getExpression()).text(":");
        }

        indentLevel++;
        return context.visit(tree.getStatements());
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
            latestLine.space().reserved("extends").space().visit(extend);
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
    public SourceXML visitContinue(ContinueTree tree, SourceXML context) {
        context = traceLine(tree, context);

        context.reserved("continue");

        Name label = tree.getLabel();

        if (label != null) {
            context.space().text(label.toString());
        }
        context.semiColon();

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

        statement.start();
        context = context.reserved("for")
                .space()
                .text("(")
                .visit(loop.getVariable())
                .space()
                .text(":")
                .space()
                .visit(loop.getExpression())
                .text(")");
        statement.end(false);

        return context.visit(loop.getStatement());
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

        return context.visit(expression.getExpression());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitForLoop(ForLoopTree loop, SourceXML context) {
        context = traceLine(loop, context);

        statement.start();
        context = context.reserved("for")
                .space()
                .text("(")
                .join(loop.getInitializer())
                .semiColon()
                .space()
                .visit(loop.getCondition())
                .semiColon()
                .join(" ", loop.getUpdate(), ",", null)
                .text(")");
        statement.end(false);

        return context.visit(loop.getStatement());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitIdentifier(IdentifierTree identifier, SourceXML context) {
        String value = identifier.getName().toString();

        if (value.equals("this")) {
            context.reserved("this");
        } else if (value.equals("super")) {
            context.reserved("super");
        } else {
            context.type(value);
        }

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
        latestLine.reserved("if").space().visit(tree.getCondition());
        statement.end(false);

        // Then
        latestLine.visit(tree.getThenStatement());

        StatementTree elseStatement = tree.getElseStatement();

        if (elseStatement != null) {
            latestLine.space().reserved("else").visit(elseStatement);
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
    public SourceXML visitLabeledStatement(LabeledStatementTree label, SourceXML context) {
        context = traceLine(label, context);

        return context.text(label.getLabel().toString()).text(":").space().visit(label.getStatement());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitLambdaExpression(LambdaExpressionTree lambda, SourceXML context) {
        context = traceLine(lambda, context);

        List<? extends VariableTree> params = lambda.getParameters();

        if (params.size() == 1) {
            context = context.visit(params.get(0));
        } else {
            context = context.text("(").join(params).text(")");
        }

        statement.store();
        context = context.space().text("->").visit(lambda.getBody());
        statement.restore();

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
            context.string(escape(value.toString()));
            break;

        case INT_LITERAL:
            context.number(value.toString());
            break;

        case LONG_LITERAL:
            context.number(value.toString() + "L");
            break;

        case FLOAT_LITERAL:
            context.number(value.toString() + "F");
            break;

        case DOUBLE_LITERAL:
            context.number(value.toString() + "D");
            break;

        case NULL_LITERAL:
        case BOOLEAN_LITERAL:
            context.reserved(String.valueOf(value));
            break;

        default:
            throw new Error(literal.getKind().toString());
        }
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitMemberReference(MemberReferenceTree refer, SourceXML context) {
        context = traceLine(refer, context);

        String name;

        switch (refer.getMode()) {
        case INVOKE:
            name = refer.getName().toString();
            break;

        case NEW:
            name = "new";
            break;

        default:
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error(refer.getMode().toString());
        }

        return context.visit(refer.getQualifierExpression())
                .text("::")
                .typeParams(refer.getTypeArguments(), true)
                .text(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitMemberSelect(MemberSelectTree select, SourceXML context) {
        context = traceLine(select, context);

        String member = select.getIdentifier().toString();

        if (member.equals("class")) {
            return context.visit(select.getExpression()).text(".").reserved("class");
        } else {
            return context.visit(select.getExpression()).text(".").memberAccess(member);
        }
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
            context.visit(tree.meth);
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

        Tree type = array.getType();

        if (type == null) {
            // shorthand initializer (e.g. array = {1, 2, 3})
            context.text("{").join(array.getInitializers()).text("}");
        } else {
            // new initializer (e.g. array = new int[] {1, 2, 3})
            context.join(array.getAnnotations());

            context.reserved("new").space();
            array.getType().accept(this, context);
            context.text("[").join(array.getDimensions()).text("]");

            List<? extends ExpressionTree> initializers = array.getInitializers();

            if (initializers != null && !initializers.isEmpty()) {
                context.space().join("{", initializers, "}");
            }
        }

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

        return context.visit(type.getType()).typeParams(type.getTypeArguments(), false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitParenthesized(ParenthesizedTree tree, SourceXML context) {
        context = traceLine(tree, context);

        return context.text("(").visit(tree.getExpression()).text(")");
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
    public SourceXML visitSwitch(SwitchTree tree, SourceXML context) {
        context = traceLine(tree, context);

        context.reserved("switch").space().visit(tree.getExpression());
        writeBlock(tree.getCases(), context);

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitSynchronized(SynchronizedTree tree, SourceXML context) {
        context = traceLine(tree, context);

        context.reserved("synchronized").space().visit(tree.getExpression()).visit(tree.getBlock());

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

            latestLine.visit(catchTree.getBlock());
        }

        BlockTree finallyTree = tree.getFinallyBlock();

        if (finallyTree != null) {
            latestLine.space().reserved("finally").visit(finallyTree);
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

        List<? extends Tree> bounds = param.getBounds();

        if (!bounds.isEmpty()) {
            context = context.space().reserved("extends").space().join(null, bounds, " &", null);
        }

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
    public SourceXML visitUnionType(UnionTypeTree tree, SourceXML context) {
        context = traceLine(tree, context);

        context.join(null, tree.getTypeAlternatives(), " |", null);

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
            if (isVararg(variable)) {
                ArrayTypeTree array = (ArrayTypeTree) variable.getType();
                latestLine.visit(array.getType()).text("...").space();
            } else {
                Tree type = variable.getType();

                if (type != null) {
                    latestLine.visit(type).space();
                }
            }

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
    public SourceXML visitWildcard(WildcardTree wildcard, SourceXML context) {
        context = traceLine(wildcard, context);

        context = context.text("?");

        Tree bound = wildcard.getBound();

        if (bound != null) {
            String keyword = "";

            switch (wildcard.getKind()) {
            case EXTENDS_WILDCARD:
                keyword = "extends";
                break;

            case SUPER_WILDCARD:
                keyword = "super";
                break;

            default:
                // If this exception will be thrown, it is bug of this program. So we must rethrow
                // the wrapped error in here.
                throw new Error(wildcard.getKind().toString());
            }
            context = context.space().reserved(keyword).space().visit(bound);
        }

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
     * Helper method to check vararg.
     * 
     * @param tree
     * @return
     */
    private boolean isVararg(VariableTree tree) {
        return tree instanceof JCVariableDecl && (((JCVariableDecl) tree).mods.flags & VARARGS) != 0;
    }

    /**
     * <p>
     * Escape quote.
     * </p>
     * 
     * @param value
     * @return
     */
    private String escape(String value) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            switch (c) {
            case '"':
                builder.append("\\\"");
                break;

            default:
                builder.append(c);
                break;
            }
        }
        return builder.toString();
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

        /** The nest level recoder. */
        private Deque<Integer> levels = new ArrayDeque();

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

        /**
         * 
         */
        private void store() {
            levels.addLast(expressionNestLevel);
            expressionNestLevel = 0;
        }

        /**
         * 
         */
        private void restore() {
            expressionNestLevel = levels.pollLast();
        }
    }
}
