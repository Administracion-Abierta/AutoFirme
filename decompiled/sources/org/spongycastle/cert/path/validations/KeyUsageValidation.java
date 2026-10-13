package org.spongycastle.cert.path.validations;

import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.asn1.x509.KeyUsage;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.path.CertPathValidation;
import org.spongycastle.cert.path.CertPathValidationContext;
import org.spongycastle.cert.path.CertPathValidationException;
import org.spongycastle.util.Memoable;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/path/validations/KeyUsageValidation.class */
public class KeyUsageValidation implements CertPathValidation {
    private boolean isMandatory;

    public KeyUsageValidation() {
        this(true);
    }

    public KeyUsageValidation(boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    @Override // org.spongycastle.cert.path.CertPathValidation
    public void validate(CertPathValidationContext context, X509CertificateHolder certificate) throws CertPathValidationException {
        context.addHandledExtension(Extension.keyUsage);
        if (!context.isEndEntity()) {
            KeyUsage usage = KeyUsage.fromExtensions(certificate.getExtensions());
            if (usage != null) {
                if (!usage.hasUsages(4)) {
                    throw new CertPathValidationException("Issuer certificate KeyUsage extension does not permit key signing");
                }
            } else if (this.isMandatory) {
                throw new CertPathValidationException("KeyUsage extension not present in CA certificate");
            }
        }
    }

    @Override // org.spongycastle.util.Memoable
    public Memoable copy() {
        return new KeyUsageValidation(this.isMandatory);
    }

    @Override // org.spongycastle.util.Memoable
    public void reset(Memoable other) {
        KeyUsageValidation v = (KeyUsageValidation) other;
        this.isMandatory = v.isMandatory;
    }
}
