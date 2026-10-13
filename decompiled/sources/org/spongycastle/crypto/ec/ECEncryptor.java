package org.spongycastle.crypto.ec;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.math.ec.ECPoint;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/ec/ECEncryptor.class */
public interface ECEncryptor {
    void init(CipherParameters cipherParameters);

    ECPair encrypt(ECPoint eCPoint);
}
