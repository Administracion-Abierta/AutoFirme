package org.spongycastle.crypto.ec;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.math.ec.ECPoint;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/ec/ECDecryptor.class */
public interface ECDecryptor {
    void init(CipherParameters cipherParameters);

    ECPoint decrypt(ECPair eCPair);
}
