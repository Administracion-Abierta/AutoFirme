package org.mozilla.javascript;

import java.io.Serializable;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeWith.class */
public class NativeWith implements Scriptable, SymbolScriptable, IdFunctionCall, Serializable {
    private static final long serialVersionUID = 1;
    private static final Object FTAG = "With";
    private static final int Id_constructor = 1;
    protected Scriptable prototype;
    protected Scriptable parent;

    static void init(Scriptable scope, boolean sealed) {
        NativeWith obj = new NativeWith();
        obj.setParentScope(scope);
        obj.setPrototype(ScriptableObject.getObjectPrototype(scope));
        IdFunctionObject ctor = new IdFunctionObject(obj, FTAG, 1, "With", 0, scope);
        ctor.markAsConstructor(obj);
        if (sealed) {
            ctor.sealObject();
        }
        ctor.exportAsScopeProperty();
    }

    private NativeWith() {
    }

    protected NativeWith(Scriptable parent, Scriptable prototype) {
        this.parent = parent;
        this.prototype = prototype;
    }

    @Override // org.mozilla.javascript.Scriptable
    public String getClassName() {
        return "With";
    }

    @Override // org.mozilla.javascript.Scriptable
    public boolean has(String id, Scriptable start) {
        return this.prototype.has(id, this.prototype);
    }

    @Override // org.mozilla.javascript.SymbolScriptable
    public boolean has(Symbol key, Scriptable start) {
        if (this.prototype instanceof SymbolScriptable) {
            return ((SymbolScriptable) this.prototype).has(key, this.prototype);
        }
        return false;
    }

    @Override // org.mozilla.javascript.Scriptable
    public boolean has(int index, Scriptable start) {
        return this.prototype.has(index, this.prototype);
    }

    @Override // org.mozilla.javascript.Scriptable
    public Object get(String id, Scriptable start) {
        if (start == this) {
            start = this.prototype;
        }
        return this.prototype.get(id, start);
    }

    @Override // org.mozilla.javascript.SymbolScriptable
    public Object get(Symbol key, Scriptable start) {
        if (start == this) {
            start = this.prototype;
        }
        if (this.prototype instanceof SymbolScriptable) {
            return ((SymbolScriptable) this.prototype).get(key, start);
        }
        return Scriptable.NOT_FOUND;
    }

    @Override // org.mozilla.javascript.Scriptable
    public Object get(int index, Scriptable start) {
        if (start == this) {
            start = this.prototype;
        }
        return this.prototype.get(index, start);
    }

    @Override // org.mozilla.javascript.Scriptable
    public void put(String id, Scriptable start, Object value) {
        if (start == this) {
            start = this.prototype;
        }
        this.prototype.put(id, start, value);
    }

    @Override // org.mozilla.javascript.SymbolScriptable
    public void put(Symbol symbol, Scriptable start, Object value) {
        if (start == this) {
            start = this.prototype;
        }
        if (this.prototype instanceof SymbolScriptable) {
            ((SymbolScriptable) this.prototype).put(symbol, start, value);
        }
    }

    @Override // org.mozilla.javascript.Scriptable
    public void put(int index, Scriptable start, Object value) {
        if (start == this) {
            start = this.prototype;
        }
        this.prototype.put(index, start, value);
    }

    @Override // org.mozilla.javascript.Scriptable
    public void delete(String id) {
        this.prototype.delete(id);
    }

    @Override // org.mozilla.javascript.SymbolScriptable
    public void delete(Symbol key) {
        if (this.prototype instanceof SymbolScriptable) {
            ((SymbolScriptable) this.prototype).delete(key);
        }
    }

    @Override // org.mozilla.javascript.Scriptable
    public void delete(int index) {
        this.prototype.delete(index);
    }

    @Override // org.mozilla.javascript.Scriptable
    public Scriptable getPrototype() {
        return this.prototype;
    }

    @Override // org.mozilla.javascript.Scriptable
    public void setPrototype(Scriptable prototype) {
        this.prototype = prototype;
    }

    @Override // org.mozilla.javascript.Scriptable
    public Scriptable getParentScope() {
        return this.parent;
    }

    @Override // org.mozilla.javascript.Scriptable
    public void setParentScope(Scriptable parent) {
        this.parent = parent;
    }

    @Override // org.mozilla.javascript.Scriptable
    public Object[] getIds() {
        return this.prototype.getIds();
    }

    @Override // org.mozilla.javascript.Scriptable
    public Object getDefaultValue(Class<?> typeHint) {
        return this.prototype.getDefaultValue(typeHint);
    }

    @Override // org.mozilla.javascript.Scriptable
    public boolean hasInstance(Scriptable value) {
        return this.prototype.hasInstance(value);
    }

    protected Object updateDotQuery(boolean value) {
        throw new IllegalStateException();
    }

    @Override // org.mozilla.javascript.IdFunctionCall
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (f.hasTag(FTAG) && f.methodId() == 1) {
            throw Context.reportRuntimeError1("msg.cant.call.indirect", "With");
        }
        throw f.unknown();
    }

    static boolean isWithFunction(Object functionObj) {
        if (functionObj instanceof IdFunctionObject) {
            IdFunctionObject f = (IdFunctionObject) functionObj;
            return f.hasTag(FTAG) && f.methodId() == 1;
        }
        return false;
    }

    static Object newWithSpecial(Context cx, Scriptable scope, Object[] args) {
        Scriptable object;
        ScriptRuntime.checkDeprecated(cx, "With");
        Scriptable scope2 = ScriptableObject.getTopLevelScope(scope);
        NativeWith thisObj = new NativeWith();
        if (args.length == 0) {
            object = ScriptableObject.getObjectPrototype(scope2);
        } else {
            object = ScriptRuntime.toObject(cx, scope2, args[0]);
        }
        thisObj.setPrototype(object);
        thisObj.setParentScope(scope2);
        return thisObj;
    }
}
