package org.bouncycastle.util.io.pem;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/io/pem/PemGenerationException.class */
public class PemGenerationException extends IOException {
    private Throwable cause;

    public PemGenerationException(String str, Throwable th) {
        super(str);
        this.cause = th;
    }

    public PemGenerationException(String str) {
        super(str);
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
