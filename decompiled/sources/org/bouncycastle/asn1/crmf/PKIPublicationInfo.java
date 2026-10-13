package org.bouncycastle.asn1.crmf;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/crmf/PKIPublicationInfo.class */
public class PKIPublicationInfo extends ASN1Object {
    public static final ASN1Integer dontPublish = new ASN1Integer(0);
    public static final ASN1Integer pleasePublish = new ASN1Integer(1);
    private ASN1Integer action;
    private ASN1Sequence pubInfos;

    private PKIPublicationInfo(ASN1Sequence aSN1Sequence) {
        this.action = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0));
        if (aSN1Sequence.size() > 1) {
            this.pubInfos = ASN1Sequence.getInstance(aSN1Sequence.getObjectAt(1));
        }
    }

    public static PKIPublicationInfo getInstance(Object obj) {
        if (obj instanceof PKIPublicationInfo) {
            return (PKIPublicationInfo) obj;
        }
        if (obj != null) {
            return new PKIPublicationInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public PKIPublicationInfo(BigInteger bigInteger) {
        this(new ASN1Integer(bigInteger));
    }

    public PKIPublicationInfo(ASN1Integer aSN1Integer) {
        this.action = aSN1Integer;
    }

    public PKIPublicationInfo(SinglePubInfo singlePubInfo) {
        this(singlePubInfo != null ? new SinglePubInfo[]{singlePubInfo} : (SinglePubInfo[]) null);
    }

    public PKIPublicationInfo(SinglePubInfo[] singlePubInfoArr) {
        this.action = pleasePublish;
        if (singlePubInfoArr != null) {
            this.pubInfos = new DERSequence(singlePubInfoArr);
        } else {
            this.pubInfos = null;
        }
    }

    public ASN1Integer getAction() {
        return this.action;
    }

    public SinglePubInfo[] getPubInfos() {
        if (this.pubInfos == null) {
            return null;
        }
        SinglePubInfo[] singlePubInfoArr = new SinglePubInfo[this.pubInfos.size()];
        for (int i = 0; i != singlePubInfoArr.length; i++) {
            singlePubInfoArr[i] = SinglePubInfo.getInstance(this.pubInfos.getObjectAt(i));
        }
        return singlePubInfoArr;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(2);
        aSN1EncodableVector.add(this.action);
        if (this.pubInfos != null) {
            aSN1EncodableVector.add(this.pubInfos);
        }
        return new DERSequence(aSN1EncodableVector);
    }
}
