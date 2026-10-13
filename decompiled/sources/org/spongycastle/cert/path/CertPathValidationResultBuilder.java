package org.spongycastle.cert.path;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/path/CertPathValidationResultBuilder.class */
class CertPathValidationResultBuilder {
    CertPathValidationResultBuilder() {
    }

    public CertPathValidationResult build() {
        return new CertPathValidationResult((CertPathValidationContext) null, 0, 0, (CertPathValidationException) null);
    }

    public void addException(CertPathValidationException exception) {
    }
}
