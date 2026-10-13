package org.bouncycastle.crypto.ec;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.math.ec.ECPoint;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/ec/ECDecryptor.class */
public interface ECDecryptor {
    void init(CipherParameters cipherParameters);

    ECPoint decrypt(ECPair eCPair);
}
