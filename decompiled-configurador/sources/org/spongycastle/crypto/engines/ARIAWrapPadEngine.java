package org.spongycastle.crypto.engines;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/engines/ARIAWrapPadEngine.class */
public class ARIAWrapPadEngine extends RFC5649WrapEngine {
    public ARIAWrapPadEngine() {
        super(new ARIAEngine());
    }
}
