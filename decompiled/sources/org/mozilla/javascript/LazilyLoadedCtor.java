package org.mozilla.javascript;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/LazilyLoadedCtor.class */
public final class LazilyLoadedCtor implements Serializable {
    private static final long serialVersionUID = 1;
    private static final int STATE_BEFORE_INIT = 0;
    private static final int STATE_INITIALIZING = 1;
    private static final int STATE_WITH_VALUE = 2;
    private final ScriptableObject scope;
    private final String propertyName;
    private final String className;
    private final boolean sealed;
    private final boolean privileged;
    private Object initializedValue;
    private int state;

    public LazilyLoadedCtor(ScriptableObject scope, String propertyName, String className, boolean sealed) {
        this(scope, propertyName, className, sealed, false);
    }

    LazilyLoadedCtor(ScriptableObject scope, String propertyName, String className, boolean sealed, boolean privileged) {
        this.scope = scope;
        this.propertyName = propertyName;
        this.className = className;
        this.sealed = sealed;
        this.privileged = privileged;
        this.state = 0;
        scope.addLazilyInitializedValue(propertyName, 0, this, 2);
    }

    void init() {
        synchronized (this) {
            if (this.state == 1) {
                throw new IllegalStateException("Recursive initialization for " + this.propertyName);
            }
            if (this.state == 0) {
                this.state = 1;
                Object value = Scriptable.NOT_FOUND;
                try {
                    value = buildValue();
                    this.initializedValue = value;
                    this.state = 2;
                } catch (Throwable th) {
                    this.initializedValue = value;
                    this.state = 2;
                    throw th;
                }
            }
        }
    }

    Object getValue() {
        if (this.state != 2) {
            throw new IllegalStateException(this.propertyName);
        }
        return this.initializedValue;
    }

    private Object buildValue() {
        if (this.privileged) {
            return AccessController.doPrivileged(new PrivilegedAction<Object>() { // from class: org.mozilla.javascript.LazilyLoadedCtor.1
                @Override // java.security.PrivilegedAction
                public Object run() {
                    return LazilyLoadedCtor.this.buildValue0();
                }
            });
        }
        return buildValue0();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Object buildValue0() {
        Class<? extends Scriptable> cl = cast(Kit.classOrNull(this.className));
        if (cl != null) {
            try {
                Object value = ScriptableObject.buildClassCtor(this.scope, cl, this.sealed, false);
                if (value != null) {
                    return value;
                }
                Object value2 = this.scope.get(this.propertyName, this.scope);
                if (value2 != Scriptable.NOT_FOUND) {
                    return value2;
                }
            } catch (IllegalAccessException e) {
            } catch (InstantiationException e2) {
            } catch (SecurityException e3) {
            } catch (InvocationTargetException ex) {
                Throwable target = ex.getTargetException();
                if (target instanceof RuntimeException) {
                    throw ((RuntimeException) target);
                }
            } catch (RhinoException e4) {
            }
        }
        return Scriptable.NOT_FOUND;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static Class<? extends Scriptable> cast(Class<?> cls) {
        return cls;
    }
}
