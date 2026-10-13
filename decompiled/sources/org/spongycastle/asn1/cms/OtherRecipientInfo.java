package org.spongycastle.asn1.cms;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cms/OtherRecipientInfo.class */
public class OtherRecipientInfo extends ASN1Object {
    private ASN1ObjectIdentifier oriType;
    private ASN1Encodable oriValue;

    public OtherRecipientInfo(ASN1ObjectIdentifier oriType, ASN1Encodable oriValue) {
        this.oriType = oriType;
        this.oriValue = oriValue;
    }

    public OtherRecipientInfo(ASN1Sequence seq) {
        this.oriType = ASN1ObjectIdentifier.getInstance(seq.getObjectAt(0));
        this.oriValue = seq.getObjectAt(1);
    }

    public static OtherRecipientInfo getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static OtherRecipientInfo getInstance(Object obj) {
        if (obj instanceof OtherRecipientInfo) {
            return (OtherRecipientInfo) obj;
        }
        if (obj != null) {
            return new OtherRecipientInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1ObjectIdentifier getType() {
        return this.oriType;
    }

    public ASN1Encodable getValue() {
        return this.oriValue;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.oriType);
        v.add(this.oriValue);
        return new DERSequence(v);
    }
}
