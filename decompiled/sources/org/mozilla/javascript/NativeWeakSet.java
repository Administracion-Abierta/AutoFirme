package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.WeakHashMap;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeWeakSet.class */
public class NativeWeakSet extends IdScriptableObject {
    private static final long serialVersionUID = 2065753364224029534L;
    private static final Object MAP_TAG = "WeakSet";
    private boolean instanceOfWeakSet = false;
    private transient WeakHashMap<Scriptable, Boolean> map = new WeakHashMap<>();
    private static final int Id_constructor = 1;
    private static final int Id_add = 2;
    private static final int Id_delete = 3;
    private static final int Id_has = 4;
    private static final int SymbolId_toStringTag = 5;
    private static final int MAX_PROTOTYPE_ID = 5;

    static void init(Scriptable scope, boolean sealed) {
        NativeWeakSet m = new NativeWeakSet();
        m.exportAsJSClass(5, scope, sealed);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return "WeakSet";
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.IdFunctionCall
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(MAP_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case 1:
                if (thisObj == null) {
                    NativeWeakSet ns = new NativeWeakSet();
                    ns.instanceOfWeakSet = true;
                    if (args.length > 0) {
                        NativeSet.loadFromIterable(cx, scope, ns, args[0]);
                    }
                    return ns;
                }
                throw ScriptRuntime.typeError1("msg.no.new", "WeakSet");
            case 2:
                return realThis(thisObj, f).js_add(args.length > 0 ? args[0] : Undefined.instance);
            case 3:
                return realThis(thisObj, f).js_delete(args.length > 0 ? args[0] : Undefined.instance);
            case 4:
                return realThis(thisObj, f).js_has(args.length > 0 ? args[0] : Undefined.instance);
            default:
                throw new IllegalArgumentException("WeakMap.prototype has no method: " + f.getFunctionName());
        }
    }

    private Object js_add(Object key) {
        if (!ScriptRuntime.isObject(key)) {
            throw ScriptRuntime.typeError1("msg.arg.not.object", ScriptRuntime.typeof(key));
        }
        this.map.put((Scriptable) key, Boolean.TRUE);
        return this;
    }

    private Object js_delete(Object key) {
        if (ScriptRuntime.isObject(key)) {
            return Boolean.valueOf(this.map.remove(key) != null);
        }
        return Boolean.FALSE;
    }

    private Object js_has(Object key) {
        if (!ScriptRuntime.isObject(key)) {
            return Boolean.FALSE;
        }
        return Boolean.valueOf(this.map.containsKey(key));
    }

    private static NativeWeakSet realThis(Scriptable thisObj, IdFunctionObject f) {
        if (thisObj == null) {
            throw incompatibleCallError(f);
        }
        try {
            NativeWeakSet ns = (NativeWeakSet) thisObj;
            if (!ns.instanceOfWeakSet) {
                throw incompatibleCallError(f);
            }
            return ns;
        } catch (ClassCastException e) {
            throw incompatibleCallError(f);
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void initPrototypeId(int id) {
        int arity;
        String s;
        if (id == 5) {
            initPrototypeValue(5, SymbolKey.TO_STRING_TAG, getClassName(), 3);
            return;
        }
        switch (id) {
            case 1:
                arity = 0;
                s = "constructor";
                break;
            case 2:
                arity = 1;
                s = "add";
                break;
            case 3:
                arity = 1;
                s = "delete";
                break;
            case 4:
                arity = 1;
                s = "has";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(MAP_TAG, id, s, (String) null, arity);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(Symbol k) {
        if (SymbolKey.TO_STRING_TAG.equals(k)) {
            return 5;
        }
        return 0;
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(String s) {
        int id = 0;
        String X = null;
        int s_length = s.length();
        if (s_length == 3) {
            int c = s.charAt(0);
            if (c == 97) {
                if (s.charAt(2) == 'd' && s.charAt(1) == 'd') {
                    id = 2;
                }
            } else if (c == 104 && s.charAt(2) == 's' && s.charAt(1) == 'a') {
                id = 4;
            }
            return id;
        }
        if (s_length == 6) {
            X = "delete";
            id = 3;
        } else if (s_length == 11) {
            X = "constructor";
            id = 1;
        }
        if (X != null && X != s && !X.equals(s)) {
            id = 0;
        }
        return id;
    }

    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        stream.defaultReadObject();
        this.map = new WeakHashMap<>();
    }
}
