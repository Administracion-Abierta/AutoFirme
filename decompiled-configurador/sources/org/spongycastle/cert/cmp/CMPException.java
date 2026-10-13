package org.spongycastle.cert.cmp;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/cmp/CMPException.class */
public class CMPException extends Exception {
    private Throwable cause;

    public CMPException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    public CMPException(String msg) {
        super(msg);
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
