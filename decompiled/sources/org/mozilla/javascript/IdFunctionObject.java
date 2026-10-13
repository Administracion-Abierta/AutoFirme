package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/IdFunctionObject.class */
public class IdFunctionObject extends BaseFunction {
    private static final long serialVersionUID = -5332312783643935019L;
    private final IdFunctionCall idcall;
    private final Object tag;
    private final int methodId;
    private int arity;
    private boolean useCallAsConstructor;
    private String functionName;

    public IdFunctionObject(IdFunctionCall idcall, Object tag, int id, int arity) {
        if (arity < 0) {
            throw new IllegalArgumentException();
        }
        this.idcall = idcall;
        this.tag = tag;
        this.methodId = id;
        this.arity = arity;
    }

    public IdFunctionObject(IdFunctionCall idcall, Object tag, int id, String name, int arity, Scriptable scope) {
        super(scope, null);
        if (arity < 0) {
            throw new IllegalArgumentException();
        }
        if (name == null) {
            throw new IllegalArgumentException();
        }
        this.idcall = idcall;
        this.tag = tag;
        this.methodId = id;
        this.arity = arity;
        this.functionName = name;
    }

    public void initFunction(String name, Scriptable scope) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (scope == null) {
            throw new IllegalArgumentException();
        }
        this.functionName = name;
        setParentScope(scope);
    }

    public final boolean hasTag(Object tag) {
        if (tag == null) {
            return this.tag == null;
        }
        return tag.equals(this.tag);
    }

    public Object getTag() {
        return this.tag;
    }

    public final int methodId() {
        return this.methodId;
    }

    public final void markAsConstructor(Scriptable prototypeProperty) {
        this.useCallAsConstructor = true;
        setImmunePrototypeProperty(prototypeProperty);
    }

    public final void addAsProperty(Scriptable target) {
        ScriptableObject.defineProperty(target, this.functionName, this, 2);
    }

    public void exportAsScopeProperty() {
        addAsProperty(getParentScope());
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public Scriptable getPrototype() {
        Scriptable proto = super.getPrototype();
        if (proto == null) {
            proto = getFunctionPrototype(getParentScope());
            setPrototype(proto);
        }
        return proto;
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.Function, org.mozilla.javascript.Callable
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        return this.idcall.execIdCall(this, cx, scope, thisObj, args);
    }

    @Override // org.mozilla.javascript.BaseFunction
    public Scriptable createObject(Context cx, Scriptable scope) {
        if (this.useCallAsConstructor) {
            return null;
        }
        throw ScriptRuntime.typeError1("msg.not.ctor", this.functionName);
    }

    @Override // org.mozilla.javascript.BaseFunction
    String decompile(int indent, int flags) {
        StringBuilder sb = new StringBuilder();
        boolean justbody = 0 != (flags & 1);
        if (!justbody) {
            sb.append("function ");
            sb.append(getFunctionName());
            sb.append("() { ");
        }
        sb.append("[native code for ");
        if (this.idcall instanceof Scriptable) {
            Scriptable sobj = (Scriptable) this.idcall;
            sb.append(sobj.getClassName());
            sb.append('.');
        }
        sb.append(getFunctionName());
        sb.append(", arity=");
        sb.append(getArity());
        sb.append(justbody ? "]\n" : "] }\n");
        return sb.toString();
    }

    @Override // org.mozilla.javascript.BaseFunction
    public int getArity() {
        return this.arity;
    }

    @Override // org.mozilla.javascript.BaseFunction
    public int getLength() {
        return getArity();
    }

    @Override // org.mozilla.javascript.BaseFunction
    public String getFunctionName() {
        return this.functionName == null ? "" : this.functionName;
    }

    public final RuntimeException unknown() {
        return new IllegalArgumentException("BAD FUNCTION ID=" + this.methodId + " MASTER=" + this.idcall);
    }

    static boolean equalObjectGraphs(IdFunctionObject f1, IdFunctionObject f2, EqualObjectGraphs eq) {
        return f1.methodId == f2.methodId && f1.hasTag(f2.tag) && eq.equalGraphs(f1.idcall, f2.idcall);
    }
}
