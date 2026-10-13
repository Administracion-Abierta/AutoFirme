package org.mozilla.javascript;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;
import org.mozilla.javascript.annotations.JSSetter;
import org.mozilla.javascript.annotations.JSStaticFunction;
import org.mozilla.javascript.debug.DebuggableObject;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ScriptableObject.class */
public abstract class ScriptableObject implements Scriptable, SymbolScriptable, Serializable, DebuggableObject, ConstProperties {
    private static final long serialVersionUID = 2829861078851942586L;
    public static final int EMPTY = 0;
    public static final int READONLY = 1;
    public static final int DONTENUM = 2;
    public static final int PERMANENT = 4;
    public static final int UNINITIALIZED_CONST = 8;
    public static final int CONST = 13;
    private Scriptable prototypeObject;
    private Scriptable parentScopeObject;
    private transient SlotMapContainer slotMap;
    private transient ExternalArrayData externalData;
    private volatile Map<Object, Object> associatedValues;
    private boolean isExtensible;
    private boolean isSealed;
    private static final Method GET_ARRAY_LENGTH;
    private static final Comparator<Object> KEY_COMPARATOR;
    static final /* synthetic */ boolean $assertionsDisabled;

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ScriptableObject$SlotAccess.class */
    enum SlotAccess {
        QUERY,
        MODIFY,
        MODIFY_CONST,
        MODIFY_GETTER_SETTER,
        CONVERT_ACCESSOR_TO_DATA
    }

    @Override // org.mozilla.javascript.Scriptable
    public abstract String getClassName();

