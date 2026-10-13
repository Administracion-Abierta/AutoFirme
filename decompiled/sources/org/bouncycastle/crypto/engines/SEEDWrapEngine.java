package org.bouncycastle.crypto.engines;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/SEEDWrapEngine.class */
public class SEEDWrapEngine extends RFC3394WrapEngine {
    public SEEDWrapEngine() {
        super(new SEEDEngine());
    }
}
