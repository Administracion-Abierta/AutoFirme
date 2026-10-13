package org.spongycastle.crypto.engines;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/ARIAWrapPadEngine.class */
public class ARIAWrapPadEngine extends RFC5649WrapEngine {
    public ARIAWrapPadEngine() {
        super(new ARIAEngine());
    }
}
