package org.spongycastle.cert.path.validations;

import org.spongycastle.cert.path.CertPath;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/path/validations/CertificatePoliciesValidationBuilder.class */
public class CertificatePoliciesValidationBuilder {
    private boolean isExplicitPolicyRequired;
    private boolean isAnyPolicyInhibited;
    private boolean isPolicyMappingInhibited;

    public void setAnyPolicyInhibited(boolean anyPolicyInhibited) {
        this.isAnyPolicyInhibited = anyPolicyInhibited;
    }

    public void setExplicitPolicyRequired(boolean explicitPolicyRequired) {
        this.isExplicitPolicyRequired = explicitPolicyRequired;
    }

    public void setPolicyMappingInhibited(boolean policyMappingInhibited) {
        this.isPolicyMappingInhibited = policyMappingInhibited;
    }

    public CertificatePoliciesValidation build(int pathLen) {
        return new CertificatePoliciesValidation(pathLen, this.isExplicitPolicyRequired, this.isAnyPolicyInhibited, this.isPolicyMappingInhibited);
    }

    public CertificatePoliciesValidation build(CertPath path) {
        return build(path.length());
    }
}
