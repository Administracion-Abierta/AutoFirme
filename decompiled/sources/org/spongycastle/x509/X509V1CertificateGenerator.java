package org.spongycastle.x509;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Iterator;
import javax.security.auth.x500.X500Principal;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.asn1.x509.TBSCertificate;
import org.spongycastle.asn1.x509.Time;
import org.spongycastle.asn1.x509.V1TBSCertificateGenerator;
import org.spongycastle.asn1.x509.X509Name;
import org.spongycastle.jcajce.provider.asymmetric.x509.CertificateFactory;
import org.spongycastle.jcajce.util.BCJcaJceHelper;
import org.spongycastle.jcajce.util.JcaJceHelper;
import org.spongycastle.jce.X509Principal;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/x509/X509V1CertificateGenerator.class */
public class X509V1CertificateGenerator {
    private final JcaJceHelper bcHelper = new BCJcaJceHelper();
    private final CertificateFactory certificateFactory = new CertificateFactory();
    private V1TBSCertificateGenerator tbsGen = new V1TBSCertificateGenerator();
    private ASN1ObjectIdentifier sigOID;
    private AlgorithmIdentifier sigAlgId;
    private String signatureAlgorithm;

    public void reset() {
        this.tbsGen = new V1TBSCertificateGenerator();
    }

    public void setSerialNumber(BigInteger serialNumber) {
        if (serialNumber.compareTo(BigInteger.ZERO) <= 0) {
            throw new IllegalArgumentException("serial number must be a positive integer");
        }
        this.tbsGen.setSerialNumber(new ASN1Integer(serialNumber));
    }

    public void setIssuerDN(X500Principal issuer) {
        try {
            this.tbsGen.setIssuer(new X509Principal(issuer.getEncoded()));
        } catch (IOException e) {
            throw new IllegalArgumentException("can't process principal: " + e);
        }
    }

    public void setIssuerDN(X509Name issuer) {
        this.tbsGen.setIssuer(issuer);
    }

    public void setNotBefore(Date date) {
        this.tbsGen.setStartDate(new Time(date));
    }

    public void setNotAfter(Date date) {
        this.tbsGen.setEndDate(new Time(date));
    }

    public void setSubjectDN(X500Principal subject) {
        try {
            this.tbsGen.setSubject(new X509Principal(subject.getEncoded()));
        } catch (IOException e) {
            throw new IllegalArgumentException("can't process principal: " + e);
        }
    }

    public void setSubjectDN(X509Name subject) {
        this.tbsGen.setSubject(subject);
    }

    public void setPublicKey(PublicKey key) {
        try {
            this.tbsGen.setSubjectPublicKeyInfo(SubjectPublicKeyInfo.getInstance(key.getEncoded()));
        } catch (Exception e) {
            throw new IllegalArgumentException("unable to process key - " + e.toString());
        }
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
        try {
            this.sigOID = X509Util.getAlgorithmOID(signatureAlgorithm);
            this.sigAlgId = X509Util.getSigAlgID(this.sigOID, signatureAlgorithm);
            this.tbsGen.setSignature(this.sigAlgId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown signature type requested");
        }
    }

    public X509Certificate generateX509Certificate(PrivateKey key) throws SignatureException, SecurityException, InvalidKeyException {
        try {
            return generateX509Certificate(key, "SC", null);
        } catch (NoSuchProviderException e) {
            throw new SecurityException("BC provider not installed!");
        }
    }

    public X509Certificate generateX509Certificate(PrivateKey key, SecureRandom random) throws SignatureException, SecurityException, InvalidKeyException {
        try {
            return generateX509Certificate(key, "SC", random);
        } catch (NoSuchProviderException e) {
            throw new SecurityException("BC provider not installed!");
        }
    }

    public X509Certificate generateX509Certificate(PrivateKey key, String provider) throws SignatureException, SecurityException, InvalidKeyException, NoSuchProviderException {
        return generateX509Certificate(key, provider, null);
    }

    public X509Certificate generateX509Certificate(PrivateKey key, String provider, SecureRandom random) throws SignatureException, InvalidKeyException, SecurityException, NoSuchProviderException {
        try {
            return generate(key, provider, random);
        } catch (InvalidKeyException e) {
            throw e;
        } catch (NoSuchProviderException e2) {
            throw e2;
        } catch (SignatureException e3) {
            throw e3;
        } catch (GeneralSecurityException e4) {
            throw new SecurityException("exception: " + e4);
        }
    }

    public X509Certificate generate(PrivateKey key) throws IllegalStateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, CertificateEncodingException {
        return generate(key, (SecureRandom) null);
    }

    public X509Certificate generate(PrivateKey key, SecureRandom random) throws IllegalStateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, CertificateEncodingException {
        TBSCertificate tbsCert = this.tbsGen.generateTBSCertificate();
        try {
            byte[] signature = X509Util.calculateSignature(this.sigOID, this.signatureAlgorithm, key, random, tbsCert);
            return generateJcaObject(tbsCert, signature);
        } catch (IOException e) {
            throw new ExtCertificateEncodingException("exception encoding TBS cert", e);
        }
    }

    public X509Certificate generate(PrivateKey key, String provider) throws IllegalStateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchProviderException, CertificateEncodingException {
        return generate(key, provider, null);
    }

    public X509Certificate generate(PrivateKey key, String provider, SecureRandom random) throws IllegalStateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchProviderException, CertificateEncodingException {
        TBSCertificate tbsCert = this.tbsGen.generateTBSCertificate();
        try {
            byte[] signature = X509Util.calculateSignature(this.sigOID, this.signatureAlgorithm, provider, key, random, tbsCert);
            return generateJcaObject(tbsCert, signature);
        } catch (IOException e) {
            throw new ExtCertificateEncodingException("exception encoding TBS cert", e);
        }
    }

    private X509Certificate generateJcaObject(TBSCertificate tbsCert, byte[] signature) throws CertificateEncodingException {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(tbsCert);
        v.add(this.sigAlgId);
        v.add(new DERBitString(signature));
        try {
            return (X509Certificate) this.certificateFactory.engineGenerateCertificate(new ByteArrayInputStream(new DERSequence(v).getEncoded("DER")));
        } catch (Exception e) {
            throw new ExtCertificateEncodingException("exception producing certificate object", e);
        }
    }

    public Iterator getSignatureAlgNames() {
        return X509Util.getAlgNames();
    }
}
