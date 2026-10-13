package org.spongycastle.cert.crmf;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/crmf/CRMFRuntimeException.class */
public class CRMFRuntimeException extends RuntimeException {
    private Throwable cause;

    public CRMFRuntimeException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
