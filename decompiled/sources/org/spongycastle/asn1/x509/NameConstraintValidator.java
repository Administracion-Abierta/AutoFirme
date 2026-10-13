package org.spongycastle.asn1.x509;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/x509/NameConstraintValidator.class */
public interface NameConstraintValidator {
    void checkPermitted(GeneralName generalName) throws NameConstraintValidatorException;

    void checkExcluded(GeneralName generalName) throws NameConstraintValidatorException;

    void intersectPermittedSubtree(GeneralSubtree generalSubtree);

    void intersectPermittedSubtree(GeneralSubtree[] generalSubtreeArr);

    void intersectEmptyPermittedSubtree(int i);

    void addExcludedSubtree(GeneralSubtree generalSubtree);
}
