package org.mozilla.javascript;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import java.lang.reflect.Array;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeJavaArray.class */
public class NativeJavaArray extends NativeJavaObject implements SymbolScriptable {
    private static final long serialVersionUID = -924022554283675333L;
    Object array;
    int length;
    Class<?> cls;

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return "JavaArray";
    }

    public static NativeJavaArray wrap(Scriptable scope, Object array) {
        return new NativeJavaArray(scope, array);
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Wrapper
    public Object unwrap() {
        return this.array;
    }

    public NativeJavaArray(Scriptable scope, Object array) {
        super(scope, null, ScriptRuntime.ObjectClass);
        Class<?> cl = array.getClass();
        if (!cl.isArray()) {
            throw new RuntimeException("Array expected");
        }
        this.array = array;
        this.length = Array.getLength(array);
        this.cls = cl.getComponentType();
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public boolean has(String id, Scriptable start) {
        return id.equals(XSDatatype.FACET_LENGTH) || super.has(id, start);
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public boolean has(int index, Scriptable start) {
        return 0 <= index && index < this.length;
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.SymbolScriptable
    public boolean has(Symbol key, Scriptable start) {
        return SymbolKey.IS_CONCAT_SPREADABLE.equals(key);
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public Object get(String id, Scriptable start) {
        if (id.equals(XSDatatype.FACET_LENGTH)) {
            return Integer.valueOf(this.length);
        }
        Object result = super.get(id, start);
        if (result == NOT_FOUND && !ScriptableObject.hasProperty(getPrototype(), id)) {
            throw Context.reportRuntimeError2("msg.java.member.not.found", this.array.getClass().getName(), id);
        }
        return result;
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public Object get(int index, Scriptable start) {
        if (0 <= index && index < this.length) {
            Context cx = Context.getContext();
            Object obj = Array.get(this.array, index);
            return cx.getWrapFactory().wrap(cx, this, obj, this.cls);
        }
        return Undefined.instance;
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.SymbolScriptable
    public Object get(Symbol key, Scriptable start) {
        if (SymbolKey.IS_CONCAT_SPREADABLE.equals(key)) {
            return Boolean.TRUE;
        }
        return Scriptable.NOT_FOUND;
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public void put(String id, Scriptable start, Object value) {
        if (!id.equals(XSDatatype.FACET_LENGTH)) {
            throw Context.reportRuntimeError1("msg.java.array.member.not.found", id);
        }
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public void put(int index, Scriptable start, Object value) {
        if (0 <= index && index < this.length) {
            Array.set(this.array, index, Context.jsToJava(value, this.cls));
            return;
        }
        throw Context.reportRuntimeError2("msg.java.array.index.out.of.bounds", String.valueOf(index), String.valueOf(this.length - 1));
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.SymbolScriptable
    public void delete(Symbol key) {
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public Object getDefaultValue(Class<?> hint) {
        if (hint == null || hint == ScriptRuntime.StringClass) {
            return this.array.toString();
        }
        if (hint == ScriptRuntime.BooleanClass) {
            return Boolean.TRUE;
        }
        if (hint == ScriptRuntime.NumberClass) {
            return ScriptRuntime.NaNobj;
        }
        return this;
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public Object[] getIds() {
        Object[] result = new Object[this.length];
        int i = this.length;
        while (true) {
            i--;
            if (i >= 0) {
                result[i] = Integer.valueOf(i);
            } else {
                return result;
            }
        }
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public boolean hasInstance(Scriptable value) {
        if (!(value instanceof Wrapper)) {
            return false;
        }
        Object instance = ((Wrapper) value).unwrap();
        return this.cls.isInstance(instance);
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public Scriptable getPrototype() {
        if (this.prototype == null) {
            this.prototype = ScriptableObject.getArrayPrototype(getParentScope());
        }
        return this.prototype;
    }
}
