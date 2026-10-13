package org.spongycastle.asn1.crmf;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/crmf/PKIPublicationInfo.class */
public class PKIPublicationInfo extends ASN1Object {
    private ASN1Integer action;
    private ASN1Sequence pubInfos;

    private PKIPublicationInfo(ASN1Sequence seq) {
        this.action = ASN1Integer.getInstance(seq.getObjectAt(0));
        this.pubInfos = ASN1Sequence.getInstance(seq.getObjectAt(1));
    }

    public static PKIPublicationInfo getInstance(Object o) {
        if (o instanceof PKIPublicationInfo) {
            return (PKIPublicationInfo) o;
        }
        if (o != null) {
            return new PKIPublicationInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public ASN1Integer getAction() {
        return this.action;
    }

    public SinglePubInfo[] getPubInfos() {
        if (this.pubInfos == null) {
            return null;
        }
        SinglePubInfo[] results = new SinglePubInfo[this.pubInfos.size()];
        for (int i = 0; i != results.length; i++) {
            results[i] = SinglePubInfo.getInstance(this.pubInfos.getObjectAt(i));
        }
        return results;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.action);
        v.add(this.pubInfos);
        return new DERSequence(v);
    }
}
