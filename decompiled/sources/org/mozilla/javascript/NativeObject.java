package org.mozilla.javascript;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.xml.security.utils.Constants;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeObject.class */
public class NativeObject extends IdScriptableObject implements Map {
    private static final long serialVersionUID = -6345305608474346996L;
    private static final Object OBJECT_TAG = Constants._TAG_OBJECT;
    private static final int ConstructorId_getPrototypeOf = -1;
    private static final int ConstructorId_keys = -2;
    private static final int ConstructorId_getOwnPropertyNames = -3;
    private static final int ConstructorId_getOwnPropertyDescriptor = -4;
    private static final int ConstructorId_defineProperty = -5;
    private static final int ConstructorId_isExtensible = -6;
    private static final int ConstructorId_preventExtensions = -7;
    private static final int ConstructorId_defineProperties = -8;
    private static final int ConstructorId_create = -9;
    private static final int ConstructorId_isSealed = -10;
    private static final int ConstructorId_isFrozen = -11;
    private static final int ConstructorId_seal = -12;
    private static final int ConstructorId_freeze = -13;
    private static final int ConstructorId_getOwnPropertySymbols = -14;
    private static final int ConstructorId_assign = -15;
    private static final int ConstructorId_is = -16;
    private static final int ConstructorId_setPrototypeOf = -17;
    private static final int Id_constructor = 1;
    private static final int Id_toString = 2;
    private static final int Id_toLocaleString = 3;
    private static final int Id_valueOf = 4;
    private static final int Id_hasOwnProperty = 5;
    private static final int Id_propertyIsEnumerable = 6;
    private static final int Id_isPrototypeOf = 7;
    private static final int Id_toSource = 8;
    private static final int Id___defineGetter__ = 9;
    private static final int Id___defineSetter__ = 10;
    private static final int Id___lookupGetter__ = 11;
    private static final int Id___lookupSetter__ = 12;
    private static final int MAX_PROTOTYPE_ID = 12;

    static void init(Scriptable scope, boolean sealed) {
        NativeObject obj = new NativeObject();
        obj.exportAsJSClass(12, scope, sealed);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return Constants._TAG_OBJECT;
    }

