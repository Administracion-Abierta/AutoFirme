package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/SecurityController.class */
public abstract class SecurityController {
    private static SecurityController global;

    public abstract GeneratedClassLoader createClassLoader(ClassLoader classLoader, Object obj);

    public abstract Object getDynamicSecurityDomain(Object obj);

    static SecurityController global() {
        return global;
    }

    public static boolean hasGlobal() {
        return global != null;
    }

    public static void initGlobal(SecurityController controller) {
        if (controller == null) {
            throw new IllegalArgumentException();
        }
        if (global != null) {
            throw new SecurityException("Cannot overwrite already installed global SecurityController");
        }
        global = controller;
    }

    public static GeneratedClassLoader createLoader(ClassLoader parent, Object staticDomain) {
        GeneratedClassLoader loader;
        Context cx = Context.getContext();
        if (parent == null) {
            parent = cx.getApplicationClassLoader();
        }
        SecurityController sc = cx.getSecurityController();
        if (sc == null) {
            loader = cx.createClassLoader(parent);
        } else {
            Object dynamicDomain = sc.getDynamicSecurityDomain(staticDomain);
            loader = sc.createClassLoader(parent, dynamicDomain);
        }
        return loader;
    }

    public static Class<?> getStaticSecurityDomainClass() {
        SecurityController sc = Context.getContext().getSecurityController();
        if (sc == null) {
            return null;
        }
        return sc.getStaticSecurityDomainClassInternal();
    }

    public Class<?> getStaticSecurityDomainClassInternal() {
        return null;
    }

    public Object callWithDomain(Object securityDomain, Context cx, final Callable callable, Scriptable scope, final Scriptable thisObj, final Object[] args) {
        return execWithDomain(cx, scope, new Script() { // from class: org.mozilla.javascript.SecurityController.1
            @Override // org.mozilla.javascript.Script
            public Object exec(Context cx2, Scriptable scope2) {
                return callable.call(cx2, scope2, thisObj, args);
            }
        }, securityDomain);
    }

    @Deprecated
    public Object execWithDomain(Context cx, Scriptable scope, Script script, Object securityDomain) {
        throw new IllegalStateException("callWithDomain should be overridden");
    }
}
