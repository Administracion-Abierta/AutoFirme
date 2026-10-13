package es.uji.crypto.xades.jxades.util;

import java.io.OutputStream;
import java.io.Writer;
import org.w3c.dom.ls.LSOutput;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/util/DOMOutputImpl.class */
public class DOMOutputImpl implements LSOutput {
    protected Writer fCharStream = null;
    protected OutputStream fByteStream = null;
    protected String fSystemId = null;
    protected String fEncoding = null;

    @Override // org.w3c.dom.ls.LSOutput
    public Writer getCharacterStream() {
        return this.fCharStream;
    }

    @Override // org.w3c.dom.ls.LSOutput
    public void setCharacterStream(Writer characterStream) {
        this.fCharStream = characterStream;
    }

    @Override // org.w3c.dom.ls.LSOutput
    public OutputStream getByteStream() {
        return this.fByteStream;
    }

    @Override // org.w3c.dom.ls.LSOutput
    public void setByteStream(OutputStream byteStream) {
        this.fByteStream = byteStream;
    }

    @Override // org.w3c.dom.ls.LSOutput
    public String getSystemId() {
        return this.fSystemId;
    }

    @Override // org.w3c.dom.ls.LSOutput
    public void setSystemId(String systemId) {
        this.fSystemId = systemId;
    }

    @Override // org.w3c.dom.ls.LSOutput
    public String getEncoding() {
        return this.fEncoding;
    }

    @Override // org.w3c.dom.ls.LSOutput
    public void setEncoding(String encoding) {
        this.fEncoding = encoding;
    }
}
