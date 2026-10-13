package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/params/RSABlindingParameters.class */
public class RSABlindingParameters implements CipherParameters {
    private RSAKeyParameters publicKey;
    private BigInteger blindingFactor;

    public RSABlindingParameters(RSAKeyParameters rSAKeyParameters, BigInteger bigInteger) {
        if (rSAKeyParameters instanceof RSAPrivateCrtKeyParameters) {
            throw new IllegalArgumentException("RSA parameters should be for a public key");
        }
        this.publicKey = rSAKeyParameters;
        this.blindingFactor = bigInteger;
    }

    public RSAKeyParameters getPublicKey() {
        return this.publicKey;
    }

    public BigInteger getBlindingFactor() {
        return this.blindingFactor;
    }
}
