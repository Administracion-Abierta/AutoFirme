package org.spongycastle.dvcs;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/dvcs/DVCSException.class */
public class DVCSException extends Exception {
    private static final long serialVersionUID = 389345256020131488L;
    private Throwable cause;

    public DVCSException(String message) {
        super(message);
    }

    public DVCSException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
