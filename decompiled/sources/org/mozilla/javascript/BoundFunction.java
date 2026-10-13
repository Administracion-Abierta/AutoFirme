package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/BoundFunction.class */
public class BoundFunction extends BaseFunction {
    private static final long serialVersionUID = 2118137342826470729L;
    private final Callable targetFunction;
    private final Scriptable boundThis;
    private final Object[] boundArgs;
    private final int length;

    public BoundFunction(Context cx, Scriptable scope, Callable targetFunction, Scriptable boundThis, Object[] boundArgs) {
        this.targetFunction = targetFunction;
        this.boundThis = boundThis;
        this.boundArgs = boundArgs;
        if (targetFunction instanceof BaseFunction) {
            this.length = Math.max(0, ((BaseFunction) targetFunction).getLength() - boundArgs.length);
        } else {
            this.length = 0;
        }
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
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] extraArgs) {
        Scriptable callThis = this.boundThis != null ? this.boundThis : ScriptRuntime.getTopCallScope(cx);
        return this.targetFunction.call(cx, scope, callThis, concat(this.boundArgs, extraArgs));
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.Function
    public Scriptable construct(Context cx, Scriptable scope, Object[] extraArgs) {
        if (this.targetFunction instanceof Function) {
            return ((Function) this.targetFunction).construct(cx, scope, concat(this.boundArgs, extraArgs));
        }
        throw ScriptRuntime.typeError0("msg.not.ctor");
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
        return this.length;
    }

    private static Object[] concat(Object[] first, Object[] second) {
        Object[] args = new Object[first.length + second.length];
        System.arraycopy(first, 0, args, 0, first.length);
        System.arraycopy(second, 0, args, first.length, second.length);
        return args;
    }

    static boolean equalObjectGraphs(BoundFunction f1, BoundFunction f2, EqualObjectGraphs eq) {
        return eq.equalGraphs(f1.boundThis, f2.boundThis) && eq.equalGraphs(f1.targetFunction, f2.targetFunction) && eq.equalGraphs(f1.boundArgs, f2.boundArgs);
    }
}
