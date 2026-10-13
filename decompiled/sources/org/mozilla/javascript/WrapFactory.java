package org.mozilla.javascript;

import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/WrapFactory.class */
public class WrapFactory {
    private boolean javaPrimitiveWrap = true;

    public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
        if (obj == null || obj == Undefined.instance || (obj instanceof Scriptable)) {
            return obj;
        }
        if (staticType != null && staticType.isPrimitive()) {
            if (staticType == Void.TYPE) {
                return Undefined.instance;
            }
            if (staticType == Character.TYPE) {
                return Integer.valueOf(((Character) obj).charValue());
            }
            return obj;
        }
        if (!isJavaPrimitiveWrap()) {
            if ((obj instanceof String) || (obj instanceof Boolean) || (obj instanceof Integer) || (obj instanceof Short) || (obj instanceof Long) || (obj instanceof Float) || (obj instanceof Double)) {
                return obj;
            }
            if (obj instanceof Character) {
                return String.valueOf(((Character) obj).charValue());
            }
        }
        Class<?> cls = obj.getClass();
        if (cls.isArray()) {
            return NativeJavaArray.wrap(scope, obj);
        }
        return wrapAsJavaObject(cx, scope, obj, staticType);
    }

    public Scriptable wrapNewObject(Context cx, Scriptable scope, Object obj) {
        if (obj instanceof Scriptable) {
            return (Scriptable) obj;
        }
        Class<?> cls = obj.getClass();
        if (cls.isArray()) {
            return NativeJavaArray.wrap(scope, obj);
        }
        return wrapAsJavaObject(cx, scope, obj, null);
    }

    public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
        if (List.class.isAssignableFrom(javaObject.getClass())) {
            return new NativeJavaList(scope, javaObject);
        }
        if (Map.class.isAssignableFrom(javaObject.getClass())) {
            return new NativeJavaMap(scope, javaObject);
        }
        return new NativeJavaObject(scope, javaObject, staticType);
    }

    public Scriptable wrapJavaClass(Context cx, Scriptable scope, Class<?> javaClass) {
        return new NativeJavaClass(scope, javaClass);
    }

    public final boolean isJavaPrimitiveWrap() {
        return this.javaPrimitiveWrap;
    }

    public final void setJavaPrimitiveWrap(boolean value) {
        Context cx = Context.getCurrentContext();
        if (cx != null && cx.isSealed()) {
            Context.onSealedMutation();
        }
        this.javaPrimitiveWrap = value;
    }
}
