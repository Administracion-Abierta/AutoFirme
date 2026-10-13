package com.ctc.wstx.shaded.msv.org_isorelax.jaxp;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Schema;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/org_isorelax/jaxp/ValidatingDocumentBuilderFactory.class */
public class ValidatingDocumentBuilderFactory extends DocumentBuilderFactory {
    protected Schema _Schema;
    protected DocumentBuilderFactory _WrappedFactory;
    private boolean validation = true;

    protected ValidatingDocumentBuilderFactory(DocumentBuilderFactory documentbuilderfactory, Schema schema) {
        this._WrappedFactory = documentbuilderfactory;
        this._Schema = schema;
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        if (isValidating()) {
            try {
                return new ValidatingDocumentBuilder(this._WrappedFactory.newDocumentBuilder(), this._Schema.newVerifier());
            } catch (VerifierConfigurationException verifierconfigurationexception) {
                throw new ParserConfigurationException(verifierconfigurationexception.getMessage());
            }
        }
        return this._WrappedFactory.newDocumentBuilder();
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public void setAttribute(String s, Object obj) {
        this._WrappedFactory.setAttribute(s, obj);
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public Object getAttribute(String s) {
        return this._WrappedFactory.getAttribute(s);
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public boolean isValidating() {
        return this.validation;
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public void setValidating(boolean flag) {
        this.validation = flag;
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public boolean isCoalescing() {
        return this._WrappedFactory.isCoalescing();
    }

    public boolean isExpandEntityReference() {
        return this._WrappedFactory.isExpandEntityReferences();
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public boolean isIgnoringComments() {
        return this._WrappedFactory.isIgnoringComments();
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public boolean isIgnoringElementContentWhitespace() {
        return this._WrappedFactory.isIgnoringElementContentWhitespace();
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public boolean isNamespaceAware() {
        return this._WrappedFactory.isNamespaceAware();
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public void setCoalescing(boolean flag) {
        this._WrappedFactory.setCoalescing(flag);
    }

    public void setExpandEntityReference(boolean flag) {
        this._WrappedFactory.setExpandEntityReferences(flag);
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public void setIgnoringComments(boolean flag) {
        this._WrappedFactory.setIgnoringComments(flag);
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public void setIgnoringElementContentWhitespace(boolean flag) {
        this._WrappedFactory.setIgnoringElementContentWhitespace(flag);
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public void setNamespaceAware(boolean flag) {
        this._WrappedFactory.setNamespaceAware(flag);
    }

    public static ValidatingDocumentBuilderFactory newInstance(Schema schema) {
        return new ValidatingDocumentBuilderFactory(DocumentBuilderFactory.newInstance(), schema);
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public void setFeature(String name, boolean value) throws ParserConfigurationException {
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public boolean getFeature(String name) throws ParserConfigurationException {
        return false;
    }
}
