package org.spongycastle.crypto;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/AsymmetricBlockCipher.class */
public interface AsymmetricBlockCipher {
    void init(boolean z, CipherParameters cipherParameters);

    int getInputBlockSize();

    int getOutputBlockSize();

    byte[] processBlock(byte[] bArr, int i, int i2) throws InvalidCipherTextException;
}
