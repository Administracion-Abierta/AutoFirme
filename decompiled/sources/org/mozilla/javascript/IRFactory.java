package org.mozilla.javascript;

import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.mozilla.javascript.ast.ArrayComprehension;
import org.mozilla.javascript.ast.ArrayComprehensionLoop;
import org.mozilla.javascript.ast.ArrayLiteral;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Block;
import org.mozilla.javascript.ast.BreakStatement;
import org.mozilla.javascript.ast.CatchClause;
import org.mozilla.javascript.ast.ConditionalExpression;
import org.mozilla.javascript.ast.ContinueStatement;
import org.mozilla.javascript.ast.DestructuringForm;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.ElementGet;
import org.mozilla.javascript.ast.EmptyExpression;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.ForInLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.GeneratorExpression;
import org.mozilla.javascript.ast.GeneratorExpressionLoop;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Jump;
import org.mozilla.javascript.ast.Label;
import org.mozilla.javascript.ast.LabeledStatement;
import org.mozilla.javascript.ast.LetNode;
import org.mozilla.javascript.ast.Loop;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NewExpression;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.ParenthesizedExpression;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.RegExpLiteral;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.SwitchCase;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.ThrowStatement;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.UnaryExpression;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.mozilla.javascript.ast.WhileLoop;
import org.mozilla.javascript.ast.WithStatement;
import org.mozilla.javascript.ast.XmlDotQuery;
import org.mozilla.javascript.ast.XmlElemRef;
import org.mozilla.javascript.ast.XmlExpression;
import org.mozilla.javascript.ast.XmlFragment;
import org.mozilla.javascript.ast.XmlLiteral;
import org.mozilla.javascript.ast.XmlMemberGet;
import org.mozilla.javascript.ast.XmlPropRef;
import org.mozilla.javascript.ast.XmlRef;
import org.mozilla.javascript.ast.XmlString;
import org.mozilla.javascript.ast.Yield;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/IRFactory.class */
public final class IRFactory extends Parser {
    private static final int LOOP_DO_WHILE = 0;
    private static final int LOOP_WHILE = 1;
    private static final int LOOP_FOR = 2;
    private static final int ALWAYS_TRUE_BOOLEAN = 1;
    private static final int ALWAYS_FALSE_BOOLEAN = -1;
    private Decompiler decompiler;

    public IRFactory() {
        this.decompiler = new Decompiler();
    }

    public IRFactory(CompilerEnvirons env) {
        this(env, env.getErrorReporter());
    }

    public IRFactory(CompilerEnvirons env, ErrorReporter errorReporter) {
        super(env, errorReporter);
        this.decompiler = new Decompiler();
    }

    public ScriptNode transformTree(AstRoot root) {
        this.currentScriptOrFn = root;
        this.inUseStrictDirective = root.isInStrictMode();
        int sourceStartOffset = this.decompiler.getCurrentOffset();
        ScriptNode script = (ScriptNode) transform(root);
        int sourceEndOffset = this.decompiler.getCurrentOffset();
        script.setEncodedSourceBounds(sourceStartOffset, sourceEndOffset);
        if (this.compilerEnv.isGeneratingSource()) {
            script.setEncodedSource(this.decompiler.getEncodedSource());
        }
        this.decompiler = null;
        return script;
    }

    public Node transform(AstNode node) {
        switch (node.getType()) {
            case 4:
                return transformReturn((ReturnStatement) node);
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 31:
            case 32:
            case 34:
            case 35:
            case 37:
            case 46:
            case 47:
            case 49:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 111:
            case 112:
            case 114:
            case 116:
            case 117:
            case 123:
            case 125:
            case 126:
            case 127:
            case 128:
            case 131:
            case 132:
            case 133:
            case 134:
            case 135:
            case 136:
            case 138:
            case 139:
            case 140:
            case 141:
            case 142:
            case 143:
            case 144:
            case 145:
            case 146:
            case 147:
            case 148:
            case 149:
            case 150:
            case 151:
            case 152:
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 159:
            case 160:
            case 162:
            case 164:
            case 165:
            default:
                if (node instanceof ExpressionStatement) {
                    return transformExprStmt((ExpressionStatement) node);
                }
                if (node instanceof Assignment) {
                    return transformAssignment((Assignment) node);
                }
                if (node instanceof UnaryExpression) {
                    return transformUnary((UnaryExpression) node);
                }
                if (node instanceof XmlMemberGet) {
                    return transformXmlMemberGet((XmlMemberGet) node);
                }
                if (node instanceof InfixExpression) {
                    return transformInfix((InfixExpression) node);
                }
                if (node instanceof VariableDeclaration) {
                    return transformVariables((VariableDeclaration) node);
                }
                if (node instanceof ParenthesizedExpression) {
                    return transformParenExpr((ParenthesizedExpression) node);
                }
                if (node instanceof LabeledStatement) {
                    return transformLabeledStatement((LabeledStatement) node);
                }
                if (node instanceof LetNode) {
                    return transformLetNode((LetNode) node);
                }
                if (node instanceof XmlRef) {
                    return transformXmlRef((XmlRef) node);
                }
                if (node instanceof XmlLiteral) {
                    return transformXmlLiteral((XmlLiteral) node);
                }
                throw new IllegalArgumentException("Can't transform: " + node);
            case 30:
                return transformNewExpr((NewExpression) node);
            case 33:
                return transformPropertyGet((PropertyGet) node);
            case 36:
                return transformElementGet((ElementGet) node);
            case 38:
                return transformFunctionCall((FunctionCall) node);
            case 39:
                return transformName((Name) node);
            case 40:
                return transformNumber((NumberLiteral) node);
            case 41:
                return transformString((StringLiteral) node);
            case 42:
            case 43:
            case 44:
            case 45:
            case 161:
                return transformLiteral(node);
            case 48:
                return transformRegExp((RegExpLiteral) node);
            case 50:
                return transformThrow((ThrowStatement) node);
            case 66:
                return transformArrayLiteral((ArrayLiteral) node);
            case 67:
                return transformObjectLiteral((ObjectLiteral) node);
            case 73:
            case 166:
                return transformYield((Yield) node);
            case 82:
                return transformTry((TryStatement) node);
            case 103:
                return transformCondExpr((ConditionalExpression) node);
            case 110:
                return transformFunction((FunctionNode) node);
            case 113:
                return transformIf((IfStatement) node);
            case 115:
                return transformSwitch((SwitchStatement) node);
            case 118:
                return transformWhileLoop((WhileLoop) node);
            case 119:
                return transformDoLoop((DoLoop) node);
            case 120:
                if (node instanceof ForInLoop) {
                    return transformForInLoop((ForInLoop) node);
                }
                return transformForLoop((ForLoop) node);
            case 121:
                return transformBreak((BreakStatement) node);
            case 122:
                return transformContinue((ContinueStatement) node);
            case 124:
                return transformWith((WithStatement) node);
            case 129:
                return node;
            case 130:
                return transformBlock(node);
            case 137:
                return transformScript((ScriptNode) node);
            case 158:
                return transformArrayComp((ArrayComprehension) node);
            case 163:
                return transformGenExpr((GeneratorExpression) node);
        }
    }

    private Node transformArrayComp(ArrayComprehension node) {
        int lineno = node.getLineno();
        Scope scopeNode = createScopeNode(158, lineno);
        String arrayName = this.currentScriptOrFn.getNextTempName();
        pushScope(scopeNode);
        try {
            defineSymbol(154, arrayName, false);
            Node block = new Node(130, lineno);
            Node newArray = createCallOrNew(30, createName("Array"));
            Node init = new Node(134, createAssignment(91, createName(arrayName), newArray), lineno);
            block.addChildToBack(init);
            block.addChildToBack(arrayCompTransformHelper(node, arrayName));
            scopeNode.addChildToBack(block);
            scopeNode.addChildToBack(createName(arrayName));
            return scopeNode;
        } finally {
            popScope();
        }
    }

