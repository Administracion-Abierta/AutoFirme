package org.spongycastle.tsp;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/tsp/TSPException.class */
public class TSPException extends Exception {
    Throwable underlyingException;

    public TSPException(String message) {
        super(message);
    }

    public TSPException(String message, Throwable e) {
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
