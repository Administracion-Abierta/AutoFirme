package org.bouncycastle.cert.path.validations;

import java.util.Collection;
import java.util.Iterator;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.path.CertPathValidation;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/path/validations/CRLValidation.class */
public class CRLValidation implements CertPathValidation {
    private Store crls;
    private X500Name workingIssuerName;

    public CRLValidation(X500Name x500Name, Store store) {
        this.workingIssuerName = x500Name;
        this.crls = store;
    }

    @Override // org.bouncycastle.cert.path.CertPathValidation
    public void validate(CertPathValidationContext certPathValidationContext, X509CertificateHolder x509CertificateHolder) throws CertPathValidationException {
        Collection matches = this.crls.getMatches(new Selector() { // from class: org.bouncycastle.cert.path.validations.CRLValidation.1
            @Override // org.bouncycastle.util.Selector
            public boolean match(Object obj) {
                return ((X509CRLHolder) obj).getIssuer().equals(CRLValidation.this.workingIssuerName);
            }

            @Override // org.bouncycastle.util.Selector
            public Object clone() {
                return this;
            }
        });
        if (matches.isEmpty()) {
            throw new CertPathValidationException("CRL for " + this.workingIssuerName + " not found");
        }
        Iterator it = matches.iterator();
        while (it.hasNext()) {
            if (((X509CRLHolder) it.next()).getRevokedCertificate(x509CertificateHolder.getSerialNumber()) != null) {
                throw new CertPathValidationException("Certificate revoked");
            }
        }
        this.workingIssuerName = x509CertificateHolder.getSubject();
    }

    @Override // org.bouncycastle.util.Memoable
    public Memoable copy() {
        return new CRLValidation(this.workingIssuerName, this.crls);
    }

    @Override // org.bouncycastle.util.Memoable
    public void reset(Memoable memoable) {
        CRLValidation cRLValidation = (CRLValidation) memoable;
        this.workingIssuerName = cRLValidation.workingIssuerName;
        this.crls = cRLValidation.crls;
    }
}
