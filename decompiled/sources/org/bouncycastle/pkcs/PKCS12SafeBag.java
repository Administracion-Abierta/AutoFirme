package org.bouncycastle.pkcs;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.CRLBag;
import org.bouncycastle.asn1.pkcs.CertBag;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.SafeBag;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkcs/PKCS12SafeBag.class */
public class PKCS12SafeBag {
    public static final ASN1ObjectIdentifier friendlyNameAttribute = PKCSObjectIdentifiers.pkcs_9_at_friendlyName;
    public static final ASN1ObjectIdentifier localKeyIdAttribute = PKCSObjectIdentifiers.pkcs_9_at_localKeyId;
    private SafeBag safeBag;

    public PKCS12SafeBag(SafeBag safeBag) {
        this.safeBag = safeBag;
    }

    public SafeBag toASN1Structure() {
        return this.safeBag;
    }

    public ASN1ObjectIdentifier getType() {
        return this.safeBag.getBagId();
    }

    public Attribute[] getAttributes() {
        ASN1Set bagAttributes = this.safeBag.getBagAttributes();
        if (bagAttributes == null) {
            return null;
        }
        Attribute[] attributeArr = new Attribute[bagAttributes.size()];
        for (int i = 0; i != bagAttributes.size(); i++) {
            attributeArr[i] = Attribute.getInstance(bagAttributes.getObjectAt(i));
        }
        return attributeArr;
    }

    public Object getBagValue() {
        if (getType().equals((ASN1Primitive) PKCSObjectIdentifiers.pkcs8ShroudedKeyBag)) {
            return new PKCS8EncryptedPrivateKeyInfo(EncryptedPrivateKeyInfo.getInstance(this.safeBag.getBagValue()));
        }
        if (getType().equals((ASN1Primitive) PKCSObjectIdentifiers.certBag)) {
            return new X509CertificateHolder(Certificate.getInstance(ASN1OctetString.getInstance(CertBag.getInstance(this.safeBag.getBagValue()).getCertValue()).getOctets()));
        }
        if (getType().equals((ASN1Primitive) PKCSObjectIdentifiers.keyBag)) {
            return PrivateKeyInfo.getInstance(this.safeBag.getBagValue());
        }
        return getType().equals((ASN1Primitive) PKCSObjectIdentifiers.crlBag) ? new X509CRLHolder(CertificateList.getInstance(ASN1OctetString.getInstance(CRLBag.getInstance(this.safeBag.getBagValue()).getCrlValue()).getOctets())) : this.safeBag.getBagValue();
    }
}
