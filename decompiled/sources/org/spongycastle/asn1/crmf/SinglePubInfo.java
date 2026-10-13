package org.spongycastle.asn1.crmf;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x509.GeneralName;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/crmf/SinglePubInfo.class */
public class SinglePubInfo extends ASN1Object {
    private ASN1Integer pubMethod;
    private GeneralName pubLocation;

    private SinglePubInfo(ASN1Sequence seq) {
        this.pubMethod = ASN1Integer.getInstance(seq.getObjectAt(0));
        if (seq.size() == 2) {
            this.pubLocation = GeneralName.getInstance(seq.getObjectAt(1));
        }
    }

    public static SinglePubInfo getInstance(Object o) {
        if (o instanceof SinglePubInfo) {
            return (SinglePubInfo) o;
        }
        if (o != null) {
            return new SinglePubInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public GeneralName getPubLocation() {
        return this.pubLocation;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.pubMethod);
        if (this.pubLocation != null) {
            v.add(this.pubLocation);
        }
        return new DERSequence(v);
    }
}
