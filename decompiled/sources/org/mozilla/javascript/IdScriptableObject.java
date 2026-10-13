package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/IdScriptableObject.class */
public abstract class IdScriptableObject extends ScriptableObject implements IdFunctionCall {
    private static final long serialVersionUID = -3744239272168621609L;
    private transient PrototypeValues prototypeValues;

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/IdScriptableObject$PrototypeValues.class */
    private static final class PrototypeValues implements Serializable {
        private static final long serialVersionUID = 3038645279153854371L;
        private static final int NAME_SLOT = 1;
        private static final int SLOT_SPAN = 2;
        private IdScriptableObject obj;
        private int maxId;
        private Object[] valueArray;
        private short[] attributeArray;
        int constructorId;
        private IdFunctionObject constructor;
        private short constructorAttrs;

        PrototypeValues(IdScriptableObject obj, int maxId) {
            if (obj == null) {
                throw new IllegalArgumentException();
            }
            if (maxId < 1) {
                throw new IllegalArgumentException();
            }
            this.obj = obj;
            this.maxId = maxId;
        }

        final int getMaxId() {
            return this.maxId;
        }

        final void initValue(int id, String name, Object value, int attributes) {
            if (1 > id || id > this.maxId) {
                throw new IllegalArgumentException();
            }
            if (name == null) {
                throw new IllegalArgumentException();
            }
            if (value == Scriptable.NOT_FOUND) {
                throw new IllegalArgumentException();
            }
            ScriptableObject.checkValidAttributes(attributes);
            if (this.obj.findPrototypeId(name) != id) {
                throw new IllegalArgumentException(name);
            }
            if (id == this.constructorId) {
                if (!(value instanceof IdFunctionObject)) {
                    throw new IllegalArgumentException("consructor should be initialized with IdFunctionObject");
                }
                this.constructor = (IdFunctionObject) value;
                this.constructorAttrs = (short) attributes;
                return;
            }
            initSlot(id, name, value, attributes);
        }

        final void initValue(int id, Symbol key, Object value, int attributes) {
            if (1 > id || id > this.maxId) {
                throw new IllegalArgumentException();
            }
            if (key == null) {
                throw new IllegalArgumentException();
            }
            if (value == Scriptable.NOT_FOUND) {
                throw new IllegalArgumentException();
            }
            ScriptableObject.checkValidAttributes(attributes);
            if (this.obj.findPrototypeId(key) != id) {
                throw new IllegalArgumentException(key.toString());
            }
            if (id == this.constructorId) {
                if (!(value instanceof IdFunctionObject)) {
                    throw new IllegalArgumentException("consructor should be initialized with IdFunctionObject");
                }
                this.constructor = (IdFunctionObject) value;
                this.constructorAttrs = (short) attributes;
                return;
            }
            initSlot(id, key, value, attributes);
        }

        private void initSlot(int id, Object name, Object value, int attributes) {
            Object[] array = this.valueArray;
            if (array == null) {
                throw new IllegalStateException();
            }
            if (value == null) {
                value = UniqueTag.NULL_VALUE;
            }
            int index = (id - 1) * 2;
            synchronized (this) {
                Object value2 = array[index];
                if (value2 == null) {
                    array[index] = value;
                    array[index + 1] = name;
                    this.attributeArray[id - 1] = (short) attributes;
                } else if (!name.equals(array[index + 1])) {
                    throw new IllegalStateException();
                }
            }
        }

        final IdFunctionObject createPrecachedConstructor() {
            if (this.constructorId != 0) {
                throw new IllegalStateException();
            }
            this.constructorId = this.obj.findPrototypeId("constructor");
            if (this.constructorId == 0) {
                throw new IllegalStateException("No id for constructor property");
            }
            this.obj.initPrototypeId(this.constructorId);
            if (this.constructor == null) {
                throw new IllegalStateException(this.obj.getClass().getName() + ".initPrototypeId() did not initialize id=" + this.constructorId);
            }
            this.constructor.initFunction(this.obj.getClassName(), ScriptableObject.getTopLevelScope(this.obj));
            this.constructor.markAsConstructor(this.obj);
            return this.constructor;
        }

        final int findId(String name) {
            return this.obj.findPrototypeId(name);
        }

        final int findId(Symbol key) {
            return this.obj.findPrototypeId(key);
        }

