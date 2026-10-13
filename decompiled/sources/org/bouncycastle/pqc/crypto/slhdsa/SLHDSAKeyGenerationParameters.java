package org.bouncycastle.pqc.crypto.slhdsa;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/slhdsa/SLHDSAKeyGenerationParameters.class */
public class SLHDSAKeyGenerationParameters extends KeyGenerationParameters {
    private final SLHDSAParameters parameters;

    public SLHDSAKeyGenerationParameters(SecureRandom secureRandom, SLHDSAParameters sLHDSAParameters) {
        super(secureRandom, -1);
        this.parameters = sLHDSAParameters;
    }

    SLHDSAParameters getParameters() {
        return this.parameters;
    }
}
