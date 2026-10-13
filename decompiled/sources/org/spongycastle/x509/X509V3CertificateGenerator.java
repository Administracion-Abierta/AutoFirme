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
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Iterator;
import javax.security.auth.x500.X500Principal;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.asn1.x509.TBSCertificate;
import org.spongycastle.asn1.x509.Time;
import org.spongycastle.asn1.x509.V3TBSCertificateGenerator;
import org.spongycastle.asn1.x509.X509ExtensionsGenerator;
import org.spongycastle.asn1.x509.X509Name;
import org.spongycastle.jcajce.provider.asymmetric.x509.CertificateFactory;
import org.spongycastle.jcajce.util.BCJcaJceHelper;
import org.spongycastle.jcajce.util.JcaJceHelper;
import org.spongycastle.jce.X509Principal;
import org.spongycastle.x509.extension.X509ExtensionUtil;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/x509/X509V3CertificateGenerator.class */
public class X509V3CertificateGenerator {
    private ASN1ObjectIdentifier sigOID;
    private AlgorithmIdentifier sigAlgId;
    private String signatureAlgorithm;
    private final JcaJceHelper bcHelper = new BCJcaJceHelper();
    private final CertificateFactory certificateFactory = new CertificateFactory();
    private V3TBSCertificateGenerator tbsGen = new V3TBSCertificateGenerator();
    private X509ExtensionsGenerator extGenerator = new X509ExtensionsGenerator();

    public void reset() {
        this.tbsGen = new V3TBSCertificateGenerator();
        this.extGenerator.reset();
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

    public void setPublicKey(PublicKey key) throws IllegalArgumentException {
        try {
            this.tbsGen.setSubjectPublicKeyInfo(SubjectPublicKeyInfo.getInstance(new ASN1InputStream(key.getEncoded()).readObject()));
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
            throw new IllegalArgumentException("Unknown signature type requested: " + signatureAlgorithm);
        }
    }

    public void setSubjectUniqueID(boolean[] uniqueID) {
        this.tbsGen.setSubjectUniqueID(booleanToBitString(uniqueID));
    }

    public void setIssuerUniqueID(boolean[] uniqueID) {
        this.tbsGen.setIssuerUniqueID(booleanToBitString(uniqueID));
    }

    private DERBitString booleanToBitString(boolean[] id) {
        byte[] bytes = new byte[(id.length + 7) / 8];
        for (int i = 0; i != id.length; i++) {
            int i2 = i / 8;
            bytes[i2] = (byte) (bytes[i2] | (id[i] ? 1 << (7 - (i % 8)) : 0));
        }
        int pad = id.length % 8;
        if (pad == 0) {
            return new DERBitString(bytes);
        }
        return new DERBitString(bytes, 8 - pad);
    }

    public void addExtension(String oid, boolean critical, ASN1Encodable value) {
        addExtension(new ASN1ObjectIdentifier(oid), critical, value);
    }

    public void addExtension(ASN1ObjectIdentifier oid, boolean critical, ASN1Encodable value) {
        this.extGenerator.addExtension(new ASN1ObjectIdentifier(oid.getId()), critical, value);
    }

    public void addExtension(String oid, boolean critical, byte[] value) {
        addExtension(new ASN1ObjectIdentifier(oid), critical, value);
    }

    public void addExtension(ASN1ObjectIdentifier oid, boolean critical, byte[] value) {
        this.extGenerator.addExtension(new ASN1ObjectIdentifier(oid.getId()), critical, value);
    }

    public void copyAndAddExtension(String oid, boolean critical, X509Certificate cert) throws CertificateParsingException {
        byte[] extValue = cert.getExtensionValue(oid);
        if (extValue == null) {
            throw new CertificateParsingException("extension " + oid + " not present");
        }
        try {
            ASN1Encodable value = X509ExtensionUtil.fromExtensionValue(extValue);
            addExtension(oid, critical, value);
        } catch (IOException e) {
            throw new CertificateParsingException(e.toString());
        }
    }

    public void copyAndAddExtension(ASN1ObjectIdentifier oid, boolean critical, X509Certificate cert) throws CertificateParsingException {
        copyAndAddExtension(oid.getId(), critical, cert);
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
        TBSCertificate tbsCert = generateTbsCert();
        try {
            byte[] signature = X509Util.calculateSignature(this.sigOID, this.signatureAlgorithm, key, random, tbsCert);
            try {
                return generateJcaObject(tbsCert, signature);
            } catch (Exception e) {
                throw new ExtCertificateEncodingException("exception producing certificate object", e);
            }
        } catch (IOException e2) {
            throw new ExtCertificateEncodingException("exception encoding TBS cert", e2);
        }
    }

    public X509Certificate generate(PrivateKey key, String provider) throws IllegalStateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchProviderException, CertificateEncodingException {
        return generate(key, provider, null);
    }

    public X509Certificate generate(PrivateKey key, String provider, SecureRandom random) throws IllegalStateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchProviderException, CertificateEncodingException {
        TBSCertificate tbsCert = generateTbsCert();
        try {
            byte[] signature = X509Util.calculateSignature(this.sigOID, this.signatureAlgorithm, provider, key, random, tbsCert);
            try {
                return generateJcaObject(tbsCert, signature);
            } catch (Exception e) {
                throw new ExtCertificateEncodingException("exception producing certificate object", e);
            }
        } catch (IOException e2) {
            throw new ExtCertificateEncodingException("exception encoding TBS cert", e2);
        }
    }

    private TBSCertificate generateTbsCert() {
        if (!this.extGenerator.isEmpty()) {
            this.tbsGen.setExtensions(this.extGenerator.generate());
        }
        return this.tbsGen.generateTBSCertificate();
    }

    private X509Certificate generateJcaObject(TBSCertificate tbsCert, byte[] signature) throws Exception {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(tbsCert);
        v.add(this.sigAlgId);
        v.add(new DERBitString(signature));
        return (X509Certificate) this.certificateFactory.engineGenerateCertificate(new ByteArrayInputStream(new DERSequence(v).getEncoded("DER")));
    }

    public Iterator getSignatureAlgNames() {
        return X509Util.getAlgNames();
    }
}
