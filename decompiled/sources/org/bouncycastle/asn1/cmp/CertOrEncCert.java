package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.crmf.EncryptedKey;
import org.bouncycastle.asn1.crmf.EncryptedValue;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cmp/CertOrEncCert.class */
public class CertOrEncCert extends ASN1Object implements ASN1Choice {
    private CMPCertificate certificate;
    private EncryptedKey encryptedCert;

    private CertOrEncCert(ASN1TaggedObject aSN1TaggedObject) {
        if (aSN1TaggedObject.hasContextTag(0)) {
            this.certificate = CMPCertificate.getInstance(aSN1TaggedObject.getExplicitBaseObject());
        } else {
            if (!aSN1TaggedObject.hasContextTag(1)) {
                throw new IllegalArgumentException("unknown tag: " + ASN1Util.getTagText(aSN1TaggedObject));
            }
            this.encryptedCert = EncryptedKey.getInstance(aSN1TaggedObject.getExplicitBaseObject());
        }
    }

    public CertOrEncCert(CMPCertificate cMPCertificate) {
        if (cMPCertificate == null) {
            throw new IllegalArgumentException("'certificate' cannot be null");
        }
        this.certificate = cMPCertificate;
    }

    public CertOrEncCert(EncryptedValue encryptedValue) {
        if (encryptedValue == null) {
            throw new IllegalArgumentException("'encryptedCert' cannot be null");
        }
        this.encryptedCert = new EncryptedKey(encryptedValue);
    }

    public CertOrEncCert(EncryptedKey encryptedKey) {
        if (encryptedKey == null) {
            throw new IllegalArgumentException("'encryptedCert' cannot be null");
        }
        this.encryptedCert = encryptedKey;
    }

    public static CertOrEncCert getInstance(Object obj) {
        if (obj instanceof CertOrEncCert) {
            return (CertOrEncCert) obj;
        }
        if (obj instanceof ASN1TaggedObject) {
            return new CertOrEncCert(ASN1TaggedObject.getInstance(obj, 128));
        }
        return null;
    }

    public boolean hasEncryptedCertificate() {
        return this.encryptedCert != null;
    }

    public CMPCertificate getCertificate() {
        return this.certificate;
    }

    public EncryptedKey getEncryptedCert() {
        return this.encryptedCert;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.certificate != null ? new DERTaggedObject(true, 0, (ASN1Encodable) this.certificate) : new DERTaggedObject(true, 1, (ASN1Encodable) this.encryptedCert);
    }
}
