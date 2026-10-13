package org.spongycastle.crypto.tls;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/AbstractTlsPeer.class */
public abstract class AbstractTlsPeer implements TlsPeer {
    @Override // org.spongycastle.crypto.tls.TlsPeer
    public boolean shouldUseGMTUnixTime() {
        return false;
    }

    @Override // org.spongycastle.crypto.tls.TlsPeer
    public void notifySecureRenegotiation(boolean secureRenegotiation) throws IOException {
        if (!secureRenegotiation) {
            throw new TlsFatalAlert((short) 40);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsPeer
    public void notifyAlertRaised(short alertLevel, short alertDescription, String message, Throwable cause) {
    }

    @Override // org.spongycastle.crypto.tls.TlsPeer
    public void notifyAlertReceived(short alertLevel, short alertDescription) {
    }

    @Override // org.spongycastle.crypto.tls.TlsPeer
    public void notifyHandshakeComplete() throws IOException {
    }
}
