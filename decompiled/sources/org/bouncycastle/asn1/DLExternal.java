package org.bouncycastle.asn1;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/DLExternal.class */
public class DLExternal extends ASN1External {
    public DLExternal(ASN1EncodableVector aSN1EncodableVector) {
        this(DLFactory.createSequence(aSN1EncodableVector));
    }

    public DLExternal(DLSequence dLSequence) {
        super(dLSequence);
    }

    public DLExternal(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Integer aSN1Integer, ASN1Primitive aSN1Primitive, DERTaggedObject dERTaggedObject) {
        super(aSN1ObjectIdentifier, aSN1Integer, aSN1Primitive, dERTaggedObject);
    }

    public DLExternal(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Integer aSN1Integer, ASN1Primitive aSN1Primitive, int i, ASN1Primitive aSN1Primitive2) {
        super(aSN1ObjectIdentifier, aSN1Integer, aSN1Primitive, i, aSN1Primitive2);
    }

    @Override // org.bouncycastle.asn1.ASN1External
    ASN1Sequence buildSequence() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(4);
        if (this.directReference != null) {
            aSN1EncodableVector.add(this.directReference);
        }
        if (this.indirectReference != null) {
            aSN1EncodableVector.add(this.indirectReference);
        }
        if (this.dataValueDescriptor != null) {
            aSN1EncodableVector.add(this.dataValueDescriptor.toDLObject());
        }
        aSN1EncodableVector.add(new DLTaggedObject(0 == this.encoding, this.encoding, this.externalContent));
        return new DLSequence(aSN1EncodableVector);
    }

    @Override // org.bouncycastle.asn1.ASN1External, org.bouncycastle.asn1.ASN1Primitive
    ASN1Primitive toDLObject() {
        return this;
    }
}
