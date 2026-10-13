package org.bouncycastle.crypto.params;

import com.sun.jna.platform.linux.Fcntl;
import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/params/Ed448KeyGenerationParameters.class */
public class Ed448KeyGenerationParameters extends KeyGenerationParameters {
    public Ed448KeyGenerationParameters(SecureRandom secureRandom) {
        super(secureRandom, Fcntl.S_IRWXU);
    }
}
