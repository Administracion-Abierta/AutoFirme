package org.spongycastle.crypto.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.spongycastle.crypto.Mac;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/io/MacInputStream.class */
public class MacInputStream extends FilterInputStream {
    protected Mac mac;

    public MacInputStream(InputStream stream, Mac mac) {
        super(stream);
        this.mac = mac;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        int b = this.in.read();
        if (b >= 0) {
            this.mac.update((byte) b);
        }
        return b;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        int n = this.in.read(b, off, len);
        if (n >= 0) {
            this.mac.update(b, off, n);
        }
        return n;
    }

    public Mac getMac() {
        return this.mac;
    }
}
