package org.spongycastle.asn1.esf;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/esf/SigPolicyQualifierInfo.class */
public class SigPolicyQualifierInfo extends ASN1Object {
    private ASN1ObjectIdentifier sigPolicyQualifierId;
    private ASN1Encodable sigQualifier;

    public SigPolicyQualifierInfo(ASN1ObjectIdentifier sigPolicyQualifierId, ASN1Encodable sigQualifier) {
        this.sigPolicyQualifierId = sigPolicyQualifierId;
        this.sigQualifier = sigQualifier;
    }

    private SigPolicyQualifierInfo(ASN1Sequence seq) {
        this.sigPolicyQualifierId = ASN1ObjectIdentifier.getInstance(seq.getObjectAt(0));
        this.sigQualifier = seq.getObjectAt(1);
    }

    public static SigPolicyQualifierInfo getInstance(Object obj) {
        if (obj instanceof SigPolicyQualifierInfo) {
            return (SigPolicyQualifierInfo) obj;
        }
        if (obj != null) {
            return new SigPolicyQualifierInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1ObjectIdentifier getSigPolicyQualifierId() {
        return new ASN1ObjectIdentifier(this.sigPolicyQualifierId.getId());
    }

    public ASN1Encodable getSigQualifier() {
        return this.sigQualifier;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.sigPolicyQualifierId);
        v.add(this.sigQualifier);
        return new DERSequence(v);
    }
}
