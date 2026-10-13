package jakarta.xml.bind;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.attachment.AttachmentMarshaller;
import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.validation.Schema;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:jakarta/xml/bind/Marshaller.class */
public interface Marshaller {
    public static final String JAXB_ENCODING = "jaxb.encoding";
    public static final String JAXB_FORMATTED_OUTPUT = "jaxb.formatted.output";
    public static final String JAXB_SCHEMA_LOCATION = "jaxb.schemaLocation";
    public static final String JAXB_NO_NAMESPACE_SCHEMA_LOCATION = "jaxb.noNamespaceSchemaLocation";
    public static final String JAXB_FRAGMENT = "jaxb.fragment";

    void marshal(Object obj, Result result) throws JAXBException;

    void marshal(Object obj, OutputStream outputStream) throws JAXBException;

    void marshal(Object obj, File file) throws JAXBException;

    void marshal(Object obj, Writer writer) throws JAXBException;

    void marshal(Object obj, ContentHandler contentHandler) throws JAXBException;

    void marshal(Object obj, Node node) throws JAXBException;

    void marshal(Object obj, XMLStreamWriter xMLStreamWriter) throws JAXBException;

    void marshal(Object obj, XMLEventWriter xMLEventWriter) throws JAXBException;

    Node getNode(Object obj) throws JAXBException;

    void setProperty(String str, Object obj) throws PropertyException;

    Object getProperty(String str) throws PropertyException;

    void setEventHandler(ValidationEventHandler validationEventHandler) throws JAXBException;

    ValidationEventHandler getEventHandler() throws JAXBException;

    void setAdapter(XmlAdapter xmlAdapter);

    <A extends XmlAdapter> void setAdapter(Class<A> cls, A a);

    <A extends XmlAdapter> A getAdapter(Class<A> cls);

    void setAttachmentMarshaller(AttachmentMarshaller attachmentMarshaller);

    AttachmentMarshaller getAttachmentMarshaller();

    void setSchema(Schema schema);

    Schema getSchema();

    void setListener(Listener listener);

    Listener getListener();

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:jakarta/xml/bind/Marshaller$Listener.class */
    public static abstract class Listener {
        public void beforeMarshal(Object source) {
        }

        public void afterMarshal(Object source) {
        }
    }
}
