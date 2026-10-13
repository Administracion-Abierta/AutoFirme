package org.bouncycastle.x509;

import java.security.cert.CertificateEncodingException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/x509/ExtCertificateEncodingException.class */
class ExtCertificateEncodingException extends CertificateEncodingException {
    Throwable cause;

    ExtCertificateEncodingException(String str, Throwable th) {
        super(str);
        this.cause = th;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
