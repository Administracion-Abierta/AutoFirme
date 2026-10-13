package org.bouncycastle.cert.path.validations;

import org.bouncycastle.cert.path.CertPath;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/path/validations/CertificatePoliciesValidationBuilder.class */
public class CertificatePoliciesValidationBuilder {
    private boolean isExplicitPolicyRequired;
    private boolean isAnyPolicyInhibited;
    private boolean isPolicyMappingInhibited;

    public void setAnyPolicyInhibited(boolean z) {
        this.isAnyPolicyInhibited = z;
    }

    public void setExplicitPolicyRequired(boolean z) {
        this.isExplicitPolicyRequired = z;
    }

    public void setPolicyMappingInhibited(boolean z) {
        this.isPolicyMappingInhibited = z;
    }

    public CertificatePoliciesValidation build(int i) {
        return new CertificatePoliciesValidation(i, this.isExplicitPolicyRequired, this.isAnyPolicyInhibited, this.isPolicyMappingInhibited);
    }

    public CertificatePoliciesValidation build(CertPath certPath) {
        return build(certPath.length());
    }
}
