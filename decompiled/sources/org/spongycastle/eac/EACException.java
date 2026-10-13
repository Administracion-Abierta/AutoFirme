package org.spongycastle.eac;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/eac/EACException.class */
public class EACException extends Exception {
    private Throwable cause;

    public EACException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    public EACException(String msg) {
        super(msg);
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
