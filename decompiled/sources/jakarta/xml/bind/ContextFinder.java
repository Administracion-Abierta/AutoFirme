package jakarta.xml.bind;

import com.twelvemonkeys.util.service.ServiceRegistry;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/ContextFinder.class */
class ContextFinder {
    private static final String JAXB_CONTEXT_FACTORY_DEPRECATED = "jakarta.xml.bind.context.factory";
    private static final Logger logger = Logger.getLogger("jakarta.xml.bind");
    static final String DEFAULT_FACTORY_CLASS = "org.glassfish.jaxb.runtime.v2.ContextFactory";
    private static ServiceLoaderUtil.ExceptionHandler<JAXBException> EXCEPTION_HANDLER;

    ContextFinder() {
    }

    static {
        try {
            if (AccessController.doPrivileged(new GetPropertyAction("jaxb.debug")) != null) {
                logger.setUseParentHandlers(false);
                logger.setLevel(Level.ALL);
                ConsoleHandler handler = new ConsoleHandler();
                handler.setLevel(Level.ALL);
                logger.addHandler(handler);
            }
        } catch (Throwable th) {
        }
        EXCEPTION_HANDLER = new ServiceLoaderUtil.ExceptionHandler<JAXBException>() { // from class: jakarta.xml.bind.ContextFinder.1
            @Override // jakarta.xml.bind.ServiceLoaderUtil.ExceptionHandler
            public JAXBException createException(Throwable throwable, String message) {
                return new JAXBException(message, throwable);
            }
        };
    }

    private static Throwable handleInvocationTargetException(InvocationTargetException x) throws JAXBException {
        Throwable t = x.getTargetException();
        if (t != null) {
            if (t instanceof JAXBException) {
                throw ((JAXBException) t);
            }
            if (t instanceof RuntimeException) {
                throw ((RuntimeException) t);
            }
            if (t instanceof Error) {
                throw ((Error) t);
            }
            return t;
        }
        return x;
    }

    private static JAXBException handleClassCastException(Class originalType, Class targetType) {
        URL targetTypeURL = which(targetType);
        return new JAXBException(Messages.format("JAXBContext.IllegalCast", getClassClassLoader(originalType).getResource("jakarta/xml/bind/JAXBContext.class"), targetTypeURL));
    }

    static JAXBContext newInstance(String contextPath, Class[] contextPathClasses, String className, ClassLoader classLoader, Map properties) throws Exception {
        try {
            Class spFactory = ServiceLoaderUtil.safeLoadClass(className, DEFAULT_FACTORY_CLASS, classLoader);
            return newInstance(contextPath, contextPathClasses, spFactory, classLoader, properties);
        } catch (JAXBException | RuntimeException x) {
            throw x;
        } catch (ClassNotFoundException x2) {
            throw new JAXBException(Messages.format("ContextFinder.DefaultProviderNotFound"), x2);
        } catch (Exception x3) {
            throw new JAXBException(Messages.format("ContextFinder.CouldNotInstantiate", className, x3), x3);
        }
    }

    static JAXBContext newInstance(String contextPath, Class[] contextPathClasses, Class spFactory, ClassLoader classLoader, Map properties) throws JAXBException {
        try {
            ModuleUtil.delegateAddOpensToImplModule(contextPathClasses, spFactory);
            Object context = null;
            try {
                Method m = spFactory.getMethod("createContext", String.class, ClassLoader.class, Map.class);
                Object obj = instantiateProviderIfNecessary(spFactory);
                context = m.invoke(obj, contextPath, classLoader, properties);
            } catch (NoSuchMethodException e) {
            }
            if (context == null) {
                Method m2 = spFactory.getMethod("createContext", String.class, ClassLoader.class);
                Object obj2 = instantiateProviderIfNecessary(spFactory);
                context = m2.invoke(obj2, contextPath, classLoader);
            }
            if (!(context instanceof JAXBContext)) {
                throw handleClassCastException(context.getClass(), JAXBContext.class);
            }
            return (JAXBContext) context;
        } catch (InvocationTargetException x) {
            Throwable e2 = handleInvocationTargetException(x);
            throw new JAXBException(Messages.format("ContextFinder.CouldNotInstantiate", spFactory, e2), e2);
        } catch (Exception x2) {
            throw new JAXBException(Messages.format("ContextFinder.CouldNotInstantiate", spFactory, x2), x2);
        }
    }

