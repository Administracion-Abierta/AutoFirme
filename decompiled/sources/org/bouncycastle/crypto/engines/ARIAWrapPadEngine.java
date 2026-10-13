package org.bouncycastle.crypto.engines;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/ARIAWrapPadEngine.class */
public class ARIAWrapPadEngine extends RFC5649WrapEngine {
    public ARIAWrapPadEngine() {
        super(new ARIAEngine());
    }
}
