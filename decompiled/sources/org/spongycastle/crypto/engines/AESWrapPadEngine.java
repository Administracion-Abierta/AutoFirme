package org.spongycastle.crypto.engines;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/AESWrapPadEngine.class */
public class AESWrapPadEngine extends RFC5649WrapEngine {
    public AESWrapPadEngine() {
        super(new AESEngine());
    }
}
