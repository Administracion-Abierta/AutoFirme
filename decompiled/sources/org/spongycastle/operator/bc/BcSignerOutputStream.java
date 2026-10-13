package org.spongycastle.operator.bc;

import java.io.IOException;
import java.io.OutputStream;
import org.spongycastle.crypto.CryptoException;
import org.spongycastle.crypto.Signer;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/operator/bc/BcSignerOutputStream.class */
public class BcSignerOutputStream extends OutputStream {
    private Signer sig;

    BcSignerOutputStream(Signer sig) {
        this.sig = sig;
    }

    @Override // java.io.OutputStream
    public void write(byte[] bytes, int off, int len) throws IOException {
        this.sig.update(bytes, off, len);
    }

    @Override // java.io.OutputStream
    public void write(byte[] bytes) throws IOException {
        this.sig.update(bytes, 0, bytes.length);
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        this.sig.update((byte) b);
    }

    byte[] getSignature() throws CryptoException {
        return this.sig.generateSignature();
    }

    boolean verify(byte[] expected) {
        return this.sig.verifySignature(expected);
    }
}
