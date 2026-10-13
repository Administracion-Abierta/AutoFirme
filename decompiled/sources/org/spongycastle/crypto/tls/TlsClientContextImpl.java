package org.spongycastle.crypto.tls;

import java.security.SecureRandom;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/TlsClientContextImpl.class */
class TlsClientContextImpl extends AbstractTlsContext implements TlsClientContext {
    TlsClientContextImpl(SecureRandom secureRandom, SecurityParameters securityParameters) {
        super(secureRandom, securityParameters);
    }

    @Override // org.spongycastle.crypto.tls.TlsContext
    public boolean isServer() {
        return false;
    }
}
