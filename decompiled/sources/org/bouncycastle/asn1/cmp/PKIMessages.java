package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cmp/PKIMessages.class */
public class PKIMessages extends ASN1Object {
    private final ASN1Sequence content;

    protected PKIMessages(ASN1Sequence aSN1Sequence) {
        this.content = aSN1Sequence;
    }

    public PKIMessages(PKIMessage pKIMessage) {
        this.content = new DERSequence(pKIMessage);
    }

    public PKIMessages(PKIMessage[] pKIMessageArr) {
        this.content = new DERSequence(pKIMessageArr);
    }

    public static PKIMessages getInstance(Object obj) {
        if (obj instanceof PKIMessages) {
            return (PKIMessages) obj;
        }
        if (obj != null) {
            return new PKIMessages(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public PKIMessage[] toPKIMessageArray() {
        PKIMessage[] pKIMessageArr = new PKIMessage[this.content.size()];
        for (int i = 0; i != pKIMessageArr.length; i++) {
            pKIMessageArr[i] = PKIMessage.getInstance(this.content.getObjectAt(i));
        }
        return pKIMessageArr;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}
