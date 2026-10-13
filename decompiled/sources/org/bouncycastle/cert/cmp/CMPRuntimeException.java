package org.bouncycastle.cert.cmp;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/cmp/CMPRuntimeException.class */
public class CMPRuntimeException extends RuntimeException {
    private Throwable cause;

    public CMPRuntimeException(String str, Throwable th) {
        super(str);
        this.cause = th;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
