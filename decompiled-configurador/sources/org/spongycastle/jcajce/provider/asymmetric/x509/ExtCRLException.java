package org.spongycastle.jcajce.provider.asymmetric.x509;

import java.security.cert.CRLException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/asymmetric/x509/ExtCRLException.class */
class ExtCRLException extends CRLException {
    Throwable cause;

    ExtCRLException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
