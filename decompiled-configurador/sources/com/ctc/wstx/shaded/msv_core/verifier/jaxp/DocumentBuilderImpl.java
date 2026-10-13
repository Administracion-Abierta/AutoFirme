package com.ctc.wstx.shaded.msv_core.verifier.jaxp;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Schema;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier;
import com.ctc.wstx.shaded.msv_core.verifier.util.ErrorHandlerImpl;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/jaxp/DocumentBuilderImpl.class */
class DocumentBuilderImpl extends DocumentBuilder {
    private final DocumentBuilder core;
    private final Verifier verifier;

    DocumentBuilderImpl(DocumentBuilder _core, Schema _schema) throws ParserConfigurationException {
        this.core = _core;
        try {
            this.verifier = _schema.newVerifier();
            this.verifier.setErrorHandler(ErrorHandlerImpl.theInstance);
        } catch (Exception e) {
            throw new ParserConfigurationException(e.toString());
        }
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public DOMImplementation getDOMImplementation() {
        return this.core.getDOMImplementation();
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public boolean isNamespaceAware() {
        return this.core.isNamespaceAware();
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public boolean isValidating() {
        return true;
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public Document newDocument() {
        return this.core.newDocument();
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public Document parse(InputSource is) throws SAXException, IOException {
        return verify(this.core.parse(is));
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public Document parse(File f) throws SAXException, IOException {
        return verify(this.core.parse(f));
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public Document parse(InputStream is) throws SAXException, IOException {
        return verify(this.core.parse(is));
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public Document parse(InputStream is, String systemId) throws SAXException, IOException {
        return verify(this.core.parse(is, systemId));
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public Document parse(String url) throws SAXException, IOException {
        return verify(this.core.parse(url));
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public void setEntityResolver(EntityResolver resolver) {
        this.verifier.setEntityResolver(resolver);
        this.core.setEntityResolver(resolver);
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public void setErrorHandler(ErrorHandler handler) {
        this.verifier.setErrorHandler(handler);
        this.core.setErrorHandler(handler);
    }

    private Document verify(Document dom) throws SAXException, IOException {
        if (this.verifier.verify(dom)) {
            return dom;
        }
        throw new SAXException("the document is invalid");
    }
}
