package org.spongycastle.cert.crmf;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/crmf/CRMFException.class */
public class CRMFException extends Exception {
    private Throwable cause;

    public CRMFException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
