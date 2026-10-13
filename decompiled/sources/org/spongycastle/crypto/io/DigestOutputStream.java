package org.spongycastle.crypto.io;

import java.io.IOException;
import java.io.OutputStream;
import org.spongycastle.crypto.Digest;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/io/DigestOutputStream.class */
public class DigestOutputStream extends OutputStream {
    protected Digest digest;

    public DigestOutputStream(Digest Digest) {
        this.digest = Digest;
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        this.digest.update((byte) b);
    }

    @Override // java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        this.digest.update(b, off, len);
    }

    public byte[] getDigest() {
        byte[] res = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(res, 0);
        return res;
    }
}
