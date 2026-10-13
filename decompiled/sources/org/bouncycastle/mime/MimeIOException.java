package org.bouncycastle.mime;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/mime/MimeIOException.class */
public class MimeIOException extends IOException {
    private Throwable cause;

    public MimeIOException(String str, Throwable th) {
        super(str);
        this.cause = th;
    }

    public MimeIOException(String str) {
        super(str);
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
