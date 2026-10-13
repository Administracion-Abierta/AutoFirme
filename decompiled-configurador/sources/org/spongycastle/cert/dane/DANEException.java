package org.spongycastle.cert.dane;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/dane/DANEException.class */
public class DANEException extends Exception {
    private Throwable cause;

    public DANEException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    public DANEException(String msg) {
        super(msg);
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
