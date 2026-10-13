package org.spongycastle.crypto;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/BlockCipher.class */
public interface BlockCipher {
    void init(boolean z, CipherParameters cipherParameters) throws IllegalArgumentException;

    String getAlgorithmName();

    int getBlockSize();

    int processBlock(byte[] bArr, int i, byte[] bArr2, int i2) throws IllegalStateException, DataLengthException;

    void reset();
}
