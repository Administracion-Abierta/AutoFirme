package org.spongycastle.cert.ocsp;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/ocsp/OCSPException.class */
public class OCSPException extends Exception {
    private Throwable cause;

    public OCSPException(String name) {
        super(name);
    }

    public OCSPException(String name, Throwable cause) {
        super(name);
        this.cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
