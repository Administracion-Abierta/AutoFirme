package org.spongycastle.crypto.params;

import java.security.SecureRandom;
import org.spongycastle.crypto.KeyGenerationParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/params/ECKeyGenerationParameters.class */
public class ECKeyGenerationParameters extends KeyGenerationParameters {
    private ECDomainParameters domainParams;

    public ECKeyGenerationParameters(ECDomainParameters domainParams, SecureRandom random) {
        super(random, domainParams.getN().bitLength());
        this.domainParams = domainParams;
    }

    public ECDomainParameters getDomainParameters() {
        return this.domainParams;
    }
}
