package org.mozilla.javascript;

import java.util.EnumMap;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/TopLevel.class */
public class TopLevel extends IdScriptableObject {
    private static final long serialVersionUID = -4648046356662472260L;
    private EnumMap<Builtins, BaseFunction> ctors;
    private EnumMap<NativeErrors, BaseFunction> errors;
    static final /* synthetic */ boolean $assertionsDisabled;

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/TopLevel$Builtins.class */
    public enum Builtins {
        Object,
        Array,
        Function,
        String,
        Number,
        Boolean,
        RegExp,
        Error,
        Symbol,
        GeneratorFunction
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/TopLevel$NativeErrors.class */
    enum NativeErrors {
        Error,
        EvalError,
        RangeError,
        ReferenceError,
        SyntaxError,
        TypeError,
        URIError,
        InternalError,
        JavaException
    }

    static {
        $assertionsDisabled = !TopLevel.class.desiredAssertionStatus();
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return "global";
    }

    public void cacheBuiltins(Scriptable scope, boolean sealed) {
        this.ctors = new EnumMap<>(Builtins.class);
        for (Builtins builtin : Builtins.values()) {
            Object value = ScriptableObject.getProperty(this, builtin.name());
            if (value instanceof BaseFunction) {
                this.ctors.put(builtin, (BaseFunction) value);
            } else if (builtin == Builtins.GeneratorFunction) {
                this.ctors.put(builtin, (BaseFunction) BaseFunction.initAsGeneratorFunction(scope, sealed));
            }
        }
        this.errors = new EnumMap<>(NativeErrors.class);
        for (NativeErrors error : NativeErrors.values()) {
            Object value2 = ScriptableObject.getProperty(this, error.name());
            if (value2 instanceof BaseFunction) {
                this.errors.put(error, (BaseFunction) value2);
            }
        }
    }

    public static Function getBuiltinCtor(Context cx, Scriptable scope, Builtins type) {
        String typeName;
        Function result;
        if (!$assertionsDisabled && scope.getParentScope() != null) {
            throw new AssertionError();
        }
        if ((scope instanceof TopLevel) && (result = ((TopLevel) scope).getBuiltinCtor(type)) != null) {
            return result;
        }
        if (type == Builtins.GeneratorFunction) {
            typeName = "__GeneratorFunction";
        } else {
            typeName = type.name();
        }
        return ScriptRuntime.getExistingCtor(cx, scope, typeName);
    }

    static Function getNativeErrorCtor(Context cx, Scriptable scope, NativeErrors type) {
        Function result;
        if (!$assertionsDisabled && scope.getParentScope() != null) {
            throw new AssertionError();
        }
        if ((scope instanceof TopLevel) && (result = ((TopLevel) scope).getNativeErrorCtor(type)) != null) {
            return result;
        }
        return ScriptRuntime.getExistingCtor(cx, scope, type.name());
    }

    public static Scriptable getBuiltinPrototype(Scriptable scope, Builtins type) {
        String typeName;
        Scriptable result;
        if (!$assertionsDisabled && scope.getParentScope() != null) {
            throw new AssertionError();
        }
        if ((scope instanceof TopLevel) && (result = ((TopLevel) scope).getBuiltinPrototype(type)) != null) {
            return result;
        }
        if (type == Builtins.GeneratorFunction) {
            typeName = "__GeneratorFunction";
        } else {
            typeName = type.name();
        }
        return ScriptableObject.getClassPrototype(scope, typeName);
    }

    public BaseFunction getBuiltinCtor(Builtins type) {
        if (this.ctors != null) {
            return this.ctors.get(type);
        }
        return null;
    }

    BaseFunction getNativeErrorCtor(NativeErrors type) {
        if (this.errors != null) {
            return this.errors.get(type);
        }
        return null;
    }

    public Scriptable getBuiltinPrototype(Builtins type) {
        BaseFunction func = getBuiltinCtor(type);
        Object proto = func != null ? func.getPrototypeProperty() : null;
        if (proto instanceof Scriptable) {
            return (Scriptable) proto;
        }
        return null;
    }
}
