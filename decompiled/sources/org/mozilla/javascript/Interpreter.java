package org.mozilla.javascript;

import com.sun.jna.platform.win32.Msi;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.Wincon;
import es.gob.afirma.core.signers.der.DerValue;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.debug.DebugFrame;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/Interpreter.class */
public final class Interpreter extends Icode implements Evaluator {
    InterpreterData itsData;
    static final int EXCEPTION_TRY_START_SLOT = 0;
    static final int EXCEPTION_TRY_END_SLOT = 1;
    static final int EXCEPTION_HANDLER_SLOT = 2;
    static final int EXCEPTION_TYPE_SLOT = 3;
    static final int EXCEPTION_LOCAL_SLOT = 4;
    static final int EXCEPTION_SCOPE_SLOT = 5;
    static final int EXCEPTION_SLOT_SIZE = 6;

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/Interpreter$CallFrame.class */
    private static class CallFrame implements Cloneable, Serializable {
        private static final long serialVersionUID = -2843792508994958978L;
        CallFrame parentFrame;
        int frameIndex;
        boolean frozen;
        final InterpretedFunction fnOrScript;
        final InterpreterData idata;
        Object[] stack;
        int[] stackAttributes;
        double[] sDbl;
        final CallFrame varSource;
        final int localShift;
        final int emptyStackTop;
        final DebugFrame debuggerFrame;
        final boolean useActivation;
        boolean isContinuationsTopFrame;
        final Scriptable thisObj;
        Object result;
        double resultDbl;
        int pc;
        int pcPrevBranch;
        int pcSourceLineStart;
        Scriptable scope;
        int savedStackTop;
        int savedCallOp;
        Object throwable;

        CallFrame(Context cx, Scriptable thisObj, InterpretedFunction fnOrScript, CallFrame parentFrame) {
            this.idata = fnOrScript.idata;
            this.debuggerFrame = cx.debugger != null ? cx.debugger.getFrame(cx, this.idata) : null;
            this.useActivation = this.debuggerFrame != null || this.idata.itsNeedsActivation;
            this.emptyStackTop = (this.idata.itsMaxVars + this.idata.itsMaxLocals) - 1;
            this.fnOrScript = fnOrScript;
            this.varSource = this;
            this.localShift = this.idata.itsMaxVars;
            this.thisObj = thisObj;
            this.parentFrame = parentFrame;
            this.frameIndex = parentFrame == null ? 0 : parentFrame.frameIndex + 1;
            if (this.frameIndex > cx.getMaximumInterpreterStackDepth()) {
                throw Context.reportRuntimeError("Exceeded maximum stack depth");
            }
            this.result = Undefined.instance;
            this.pcSourceLineStart = this.idata.firstLinePC;
            this.savedStackTop = this.emptyStackTop;
        }

        void initializeArgs(Context cx, Scriptable callerScope, Object[] args, double[] argsDbl, int argShift, int argCount) {
            if (this.useActivation) {
                if (argsDbl != null) {
                    args = Interpreter.getArgsArray(args, argsDbl, argShift, argCount);
                }
                argShift = 0;
                argsDbl = null;
            }
            if (this.idata.itsFunctionType != 0) {
                this.scope = this.fnOrScript.getParentScope();
                if (this.useActivation) {
                    if (this.idata.itsFunctionType == 4) {
                        this.scope = ScriptRuntime.createArrowFunctionActivation(this.fnOrScript, this.scope, args, this.idata.isStrict);
                    } else {
                        this.scope = ScriptRuntime.createFunctionActivation(this.fnOrScript, this.scope, args, this.idata.isStrict);
                    }
                }
            } else {
                this.scope = callerScope;
                ScriptRuntime.initScript(this.fnOrScript, this.thisObj, cx, this.scope, this.fnOrScript.idata.evalScriptFlag);
            }
            if (this.idata.itsNestedFunctions != null) {
                if (this.idata.itsFunctionType != 0 && !this.idata.itsNeedsActivation) {
                    Kit.codeBug();
                }
                for (int i = 0; i < this.idata.itsNestedFunctions.length; i++) {
                    InterpreterData fdata = this.idata.itsNestedFunctions[i];
                    if (fdata.itsFunctionType == 1) {
                        Interpreter.initFunction(cx, this.scope, this.fnOrScript, i);
                    }
                }
            }
            int maxFrameArray = this.idata.itsMaxFrameArray;
            if (maxFrameArray != this.emptyStackTop + this.idata.itsMaxStack + 1) {
                Kit.codeBug();
            }
            this.stack = new Object[maxFrameArray];
            this.stackAttributes = new int[maxFrameArray];
            this.sDbl = new double[maxFrameArray];
            int varCount = this.idata.getParamAndVarCount();
            for (int i2 = 0; i2 < varCount; i2++) {
                if (this.idata.getParamOrVarConst(i2)) {
                    this.stackAttributes[i2] = 13;
                }
            }
            int definedArgs = this.idata.argCount;
            if (definedArgs > argCount) {
                definedArgs = argCount;
            }
            System.arraycopy(args, argShift, this.stack, 0, definedArgs);
            if (argsDbl != null) {
                System.arraycopy(argsDbl, argShift, this.sDbl, 0, definedArgs);
            }
            for (int i3 = definedArgs; i3 != this.idata.itsMaxVars; i3++) {
                this.stack[i3] = Undefined.instance;
            }
        }

        CallFrame cloneFrozen() {
            if (!this.frozen) {
                Kit.codeBug();
            }
            try {
                CallFrame copy = (CallFrame) clone();
                copy.stack = (Object[]) this.stack.clone();
                copy.stackAttributes = (int[]) this.stackAttributes.clone();
                copy.sDbl = (double[]) this.sDbl.clone();
                copy.frozen = false;
                return copy;
            } catch (CloneNotSupportedException e) {
                throw new IllegalStateException();
            }
        }

        public boolean equals(Object other) {
            if (other instanceof CallFrame) {
                Context cx = Context.enter();
                try {
                    if (!ScriptRuntime.hasTopCall(cx)) {
                        Scriptable top = ScriptableObject.getTopLevelScope(this.scope);
                        return ((Boolean) ScriptRuntime.doTopCall((c, scope, thisObj, args) -> {
                            return equalsInTopScope(other);
                        }, cx, top, top, ScriptRuntime.emptyArgs, isStrictTopFrame())).booleanValue();
                    }
                    return equalsInTopScope(other).booleanValue();
                } finally {
                    Context.exit();
                }
            }
            return false;
        }

        public int hashCode() {
            int i;
            int depth = 0;
            CallFrame f = this;
            int h = 0;
            do {
                h = (31 * ((31 * h) + f.pc)) + f.idata.icodeHashCode();
                f = f.parentFrame;
                if (f == null) {
                    break;
                }
                i = depth;
                depth++;
            } while (i < 8);
            return h;
        }

        private Boolean equalsInTopScope(Object other) {
            return (Boolean) EqualObjectGraphs.withThreadLocal(eq -> {
                return equals(this, (CallFrame) other, eq);
            });
        }

        private boolean isStrictTopFrame() {
            CallFrame callFrame = this;
            while (true) {
                CallFrame f = callFrame;
                CallFrame p = f.parentFrame;
                if (p == null) {
                    return f.idata.isStrict;
                }
                callFrame = p;
            }
        }

        private static Boolean equals(CallFrame f1, CallFrame f2, EqualObjectGraphs equal) {
            while (f1 != f2) {
                if (f1 == null || f2 == null) {
                    return Boolean.FALSE;
                }
                if (!f1.fieldsEqual(f2, equal)) {
                    return Boolean.FALSE;
                }
                f1 = f1.parentFrame;
                f2 = f2.parentFrame;
            }
            return Boolean.TRUE;
        }

