package org.bouncycastle.crypto.prng;

import org.bouncycastle.crypto.prng.drbg.SP80090DRBG;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/prng/DRBGProvider.class */
interface DRBGProvider {
    String getAlgorithm();

    SP80090DRBG get(EntropySource entropySource);
}
