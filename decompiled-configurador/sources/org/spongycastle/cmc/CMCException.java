package org.spongycastle.cmc;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cmc/CMCException.class */
public class CMCException extends Exception {
    private final Throwable cause;

    public CMCException(String msg) {
        this(msg, null);
    }

    public CMCException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
