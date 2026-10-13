package org.bouncycastle.asn1.cmc;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.crmf.CertReqMsg;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cmc/TaggedRequest.class */
public class TaggedRequest extends ASN1Object implements ASN1Choice {
    public static final int TCR = 0;
    public static final int CRM = 1;
    public static final int ORM = 2;
    private final int tagNo = 0;
    private final ASN1Encodable value;

    public TaggedRequest(TaggedCertificationRequest taggedCertificationRequest) {
        this.value = taggedCertificationRequest;
    }

    public TaggedRequest(CertReqMsg certReqMsg) {
        this.value = certReqMsg;
    }

    private TaggedRequest(ASN1Sequence aSN1Sequence) {
        this.value = aSN1Sequence;
    }

    public static TaggedRequest getInstance(Object obj) {
        if (obj instanceof TaggedRequest) {
            return (TaggedRequest) obj;
        }
        if (obj == null) {
            return null;
        }
        if (!(obj instanceof ASN1Encodable)) {
            if (!(obj instanceof byte[])) {
                throw new IllegalArgumentException("unknown object in getInstance(): " + obj.getClass().getName());
            }
            try {
                return getInstance(ASN1Primitive.fromByteArray((byte[]) obj));
            } catch (IOException e) {
                throw new IllegalArgumentException("unknown encoding in getInstance()");
            }
        }
        ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(((ASN1Encodable) obj).toASN1Primitive());
        switch (aSN1TaggedObject.getTagNo()) {
            case 0:
                return new TaggedRequest(TaggedCertificationRequest.getInstance(aSN1TaggedObject, false));
            case 1:
                return new TaggedRequest(CertReqMsg.getInstance(aSN1TaggedObject, false));
            case 2:
                return new TaggedRequest(ASN1Sequence.getInstance(aSN1TaggedObject, false));
            default:
                throw new IllegalArgumentException("unknown tag in getInstance(): " + aSN1TaggedObject.getTagNo());
        }
    }

    public int getTagNo() {
        return this.tagNo;
    }

    public ASN1Encodable getValue() {
        return this.value;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(false, this.tagNo, this.value);
    }
}
