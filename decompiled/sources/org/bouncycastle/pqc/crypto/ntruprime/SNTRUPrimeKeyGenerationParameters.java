package org.bouncycastle.pqc.crypto.ntruprime;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/ntruprime/SNTRUPrimeKeyGenerationParameters.class */
public class SNTRUPrimeKeyGenerationParameters extends KeyGenerationParameters {
    private final SNTRUPrimeParameters sntrupParams;

    public SNTRUPrimeKeyGenerationParameters(SecureRandom secureRandom, SNTRUPrimeParameters sNTRUPrimeParameters) {
        super(null != secureRandom ? secureRandom : CryptoServicesRegistrar.getSecureRandom(), 256);
        this.sntrupParams = sNTRUPrimeParameters;
    }

    public SNTRUPrimeParameters getSntrupParams() {
        return this.sntrupParams;
    }
}
