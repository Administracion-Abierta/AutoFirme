package com.ctc.wstx.shaded.msv.org_isorelax.jaxp;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Schema;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv/org_isorelax/jaxp/ValidatingSAXParserFactory.class */
public class ValidatingSAXParserFactory extends SAXParserFactory {
    protected SAXParserFactory _WrappedFactory;
    protected Schema _Schema;
    private boolean validation = true;

    protected ValidatingSAXParserFactory(SAXParserFactory saxparserfactory, Schema schema) {
        this._WrappedFactory = saxparserfactory;
        this._Schema = schema;
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public SAXParser newSAXParser() throws ParserConfigurationException, SAXException {
        if (this._WrappedFactory.isValidating()) {
            try {
                return new ValidatingSAXParser(this._WrappedFactory.newSAXParser(), this._Schema.newVerifier());
            } catch (VerifierConfigurationException verifierconfigurationexception) {
                throw new ParserConfigurationException(verifierconfigurationexception.getMessage());
            }
        }
        return this._WrappedFactory.newSAXParser();
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public void setFeature(String s, boolean flag) throws SAXNotRecognizedException, SAXNotSupportedException, ParserConfigurationException {
        this._WrappedFactory.setFeature(s, flag);
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public boolean getFeature(String s) throws SAXNotRecognizedException, SAXNotSupportedException, ParserConfigurationException {
        return this._WrappedFactory.getFeature(s);
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public boolean isNamespaceAware() {
        return this._WrappedFactory.isNamespaceAware();
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public void setNamespaceAware(boolean flag) {
        this._WrappedFactory.setNamespaceAware(flag);
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public boolean isValidating() {
        return this.validation;
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public void setValidating(boolean flag) {
        this.validation = flag;
    }

    public static ValidatingSAXParserFactory newInstance(Schema schema) {
        return new ValidatingSAXParserFactory(SAXParserFactory.newInstance(), schema);
    }
}
