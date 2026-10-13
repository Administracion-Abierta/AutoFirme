package org.spongycastle.crypto.params;

import java.security.SecureRandom;
import org.spongycastle.crypto.KeyGenerationParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/params/DSAKeyGenerationParameters.class */
public class DSAKeyGenerationParameters extends KeyGenerationParameters {
    private DSAParameters params;

    public DSAKeyGenerationParameters(SecureRandom random, DSAParameters params) {
        super(random, params.getP().bitLength() - 1);
        this.params = params;
    }

    public DSAParameters getParameters() {
        return this.params;
    }
}
