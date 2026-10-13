package org.apache.xml.security.stax.impl.util;

import java.io.InputStream;
import java.io.Reader;
import org.w3c.dom.ls.LSInput;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/util/ConcreteLSInput.class */
public class ConcreteLSInput implements LSInput {
    private Reader reader;
    private InputStream inputStream;
    private String stringData;
    private String systemId;
    private String publicId;
    private String baseURI;
    private String encoding;
    private boolean certifiedText;

    @Override // org.w3c.dom.ls.LSInput
    public Reader getCharacterStream() {
        return this.reader;
    }

    @Override // org.w3c.dom.ls.LSInput
    public void setCharacterStream(Reader characterStream) {
        this.reader = characterStream;
    }

    @Override // org.w3c.dom.ls.LSInput
    public InputStream getByteStream() {
        return this.inputStream;
    }

    @Override // org.w3c.dom.ls.LSInput
    public void setByteStream(InputStream byteStream) {
        this.inputStream = byteStream;
    }

    @Override // org.w3c.dom.ls.LSInput
    public String getStringData() {
        return this.stringData;
    }

    @Override // org.w3c.dom.ls.LSInput
    public void setStringData(String stringData) {
        this.stringData = stringData;
    }

    @Override // org.w3c.dom.ls.LSInput
    public String getSystemId() {
        return this.systemId;
    }

    @Override // org.w3c.dom.ls.LSInput
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Override // org.w3c.dom.ls.LSInput
    public String getPublicId() {
        return this.publicId;
    }

    @Override // org.w3c.dom.ls.LSInput
    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    @Override // org.w3c.dom.ls.LSInput
    public String getBaseURI() {
        return this.baseURI;
    }

    @Override // org.w3c.dom.ls.LSInput
    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    @Override // org.w3c.dom.ls.LSInput
    public String getEncoding() {
        return this.encoding;
    }

    @Override // org.w3c.dom.ls.LSInput
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override // org.w3c.dom.ls.LSInput
    public boolean getCertifiedText() {
        return this.certifiedText;
    }

    @Override // org.w3c.dom.ls.LSInput
    public void setCertifiedText(boolean certifiedText) {
        this.certifiedText = certifiedText;
    }
}
