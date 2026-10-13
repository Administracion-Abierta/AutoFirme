package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.WeakHashMap;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeWeakMap.class */
public class NativeWeakMap extends IdScriptableObject {
    private static final long serialVersionUID = 8670434366883930453L;
    private boolean instanceOfWeakMap = false;
    private transient WeakHashMap<Scriptable, Object> map = new WeakHashMap<>();
    private static final int Id_constructor = 1;
    private static final int Id_delete = 2;
    private static final int Id_get = 3;
    private static final int Id_has = 4;
    private static final int Id_set = 5;
    private static final int SymbolId_toStringTag = 6;
    private static final int MAX_PROTOTYPE_ID = 6;
    private static final Object MAP_TAG = "WeakMap";
    private static final Object NULL_VALUE = new Object();

    static void init(Scriptable scope, boolean sealed) {
        NativeWeakMap m = new NativeWeakMap();
        m.exportAsJSClass(6, scope, sealed);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return "WeakMap";
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
                    NativeWeakMap nm = new NativeWeakMap();
                    nm.instanceOfWeakMap = true;
                    if (args.length > 0) {
                        NativeMap.loadFromIterable(cx, scope, nm, args[0]);
                    }
                    return nm;
                }
                throw ScriptRuntime.typeError1("msg.no.new", "WeakMap");
            case 2:
                return realThis(thisObj, f).js_delete(args.length > 0 ? args[0] : Undefined.instance);
            case 3:
                return realThis(thisObj, f).js_get(args.length > 0 ? args[0] : Undefined.instance);
            case 4:
                return realThis(thisObj, f).js_has(args.length > 0 ? args[0] : Undefined.instance);
            case 5:
                return realThis(thisObj, f).js_set(args.length > 0 ? args[0] : Undefined.instance, args.length > 1 ? args[1] : Undefined.instance);
            default:
                throw new IllegalArgumentException("WeakMap.prototype has no method: " + f.getFunctionName());
        }
    }

    private Object js_delete(Object key) {
        if (ScriptRuntime.isObject(key)) {
            return Boolean.valueOf(this.map.remove(key) != null);
        }
        return Boolean.FALSE;
    }

    private Object js_get(Object key) {
        if (!ScriptRuntime.isObject(key)) {
            return Undefined.instance;
        }
        Object result = this.map.get(key);
        if (result == null) {
            return Undefined.instance;
        }
        if (result == NULL_VALUE) {
            return null;
        }
        return result;
    }

    private Object js_has(Object key) {
        if (!ScriptRuntime.isObject(key)) {
            return Boolean.FALSE;
        }
        return Boolean.valueOf(this.map.containsKey(key));
    }

    private Object js_set(Object key, Object v) {
        if (!ScriptRuntime.isObject(key)) {
            throw ScriptRuntime.typeError1("msg.arg.not.object", ScriptRuntime.typeof(key));
        }
        Object value = v == null ? NULL_VALUE : v;
        this.map.put((Scriptable) key, value);
        return this;
    }

    private static NativeWeakMap realThis(Scriptable thisObj, IdFunctionObject f) {
        if (thisObj == null) {
            throw incompatibleCallError(f);
        }
        try {
            NativeWeakMap nm = (NativeWeakMap) thisObj;
            if (!nm.instanceOfWeakMap) {
                throw incompatibleCallError(f);
            }
            return nm;
        } catch (ClassCastException e) {
            throw incompatibleCallError(f);
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void initPrototypeId(int id) {
        int arity;
        String s;
        if (id == 6) {
            initPrototypeValue(6, SymbolKey.TO_STRING_TAG, getClassName(), 3);
            return;
        }
        switch (id) {
            case 1:
                arity = 0;
                s = "constructor";
                break;
            case 2:
                arity = 1;
                s = "delete";
                break;
            case 3:
                arity = 1;
                s = "get";
                break;
            case 4:
                arity = 1;
                s = "has";
                break;
            case 5:
                arity = 2;
                s = "set";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(MAP_TAG, id, s, (String) null, arity);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(Symbol k) {
        if (SymbolKey.TO_STRING_TAG.equals(k)) {
            return 6;
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
            if (c == 103) {
                if (s.charAt(2) == 't' && s.charAt(1) == 'e') {
                    id = 3;
                }
            } else if (c == 104) {
                if (s.charAt(2) == 's' && s.charAt(1) == 'a') {
                    id = 4;
                }
            } else if (c == 115 && s.charAt(2) == 't' && s.charAt(1) == 'e') {
                id = 5;
            }
            return id;
        }
        if (s_length == 6) {
            X = "delete";
            id = 2;
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
