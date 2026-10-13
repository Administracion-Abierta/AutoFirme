package org.bouncycastle.pqc.crypto.ntru;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/ntru/NTRUKeyGenerationParameters.class */
public class NTRUKeyGenerationParameters extends KeyGenerationParameters {
    private final NTRUParameters ntruParameters;

    public NTRUKeyGenerationParameters(SecureRandom secureRandom, NTRUParameters nTRUParameters) {
        super(secureRandom, 0);
        this.ntruParameters = nTRUParameters;
    }

    public NTRUParameters getParameters() {
        return this.ntruParameters;
    }
}
