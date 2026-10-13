package org.spongycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/CertificateURL.class */
public class CertificateURL {
    protected short type;
    protected Vector urlAndHashList;

    public CertificateURL(short type, Vector urlAndHashList) {
        if (!CertChainType.isValid(type)) {
            throw new IllegalArgumentException("'type' is not a valid CertChainType value");
        }
        if (urlAndHashList == null || urlAndHashList.isEmpty()) {
            throw new IllegalArgumentException("'urlAndHashList' must have length > 0");
        }
        this.type = type;
        this.urlAndHashList = urlAndHashList;
    }

    public short getType() {
        return this.type;
    }

    public Vector getURLAndHashList() {
        return this.urlAndHashList;
    }

    public void encode(OutputStream output) throws IOException {
        TlsUtils.writeUint8(this.type, output);
        ListBuffer16 buf = new ListBuffer16();
        for (int i = 0; i < this.urlAndHashList.size(); i++) {
            URLAndHash urlAndHash = (URLAndHash) this.urlAndHashList.elementAt(i);
            urlAndHash.encode(buf);
        }
        buf.encodeTo(output);
    }

    public static CertificateURL parse(TlsContext context, InputStream input) throws IOException {
        short type = TlsUtils.readUint8(input);
        if (!CertChainType.isValid(type)) {
            throw new TlsFatalAlert((short) 50);
        }
        int totalLength = TlsUtils.readUint16(input);
        if (totalLength < 1) {
            throw new TlsFatalAlert((short) 50);
        }
        byte[] urlAndHashListData = TlsUtils.readFully(totalLength, input);
        ByteArrayInputStream buf = new ByteArrayInputStream(urlAndHashListData);
        Vector url_and_hash_list = new Vector();
        while (buf.available() > 0) {
            URLAndHash url_and_hash = URLAndHash.parse(context, buf);
            url_and_hash_list.addElement(url_and_hash);
        }
        return new CertificateURL(type, url_and_hash_list);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/CertificateURL$ListBuffer16.class */
    class ListBuffer16 extends ByteArrayOutputStream {
        ListBuffer16() throws IOException {
            TlsUtils.writeUint16(0, this);
        }

        void encodeTo(OutputStream output) throws IOException {
            int length = this.count - 2;
            TlsUtils.checkUint16(length);
            TlsUtils.writeUint16(length, this.buf, 0);
            output.write(this.buf, 0, this.count);
            this.buf = null;
        }
    }
}
