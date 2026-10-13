package org.bouncycastle.crypto.engines;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/AESWrapPadEngine.class */
public class AESWrapPadEngine extends RFC5649WrapEngine {
    public AESWrapPadEngine() {
        super(AESEngine.newInstance());
    }
}
