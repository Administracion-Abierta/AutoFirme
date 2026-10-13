package org.bouncycastle.pkcs;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkcs/PKCSIOException.class */
public class PKCSIOException extends IOException {
    private Throwable cause;

    public PKCSIOException(String str, Throwable th) {
        super(str);
        this.cause = th;
    }

    public PKCSIOException(String str) {
        super(str);
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
