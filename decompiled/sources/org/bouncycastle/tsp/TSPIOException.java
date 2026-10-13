package org.bouncycastle.tsp;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/tsp/TSPIOException.class */
public class TSPIOException extends IOException {
    Throwable underlyingException;

    public TSPIOException(String str) {
        super(str);
    }

    public TSPIOException(String str, Throwable th) {
        super(str);
        this.underlyingException = th;
    }

    public Exception getUnderlyingException() {
        return (Exception) this.underlyingException;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.underlyingException;
    }
}
