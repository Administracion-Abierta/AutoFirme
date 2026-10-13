package org.mozilla.javascript;

import com.aowagie.text.ElementTags;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeSet.class */
public class NativeSet extends IdScriptableObject {
    private static final long serialVersionUID = -8442212766987072986L;
    static final String ITERATOR_TAG = "Set Iterator";
    private final Hashtable entries = new Hashtable();
    private boolean instanceOfSet = false;
    private static final int Id_constructor = 1;
    private static final int Id_add = 2;
    private static final int Id_delete = 3;
    private static final int Id_has = 4;
    private static final int Id_clear = 5;
    private static final int Id_keys = 6;
    private static final int Id_values = 6;
    private static final int Id_entries = 7;
    private static final int Id_forEach = 8;
    private static final int SymbolId_getSize = 9;
    private static final int SymbolId_toStringTag = 10;
    private static final int MAX_PROTOTYPE_ID = 10;
    private static final Object SET_TAG = "Set";
    static final SymbolKey GETSIZE = new SymbolKey("[Symbol.getSize]");

    static void init(Context cx, Scriptable scope, boolean sealed) {
        NativeSet nativeSet = new NativeSet();
        nativeSet.exportAsJSClass(10, scope, false);
        ScriptableObject scriptableObject = (ScriptableObject) cx.newObject(scope);
        scriptableObject.put("enumerable", scriptableObject, Boolean.FALSE);
        scriptableObject.put("configurable", scriptableObject, Boolean.TRUE);
        scriptableObject.put("get", scriptableObject, nativeSet.get(GETSIZE, nativeSet));
        nativeSet.defineOwnProperty(cx, ElementTags.SIZE, scriptableObject);
        if (sealed) {
            nativeSet.sealObject();
        }
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return "Set";
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.IdFunctionCall
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(SET_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case 1:
                if (thisObj == null) {
                    NativeSet ns = new NativeSet();
                    ns.instanceOfSet = true;
                    if (args.length > 0) {
                        loadFromIterable(cx, scope, ns, args[0]);
                    }
                    return ns;
                }
                throw ScriptRuntime.typeError1("msg.no.new", "Set");
            case 2:
                return realThis(thisObj, f).js_add(args.length > 0 ? args[0] : Undefined.instance);
            case 3:
                return realThis(thisObj, f).js_delete(args.length > 0 ? args[0] : Undefined.instance);
            case 4:
                return realThis(thisObj, f).js_has(args.length > 0 ? args[0] : Undefined.instance);
            case 5:
                return realThis(thisObj, f).js_clear();
            case 6:
                return realThis(thisObj, f).js_iterator(scope, NativeCollectionIterator.Type.VALUES);
            case 7:
                return realThis(thisObj, f).js_iterator(scope, NativeCollectionIterator.Type.BOTH);
            case 8:
                return realThis(thisObj, f).js_forEach(cx, scope, args.length > 0 ? args[0] : Undefined.instance, args.length > 1 ? args[1] : Undefined.instance);
            case 9:
                return realThis(thisObj, f).js_getSize();
            default:
                throw new IllegalArgumentException("Set.prototype has no method: " + f.getFunctionName());
        }
    }

    private Object js_add(Object k) {
        Object key = k;
        if ((key instanceof Number) && ((Number) key).doubleValue() == ScriptRuntime.negativeZero) {
            key = ScriptRuntime.zeroObj;
        }
        this.entries.put(key, key);
        return this;
    }

    private Object js_delete(Object arg) {
        Object ov = this.entries.delete(arg);
        return Boolean.valueOf(ov != null);
    }

    private Object js_has(Object arg) {
        return Boolean.valueOf(this.entries.has(arg));
    }

    private Object js_clear() {
        this.entries.clear();
        return Undefined.instance;
    }

    private Object js_getSize() {
        return Integer.valueOf(this.entries.size());
    }

    private Object js_iterator(Scriptable scope, NativeCollectionIterator.Type type) {
        return new NativeCollectionIterator(scope, ITERATOR_TAG, type, this.entries.iterator());
    }

