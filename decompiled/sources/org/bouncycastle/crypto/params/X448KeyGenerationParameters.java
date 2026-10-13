package org.bouncycastle.crypto.params;

import com.sun.jna.platform.linux.Fcntl;
import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/params/X448KeyGenerationParameters.class */
public class X448KeyGenerationParameters extends KeyGenerationParameters {
    public X448KeyGenerationParameters(SecureRandom secureRandom) {
        super(secureRandom, Fcntl.S_IRWXU);
    }
}
