package org.spongycastle.cert;

import java.math.BigInteger;
import java.util.Date;
import java.util.Locale;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.asn1.x509.Time;
import org.spongycastle.asn1.x509.V1TBSCertificateGenerator;
import org.spongycastle.operator.ContentSigner;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/X509v1CertificateBuilder.class */
public class X509v1CertificateBuilder {
    private V1TBSCertificateGenerator tbsGen;

    public X509v1CertificateBuilder(X500Name issuer, BigInteger serial, Date notBefore, Date notAfter, X500Name subject, SubjectPublicKeyInfo publicKeyInfo) {
        this(issuer, serial, new Time(notBefore), new Time(notAfter), subject, publicKeyInfo);
    }

    public X509v1CertificateBuilder(X500Name issuer, BigInteger serial, Date notBefore, Date notAfter, Locale dateLocale, X500Name subject, SubjectPublicKeyInfo publicKeyInfo) {
        this(issuer, serial, new Time(notBefore, dateLocale), new Time(notAfter, dateLocale), subject, publicKeyInfo);
    }

    public X509v1CertificateBuilder(X500Name issuer, BigInteger serial, Time notBefore, Time notAfter, X500Name subject, SubjectPublicKeyInfo publicKeyInfo) {
        if (issuer == null) {
            throw new IllegalArgumentException("issuer must not be null");
        }
        if (publicKeyInfo == null) {
            throw new IllegalArgumentException("publicKeyInfo must not be null");
        }
        this.tbsGen = new V1TBSCertificateGenerator();
        this.tbsGen.setSerialNumber(new ASN1Integer(serial));
        this.tbsGen.setIssuer(issuer);
        this.tbsGen.setStartDate(notBefore);
        this.tbsGen.setEndDate(notAfter);
        this.tbsGen.setSubject(subject);
        this.tbsGen.setSubjectPublicKeyInfo(publicKeyInfo);
    }

    public X509CertificateHolder build(ContentSigner signer) {
        this.tbsGen.setSignature(signer.getAlgorithmIdentifier());
        return CertUtils.generateFullCert(signer, this.tbsGen.generateTBSCertificate());
    }
}
