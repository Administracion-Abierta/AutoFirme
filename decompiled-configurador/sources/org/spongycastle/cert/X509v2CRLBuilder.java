package org.spongycastle.cert;

import java.math.BigInteger;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1GeneralizedTime;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.asn1.x509.Extensions;
import org.spongycastle.asn1.x509.ExtensionsGenerator;
import org.spongycastle.asn1.x509.TBSCertList;
import org.spongycastle.asn1.x509.Time;
import org.spongycastle.asn1.x509.V2TBSCertListGenerator;
import org.spongycastle.operator.ContentSigner;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/X509v2CRLBuilder.class */
public class X509v2CRLBuilder {
    private V2TBSCertListGenerator tbsGen = new V2TBSCertListGenerator();
    private ExtensionsGenerator extGenerator = new ExtensionsGenerator();

    public X509v2CRLBuilder(X500Name issuer, Date thisUpdate) {
        this.tbsGen.setIssuer(issuer);
        this.tbsGen.setThisUpdate(new Time(thisUpdate));
    }

    public X509v2CRLBuilder(X500Name issuer, Date thisUpdate, Locale dateLocale) {
        this.tbsGen.setIssuer(issuer);
        this.tbsGen.setThisUpdate(new Time(thisUpdate, dateLocale));
    }

    public X509v2CRLBuilder(X500Name issuer, Time thisUpdate) {
        this.tbsGen.setIssuer(issuer);
        this.tbsGen.setThisUpdate(thisUpdate);
    }

    public X509v2CRLBuilder setNextUpdate(Date date) {
        return setNextUpdate(new Time(date));
    }

    public X509v2CRLBuilder setNextUpdate(Date date, Locale dateLocale) {
        return setNextUpdate(new Time(date, dateLocale));
    }

    public X509v2CRLBuilder setNextUpdate(Time date) {
        this.tbsGen.setNextUpdate(date);
        return this;
    }

    public X509v2CRLBuilder addCRLEntry(BigInteger userCertificateSerial, Date revocationDate, int reason) {
        this.tbsGen.addCRLEntry(new ASN1Integer(userCertificateSerial), new Time(revocationDate), reason);
        return this;
    }

    public X509v2CRLBuilder addCRLEntry(BigInteger userCertificateSerial, Date revocationDate, int reason, Date invalidityDate) {
        this.tbsGen.addCRLEntry(new ASN1Integer(userCertificateSerial), new Time(revocationDate), reason, new ASN1GeneralizedTime(invalidityDate));
        return this;
    }

    public X509v2CRLBuilder addCRLEntry(BigInteger userCertificateSerial, Date revocationDate, Extensions extensions) {
        this.tbsGen.addCRLEntry(new ASN1Integer(userCertificateSerial), new Time(revocationDate), extensions);
        return this;
    }

    public X509v2CRLBuilder addCRL(X509CRLHolder other) {
        TBSCertList revocations = other.toASN1Structure().getTBSCertList();
        if (revocations != null) {
            Enumeration en = revocations.getRevokedCertificateEnumeration();
            while (en.hasMoreElements()) {
                this.tbsGen.addCRLEntry(ASN1Sequence.getInstance(((ASN1Encodable) en.nextElement()).toASN1Primitive()));
            }
        }
        return this;
    }

    public X509v2CRLBuilder addExtension(ASN1ObjectIdentifier oid, boolean isCritical, ASN1Encodable value) throws CertIOException {
        CertUtils.addExtension(this.extGenerator, oid, isCritical, value);
        return this;
    }

    public X509v2CRLBuilder addExtension(ASN1ObjectIdentifier oid, boolean isCritical, byte[] encodedValue) throws CertIOException {
        this.extGenerator.addExtension(oid, isCritical, encodedValue);
        return this;
    }

    public X509v2CRLBuilder addExtension(Extension extension) throws CertIOException {
        this.extGenerator.addExtension(extension);
        return this;
    }

    public X509CRLHolder build(ContentSigner signer) {
        this.tbsGen.setSignature(signer.getAlgorithmIdentifier());
        if (!this.extGenerator.isEmpty()) {
            this.tbsGen.setExtensions(this.extGenerator.generate());
        }
        return CertUtils.generateFullCRL(signer, this.tbsGen.generateTBSCertList());
    }
}
