package org.bouncycastle.cert.ocsp;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/ocsp/OCSPException.class */
public class OCSPException extends Exception {
    private Throwable cause;

    public OCSPException(String str) {
        super(str);
    }

    public OCSPException(String str, Throwable th) {
        super(str);
        this.cause = th;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
