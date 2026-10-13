package org.bouncycastle.pqc.crypto.cmce;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/cmce/CMCEKeyGenerationParameters.class */
public class CMCEKeyGenerationParameters extends KeyGenerationParameters {
    private CMCEParameters params;

    public CMCEKeyGenerationParameters(SecureRandom secureRandom, CMCEParameters cMCEParameters) {
        super(secureRandom, 256);
        this.params = cMCEParameters;
    }

    public CMCEParameters getParameters() {
        return this.params;
    }
}
