package org.bouncycastle.eac;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/eac/EACIOException.class */
public class EACIOException extends IOException {
    private Throwable cause;

    public EACIOException(String str, Throwable th) {
        super(str);
        this.cause = th;
    }

    public EACIOException(String str) {
        super(str);
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
