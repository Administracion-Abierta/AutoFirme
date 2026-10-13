package org.spongycastle.x509;

import java.security.cert.CertificateEncodingException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/x509/ExtCertificateEncodingException.class */
class ExtCertificateEncodingException extends CertificateEncodingException {
    Throwable cause;

    ExtCertificateEncodingException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
