package org.spongycastle.cert;

import java.math.BigInteger;
import java.util.Date;
import java.util.Locale;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1GeneralizedTime;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.x509.AttCertIssuer;
import org.spongycastle.asn1.x509.Attribute;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.asn1.x509.ExtensionsGenerator;
import org.spongycastle.asn1.x509.V2AttributeCertificateInfoGenerator;
import org.spongycastle.operator.ContentSigner;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/X509v2AttributeCertificateBuilder.class */
public class X509v2AttributeCertificateBuilder {
    private V2AttributeCertificateInfoGenerator acInfoGen = new V2AttributeCertificateInfoGenerator();
    private ExtensionsGenerator extGenerator = new ExtensionsGenerator();

    public X509v2AttributeCertificateBuilder(AttributeCertificateHolder holder, AttributeCertificateIssuer issuer, BigInteger serialNumber, Date notBefore, Date notAfter) {
        this.acInfoGen.setHolder(holder.holder);
        this.acInfoGen.setIssuer(AttCertIssuer.getInstance(issuer.form));
        this.acInfoGen.setSerialNumber(new ASN1Integer(serialNumber));
        this.acInfoGen.setStartDate(new ASN1GeneralizedTime(notBefore));
        this.acInfoGen.setEndDate(new ASN1GeneralizedTime(notAfter));
    }

    public X509v2AttributeCertificateBuilder(AttributeCertificateHolder holder, AttributeCertificateIssuer issuer, BigInteger serialNumber, Date notBefore, Date notAfter, Locale dateLocale) {
        this.acInfoGen.setHolder(holder.holder);
        this.acInfoGen.setIssuer(AttCertIssuer.getInstance(issuer.form));
        this.acInfoGen.setSerialNumber(new ASN1Integer(serialNumber));
        this.acInfoGen.setStartDate(new ASN1GeneralizedTime(notBefore, dateLocale));
        this.acInfoGen.setEndDate(new ASN1GeneralizedTime(notAfter, dateLocale));
    }

    public X509v2AttributeCertificateBuilder addAttribute(ASN1ObjectIdentifier attrType, ASN1Encodable attrValue) {
        this.acInfoGen.addAttribute(new Attribute(attrType, new DERSet(attrValue)));
        return this;
    }

    public X509v2AttributeCertificateBuilder addAttribute(ASN1ObjectIdentifier attrType, ASN1Encodable[] attrValues) {
        this.acInfoGen.addAttribute(new Attribute(attrType, new DERSet(attrValues)));
        return this;
    }

    public void setIssuerUniqueId(boolean[] iui) {
        this.acInfoGen.setIssuerUniqueID(CertUtils.booleanToBitString(iui));
    }

    public X509v2AttributeCertificateBuilder addExtension(ASN1ObjectIdentifier oid, boolean isCritical, ASN1Encodable value) throws CertIOException {
        CertUtils.addExtension(this.extGenerator, oid, isCritical, value);
        return this;
    }

    public X509v2AttributeCertificateBuilder addExtension(ASN1ObjectIdentifier oid, boolean isCritical, byte[] encodedValue) throws CertIOException {
        this.extGenerator.addExtension(oid, isCritical, encodedValue);
        return this;
    }

    public X509v2AttributeCertificateBuilder addExtension(Extension extension) throws CertIOException {
        this.extGenerator.addExtension(extension);
        return this;
    }

    public X509AttributeCertificateHolder build(ContentSigner signer) {
        this.acInfoGen.setSignature(signer.getAlgorithmIdentifier());
        if (!this.extGenerator.isEmpty()) {
            this.acInfoGen.setExtensions(this.extGenerator.generate());
        }
        return CertUtils.generateFullAttrCert(signer, this.acInfoGen.generateAttributeCertificateInfo());
    }
}
