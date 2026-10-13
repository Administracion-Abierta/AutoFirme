package org.apache.xml.security.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/utils/ClassLoaderUtils.class */
public final class ClassLoaderUtils {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) ClassLoaderUtils.class);

    private ClassLoaderUtils() {
    }

    public static URL getResource(String resourceName, Class<?> callingClass) {
        ClassLoader cl;
        if (resourceName == null) {
            throw new NullPointerException();
        }
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        if (url == null && resourceName.charAt(0) == '/') {
            url = Thread.currentThread().getContextClassLoader().getResource(resourceName.substring(1));
        }
        ClassLoader cluClassloader = ClassLoaderUtils.class.getClassLoader();
        if (cluClassloader == null) {
            cluClassloader = ClassLoader.getSystemClassLoader();
        }
        if (url == null) {
            url = cluClassloader.getResource(resourceName);
        }
        if (url == null && resourceName.charAt(0) == '/') {
            url = cluClassloader.getResource(resourceName.substring(1));
        }
        if (url == null && (cl = callingClass.getClassLoader()) != null) {
            url = cl.getResource(resourceName);
        }
        if (url == null) {
            url = callingClass.getResource(resourceName);
        }
        if (url == null && resourceName.charAt(0) != '/') {
            return getResource('/' + resourceName, callingClass);
        }
        return url;
    }

    public static List<URL> getResources(String resourceName, Class<?> callingClass) {
        URL url;
        ClassLoader cl;
        if (resourceName == null) {
            throw new NullPointerException();
        }
        List<URL> ret = new ArrayList<>();
        Enumeration<URL> urls = new Enumeration<URL>() { // from class: org.apache.xml.security.utils.ClassLoaderUtils.1
            @Override // java.util.Enumeration
            public boolean hasMoreElements() {
                return false;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.Enumeration
            public URL nextElement() {
                return null;
            }
        };
        try {
            urls = Thread.currentThread().getContextClassLoader().getResources(resourceName);
        } catch (IOException e) {
            LOG.debug(e.getMessage(), (Throwable) e);
        }
        if (!urls.hasMoreElements() && resourceName.charAt(0) == '/') {
            try {
                urls = Thread.currentThread().getContextClassLoader().getResources(resourceName.substring(1));
            } catch (IOException e2) {
                LOG.debug(e2.getMessage(), (Throwable) e2);
            }
        }
        ClassLoader cluClassloader = ClassLoaderUtils.class.getClassLoader();
        if (cluClassloader == null) {
            cluClassloader = ClassLoader.getSystemClassLoader();
        }
        if (!urls.hasMoreElements()) {
            try {
                urls = cluClassloader.getResources(resourceName);
            } catch (IOException e3) {
                LOG.debug(e3.getMessage(), (Throwable) e3);
            }
        }
        if (!urls.hasMoreElements() && resourceName.charAt(0) == '/') {
            try {
                urls = cluClassloader.getResources(resourceName.substring(1));
            } catch (IOException e4) {
                LOG.debug(e4.getMessage(), (Throwable) e4);
            }
        }
        if (!urls.hasMoreElements() && (cl = callingClass.getClassLoader()) != null) {
            try {
                urls = cl.getResources(resourceName);
            } catch (IOException e5) {
                LOG.debug(e5.getMessage(), (Throwable) e5);
            }
        }
        if (!urls.hasMoreElements() && (url = callingClass.getResource(resourceName)) != null) {
            ret.add(url);
        }
        while (urls.hasMoreElements()) {
            ret.add(urls.nextElement());
        }
        if (ret.isEmpty() && resourceName.charAt(0) != '/') {
            return getResources('/' + resourceName, callingClass);
        }
        return ret;
    }

    public static InputStream getResourceAsStream(String resourceName, Class<?> callingClass) {
        URL url = getResource(resourceName, callingClass);
        if (url == null) {
            return null;
        }
        try {
            return url.openStream();
        } catch (IOException e) {
            LOG.debug(e.getMessage(), (Throwable) e);
            return null;
        }
    }

    public static Class<?> loadClass(String className, Class<?> callingClass) throws ClassNotFoundException {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl != null) {
                return cl.loadClass(className);
            }
        } catch (ClassNotFoundException e) {
            LOG.debug(e.getMessage(), (Throwable) e);
        }
        return loadClass2(className, callingClass);
    }

    private static Class<?> loadClass2(String className, Class<?> callingClass) throws ClassNotFoundException {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            try {
                if (ClassLoaderUtils.class.getClassLoader() != null) {
                    return ClassLoaderUtils.class.getClassLoader().loadClass(className);
                }
            } catch (ClassNotFoundException e) {
                if (callingClass != null && callingClass.getClassLoader() != null) {
                    return callingClass.getClassLoader().loadClass(className);
                }
            }
            LOG.debug(ex.getMessage(), (Throwable) ex);
            throw ex;
        }
    }
}