    public String toString() {
        return ScriptRuntime.defaultObjectToString(this);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void fillConstructorProperties(IdFunctionObject ctor) {
        addIdFunctionProperty(ctor, OBJECT_TAG, -1, "getPrototypeOf", 1);
        if (Context.getCurrentContext().version >= 200) {
            addIdFunctionProperty(ctor, OBJECT_TAG, ConstructorId_setPrototypeOf, "setPrototypeOf", 2);
        }
        addIdFunctionProperty(ctor, OBJECT_TAG, -2, "keys", 1);
        addIdFunctionProperty(ctor, OBJECT_TAG, -3, "getOwnPropertyNames", 1);
        addIdFunctionProperty(ctor, OBJECT_TAG, -14, "getOwnPropertySymbols", 1);
        addIdFunctionProperty(ctor, OBJECT_TAG, -4, "getOwnPropertyDescriptor", 2);
        addIdFunctionProperty(ctor, OBJECT_TAG, -5, "defineProperty", 3);
        addIdFunctionProperty(ctor, OBJECT_TAG, -6, "isExtensible", 1);
        addIdFunctionProperty(ctor, OBJECT_TAG, -7, "preventExtensions", 1);
        addIdFunctionProperty(ctor, OBJECT_TAG, -8, "defineProperties", 2);
        addIdFunctionProperty(ctor, OBJECT_TAG, ConstructorId_create, "create", 2);
        addIdFunctionProperty(ctor, OBJECT_TAG, -10, "isSealed", 1);
        addIdFunctionProperty(ctor, OBJECT_TAG, -11, "isFrozen", 1);
        addIdFunctionProperty(ctor, OBJECT_TAG, -12, "seal", 1);
        addIdFunctionProperty(ctor, OBJECT_TAG, ConstructorId_freeze, "freeze", 1);
        addIdFunctionProperty(ctor, OBJECT_TAG, ConstructorId_assign, "assign", 2);
        addIdFunctionProperty(ctor, OBJECT_TAG, -16, "is", 2);
        super.fillConstructorProperties(ctor);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void initPrototypeId(int id) {
        int arity;
        String s;
        switch (id) {
            case 1:
                arity = 1;
                s = "constructor";
                break;
            case 2:
                arity = 0;
                s = "toString";
                break;
            case 3:
                arity = 0;
                s = "toLocaleString";
                break;
            case 4:
                arity = 0;
                s = "valueOf";
                break;
            case 5:
                arity = 1;
                s = "hasOwnProperty";
                break;
            case 6:
                arity = 1;
                s = "propertyIsEnumerable";
                break;
            case 7:
                arity = 1;
                s = "isPrototypeOf";
                break;
            case 8:
                arity = 0;
                s = "toSource";
                break;
            case 9:
                arity = 2;
                s = "__defineGetter__";
                break;
            case 10:
                arity = 2;
                s = "__defineSetter__";
                break;
            case 11:
                arity = 1;
                s = "__lookupGetter__";
                break;
            case 12:
                arity = 1;
                s = "__lookupSetter__";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(OBJECT_TAG, id, s, arity);
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.IdFunctionCall
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        int ii;
        Object val;
        Object gs;
        Scriptable v;
        boolean result;
        boolean result2;
        if (!f.hasTag(OBJECT_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case ConstructorId_setPrototypeOf /* -17 */:
                if (args.length < 2) {
                    throw ScriptRuntime.typeError1("msg.incompat.call", "setPrototypeOf");
                }
                Scriptable proto = args[1] == null ? null : ensureScriptable(args[1]);
                if (proto instanceof Symbol) {
                    throw ScriptRuntime.typeError1("msg.arg.not.object", ScriptRuntime.typeof(proto));
                }
                Object arg0 = args[0];
                if (cx.getLanguageVersion() >= 200) {
                    ScriptRuntimeES6.requireObjectCoercible(cx, arg0, f);
                }
                if (!(arg0 instanceof ScriptableObject)) {
                    return arg0;
                }
                ScriptableObject obj = (ScriptableObject) arg0;
                if (!obj.isExtensible()) {
                    throw ScriptRuntime.typeError0("msg.not.extensible");
                }
                Scriptable prototype = proto;
                while (true) {
                    Scriptable prototypeProto = prototype;
                    if (prototypeProto != null) {
                        if (prototypeProto == obj) {
                            throw ScriptRuntime.typeError1("msg.object.cyclic.prototype", obj.getClass().getSimpleName());
                        }
                        prototype = prototypeProto.getPrototype();
                    } else {
                        obj.setPrototype(proto);
                        return obj;
                    }
                }
                break;
            case -16:
                Object a1 = args.length < 1 ? Undefined.instance : args[0];
                Object a2 = args.length < 2 ? Undefined.instance : args[1];
                return ScriptRuntime.wrapBoolean(ScriptRuntime.same(a1, a2));
            case ConstructorId_assign /* -15 */:
                if (args.length < 1) {
                    throw ScriptRuntime.typeError1("msg.incompat.call", "assign");
                }
                Scriptable targetObj = ScriptRuntime.toObject(cx, thisObj, args[0]);
                for (int i = 1; i < args.length; i++) {
                    if (args[i] != null && !Undefined.isUndefined(args[i])) {
                        Scriptable sourceObj = ScriptRuntime.toObject(cx, thisObj, args[i]);
                        for (Object key : sourceObj.getIds()) {
                            if (key instanceof String) {
                                Object val2 = sourceObj.get((String) key, sourceObj);
                                if (val2 != Scriptable.NOT_FOUND && !Undefined.isUndefined(val2)) {
                                    targetObj.put((String) key, targetObj, val2);
                                }
                            } else if ((key instanceof Number) && (val = sourceObj.get((ii = ScriptRuntime.toInt32(key)), sourceObj)) != Scriptable.NOT_FOUND && !Undefined.isUndefined(val)) {
                                targetObj.put(ii, targetObj, val);
                            }
                        }
                    }
                }
                return targetObj;
            case -14:
                Scriptable s = getCompatibleObject(cx, scope, args.length < 1 ? Undefined.instance : args[0]);
                Object[] ids = ensureScriptableObject(s).getIds(true, true);
                ArrayList<Object> syms = new ArrayList<>();
                for (int i2 = 0; i2 < ids.length; i2++) {
                    if (ids[i2] instanceof Symbol) {
                        syms.add(ids[i2]);
                    }
                }
                return cx.newArray(scope, syms.toArray());
            case ConstructorId_freeze /* -13 */:
                Object arg = args.length < 1 ? Undefined.instance : args[0];
                if (cx.getLanguageVersion() >= 200 && !(arg instanceof ScriptableObject)) {
                    return arg;
                }
                ScriptableObject scriptableObjectEnsureScriptableObject = ensureScriptableObject(arg);
                for (Object name : scriptableObjectEnsureScriptableObject.getIds(true, true)) {
                    ScriptableObject ownPropertyDescriptor = scriptableObjectEnsureScriptableObject.getOwnPropertyDescriptor(cx, name);
                    if (isDataDescriptor(ownPropertyDescriptor) && Boolean.TRUE.equals(ownPropertyDescriptor.get("writable"))) {
                        ownPropertyDescriptor.put("writable", ownPropertyDescriptor, Boolean.FALSE);
                    }
                    if (Boolean.TRUE.equals(ownPropertyDescriptor.get("configurable"))) {
                        ownPropertyDescriptor.put("configurable", ownPropertyDescriptor, Boolean.FALSE);
                    }
                    scriptableObjectEnsureScriptableObject.defineOwnProperty(cx, name, ownPropertyDescriptor, false);
                }
                scriptableObjectEnsureScriptableObject.preventExtensions();
                return scriptableObjectEnsureScriptableObject;
            case -12:
                Object arg2 = args.length < 1 ? Undefined.instance : args[0];
                if (cx.getLanguageVersion() >= 200 && !(arg2 instanceof ScriptableObject)) {
                    return arg2;
                }
                ScriptableObject scriptableObjectEnsureScriptableObject2 = ensureScriptableObject(arg2);
                for (Object name2 : scriptableObjectEnsureScriptableObject2.getAllIds()) {
                    ScriptableObject ownPropertyDescriptor2 = scriptableObjectEnsureScriptableObject2.getOwnPropertyDescriptor(cx, name2);
                    if (Boolean.TRUE.equals(ownPropertyDescriptor2.get("configurable"))) {
                        ownPropertyDescriptor2.put("configurable", ownPropertyDescriptor2, Boolean.FALSE);
                        scriptableObjectEnsureScriptableObject2.defineOwnProperty(cx, name2, ownPropertyDescriptor2, false);
                    }
                }
                scriptableObjectEnsureScriptableObject2.preventExtensions();
                return scriptableObjectEnsureScriptableObject2;
            case -11:
                Object arg3 = args.length < 1 ? Undefined.instance : args[0];
                if (cx.getLanguageVersion() >= 200 && !(arg3 instanceof ScriptableObject)) {
                    return Boolean.TRUE;
                }
                ScriptableObject obj2 = ensureScriptableObject(arg3);
                if (obj2.isExtensible()) {
                    return Boolean.FALSE;
                }
                for (Object obj3 : obj2.getAllIds()) {
                    ScriptableObject desc = obj2.getOwnPropertyDescriptor(cx, obj3);
                    if (Boolean.TRUE.equals(desc.get("configurable"))) {
                        return Boolean.FALSE;
                    }
                    if (isDataDescriptor(desc) && Boolean.TRUE.equals(desc.get("writable"))) {
                        return Boolean.FALSE;
                    }
                }
                return Boolean.TRUE;
            case -10:
                Object arg4 = args.length < 1 ? Undefined.instance : args[0];
                if (cx.getLanguageVersion() >= 200 && !(arg4 instanceof ScriptableObject)) {
                    return Boolean.TRUE;
                }
                ScriptableObject obj4 = ensureScriptableObject(arg4);
                if (obj4.isExtensible()) {
                    return Boolean.FALSE;
                }
                for (Object obj5 : obj4.getAllIds()) {
                    Object configurable = obj4.getOwnPropertyDescriptor(cx, obj5).get("configurable");
                    if (Boolean.TRUE.equals(configurable)) {
                        return Boolean.FALSE;
                    }
                }
                return Boolean.TRUE;
            case ConstructorId_create /* -9 */:
                Object arg5 = args.length < 1 ? Undefined.instance : args[0];
                Scriptable obj6 = arg5 == null ? null : ensureScriptable(arg5);
                ScriptableObject newObject = new NativeObject();
                newObject.setParentScope(scope);
                newObject.setPrototype(obj6);
                if (args.length > 1 && !Undefined.isUndefined(args[1])) {
                    Scriptable props = Context.toObject(args[1], scope);
                    newObject.defineOwnProperties(cx, ensureScriptableObject(props));
                }
                return newObject;
            case -8:
                ScriptableObject obj7 = ensureScriptableObject(args.length < 1 ? Undefined.instance : args[0]);
                Object propsObj = args.length < 2 ? Undefined.instance : args[1];
                Scriptable props2 = Context.toObject(propsObj, scope);
                obj7.defineOwnProperties(cx, ensureScriptableObject(props2));
                return obj7;
            case -7:
                Object arg6 = args.length < 1 ? Undefined.instance : args[0];
                if (cx.getLanguageVersion() >= 200 && !(arg6 instanceof ScriptableObject)) {
                    return arg6;
                }
                ScriptableObject obj8 = ensureScriptableObject(arg6);
                obj8.preventExtensions();
                return obj8;
            case -6:
                Object arg7 = args.length < 1 ? Undefined.instance : args[0];
                if (cx.getLanguageVersion() >= 200 && !(arg7 instanceof ScriptableObject)) {
                    return Boolean.FALSE;
                }
                return Boolean.valueOf(ensureScriptableObject(arg7).isExtensible());
            case -5:
                ScriptableObject obj9 = ensureScriptableObject(args.length < 1 ? Undefined.instance : args[0]);
                Object name3 = args.length < 2 ? Undefined.instance : args[1];
                Object descArg = args.length < 3 ? Undefined.instance : args[2];
                obj9.defineOwnProperty(cx, name3, ensureScriptableObject(descArg));
                return obj9;
            case -4:
                Scriptable s2 = getCompatibleObject(cx, scope, args.length < 1 ? Undefined.instance : args[0]);
                ScriptableObject obj10 = ensureScriptableObject(s2);
                Object nameArg = args.length < 2 ? Undefined.instance : args[1];
                Scriptable desc2 = obj10.getOwnPropertyDescriptor(cx, nameArg);
                return desc2 == null ? Undefined.instance : desc2;
            case -3:
                Scriptable s3 = getCompatibleObject(cx, scope, args.length < 1 ? Undefined.instance : args[0]);
                Object[] ids2 = ensureScriptableObject(s3).getIds(true, false);
                for (int i3 = 0; i3 < ids2.length; i3++) {
                    ids2[i3] = ScriptRuntime.toString(ids2[i3]);
                }
                return cx.newArray(scope, ids2);
            case -2:
                Object[] ids3 = getCompatibleObject(cx, scope, args.length < 1 ? Undefined.instance : args[0]).getIds();
                for (int i4 = 0; i4 < ids3.length; i4++) {
                    ids3[i4] = ScriptRuntime.toString(ids3[i4]);
                }
                return cx.newArray(scope, ids3);
            case -1:
                return getCompatibleObject(cx, scope, args.length < 1 ? Undefined.instance : args[0]).getPrototype();
            case 0:
            default:
                throw new IllegalArgumentException(String.valueOf(id));
            case 1:
                if (thisObj != null) {
                    return f.construct(cx, scope, args);
                }
                if (args.length == 0 || args[0] == null || Undefined.isUndefined(args[0])) {
                    return new NativeObject();
                }
                return ScriptRuntime.toObject(cx, scope, args[0]);
            case 2:
                if (cx.hasFeature(4)) {
                    String s4 = ScriptRuntime.defaultObjectToSource(cx, scope, thisObj, args);
                    int L = s4.length();
                    if (L != 0 && s4.charAt(0) == '(' && s4.charAt(L - 1) == ')') {
                        s4 = s4.substring(1, L - 1);
                    }
                    return s4;
                }
                return ScriptRuntime.defaultObjectToString(thisObj);
            case 3:
                Object toString = ScriptableObject.getProperty(thisObj, "toString");
                if (!(toString instanceof Callable)) {
                    throw ScriptRuntime.notFunctionError(toString);
                }
                Callable fun = (Callable) toString;
                return fun.call(cx, scope, thisObj, ScriptRuntime.emptyArgs);
            case 4:
                if (cx.getLanguageVersion() < 180 || !(thisObj == null || Undefined.isUndefined(thisObj))) {
                    return thisObj;
                }
                throw ScriptRuntime.typeError0("msg." + (thisObj == null ? "null" : "undef") + ".to.object");
            case 5:
                if (cx.getLanguageVersion() >= 180 && (thisObj == null || Undefined.isUndefined(thisObj))) {
                    throw ScriptRuntime.typeError0("msg." + (thisObj == null ? "null" : "undef") + ".to.object");
                }
                Object arg8 = args.length < 1 ? Undefined.instance : args[0];
                if (arg8 instanceof Symbol) {
                    result2 = ensureSymbolScriptable(thisObj).has((Symbol) arg8, thisObj);
                } else {
                    ScriptRuntime.StringIdOrIndex s5 = ScriptRuntime.toStringIdOrIndex(cx, arg8);
                    if (s5.stringId == null) {
                        result2 = thisObj.has(s5.index, thisObj);
                    } else {
                        result2 = thisObj.has(s5.stringId, thisObj);
                    }
                }
                return ScriptRuntime.wrapBoolean(result2);
            case 6:
                if (cx.getLanguageVersion() >= 180 && (thisObj == null || Undefined.isUndefined(thisObj))) {
                    throw ScriptRuntime.typeError0("msg." + (thisObj == null ? "null" : "undef") + ".to.object");
                }
                Object arg9 = args.length < 1 ? Undefined.instance : args[0];
                if (arg9 instanceof Symbol) {
                    result = ((SymbolScriptable) thisObj).has((Symbol) arg9, thisObj);
                    if (result && (thisObj instanceof ScriptableObject)) {
                        ScriptableObject so = (ScriptableObject) thisObj;
                        int attrs = so.getAttributes((Symbol) arg9);
                        result = (attrs & 2) == 0;
                    }
                } else {
                    ScriptRuntime.StringIdOrIndex s6 = ScriptRuntime.toStringIdOrIndex(cx, arg9);
                    try {
                        if (s6.stringId == null) {
                            result = thisObj.has(s6.index, thisObj);
                            if (result && (thisObj instanceof ScriptableObject)) {
                                ScriptableObject so2 = (ScriptableObject) thisObj;
                                int attrs2 = so2.getAttributes(s6.index);
                                result = (attrs2 & 2) == 0;
                            }
                        } else {
                            result = thisObj.has(s6.stringId, thisObj);
                            if (result && (thisObj instanceof ScriptableObject)) {
                                ScriptableObject so3 = (ScriptableObject) thisObj;
                                int attrs3 = so3.getAttributes(s6.stringId);
                                result = (attrs3 & 2) == 0;
                            }
                        }
                    } catch (EvaluatorException ee) {
                        if (ee.getMessage().startsWith(ScriptRuntime.getMessage1("msg.prop.not.found", s6.stringId == null ? Integer.toString(s6.index) : s6.stringId))) {
                            result = false;
                        } else {
                            throw ee;
                        }
                    }
                }
                return ScriptRuntime.wrapBoolean(result);
            case 7:
                if (cx.getLanguageVersion() >= 180 && (thisObj == null || Undefined.isUndefined(thisObj))) {
                    throw ScriptRuntime.typeError0("msg." + (thisObj == null ? "null" : "undef") + ".to.object");
                }
                boolean result3 = false;
                if (args.length != 0 && (args[0] instanceof Scriptable)) {
                    Scriptable v2 = (Scriptable) args[0];
                    do {
                        v2 = v2.getPrototype();
                        if (v2 == thisObj) {
                            result3 = true;
                        }
                    } while (v2 != null);
                }
                return ScriptRuntime.wrapBoolean(result3);
            case 8:
                return ScriptRuntime.defaultObjectToSource(cx, scope, thisObj, args);
            case 9:
            case 10:
                if (args.length < 2 || !(args[1] instanceof Callable)) {
                    Object badArg = args.length >= 2 ? args[1] : Undefined.instance;
                    throw ScriptRuntime.notFunctionError(badArg);
                }
                if (!(thisObj instanceof ScriptableObject)) {
                    throw Context.reportRuntimeError2("msg.extend.scriptable", thisObj == null ? "null" : thisObj.getClass().getName(), String.valueOf(args[0]));
                }
                ScriptableObject so4 = (ScriptableObject) thisObj;
                ScriptRuntime.StringIdOrIndex s7 = ScriptRuntime.toStringIdOrIndex(cx, args[0]);
                int index = s7.stringId != null ? 0 : s7.index;
                Callable getterOrSetter = (Callable) args[1];
                boolean isSetter = id == 10;
                so4.setGetterOrSetter(s7.stringId, index, getterOrSetter, isSetter);
                if (so4 instanceof NativeArray) {
                    ((NativeArray) so4).setDenseOnly(false);
                }
                return Undefined.instance;
            case 11:
            case 12:
                if (args.length < 1 || !(thisObj instanceof ScriptableObject)) {
                    return Undefined.instance;
                }
                ScriptableObject so5 = (ScriptableObject) thisObj;
                ScriptRuntime.StringIdOrIndex s8 = ScriptRuntime.toStringIdOrIndex(cx, args[0]);
                int index2 = s8.stringId != null ? 0 : s8.index;
                boolean isSetter2 = id == 12;
                while (true) {
                    gs = so5.getGetterOrSetter(s8.stringId, index2, isSetter2);
                    if (gs == null && (v = so5.getPrototype()) != null && (v instanceof ScriptableObject)) {
                        so5 = (ScriptableObject) v;
                    }
                }
                if (gs != null) {
                    return gs;
                }
                return Undefined.instance;
        }
    }

    private static Scriptable getCompatibleObject(Context cx, Scriptable scope, Object arg) {
        if (cx.getLanguageVersion() >= 200) {
            Scriptable s = ScriptRuntime.toObject(cx, scope, arg);
            return ensureScriptable(s);
        }
        return ensureScriptable(arg);
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        if (key instanceof String) {
            return has((String) key, this);
        }
        if (key instanceof Number) {
            return has(((Number) key).intValue(), this);
        }
        return false;
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        for (Object obj : values()) {
            if (value != obj) {
                if (value != null && value.equals(obj)) {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    @Override // java.util.Map
    public Object remove(Object key) {
        Object value = get(key);
        if (key instanceof String) {
            delete((String) key);
        } else if (key instanceof Number) {
            delete(((Number) key).intValue());
        }
        return value;
    }

    @Override // java.util.Map
    public Set<Object> keySet() {
        return new KeySet();
    }

    @Override // java.util.Map
    public Collection<Object> values() {
        return new ValueCollection();
    }

    @Override // java.util.Map
    public Set<Map.Entry<Object, Object>> entrySet() {
        return new EntrySet();
    }

    @Override // java.util.Map
    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Map
    public void putAll(Map m) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Map
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeObject$EntrySet.class */
    class EntrySet extends AbstractSet<Map.Entry<Object, Object>> {
        EntrySet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<Map.Entry<Object, Object>> iterator() {
            return new Iterator<Map.Entry<Object, Object>>() { // from class: org.mozilla.javascript.NativeObject.EntrySet.1
                Object[] ids;
                Object key = null;
                int index = 0;

                {
                    this.ids = NativeObject.this.getIds();
                }

                @Override // java.util.Iterator
                public boolean hasNext() {
                    return this.index < this.ids.length;
                }

                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.Iterator
                public Map.Entry<Object, Object> next() {
                    Object[] objArr = this.ids;
                    int i = this.index;
                    this.index = i + 1;
                    final Object ekey = objArr[i];
                    this.key = ekey;
                    final Object value = NativeObject.this.get(this.key);
                    return new Map.Entry<Object, Object>() { // from class: org.mozilla.javascript.NativeObject.EntrySet.1.1
                        @Override // java.util.Map.Entry
                        public Object getKey() {
                            return ekey;
                        }

                        @Override // java.util.Map.Entry
                        public Object getValue() {
                            return value;
                        }

                        @Override // java.util.Map.Entry
                        public Object setValue(Object value2) {
                            throw new UnsupportedOperationException();
                        }

                        @Override // java.util.Map.Entry
                        public boolean equals(Object other) {
                            if (!(other instanceof Map.Entry)) {
                                return false;
                            }
                            Map.Entry<?, ?> e = (Map.Entry) other;
                            if (ekey != null ? ekey.equals(e.getKey()) : e.getKey() == null) {
                                if (value != null ? value.equals(e.getValue()) : e.getValue() == null) {
                                    return true;
                                }
                            }
                            return false;
                        }

                        @Override // java.util.Map.Entry
                        public int hashCode() {
                            return (ekey == null ? 0 : ekey.hashCode()) ^ (value == null ? 0 : value.hashCode());
                        }

                        public String toString() {
                            return ekey + "=" + value;
                        }
                    };
                }

                @Override // java.util.Iterator
                public void remove() {
                    if (this.key == null) {
                        throw new IllegalStateException();
                    }
                    NativeObject.this.remove(this.key);
                    this.key = null;
                }
            };
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return NativeObject.this.size();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeObject$KeySet.class */
    class KeySet extends AbstractSet<Object> {
        KeySet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object key) {
            return NativeObject.this.containsKey(key);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<Object> iterator() {
            return new Iterator<Object>() { // from class: org.mozilla.javascript.NativeObject.KeySet.1
                Object[] ids;
                Object key;
                int index = 0;

                {
                    this.ids = NativeObject.this.getIds();
                }

                @Override // java.util.Iterator
                public boolean hasNext() {
                    return this.index < this.ids.length;
                }

                @Override // java.util.Iterator
                public Object next() {
                    try {
                        Object[] objArr = this.ids;
                        int i = this.index;
                        this.index = i + 1;
                        Object obj = objArr[i];
                        this.key = obj;
                        return obj;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        this.key = null;
                        throw new NoSuchElementException();
                    }
                }

                @Override // java.util.Iterator
                public void remove() {
                    if (this.key == null) {
                        throw new IllegalStateException();
                    }
                    NativeObject.this.remove(this.key);
                    this.key = null;
                }
            };
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return NativeObject.this.size();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeObject$ValueCollection.class */
    class ValueCollection extends AbstractCollection<Object> {
        ValueCollection() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<Object> iterator() {
            return new Iterator<Object>() { // from class: org.mozilla.javascript.NativeObject.ValueCollection.1
                Object[] ids;
                Object key;
                int index = 0;

                {
                    this.ids = NativeObject.this.getIds();
                }

                @Override // java.util.Iterator
                public boolean hasNext() {
                    return this.index < this.ids.length;
                }

                @Override // java.util.Iterator
                public Object next() {
                    NativeObject nativeObject = NativeObject.this;
                    Object[] objArr = this.ids;
                    int i = this.index;
                    this.index = i + 1;
                    Object obj = objArr[i];
                    this.key = obj;
                    return nativeObject.get(obj);
                }

                @Override // java.util.Iterator
                public void remove() {
                    if (this.key == null) {
                        throw new IllegalStateException();
                    }
                    NativeObject.this.remove(this.key);
                    this.key = null;
                }
            };
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public int size() {
            return NativeObject.this.size();
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(String s) {
        int id = 0;
        String X = null;
        switch (s.length()) {
            case 7:
                X = "valueOf";
                id = 4;
                break;
            case 8:
                int c = s.charAt(3);
                if (c == 111) {
                    X = "toSource";
                    id = 8;
                } else if (c == 116) {
                    X = "toString";
                    id = 2;
                }
                break;
            case 11:
                X = "constructor";
                id = 1;
                break;
            case 13:
                X = "isPrototypeOf";
                id = 7;
                break;
            case 14:
                int c2 = s.charAt(0);
                if (c2 == 104) {
                    X = "hasOwnProperty";
                    id = 5;
                } else if (c2 == 116) {
                    X = "toLocaleString";
                    id = 3;
                }
                break;
            case 16:
                int c3 = s.charAt(2);
                if (c3 == 100) {
                    int c4 = s.charAt(8);
                    if (c4 == 71) {
                        X = "__defineGetter__";
                        id = 9;
                    } else if (c4 == 83) {
                        X = "__defineSetter__";
                        id = 10;
                    }
                } else if (c3 == 108) {
                    int c5 = s.charAt(8);
                    if (c5 == 71) {
                        X = "__lookupGetter__";
                        id = 11;
                    } else if (c5 == 83) {
                        X = "__lookupSetter__";
                        id = 12;
                    }
                }
                break;
            case 20:
                X = "propertyIsEnumerable";
                id = 6;
                break;
        }
        if (X != null && X != s && !X.equals(s)) {
            id = 0;
        }
        return id;
    }
}
