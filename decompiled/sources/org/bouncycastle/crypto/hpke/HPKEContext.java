package org.bouncycastle.crypto.hpke;

import org.bouncycastle.crypto.InvalidCipherTextException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/hpke/HPKEContext.class */
public class HPKEContext {
    protected final AEAD aead;
    protected final HKDF hkdf;
    protected final byte[] exporterSecret;
    protected final byte[] suiteId;

    HPKEContext(AEAD aead, HKDF hkdf, byte[] bArr, byte[] bArr2) {
        this.aead = aead;
        this.hkdf = hkdf;
        this.exporterSecret = bArr;
        this.suiteId = bArr2;
    }

    public byte[] export(byte[] bArr, int i) {
        return this.hkdf.LabeledExpand(this.exporterSecret, this.suiteId, "sec", bArr, i);
    }

    public byte[] seal(byte[] bArr, byte[] bArr2) throws InvalidCipherTextException {
        return this.aead.seal(bArr, bArr2);
    }

    public byte[] seal(byte[] bArr, byte[] bArr2, int i, int i2) throws InvalidCipherTextException {
        return this.aead.seal(bArr, bArr2, i, i2);
    }

    public byte[] open(byte[] bArr, byte[] bArr2) throws InvalidCipherTextException {
        return this.aead.open(bArr, bArr2);
    }

    public byte[] open(byte[] bArr, byte[] bArr2, int i, int i2) throws InvalidCipherTextException {
        return this.aead.open(bArr, bArr2, i, i2);
    }

    public byte[] extract(byte[] bArr, byte[] bArr2) {
        return this.hkdf.Extract(bArr, bArr2);
    }

    public byte[] expand(byte[] bArr, byte[] bArr2, int i) {
        return this.hkdf.Expand(bArr, bArr2, i);
    }
}
