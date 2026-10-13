package org.bouncycastle.cert.dane;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/dane/DANEException.class */
public class DANEException extends Exception {
    private Throwable cause;

    public DANEException(String str, Throwable th) {
        super(str);
        this.cause = th;
    }

    public DANEException(String str) {
        super(str);
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
