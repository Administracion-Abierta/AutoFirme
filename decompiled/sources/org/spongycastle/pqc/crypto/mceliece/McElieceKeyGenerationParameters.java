package org.spongycastle.pqc.crypto.mceliece;

import java.security.SecureRandom;
import org.spongycastle.crypto.KeyGenerationParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/mceliece/McElieceKeyGenerationParameters.class */
public class McElieceKeyGenerationParameters extends KeyGenerationParameters {
    private McElieceParameters params;

    public McElieceKeyGenerationParameters(SecureRandom random, McElieceParameters params) {
        super(random, 256);
        this.params = params;
    }

    public McElieceParameters getParameters() {
        return this.params;
    }
}