        final boolean has(int id) {
            Object[] array = this.valueArray;
            if (array == null) {
                return true;
            }
            int valueSlot = (id - 1) * 2;
            Object value = array[valueSlot];
            return value == null || value != Scriptable.NOT_FOUND;
        }

        final Object get(int id) {
            Object value = ensureId(id);
            if (value == UniqueTag.NULL_VALUE) {
                value = null;
            }
            return value;
        }

        final void set(int id, Scriptable start, Object value) {
            if (value == Scriptable.NOT_FOUND) {
                throw new IllegalArgumentException();
            }
            ensureId(id);
            if ((this.attributeArray[id - 1] & 1) == 0) {
                if (start == this.obj) {
                    if (value == null) {
                        value = UniqueTag.NULL_VALUE;
                    }
                    int valueSlot = (id - 1) * 2;
                    synchronized (this) {
                        this.valueArray[valueSlot] = value;
                    }
                    return;
                }
                int nameSlot = ((id - 1) * 2) + 1;
                Object name = this.valueArray[nameSlot];
                if (name instanceof Symbol) {
                    if (start instanceof SymbolScriptable) {
                        ((SymbolScriptable) start).put((Symbol) name, start, value);
                        return;
                    }
                    return;
                }
                start.put((String) name, start, value);
            }
        }

        final void delete(int id) {
            ensureId(id);
            if ((this.attributeArray[id - 1] & 4) != 0) {
                Context cx = Context.getContext();
                if (cx.isStrictMode()) {
                    int nameSlot = ((id - 1) * 2) + 1;
                    String name = (String) this.valueArray[nameSlot];
                    throw ScriptRuntime.typeError1("msg.delete.property.with.configurable.false", name);
                }
                return;
            }
            int valueSlot = (id - 1) * 2;
            synchronized (this) {
                this.valueArray[valueSlot] = Scriptable.NOT_FOUND;
                this.attributeArray[id - 1] = 0;
            }
        }

        final int getAttributes(int id) {
            ensureId(id);
            return this.attributeArray[id - 1];
        }

        final void setAttributes(int id, int attributes) {
            ScriptableObject.checkValidAttributes(attributes);
            ensureId(id);
            synchronized (this) {
                this.attributeArray[id - 1] = (short) attributes;
            }
        }

        final Object[] getNames(boolean getAll, boolean getSymbols, Object[] extraEntries) {
            Object[] names = null;
            int count = 0;
            for (int id = 1; id <= this.maxId; id++) {
                Object value = ensureId(id);
                if ((getAll || (this.attributeArray[id - 1] & 2) == 0) && value != Scriptable.NOT_FOUND) {
                    int nameSlot = ((id - 1) * 2) + 1;
                    Object name = this.valueArray[nameSlot];
                    if (name instanceof String) {
                        if (names == null) {
                            names = new Object[this.maxId];
                        }
                        int i = count;
                        count++;
                        names[i] = name;
                    } else if (getSymbols && (name instanceof Symbol)) {
                        if (names == null) {
                            names = new Object[this.maxId];
                        }
                        int i2 = count;
                        count++;
                        names[i2] = name.toString();
                    }
                }
            }
            if (count == 0) {
                return extraEntries;
            }
            if (extraEntries == null || extraEntries.length == 0) {
                if (count != names.length) {
                    Object[] tmp = new Object[count];
                    System.arraycopy(names, 0, tmp, 0, count);
                    names = tmp;
                }
                return names;
            }
            int extra = extraEntries.length;
            Object[] tmp2 = new Object[extra + count];
            System.arraycopy(extraEntries, 0, tmp2, 0, extra);
            System.arraycopy(names, 0, tmp2, extra, count);
            return tmp2;
        }

        private Object ensureId(int id) {
            Object[] array = this.valueArray;
            if (array == null) {
                synchronized (this) {
                    array = this.valueArray;
                    if (array == null) {
                        array = new Object[this.maxId * 2];
                        this.valueArray = array;
                        this.attributeArray = new short[this.maxId];
                    }
                }
            }
            int valueSlot = (id - 1) * 2;
            Object value = array[valueSlot];
            if (value == null) {
                if (id == this.constructorId) {
                    initSlot(this.constructorId, "constructor", this.constructor, this.constructorAttrs);
                    this.constructor = null;
                } else {
                    this.obj.initPrototypeId(id);
                }
                value = array[valueSlot];
                if (value == null) {
                    throw new IllegalStateException(this.obj.getClass().getName() + ".initPrototypeId(int id) did not initialize id=" + id);
                }
            }
            return value;
        }
    }

