package jakarta.xml.bind;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/ServiceLoaderUtil.class */
class ServiceLoaderUtil {
    private static final String OSGI_SERVICE_LOADER_CLASS_NAME = "org.glassfish.hk2.osgiresourcelocator.ServiceLoader";
    private static final String OSGI_SERVICE_LOADER_METHOD_NAME = "lookupProviderClasses";

    ServiceLoaderUtil() {
    }

    static <P, T extends Exception> P firstByServiceLoader(Class<P> spiClass, Logger logger, ExceptionHandler<T> handler) throws Exception {
        try {
            ServiceLoader<P> serviceLoader = ServiceLoader.load(spiClass);
            Iterator<P> it = serviceLoader.iterator();
            if (it.hasNext()) {
                P impl = it.next();
                logger.fine("ServiceProvider loading Facility used; returning object [" + impl.getClass().getName() + "]");
                return impl;
            }
            return null;
        } catch (Throwable t) {
            throw handler.createException(t, "Error while searching for service [" + spiClass.getName() + "]");
        }
    }

    static Object lookupUsingOSGiServiceLoader(String factoryId, Logger logger) {
        try {
            Class<?> cls = Class.forName(factoryId);
            Method m = Class.forName(OSGI_SERVICE_LOADER_CLASS_NAME).getMethod(OSGI_SERVICE_LOADER_METHOD_NAME, Class.class);
            Iterator iter = ((Iterable) m.invoke(null, cls)).iterator();
            if (iter.hasNext()) {
                Object next = iter.next();
                logger.fine("Found implementation using OSGi facility; returning object [" + next.getClass().getName() + "].");
                return next;
            }
            return null;
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ignored) {
            logger.log(Level.FINE, "Unable to find from OSGi: [" + factoryId + "]", (Throwable) ignored);
            return null;
        }
    }

    static void checkPackageAccess(String className) {
        int i;
        SecurityManager s = System.getSecurityManager();
        if (s != null && (i = className.lastIndexOf(46)) != -1) {
            s.checkPackageAccess(className.substring(0, i));
        }
    }

    static Class nullSafeLoadClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
        if (classLoader == null) {
            return Class.forName(className);
        }
        return classLoader.loadClass(className);
    }

    static <T extends Exception> Object newInstance(String className, String defaultImplClassName, ExceptionHandler<T> handler) throws Exception {
        try {
            return safeLoadClass(className, defaultImplClassName, contextClassLoader(handler)).newInstance();
        } catch (ClassNotFoundException x) {
            throw handler.createException(x, "Provider " + className + " not found");
        } catch (Exception x2) {
            throw handler.createException(x2, "Provider " + className + " could not be instantiated: " + x2);
        }
    }

    static Class safeLoadClass(String className, String defaultImplClassName, ClassLoader classLoader) throws ClassNotFoundException {
        try {
            checkPackageAccess(className);
            return nullSafeLoadClass(className, classLoader);
        } catch (SecurityException se) {
            if (defaultImplClassName != null && defaultImplClassName.equals(className)) {
                return Class.forName(className);
            }
            throw se;
        }
    }

    static ClassLoader contextClassLoader(ExceptionHandler exceptionHandler) throws Exception {
        try {
            return Thread.currentThread().getContextClassLoader();
        } catch (Exception x) {
            throw exceptionHandler.createException(x, x.toString());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/ServiceLoaderUtil$ExceptionHandler.class */
    static abstract class ExceptionHandler<T extends Exception> {
        public abstract T createException(Throwable th, String str);

        ExceptionHandler() {
        }
    }
}
