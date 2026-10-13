package org.spongycastle.asn1.cmp;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/cmp/RevReqContent.class */
public class RevReqContent extends ASN1Object {
    private ASN1Sequence content;

    private RevReqContent(ASN1Sequence seq) {
        this.content = seq;
    }

    public static RevReqContent getInstance(Object o) {
        if (o instanceof RevReqContent) {
            return (RevReqContent) o;
        }
        if (o != null) {
            return new RevReqContent(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public RevReqContent(RevDetails revDetails) {
        this.content = new DERSequence(revDetails);
    }

    public RevReqContent(RevDetails[] revDetailsArray) {
        ASN1EncodableVector v = new ASN1EncodableVector();
        for (int i = 0; i != revDetailsArray.length; i++) {
            v.add(revDetailsArray[i]);
        }
        this.content = new DERSequence(v);
    }

    public RevDetails[] toRevDetailsArray() {
        RevDetails[] result = new RevDetails[this.content.size()];
        for (int i = 0; i != result.length; i++) {
            result[i] = RevDetails.getInstance(this.content.getObjectAt(i));
        }
        return result;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}
