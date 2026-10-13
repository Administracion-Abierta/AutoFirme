package org.bouncycastle.crypto;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/BasicAgreement.class */
public interface BasicAgreement {
    void init(CipherParameters cipherParameters);

    int getFieldSize();

    BigInteger calculateAgreement(CipherParameters cipherParameters);
}
