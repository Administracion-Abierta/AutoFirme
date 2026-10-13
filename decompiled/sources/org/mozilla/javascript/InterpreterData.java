package org.mozilla.javascript;

import java.io.Serializable;
import java.util.Arrays;
import org.mozilla.javascript.debug.DebuggableScript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/InterpreterData.class */
final class InterpreterData implements Serializable, DebuggableScript {
    private static final long serialVersionUID = 5067677351589230234L;
    static final int INITIAL_MAX_ICODE_LENGTH = 1024;
    static final int INITIAL_STRINGTABLE_SIZE = 64;
    static final int INITIAL_NUMBERTABLE_SIZE = 64;
    String itsName;
    String itsSourceFile;
    boolean itsNeedsActivation;
    int itsFunctionType;
    String[] itsStringTable;
    double[] itsDoubleTable;
    InterpreterData[] itsNestedFunctions;
    Object[] itsRegExpLiterals;
    byte[] itsICode;
    int[] itsExceptionTable;
    int itsMaxVars;
    int itsMaxLocals;
    int itsMaxStack;
    int itsMaxFrameArray;
    String[] argNames;
    boolean[] argIsConst;
    int argCount;
    int itsMaxCalleeArgs;
    String encodedSource;
    int encodedSourceStart;
    int encodedSourceEnd;
    int languageVersion;
    boolean isStrict;
    boolean topLevel;
    boolean isES6Generator;
    Object[] literalIds;
    UintMap longJumps;
    InterpreterData parentData;
    boolean evalScriptFlag;
    boolean declaredAsVar;
    boolean declaredAsFunctionExpression;
    int firstLinePC = -1;
    private int icodeHashCode = 0;

    InterpreterData(int languageVersion, String sourceFile, String encodedSource, boolean isStrict) {
        this.languageVersion = languageVersion;
        this.itsSourceFile = sourceFile;
        this.encodedSource = encodedSource;
        this.isStrict = isStrict;
        init();
    }

    InterpreterData(InterpreterData parent) {
        this.parentData = parent;
        this.languageVersion = parent.languageVersion;
        this.itsSourceFile = parent.itsSourceFile;
        this.encodedSource = parent.encodedSource;
        this.isStrict = parent.isStrict;
        init();
    }

    private void init() {
        this.itsICode = new byte[1024];
        this.itsStringTable = new String[64];
    }

    @Override // org.mozilla.javascript.debug.DebuggableScript
    public boolean isTopLevel() {
        return this.topLevel;
    }

    @Override // org.mozilla.javascript.debug.DebuggableScript
    public boolean isFunction() {
        return this.itsFunctionType != 0;
    }

    @Override // org.mozilla.javascript.debug.DebuggableScript
    public String getFunctionName() {
        return this.itsName;
    }

    @Override // org.mozilla.javascript.debug.DebuggableScript
    public int getParamCount() {
        return this.argCount;
    }

    @Override // org.mozilla.javascript.debug.DebuggableScript
    public int getParamAndVarCount() {
        return this.argNames.length;
    }

    @Override // org.mozilla.javascript.debug.DebuggableScript
    public String getParamOrVarName(int index) {
        return this.argNames[index];
    }

    public boolean getParamOrVarConst(int index) {
        return this.argIsConst[index];
    }

    @Override // org.mozilla.javascript.debug.DebuggableScript
    public String getSourceName() {
        return this.itsSourceFile;
    }

    @Override // org.mozilla.javascript.debug.DebuggableScript
    public boolean isGeneratedScript() {
        return ScriptRuntime.isGeneratedScript(this.itsSourceFile);
    }

    @Override // org.mozilla.javascript.debug.DebuggableScript
    public int[] getLineNumbers() {
        return Interpreter.getLineNumbers(this);
    }

    @Override // org.mozilla.javascript.debug.DebuggableScript
    public int getFunctionCount() {
        if (this.itsNestedFunctions == null) {
            return 0;
        }
        return this.itsNestedFunctions.length;
    }

    @Override // org.mozilla.javascript.debug.DebuggableScript
    public DebuggableScript getFunction(int index) {
        return this.itsNestedFunctions[index];
    }

    @Override // org.mozilla.javascript.debug.DebuggableScript
    public DebuggableScript getParent() {
        return this.parentData;
    }

    public int icodeHashCode() {
        int h = this.icodeHashCode;
        if (h == 0) {
            int iHashCode = Arrays.hashCode(this.itsICode);
            h = iHashCode;
            this.icodeHashCode = iHashCode;
        }
        return h;
    }
}
