package org.spongycastle.crypto.params;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.KeyGenerationParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/params/RSAKeyGenerationParameters.class */
public class RSAKeyGenerationParameters extends KeyGenerationParameters {
    private BigInteger publicExponent;
    private int certainty;

    public RSAKeyGenerationParameters(BigInteger publicExponent, SecureRandom random, int strength, int certainty) {
        super(random, strength);
        if (strength < 12) {
            throw new IllegalArgumentException("key strength too small");
        }
        if (!publicExponent.testBit(0)) {
            throw new IllegalArgumentException("public exponent cannot be even");
        }
        this.publicExponent = publicExponent;
        this.certainty = certainty;
    }

    public BigInteger getPublicExponent() {
        return this.publicExponent;
    }

    public int getCertainty() {
        return this.certainty;
    }
}
