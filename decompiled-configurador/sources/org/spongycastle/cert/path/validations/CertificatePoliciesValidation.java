package org.spongycastle.cert.path.validations;

import java.math.BigInteger;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.asn1.x509.PolicyConstraints;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.path.CertPathValidation;
import org.spongycastle.cert.path.CertPathValidationContext;
import org.spongycastle.cert.path.CertPathValidationException;
import org.spongycastle.util.Memoable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/path/validations/CertificatePoliciesValidation.class */
public class CertificatePoliciesValidation implements CertPathValidation {
    private int explicitPolicy;
    private int policyMapping;
    private int inhibitAnyPolicy;

    CertificatePoliciesValidation(int pathLength) {
        this(pathLength, false, false, false);
    }

    CertificatePoliciesValidation(int pathLength, boolean isExplicitPolicyRequired, boolean isAnyPolicyInhibited, boolean isPolicyMappingInhibited) {
        if (isExplicitPolicyRequired) {
            this.explicitPolicy = 0;
        } else {
            this.explicitPolicy = pathLength + 1;
        }
        if (isAnyPolicyInhibited) {
            this.inhibitAnyPolicy = 0;
        } else {
            this.inhibitAnyPolicy = pathLength + 1;
        }
        if (isPolicyMappingInhibited) {
            this.policyMapping = 0;
        } else {
            this.policyMapping = pathLength + 1;
        }
    }

    @Override // org.spongycastle.cert.path.CertPathValidation
    public void validate(CertPathValidationContext context, X509CertificateHolder certificate) throws CertPathValidationException {
        int extValue;
        context.addHandledExtension(Extension.policyConstraints);
        context.addHandledExtension(Extension.inhibitAnyPolicy);
        if (!context.isEndEntity() && !ValidationUtils.isSelfIssued(certificate)) {
            this.explicitPolicy = countDown(this.explicitPolicy);
            this.policyMapping = countDown(this.policyMapping);
            this.inhibitAnyPolicy = countDown(this.inhibitAnyPolicy);
            PolicyConstraints policyConstraints = PolicyConstraints.fromExtensions(certificate.getExtensions());
            if (policyConstraints != null) {
                BigInteger requireExplicitPolicyMapping = policyConstraints.getRequireExplicitPolicyMapping();
                if (requireExplicitPolicyMapping != null && requireExplicitPolicyMapping.intValue() < this.explicitPolicy) {
                    this.explicitPolicy = requireExplicitPolicyMapping.intValue();
                }
                BigInteger inhibitPolicyMapping = policyConstraints.getInhibitPolicyMapping();
                if (inhibitPolicyMapping != null && inhibitPolicyMapping.intValue() < this.policyMapping) {
                    this.policyMapping = inhibitPolicyMapping.intValue();
                }
            }
            Extension ext = certificate.getExtension(Extension.inhibitAnyPolicy);
            if (ext != null && (extValue = ASN1Integer.getInstance(ext.getParsedValue()).getValue().intValue()) < this.inhibitAnyPolicy) {
                this.inhibitAnyPolicy = extValue;
            }
        }
    }

    private int countDown(int policyCounter) {
        if (policyCounter != 0) {
            return policyCounter - 1;
        }
        return 0;
    }

    @Override // org.spongycastle.util.Memoable
    public Memoable copy() {
        return new CertificatePoliciesValidation(0);
    }

    @Override // org.spongycastle.util.Memoable
    public void reset(Memoable other) {
    }
}
