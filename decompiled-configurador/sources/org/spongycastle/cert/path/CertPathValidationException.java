package org.spongycastle.cert.path;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/path/CertPathValidationException.class */
public class CertPathValidationException extends Exception {
    private final Exception cause;

    public CertPathValidationException(String msg) {
        this(msg, null);
    }

    public CertPathValidationException(String msg, Exception cause) {
        super(msg);
        this.cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
