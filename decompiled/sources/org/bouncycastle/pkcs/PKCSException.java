package org.bouncycastle.pkcs;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkcs/PKCSException.class */
public class PKCSException extends Exception {
    private Throwable cause;

    public PKCSException(String str, Throwable th) {
        super(str);
        this.cause = th;
    }

    public PKCSException(String str) {
        super(str);
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
