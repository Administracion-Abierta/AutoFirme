package org.spongycastle.cert.path;

import org.spongycastle.cert.X509CertificateHolder;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/path/CertPath.class */
public class CertPath {
    private final X509CertificateHolder[] certificates;

    public CertPath(X509CertificateHolder[] certificates) {
        this.certificates = copyArray(certificates);
    }

    public X509CertificateHolder[] getCertificates() {
        return copyArray(this.certificates);
    }

    public CertPathValidationResult validate(CertPathValidation[] ruleSet) {
        CertPathValidationContext context = new CertPathValidationContext(CertPathUtils.getCriticalExtensionsOIDs(this.certificates));
        for (int i = 0; i != ruleSet.length; i++) {
            int j = this.certificates.length - 1;
            while (j >= 0) {
                try {
                    context.setIsEndEntity(j == 0);
                    ruleSet[i].validate(context, this.certificates[j]);
                    j--;
                } catch (CertPathValidationException e) {
                    return new CertPathValidationResult(context, j, i, e);
                }
            }
        }
        return new CertPathValidationResult(context);
    }

    public CertPathValidationResult evaluate(CertPathValidation[] ruleSet) {
        CertPathValidationContext context = new CertPathValidationContext(CertPathUtils.getCriticalExtensionsOIDs(this.certificates));
        CertPathValidationResultBuilder builder = new CertPathValidationResultBuilder();
        for (int i = 0; i != ruleSet.length; i++) {
            int j = this.certificates.length - 1;
            while (j >= 0) {
                try {
                    context.setIsEndEntity(j == 0);
                    ruleSet[i].validate(context, this.certificates[j]);
                } catch (CertPathValidationException e) {
                    builder.addException(e);
                }
                j--;
            }
        }
        return builder.build();
    }

    private X509CertificateHolder[] copyArray(X509CertificateHolder[] array) {
        X509CertificateHolder[] rv = new X509CertificateHolder[array.length];
        System.arraycopy(array, 0, rv, 0, rv.length);
        return rv;
    }

    public int length() {
        return this.certificates.length;
    }
}
