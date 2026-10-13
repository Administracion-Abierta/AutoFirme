package org.spongycastle.crypto.engines;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/CamelliaWrapEngine.class */
public class CamelliaWrapEngine extends RFC3394WrapEngine {
    public CamelliaWrapEngine() {
        super(new CamelliaEngine());
    }
}
