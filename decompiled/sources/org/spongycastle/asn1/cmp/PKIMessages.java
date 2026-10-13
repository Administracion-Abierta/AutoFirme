package org.spongycastle.asn1.cmp;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cmp/PKIMessages.class */
public class PKIMessages extends ASN1Object {
    private ASN1Sequence content;

    private PKIMessages(ASN1Sequence seq) {
        this.content = seq;
    }

    public static PKIMessages getInstance(Object o) {
        if (o instanceof PKIMessages) {
            return (PKIMessages) o;
        }
        if (o != null) {
            return new PKIMessages(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public PKIMessages(PKIMessage msg) {
        this.content = new DERSequence(msg);
    }

    public PKIMessages(PKIMessage[] msgs) {
        ASN1EncodableVector v = new ASN1EncodableVector();
        for (PKIMessage pKIMessage : msgs) {
            v.add(pKIMessage);
        }
        this.content = new DERSequence(v);
    }

    public PKIMessage[] toPKIMessageArray() {
        PKIMessage[] result = new PKIMessage[this.content.size()];
        for (int i = 0; i != result.length; i++) {
            result[i] = PKIMessage.getInstance(this.content.getObjectAt(i));
        }
        return result;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}
