package org.spongycastle.crypto.tls;

import java.io.EOFException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/TlsNoCloseNotifyException.class */
public class TlsNoCloseNotifyException extends EOFException {
    public TlsNoCloseNotifyException() {
        super("No close_notify alert received before connection closed");
    }
}
