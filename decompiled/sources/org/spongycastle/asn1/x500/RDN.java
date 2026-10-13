package org.spongycastle.asn1.x500;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERSet;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/x500/RDN.class */
public class RDN extends ASN1Object {
    private ASN1Set values;

    private RDN(ASN1Set values) {
        this.values = values;
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

    public RDN(ASN1ObjectIdentifier oid, ASN1Encodable value) {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(oid);
        v.add(value);
        this.values = new DERSet(new DERSequence(v));
    }

    public RDN(AttributeTypeAndValue attrTAndV) {
        this.values = new DERSet(attrTAndV);
    }

    public RDN(AttributeTypeAndValue[] aAndVs) {
        this.values = new DERSet(aAndVs);
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
        AttributeTypeAndValue[] tmp = new AttributeTypeAndValue[this.values.size()];
        for (int i = 0; i != tmp.length; i++) {
            tmp[i] = AttributeTypeAndValue.getInstance(this.values.getObjectAt(i));
        }
        return tmp;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.values;
    }
}
