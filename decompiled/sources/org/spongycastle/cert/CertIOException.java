package org.spongycastle.cert;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/CertIOException.class */
public class CertIOException extends IOException {
    private Throwable cause;

    public CertIOException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    public CertIOException(String msg) {
        super(msg);
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
