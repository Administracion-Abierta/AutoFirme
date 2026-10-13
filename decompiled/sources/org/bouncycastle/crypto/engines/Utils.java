package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CryptoServicePurpose;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/Utils.class */
class Utils {
    Utils() {
    }

    static CryptoServicePurpose getPurpose(boolean z) {
        return z ? CryptoServicePurpose.ENCRYPTION : CryptoServicePurpose.DECRYPTION;
    }
}
