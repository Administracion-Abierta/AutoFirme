package org.bouncycastle.pqc.legacy.crypto.qtesla;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/crypto/qtesla/QTESLAKeyGenerationParameters.class */
public class QTESLAKeyGenerationParameters extends KeyGenerationParameters {
    private final int securityCategory;

    public QTESLAKeyGenerationParameters(int i, SecureRandom secureRandom) {
        super(secureRandom, -1);
        QTESLASecurityCategory.getPrivateSize(i);
        this.securityCategory = i;
    }

    public int getSecurityCategory() {
        return this.securityCategory;
    }
}
