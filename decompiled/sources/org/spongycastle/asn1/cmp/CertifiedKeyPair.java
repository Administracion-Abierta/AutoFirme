package org.spongycastle.asn1.cmp;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.crmf.EncryptedValue;
import org.spongycastle.asn1.crmf.PKIPublicationInfo;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cmp/CertifiedKeyPair.class */
public class CertifiedKeyPair extends ASN1Object {
    private CertOrEncCert certOrEncCert;
    private EncryptedValue privateKey;
    private PKIPublicationInfo publicationInfo;

    private CertifiedKeyPair(ASN1Sequence seq) {
        this.certOrEncCert = CertOrEncCert.getInstance(seq.getObjectAt(0));
        if (seq.size() >= 2) {
            if (seq.size() == 2) {
                ASN1TaggedObject tagged = ASN1TaggedObject.getInstance(seq.getObjectAt(1));
                if (tagged.getTagNo() == 0) {
                    this.privateKey = EncryptedValue.getInstance(tagged.getObject());
                    return;
                } else {
                    this.publicationInfo = PKIPublicationInfo.getInstance(tagged.getObject());
                    return;
                }
            }
            this.privateKey = EncryptedValue.getInstance(ASN1TaggedObject.getInstance(seq.getObjectAt(1)));
            this.publicationInfo = PKIPublicationInfo.getInstance(ASN1TaggedObject.getInstance(seq.getObjectAt(2)));
        }
    }

    public static CertifiedKeyPair getInstance(Object o) {
        if (o instanceof CertifiedKeyPair) {
            return (CertifiedKeyPair) o;
        }
        if (o != null) {
            return new CertifiedKeyPair(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public CertifiedKeyPair(CertOrEncCert certOrEncCert) {
        this(certOrEncCert, null, null);
    }

    public CertifiedKeyPair(CertOrEncCert certOrEncCert, EncryptedValue privateKey, PKIPublicationInfo publicationInfo) {
        if (certOrEncCert == null) {
            throw new IllegalArgumentException("'certOrEncCert' cannot be null");
        }
        this.certOrEncCert = certOrEncCert;
        this.privateKey = privateKey;
        this.publicationInfo = publicationInfo;
    }

    public CertOrEncCert getCertOrEncCert() {
        return this.certOrEncCert;
    }

    public EncryptedValue getPrivateKey() {
        return this.privateKey;
    }

    public PKIPublicationInfo getPublicationInfo() {
        return this.publicationInfo;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.certOrEncCert);
        if (this.privateKey != null) {
            v.add(new DERTaggedObject(true, 0, this.privateKey));
        }
        if (this.publicationInfo != null) {
            v.add(new DERTaggedObject(true, 1, this.publicationInfo));
        }
        return new DERSequence(v);
    }
}
