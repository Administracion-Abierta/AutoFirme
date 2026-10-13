package org.mozilla.javascript;

import org.mozilla.javascript.debug.DebuggableScript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/InterpretedFunction.class */
final class InterpretedFunction extends NativeFunction implements Script {
    private static final long serialVersionUID = 541475680333911468L;
    InterpreterData idata;
    SecurityController securityController;
    Object securityDomain;

    private InterpretedFunction(InterpreterData idata, Object staticSecurityDomain) {
        Object dynamicDomain;
        this.idata = idata;
        Context cx = Context.getContext();
        SecurityController sc = cx.getSecurityController();
        if (sc != null) {
            dynamicDomain = sc.getDynamicSecurityDomain(staticSecurityDomain);
        } else {
            if (staticSecurityDomain != null) {
                throw new IllegalArgumentException();
            }
            dynamicDomain = null;
        }
        this.securityController = sc;
        this.securityDomain = dynamicDomain;
    }

    private InterpretedFunction(InterpretedFunction parent, int index) {
        this.idata = parent.idata.itsNestedFunctions[index];
        this.securityController = parent.securityController;
        this.securityDomain = parent.securityDomain;
    }

    static InterpretedFunction createScript(InterpreterData idata, Object staticSecurityDomain) {
        InterpretedFunction f = new InterpretedFunction(idata, staticSecurityDomain);
        return f;
    }

    static InterpretedFunction createFunction(Context cx, Scriptable scope, InterpreterData idata, Object staticSecurityDomain) {
        InterpretedFunction f = new InterpretedFunction(idata, staticSecurityDomain);
        f.initScriptFunction(cx, scope, f.idata.isES6Generator);
        return f;
    }

    static InterpretedFunction createFunction(Context cx, Scriptable scope, InterpretedFunction parent, int index) {
        InterpretedFunction f = new InterpretedFunction(parent, index);
        f.initScriptFunction(cx, scope, f.idata.isES6Generator);
        return f;
    }

    @Override // org.mozilla.javascript.BaseFunction
    public String getFunctionName() {
        return this.idata.itsName == null ? "" : this.idata.itsName;
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.Function, org.mozilla.javascript.Callable
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!ScriptRuntime.hasTopCall(cx)) {
            return ScriptRuntime.doTopCall(this, cx, scope, thisObj, args, this.idata.isStrict);
        }
        return Interpreter.interpret(this, cx, scope, thisObj, args);
    }

    @Override // org.mozilla.javascript.Script
    public Object exec(Context cx, Scriptable scope) {
        if (!isScript()) {
            throw new IllegalStateException();
        }
        if (!ScriptRuntime.hasTopCall(cx)) {
            return ScriptRuntime.doTopCall(this, cx, scope, scope, ScriptRuntime.emptyArgs, this.idata.isStrict);
        }
        return Interpreter.interpret(this, cx, scope, scope, ScriptRuntime.emptyArgs);
    }

    public boolean isScript() {
        return this.idata.itsFunctionType == 0;
    }

    @Override // org.mozilla.javascript.NativeFunction
    public String getEncodedSource() {
        return Interpreter.getEncodedSource(this.idata);
    }

    @Override // org.mozilla.javascript.NativeFunction
    public DebuggableScript getDebuggableView() {
        return this.idata;
    }

    @Override // org.mozilla.javascript.NativeFunction
    public Object resumeGenerator(Context cx, Scriptable scope, int operation, Object state, Object value) {
        return Interpreter.resumeGenerator(cx, scope, operation, state, value);
    }

    @Override // org.mozilla.javascript.NativeFunction
    protected int getLanguageVersion() {
        return this.idata.languageVersion;
    }

    @Override // org.mozilla.javascript.NativeFunction
    protected int getParamCount() {
        return this.idata.argCount;
    }

    @Override // org.mozilla.javascript.NativeFunction
    protected int getParamAndVarCount() {
        return this.idata.argNames.length;
    }

    @Override // org.mozilla.javascript.NativeFunction
    protected String getParamOrVarName(int index) {
        return this.idata.argNames[index];
    }

    @Override // org.mozilla.javascript.NativeFunction
    protected boolean getParamOrVarConst(int index) {
        return this.idata.argIsConst[index];
    }

    boolean hasFunctionNamed(String name) {
        for (int f = 0; f < this.idata.getFunctionCount(); f++) {
            InterpreterData functionData = (InterpreterData) this.idata.getFunction(f);
            if (!functionData.declaredAsFunctionExpression && name.equals(functionData.getFunctionName())) {
                return false;
            }
        }
        return true;
    }
}
