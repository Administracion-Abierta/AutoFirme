package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeCall.class */
public final class NativeCall extends IdScriptableObject {
    private static final long serialVersionUID = -7471457301304454454L;
    private static final Object CALL_TAG = "Call";
    private static final int Id_constructor = 1;
    private static final int MAX_PROTOTYPE_ID = 1;
    NativeFunction function;
    Object[] originalArgs;
    boolean isStrict;
    private Arguments arguments;
    transient NativeCall parentActivationCall;

    static void init(Scriptable scope, boolean sealed) {
        NativeCall obj = new NativeCall();
        obj.exportAsJSClass(1, scope, sealed);
    }

    NativeCall() {
    }

    NativeCall(NativeFunction function, Scriptable scope, Object[] args, boolean isArrow, boolean isStrict) {
        this.function = function;
        setParentScope(scope);
        this.originalArgs = args == null ? ScriptRuntime.emptyArgs : args;
        this.isStrict = isStrict;
        int paramAndVarCount = function.getParamAndVarCount();
        int paramCount = function.getParamCount();
        if (paramAndVarCount != 0) {
            int i = 0;
            while (i < paramCount) {
                String name = function.getParamOrVarName(i);
                Object val = i < args.length ? args[i] : Undefined.instance;
                defineProperty(name, val, 4);
                i++;
            }
        }
        if (!super.has("arguments", this) && !isArrow) {
            this.arguments = new Arguments(this);
            defineProperty("arguments", this.arguments, 4);
        }
        if (paramAndVarCount != 0) {
            for (int i2 = paramCount; i2 < paramAndVarCount; i2++) {
                String name2 = function.getParamOrVarName(i2);
                if (!super.has(name2, this)) {
                    if (function.getParamOrVarConst(i2)) {
                        defineProperty(name2, Undefined.instance, 13);
                    } else if (!(function instanceof InterpretedFunction) || ((InterpretedFunction) function).hasFunctionNamed(name2)) {
                        defineProperty(name2, Undefined.instance, 4);
                    }
                }
            }
        }
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return "Call";
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(String s) {
        return s.equals("constructor") ? 1 : 0;
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void initPrototypeId(int id) {
        if (id != 1) {
            throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(CALL_TAG, id, "constructor", 1);
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.IdFunctionCall
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(CALL_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        if (id == 1) {
            if (thisObj != null) {
                throw Context.reportRuntimeError1("msg.only.from.new", "Call");
            }
            ScriptRuntime.checkDeprecated(cx, "Call");
            NativeCall result = new NativeCall();
            result.setPrototype(getObjectPrototype(scope));
            return result;
        }
        throw new IllegalArgumentException(String.valueOf(id));
    }

    public void defineAttributesForArguments() {
        if (this.arguments != null) {
            this.arguments.defineAttributesForStrictMode();
        }
    }
}
