package org.spongycastle.asn1.cmp;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.crmf.CertTemplate;
import org.spongycastle.asn1.x509.Extensions;
import org.spongycastle.asn1.x509.X509Extensions;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cmp/RevDetails.class */
public class RevDetails extends ASN1Object {
    private CertTemplate certDetails;
    private Extensions crlEntryDetails;

    private RevDetails(ASN1Sequence seq) {
        this.certDetails = CertTemplate.getInstance(seq.getObjectAt(0));
        if (seq.size() > 1) {
            this.crlEntryDetails = Extensions.getInstance(seq.getObjectAt(1));
        }
    }

    public static RevDetails getInstance(Object o) {
        if (o instanceof RevDetails) {
            return (RevDetails) o;
        }
        if (o != null) {
            return new RevDetails(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public RevDetails(CertTemplate certDetails) {
        this.certDetails = certDetails;
    }

    public RevDetails(CertTemplate certDetails, X509Extensions crlEntryDetails) {
        this.certDetails = certDetails;
        this.crlEntryDetails = Extensions.getInstance(crlEntryDetails.toASN1Primitive());
    }

    public RevDetails(CertTemplate certDetails, Extensions crlEntryDetails) {
        this.certDetails = certDetails;
        this.crlEntryDetails = crlEntryDetails;
    }

    public CertTemplate getCertDetails() {
        return this.certDetails;
    }

    public Extensions getCrlEntryDetails() {
        return this.crlEntryDetails;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.certDetails);
        if (this.crlEntryDetails != null) {
            v.add(this.crlEntryDetails);
        }
        return new DERSequence(v);
    }
}
