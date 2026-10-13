package org.spongycastle.cert;

import java.math.BigInteger;
import java.util.Date;
import java.util.Locale;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.Certificate;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.asn1.x509.ExtensionsGenerator;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.asn1.x509.Time;
import org.spongycastle.asn1.x509.V3TBSCertificateGenerator;
import org.spongycastle.operator.ContentSigner;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/X509v3CertificateBuilder.class */
public class X509v3CertificateBuilder {
    private V3TBSCertificateGenerator tbsGen;
    private ExtensionsGenerator extGenerator;

    public X509v3CertificateBuilder(X500Name issuer, BigInteger serial, Date notBefore, Date notAfter, X500Name subject, SubjectPublicKeyInfo publicKeyInfo) {
        this(issuer, serial, new Time(notBefore), new Time(notAfter), subject, publicKeyInfo);
    }

    public X509v3CertificateBuilder(X500Name issuer, BigInteger serial, Date notBefore, Date notAfter, Locale dateLocale, X500Name subject, SubjectPublicKeyInfo publicKeyInfo) {
        this(issuer, serial, new Time(notBefore, dateLocale), new Time(notAfter, dateLocale), subject, publicKeyInfo);
    }

    public X509v3CertificateBuilder(X500Name issuer, BigInteger serial, Time notBefore, Time notAfter, X500Name subject, SubjectPublicKeyInfo publicKeyInfo) {
        this.tbsGen = new V3TBSCertificateGenerator();
        this.tbsGen.setSerialNumber(new ASN1Integer(serial));
        this.tbsGen.setIssuer(issuer);
        this.tbsGen.setStartDate(notBefore);
        this.tbsGen.setEndDate(notAfter);
        this.tbsGen.setSubject(subject);
        this.tbsGen.setSubjectPublicKeyInfo(publicKeyInfo);
        this.extGenerator = new ExtensionsGenerator();
    }

    public X509v3CertificateBuilder setSubjectUniqueID(boolean[] uniqueID) {
        this.tbsGen.setSubjectUniqueID(CertUtils.booleanToBitString(uniqueID));
        return this;
    }

    public X509v3CertificateBuilder setIssuerUniqueID(boolean[] uniqueID) {
        this.tbsGen.setIssuerUniqueID(CertUtils.booleanToBitString(uniqueID));
        return this;
    }

    public X509v3CertificateBuilder addExtension(ASN1ObjectIdentifier oid, boolean isCritical, ASN1Encodable value) throws CertIOException {
        CertUtils.addExtension(this.extGenerator, oid, isCritical, value);
        return this;
    }

    public X509v3CertificateBuilder addExtension(Extension extension) throws CertIOException {
        this.extGenerator.addExtension(extension);
        return this;
    }

    public X509v3CertificateBuilder addExtension(ASN1ObjectIdentifier oid, boolean isCritical, byte[] encodedValue) throws CertIOException {
        this.extGenerator.addExtension(oid, isCritical, encodedValue);
        return this;
    }

    public X509v3CertificateBuilder copyAndAddExtension(ASN1ObjectIdentifier oid, boolean isCritical, X509CertificateHolder certHolder) {
        Certificate cert = certHolder.toASN1Structure();
        Extension extension = cert.getTBSCertificate().getExtensions().getExtension(oid);
        if (extension == null) {
            throw new NullPointerException("extension " + oid + " not present");
        }
        this.extGenerator.addExtension(oid, isCritical, extension.getExtnValue().getOctets());
        return this;
    }

    public X509CertificateHolder build(ContentSigner signer) {
        this.tbsGen.setSignature(signer.getAlgorithmIdentifier());
        if (!this.extGenerator.isEmpty()) {
            this.tbsGen.setExtensions(this.extGenerator.generate());
        }
        return CertUtils.generateFullCert(signer, this.tbsGen.generateTBSCertificate());
    }
}
