package org.bouncycastle.asn1;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/DERFactory.class */
class DERFactory {
    static final DERSequence EMPTY_SEQUENCE = new DERSequence();
    static final DERSet EMPTY_SET = new DERSet();

    DERFactory() {
    }

    static DERSequence createSequence(ASN1EncodableVector aSN1EncodableVector) {
        return aSN1EncodableVector.size() < 1 ? EMPTY_SEQUENCE : new DERSequence(aSN1EncodableVector);
    }

    static DERSet createSet(ASN1EncodableVector aSN1EncodableVector) {
        return aSN1EncodableVector.size() < 1 ? EMPTY_SET : new DERSet(aSN1EncodableVector);
    }
}
