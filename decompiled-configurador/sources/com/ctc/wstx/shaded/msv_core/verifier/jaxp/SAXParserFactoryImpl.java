package com.ctc.wstx.shaded.msv_core.verifier.jaxp;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Schema;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierConfigurationException;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFactory;
import com.ctc.wstx.shaded.msv_core.verifier.jarv.TheFactoryImpl;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/jaxp/SAXParserFactoryImpl.class */
public class SAXParserFactoryImpl extends SAXParserFactory {
    private final SAXParserFactory core;
    private final VerifierFactory jarvFactory;
    private Schema schema;

    public SAXParserFactoryImpl() {
        this(SAXParserFactory.newInstance());
    }

    public SAXParserFactoryImpl(SAXParserFactory _factory) {
        this(_factory, null);
    }

    public SAXParserFactoryImpl(Schema schema) {
        this(SAXParserFactory.newInstance(), schema);
    }

    public SAXParserFactoryImpl(File schemaAsFile) throws VerifierConfigurationException, SAXException, IOException {
        this();
        this.schema = this.jarvFactory.compileSchema(schemaAsFile);
    }

    public SAXParserFactoryImpl(InputSource _schema) throws VerifierConfigurationException, SAXException, IOException {
        this();
        this.schema = this.jarvFactory.compileSchema(_schema);
    }

    public SAXParserFactoryImpl(String schemaUrl) throws VerifierConfigurationException, SAXException, IOException {
        this();
        this.schema = this.jarvFactory.compileSchema(schemaUrl);
    }

    public SAXParserFactoryImpl(SAXParserFactory _factory, Schema _schema) {
        this.core = _factory;
        this.core.setNamespaceAware(true);
        this.jarvFactory = new TheFactoryImpl(this.core);
        this.schema = _schema;
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException, ParserConfigurationException {
        if (name.equals(com.ctc.wstx.shaded.msv_core.verifier.jarv.Const.PANIC_MODE_FEATURE)) {
            return this.jarvFactory.isFeature(name);
        }
        return this.core.getFeature(name);
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException, ParserConfigurationException {
        if (name.equals(com.ctc.wstx.shaded.msv_core.verifier.jarv.Const.PANIC_MODE_FEATURE)) {
            this.jarvFactory.setFeature(name, value);
        }
        this.core.setFeature(name, value);
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public SAXParser newSAXParser() throws ParserConfigurationException, SAXException {
        try {
            return new SAXParserImpl(this.core.newSAXParser(), this.jarvFactory, this.schema == null ? null : this.schema.newVerifier());
        } catch (VerifierConfigurationException e) {
            throw new SAXException(e);
        }
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public void setNamespaceAware(boolean awareness) {
        this.core.setNamespaceAware(awareness);
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public boolean isNamespaceAware() {
        return this.core.isNamespaceAware();
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public void setValidating(boolean validating) {
        this.core.setValidating(validating);
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public boolean isValidating() {
        return this.core.isValidating();
    }
}
