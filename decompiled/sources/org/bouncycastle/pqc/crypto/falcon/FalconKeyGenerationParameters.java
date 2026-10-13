package org.bouncycastle.pqc.crypto.falcon;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/falcon/FalconKeyGenerationParameters.class */
public class FalconKeyGenerationParameters extends KeyGenerationParameters {
    private final FalconParameters params;

    public FalconKeyGenerationParameters(SecureRandom secureRandom, FalconParameters falconParameters) {
        super(secureRandom, 320);
        this.params = falconParameters;
    }

    public FalconParameters getParameters() {
        return this.params;
    }
}
