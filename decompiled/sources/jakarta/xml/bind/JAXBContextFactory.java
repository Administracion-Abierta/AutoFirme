package jakarta.xml.bind;

import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/JAXBContextFactory.class */
public interface JAXBContextFactory {
    JAXBContext createContext(Class<?>[] clsArr, Map<String, ?> map) throws JAXBException;

    JAXBContext createContext(String str, ClassLoader classLoader, Map<String, ?> map) throws JAXBException;
}