    static {
        $assertionsDisabled = !ScriptableObject.class.desiredAssertionStatus();
        try {
            GET_ARRAY_LENGTH = ScriptableObject.class.getMethod("getExternalArrayLength", new Class[0]);
            KEY_COMPARATOR = new KeyComparator();
        } catch (NoSuchMethodException nsm) {
            throw new RuntimeException(nsm);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ScriptableObject$Slot.class */
    static class Slot implements Serializable {
        private static final long serialVersionUID = -6090581677123995491L;
        Object name;
        int indexOrHash;
        private short attributes;
        Object value;
        transient Slot next;
        transient Slot orderedNext;

        Slot(Object name, int indexOrHash, int attributes) {
            this.name = name;
            this.indexOrHash = indexOrHash;
            this.attributes = (short) attributes;
        }

        private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
            in.defaultReadObject();
            if (this.name != null) {
                this.indexOrHash = this.name.hashCode();
            }
        }

        boolean setValue(Object value, Scriptable owner, Scriptable start) {
            if ((this.attributes & 1) != 0) {
                if (Context.getContext().isStrictMode()) {
                    throw ScriptRuntime.typeError1("msg.modify.readonly", this.name);
                }
                return true;
            }
            if (owner == start) {
                this.value = value;
                return true;
            }
            return false;
        }

        Object getValue(Scriptable start) {
            return this.value;
        }

        int getAttributes() {
            return this.attributes;
        }

        synchronized void setAttributes(int value) {
            ScriptableObject.checkValidAttributes(value);
            this.attributes = (short) value;
        }

        ScriptableObject getPropertyDescriptor(Context cx, Scriptable scope) {
            return ScriptableObject.buildDataDescriptor(scope, this.value, this.attributes);
        }
    }

    protected static ScriptableObject buildDataDescriptor(Scriptable scope, Object value, int attributes) {
        ScriptableObject desc = new NativeObject();
        ScriptRuntime.setBuiltinProtoAndParent(desc, scope, TopLevel.Builtins.Object);
        desc.defineProperty(ES6Iterator.VALUE_PROPERTY, value, 0);
        desc.defineProperty("writable", Boolean.valueOf((attributes & 1) == 0), 0);
        desc.defineProperty("enumerable", Boolean.valueOf((attributes & 2) == 0), 0);
        desc.defineProperty("configurable", Boolean.valueOf((attributes & 4) == 0), 0);
        return desc;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ScriptableObject$GetterSlot.class */
    static final class GetterSlot extends Slot {
        private static final long serialVersionUID = -4900574849788797588L;
        Object getter;
        Object setter;

        GetterSlot(Object name, int indexOrHash, int attributes) {
            super(name, indexOrHash, attributes);
        }

        @Override // org.mozilla.javascript.ScriptableObject.Slot
        ScriptableObject getPropertyDescriptor(Context cx, Scriptable scope) {
            int attr = getAttributes();
            ScriptableObject desc = new NativeObject();
            ScriptRuntime.setBuiltinProtoAndParent(desc, scope, TopLevel.Builtins.Object);
            desc.defineProperty("enumerable", Boolean.valueOf((attr & 2) == 0), 0);
            desc.defineProperty("configurable", Boolean.valueOf((attr & 4) == 0), 0);
            if (this.getter == null && this.setter == null) {
                desc.defineProperty("writable", Boolean.valueOf((attr & 1) == 0), 0);
            }
            String fName = this.name == null ? OperatorName.FILL_NON_ZERO : this.name.toString();
            if (this.getter != null) {
                if (this.getter instanceof MemberBox) {
                    desc.defineProperty("get", new FunctionObject(fName, ((MemberBox) this.getter).member(), scope), 0);
                } else if (this.getter instanceof Member) {
                    desc.defineProperty("get", new FunctionObject(fName, (Member) this.getter, scope), 0);
                } else {
                    desc.defineProperty("get", this.getter, 0);
                }
            }
            if (this.setter != null) {
                if (this.setter instanceof MemberBox) {
                    desc.defineProperty("set", new FunctionObject(fName, ((MemberBox) this.setter).member(), scope), 0);
                } else if (this.setter instanceof Member) {
                    desc.defineProperty("set", new FunctionObject(fName, (Member) this.setter, scope), 0);
                } else {
                    desc.defineProperty("set", this.setter, 0);
                }
            }
            return desc;
        }

        @Override // org.mozilla.javascript.ScriptableObject.Slot
        boolean setValue(Object value, Scriptable owner, Scriptable start) {
            Object setterThis;
            Object[] args;
            if (this.setter == null) {
                if (this.getter != null) {
                    Context cx = Context.getContext();
                    if (cx.isStrictMode() || cx.hasFeature(11)) {
                        String prop = "";
                        if (this.name != null) {
                            prop = "[" + start.getClassName() + "]." + this.name;
                        }
                        throw ScriptRuntime.typeError2("msg.set.prop.no.setter", prop, Context.toString(value));
                    }
                    return true;
                }
                return super.setValue(value, owner, start);
            }
            Context cx2 = Context.getContext();
            if (!(this.setter instanceof MemberBox)) {
                if (this.setter instanceof Function) {
                    Function f = (Function) this.setter;
                    f.call(cx2, f.getParentScope(), start, new Object[]{value});
                    return true;
                }
                return true;
            }
            MemberBox nativeSetter = (MemberBox) this.setter;
            Class<?>[] pTypes = nativeSetter.argTypes;
            Class<?> valueType = pTypes[pTypes.length - 1];
            int tag = FunctionObject.getTypeTag(valueType);
            Object actualArg = FunctionObject.convertArg(cx2, start, value, tag);
            if (nativeSetter.delegateTo == null) {
                setterThis = start;
                args = new Object[]{actualArg};
            } else {
                setterThis = nativeSetter.delegateTo;
                args = new Object[]{start, actualArg};
            }
            nativeSetter.invoke(setterThis, args);
            return true;
        }

        @Override // org.mozilla.javascript.ScriptableObject.Slot
        Object getValue(Scriptable start) {
            Object getterThis;
            Object[] args;
            if (this.getter != null) {
                if (this.getter instanceof MemberBox) {
                    MemberBox nativeGetter = (MemberBox) this.getter;
                    if (nativeGetter.delegateTo == null) {
                        getterThis = start;
                        args = ScriptRuntime.emptyArgs;
                    } else {
                        getterThis = nativeGetter.delegateTo;
                        args = new Object[]{start};
                    }
                    return nativeGetter.invoke(getterThis, args);
                }
                if (this.getter instanceof Function) {
                    Function f = (Function) this.getter;
                    Context cx = Context.getContext();
                    return f.call(cx, f.getParentScope(), start, ScriptRuntime.emptyArgs);
                }
            }
            Object val = this.value;
            if (val instanceof LazilyLoadedCtor) {
                LazilyLoadedCtor initializer = (LazilyLoadedCtor) val;
                try {
                    initializer.init();
                    val = initializer.getValue();
                } finally {
                    this.value = initializer.getValue();
                }
            }
            return val;
        }
    }

    static void checkValidAttributes(int attributes) {
        if ((attributes & (-16)) != 0) {
            throw new IllegalArgumentException(String.valueOf(attributes));
        }
    }

    private static SlotMapContainer createSlotMap(int initialSize) {
        Context cx = Context.getCurrentContext();
        if (cx != null && cx.hasFeature(17)) {
            return new ThreadSafeSlotMapContainer(initialSize);
        }
        return new SlotMapContainer(initialSize);
    }

    public ScriptableObject() {
        this.isExtensible = true;
        this.isSealed = false;
        this.slotMap = createSlotMap(0);
    }

    public ScriptableObject(Scriptable scope, Scriptable prototype) {
        this.isExtensible = true;
        this.isSealed = false;
        if (scope == null) {
            throw new IllegalArgumentException();
        }
        this.parentScopeObject = scope;
        this.prototypeObject = prototype;
        this.slotMap = createSlotMap(0);
    }

    public String getTypeOf() {
        return avoidObjectDetection() ? "undefined" : "object";
    }

    @Override // org.mozilla.javascript.Scriptable
    public boolean has(String name, Scriptable start) {
        return null != this.slotMap.query(name, 0);
    }

    @Override // org.mozilla.javascript.Scriptable
    public boolean has(int index, Scriptable start) {
        if (this.externalData != null) {
            return index < this.externalData.getArrayLength();
        }
        return null != this.slotMap.query(null, index);
    }

    public boolean has(Symbol key, Scriptable start) {
        return null != this.slotMap.query(key, 0);
    }

    @Override // org.mozilla.javascript.Scriptable
    public Object get(String name, Scriptable start) {
        Slot slot = this.slotMap.query(name, 0);
        if (slot == null) {
            return Scriptable.NOT_FOUND;
        }
        return slot.getValue(start);
    }

    @Override // org.mozilla.javascript.Scriptable
    public Object get(int index, Scriptable start) {
        if (this.externalData != null) {
            if (index < this.externalData.getArrayLength()) {
                return this.externalData.getArrayElement(index);
            }
            return Scriptable.NOT_FOUND;
        }
        Slot slot = this.slotMap.query(null, index);
        if (slot == null) {
            return Scriptable.NOT_FOUND;
        }
        return slot.getValue(start);
    }

    public Object get(Symbol key, Scriptable start) {
        Slot slot = this.slotMap.query(key, 0);
        if (slot == null) {
            return Scriptable.NOT_FOUND;
        }
        return slot.getValue(start);
    }

    @Override // org.mozilla.javascript.Scriptable
    public void put(String name, Scriptable start, Object value) {
        if (putImpl(name, 0, start, value)) {
            return;
        }
        if (start == this) {
            throw Kit.codeBug();
        }
        start.put(name, start, value);
    }

    @Override // org.mozilla.javascript.Scriptable
    public void put(int index, Scriptable start, Object value) {
        if (this.externalData != null) {
            if (index < this.externalData.getArrayLength()) {
                this.externalData.setArrayElement(index, value);
                return;
            }
            throw new JavaScriptException(ScriptRuntime.newNativeError(Context.getCurrentContext(), this, TopLevel.NativeErrors.RangeError, new Object[]{"External array index out of bounds "}), null, 0);
        }
        if (putImpl(null, index, start, value)) {
            return;
        }
        if (start == this) {
            throw Kit.codeBug();
        }
        start.put(index, start, value);
    }

    public void put(Symbol key, Scriptable start, Object value) {
        if (putImpl(key, 0, start, value)) {
            return;
        }
        if (start == this) {
            throw Kit.codeBug();
        }
        ensureSymbolScriptable(start).put(key, start, value);
    }

    @Override // org.mozilla.javascript.Scriptable
    public void delete(String name) {
        checkNotSealed(name, 0);
        this.slotMap.remove(name, 0);
    }

    @Override // org.mozilla.javascript.Scriptable
    public void delete(int index) {
        checkNotSealed(null, index);
        this.slotMap.remove(null, index);
    }

    public void delete(Symbol key) {
        checkNotSealed(key, 0);
        this.slotMap.remove(key, 0);
    }

    @Override // org.mozilla.javascript.ConstProperties
    public void putConst(String name, Scriptable start, Object value) {
        if (putConstImpl(name, 0, start, value, 1)) {
            return;
        }
        if (start == this) {
            throw Kit.codeBug();
        }
        if (start instanceof ConstProperties) {
            ((ConstProperties) start).putConst(name, start, value);
        } else {
            start.put(name, start, value);
        }
    }

    @Override // org.mozilla.javascript.ConstProperties
    public void defineConst(String name, Scriptable start) {
        if (putConstImpl(name, 0, start, Undefined.instance, 8)) {
            return;
        }
        if (start == this) {
            throw Kit.codeBug();
        }
        if (start instanceof ConstProperties) {
            ((ConstProperties) start).defineConst(name, start);
        }
    }

    @Override // org.mozilla.javascript.ConstProperties
    public boolean isConst(String name) {
        Slot slot = this.slotMap.query(name, 0);
        return slot != null && (slot.getAttributes() & 5) == 5;
    }

    @Deprecated
    public final int getAttributes(String name, Scriptable start) {
        return getAttributes(name);
    }

    @Deprecated
    public final int getAttributes(int index, Scriptable start) {
        return getAttributes(index);
    }

    @Deprecated
    public final void setAttributes(String name, Scriptable start, int attributes) {
        setAttributes(name, attributes);
    }

    @Deprecated
    public void setAttributes(int index, Scriptable start, int attributes) {
        setAttributes(index, attributes);
    }

    public int getAttributes(String name) {
        return findAttributeSlot(name, 0, SlotAccess.QUERY).getAttributes();
    }

    public int getAttributes(int index) {
        return findAttributeSlot(null, index, SlotAccess.QUERY).getAttributes();
    }

    public int getAttributes(Symbol sym) {
        return findAttributeSlot(sym, SlotAccess.QUERY).getAttributes();
    }

    public void setAttributes(String name, int attributes) {
        checkNotSealed(name, 0);
        findAttributeSlot(name, 0, SlotAccess.MODIFY).setAttributes(attributes);
    }

    public void setAttributes(int index, int attributes) {
        checkNotSealed(null, index);
        findAttributeSlot(null, index, SlotAccess.MODIFY).setAttributes(attributes);
    }

    public void setAttributes(Symbol key, int attributes) {
        checkNotSealed(key, 0);
        findAttributeSlot(key, SlotAccess.MODIFY).setAttributes(attributes);
    }

    public void setGetterOrSetter(String name, int index, Callable getterOrSetter, boolean isSetter) {
        setGetterOrSetter(name, index, getterOrSetter, isSetter, false);
    }

    private void setGetterOrSetter(String name, int index, Callable getterOrSetter, boolean isSetter, boolean force) {
        GetterSlot gslot;
        if (name != null && index != 0) {
            throw new IllegalArgumentException(name);
        }
        if (!force) {
            checkNotSealed(name, index);
        }
        if (isExtensible()) {
            gslot = (GetterSlot) this.slotMap.get(name, index, SlotAccess.MODIFY_GETTER_SETTER);
        } else {
            Slot slot = this.slotMap.query(name, index);
            if (!(slot instanceof GetterSlot)) {
                return;
            } else {
                gslot = (GetterSlot) slot;
            }
        }
        if (!force) {
            int attributes = gslot.getAttributes();
            if ((attributes & 1) != 0) {
                throw Context.reportRuntimeError1("msg.modify.readonly", name);
            }
        }
        if (isSetter) {
            gslot.setter = getterOrSetter;
        } else {
            gslot.getter = getterOrSetter;
        }
        gslot.value = Undefined.instance;
    }

    public Object getGetterOrSetter(String name, int index, boolean isSetter) {
        if (name != null && index != 0) {
            throw new IllegalArgumentException(name);
        }
        Slot slot = this.slotMap.query(name, index);
        if (slot == null) {
            return null;
        }
        if (slot instanceof GetterSlot) {
            GetterSlot gslot = (GetterSlot) slot;
            Object result = isSetter ? gslot.setter : gslot.getter;
            return result != null ? result : Undefined.instance;
        }
        return Undefined.instance;
    }

    protected boolean isGetterOrSetter(String name, int index, boolean setter) {
        Slot slot = this.slotMap.query(name, index);
        if (slot instanceof GetterSlot) {
            if (!setter || ((GetterSlot) slot).setter == null) {
                return (setter || ((GetterSlot) slot).getter == null) ? false : true;
            }
            return true;
        }
        return false;
    }

    void addLazilyInitializedValue(String name, int index, LazilyLoadedCtor init, int attributes) {
        if (name != null && index != 0) {
            throw new IllegalArgumentException(name);
        }
        checkNotSealed(name, index);
        GetterSlot gslot = (GetterSlot) this.slotMap.get(name, index, SlotAccess.MODIFY_GETTER_SETTER);
        gslot.setAttributes(attributes);
        gslot.getter = null;
        gslot.setter = null;
        gslot.value = init;
    }

    public void setExternalArrayData(ExternalArrayData array) {
        this.externalData = array;
        if (array == null) {
            delete(XSDatatype.FACET_LENGTH);
        } else {
            defineProperty(XSDatatype.FACET_LENGTH, null, GET_ARRAY_LENGTH, null, 3);
        }
    }

    public ExternalArrayData getExternalArrayData() {
        return this.externalData;
    }

    public Object getExternalArrayLength() {
        return Integer.valueOf(this.externalData == null ? 0 : this.externalData.getArrayLength());
    }

    @Override // org.mozilla.javascript.Scriptable
    public Scriptable getPrototype() {
        return this.prototypeObject;
    }

    @Override // org.mozilla.javascript.Scriptable
    public void setPrototype(Scriptable m) {
        if (!isExtensible() && Context.getContext().getLanguageVersion() >= 180) {
            throw ScriptRuntime.typeError0("msg.not.extensible");
        }
        this.prototypeObject = m;
    }

    @Override // org.mozilla.javascript.Scriptable
    public Scriptable getParentScope() {
        return this.parentScopeObject;
    }

    @Override // org.mozilla.javascript.Scriptable
    public void setParentScope(Scriptable m) {
        this.parentScopeObject = m;
    }

    @Override // org.mozilla.javascript.Scriptable
    public Object[] getIds() {
        return getIds(false, false);
    }

    @Override // org.mozilla.javascript.debug.DebuggableObject
    public Object[] getAllIds() {
        return getIds(true, false);
    }

    @Override // org.mozilla.javascript.Scriptable
    public Object getDefaultValue(Class<?> typeHint) {
        return getDefaultValue(this, typeHint);
    }

    public static Object getDefaultValue(Scriptable object, Class<?> typeHint) {
        boolean tryToString;
        String methodName;
        Context cx = null;
        int i = 0;
        while (i < 2) {
            if (typeHint == ScriptRuntime.StringClass) {
                tryToString = i == 0;
            } else {
                tryToString = i == 1;
            }
            if (tryToString) {
                methodName = "toString";
            } else {
                methodName = "valueOf";
            }
            Object v = getProperty(object, methodName);
            if (v instanceof Function) {
                Function fun = (Function) v;
                if (cx == null) {
                    cx = Context.getContext();
                }
                Object v2 = fun.call(cx, fun.getParentScope(), object, ScriptRuntime.emptyArgs);
                if (v2 == null) {
                    continue;
                } else {
                    if (!(v2 instanceof Scriptable)) {
                        return v2;
                    }
                    if (typeHint == ScriptRuntime.ScriptableClass || typeHint == ScriptRuntime.FunctionClass) {
                        return v2;
                    }
                    if (tryToString && (v2 instanceof Wrapper)) {
                        Object u = ((Wrapper) v2).unwrap();
                        if (u instanceof String) {
                            return u;
                        }
                    }
                }
            }
            i++;
        }
        String arg = typeHint == null ? "undefined" : typeHint.getName();
        throw ScriptRuntime.typeError1("msg.default.value", arg);
    }

    @Override // org.mozilla.javascript.Scriptable
    public boolean hasInstance(Scriptable instance) {
        return ScriptRuntime.jsDelegatesTo(instance, this);
    }

    public boolean avoidObjectDetection() {
        return false;
    }

    protected Object equivalentValues(Object value) {
        return this == value ? Boolean.TRUE : Scriptable.NOT_FOUND;
    }

    public static <T extends Scriptable> void defineClass(Scriptable scope, Class<T> clazz) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        defineClass(scope, clazz, false, false);
    }

    public static <T extends Scriptable> void defineClass(Scriptable scope, Class<T> clazz, boolean sealed) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        defineClass(scope, clazz, sealed, false);
    }

