package org.spongycastle.asn1.crmf;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/crmf/Controls.class */
public class Controls extends ASN1Object {
    private ASN1Sequence content;

    private Controls(ASN1Sequence seq) {
        this.content = seq;
    }

    public static Controls getInstance(Object o) {
        if (o instanceof Controls) {
            return (Controls) o;
        }
        if (o != null) {
            return new Controls(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public Controls(AttributeTypeAndValue atv) {
        this.content = new DERSequence(atv);
    }

    public Controls(AttributeTypeAndValue[] atvs) {
        ASN1EncodableVector v = new ASN1EncodableVector();
        for (AttributeTypeAndValue attributeTypeAndValue : atvs) {
            v.add(attributeTypeAndValue);
        }
        this.content = new DERSequence(v);
    }

    public AttributeTypeAndValue[] toAttributeTypeAndValueArray() {
        AttributeTypeAndValue[] result = new AttributeTypeAndValue[this.content.size()];
        for (int i = 0; i != result.length; i++) {
            result[i] = AttributeTypeAndValue.getInstance(this.content.getObjectAt(i));
        }
        return result;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}