        private boolean fieldsEqual(CallFrame other, EqualObjectGraphs equal) {
            return this.frameIndex == other.frameIndex && this.pc == other.pc && Interpreter.compareIdata(this.idata, other.idata) && equal.equalGraphs(this.varSource.stack, other.varSource.stack) && Arrays.equals(this.varSource.sDbl, other.varSource.sDbl) && equal.equalGraphs(this.thisObj, other.thisObj) && equal.equalGraphs(this.fnOrScript, other.fnOrScript) && equal.equalGraphs(this.scope, other.scope);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean compareIdata(InterpreterData i1, InterpreterData i2) {
        return i1 == i2 || Objects.equals(getEncodedSource(i1), getEncodedSource(i2));
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/Interpreter$ContinuationJump.class */
    private static final class ContinuationJump implements Serializable {
        private static final long serialVersionUID = 7687739156004308247L;
        CallFrame capturedFrame;
        CallFrame branchFrame;
        Object result;
        double resultDbl;

        ContinuationJump(NativeContinuation c, CallFrame current) {
            this.capturedFrame = (CallFrame) c.getImplementation();
            if (this.capturedFrame == null || current == null) {
                this.branchFrame = null;
                return;
            }
            CallFrame chain1 = this.capturedFrame;
            CallFrame chain2 = current;
            int diff = chain1.frameIndex - chain2.frameIndex;
            if (diff != 0) {
                if (diff < 0) {
                    chain1 = current;
                    chain2 = this.capturedFrame;
                    diff = -diff;
                }
                do {
                    chain1 = chain1.parentFrame;
                    diff--;
                } while (diff != 0);
                if (chain1.frameIndex != chain2.frameIndex) {
                    Kit.codeBug();
                }
            }
            while (chain1 != chain2 && chain1 != null) {
                chain1 = chain1.parentFrame;
                chain2 = chain2.parentFrame;
            }
            this.branchFrame = chain1;
            if (this.branchFrame != null && !this.branchFrame.frozen) {
                Kit.codeBug();
            }
        }
    }

    private static CallFrame captureFrameForGenerator(CallFrame frame) {
        frame.frozen = true;
        CallFrame result = frame.cloneFrozen();
        frame.frozen = false;
        result.parentFrame = null;
        result.frameIndex = 0;
        return result;
    }

    @Override // org.mozilla.javascript.Evaluator
    public Object compile(CompilerEnvirons compilerEnv, ScriptNode tree, String encodedSource, boolean returnFunction) {
        CodeGenerator cgen = new CodeGenerator();
        this.itsData = cgen.compile(compilerEnv, tree, encodedSource, returnFunction);
        return this.itsData;
    }

    @Override // org.mozilla.javascript.Evaluator
    public Script createScriptObject(Object bytecode, Object staticSecurityDomain) {
        if (bytecode != this.itsData) {
            Kit.codeBug();
        }
        return InterpretedFunction.createScript(this.itsData, staticSecurityDomain);
    }

    @Override // org.mozilla.javascript.Evaluator
    public void setEvalScriptFlag(Script script) {
        ((InterpretedFunction) script).idata.evalScriptFlag = true;
    }

    @Override // org.mozilla.javascript.Evaluator
    public Function createFunctionObject(Context cx, Scriptable scope, Object bytecode, Object staticSecurityDomain) {
        if (bytecode != this.itsData) {
            Kit.codeBug();
        }
        return InterpretedFunction.createFunction(cx, scope, this.itsData, staticSecurityDomain);
    }

    private static int getShort(byte[] iCode, int pc) {
        return (iCode[pc] << 8) | (iCode[pc + 1] & 255);
    }

    private static int getIndex(byte[] iCode, int pc) {
        return ((iCode[pc] & 255) << 8) | (iCode[pc + 1] & 255);
    }

    private static int getInt(byte[] iCode, int pc) {
        return (iCode[pc] << 24) | ((iCode[pc + 1] & 255) << 16) | ((iCode[pc + 2] & 255) << 8) | (iCode[pc + 3] & 255);
    }

    private static int getExceptionHandler(CallFrame frame, boolean onlyFinally) {
        int[] exceptionTable = frame.idata.itsExceptionTable;
        if (exceptionTable == null) {
            return -1;
        }
        int pc = frame.pc - 1;
        int best = -1;
        int bestStart = 0;
        int bestEnd = 0;
        for (int i = 0; i != exceptionTable.length; i += 6) {
            int start = exceptionTable[i + 0];
            int end = exceptionTable[i + 1];
            if (start <= pc && pc < end && (!onlyFinally || exceptionTable[i + 3] == 1)) {
                if (best >= 0) {
                    if (bestEnd >= end) {
                        if (bestStart > start) {
                            Kit.codeBug();
                        }
                        if (bestEnd == end) {
                            Kit.codeBug();
                        }
                        best = i;
                        bestStart = start;
                        bestEnd = end;
                    }
                } else {
                    best = i;
                    bestStart = start;
                    bestEnd = end;
                }
            }
        }
        return best;
    }

    static void dumpICode(InterpreterData idata) {
    }

    private static int bytecodeSpan(int bytecode) {
        switch (bytecode) {
            case -66:
            case -65:
            case -63:
            case -62:
            case 50:
            case 73:
                return 3;
            case DerValue.TAG_PRIVATE /* -64 */:
            case -60:
            case -59:
            case -58:
            case -57:
            case -56:
            case -55:
            case -53:
            case -52:
            case -51:
            case -50:
            case -44:
            case -43:
            case -42:
            case -41:
            case -37:
            case -36:
            case -35:
            case -34:
            case -33:
            case WinUser.GCW_ATOM /* -32 */:
            case -31:
            case -30:
            case -29:
            case -25:
            case WinUser.GCLP_WNDPROC /* -24 */:
            case -22:
            case -20:
            case -19:
            case WinUser.GCL_CBWNDEXTRA /* -18 */:
            case -17:
            case -16:
            case -15:
            case -14:
            case -13:
            case -12:
            case Msi.INSTALLSTATE_INCOMPLETE /* -5 */:
            case -4:
            case -3:
            case -2:
            case -1:
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
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
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            default:
                if (validBytecode(bytecode)) {
                    return 1;
                }
                throw Kit.codeBug();
            case -61:
            case -49:
            case -48:
                return 2;
            case -54:
            case -23:
            case -6:
            case 5:
            case 6:
            case 7:
                return 3;
            case -47:
                return 5;
            case -46:
                return 3;
            case -45:
                return 2;
            case -40:
                return 5;
            case -39:
                return 3;
            case -38:
                return 2;
            case -28:
                return 5;
            case -27:
                return 3;
            case WinUser.GCL_STYLE /* -26 */:
                return 3;
            case WinUser.GWL_USERDATA /* -21 */:
                return 5;
            case Wincon.STD_OUTPUT_HANDLE /* -11 */:
            case -10:
            case -9:
            case -8:
            case Msi.INSTALLSTATE_NOTUSED /* -7 */:
                return 2;
            case 57:
                return 2;
        }
    }

    static int[] getLineNumbers(InterpreterData data) {
        UintMap presentLines = new UintMap();
        byte[] iCode = data.itsICode;
        int iCodeLength = iCode.length;
        int i = 0;
        while (true) {
            int pc = i;
            if (pc != iCodeLength) {
                byte b = iCode[pc];
                int span = bytecodeSpan(b);
                if (b == -26) {
                    if (span != 3) {
                        Kit.codeBug();
                    }
                    int line = getIndex(iCode, pc + 1);
                    presentLines.put(line, 0);
                }
                i = pc + span;
            } else {
                return presentLines.getKeys();
            }
        }
    }

    @Override // org.mozilla.javascript.Evaluator
    public void captureStackInfo(RhinoException ex) {
        CallFrame[] array;
        Context cx = Context.getCurrentContext();
        if (cx == null || cx.lastInterpreterFrame == null) {
            ex.interpreterStackInfo = null;
            ex.interpreterLineData = null;
            return;
        }
        if (cx.previousInterpreterInvocations == null || cx.previousInterpreterInvocations.size() == 0) {
            array = new CallFrame[1];
        } else {
            int previousCount = cx.previousInterpreterInvocations.size();
            if (cx.previousInterpreterInvocations.peek() == cx.lastInterpreterFrame) {
                previousCount--;
            }
            array = new CallFrame[previousCount + 1];
            cx.previousInterpreterInvocations.toArray(array);
        }
        array[array.length - 1] = (CallFrame) cx.lastInterpreterFrame;
        int interpreterFrameCount = 0;
        for (int i = 0; i != array.length; i++) {
            interpreterFrameCount += 1 + array[i].frameIndex;
        }
        int[] linePC = new int[interpreterFrameCount];
        int linePCIndex = interpreterFrameCount;
        int i2 = array.length;
        while (i2 != 0) {
            i2--;
            CallFrame callFrame = array[i2];
            while (true) {
                CallFrame frame = callFrame;
                if (frame != null) {
                    linePCIndex--;
                    linePC[linePCIndex] = frame.pcSourceLineStart;
                    callFrame = frame.parentFrame;
                }
            }
        }
        if (linePCIndex != 0) {
            Kit.codeBug();
        }
        ex.interpreterStackInfo = array;
        ex.interpreterLineData = linePC;
    }

    @Override // org.mozilla.javascript.Evaluator
    public String getSourcePositionFromStack(Context cx, int[] linep) {
        CallFrame frame = (CallFrame) cx.lastInterpreterFrame;
        InterpreterData idata = frame.idata;
        if (frame.pcSourceLineStart >= 0) {
            linep[0] = getIndex(idata.itsICode, frame.pcSourceLineStart);
        } else {
            linep[0] = 0;
        }
        return idata.itsSourceFile;
    }

    @Override // org.mozilla.javascript.Evaluator
    public String getPatchedStack(RhinoException ex, String nativeStackTrace) {
        char c;
        StringBuilder sb = new StringBuilder(nativeStackTrace.length() + 1000);
        String lineSeparator = SecurityUtilities.getSystemProperty("line.separator");
        CallFrame[] array = (CallFrame[]) ex.interpreterStackInfo;
        int[] linePC = ex.interpreterLineData;
        int arrayIndex = array.length;
        int linePCIndex = linePC.length;
        int offset = 0;
        while (arrayIndex != 0) {
            arrayIndex--;
            int pos = nativeStackTrace.indexOf("org.mozilla.javascript.Interpreter.interpretLoop", offset);
            if (pos < 0) {
                break;
            }
            int pos2 = pos + "org.mozilla.javascript.Interpreter.interpretLoop".length();
            while (pos2 != nativeStackTrace.length() && (c = nativeStackTrace.charAt(pos2)) != '\n' && c != '\r') {
                pos2++;
            }
            sb.append(nativeStackTrace.substring(offset, pos2));
            offset = pos2;
            CallFrame callFrame = array[arrayIndex];
            while (true) {
                CallFrame frame = callFrame;
                if (frame != null) {
                    if (linePCIndex == 0) {
                        Kit.codeBug();
                    }
                    linePCIndex--;
                    InterpreterData idata = frame.idata;
                    sb.append(lineSeparator);
                    sb.append("\tat script");
                    if (idata.itsName != null && idata.itsName.length() != 0) {
                        sb.append('.');
                        sb.append(idata.itsName);
                    }
                    sb.append('(');
                    sb.append(idata.itsSourceFile);
                    int pc = linePC[linePCIndex];
                    if (pc >= 0) {
                        sb.append(':');
                        sb.append(getIndex(idata.itsICode, pc));
                    }
                    sb.append(')');
                    callFrame = frame.parentFrame;
                }
            }
        }
        sb.append(nativeStackTrace.substring(offset));
        return sb.toString();
    }

    @Override // org.mozilla.javascript.Evaluator
    public List<String> getScriptStack(RhinoException ex) {
        ScriptStackElement[][] stack = getScriptStackElements(ex);
        List<String> list = new ArrayList<>(stack.length);
        String lineSeparator = SecurityUtilities.getSystemProperty("line.separator");
        for (ScriptStackElement[] group : stack) {
            StringBuilder sb = new StringBuilder();
            for (ScriptStackElement elem : group) {
                elem.renderJavaStyle(sb);
                sb.append(lineSeparator);
            }
            list.add(sb.toString());
        }
        return list;
    }

    public ScriptStackElement[][] getScriptStackElements(RhinoException ex) {
        if (ex.interpreterStackInfo == null) {
            return (ScriptStackElement[][]) null;
        }
        ArrayList arrayList = new ArrayList();
        CallFrame[] array = (CallFrame[]) ex.interpreterStackInfo;
        int[] linePC = ex.interpreterLineData;
        int arrayIndex = array.length;
        int linePCIndex = linePC.length;
        while (arrayIndex != 0) {
            arrayIndex--;
            CallFrame frame = array[arrayIndex];
            List<ScriptStackElement> group = new ArrayList<>();
            while (frame != null) {
                if (linePCIndex == 0) {
                    Kit.codeBug();
                }
                linePCIndex--;
                InterpreterData idata = frame.idata;
                String fileName = idata.itsSourceFile;
                String functionName = null;
                int lineNumber = -1;
                int pc = linePC[linePCIndex];
                if (pc >= 0) {
                    lineNumber = getIndex(idata.itsICode, pc);
                }
                if (idata.itsName != null && idata.itsName.length() != 0) {
                    functionName = idata.itsName;
                }
                frame = frame.parentFrame;
                group.add(new ScriptStackElement(fileName, functionName, lineNumber));
            }
            arrayList.add(group.toArray(new ScriptStackElement[group.size()]));
        }
        return (ScriptStackElement[][]) arrayList.toArray(new ScriptStackElement[arrayList.size()][]);
    }

    static String getEncodedSource(InterpreterData idata) {
        if (idata.encodedSource == null) {
            return null;
        }
        return idata.encodedSource.substring(idata.encodedSourceStart, idata.encodedSourceEnd);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void initFunction(Context cx, Scriptable scope, InterpretedFunction parent, int index) {
        InterpretedFunction fn = InterpretedFunction.createFunction(cx, scope, parent, index);
        ScriptRuntime.initFunction(cx, scope, fn, fn.idata.itsFunctionType, parent.idata.evalScriptFlag);
    }

    static Object interpret(InterpretedFunction ifun, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!ScriptRuntime.hasTopCall(cx)) {
            Kit.codeBug();
        }
        if (cx.interpreterSecurityDomain != ifun.securityDomain) {
            Object savedDomain = cx.interpreterSecurityDomain;
            cx.interpreterSecurityDomain = ifun.securityDomain;
            try {
                return ifun.securityController.callWithDomain(ifun.securityDomain, cx, ifun, scope, thisObj, args);
            } finally {
                cx.interpreterSecurityDomain = savedDomain;
            }
        }
        CallFrame frame = initFrame(cx, scope, thisObj, args, null, 0, args.length, ifun, null);
        frame.isContinuationsTopFrame = cx.isContinuationsTopCall;
        cx.isContinuationsTopCall = false;
        return interpretLoop(cx, frame, null);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/Interpreter$GeneratorState.class */
    static class GeneratorState {
        int operation;
        Object value;
        RuntimeException returnedException;

        GeneratorState(int operation, Object value) {
            this.operation = operation;
            this.value = value;
        }
    }

    public static Object resumeGenerator(Context cx, Scriptable scope, int operation, Object savedState, Object value) {
        CallFrame frame = (CallFrame) savedState;
        GeneratorState generatorState = new GeneratorState(operation, value);
        if (operation == 2) {
            try {
                return interpretLoop(cx, frame, generatorState);
            } catch (RuntimeException e) {
                if (e != value) {
                    throw e;
                }
                return Undefined.instance;
            }
        }
        Object result = interpretLoop(cx, frame, generatorState);
        if (generatorState.returnedException != null) {
            throw generatorState.returnedException;
        }
        return result;
    }

    public static Object restartContinuation(NativeContinuation c, Context cx, Scriptable scope, Object[] args) {
        Object arg;
        if (!ScriptRuntime.hasTopCall(cx)) {
            return ScriptRuntime.doTopCall(c, cx, scope, null, args, cx.isTopLevelStrict);
        }
        if (args.length == 0) {
            arg = Undefined.instance;
        } else {
            arg = args[0];
        }
        CallFrame capturedFrame = (CallFrame) c.getImplementation();
        if (capturedFrame == null) {
            return arg;
        }
        ContinuationJump cjump = new ContinuationJump(c, null);
        cjump.result = arg;
        return interpretLoop(cx, null, cjump);
    }

    /* JADX WARN: Code duplicated, block: B:392:0x1734 A[Catch: Throwable -> 0x17b7, TryCatch #2 {Throwable -> 0x17b7, blocks: (B:23:0x0080, B:29:0x00a8, B:30:0x00ec, B:31:0x00fe, B:32:0x048c, B:34:0x0493, B:36:0x04b3, B:400:0x177c, B:402:0x1795, B:404:0x17a1, B:405:0x17a6, B:37:0x04cc, B:38:0x04e5, B:44:0x04fe, B:46:0x0502, B:49:0x051c, B:50:0x054b, B:52:0x0573, B:54:0x0581, B:53:0x057d, B:55:0x05ab, B:57:0x05b8, B:58:0x05c2, B:59:0x05e6, B:60:0x05f8, B:61:0x0609, B:62:0x061a, B:66:0x0636, B:67:0x0646, B:71:0x0662, B:72:0x0672, B:74:0x067e, B:392:0x1734, B:393:0x173a, B:395:0x174a, B:399:0x1771, B:396:0x175a, B:75:0x068b, B:77:0x0697, B:78:0x06a4, B:80:0x06b0, B:81:0x06bd, B:83:0x06cc, B:84:0x06e4, B:86:0x06ef, B:87:0x0712, B:89:0x071b, B:92:0x0727, B:93:0x072d, B:96:0x0749, B:98:0x0758, B:99:0x0763, B:100:0x076f, B:101:0x078d, B:102:0x07ab, B:103:0x07e5, B:104:0x0820, B:106:0x083b, B:107:0x0844, B:108:0x085f, B:109:0x0870, B:110:0x089f, B:113:0x08b9, B:114:0x08c3, B:115:0x08d3, B:116:0x08e4, B:120:0x08f6, B:121:0x08fd, B:122:0x0912, B:124:0x091f, B:125:0x0929, B:127:0x0941, B:129:0x0960, B:128:0x0952, B:130:0x0964, B:132:0x0971, B:133:0x097b, B:134:0x099a, B:135:0x09ac, B:137:0x09b9, B:138:0x09c3, B:139:0x09d7, B:141:0x09e4, B:142:0x09ee, B:143:0x0a02, B:145:0x0a0f, B:146:0x0a19, B:148:0x0a29, B:149:0x0a33, B:150:0x0a49, B:152:0x0a56, B:153:0x0a60, B:154:0x0a85, B:155:0x0a95, B:156:0x0aa5, B:157:0x0ab7, B:158:0x0acf, B:160:0x0adc, B:161:0x0ae6, B:162:0x0b07, B:163:0x0b1f, B:164:0x0b4c, B:165:0x0b6f, B:166:0x0b81, B:167:0x0ba2, B:169:0x0baf, B:170:0x0bb9, B:171:0x0bd9, B:173:0x0be8, B:174:0x0bf4, B:176:0x0c01, B:177:0x0c0b, B:178:0x0c2a, B:180:0x0c37, B:181:0x0c41, B:184:0x0c60, B:185:0x0c6b, B:188:0x0c84, B:189:0x0c8f, B:191:0x0cb5, B:192:0x0cd6, B:194:0x0ce3, B:195:0x0cec, B:197:0x0cf4, B:201:0x0d14, B:202:0x0d20, B:204:0x0d3f, B:206:0x0d51, B:208:0x0d59, B:210:0x0d6d, B:211:0x0d77, B:213:0x0d95, B:215:0x0d9d, B:217:0x0dac, B:218:0x0dbf, B:220:0x0dc7, B:222:0x0dd6, B:224:0x0dec, B:225:0x0e05, B:227:0x0e0d, B:229:0x0e23, B:231:0x0e39, B:232:0x0e54, B:235:0x0e8b, B:236:0x0e96, B:238:0x0eac, B:240:0x0ec2, B:241:0x0f00, B:245:0x0f0e, B:246:0x0f18, B:247:0x0f1d, B:248:0x0f1e, B:250:0x0f2d, B:252:0x0f3c, B:253:0x0f4f, B:254:0x0f74, B:256:0x0f81, B:257:0x0f8b, B:258:0x0f98, B:259:0x0fac, B:260:0x0fb9, B:261:0x0fde, B:262:0x1003, B:263:0x101e, B:264:0x1033, B:265:0x1059, B:266:0x1069, B:267:0x1080, B:268:0x1090, B:269:0x10a7, B:270:0x10b7, B:271:0x10cc, B:272:0x10e4, B:273:0x10f6, B:274:0x1108, B:275:0x1114, B:276:0x1123, B:277:0x1132, B:278:0x1140, B:279:0x114e, B:280:0x115b, B:282:0x1168, B:283:0x1172, B:284:0x1186, B:285:0x1194, B:289:0x11b4, B:293:0x11d7, B:292:0x11cd, B:294:0x11f7, B:296:0x1204, B:297:0x120e, B:307:0x123d, B:308:0x1253, B:310:0x1271, B:312:0x127f, B:311:0x1279, B:313:0x1283, B:315:0x1290, B:316:0x129a, B:317:0x12ae, B:318:0x12bf, B:319:0x12d0, B:321:0x12dd, B:322:0x12e7, B:323:0x12fb, B:324:0x130d, B:325:0x1325, B:326:0x133a, B:328:0x1356, B:329:0x1373, B:330:0x1380, B:331:0x1391, B:332:0x13b2, B:333:0x13d4, B:335:0x13e1, B:336:0x13eb, B:337:0x1413, B:338:0x1453, B:339:0x1493, B:341:0x14b7, B:346:0x1506, B:344:0x14e6, B:345:0x14f8, B:347:0x1510, B:349:0x151d, B:350:0x1527, B:351:0x153a, B:353:0x1552, B:354:0x1571, B:355:0x1577, B:357:0x1584, B:358:0x158e, B:359:0x159c, B:361:0x15a9, B:362:0x15b7, B:364:0x15c4, B:365:0x15d2, B:367:0x15d9, B:368:0x15e6, B:370:0x15f5, B:371:0x160c, B:378:0x163d, B:379:0x1657, B:380:0x166f, B:381:0x1687, B:382:0x1690, B:383:0x1699, B:384:0x16a2, B:385:0x16ab, B:386:0x16c8, B:387:0x16e3, B:388:0x16fe, B:389:0x172e, B:26:0x009d, B:28:0x00a4), top: B:519:0x0080 }] */
    /* JADX WARN: Code duplicated, block: B:395:0x174a A[Catch: Throwable -> 0x17b7, TryCatch #2 {Throwable -> 0x17b7, blocks: (B:23:0x0080, B:29:0x00a8, B:30:0x00ec, B:31:0x00fe, B:32:0x048c, B:34:0x0493, B:36:0x04b3, B:400:0x177c, B:402:0x1795, B:404:0x17a1, B:405:0x17a6, B:37:0x04cc, B:38:0x04e5, B:44:0x04fe, B:46:0x0502, B:49:0x051c, B:50:0x054b, B:52:0x0573, B:54:0x0581, B:53:0x057d, B:55:0x05ab, B:57:0x05b8, B:58:0x05c2, B:59:0x05e6, B:60:0x05f8, B:61:0x0609, B:62:0x061a, B:66:0x0636, B:67:0x0646, B:71:0x0662, B:72:0x0672, B:74:0x067e, B:392:0x1734, B:393:0x173a, B:395:0x174a, B:399:0x1771, B:396:0x175a, B:75:0x068b, B:77:0x0697, B:78:0x06a4, B:80:0x06b0, B:81:0x06bd, B:83:0x06cc, B:84:0x06e4, B:86:0x06ef, B:87:0x0712, B:89:0x071b, B:92:0x0727, B:93:0x072d, B:96:0x0749, B:98:0x0758, B:99:0x0763, B:100:0x076f, B:101:0x078d, B:102:0x07ab, B:103:0x07e5, B:104:0x0820, B:106:0x083b, B:107:0x0844, B:108:0x085f, B:109:0x0870, B:110:0x089f, B:113:0x08b9, B:114:0x08c3, B:115:0x08d3, B:116:0x08e4, B:120:0x08f6, B:121:0x08fd, B:122:0x0912, B:124:0x091f, B:125:0x0929, B:127:0x0941, B:129:0x0960, B:128:0x0952, B:130:0x0964, B:132:0x0971, B:133:0x097b, B:134:0x099a, B:135:0x09ac, B:137:0x09b9, B:138:0x09c3, B:139:0x09d7, B:141:0x09e4, B:142:0x09ee, B:143:0x0a02, B:145:0x0a0f, B:146:0x0a19, B:148:0x0a29, B:149:0x0a33, B:150:0x0a49, B:152:0x0a56, B:153:0x0a60, B:154:0x0a85, B:155:0x0a95, B:156:0x0aa5, B:157:0x0ab7, B:158:0x0acf, B:160:0x0adc, B:161:0x0ae6, B:162:0x0b07, B:163:0x0b1f, B:164:0x0b4c, B:165:0x0b6f, B:166:0x0b81, B:167:0x0ba2, B:169:0x0baf, B:170:0x0bb9, B:171:0x0bd9, B:173:0x0be8, B:174:0x0bf4, B:176:0x0c01, B:177:0x0c0b, B:178:0x0c2a, B:180:0x0c37, B:181:0x0c41, B:184:0x0c60, B:185:0x0c6b, B:188:0x0c84, B:189:0x0c8f, B:191:0x0cb5, B:192:0x0cd6, B:194:0x0ce3, B:195:0x0cec, B:197:0x0cf4, B:201:0x0d14, B:202:0x0d20, B:204:0x0d3f, B:206:0x0d51, B:208:0x0d59, B:210:0x0d6d, B:211:0x0d77, B:213:0x0d95, B:215:0x0d9d, B:217:0x0dac, B:218:0x0dbf, B:220:0x0dc7, B:222:0x0dd6, B:224:0x0dec, B:225:0x0e05, B:227:0x0e0d, B:229:0x0e23, B:231:0x0e39, B:232:0x0e54, B:235:0x0e8b, B:236:0x0e96, B:238:0x0eac, B:240:0x0ec2, B:241:0x0f00, B:245:0x0f0e, B:246:0x0f18, B:247:0x0f1d, B:248:0x0f1e, B:250:0x0f2d, B:252:0x0f3c, B:253:0x0f4f, B:254:0x0f74, B:256:0x0f81, B:257:0x0f8b, B:258:0x0f98, B:259:0x0fac, B:260:0x0fb9, B:261:0x0fde, B:262:0x1003, B:263:0x101e, B:264:0x1033, B:265:0x1059, B:266:0x1069, B:267:0x1080, B:268:0x1090, B:269:0x10a7, B:270:0x10b7, B:271:0x10cc, B:272:0x10e4, B:273:0x10f6, B:274:0x1108, B:275:0x1114, B:276:0x1123, B:277:0x1132, B:278:0x1140, B:279:0x114e, B:280:0x115b, B:282:0x1168, B:283:0x1172, B:284:0x1186, B:285:0x1194, B:289:0x11b4, B:293:0x11d7, B:292:0x11cd, B:294:0x11f7, B:296:0x1204, B:297:0x120e, B:307:0x123d, B:308:0x1253, B:310:0x1271, B:312:0x127f, B:311:0x1279, B:313:0x1283, B:315:0x1290, B:316:0x129a, B:317:0x12ae, B:318:0x12bf, B:319:0x12d0, B:321:0x12dd, B:322:0x12e7, B:323:0x12fb, B:324:0x130d, B:325:0x1325, B:326:0x133a, B:328:0x1356, B:329:0x1373, B:330:0x1380, B:331:0x1391, B:332:0x13b2, B:333:0x13d4, B:335:0x13e1, B:336:0x13eb, B:337:0x1413, B:338:0x1453, B:339:0x1493, B:341:0x14b7, B:346:0x1506, B:344:0x14e6, B:345:0x14f8, B:347:0x1510, B:349:0x151d, B:350:0x1527, B:351:0x153a, B:353:0x1552, B:354:0x1571, B:355:0x1577, B:357:0x1584, B:358:0x158e, B:359:0x159c, B:361:0x15a9, B:362:0x15b7, B:364:0x15c4, B:365:0x15d2, B:367:0x15d9, B:368:0x15e6, B:370:0x15f5, B:371:0x160c, B:378:0x163d, B:379:0x1657, B:380:0x166f, B:381:0x1687, B:382:0x1690, B:383:0x1699, B:384:0x16a2, B:385:0x16ab, B:386:0x16c8, B:387:0x16e3, B:388:0x16fe, B:389:0x172e, B:26:0x009d, B:28:0x00a4), top: B:519:0x0080 }] */
    /* JADX WARN: Code duplicated, block: B:396:0x175a A[Catch: Throwable -> 0x17b7, TryCatch #2 {Throwable -> 0x17b7, blocks: (B:23:0x0080, B:29:0x00a8, B:30:0x00ec, B:31:0x00fe, B:32:0x048c, B:34:0x0493, B:36:0x04b3, B:400:0x177c, B:402:0x1795, B:404:0x17a1, B:405:0x17a6, B:37:0x04cc, B:38:0x04e5, B:44:0x04fe, B:46:0x0502, B:49:0x051c, B:50:0x054b, B:52:0x0573, B:54:0x0581, B:53:0x057d, B:55:0x05ab, B:57:0x05b8, B:58:0x05c2, B:59:0x05e6, B:60:0x05f8, B:61:0x0609, B:62:0x061a, B:66:0x0636, B:67:0x0646, B:71:0x0662, B:72:0x0672, B:74:0x067e, B:392:0x1734, B:393:0x173a, B:395:0x174a, B:399:0x1771, B:396:0x175a, B:75:0x068b, B:77:0x0697, B:78:0x06a4, B:80:0x06b0, B:81:0x06bd, B:83:0x06cc, B:84:0x06e4, B:86:0x06ef, B:87:0x0712, B:89:0x071b, B:92:0x0727, B:93:0x072d, B:96:0x0749, B:98:0x0758, B:99:0x0763, B:100:0x076f, B:101:0x078d, B:102:0x07ab, B:103:0x07e5, B:104:0x0820, B:106:0x083b, B:107:0x0844, B:108:0x085f, B:109:0x0870, B:110:0x089f, B:113:0x08b9, B:114:0x08c3, B:115:0x08d3, B:116:0x08e4, B:120:0x08f6, B:121:0x08fd, B:122:0x0912, B:124:0x091f, B:125:0x0929, B:127:0x0941, B:129:0x0960, B:128:0x0952, B:130:0x0964, B:132:0x0971, B:133:0x097b, B:134:0x099a, B:135:0x09ac, B:137:0x09b9, B:138:0x09c3, B:139:0x09d7, B:141:0x09e4, B:142:0x09ee, B:143:0x0a02, B:145:0x0a0f, B:146:0x0a19, B:148:0x0a29, B:149:0x0a33, B:150:0x0a49, B:152:0x0a56, B:153:0x0a60, B:154:0x0a85, B:155:0x0a95, B:156:0x0aa5, B:157:0x0ab7, B:158:0x0acf, B:160:0x0adc, B:161:0x0ae6, B:162:0x0b07, B:163:0x0b1f, B:164:0x0b4c, B:165:0x0b6f, B:166:0x0b81, B:167:0x0ba2, B:169:0x0baf, B:170:0x0bb9, B:171:0x0bd9, B:173:0x0be8, B:174:0x0bf4, B:176:0x0c01, B:177:0x0c0b, B:178:0x0c2a, B:180:0x0c37, B:181:0x0c41, B:184:0x0c60, B:185:0x0c6b, B:188:0x0c84, B:189:0x0c8f, B:191:0x0cb5, B:192:0x0cd6, B:194:0x0ce3, B:195:0x0cec, B:197:0x0cf4, B:201:0x0d14, B:202:0x0d20, B:204:0x0d3f, B:206:0x0d51, B:208:0x0d59, B:210:0x0d6d, B:211:0x0d77, B:213:0x0d95, B:215:0x0d9d, B:217:0x0dac, B:218:0x0dbf, B:220:0x0dc7, B:222:0x0dd6, B:224:0x0dec, B:225:0x0e05, B:227:0x0e0d, B:229:0x0e23, B:231:0x0e39, B:232:0x0e54, B:235:0x0e8b, B:236:0x0e96, B:238:0x0eac, B:240:0x0ec2, B:241:0x0f00, B:245:0x0f0e, B:246:0x0f18, B:247:0x0f1d, B:248:0x0f1e, B:250:0x0f2d, B:252:0x0f3c, B:253:0x0f4f, B:254:0x0f74, B:256:0x0f81, B:257:0x0f8b, B:258:0x0f98, B:259:0x0fac, B:260:0x0fb9, B:261:0x0fde, B:262:0x1003, B:263:0x101e, B:264:0x1033, B:265:0x1059, B:266:0x1069, B:267:0x1080, B:268:0x1090, B:269:0x10a7, B:270:0x10b7, B:271:0x10cc, B:272:0x10e4, B:273:0x10f6, B:274:0x1108, B:275:0x1114, B:276:0x1123, B:277:0x1132, B:278:0x1140, B:279:0x114e, B:280:0x115b, B:282:0x1168, B:283:0x1172, B:284:0x1186, B:285:0x1194, B:289:0x11b4, B:293:0x11d7, B:292:0x11cd, B:294:0x11f7, B:296:0x1204, B:297:0x120e, B:307:0x123d, B:308:0x1253, B:310:0x1271, B:312:0x127f, B:311:0x1279, B:313:0x1283, B:315:0x1290, B:316:0x129a, B:317:0x12ae, B:318:0x12bf, B:319:0x12d0, B:321:0x12dd, B:322:0x12e7, B:323:0x12fb, B:324:0x130d, B:325:0x1325, B:326:0x133a, B:328:0x1356, B:329:0x1373, B:330:0x1380, B:331:0x1391, B:332:0x13b2, B:333:0x13d4, B:335:0x13e1, B:336:0x13eb, B:337:0x1413, B:338:0x1453, B:339:0x1493, B:341:0x14b7, B:346:0x1506, B:344:0x14e6, B:345:0x14f8, B:347:0x1510, B:349:0x151d, B:350:0x1527, B:351:0x153a, B:353:0x1552, B:354:0x1571, B:355:0x1577, B:357:0x1584, B:358:0x158e, B:359:0x159c, B:361:0x15a9, B:362:0x15b7, B:364:0x15c4, B:365:0x15d2, B:367:0x15d9, B:368:0x15e6, B:370:0x15f5, B:371:0x160c, B:378:0x163d, B:379:0x1657, B:380:0x166f, B:381:0x1687, B:382:0x1690, B:383:0x1699, B:384:0x16a2, B:385:0x16ab, B:386:0x16c8, B:387:0x16e3, B:388:0x16fe, B:389:0x172e, B:26:0x009d, B:28:0x00a4), top: B:519:0x0080 }] */
    /* JADX WARN: Code duplicated, block: B:402:0x1795 A[Catch: Throwable -> 0x17b7, TryCatch #2 {Throwable -> 0x17b7, blocks: (B:23:0x0080, B:29:0x00a8, B:30:0x00ec, B:31:0x00fe, B:32:0x048c, B:34:0x0493, B:36:0x04b3, B:400:0x177c, B:402:0x1795, B:404:0x17a1, B:405:0x17a6, B:37:0x04cc, B:38:0x04e5, B:44:0x04fe, B:46:0x0502, B:49:0x051c, B:50:0x054b, B:52:0x0573, B:54:0x0581, B:53:0x057d, B:55:0x05ab, B:57:0x05b8, B:58:0x05c2, B:59:0x05e6, B:60:0x05f8, B:61:0x0609, B:62:0x061a, B:66:0x0636, B:67:0x0646, B:71:0x0662, B:72:0x0672, B:74:0x067e, B:392:0x1734, B:393:0x173a, B:395:0x174a, B:399:0x1771, B:396:0x175a, B:75:0x068b, B:77:0x0697, B:78:0x06a4, B:80:0x06b0, B:81:0x06bd, B:83:0x06cc, B:84:0x06e4, B:86:0x06ef, B:87:0x0712, B:89:0x071b, B:92:0x0727, B:93:0x072d, B:96:0x0749, B:98:0x0758, B:99:0x0763, B:100:0x076f, B:101:0x078d, B:102:0x07ab, B:103:0x07e5, B:104:0x0820, B:106:0x083b, B:107:0x0844, B:108:0x085f, B:109:0x0870, B:110:0x089f, B:113:0x08b9, B:114:0x08c3, B:115:0x08d3, B:116:0x08e4, B:120:0x08f6, B:121:0x08fd, B:122:0x0912, B:124:0x091f, B:125:0x0929, B:127:0x0941, B:129:0x0960, B:128:0x0952, B:130:0x0964, B:132:0x0971, B:133:0x097b, B:134:0x099a, B:135:0x09ac, B:137:0x09b9, B:138:0x09c3, B:139:0x09d7, B:141:0x09e4, B:142:0x09ee, B:143:0x0a02, B:145:0x0a0f, B:146:0x0a19, B:148:0x0a29, B:149:0x0a33, B:150:0x0a49, B:152:0x0a56, B:153:0x0a60, B:154:0x0a85, B:155:0x0a95, B:156:0x0aa5, B:157:0x0ab7, B:158:0x0acf, B:160:0x0adc, B:161:0x0ae6, B:162:0x0b07, B:163:0x0b1f, B:164:0x0b4c, B:165:0x0b6f, B:166:0x0b81, B:167:0x0ba2, B:169:0x0baf, B:170:0x0bb9, B:171:0x0bd9, B:173:0x0be8, B:174:0x0bf4, B:176:0x0c01, B:177:0x0c0b, B:178:0x0c2a, B:180:0x0c37, B:181:0x0c41, B:184:0x0c60, B:185:0x0c6b, B:188:0x0c84, B:189:0x0c8f, B:191:0x0cb5, B:192:0x0cd6, B:194:0x0ce3, B:195:0x0cec, B:197:0x0cf4, B:201:0x0d14, B:202:0x0d20, B:204:0x0d3f, B:206:0x0d51, B:208:0x0d59, B:210:0x0d6d, B:211:0x0d77, B:213:0x0d95, B:215:0x0d9d, B:217:0x0dac, B:218:0x0dbf, B:220:0x0dc7, B:222:0x0dd6, B:224:0x0dec, B:225:0x0e05, B:227:0x0e0d, B:229:0x0e23, B:231:0x0e39, B:232:0x0e54, B:235:0x0e8b, B:236:0x0e96, B:238:0x0eac, B:240:0x0ec2, B:241:0x0f00, B:245:0x0f0e, B:246:0x0f18, B:247:0x0f1d, B:248:0x0f1e, B:250:0x0f2d, B:252:0x0f3c, B:253:0x0f4f, B:254:0x0f74, B:256:0x0f81, B:257:0x0f8b, B:258:0x0f98, B:259:0x0fac, B:260:0x0fb9, B:261:0x0fde, B:262:0x1003, B:263:0x101e, B:264:0x1033, B:265:0x1059, B:266:0x1069, B:267:0x1080, B:268:0x1090, B:269:0x10a7, B:270:0x10b7, B:271:0x10cc, B:272:0x10e4, B:273:0x10f6, B:274:0x1108, B:275:0x1114, B:276:0x1123, B:277:0x1132, B:278:0x1140, B:279:0x114e, B:280:0x115b, B:282:0x1168, B:283:0x1172, B:284:0x1186, B:285:0x1194, B:289:0x11b4, B:293:0x11d7, B:292:0x11cd, B:294:0x11f7, B:296:0x1204, B:297:0x120e, B:307:0x123d, B:308:0x1253, B:310:0x1271, B:312:0x127f, B:311:0x1279, B:313:0x1283, B:315:0x1290, B:316:0x129a, B:317:0x12ae, B:318:0x12bf, B:319:0x12d0, B:321:0x12dd, B:322:0x12e7, B:323:0x12fb, B:324:0x130d, B:325:0x1325, B:326:0x133a, B:328:0x1356, B:329:0x1373, B:330:0x1380, B:331:0x1391, B:332:0x13b2, B:333:0x13d4, B:335:0x13e1, B:336:0x13eb, B:337:0x1413, B:338:0x1453, B:339:0x1493, B:341:0x14b7, B:346:0x1506, B:344:0x14e6, B:345:0x14f8, B:347:0x1510, B:349:0x151d, B:350:0x1527, B:351:0x153a, B:353:0x1552, B:354:0x1571, B:355:0x1577, B:357:0x1584, B:358:0x158e, B:359:0x159c, B:361:0x15a9, B:362:0x15b7, B:364:0x15c4, B:365:0x15d2, B:367:0x15d9, B:368:0x15e6, B:370:0x15f5, B:371:0x160c, B:378:0x163d, B:379:0x1657, B:380:0x166f, B:381:0x1687, B:382:0x1690, B:383:0x1699, B:384:0x16a2, B:385:0x16ab, B:386:0x16c8, B:387:0x16e3, B:388:0x16fe, B:389:0x172e, B:26:0x009d, B:28:0x00a4), top: B:519:0x0080 }] */
    /* JADX WARN: Code duplicated, block: B:404:0x17a1 A[Catch: Throwable -> 0x17b7, TryCatch #2 {Throwable -> 0x17b7, blocks: (B:23:0x0080, B:29:0x00a8, B:30:0x00ec, B:31:0x00fe, B:32:0x048c, B:34:0x0493, B:36:0x04b3, B:400:0x177c, B:402:0x1795, B:404:0x17a1, B:405:0x17a6, B:37:0x04cc, B:38:0x04e5, B:44:0x04fe, B:46:0x0502, B:49:0x051c, B:50:0x054b, B:52:0x0573, B:54:0x0581, B:53:0x057d, B:55:0x05ab, B:57:0x05b8, B:58:0x05c2, B:59:0x05e6, B:60:0x05f8, B:61:0x0609, B:62:0x061a, B:66:0x0636, B:67:0x0646, B:71:0x0662, B:72:0x0672, B:74:0x067e, B:392:0x1734, B:393:0x173a, B:395:0x174a, B:399:0x1771, B:396:0x175a, B:75:0x068b, B:77:0x0697, B:78:0x06a4, B:80:0x06b0, B:81:0x06bd, B:83:0x06cc, B:84:0x06e4, B:86:0x06ef, B:87:0x0712, B:89:0x071b, B:92:0x0727, B:93:0x072d, B:96:0x0749, B:98:0x0758, B:99:0x0763, B:100:0x076f, B:101:0x078d, B:102:0x07ab, B:103:0x07e5, B:104:0x0820, B:106:0x083b, B:107:0x0844, B:108:0x085f, B:109:0x0870, B:110:0x089f, B:113:0x08b9, B:114:0x08c3, B:115:0x08d3, B:116:0x08e4, B:120:0x08f6, B:121:0x08fd, B:122:0x0912, B:124:0x091f, B:125:0x0929, B:127:0x0941, B:129:0x0960, B:128:0x0952, B:130:0x0964, B:132:0x0971, B:133:0x097b, B:134:0x099a, B:135:0x09ac, B:137:0x09b9, B:138:0x09c3, B:139:0x09d7, B:141:0x09e4, B:142:0x09ee, B:143:0x0a02, B:145:0x0a0f, B:146:0x0a19, B:148:0x0a29, B:149:0x0a33, B:150:0x0a49, B:152:0x0a56, B:153:0x0a60, B:154:0x0a85, B:155:0x0a95, B:156:0x0aa5, B:157:0x0ab7, B:158:0x0acf, B:160:0x0adc, B:161:0x0ae6, B:162:0x0b07, B:163:0x0b1f, B:164:0x0b4c, B:165:0x0b6f, B:166:0x0b81, B:167:0x0ba2, B:169:0x0baf, B:170:0x0bb9, B:171:0x0bd9, B:173:0x0be8, B:174:0x0bf4, B:176:0x0c01, B:177:0x0c0b, B:178:0x0c2a, B:180:0x0c37, B:181:0x0c41, B:184:0x0c60, B:185:0x0c6b, B:188:0x0c84, B:189:0x0c8f, B:191:0x0cb5, B:192:0x0cd6, B:194:0x0ce3, B:195:0x0cec, B:197:0x0cf4, B:201:0x0d14, B:202:0x0d20, B:204:0x0d3f, B:206:0x0d51, B:208:0x0d59, B:210:0x0d6d, B:211:0x0d77, B:213:0x0d95, B:215:0x0d9d, B:217:0x0dac, B:218:0x0dbf, B:220:0x0dc7, B:222:0x0dd6, B:224:0x0dec, B:225:0x0e05, B:227:0x0e0d, B:229:0x0e23, B:231:0x0e39, B:232:0x0e54, B:235:0x0e8b, B:236:0x0e96, B:238:0x0eac, B:240:0x0ec2, B:241:0x0f00, B:245:0x0f0e, B:246:0x0f18, B:247:0x0f1d, B:248:0x0f1e, B:250:0x0f2d, B:252:0x0f3c, B:253:0x0f4f, B:254:0x0f74, B:256:0x0f81, B:257:0x0f8b, B:258:0x0f98, B:259:0x0fac, B:260:0x0fb9, B:261:0x0fde, B:262:0x1003, B:263:0x101e, B:264:0x1033, B:265:0x1059, B:266:0x1069, B:267:0x1080, B:268:0x1090, B:269:0x10a7, B:270:0x10b7, B:271:0x10cc, B:272:0x10e4, B:273:0x10f6, B:274:0x1108, B:275:0x1114, B:276:0x1123, B:277:0x1132, B:278:0x1140, B:279:0x114e, B:280:0x115b, B:282:0x1168, B:283:0x1172, B:284:0x1186, B:285:0x1194, B:289:0x11b4, B:293:0x11d7, B:292:0x11cd, B:294:0x11f7, B:296:0x1204, B:297:0x120e, B:307:0x123d, B:308:0x1253, B:310:0x1271, B:312:0x127f, B:311:0x1279, B:313:0x1283, B:315:0x1290, B:316:0x129a, B:317:0x12ae, B:318:0x12bf, B:319:0x12d0, B:321:0x12dd, B:322:0x12e7, B:323:0x12fb, B:324:0x130d, B:325:0x1325, B:326:0x133a, B:328:0x1356, B:329:0x1373, B:330:0x1380, B:331:0x1391, B:332:0x13b2, B:333:0x13d4, B:335:0x13e1, B:336:0x13eb, B:337:0x1413, B:338:0x1453, B:339:0x1493, B:341:0x14b7, B:346:0x1506, B:344:0x14e6, B:345:0x14f8, B:347:0x1510, B:349:0x151d, B:350:0x1527, B:351:0x153a, B:353:0x1552, B:354:0x1571, B:355:0x1577, B:357:0x1584, B:358:0x158e, B:359:0x159c, B:361:0x15a9, B:362:0x15b7, B:364:0x15c4, B:365:0x15d2, B:367:0x15d9, B:368:0x15e6, B:370:0x15f5, B:371:0x160c, B:378:0x163d, B:379:0x1657, B:380:0x166f, B:381:0x1687, B:382:0x1690, B:383:0x1699, B:384:0x16a2, B:385:0x16ab, B:386:0x16c8, B:387:0x16e3, B:388:0x16fe, B:389:0x172e, B:26:0x009d, B:28:0x00a4), top: B:519:0x0080 }] */
    /* JADX WARN: Code duplicated, block: B:415:0x17d4  */
    /* JADX WARN: Code duplicated, block: B:418:0x17e9  */
    /* JADX WARN: Code duplicated, block: B:423:0x1801  */
    /* JADX WARN: Code duplicated, block: B:425:0x1808 A[ADDED_TO_REGION, REMOVE] */
    /* JADX WARN: Code duplicated, block: B:426:0x180e  */
    /* JADX WARN: Code duplicated, block: B:42:0x04f9  */
    /* JADX WARN: Code duplicated, block: B:434:0x182f  */
    /* JADX WARN: Code duplicated, block: B:435:0x1835  */
    /* JADX WARN: Code duplicated, block: B:437:0x183c  */
    /* JADX WARN: Code duplicated, block: B:439:0x1845  */
    /* JADX WARN: Code duplicated, block: B:43:0x04fd  */
    /* JADX WARN: Code duplicated, block: B:440:0x1849  */
    /* JADX WARN: Code duplicated, block: B:442:0x184f  */
    /* JADX WARN: Code duplicated, block: B:444:0x1856  */
    /* JADX WARN: Code duplicated, block: B:446:0x185f  */
    /* JADX WARN: Code duplicated, block: B:447:0x1863  */
    /* JADX WARN: Code duplicated, block: B:449:0x1869  */
    /* JADX WARN: Code duplicated, block: B:451:0x1870  */
    /* JADX WARN: Code duplicated, block: B:452:0x187c  */
    /* JADX WARN: Code duplicated, block: B:454:0x1885  */
    /* JADX WARN: Code duplicated, block: B:455:0x1889  */
    /* JADX WARN: Code duplicated, block: B:466:0x18b8  */
    /* JADX WARN: Code duplicated, block: B:46:0x0502 A[Catch: Throwable -> 0x17b7, TRY_ENTER, TryCatch #2 {Throwable -> 0x17b7, blocks: (B:23:0x0080, B:29:0x00a8, B:30:0x00ec, B:31:0x00fe, B:32:0x048c, B:34:0x0493, B:36:0x04b3, B:400:0x177c, B:402:0x1795, B:404:0x17a1, B:405:0x17a6, B:37:0x04cc, B:38:0x04e5, B:44:0x04fe, B:46:0x0502, B:49:0x051c, B:50:0x054b, B:52:0x0573, B:54:0x0581, B:53:0x057d, B:55:0x05ab, B:57:0x05b8, B:58:0x05c2, B:59:0x05e6, B:60:0x05f8, B:61:0x0609, B:62:0x061a, B:66:0x0636, B:67:0x0646, B:71:0x0662, B:72:0x0672, B:74:0x067e, B:392:0x1734, B:393:0x173a, B:395:0x174a, B:399:0x1771, B:396:0x175a, B:75:0x068b, B:77:0x0697, B:78:0x06a4, B:80:0x06b0, B:81:0x06bd, B:83:0x06cc, B:84:0x06e4, B:86:0x06ef, B:87:0x0712, B:89:0x071b, B:92:0x0727, B:93:0x072d, B:96:0x0749, B:98:0x0758, B:99:0x0763, B:100:0x076f, B:101:0x078d, B:102:0x07ab, B:103:0x07e5, B:104:0x0820, B:106:0x083b, B:107:0x0844, B:108:0x085f, B:109:0x0870, B:110:0x089f, B:113:0x08b9, B:114:0x08c3, B:115:0x08d3, B:116:0x08e4, B:120:0x08f6, B:121:0x08fd, B:122:0x0912, B:124:0x091f, B:125:0x0929, B:127:0x0941, B:129:0x0960, B:128:0x0952, B:130:0x0964, B:132:0x0971, B:133:0x097b, B:134:0x099a, B:135:0x09ac, B:137:0x09b9, B:138:0x09c3, B:139:0x09d7, B:141:0x09e4, B:142:0x09ee, B:143:0x0a02, B:145:0x0a0f, B:146:0x0a19, B:148:0x0a29, B:149:0x0a33, B:150:0x0a49, B:152:0x0a56, B:153:0x0a60, B:154:0x0a85, B:155:0x0a95, B:156:0x0aa5, B:157:0x0ab7, B:158:0x0acf, B:160:0x0adc, B:161:0x0ae6, B:162:0x0b07, B:163:0x0b1f, B:164:0x0b4c, B:165:0x0b6f, B:166:0x0b81, B:167:0x0ba2, B:169:0x0baf, B:170:0x0bb9, B:171:0x0bd9, B:173:0x0be8, B:174:0x0bf4, B:176:0x0c01, B:177:0x0c0b, B:178:0x0c2a, B:180:0x0c37, B:181:0x0c41, B:184:0x0c60, B:185:0x0c6b, B:188:0x0c84, B:189:0x0c8f, B:191:0x0cb5, B:192:0x0cd6, B:194:0x0ce3, B:195:0x0cec, B:197:0x0cf4, B:201:0x0d14, B:202:0x0d20, B:204:0x0d3f, B:206:0x0d51, B:208:0x0d59, B:210:0x0d6d, B:211:0x0d77, B:213:0x0d95, B:215:0x0d9d, B:217:0x0dac, B:218:0x0dbf, B:220:0x0dc7, B:222:0x0dd6, B:224:0x0dec, B:225:0x0e05, B:227:0x0e0d, B:229:0x0e23, B:231:0x0e39, B:232:0x0e54, B:235:0x0e8b, B:236:0x0e96, B:238:0x0eac, B:240:0x0ec2, B:241:0x0f00, B:245:0x0f0e, B:246:0x0f18, B:247:0x0f1d, B:248:0x0f1e, B:250:0x0f2d, B:252:0x0f3c, B:253:0x0f4f, B:254:0x0f74, B:256:0x0f81, B:257:0x0f8b, B:258:0x0f98, B:259:0x0fac, B:260:0x0fb9, B:261:0x0fde, B:262:0x1003, B:263:0x101e, B:264:0x1033, B:265:0x1059, B:266:0x1069, B:267:0x1080, B:268:0x1090, B:269:0x10a7, B:270:0x10b7, B:271:0x10cc, B:272:0x10e4, B:273:0x10f6, B:274:0x1108, B:275:0x1114, B:276:0x1123, B:277:0x1132, B:278:0x1140, B:279:0x114e, B:280:0x115b, B:282:0x1168, B:283:0x1172, B:284:0x1186, B:285:0x1194, B:289:0x11b4, B:293:0x11d7, B:292:0x11cd, B:294:0x11f7, B:296:0x1204, B:297:0x120e, B:307:0x123d, B:308:0x1253, B:310:0x1271, B:312:0x127f, B:311:0x1279, B:313:0x1283, B:315:0x1290, B:316:0x129a, B:317:0x12ae, B:318:0x12bf, B:319:0x12d0, B:321:0x12dd, B:322:0x12e7, B:323:0x12fb, B:324:0x130d, B:325:0x1325, B:326:0x133a, B:328:0x1356, B:329:0x1373, B:330:0x1380, B:331:0x1391, B:332:0x13b2, B:333:0x13d4, B:335:0x13e1, B:336:0x13eb, B:337:0x1413, B:338:0x1453, B:339:0x1493, B:341:0x14b7, B:346:0x1506, B:344:0x14e6, B:345:0x14f8, B:347:0x1510, B:349:0x151d, B:350:0x1527, B:351:0x153a, B:353:0x1552, B:354:0x1571, B:355:0x1577, B:357:0x1584, B:358:0x158e, B:359:0x159c, B:361:0x15a9, B:362:0x15b7, B:364:0x15c4, B:365:0x15d2, B:367:0x15d9, B:368:0x15e6, B:370:0x15f5, B:371:0x160c, B:378:0x163d, B:379:0x1657, B:380:0x166f, B:381:0x1687, B:382:0x1690, B:383:0x1699, B:384:0x16a2, B:385:0x16ab, B:386:0x16c8, B:387:0x16e3, B:388:0x16fe, B:389:0x172e, B:26:0x009d, B:28:0x00a4), top: B:519:0x0080 }] */
    /* JADX WARN: Code duplicated, block: B:474:0x18e4  */
    /* JADX WARN: Code duplicated, block: B:476:0x18ea  */
    /* JADX WARN: Code duplicated, block: B:477:0x18ee  */
    /* JADX WARN: Code duplicated, block: B:484:0x1913  */
    /* JADX WARN: Code duplicated, block: B:491:0x192c  */
    /* JADX WARN: Code duplicated, block: B:493:0x1934  */
    /* JADX WARN: Code duplicated, block: B:496:0x1940  */
    /* JADX WARN: Code duplicated, block: B:520:0x1891 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:525:0x04ec A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:526:0x0516 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:534:0x1959 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:542:0x1927 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:544:0x1959 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:545:0x1946 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:548:0x007c A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:673:0x1771 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:701:0x00ec A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:817:0x00ec A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:818:0x1918 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:820:0x18df A[ADDED_TO_REGION, REMOVE, SYNTHETIC] */
    private static Object interpretLoop(Context cx, CallFrame frame, Object throwable) {
        ContinuationJump cjump;
        int i;
        int exState;
        int i2;
        int i3;
        boolean z;
        boolean z2;
        Object obj;
        Object val;
        Object objEnumId;
        int i4;
        Scriptable lastCatchScope;
        Object objStrictSetName;
        int offset;
        Object DBL_MRK = UniqueTag.DOUBLE_MARK;
        Object undefined = Undefined.instance;
        boolean instructionCounting = cx.instructionThreshold != 0;
        String stringReg = null;
        int indexReg = -1;
        if (cx.lastInterpreterFrame != null) {
            if (cx.previousInterpreterInvocations == null) {
                cx.previousInterpreterInvocations = new ObjArray();
            }
            cx.previousInterpreterInvocations.push(cx.lastInterpreterFrame);
        }
        GeneratorState generatorState = null;
        if (throwable != null) {
            if (throwable instanceof GeneratorState) {
                generatorState = (GeneratorState) throwable;
                enterFrame(cx, frame, ScriptRuntime.emptyArgs, true);
                throwable = null;
            } else if (!(throwable instanceof ContinuationJump)) {
                Kit.codeBug();
            }
        }
        Object interpreterResult = null;
        double interpreterResultDbl = 0.0d;
        while (true) {
            if (throwable != null) {
                try {
                    frame = processThrowable(cx, throwable, frame, indexReg, instructionCounting);
                    throwable = frame.throwable;
                    frame.throwable = null;
                } catch (Throwable ex) {
                    if (throwable != null) {
                        ex.printStackTrace(System.err);
                        throw new IllegalStateException();
                    }
                    throwable = ex;
                }
            } else if (generatorState == null && frame.frozen) {
                Kit.codeBug();
            }
            Object[] stack = frame.stack;
            double[] sDbl = frame.sDbl;
            Object[] vars = frame.varSource.stack;
            double[] varDbls = frame.varSource.sDbl;
            int[] varAttributes = frame.varSource.stackAttributes;
            byte[] iCode = frame.idata.itsICode;
            String[] strings = frame.idata.itsStringTable;
            int stackTop = frame.savedStackTop;
            cx.lastInterpreterFrame = frame;
            while (true) {
                CallFrame callFrame = frame;
                int i5 = callFrame.pc;
                callFrame.pc = i5 + 1;
                byte b = iCode[i5];
                switch (b) {
                    case -66:
                    case 73:
                        if (!frame.frozen) {
                            CallFrame callFrame2 = frame;
                            int i6 = stackTop;
                            GeneratorState generatorState2 = generatorState;
                            if (b == -66) {
                                z2 = true;
                            } else {
                                z2 = false;
                            }
                            return freezeGenerator(cx, callFrame2, i6, generatorState2, z2);
                        }
                        obj = thawGenerator(frame, stackTop, generatorState, b);
                        if (obj != Scriptable.NOT_FOUND) {
                            throwable = obj;
                            if (throwable == null) {
                                Kit.codeBug();
                            }
                            cjump = null;
                            if (generatorState == null && generatorState.operation == 2 && throwable == generatorState.value) {
                                exState = 1;
                            } else if (!(throwable instanceof JavaScriptException) || (throwable instanceof EcmaError) || (throwable instanceof EvaluatorException)) {
                                exState = 2;
                            } else if (throwable instanceof ContinuationPending) {
                                exState = 0;
                            } else if (throwable instanceof RuntimeException) {
                                if (cx.hasFeature(13)) {
                                    i3 = 2;
                                } else {
                                    i3 = 1;
                                }
                                exState = i3;
                            } else if (throwable instanceof Error) {
                                if (cx.hasFeature(13)) {
                                    i2 = 2;
                                } else {
                                    i2 = 0;
                                }
                                exState = i2;
                            } else if (throwable instanceof ContinuationJump) {
                                exState = 1;
                                cjump = (ContinuationJump) throwable;
                            } else {
                                if (cx.hasFeature(13)) {
                                    i = 2;
                                } else {
                                    i = 1;
                                }
                                exState = i;
                            }
                            if (instructionCounting) {
                                try {
                                    addInstructionCount(cx, frame, 100);
                                } catch (Error ex2) {
                                    throwable = ex2;
                                    cjump = null;
                                    exState = 0;
                                } catch (RuntimeException ex3) {
                                    throwable = ex3;
                                    exState = 1;
                                }
                            }
                            if (frame.debuggerFrame != null && (throwable instanceof RuntimeException)) {
                                RuntimeException rex = (RuntimeException) throwable;
                                try {
                                    frame.debuggerFrame.onExceptionThrown(cx, rex);
                                } catch (Throwable ex4) {
                                    throwable = ex4;
                                    cjump = null;
                                    exState = 0;
                                }
                            }
                            while (true) {
                                if (exState != 0) {
                                    if (exState != 2) {
                                        z = true;
                                    } else {
                                        z = false;
                                    }
                                    boolean onlyFinally = z;
                                    indexReg = getExceptionHandler(frame, onlyFinally);
                                    if (indexReg >= 0) {
                                        continue;
                                    }
                                }
                                exitFrame(cx, frame, throwable);
                                frame = frame.parentFrame;
                                if (frame == null) {
                                    if (cjump == null && cjump.branchFrame == frame) {
                                        indexReg = -1;
                                    }
                                } else {
                                    if (cjump == null) {
                                        if (cjump.branchFrame != null) {
                                            Kit.codeBug();
                                        }
                                        if (cjump.capturedFrame != null) {
                                            indexReg = -1;
                                        } else {
                                            interpreterResult = cjump.result;
                                            interpreterResultDbl = cjump.resultDbl;
                                            throwable = null;
                                        }
                                    }
                                    break;
                                }
                                break;
                            }
                        }
                        break;
                        break;
                    case -65:
                        frame.frozen = true;
                        frame.result = stack[stackTop];
                        frame.resultDbl = sDbl[stackTop];
                        int i7 = stackTop - 1;
                        NativeIterator.StopIteration si = new NativeIterator.StopIteration(frame.result == UniqueTag.DOUBLE_MARK ? Double.valueOf(frame.resultDbl) : frame.result);
                        int sourceLine = getIndex(iCode, frame.pc);
                        generatorState.returnedException = new JavaScriptException(si, frame.idata.itsSourceFile, sourceLine);
                        exitFrame(cx, frame, null);
                        interpreterResult = frame.result;
                        interpreterResultDbl = frame.resultDbl;
                        if (frame.parentFrame != null) {
                            frame = frame.parentFrame;
                            if (frame.frozen) {
                                frame = frame.cloneFrozen();
                            }
                            setCallResult(frame, interpreterResult, interpreterResultDbl);
                            interpreterResult = null;
                        }
                        break;
                    case DerValue.TAG_PRIVATE /* -64 */:
                        if (frame.debuggerFrame != null) {
                            frame.debuggerFrame.onDebuggerStatement(cx);
                        }
                        break;
                    case -63:
                        frame.frozen = true;
                        int sourceLine2 = getIndex(iCode, frame.pc);
                        generatorState.returnedException = new JavaScriptException(NativeIterator.getStopIterationObject(frame.scope), frame.idata.itsSourceFile, sourceLine2);
                        exitFrame(cx, frame, null);
                        interpreterResult = frame.result;
                        interpreterResultDbl = frame.resultDbl;
                        if (frame.parentFrame != null) {
                            frame = frame.parentFrame;
                            if (frame.frozen) {
                                frame = frame.cloneFrozen();
                            }
                            setCallResult(frame, interpreterResult, interpreterResultDbl);
                            interpreterResult = null;
                        }
                        break;
                    case -62:
                        if (!frame.frozen) {
                            frame.pc--;
                            CallFrame generatorFrame = captureFrameForGenerator(frame);
                            generatorFrame.frozen = true;
                            if (cx.getLanguageVersion() >= 200) {
                                frame.result = new ES6Generator(frame.scope, generatorFrame.fnOrScript, generatorFrame);
                            } else {
                                frame.result = new NativeGenerator(frame.scope, generatorFrame.fnOrScript, generatorFrame);
                            }
                            exitFrame(cx, frame, null);
                            interpreterResult = frame.result;
                            interpreterResultDbl = frame.resultDbl;
                            if (frame.parentFrame != null) {
                                frame = frame.parentFrame;
                                if (frame.frozen) {
                                    frame = frame.cloneFrozen();
                                }
                                setCallResult(frame, interpreterResult, interpreterResultDbl);
                                interpreterResult = null;
                            }
                        }
                        if (!frame.frozen) {
                            CallFrame callFrame3 = frame;
                            int i8 = stackTop;
                            GeneratorState generatorState3 = generatorState;
                            if (b == -66) {
                                z2 = true;
                            } else {
                                z2 = false;
                            }
                            return freezeGenerator(cx, callFrame3, i8, generatorState3, z2);
                        }
                        obj = thawGenerator(frame, stackTop, generatorState, b);
                        if (obj != Scriptable.NOT_FOUND) {
                            throwable = obj;
                            if (throwable == null) {
                                Kit.codeBug();
                            }
                            cjump = null;
                            if (generatorState == null) {
                                if (!(throwable instanceof JavaScriptException)) {
                                    exState = 2;
                                } else if (throwable instanceof ContinuationPending) {
                                    exState = 0;
                                } else if (throwable instanceof RuntimeException) {
                                    if (cx.hasFeature(13)) {
                                        i3 = 2;
                                    } else {
                                        i3 = 1;
                                    }
                                    exState = i3;
                                } else if (throwable instanceof Error) {
                                    if (cx.hasFeature(13)) {
                                        i2 = 2;
                                    } else {
                                        i2 = 0;
                                    }
                                    exState = i2;
                                } else if (throwable instanceof ContinuationJump) {
                                    exState = 1;
                                    cjump = (ContinuationJump) throwable;
                                } else {
                                    if (cx.hasFeature(13)) {
                                        i = 2;
                                    } else {
                                        i = 1;
                                    }
                                    exState = i;
                                }
                            } else if (!(throwable instanceof JavaScriptException)) {
                                exState = 2;
                            } else if (throwable instanceof ContinuationPending) {
                                exState = 0;
                            } else if (throwable instanceof RuntimeException) {
                                if (cx.hasFeature(13)) {
                                    i3 = 2;
                                } else {
                                    i3 = 1;
                                }
                                exState = i3;
                            } else if (throwable instanceof Error) {
                                if (cx.hasFeature(13)) {
                                    i2 = 2;
                                } else {
                                    i2 = 0;
                                }
                                exState = i2;
                            } else if (throwable instanceof ContinuationJump) {
                                exState = 1;
                                cjump = (ContinuationJump) throwable;
                            } else {
                                if (cx.hasFeature(13)) {
                                    i = 2;
                                } else {
                                    i = 1;
                                }
                                exState = i;
                            }
                            if (instructionCounting) {
                                addInstructionCount(cx, frame, 100);
                            }
                            if (frame.debuggerFrame != null) {
                                RuntimeException rex2 = (RuntimeException) throwable;
                                frame.debuggerFrame.onExceptionThrown(cx, rex2);
                            }
                            while (true) {
                                if (exState != 0) {
                                    if (exState != 2) {
                                        z = true;
                                    } else {
                                        z = false;
                                    }
                                    boolean onlyFinally2 = z;
                                    indexReg = getExceptionHandler(frame, onlyFinally2);
                                    if (indexReg >= 0) {
                                        continue;
                                    }
                                }
                                exitFrame(cx, frame, throwable);
                                frame = frame.parentFrame;
                                if (frame == null) {
                                    if (cjump == null) {
                                    }
                                } else {
                                    if (cjump == null) {
                                        if (cjump.branchFrame != null) {
                                            Kit.codeBug();
                                        }
                                        if (cjump.capturedFrame != null) {
                                            indexReg = -1;
                                        } else {
                                            interpreterResult = cjump.result;
                                            interpreterResultDbl = cjump.resultDbl;
                                            throwable = null;
                                        }
                                    }
                                    break;
                                }
                                break;
                            }
                            break;
                        }
                        break;
                        break;
                    case -61:
                        CallFrame callFrame4 = frame;
                        int i9 = callFrame4.pc;
                        callFrame4.pc = i9 + 1;
                        indexReg = iCode[i9];
                        stackTop = doSetConstVar(frame, stack, sDbl, stackTop, vars, varDbls, varAttributes, indexReg);
                        break;
                    case -60:
                    case 1:
                    case 82:
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
                    case 103:
                    case 104:
                    case 105:
                    case 106:
                    case 107:
                    case 108:
                    case 109:
                    case 110:
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
                    case 127:
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
                    default:
                        dumpICode(frame.idata);
                        throw new RuntimeException("Unknown icode : " + ((int) b) + " @ pc : " + (frame.pc - 1));
                    case -59:
                        Object rhs = stack[stackTop];
                        if (rhs == DBL_MRK) {
                            rhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                        }
                        stackTop--;
                        stack[stackTop] = ScriptRuntime.setConst((Scriptable) stack[stackTop], rhs, cx, stringReg);
                        break;
                    case -58:
                        Object value = stack[stackTop];
                        stackTop--;
                        int i10 = (int) sDbl[stackTop];
                        ((Object[]) stack[stackTop])[i10] = value;
                        ((int[]) stack[stackTop - 1])[i10] = 1;
                        sDbl[stackTop] = i10 + 1;
                        break;
                    case -57:
                        Object value2 = stack[stackTop];
                        stackTop--;
                        int i11 = (int) sDbl[stackTop];
                        ((Object[]) stack[stackTop])[i11] = value2;
                        ((int[]) stack[stackTop - 1])[i11] = -1;
                        sDbl[stackTop] = i11 + 1;
                        break;
                    case -56:
                        indexReg += frame.localShift;
                        stack[indexReg] = null;
                        break;
                    case -55:
                    case 38:
                    case 71:
                        if (instructionCounting) {
                            cx.instructionCount += 100;
                        }
                        stackTop -= 1 + indexReg;
                        Callable fun = (Callable) stack[stackTop];
                        Scriptable funThisObj = (Scriptable) stack[stackTop + 1];
                        if (b == 71) {
                            Object[] outArgs = getArgsArray(stack, sDbl, stackTop + 2, indexReg);
                            stack[stackTop] = ScriptRuntime.callRef(fun, funThisObj, outArgs, cx);
                        } else {
                            Scriptable calleeScope = frame.scope;
                            if (frame.useActivation) {
                                calleeScope = ScriptableObject.getTopLevelScope(frame.scope);
                            }
                            if (fun instanceof InterpretedFunction) {
                                InterpretedFunction ifun = (InterpretedFunction) fun;
                                if (frame.fnOrScript.securityDomain == ifun.securityDomain) {
                                    CallFrame callParentFrame = frame;
                                    if (b == -55) {
                                        callParentFrame = frame.parentFrame;
                                        exitFrame(cx, frame, null);
                                    }
                                    CallFrame calleeFrame = initFrame(cx, calleeScope, funThisObj, stack, sDbl, stackTop + 2, indexReg, ifun, callParentFrame);
                                    if (b != -55) {
                                        frame.savedStackTop = stackTop;
                                        frame.savedCallOp = b;
                                    }
                                    frame = calleeFrame;
                                }
                            }
                            if (fun instanceof NativeContinuation) {
                                ContinuationJump cjump2 = new ContinuationJump((NativeContinuation) fun, frame);
                                if (indexReg == 0) {
                                    cjump2.result = undefined;
                                } else {
                                    cjump2.result = stack[stackTop + 2];
                                    cjump2.resultDbl = sDbl[stackTop + 2];
                                }
                                throwable = cjump2;
                                if (throwable == null) {
                                    Kit.codeBug();
                                }
                                cjump = null;
                                if (generatorState == null) {
                                    if (!(throwable instanceof JavaScriptException)) {
                                        exState = 2;
                                    } else if (throwable instanceof ContinuationPending) {
                                        exState = 0;
                                    } else if (throwable instanceof RuntimeException) {
                                        if (cx.hasFeature(13)) {
                                            i3 = 2;
                                        } else {
                                            i3 = 1;
                                        }
                                        exState = i3;
                                    } else if (throwable instanceof Error) {
                                        if (cx.hasFeature(13)) {
                                            i2 = 2;
                                        } else {
                                            i2 = 0;
                                        }
                                        exState = i2;
                                    } else if (throwable instanceof ContinuationJump) {
                                        exState = 1;
                                        cjump = (ContinuationJump) throwable;
                                    } else {
                                        if (cx.hasFeature(13)) {
                                            i = 2;
                                        } else {
                                            i = 1;
                                        }
                                        exState = i;
                                    }
                                } else if (!(throwable instanceof JavaScriptException)) {
                                    exState = 2;
                                } else if (throwable instanceof ContinuationPending) {
                                    exState = 0;
                                } else if (throwable instanceof RuntimeException) {
                                    if (cx.hasFeature(13)) {
                                        i3 = 2;
                                    } else {
                                        i3 = 1;
                                    }
                                    exState = i3;
                                } else if (throwable instanceof Error) {
                                    if (cx.hasFeature(13)) {
                                        i2 = 2;
                                    } else {
                                        i2 = 0;
                                    }
                                    exState = i2;
                                } else if (throwable instanceof ContinuationJump) {
                                    exState = 1;
                                    cjump = (ContinuationJump) throwable;
                                } else {
                                    if (cx.hasFeature(13)) {
                                        i = 2;
                                    } else {
                                        i = 1;
                                    }
                                    exState = i;
                                }
                                if (instructionCounting) {
                                    addInstructionCount(cx, frame, 100);
                                }
                                if (frame.debuggerFrame != null) {
                                    RuntimeException rex3 = (RuntimeException) throwable;
                                    frame.debuggerFrame.onExceptionThrown(cx, rex3);
                                }
                                while (true) {
                                    if (exState != 0) {
                                        if (exState != 2) {
                                            z = true;
                                        } else {
                                            z = false;
                                        }
                                        boolean onlyFinally3 = z;
                                        indexReg = getExceptionHandler(frame, onlyFinally3);
                                        if (indexReg >= 0) {
                                            continue;
                                        }
                                    }
                                    exitFrame(cx, frame, throwable);
                                    frame = frame.parentFrame;
                                    if (frame == null) {
                                        if (cjump == null) {
                                        }
                                    } else {
                                        if (cjump == null) {
                                            if (cjump.branchFrame != null) {
                                                Kit.codeBug();
                                            }
                                            if (cjump.capturedFrame != null) {
                                                indexReg = -1;
                                            } else {
                                                interpreterResult = cjump.result;
                                                interpreterResultDbl = cjump.resultDbl;
                                                throwable = null;
                                            }
                                        }
                                        break;
                                    }
                                }
                            } else {
                                if (fun instanceof IdFunctionObject) {
                                    IdFunctionObject ifun2 = (IdFunctionObject) fun;
                                    if (NativeContinuation.isContinuationConstructor(ifun2)) {
                                        frame.stack[stackTop] = captureContinuation(cx, frame.parentFrame, false);
                                    } else if (BaseFunction.isApplyOrCall(ifun2)) {
                                        Callable applyCallable = ScriptRuntime.getCallable(funThisObj);
                                        if (applyCallable instanceof InterpretedFunction) {
                                            InterpretedFunction iApplyCallable = (InterpretedFunction) applyCallable;
                                            if (frame.fnOrScript.securityDomain == iApplyCallable.securityDomain) {
                                                frame = initFrameForApplyOrCall(cx, frame, indexReg, stack, sDbl, stackTop, b, calleeScope, ifun2, iApplyCallable);
                                            }
                                        }
                                    }
                                }
                                if (fun instanceof ScriptRuntime.NoSuchMethodShim) {
                                    ScriptRuntime.NoSuchMethodShim noSuchMethodShim = (ScriptRuntime.NoSuchMethodShim) fun;
                                    Callable noSuchMethodMethod = noSuchMethodShim.noSuchMethodMethod;
                                    if (noSuchMethodMethod instanceof InterpretedFunction) {
                                        InterpretedFunction ifun3 = (InterpretedFunction) noSuchMethodMethod;
                                        if (frame.fnOrScript.securityDomain == ifun3.securityDomain) {
                                            frame = initFrameForNoSuchMethod(cx, frame, indexReg, stack, sDbl, stackTop, b, funThisObj, calleeScope, noSuchMethodShim, ifun3);
                                        }
                                    }
                                }
                                cx.lastInterpreterFrame = frame;
                                frame.savedCallOp = b;
                                frame.savedStackTop = stackTop;
                                stack[stackTop] = fun.call(cx, calleeScope, funThisObj, getArgsArray(stack, sDbl, stackTop + 2, indexReg));
                            }
                        }
                        break;
                    case -54:
                        boolean valBln = stack_boolean(frame, stackTop);
                        Object x = ScriptRuntime.updateDotQuery(valBln, frame.scope);
                        if (x != null) {
                            stack[stackTop] = x;
                            frame.scope = ScriptRuntime.leaveDotQuery(frame.scope);
                            frame.pc += 2;
                        } else {
                            stackTop--;
                            if (instructionCounting) {
                                addInstructionCount(cx, frame, 2);
                            }
                            offset = getShort(iCode, frame.pc);
                            if (offset != 0) {
                                frame.pc += offset - 1;
                            } else {
                                frame.pc = frame.idata.longJumps.getExistingInt(frame.pc);
                            }
                            if (instructionCounting) {
                                frame.pcPrevBranch = frame.pc;
                            }
                        }
                        break;
                    case -53:
                        Object lhs = stack[stackTop];
                        if (lhs == DBL_MRK) {
                            lhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                        }
                        stackTop--;
                        frame.scope = ScriptRuntime.enterDotQuery(lhs, frame.scope);
                        break;
                    case -52:
                        stackTop++;
                        stack[stackTop] = DBL_MRK;
                        sDbl[stackTop] = 1.0d;
                        break;
                    case -51:
                        stackTop++;
                        stack[stackTop] = DBL_MRK;
                        sDbl[stackTop] = 0.0d;
                        break;
                    case -50:
                        stackTop++;
                        stack[stackTop] = undefined;
                        break;
                    case -49:
                        CallFrame callFrame5 = frame;
                        int i12 = callFrame5.pc;
                        callFrame5.pc = i12 + 1;
                        indexReg = iCode[i12];
                        stackTop = doSetVar(frame, stack, sDbl, stackTop, vars, varDbls, varAttributes, indexReg);
                        break;
                    case -48:
                        CallFrame callFrame6 = frame;
                        int i13 = callFrame6.pc;
                        callFrame6.pc = i13 + 1;
                        indexReg = iCode[i13];
                        stackTop = doGetVar(frame, stack, sDbl, stackTop, vars, varDbls, indexReg);
                        break;
                    case -47:
                        stringReg = strings[getInt(iCode, frame.pc)];
                        frame.pc += 4;
                        break;
                    case -46:
                        stringReg = strings[getIndex(iCode, frame.pc)];
                        frame.pc += 2;
                        break;
                    case -45:
                        stringReg = strings[255 & iCode[frame.pc]];
                        frame.pc++;
                        break;
                    case -44:
                        stringReg = strings[3];
                        break;
                    case -43:
                        stringReg = strings[2];
                        break;
                    case -42:
                        stringReg = strings[1];
                        break;
                    case -41:
                        stringReg = strings[0];
                        break;
                    case -40:
                        indexReg = getInt(iCode, frame.pc);
                        frame.pc += 4;
                        break;
                    case -39:
                        indexReg = getIndex(iCode, frame.pc);
                        frame.pc += 2;
                        break;
                    case -38:
                        indexReg = 255 & iCode[frame.pc];
                        frame.pc++;
                        break;
                    case -37:
                        indexReg = 5;
                        break;
                    case -36:
                        indexReg = 4;
                        break;
                    case -35:
                        indexReg = 3;
                        break;
                    case -34:
                        indexReg = 2;
                        break;
                    case -33:
                        indexReg = 1;
                        break;
                    case WinUser.GCW_ATOM /* -32 */:
                        indexReg = 0;
                        break;
                    case -31:
                    case 66:
                    case 67:
                        Object[] data = (Object[]) stack[stackTop];
                        stackTop--;
                        int[] getterSetters = (int[]) stack[stackTop];
                        if (b == 67) {
                            Object[] ids = (Object[]) frame.idata.literalIds[indexReg];
                            val = ScriptRuntime.newObjectLiteral(ids, data, getterSetters, cx, frame.scope);
                        } else {
                            int[] skipIndexces = null;
                            if (b == -31) {
                                skipIndexces = (int[]) frame.idata.literalIds[indexReg];
                            }
                            val = ScriptRuntime.newArrayLiteral(data, skipIndexces, cx, frame.scope);
                        }
                        stack[stackTop] = val;
                        break;
                    case -30:
                        Object value3 = stack[stackTop];
                        if (value3 == DBL_MRK) {
                            value3 = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                        }
                        stackTop--;
                        int i14 = (int) sDbl[stackTop];
                        ((Object[]) stack[stackTop])[i14] = value3;
                        sDbl[stackTop] = i14 + 1;
                        break;
                    case -29:
                        int stackTop2 = stackTop + 1;
                        stack[stackTop2] = new int[indexReg];
                        stackTop = stackTop2 + 1;
                        stack[stackTop] = new Object[indexReg];
                        sDbl[stackTop] = 0.0d;
                        break;
                    case -28:
                        stackTop++;
                        stack[stackTop] = DBL_MRK;
                        sDbl[stackTop] = getInt(iCode, frame.pc);
                        frame.pc += 4;
                        break;
                    case -27:
                        stackTop++;
                        stack[stackTop] = DBL_MRK;
                        sDbl[stackTop] = getShort(iCode, frame.pc);
                        frame.pc += 2;
                        break;
                    case WinUser.GCL_STYLE /* -26 */:
                        frame.pcSourceLineStart = frame.pc;
                        if (frame.debuggerFrame != null) {
                            int line = getIndex(iCode, frame.pc);
                            frame.debuggerFrame.onLineChange(cx, line);
                        }
                        frame.pc += 2;
                        break;
                    case -25:
                        if (instructionCounting) {
                            addInstructionCount(cx, frame, 0);
                        }
                        indexReg += frame.localShift;
                        Object value4 = stack[indexReg];
                        if (value4 != DBL_MRK) {
                            throwable = value4;
                            if (throwable == null) {
                                Kit.codeBug();
                            }
                            cjump = null;
                            if (generatorState == null) {
                                if (!(throwable instanceof JavaScriptException)) {
                                    exState = 2;
                                } else if (throwable instanceof ContinuationPending) {
                                    exState = 0;
                                } else if (throwable instanceof RuntimeException) {
                                    if (cx.hasFeature(13)) {
                                        i3 = 2;
                                    } else {
                                        i3 = 1;
                                    }
                                    exState = i3;
                                } else if (throwable instanceof Error) {
                                    if (cx.hasFeature(13)) {
                                        i2 = 2;
                                    } else {
                                        i2 = 0;
                                    }
                                    exState = i2;
                                } else if (throwable instanceof ContinuationJump) {
                                    exState = 1;
                                    cjump = (ContinuationJump) throwable;
                                } else {
                                    if (cx.hasFeature(13)) {
                                        i = 2;
                                    } else {
                                        i = 1;
                                    }
                                    exState = i;
                                }
                            } else if (!(throwable instanceof JavaScriptException)) {
                                exState = 2;
                            } else if (throwable instanceof ContinuationPending) {
                                exState = 0;
                            } else if (throwable instanceof RuntimeException) {
                                if (cx.hasFeature(13)) {
                                    i3 = 2;
                                } else {
                                    i3 = 1;
                                }
                                exState = i3;
                            } else if (throwable instanceof Error) {
                                if (cx.hasFeature(13)) {
                                    i2 = 2;
                                } else {
                                    i2 = 0;
                                }
                                exState = i2;
                            } else if (throwable instanceof ContinuationJump) {
                                exState = 1;
                                cjump = (ContinuationJump) throwable;
                            } else {
                                if (cx.hasFeature(13)) {
                                    i = 2;
                                } else {
                                    i = 1;
                                }
                                exState = i;
                            }
                            if (instructionCounting) {
                                addInstructionCount(cx, frame, 100);
                            }
                            if (frame.debuggerFrame != null) {
                                RuntimeException rex4 = (RuntimeException) throwable;
                                frame.debuggerFrame.onExceptionThrown(cx, rex4);
                            }
                            while (true) {
                                if (exState != 0) {
                                    if (exState != 2) {
                                        z = true;
                                    } else {
                                        z = false;
                                    }
                                    boolean onlyFinally4 = z;
                                    indexReg = getExceptionHandler(frame, onlyFinally4);
                                    if (indexReg >= 0) {
                                        continue;
                                    }
                                }
                                exitFrame(cx, frame, throwable);
                                frame = frame.parentFrame;
                                if (frame == null) {
                                    if (cjump == null) {
                                    }
                                } else {
                                    if (cjump == null) {
                                        if (cjump.branchFrame != null) {
                                            Kit.codeBug();
                                        }
                                        if (cjump.capturedFrame != null) {
                                            indexReg = -1;
                                        } else {
                                            interpreterResult = cjump.result;
                                            interpreterResultDbl = cjump.resultDbl;
                                            throwable = null;
                                        }
                                    }
                                    break;
                                }
                                break;
                            }
                            break;
                        } else {
                            frame.pc = (int) sDbl[indexReg];
                            if (instructionCounting) {
                                frame.pcPrevBranch = frame.pc;
                            }
                        }
                        break;
                    case WinUser.GCLP_WNDPROC /* -24 */:
                        if (stackTop == frame.emptyStackTop + 1) {
                            indexReg += frame.localShift;
                            stack[indexReg] = stack[stackTop];
                            sDbl[indexReg] = sDbl[stackTop];
                            stackTop--;
                        } else if (stackTop != frame.emptyStackTop) {
                            Kit.codeBug();
                        }
                        break;
                    case -23:
                        stackTop++;
                        stack[stackTop] = DBL_MRK;
                        sDbl[stackTop] = frame.pc + 2;
                        if (instructionCounting) {
                            addInstructionCount(cx, frame, 2);
                        }
                        offset = getShort(iCode, frame.pc);
                        if (offset != 0) {
                            frame.pc += offset - 1;
                        } else {
                            frame.pc = frame.idata.longJumps.getExistingInt(frame.pc);
                        }
                        if (instructionCounting) {
                            frame.pcPrevBranch = frame.pc;
                        }
                        break;
                    case -22:
                        frame.result = undefined;
                        exitFrame(cx, frame, null);
                        interpreterResult = frame.result;
                        interpreterResultDbl = frame.resultDbl;
                        if (frame.parentFrame != null) {
                            frame = frame.parentFrame;
                            if (frame.frozen) {
                                frame = frame.cloneFrozen();
                            }
                            setCallResult(frame, interpreterResult, interpreterResultDbl);
                            interpreterResult = null;
                        }
                        break;
                    case WinUser.GWL_USERDATA /* -21 */:
                        if (instructionCounting) {
                            cx.instructionCount += 100;
                        }
                        stackTop = doCallSpecial(cx, frame, stack, sDbl, stackTop, iCode, indexReg);
                        break;
                    case -20:
                        initFunction(cx, frame.scope, frame.fnOrScript, indexReg);
                        break;
                    case -19:
                        InterpretedFunction fn = InterpretedFunction.createFunction(cx, frame.scope, frame.fnOrScript, indexReg);
                        if (fn.idata.itsFunctionType == 4) {
                            stackTop++;
                            stack[stackTop] = new ArrowFunction(cx, frame.scope, fn, frame.thisObj);
                        } else {
                            stackTop++;
                            stack[stackTop] = fn;
                        }
                        break;
                    case WinUser.GCL_CBWNDEXTRA /* -18 */:
                        Object value5 = stack[stackTop];
                        if (value5 == DBL_MRK) {
                            value5 = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                        }
                        stack[stackTop] = ScriptRuntime.getValueFunctionAndThis(value5, cx);
                        stackTop++;
                        stack[stackTop] = ScriptRuntime.lastStoredScriptable(cx);
                        break;
                    case -17:
                        Object obj2 = stack[stackTop - 1];
                        if (obj2 == DBL_MRK) {
                            obj2 = ScriptRuntime.wrapNumber(sDbl[stackTop - 1]);
                        }
                        Object id = stack[stackTop];
                        if (id == DBL_MRK) {
                            id = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                        }
                        stack[stackTop - 1] = ScriptRuntime.getElemFunctionAndThis(obj2, id, cx, frame.scope);
                        stack[stackTop] = ScriptRuntime.lastStoredScriptable(cx);
                        break;
                    case -16:
                        Object obj3 = stack[stackTop];
                        if (obj3 == DBL_MRK) {
                            obj3 = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                        }
                        stack[stackTop] = ScriptRuntime.getPropFunctionAndThis(obj3, stringReg, cx, frame.scope);
                        stackTop++;
                        stack[stackTop] = ScriptRuntime.lastStoredScriptable(cx);
                        break;
                    case -15:
                        int stackTop3 = stackTop + 1;
                        stack[stackTop3] = ScriptRuntime.getNameFunctionAndThis(stringReg, cx, frame.scope);
                        stackTop = stackTop3 + 1;
                        stack[stackTop] = ScriptRuntime.lastStoredScriptable(cx);
                        break;
                    case -14:
                        stackTop++;
                        stack[stackTop] = ScriptRuntime.typeofName(frame.scope, stringReg);
                        break;
                    case -13:
                        indexReg += frame.localShift;
                        stack[indexReg] = frame.scope;
                        break;
                    case -12:
                        indexReg += frame.localShift;
                        frame.scope = (Scriptable) stack[indexReg];
                        break;
                    case Wincon.STD_OUTPUT_HANDLE /* -11 */:
                        Ref ref = (Ref) stack[stackTop];
                        stack[stackTop] = ScriptRuntime.refIncrDecr(ref, cx, frame.scope, iCode[frame.pc]);
                        frame.pc++;
                        break;
                    case -10:
                        stackTop = doElemIncDec(cx, frame, iCode, stack, sDbl, stackTop);
                        break;
                    case -9:
                        Object lhs2 = stack[stackTop];
                        if (lhs2 == DBL_MRK) {
                            lhs2 = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                        }
                        stack[stackTop] = ScriptRuntime.propIncrDecr(lhs2, stringReg, cx, frame.scope, iCode[frame.pc]);
                        frame.pc++;
                        break;
                    case -8:
                        stackTop++;
                        stack[stackTop] = ScriptRuntime.nameIncrDecr(frame.scope, stringReg, cx, iCode[frame.pc]);
                        frame.pc++;
                        break;
                    case Msi.INSTALLSTATE_NOTUSED /* -7 */:
                        stackTop = doVarIncDec(cx, frame, stack, sDbl, stackTop, vars, varDbls, varAttributes, indexReg);
                        break;
                    case -6:
                        int i15 = stackTop;
                        stackTop--;
                        if (stack_boolean(frame, i15)) {
                            stackTop--;
                            stack[stackTop] = null;
                            if (instructionCounting) {
                                addInstructionCount(cx, frame, 2);
                            }
                            offset = getShort(iCode, frame.pc);
                            if (offset != 0) {
                                frame.pc += offset - 1;
                            } else {
                                frame.pc = frame.idata.longJumps.getExistingInt(frame.pc);
                            }
                            if (instructionCounting) {
                                frame.pcPrevBranch = frame.pc;
                            }
                        } else {
                            frame.pc += 2;
                        }
                        break;
                    case Msi.INSTALLSTATE_INCOMPLETE /* -5 */:
                        frame.result = stack[stackTop];
                        frame.resultDbl = sDbl[stackTop];
                        stack[stackTop] = null;
                        stackTop--;
                        break;
                    case -4:
                        stack[stackTop] = null;
                        stackTop--;
                        break;
                    case -3:
                        Object o = stack[stackTop];
                        stack[stackTop] = stack[stackTop - 1];
                        stack[stackTop - 1] = o;
                        double d = sDbl[stackTop];
                        sDbl[stackTop] = sDbl[stackTop - 1];
                        sDbl[stackTop - 1] = d;
                        break;
                    case -2:
                        stack[stackTop + 1] = stack[stackTop - 1];
                        sDbl[stackTop + 1] = sDbl[stackTop - 1];
                        stack[stackTop + 2] = stack[stackTop];
                        sDbl[stackTop + 2] = sDbl[stackTop];
                        stackTop += 2;
                        break;
                    case -1:
                        stack[stackTop + 1] = stack[stackTop];
                        sDbl[stackTop + 1] = sDbl[stackTop];
                        stackTop++;
                        break;
                    case 0:
                    case 31:
                        stackTop = doDelName(cx, frame, b, stack, sDbl, stackTop);
                        break;
                    case 2:
                        Object lhs3 = stack[stackTop];
                        if (lhs3 == DBL_MRK) {
                            lhs3 = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                        }
                        stackTop--;
                        frame.scope = ScriptRuntime.enterWith(lhs3, cx, frame.scope);
                        break;
                    case 3:
                        frame.scope = ScriptRuntime.leaveWith(frame.scope);
                        break;
                    case 4:
                        frame.result = stack[stackTop];
                        frame.resultDbl = sDbl[stackTop];
                        int i16 = stackTop - 1;
                        exitFrame(cx, frame, null);
                        interpreterResult = frame.result;
                        interpreterResultDbl = frame.resultDbl;
                        if (frame.parentFrame != null) {
                            frame = frame.parentFrame;
                            if (frame.frozen) {
                                frame = frame.cloneFrozen();
                            }
                            setCallResult(frame, interpreterResult, interpreterResultDbl);
                            interpreterResult = null;
                        }
                        break;
                    case 5:
                        if (instructionCounting) {
                            addInstructionCount(cx, frame, 2);
                        }
                        offset = getShort(iCode, frame.pc);
                        if (offset != 0) {
                            frame.pc += offset - 1;
                        } else {
                            frame.pc = frame.idata.longJumps.getExistingInt(frame.pc);
                        }
                        if (instructionCounting) {
                            frame.pcPrevBranch = frame.pc;
                        }
                        break;
                    case 6:
                        int i17 = stackTop;
                        stackTop--;
                        if (stack_boolean(frame, i17)) {
                            if (instructionCounting) {
                                addInstructionCount(cx, frame, 2);
                            }
                            offset = getShort(iCode, frame.pc);
                            if (offset != 0) {
                                frame.pc += offset - 1;
                            } else {
                                frame.pc = frame.idata.longJumps.getExistingInt(frame.pc);
                            }
                            if (instructionCounting) {
                                frame.pcPrevBranch = frame.pc;
                            }
                        } else {
                            frame.pc += 2;
                        }
                        break;
                    case 7:
                        int i18 = stackTop;
                        stackTop--;
                        if (stack_boolean(frame, i18)) {
                            frame.pc += 2;
                        } else {
                            if (instructionCounting) {
                                addInstructionCount(cx, frame, 2);
                            }
                            offset = getShort(iCode, frame.pc);
                            if (offset != 0) {
                                frame.pc += offset - 1;
                            } else {
                                frame.pc = frame.idata.longJumps.getExistingInt(frame.pc);
                            }
                            if (instructionCounting) {
                                frame.pcPrevBranch = frame.pc;
                            }
                        }
                        break;
                    case 8:
                    case 74:
                        Object rhs2 = stack[stackTop];
                        if (rhs2 == DBL_MRK) {
                            rhs2 = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                        }
                        stackTop--;
                        Scriptable lhs4 = (Scriptable) stack[stackTop];
                        if (b == 8) {
                            objStrictSetName = ScriptRuntime.setName(lhs4, rhs2, cx, frame.scope, stringReg);
                        } else {
                            objStrictSetName = ScriptRuntime.strictSetName(lhs4, rhs2, cx, frame.scope, stringReg);
                        }
                        stack[stackTop] = objStrictSetName;
                        break;
                    case 9:
                    case 10:
                    case 11:
                    case 18:
                    case 19:
                        stackTop = doBitOp(frame, b, stack, sDbl, stackTop);
                        break;
                    case 12:
                    case 13:
                        stackTop--;
                        boolean valBln2 = doEquals(stack, sDbl, stackTop);
                        stack[stackTop] = ScriptRuntime.wrapBoolean(valBln2 ^ (b == 13));
                        break;
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                        stackTop = doCompare(frame, b, stack, sDbl, stackTop);
                        break;
                    case 20:
                        double lDbl = stack_double(frame, stackTop - 1);
                        int rIntValue = stack_int32(frame, stackTop) & 31;
                        stackTop--;
                        stack[stackTop] = DBL_MRK;
                        sDbl[stackTop] = ScriptRuntime.toUint32(lDbl) >>> rIntValue;
                        break;
                    case 21:
                        stackTop--;
                        doAdd(stack, sDbl, stackTop, cx);
                        break;
                    case 22:
                    case 23:
                    case 24:
                    case 25:
                        stackTop = doArithmetic(frame, b, stack, sDbl, stackTop);
                        break;
                    case 26:
                        stack[stackTop] = ScriptRuntime.wrapBoolean(!stack_boolean(frame, stackTop));
                        break;
                    case 27:
                        int rIntValue2 = stack_int32(frame, stackTop);
                        stack[stackTop] = DBL_MRK;
                        sDbl[stackTop] = rIntValue2 ^ (-1);
                        break;
                    case 28:
                    case 29:
                        double rDbl = stack_double(frame, stackTop);
                        stack[stackTop] = DBL_MRK;
                        if (b == 29) {
                            rDbl = -rDbl;
                        }
                        sDbl[stackTop] = rDbl;
                        break;
                    case 30:
                        if (instructionCounting) {
                            cx.instructionCount += 100;
                        }
                        stackTop -= indexReg;
                        Object lhs5 = stack[stackTop];
                        if (lhs5 instanceof InterpretedFunction) {
                            InterpretedFunction f = (InterpretedFunction) lhs5;
                            if (frame.fnOrScript.securityDomain == f.securityDomain) {
                                Scriptable newInstance = f.createObject(cx, frame.scope);
                                CallFrame calleeFrame2 = initFrame(cx, frame.scope, newInstance, stack, sDbl, stackTop + 1, indexReg, f, frame);
                                stack[stackTop] = newInstance;
                                frame.savedStackTop = stackTop;
                                frame.savedCallOp = b;
                                frame = calleeFrame2;
                            }
                        }
                        if (!(lhs5 instanceof Function)) {
                            if (lhs5 == DBL_MRK) {
                                lhs5 = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                            }
                            throw ScriptRuntime.notFunctionError(lhs5);
                        }
                        Function fun2 = (Function) lhs5;
                        if ((fun2 instanceof IdFunctionObject) && NativeContinuation.isContinuationConstructor((IdFunctionObject) fun2)) {
                            frame.stack[stackTop] = captureContinuation(cx, frame.parentFrame, false);
                        } else {
                            Object[] outArgs2 = getArgsArray(stack, sDbl, stackTop + 1, indexReg);
                            stack[stackTop] = fun2.construct(cx, frame.scope, outArgs2);
                        }
                        break;
                        break;
                    case 32:
                        Object lhs6 = stack[stackTop];
                        if (lhs6 == DBL_MRK) {
                            lhs6 = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                        }
                        stack[stackTop] = ScriptRuntime.typeof(lhs6);
                        break;
                    case 33:
                        Object lhs7 = stack[stackTop];
                        if (lhs7 == DBL_MRK) {
                            lhs7 = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                        }
                        stack[stackTop] = ScriptRuntime.getObjectProp(lhs7, stringReg, cx, frame.scope);
                        break;
                    case 34:
                        Object lhs8 = stack[stackTop];
                        if (lhs8 == DBL_MRK) {
                            lhs8 = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                        }
                        stack[stackTop] = ScriptRuntime.getObjectPropNoWarn(lhs8, stringReg, cx, frame.scope);
                        break;
                    case 35:
                        Object rhs3 = stack[stackTop];
                        if (rhs3 == DBL_MRK) {
                            rhs3 = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                        }
                        stackTop--;
                        Object lhs9 = stack[stackTop];
                        if (lhs9 == DBL_MRK) {
                            lhs9 = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                        }
                        stack[stackTop] = ScriptRuntime.setObjectProp(lhs9, stringReg, rhs3, cx, frame.scope);
                        break;
                    case 36:
                        stackTop = doGetElem(cx, frame, stack, sDbl, stackTop);
                        break;
                    case 37:
                        stackTop = doSetElem(cx, frame, stack, sDbl, stackTop);
                        break;
                    case 39:
                        stackTop++;
                        stack[stackTop] = ScriptRuntime.name(cx, frame.scope, stringReg);
                        break;
                    case 40:
                        stackTop++;
                        stack[stackTop] = DBL_MRK;
                        sDbl[stackTop] = frame.idata.itsDoubleTable[indexReg];
                        break;
                    case 41:
                        stackTop++;
                        stack[stackTop] = stringReg;
                        break;
                    case 42:
                        stackTop++;
                        stack[stackTop] = null;
                        break;
                    case 43:
                        stackTop++;
                        stack[stackTop] = frame.thisObj;
                        break;
                    case 44:
                        stackTop++;
                        stack[stackTop] = Boolean.FALSE;
                        break;
                    case 45:
                        stackTop++;
                        stack[stackTop] = Boolean.TRUE;
                        break;
                    case 46:
                    case 47:
                        stackTop--;
                        boolean valBln3 = doShallowEquals(stack, sDbl, stackTop);
                        stack[stackTop] = ScriptRuntime.wrapBoolean(valBln3 ^ (b == 47));
                        break;
                    case 48:
                        Object re = frame.idata.itsRegExpLiterals[indexReg];
                        stackTop++;
                        stack[stackTop] = ScriptRuntime.wrapRegExp(cx, frame.scope, re);
                        break;
                    case 49:
                        stackTop++;
                        stack[stackTop] = ScriptRuntime.bind(cx, frame.scope, stringReg);
                        break;
                    case 50:
                        Object value6 = stack[stackTop];
                        if (value6 == DBL_MRK) {
                            value6 = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                        }
                        int i19 = stackTop - 1;
                        int sourceLine3 = getIndex(iCode, frame.pc);
                        throwable = new JavaScriptException(value6, frame.idata.itsSourceFile, sourceLine3);
                        if (throwable == null) {
                            Kit.codeBug();
                        }
                        cjump = null;
                        if (generatorState == null) {
                            if (!(throwable instanceof JavaScriptException)) {
                                exState = 2;
                            } else if (throwable instanceof ContinuationPending) {
                                exState = 0;
                            } else if (throwable instanceof RuntimeException) {
                                if (cx.hasFeature(13)) {
                                    i3 = 2;
                                } else {
                                    i3 = 1;
                                }
                                exState = i3;
                            } else if (throwable instanceof Error) {
                                if (cx.hasFeature(13)) {
                                    i2 = 2;
                                } else {
                                    i2 = 0;
                                }
                                exState = i2;
                            } else if (throwable instanceof ContinuationJump) {
                                exState = 1;
                                cjump = (ContinuationJump) throwable;
                            } else {
                                if (cx.hasFeature(13)) {
                                    i = 2;
                                } else {
                                    i = 1;
                                }
                                exState = i;
                            }
                        } else if (!(throwable instanceof JavaScriptException)) {
                            exState = 2;
                        } else if (throwable instanceof ContinuationPending) {
                            exState = 0;
                        } else if (throwable instanceof RuntimeException) {
                            if (cx.hasFeature(13)) {
                                i3 = 2;
                            } else {
                                i3 = 1;
                            }
                            exState = i3;
                        } else if (throwable instanceof Error) {
                            if (cx.hasFeature(13)) {
                                i2 = 2;
                            } else {
                                i2 = 0;
                            }
                            exState = i2;
                        } else if (throwable instanceof ContinuationJump) {
                            exState = 1;
                            cjump = (ContinuationJump) throwable;
                        } else {
                            if (cx.hasFeature(13)) {
                                i = 2;
                            } else {
                                i = 1;
                            }
                            exState = i;
                        }
                        if (instructionCounting) {
                            addInstructionCount(cx, frame, 100);
                        }
                        if (frame.debuggerFrame != null) {
                            RuntimeException rex5 = (RuntimeException) throwable;
                            frame.debuggerFrame.onExceptionThrown(cx, rex5);
                        }
                        while (true) {
                            if (exState != 0) {
                                if (exState != 2) {
                                    z = true;
                                } else {
                                    z = false;
                                }
                                boolean onlyFinally5 = z;
                                indexReg = getExceptionHandler(frame, onlyFinally5);
                                if (indexReg >= 0) {
                                    continue;
                                }
                            }
                            exitFrame(cx, frame, throwable);
                            frame = frame.parentFrame;
                            if (frame == null) {
                                if (cjump == null) {
                                }
                            } else {
                                if (cjump == null) {
                                    if (cjump.branchFrame != null) {
                                        Kit.codeBug();
                                    }
                                    if (cjump.capturedFrame != null) {
                                        indexReg = -1;
                                    } else {
                                        interpreterResult = cjump.result;
                                        interpreterResultDbl = cjump.resultDbl;
                                        throwable = null;
                                    }
                                }
                                break;
                            }
                            break;
                        }
                        break;
                    case 51:
                        throwable = stack[indexReg + frame.localShift];
                        if (throwable == null) {
                            Kit.codeBug();
                        }
                        cjump = null;
                        if (generatorState == null) {
                            if (!(throwable instanceof JavaScriptException)) {
                                exState = 2;
                            } else if (throwable instanceof ContinuationPending) {
                                exState = 0;
                            } else if (throwable instanceof RuntimeException) {
                                if (cx.hasFeature(13)) {
                                    i3 = 2;
                                } else {
                                    i3 = 1;
                                }
                                exState = i3;
                            } else if (throwable instanceof Error) {
                                if (cx.hasFeature(13)) {
                                    i2 = 2;
                                } else {
                                    i2 = 0;
                                }
                                exState = i2;
                            } else if (throwable instanceof ContinuationJump) {
                                exState = 1;
                                cjump = (ContinuationJump) throwable;
                            } else {
                                if (cx.hasFeature(13)) {
                                    i = 2;
                                } else {
                                    i = 1;
                                }
                                exState = i;
                            }
                        } else if (!(throwable instanceof JavaScriptException)) {
                            exState = 2;
                        } else if (throwable instanceof ContinuationPending) {
                            exState = 0;
                        } else if (throwable instanceof RuntimeException) {
                            if (cx.hasFeature(13)) {
                                i3 = 2;
                            } else {
                                i3 = 1;
                            }
                            exState = i3;
                        } else if (throwable instanceof Error) {
                            if (cx.hasFeature(13)) {
                                i2 = 2;
                            } else {
                                i2 = 0;
                            }
                            exState = i2;
                        } else if (throwable instanceof ContinuationJump) {
                            exState = 1;
                            cjump = (ContinuationJump) throwable;
                        } else {
                            if (cx.hasFeature(13)) {
                                i = 2;
                            } else {
                                i = 1;
                            }
                            exState = i;
                        }
                        if (instructionCounting) {
                            addInstructionCount(cx, frame, 100);
                        }
                        if (frame.debuggerFrame != null) {
                            RuntimeException rex6 = (RuntimeException) throwable;
                            frame.debuggerFrame.onExceptionThrown(cx, rex6);
                        }
                        while (true) {
                            if (exState != 0) {
                                if (exState != 2) {
                                    z = true;
                                } else {
                                    z = false;
                                }
                                boolean onlyFinally6 = z;
                                indexReg = getExceptionHandler(frame, onlyFinally6);
                                if (indexReg >= 0) {
                                    continue;
                                }
                            }
                            exitFrame(cx, frame, throwable);
                            frame = frame.parentFrame;
                            if (frame == null) {
                                if (cjump == null) {
                                }
                            } else {
                                if (cjump == null) {
                                    if (cjump.branchFrame != null) {
                                        Kit.codeBug();
                                    }
                                    if (cjump.capturedFrame != null) {
                                        indexReg = -1;
                                    } else {
                                        interpreterResult = cjump.result;
                                        interpreterResultDbl = cjump.resultDbl;
                                        throwable = null;
                                    }
                                }
                                break;
                            }
                            break;
                        }
                        break;
                    case 52:
                    case 53:
                        stackTop = doInOrInstanceof(cx, b, stack, sDbl, stackTop);
                        break;
                    case 54:
                        stackTop++;
                        indexReg += frame.localShift;
                        stack[stackTop] = stack[indexReg];
                        sDbl[stackTop] = sDbl[indexReg];
                        break;
                    case 55:
                        stackTop = doGetVar(frame, stack, sDbl, stackTop, vars, varDbls, indexReg);
                        break;
                    case 56:
                        stackTop = doSetVar(frame, stack, sDbl, stackTop, vars, varDbls, varAttributes, indexReg);
                        break;
                    case 57:
                        stackTop--;
                        indexReg += frame.localShift;
                        boolean afterFirstScope = frame.idata.itsICode[frame.pc] != 0;
                        Throwable caughtException = (Throwable) stack[stackTop + 1];
                        if (!afterFirstScope) {
                            lastCatchScope = null;
                        } else {
                            lastCatchScope = (Scriptable) stack[indexReg];
                        }
                        stack[indexReg] = ScriptRuntime.newCatchScope(caughtException, lastCatchScope, stringReg, cx, frame.scope);
                        frame.pc++;
                        break;
                    case 58:
                    case 59:
                    case 60:
                    case 61:
                        Object lhs10 = stack[stackTop];
                        if (lhs10 == DBL_MRK) {
                            lhs10 = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                        }
                        stackTop--;
                        indexReg += frame.localShift;
                        if (b == 58) {
                            i4 = 0;
                        } else if (b == 59) {
                            i4 = 1;
                        } else {
                            i4 = b == 61 ? 6 : 2;
                        }
                        int enumType = i4;
                        stack[indexReg] = ScriptRuntime.enumInit(lhs10, cx, frame.scope, enumType);
                        break;
                    case 62:
                    case 63:
                        indexReg += frame.localShift;
                        Object val2 = stack[indexReg];
                        stackTop++;
                        if (b == 62) {
                            objEnumId = ScriptRuntime.enumNext(val2);
                        } else {
                            objEnumId = ScriptRuntime.enumId(val2, cx);
                        }
                        stack[stackTop] = objEnumId;
                        break;
                    case 64:
                        stackTop++;
                        stack[stackTop] = frame.fnOrScript;
                        break;
                    case 65:
                        exitFrame(cx, frame, null);
                        interpreterResult = frame.result;
                        interpreterResultDbl = frame.resultDbl;
                        if (frame.parentFrame != null) {
                            frame = frame.parentFrame;
                            if (frame.frozen) {
                                frame = frame.cloneFrozen();
                            }
                            setCallResult(frame, interpreterResult, interpreterResultDbl);
                            interpreterResult = null;
                        }
                        break;
                    case 68:
                        Ref ref2 = (Ref) stack[stackTop];
                        stack[stackTop] = ScriptRuntime.refGet(ref2, cx);
                        break;
                    case 69:
                        Object value7 = stack[stackTop];
                        if (value7 == DBL_MRK) {
                            value7 = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                        }
                        stackTop--;
                        Ref ref3 = (Ref) stack[stackTop];
                        stack[stackTop] = ScriptRuntime.refSet(ref3, value7, cx, frame.scope);
                        break;
                    case 70:
                        Ref ref4 = (Ref) stack[stackTop];
                        stack[stackTop] = ScriptRuntime.refDel(ref4, cx);
                        break;
                    case 72:
                        Object obj4 = stack[stackTop];
                        if (obj4 == DBL_MRK) {
                            obj4 = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                        }
                        stack[stackTop] = ScriptRuntime.specialRef(obj4, stringReg, cx, frame.scope);
                        break;
                    case 75:
                        Object value8 = stack[stackTop];
                        if (value8 == DBL_MRK) {
                            value8 = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                        }
                        stack[stackTop] = ScriptRuntime.setDefaultNamespace(value8, cx);
                        break;
                    case 76:
                        Object value9 = stack[stackTop];
                        if (value9 != DBL_MRK) {
                            stack[stackTop] = ScriptRuntime.escapeAttributeValue(value9, cx);
                        }
                        break;
                    case 77:
                        Object value10 = stack[stackTop];
                        if (value10 != DBL_MRK) {
                            stack[stackTop] = ScriptRuntime.escapeTextValue(value10, cx);
                        }
                        break;
                    case 78:
                        stackTop = doRefMember(cx, stack, sDbl, stackTop, indexReg);
                        break;
                    case 79:
                        stackTop = doRefNsMember(cx, stack, sDbl, stackTop, indexReg);
                        break;
                    case 80:
                        Object name = stack[stackTop];
                        if (name == DBL_MRK) {
                            name = ScriptRuntime.wrapNumber(sDbl[stackTop]);
                        }
                        stack[stackTop] = ScriptRuntime.nameRef(name, cx, frame.scope, indexReg);
                        break;
                    case 81:
                        stackTop = doRefNsName(cx, frame, stack, sDbl, stackTop, indexReg);
                        break;
                    case 157:
                        stackTop = doSetConstVar(frame, stack, sDbl, stackTop, vars, varDbls, varAttributes, indexReg);
                        break;
                }
            }
        }
        if (cx.previousInterpreterInvocations != null && cx.previousInterpreterInvocations.size() != 0) {
            cx.lastInterpreterFrame = cx.previousInterpreterInvocations.pop();
        } else {
            cx.lastInterpreterFrame = null;
            cx.previousInterpreterInvocations = null;
        }
        if (throwable == null) {
            return interpreterResult != DBL_MRK ? interpreterResult : ScriptRuntime.wrapNumber(interpreterResultDbl);
        }
        if (throwable instanceof RuntimeException) {
            throw ((RuntimeException) throwable);
        }
        throw ((Error) throwable);
    }

    private static int doInOrInstanceof(Context cx, int op, Object[] stack, double[] sDbl, int stackTop) {
        boolean valBln;
        Object rhs = stack[stackTop];
        if (rhs == UniqueTag.DOUBLE_MARK) {
            rhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
        }
        int stackTop2 = stackTop - 1;
        Object lhs = stack[stackTop2];
        if (lhs == UniqueTag.DOUBLE_MARK) {
            lhs = ScriptRuntime.wrapNumber(sDbl[stackTop2]);
        }
        if (op == 52) {
            valBln = ScriptRuntime.in(lhs, rhs, cx);
        } else {
            valBln = ScriptRuntime.instanceOf(lhs, rhs, cx);
        }
        stack[stackTop2] = ScriptRuntime.wrapBoolean(valBln);
        return stackTop2;
    }

    private static int doCompare(CallFrame frame, int op, Object[] stack, double[] sDbl, int stackTop) {
        boolean valBln;
        double rDbl;
        double lDbl;
        int stackTop2 = stackTop - 1;
        Object rhs = stack[stackTop2 + 1];
        Object lhs = stack[stackTop2];
        if (rhs == UniqueTag.DOUBLE_MARK) {
            rDbl = sDbl[stackTop2 + 1];
            lDbl = stack_double(frame, stackTop2);
        } else {
            if (lhs == UniqueTag.DOUBLE_MARK) {
                rDbl = ScriptRuntime.toNumber(rhs);
                lDbl = sDbl[stackTop2];
            } else {
                switch (op) {
                    case 14:
                        valBln = ScriptRuntime.cmp_LT(lhs, rhs);
                        break;
                    case 15:
                        valBln = ScriptRuntime.cmp_LE(lhs, rhs);
                        break;
                    case 16:
                        valBln = ScriptRuntime.cmp_LT(rhs, lhs);
                        break;
                    case 17:
                        valBln = ScriptRuntime.cmp_LE(rhs, lhs);
                        break;
                    default:
                        throw Kit.codeBug();
                }
            }
            stack[stackTop2] = ScriptRuntime.wrapBoolean(valBln);
            return stackTop2;
        }
        switch (op) {
            case 14:
                valBln = lDbl < rDbl;
                break;
            case 15:
                valBln = lDbl <= rDbl;
                break;
            case 16:
                valBln = lDbl > rDbl;
                break;
            case 17:
                valBln = lDbl >= rDbl;
                break;
            default:
                throw Kit.codeBug();
        }
        stack[stackTop2] = ScriptRuntime.wrapBoolean(valBln);
        return stackTop2;
    }

    private static int doBitOp(CallFrame frame, int op, Object[] stack, double[] sDbl, int stackTop) {
        int lIntValue = stack_int32(frame, stackTop - 1);
        int rIntValue = stack_int32(frame, stackTop);
        int stackTop2 = stackTop - 1;
        stack[stackTop2] = UniqueTag.DOUBLE_MARK;
        switch (op) {
            case 9:
                lIntValue |= rIntValue;
                break;
            case 10:
                lIntValue ^= rIntValue;
                break;
            case 11:
                lIntValue &= rIntValue;
                break;
            case 18:
                lIntValue <<= rIntValue;
                break;
            case 19:
                lIntValue >>= rIntValue;
                break;
        }
        sDbl[stackTop2] = lIntValue;
        return stackTop2;
    }

    private static int doDelName(Context cx, CallFrame frame, int op, Object[] stack, double[] sDbl, int stackTop) {
        Object rhs = stack[stackTop];
        if (rhs == UniqueTag.DOUBLE_MARK) {
            rhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
        }
        int stackTop2 = stackTop - 1;
        Object lhs = stack[stackTop2];
        if (lhs == UniqueTag.DOUBLE_MARK) {
            lhs = ScriptRuntime.wrapNumber(sDbl[stackTop2]);
        }
        stack[stackTop2] = ScriptRuntime.delete(lhs, rhs, cx, frame.scope, op == 0);
        return stackTop2;
    }

    private static int doGetElem(Context cx, CallFrame frame, Object[] stack, double[] sDbl, int stackTop) {
        Object value;
        int stackTop2 = stackTop - 1;
        Object lhs = stack[stackTop2];
        if (lhs == UniqueTag.DOUBLE_MARK) {
            lhs = ScriptRuntime.wrapNumber(sDbl[stackTop2]);
        }
        Object id = stack[stackTop2 + 1];
        if (id != UniqueTag.DOUBLE_MARK) {
            value = ScriptRuntime.getObjectElem(lhs, id, cx, frame.scope);
        } else {
            double d = sDbl[stackTop2 + 1];
            value = ScriptRuntime.getObjectIndex(lhs, d, cx, frame.scope);
        }
        stack[stackTop2] = value;
        return stackTop2;
    }

    private static int doSetElem(Context cx, CallFrame frame, Object[] stack, double[] sDbl, int stackTop) {
        Object value;
        int stackTop2 = stackTop - 2;
        Object rhs = stack[stackTop2 + 2];
        if (rhs == UniqueTag.DOUBLE_MARK) {
            rhs = ScriptRuntime.wrapNumber(sDbl[stackTop2 + 2]);
        }
        Object lhs = stack[stackTop2];
        if (lhs == UniqueTag.DOUBLE_MARK) {
            lhs = ScriptRuntime.wrapNumber(sDbl[stackTop2]);
        }
        Object id = stack[stackTop2 + 1];
        if (id != UniqueTag.DOUBLE_MARK) {
            value = ScriptRuntime.setObjectElem(lhs, id, rhs, cx, frame.scope);
        } else {
            double d = sDbl[stackTop2 + 1];
            value = ScriptRuntime.setObjectIndex(lhs, d, rhs, cx, frame.scope);
        }
        stack[stackTop2] = value;
        return stackTop2;
    }

    private static int doElemIncDec(Context cx, CallFrame frame, byte[] iCode, Object[] stack, double[] sDbl, int stackTop) {
        Object rhs = stack[stackTop];
        if (rhs == UniqueTag.DOUBLE_MARK) {
            rhs = ScriptRuntime.wrapNumber(sDbl[stackTop]);
        }
        int stackTop2 = stackTop - 1;
        Object lhs = stack[stackTop2];
        if (lhs == UniqueTag.DOUBLE_MARK) {
            lhs = ScriptRuntime.wrapNumber(sDbl[stackTop2]);
        }
        stack[stackTop2] = ScriptRuntime.elemIncrDecr(lhs, rhs, cx, frame.scope, iCode[frame.pc]);
        frame.pc++;
        return stackTop2;
    }

    private static int doCallSpecial(Context cx, CallFrame frame, Object[] stack, double[] sDbl, int stackTop, byte[] iCode, int indexReg) {
        int stackTop2;
        int callType = iCode[frame.pc] & 255;
        boolean isNew = iCode[frame.pc + 1] != 0;
        int sourceLine = getIndex(iCode, frame.pc + 2);
        if (isNew) {
            stackTop2 = stackTop - indexReg;
            Object function = stack[stackTop2];
            if (function == UniqueTag.DOUBLE_MARK) {
                function = ScriptRuntime.wrapNumber(sDbl[stackTop2]);
            }
            Object[] outArgs = getArgsArray(stack, sDbl, stackTop2 + 1, indexReg);
            stack[stackTop2] = ScriptRuntime.newSpecial(cx, function, outArgs, frame.scope, callType);
        } else {
            stackTop2 = stackTop - (1 + indexReg);
            Scriptable functionThis = (Scriptable) stack[stackTop2 + 1];
            Callable function2 = (Callable) stack[stackTop2];
            Object[] outArgs2 = getArgsArray(stack, sDbl, stackTop2 + 2, indexReg);
            stack[stackTop2] = ScriptRuntime.callSpecial(cx, function2, functionThis, outArgs2, frame.scope, frame.thisObj, callType, frame.idata.itsSourceFile, sourceLine);
        }
        frame.pc += 4;
        return stackTop2;
    }

    private static int doSetConstVar(CallFrame frame, Object[] stack, double[] sDbl, int stackTop, Object[] vars, double[] varDbls, int[] varAttributes, int indexReg) {
        if (!frame.useActivation) {
            if ((varAttributes[indexReg] & 1) == 0) {
                throw Context.reportRuntimeError1("msg.var.redecl", frame.idata.argNames[indexReg]);
            }
            if ((varAttributes[indexReg] & 8) != 0) {
                vars[indexReg] = stack[stackTop];
                varAttributes[indexReg] = varAttributes[indexReg] & (-9);
                varDbls[indexReg] = sDbl[stackTop];
            }
        } else {
            Object val = stack[stackTop];
            if (val == UniqueTag.DOUBLE_MARK) {
                val = ScriptRuntime.wrapNumber(sDbl[stackTop]);
            }
            String stringReg = frame.idata.argNames[indexReg];
            if (frame.scope instanceof ConstProperties) {
                ConstProperties cp = (ConstProperties) frame.scope;
                cp.putConst(stringReg, frame.scope, val);
            } else {
                throw Kit.codeBug();
            }
        }
        return stackTop;
    }

    private static int doSetVar(CallFrame frame, Object[] stack, double[] sDbl, int stackTop, Object[] vars, double[] varDbls, int[] varAttributes, int indexReg) {
        if (!frame.useActivation) {
            if ((varAttributes[indexReg] & 1) == 0) {
                vars[indexReg] = stack[stackTop];
                varDbls[indexReg] = sDbl[stackTop];
            }
        } else {
            Object val = stack[stackTop];
            if (val == UniqueTag.DOUBLE_MARK) {
                val = ScriptRuntime.wrapNumber(sDbl[stackTop]);
            }
            String stringReg = frame.idata.argNames[indexReg];
            frame.scope.put(stringReg, frame.scope, val);
        }
        return stackTop;
    }

    private static int doGetVar(CallFrame frame, Object[] stack, double[] sDbl, int stackTop, Object[] vars, double[] varDbls, int indexReg) {
        int stackTop2 = stackTop + 1;
        if (!frame.useActivation) {
            stack[stackTop2] = vars[indexReg];
            sDbl[stackTop2] = varDbls[indexReg];
        } else {
            String stringReg = frame.idata.argNames[indexReg];
            stack[stackTop2] = frame.scope.get(stringReg, frame.scope);
        }
        return stackTop2;
    }

    private static int doVarIncDec(Context cx, CallFrame frame, Object[] stack, double[] sDbl, int stackTop, Object[] vars, double[] varDbls, int[] varAttributes, int indexReg) {
        double d;
        int stackTop2 = stackTop + 1;
        byte b = frame.idata.itsICode[frame.pc];
        if (!frame.useActivation) {
            Object varValue = vars[indexReg];
            if (varValue == UniqueTag.DOUBLE_MARK) {
                d = varDbls[indexReg];
            } else {
                d = ScriptRuntime.toNumber(varValue);
            }
            double d2 = (b & 1) == 0 ? d + 1.0d : d - 1.0d;
            boolean post = (b & 2) != 0;
            if ((varAttributes[indexReg] & 1) == 0) {
                if (varValue != UniqueTag.DOUBLE_MARK) {
                    vars[indexReg] = UniqueTag.DOUBLE_MARK;
                }
                varDbls[indexReg] = d2;
                stack[stackTop2] = UniqueTag.DOUBLE_MARK;
                sDbl[stackTop2] = post ? d : d2;
            } else if (post && varValue != UniqueTag.DOUBLE_MARK) {
                stack[stackTop2] = varValue;
            } else {
                stack[stackTop2] = UniqueTag.DOUBLE_MARK;
                sDbl[stackTop2] = post ? d : d2;
            }
        } else {
            String varName = frame.idata.argNames[indexReg];
            stack[stackTop2] = ScriptRuntime.nameIncrDecr(frame.scope, varName, cx, b);
        }
        frame.pc++;
        return stackTop2;
    }

    private static int doRefMember(Context cx, Object[] stack, double[] sDbl, int stackTop, int flags) {
        Object elem = stack[stackTop];
        if (elem == UniqueTag.DOUBLE_MARK) {
            elem = ScriptRuntime.wrapNumber(sDbl[stackTop]);
        }
        int stackTop2 = stackTop - 1;
        Object obj = stack[stackTop2];
        if (obj == UniqueTag.DOUBLE_MARK) {
            obj = ScriptRuntime.wrapNumber(sDbl[stackTop2]);
        }
        stack[stackTop2] = ScriptRuntime.memberRef(obj, elem, cx, flags);
        return stackTop2;
    }

    private static int doRefNsMember(Context cx, Object[] stack, double[] sDbl, int stackTop, int flags) {
        Object elem = stack[stackTop];
        if (elem == UniqueTag.DOUBLE_MARK) {
            elem = ScriptRuntime.wrapNumber(sDbl[stackTop]);
        }
        int stackTop2 = stackTop - 1;
        Object ns = stack[stackTop2];
        if (ns == UniqueTag.DOUBLE_MARK) {
            ns = ScriptRuntime.wrapNumber(sDbl[stackTop2]);
        }
        int stackTop3 = stackTop2 - 1;
        Object obj = stack[stackTop3];
        if (obj == UniqueTag.DOUBLE_MARK) {
            obj = ScriptRuntime.wrapNumber(sDbl[stackTop3]);
        }
        stack[stackTop3] = ScriptRuntime.memberRef(obj, ns, elem, cx, flags);
        return stackTop3;
    }

    private static int doRefNsName(Context cx, CallFrame frame, Object[] stack, double[] sDbl, int stackTop, int flags) {
        Object name = stack[stackTop];
        if (name == UniqueTag.DOUBLE_MARK) {
            name = ScriptRuntime.wrapNumber(sDbl[stackTop]);
        }
        int stackTop2 = stackTop - 1;
        Object ns = stack[stackTop2];
        if (ns == UniqueTag.DOUBLE_MARK) {
            ns = ScriptRuntime.wrapNumber(sDbl[stackTop2]);
        }
        stack[stackTop2] = ScriptRuntime.nameRef(ns, name, cx, frame.scope, flags);
        return stackTop2;
    }

    private static CallFrame initFrameForNoSuchMethod(Context cx, CallFrame frame, int indexReg, Object[] stack, double[] sDbl, int stackTop, int op, Scriptable funThisObj, Scriptable calleeScope, ScriptRuntime.NoSuchMethodShim noSuchMethodShim, InterpretedFunction ifun) {
        int shift = stackTop + 2;
        Object[] elements = new Object[indexReg];
        int i = 0;
        while (i < indexReg) {
            Object val = stack[shift];
            if (val == UniqueTag.DOUBLE_MARK) {
                val = ScriptRuntime.wrapNumber(sDbl[shift]);
            }
            elements[i] = val;
            i++;
            shift++;
        }
        Object[] argsArray = {noSuchMethodShim.methodName, cx.newArray(calleeScope, elements)};
        CallFrame callParentFrame = frame;
        if (op == -55) {
            callParentFrame = frame.parentFrame;
            exitFrame(cx, frame, null);
        }
        CallFrame calleeFrame = initFrame(cx, calleeScope, funThisObj, argsArray, null, 0, 2, ifun, callParentFrame);
        if (op != -55) {
            frame.savedStackTop = stackTop;
            frame.savedCallOp = op;
        }
        return calleeFrame;
    }

    private static boolean doEquals(Object[] stack, double[] sDbl, int stackTop) {
        Object rhs = stack[stackTop + 1];
        Object lhs = stack[stackTop];
        if (rhs == UniqueTag.DOUBLE_MARK) {
            if (lhs == UniqueTag.DOUBLE_MARK) {
                return sDbl[stackTop] == sDbl[stackTop + 1];
            }
            return ScriptRuntime.eqNumber(sDbl[stackTop + 1], lhs);
        }
        if (lhs == UniqueTag.DOUBLE_MARK) {
            return ScriptRuntime.eqNumber(sDbl[stackTop], rhs);
        }
        return ScriptRuntime.eq(lhs, rhs);
    }

    private static boolean doShallowEquals(Object[] stack, double[] sDbl, int stackTop) {
        double ldbl;
        double rdbl;
        Object rhs = stack[stackTop + 1];
        Object lhs = stack[stackTop];
        Object DBL_MRK = UniqueTag.DOUBLE_MARK;
        if (rhs == DBL_MRK) {
            rdbl = sDbl[stackTop + 1];
            if (lhs == DBL_MRK) {
                ldbl = sDbl[stackTop];
            } else if (lhs instanceof Number) {
                ldbl = ((Number) lhs).doubleValue();
            } else {
                return false;
            }
        } else if (lhs == DBL_MRK) {
            ldbl = sDbl[stackTop];
            if (rhs instanceof Number) {
                rdbl = ((Number) rhs).doubleValue();
            } else {
                return false;
            }
        } else {
            return ScriptRuntime.shallowEq(lhs, rhs);
        }
        return ldbl == rdbl;
    }

    private static CallFrame processThrowable(Context cx, Object throwable, CallFrame frame, int indexReg, boolean instructionCounting) {
        Object throwable2;
        if (indexReg >= 0) {
            if (frame.frozen) {
                frame = frame.cloneFrozen();
            }
            int[] table = frame.idata.itsExceptionTable;
            frame.pc = table[indexReg + 2];
            if (instructionCounting) {
                frame.pcPrevBranch = frame.pc;
            }
            frame.savedStackTop = frame.emptyStackTop;
            int scopeLocal = frame.localShift + table[indexReg + 5];
            int exLocal = frame.localShift + table[indexReg + 4];
            frame.scope = (Scriptable) frame.stack[scopeLocal];
            frame.stack[exLocal] = throwable;
            throwable2 = null;
        } else {
            ContinuationJump cjump = (ContinuationJump) throwable;
            throwable2 = null;
            if (cjump.branchFrame != frame) {
                Kit.codeBug();
            }
            if (cjump.capturedFrame == null) {
                Kit.codeBug();
            }
            int rewindCount = cjump.capturedFrame.frameIndex + 1;
            if (cjump.branchFrame != null) {
                rewindCount -= cjump.branchFrame.frameIndex;
            }
            int enterCount = 0;
            CallFrame[] enterFrames = null;
            CallFrame x = cjump.capturedFrame;
            for (int i = 0; i != rewindCount; i++) {
                if (!x.frozen) {
                    Kit.codeBug();
                }
                if (x.useActivation) {
                    if (enterFrames == null) {
                        enterFrames = new CallFrame[rewindCount - i];
                    }
                    enterFrames[enterCount] = x;
                    enterCount++;
                }
                x = x.parentFrame;
            }
            while (enterCount != 0) {
                enterCount--;
                CallFrame x2 = enterFrames[enterCount];
                enterFrame(cx, x2, ScriptRuntime.emptyArgs, true);
            }
            frame = cjump.capturedFrame.cloneFrozen();
            setCallResult(frame, cjump.result, cjump.resultDbl);
        }
        frame.throwable = throwable2;
        return frame;
    }

    private static Object freezeGenerator(Context cx, CallFrame frame, int stackTop, GeneratorState generatorState, boolean yieldStar) {
        if (generatorState.operation == 2) {
            throw ScriptRuntime.typeError0("msg.yield.closing");
        }
        frame.frozen = true;
        frame.result = frame.stack[stackTop];
        frame.resultDbl = frame.sDbl[stackTop];
        frame.savedStackTop = stackTop;
        frame.pc--;
        ScriptRuntime.exitActivationFunction(cx);
        Object result = frame.result != UniqueTag.DOUBLE_MARK ? frame.result : ScriptRuntime.wrapNumber(frame.resultDbl);
        if (yieldStar) {
            return new ES6Generator.YieldStarResult(result);
        }
        return result;
    }

    private static Object thawGenerator(CallFrame frame, int stackTop, GeneratorState generatorState, int op) {
        frame.frozen = false;
        int sourceLine = getIndex(frame.idata.itsICode, frame.pc);
        frame.pc += 2;
        if (generatorState.operation == 1) {
            return new JavaScriptException(generatorState.value, frame.idata.itsSourceFile, sourceLine);
        }
        if (generatorState.operation == 2) {
            return generatorState.value;
        }
        if (generatorState.operation != 0) {
            throw Kit.codeBug();
        }
        if (op == 73 || op == -66) {
            frame.stack[stackTop] = generatorState.value;
        }
        return Scriptable.NOT_FOUND;
    }

    private static CallFrame initFrameForApplyOrCall(Context cx, CallFrame frame, int indexReg, Object[] stack, double[] sDbl, int stackTop, int op, Scriptable calleeScope, IdFunctionObject ifun, InterpretedFunction iApplyCallable) {
        Scriptable applyThis;
        CallFrame calleeFrame;
        if (indexReg != 0) {
            Object obj = stack[stackTop + 2];
            if (obj == UniqueTag.DOUBLE_MARK) {
                obj = ScriptRuntime.wrapNumber(sDbl[stackTop + 2]);
            }
            applyThis = ScriptRuntime.toObjectOrNull(cx, obj, frame.scope);
        } else {
            applyThis = null;
        }
        if (applyThis == null) {
            applyThis = ScriptRuntime.getTopCallScope(cx);
        }
        if (op == -55) {
            exitFrame(cx, frame, null);
            frame = frame.parentFrame;
        } else {
            frame.savedStackTop = stackTop;
            frame.savedCallOp = op;
        }
        if (BaseFunction.isApply(ifun)) {
            Object[] callArgs = indexReg < 2 ? ScriptRuntime.emptyArgs : ScriptRuntime.getApplyArguments(cx, stack[stackTop + 3]);
            calleeFrame = initFrame(cx, calleeScope, applyThis, callArgs, null, 0, callArgs.length, iApplyCallable, frame);
        } else {
            for (int i = 1; i < indexReg; i++) {
                stack[stackTop + 1 + i] = stack[stackTop + 2 + i];
                sDbl[stackTop + 1 + i] = sDbl[stackTop + 2 + i];
            }
            int argCount = indexReg < 2 ? 0 : indexReg - 1;
            calleeFrame = initFrame(cx, calleeScope, applyThis, stack, sDbl, stackTop + 2, argCount, iApplyCallable, frame);
        }
        return calleeFrame;
    }

    private static CallFrame initFrame(Context cx, Scriptable callerScope, Scriptable thisObj, Object[] args, double[] argsDbl, int argShift, int argCount, InterpretedFunction fnOrScript, CallFrame parentFrame) {
        CallFrame frame = new CallFrame(cx, thisObj, fnOrScript, parentFrame);
        frame.initializeArgs(cx, callerScope, args, argsDbl, argShift, argCount);
        enterFrame(cx, frame, args, false);
        return frame;
    }

    private static void enterFrame(Context cx, CallFrame frame, Object[] args, boolean continuationRestart) {
        boolean usesActivation = frame.idata.itsNeedsActivation;
        boolean isDebugged = frame.debuggerFrame != null;
        if (usesActivation || isDebugged) {
            Scriptable scope = frame.scope;
            if (scope == null) {
                Kit.codeBug();
            } else if (continuationRestart) {
                while (scope instanceof NativeWith) {
                    scope = scope.getParentScope();
                    if (scope == null || (frame.parentFrame != null && frame.parentFrame.scope == scope)) {
                        Kit.codeBug();
                        break;
                    }
                }
            }
            if (isDebugged) {
                frame.debuggerFrame.onEnter(cx, scope, frame.thisObj, args);
            }
            if (usesActivation) {
                ScriptRuntime.enterActivationFunction(cx, scope);
            }
        }
    }

    private static void exitFrame(Context cx, CallFrame frame, Object throwable) {
        Object result;
        double resultDbl;
        if (frame.idata.itsNeedsActivation) {
            ScriptRuntime.exitActivationFunction(cx);
        }
        if (frame.debuggerFrame != null) {
            try {
                if (throwable instanceof Throwable) {
                    frame.debuggerFrame.onExit(cx, true, throwable);
                } else {
                    ContinuationJump cjump = (ContinuationJump) throwable;
                    if (cjump == null) {
                        result = frame.result;
                    } else {
                        result = cjump.result;
                    }
                    if (result == UniqueTag.DOUBLE_MARK) {
                        if (cjump == null) {
                            resultDbl = frame.resultDbl;
                        } else {
                            resultDbl = cjump.resultDbl;
                        }
                        result = ScriptRuntime.wrapNumber(resultDbl);
                    }
                    frame.debuggerFrame.onExit(cx, false, result);
                }
            } catch (Throwable ex) {
                System.err.println("RHINO USAGE WARNING: onExit terminated with exception");
                ex.printStackTrace(System.err);
            }
        }
    }

    private static void setCallResult(CallFrame frame, Object callResult, double callResultDbl) {
        if (frame.savedCallOp == 38) {
            frame.stack[frame.savedStackTop] = callResult;
            frame.sDbl[frame.savedStackTop] = callResultDbl;
        } else if (frame.savedCallOp == 30) {
            if (callResult instanceof Scriptable) {
                frame.stack[frame.savedStackTop] = callResult;
            }
        } else {
            Kit.codeBug();
        }
        frame.savedCallOp = 0;
    }

    public static NativeContinuation captureContinuation(Context cx) {
        if (cx.lastInterpreterFrame == null || !(cx.lastInterpreterFrame instanceof CallFrame)) {
            throw new IllegalStateException("Interpreter frames not found");
        }
        return captureContinuation(cx, (CallFrame) cx.lastInterpreterFrame, true);
    }

    private static NativeContinuation captureContinuation(Context cx, CallFrame frame, boolean requireContinuationsTopFrame) {
        NativeContinuation c = new NativeContinuation();
        ScriptRuntime.setObjectProtoAndParent(c, ScriptRuntime.getTopCallScope(cx));
        CallFrame outermost = frame;
        for (CallFrame x = frame; x != null && !x.frozen; x = x.parentFrame) {
            x.frozen = true;
            for (int i = x.savedStackTop + 1; i != x.stack.length; i++) {
                x.stack[i] = null;
                x.stackAttributes[i] = 0;
            }
            if (x.savedCallOp == 38) {
                x.stack[x.savedStackTop] = null;
            } else if (x.savedCallOp != 30) {
                Kit.codeBug();
            }
            outermost = x;
        }
        if (requireContinuationsTopFrame) {
            while (outermost.parentFrame != null) {
                outermost = outermost.parentFrame;
            }
            if (!outermost.isContinuationsTopFrame) {
                throw new IllegalStateException("Cannot capture continuation from JavaScript code not called directly by executeScriptWithContinuations or callFunctionWithContinuations");
            }
        }
        c.initImplementation(frame);
        return c;
    }

    private static int stack_int32(CallFrame frame, int i) {
        Object x = frame.stack[i];
        if (x == UniqueTag.DOUBLE_MARK) {
            return ScriptRuntime.toInt32(frame.sDbl[i]);
        }
        return ScriptRuntime.toInt32(x);
    }

    private static double stack_double(CallFrame frame, int i) {
        Object x = frame.stack[i];
        if (x != UniqueTag.DOUBLE_MARK) {
            return ScriptRuntime.toNumber(x);
        }
        return frame.sDbl[i];
    }

    private static boolean stack_boolean(CallFrame frame, int i) {
        Object x = frame.stack[i];
        if (Boolean.TRUE.equals(x)) {
            return true;
        }
        if (Boolean.FALSE.equals(x)) {
            return false;
        }
        if (x == UniqueTag.DOUBLE_MARK) {
            double d = frame.sDbl[i];
            return (Double.isNaN(d) || d == 0.0d) ? false : true;
        }
        if (x == null || x == Undefined.instance) {
            return false;
        }
        if (x instanceof Number) {
            double d2 = ((Number) x).doubleValue();
            return (Double.isNaN(d2) || d2 == 0.0d) ? false : true;
        }
        return ScriptRuntime.toBoolean(x);
    }

    private static void doAdd(Object[] stack, double[] sDbl, int stackTop, Context cx) {
        double d;
        boolean leftRightOrder;
        Object rhs = stack[stackTop + 1];
        Object lhs = stack[stackTop];
        if (rhs == UniqueTag.DOUBLE_MARK) {
            d = sDbl[stackTop + 1];
            if (lhs == UniqueTag.DOUBLE_MARK) {
                sDbl[stackTop] = sDbl[stackTop] + d;
                return;
            }
            leftRightOrder = true;
        } else if (lhs == UniqueTag.DOUBLE_MARK) {
            d = sDbl[stackTop];
            lhs = rhs;
            leftRightOrder = false;
        } else {
            if ((lhs instanceof Scriptable) || (rhs instanceof Scriptable)) {
                stack[stackTop] = ScriptRuntime.add(lhs, rhs, cx);
                return;
            }
            if (lhs instanceof CharSequence) {
                if (rhs instanceof CharSequence) {
                    stack[stackTop] = new ConsString((CharSequence) lhs, (CharSequence) rhs);
                    return;
                } else {
                    stack[stackTop] = new ConsString((CharSequence) lhs, ScriptRuntime.toCharSequence(rhs));
                    return;
                }
            }
            if (rhs instanceof CharSequence) {
                stack[stackTop] = new ConsString(ScriptRuntime.toCharSequence(lhs), (CharSequence) rhs);
                return;
            }
            double lDbl = lhs instanceof Number ? ((Number) lhs).doubleValue() : ScriptRuntime.toNumber(lhs);
            double rDbl = rhs instanceof Number ? ((Number) rhs).doubleValue() : ScriptRuntime.toNumber(rhs);
            stack[stackTop] = UniqueTag.DOUBLE_MARK;
            sDbl[stackTop] = lDbl + rDbl;
            return;
        }
        if (lhs instanceof Scriptable) {
            Object rhs2 = ScriptRuntime.wrapNumber(d);
            if (!leftRightOrder) {
                Object tmp = lhs;
                lhs = rhs2;
                rhs2 = tmp;
            }
            stack[stackTop] = ScriptRuntime.add(lhs, rhs2, cx);
            return;
        }
        if (lhs instanceof CharSequence) {
            CharSequence rstr = ScriptRuntime.numberToString(d, 10);
            if (leftRightOrder) {
                stack[stackTop] = new ConsString((CharSequence) lhs, rstr);
                return;
            } else {
                stack[stackTop] = new ConsString(rstr, (CharSequence) lhs);
                return;
            }
        }
        double lDbl2 = lhs instanceof Number ? ((Number) lhs).doubleValue() : ScriptRuntime.toNumber(lhs);
        stack[stackTop] = UniqueTag.DOUBLE_MARK;
        sDbl[stackTop] = lDbl2 + d;
    }

    private static int doArithmetic(CallFrame frame, int op, Object[] stack, double[] sDbl, int stackTop) {
        double rDbl = stack_double(frame, stackTop);
        int stackTop2 = stackTop - 1;
        double lDbl = stack_double(frame, stackTop2);
        stack[stackTop2] = UniqueTag.DOUBLE_MARK;
        switch (op) {
            case 22:
                lDbl -= rDbl;
                break;
            case 23:
                lDbl *= rDbl;
                break;
            case 24:
                lDbl /= rDbl;
                break;
            case 25:
                lDbl %= rDbl;
                break;
        }
        sDbl[stackTop2] = lDbl;
        return stackTop2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Object[] getArgsArray(Object[] stack, double[] sDbl, int shift, int count) {
        if (count == 0) {
            return ScriptRuntime.emptyArgs;
        }
        Object[] args = new Object[count];
        int i = 0;
        while (i != count) {
            Object val = stack[shift];
            if (val == UniqueTag.DOUBLE_MARK) {
                val = ScriptRuntime.wrapNumber(sDbl[shift]);
            }
            args[i] = val;
            i++;
            shift++;
        }
        return args;
    }

    private static void addInstructionCount(Context cx, CallFrame frame, int extra) {
        cx.instructionCount += (frame.pc - frame.pcPrevBranch) + extra;
        if (cx.instructionCount > cx.instructionThreshold) {
            cx.observeInstructionCount(cx.instructionCount);
            cx.instructionCount = 0;
        }
    }
}
