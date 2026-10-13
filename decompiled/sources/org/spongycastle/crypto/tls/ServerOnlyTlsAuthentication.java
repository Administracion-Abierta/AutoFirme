package org.spongycastle.crypto.tls;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/ServerOnlyTlsAuthentication.class */
public abstract class ServerOnlyTlsAuthentication implements TlsAuthentication {
    @Override // org.spongycastle.crypto.tls.TlsAuthentication
    public final TlsCredentials getClientCredentials(CertificateRequest certificateRequest) {
        return null;
    }
}
