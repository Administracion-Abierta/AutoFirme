package org.bouncycastle.crypto.engines;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/AESWrapEngine.class */
public class AESWrapEngine extends RFC3394WrapEngine {
    public AESWrapEngine() {
        super(AESEngine.newInstance());
    }

    public AESWrapEngine(boolean z) {
        super(AESEngine.newInstance(), z);
    }
}
