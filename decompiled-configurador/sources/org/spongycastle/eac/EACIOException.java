package org.spongycastle.eac;

import java.io.IOException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/eac/EACIOException.class */
public class EACIOException extends IOException {
    private Throwable cause;

    public EACIOException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    public EACIOException(String msg) {
        super(msg);
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
