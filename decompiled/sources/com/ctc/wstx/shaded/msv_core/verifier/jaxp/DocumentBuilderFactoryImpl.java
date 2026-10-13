package com.ctc.wstx.shaded.msv_core.verifier.jaxp;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Schema;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFactory;
import com.ctc.wstx.shaded.msv_core.verifier.jarv.TheFactoryImpl;
import java.io.File;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/verifier/jaxp/DocumentBuilderFactoryImpl.class */
public class DocumentBuilderFactoryImpl extends DocumentBuilderFactory {
    private final DocumentBuilderFactory core;
    private final VerifierFactory jarvFactory;
    private Schema schema;

    public DocumentBuilderFactoryImpl() {
        this(DocumentBuilderFactory.newInstance());
    }

    public DocumentBuilderFactoryImpl(DocumentBuilderFactory _factory) {
        this(_factory, null);
    }

    public DocumentBuilderFactoryImpl(DocumentBuilderFactory _factory, Schema _schema) {
        this.core = _factory;
        this.jarvFactory = new TheFactoryImpl();
        this.schema = _schema;
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public Object getAttribute(String name) {
        if (name.equals(com.ctc.wstx.shaded.msv_core.verifier.jarv.Const.PANIC_MODE_FEATURE)) {
            try {
                return this.jarvFactory.isFeature(name) ? Boolean.TRUE : Boolean.FALSE;
            } catch (SAXException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        return this.core.getAttribute(name);
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public void setAttribute(String name, Object value) {
        if (name.equals(com.ctc.wstx.shaded.msv_core.verifier.jarv.Const.PANIC_MODE_FEATURE)) {
            try {
                this.jarvFactory.setFeature(name, ((Boolean) value).booleanValue());
            } catch (SAXException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        if (Const.SCHEMA_PROPNAME.equals(name)) {
            try {
                if (value instanceof String) {
                    this.schema = this.jarvFactory.compileSchema((String) value);
                    return;
                }
                if (value instanceof File) {
                    this.schema = this.jarvFactory.compileSchema((File) value);
                    return;
                }
                if (value instanceof InputSource) {
                    this.schema = this.jarvFactory.compileSchema((InputSource) value);
                    return;
                } else if (value instanceof InputStream) {
                    this.schema = this.jarvFactory.compileSchema((InputStream) value);
                    return;
                } else {
                    if (value instanceof Schema) {
                        this.schema = (Schema) value;
                        return;
                    }
                    throw new IllegalArgumentException("unrecognized value type: " + value.getClass().getName());
                }
            } catch (Exception e2) {
                throw new IllegalArgumentException(e2.toString());
            }
        }
        this.core.setAttribute(name, value);
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public boolean isCoalescing() {
        return this.core.isCoalescing();
    }

    public boolean isExpandEntityReference() {
        return this.core.isExpandEntityReferences();
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public boolean isIgnoringComments() {
        return this.core.isIgnoringComments();
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public boolean isIgnoringElementContentWhitespace() {
        return this.core.isIgnoringElementContentWhitespace();
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public boolean isNamespaceAware() {
        return this.core.isNamespaceAware();
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public boolean isValidating() {
        return this.core.isValidating();
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        return this.schema == null ? this.core.newDocumentBuilder() : new DocumentBuilderImpl(this.core.newDocumentBuilder(), this.schema);
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public void setCoalescing(boolean newVal) {
        this.core.setCoalescing(newVal);
    }

    public void setExpandEntityReference(boolean newVal) {
        this.core.setExpandEntityReferences(newVal);
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public void setIgnoringComments(boolean newVal) {
        this.core.setIgnoringComments(newVal);
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public void setIgnoringElementContentWhitespace(boolean newVal) {
        this.core.setIgnoringElementContentWhitespace(newVal);
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public void setNamespaceAware(boolean newVal) {
        this.core.setNamespaceAware(newVal);
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public void setValidating(boolean newVal) {
        this.core.setValidating(newVal);
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public boolean getFeature(String name) {
        throw new UnsupportedOperationException();
    }

    @Override // javax.xml.parsers.DocumentBuilderFactory
    public void setFeature(String name, boolean value) {
        throw new UnsupportedOperationException();
    }
}
