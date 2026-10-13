package org.spongycastle.cert;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/CertRuntimeException.class */
public class CertRuntimeException extends RuntimeException {
    private Throwable cause;

    public CertRuntimeException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
