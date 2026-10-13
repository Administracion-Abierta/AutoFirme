package jakarta.xml.bind.helpers;

import com.ctc.wstx.io.CharsetNames;
import es.gob.afirma.signers.tsp.pkcs7.TsaParams;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.PropertyException;
import jakarta.xml.bind.ValidationEventHandler;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.attachment.AttachmentMarshaller;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:jakarta/xml/bind/helpers/AbstractMarshallerImpl.class */
public abstract class AbstractMarshallerImpl implements Marshaller {
    private ValidationEventHandler eventHandler = new DefaultValidationEventHandler();
    private String encoding = "UTF-8";
    private String schemaLocation = null;
    private String noNSSchemaLocation = null;
    private boolean formattedOutput = false;
    private boolean fragment = false;
    static String[] aliases = {"UTF-8", "UTF8", CharsetNames.CS_UTF16, "Unicode", CharsetNames.CS_UTF16BE, "UnicodeBigUnmarked", CharsetNames.CS_UTF16LE, "UnicodeLittleUnmarked", CharsetNames.CS_US_ASCII, "ASCII", "TIS-620", "TIS620", "ISO-10646-UCS-2", "Unicode", "EBCDIC-CP-US", "cp037", "EBCDIC-CP-CA", "cp037", "EBCDIC-CP-NL", "cp037", "EBCDIC-CP-WT", "cp037", "EBCDIC-CP-DK", "cp277", "EBCDIC-CP-NO", "cp277", "EBCDIC-CP-FI", "cp278", "EBCDIC-CP-SE", "cp278", "EBCDIC-CP-IT", "cp280", "EBCDIC-CP-ES", "cp284", "EBCDIC-CP-GB", "cp285", "EBCDIC-CP-FR", "cp297", "EBCDIC-CP-AR1", "cp420", "EBCDIC-CP-HE", "cp424", "EBCDIC-CP-BE", "cp500", "EBCDIC-CP-CH", "cp500", "EBCDIC-CP-ROECE", "cp870", "EBCDIC-CP-YU", "cp870", "EBCDIC-CP-IS", "cp871", "EBCDIC-CP-AR2", "cp918"};

    @Override // jakarta.xml.bind.Marshaller
    public final void marshal(Object obj, OutputStream os) throws JAXBException {
        checkNotNull(obj, "obj", os, "os");
        marshal(obj, new StreamResult(os));
    }

    @Override // jakarta.xml.bind.Marshaller
    public void marshal(Object jaxbElement, File output) throws JAXBException {
        checkNotNull(jaxbElement, "jaxbElement", output, "output");
        try {
            OutputStream os = new BufferedOutputStream(new FileOutputStream(output));
            try {
                marshal(jaxbElement, new StreamResult(os));
            } finally {
                os.close();
            }
        } catch (IOException e) {
            throw new JAXBException(e);
        }
    }

    @Override // jakarta.xml.bind.Marshaller
    public final void marshal(Object obj, Writer w) throws JAXBException {
        checkNotNull(obj, "obj", w, "writer");
        marshal(obj, new StreamResult(w));
    }

    @Override // jakarta.xml.bind.Marshaller
    public final void marshal(Object obj, ContentHandler handler) throws JAXBException {
        checkNotNull(obj, "obj", handler, "handler");
        marshal(obj, new SAXResult(handler));
    }

    @Override // jakarta.xml.bind.Marshaller
    public final void marshal(Object obj, Node node) throws JAXBException {
        checkNotNull(obj, "obj", node, "node");
        marshal(obj, new DOMResult(node));
    }

    @Override // jakarta.xml.bind.Marshaller
    public Node getNode(Object obj) throws JAXBException {
        checkNotNull(obj, "obj", Boolean.TRUE, "foo");
        throw new UnsupportedOperationException();
    }

    protected String getEncoding() {
        return this.encoding;
    }

    protected void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    protected String getSchemaLocation() {
        return this.schemaLocation;
    }

    protected void setSchemaLocation(String location) {
        this.schemaLocation = location;
    }

    protected String getNoNSSchemaLocation() {
        return this.noNSSchemaLocation;
    }

    protected void setNoNSSchemaLocation(String location) {
        this.noNSSchemaLocation = location;
    }

    protected boolean isFormattedOutput() {
        return this.formattedOutput;
    }

    protected void setFormattedOutput(boolean v) {
        this.formattedOutput = v;
    }

    protected boolean isFragment() {
        return this.fragment;
    }

    protected void setFragment(boolean v) {
        this.fragment = v;
    }

    protected String getJavaEncoding(String encoding) throws UnsupportedEncodingException {
        try {
            TsaParams.TS_SIGN.getBytes(encoding);
            return encoding;
        } catch (UnsupportedEncodingException e) {
            for (int i = 0; i < aliases.length; i += 2) {
                if (encoding.equals(aliases[i])) {
                    TsaParams.TS_SIGN.getBytes(aliases[i + 1]);
                    return aliases[i + 1];
                }
            }
            throw new UnsupportedEncodingException(encoding);
        }
    }

