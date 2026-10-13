package org.mozilla.javascript;

import com.sun.jna.platform.win32.Msi;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Block;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Jump;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.VariableInitializer;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/CodeGenerator.class */
class CodeGenerator extends Icode {
    private static final int MIN_LABEL_TABLE_SIZE = 32;
    private static final int MIN_FIXUP_TABLE_SIZE = 40;
    private CompilerEnvirons compilerEnv;
    private boolean itsInFunctionFlag;
    private boolean itsInTryFlag;
    private InterpreterData itsData;
    private ScriptNode scriptOrFn;
    private int iCodeTop;
    private int stackDepth;
    private int lineNumber;
    private int doubleTableTop;
    private int localTop;
    private int[] labelTable;
    private int labelTableTop;
    private long[] fixupTable;
    private int fixupTableTop;
    private int exceptionTableTop;
    private static final int ECF_TAIL = 1;
    private ObjToIntMap strings = new ObjToIntMap(20);
    private ObjArray literalIds = new ObjArray();

    CodeGenerator() {
    }

    public InterpreterData compile(CompilerEnvirons compilerEnv, ScriptNode tree, String encodedSource, boolean returnFunction) {
        this.compilerEnv = compilerEnv;
        new NodeTransformer().transform(tree, compilerEnv);
        if (returnFunction) {
            this.scriptOrFn = tree.getFunctionNode(0);
        } else {
            this.scriptOrFn = tree;
        }
        this.itsData = new InterpreterData(compilerEnv.getLanguageVersion(), this.scriptOrFn.getSourceName(), encodedSource, this.scriptOrFn.isInStrictMode());
        this.itsData.topLevel = true;
        if (returnFunction) {
            generateFunctionICode();
        } else {
            generateICodeFromTree(this.scriptOrFn);
        }
        return this.itsData;
    }

    private void generateFunctionICode() {
        this.itsInFunctionFlag = true;
        FunctionNode theFunction = (FunctionNode) this.scriptOrFn;
        this.itsData.itsFunctionType = theFunction.getFunctionType();
        this.itsData.itsNeedsActivation = theFunction.requiresActivation();
        if (theFunction.getFunctionName() != null) {
            this.itsData.itsName = theFunction.getName();
        }
        if (theFunction.isGenerator()) {
            addIcode(-62);
            addUint16(theFunction.getBaseLineno() & 65535);
        }
        if (theFunction.isInStrictMode()) {
            this.itsData.isStrict = true;
        }
        if (theFunction.isES6Generator()) {
            this.itsData.isES6Generator = true;
        }
        this.itsData.declaredAsVar = theFunction.getParent() instanceof VariableInitializer;
        generateICodeFromTree(theFunction.getLastChild());
    }

    private void generateICodeFromTree(Node tree) {
        generateNestedFunctions();
        generateRegExpLiterals();
        visitStatement(tree, 0);
        fixLabelGotos();
        if (this.itsData.itsFunctionType == 0) {
            addToken(65);
        }
        if (this.itsData.itsICode.length != this.iCodeTop) {
            byte[] tmp = new byte[this.iCodeTop];
            System.arraycopy(this.itsData.itsICode, 0, tmp, 0, this.iCodeTop);
            this.itsData.itsICode = tmp;
        }
        if (this.strings.size() == 0) {
            this.itsData.itsStringTable = null;
        } else {
            this.itsData.itsStringTable = new String[this.strings.size()];
            ObjToIntMap.Iterator iter = this.strings.newIterator();
            iter.start();
            while (!iter.done()) {
                String str = (String) iter.getKey();
                int index = iter.getValue();
                if (this.itsData.itsStringTable[index] != null) {
                    Kit.codeBug();
                }
                this.itsData.itsStringTable[index] = str;
                iter.next();
            }
        }
        if (this.doubleTableTop == 0) {
            this.itsData.itsDoubleTable = null;
        } else if (this.itsData.itsDoubleTable.length != this.doubleTableTop) {
            double[] tmp2 = new double[this.doubleTableTop];
            System.arraycopy(this.itsData.itsDoubleTable, 0, tmp2, 0, this.doubleTableTop);
            this.itsData.itsDoubleTable = tmp2;
        }
        if (this.exceptionTableTop != 0 && this.itsData.itsExceptionTable.length != this.exceptionTableTop) {
            int[] tmp3 = new int[this.exceptionTableTop];
            System.arraycopy(this.itsData.itsExceptionTable, 0, tmp3, 0, this.exceptionTableTop);
            this.itsData.itsExceptionTable = tmp3;
        }
        this.itsData.itsMaxVars = this.scriptOrFn.getParamAndVarCount();
        this.itsData.itsMaxFrameArray = this.itsData.itsMaxVars + this.itsData.itsMaxLocals + this.itsData.itsMaxStack;
        this.itsData.argNames = this.scriptOrFn.getParamAndVarNames();
        this.itsData.argIsConst = this.scriptOrFn.getParamAndVarConst();
        this.itsData.argCount = this.scriptOrFn.getParamCount();
        this.itsData.encodedSourceStart = this.scriptOrFn.getEncodedSourceStart();
        this.itsData.encodedSourceEnd = this.scriptOrFn.getEncodedSourceEnd();
        if (this.literalIds.size() != 0) {
            this.itsData.literalIds = this.literalIds.toArray();
        }
    }

