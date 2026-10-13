package es.gob.afirma.standalone.ui;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/OSXHandler.class */
public class OSXHandler implements InvocationHandler {
    protected Object targetObject;
    protected Method targetMethod;
    protected String proxySignature;
    static Object macOSXApplication;
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    protected OSXHandler(String str, Object obj, Method method) {
        this.proxySignature = str;
        this.targetObject = obj;
        this.targetMethod = method;
    }

    public static void setQuitHandler(Object obj, Method method) {
        Class<?> cls;
        OSXHandler oSXHandler = new OSXHandler("handleQuitRequestWith", obj, method) { // from class: es.gob.afirma.standalone.ui.OSXHandler.1
            @Override // es.gob.afirma.standalone.ui.OSXHandler
            public boolean callTarget(Object obj2, Object obj3) {
                if (obj2 != null) {
                    try {
                        this.targetMethod.invoke(this.targetObject, obj2, obj3);
                        return true;
                    } catch (IllegalAccessException | IllegalArgumentException | SecurityException | InvocationTargetException e) {
                        LOGGER.severe("Mac OS X Adapter could not talk to EAWT:");
                        return true;
                    }
                }
                return true;
            }
        };
        try {
            Class<?> cls2 = Class.forName("com.apple.eawt.Application");
            if (macOSXApplication == null) {
                macOSXApplication = cls2.getConstructor((Class[]) null).newInstance((Object[]) null);
            }
            try {
                cls = Class.forName("com.apple.eawt.QuitHandler");
            } catch (Error | Exception e) {
                cls = Class.forName("java.awt.desktop.QuitHandler");
            }
            cls2.getDeclaredMethod("setQuitHandler", cls).invoke(macOSXApplication, Proxy.newProxyInstance(OSXHandler.class.getClassLoader(), new Class[]{cls}, oSXHandler));
        } catch (ClassNotFoundException e2) {
            LOGGER.log(Level.SEVERE, "This version of Mac OS X does not support the Apple EAWT. ApplicationEvent handling has been disabled", (Throwable) e2);
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e3) {
            LOGGER.log(Level.SEVERE, "Mac OS X Adapter could not talk to EAWT", e3);
        }
    }

    public static void setAboutHandler(Object obj, Method method) {
        Class<?> cls;
        OSXHandler oSXHandler = new OSXHandler("handleAbout", obj, method) { // from class: es.gob.afirma.standalone.ui.OSXHandler.2
            @Override // es.gob.afirma.standalone.ui.OSXHandler
            public boolean callTarget(Object obj2) {
                if (obj2 != null) {
                    try {
                        this.targetMethod.invoke(this.targetObject, obj2);
                        return true;
                    } catch (IllegalAccessException | IllegalArgumentException | SecurityException | InvocationTargetException e) {
                        LOGGER.severe("Mac OS X Adapter could not talk to EAWT:");
                        return true;
                    }
                }
                return true;
            }
        };
        try {
            Class<?> cls2 = Class.forName("com.apple.eawt.Application");
            if (macOSXApplication == null) {
                macOSXApplication = cls2.getConstructor((Class[]) null).newInstance((Object[]) null);
            }
            try {
                cls = Class.forName("com.apple.eawt.AboutHandler");
            } catch (Error | Exception e) {
                cls = Class.forName("java.awt.desktop.AboutHandler");
            }
            cls2.getDeclaredMethod("setAboutHandler", cls).invoke(macOSXApplication, Proxy.newProxyInstance(OSXHandler.class.getClassLoader(), new Class[]{cls}, oSXHandler));
        } catch (ClassNotFoundException e2) {
            LOGGER.log(Level.SEVERE, "This version of Mac OS X does not support the Apple EAWT. ApplicationEvent handling has been disabled", (Throwable) e2);
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e3) {
            LOGGER.log(Level.SEVERE, "Mac OS X Adapter could not talk to EAWT", e3);
        }
    }

    public static void setPreferencesHandler(Object obj, Method method) {
        Class<?> cls;
        OSXHandler oSXHandler = new OSXHandler("handlePreferences", obj, method) { // from class: es.gob.afirma.standalone.ui.OSXHandler.3
            @Override // es.gob.afirma.standalone.ui.OSXHandler
            public boolean callTarget(Object obj2) {
                if (obj2 != null) {
                    try {
                        this.targetMethod.invoke(this.targetObject, obj2);
                        return true;
                    } catch (IllegalAccessException | IllegalArgumentException | SecurityException | InvocationTargetException e) {
                        LOGGER.severe("Mac OS X Adapter could not talk to EAWT:");
                        return true;
                    }
                }
                return true;
            }
        };
        try {
            Class<?> cls2 = Class.forName("com.apple.eawt.Application");
            if (macOSXApplication == null) {
                macOSXApplication = cls2.getConstructor((Class[]) null).newInstance((Object[]) null);
            }
            try {
                cls = Class.forName("com.apple.eawt.PreferencesHandler");
            } catch (Error | Exception e) {
                cls = Class.forName("java.awt.desktop.PreferencesHandler");
            }
            cls2.getDeclaredMethod("setPreferencesHandler", cls).invoke(macOSXApplication, Proxy.newProxyInstance(OSXHandler.class.getClassLoader(), new Class[]{cls}, oSXHandler));
        } catch (ClassNotFoundException e2) {
            LOGGER.log(Level.SEVERE, "This version of Mac OS X does not support the Apple EAWT. ApplicationEvent handling has been disabled", (Throwable) e2);
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e3) {
            LOGGER.log(Level.SEVERE, "Mac OS X Adapter could not talk to EAWT", e3);
        }
    }

    @Override // java.lang.reflect.InvocationHandler
    public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
        if (isCorrectMethod(method, objArr)) {
            if (objArr.length == 1) {
                callTarget(objArr[0]);
                return null;
            }
            callTarget(objArr[0], objArr[1]);
            return null;
        }
        return null;
    }

    public boolean callTarget(Object obj) throws IllegalAccessException, InvocationTargetException {
        Object objInvoke = this.targetMethod.invoke(this.targetObject, (Object[]) null);
        if (objInvoke == null) {
            return true;
        }
        return Boolean.parseBoolean(objInvoke.toString());
    }

    public boolean callTarget(Object obj, Object obj2) throws IllegalAccessException, InvocationTargetException {
        Object objInvoke = this.targetMethod.invoke(this.targetObject, (Object[]) null);
        if (objInvoke == null) {
            return true;
        }
        return Boolean.parseBoolean(objInvoke.toString());
    }

    protected boolean isCorrectMethod(Method method, Object[] objArr) {
        return this.targetMethod != null && this.proxySignature.equals(method.getName()) && objArr.length > 0;
    }
}