    private Node arrayCompTransformHelper(ArrayComprehension node, String arrayName) {
        String name;
        this.decompiler.addToken(84);
        int lineno = node.getLineno();
        Node expr = transform(node.getResult());
        List<ArrayComprehensionLoop> loops = node.getLoops();
        int numLoops = loops.size();
        Node[] iterators = new Node[numLoops];
        Node[] iteratedObjs = new Node[numLoops];
        for (int i = 0; i < numLoops; i++) {
            ArrayComprehensionLoop acl = loops.get(i);
            this.decompiler.addName(" ");
            this.decompiler.addToken(120);
            if (acl.isForEach()) {
                this.decompiler.addName("each ");
            }
            this.decompiler.addToken(88);
            AstNode iter = acl.getIterator();
            if (iter.getType() == 39) {
                name = iter.getString();
                this.decompiler.addName(name);
            } else {
                decompile(iter);
                name = this.currentScriptOrFn.getNextTempName();
                defineSymbol(88, name, false);
                expr = createBinary(90, createAssignment(91, iter, createName(name)), expr);
            }
            Node init = createName(name);
            defineSymbol(154, name, false);
            iterators[i] = init;
            if (acl.isForOf()) {
                this.decompiler.addName("of ");
            } else {
                this.decompiler.addToken(52);
            }
            iteratedObjs[i] = transform(acl.getIteratedObject());
            this.decompiler.addToken(89);
        }
        Node call = createCallOrNew(38, createPropertyGet(createName(arrayName), null, "push", 0));
        Node body = new Node(134, call, lineno);
        if (node.getFilter() != null) {
            this.decompiler.addName(" ");
            this.decompiler.addToken(113);
            this.decompiler.addToken(88);
            body = createIf(transform(node.getFilter()), body, null, lineno);
            this.decompiler.addToken(89);
        }
        int pushed = 0;
        try {
            for (int i2 = numLoops - 1; i2 >= 0; i2--) {
                ArrayComprehensionLoop acl2 = loops.get(i2);
                Scope loop = createLoopNode(null, acl2.getLineno());
                pushScope(loop);
                pushed++;
                body = createForIn(154, loop, iterators[i2], iteratedObjs[i2], body, acl2.isForEach(), acl2.isForOf());
            }
            for (int i3 = 0; i3 < pushed; i3++) {
                popScope();
            }
            this.decompiler.addToken(85);
            call.addChildToBack(expr);
            return body;
        } catch (Throwable th) {
            for (int i4 = 0; i4 < pushed; i4++) {
                popScope();
            }
            throw th;
        }
    }

    private Node transformArrayLiteral(ArrayLiteral node) {
        if (node.isDestructuring()) {
            return node;
        }
        this.decompiler.addToken(84);
        List<AstNode> elems = node.getElements();
        Node array = new Node(66);
        List<Integer> skipIndexes = null;
        for (int i = 0; i < elems.size(); i++) {
            AstNode elem = elems.get(i);
            if (elem.getType() != 129) {
                array.addChildToBack(transform(elem));
            } else {
                if (skipIndexes == null) {
                    skipIndexes = new ArrayList<>();
                }
                skipIndexes.add(Integer.valueOf(i));
            }
            if (i < elems.size() - 1) {
                this.decompiler.addToken(90);
            }
        }
        this.decompiler.addToken(85);
        array.putIntProp(21, node.getDestructuringLength());
        if (skipIndexes != null) {
            int[] skips = new int[skipIndexes.size()];
            for (int i2 = 0; i2 < skipIndexes.size(); i2++) {
                skips[i2] = skipIndexes.get(i2).intValue();
            }
            array.putProp(11, skips);
        }
        return array;
    }

    private Node transformAssignment(Assignment node) {
        Node target;
        AstNode left = removeParens(node.getLeft());
        if (isDestructuring(left)) {
            decompile(left);
            target = left;
        } else {
            target = transform(left);
        }
        this.decompiler.addToken(node.getType());
        return createAssignment(node.getType(), target, transform(node.getRight()));
    }

    private Node transformBlock(AstNode node) {
        if (node instanceof Scope) {
            pushScope((Scope) node);
        }
        try {
            List<Node> kids = new ArrayList<>();
            for (Node kid : node) {
                kids.add(transform((AstNode) kid));
            }
            node.removeChildren();
            for (Node kid2 : kids) {
                node.addChildToBack(kid2);
            }
            return node;
        } finally {
            if (node instanceof Scope) {
                popScope();
            }
        }
    }

    private Node transformBreak(BreakStatement node) {
        this.decompiler.addToken(121);
        if (node.getBreakLabel() != null) {
            this.decompiler.addName(node.getBreakLabel().getIdentifier());
        }
        this.decompiler.addEOL(83);
        return node;
    }

    private Node transformCondExpr(ConditionalExpression node) {
        Node test = transform(node.getTestExpression());
        this.decompiler.addToken(103);
        Node ifTrue = transform(node.getTrueExpression());
        this.decompiler.addToken(104);
        Node ifFalse = transform(node.getFalseExpression());
        return createCondExpr(test, ifTrue, ifFalse);
    }

    private Node transformContinue(ContinueStatement node) {
        this.decompiler.addToken(122);
        if (node.getLabel() != null) {
            this.decompiler.addName(node.getLabel().getIdentifier());
        }
        this.decompiler.addEOL(83);
        return node;
    }

    private Node transformDoLoop(DoLoop loop) {
        loop.setType(133);
        pushScope(loop);
        try {
            this.decompiler.addToken(119);
            this.decompiler.addEOL(86);
            Node body = transform(loop.getBody());
            this.decompiler.addToken(87);
            this.decompiler.addToken(118);
            this.decompiler.addToken(88);
            Node cond = transform(loop.getCondition());
            this.decompiler.addToken(89);
            this.decompiler.addEOL(83);
            return createLoop(loop, 0, body, cond, null, null);
        } finally {
            popScope();
        }
    }

    private Node transformElementGet(ElementGet node) {
        Node target = transform(node.getTarget());
        this.decompiler.addToken(84);
        Node element = transform(node.getElement());
        this.decompiler.addToken(85);
        return new Node(36, target, element);
    }

    private Node transformExprStmt(ExpressionStatement node) {
        Node expr = transform(node.getExpression());
        this.decompiler.addEOL(83);
        return new Node(node.getType(), expr, node.getLineno());
    }

    private Node transformForInLoop(ForInLoop loop) {
        this.decompiler.addToken(120);
        if (loop.isForEach()) {
            this.decompiler.addName("each ");
        }
        this.decompiler.addToken(88);
        loop.setType(133);
        pushScope(loop);
        try {
            int declType = -1;
            AstNode iter = loop.getIterator();
            if (iter instanceof VariableDeclaration) {
                declType = ((VariableDeclaration) iter).getType();
            }
            Node lhs = transform(iter);
            if (loop.isForOf()) {
                this.decompiler.addName("of ");
            } else {
                this.decompiler.addToken(52);
            }
            Node obj = transform(loop.getIteratedObject());
            this.decompiler.addToken(89);
            this.decompiler.addEOL(86);
            Node body = transform(loop.getBody());
            this.decompiler.addEOL(87);
            return createForIn(declType, loop, lhs, obj, body, loop.isForEach(), loop.isForOf());
        } finally {
            popScope();
        }
    }

    private Node transformForLoop(ForLoop loop) {
        this.decompiler.addToken(120);
        this.decompiler.addToken(88);
        loop.setType(133);
        Scope savedScope = this.currentScope;
        this.currentScope = loop;
        try {
            Node init = transform(loop.getInitializer());
            this.decompiler.addToken(83);
            Node test = transform(loop.getCondition());
            this.decompiler.addToken(83);
            Node incr = transform(loop.getIncrement());
            this.decompiler.addToken(89);
            this.decompiler.addEOL(86);
            Node body = transform(loop.getBody());
            this.decompiler.addEOL(87);
            return createFor(loop, init, test, incr, body);
        } finally {
            this.currentScope = savedScope;
        }
    }

