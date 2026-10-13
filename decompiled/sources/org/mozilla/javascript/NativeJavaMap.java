package org.mozilla.javascript;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeJavaMap.class */
public class NativeJavaMap extends NativeJavaObject {
    private Map<Object, Object> map;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !NativeJavaMap.class.desiredAssertionStatus();
    }

    public NativeJavaMap(Scriptable scope, Object map) {
        super(scope, map, map.getClass());
        if (!$assertionsDisabled && !(map instanceof Map)) {
            throw new AssertionError();
        }
        this.map = (Map) map;
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return "JavaMap";
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public boolean has(String name, Scriptable start) {
        if (this.map.containsKey(name)) {
            return true;
        }
        return super.has(name, start);
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public boolean has(int index, Scriptable start) {
        if (this.map.containsKey(Integer.valueOf(index))) {
            return true;
        }
        return super.has(index, start);
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public Object get(String name, Scriptable start) {
        if (this.map.containsKey(name)) {
            Context cx = Context.getContext();
            Object obj = this.map.get(name);
            return cx.getWrapFactory().wrap(cx, this, obj, obj.getClass());
        }
        return super.get(name, start);
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public Object get(int index, Scriptable start) {
        if (this.map.containsKey(Integer.valueOf(index))) {
            Context cx = Context.getContext();
            Object obj = this.map.get(Integer.valueOf(index));
            return cx.getWrapFactory().wrap(cx, this, obj, obj.getClass());
        }
        return super.get(index, start);
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public void put(String name, Scriptable start, Object value) {
        this.map.put(name, Context.jsToJava(value, Object.class));
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public void put(int index, Scriptable start, Object value) {
        this.map.put(Integer.valueOf(index), Context.jsToJava(value, Object.class));
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public Object[] getIds() {
        List<Object> ids = new ArrayList<>(this.map.size());
        for (Object key : this.map.keySet()) {
            if (key instanceof Integer) {
                ids.add((Integer) key);
            } else {
                ids.add(ScriptRuntime.toString(key));
            }
        }
        return ids.toArray();
    }
}
