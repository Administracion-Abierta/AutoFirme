package org.spongycastle.crypto;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/BasicAgreement.class */
public interface BasicAgreement {
    void init(CipherParameters cipherParameters);

    int getFieldSize();

    BigInteger calculateAgreement(CipherParameters cipherParameters);
}
