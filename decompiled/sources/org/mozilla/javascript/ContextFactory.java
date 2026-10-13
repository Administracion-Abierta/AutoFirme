package org.mozilla.javascript;

import java.security.AccessController;
import java.security.PrivilegedAction;
import org.mozilla.javascript.xml.XMLLib;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ContextFactory.class */
public class ContextFactory {
    private static volatile boolean hasCustomGlobal;
    private static ContextFactory global = new ContextFactory();
    private volatile boolean sealed;
    private final Object listenersLock = new Object();
    private volatile Object listeners;
    private boolean disabledListening;
    private ClassLoader applicationClassLoader;

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ContextFactory$GlobalSetter.class */
    public interface GlobalSetter {
        void setContextFactoryGlobal(ContextFactory contextFactory);

        ContextFactory getContextFactoryGlobal();
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ContextFactory$Listener.class */
    public interface Listener {
        void contextCreated(Context context);

        void contextReleased(Context context);
    }

    public static ContextFactory getGlobal() {
        return global;
    }

    public static boolean hasExplicitGlobal() {
        return hasCustomGlobal;
    }

    public static synchronized void initGlobal(ContextFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException();
        }
        if (hasCustomGlobal) {
            throw new IllegalStateException();
        }
        hasCustomGlobal = true;
        global = factory;
    }

    public static synchronized GlobalSetter getGlobalSetter() {
        if (hasCustomGlobal) {
            throw new IllegalStateException();
        }
        hasCustomGlobal = true;
        return new GlobalSetter() { // from class: org.mozilla.javascript.ContextFactory.1GlobalSetterImpl
            @Override // org.mozilla.javascript.ContextFactory.GlobalSetter
            public void setContextFactoryGlobal(ContextFactory factory) {
                ContextFactory unused = ContextFactory.global = factory == null ? new ContextFactory() : factory;
            }

            @Override // org.mozilla.javascript.ContextFactory.GlobalSetter
            public ContextFactory getContextFactoryGlobal() {
                return ContextFactory.global;
            }
        };
    }

    protected Context makeContext() {
        return new Context(this);
    }

    protected boolean hasFeature(Context cx, int featureIndex) {
        switch (featureIndex) {
            case 1:
                int version = cx.getLanguageVersion();
                return version == 100 || version == 110 || version == 120;
            case 2:
                return false;
            case 3:
                return true;
            case 4:
                return cx.getLanguageVersion() == 120;
            case 5:
                return true;
            case 6:
                int version2 = cx.getLanguageVersion();
                return version2 == 0 || version2 >= 160;
            case 7:
                return false;
            case 8:
                return false;
            case 9:
                return false;
            case 10:
                return false;
            case 11:
                return false;
            case 12:
                return false;
            case 13:
                return false;
            case 14:
                return true;
            case 15:
                return cx.getLanguageVersion() <= 170;
            case 16:
                return cx.getLanguageVersion() >= 200;
            case 17:
                return false;
            case 18:
                return false;
            case 19:
                return false;
            case 20:
                return true;
            default:
                throw new IllegalArgumentException(String.valueOf(featureIndex));
        }
    }

    private static boolean isDom3Present() {
        Class<?> nodeClass = Kit.classOrNull("org.w3c.dom.Node");
        if (nodeClass == null) {
            return false;
        }
        try {
            nodeClass.getMethod("getUserData", String.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    protected XMLLib.Factory getE4xImplementationFactory() {
        if (isDom3Present()) {
            return XMLLib.Factory.create("org.mozilla.javascript.xmlimpl.XMLLibImpl");
        }
        return null;
    }

    protected GeneratedClassLoader createClassLoader(final ClassLoader parent) {
        return (GeneratedClassLoader) AccessController.doPrivileged(new PrivilegedAction<DefiningClassLoader>() { // from class: org.mozilla.javascript.ContextFactory.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.security.PrivilegedAction
            public DefiningClassLoader run() {
                return new DefiningClassLoader(parent);
            }
        });
    }

    public final ClassLoader getApplicationClassLoader() {
        return this.applicationClassLoader;
    }

    public final void initApplicationClassLoader(ClassLoader loader) {
        if (loader == null) {
            throw new IllegalArgumentException("loader is null");
        }
        if (!Kit.testIfCanLoadRhinoClasses(loader)) {
            throw new IllegalArgumentException("Loader can not resolve Rhino classes");
        }
        if (this.applicationClassLoader != null) {
            throw new IllegalStateException("applicationClassLoader can only be set once");
        }
        checkNotSealed();
        this.applicationClassLoader = loader;
    }

    protected Object doTopCall(Callable callable, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        Object result = callable.call(cx, scope, thisObj, args);
        return result instanceof ConsString ? result.toString() : result;
    }

    protected void observeInstructionCount(Context cx, int instructionCount) {
    }

    protected void onContextCreated(Context cx) {
        Object listeners = this.listeners;
        int i = 0;
        while (true) {
            Listener l = (Listener) Kit.getListener(listeners, i);
            if (l != null) {
                l.contextCreated(cx);
                i++;
            } else {
                return;
            }
        }
    }

    protected void onContextReleased(Context cx) {
        Object listeners = this.listeners;
        int i = 0;
        while (true) {
            Listener l = (Listener) Kit.getListener(listeners, i);
            if (l != null) {
                l.contextReleased(cx);
                i++;
            } else {
                return;
            }
        }
    }

    public final void addListener(Listener listener) {
        checkNotSealed();
        synchronized (this.listenersLock) {
            if (this.disabledListening) {
                throw new IllegalStateException();
            }
            this.listeners = Kit.addListener(this.listeners, listener);
        }
    }

    public final void removeListener(Listener listener) {
        checkNotSealed();
        synchronized (this.listenersLock) {
            if (this.disabledListening) {
                throw new IllegalStateException();
            }
            this.listeners = Kit.removeListener(this.listeners, listener);
        }
    }

    final void disableContextListening() {
        checkNotSealed();
        synchronized (this.listenersLock) {
            this.disabledListening = true;
            this.listeners = null;
        }
    }

    public final boolean isSealed() {
        return this.sealed;
    }

    public final void seal() {
        checkNotSealed();
        this.sealed = true;
    }

    protected final void checkNotSealed() {
        if (this.sealed) {
            throw new IllegalStateException();
        }
    }

    public final <T> T call(ContextAction<T> contextAction) {
        return (T) Context.call(this, contextAction);
    }

    public Context enterContext() {
        return enterContext(null);
    }

    @Deprecated
    public final Context enter() {
        return enterContext(null);
    }

    @Deprecated
    public final void exit() {
        Context.exit();
    }

    public final Context enterContext(Context cx) {
        return Context.enter(cx, this);
    }
}
