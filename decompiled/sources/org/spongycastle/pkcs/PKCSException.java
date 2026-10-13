package org.spongycastle.pkcs;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pkcs/PKCSException.class */
public class PKCSException extends Exception {
    private Throwable cause;

    public PKCSException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    public PKCSException(String msg) {
        super(msg);
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