    private Object js_forEach(Context cx, Scriptable scope, Object arg1, Object arg2) {
        if (!(arg1 instanceof Callable)) {
            throw ScriptRuntime.notFunctionError(arg1);
        }
        Callable f = (Callable) arg1;
        boolean isStrict = cx.isStrictMode();
        for (Hashtable.Entry e : this.entries) {
            Scriptable thisObj = ScriptRuntime.toObjectOrNull(cx, arg2, scope);
            if (thisObj == null && !isStrict) {
                thisObj = scope;
            }
            if (thisObj == null) {
                thisObj = Undefined.SCRIPTABLE_UNDEFINED;
            }
            f.call(cx, scope, thisObj, new Object[]{e.value, e.value, this});
        }
        return Undefined.instance;
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x00c6 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static void loadFromIterable(org.mozilla.javascript.Context r9, org.mozilla.javascript.Scriptable r10, org.mozilla.javascript.ScriptableObject r11, java.lang.Object r12) {
        /*
            Method dump skipped, instruction units count: 239
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.NativeSet.loadFromIterable(org.mozilla.javascript.Context, org.mozilla.javascript.Scriptable, org.mozilla.javascript.ScriptableObject, java.lang.Object):void");
    }

    private static NativeSet realThis(Scriptable thisObj, IdFunctionObject f) {
        if (thisObj == null) {
            throw incompatibleCallError(f);
        }
        try {
            NativeSet ns = (NativeSet) thisObj;
            if (!ns.instanceOfSet) {
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
        switch (id) {
            case 9:
                initPrototypeMethod(SET_TAG, id, GETSIZE, "get size", 0);
                return;
            case 10:
                initPrototypeValue(10, SymbolKey.TO_STRING_TAG, getClassName(), 3);
                return;
            default:
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
                    case 5:
                        arity = 0;
                        s = "clear";
                        break;
                    case 6:
                        arity = 0;
                        s = "values";
                        break;
                    case 7:
                        arity = 0;
                        s = "entries";
                        break;
                    case 8:
                        arity = 1;
                        s = "forEach";
                        break;
                    default:
                        throw new IllegalArgumentException(String.valueOf(id));
                }
                initPrototypeMethod(SET_TAG, id, s, (String) null, arity);
                return;
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(Symbol k) {
        if (GETSIZE.equals(k)) {
            return 9;
        }
        if (SymbolKey.ITERATOR.equals(k)) {
            return 6;
        }
        if (SymbolKey.TO_STRING_TAG.equals(k)) {
            return 10;
        }
        return 0;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:33:0x00e9 A[PHI: r5 r6
      0x00e9: PHI (r5v1 'id' int) = 
      (r5v0 'id' int)
      (r5v3 'id' int)
      (r5v0 'id' int)
      (r5v4 'id' int)
      (r5v5 'id' int)
      (r5v0 'id' int)
      (r5v6 'id' int)
      (r5v7 'id' int)
      (r5v8 'id' int)
      (r5v9 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
     binds: [B:3:0x0008, B:32:0x00e1, B:30:0x00d5, B:31:0x00d8, B:28:0x00c8, B:24:0x00ae, B:25:0x00b1, B:22:0x00a2, B:19:0x008c, B:18:0x0083, B:12:0x0067, B:14:0x0071, B:16:0x007b, B:7:0x0051, B:9:0x005b] A[DONT_GENERATE, DONT_INLINE]
      0x00e9: PHI (r6v1 'X' java.lang.String) = 
      (r6v0 'X' java.lang.String)
      (r6v2 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v3 'X' java.lang.String)
      (r6v4 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v5 'X' java.lang.String)
      (r6v6 'X' java.lang.String)
      (r6v7 'X' java.lang.String)
      (r6v8 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
     binds: [B:3:0x0008, B:32:0x00e1, B:30:0x00d5, B:31:0x00d8, B:28:0x00c8, B:24:0x00ae, B:25:0x00b1, B:22:0x00a2, B:19:0x008c, B:18:0x0083, B:12:0x0067, B:14:0x0071, B:16:0x007b, B:7:0x0051, B:9:0x005b] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:35:0x00ed  */
    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(String s) {
        int id = 0;
        String X = null;
        switch (s.length()) {
            case 3:
                int c = s.charAt(0);
                if (c != 97) {
                    if (c == 104 && s.charAt(2) == 's' && s.charAt(1) == 'a') {
                        id = 4;
                    } else if (X != null) {
                        id = 0;
                    }
                } else if (s.charAt(2) == 'd' && s.charAt(1) == 'd') {
                    id = 2;
                } else if (X != null && X != s && !X.equals(s)) {
                    id = 0;
                }
                break;
            case 4:
                X = "keys";
                id = 6;
                if (X != null) {
                    id = 0;
                }
                break;
            case 5:
                X = "clear";
                id = 5;
                if (X != null) {
                    id = 0;
                }
                break;
            case 6:
                int c2 = s.charAt(0);
                if (c2 == 100) {
                    X = "delete";
                    id = 3;
                } else if (c2 == 118) {
                    X = "values";
                    id = 6;
                }
                if (X != null) {
                    id = 0;
                }
                break;
            case 7:
                int c3 = s.charAt(0);
                if (c3 == 101) {
                    X = "entries";
                    id = 7;
                } else if (c3 == 102) {
                    X = "forEach";
                    id = 8;
                }
                if (X != null) {
                    id = 0;
                }
                break;
            case 8:
            case 9:
            case 10:
            default:
                if (X != null) {
                    id = 0;
                }
                break;
            case 11:
                X = "constructor";
                id = 1;
                if (X != null) {
                    id = 0;
                }
                break;
        }
        return id;
    }
}
