package com.ctc.wstx.shaded.msv.org_isorelax.verifier.impl;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierConfigurationException;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFilter;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierHandler;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/org_isorelax/verifier/impl/VerifierImpl.class */
public abstract class VerifierImpl implements Verifier {
    protected XMLReader reader;
    protected ErrorHandler errorHandler = new ErrorHandler() { // from class: com.ctc.wstx.shaded.msv.org_isorelax.verifier.impl.VerifierImpl.1
        @Override // org.xml.sax.ErrorHandler
        public void warning(SAXParseException e) {
        }

        @Override // org.xml.sax.ErrorHandler
        public void error(SAXParseException e) {
        }

        @Override // org.xml.sax.ErrorHandler
        public void fatalError(SAXParseException e) {
        }
    };
    protected EntityResolver entityResolver;
    private VerifierFilter filter;

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier
    public abstract VerifierHandler getVerifierHandler() throws SAXException;

    protected VerifierImpl() throws VerifierConfigurationException {
        prepareXMLReader();
    }

    protected void prepareXMLReader() throws VerifierConfigurationException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            this.reader = factory.newSAXParser().getXMLReader();
        } catch (ParserConfigurationException pce) {
            throw new VerifierConfigurationException(pce);
        } catch (SAXException e) {
            throw new VerifierConfigurationException(e);
        }
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier
    public boolean isFeature(String feature) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (Verifier.FEATURE_HANDLER.equals(feature) || Verifier.FEATURE_FILTER.equals(feature)) {
            return true;
        }
        throw new SAXNotRecognizedException(feature);
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier
    public void setFeature(String feature, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException(feature);
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier
    public Object getProperty(String property) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException(property);
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier
    public void setProperty(String property, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException(property);
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier
    public void setErrorHandler(ErrorHandler handler) {
        this.errorHandler = handler;
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier
    public void setEntityResolver(EntityResolver resolver) {
        this.entityResolver = resolver;
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier
    public boolean verify(String uri) throws SAXException, IOException {
        return verify(new InputSource(uri));
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier
    public boolean verify(InputSource source) throws SAXException, IOException {
        VerifierHandler handler = getVerifierHandler();
        this.reader.setErrorHandler(this.errorHandler);
        if (this.entityResolver != null) {
            this.reader.setEntityResolver(this.entityResolver);
        }
        this.reader.setContentHandler(handler);
        this.reader.parse(source);
        return handler.isValid();
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier
    public boolean verify(File f) throws SAXException, IOException {
        String uri = "file:" + f.getAbsolutePath();
        if (File.separatorChar == '\\') {
            uri = uri.replace('\\', '/');
        }
        return verify(new InputSource(uri));
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier
    public boolean verify(Node node) throws SAXException {
        SAXEventGenerator generator = new SAXEventGenerator(node);
        generator.setDocumentEmulation(true);
        generator.setErrorHandler(this.errorHandler);
        VerifierHandler handler = getVerifierHandler();
        generator.makeEvent(handler);
        return handler.isValid();
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier
    public VerifierFilter getVerifierFilter() throws SAXException {
        if (this.filter == null) {
            this.filter = new VerifierFilterImpl(this);
        }
        return this.filter;
    }
}
