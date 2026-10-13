package org.spongycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/signers/DSAKCalculator.class */
public interface DSAKCalculator {
    boolean isDeterministic();

    void init(BigInteger bigInteger, SecureRandom secureRandom);

    void init(BigInteger bigInteger, BigInteger bigInteger2, byte[] bArr);

    BigInteger nextK();
}
