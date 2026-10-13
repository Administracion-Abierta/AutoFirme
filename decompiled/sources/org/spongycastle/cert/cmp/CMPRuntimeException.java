package org.spongycastle.cert.cmp;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/cmp/CMPRuntimeException.class */
public class CMPRuntimeException extends RuntimeException {
    private Throwable cause;

    public CMPRuntimeException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
