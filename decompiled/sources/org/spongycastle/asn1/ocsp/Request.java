package org.spongycastle.asn1.ocsp;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.x509.Extensions;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/ocsp/Request.class */
public class Request extends ASN1Object {
    CertID reqCert;
    Extensions singleRequestExtensions;

    public Request(CertID reqCert, Extensions singleRequestExtensions) {
        this.reqCert = reqCert;
        this.singleRequestExtensions = singleRequestExtensions;
    }

    private Request(ASN1Sequence seq) {
        this.reqCert = CertID.getInstance(seq.getObjectAt(0));
        if (seq.size() == 2) {
            this.singleRequestExtensions = Extensions.getInstance((ASN1TaggedObject) seq.getObjectAt(1), true);
        }
    }

    public static Request getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static Request getInstance(Object obj) {
        if (obj instanceof Request) {
            return (Request) obj;
        }
        if (obj != null) {
            return new Request(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public CertID getReqCert() {
        return this.reqCert;
    }

    public Extensions getSingleRequestExtensions() {
        return this.singleRequestExtensions;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.reqCert);
        if (this.singleRequestExtensions != null) {
            v.add(new DERTaggedObject(true, 0, this.singleRequestExtensions));
        }
        return new DERSequence(v);
    }
}
