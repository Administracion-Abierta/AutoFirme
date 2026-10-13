package org.spongycastle.crypto;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/Signer.class */
public interface Signer {
    void init(boolean z, CipherParameters cipherParameters);

    void update(byte b);

    void update(byte[] bArr, int i, int i2);

    byte[] generateSignature() throws DataLengthException, CryptoException;

    boolean verifySignature(byte[] bArr);

    void reset();
}