    private Node transformFunction(FunctionNode fn) {
        int functionType = fn.getFunctionType();
        int start = this.decompiler.markFunctionStart(functionType);
        Node mexpr = decompileFunctionHeader(fn);
        int index = this.currentScriptOrFn.addFunction(fn);
        Parser.PerFunctionVariables savedVars = new Parser.PerFunctionVariables(fn);
        try {
            Node destructuring = (Node) fn.getProp(23);
            fn.removeProp(23);
            int lineno = fn.getBody().getLineno();
            this.nestingOfFunction++;
            Node body = transform(fn.getBody());
            if (!fn.isExpressionClosure()) {
                this.decompiler.addToken(87);
            }
            fn.setEncodedSourceBounds(start, this.decompiler.markFunctionEnd(start));
            if (functionType != 2 && !fn.isExpressionClosure()) {
                this.decompiler.addToken(1);
            }
            if (destructuring != null) {
                body.addChildToFront(new Node(134, destructuring, lineno));
            }
            int syntheticType = fn.getFunctionType();
            Node pn = initFunction(fn, index, body, syntheticType);
            if (mexpr != null) {
                pn = createAssignment(91, mexpr, pn);
                if (syntheticType != 2) {
                    pn = createExprStatementNoReturn(pn, fn.getLineno());
                }
            }
            return pn;
        } finally {
            this.nestingOfFunction--;
            savedVars.restore();
        }
    }

    private Node transformFunctionCall(FunctionCall node) {
        Node call = createCallOrNew(38, transform(node.getTarget()));
        call.setLineno(node.getLineno());
        this.decompiler.addToken(88);
        List<AstNode> args = node.getArguments();
        for (int i = 0; i < args.size(); i++) {
            AstNode arg = args.get(i);
            call.addChildToBack(transform(arg));
            if (i < args.size() - 1) {
                this.decompiler.addToken(90);
            }
        }
        this.decompiler.addToken(89);
        return call;
    }

    private Node transformGenExpr(GeneratorExpression node) {
        FunctionNode fn = new FunctionNode();
        fn.setSourceName(this.currentScriptOrFn.getNextTempName());
        fn.setIsGenerator();
        fn.setFunctionType(2);
        fn.setRequiresActivation();
        int functionType = fn.getFunctionType();
        int start = this.decompiler.markFunctionStart(functionType);
        Node mexpr = decompileFunctionHeader(fn);
        int index = this.currentScriptOrFn.addFunction(fn);
        Parser.PerFunctionVariables savedVars = new Parser.PerFunctionVariables(fn);
        try {
            Node destructuring = (Node) fn.getProp(23);
            fn.removeProp(23);
            int lineno = node.lineno;
            this.nestingOfFunction++;
            Node body = genExprTransformHelper(node);
            if (!fn.isExpressionClosure()) {
                this.decompiler.addToken(87);
            }
            fn.setEncodedSourceBounds(start, this.decompiler.markFunctionEnd(start));
            if (functionType != 2 && !fn.isExpressionClosure()) {
                this.decompiler.addToken(1);
            }
            if (destructuring != null) {
                body.addChildToFront(new Node(134, destructuring, lineno));
            }
            int syntheticType = fn.getFunctionType();
            Node pn = initFunction(fn, index, body, syntheticType);
            if (mexpr != null) {
                pn = createAssignment(91, mexpr, pn);
                if (syntheticType != 2) {
                    pn = createExprStatementNoReturn(pn, fn.getLineno());
                }
            }
            this.nestingOfFunction--;
            savedVars.restore();
            Node call = createCallOrNew(38, pn);
            call.setLineno(node.getLineno());
            this.decompiler.addToken(88);
            this.decompiler.addToken(89);
            return call;
        } catch (Throwable th) {
            this.nestingOfFunction--;
            savedVars.restore();
            throw th;
        }
    }

    private Node genExprTransformHelper(GeneratorExpression node) {
        String name;
        this.decompiler.addToken(88);
        int lineno = node.getLineno();
        Node expr = transform(node.getResult());
        List<GeneratorExpressionLoop> loops = node.getLoops();
        int numLoops = loops.size();
        Node[] iterators = new Node[numLoops];
        Node[] iteratedObjs = new Node[numLoops];
        for (int i = 0; i < numLoops; i++) {
            GeneratorExpressionLoop acl = loops.get(i);
            this.decompiler.addName(" ");
            this.decompiler.addToken(120);
            this.decompiler.addToken(88);
            AstNode iter = acl.getIterator();
            if (iter.getType() == 39) {
                name = iter.getString();
                this.decompiler.addName(name);
            } else {
                decompile(iter);
                name = this.currentScriptOrFn.getNextTempName();
                defineSymbol(88, name, false);
                expr = createBinary(90, createAssignment(91, iter, createName(name)), expr);
            }
            Node init = createName(name);
            defineSymbol(154, name, false);
            iterators[i] = init;
            if (acl.isForOf()) {
                this.decompiler.addName("of ");
            } else {
                this.decompiler.addToken(52);
            }
            iteratedObjs[i] = transform(acl.getIteratedObject());
            this.decompiler.addToken(89);
        }
        Node yield = new Node(73, expr, node.getLineno());
        Node body = new Node(134, yield, lineno);
        if (node.getFilter() != null) {
            this.decompiler.addName(" ");
            this.decompiler.addToken(113);
            this.decompiler.addToken(88);
            body = createIf(transform(node.getFilter()), body, null, lineno);
            this.decompiler.addToken(89);
        }
        int pushed = 0;
        try {
            for (int i2 = numLoops - 1; i2 >= 0; i2--) {
                GeneratorExpressionLoop acl2 = loops.get(i2);
                Scope loop = createLoopNode(null, acl2.getLineno());
                pushScope(loop);
                pushed++;
                body = createForIn(154, loop, iterators[i2], iteratedObjs[i2], body, acl2.isForEach(), acl2.isForOf());
            }
            for (int i3 = 0; i3 < pushed; i3++) {
                popScope();
            }
            this.decompiler.addToken(89);
            return body;
        } catch (Throwable th) {
            for (int i4 = 0; i4 < pushed; i4++) {
                popScope();
            }
            throw th;
        }
    }

    private Node transformIf(IfStatement n) {
        this.decompiler.addToken(113);
        this.decompiler.addToken(88);
        Node cond = transform(n.getCondition());
        this.decompiler.addToken(89);
        this.decompiler.addEOL(86);
        Node ifTrue = transform(n.getThenPart());
        Node ifFalse = null;
        if (n.getElsePart() != null) {
            this.decompiler.addToken(87);
            this.decompiler.addToken(114);
            this.decompiler.addEOL(86);
            ifFalse = transform(n.getElsePart());
        }
        this.decompiler.addEOL(87);
        return createIf(cond, ifTrue, ifFalse, n.getLineno());
    }

    private Node transformInfix(InfixExpression node) {
        Node left = transform(node.getLeft());
        this.decompiler.addToken(node.getType());
        Node right = transform(node.getRight());
        if (node instanceof XmlDotQuery) {
            this.decompiler.addToken(89);
        }
        return createBinary(node.getType(), left, right);
    }

    private Node transformLabeledStatement(LabeledStatement ls) {
        Label label = ls.getFirstLabel();
        List<Label> labels = ls.getLabels();
        this.decompiler.addName(label.getName());
        if (labels.size() > 1) {
            for (Label lb : labels.subList(1, labels.size())) {
                this.decompiler.addEOL(104);
                this.decompiler.addName(lb.getName());
            }
        }
        if (ls.getStatement().getType() == 130) {
            this.decompiler.addToken(67);
            this.decompiler.addEOL(86);
        } else {
            this.decompiler.addEOL(104);
        }
        Node statement = transform(ls.getStatement());
        if (ls.getStatement().getType() == 130) {
            this.decompiler.addEOL(87);
        }
        Node breakTarget = Node.newTarget();
        Node block = new Node(130, label, statement, breakTarget);
        label.target = breakTarget;
        return block;
    }

