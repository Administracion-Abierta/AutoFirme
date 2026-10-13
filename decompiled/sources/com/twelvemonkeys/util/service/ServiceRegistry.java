package com.twelvemonkeys.util.service;

import com.twelvemonkeys.lang.Validate;
import com.twelvemonkeys.util.FilterIterator;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.spi.CharsetProvider;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageReaderWriterSpi;
import javax.imageio.spi.ImageWriterSpi;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/service/ServiceRegistry.class */
public class ServiceRegistry {
    public static final String SERVICES = "META-INF/services/";
    private final Map<Class<?>, CategoryRegistry> categoryMap;

    public ServiceRegistry(Iterator<? extends Class<?>> it) {
        Validate.notNull(it, "categories");
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        while (it.hasNext()) {
            putCategory(linkedHashMap, it.next());
        }
        this.categoryMap = Collections.unmodifiableMap(linkedHashMap);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <T> void putCategory(Map<Class<?>, CategoryRegistry> map, Class<T> cls) {
        map.put(cls, new CategoryRegistry(cls));
    }

    public void registerApplicationClasspathSPIs() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Iterator<Class<?>> itCategories = categories();
        while (itCategories.hasNext()) {
            Class<?> next = itCategories.next();
            try {
                Enumeration<URL> resources = contextClassLoader.getResources(SERVICES + next.getName());
                while (resources.hasMoreElements()) {
                    registerSPIs(resources.nextElement(), next, contextClassLoader);
                }
            } catch (IOException e) {
                throw new ServiceConfigurationError(e);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    <T> void registerSPIs(URL url, Class<T> cls, ClassLoader classLoader) {
        Properties properties = new Properties();
        try {
            properties.load(url.openStream());
            if (!properties.isEmpty()) {
                CategoryRegistry categoryRegistry = this.categoryMap.get(cls);
                Iterator it = properties.keySet().iterator();
                while (it.hasNext()) {
                    try {
                        categoryRegistry.register(Class.forName((String) it.next(), true, classLoader).newInstance());
                    } catch (ClassNotFoundException e) {
                        throw new ServiceConfigurationError(e);
                    } catch (IllegalAccessException e2) {
                        throw new ServiceConfigurationError(e2);
                    } catch (IllegalArgumentException e3) {
                        throw new ServiceConfigurationError(e3);
                    } catch (InstantiationException e4) {
                        throw new ServiceConfigurationError(e4);
                    }
                }
            }
        } catch (IOException e5) {
            throw new ServiceConfigurationError(e5);
        }
    }

    protected <T> Iterator<T> providers(Class<T> cls) {
        return getRegistry(cls).providers();
    }

    protected Iterator<Class<?>> categories() {
        return this.categoryMap.keySet().iterator();
    }

    protected Iterator<Class<?>> compatibleCategories(final Object obj) {
        return new FilterIterator(categories(), new FilterIterator.Filter<Class<?>>() { // from class: com.twelvemonkeys.util.service.ServiceRegistry.1
            @Override // com.twelvemonkeys.util.FilterIterator.Filter
            public boolean accept(Class<?> cls) {
                return cls.isInstance(obj);
            }
        });
    }

    protected Iterator<Class<?>> containingCategories(final Object obj) {
        return new FilterIterator<Class<?>>(categories(), new FilterIterator.Filter<Class<?>>() { // from class: com.twelvemonkeys.util.service.ServiceRegistry.2
            @Override // com.twelvemonkeys.util.FilterIterator.Filter
            public boolean accept(Class<?> cls) {
                return ServiceRegistry.this.getRegistry(cls).contains(obj);
            }
        }) { // from class: com.twelvemonkeys.util.service.ServiceRegistry.3
            Class<?> current;

            @Override // com.twelvemonkeys.util.FilterIterator, java.util.Iterator
            public Class next() {
                Class<?> cls = (Class) super.next();
                this.current = cls;
                return cls;
            }

            @Override // com.twelvemonkeys.util.FilterIterator, java.util.Iterator
            public void remove() {
                if (this.current != null) {
                    ServiceRegistry.this.getRegistry(this.current).deregister(obj);
                    this.current = null;
                    return;
                }
                throw new IllegalStateException("No current element");
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public <T> CategoryRegistry<T> getRegistry(Class<T> cls) {
        CategoryRegistry<T> categoryRegistry = this.categoryMap.get(cls);
        if (categoryRegistry == null) {
            throw new IllegalArgumentException("No such category: " + cls.getName());
        }
        return categoryRegistry;
    }

    public boolean register(Object obj) {
        Iterator<Class<?>> itCompatibleCategories = compatibleCategories(obj);
        boolean z = false;
        while (itCompatibleCategories.hasNext()) {
            if (registerImpl(obj, itCompatibleCategories.next()) && !z) {
                z = true;
            }
        }
        return z;
    }

    private <T> boolean registerImpl(Object obj, Class<T> cls) {
        return getRegistry(cls).register(cls.cast(obj));
    }

    public <T> boolean register(T t, Class<? super T> cls) {
        return registerImpl(t, cls);
    }

    public boolean deregister(Object obj) {
        Iterator<Class<?>> itContainingCategories = containingCategories(obj);
        boolean z = false;
        while (itContainingCategories.hasNext()) {
            if (deregister(obj, itContainingCategories.next()) && !z) {
                z = true;
            }
        }
        return z;
    }

    public boolean deregister(Object obj, Class<?> cls) {
        return getRegistry(cls).deregister(obj);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/service/ServiceRegistry$CategoryRegistry.class */
    class CategoryRegistry<T> {
        private final Class<T> category;
        private final Map<Class, T> providers = new LinkedHashMap();

        CategoryRegistry(Class<T> cls) {
            Validate.notNull(cls, "category");
            this.category = cls;
        }

        private void checkCategory(Object obj) {
            if (!this.category.isInstance(obj)) {
                throw new IllegalArgumentException(obj + " not instance of category " + this.category.getName());
            }
        }

        public boolean register(T t) {
            checkCategory(t);
            if (!contains(t)) {
                this.providers.put(t.getClass(), t);
                processRegistration(t);
                return true;
            }
            return false;
        }

        void processRegistration(T t) {
            if (t instanceof RegisterableService) {
                ((RegisterableService) t).onRegistration(ServiceRegistry.this, this.category);
            }
        }

        public boolean deregister(Object obj) {
            checkCategory(obj);
            T tRemove = this.providers.remove(obj.getClass());
            if (tRemove != null) {
                processDeregistration(tRemove);
                return true;
            }
            return false;
        }

        void processDeregistration(T t) {
            if (t instanceof RegisterableService) {
                ((RegisterableService) t).onDeregistration(ServiceRegistry.this, this.category);
            }
        }

        public boolean contains(Object obj) {
            return this.providers.containsKey(obj != null ? obj.getClass() : null);
        }

        public Iterator<T> providers() {
            final Iterator<T> it = this.providers.values().iterator();
            return new Iterator<T>() { // from class: com.twelvemonkeys.util.service.ServiceRegistry.CategoryRegistry.1
                T current;

                @Override // java.util.Iterator
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override // java.util.Iterator
                public T next() {
                    T t = (T) it.next();
                    this.current = t;
                    return t;
                }

                @Override // java.util.Iterator
                public void remove() {
                    it.remove();
                    CategoryRegistry.this.processDeregistration(this.current);
                }
            };
        }
    }

    /* JADX INFO: renamed from: com.twelvemonkeys.util.service.ServiceRegistry$1Spi, reason: invalid class name */
    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/service/ServiceRegistry$1Spi.class */
    abstract class C1Spi {
        C1Spi() {
        }
    }

    public static void main(String[] strArr) {
        ServiceRegistry serviceRegistry = new ServiceRegistry(Arrays.asList(CharsetProvider.class, SelectorProvider.class, ImageReaderSpi.class, ImageWriterSpi.class, C1Spi.class).iterator());
        serviceRegistry.registerApplicationClasspathSPIs();
        C1Spi c1Spi = new C1Spi() { // from class: com.twelvemonkeys.util.service.ServiceRegistry.1One
        };
        C1Spi c1Spi2 = new C1Spi() { // from class: com.twelvemonkeys.util.service.ServiceRegistry.1Two
        };
        serviceRegistry.register(c1Spi, C1Spi.class);
        serviceRegistry.register(c1Spi2, C1Spi.class);
        serviceRegistry.deregister(c1Spi);
        serviceRegistry.deregister(c1Spi, C1Spi.class);
        serviceRegistry.deregister(c1Spi2, C1Spi.class);
        serviceRegistry.deregister(c1Spi2);
        Iterator<Class<?>> itCategories = serviceRegistry.categories();
        System.out.println("Categories: ");
        while (itCategories.hasNext()) {
            Class<?> next = itCategories.next();
            System.out.println("  " + next.getName() + ":");
            Iterator itProviders = serviceRegistry.providers(next);
            Object next2 = null;
            while (itProviders.hasNext()) {
                next2 = itProviders.next();
                System.out.println("    " + next2);
                if (next2 instanceof ImageReaderWriterSpi) {
                    System.out.println("    - " + ((ImageReaderWriterSpi) next2).getDescription((Locale) null));
                }
                if (itProviders.hasNext()) {
                    itProviders.remove();
                }
            }
            if (next2 != null) {
                Iterator<Class<?>> itContainingCategories = serviceRegistry.containingCategories(next2);
                int i = 0;
                while (itContainingCategories.hasNext()) {
                    if (next == itContainingCategories.next()) {
                        itContainingCategories.remove();
                        i++;
                    }
                }
                if (i != 1) {
                    System.err.println("Removed " + next2 + " from " + i + " categories");
                }
            }
            Iterator itProviders2 = serviceRegistry.providers(next);
            if (!itProviders2.hasNext()) {
                System.out.println("All providers successfully deregistered");
            }
            while (itProviders2.hasNext()) {
                System.err.println("Not removed: " + itProviders2.next());
            }
        }
    }
}