    @Override // jakarta.xml.bind.Marshaller
    public void setProperty(String name, Object value) throws PropertyException {
        if (name == null) {
            throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "name"));
        }
        if (Marshaller.JAXB_ENCODING.equals(name)) {
            checkString(name, value);
            setEncoding((String) value);
            return;
        }
        if (Marshaller.JAXB_FORMATTED_OUTPUT.equals(name)) {
            checkBoolean(name, value);
            setFormattedOutput(((Boolean) value).booleanValue());
            return;
        }
        if (Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION.equals(name)) {
            checkString(name, value);
            setNoNSSchemaLocation((String) value);
        } else if (Marshaller.JAXB_SCHEMA_LOCATION.equals(name)) {
            checkString(name, value);
            setSchemaLocation((String) value);
        } else {
            if (Marshaller.JAXB_FRAGMENT.equals(name)) {
                checkBoolean(name, value);
                setFragment(((Boolean) value).booleanValue());
                return;
            }
            throw new PropertyException(name, value);
        }
    }

    @Override // jakarta.xml.bind.Marshaller
    public Object getProperty(String name) throws PropertyException {
        if (name == null) {
            throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "name"));
        }
        if (Marshaller.JAXB_ENCODING.equals(name)) {
            return getEncoding();
        }
        if (Marshaller.JAXB_FORMATTED_OUTPUT.equals(name)) {
            return isFormattedOutput() ? Boolean.TRUE : Boolean.FALSE;
        }
        if (Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION.equals(name)) {
            return getNoNSSchemaLocation();
        }
        if (Marshaller.JAXB_SCHEMA_LOCATION.equals(name)) {
            return getSchemaLocation();
        }
        if (Marshaller.JAXB_FRAGMENT.equals(name)) {
            return isFragment() ? Boolean.TRUE : Boolean.FALSE;
        }
        throw new PropertyException(name);
    }

    @Override // jakarta.xml.bind.Marshaller
    public ValidationEventHandler getEventHandler() throws JAXBException {
        return this.eventHandler;
    }

    @Override // jakarta.xml.bind.Marshaller
    public void setEventHandler(ValidationEventHandler handler) throws JAXBException {
        if (handler == null) {
            this.eventHandler = new DefaultValidationEventHandler();
        } else {
            this.eventHandler = handler;
        }
    }

    private void checkBoolean(String name, Object value) throws PropertyException {
        if (!(value instanceof Boolean)) {
            throw new PropertyException(Messages.format("AbstractMarshallerImpl.MustBeBoolean", name));
        }
    }

    private void checkString(String name, Object value) throws PropertyException {
        if (!(value instanceof String)) {
            throw new PropertyException(Messages.format("AbstractMarshallerImpl.MustBeString", name));
        }
    }

    private void checkNotNull(Object o1, String o1Name, Object o2, String o2Name) {
        if (o1 == null) {
            throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", o1Name));
        }
        if (o2 == null) {
            throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", o2Name));
        }
    }

    @Override // jakarta.xml.bind.Marshaller
    public void marshal(Object obj, XMLEventWriter writer) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    @Override // jakarta.xml.bind.Marshaller
    public void marshal(Object obj, XMLStreamWriter writer) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    @Override // jakarta.xml.bind.Marshaller
    public void setSchema(Schema schema) {
        throw new UnsupportedOperationException();
    }

    @Override // jakarta.xml.bind.Marshaller
    public Schema getSchema() {
        throw new UnsupportedOperationException();
    }

    @Override // jakarta.xml.bind.Marshaller
    public void setAdapter(XmlAdapter adapter) {
        if (adapter == null) {
            throw new IllegalArgumentException();
        }
        setAdapter(adapter.getClass(), adapter);
    }

    @Override // jakarta.xml.bind.Marshaller
    public <A extends XmlAdapter> void setAdapter(Class<A> type, A adapter) {
        throw new UnsupportedOperationException();
    }

    @Override // jakarta.xml.bind.Marshaller
    public <A extends XmlAdapter> A getAdapter(Class<A> type) {
        throw new UnsupportedOperationException();
    }

    @Override // jakarta.xml.bind.Marshaller
    public void setAttachmentMarshaller(AttachmentMarshaller am) {
        throw new UnsupportedOperationException();
    }

    @Override // jakarta.xml.bind.Marshaller
    public AttachmentMarshaller getAttachmentMarshaller() {
        throw new UnsupportedOperationException();
    }

    @Override // jakarta.xml.bind.Marshaller
    public void setListener(Marshaller.Listener listener) {
        throw new UnsupportedOperationException();
    }

    @Override // jakarta.xml.bind.Marshaller
    public Marshaller.Listener getListener() {
        throw new UnsupportedOperationException();
    }
}
