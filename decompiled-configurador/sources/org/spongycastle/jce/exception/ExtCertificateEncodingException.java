package org.spongycastle.jce.exception;

import java.security.cert.CertificateEncodingException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jce/exception/ExtCertificateEncodingException.class */
public class ExtCertificateEncodingException extends CertificateEncodingException implements ExtException {
    private Throwable cause;

    public ExtCertificateEncodingException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    @Override // java.lang.Throwable, org.spongycastle.jce.exception.ExtException
    public Throwable getCause() {
        return this.cause;
    }
}
