package org.bouncycastle.crypto.engines;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/CamelliaWrapEngine.class */
public class CamelliaWrapEngine extends RFC3394WrapEngine {
    public CamelliaWrapEngine() {
        super(new CamelliaEngine());
    }
}
