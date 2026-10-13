package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ArrowFunction.class */
public class ArrowFunction extends BaseFunction {
    private static final long serialVersionUID = -7377989503697220633L;
    private final Callable targetFunction;
    private final Scriptable boundThis;

    public ArrowFunction(Context cx, Scriptable scope, Callable targetFunction, Scriptable boundThis) {
        this.targetFunction = targetFunction;
        this.boundThis = boundThis;
        ScriptRuntime.setFunctionProtoAndParent(this, scope);
        Object thrower = ScriptRuntime.typeErrorThrower(cx);
        NativeObject nativeObject = new NativeObject();
        nativeObject.put("get", nativeObject, thrower);
        nativeObject.put("set", nativeObject, thrower);
        nativeObject.put("enumerable", nativeObject, Boolean.FALSE);
        nativeObject.put("configurable", nativeObject, Boolean.FALSE);
        nativeObject.preventExtensions();
        defineOwnProperty(cx, "caller", nativeObject, false);
        defineOwnProperty(cx, "arguments", nativeObject, false);
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.Function, org.mozilla.javascript.Callable
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        Scriptable callThis = this.boundThis != null ? this.boundThis : ScriptRuntime.getTopCallScope(cx);
        return this.targetFunction.call(cx, scope, callThis, args);
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.Function
    public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
        throw ScriptRuntime.typeError1("msg.not.ctor", decompile(0, 0));
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public boolean hasInstance(Scriptable instance) {
        if (this.targetFunction instanceof Function) {
            return ((Function) this.targetFunction).hasInstance(instance);
        }
        throw ScriptRuntime.typeError0("msg.not.ctor");
    }

    @Override // org.mozilla.javascript.BaseFunction
    public int getLength() {
        if (this.targetFunction instanceof BaseFunction) {
            return ((BaseFunction) this.targetFunction).getLength();
        }
        return 0;
    }

    @Override // org.mozilla.javascript.BaseFunction
    public int getArity() {
        return getLength();
    }

    @Override // org.mozilla.javascript.BaseFunction
    String decompile(int indent, int flags) {
        if (this.targetFunction instanceof BaseFunction) {
            return ((BaseFunction) this.targetFunction).decompile(indent, flags);
        }
        return super.decompile(indent, flags);
    }

    static boolean equalObjectGraphs(ArrowFunction f1, ArrowFunction f2, EqualObjectGraphs eq) {
        return eq.equalGraphs(f1.boundThis, f2.boundThis) && eq.equalGraphs(f1.targetFunction, f2.targetFunction);
    }
}
