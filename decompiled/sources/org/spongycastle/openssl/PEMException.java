package org.spongycastle.openssl;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/PEMException.class */
public class PEMException extends IOException {
    Exception underlying;

    public PEMException(String message) {
        super(message);
    }

    public PEMException(String message, Exception underlying) {
        super(message);
        this.underlying = underlying;
    }

    public Exception getUnderlyingException() {
        return this.underlying;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.underlying;
    }
}
