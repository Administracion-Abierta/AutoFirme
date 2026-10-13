package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/params/ParametersWithRandom.class */
public class ParametersWithRandom implements CipherParameters {
    private SecureRandom random;
    private CipherParameters parameters;

    public ParametersWithRandom(CipherParameters cipherParameters, SecureRandom secureRandom) {
        this.random = CryptoServicesRegistrar.getSecureRandom(secureRandom);
        this.parameters = cipherParameters;
    }

    public ParametersWithRandom(CipherParameters cipherParameters) {
        this(cipherParameters, null);
    }

    public SecureRandom getRandom() {
        return this.random;
    }

    public CipherParameters getParameters() {
        return this.parameters;
    }
}
