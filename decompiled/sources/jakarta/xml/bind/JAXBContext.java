package jakarta.xml.bind;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Map;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/JAXBContext.class */
public abstract class JAXBContext {
    public static final String JAXB_CONTEXT_FACTORY = "jakarta.xml.bind.JAXBContextFactory";

    public abstract Unmarshaller createUnmarshaller() throws JAXBException;

    public abstract Marshaller createMarshaller() throws JAXBException;

    @Deprecated
    public abstract Validator createValidator() throws JAXBException;

    protected JAXBContext() {
    }

    public static JAXBContext newInstance(String contextPath) throws JAXBException {
        return newInstance(contextPath, getContextClassLoader());
    }

    public static JAXBContext newInstance(String contextPath, ClassLoader classLoader) throws JAXBException {
        return newInstance(contextPath, classLoader, Collections.emptyMap());
    }

    public static JAXBContext newInstance(String contextPath, ClassLoader classLoader, Map<String, ?> properties) throws JAXBException {
        return ContextFinder.find(JAXB_CONTEXT_FACTORY, contextPath, classLoader, properties);
    }

    public static JAXBContext newInstance(Class<?>... classesToBeBound) throws JAXBException {
        return newInstance(classesToBeBound, (Map<String, ?>) Collections.emptyMap());
    }

    public static JAXBContext newInstance(Class<?>[] classesToBeBound, Map<String, ?> properties) throws JAXBException {
        if (classesToBeBound == null) {
            throw new IllegalArgumentException();
        }
        for (int i = classesToBeBound.length - 1; i >= 0; i--) {
            if (classesToBeBound[i] == null) {
                throw new IllegalArgumentException();
            }
        }
        return ContextFinder.find(classesToBeBound, properties);
    }

    public <T> Binder<T> createBinder(Class<T> domType) {
        throw new UnsupportedOperationException();
    }

    public Binder<Node> createBinder() {
        return createBinder(Node.class);
    }

    public JAXBIntrospector createJAXBIntrospector() {
        throw new UnsupportedOperationException();
    }

    public void generateSchema(SchemaOutputResolver outputResolver) throws IOException {
        throw new UnsupportedOperationException();
    }

    private static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        }
        return (ClassLoader) AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() { // from class: jakarta.xml.bind.JAXBContext.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.security.PrivilegedAction
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
    }
}
