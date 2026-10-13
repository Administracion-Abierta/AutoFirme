package org.bouncycastle.pkix.jcajce;

import java.security.cert.CertPathValidatorException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkix/jcajce/CRLNotFoundException.class */
class CRLNotFoundException extends CertPathValidatorException {
    CRLNotFoundException(String str) {
        super(str);
    }

    public CRLNotFoundException(String str, Throwable th) {
        super(str, th);
    }
}
