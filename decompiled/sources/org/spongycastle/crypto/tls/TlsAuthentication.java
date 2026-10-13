package org.spongycastle.crypto.tls;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/TlsAuthentication.class */
public interface TlsAuthentication {
    void notifyServerCertificate(Certificate certificate) throws IOException;

    TlsCredentials getClientCredentials(CertificateRequest certificateRequest) throws IOException;
}
