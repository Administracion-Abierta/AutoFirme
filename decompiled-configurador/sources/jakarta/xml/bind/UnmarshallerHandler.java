package jakarta.xml.bind;

import org.xml.sax.ContentHandler;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:jakarta/xml/bind/UnmarshallerHandler.class */
public interface UnmarshallerHandler extends ContentHandler {
    Object getResult() throws IllegalStateException, JAXBException;
}
