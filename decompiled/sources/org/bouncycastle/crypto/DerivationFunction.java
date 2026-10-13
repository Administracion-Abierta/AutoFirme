package org.bouncycastle.crypto;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/DerivationFunction.class */
public interface DerivationFunction {
    void init(DerivationParameters derivationParameters);

    int generateBytes(byte[] bArr, int i, int i2) throws DataLengthException, IllegalArgumentException;
}
