package org.bouncycastle.pqc.crypto.bike;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/bike/BIKEKeyGenerationParameters.class */
public class BIKEKeyGenerationParameters extends KeyGenerationParameters {
    private BIKEParameters params;

    public BIKEKeyGenerationParameters(SecureRandom secureRandom, BIKEParameters bIKEParameters) {
        super(secureRandom, 256);
        this.params = bIKEParameters;
    }

    public BIKEParameters getParameters() {
        return this.params;
    }
}
