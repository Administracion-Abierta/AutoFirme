package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/RhinoSecurityManager.class */
public class RhinoSecurityManager extends SecurityManager {
    protected Class<?> getCurrentScriptClass() {
        Class<?>[] context = getClassContext();
        for (Class<?> c : context) {
            if ((c != InterpretedFunction.class && NativeFunction.class.isAssignableFrom(c)) || PolicySecurityController.SecureCaller.class.isAssignableFrom(c)) {
                return c;
            }
        }
        return null;
    }
}
