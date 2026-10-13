package org.bouncycastle.cert.path.validations;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.path.CertPathValidation;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Memoable;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/path/validations/BasicConstraintsValidation.class */
public class BasicConstraintsValidation implements CertPathValidation {
    private boolean previousCertWasCA;
    private Integer maxPathLength;
    private boolean isMandatory;

    public BasicConstraintsValidation() {
        this(true);
    }

    public BasicConstraintsValidation(boolean z) {
        this.previousCertWasCA = true;
        this.maxPathLength = null;
        this.isMandatory = true;
        this.isMandatory = z;
    }

    @Override // org.bouncycastle.cert.path.CertPathValidation
    public void validate(CertPathValidationContext certPathValidationContext, X509CertificateHolder x509CertificateHolder) throws CertPathValidationException {
        ASN1Integer pathLenConstraintInteger;
        certPathValidationContext.addHandledExtension(Extension.basicConstraints);
        if (!this.previousCertWasCA) {
            throw new CertPathValidationException("Basic constraints violated: issuer is not a CA");
        }
        BasicConstraints basicConstraintsFromExtensions = BasicConstraints.fromExtensions(x509CertificateHolder.getExtensions());
        this.previousCertWasCA = (basicConstraintsFromExtensions != null && basicConstraintsFromExtensions.isCA()) || (basicConstraintsFromExtensions == null && !this.isMandatory);
        if (this.maxPathLength != null && !x509CertificateHolder.getSubject().equals(x509CertificateHolder.getIssuer())) {
            if (this.maxPathLength.intValue() < 0) {
                throw new CertPathValidationException("Basic constraints violated: path length exceeded");
            }
            this.maxPathLength = Integers.valueOf(this.maxPathLength.intValue() - 1);
        }
        if (basicConstraintsFromExtensions == null || !basicConstraintsFromExtensions.isCA() || (pathLenConstraintInteger = basicConstraintsFromExtensions.getPathLenConstraintInteger()) == null) {
            return;
        }
        int iIntPositiveValueExact = pathLenConstraintInteger.intPositiveValueExact();
        if (this.maxPathLength == null || iIntPositiveValueExact < this.maxPathLength.intValue()) {
            this.maxPathLength = Integers.valueOf(iIntPositiveValueExact);
        }
    }

    @Override // org.bouncycastle.util.Memoable
    public Memoable copy() {
        BasicConstraintsValidation basicConstraintsValidation = new BasicConstraintsValidation();
        basicConstraintsValidation.isMandatory = this.isMandatory;
        basicConstraintsValidation.previousCertWasCA = this.previousCertWasCA;
        basicConstraintsValidation.maxPathLength = this.maxPathLength;
        return basicConstraintsValidation;
    }

    @Override // org.bouncycastle.util.Memoable
    public void reset(Memoable memoable) {
        BasicConstraintsValidation basicConstraintsValidation = (BasicConstraintsValidation) memoable;
        this.isMandatory = basicConstraintsValidation.isMandatory;
        this.previousCertWasCA = basicConstraintsValidation.previousCertWasCA;
        this.maxPathLength = basicConstraintsValidation.maxPathLength;
    }
}
