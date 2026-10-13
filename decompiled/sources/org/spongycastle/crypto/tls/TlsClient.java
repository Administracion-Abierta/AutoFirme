package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/TlsClient.class */
public interface TlsClient extends TlsPeer {
    void init(TlsClientContext tlsClientContext);

    TlsSession getSessionToResume();

    ProtocolVersion getClientHelloRecordLayerVersion();

    ProtocolVersion getClientVersion();

    boolean isFallback();

    int[] getCipherSuites();

    short[] getCompressionMethods();

    Hashtable getClientExtensions() throws IOException;

    void notifyServerVersion(ProtocolVersion protocolVersion) throws IOException;

    void notifySessionID(byte[] bArr);

    void notifySelectedCipherSuite(int i);

    void notifySelectedCompressionMethod(short s);

    void processServerExtensions(Hashtable hashtable) throws IOException;

    void processServerSupplementalData(Vector vector) throws IOException;

    TlsKeyExchange getKeyExchange() throws IOException;

    TlsAuthentication getAuthentication() throws IOException;

    Vector getClientSupplementalData() throws IOException;

    void notifyNewSessionTicket(NewSessionTicket newSessionTicket) throws IOException;
}
