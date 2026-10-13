package org.bouncycastle.cert.path.validations;

import org.bouncycastle.cert.X509CertificateHolder;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/path/validations/ValidationUtils.class */
class ValidationUtils {
    ValidationUtils() {
    }

    static boolean isSelfIssued(X509CertificateHolder x509CertificateHolder) {
        return x509CertificateHolder.getSubject().equals(x509CertificateHolder.getIssuer());
    }
}
