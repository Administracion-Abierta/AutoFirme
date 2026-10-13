package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/params/DHKeyGenerationParameters.class */
public class DHKeyGenerationParameters extends KeyGenerationParameters {
    private DHParameters params;

    public DHKeyGenerationParameters(SecureRandom secureRandom, DHParameters dHParameters) {
        super(secureRandom, getStrength(dHParameters));
        this.params = dHParameters;
    }

    public DHParameters getParameters() {
        return this.params;
    }

    static int getStrength(DHParameters dHParameters) {
        return dHParameters.getL() != 0 ? dHParameters.getL() : dHParameters.getP().bitLength();
    }
}
