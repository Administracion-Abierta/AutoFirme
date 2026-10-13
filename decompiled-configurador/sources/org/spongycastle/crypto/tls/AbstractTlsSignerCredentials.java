package org.spongycastle.crypto.tls;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/AbstractTlsSignerCredentials.class */
public abstract class AbstractTlsSignerCredentials extends AbstractTlsCredentials implements TlsSignerCredentials {
    @Override // org.spongycastle.crypto.tls.TlsSignerCredentials
    public SignatureAndHashAlgorithm getSignatureAndHashAlgorithm() {
        throw new IllegalStateException("TlsSignerCredentials implementation does not support (D)TLS 1.2+");
    }
}
