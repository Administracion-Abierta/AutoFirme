package org.spongycastle.crypto.tls;

import java.security.SecureRandom;
import org.spongycastle.crypto.prng.RandomGenerator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/TlsContext.class */
public interface TlsContext {
    RandomGenerator getNonceRandomGenerator();

    SecureRandom getSecureRandom();

    SecurityParameters getSecurityParameters();

    boolean isServer();

    ProtocolVersion getClientVersion();

    ProtocolVersion getServerVersion();

    TlsSession getResumableSession();

    Object getUserObject();

    void setUserObject(Object obj);

    byte[] exportKeyingMaterial(String str, byte[] bArr, int i);
}
