package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/Delegator.class */
public class Delegator implements Function, SymbolScriptable {
    protected Scriptable obj;

    public Delegator() {
        this.obj = null;
    }

    public Delegator(Scriptable obj) {
        this.obj = null;
        this.obj = obj;
    }

    protected Delegator newInstance() {
        try {
            return (Delegator) getClass().newInstance();
        } catch (Exception ex) {
            throw Context.throwAsScriptRuntimeEx(ex);
        }
    }

    public Scriptable getDelegee() {
        return this.obj;
    }

    public void setDelegee(Scriptable obj) {
        this.obj = obj;
    }

    @Override // org.mozilla.javascript.Scriptable
    public String getClassName() {
        return getDelegee().getClassName();
    }

    @Override // org.mozilla.javascript.Scriptable
    public Object get(String name, Scriptable start) {
        return getDelegee().get(name, start);
    }

    @Override // org.mozilla.javascript.SymbolScriptable
    public Object get(Symbol key, Scriptable start) {
        Scriptable delegee = getDelegee();
        if (delegee instanceof SymbolScriptable) {
            return ((SymbolScriptable) delegee).get(key, start);
        }
        return Scriptable.NOT_FOUND;
    }

    @Override // org.mozilla.javascript.Scriptable
    public Object get(int index, Scriptable start) {
        return getDelegee().get(index, start);
    }

    @Override // org.mozilla.javascript.Scriptable
    public boolean has(String name, Scriptable start) {
        return getDelegee().has(name, start);
    }

    @Override // org.mozilla.javascript.SymbolScriptable
    public boolean has(Symbol key, Scriptable start) {
        Scriptable delegee = getDelegee();
        if (delegee instanceof SymbolScriptable) {
            return ((SymbolScriptable) delegee).has(key, start);
        }
        return false;
    }

    @Override // org.mozilla.javascript.Scriptable
    public boolean has(int index, Scriptable start) {
        return getDelegee().has(index, start);
    }

    @Override // org.mozilla.javascript.Scriptable
    public void put(String name, Scriptable start, Object value) {
        getDelegee().put(name, start, value);
    }

    @Override // org.mozilla.javascript.SymbolScriptable
    public void put(Symbol symbol, Scriptable start, Object value) {
        Scriptable delegee = getDelegee();
        if (delegee instanceof SymbolScriptable) {
            ((SymbolScriptable) delegee).put(symbol, start, value);
        }
    }

    @Override // org.mozilla.javascript.Scriptable
    public void put(int index, Scriptable start, Object value) {
        getDelegee().put(index, start, value);
    }

    @Override // org.mozilla.javascript.Scriptable
    public void delete(String name) {
        getDelegee().delete(name);
    }

    @Override // org.mozilla.javascript.SymbolScriptable
    public void delete(Symbol key) {
        Scriptable delegee = getDelegee();
        if (delegee instanceof SymbolScriptable) {
            ((SymbolScriptable) delegee).delete(key);
        }
    }

    @Override // org.mozilla.javascript.Scriptable
    public void delete(int index) {
        getDelegee().delete(index);
    }

    @Override // org.mozilla.javascript.Scriptable
    public Scriptable getPrototype() {
        return getDelegee().getPrototype();
    }

    @Override // org.mozilla.javascript.Scriptable
    public void setPrototype(Scriptable prototype) {
        getDelegee().setPrototype(prototype);
    }

    @Override // org.mozilla.javascript.Scriptable
    public Scriptable getParentScope() {
        return getDelegee().getParentScope();
    }

    @Override // org.mozilla.javascript.Scriptable
    public void setParentScope(Scriptable parent) {
        getDelegee().setParentScope(parent);
    }

    @Override // org.mozilla.javascript.Scriptable
    public Object[] getIds() {
        return getDelegee().getIds();
    }

    @Override // org.mozilla.javascript.Scriptable
    public Object getDefaultValue(Class<?> hint) {
        return (hint == null || hint == ScriptRuntime.ScriptableClass || hint == ScriptRuntime.FunctionClass) ? this : getDelegee().getDefaultValue(hint);
    }

    @Override // org.mozilla.javascript.Scriptable
    public boolean hasInstance(Scriptable instance) {
        return getDelegee().hasInstance(instance);
    }

    @Override // org.mozilla.javascript.Function, org.mozilla.javascript.Callable
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        return ((Function) getDelegee()).call(cx, scope, thisObj, args);
    }

    @Override // org.mozilla.javascript.Function
    public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
        Scriptable delegee;
        Scriptable myDelegee = getDelegee();
        if (myDelegee == null) {
            Delegator n = newInstance();
            if (args.length == 0) {
                delegee = new NativeObject();
            } else {
                delegee = ScriptRuntime.toObject(cx, scope, args[0]);
            }
            n.setDelegee(delegee);
            return n;
        }
        return ((Function) myDelegee).construct(cx, scope, args);
    }
}
