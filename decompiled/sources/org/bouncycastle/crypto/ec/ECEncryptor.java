package org.bouncycastle.crypto.ec;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.math.ec.ECPoint;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/ec/ECEncryptor.class */
public interface ECEncryptor {
    void init(CipherParameters cipherParameters);

    ECPair encrypt(ECPoint eCPoint);
}
