package org.spongycastle.pqc.crypto.xmss;

import java.security.SecureRandom;
import org.spongycastle.crypto.KeyGenerationParameters;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/xmss/XMSSKeyGenerationParameters.class */
public final class XMSSKeyGenerationParameters extends KeyGenerationParameters {
    private final XMSSParameters xmssParameters;

    public XMSSKeyGenerationParameters(XMSSParameters xmssParameters, SecureRandom prng) {
        super(prng, -1);
        this.xmssParameters = xmssParameters;
    }

    public XMSSParameters getParameters() {
        return this.xmssParameters;
    }
}
