package org.spongycastle.crypto.ec;

import org.spongycastle.crypto.CipherParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/ec/ECPairTransform.class */
public interface ECPairTransform {
    void init(CipherParameters cipherParameters);

    ECPair transform(ECPair eCPair);
}
