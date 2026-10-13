package org.spongycastle.tsp;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/tsp/TSPIOException.class */
public class TSPIOException extends IOException {
    Throwable underlyingException;

    public TSPIOException(String message) {
        super(message);
    }

    public TSPIOException(String message, Throwable e) {
        super(message);
        this.underlyingException = e;
    }

    public Exception getUnderlyingException() {
        return (Exception) this.underlyingException;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.underlyingException;
    }
}
