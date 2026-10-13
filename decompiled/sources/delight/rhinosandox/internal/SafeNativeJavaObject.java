package delight.rhinosandox.internal;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

/* JADX INFO: loaded from: autofirma.jar:delight/rhinosandox/internal/SafeNativeJavaObject.class */
public class SafeNativeJavaObject extends NativeJavaObject {
    public SafeNativeJavaObject(Scriptable scope, Object javaObject, Class<?> staticType) {
        super(scope, javaObject, staticType);
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public Object get(String name, Scriptable start) {
        boolean _equals = "getClass".equals(name);
        if (_equals) {
            return Scriptable.NOT_FOUND;
        }
        return super.get(name, start);
    }
}
