package org.bouncycastle.crypto;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/DSA.class */
public interface DSA {
    void init(boolean z, CipherParameters cipherParameters);

    BigInteger[] generateSignature(byte[] bArr);

    boolean verifySignature(byte[] bArr, BigInteger bigInteger, BigInteger bigInteger2);
}
