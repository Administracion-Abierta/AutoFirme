package org.mozilla.javascript;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeSymbol.class */
public class NativeSymbol extends IdScriptableObject implements Symbol {
    private static final long serialVersionUID = -589539749749830003L;
    public static final String CLASS_NAME = "Symbol";
    public static final String TYPE_NAME = "symbol";
    private static final Object GLOBAL_TABLE_KEY = new Object();
    private static final Object CONSTRUCTOR_SLOT = new Object();
    private final SymbolKey key;
    private final NativeSymbol symbolData;
    private static final int ConstructorId_keyFor = -2;
    private static final int ConstructorId_for = -1;
    private static final int Id_constructor = 1;
    private static final int Id_toString = 2;
    private static final int Id_valueOf = 4;
    private static final int SymbolId_toStringTag = 3;
    private static final int SymbolId_toPrimitive = 5;
    private static final int MAX_PROTOTYPE_ID = 5;

    public static void init(Context cx, Scriptable scope, boolean sealed) {
        NativeSymbol obj = new NativeSymbol("");
        ScriptableObject ctor = obj.exportAsJSClass(5, scope, false);
        cx.putThreadLocal(CONSTRUCTOR_SLOT, Boolean.TRUE);
        try {
            createStandardSymbol(cx, scope, ctor, "iterator", SymbolKey.ITERATOR);
            createStandardSymbol(cx, scope, ctor, "species", SymbolKey.SPECIES);
            createStandardSymbol(cx, scope, ctor, "toStringTag", SymbolKey.TO_STRING_TAG);
            createStandardSymbol(cx, scope, ctor, "hasInstance", SymbolKey.HAS_INSTANCE);
            createStandardSymbol(cx, scope, ctor, "isConcatSpreadable", SymbolKey.IS_CONCAT_SPREADABLE);
            createStandardSymbol(cx, scope, ctor, "isRegExp", SymbolKey.IS_REGEXP);
            createStandardSymbol(cx, scope, ctor, "toPrimitive", SymbolKey.TO_PRIMITIVE);
            createStandardSymbol(cx, scope, ctor, "match", SymbolKey.MATCH);
            createStandardSymbol(cx, scope, ctor, "replace", SymbolKey.REPLACE);
            createStandardSymbol(cx, scope, ctor, "search", SymbolKey.SEARCH);
            createStandardSymbol(cx, scope, ctor, "split", SymbolKey.SPLIT);
            createStandardSymbol(cx, scope, ctor, "unscopables", SymbolKey.UNSCOPABLES);
            cx.removeThreadLocal(CONSTRUCTOR_SLOT);
            if (sealed) {
                ctor.sealObject();
            }
        } catch (Throwable th) {
            cx.removeThreadLocal(CONSTRUCTOR_SLOT);
            throw th;
        }
    }

    private NativeSymbol(String desc) {
        this.key = new SymbolKey(desc);
        this.symbolData = null;
    }

    private NativeSymbol(SymbolKey key) {
        this.key = key;
        this.symbolData = this;
    }

    public NativeSymbol(NativeSymbol s) {
        this.key = s.key;
        this.symbolData = s.symbolData;
    }

