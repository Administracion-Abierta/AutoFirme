package org.spongycastle.crypto;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/Mac.class */
public interface Mac {
    void init(CipherParameters cipherParameters) throws IllegalArgumentException;

    String getAlgorithmName();

    int getMacSize();

    void update(byte b) throws IllegalStateException;

    void update(byte[] bArr, int i, int i2) throws IllegalStateException, DataLengthException;

    int doFinal(byte[] bArr, int i) throws IllegalStateException, DataLengthException;

    void reset();
}