    private Node transformLetNode(LetNode node) {
        pushScope(node);
        try {
            this.decompiler.addToken(154);
            this.decompiler.addToken(88);
            Node vars = transformVariableInitializers(node.getVariables());
            this.decompiler.addToken(89);
            node.addChildToBack(vars);
            boolean letExpr = node.getType() == 159;
            if (node.getBody() != null) {
                if (letExpr) {
                    this.decompiler.addName(" ");
                } else {
                    this.decompiler.addEOL(86);
                }
                node.addChildToBack(transform(node.getBody()));
                if (!letExpr) {
                    this.decompiler.addEOL(87);
                }
            }
            return node;
        } finally {
            popScope();
        }
    }

    private Node transformLiteral(AstNode node) {
        this.decompiler.addToken(node.getType());
        return node;
    }

    private Node transformName(Name node) {
        this.decompiler.addName(node.getIdentifier());
        return node;
    }

    private Node transformNewExpr(NewExpression node) {
        this.decompiler.addToken(30);
        Node nx = createCallOrNew(30, transform(node.getTarget()));
        nx.setLineno(node.getLineno());
        List<AstNode> args = node.getArguments();
        this.decompiler.addToken(88);
        for (int i = 0; i < args.size(); i++) {
            AstNode arg = args.get(i);
            nx.addChildToBack(transform(arg));
            if (i < args.size() - 1) {
                this.decompiler.addToken(90);
            }
        }
        this.decompiler.addToken(89);
        if (node.getInitializer() != null) {
            nx.addChildToBack(transformObjectLiteral(node.getInitializer()));
        }
        return nx;
    }

    private Node transformNumber(NumberLiteral node) {
        this.decompiler.addNumber(node.getNumber());
        return node;
    }

    private Node transformObjectLiteral(ObjectLiteral node) {
        Object[] properties;
        if (node.isDestructuring()) {
            return node;
        }
        this.decompiler.addToken(86);
        List<ObjectProperty> elems = node.getElements();
        Node object = new Node(67);
        if (elems.isEmpty()) {
            properties = ScriptRuntime.emptyArgs;
        } else {
            int size = elems.size();
            int i = 0;
            properties = new Object[size];
            for (ObjectProperty prop : elems) {
                if (prop.isGetterMethod()) {
                    this.decompiler.addToken(152);
                } else if (prop.isSetterMethod()) {
                    this.decompiler.addToken(153);
                } else if (prop.isNormalMethod()) {
                    this.decompiler.addToken(164);
                }
                int i2 = i;
                i++;
                properties[i2] = getPropKey(prop.getLeft());
                if (!prop.isMethod()) {
                    this.decompiler.addToken(67);
                }
                Node right = transform(prop.getRight());
                if (prop.isGetterMethod()) {
                    right = createUnary(152, right);
                } else if (prop.isSetterMethod()) {
                    right = createUnary(153, right);
                } else if (prop.isNormalMethod()) {
                    right = createUnary(164, right);
                }
                object.addChildToBack(right);
                if (i < size) {
                    this.decompiler.addToken(90);
                }
            }
        }
        this.decompiler.addToken(87);
        object.putProp(12, properties);
        return object;
    }

    private Object getPropKey(Node id) {
        Object key;
        if (id instanceof Name) {
            String s = ((Name) id).getIdentifier();
            this.decompiler.addName(s);
            key = ScriptRuntime.getIndexObject(s);
        } else if (id instanceof StringLiteral) {
            String s2 = ((StringLiteral) id).getValue();
            this.decompiler.addString(s2);
            key = ScriptRuntime.getIndexObject(s2);
        } else if (id instanceof NumberLiteral) {
            double n = ((NumberLiteral) id).getNumber();
            this.decompiler.addNumber(n);
            key = ScriptRuntime.getIndexObject(n);
        } else {
            throw Kit.codeBug();
        }
        return key;
    }

    private Node transformParenExpr(ParenthesizedExpression node) {
        AstNode expr = node.getExpression();
        this.decompiler.addToken(88);
        int count = 1;
        while (expr instanceof ParenthesizedExpression) {
            this.decompiler.addToken(88);
            count++;
            expr = ((ParenthesizedExpression) expr).getExpression();
        }
        Node result = transform(expr);
        for (int i = 0; i < count; i++) {
            this.decompiler.addToken(89);
        }
        result.putProp(19, Boolean.TRUE);
        return result;
    }

    private Node transformPropertyGet(PropertyGet node) {
        Node target = transform(node.getTarget());
        String name = node.getProperty().getIdentifier();
        this.decompiler.addToken(109);
        this.decompiler.addName(name);
        return createPropertyGet(target, null, name, 0);
    }

    private Node transformRegExp(RegExpLiteral node) {
        this.decompiler.addRegexp(node.getValue(), node.getFlags());
        this.currentScriptOrFn.addRegExp(node);
        return node;
    }

    private Node transformReturn(ReturnStatement node) {
        boolean expClosure = Boolean.TRUE.equals(node.getProp(25));
        boolean isArrow = Boolean.TRUE.equals(node.getProp(27));
        if (expClosure) {
            if (!isArrow) {
                this.decompiler.addName(" ");
            }
        } else {
            this.decompiler.addToken(4);
        }
        AstNode rv = node.getReturnValue();
        Node value = rv == null ? null : transform(rv);
        if (!expClosure) {
            this.decompiler.addEOL(83);
        }
        if (rv == null) {
            return new Node(4, node.getLineno());
        }
        return new Node(4, value, node.getLineno());
    }

    private Node transformScript(ScriptNode node) {
        this.decompiler.addToken(137);
        if (this.currentScope != null) {
            Kit.codeBug();
        }
        this.currentScope = node;
        Node body = new Node(130);
        for (Node kid : node) {
            body.addChildToBack(transform((AstNode) kid));
        }
        node.removeChildren();
        Node children = body.getFirstChild();
        if (children != null) {
            node.addChildrenToBack(children);
        }
        return node;
    }

    private Node transformString(StringLiteral node) {
        this.decompiler.addString(node.getValue());
        return Node.newString(node.getValue());
    }

    private Node transformSwitch(SwitchStatement node) {
        this.decompiler.addToken(115);
        this.decompiler.addToken(88);
        Node switchExpr = transform(node.getExpression());
        this.decompiler.addToken(89);
        node.addChildToBack(switchExpr);
        Node block = new Node(130, node, node.getLineno());
        this.decompiler.addEOL(86);
        for (SwitchCase sc : node.getCases()) {
            AstNode expr = sc.getExpression();
            Node caseExpr = null;
            if (expr != null) {
                this.decompiler.addToken(116);
                caseExpr = transform(expr);
            } else {
                this.decompiler.addToken(117);
            }
            this.decompiler.addEOL(104);
            List<AstNode> stmts = sc.getStatements();
            Node body = new Block();
            if (stmts != null) {
                for (AstNode kid : stmts) {
                    body.addChildToBack(transform(kid));
                }
            }
            addSwitchCase(block, caseExpr, body);
        }
        this.decompiler.addEOL(87);
        closeSwitch(block);
        return block;
    }

    private Node transformThrow(ThrowStatement node) {
        this.decompiler.addToken(50);
        Node value = transform(node.getExpression());
        this.decompiler.addEOL(83);
        return new Node(50, value, node.getLineno());
    }

    private Node transformTry(TryStatement node) {
        Node emptyExpression;
        this.decompiler.addToken(82);
        this.decompiler.addEOL(86);
        Node tryBlock = transform(node.getTryBlock());
        this.decompiler.addEOL(87);
        Node catchBlocks = new Block();
        for (CatchClause cc : node.getCatchClauses()) {
            this.decompiler.addToken(125);
            this.decompiler.addToken(88);
            String varName = cc.getVarName().getIdentifier();
            this.decompiler.addName(varName);
            AstNode ccc = cc.getCatchCondition();
            if (ccc != null) {
                this.decompiler.addName(" ");
                this.decompiler.addToken(113);
                emptyExpression = transform(ccc);
            } else {
                emptyExpression = new EmptyExpression();
            }
            Node catchCond = emptyExpression;
            this.decompiler.addToken(89);
            this.decompiler.addEOL(86);
            Node body = transform(cc.getBody());
            this.decompiler.addEOL(87);
            catchBlocks.addChildToBack(createCatch(varName, catchCond, body, cc.getLineno()));
        }
        Node finallyBlock = null;
        if (node.getFinallyBlock() != null) {
            this.decompiler.addToken(126);
            this.decompiler.addEOL(86);
            finallyBlock = transform(node.getFinallyBlock());
            this.decompiler.addEOL(87);
        }
        return createTryCatchFinally(tryBlock, catchBlocks, finallyBlock, node.getLineno());
    }