    private void generateNestedFunctions() {
        int functionCount = this.scriptOrFn.getFunctionCount();
        if (functionCount == 0) {
            return;
        }
        InterpreterData[] array = new InterpreterData[functionCount];
        for (int i = 0; i != functionCount; i++) {
            FunctionNode fn = this.scriptOrFn.getFunctionNode(i);
            CodeGenerator gen = new CodeGenerator();
            gen.compilerEnv = this.compilerEnv;
            gen.scriptOrFn = fn;
            gen.itsData = new InterpreterData(this.itsData);
            gen.generateFunctionICode();
            array[i] = gen.itsData;
            AstNode fnParent = fn.getParent();
            if (!(fnParent instanceof AstRoot) && !(fnParent instanceof Scope) && !(fnParent instanceof Block)) {
                gen.itsData.declaredAsFunctionExpression = true;
            }
        }
        this.itsData.itsNestedFunctions = array;
    }

    private void generateRegExpLiterals() {
        int N = this.scriptOrFn.getRegexpCount();
        if (N == 0) {
            return;
        }
        Context cx = Context.getContext();
        RegExpProxy rep = ScriptRuntime.checkRegExpProxy(cx);
        Object[] array = new Object[N];
        for (int i = 0; i != N; i++) {
            String string = this.scriptOrFn.getRegexpString(i);
            String flags = this.scriptOrFn.getRegexpFlags(i);
            array[i] = rep.compileRegExp(cx, string, flags);
        }
        this.itsData.itsRegExpLiterals = array;
    }

    private void updateLineNumber(Node node) {
        int lineno = node.getLineno();
        if (lineno != this.lineNumber && lineno >= 0) {
            if (this.itsData.firstLinePC < 0) {
                this.itsData.firstLinePC = lineno;
            }
            this.lineNumber = lineno;
            addIcode(-26);
            addUint16(lineno & 65535);
        }
    }

