package org.spongycastle.jce.provider;

import java.security.cert.CRLException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/provider/ExtCRLException.class */
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
