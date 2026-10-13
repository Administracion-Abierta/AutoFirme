package org.spongycastle.crypto.tls;

import java.security.SecureRandom;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/TlsServerContextImpl.class */
class TlsServerContextImpl extends AbstractTlsContext implements TlsServerContext {
    TlsServerContextImpl(SecureRandom secureRandom, SecurityParameters securityParameters) {
        super(secureRandom, securityParameters);
    }

    @Override // org.spongycastle.crypto.tls.TlsContext
    public boolean isServer() {
        return true;
    }
}
