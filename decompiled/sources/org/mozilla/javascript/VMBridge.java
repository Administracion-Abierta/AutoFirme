package org.mozilla.javascript;

import java.lang.reflect.AccessibleObject;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/VMBridge.class */
public abstract class VMBridge {
    static final VMBridge instance = makeInstance();

    protected abstract Object getThreadContextHelper();

    protected abstract Context getContext(Object obj);

    protected abstract void setContext(Object obj, Context context);

    protected abstract boolean tryToMakeAccessible(AccessibleObject accessibleObject);

    protected abstract Object getInterfaceProxyHelper(ContextFactory contextFactory, Class<?>[] clsArr);

    protected abstract Object newInterfaceProxy(Object obj, ContextFactory contextFactory, InterfaceAdapter interfaceAdapter, Object obj2, Scriptable scriptable);

    private static VMBridge makeInstance() {
        VMBridge bridge;
        String[] classNames = {"org.mozilla.javascript.VMBridge_custom", "org.mozilla.javascript.jdk18.VMBridge_jdk18"};
        for (int i = 0; i != classNames.length; i++) {
            String className = classNames[i];
            Class<?> cl = Kit.classOrNull(className);
            if (cl != null && (bridge = (VMBridge) Kit.newInstanceOrNull(cl)) != null) {
                return bridge;
            }
        }
        throw new IllegalStateException("Failed to create VMBridge instance");
    }
}