    public IdScriptableObject() {
    }

    public IdScriptableObject(Scriptable scope, Scriptable prototype) {
        super(scope, prototype);
    }

    protected final boolean defaultHas(String name) {
        return super.has(name, this);
    }

    protected final Object defaultGet(String name) {
        return super.get(name, this);
    }

    protected final void defaultPut(String name, Object value) {
        super.put(name, this, value);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public boolean has(String name, Scriptable start) {
        int id;
        int info = findInstanceIdInfo(name);
        if (info != 0) {
            int attr = info >>> 16;
            if ((attr & 4) != 0) {
                return true;
            }
            return NOT_FOUND != getInstanceIdValue(info & 65535);
        }
        if (this.prototypeValues != null && (id = this.prototypeValues.findId(name)) != 0) {
            return this.prototypeValues.has(id);
        }
        return super.has(name, start);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.SymbolScriptable
    public boolean has(Symbol key, Scriptable start) {
        int id;
        int info = findInstanceIdInfo(key);
        if (info != 0) {
            int attr = info >>> 16;
            if ((attr & 4) != 0) {
                return true;
            }
            return NOT_FOUND != getInstanceIdValue(info & 65535);
        }
        if (this.prototypeValues != null && (id = this.prototypeValues.findId(key)) != 0) {
            return this.prototypeValues.has(id);
        }
        return super.has(key, start);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public Object get(String name, Scriptable start) {
        int id;
        Object value;
        Object value2;
        Object value3 = super.get(name, start);
        if (value3 != NOT_FOUND) {
            return value3;
        }
        int info = findInstanceIdInfo(name);
        if (info == 0 || (value2 = getInstanceIdValue(info & 65535)) == NOT_FOUND) {
            return (this.prototypeValues == null || (id = this.prototypeValues.findId(name)) == 0 || (value = this.prototypeValues.get(id)) == NOT_FOUND) ? NOT_FOUND : value;
        }
        return value2;
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.SymbolScriptable
    public Object get(Symbol key, Scriptable start) {
        int id;
        Object value;
        Object value2;
        Object value3 = super.get(key, start);
        if (value3 != NOT_FOUND) {
            return value3;
        }
        int info = findInstanceIdInfo(key);
        if (info == 0 || (value2 = getInstanceIdValue(info & 65535)) == NOT_FOUND) {
            return (this.prototypeValues == null || (id = this.prototypeValues.findId(key)) == 0 || (value = this.prototypeValues.get(id)) == NOT_FOUND) ? NOT_FOUND : value;
        }
        return value2;
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public void put(String name, Scriptable start, Object value) {
        int id;
        int info = findInstanceIdInfo(name);
        if (info != 0) {
            if (start == this && isSealed()) {
                throw Context.reportRuntimeError1("msg.modify.sealed", name);
            }
            int attr = info >>> 16;
            if ((attr & 1) == 0) {
                if (start == this) {
                    setInstanceIdValue(info & 65535, value);
                    return;
                } else {
                    start.put(name, start, value);
                    return;
                }
            }
            return;
        }
        if (this.prototypeValues != null && (id = this.prototypeValues.findId(name)) != 0) {
            if (start == this && isSealed()) {
                throw Context.reportRuntimeError1("msg.modify.sealed", name);
            }
            this.prototypeValues.set(id, start, value);
            return;
        }
        super.put(name, start, value);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.SymbolScriptable
    public void put(Symbol key, Scriptable start, Object value) {
        int id;
        int info = findInstanceIdInfo(key);
        if (info != 0) {
            if (start == this && isSealed()) {
                throw Context.reportRuntimeError0("msg.modify.sealed");
            }
            int attr = info >>> 16;
            if ((attr & 1) == 0) {
                if (start == this) {
                    setInstanceIdValue(info & 65535, value);
                    return;
                } else {
                    ensureSymbolScriptable(start).put(key, start, value);
                    return;
                }
            }
            return;
        }
        if (this.prototypeValues != null && (id = this.prototypeValues.findId(key)) != 0) {
            if (start == this && isSealed()) {
                throw Context.reportRuntimeError0("msg.modify.sealed");
            }
            this.prototypeValues.set(id, start, value);
            return;
        }
        super.put(key, start, value);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public void delete(String name) {
        int id;
        int info = findInstanceIdInfo(name);
        if (info != 0 && !isSealed()) {
            int attr = info >>> 16;
            if ((attr & 4) != 0) {
                Context cx = Context.getContext();
                if (cx.isStrictMode()) {
                    throw ScriptRuntime.typeError1("msg.delete.property.with.configurable.false", name);
                }
                return;
            }
            setInstanceIdValue(info & 65535, NOT_FOUND);
            return;
        }
        if (this.prototypeValues != null && (id = this.prototypeValues.findId(name)) != 0) {
            if (!isSealed()) {
                this.prototypeValues.delete(id);
                return;
            }
            return;
        }
        super.delete(name);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.SymbolScriptable
    public void delete(Symbol key) {
        int id;
        int info = findInstanceIdInfo(key);
        if (info != 0 && !isSealed()) {
            int attr = info >>> 16;
            if ((attr & 4) != 0) {
                Context cx = Context.getContext();
                if (cx.isStrictMode()) {
                    throw ScriptRuntime.typeError0("msg.delete.property.with.configurable.false");
                }
                return;
            }
            setInstanceIdValue(info & 65535, NOT_FOUND);
            return;
        }
        if (this.prototypeValues != null && (id = this.prototypeValues.findId(key)) != 0) {
            if (!isSealed()) {
                this.prototypeValues.delete(id);
                return;
            }
            return;
        }
        super.delete(key);
    }

    @Override // org.mozilla.javascript.ScriptableObject
    public int getAttributes(String name) {
        int id;
        int info = findInstanceIdInfo(name);
        if (info != 0) {
            int attr = info >>> 16;
            return attr;
        }
        if (this.prototypeValues != null && (id = this.prototypeValues.findId(name)) != 0) {
            return this.prototypeValues.getAttributes(id);
        }
        return super.getAttributes(name);
    }

    @Override // org.mozilla.javascript.ScriptableObject
    public int getAttributes(Symbol key) {
        int id;
        int info = findInstanceIdInfo(key);
        if (info != 0) {
            int attr = info >>> 16;
            return attr;
        }
        if (this.prototypeValues != null && (id = this.prototypeValues.findId(key)) != 0) {
            return this.prototypeValues.getAttributes(id);
        }
        return super.getAttributes(key);
    }

    @Override // org.mozilla.javascript.ScriptableObject
    public void setAttributes(String name, int attributes) {
        int id;
        ScriptableObject.checkValidAttributes(attributes);
        int info = findInstanceIdInfo(name);
        if (info != 0) {
            int id2 = info & 65535;
            int currentAttributes = info >>> 16;
            if (attributes != currentAttributes) {
                setInstanceIdAttributes(id2, attributes);
                return;
            }
            return;
        }
        if (this.prototypeValues != null && (id = this.prototypeValues.findId(name)) != 0) {
            this.prototypeValues.setAttributes(id, attributes);
        } else {
            super.setAttributes(name, attributes);
        }
    }

    @Override // org.mozilla.javascript.ScriptableObject
    Object[] getIds(boolean getNonEnumerable, boolean getSymbols) {
        Object[] result = super.getIds(getNonEnumerable, getSymbols);
        if (this.prototypeValues != null) {
            result = this.prototypeValues.getNames(getNonEnumerable, getSymbols, result);
        }
        int maxInstanceId = getMaxInstanceId();
        if (maxInstanceId != 0) {
            Object[] ids = null;
            int count = 0;
            for (int id = maxInstanceId; id != 0; id--) {
                String name = getInstanceIdName(id);
                int info = findInstanceIdInfo(name);
                if (info != 0) {
                    int attr = info >>> 16;
                    if (((attr & 4) != 0 || NOT_FOUND != getInstanceIdValue(id)) && (getNonEnumerable || (attr & 2) == 0)) {
                        if (count == 0) {
                            ids = new Object[id];
                        }
                        int i = count;
                        count++;
                        ids[i] = name;
                    }
                }
            }
            if (count != 0) {
                if (result.length == 0 && ids.length == count) {
                    result = ids;
                } else {
                    Object[] tmp = new Object[result.length + count];
                    System.arraycopy(result, 0, tmp, 0, result.length);
                    System.arraycopy(ids, 0, tmp, result.length, count);
                    result = tmp;
                }
            }
        }
        return result;
    }

    protected int getMaxInstanceId() {
        return 0;
    }

    protected static int instanceIdInfo(int attributes, int id) {
        return (attributes << 16) | id;
    }

    protected int findInstanceIdInfo(String name) {
        return 0;
    }

    protected int findInstanceIdInfo(Symbol key) {
        return 0;
    }

    protected String getInstanceIdName(int id) {
        throw new IllegalArgumentException(String.valueOf(id));
    }

    protected Object getInstanceIdValue(int id) {
        throw new IllegalStateException(String.valueOf(id));
    }

    protected void setInstanceIdValue(int id, Object value) {
        throw new IllegalStateException(String.valueOf(id));
    }

    protected void setInstanceIdAttributes(int id, int attr) {
        throw ScriptRuntime.constructError("InternalError", "Changing attributes not supported for " + getClassName() + " " + getInstanceIdName(id) + " property");
    }

    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        throw f.unknown();
    }

    public final IdFunctionObject exportAsJSClass(int maxPrototypeId, Scriptable scope, boolean sealed) {
        if (scope != this && scope != null) {
            setParentScope(scope);
            setPrototype(getObjectPrototype(scope));
        }
        activatePrototypeMap(maxPrototypeId);
        IdFunctionObject ctor = this.prototypeValues.createPrecachedConstructor();
        if (sealed) {
            sealObject();
        }
        fillConstructorProperties(ctor);
        if (sealed) {
            ctor.sealObject();
        }
        ctor.exportAsScopeProperty();
        return ctor;
    }

    public final boolean hasPrototypeMap() {
        return this.prototypeValues != null;
    }

    public final void activatePrototypeMap(int maxPrototypeId) {
        PrototypeValues values = new PrototypeValues(this, maxPrototypeId);
        synchronized (this) {
            if (this.prototypeValues != null) {
                throw new IllegalStateException();
            }
            this.prototypeValues = values;
        }
    }

    public final IdFunctionObject initPrototypeMethod(Object tag, int id, String name, int arity) {
        return initPrototypeMethod(tag, id, name, name, arity);
    }

    public final IdFunctionObject initPrototypeMethod(Object tag, int id, String propertyName, String functionName, int arity) {
        Scriptable scope = ScriptableObject.getTopLevelScope(this);
        IdFunctionObject function = newIdFunction(tag, id, functionName != null ? functionName : propertyName, arity, scope);
        this.prototypeValues.initValue(id, propertyName, function, 2);
        return function;
    }

    public final IdFunctionObject initPrototypeMethod(Object tag, int id, Symbol key, String functionName, int arity) {
        Scriptable scope = ScriptableObject.getTopLevelScope(this);
        IdFunctionObject function = newIdFunction(tag, id, functionName, arity, scope);
        this.prototypeValues.initValue(id, key, function, 2);
        return function;
    }

    public final void initPrototypeConstructor(IdFunctionObject f) {
        int id = this.prototypeValues.constructorId;
        if (id == 0) {
            throw new IllegalStateException();
        }
        if (f.methodId() != id) {
            throw new IllegalArgumentException();
        }
        if (isSealed()) {
            f.sealObject();
        }
        this.prototypeValues.initValue(id, "constructor", f, 2);
    }

    public final void initPrototypeValue(int id, String name, Object value, int attributes) {
        this.prototypeValues.initValue(id, name, value, attributes);
    }

    public final void initPrototypeValue(int id, Symbol key, Object value, int attributes) {
        this.prototypeValues.initValue(id, key, value, attributes);
    }

    protected void initPrototypeId(int id) {
        throw new IllegalStateException(String.valueOf(id));
    }

    protected int findPrototypeId(String name) {
        throw new IllegalStateException(name);
    }

    protected int findPrototypeId(Symbol key) {
        return 0;
    }

    protected void fillConstructorProperties(IdFunctionObject ctor) {
    }

    protected void addIdFunctionProperty(Scriptable obj, Object tag, int id, String name, int arity) {
        Scriptable scope = ScriptableObject.getTopLevelScope(obj);
        IdFunctionObject f = newIdFunction(tag, id, name, arity, scope);
        f.addAsProperty(obj);
    }

    protected static EcmaError incompatibleCallError(IdFunctionObject f) {
        throw ScriptRuntime.typeError1("msg.incompat.call", f.getFunctionName());
    }

    private IdFunctionObject newIdFunction(Object tag, int id, String name, int arity, Scriptable scope) {
        IdFunctionObject function;
        if (Context.getContext().getLanguageVersion() < 200) {
            function = new IdFunctionObject(this, tag, id, name, arity, scope);
        } else {
            function = new IdFunctionObjectES6(this, tag, id, name, arity, scope);
        }
        if (isSealed()) {
            function.sealObject();
        }
        return function;
    }

    @Override // org.mozilla.javascript.ScriptableObject
    public void defineOwnProperty(Context cx, Object key, ScriptableObject desc) {
        int id;
        if (key instanceof String) {
            String name = (String) key;
            int info = findInstanceIdInfo(name);
            if (info != 0) {
                int id2 = info & 65535;
                if (isAccessorDescriptor(desc)) {
                    delete(id2);
                } else {
                    checkPropertyDefinition(desc);
                    ScriptableObject current = getOwnPropertyDescriptor(cx, key);
                    checkPropertyChange(name, current, desc);
                    int attr = info >>> 16;
                    Object value = getProperty(desc, ES6Iterator.VALUE_PROPERTY);
                    if (value != NOT_FOUND && (attr & 1) == 0) {
                        Object currentValue = getInstanceIdValue(id2);
                        if (!sameValue(value, currentValue)) {
                            setInstanceIdValue(id2, value);
                        }
                    }
                    setAttributes(name, applyDescriptorToAttributeBitset(attr, desc));
                    return;
                }
            }
            if (this.prototypeValues != null && (id = this.prototypeValues.findId(name)) != 0) {
                if (isAccessorDescriptor(desc)) {
                    this.prototypeValues.delete(id);
                } else {
                    checkPropertyDefinition(desc);
                    ScriptableObject current2 = getOwnPropertyDescriptor(cx, key);
                    checkPropertyChange(name, current2, desc);
                    int attr2 = this.prototypeValues.getAttributes(id);
                    Object value2 = getProperty(desc, ES6Iterator.VALUE_PROPERTY);
                    if (value2 != NOT_FOUND && (attr2 & 1) == 0) {
                        Object currentValue2 = this.prototypeValues.get(id);
                        if (!sameValue(value2, currentValue2)) {
                            this.prototypeValues.set(id, this, value2);
                        }
                    }
                    this.prototypeValues.setAttributes(id, applyDescriptorToAttributeBitset(attr2, desc));
                    if (super.has(name, this)) {
                        super.delete(name);
                        return;
                    }
                    return;
                }
            }
        }
        super.defineOwnProperty(cx, key, desc);
    }

    @Override // org.mozilla.javascript.ScriptableObject
    protected ScriptableObject getOwnPropertyDescriptor(Context cx, Object id) {
        ScriptableObject desc = super.getOwnPropertyDescriptor(cx, id);
        if (desc == null) {
            if (id instanceof String) {
                desc = getBuiltInDescriptor((String) id);
            } else if (ScriptRuntime.isSymbol(id)) {
                desc = getBuiltInDescriptor(((NativeSymbol) id).getKey());
            }
        }
        return desc;
    }

    private ScriptableObject getBuiltInDescriptor(String name) {
        int id;
        Scriptable scope = getParentScope();
        if (scope == null) {
            scope = this;
        }
        int info = findInstanceIdInfo(name);
        if (info != 0) {
            Object value = getInstanceIdValue(info & 65535);
            int attr = info >>> 16;
            return buildDataDescriptor(scope, value, attr);
        }
        if (this.prototypeValues != null && (id = this.prototypeValues.findId(name)) != 0) {
            Object value2 = this.prototypeValues.get(id);
            int attr2 = this.prototypeValues.getAttributes(id);
            return buildDataDescriptor(scope, value2, attr2);
        }
        return null;
    }

    private ScriptableObject getBuiltInDescriptor(Symbol key) {
        int id;
        Scriptable scope = getParentScope();
        if (scope == null) {
            scope = this;
        }
        if (this.prototypeValues != null && (id = this.prototypeValues.findId(key)) != 0) {
            Object value = this.prototypeValues.get(id);
            int attr = this.prototypeValues.getAttributes(id);
            return buildDataDescriptor(scope, value, attr);
        }
        return null;
    }

    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        stream.defaultReadObject();
        int maxPrototypeId = stream.readInt();
        if (maxPrototypeId != 0) {
            activatePrototypeMap(maxPrototypeId);
        }
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        int maxPrototypeId = 0;
        if (this.prototypeValues != null) {
            maxPrototypeId = this.prototypeValues.getMaxId();
        }
        stream.writeInt(maxPrototypeId);
    }
}
