package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cms/Attribute.class */
public class Attribute extends ASN1Object {
    private ASN1ObjectIdentifier attrType;
    private ASN1Set attrValues;

    public static Attribute getInstance(Object obj) {
        if (obj instanceof Attribute) {
            return (Attribute) obj;
        }
        if (obj != null) {
            return new Attribute(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private Attribute(ASN1Sequence aSN1Sequence) {
        this.attrType = (ASN1ObjectIdentifier) aSN1Sequence.getObjectAt(0);
        this.attrValues = (ASN1Set) aSN1Sequence.getObjectAt(1);
    }

    public Attribute(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Set aSN1Set) {
        this.attrType = aSN1ObjectIdentifier;
        this.attrValues = aSN1Set;
    }

    public ASN1ObjectIdentifier getAttrType() {
        return this.attrType;
    }

    public ASN1Set getAttrValues() {
        return this.attrValues;
    }

    public ASN1Encodable[] getAttributeValues() {
        return this.attrValues.toArray();
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.attrType, this.attrValues);
    }
}
