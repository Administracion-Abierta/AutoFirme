package org.spongycastle.crypto.tls;

import org.spongycastle.crypto.Digest;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/TlsHandshakeHash.class */
public interface TlsHandshakeHash extends Digest {
    void init(TlsContext tlsContext);

    TlsHandshakeHash notifyPRFDetermined();

    void trackHashAlgorithm(short s);

    void sealHashAlgorithms();

    TlsHandshakeHash stopTracking();

    Digest forkPRFHash();

    byte[] getFinalHash(short s);
}