    private Node transformUnary(UnaryExpression node) {
        int type = node.getType();
        if (type == 75) {
            return transformDefaultXmlNamepace(node);
        }
        if (node.isPrefix()) {
            this.decompiler.addToken(type);
        }
        Node child = transform(node.getOperand());
        if (node.isPostfix()) {
            this.decompiler.addToken(type);
        }
        if (type == 107 || type == 108) {
            return createIncDec(type, node.isPostfix(), child);
        }
        return createUnary(type, child);
    }

    private Node transformVariables(VariableDeclaration node) {
        this.decompiler.addToken(node.getType());
        transformVariableInitializers(node);
        AstNode parent = node.getParent();
        if (!(parent instanceof Loop) && !(parent instanceof LetNode)) {
            this.decompiler.addEOL(83);
        }
        return node;
    }

    private Node transformVariableInitializers(VariableDeclaration node) {
        Node left;
        List<VariableInitializer> vars = node.getVariables();
        int size = vars.size();
        int i = 0;
        for (VariableInitializer var : vars) {
            AstNode target = var.getTarget();
            AstNode init = var.getInitializer();
            if (var.isDestructuring()) {
                decompile(target);
                left = target;
            } else {
                left = transform(target);
            }
            Node right = null;
            if (init != null) {
                this.decompiler.addToken(91);
                right = transform(init);
            }
            if (var.isDestructuring()) {
                if (right == null) {
                    node.addChildToBack(left);
                } else {
                    Node d = createDestructuringAssignment(node.getType(), left, right);
                    node.addChildToBack(d);
                }
            } else {
                if (right != null) {
                    left.addChildToBack(right);
                }
                node.addChildToBack(left);
            }
            int i2 = i;
            i++;
            if (i2 < size - 1) {
                this.decompiler.addToken(90);
            }
        }
        return node;
    }

    private Node transformWhileLoop(WhileLoop loop) {
        this.decompiler.addToken(118);
        loop.setType(133);
        pushScope(loop);
        try {
            this.decompiler.addToken(88);
            Node cond = transform(loop.getCondition());
            this.decompiler.addToken(89);
            this.decompiler.addEOL(86);
            Node body = transform(loop.getBody());
            this.decompiler.addEOL(87);
            return createLoop(loop, 1, body, cond, null, null);
        } finally {
            popScope();
        }
    }

    private Node transformWith(WithStatement node) {
        this.decompiler.addToken(124);
        this.decompiler.addToken(88);
        Node expr = transform(node.getExpression());
        this.decompiler.addToken(89);
        this.decompiler.addEOL(86);
        Node stmt = transform(node.getStatement());
        this.decompiler.addEOL(87);
        return createWith(expr, stmt, node.getLineno());
    }

    private Node transformYield(Yield node) {
        this.decompiler.addToken(node.getType());
        Node kid = node.getValue() == null ? null : transform(node.getValue());
        if (kid != null) {
            return new Node(node.getType(), kid, node.getLineno());
        }
        return new Node(node.getType(), node.getLineno());
    }

    private Node transformXmlLiteral(XmlLiteral node) {
        Node expr;
        Node nodeCreateUnary;
        Node pnXML = new Node(30, node.getLineno());
        List<XmlFragment> frags = node.getFragments();
        XmlString first = (XmlString) frags.get(0);
        boolean anon = first.getXml().trim().startsWith("<>");
        pnXML.addChildToBack(createName(anon ? "XMLList" : "XML"));
        Node pn = null;
        for (XmlFragment frag : frags) {
            if (frag instanceof XmlString) {
                String xml = ((XmlString) frag).getXml();
                this.decompiler.addName(xml);
                if (pn == null) {
                    pn = createString(xml);
                } else {
                    pn = createBinary(21, pn, createString(xml));
                }
            } else {
                XmlExpression xexpr = (XmlExpression) frag;
                boolean isXmlAttr = xexpr.isXmlAttribute();
                this.decompiler.addToken(86);
                if (xexpr.getExpression() instanceof EmptyExpression) {
                    expr = createString("");
                } else {
                    expr = transform(xexpr.getExpression());
                }
                this.decompiler.addToken(87);
                if (isXmlAttr) {
                    Node prepend = createBinary(21, createString(OperatorName.SHOW_TEXT_LINE_AND_SPACE), createUnary(76, expr));
                    nodeCreateUnary = createBinary(21, prepend, createString(OperatorName.SHOW_TEXT_LINE_AND_SPACE));
                } else {
                    nodeCreateUnary = createUnary(77, expr);
                }
                Node expr2 = nodeCreateUnary;
                pn = createBinary(21, pn, expr2);
            }
        }
        pnXML.addChildToBack(pn);
        return pnXML;
    }

    private Node transformXmlMemberGet(XmlMemberGet node) {
        XmlRef ref = node.getMemberRef();
        Node pn = transform(node.getLeft());
        int flags = ref.isAttributeAccess() ? 2 : 0;
        if (node.getType() == 144) {
            flags |= 4;
            this.decompiler.addToken(144);
        } else {
            this.decompiler.addToken(109);
        }
        return transformXmlRef(pn, ref, flags);
    }

    private Node transformXmlRef(XmlRef node) {
        int memberTypeFlags = node.isAttributeAccess() ? 2 : 0;
        return transformXmlRef(null, node, memberTypeFlags);
    }

    private Node transformXmlRef(Node pn, XmlRef node, int memberTypeFlags) {
        if ((memberTypeFlags & 2) != 0) {
            this.decompiler.addToken(148);
        }
        Name namespace = node.getNamespace();
        String ns = namespace != null ? namespace.getIdentifier() : null;
        if (ns != null) {
            this.decompiler.addName(ns);
            this.decompiler.addToken(145);
        }
        if (node instanceof XmlPropRef) {
            String name = ((XmlPropRef) node).getPropName().getIdentifier();
            this.decompiler.addName(name);
            return createPropertyGet(pn, ns, name, memberTypeFlags);
        }
        this.decompiler.addToken(84);
        Node expr = transform(((XmlElemRef) node).getExpression());
        this.decompiler.addToken(85);
        return createElementGet(pn, ns, expr, memberTypeFlags);
    }

    private Node transformDefaultXmlNamepace(UnaryExpression node) {
        this.decompiler.addToken(117);
        this.decompiler.addName(" xml");
        this.decompiler.addName(" namespace");
        this.decompiler.addToken(91);
        Node child = transform(node.getOperand());
        return createUnary(75, child);
    }

    private static void addSwitchCase(Node switchBlock, Node caseExpression, Node statements) {
        if (switchBlock.getType() != 130) {
            throw Kit.codeBug();
        }
        Jump switchNode = (Jump) switchBlock.getFirstChild();
        if (switchNode.getType() != 115) {
            throw Kit.codeBug();
        }
        Node gotoTarget = Node.newTarget();
        if (caseExpression != null) {
            Jump caseNode = new Jump(116, caseExpression);
            caseNode.target = gotoTarget;
            switchNode.addChildToBack(caseNode);
        } else {
            switchNode.setDefault(gotoTarget);
        }
        switchBlock.addChildToBack(gotoTarget);
        switchBlock.addChildToBack(statements);
    }

