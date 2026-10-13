package org.bouncycastle.asn1.x500;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSet;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x500/RDN.class */
public class RDN extends ASN1Object {
    private ASN1Set values;

    private RDN(ASN1Set aSN1Set) {
        this.values = aSN1Set;
    }

    public static RDN getInstance(Object obj) {
        if (obj instanceof RDN) {
            return (RDN) obj;
        }
        if (obj != null) {
            return new RDN(ASN1Set.getInstance(obj));
        }
        return null;
    }

    public static RDN getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return new RDN(ASN1Set.getInstance(aSN1TaggedObject, z));
    }

    public RDN(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Encodable aSN1Encodable) {
        this(new AttributeTypeAndValue(aSN1ObjectIdentifier, aSN1Encodable));
    }

    public RDN(AttributeTypeAndValue attributeTypeAndValue) {
        this.values = new DERSet(attributeTypeAndValue);
    }

    public RDN(AttributeTypeAndValue[] attributeTypeAndValueArr) {
        this.values = new DERSet(attributeTypeAndValueArr);
    }

    public boolean isMultiValued() {
        return this.values.size() > 1;
    }

    public int size() {
        return this.values.size();
    }

    public AttributeTypeAndValue getFirst() {
        if (this.values.size() == 0) {
            return null;
        }
        return AttributeTypeAndValue.getInstance(this.values.getObjectAt(0));
    }

    public AttributeTypeAndValue[] getTypesAndValues() {
        AttributeTypeAndValue[] attributeTypeAndValueArr = new AttributeTypeAndValue[this.values.size()];
        for (int i = 0; i != attributeTypeAndValueArr.length; i++) {
            attributeTypeAndValueArr[i] = AttributeTypeAndValue.getInstance(this.values.getObjectAt(i));
        }
        return attributeTypeAndValueArr;
    }

    int collectAttributeTypes(ASN1ObjectIdentifier[] aSN1ObjectIdentifierArr, int i) {
        int size = this.values.size();
        for (int i2 = 0; i2 < size; i2++) {
            aSN1ObjectIdentifierArr[i + i2] = AttributeTypeAndValue.getInstance(this.values.getObjectAt(i2)).getType();
        }
        return size;
    }

    boolean containsAttributeType(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        int size = this.values.size();
        for (int i = 0; i < size; i++) {
            if (AttributeTypeAndValue.getInstance(this.values.getObjectAt(i)).getType().equals((ASN1Primitive) aSN1ObjectIdentifier)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.values;
    }
}
