package org.mozilla.javascript;

import org.mozilla.javascript.debug.DebuggableScript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeFunction.class */
public abstract class NativeFunction extends BaseFunction {
    private static final long serialVersionUID = 8713897114082216401L;

    protected abstract int getLanguageVersion();

    protected abstract int getParamCount();

    protected abstract int getParamAndVarCount();

    protected abstract String getParamOrVarName(int i);

    public final void initScriptFunction(Context cx, Scriptable scope) {
        initScriptFunction(cx, scope, isGeneratorFunction());
    }

    public final void initScriptFunction(Context cx, Scriptable scope, boolean es6GeneratorFunction) {
        ScriptRuntime.setFunctionProtoAndParent(this, scope, es6GeneratorFunction);
    }

    @Override // org.mozilla.javascript.BaseFunction
    final String decompile(int indent, int flags) {
        String encodedSource = getEncodedSource();
        if (encodedSource == null) {
            return super.decompile(indent, flags);
        }
        UintMap properties = new UintMap(1);
        properties.put(1, indent);
        return Decompiler.decompile(encodedSource, flags, properties);
    }

    @Override // org.mozilla.javascript.BaseFunction
    public int getLength() {
        int paramCount = getParamCount();
        if (getLanguageVersion() != 120) {
            return paramCount;
        }
        Context cx = Context.getContext();
        NativeCall activation = ScriptRuntime.findFunctionActivation(cx, this);
        if (activation == null) {
            return paramCount;
        }
        return activation.originalArgs.length;
    }

    @Override // org.mozilla.javascript.BaseFunction
    public int getArity() {
        return getParamCount();
    }

    @Deprecated
    public String jsGet_name() {
        return getFunctionName();
    }

    public String getEncodedSource() {
        return null;
    }

    public DebuggableScript getDebuggableView() {
        return null;
    }

    public Object resumeGenerator(Context cx, Scriptable scope, int operation, Object state, Object value) {
        throw new EvaluatorException("resumeGenerator() not implemented");
    }

    protected boolean getParamOrVarConst(int index) {
        return false;
    }
}
