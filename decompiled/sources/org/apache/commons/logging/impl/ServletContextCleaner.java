package org.apache.commons.logging.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/commons/logging/impl/ServletContextCleaner.class */
public class ServletContextCleaner implements ServletContextListener {
    private static final Class[] RELEASE_SIGNATURE;
    static Class class$java$lang$ClassLoader;

    static Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        Class clsClass$;
        Class[] clsArr = new Class[1];
        if (class$java$lang$ClassLoader == null) {
            clsClass$ = class$("java.lang.ClassLoader");
            class$java$lang$ClassLoader = clsClass$;
        } else {
            clsClass$ = class$java$lang$ClassLoader;
        }
        clsArr[0] = clsClass$;
        RELEASE_SIGNATURE = clsArr;
    }

    public void contextDestroyed(ServletContextEvent sce) {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        Object[] params = {tccl};
        ClassLoader loader = tccl;
        while (loader != null) {
            try {
                Class<?> clsLoadClass = loader.loadClass(LogFactory.FACTORY_PROPERTY);
                Method releaseMethod = clsLoadClass.getMethod("release", RELEASE_SIGNATURE);
                releaseMethod.invoke(null, params);
                loader = clsLoadClass.getClassLoader().getParent();
            } catch (ClassNotFoundException e) {
                loader = null;
            } catch (IllegalAccessException e2) {
                System.err.println("LogFactory instance found which is not accessable!");
                loader = null;
            } catch (NoSuchMethodException e3) {
                System.err.println("LogFactory instance found which does not support release method!");
                loader = null;
            } catch (InvocationTargetException e4) {
                System.err.println("LogFactory instance release method failed!");
                loader = null;
            }
        }
        LogFactory.release(tccl);
    }

    public void contextInitialized(ServletContextEvent sce) {
    }
}
