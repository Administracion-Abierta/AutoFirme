package org.spongycastle.crypto;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/StreamCipher.class */
public interface StreamCipher {
    void init(boolean z, CipherParameters cipherParameters) throws IllegalArgumentException;

    String getAlgorithmName();

    byte returnByte(byte b);

    int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws DataLengthException;

    void reset();
}
