package org.spongycastle.util;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/util/StoreException.class */
public class StoreException extends RuntimeException {
    private Throwable _e;

    public StoreException(String msg, Throwable cause) {
        super(msg);
        this._e = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this._e;
    }
}
