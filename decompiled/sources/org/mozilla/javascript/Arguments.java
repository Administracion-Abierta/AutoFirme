package org.mozilla.javascript;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/Arguments.class */
final class Arguments extends IdScriptableObject {
    private static final long serialVersionUID = 4275508002492040609L;
    private static final String FTAG = "Arguments";
    private static final int Id_callee = 1;
    private static final int Id_length = 2;
    private static final int Id_caller = 3;
    private static final int MAX_INSTANCE_ID = 3;
    private static BaseFunction iteratorMethod = new BaseFunction() { // from class: org.mozilla.javascript.Arguments.1
        private static final long serialVersionUID = 4239122318596177391L;

        @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.Function, org.mozilla.javascript.Callable
        public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
            return new NativeArrayIterator(scope, thisObj, NativeArrayIterator.ARRAY_ITERATOR_TYPE.VALUES);
        }
    };
    private Object callerObj;
    private Object calleeObj;
    private Object lengthObj;
    private int callerAttr = 2;
    private int calleeAttr = 2;
    private int lengthAttr = 2;
    private NativeCall activation;
    private Object[] args;

    public Arguments(NativeCall activation) {
        this.activation = activation;
        Scriptable parent = activation.getParentScope();
        setParentScope(parent);
        setPrototype(ScriptableObject.getObjectPrototype(parent));
        this.args = activation.originalArgs;
        this.lengthObj = Integer.valueOf(this.args.length);
        NativeFunction f = activation.function;
        this.calleeObj = f;
        int version = f.getLanguageVersion();
        if (version <= 130 && version != 0) {
            this.callerObj = null;
        } else {
            this.callerObj = NOT_FOUND;
        }
        defineProperty(SymbolKey.ITERATOR, iteratorMethod, 2);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return FTAG;
    }

    private Object arg(int index) {
        return (index < 0 || this.args.length <= index) ? NOT_FOUND : this.args[index];
    }

    private void putIntoActivation(int index, Object value) {
        String argName = this.activation.function.getParamOrVarName(index);
        this.activation.put(argName, this.activation, value);
    }

    private Object getFromActivation(int index) {
        String argName = this.activation.function.getParamOrVarName(index);
        return this.activation.get(argName, this.activation);
    }

    private void replaceArg(int index, Object value) {
        if (sharedWithActivation(index)) {
            putIntoActivation(index, value);
        }
        synchronized (this) {
            if (this.args == this.activation.originalArgs) {
                this.args = (Object[]) this.args.clone();
            }
            this.args[index] = value;
        }
    }

    private void removeArg(int index) {
        synchronized (this) {
            if (this.args[index] != NOT_FOUND) {
                if (this.args == this.activation.originalArgs) {
                    this.args = (Object[]) this.args.clone();
                }
                this.args[index] = NOT_FOUND;
            }
        }
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public boolean has(int index, Scriptable start) {
        if (arg(index) != NOT_FOUND) {
            return true;
        }
        return super.has(index, start);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public Object get(int index, Scriptable start) {
        Object value = arg(index);
        if (value == NOT_FOUND) {
            return super.get(index, start);
        }
        if (sharedWithActivation(index)) {
            return getFromActivation(index);
        }
        return value;
    }

    private boolean sharedWithActivation(int index) {
        NativeFunction f;
        int definedCount;
        Context cx = Context.getContext();
        if (!cx.isStrictMode() && index < (definedCount = (f = this.activation.function).getParamCount())) {
            if (index < definedCount - 1) {
                String argName = f.getParamOrVarName(index);
                for (int i = index + 1; i < definedCount; i++) {
                    if (argName.equals(f.getParamOrVarName(i))) {
                        return false;
                    }
                }
                return true;
            }
            return true;
        }
        return false;
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public void put(int index, Scriptable start, Object value) {
        if (arg(index) == NOT_FOUND) {
            super.put(index, start, value);
        } else {
            replaceArg(index, value);
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public void put(String name, Scriptable start, Object value) {
        super.put(name, start, value);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public void delete(int index) {
        if (0 <= index && index < this.args.length) {
            removeArg(index);
        }
        super.delete(index);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int getMaxInstanceId() {
        return 3;
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findInstanceIdInfo(String s) {
        int attr;
        int id = 0;
        String X = null;
        int s_length = s.length();
        if (s_length == 6) {
            int c = s.charAt(5);
            if (c == 101) {
                X = "callee";
                id = 1;
            } else if (c == 104) {
                X = XSDatatype.FACET_LENGTH;
                id = 2;
            } else if (c == 114) {
                X = "caller";
                id = 3;
            }
        }
        if (X != null && X != s && !X.equals(s)) {
            id = 0;
        }
        Context cx = Context.getContext();
        if (cx.isStrictMode() && (id == 1 || id == 3)) {
            return super.findInstanceIdInfo(s);
        }
        if (id == 0) {
            return super.findInstanceIdInfo(s);
        }
        switch (id) {
            case 1:
                attr = this.calleeAttr;
                break;
            case 2:
                attr = this.lengthAttr;
                break;
            case 3:
                attr = this.callerAttr;
                break;
            default:
                throw new IllegalStateException();
        }
        return instanceIdInfo(attr, id);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected String getInstanceIdName(int id) {
        switch (id) {
            case 1:
                return "callee";
            case 2:
                return XSDatatype.FACET_LENGTH;
            case 3:
                return "caller";
            default:
                return null;
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected Object getInstanceIdValue(int id) {
        NativeCall caller;
        switch (id) {
            case 1:
                return this.calleeObj;
            case 2:
                return this.lengthObj;
            case 3:
                Object value = this.callerObj;
                if (value == UniqueTag.NULL_VALUE) {
                    value = null;
                } else if (value == null && (caller = this.activation.parentActivationCall) != null) {
                    value = caller.get("arguments", caller);
                }
                return value;
            default:
                return super.getInstanceIdValue(id);
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void setInstanceIdValue(int id, Object value) {
        switch (id) {
            case 1:
                this.calleeObj = value;
                break;
            case 2:
                this.lengthObj = value;
                break;
            case 3:
                this.callerObj = value != null ? value : UniqueTag.NULL_VALUE;
                break;
            default:
                super.setInstanceIdValue(id, value);
                break;
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void setInstanceIdAttributes(int id, int attr) {
        switch (id) {
            case 1:
                this.calleeAttr = attr;
                break;
            case 2:
                this.lengthAttr = attr;
                break;
            case 3:
                this.callerAttr = attr;
                break;
            default:
                super.setInstanceIdAttributes(id, attr);
                break;
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.ScriptableObject
    Object[] getIds(boolean getNonEnumerable, boolean getSymbols) {
        int index;
        Object[] ids = super.getIds(getNonEnumerable, getSymbols);
        if (this.args.length != 0) {
            boolean[] present = new boolean[this.args.length];
            int extraCount = this.args.length;
            for (int i = 0; i != ids.length; i++) {
                Object id = ids[i];
                if ((id instanceof Integer) && 0 <= (index = ((Integer) id).intValue()) && index < this.args.length && !present[index]) {
                    present[index] = true;
                    extraCount--;
                }
            }
            if (!getNonEnumerable) {
                for (int i2 = 0; i2 < present.length; i2++) {
                    if (!present[i2] && super.has(i2, this)) {
                        present[i2] = true;
                        extraCount--;
                    }
                }
            }
            if (extraCount != 0) {
                Object[] tmp = new Object[extraCount + ids.length];
                System.arraycopy(ids, 0, tmp, extraCount, ids.length);
                ids = tmp;
                int offset = 0;
                for (int i3 = 0; i3 != this.args.length; i3++) {
                    if (!present[i3]) {
                        ids[offset] = Integer.valueOf(i3);
                        offset++;
                    }
                }
                if (offset != extraCount) {
                    Kit.codeBug();
                }
            }
        }
        return ids;
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.ScriptableObject
    protected ScriptableObject getOwnPropertyDescriptor(Context cx, Object id) {
        if (ScriptRuntime.isSymbol(id) || (id instanceof Scriptable)) {
            return super.getOwnPropertyDescriptor(cx, id);
        }
        double d = ScriptRuntime.toNumber(id);
        int index = (int) d;
        if (d != index) {
            return super.getOwnPropertyDescriptor(cx, id);
        }
        Object value = arg(index);
        if (value == NOT_FOUND) {
            return super.getOwnPropertyDescriptor(cx, id);
        }
        if (sharedWithActivation(index)) {
            value = getFromActivation(index);
        }
        if (super.has(index, this)) {
            ScriptableObject ownPropertyDescriptor = super.getOwnPropertyDescriptor(cx, id);
            ownPropertyDescriptor.put(ES6Iterator.VALUE_PROPERTY, ownPropertyDescriptor, value);
            return ownPropertyDescriptor;
        }
        Scriptable scope = getParentScope();
        if (scope == null) {
            scope = this;
        }
        return buildDataDescriptor(scope, value, 0);
    }

    @Override // org.mozilla.javascript.ScriptableObject
    protected void defineOwnProperty(Context cx, Object id, ScriptableObject desc, boolean checkValid) {
        super.defineOwnProperty(cx, id, desc, checkValid);
        if (ScriptRuntime.isSymbol(id)) {
            return;
        }
        double d = ScriptRuntime.toNumber(id);
        int index = (int) d;
        if (d != index) {
            return;
        }
        Object value = arg(index);
        if (value == NOT_FOUND) {
            return;
        }
        if (isAccessorDescriptor(desc)) {
            removeArg(index);
            return;
        }
        Object newValue = getProperty(desc, ES6Iterator.VALUE_PROPERTY);
        if (newValue == NOT_FOUND) {
            return;
        }
        replaceArg(index, newValue);
        if (isFalse(getProperty(desc, "writable"))) {
            removeArg(index);
        }
    }

    void defineAttributesForStrictMode() {
        Context cx = Context.getContext();
        if (!cx.isStrictMode()) {
            return;
        }
        setGetterOrSetter("caller", 0, new ThrowTypeError("caller"), true);
        setGetterOrSetter("caller", 0, new ThrowTypeError("caller"), false);
        setGetterOrSetter("callee", 0, new ThrowTypeError("callee"), true);
        setGetterOrSetter("callee", 0, new ThrowTypeError("callee"), false);
        setAttributes("caller", 6);
        setAttributes("callee", 6);
        this.callerObj = null;
        this.calleeObj = null;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/Arguments$ThrowTypeError.class */
    private static class ThrowTypeError extends BaseFunction {
        private static final long serialVersionUID = -744615873947395749L;
        private String propertyName;

        ThrowTypeError(String propertyName) {
            this.propertyName = propertyName;
        }

        @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.Function, org.mozilla.javascript.Callable
        public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
            throw ScriptRuntime.typeError1("msg.arguments.not.access.strict", this.propertyName);
        }
    }
}
