package org.spongycastle.crypto.tls;

import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/TlsSessionImpl.class */
class TlsSessionImpl implements TlsSession {
    final byte[] sessionID;
    SessionParameters sessionParameters;

    TlsSessionImpl(byte[] sessionID, SessionParameters sessionParameters) {
        if (sessionID == null) {
            throw new IllegalArgumentException("'sessionID' cannot be null");
        }
        if (sessionID.length < 1 || sessionID.length > 32) {
            throw new IllegalArgumentException("'sessionID' must have length between 1 and 32 bytes, inclusive");
        }
        this.sessionID = Arrays.clone(sessionID);
        this.sessionParameters = sessionParameters;
    }

    @Override // org.spongycastle.crypto.tls.TlsSession
    public synchronized SessionParameters exportSessionParameters() {
        if (this.sessionParameters == null) {
            return null;
        }
        return this.sessionParameters.copy();
    }

    @Override // org.spongycastle.crypto.tls.TlsSession
    public synchronized byte[] getSessionID() {
        return this.sessionID;
    }

    @Override // org.spongycastle.crypto.tls.TlsSession
    public synchronized void invalidate() {
        if (this.sessionParameters != null) {
            this.sessionParameters.clear();
            this.sessionParameters = null;
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsSession
    public synchronized boolean isResumable() {
        return this.sessionParameters != null;
    }
}
