package org.spongycastle.cert.path.validations;

import java.math.BigInteger;
import org.spongycastle.asn1.x509.BasicConstraints;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.path.CertPathValidation;
import org.spongycastle.cert.path.CertPathValidationContext;
import org.spongycastle.cert.path.CertPathValidationException;
import org.spongycastle.util.Memoable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/path/validations/BasicConstraintsValidation.class */
public class BasicConstraintsValidation implements CertPathValidation {
    private boolean isMandatory;
    private BasicConstraints bc;
    private int pathLengthRemaining;
    private BigInteger maxPathLength;

    public BasicConstraintsValidation() {
        this(true);
    }

    public BasicConstraintsValidation(boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    @Override // org.spongycastle.cert.path.CertPathValidation
    public void validate(CertPathValidationContext context, X509CertificateHolder certificate) throws CertPathValidationException {
        BigInteger pathLengthConstraint;
        int plc;
        if (this.maxPathLength != null && this.pathLengthRemaining < 0) {
            throw new CertPathValidationException("BasicConstraints path length exceeded");
        }
        context.addHandledExtension(Extension.basicConstraints);
        BasicConstraints certBC = BasicConstraints.fromExtensions(certificate.getExtensions());
        if (certBC != null) {
            if (this.bc != null) {
                if (certBC.isCA() && (pathLengthConstraint = certBC.getPathLenConstraint()) != null && (plc = pathLengthConstraint.intValue()) < this.pathLengthRemaining) {
                    this.pathLengthRemaining = plc;
                    this.bc = certBC;
                }
            } else {
                this.bc = certBC;
                if (certBC.isCA()) {
                    this.maxPathLength = certBC.getPathLenConstraint();
                    if (this.maxPathLength != null) {
                        this.pathLengthRemaining = this.maxPathLength.intValue();
                    }
                }
            }
        } else if (this.bc != null) {
            this.pathLengthRemaining--;
        }
        if (this.isMandatory && this.bc == null) {
            throw new CertPathValidationException("BasicConstraints not present in path");
        }
    }

    @Override // org.spongycastle.util.Memoable
    public Memoable copy() {
        BasicConstraintsValidation v = new BasicConstraintsValidation(this.isMandatory);
        v.bc = this.bc;
        v.pathLengthRemaining = this.pathLengthRemaining;
        return v;
    }

    @Override // org.spongycastle.util.Memoable
    public void reset(Memoable other) {
        BasicConstraintsValidation v = (BasicConstraintsValidation) other;
        this.isMandatory = v.isMandatory;
        this.bc = v.bc;
        this.pathLengthRemaining = v.pathLengthRemaining;
    }
}
