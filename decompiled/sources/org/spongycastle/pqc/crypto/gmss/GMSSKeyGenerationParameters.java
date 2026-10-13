package org.spongycastle.pqc.crypto.gmss;

import java.security.SecureRandom;
import org.spongycastle.crypto.KeyGenerationParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/gmss/GMSSKeyGenerationParameters.class */
public class GMSSKeyGenerationParameters extends KeyGenerationParameters {
    private GMSSParameters params;

    public GMSSKeyGenerationParameters(SecureRandom random, GMSSParameters params) {
        super(random, 1);
        this.params = params;
    }

    public GMSSParameters getParameters() {
        return this.params;
    }
}
