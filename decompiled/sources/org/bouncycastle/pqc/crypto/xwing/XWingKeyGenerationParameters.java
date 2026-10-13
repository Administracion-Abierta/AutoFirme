package org.bouncycastle.pqc.crypto.xwing;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/xwing/XWingKeyGenerationParameters.class */
public class XWingKeyGenerationParameters extends KeyGenerationParameters {
    public XWingKeyGenerationParameters(SecureRandom secureRandom) {
        super(secureRandom, 128);
    }
}
