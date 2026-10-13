package jakarta.xml.bind;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.attachment.AttachmentUnmarshaller;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/Unmarshaller.class */
public interface Unmarshaller {
    Object unmarshal(File file) throws JAXBException;

    Object unmarshal(InputStream inputStream) throws JAXBException;

    Object unmarshal(Reader reader) throws JAXBException;

    Object unmarshal(URL url) throws JAXBException;

    Object unmarshal(InputSource inputSource) throws JAXBException;

    Object unmarshal(Node node) throws JAXBException;

    <T> JAXBElement<T> unmarshal(Node node, Class<T> cls) throws JAXBException;

    Object unmarshal(Source source) throws JAXBException;

    <T> JAXBElement<T> unmarshal(Source source, Class<T> cls) throws JAXBException;

    Object unmarshal(XMLStreamReader xMLStreamReader) throws JAXBException;

    <T> JAXBElement<T> unmarshal(XMLStreamReader xMLStreamReader, Class<T> cls) throws JAXBException;

    Object unmarshal(XMLEventReader xMLEventReader) throws JAXBException;

    <T> JAXBElement<T> unmarshal(XMLEventReader xMLEventReader, Class<T> cls) throws JAXBException;

    UnmarshallerHandler getUnmarshallerHandler();

    void setValidating(boolean z) throws JAXBException;

    boolean isValidating() throws JAXBException;

    void setEventHandler(ValidationEventHandler validationEventHandler) throws JAXBException;

    ValidationEventHandler getEventHandler() throws JAXBException;

    void setProperty(String str, Object obj) throws PropertyException;

    Object getProperty(String str) throws PropertyException;

    void setSchema(Schema schema);

    Schema getSchema();

    void setAdapter(XmlAdapter xmlAdapter);

    <A extends XmlAdapter> void setAdapter(Class<A> cls, A a);

    <A extends XmlAdapter> A getAdapter(Class<A> cls);

    void setAttachmentUnmarshaller(AttachmentUnmarshaller attachmentUnmarshaller);

    AttachmentUnmarshaller getAttachmentUnmarshaller();

    void setListener(Listener listener);

    Listener getListener();

    /* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/Unmarshaller$Listener.class */
    public static abstract class Listener {
        public void beforeUnmarshal(Object target, Object parent) {
        }

        public void afterUnmarshal(Object target, Object parent) {
        }
    }
}
