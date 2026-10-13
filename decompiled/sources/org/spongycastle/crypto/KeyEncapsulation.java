package org.spongycastle.crypto;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/KeyEncapsulation.class */
public interface KeyEncapsulation {
    void init(CipherParameters cipherParameters);

    CipherParameters encrypt(byte[] bArr, int i, int i2);

    CipherParameters decrypt(byte[] bArr, int i, int i2, int i3);
}
