package org.spongycastle.crypto.engines;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/ARIAWrapEngine.class */
public class ARIAWrapEngine extends RFC3394WrapEngine {
    public ARIAWrapEngine() {
        super(new ARIAEngine());
    }

    public ARIAWrapEngine(boolean useReverseDirection) {
        super(new ARIAEngine(), useReverseDirection);
    }
}
