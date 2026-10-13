package org.spongycastle.cms;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/CMSTypedStream.class */
public class CMSTypedStream {
    private static final int BUF_SIZ = 32768;
    private final ASN1ObjectIdentifier _oid;
    protected InputStream _in;

    public CMSTypedStream(InputStream in) {
        this(PKCSObjectIdentifiers.data.getId(), in, 32768);
    }

    public CMSTypedStream(String oid, InputStream in) {
        this(new ASN1ObjectIdentifier(oid), in, 32768);
    }

    public CMSTypedStream(String oid, InputStream in, int bufSize) {
        this(new ASN1ObjectIdentifier(oid), in, bufSize);
    }

    public CMSTypedStream(ASN1ObjectIdentifier oid, InputStream in) {
        this(oid, in, 32768);
    }

    public CMSTypedStream(ASN1ObjectIdentifier oid, InputStream in, int bufSize) {
        this._oid = oid;
        this._in = new FullReaderStream(new BufferedInputStream(in, bufSize));
    }

    protected CMSTypedStream(ASN1ObjectIdentifier oid) {
        this._oid = oid;
    }

    public ASN1ObjectIdentifier getContentType() {
        return this._oid;
    }

    public InputStream getContentStream() {
        return this._in;
    }

    public void drain() throws IOException {
        Streams.drain(this._in);
        this._in.close();
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/CMSTypedStream$FullReaderStream.class */
    private static class FullReaderStream extends FilterInputStream {
        FullReaderStream(InputStream in) {
            super(in);
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public int read(byte[] buf, int off, int len) throws IOException {
            int totalRead = Streams.readFully(((FilterInputStream) this).in, buf, off, len);
            if (totalRead > 0) {
                return totalRead;
            }
            return -1;
        }
    }
}
