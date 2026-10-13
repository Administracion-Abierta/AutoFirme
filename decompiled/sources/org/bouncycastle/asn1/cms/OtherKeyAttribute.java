package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cms/OtherKeyAttribute.class */
public class OtherKeyAttribute extends ASN1Object {
    private ASN1ObjectIdentifier keyAttrId;
    private ASN1Encodable keyAttr;

    public static OtherKeyAttribute getInstance(Object obj) {
        if (obj instanceof OtherKeyAttribute) {
            return (OtherKeyAttribute) obj;
        }
        if (obj != null) {
            return new OtherKeyAttribute(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private OtherKeyAttribute(ASN1Sequence aSN1Sequence) {
        this.keyAttrId = (ASN1ObjectIdentifier) aSN1Sequence.getObjectAt(0);
        if (aSN1Sequence.size() > 1) {
            this.keyAttr = aSN1Sequence.getObjectAt(1);
        }
    }

    public OtherKeyAttribute(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Encodable aSN1Encodable) {
        this.keyAttrId = aSN1ObjectIdentifier;
        this.keyAttr = aSN1Encodable;
    }

    public ASN1ObjectIdentifier getKeyAttrId() {
        return this.keyAttrId;
    }

    public ASN1Encodable getKeyAttr() {
        return this.keyAttr;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(2);
        aSN1EncodableVector.add(this.keyAttrId);
        if (this.keyAttr != null) {
            aSN1EncodableVector.add(this.keyAttr);
        }
        return new DERSequence(aSN1EncodableVector);
    }
}
