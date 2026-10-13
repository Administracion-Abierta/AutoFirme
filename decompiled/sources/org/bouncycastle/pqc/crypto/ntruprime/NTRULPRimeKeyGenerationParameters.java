package org.bouncycastle.pqc.crypto.ntruprime;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/ntruprime/NTRULPRimeKeyGenerationParameters.class */
public class NTRULPRimeKeyGenerationParameters extends KeyGenerationParameters {
    private final NTRULPRimeParameters ntrulprParams;

    public NTRULPRimeKeyGenerationParameters(SecureRandom secureRandom, NTRULPRimeParameters nTRULPRimeParameters) {
        super(null != secureRandom ? secureRandom : CryptoServicesRegistrar.getSecureRandom(), 256);
        this.ntrulprParams = nTRULPRimeParameters;
    }

    public NTRULPRimeParameters getNtrulprParams() {
        return this.ntrulprParams;
    }
}