    private static void closeSwitch(Node switchBlock) {
        if (switchBlock.getType() != 130) {
            throw Kit.codeBug();
        }
        Jump switchNode = (Jump) switchBlock.getFirstChild();
        if (switchNode.getType() != 115) {
            throw Kit.codeBug();
        }
        Node switchBreakTarget = Node.newTarget();
        switchNode.target = switchBreakTarget;
        Node defaultTarget = switchNode.getDefault();
        if (defaultTarget == null) {
            defaultTarget = switchBreakTarget;
        }
        switchBlock.addChildAfter(makeJump(5, defaultTarget), switchNode);
        switchBlock.addChildToBack(switchBreakTarget);
    }

    private static Node createExprStatementNoReturn(Node expr, int lineno) {
        return new Node(134, expr, lineno);
    }

    private static Node createString(String string) {
        return Node.newString(string);
    }

    private Node createCatch(String varName, Node catchCond, Node stmts, int lineno) {
        if (catchCond == null) {
            catchCond = new Node(129);
        }
        return new Node(125, createName(varName), catchCond, stmts, lineno);
    }

    private static Node initFunction(FunctionNode fnNode, int functionIndex, Node statements, int functionType) {
        Name name;
        fnNode.setFunctionType(functionType);
        fnNode.addChildToBack(statements);
        int functionCount = fnNode.getFunctionCount();
        if (functionCount != 0) {
            fnNode.setRequiresActivation();
        }
        if (functionType == 2 && (name = fnNode.getFunctionName()) != null && name.length() != 0 && fnNode.getSymbol(name.getIdentifier()) == null) {
            fnNode.putSymbol(new org.mozilla.javascript.ast.Symbol(110, name.getIdentifier()));
            Node setFn = new Node(134, new Node(8, Node.newString(49, name.getIdentifier()), new Node(64)));
            statements.addChildrenToFront(setFn);
        }
        Node lastStmt = statements.getLastChild();
        if (lastStmt == null || lastStmt.getType() != 4) {
            statements.addChildToBack(new Node(4));
        }
        Node result = Node.newString(110, fnNode.getName());
        result.putIntProp(1, functionIndex);
        return result;
    }

    private Scope createLoopNode(Node loopLabel, int lineno) {
        Scope result = createScopeNode(133, lineno);
        if (loopLabel != null) {
            ((Jump) loopLabel).setLoop(result);
        }
        return result;
    }

    private static Node createFor(Scope loop, Node init, Node test, Node incr, Node body) {
        if (init.getType() == 154) {
            Scope let = Scope.splitScope(loop);
            let.setType(154);
            let.addChildrenToBack(init);
            let.addChildToBack(createLoop(loop, 2, body, test, new Node(129), incr));
            return let;
        }
        return createLoop(loop, 2, body, test, init, incr);
    }

    private static Node createLoop(Jump loop, int loopType, Node body, Node cond, Node init, Node incr) {
        Node bodyTarget = Node.newTarget();
        Node condTarget = Node.newTarget();
        if (loopType == 2 && cond.getType() == 129) {
            cond = new Node(45);
        }
        Jump IFEQ = new Jump(6, cond);
        IFEQ.target = bodyTarget;
        Node breakTarget = Node.newTarget();
        loop.addChildToBack(bodyTarget);
        loop.addChildrenToBack(body);
        if (loopType == 1 || loopType == 2) {
            loop.addChildrenToBack(new Node(129, loop.getLineno()));
        }
        loop.addChildToBack(condTarget);
        loop.addChildToBack(IFEQ);
        loop.addChildToBack(breakTarget);
        loop.target = breakTarget;
        Node continueTarget = condTarget;
        if (loopType == 1 || loopType == 2) {
            loop.addChildToFront(makeJump(5, condTarget));
            if (loopType == 2) {
                int initType = init.getType();
                if (initType != 129) {
                    if (initType != 123 && initType != 154) {
                        init = new Node(134, init);
                    }
                    loop.addChildToFront(init);
                }
                Node incrTarget = Node.newTarget();
                loop.addChildAfter(incrTarget, body);
                if (incr.getType() != 129) {
                    loop.addChildAfter(new Node(134, incr), incrTarget);
                }
                continueTarget = incrTarget;
            }
        }
        loop.setContinue(continueTarget);
        return loop;
    }

    private Node createForIn(int declType, Node loop, Node lhs, Node obj, Node body, boolean isForEach, boolean isForOf) {
        Node lvalue;
        int i;
        Node assign;
        int destructuring = -1;
        int destructuringLen = 0;
        int type = lhs.getType();
        if (type == 123 || type == 154) {
            Node kid = lhs.getLastChild();
            int kidType = kid.getType();
            if (kidType == 66 || kidType == 67) {
                destructuring = kidType;
                type = kidType;
                lvalue = kid;
                destructuringLen = 0;
                if (kid instanceof ArrayLiteral) {
                    destructuringLen = ((ArrayLiteral) kid).getDestructuringLength();
                }
            } else if (kidType == 39) {
                lvalue = Node.newString(39, kid.getString());
            } else {
                reportError("msg.bad.for.in.lhs");
                return null;
            }
        } else if (type == 66 || type == 67) {
            destructuring = type;
            lvalue = lhs;
            destructuringLen = 0;
            if (lhs instanceof ArrayLiteral) {
                destructuringLen = ((ArrayLiteral) lhs).getDestructuringLength();
            }
        } else {
            lvalue = makeReference(lhs);
            if (lvalue == null) {
                reportError("msg.bad.for.in.lhs");
                return null;
            }
        }
        Node localBlock = new Node(142);
        if (isForEach) {
            i = 59;
        } else if (isForOf) {
            i = 61;
        } else {
            i = destructuring != -1 ? 60 : 58;
        }
        int initType = i;
        Node init = new Node(initType, obj);
        init.putProp(3, localBlock);
        Node cond = new Node(62);
        cond.putProp(3, localBlock);
        Node id = new Node(63);
        id.putProp(3, localBlock);
        Node newBody = new Node(130);
        if (destructuring != -1) {
            assign = createDestructuringAssignment(declType, lvalue, id);
            if (!isForEach && !isForOf && (destructuring == 67 || destructuringLen != 2)) {
                reportError("msg.bad.for.in.destruct");
            }
        } else {
            assign = simpleAssignment(lvalue, id);
        }
        newBody.addChildToBack(new Node(134, assign));
        newBody.addChildToBack(body);
        Node loop2 = createLoop((Jump) loop, 1, newBody, cond, null, null);
        loop2.addChildToFront(init);
        if (type == 123 || type == 154) {
            loop2.addChildToFront(lhs);
        }
        localBlock.addChildToBack(loop2);
        return localBlock;
    }

