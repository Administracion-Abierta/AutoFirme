package org.spongycastle.crypto;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/DerivationFunction.class */
public interface DerivationFunction {
    void init(DerivationParameters derivationParameters);

    int generateBytes(byte[] bArr, int i, int i2) throws DataLengthException, IllegalArgumentException;
}
