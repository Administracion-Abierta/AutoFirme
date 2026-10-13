package org.spongycastle.cert;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/CertException.class */
public class CertException extends Exception {
    private Throwable cause;

    public CertException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    public CertException(String msg) {
        super(msg);
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
