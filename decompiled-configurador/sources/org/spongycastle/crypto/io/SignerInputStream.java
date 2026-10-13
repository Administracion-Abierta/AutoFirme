package org.spongycastle.crypto.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.spongycastle.crypto.Signer;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/io/SignerInputStream.class */
public class SignerInputStream extends FilterInputStream {
    protected Signer signer;

    public SignerInputStream(InputStream stream, Signer signer) {
        super(stream);
        this.signer = signer;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        int b = this.in.read();
        if (b >= 0) {
            this.signer.update((byte) b);
        }
        return b;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        int n = this.in.read(b, off, len);
        if (n > 0) {
            this.signer.update(b, off, n);
        }
        return n;
    }

    public Signer getSigner() {
        return this.signer;
    }
}
