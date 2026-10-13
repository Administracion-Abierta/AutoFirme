package jakarta.xml.bind.helpers;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.PropertyException;
import jakarta.xml.bind.UnmarshalException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.ValidationEventHandler;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.attachment.AttachmentUnmarshaller;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:jakarta/xml/bind/helpers/AbstractUnmarshallerImpl.class */
public abstract class AbstractUnmarshallerImpl implements Unmarshaller {
    private ValidationEventHandler eventHandler = new DefaultValidationEventHandler();
    protected boolean validating = false;
    private XMLReader reader = null;

    protected abstract Object unmarshal(XMLReader xMLReader, InputSource inputSource) throws JAXBException;

    protected XMLReader getXMLReader() throws JAXBException {
        if (this.reader == null) {
            try {
                SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                parserFactory.setNamespaceAware(true);
                parserFactory.setValidating(false);
                this.reader = parserFactory.newSAXParser().getXMLReader();
            } catch (ParserConfigurationException e) {
                throw new JAXBException(e);
            } catch (SAXException e2) {
                throw new JAXBException(e2);
            }
        }
        return this.reader;
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public Object unmarshal(Source source) throws JAXBException {
        if (source == null) {
            throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "source"));
        }
        if (source instanceof SAXSource) {
            return unmarshal((SAXSource) source);
        }
        if (source instanceof StreamSource) {
            return unmarshal(streamSourceToInputSource((StreamSource) source));
        }
        if (source instanceof DOMSource) {
            return unmarshal(((DOMSource) source).getNode());
        }
        throw new IllegalArgumentException();
    }

    private Object unmarshal(SAXSource source) throws JAXBException {
        XMLReader r = source.getXMLReader();
        if (r == null) {
            r = getXMLReader();
        }
        return unmarshal(r, source.getInputSource());
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public final Object unmarshal(InputSource source) throws JAXBException {
        if (source == null) {
            throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "source"));
        }
        return unmarshal(getXMLReader(), source);
    }

    private Object unmarshal(String url) throws JAXBException {
        return unmarshal(new InputSource(url));
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public final Object unmarshal(URL url) throws JAXBException {
        if (url == null) {
            throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "url"));
        }
        return unmarshal(url.toExternalForm());
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public final Object unmarshal(File f) throws JAXBException {
        if (f == null) {
            throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "file"));
        }
        try {
            return unmarshal(new BufferedInputStream(new FileInputStream(f)));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public final Object unmarshal(InputStream is) throws JAXBException {
        if (is == null) {
            throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "is"));
        }
        InputSource isrc = new InputSource(is);
        return unmarshal(isrc);
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public final Object unmarshal(Reader reader) throws JAXBException {
        if (reader == null) {
            throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "reader"));
        }
        InputSource isrc = new InputSource(reader);
        return unmarshal(isrc);
    }

    private static InputSource streamSourceToInputSource(StreamSource ss) {
        InputSource is = new InputSource();
        is.setSystemId(ss.getSystemId());
        is.setByteStream(ss.getInputStream());
        is.setCharacterStream(ss.getReader());
        return is;
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public boolean isValidating() throws JAXBException {
        return this.validating;
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public void setEventHandler(ValidationEventHandler handler) throws JAXBException {
        if (handler == null) {
            this.eventHandler = new DefaultValidationEventHandler();
        } else {
            this.eventHandler = handler;
        }
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public void setValidating(boolean validating) throws JAXBException {
        this.validating = validating;
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public ValidationEventHandler getEventHandler() throws JAXBException {
        return this.eventHandler;
    }

    protected UnmarshalException createUnmarshalException(SAXException e) {
        Exception nested = e.getException();
        if (nested instanceof UnmarshalException) {
            return (UnmarshalException) nested;
        }
        if (nested instanceof RuntimeException) {
            throw ((RuntimeException) nested);
        }
        if (nested != null) {
            return new UnmarshalException(nested);
        }
        return new UnmarshalException(e);
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public void setProperty(String name, Object value) throws PropertyException {
        if (name == null) {
            throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "name"));
        }
        throw new PropertyException(name, value);
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public Object getProperty(String name) throws PropertyException {
        if (name == null) {
            throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "name"));
        }
        throw new PropertyException(name);
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public Object unmarshal(XMLEventReader reader) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public Object unmarshal(XMLStreamReader reader) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public <T> JAXBElement<T> unmarshal(Node node, Class<T> expectedType) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public <T> JAXBElement<T> unmarshal(Source source, Class<T> expectedType) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public <T> JAXBElement<T> unmarshal(XMLStreamReader reader, Class<T> expectedType) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public <T> JAXBElement<T> unmarshal(XMLEventReader reader, Class<T> expectedType) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public void setSchema(Schema schema) {
        throw new UnsupportedOperationException();
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public Schema getSchema() {
        throw new UnsupportedOperationException();
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public void setAdapter(XmlAdapter adapter) {
        if (adapter == null) {
            throw new IllegalArgumentException();
        }
        setAdapter(adapter.getClass(), adapter);
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public <A extends XmlAdapter> void setAdapter(Class<A> type, A adapter) {
        throw new UnsupportedOperationException();
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public <A extends XmlAdapter> A getAdapter(Class<A> type) {
        throw new UnsupportedOperationException();
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public void setAttachmentUnmarshaller(AttachmentUnmarshaller au) {
        throw new UnsupportedOperationException();
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public AttachmentUnmarshaller getAttachmentUnmarshaller() {
        throw new UnsupportedOperationException();
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public void setListener(Unmarshaller.Listener listener) {
        throw new UnsupportedOperationException();
    }

    @Override // jakarta.xml.bind.Unmarshaller
    public Unmarshaller.Listener getListener() {
        throw new UnsupportedOperationException();
    }
}
