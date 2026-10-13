package org.spongycastle.crypto.params;

import java.security.SecureRandom;
import org.spongycastle.crypto.KeyGenerationParameters;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/params/ElGamalKeyGenerationParameters.class */
public class ElGamalKeyGenerationParameters extends KeyGenerationParameters {
    private ElGamalParameters params;

    public ElGamalKeyGenerationParameters(SecureRandom random, ElGamalParameters params) {
        super(random, getStrength(params));
        this.params = params;
    }

    public ElGamalParameters getParameters() {
        return this.params;
    }

    static int getStrength(ElGamalParameters params) {
        return params.getL() != 0 ? params.getL() : params.getP().bitLength();
    }
}
