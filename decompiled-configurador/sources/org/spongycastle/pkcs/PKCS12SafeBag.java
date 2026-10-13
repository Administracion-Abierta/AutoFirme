package org.spongycastle.pkcs;

import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.pkcs.Attribute;
import org.spongycastle.asn1.pkcs.CRLBag;
import org.spongycastle.asn1.pkcs.CertBag;
import org.spongycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.pkcs.SafeBag;
import org.spongycastle.asn1.x509.Certificate;
import org.spongycastle.asn1.x509.CertificateList;
import org.spongycastle.cert.X509CRLHolder;
import org.spongycastle.cert.X509CertificateHolder;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pkcs/PKCS12SafeBag.class */
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
        ASN1Set attrs = this.safeBag.getBagAttributes();
        if (attrs == null) {
            return null;
        }
        Attribute[] attributes = new Attribute[attrs.size()];
        for (int i = 0; i != attrs.size(); i++) {
            attributes[i] = Attribute.getInstance(attrs.getObjectAt(i));
        }
        return attributes;
    }

    public Object getBagValue() {
        if (getType().equals(PKCSObjectIdentifiers.pkcs8ShroudedKeyBag)) {
            return new PKCS8EncryptedPrivateKeyInfo(EncryptedPrivateKeyInfo.getInstance(this.safeBag.getBagValue()));
        }
        if (getType().equals(PKCSObjectIdentifiers.certBag)) {
            CertBag certBag = CertBag.getInstance(this.safeBag.getBagValue());
            return new X509CertificateHolder(Certificate.getInstance(ASN1OctetString.getInstance(certBag.getCertValue()).getOctets()));
        }
        if (getType().equals(PKCSObjectIdentifiers.keyBag)) {
            return PrivateKeyInfo.getInstance(this.safeBag.getBagValue());
        }
        if (getType().equals(PKCSObjectIdentifiers.crlBag)) {
            CRLBag crlBag = CRLBag.getInstance(this.safeBag.getBagValue());
            return new X509CRLHolder(CertificateList.getInstance(ASN1OctetString.getInstance(crlBag.getCrlValue()).getOctets()));
        }
        return this.safeBag.getBagValue();
    }
}
