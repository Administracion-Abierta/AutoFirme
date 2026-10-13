package org.spongycastle.crypto.params;

import java.security.SecureRandom;
import org.spongycastle.crypto.CipherParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/params/ParametersWithRandom.class */
public class ParametersWithRandom implements CipherParameters {
    private SecureRandom random;
    private CipherParameters parameters;

    public ParametersWithRandom(CipherParameters parameters, SecureRandom random) {
        this.random = random;
        this.parameters = parameters;
    }

    public ParametersWithRandom(CipherParameters parameters) {
        this(parameters, new SecureRandom());
    }

    public SecureRandom getRandom() {
        return this.random;
    }

    public CipherParameters getParameters() {
        return this.parameters;
    }
}
