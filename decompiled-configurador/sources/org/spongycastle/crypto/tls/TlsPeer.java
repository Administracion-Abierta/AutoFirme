package org.spongycastle.crypto.tls;

import java.io.IOException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/TlsPeer.class */
public interface TlsPeer {
    boolean shouldUseGMTUnixTime();

    void notifySecureRenegotiation(boolean z) throws IOException;

    TlsCompression getCompression() throws IOException;

    TlsCipher getCipher() throws IOException;

    void notifyAlertRaised(short s, short s2, String str, Throwable th);

    void notifyAlertReceived(short s, short s2);

    void notifyHandshakeComplete() throws IOException;
}
