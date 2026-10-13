package org.spongycastle.asn1.cmp;

import org.spongycastle.asn1.ASN1Choice;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.crmf.EncryptedValue;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cmp/CertOrEncCert.class */
public class CertOrEncCert extends ASN1Object implements ASN1Choice {
    private CMPCertificate certificate;
    private EncryptedValue encryptedCert;

    private CertOrEncCert(ASN1TaggedObject tagged) {
        if (tagged.getTagNo() == 0) {
            this.certificate = CMPCertificate.getInstance(tagged.getObject());
        } else {
            if (tagged.getTagNo() == 1) {
                this.encryptedCert = EncryptedValue.getInstance(tagged.getObject());
                return;
            }
            throw new IllegalArgumentException("unknown tag: " + tagged.getTagNo());
        }
    }

    public static CertOrEncCert getInstance(Object o) {
        if (o instanceof CertOrEncCert) {
            return (CertOrEncCert) o;
        }
        if (o instanceof ASN1TaggedObject) {
            return new CertOrEncCert((ASN1TaggedObject) o);
        }
        return null;
    }

    public CertOrEncCert(CMPCertificate certificate) {
        if (certificate == null) {
            throw new IllegalArgumentException("'certificate' cannot be null");
        }
        this.certificate = certificate;
    }

    public CertOrEncCert(EncryptedValue encryptedCert) {
        if (encryptedCert == null) {
            throw new IllegalArgumentException("'encryptedCert' cannot be null");
        }
        this.encryptedCert = encryptedCert;
    }

    public CMPCertificate getCertificate() {
        return this.certificate;
    }

    public EncryptedValue getEncryptedCert() {
        return this.encryptedCert;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        if (this.certificate != null) {
            return new DERTaggedObject(true, 0, this.certificate);
        }
        return new DERTaggedObject(true, 1, this.encryptedCert);
    }
}