    private static Object instantiateProviderIfNecessary(final Class<?> implClass) throws JAXBException {
        try {
            if (JAXBContextFactory.class.isAssignableFrom(implClass)) {
                return AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() { // from class: jakarta.xml.bind.ContextFinder.2
                    @Override // java.security.PrivilegedExceptionAction
                    public Object run() throws Exception {
                        return implClass.newInstance();
                    }
                });
            }
            return null;
        } catch (PrivilegedActionException x) {
            Throwable e = x.getCause() == null ? x : x.getCause();
            throw new JAXBException(Messages.format("ContextFinder.CouldNotInstantiate", implClass, e), e);
        }
    }

    private static JAXBContext newInstance(Class[] classes, Map properties, String className) throws JAXBException {
        return newInstance(classes, properties, className, getContextClassLoader());
    }

    private static JAXBContext newInstance(Class[] classes, Map properties, String className, ClassLoader loader) throws JAXBException {
        try {
            Class spi = ServiceLoaderUtil.safeLoadClass(className, DEFAULT_FACTORY_CLASS, loader);
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "loaded {0} from {1}", new Object[]{className, which(spi)});
            }
            return newInstance(classes, properties, spi);
        } catch (ClassNotFoundException e) {
            throw new JAXBException(Messages.format("ContextFinder.DefaultProviderNotFound"), e);
        }
    }

    static JAXBContext newInstance(Class[] classes, Map properties, Class spFactory) throws JAXBException {
        try {
            ModuleUtil.delegateAddOpensToImplModule(classes, spFactory);
            Method m = spFactory.getMethod("createContext", Class[].class, Map.class);
            Object obj = instantiateProviderIfNecessary(spFactory);
            Object context = m.invoke(obj, classes, properties);
            if (!(context instanceof JAXBContext)) {
                throw handleClassCastException(context.getClass(), JAXBContext.class);
            }
            return (JAXBContext) context;
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new JAXBException(e);
        } catch (InvocationTargetException e2) {
            Throwable x = handleInvocationTargetException(e2);
            throw new JAXBException(x);
        }
    }

    static JAXBContext find(String factoryId, String contextPath, ClassLoader classLoader, Map properties) throws JAXBException {
        if (contextPath == null || contextPath.isEmpty()) {
            throw new JAXBException(Messages.format("ContextFinder.NoPackageInContextPath"));
        }
        Class[] contextPathClasses = ModuleUtil.getClassesFromContextPath(contextPath, classLoader);
        String factoryClassName = jaxbProperties(contextPath, classLoader, factoryId);
        if (factoryClassName == null && contextPathClasses != null) {
            factoryClassName = jaxbProperties(contextPathClasses, factoryId);
        }
        if (factoryClassName != null) {
            return newInstance(contextPath, contextPathClasses, factoryClassName, classLoader, properties);
        }
        String factoryName = classNameFromSystemProperties();
        if (factoryName != null) {
            return newInstance(contextPath, contextPathClasses, factoryName, classLoader, properties);
        }
        JAXBContextFactory obj = (JAXBContextFactory) ServiceLoaderUtil.firstByServiceLoader(JAXBContextFactory.class, logger, EXCEPTION_HANDLER);
        if (obj != null) {
            ModuleUtil.delegateAddOpensToImplModule(contextPathClasses, obj.getClass());
            return obj.createContext(contextPath, classLoader, properties);
        }
        String factoryName2 = firstByServiceLoaderDeprecated(JAXBContext.class, classLoader);
        if (factoryName2 != null) {
            return newInstance(contextPath, contextPathClasses, factoryName2, classLoader, properties);
        }
        Class ctxFactory = (Class) ServiceLoaderUtil.lookupUsingOSGiServiceLoader("jakarta.xml.bind.JAXBContext", logger);
        if (ctxFactory != null) {
            return newInstance(contextPath, contextPathClasses, ctxFactory, classLoader, properties);
        }
        logger.fine("Trying to create the platform default provider");
        return newInstance(contextPath, contextPathClasses, DEFAULT_FACTORY_CLASS, classLoader, properties);
    }

    static JAXBContext find(Class<?>[] classes, Map<String, ?> properties) throws JAXBException {
        ClassLoader c;
        URL jaxbPropertiesUrl;
        logger.fine("Searching jaxb.properties");
        for (Class<?> cls : classes) {
            if (cls.getPackage() != null && (jaxbPropertiesUrl = getResourceUrl(cls, "jaxb.properties")) != null) {
                return newInstance(classes, properties, classNameFromPackageProperties(jaxbPropertiesUrl, JAXBContext.JAXB_CONTEXT_FACTORY, JAXB_CONTEXT_FACTORY_DEPRECATED), getClassClassLoader(cls));
            }
        }
        String factoryClassName = classNameFromSystemProperties();
        if (factoryClassName != null) {
            return newInstance(classes, properties, factoryClassName);
        }
        JAXBContextFactory factory = (JAXBContextFactory) ServiceLoaderUtil.firstByServiceLoader(JAXBContextFactory.class, logger, EXCEPTION_HANDLER);
        if (factory != null) {
            ModuleUtil.delegateAddOpensToImplModule(classes, factory.getClass());
            return factory.createContext(classes, properties);
        }
        ClassLoader loader = getContextClassLoader();
        if (classes.length > 0 && (c = getClassClassLoader(classes[0])) != null) {
            loader = c;
        }
        String className = firstByServiceLoaderDeprecated(JAXBContext.class, loader);
        if (className != null) {
            return newInstance(classes, properties, className, loader);
        }
        logger.fine("Trying to create the platform default provider");
        Class ctxFactoryClass = (Class) ServiceLoaderUtil.lookupUsingOSGiServiceLoader("jakarta.xml.bind.JAXBContext", logger);
        if (ctxFactoryClass != null) {
            return newInstance(classes, properties, ctxFactoryClass);
        }
        logger.fine("Trying to create the platform default provider");
        return newInstance(classes, properties, DEFAULT_FACTORY_CLASS);
    }

    private static String classNameFromPackageProperties(URL packagePropertiesUrl, String... factoryIds) throws JAXBException {
        logger.log(Level.FINE, "Trying to locate {0}", packagePropertiesUrl.toString());
        Properties props = loadJAXBProperties(packagePropertiesUrl);
        for (String factoryId : factoryIds) {
            if (props.containsKey(factoryId)) {
                return props.getProperty(factoryId);
            }
        }
        String propertiesUrl = packagePropertiesUrl.toExternalForm();
        String packageName = propertiesUrl.substring(0, propertiesUrl.indexOf("/jaxb.properties"));
        throw new JAXBException(Messages.format("ContextFinder.MissingProperty", packageName, factoryIds[0]));
    }

    private static String classNameFromSystemProperties() throws JAXBException {
        String factoryClassName = getSystemProperty(JAXBContext.JAXB_CONTEXT_FACTORY);
        if (factoryClassName != null) {
            return factoryClassName;
        }
        String factoryClassName2 = getDeprecatedSystemProperty(JAXB_CONTEXT_FACTORY_DEPRECATED);
        if (factoryClassName2 != null) {
            return factoryClassName2;
        }
        String factoryClassName3 = getDeprecatedSystemProperty(JAXBContext.class.getName());
        if (factoryClassName3 != null) {
            return factoryClassName3;
        }
        return null;
    }

    private static String getDeprecatedSystemProperty(String property) {
        String value = getSystemProperty(property);
        if (value != null) {
            logger.log(Level.WARNING, "Using non-standard property: {0}. Property {1} should be used instead.", new Object[]{property, JAXBContext.JAXB_CONTEXT_FACTORY});
        }
        return value;
    }

    private static String getSystemProperty(String property) {
        logger.log(Level.FINE, "Checking system property {0}", property);
        String value = (String) AccessController.doPrivileged(new GetPropertyAction(property));
        if (value != null) {
            logger.log(Level.FINE, "  found {0}", value);
        } else {
            logger.log(Level.FINE, "  not found");
        }
        return value;
    }

    private static Properties loadJAXBProperties(URL url) throws JAXBException {
        try {
            logger.log(Level.FINE, "loading props from {0}", url);
            Properties props = new Properties();
            InputStream is = url.openStream();
            props.load(is);
            is.close();
            return props;
        } catch (IOException ioe) {
            logger.log(Level.FINE, "Unable to load " + url.toString(), (Throwable) ioe);
            throw new JAXBException(ioe.toString(), ioe);
        }
    }

    private static URL getResourceUrl(ClassLoader classLoader, String resourceName) {
        URL url;
        if (classLoader == null) {
            url = ClassLoader.getSystemResource(resourceName);
        } else {
            url = classLoader.getResource(resourceName);
        }
        return url;
    }

    private static URL getResourceUrl(Class<?> clazz, String resourceName) {
        return clazz.getResource(resourceName);
    }

    static URL which(Class clazz, ClassLoader loader) {
        String classnameAsResource = clazz.getName().replace('.', '/') + ".class";
        if (loader == null) {
            loader = getSystemClassLoader();
        }
        return loader.getResource(classnameAsResource);
    }

    static URL which(Class clazz) {
        return which(clazz, getClassClassLoader(clazz));
    }

    private static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        }
        return (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() { // from class: jakarta.xml.bind.ContextFinder.3
            @Override // java.security.PrivilegedAction
            public Object run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
    }

    private static ClassLoader getClassClassLoader(final Class c) {
        if (System.getSecurityManager() == null) {
            return c.getClassLoader();
        }
        return (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() { // from class: jakarta.xml.bind.ContextFinder.4
            @Override // java.security.PrivilegedAction
            public Object run() {
                return c.getClassLoader();
            }
        });
    }

    private static ClassLoader getSystemClassLoader() {
        if (System.getSecurityManager() == null) {
            return ClassLoader.getSystemClassLoader();
        }
        return (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() { // from class: jakarta.xml.bind.ContextFinder.5
            @Override // java.security.PrivilegedAction
            public Object run() {
                return ClassLoader.getSystemClassLoader();
            }
        });
    }

    @Deprecated
    static String firstByServiceLoaderDeprecated(Class spiClass, ClassLoader classLoader) throws JAXBException {
        InputStream resourceAsStream;
        String jaxbContextFQCN = spiClass.getName();
        logger.fine("Searching META-INF/services");
        BufferedReader r = null;
        String resource = ServiceRegistry.SERVICES + jaxbContextFQCN;
        try {
            try {
                if (classLoader == null) {
                    resourceAsStream = ClassLoader.getSystemResourceAsStream(resource);
                } else {
                    resourceAsStream = classLoader.getResourceAsStream(resource);
                }
                InputStream resourceStream = resourceAsStream;
                if (resourceStream != null) {
                    BufferedReader r2 = new BufferedReader(new InputStreamReader(resourceStream, "UTF-8"));
                    String factoryClassName = r2.readLine();
                    if (factoryClassName != null) {
                        factoryClassName = factoryClassName.trim();
                    }
                    r2.close();
                    logger.log(Level.FINE, "Configured factorty class:{0}", factoryClassName);
                    String str = factoryClassName;
                    if (r2 != null) {
                        try {
                            r2.close();
                        } catch (IOException ex) {
                            logger.log(Level.SEVERE, "Unable to close resource: " + resource, (Throwable) ex);
                        }
                    }
                    return str;
                }
                logger.log(Level.FINE, "Unable to load:{0}", resource);
                if (0 != 0) {
                    try {
                        r.close();
                    } catch (IOException ex2) {
                        logger.log(Level.SEVERE, "Unable to close resource: " + resource, (Throwable) ex2);
                    }
                }
                return null;
            } catch (IOException e) {
                throw new JAXBException(e);
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    r.close();
                } catch (IOException ex3) {
                    logger.log(Level.SEVERE, "Unable to close resource: " + resource, (Throwable) ex3);
                    throw th;
                }
            }
            throw th;
        }
    }

    private static String jaxbProperties(String contextPath, ClassLoader classLoader, String factoryId) throws JAXBException {
        String[] packages = contextPath.split(":");
        for (String pkg : packages) {
            String pkgUrl = pkg.replace('.', '/');
            URL jaxbPropertiesUrl = getResourceUrl(classLoader, pkgUrl + "/jaxb.properties");
            if (jaxbPropertiesUrl != null) {
                return classNameFromPackageProperties(jaxbPropertiesUrl, factoryId, JAXB_CONTEXT_FACTORY_DEPRECATED);
            }
        }
        return null;
    }

    private static String jaxbProperties(Class[] classesFromContextPath, String factoryId) throws JAXBException {
        for (Class c : classesFromContextPath) {
            URL jaxbPropertiesUrl = getResourceUrl((Class<?>) c, "jaxb.properties");
            if (jaxbPropertiesUrl != null) {
                return classNameFromPackageProperties(jaxbPropertiesUrl, factoryId, JAXB_CONTEXT_FACTORY_DEPRECATED);
            }
        }
        return null;
    }
}