    private Node createTryCatchFinally(Node tryBlock, Node catchBlocks, Node finallyBlock, int lineno) {
        Node condStmt;
        boolean hasFinally = finallyBlock != null && (finallyBlock.getType() != 130 || finallyBlock.hasChildren());
        if (tryBlock.getType() == 130 && !tryBlock.hasChildren() && !hasFinally) {
            return tryBlock;
        }
        boolean hasCatch = catchBlocks.hasChildren();
        if (!hasFinally && !hasCatch) {
            return tryBlock;
        }
        Node node = new Node(142);
        Jump jump = new Jump(82, tryBlock, lineno);
        jump.putProp(3, node);
        if (hasCatch) {
            Node endCatch = Node.newTarget();
            jump.addChildToBack(makeJump(5, endCatch));
            Node catchTarget = Node.newTarget();
            jump.target = catchTarget;
            jump.addChildToBack(catchTarget);
            Node catchScopeBlock = new Node(142);
            Node cb = catchBlocks.getFirstChild();
            boolean hasDefault = false;
            int scopeIndex = 0;
            while (cb != null) {
                int catchLineNo = cb.getLineno();
                Node name = cb.getFirstChild();
                Node cond = name.getNext();
                Node catchStatement = cond.getNext();
                cb.removeChild(name);
                cb.removeChild(cond);
                cb.removeChild(catchStatement);
                catchStatement.addChildToBack(new Node(3));
                catchStatement.addChildToBack(makeJump(5, endCatch));
                if (cond.getType() == 129) {
                    condStmt = catchStatement;
                    hasDefault = true;
                } else {
                    condStmt = createIf(cond, catchStatement, null, catchLineNo);
                }
                Node catchScope = new Node(57, name, createUseLocal(node));
                catchScope.putProp(3, catchScopeBlock);
                catchScope.putIntProp(14, scopeIndex);
                catchScopeBlock.addChildToBack(catchScope);
                catchScopeBlock.addChildToBack(createWith(createUseLocal(catchScopeBlock), condStmt, catchLineNo));
                cb = cb.getNext();
                scopeIndex++;
            }
            jump.addChildToBack(catchScopeBlock);
            if (!hasDefault) {
                Node node2 = new Node(51);
                node2.putProp(3, node);
                jump.addChildToBack(node2);
            }
            jump.addChildToBack(endCatch);
        }
        if (hasFinally) {
            Node finallyTarget = Node.newTarget();
            jump.setFinally(finallyTarget);
            jump.addChildToBack(makeJump(136, finallyTarget));
            Node finallyEnd = Node.newTarget();
            jump.addChildToBack(makeJump(5, finallyEnd));
            jump.addChildToBack(finallyTarget);
            Node node3 = new Node(126, finallyBlock);
            node3.putProp(3, node);
            jump.addChildToBack(node3);
            jump.addChildToBack(finallyEnd);
        }
        node.addChildToBack(jump);
        return node;
    }

    private Node createWith(Node obj, Node body, int lineno) {
        setRequiresActivation();
        Node result = new Node(130, lineno);
        result.addChildToBack(new Node(2, obj));
        Node bodyNode = new Node(124, body, lineno);
        result.addChildrenToBack(bodyNode);
        result.addChildToBack(new Node(3));
        return result;
    }

    private static Node createIf(Node cond, Node ifTrue, Node ifFalse, int lineno) {
        int condStatus = isAlwaysDefinedBoolean(cond);
        if (condStatus == 1) {
            return ifTrue;
        }
        if (condStatus == -1) {
            if (ifFalse != null) {
                return ifFalse;
            }
            return new Node(130, lineno);
        }
        Node result = new Node(130, lineno);
        Node ifNotTarget = Node.newTarget();
        Jump IFNE = new Jump(7, cond);
        IFNE.target = ifNotTarget;
        result.addChildToBack(IFNE);
        result.addChildrenToBack(ifTrue);
        if (ifFalse != null) {
            Node endTarget = Node.newTarget();
            result.addChildToBack(makeJump(5, endTarget));
            result.addChildToBack(ifNotTarget);
            result.addChildrenToBack(ifFalse);
            result.addChildToBack(endTarget);
        } else {
            result.addChildToBack(ifNotTarget);
        }
        return result;
    }

    private static Node createCondExpr(Node cond, Node ifTrue, Node ifFalse) {
        int condStatus = isAlwaysDefinedBoolean(cond);
        if (condStatus == 1) {
            return ifTrue;
        }
        if (condStatus == -1) {
            return ifFalse;
        }
        return new Node(103, cond, ifTrue, ifFalse);
    }

    private static Node createUnary(int nodeType, Node child) {
        int type;
        Node n;
        int childType = child.getType();
        switch (nodeType) {
            case 26:
                int status = isAlwaysDefinedBoolean(child);
                if (status != 0) {
                    if (status == 1) {
                        type = 44;
                    } else {
                        type = 45;
                    }
                    if (childType == 45 || childType == 44) {
                        child.setType(type);
                        return child;
                    }
                    return new Node(type);
                }
                break;
            case 27:
                if (childType == 40) {
                    int value = ScriptRuntime.toInt32(child.getDouble());
                    child.setDouble(value ^ (-1));
                    return child;
                }
                break;
            case 29:
                if (childType == 40) {
                    child.setDouble(-child.getDouble());
                    return child;
                }
                break;
            case 31:
                if (childType == 39) {
                    child.setType(49);
                    n = new Node(nodeType, child, Node.newString(child.getString()));
                } else if (childType == 33 || childType == 36) {
                    Node left = child.getFirstChild();
                    Node right = child.getLastChild();
                    child.removeChild(left);
                    child.removeChild(right);
                    n = new Node(nodeType, left, right);
                } else if (childType == 68) {
                    Node ref = child.getFirstChild();
                    child.removeChild(ref);
                    n = new Node(70, ref);
                } else {
                    n = new Node(nodeType, new Node(45), child);
                }
                return n;
            case 32:
                if (childType == 39) {
                    child.setType(138);
                    return child;
                }
                break;
        }
        return new Node(nodeType, child);
    }

    private Node createCallOrNew(int nodeType, Node child) {
        int type = 0;
        if (child.getType() == 39) {
            String name = child.getString();
            if (name.equals("eval")) {
                type = 1;
            } else if (name.equals("With")) {
                type = 2;
            }
        } else if (child.getType() == 33 && child.getLastChild().getString().equals("eval")) {
            type = 1;
        }
        Node node = new Node(nodeType, child);
        if (type != 0) {
            setRequiresActivation();
            node.putIntProp(10, type);
        }
        return node;
    }

    private static Node createIncDec(int nodeType, boolean post, Node child) {
        Node child2 = makeReference(child);
        int childType = child2.getType();
        switch (childType) {
            case 33:
            case 36:
            case 39:
            case 68:
                Node n = new Node(nodeType, child2);
                int incrDecrMask = 0;
                if (nodeType == 108) {
                    incrDecrMask = 0 | 1;
                }
                if (post) {
                    incrDecrMask |= 2;
                }
                n.putIntProp(13, incrDecrMask);
                return n;
            default:
                throw Kit.codeBug();
        }
    }

    private Node createPropertyGet(Node target, String namespace, String name, int memberTypeFlags) {
        if (namespace == null && memberTypeFlags == 0) {
            if (target == null) {
                return createName(name);
            }
            checkActivationName(name, 33);
            if (ScriptRuntime.isSpecialProperty(name)) {
                Node ref = new Node(72, target);
                ref.putProp(17, name);
                return new Node(68, ref);
            }
            return new Node(33, target, Node.newString(name));
        }
        Node elem = Node.newString(name);
        return createMemberRefGet(target, namespace, elem, memberTypeFlags | 1);
    }

    private Node createElementGet(Node target, String namespace, Node elem, int memberTypeFlags) {
        if (namespace == null && memberTypeFlags == 0) {
            if (target == null) {
                throw Kit.codeBug();
            }
            return new Node(36, target, elem);
        }
        return createMemberRefGet(target, namespace, elem, memberTypeFlags);
    }

    private Node createMemberRefGet(Node target, String namespace, Node elem, int memberTypeFlags) {
        Node ref;
        Node nsNode = null;
        if (namespace != null) {
            if (namespace.equals("*")) {
                nsNode = new Node(42);
            } else {
                nsNode = createName(namespace);
            }
        }
        if (target == null) {
            if (namespace == null) {
                ref = new Node(80, elem);
            } else {
                ref = new Node(81, nsNode, elem);
            }
        } else if (namespace == null) {
            ref = new Node(78, target, elem);
        } else {
            ref = new Node(79, target, nsNode, elem);
        }
        if (memberTypeFlags != 0) {
            ref.putIntProp(16, memberTypeFlags);
        }
        return new Node(68, ref);
    }

