package org.spongycastle.cert.path.validations;

import org.spongycastle.cert.X509CertificateHolder;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/path/validations/ValidationUtils.class */
class ValidationUtils {
    ValidationUtils() {
    }

    static boolean isSelfIssued(X509CertificateHolder cert) {
        return cert.getSubject().equals(cert.getIssuer());
    }
}
