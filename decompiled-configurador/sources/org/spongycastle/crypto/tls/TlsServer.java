package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/TlsServer.class */
public interface TlsServer extends TlsPeer {
    void init(TlsServerContext tlsServerContext);

    void notifyClientVersion(ProtocolVersion protocolVersion) throws IOException;

    void notifyFallback(boolean z) throws IOException;

    void notifyOfferedCipherSuites(int[] iArr) throws IOException;

    void notifyOfferedCompressionMethods(short[] sArr) throws IOException;

    void processClientExtensions(Hashtable hashtable) throws IOException;

    ProtocolVersion getServerVersion() throws IOException;

    int getSelectedCipherSuite() throws IOException;

    short getSelectedCompressionMethod() throws IOException;

    Hashtable getServerExtensions() throws IOException;

    Vector getServerSupplementalData() throws IOException;

    TlsCredentials getCredentials() throws IOException;

    CertificateStatus getCertificateStatus() throws IOException;

    TlsKeyExchange getKeyExchange() throws IOException;

    CertificateRequest getCertificateRequest() throws IOException;

    void processClientSupplementalData(Vector vector) throws IOException;

    void notifyClientCertificate(Certificate certificate) throws IOException;

    NewSessionTicket getNewSessionTicket() throws IOException;
}
