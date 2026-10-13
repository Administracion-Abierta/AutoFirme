package org.bouncycastle.operator.bc;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/bc/BcSignerOutputStream.class */
public class BcSignerOutputStream extends OutputStream {
    private Signer sig;

    BcSignerOutputStream(Signer signer) {
        this.sig = signer;
    }

    @Override // java.io.OutputStream
    public void write(byte[] bArr, int i, int i2) throws IOException {
        this.sig.update(bArr, i, i2);
    }

    @Override // java.io.OutputStream
    public void write(byte[] bArr) throws IOException {
        this.sig.update(bArr, 0, bArr.length);
    }

    @Override // java.io.OutputStream
    public void write(int i) throws IOException {
        this.sig.update((byte) i);
    }

    byte[] getSignature() throws CryptoException {
        return this.sig.generateSignature();
    }

    boolean verify(byte[] bArr) {
        return this.sig.verifySignature(bArr);
    }
}
