package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cmc/TaggedCertificationRequest.class */
public class TaggedCertificationRequest extends ASN1Object {
    private final BodyPartID bodyPartID;
    private final CertificationRequest certificationRequest;

    public TaggedCertificationRequest(BodyPartID bodyPartID, CertificationRequest certificationRequest) {
        this.bodyPartID = bodyPartID;
        this.certificationRequest = certificationRequest;
    }

    private TaggedCertificationRequest(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.bodyPartID = BodyPartID.getInstance(aSN1Sequence.getObjectAt(0));
        this.certificationRequest = CertificationRequest.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static TaggedCertificationRequest getInstance(Object obj) {
        if (obj instanceof TaggedCertificationRequest) {
            return (TaggedCertificationRequest) obj;
        }
        if (obj != null) {
            return new TaggedCertificationRequest(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static TaggedCertificationRequest getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, z));
    }

    public BodyPartID getBodyPartID() {
        return this.bodyPartID;
    }

    public CertificationRequest getCertificationRequest() {
        return this.certificationRequest;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.bodyPartID, this.certificationRequest);
    }
}