    public static <T extends Scriptable> String defineClass(Scriptable scope, Class<T> clazz, boolean sealed, boolean mapInheritance) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        BaseFunction ctor = buildClassCtor(scope, clazz, sealed, mapInheritance);
        if (ctor == null) {
            return null;
        }
        String name = ctor.getClassPrototype().getClassName();
        defineProperty(scope, name, ctor, 2);
        return name;
    }

    /* JADX WARN: Code duplicated, block: B:110:0x02da  */
    /* JADX WARN: Code duplicated, block: B:118:0x0305  */
    /* JADX WARN: Code duplicated, block: B:119:0x0311  */
    /* JADX WARN: Code duplicated, block: B:121:0x031b  */
    /* JADX WARN: Code duplicated, block: B:122:0x0327  */
    /* JADX WARN: Code duplicated, block: B:124:0x0331  */
    /* JADX WARN: Code duplicated, block: B:125:0x033d  */
    /* JADX WARN: Code duplicated, block: B:130:0x034f  */
    /* JADX WARN: Code duplicated, block: B:132:0x0359  */
    /* JADX WARN: Code duplicated, block: B:133:0x0360  */
    /* JADX WARN: Code duplicated, block: B:135:0x036a  */
    /* JADX WARN: Code duplicated, block: B:136:0x0371  */
    /* JADX WARN: Code duplicated, block: B:138:0x037b  */
    /* JADX WARN: Code duplicated, block: B:139:0x037f A[PHI: r34
      0x037f: PHI (r34v1 'prefix' java.lang.String) = 
      (r34v0 'prefix' java.lang.String)
      (r34v2 'prefix' java.lang.String)
      (r34v3 'prefix' java.lang.String)
      (r34v4 'prefix' java.lang.String)
     binds: [B:129:0x034c, B:138:0x037b, B:135:0x036a, B:132:0x0359] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:141:0x0387  */
    /* JADX WARN: Code duplicated, block: B:143:0x038e  */
    /* JADX WARN: Code duplicated, block: B:147:0x039a  */
    /* JADX WARN: Code duplicated, block: B:148:0x039f  */
    /* JADX WARN: Code duplicated, block: B:153:0x03c2  */
    /* JADX WARN: Code duplicated, block: B:155:0x03d6  */
    /* JADX WARN: Code duplicated, block: B:157:0x03dd  */
    /* JADX WARN: Code duplicated, block: B:161:0x03f5  */
    /* JADX WARN: Code duplicated, block: B:163:0x0407  */
    /* JADX WARN: Code duplicated, block: B:164:0x040b  */
    /* JADX WARN: Code duplicated, block: B:203:0x03b8 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:204:0x03e5 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:210:0x047a A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:216:0x047a A[SYNTHETIC] */
    static <T extends Scriptable> BaseFunction buildClassCtor(Scriptable scope, Class<T> clazz, boolean sealed, boolean mapInheritance) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Annotation annotation;
        String prefix;
        boolean z;
        boolean isStatic;
        HashSet<String> hashSet;
        HashSet<String> names;
        String propName;
        Method setter;
        int i;
        Object existingProto;
        Method[] methods = FunctionObject.getMethodList(clazz);
        for (Method method : methods) {
            if (method.getName().equals("init")) {
                Class<?>[] parmTypes = method.getParameterTypes();
                if (parmTypes.length == 3 && parmTypes[0] == ScriptRuntime.ContextClass && parmTypes[1] == ScriptRuntime.ScriptableClass && parmTypes[2] == Boolean.TYPE && Modifier.isStatic(method.getModifiers())) {
                    Object[] args = new Object[3];
                    args[0] = Context.getContext();
                    args[1] = scope;
                    args[2] = sealed ? Boolean.TRUE : Boolean.FALSE;
                    method.invoke(null, args);
                    return null;
                }
                if (parmTypes.length == 1 && parmTypes[0] == ScriptRuntime.ScriptableClass && Modifier.isStatic(method.getModifiers())) {
                    method.invoke(null, scope);
                    return null;
                }
            }
        }
        Constructor<?>[] ctors = clazz.getConstructors();
        Constructor<?> protoCtor = null;
        for (int i2 = 0; i2 < ctors.length; i2++) {
            if (ctors[i2].getParameterTypes().length == 0) {
                protoCtor = ctors[i2];
                break;
            }
        }
        if (protoCtor == null) {
            throw Context.reportRuntimeError1("msg.zero.arg.ctor", clazz.getName());
        }
        Scriptable proto = (Scriptable) protoCtor.newInstance(ScriptRuntime.emptyArgs);
        String className = proto.getClassName();
        Object existing = getProperty(getTopLevelScope(scope), className);
        if ((existing instanceof BaseFunction) && (existingProto = ((BaseFunction) existing).getPrototypeProperty()) != null && clazz.equals(existingProto.getClass())) {
            return (BaseFunction) existing;
        }
        Scriptable superProto = null;
        if (mapInheritance) {
            Class<? super T> superClass = clazz.getSuperclass();
            if (ScriptRuntime.ScriptableClass.isAssignableFrom(superClass) && !Modifier.isAbstract(superClass.getModifiers())) {
                Class<? extends Scriptable> superScriptable = extendsScriptable(superClass);
                String name = defineClass(scope, superScriptable, sealed, mapInheritance);
                if (name != null) {
                    superProto = getClassPrototype(scope, name);
                }
            }
        }
        if (superProto == null) {
            superProto = getObjectPrototype(scope);
        }
        proto.setPrototype(superProto);
        Member ctorMember = findAnnotatedMember(methods, JSConstructor.class);
        if (ctorMember == null) {
            ctorMember = findAnnotatedMember(ctors, JSConstructor.class);
        }
        if (ctorMember == null) {
            ctorMember = FunctionObject.findSingleMethod(methods, "jsConstructor");
        }
        if (ctorMember == null) {
            if (ctors.length == 1) {
                ctorMember = ctors[0];
            } else if (ctors.length == 2) {
                if (ctors[0].getParameterTypes().length == 0) {
                    ctorMember = ctors[1];
                } else if (ctors[1].getParameterTypes().length == 0) {
                    ctorMember = ctors[0];
                }
            }
            if (ctorMember == null) {
                throw Context.reportRuntimeError1("msg.ctor.multiple.parms", clazz.getName());
            }
        }
        FunctionObject ctor = new FunctionObject(className, ctorMember, scope);
        if (ctor.isVarArgsMethod()) {
            throw Context.reportRuntimeError1("msg.varargs.ctor", ctorMember.getName());
        }
        ctor.initAsConstructor(scope, proto);
        Method finishInit = null;
        HashSet<String> staticNames = new HashSet<>();
        HashSet<String> instanceNames = new HashSet<>();
        for (Method method2 : methods) {
            if (method2 != ctorMember) {
                String name2 = method2.getName();
                if (name2.equals("finishInit")) {
                    Class<?>[] parmTypes2 = method2.getParameterTypes();
                    if (parmTypes2.length == 3 && parmTypes2[0] == ScriptRuntime.ScriptableClass && parmTypes2[1] == FunctionObject.class && parmTypes2[2] == ScriptRuntime.ScriptableClass && Modifier.isStatic(method2.getModifiers())) {
                        finishInit = method2;
                    } else if (name2.indexOf(36) != -1 && !name2.equals("jsConstructor")) {
                        annotation = null;
                        prefix = null;
                        if (method2.isAnnotationPresent(JSFunction.class)) {
                            annotation = method2.getAnnotation(JSFunction.class);
                        } else if (method2.isAnnotationPresent(JSStaticFunction.class)) {
                            annotation = method2.getAnnotation(JSStaticFunction.class);
                        } else if (method2.isAnnotationPresent(JSGetter.class)) {
                            annotation = method2.getAnnotation(JSGetter.class);
                        } else if (method2.isAnnotationPresent(JSSetter.class)) {
                            continue;
                        }
                        if (annotation != null) {
                            if (name2.startsWith("jsFunction_")) {
                                prefix = "jsFunction_";
                            } else if (name2.startsWith("jsStaticFunction_")) {
                                prefix = "jsStaticFunction_";
                            } else if (name2.startsWith("jsGet_")) {
                                prefix = "jsGet_";
                            } else {
                                continue;
                            }
                            if (annotation instanceof JSStaticFunction) {
                                z = true;
                            } else {
                                z = true;
                            }
                            isStatic = z;
                            if (isStatic) {
                                hashSet = staticNames;
                            } else {
                                hashSet = instanceNames;
                            }
                            names = hashSet;
                            propName = getPropertyName(name2, prefix, annotation);
                            if (names.contains(propName)) {
                                throw Context.reportRuntimeError2("duplicate.defineClass.name", name2, propName);
                            }
                            names.add(propName);
                            if (!(annotation instanceof JSGetter)) {
                                if (!(proto instanceof ScriptableObject)) {
                                    throw Context.reportRuntimeError2("msg.extend.scriptable", proto.getClass().toString(), propName);
                                }
                                setter = findSetterMethod(methods, propName, "jsSet_");
                                if (setter != null) {
                                    i = 0;
                                } else {
                                    i = 1;
                                }
                                int attr = 6 | i;
                                ((ScriptableObject) proto).defineProperty(propName, null, method2, setter, attr);
                            } else {
                                if (!(proto instanceof ScriptableObject)) {
                                    throw Context.reportRuntimeError2("msg.extend.scriptable", proto.getClass().toString(), propName);
                                }
                                setter = findSetterMethod(methods, propName, "jsSet_");
                                if (setter != null) {
                                    i = 0;
                                } else {
                                    i = 1;
                                }
                                int attr2 = 6 | i;
                                ((ScriptableObject) proto).defineProperty(propName, null, method2, setter, attr2);
                            }
                        } else {
                            if ((annotation instanceof JSStaticFunction) || prefix == "jsStaticFunction_") {
                                z = true;
                            } else {
                                z = false;
                            }
                            isStatic = z;
                            if (isStatic) {
                                hashSet = staticNames;
                            } else {
                                hashSet = instanceNames;
                            }
                            names = hashSet;
                            propName = getPropertyName(name2, prefix, annotation);
                            if (names.contains(propName)) {
                                throw Context.reportRuntimeError2("duplicate.defineClass.name", name2, propName);
                            }
                            names.add(propName);
                            if (!(annotation instanceof JSGetter) || prefix == "jsGet_") {
                                if (!(proto instanceof ScriptableObject)) {
                                    throw Context.reportRuntimeError2("msg.extend.scriptable", proto.getClass().toString(), propName);
                                }
                                setter = findSetterMethod(methods, propName, "jsSet_");
                                if (setter != null) {
                                    i = 0;
                                } else {
                                    i = 1;
                                }
                                int attr3 = 6 | i;
                                ((ScriptableObject) proto).defineProperty(propName, null, method2, setter, attr3);
                            } else if (!isStatic || Modifier.isStatic(method2.getModifiers())) {
                                FunctionObject f = new FunctionObject(propName, method2, proto);
                                if (!f.isVarArgsConstructor()) {
                                    defineProperty(isStatic ? ctor : proto, propName, f, 2);
                                    if (sealed) {
                                        f.sealObject();
                                    }
                                } else {
                                    throw Context.reportRuntimeError1("msg.varargs.fun", ctorMember.getName());
                                }
                            } else {
                                throw Context.reportRuntimeError("jsStaticFunction must be used with static method.");
                            }
                        }
                    }
                } else if (name2.indexOf(36) != -1) {
                    continue;
                } else {
                    annotation = null;
                    prefix = null;
                    if (method2.isAnnotationPresent(JSFunction.class)) {
                        annotation = method2.getAnnotation(JSFunction.class);
                    } else if (method2.isAnnotationPresent(JSStaticFunction.class)) {
                        annotation = method2.getAnnotation(JSStaticFunction.class);
                    } else if (method2.isAnnotationPresent(JSGetter.class)) {
                        annotation = method2.getAnnotation(JSGetter.class);
                    } else if (method2.isAnnotationPresent(JSSetter.class)) {
                        continue;
                    }
                    if (annotation != null) {
                        if (name2.startsWith("jsFunction_")) {
                            prefix = "jsFunction_";
                        } else if (name2.startsWith("jsStaticFunction_")) {
                            prefix = "jsStaticFunction_";
                        } else if (name2.startsWith("jsGet_")) {
                            prefix = "jsGet_";
                        } else {
                            continue;
                        }
                        if (annotation instanceof JSStaticFunction) {
                            z = true;
                        } else {
                            z = true;
                        }
                        isStatic = z;
                        if (isStatic) {
                            hashSet = staticNames;
                        } else {
                            hashSet = instanceNames;
                        }
                        names = hashSet;
                        propName = getPropertyName(name2, prefix, annotation);
                        if (names.contains(propName)) {
                            throw Context.reportRuntimeError2("duplicate.defineClass.name", name2, propName);
                        }
                        names.add(propName);
                        if (!(annotation instanceof JSGetter)) {
                            if (!(proto instanceof ScriptableObject)) {
                                throw Context.reportRuntimeError2("msg.extend.scriptable", proto.getClass().toString(), propName);
                            }
                            setter = findSetterMethod(methods, propName, "jsSet_");
                            if (setter != null) {
                                i = 0;
                            } else {
                                i = 1;
                            }
                            int attr4 = 6 | i;
                            ((ScriptableObject) proto).defineProperty(propName, null, method2, setter, attr4);
                        } else {
                            if (!(proto instanceof ScriptableObject)) {
                                throw Context.reportRuntimeError2("msg.extend.scriptable", proto.getClass().toString(), propName);
                            }
                            setter = findSetterMethod(methods, propName, "jsSet_");
                            if (setter != null) {
                                i = 0;
                            } else {
                                i = 1;
                            }
                            int attr5 = 6 | i;
                            ((ScriptableObject) proto).defineProperty(propName, null, method2, setter, attr5);
                        }
                    } else {
                        if (annotation instanceof JSStaticFunction) {
                            z = true;
                        } else {
                            z = true;
                        }
                        isStatic = z;
                        if (isStatic) {
                            hashSet = staticNames;
                        } else {
                            hashSet = instanceNames;
                        }
                        names = hashSet;
                        propName = getPropertyName(name2, prefix, annotation);
                        if (names.contains(propName)) {
                            throw Context.reportRuntimeError2("duplicate.defineClass.name", name2, propName);
                        }
                        names.add(propName);
                        if (!(annotation instanceof JSGetter)) {
                            if (!(proto instanceof ScriptableObject)) {
                                throw Context.reportRuntimeError2("msg.extend.scriptable", proto.getClass().toString(), propName);
                            }
                            setter = findSetterMethod(methods, propName, "jsSet_");
                            if (setter != null) {
                                i = 0;
                            } else {
                                i = 1;
                            }
                            int attr6 = 6 | i;
                            ((ScriptableObject) proto).defineProperty(propName, null, method2, setter, attr6);
                        } else {
                            if (!(proto instanceof ScriptableObject)) {
                                throw Context.reportRuntimeError2("msg.extend.scriptable", proto.getClass().toString(), propName);
                            }
                            setter = findSetterMethod(methods, propName, "jsSet_");
                            if (setter != null) {
                                i = 0;
                            } else {
                                i = 1;
                            }
                            int attr7 = 6 | i;
                            ((ScriptableObject) proto).defineProperty(propName, null, method2, setter, attr7);
                        }
                    }
                }
            }
        }
        if (finishInit != null) {
            Object[] finishArgs = {scope, ctor, proto};
            finishInit.invoke(null, finishArgs);
        }
        if (sealed) {
            ctor.sealObject();
            if (proto instanceof ScriptableObject) {
                ((ScriptableObject) proto).sealObject();
            }
        }
        return ctor;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static Member findAnnotatedMember(AccessibleObject[] accessibleObjectArr, Class<? extends Annotation> annotation) {
        for (Method method : accessibleObjectArr) {
            if (method.isAnnotationPresent(annotation)) {
                return method;
            }
        }
        return null;
    }

    private static Method findSetterMethod(Method[] methods, String name, String prefix) {
        String newStyleName = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
        for (Method method : methods) {
            JSSetter annotation = (JSSetter) method.getAnnotation(JSSetter.class);
            if (annotation != null && (name.equals(annotation.value()) || ("".equals(annotation.value()) && newStyleName.equals(method.getName())))) {
                return method;
            }
        }
        String oldStyleName = prefix + name;
        for (Method method2 : methods) {
            if (oldStyleName.equals(method2.getName())) {
                return method2;
            }
        }
        return null;
    }

    private static String getPropertyName(String methodName, String prefix, Annotation annotation) {
        if (prefix != null) {
            return methodName.substring(prefix.length());
        }
        String propName = null;
        if (annotation instanceof JSGetter) {
            propName = ((JSGetter) annotation).value();
            if ((propName == null || propName.length() == 0) && methodName.length() > 3 && methodName.startsWith("get")) {
                propName = methodName.substring(3);
                if (Character.isUpperCase(propName.charAt(0))) {
                    if (propName.length() == 1) {
                        propName = propName.toLowerCase();
                    } else if (!Character.isUpperCase(propName.charAt(1))) {
                        propName = Character.toLowerCase(propName.charAt(0)) + propName.substring(1);
                    }
                }
            }
        } else if (annotation instanceof JSFunction) {
            propName = ((JSFunction) annotation).value();
        } else if (annotation instanceof JSStaticFunction) {
            propName = ((JSStaticFunction) annotation).value();
        }
        if (propName == null || propName.length() == 0) {
            propName = methodName;
        }
        return propName;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static <T extends Scriptable> Class<T> extendsScriptable(Class<?> cls) {
        if (ScriptRuntime.ScriptableClass.isAssignableFrom(cls)) {
            return cls;
        }
        return null;
    }

    public void defineProperty(String propertyName, Object value, int attributes) {
        checkNotSealed(propertyName, 0);
        put(propertyName, this, value);
        setAttributes(propertyName, attributes);
    }

    public void defineProperty(Symbol key, Object value, int attributes) {
        checkNotSealed(key, 0);
        put(key, this, value);
        setAttributes(key, attributes);
    }

    public static void defineProperty(Scriptable destination, String propertyName, Object value, int attributes) {
        if (!(destination instanceof ScriptableObject)) {
            destination.put(propertyName, destination, value);
        } else {
            ScriptableObject so = (ScriptableObject) destination;
            so.defineProperty(propertyName, value, attributes);
        }
    }

    public static void defineConstProperty(Scriptable destination, String propertyName) {
        if (destination instanceof ConstProperties) {
            ConstProperties cp = (ConstProperties) destination;
            cp.defineConst(propertyName, destination);
        } else {
            defineProperty(destination, propertyName, Undefined.instance, 13);
        }
    }

    public void defineProperty(String propertyName, Class<?> clazz, int attributes) {
        int length = propertyName.length();
        if (length == 0) {
            throw new IllegalArgumentException();
        }
        char[] buf = new char[3 + length];
        propertyName.getChars(0, length, buf, 3);
        buf[3] = Character.toUpperCase(buf[3]);
        buf[0] = 'g';
        buf[1] = 'e';
        buf[2] = 't';
        String getterName = new String(buf);
        buf[0] = 's';
        String setterName = new String(buf);
        Method[] methods = FunctionObject.getMethodList(clazz);
        Method getter = FunctionObject.findSingleMethod(methods, getterName);
        Method setter = FunctionObject.findSingleMethod(methods, setterName);
        if (setter == null) {
            attributes |= 1;
        }
        defineProperty(propertyName, null, getter, setter == null ? null : setter, attributes);
    }

    public void defineProperty(String propertyName, Object delegateTo, Method getter, Method setter, int attributes) {
        boolean delegatedForm;
        boolean delegatedForm2;
        MemberBox getterBox = null;
        if (getter != null) {
            getterBox = new MemberBox(getter);
            if (!Modifier.isStatic(getter.getModifiers())) {
                delegatedForm2 = delegateTo != null;
                getterBox.delegateTo = delegateTo;
            } else {
                delegatedForm2 = true;
                getterBox.delegateTo = Void.TYPE;
            }
            String errorId = null;
            Object[] parmTypes = getter.getParameterTypes();
            if (parmTypes.length == 0) {
                if (delegatedForm2) {
                    errorId = "msg.obj.getter.parms";
                }
            } else if (parmTypes.length == 1) {
                Object argType = parmTypes[0];
                if ((argType != ScriptRuntime.ScriptableClass && argType != ScriptRuntime.ScriptableObjectClass) || !delegatedForm2) {
                    errorId = "msg.bad.getter.parms";
                }
            } else {
                errorId = "msg.bad.getter.parms";
            }
            if (errorId != null) {
                throw Context.reportRuntimeError1(errorId, getter.toString());
            }
        }
        MemberBox setterBox = null;
        if (setter != null) {
            if (setter.getReturnType() != Void.TYPE) {
                throw Context.reportRuntimeError1("msg.setter.return", setter.toString());
            }
            setterBox = new MemberBox(setter);
            if (!Modifier.isStatic(setter.getModifiers())) {
                delegatedForm = delegateTo != null;
                setterBox.delegateTo = delegateTo;
            } else {
                delegatedForm = true;
                setterBox.delegateTo = Void.TYPE;
            }
            String errorId2 = null;
            Object[] parmTypes2 = setter.getParameterTypes();
            if (parmTypes2.length == 1) {
                if (delegatedForm) {
                    errorId2 = "msg.setter2.expected";
                }
            } else if (parmTypes2.length == 2) {
                Object argType2 = parmTypes2[0];
                if (argType2 != ScriptRuntime.ScriptableClass && argType2 != ScriptRuntime.ScriptableObjectClass) {
                    errorId2 = "msg.setter2.parms";
                } else if (!delegatedForm) {
                    errorId2 = "msg.setter1.parms";
                }
            } else {
                errorId2 = "msg.setter.parms";
            }
            if (errorId2 != null) {
                throw Context.reportRuntimeError1(errorId2, setter.toString());
            }
        }
        GetterSlot gslot = (GetterSlot) this.slotMap.get(propertyName, 0, SlotAccess.MODIFY_GETTER_SETTER);
        gslot.setAttributes(attributes);
        gslot.getter = getterBox;
        gslot.setter = setterBox;
    }

    public void defineOwnProperties(Context cx, ScriptableObject props) {
        Object[] ids = props.getIds(false, true);
        ScriptableObject[] descs = new ScriptableObject[ids.length];
        int len = ids.length;
        for (int i = 0; i < len; i++) {
            Object descObj = ScriptRuntime.getObjectElem((Scriptable) props, ids[i], cx);
            ScriptableObject desc = ensureScriptableObject(descObj);
            checkPropertyDefinition(desc);
            descs[i] = desc;
        }
        int len2 = ids.length;
        for (int i2 = 0; i2 < len2; i2++) {
            defineOwnProperty(cx, ids[i2], descs[i2]);
        }
    }

    public void defineOwnProperty(Context cx, Object id, ScriptableObject desc) {
        checkPropertyDefinition(desc);
        defineOwnProperty(cx, id, desc, true);
    }

    protected void defineOwnProperty(Context cx, Object id, ScriptableObject desc, boolean checkValid) {
        int attributes;
        Slot slot = getSlot(cx, id, SlotAccess.QUERY);
        boolean isNew = slot == null;
        if (checkValid) {
            ScriptableObject current = slot == null ? null : slot.getPropertyDescriptor(cx, this);
            checkPropertyChange(id, current, desc);
        }
        boolean isAccessor = isAccessorDescriptor(desc);
        if (slot == null) {
            slot = getSlot(cx, id, isAccessor ? SlotAccess.MODIFY_GETTER_SETTER : SlotAccess.MODIFY);
            attributes = applyDescriptorToAttributeBitset(7, desc);
        } else {
            attributes = applyDescriptorToAttributeBitset(slot.getAttributes(), desc);
        }
        if (isAccessor) {
            if (!(slot instanceof GetterSlot)) {
                slot = getSlot(cx, id, SlotAccess.MODIFY_GETTER_SETTER);
            }
            GetterSlot gslot = (GetterSlot) slot;
            Object getter = getProperty(desc, "get");
            if (getter != NOT_FOUND) {
                gslot.getter = getter;
            }
            Object setter = getProperty(desc, "set");
            if (setter != NOT_FOUND) {
                gslot.setter = setter;
            }
            gslot.value = Undefined.instance;
            gslot.setAttributes(attributes);
            return;
        }
        if ((slot instanceof GetterSlot) && isDataDescriptor(desc)) {
            slot = getSlot(cx, id, SlotAccess.CONVERT_ACCESSOR_TO_DATA);
        }
        Object value = getProperty(desc, ES6Iterator.VALUE_PROPERTY);
        if (value != NOT_FOUND) {
            slot.value = value;
        } else if (isNew) {
            slot.value = Undefined.instance;
        }
        slot.setAttributes(attributes);
    }

    protected void checkPropertyDefinition(ScriptableObject desc) {
        Object getter = getProperty(desc, "get");
        if (getter != NOT_FOUND && getter != Undefined.instance && !(getter instanceof Callable)) {
            throw ScriptRuntime.notFunctionError(getter);
        }
        Object setter = getProperty(desc, "set");
        if (setter != NOT_FOUND && setter != Undefined.instance && !(setter instanceof Callable)) {
            throw ScriptRuntime.notFunctionError(setter);
        }
        if (isDataDescriptor(desc) && isAccessorDescriptor(desc)) {
            throw ScriptRuntime.typeError0("msg.both.data.and.accessor.desc");
        }
    }

    protected void checkPropertyChange(Object id, ScriptableObject current, ScriptableObject desc) {
        if (current == null) {
            if (!isExtensible()) {
                throw ScriptRuntime.typeError0("msg.not.extensible");
            }
            return;
        }
        if (isFalse(current.get("configurable", current))) {
            if (isTrue(getProperty(desc, "configurable"))) {
                throw ScriptRuntime.typeError1("msg.change.configurable.false.to.true", id);
            }
            if (isTrue(current.get("enumerable", current)) != isTrue(getProperty(desc, "enumerable"))) {
                throw ScriptRuntime.typeError1("msg.change.enumerable.with.configurable.false", id);
            }
            boolean isData = isDataDescriptor(desc);
            boolean isAccessor = isAccessorDescriptor(desc);
            if (isData || isAccessor) {
                if (isData && isDataDescriptor(current)) {
                    if (isFalse(current.get("writable", current))) {
                        if (isTrue(getProperty(desc, "writable"))) {
                            throw ScriptRuntime.typeError1("msg.change.writable.false.to.true.with.configurable.false", id);
                        }
                        if (!sameValue(getProperty(desc, ES6Iterator.VALUE_PROPERTY), current.get(ES6Iterator.VALUE_PROPERTY, current))) {
                            throw ScriptRuntime.typeError1("msg.change.value.with.writable.false", id);
                        }
                        return;
                    }
                    return;
                }
                if (isAccessor && isAccessorDescriptor(current)) {
                    if (!sameValue(getProperty(desc, "set"), current.get("set", current))) {
                        throw ScriptRuntime.typeError1("msg.change.setter.with.configurable.false", id);
                    }
                    if (!sameValue(getProperty(desc, "get"), current.get("get", current))) {
                        throw ScriptRuntime.typeError1("msg.change.getter.with.configurable.false", id);
                    }
                    return;
                }
                if (isDataDescriptor(current)) {
                    throw ScriptRuntime.typeError1("msg.change.property.data.to.accessor.with.configurable.false", id);
                }
                throw ScriptRuntime.typeError1("msg.change.property.accessor.to.data.with.configurable.false", id);
            }
        }
    }

    protected static boolean isTrue(Object value) {
        return value != NOT_FOUND && ScriptRuntime.toBoolean(value);
    }

    protected static boolean isFalse(Object value) {
        return !isTrue(value);
    }

    protected boolean sameValue(Object newValue, Object currentValue) {
        if (newValue == NOT_FOUND) {
            return true;
        }
        if (currentValue == NOT_FOUND) {
            currentValue = Undefined.instance;
        }
        if ((currentValue instanceof Number) && (newValue instanceof Number)) {
            double d1 = ((Number) currentValue).doubleValue();
            double d2 = ((Number) newValue).doubleValue();
            if (Double.isNaN(d1) && Double.isNaN(d2)) {
                return true;
            }
            if (d1 == 0.0d && Double.doubleToLongBits(d1) != Double.doubleToLongBits(d2)) {
                return false;
            }
        }
        return ScriptRuntime.shallowEq(currentValue, newValue);
    }

    protected int applyDescriptorToAttributeBitset(int attributes, ScriptableObject desc) {
        Object enumerable = getProperty(desc, "enumerable");
        if (enumerable != NOT_FOUND) {
            attributes = ScriptRuntime.toBoolean(enumerable) ? attributes & (-3) : attributes | 2;
        }
        Object writable = getProperty(desc, "writable");
        if (writable != NOT_FOUND) {
            attributes = ScriptRuntime.toBoolean(writable) ? attributes & (-2) : attributes | 1;
        }
        Object configurable = getProperty(desc, "configurable");
        if (configurable != NOT_FOUND) {
            attributes = ScriptRuntime.toBoolean(configurable) ? attributes & (-5) : attributes | 4;
        }
        return attributes;
    }

    protected boolean isDataDescriptor(ScriptableObject desc) {
        return hasProperty(desc, ES6Iterator.VALUE_PROPERTY) || hasProperty(desc, "writable");
    }

    protected boolean isAccessorDescriptor(ScriptableObject desc) {
        return hasProperty(desc, "get") || hasProperty(desc, "set");
    }

    protected boolean isGenericDescriptor(ScriptableObject desc) {
        return (isDataDescriptor(desc) || isAccessorDescriptor(desc)) ? false : true;
    }

    protected static Scriptable ensureScriptable(Object arg) {
        if (!(arg instanceof Scriptable)) {
            throw ScriptRuntime.typeError1("msg.arg.not.object", ScriptRuntime.typeof(arg));
        }
        return (Scriptable) arg;
    }

    protected static SymbolScriptable ensureSymbolScriptable(Object arg) {
        if (!(arg instanceof SymbolScriptable)) {
            throw ScriptRuntime.typeError1("msg.object.not.symbolscriptable", ScriptRuntime.typeof(arg));
        }
        return (SymbolScriptable) arg;
    }

    protected static ScriptableObject ensureScriptableObject(Object arg) {
        if (!(arg instanceof ScriptableObject)) {
            throw ScriptRuntime.typeError1("msg.arg.not.object", ScriptRuntime.typeof(arg));
        }
        return (ScriptableObject) arg;
    }

    public void defineFunctionProperties(String[] names, Class<?> clazz, int attributes) {
        Method[] methods = FunctionObject.getMethodList(clazz);
        for (String name : names) {
            Method m = FunctionObject.findSingleMethod(methods, name);
            if (m == null) {
                throw Context.reportRuntimeError2("msg.method.not.found", name, clazz.getName());
            }
            FunctionObject f = new FunctionObject(name, m, this);
            defineProperty(name, f, attributes);
        }
    }

    public static Scriptable getObjectPrototype(Scriptable scope) {
        return TopLevel.getBuiltinPrototype(getTopLevelScope(scope), TopLevel.Builtins.Object);
    }

    public static Scriptable getFunctionPrototype(Scriptable scope) {
        return TopLevel.getBuiltinPrototype(getTopLevelScope(scope), TopLevel.Builtins.Function);
    }

    public static Scriptable getGeneratorFunctionPrototype(Scriptable scope) {
        return TopLevel.getBuiltinPrototype(getTopLevelScope(scope), TopLevel.Builtins.GeneratorFunction);
    }

    public static Scriptable getArrayPrototype(Scriptable scope) {
        return TopLevel.getBuiltinPrototype(getTopLevelScope(scope), TopLevel.Builtins.Array);
    }

    public static Scriptable getClassPrototype(Scriptable scope, String className) {
        Object proto;
        Object ctor = getProperty(getTopLevelScope(scope), className);
        if (ctor instanceof BaseFunction) {
            proto = ((BaseFunction) ctor).getPrototypeProperty();
        } else if (ctor instanceof Scriptable) {
            Scriptable ctorObj = (Scriptable) ctor;
            proto = ctorObj.get("prototype", ctorObj);
        } else {
            return null;
        }
        if (proto instanceof Scriptable) {
            return (Scriptable) proto;
        }
        return null;
    }

    public static Scriptable getTopLevelScope(Scriptable obj) {
        while (true) {
            Scriptable parent = obj.getParentScope();
            if (parent == null) {
                return obj;
            }
            obj = parent;
        }
    }

    public boolean isExtensible() {
        return this.isExtensible;
    }

    public void preventExtensions() {
        this.isExtensible = false;
    }

    public void sealObject() {
        if (!this.isSealed) {
            long stamp = this.slotMap.readLock();
            try {
                for (Slot slot : this.slotMap) {
                    Object value = slot.value;
                    if (value instanceof LazilyLoadedCtor) {
                        LazilyLoadedCtor initializer = (LazilyLoadedCtor) value;
                        try {
                            initializer.init();
                            slot.value = initializer.getValue();
                        } catch (Throwable th) {
                            slot.value = initializer.getValue();
                            throw th;
                        }
                    }
                }
                this.isSealed = true;
                this.slotMap.unlockRead(stamp);
            } catch (Throwable th2) {
                this.slotMap.unlockRead(stamp);
                throw th2;
            }
        }
    }

    public final boolean isSealed() {
        return this.isSealed;
    }

    private void checkNotSealed(Object key, int index) {
        if (!isSealed()) {
            return;
        }
        String str = key != null ? key.toString() : Integer.toString(index);
        throw Context.reportRuntimeError1("msg.modify.sealed", str);
    }

    public static Object getProperty(Scriptable obj, String name) {
        Object result;
        do {
            result = obj.get(name, obj);
            if (result != Scriptable.NOT_FOUND) {
                break;
            }
            obj = obj.getPrototype();
        } while (obj != null);
        return result;
    }

    public static Object getProperty(Scriptable obj, Symbol key) {
        Object result;
        do {
            result = ensureSymbolScriptable(obj).get(key, obj);
            if (result != Scriptable.NOT_FOUND) {
                break;
            }
            obj = obj.getPrototype();
        } while (obj != null);
        return result;
    }

    public static <T> T getTypedProperty(Scriptable s, int index, Class<T> type) {
        Object val = getProperty(s, index);
        if (val == Scriptable.NOT_FOUND) {
            val = null;
        }
        return type.cast(Context.jsToJava(val, type));
    }

    public static Object getProperty(Scriptable obj, int index) {
        Object result;
        do {
            result = obj.get(index, obj);
            if (result != Scriptable.NOT_FOUND) {
                break;
            }
            obj = obj.getPrototype();
        } while (obj != null);
        return result;
    }

    public static <T> T getTypedProperty(Scriptable s, String name, Class<T> type) {
        Object val = getProperty(s, name);
        if (val == Scriptable.NOT_FOUND) {
            val = null;
        }
        return type.cast(Context.jsToJava(val, type));
    }

    public static boolean hasProperty(Scriptable obj, String name) {
        return null != getBase(obj, name);
    }

    public static void redefineProperty(Scriptable obj, String name, boolean isConst) {
        Scriptable base = getBase(obj, name);
        if (base == null) {
            return;
        }
        if (base instanceof ConstProperties) {
            ConstProperties cp = (ConstProperties) base;
            if (cp.isConst(name)) {
                throw ScriptRuntime.typeError1("msg.const.redecl", name);
            }
        }
        if (isConst) {
            throw ScriptRuntime.typeError1("msg.var.redecl", name);
        }
    }

    public static boolean hasProperty(Scriptable obj, int index) {
        return null != getBase(obj, index);
    }

    public static boolean hasProperty(Scriptable obj, Symbol key) {
        return null != getBase(obj, key);
    }

    public static void putProperty(Scriptable obj, String name, Object value) {
        Scriptable base = getBase(obj, name);
        if (base == null) {
            base = obj;
        }
        base.put(name, obj, value);
    }

    public static void putProperty(Scriptable obj, Symbol key, Object value) {
        Scriptable base = getBase(obj, key);
        if (base == null) {
            base = obj;
        }
        ensureSymbolScriptable(base).put(key, obj, value);
    }

    public static void putConstProperty(Scriptable obj, String name, Object value) {
        Scriptable base = getBase(obj, name);
        if (base == null) {
            base = obj;
        }
        if (base instanceof ConstProperties) {
            ((ConstProperties) base).putConst(name, obj, value);
        }
    }

    public static void putProperty(Scriptable obj, int index, Object value) {
        Scriptable base = getBase(obj, index);
        if (base == null) {
            base = obj;
        }
        base.put(index, obj, value);
    }

    public static boolean deleteProperty(Scriptable obj, String name) {
        Scriptable base = getBase(obj, name);
        if (base == null) {
            return true;
        }
        base.delete(name);
        return !base.has(name, obj);
    }

    public static boolean deleteProperty(Scriptable obj, int index) {
        Scriptable base = getBase(obj, index);
        if (base == null) {
            return true;
        }
        base.delete(index);
        return !base.has(index, obj);
    }

    public static Object[] getPropertyIds(Scriptable obj) {
        if (obj == null) {
            return ScriptRuntime.emptyArgs;
        }
        Object[] result = obj.getIds();
        ObjToIntMap map = null;
        while (true) {
            obj = obj.getPrototype();
            if (obj == null) {
                break;
            }
            Object[] ids = obj.getIds();
            if (ids.length != 0) {
                if (map == null) {
                    if (result.length == 0) {
                        result = ids;
                    } else {
                        map = new ObjToIntMap(result.length + ids.length);
                        for (int i = 0; i != result.length; i++) {
                            map.intern(result[i]);
                        }
                        result = null;
                    }
                }
                for (int i2 = 0; i2 != ids.length; i2++) {
                    map.intern(ids[i2]);
                }
            }
        }
        if (map != null) {
            result = map.getKeys();
        }
        return result;
    }

    public static Object callMethod(Scriptable obj, String methodName, Object[] args) {
        return callMethod(null, obj, methodName, args);
    }

    public static Object callMethod(Context cx, Scriptable obj, String methodName, Object[] args) {
        Object funObj = getProperty(obj, methodName);
        if (!(funObj instanceof Function)) {
            throw ScriptRuntime.notFunctionError(obj, methodName);
        }
        Function fun = (Function) funObj;
        Scriptable scope = getTopLevelScope(obj);
        if (cx != null) {
            return fun.call(cx, scope, obj, args);
        }
        return Context.call(null, fun, scope, obj, args);
    }

    private static Scriptable getBase(Scriptable obj, String name) {
        while (!obj.has(name, obj)) {
            obj = obj.getPrototype();
            if (obj == null) {
                break;
            }
        }
        return obj;
    }

    private static Scriptable getBase(Scriptable obj, int index) {
        while (!obj.has(index, obj)) {
            obj = obj.getPrototype();
            if (obj == null) {
                break;
            }
        }
        return obj;
    }

    private static Scriptable getBase(Scriptable obj, Symbol key) {
        while (!ensureSymbolScriptable(obj).has(key, obj)) {
            obj = obj.getPrototype();
            if (obj == null) {
                break;
            }
        }
        return obj;
    }

    public final Object getAssociatedValue(Object key) {
        Map<Object, Object> h = this.associatedValues;
        if (h == null) {
            return null;
        }
        return h.get(key);
    }

    public static Object getTopScopeValue(Scriptable scope, Object key) {
        Scriptable scope2 = getTopLevelScope(scope);
        do {
            if (scope2 instanceof ScriptableObject) {
                ScriptableObject so = (ScriptableObject) scope2;
                Object value = so.getAssociatedValue(key);
                if (value != null) {
                    return value;
                }
            }
            scope2 = scope2.getPrototype();
        } while (scope2 != null);
        return null;
    }

    public final synchronized Object associateValue(Object key, Object value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
        Map<Object, Object> h = this.associatedValues;
        if (h == null) {
            h = new HashMap();
            this.associatedValues = h;
        }
        return Kit.initHash(h, key, value);
    }

    private boolean putImpl(Object key, int index, Scriptable start, Object value) {
        Slot slot;
        if (this != start) {
            slot = this.slotMap.query(key, index);
            if (!this.isExtensible && ((slot == null || (!(slot instanceof GetterSlot) && (slot.getAttributes() & 1) != 0)) && Context.getContext().isStrictMode())) {
                throw ScriptRuntime.typeError0("msg.not.extensible");
            }
            if (slot == null) {
                return false;
            }
        } else if (!this.isExtensible) {
            slot = this.slotMap.query(key, index);
            if ((slot == null || (!(slot instanceof GetterSlot) && (slot.getAttributes() & 1) != 0)) && Context.getContext().isStrictMode()) {
                throw ScriptRuntime.typeError0("msg.not.extensible");
            }
            if (slot == null) {
                return true;
            }
        } else {
            if (this.isSealed) {
                checkNotSealed(key, index);
            }
            slot = this.slotMap.get(key, index, SlotAccess.MODIFY);
        }
        return slot.setValue(value, this, start);
    }

    private boolean putConstImpl(String name, int index, Scriptable start, Object value, int constFlag) {
        Slot slot;
        if (!$assertionsDisabled && constFlag == 0) {
            throw new AssertionError();
        }
        if (!this.isExtensible) {
            Context cx = Context.getContext();
            if (cx.isStrictMode()) {
                throw ScriptRuntime.typeError0("msg.not.extensible");
            }
        }
        if (this != start) {
            slot = this.slotMap.query(name, index);
            if (slot == null) {
                return false;
            }
        } else if (!isExtensible()) {
            slot = this.slotMap.query(name, index);
            if (slot == null) {
                return true;
            }
        } else {
            checkNotSealed(name, index);
            Slot slot2 = this.slotMap.get(name, index, SlotAccess.MODIFY_CONST);
            int attr = slot2.getAttributes();
            if ((attr & 1) == 0) {
                throw Context.reportRuntimeError1("msg.var.redecl", name);
            }
            if ((attr & 8) != 0) {
                slot2.value = value;
                if (constFlag != 8) {
                    slot2.setAttributes(attr & (-9));
                    return true;
                }
                return true;
            }
            return true;
        }
        return slot.setValue(value, this, start);
    }

    private Slot findAttributeSlot(String name, int index, SlotAccess accessType) {
        Slot slot = this.slotMap.get(name, index, accessType);
        if (slot == null) {
            String str = name != null ? name : Integer.toString(index);
            throw Context.reportRuntimeError1("msg.prop.not.found", str);
        }
        return slot;
    }

    private Slot findAttributeSlot(Symbol key, SlotAccess accessType) {
        Slot slot = this.slotMap.get(key, 0, accessType);
        if (slot == null) {
            throw Context.reportRuntimeError1("msg.prop.not.found", key);
        }
        return slot;
    }

    Object[] getIds(boolean getNonEnumerable, boolean getSymbols) {
        Object[] a;
        Object[] result;
        int externalLen = this.externalData == null ? 0 : this.externalData.getArrayLength();
        if (externalLen == 0) {
            a = ScriptRuntime.emptyArgs;
        } else {
            a = new Object[externalLen];
            for (int i = 0; i < externalLen; i++) {
                a[i] = Integer.valueOf(i);
            }
        }
        if (this.slotMap.isEmpty()) {
            return a;
        }
        int c = externalLen;
        long stamp = this.slotMap.readLock();
        try {
            for (Slot slot : this.slotMap) {
                if ((getNonEnumerable || (slot.getAttributes() & 2) == 0) && (getSymbols || !(slot.name instanceof Symbol))) {
                    if (c == externalLen) {
                        Object[] oldA = a;
                        a = new Object[this.slotMap.dirtySize() + externalLen];
                        if (oldA != null) {
                            System.arraycopy(oldA, 0, a, 0, externalLen);
                        }
                    }
                    int i2 = c;
                    c++;
                    a[i2] = slot.name != null ? slot.name : Integer.valueOf(slot.indexOrHash);
                }
            }
            this.slotMap.unlockRead(stamp);
            if (c == a.length + externalLen) {
                result = a;
            } else {
                result = new Object[c];
                System.arraycopy(a, 0, result, 0, c);
            }
            Context cx = Context.getCurrentContext();
            if (cx != null && cx.hasFeature(16)) {
                Arrays.sort(result, KEY_COMPARATOR);
            }
            return result;
        } catch (Throwable th) {
            this.slotMap.unlockRead(stamp);
            throw th;
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        long stamp = this.slotMap.readLock();
        try {
            int objectsCount = this.slotMap.dirtySize();
            if (objectsCount == 0) {
                out.writeInt(0);
            } else {
                out.writeInt(objectsCount);
                for (Slot slot : this.slotMap) {
                    out.writeObject(slot);
                }
            }
        } finally {
            this.slotMap.unlockRead(stamp);
        }
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        int tableSize = in.readInt();
        this.slotMap = createSlotMap(tableSize);
        for (int i = 0; i < tableSize; i++) {
            Slot slot = (Slot) in.readObject();
            this.slotMap.addSlot(slot);
        }
    }

    protected ScriptableObject getOwnPropertyDescriptor(Context cx, Object id) {
        Slot slot = getSlot(cx, id, SlotAccess.QUERY);
        if (slot == null) {
            return null;
        }
        Scriptable scope = getParentScope();
        return slot.getPropertyDescriptor(cx, scope == null ? this : scope);
    }

    protected Slot getSlot(Context cx, Object id, SlotAccess accessType) {
        if (id instanceof Symbol) {
            return this.slotMap.get(id, 0, accessType);
        }
        ScriptRuntime.StringIdOrIndex s = ScriptRuntime.toStringIdOrIndex(cx, id);
        if (s.stringId == null) {
            return this.slotMap.get(null, s.index, accessType);
        }
        return this.slotMap.get(s.stringId, 0, accessType);
    }

    public int size() {
        return this.slotMap.size();
    }

    public boolean isEmpty() {
        return this.slotMap.isEmpty();
    }

    public Object get(Object key) {
        Object value = null;
        if (key instanceof String) {
            value = get((String) key, this);
        } else if (key instanceof Symbol) {
            value = get((Symbol) key, this);
        } else if (key instanceof Number) {
            value = get(((Number) key).intValue(), this);
        }
        if (value == Scriptable.NOT_FOUND || value == Undefined.instance) {
            return null;
        }
        if (value instanceof Wrapper) {
            return ((Wrapper) value).unwrap();
        }
        return value;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ScriptableObject$KeyComparator.class */
    public static final class KeyComparator implements Comparator<Object>, Serializable {
        private static final long serialVersionUID = 6411335891523988149L;

        @Override // java.util.Comparator
        public int compare(Object o1, Object o2) {
            int i1;
            int i2;
            if (o1 instanceof Integer) {
                if (!(o2 instanceof Integer) || (i1 = ((Integer) o1).intValue()) < (i2 = ((Integer) o2).intValue())) {
                    return -1;
                }
                if (i1 > i2) {
                    return 1;
                }
                return 0;
            }
            if (o2 instanceof Integer) {
                return 1;
            }
            return 0;
        }
    }
}
