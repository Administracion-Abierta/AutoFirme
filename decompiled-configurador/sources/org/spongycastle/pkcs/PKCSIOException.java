package org.spongycastle.pkcs;

import java.io.IOException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pkcs/PKCSIOException.class */
public class PKCSIOException extends IOException {
    private Throwable cause;

    public PKCSIOException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    public PKCSIOException(String msg) {
        super(msg);
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
