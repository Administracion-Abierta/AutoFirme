package org.bouncycastle.jce.provider;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralSubtree;
import org.bouncycastle.asn1.x509.NameConstraintValidatorException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/provider/PKIXNameConstraintValidator.class */
public class PKIXNameConstraintValidator {
    org.bouncycastle.asn1.x509.PKIXNameConstraintValidator validator = new org.bouncycastle.asn1.x509.PKIXNameConstraintValidator();

    public int hashCode() {
        return this.validator.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof PKIXNameConstraintValidator) {
            return this.validator.equals(((PKIXNameConstraintValidator) obj).validator);
        }
        return false;
    }

    public void checkPermittedDN(ASN1Sequence aSN1Sequence) throws PKIXNameConstraintValidatorException {
        try {
            this.validator.checkPermittedDN(X500Name.getInstance(aSN1Sequence));
        } catch (NameConstraintValidatorException e) {
            throw new PKIXNameConstraintValidatorException(e.getMessage(), e);
        }
    }

    public void checkExcludedDN(ASN1Sequence aSN1Sequence) throws PKIXNameConstraintValidatorException {
        try {
            this.validator.checkExcludedDN(X500Name.getInstance(aSN1Sequence));
        } catch (NameConstraintValidatorException e) {
            throw new PKIXNameConstraintValidatorException(e.getMessage(), e);
        }
    }

    public void checkPermitted(GeneralName generalName) throws PKIXNameConstraintValidatorException {
        try {
            this.validator.checkPermitted(generalName);
        } catch (NameConstraintValidatorException e) {
            throw new PKIXNameConstraintValidatorException(e.getMessage(), e);
        }
    }

    public void checkExcluded(GeneralName generalName) throws PKIXNameConstraintValidatorException {
        try {
            this.validator.checkExcluded(generalName);
        } catch (NameConstraintValidatorException e) {
            throw new PKIXNameConstraintValidatorException(e.getMessage(), e);
        }
    }

    public void intersectPermittedSubtree(GeneralSubtree generalSubtree) {
        this.validator.intersectPermittedSubtree(generalSubtree);
    }

    public void intersectPermittedSubtree(GeneralSubtree[] generalSubtreeArr) {
        this.validator.intersectPermittedSubtree(generalSubtreeArr);
    }

    public void intersectEmptyPermittedSubtree(int i) {
        this.validator.intersectEmptyPermittedSubtree(i);
    }

    public void addExcludedSubtree(GeneralSubtree generalSubtree) {
        this.validator.addExcludedSubtree(generalSubtree);
    }

    public String toString() {
        return this.validator.toString();
    }
}
