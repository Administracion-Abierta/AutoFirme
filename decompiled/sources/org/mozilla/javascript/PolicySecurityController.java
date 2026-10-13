package org.mozilla.javascript;

import java.lang.ref.SoftReference;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.SecureClassLoader;
import java.util.Map;
import java.util.WeakHashMap;
import org.mozilla.classfile.ClassFileWriter;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/PolicySecurityController.class */
public class PolicySecurityController extends SecurityController {
    private static final byte[] secureCallerImplBytecode = loadBytecode();
    private static final Map<CodeSource, Map<ClassLoader, SoftReference<SecureCaller>>> callers = new WeakHashMap();

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/PolicySecurityController$SecureCaller.class */
    public static abstract class SecureCaller {
        public abstract Object call(Callable callable, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr);
    }

    @Override // org.mozilla.javascript.SecurityController
    public Class<?> getStaticSecurityDomainClassInternal() {
        return CodeSource.class;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/PolicySecurityController$Loader.class */
    private static class Loader extends SecureClassLoader implements GeneratedClassLoader {
        private final CodeSource codeSource;

        Loader(ClassLoader parent, CodeSource codeSource) {
            super(parent);
            this.codeSource = codeSource;
        }

        @Override // org.mozilla.javascript.GeneratedClassLoader
        public Class<?> defineClass(String name, byte[] data) {
            return defineClass(name, data, 0, data.length, this.codeSource);
        }

        @Override // org.mozilla.javascript.GeneratedClassLoader
        public void linkClass(Class<?> cl) {
            resolveClass(cl);
        }
    }

    @Override // org.mozilla.javascript.SecurityController
    public GeneratedClassLoader createClassLoader(final ClassLoader parent, final Object securityDomain) {
        return (Loader) AccessController.doPrivileged(new PrivilegedAction<Object>() { // from class: org.mozilla.javascript.PolicySecurityController.1
            @Override // java.security.PrivilegedAction
            public Object run() {
                return new Loader(parent, (CodeSource) securityDomain);
            }
        });
    }

    @Override // org.mozilla.javascript.SecurityController
    public Object getDynamicSecurityDomain(Object securityDomain) {
        return securityDomain;
    }

    @Override // org.mozilla.javascript.SecurityController
    public Object callWithDomain(Object securityDomain, final Context cx, Callable callable, Scriptable scope, Scriptable thisObj, Object[] args) {
        Map<ClassLoader, SoftReference<SecureCaller>> classLoaderMap;
        SecureCaller caller;
        final ClassLoader classLoader = (ClassLoader) AccessController.doPrivileged(new PrivilegedAction<Object>() { // from class: org.mozilla.javascript.PolicySecurityController.2
            @Override // java.security.PrivilegedAction
            public Object run() {
                return cx.getApplicationClassLoader();
            }
        });
        final CodeSource codeSource = (CodeSource) securityDomain;
        synchronized (callers) {
            classLoaderMap = callers.get(codeSource);
            if (classLoaderMap == null) {
                classLoaderMap = new WeakHashMap();
                callers.put(codeSource, classLoaderMap);
            }
        }
        synchronized (classLoaderMap) {
            SoftReference<SecureCaller> ref = classLoaderMap.get(classLoader);
            if (ref != null) {
                caller = ref.get();
            } else {
                caller = null;
            }
            if (caller == null) {
                try {
                    caller = (SecureCaller) AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() { // from class: org.mozilla.javascript.PolicySecurityController.3
                        @Override // java.security.PrivilegedExceptionAction
                        public Object run() throws Exception {
                            Loader loader = new Loader(classLoader, codeSource);
                            Class<?> c = loader.defineClass(SecureCaller.class.getName() + "Impl", PolicySecurityController.secureCallerImplBytecode);
                            return c.newInstance();
                        }
                    });
                    classLoaderMap.put(classLoader, new SoftReference<>(caller));
                } catch (PrivilegedActionException ex) {
                    throw new UndeclaredThrowableException(ex.getCause());
                }
            }
        }
        return caller.call(callable, cx, scope, thisObj, args);
    }

    private static byte[] loadBytecode() {
        String secureCallerClassName = SecureCaller.class.getName();
        ClassFileWriter cfw = new ClassFileWriter(secureCallerClassName + "Impl", secureCallerClassName, "<generated>");
        cfw.startMethod("<init>", "()V", (short) 1);
        cfw.addALoad(0);
        cfw.addInvoke(183, secureCallerClassName, "<init>", "()V");
        cfw.add(177);
        cfw.stopMethod((short) 1);
        cfw.startMethod("call", "(Lorg/mozilla/javascript/Callable;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Scriptable;[Ljava/lang/Object;)Ljava/lang/Object;", (short) 17);
        for (int i = 1; i < 6; i++) {
            cfw.addALoad(i);
        }
        cfw.addInvoke(185, "org/mozilla/javascript/Callable", "call", "(Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Scriptable;[Ljava/lang/Object;)Ljava/lang/Object;");
        cfw.add(176);
        cfw.stopMethod((short) 6);
        return cfw.toByteArray();
    }
}