    public static NativeSymbol construct(Context cx, Scriptable scope, Object[] args) {
        cx.putThreadLocal(CONSTRUCTOR_SLOT, Boolean.TRUE);
        try {
            return (NativeSymbol) cx.newObject(scope, "Symbol", args);
        } finally {
            cx.removeThreadLocal(CONSTRUCTOR_SLOT);
        }
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return "Symbol";
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void fillConstructorProperties(IdFunctionObject ctor) {
        super.fillConstructorProperties(ctor);
        addIdFunctionProperty(ctor, "Symbol", -1, "for", 1);
        addIdFunctionProperty(ctor, "Symbol", -2, "keyFor", 1);
    }

    private static void createStandardSymbol(Context cx, Scriptable scope, ScriptableObject ctor, String name, SymbolKey key) {
        Scriptable sym = cx.newObject(scope, "Symbol", new Object[]{name, key});
        ctor.defineProperty(name, sym, 7);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(String s) {
        int id = 0;
        String X = null;
        int s_length = s.length();
        if (s_length == 7) {
            X = "valueOf";
            id = 4;
        } else if (s_length == 8) {
            X = "toString";
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

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(Symbol key) {
        if (SymbolKey.TO_STRING_TAG.equals(key)) {
            return 3;
        }
        if (SymbolKey.TO_PRIMITIVE.equals(key)) {
            return 5;
        }
        return 0;
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void initPrototypeId(int id) {
        switch (id) {
            case 1:
                initPrototypeMethod("Symbol", id, "constructor", 0);
                break;
            case 2:
                initPrototypeMethod("Symbol", id, "toString", 0);
                break;
            case 3:
                initPrototypeValue(id, SymbolKey.TO_STRING_TAG, "Symbol", 3);
                break;
            case 4:
                initPrototypeMethod("Symbol", id, "valueOf", 0);
                break;
            case 5:
                initPrototypeMethod("Symbol", id, SymbolKey.TO_PRIMITIVE, "Symbol.toPrimitive", 1);
                break;
            default:
                super.initPrototypeId(id);
                break;
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.IdFunctionCall
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag("Symbol")) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case -2:
                return js_keyFor(cx, scope, args);
            case -1:
                return js_for(cx, scope, args);
            case 0:
            case 3:
            default:
                return super.execIdCall(f, cx, scope, thisObj, args);
            case 1:
                if (thisObj == null) {
                    if (cx.getThreadLocal(CONSTRUCTOR_SLOT) == null) {
                        throw ScriptRuntime.typeError0("msg.no.symbol.new");
                    }
                    return js_constructor(args);
                }
                return construct(cx, scope, args);
            case 2:
                return getSelf(thisObj).toString();
            case 4:
            case 5:
                return getSelf(thisObj).js_valueOf();
        }
    }

    private static NativeSymbol getSelf(Object thisObj) {
        try {
            return (NativeSymbol) thisObj;
        } catch (ClassCastException e) {
            throw ScriptRuntime.typeError1("msg.invalid.type", thisObj.getClass().getName());
        }
    }

    private static NativeSymbol js_constructor(Object[] args) {
        String desc;
        if (args.length <= 0 || Undefined.instance.equals(args[0])) {
            desc = "";
        } else {
            desc = ScriptRuntime.toString(args[0]);
        }
        if (args.length > 1) {
            return new NativeSymbol((SymbolKey) args[1]);
        }
        return new NativeSymbol(new SymbolKey(desc));
    }

    private Object js_valueOf() {
        return this.symbolData;
    }

    private Object js_for(Context cx, Scriptable scope, Object[] args) {
        String name = args.length > 0 ? ScriptRuntime.toString(args[0]) : ScriptRuntime.toString(Undefined.instance);
        Map<String, NativeSymbol> table = getGlobalMap();
        NativeSymbol ret = table.get(name);
        if (ret == null) {
            ret = construct(cx, scope, new Object[]{name});
            table.put(name, ret);
        }
        return ret;
    }

    private Object js_keyFor(Context cx, Scriptable scope, Object[] args) {
        Object s = args.length > 0 ? args[0] : Undefined.instance;
        if (!(s instanceof NativeSymbol)) {
            throw ScriptRuntime.throwCustomError(cx, scope, "TypeError", "Not a Symbol");
        }
        NativeSymbol sym = (NativeSymbol) s;
        Map<String, NativeSymbol> table = getGlobalMap();
        for (Map.Entry<String, NativeSymbol> e : table.entrySet()) {
            if (e.getValue().key == sym.key) {
                return e.getKey();
            }
        }
        return Undefined.instance;
    }

    public String toString() {
        return this.key.toString();
    }

    private static boolean isStrictMode() {
        Context cx = Context.getCurrentContext();
        return cx != null && cx.isStrictMode();
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public void put(String name, Scriptable start, Object value) {
        if (!isSymbol()) {
            super.put(name, start, value);
        } else if (isStrictMode()) {
            throw ScriptRuntime.typeError0("msg.no.assign.symbol.strict");
        }
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public void put(int index, Scriptable start, Object value) {
        if (!isSymbol()) {
            super.put(index, start, value);
        } else if (isStrictMode()) {
            throw ScriptRuntime.typeError0("msg.no.assign.symbol.strict");
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.SymbolScriptable
    public void put(Symbol key, Scriptable start, Object value) {
        if (!isSymbol()) {
            super.put(key, start, value);
        } else if (isStrictMode()) {
            throw ScriptRuntime.typeError0("msg.no.assign.symbol.strict");
        }
    }

    public boolean isSymbol() {
        return this.symbolData == this;
    }

    @Override // org.mozilla.javascript.ScriptableObject
    public String getTypeOf() {
        return isSymbol() ? TYPE_NAME : super.getTypeOf();
    }

    public int hashCode() {
        return this.key.hashCode();
    }

    public boolean equals(Object x) {
        return this.key.equals(x);
    }

    SymbolKey getKey() {
        return this.key;
    }

    private Map<String, NativeSymbol> getGlobalMap() {
        ScriptableObject top = (ScriptableObject) getTopLevelScope(this);
        Map<String, NativeSymbol> map = (Map) top.getAssociatedValue(GLOBAL_TABLE_KEY);
        if (map == null) {
            map = new HashMap();
            top.associateValue(GLOBAL_TABLE_KEY, map);
        }
        return map;
    }
}
