package com.ctc.wstx.shaded.msv.org_isorelax.jaxp;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/org_isorelax/jaxp/ValidatingDocumentBuilder.class */
class ValidatingDocumentBuilder extends DocumentBuilder {
    protected DocumentBuilder _WrappedBuilder;
    protected Verifier _Verifier;

    protected ValidatingDocumentBuilder(DocumentBuilder documentbuilder, Verifier verifier) {
        this._WrappedBuilder = documentbuilder;
        this._Verifier = verifier;
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public Document parse(InputSource inputsource) throws SAXException, IOException {
        return verify(this._WrappedBuilder.parse(inputsource));
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public Document parse(File file) throws SAXException, IOException {
        return verify(this._WrappedBuilder.parse(file));
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public Document parse(InputStream inputstream) throws SAXException, IOException {
        return verify(this._WrappedBuilder.parse(inputstream));
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public Document parse(InputStream inputstream, String s) throws SAXException, IOException {
        return verify(this._WrappedBuilder.parse(inputstream, s));
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public Document parse(String s) throws SAXException, IOException {
        return verify(this._WrappedBuilder.parse(s));
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public boolean isNamespaceAware() {
        return this._WrappedBuilder.isNamespaceAware();
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public boolean isValidating() {
        return true;
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public void setEntityResolver(EntityResolver entityresolver) {
        this._WrappedBuilder.setEntityResolver(entityresolver);
        this._Verifier.setEntityResolver(entityresolver);
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public void setErrorHandler(ErrorHandler errorhandler) {
        this._WrappedBuilder.setErrorHandler(errorhandler);
        this._Verifier.setErrorHandler(errorhandler);
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public Document newDocument() {
        return this._WrappedBuilder.newDocument();
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public DOMImplementation getDOMImplementation() {
        return this._WrappedBuilder.getDOMImplementation();
    }

    private Document verify(Document document) throws SAXException, IOException {
        if (this._Verifier.verify(document)) {
            return document;
        }
        throw new SAXException("the document is invalid, but the ErrorHandler does not throw any Exception.");
    }
}
