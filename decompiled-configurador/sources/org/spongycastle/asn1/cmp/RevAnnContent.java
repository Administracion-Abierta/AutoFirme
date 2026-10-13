package org.spongycastle.asn1.cmp;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1GeneralizedTime;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.crmf.CertId;
import org.spongycastle.asn1.x509.Extensions;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/cmp/RevAnnContent.class */
public class RevAnnContent extends ASN1Object {
    private PKIStatus status;
    private CertId certId;
    private ASN1GeneralizedTime willBeRevokedAt;
    private ASN1GeneralizedTime badSinceDate;
    private Extensions crlDetails;

    private RevAnnContent(ASN1Sequence seq) {
        this.status = PKIStatus.getInstance(seq.getObjectAt(0));
        this.certId = CertId.getInstance(seq.getObjectAt(1));
        this.willBeRevokedAt = ASN1GeneralizedTime.getInstance(seq.getObjectAt(2));
        this.badSinceDate = ASN1GeneralizedTime.getInstance(seq.getObjectAt(3));
        if (seq.size() > 4) {
            this.crlDetails = Extensions.getInstance(seq.getObjectAt(4));
        }
    }

    public static RevAnnContent getInstance(Object o) {
        if (o instanceof RevAnnContent) {
            return (RevAnnContent) o;
        }
        if (o != null) {
            return new RevAnnContent(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public PKIStatus getStatus() {
        return this.status;
    }

    public CertId getCertId() {
        return this.certId;
    }

    public ASN1GeneralizedTime getWillBeRevokedAt() {
        return this.willBeRevokedAt;
    }

    public ASN1GeneralizedTime getBadSinceDate() {
        return this.badSinceDate;
    }

    public Extensions getCrlDetails() {
        return this.crlDetails;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.status);
        v.add(this.certId);
        v.add(this.willBeRevokedAt);
        v.add(this.badSinceDate);
        if (this.crlDetails != null) {
            v.add(this.crlDetails);
        }
        return new DERSequence(v);
    }
}
