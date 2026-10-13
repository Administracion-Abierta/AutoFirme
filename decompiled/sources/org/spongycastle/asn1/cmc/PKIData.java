package org.spongycastle.asn1.cmc;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cmc/PKIData.class */
public class PKIData extends ASN1Object {
    private final TaggedAttribute[] controlSequence;
    private final TaggedRequest[] reqSequence;
    private final TaggedContentInfo[] cmsSequence;
    private final OtherMsg[] otherMsgSequence;

    public PKIData(TaggedAttribute[] controlSequence, TaggedRequest[] reqSequence, TaggedContentInfo[] cmsSequence, OtherMsg[] otherMsgSequence) {
        this.controlSequence = controlSequence;
        this.reqSequence = reqSequence;
        this.cmsSequence = cmsSequence;
        this.otherMsgSequence = otherMsgSequence;
    }

    private PKIData(ASN1Sequence seq) {
        if (seq.size() != 4) {
            throw new IllegalArgumentException("Sequence not 4 elements.");
        }
        ASN1Sequence s = (ASN1Sequence) seq.getObjectAt(0);
        this.controlSequence = new TaggedAttribute[s.size()];
        for (int t = 0; t < this.controlSequence.length; t++) {
            this.controlSequence[t] = TaggedAttribute.getInstance(s.getObjectAt(t));
        }
        ASN1Sequence s2 = (ASN1Sequence) seq.getObjectAt(1);
        this.reqSequence = new TaggedRequest[s2.size()];
        for (int t2 = 0; t2 < this.reqSequence.length; t2++) {
            this.reqSequence[t2] = TaggedRequest.getInstance(s2.getObjectAt(t2));
        }
        ASN1Sequence s3 = (ASN1Sequence) seq.getObjectAt(2);
        this.cmsSequence = new TaggedContentInfo[s3.size()];
        for (int t3 = 0; t3 < this.cmsSequence.length; t3++) {
            this.cmsSequence[t3] = TaggedContentInfo.getInstance(s3.getObjectAt(t3));
        }
        ASN1Sequence s4 = (ASN1Sequence) seq.getObjectAt(3);
        this.otherMsgSequence = new OtherMsg[s4.size()];
        for (int t4 = 0; t4 < this.otherMsgSequence.length; t4++) {
            this.otherMsgSequence[t4] = OtherMsg.getInstance(s4.getObjectAt(t4));
        }
    }

    public static PKIData getInstance(Object src) {
        if (src instanceof PKIData) {
            return (PKIData) src;
        }
        if (src != null) {
            return new PKIData(ASN1Sequence.getInstance(src));
        }
        return null;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{new DERSequence(this.controlSequence), new DERSequence(this.reqSequence), new DERSequence(this.cmsSequence), new DERSequence(this.otherMsgSequence)});
    }

    public TaggedAttribute[] getControlSequence() {
        return this.controlSequence;
    }

    public TaggedRequest[] getReqSequence() {
        return this.reqSequence;
    }

    public TaggedContentInfo[] getCmsSequence() {
        return this.cmsSequence;
    }

    public OtherMsg[] getOtherMsgSequence() {
        return this.otherMsgSequence;
    }
}
