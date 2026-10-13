package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/TlsKeyExchange.class */
public interface TlsKeyExchange {
    void init(TlsContext tlsContext);

    void skipServerCredentials() throws IOException;

    void processServerCredentials(TlsCredentials tlsCredentials) throws IOException;

    void processServerCertificate(Certificate certificate) throws IOException;

    boolean requiresServerKeyExchange();

    byte[] generateServerKeyExchange() throws IOException;

    void skipServerKeyExchange() throws IOException;

    void processServerKeyExchange(InputStream inputStream) throws IOException;

    void validateCertificateRequest(CertificateRequest certificateRequest) throws IOException;

    void skipClientCredentials() throws IOException;

    void processClientCredentials(TlsCredentials tlsCredentials) throws IOException;

    void processClientCertificate(Certificate certificate) throws IOException;

    void generateClientKeyExchange(OutputStream outputStream) throws IOException;

    void processClientKeyExchange(InputStream inputStream) throws IOException;

    byte[] generatePremasterSecret() throws IOException;
}
