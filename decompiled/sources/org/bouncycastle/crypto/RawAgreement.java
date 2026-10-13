package org.bouncycastle.crypto;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/RawAgreement.class */
public interface RawAgreement {
    void init(CipherParameters cipherParameters);

    int getAgreementSize();

    void calculateAgreement(CipherParameters cipherParameters, byte[] bArr, int i);
}