    private static RuntimeException badTree(Node node) {
        throw new RuntimeException(node.toString());
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:18:0x016c A[LOOP:4: B:16:0x0167->B:18:0x016c, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:87:0x04d9  */
    /* JADX WARN: Code duplicated, block: B:89:0x04dd A[RETURN] */
    private void visitStatement(Node node, int initialStackDepth) {
        int type = node.getType();
        Node child = node.getFirstChild();
        switch (type) {
            case -62:
                if (this.stackDepth != initialStackDepth) {
                    throw Kit.codeBug();
                }
                return;
            case 2:
                visitExpression(child, 0);
                addToken(2);
                stackChange(-1);
                if (this.stackDepth != initialStackDepth) {
                    throw Kit.codeBug();
                }
                return;
            case 3:
                addToken(3);
                if (this.stackDepth != initialStackDepth) {
                    throw Kit.codeBug();
                }
                return;
            case 4:
                updateLineNumber(node);
                if (node.getIntProp(20, 0) != 0) {
                    if (child == null || this.compilerEnv.getLanguageVersion() < 200) {
                        addIcode(-63);
                        addUint16(this.lineNumber & 65535);
                    } else {
                        visitExpression(child, 1);
                        addIcode(-65);
                        addUint16(this.lineNumber & 65535);
                        stackChange(-1);
                    }
                } else if (child == null) {
                    addIcode(-22);
                } else {
                    visitExpression(child, 1);
                    addToken(4);
                    stackChange(-1);
                }
                if (this.stackDepth != initialStackDepth) {
                    throw Kit.codeBug();
                }
                return;
            case 5:
                Node target = ((Jump) node).target;
                addGoto(target, type);
                if (this.stackDepth != initialStackDepth) {
                    throw Kit.codeBug();
                }
                return;
            case 6:
            case 7:
                Node target2 = ((Jump) node).target;
                visitExpression(child, 0);
                addGoto(target2, type);
                stackChange(-1);
                if (this.stackDepth != initialStackDepth) {
                    throw Kit.codeBug();
                }
                return;
            case 50:
                updateLineNumber(node);
                visitExpression(child, 0);
                addToken(50);
                addUint16(this.lineNumber & 65535);
                stackChange(-1);
                if (this.stackDepth != initialStackDepth) {
                    throw Kit.codeBug();
                }
                return;
            case 51:
                updateLineNumber(node);
                addIndexOp(51, getLocalBlockRef(node));
                if (this.stackDepth != initialStackDepth) {
                    throw Kit.codeBug();
                }
                return;
            case 57:
                int localIndex = getLocalBlockRef(node);
                int scopeIndex = node.getExistingIntProp(14);
                String name = child.getString();
                visitExpression(child.getNext(), 0);
                addStringPrefix(name);
                addIndexPrefix(localIndex);
                addToken(57);
                addUint8(scopeIndex != 0 ? 1 : 0);
                stackChange(-1);
                if (this.stackDepth != initialStackDepth) {
                    throw Kit.codeBug();
                }
                return;
            case 58:
            case 59:
            case 60:
            case 61:
                visitExpression(child, 0);
                addIndexOp(type, getLocalBlockRef(node));
                stackChange(-1);
                if (this.stackDepth != initialStackDepth) {
                    throw Kit.codeBug();
                }
                return;
            case 65:
                updateLineNumber(node);
                addToken(65);
                if (this.stackDepth != initialStackDepth) {
                    throw Kit.codeBug();
                }
                return;
            case 82:
                Jump tryNode = (Jump) node;
                int exceptionObjectLocal = getLocalBlockRef(tryNode);
                int scopeLocal = allocLocal();
                addIndexOp(-13, scopeLocal);
                int tryStart = this.iCodeTop;
                boolean savedFlag = this.itsInTryFlag;
                this.itsInTryFlag = true;
                while (child != null) {
                    visitStatement(child, initialStackDepth);
                    child = child.getNext();
                }
                this.itsInTryFlag = savedFlag;
                Node catchTarget = tryNode.target;
                if (catchTarget != null) {
                    int catchStartPC = this.labelTable[getTargetLabel(catchTarget)];
                    addExceptionHandler(tryStart, catchStartPC, catchStartPC, false, exceptionObjectLocal, scopeLocal);
                }
                Node finallyTarget = tryNode.getFinally();
                if (finallyTarget != null) {
                    int finallyStartPC = this.labelTable[getTargetLabel(finallyTarget)];
                    addExceptionHandler(tryStart, finallyStartPC, finallyStartPC, true, exceptionObjectLocal, scopeLocal);
                }
                addIndexOp(-56, scopeLocal);
                releaseLocal(scopeLocal);
                if (this.stackDepth != initialStackDepth) {
                    throw Kit.codeBug();
                }
                return;
            case 110:
                int fnIndex = node.getExistingIntProp(1);
                int fnType = this.scriptOrFn.getFunctionNode(fnIndex).getFunctionType();
                if (fnType == 3) {
                    addIndexOp(-20, fnIndex);
                } else if (fnType != 1) {
                    throw Kit.codeBug();
                }
                if (!this.itsInFunctionFlag) {
                    addIndexOp(-19, fnIndex);
                    stackChange(1);
                    addIcode(-5);
                    stackChange(-1);
                }
                if (this.stackDepth != initialStackDepth) {
                    throw Kit.codeBug();
                }
                return;
            case 115:
                updateLineNumber(node);
                visitExpression(child, 0);
                Node next = child.getNext();
                while (true) {
                    Jump caseNode = (Jump) next;
                    if (caseNode != null) {
                        if (caseNode.getType() != 116) {
                            throw badTree(caseNode);
                        }
                        Node test = caseNode.getFirstChild();
                        addIcode(-1);
                        stackChange(1);
                        visitExpression(test, 0);
                        addToken(46);
                        stackChange(-1);
                        addGoto(caseNode.target, -6);
                        stackChange(-1);
                        next = caseNode.getNext();
                    } else {
                        addIcode(-4);
                        stackChange(-1);
                    }
                }
                if (this.stackDepth != initialStackDepth) {
                    throw Kit.codeBug();
                }
                return;
            case 124:
            case 129:
            case 130:
            case 131:
            case 133:
                updateLineNumber(node);
                while (child != null) {
                    visitStatement(child, initialStackDepth);
                    child = child.getNext();
                }
                if (this.stackDepth != initialStackDepth) {
                    throw Kit.codeBug();
                }
                return;
            case 126:
                stackChange(1);
                int finallyRegister = getLocalBlockRef(node);
                addIndexOp(-24, finallyRegister);
                stackChange(-1);
                while (child != null) {
                    visitStatement(child, initialStackDepth);
                    child = child.getNext();
                }
                addIndexOp(-25, finallyRegister);
                if (this.stackDepth != initialStackDepth) {
                    throw Kit.codeBug();
                }
                return;
            case 132:
                markTargetLabel(node);
                if (this.stackDepth != initialStackDepth) {
                    throw Kit.codeBug();
                }
                return;
            case 134:
            case 135:
                updateLineNumber(node);
                visitExpression(child, 0);
                addIcode(type == 134 ? -4 : -5);
                stackChange(-1);
                if (this.stackDepth != initialStackDepth) {
                    throw Kit.codeBug();
                }
                return;
            case 136:
                Node target3 = ((Jump) node).target;
                addGoto(target3, -23);
                if (this.stackDepth != initialStackDepth) {
                    throw Kit.codeBug();
                }
                return;
            case 137:
                while (child != null) {
                    visitStatement(child, initialStackDepth);
                    child = child.getNext();
                }
                if (this.stackDepth != initialStackDepth) {
                    throw Kit.codeBug();
                }
                return;
            case 142:
                int local = allocLocal();
                node.putIntProp(2, local);
                updateLineNumber(node);
                while (child != null) {
                    visitStatement(child, initialStackDepth);
                    child = child.getNext();
                }
                addIndexOp(-56, local);
                releaseLocal(local);
                if (this.stackDepth != initialStackDepth) {
                    throw Kit.codeBug();
                }
                return;
            case 161:
                addIcode(-64);
                if (this.stackDepth != initialStackDepth) {
                    throw Kit.codeBug();
                }
                return;
            default:
                throw badTree(node);
        }
    }

    private void visitExpression(Node node, int contextFlags) {
        int type = node.getType();
        Node child = node.getFirstChild();
        int savedStackDepth = this.stackDepth;
        switch (type) {
            case 8:
            case 74:
                String name = child.getString();
                visitExpression(child, 0);
                visitExpression(child.getNext(), 0);
                addStringOp(type, name);
                stackChange(-1);
                break;
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
            case 36:
            case 46:
            case 47:
            case 52:
            case 53:
                visitExpression(child, 0);
                visitExpression(child.getNext(), 0);
                addToken(type);
                stackChange(-1);
                break;
            case 26:
            case 27:
            case 28:
            case 29:
            case 32:
            case 127:
                visitExpression(child, 0);
                if (type == 127) {
                    addIcode(-4);
                    addIcode(-50);
                } else {
                    addToken(type);
                }
                break;
            case 30:
            case 38:
            case 71:
                if (type == 30) {
                    visitExpression(child, 0);
                } else {
                    generateCallFunAndThis(child);
                }
                int argCount = 0;
                while (true) {
                    Node next = child.getNext();
                    child = next;
                    if (next != null) {
                        visitExpression(child, 0);
                        argCount++;
                    } else {
                        int callType = node.getIntProp(10, 0);
                        if (type != 71 && callType != 0) {
                            addIndexOp(-21, argCount);
                            addUint8(callType);
                            addUint8(type == 30 ? 1 : 0);
                            addUint16(this.lineNumber & 65535);
                        } else {
                            if (type == 38 && (contextFlags & 1) != 0 && !this.compilerEnv.isGenerateDebugInfo() && !this.itsInTryFlag) {
                                type = -55;
                            }
                            addIndexOp(type, argCount);
                        }
                        if (type == 30) {
                            stackChange(-argCount);
                        } else {
                            stackChange((-1) - argCount);
                        }
                        if (argCount > this.itsData.itsMaxCalleeArgs) {
                            this.itsData.itsMaxCalleeArgs = argCount;
                        }
                    }
                    break;
                }
                break;
            case 31:
                boolean isName = child.getType() == 49;
                visitExpression(child, 0);
                visitExpression(child.getNext(), 0);
                if (isName) {
                    addIcode(0);
                } else {
                    addToken(31);
                }
                stackChange(-1);
                break;
            case 33:
            case 34:
                visitExpression(child, 0);
                addStringOp(type, child.getNext().getString());
                break;
            case 35:
            case 140:
                visitExpression(child, 0);
                Node child2 = child.getNext();
                String property = child2.getString();
                Node child3 = child2.getNext();
                if (type == 140) {
                    addIcode(-1);
                    stackChange(1);
                    addStringOp(33, property);
                    stackChange(-1);
                }
                visitExpression(child3, 0);
                addStringOp(35, property);
                stackChange(-1);
                break;
            case 37:
            case 141:
                visitExpression(child, 0);
                Node child4 = child.getNext();
                visitExpression(child4, 0);
                Node child5 = child4.getNext();
                if (type == 141) {
                    addIcode(-2);
                    stackChange(2);
                    addToken(36);
                    stackChange(-1);
                    stackChange(-1);
                }
                visitExpression(child5, 0);
                addToken(37);
                stackChange(-2);
                break;
            case 39:
            case 41:
            case 49:
                addStringOp(type, node.getString());
                stackChange(1);
                break;
            case 40:
                double num = node.getDouble();
                int inum = (int) num;
                if (inum == num) {
                    if (inum == 0) {
                        addIcode(-51);
                        if (1.0d / num < 0.0d) {
                            addToken(29);
                        }
                    } else if (inum == 1) {
                        addIcode(-52);
                    } else if (((short) inum) == inum) {
                        addIcode(-27);
                        addUint16(inum & 65535);
                    } else {
                        addIcode(-28);
                        addInt(inum);
                    }
                } else {
                    int index = getDoubleIndex(num);
                    addIndexOp(40, index);
                }
                stackChange(1);
                break;
            case 42:
            case 43:
            case 44:
            case 45:
            case 64:
                addToken(type);
                stackChange(1);
                break;
            case 48:
                int index2 = node.getExistingIntProp(4);
                addIndexOp(48, index2);
                stackChange(1);
                break;
            case 50:
            case 51:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 65:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
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
            case 109:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 120:
            case 121:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 128:
            case 129:
            case 130:
            case 131:
            case 132:
            case 133:
            case 134:
            case 135:
            case 136:
            case 137:
            case 142:
            case 144:
            case 145:
            case 146:
            case 148:
            case 149:
            case 150:
            case 151:
            case 152:
            case 153:
            case 154:
            case 155:
            case 159:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            default:
                throw badTree(node);
            case 54:
                int localIndex = getLocalBlockRef(node);
                addIndexOp(54, localIndex);
                stackChange(1);
                break;
            case 55:
                if (this.itsData.itsNeedsActivation) {
                    Kit.codeBug();
                }
                int index3 = this.scriptOrFn.getIndexForNameNode(node);
                addVarOp(55, index3);
                stackChange(1);
                break;
            case 56:
                if (this.itsData.itsNeedsActivation) {
                    Kit.codeBug();
                }
                int index4 = this.scriptOrFn.getIndexForNameNode(child);
                visitExpression(child.getNext(), 0);
                addVarOp(56, index4);
                break;
            case 62:
            case 63:
                addIndexOp(type, getLocalBlockRef(node));
                stackChange(1);
                break;
            case 66:
            case 67:
                visitLiteral(node, child);
                break;
            case 68:
            case 70:
                visitExpression(child, 0);
                addToken(type);
                break;
            case 69:
            case 143:
                visitExpression(child, 0);
                Node child6 = child.getNext();
                if (type == 143) {
                    addIcode(-1);
                    stackChange(1);
                    addToken(68);
                    stackChange(-1);
                }
                visitExpression(child6, 0);
                addToken(69);
                stackChange(-1);
                break;
            case 72:
                visitExpression(child, 0);
                addStringOp(type, (String) node.getProp(17));
                break;
            case 73:
            case 166:
                if (child != null) {
                    visitExpression(child, 0);
                } else {
                    addIcode(-50);
                    stackChange(1);
                }
                if (type == 73) {
                    addToken(73);
                } else {
                    addIcode(-66);
                }
                addUint16(node.getLineno() & 65535);
                break;
            case 75:
            case 76:
            case 77:
                visitExpression(child, 0);
                addToken(type);
                break;
            case 78:
            case 79:
            case 80:
            case 81:
                int memberTypeFlags = node.getIntProp(16, 0);
                int childCount = 0;
                do {
                    visitExpression(child, 0);
                    childCount++;
                    child = child.getNext();
                } while (child != null);
                addIndexOp(type, memberTypeFlags);
                stackChange(1 - childCount);
                break;
            case 90:
                Node lastChild = node.getLastChild();
                while (child != lastChild) {
                    visitExpression(child, 0);
                    addIcode(-4);
                    stackChange(-1);
                    child = child.getNext();
                }
                visitExpression(child, contextFlags & 1);
                break;
            case 103:
                Node ifThen = child.getNext();
                Node ifElse = ifThen.getNext();
                visitExpression(child, 0);
                int elseJumpStart = this.iCodeTop;
                addGotoOp(7);
                stackChange(-1);
                visitExpression(ifThen, contextFlags & 1);
                int afterElseJumpStart = this.iCodeTop;
                addGotoOp(5);
                resolveForwardGoto(elseJumpStart);
                this.stackDepth = savedStackDepth;
                visitExpression(ifElse, contextFlags & 1);
                resolveForwardGoto(afterElseJumpStart);
                break;
            case 105:
            case 106:
                visitExpression(child, 0);
                addIcode(-1);
                stackChange(1);
                int afterSecondJumpStart = this.iCodeTop;
                int jump = type == 106 ? 7 : 6;
                addGotoOp(jump);
                stackChange(-1);
                addIcode(-4);
                stackChange(-1);
                visitExpression(child.getNext(), contextFlags & 1);
                resolveForwardGoto(afterSecondJumpStart);
                break;
            case 107:
            case 108:
                visitIncDec(node, child);
                break;
            case 110:
                int fnIndex = node.getExistingIntProp(1);
                FunctionNode fn = this.scriptOrFn.getFunctionNode(fnIndex);
                if (fn.getFunctionType() != 2 && fn.getFunctionType() != 4) {
                    throw Kit.codeBug();
                }
                addIndexOp(-19, fnIndex);
                stackChange(1);
                break;
                break;
            case 138:
                int index5 = -1;
                if (this.itsInFunctionFlag && !this.itsData.itsNeedsActivation) {
                    index5 = this.scriptOrFn.getIndexForNameNode(node);
                }
                if (index5 == -1) {
                    addStringOp(-14, node.getString());
                    stackChange(1);
                } else {
                    addVarOp(55, index5);
                    stackChange(1);
                    addToken(32);
                }
                break;
            case 139:
                stackChange(1);
                break;
            case 147:
                updateLineNumber(node);
                visitExpression(child, 0);
                addIcode(-53);
                stackChange(-1);
                int queryPC = this.iCodeTop;
                visitExpression(child.getNext(), 0);
                addBackwardGoto(-54, queryPC);
                break;
            case 156:
                String name2 = child.getString();
                visitExpression(child, 0);
                visitExpression(child.getNext(), 0);
                addStringOp(-59, name2);
                stackChange(-1);
                break;
            case 157:
                if (this.itsData.itsNeedsActivation) {
                    Kit.codeBug();
                }
                int index6 = this.scriptOrFn.getIndexForNameNode(child);
                visitExpression(child.getNext(), 0);
                addVarOp(157, index6);
                break;
            case 158:
                visitArrayComprehension(node, child, child.getNext());
                break;
            case 160:
                Node enterWith = node.getFirstChild();
                Node with = enterWith.getNext();
                visitExpression(enterWith.getFirstChild(), 0);
                addToken(2);
                stackChange(-1);
                visitExpression(with.getFirstChild(), 0);
                addToken(3);
                break;
        }
        if (savedStackDepth + 1 != this.stackDepth) {
            Kit.codeBug();
        }
    }

    private void generateCallFunAndThis(Node left) {
        int type = left.getType();
        switch (type) {
            case 33:
            case 36:
                Node target = left.getFirstChild();
                visitExpression(target, 0);
                Node id = target.getNext();
                if (type == 33) {
                    String property = id.getString();
                    addStringOp(-16, property);
                    stackChange(1);
                } else {
                    visitExpression(id, 0);
                    addIcode(-17);
                }
                break;
            case 39:
                String name = left.getString();
                addStringOp(-15, name);
                stackChange(2);
                break;
            default:
                visitExpression(left, 0);
                addIcode(-18);
                stackChange(1);
                break;
        }
    }

    private void visitIncDec(Node node, Node child) {
        int incrDecrMask = node.getExistingIntProp(13);
        int childType = child.getType();
        switch (childType) {
            case 33:
                Node object = child.getFirstChild();
                visitExpression(object, 0);
                String property = object.getNext().getString();
                addStringOp(-9, property);
                addUint8(incrDecrMask);
                return;
            case 36:
                Node object2 = child.getFirstChild();
                visitExpression(object2, 0);
                Node index = object2.getNext();
                visitExpression(index, 0);
                addIcode(-10);
                addUint8(incrDecrMask);
                stackChange(-1);
                return;
            case 39:
                String name = child.getString();
                addStringOp(-8, name);
                addUint8(incrDecrMask);
                stackChange(1);
                return;
            case 55:
                if (this.itsData.itsNeedsActivation) {
                    Kit.codeBug();
                }
                int i = this.scriptOrFn.getIndexForNameNode(child);
                addVarOp(-7, i);
                addUint8(incrDecrMask);
                stackChange(1);
                return;
            case 68:
                Node ref = child.getFirstChild();
                visitExpression(ref, 0);
                addIcode(-11);
                addUint8(incrDecrMask);
                return;
            default:
                throw badTree(node);
        }
    }

    private void visitLiteral(Node node, Node child) {
        int count;
        int type = node.getType();
        Object[] propertyIds = null;
        if (type == 66) {
            count = 0;
            Node next = child;
            while (true) {
                Node n = next;
                if (n == null) {
                    break;
                }
                count++;
                next = n.getNext();
            }
        } else if (type == 67) {
            propertyIds = (Object[]) node.getProp(12);
            count = propertyIds.length;
        } else {
            throw badTree(node);
        }
        addIndexOp(-29, count);
        stackChange(2);
        while (child != null) {
            int childType = child.getType();
            if (childType == 152) {
                visitExpression(child.getFirstChild(), 0);
                addIcode(-57);
            } else if (childType == 153) {
                visitExpression(child.getFirstChild(), 0);
                addIcode(-58);
            } else if (childType == 164) {
                visitExpression(child.getFirstChild(), 0);
                addIcode(-30);
            } else {
                visitExpression(child, 0);
                addIcode(-30);
            }
            stackChange(-1);
            child = child.getNext();
        }
        if (type == 66) {
            int[] skipIndexes = (int[]) node.getProp(11);
            if (skipIndexes == null) {
                addToken(66);
            } else {
                int index = this.literalIds.size();
                this.literalIds.add(skipIndexes);
                addIndexOp(-31, index);
            }
        } else {
            int index2 = this.literalIds.size();
            this.literalIds.add(propertyIds);
            addIndexOp(67, index2);
        }
        stackChange(-1);
    }

    private void visitArrayComprehension(Node node, Node initStmt, Node expr) {
        visitStatement(initStmt, this.stackDepth);
        visitExpression(expr, 0);
    }

    private static int getLocalBlockRef(Node node) {
        Node localBlock = (Node) node.getProp(3);
        return localBlock.getExistingIntProp(2);
    }

    private int getTargetLabel(Node target) {
        int label = target.labelId();
        if (label != -1) {
            return label;
        }
        int label2 = this.labelTableTop;
        if (this.labelTable == null || label2 == this.labelTable.length) {
            if (this.labelTable == null) {
                this.labelTable = new int[32];
            } else {
                int[] tmp = new int[this.labelTable.length * 2];
                System.arraycopy(this.labelTable, 0, tmp, 0, label2);
                this.labelTable = tmp;
            }
        }
        this.labelTableTop = label2 + 1;
        this.labelTable[label2] = -1;
        target.labelId(label2);
        return label2;
    }

    private void markTargetLabel(Node target) {
        int label = getTargetLabel(target);
        if (this.labelTable[label] != -1) {
            Kit.codeBug();
        }
        this.labelTable[label] = this.iCodeTop;
    }

    private void addGoto(Node target, int gotoOp) {
        int label = getTargetLabel(target);
        if (label >= this.labelTableTop) {
            Kit.codeBug();
        }
        int targetPC = this.labelTable[label];
        if (targetPC != -1) {
            addBackwardGoto(gotoOp, targetPC);
            return;
        }
        int gotoPC = this.iCodeTop;
        addGotoOp(gotoOp);
        int top = this.fixupTableTop;
        if (this.fixupTable == null || top == this.fixupTable.length) {
            if (this.fixupTable == null) {
                this.fixupTable = new long[40];
            } else {
                long[] tmp = new long[this.fixupTable.length * 2];
                System.arraycopy(this.fixupTable, 0, tmp, 0, top);
                this.fixupTable = tmp;
            }
        }
        this.fixupTableTop = top + 1;
        this.fixupTable[top] = (((long) label) << 32) | ((long) gotoPC);
    }

    private void fixLabelGotos() {
        for (int i = 0; i < this.fixupTableTop; i++) {
            long fixup = this.fixupTable[i];
            int label = (int) (fixup >> 32);
            int jumpSource = (int) fixup;
            int pc = this.labelTable[label];
            if (pc == -1) {
                throw Kit.codeBug();
            }
            resolveGoto(jumpSource, pc);
        }
        this.fixupTableTop = 0;
    }

    private void addBackwardGoto(int gotoOp, int jumpPC) {
        int fromPC = this.iCodeTop;
        if (fromPC <= jumpPC) {
            throw Kit.codeBug();
        }
        addGotoOp(gotoOp);
        resolveGoto(fromPC, jumpPC);
    }

    private void resolveForwardGoto(int fromPC) {
        if (this.iCodeTop < fromPC + 3) {
            throw Kit.codeBug();
        }
        resolveGoto(fromPC, this.iCodeTop);
    }

    private void resolveGoto(int fromPC, int jumpPC) {
        int offset = jumpPC - fromPC;
        if (0 <= offset && offset <= 2) {
            throw Kit.codeBug();
        }
        int offsetSite = fromPC + 1;
        if (offset != ((short) offset)) {
            if (this.itsData.longJumps == null) {
                this.itsData.longJumps = new UintMap();
            }
            this.itsData.longJumps.put(offsetSite, jumpPC);
            offset = 0;
        }
        byte[] array = this.itsData.itsICode;
        array[offsetSite] = (byte) (offset >> 8);
        array[offsetSite + 1] = (byte) offset;
    }

    private void addToken(int token) {
        if (!Icode.validTokenCode(token)) {
            throw Kit.codeBug();
        }
        addUint8(token);
    }

    private void addIcode(int icode) {
        if (!Icode.validIcode(icode)) {
            throw Kit.codeBug();
        }
        addUint8(icode & 255);
    }

    private void addUint8(int value) {
        if ((value & (-256)) != 0) {
            throw Kit.codeBug();
        }
        byte[] array = this.itsData.itsICode;
        int top = this.iCodeTop;
        if (top == array.length) {
            array = increaseICodeCapacity(1);
        }
        array[top] = (byte) value;
        this.iCodeTop = top + 1;
    }

    private void addUint16(int value) {
        if ((value & (-65536)) != 0) {
            throw Kit.codeBug();
        }
        byte[] array = this.itsData.itsICode;
        int top = this.iCodeTop;
        if (top + 2 > array.length) {
            array = increaseICodeCapacity(2);
        }
        array[top] = (byte) (value >>> 8);
        array[top + 1] = (byte) value;
        this.iCodeTop = top + 2;
    }

    private void addInt(int i) {
        byte[] array = this.itsData.itsICode;
        int top = this.iCodeTop;
        if (top + 4 > array.length) {
            array = increaseICodeCapacity(4);
        }
        array[top] = (byte) (i >>> 24);
        array[top + 1] = (byte) (i >>> 16);
        array[top + 2] = (byte) (i >>> 8);
        array[top + 3] = (byte) i;
        this.iCodeTop = top + 4;
    }

    private int getDoubleIndex(double num) {
        int index = this.doubleTableTop;
        if (index == 0) {
            this.itsData.itsDoubleTable = new double[64];
        } else if (this.itsData.itsDoubleTable.length == index) {
            double[] na = new double[index * 2];
            System.arraycopy(this.itsData.itsDoubleTable, 0, na, 0, index);
            this.itsData.itsDoubleTable = na;
        }
        this.itsData.itsDoubleTable[index] = num;
        this.doubleTableTop = index + 1;
        return index;
    }

    private void addGotoOp(int gotoOp) {
        byte[] array = this.itsData.itsICode;
        int top = this.iCodeTop;
        if (top + 3 > array.length) {
            array = increaseICodeCapacity(3);
        }
        array[top] = (byte) gotoOp;
        this.iCodeTop = top + 1 + 2;
    }

    private void addVarOp(int op, int varIndex) {
        switch (op) {
            case Msi.INSTALLSTATE_NOTUSED /* -7 */:
                break;
            case 55:
            case 56:
                if (varIndex < 128) {
                    addIcode(op == 55 ? -48 : -49);
                    addUint8(varIndex);
                    return;
                }
                break;
            case 157:
                if (varIndex < 128) {
                    addIcode(-61);
                    addUint8(varIndex);
                    return;
                } else {
                    addIndexOp(-60, varIndex);
                    return;
                }
            default:
                throw Kit.codeBug();
        }
        addIndexOp(op, varIndex);
    }

    private void addStringOp(int op, String str) {
        addStringPrefix(str);
        if (Icode.validIcode(op)) {
            addIcode(op);
        } else {
            addToken(op);
        }
    }

    private void addIndexOp(int op, int index) {
        addIndexPrefix(index);
        if (Icode.validIcode(op)) {
            addIcode(op);
        } else {
            addToken(op);
        }
    }

    private void addStringPrefix(String str) {
        int index = this.strings.get(str, -1);
        if (index == -1) {
            index = this.strings.size();
            this.strings.put(str, index);
        }
        if (index < 4) {
            addIcode((-41) - index);
            return;
        }
        if (index <= 255) {
            addIcode(-45);
            addUint8(index);
        } else if (index <= 65535) {
            addIcode(-46);
            addUint16(index);
        } else {
            addIcode(-47);
            addInt(index);
        }
    }

    private void addIndexPrefix(int index) {
        if (index < 0) {
            Kit.codeBug();
        }
        if (index < 6) {
            addIcode((-32) - index);
            return;
        }
        if (index <= 255) {
            addIcode(-38);
            addUint8(index);
        } else if (index <= 65535) {
            addIcode(-39);
            addUint16(index);
        } else {
            addIcode(-40);
            addInt(index);
        }
    }

    private void addExceptionHandler(int icodeStart, int icodeEnd, int handlerStart, boolean isFinally, int exceptionObjectLocal, int scopeLocal) {
        int top = this.exceptionTableTop;
        int[] table = this.itsData.itsExceptionTable;
        if (table == null) {
            if (top != 0) {
                Kit.codeBug();
            }
            table = new int[12];
            this.itsData.itsExceptionTable = table;
        } else if (table.length == top) {
            table = new int[table.length * 2];
            System.arraycopy(this.itsData.itsExceptionTable, 0, table, 0, top);
            this.itsData.itsExceptionTable = table;
        }
        table[top + 0] = icodeStart;
        table[top + 1] = icodeEnd;
        table[top + 2] = handlerStart;
        table[top + 3] = isFinally ? 1 : 0;
        table[top + 4] = exceptionObjectLocal;
        table[top + 5] = scopeLocal;
        this.exceptionTableTop = top + 6;
    }

    private byte[] increaseICodeCapacity(int extraSize) {
        int capacity = this.itsData.itsICode.length;
        int top = this.iCodeTop;
        if (top + extraSize <= capacity) {
            throw Kit.codeBug();
        }
        int capacity2 = capacity * 2;
        if (top + extraSize > capacity2) {
            capacity2 = top + extraSize;
        }
        byte[] array = new byte[capacity2];
        System.arraycopy(this.itsData.itsICode, 0, array, 0, top);
        this.itsData.itsICode = array;
        return array;
    }

    private void stackChange(int change) {
        if (change <= 0) {
            this.stackDepth += change;
            return;
        }
        int newDepth = this.stackDepth + change;
        if (newDepth > this.itsData.itsMaxStack) {
            this.itsData.itsMaxStack = newDepth;
        }
        this.stackDepth = newDepth;
    }

    private int allocLocal() {
        int localSlot = this.localTop;
        this.localTop++;
        if (this.localTop > this.itsData.itsMaxLocals) {
            this.itsData.itsMaxLocals = this.localTop;
        }
        return localSlot;
    }

    private void releaseLocal(int localSlot) {
        this.localTop--;
        if (localSlot != this.localTop) {
            Kit.codeBug();
        }
    }
}
