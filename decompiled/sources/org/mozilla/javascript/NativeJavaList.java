package org.mozilla.javascript;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeJavaList.class */
public class NativeJavaList extends NativeJavaObject {
    private List<Object> list;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !NativeJavaList.class.desiredAssertionStatus();
    }

    public NativeJavaList(Scriptable scope, Object list) {
        super(scope, list, list.getClass());
        if (!$assertionsDisabled && !(list instanceof List)) {
            throw new AssertionError();
        }
        this.list = (List) list;
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return "JavaList";
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public boolean has(String name, Scriptable start) {
        if (name.equals(XSDatatype.FACET_LENGTH)) {
            return true;
        }
        return super.has(name, start);
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public boolean has(int index, Scriptable start) {
        if (isWithValidIndex(index)) {
            return true;
        }
        return super.has(index, start);
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.SymbolScriptable
    public boolean has(Symbol key, Scriptable start) {
        if (SymbolKey.IS_CONCAT_SPREADABLE.equals(key)) {
            return true;
        }
        return super.has(key, start);
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public Object get(String name, Scriptable start) {
        if (XSDatatype.FACET_LENGTH.equals(name)) {
            return Integer.valueOf(this.list.size());
        }
        return super.get(name, start);
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public Object get(int index, Scriptable start) {
        if (isWithValidIndex(index)) {
            Context cx = Context.getContext();
            Object obj = this.list.get(index);
            return cx.getWrapFactory().wrap(cx, this, obj, obj.getClass());
        }
        return Undefined.instance;
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.SymbolScriptable
    public Object get(Symbol key, Scriptable start) {
        if (SymbolKey.IS_CONCAT_SPREADABLE.equals(key)) {
            return Boolean.TRUE;
        }
        return super.get(key, start);
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public void put(int index, Scriptable start, Object value) {
        if (isWithValidIndex(index)) {
            this.list.set(index, Context.jsToJava(value, Object.class));
        } else {
            super.put(index, start, value);
        }
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public Object[] getIds() {
        List<?> list = (List) this.javaObject;
        Object[] result = new Object[list.size()];
        int i = list.size();
        while (true) {
            i--;
            if (i >= 0) {
                result[i] = Integer.valueOf(i);
            } else {
                return result;
            }
        }
    }

    private boolean isWithValidIndex(int index) {
        return index >= 0 && index < this.list.size();
    }
}
