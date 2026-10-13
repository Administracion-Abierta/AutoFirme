package org.spongycastle.cert.path.validations;

import java.util.Collection;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.cert.X509CRLHolder;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.path.CertPathValidation;
import org.spongycastle.cert.path.CertPathValidationContext;
import org.spongycastle.cert.path.CertPathValidationException;
import org.spongycastle.util.Memoable;
import org.spongycastle.util.Selector;
import org.spongycastle.util.Store;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/path/validations/CRLValidation.class */
public class CRLValidation implements CertPathValidation {
    private Store crls;
    private X500Name workingIssuerName;

    public CRLValidation(X500Name trustAnchorName, Store crls) {
        this.workingIssuerName = trustAnchorName;
        this.crls = crls;
    }

    @Override // org.spongycastle.cert.path.CertPathValidation
    public void validate(CertPathValidationContext context, X509CertificateHolder certificate) throws CertPathValidationException {
        Collection<X509CRLHolder> matches = this.crls.getMatches(new Selector() { // from class: org.spongycastle.cert.path.validations.CRLValidation.1
            @Override // org.spongycastle.util.Selector
            public boolean match(Object obj) {
                X509CRLHolder crl = (X509CRLHolder) obj;
                return crl.getIssuer().equals(CRLValidation.this.workingIssuerName);
            }

            @Override // org.spongycastle.util.Selector
            public Object clone() {
                return this;
            }
        });
        if (matches.isEmpty()) {
            throw new CertPathValidationException("CRL for " + this.workingIssuerName + " not found");
        }
        for (X509CRLHolder crl : matches) {
            if (crl.getRevokedCertificate(certificate.getSerialNumber()) != null) {
                throw new CertPathValidationException("Certificate revoked");
            }
        }
        this.workingIssuerName = certificate.getSubject();
    }

    @Override // org.spongycastle.util.Memoable
    public Memoable copy() {
        return new CRLValidation(this.workingIssuerName, this.crls);
    }

    @Override // org.spongycastle.util.Memoable
    public void reset(Memoable other) {
        CRLValidation v = (CRLValidation) other;
        this.workingIssuerName = v.workingIssuerName;
        this.crls = v.crls;
    }
}
