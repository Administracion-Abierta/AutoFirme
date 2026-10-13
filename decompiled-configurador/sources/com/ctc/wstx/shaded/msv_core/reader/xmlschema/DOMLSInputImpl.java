package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.reader.DOMLSInput;
import java.io.InputStream;
import java.io.Reader;
import org.w3c.dom.Element;
import org.w3c.dom.ls.LSInput;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/DOMLSInputImpl.class */
public class DOMLSInputImpl implements LSInput, DOMLSInput {
    private String baseURI;
    private String systemId;
    private Element element;

    public DOMLSInputImpl(String baseURI, String systemId, Element data) {
        this.baseURI = baseURI;
        this.element = data;
        this.systemId = systemId;
    }

    @Override // org.w3c.dom.ls.LSInput
    public String getBaseURI() {
        return this.baseURI;
    }

    @Override // org.w3c.dom.ls.LSInput
    public InputStream getByteStream() {
        return null;
    }

    @Override // org.w3c.dom.ls.LSInput
    public boolean getCertifiedText() {
        return false;
    }

    @Override // org.w3c.dom.ls.LSInput
    public Reader getCharacterStream() {
        return null;
    }

    @Override // org.w3c.dom.ls.LSInput
    public String getEncoding() {
        return null;
    }

    @Override // org.w3c.dom.ls.LSInput
    public String getPublicId() {
        return null;
    }

    @Override // org.w3c.dom.ls.LSInput
    public String getStringData() {
        return null;
    }

    @Override // org.w3c.dom.ls.LSInput
    public String getSystemId() {
        return this.systemId;
    }

    @Override // org.w3c.dom.ls.LSInput
    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    @Override // org.w3c.dom.ls.LSInput
    public void setByteStream(InputStream byteStream) {
        throw new UnsupportedOperationException();
    }

    @Override // org.w3c.dom.ls.LSInput
    public void setCertifiedText(boolean certifiedText) {
        throw new UnsupportedOperationException();
    }

    @Override // org.w3c.dom.ls.LSInput
    public void setCharacterStream(Reader characterStream) {
        throw new UnsupportedOperationException();
    }

    @Override // org.w3c.dom.ls.LSInput
    public void setEncoding(String encoding) {
        throw new UnsupportedOperationException();
    }

    @Override // org.w3c.dom.ls.LSInput
    public void setPublicId(String publicId) {
        throw new UnsupportedOperationException();
    }

    @Override // org.w3c.dom.ls.LSInput
    public void setStringData(String stringData) {
        throw new UnsupportedOperationException();
    }

    @Override // org.w3c.dom.ls.LSInput
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.DOMLSInput
    public Element getElement() {
        return this.element;
    }
}
