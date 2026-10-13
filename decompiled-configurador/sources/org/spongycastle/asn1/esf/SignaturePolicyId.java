package org.spongycastle.asn1.esf;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/esf/SignaturePolicyId.class */
public class SignaturePolicyId extends ASN1Object {
    private ASN1ObjectIdentifier sigPolicyId;
    private OtherHashAlgAndValue sigPolicyHash;
    private SigPolicyQualifiers sigPolicyQualifiers;

    public static SignaturePolicyId getInstance(Object obj) {
        if (obj instanceof SignaturePolicyId) {
            return (SignaturePolicyId) obj;
        }
        if (obj != null) {
            return new SignaturePolicyId(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private SignaturePolicyId(ASN1Sequence seq) {
        if (seq.size() != 2 && seq.size() != 3) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        this.sigPolicyId = ASN1ObjectIdentifier.getInstance(seq.getObjectAt(0));
        this.sigPolicyHash = OtherHashAlgAndValue.getInstance(seq.getObjectAt(1));
        if (seq.size() == 3) {
            this.sigPolicyQualifiers = SigPolicyQualifiers.getInstance(seq.getObjectAt(2));
        }
    }

    public SignaturePolicyId(ASN1ObjectIdentifier sigPolicyIdentifier, OtherHashAlgAndValue sigPolicyHash) {
        this(sigPolicyIdentifier, sigPolicyHash, null);
    }

    public SignaturePolicyId(ASN1ObjectIdentifier sigPolicyId, OtherHashAlgAndValue sigPolicyHash, SigPolicyQualifiers sigPolicyQualifiers) {
        this.sigPolicyId = sigPolicyId;
        this.sigPolicyHash = sigPolicyHash;
        this.sigPolicyQualifiers = sigPolicyQualifiers;
    }

    public ASN1ObjectIdentifier getSigPolicyId() {
        return new ASN1ObjectIdentifier(this.sigPolicyId.getId());
    }

    public OtherHashAlgAndValue getSigPolicyHash() {
        return this.sigPolicyHash;
    }

    public SigPolicyQualifiers getSigPolicyQualifiers() {
        return this.sigPolicyQualifiers;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.sigPolicyId);
        v.add(this.sigPolicyHash);
        if (this.sigPolicyQualifiers != null) {
            v.add(this.sigPolicyQualifiers);
        }
        return new DERSequence(v);
    }
}
