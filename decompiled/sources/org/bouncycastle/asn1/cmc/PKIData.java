package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cmc/PKIData.class */
public class PKIData extends ASN1Object {
    private final TaggedAttribute[] controlSequence;
    private final TaggedRequest[] reqSequence;
    private final TaggedContentInfo[] cmsSequence;
    private final OtherMsg[] otherMsgSequence;

    public PKIData(TaggedAttribute[] taggedAttributeArr, TaggedRequest[] taggedRequestArr, TaggedContentInfo[] taggedContentInfoArr, OtherMsg[] otherMsgArr) {
        this.controlSequence = copy(taggedAttributeArr);
        this.reqSequence = copy(taggedRequestArr);
        this.cmsSequence = copy(taggedContentInfoArr);
        this.otherMsgSequence = copy(otherMsgArr);
    }

    private PKIData(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 4) {
            throw new IllegalArgumentException("Sequence not 4 elements.");
        }
        ASN1Sequence aSN1Sequence2 = (ASN1Sequence) aSN1Sequence.getObjectAt(0);
        this.controlSequence = new TaggedAttribute[aSN1Sequence2.size()];
        for (int i = 0; i < this.controlSequence.length; i++) {
            this.controlSequence[i] = TaggedAttribute.getInstance(aSN1Sequence2.getObjectAt(i));
        }
        ASN1Sequence aSN1Sequence3 = (ASN1Sequence) aSN1Sequence.getObjectAt(1);
        this.reqSequence = new TaggedRequest[aSN1Sequence3.size()];
        for (int i2 = 0; i2 < this.reqSequence.length; i2++) {
            this.reqSequence[i2] = TaggedRequest.getInstance(aSN1Sequence3.getObjectAt(i2));
        }
        ASN1Sequence aSN1Sequence4 = (ASN1Sequence) aSN1Sequence.getObjectAt(2);
        this.cmsSequence = new TaggedContentInfo[aSN1Sequence4.size()];
        for (int i3 = 0; i3 < this.cmsSequence.length; i3++) {
            this.cmsSequence[i3] = TaggedContentInfo.getInstance(aSN1Sequence4.getObjectAt(i3));
        }
        ASN1Sequence aSN1Sequence5 = (ASN1Sequence) aSN1Sequence.getObjectAt(3);
        this.otherMsgSequence = new OtherMsg[aSN1Sequence5.size()];
        for (int i4 = 0; i4 < this.otherMsgSequence.length; i4++) {
            this.otherMsgSequence[i4] = OtherMsg.getInstance(aSN1Sequence5.getObjectAt(i4));
        }
    }

    public static PKIData getInstance(Object obj) {
        if (obj instanceof PKIData) {
            return (PKIData) obj;
        }
        if (obj != null) {
            return new PKIData(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{new DERSequence(this.controlSequence), new DERSequence(this.reqSequence), new DERSequence(this.cmsSequence), new DERSequence(this.otherMsgSequence)});
    }

    public TaggedAttribute[] getControlSequence() {
        return copy(this.controlSequence);
    }

    private TaggedAttribute[] copy(TaggedAttribute[] taggedAttributeArr) {
        TaggedAttribute[] taggedAttributeArr2 = new TaggedAttribute[taggedAttributeArr.length];
        System.arraycopy(taggedAttributeArr, 0, taggedAttributeArr2, 0, taggedAttributeArr2.length);
        return taggedAttributeArr2;
    }

    public TaggedRequest[] getReqSequence() {
        return copy(this.reqSequence);
    }

    private TaggedRequest[] copy(TaggedRequest[] taggedRequestArr) {
        TaggedRequest[] taggedRequestArr2 = new TaggedRequest[taggedRequestArr.length];
        System.arraycopy(taggedRequestArr, 0, taggedRequestArr2, 0, taggedRequestArr2.length);
        return taggedRequestArr2;
    }

    public TaggedContentInfo[] getCmsSequence() {
        return copy(this.cmsSequence);
    }

    private TaggedContentInfo[] copy(TaggedContentInfo[] taggedContentInfoArr) {
        TaggedContentInfo[] taggedContentInfoArr2 = new TaggedContentInfo[taggedContentInfoArr.length];
        System.arraycopy(taggedContentInfoArr, 0, taggedContentInfoArr2, 0, taggedContentInfoArr2.length);
        return taggedContentInfoArr2;
    }

    public OtherMsg[] getOtherMsgSequence() {
        return copy(this.otherMsgSequence);
    }

    private OtherMsg[] copy(OtherMsg[] otherMsgArr) {
        OtherMsg[] otherMsgArr2 = new OtherMsg[otherMsgArr.length];
        System.arraycopy(otherMsgArr, 0, otherMsgArr2, 0, otherMsgArr2.length);
        return otherMsgArr2;
    }
}
