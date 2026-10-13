package delight.rhinosandox.internal;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

/* JADX INFO: loaded from: autofirma.jar:delight/rhinosandox/internal/SafeWrapFactory.class */
public class SafeWrapFactory extends WrapFactory {
    @Override // org.mozilla.javascript.WrapFactory
    public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
        return new SafeNativeJavaObject(scope, javaObject, staticType);
    }
}
