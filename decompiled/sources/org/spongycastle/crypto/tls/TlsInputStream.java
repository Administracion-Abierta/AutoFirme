package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/TlsInputStream.class */
class TlsInputStream extends InputStream {
    private byte[] buf = new byte[1];
    private TlsProtocol handler;

    TlsInputStream(TlsProtocol handler) {
        this.handler = null;
        this.handler = handler;
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        return this.handler.applicationDataAvailable();
    }

    @Override // java.io.InputStream
    public int read(byte[] buf, int offset, int len) throws IOException {
        return this.handler.readApplicationData(buf, offset, len);
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        if (read(this.buf) < 0) {
            return -1;
        }
        return this.buf[0] & 255;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.handler.close();
    }
}
