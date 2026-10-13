package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.security.cert.CRLException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/x509/ExtCRLException.class */
class ExtCRLException extends CRLException {
    Throwable cause;

    ExtCRLException(String str, Throwable th) {
        super(str);
        this.cause = th;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