    private static Node createBinary(int nodeType, Node left, Node right) {
        String s2;
        switch (nodeType) {
            case 21:
                if (left.type == 41) {
                    if (right.type == 41) {
                        s2 = right.getString();
                    } else if (right.type == 40) {
                        s2 = ScriptRuntime.numberToString(right.getDouble(), 10);
                    }
                    String s1 = left.getString();
                    left.setString(s1.concat(s2));
                    return left;
                }
                if (left.type == 40) {
                    if (right.type == 40) {
                        left.setDouble(left.getDouble() + right.getDouble());
                        return left;
                    }
                    if (right.type == 41) {
                        String s3 = ScriptRuntime.numberToString(left.getDouble(), 10);
                        String s4 = right.getString();
                        right.setString(s3.concat(s4));
                        return right;
                    }
                }
                break;
            case 22:
                if (left.type == 40) {
                    double ld = left.getDouble();
                    if (right.type == 40) {
                        left.setDouble(ld - right.getDouble());
                        return left;
                    }
                    if (ld == 0.0d) {
                        return new Node(29, right);
                    }
                } else if (right.type == 40 && right.getDouble() == 0.0d) {
                    return new Node(28, left);
                }
            case 23:
                if (left.type == 40) {
                    double ld2 = left.getDouble();
                    if (right.type == 40) {
                        left.setDouble(ld2 * right.getDouble());
                        return left;
                    }
                    if (ld2 == 1.0d) {
                        return new Node(28, right);
                    }
                } else if (right.type == 40 && right.getDouble() == 1.0d) {
                    return new Node(28, left);
                }
            case 24:
                if (right.type == 40) {
                    double rd = right.getDouble();
                    if (left.type == 40) {
                        left.setDouble(left.getDouble() / rd);
                        return left;
                    }
                    if (rd == 1.0d) {
                        return new Node(28, left);
                    }
                }
                break;
            case 105:
                int leftStatus = isAlwaysDefinedBoolean(left);
                if (leftStatus == 1) {
                    return left;
                }
                if (leftStatus == -1) {
                    return right;
                }
                break;
            case 106:
                int leftStatus2 = isAlwaysDefinedBoolean(left);
                if (leftStatus2 == -1) {
                    return left;
                }
                if (leftStatus2 == 1) {
                    return right;
                }
                break;
        }
        return new Node(nodeType, left, right);
    }

    private Node createAssignment(int assignType, Node left, Node right) {
        int assignOp;
        Node ref = makeReference(left);
        if (ref == null) {
            if (left.getType() == 66 || left.getType() == 67) {
                if (assignType != 91) {
                    reportError("msg.bad.destruct.op");
                    return right;
                }
                return createDestructuringAssignment(-1, left, right);
            }
            reportError("msg.bad.assign.left");
            return right;
        }
        switch (assignType) {
            case 91:
                return simpleAssignment(ref, right);
            case 92:
                assignOp = 9;
                break;
            case 93:
                assignOp = 10;
                break;
            case 94:
                assignOp = 11;
                break;
            case 95:
                assignOp = 18;
                break;
            case 96:
                assignOp = 19;
                break;
            case 97:
                assignOp = 20;
                break;
            case 98:
                assignOp = 21;
                break;
            case 99:
                assignOp = 22;
                break;
            case 100:
                assignOp = 23;
                break;
            case 101:
                assignOp = 24;
                break;
            case 102:
                assignOp = 25;
                break;
            default:
                throw Kit.codeBug();
        }
        int nodeType = ref.getType();
        switch (nodeType) {
            case 33:
            case 36:
                Node obj = ref.getFirstChild();
                Node id = ref.getLastChild();
                int type = nodeType == 33 ? 140 : 141;
                Node opLeft = new Node(139);
                Node op = new Node(assignOp, opLeft, right);
                return new Node(type, obj, id, op);
            case 39:
                Node op2 = new Node(assignOp, ref, right);
                Node lvalueLeft = Node.newString(49, ref.getString());
                return new Node(8, lvalueLeft, op2);
            case 68:
                Node ref2 = ref.getFirstChild();
                checkMutableReference(ref2);
                Node opLeft2 = new Node(139);
                Node op3 = new Node(assignOp, opLeft2, right);
                return new Node(143, ref2, op3);
            default:
                throw Kit.codeBug();
        }
    }

    private static Node createUseLocal(Node localBlock) {
        if (142 != localBlock.getType()) {
            throw Kit.codeBug();
        }
        Node result = new Node(54);
        result.putProp(3, localBlock);
        return result;
    }

    private static Jump makeJump(int type, Node target) {
        Jump n = new Jump(type);
        n.target = target;
        return n;
    }

    private static Node makeReference(Node node) {
        int type = node.getType();
        switch (type) {
            case 33:
            case 36:
            case 39:
            case 68:
                return node;
            case 38:
                node.setType(71);
                return new Node(68, node);
            default:
                return null;
        }
    }

    private static int isAlwaysDefinedBoolean(Node node) {
        switch (node.getType()) {
            case 40:
                double num = node.getDouble();
                if (!Double.isNaN(num) && num != 0.0d) {
                    return 1;
                }
                return -1;
            case 41:
            case 43:
            default:
                return 0;
            case 42:
            case 44:
                return -1;
            case 45:
                return 1;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    boolean isDestructuring(Node node) {
        return (node instanceof DestructuringForm) && ((DestructuringForm) node).isDestructuring();
    }

    Node decompileFunctionHeader(FunctionNode fn) {
        Node mexpr = null;
        if (fn.getFunctionName() != null) {
            this.decompiler.addName(fn.getName());
        } else if (fn.getMemberExprNode() != null) {
            mexpr = transform(fn.getMemberExprNode());
        }
        boolean isArrow = fn.getFunctionType() == 4;
        boolean noParen = isArrow && fn.getLp() == -1;
        if (!noParen) {
            this.decompiler.addToken(88);
        }
        List<AstNode> params = fn.getParams();
        for (int i = 0; i < params.size(); i++) {
            decompile(params.get(i));
            if (i < params.size() - 1) {
                this.decompiler.addToken(90);
            }
        }
        if (!noParen) {
            this.decompiler.addToken(89);
        }
        if (isArrow) {
            this.decompiler.addToken(165);
        }
        if (!fn.isExpressionClosure()) {
            this.decompiler.addEOL(86);
        }
        return mexpr;
    }

    void decompile(AstNode node) {
        switch (node.getType()) {
            case 33:
                decompilePropertyGet((PropertyGet) node);
                break;
            case 36:
                decompileElementGet((ElementGet) node);
                break;
            case 39:
                this.decompiler.addName(((Name) node).getIdentifier());
                break;
            case 40:
                this.decompiler.addNumber(((NumberLiteral) node).getNumber());
                break;
            case 41:
                this.decompiler.addString(((StringLiteral) node).getValue());
                break;
            case 43:
                this.decompiler.addToken(node.getType());
                break;
            case 66:
                decompileArrayLiteral((ArrayLiteral) node);
                break;
            case 67:
                decompileObjectLiteral((ObjectLiteral) node);
                break;
            case 129:
                break;
            default:
                Kit.codeBug("unexpected token: " + Token.typeToName(node.getType()));
                break;
        }
    }

    void decompileArrayLiteral(ArrayLiteral node) {
        this.decompiler.addToken(84);
        List<AstNode> elems = node.getElements();
        int size = elems.size();
        for (int i = 0; i < size; i++) {
            AstNode elem = elems.get(i);
            decompile(elem);
            if (i < size - 1) {
                this.decompiler.addToken(90);
            }
        }
        this.decompiler.addToken(85);
    }

    void decompileObjectLiteral(ObjectLiteral node) {
        this.decompiler.addToken(86);
        List<ObjectProperty> props = node.getElements();
        int size = props.size();
        for (int i = 0; i < size; i++) {
            ObjectProperty prop = props.get(i);
            boolean destructuringShorthand = Boolean.TRUE.equals(prop.getProp(26));
            decompile(prop.getLeft());
            if (!destructuringShorthand) {
                this.decompiler.addToken(104);
                decompile(prop.getRight());
            }
            if (i < size - 1) {
                this.decompiler.addToken(90);
            }
        }
        this.decompiler.addToken(87);
    }

    void decompilePropertyGet(PropertyGet node) {
        decompile(node.getTarget());
        this.decompiler.addToken(109);
        decompile(node.getProperty());
    }

    void decompileElementGet(ElementGet node) {
        decompile(node.getTarget());
        this.decompiler.addToken(84);
        decompile(node.getElement());
        this.decompiler.addToken(85);
    }
}
