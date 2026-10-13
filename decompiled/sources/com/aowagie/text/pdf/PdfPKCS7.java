package com.aowagie.text.pdf;

import com.aowagie.text.ExceptionConverter;
import com.sun.jna.platform.win32.WinCrypt;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.signers.pkcs7.AOAlgorithmID;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Set;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.measurement.PDNumberFormatDictionary;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Enumerated;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1OutputStream;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.ASN1String;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.BERTaggedObject;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.DERUTCTime;
import org.spongycastle.asn1.cms.Attribute;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.asn1.ocsp.BasicOCSPResponse;
import org.spongycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.ocsp.BasicOCSPResp;
import org.spongycastle.cert.ocsp.CertificateID;
import org.spongycastle.cert.ocsp.SingleResp;
import org.spongycastle.jce.provider.X509CRLParser;
import org.spongycastle.jce.provider.X509CertParser;
import org.spongycastle.operator.bc.BcDigestCalculatorProvider;
import org.spongycastle.tsp.TimeStampToken;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfPKCS7.class */
public final class PdfPKCS7 {
    private byte[] sigAttr;
    private byte[] digestAttr;
    private int version;
    private int signerversion;
    private Set<String> digestalgos;
    private Collection<Certificate> certs;
    private Collection<CRL> crls;
    private Collection<Certificate> signCerts;
    private X509Certificate signCert;
    private byte[] digest;
    private MessageDigest messageDigest;
    private String digestAlgorithm;
    private String digestEncryptionAlgorithm;
    private Signature sig;
    private byte[] RSAdata;
    private boolean verified;
    private boolean verifyResult;
    private byte[] externalDigest;
    private byte[] externalRSAdata;
    private String provider;
    private static final String ID_PKCS7_DATA = "1.2.840.113549.1.7.1";
    private static final String ID_PKCS7_SIGNED_DATA = "1.2.840.113549.1.7.2";
    private static final String ID_RSA = "1.2.840.113549.1.1.1";
    private static final String ID_DSA = "1.2.840.10040.4.1";
    private static final String ID_CONTENT_TYPE = "1.2.840.113549.1.9.3";
    private static final String ID_MESSAGE_DIGEST = "1.2.840.113549.1.9.4";
    private static final String ID_SIGNING_TIME = "1.2.840.113549.1.9.5";
    private static final String ID_ADBE_REVOCATION = "1.2.840.113583.1.1.8";
    private String reason;
    private String location;
    private String contactInfo;
    private Calendar signDate;
    private String signName;
    private TimeStampToken timeStampToken;
    private static final HashMap<String, String> STRICT_DIGEST_NAMES = new LinkedHashMap();
    private static final HashMap<String, String> DIGEST_NAMES = new LinkedHashMap();
    private static final HashMap<String, String> ALGORITHM_NAMES = new LinkedHashMap();
    private static final HashMap<String, String> ALLOWED_DIGESTS = new LinkedHashMap();
    private BasicOCSPResp basicResp;
    private static final String OID_ECDSA_SHA1 = "1.2.840.10045.4.1";
    private static final String OID_ECDSA_SHA224 = "1.2.840.10045.4.3.1";
    private static final String OID_ECDSA_SHA256 = "1.2.840.10045.4.3.2";
    private static final String OID_ECDSA_SHA384 = "1.2.840.10045.4.3.3";
    private static final String OID_ECDSA_SHA512 = "1.2.840.10045.4.3.4";
    private static final String OID_RSA_SHA3_224 = "2.16.840.1.101.3.4.3.13";
    private static final String OID_RSA_SHA3_256 = "2.16.840.1.101.3.4.3.14";
    private static final String OID_RSA_SHA3_384 = "2.16.840.1.101.3.4.3.15";
    private static final String OID_RSA_SHA3_512 = "2.16.840.1.101.3.4.3.16";
    private static final String OID_ECDSA_SHA3_224 = "2.16.840.1.101.3.4.3.9";
    private static final String OID_ECDSA_SHA3_256 = "2.16.840.1.101.3.4.3.10";
    private static final String OID_ECDSA_SHA3_384 = "2.16.840.1.101.3.4.3.11";
    private static final String OID_ECDSA_SHA3_512 = "2.16.840.1.101.3.4.3.12";
    private static final Dictionary<String, String> OIDS;

    static {
        STRICT_DIGEST_NAMES.put("1.2.840.113549.2.5", MessageDigestAlgorithms.MD5);
        STRICT_DIGEST_NAMES.put("1.2.840.113549.2.2", MessageDigestAlgorithms.MD2);
        STRICT_DIGEST_NAMES.put("1.3.14.3.2.26", AOSignConstants.DIGEST_ALGORITHM_SHA1);
        STRICT_DIGEST_NAMES.put("2.16.840.1.101.3.4.2.4", "SHA224");
        STRICT_DIGEST_NAMES.put(AOAlgorithmID.OID_SHA256, "SHA256");
        STRICT_DIGEST_NAMES.put("2.16.840.1.101.3.4.2.2", AOSignConstants.DIGEST_ALGORITHM_SHA384);
        STRICT_DIGEST_NAMES.put("2.16.840.1.101.3.4.2.3", "SHA512");
        STRICT_DIGEST_NAMES.put("1.3.36.3.2.2", "RIPEMD128");
        STRICT_DIGEST_NAMES.put("1.3.36.3.2.1", "RIPEMD160");
        STRICT_DIGEST_NAMES.put("1.3.36.3.2.3", "RIPEMD256");
        DIGEST_NAMES.put("1.2.840.113549.2.5", MessageDigestAlgorithms.MD5);
        DIGEST_NAMES.put("1.2.840.113549.2.2", MessageDigestAlgorithms.MD2);
        DIGEST_NAMES.put("1.3.14.3.2.26", AOSignConstants.DIGEST_ALGORITHM_SHA1);
        DIGEST_NAMES.put("2.16.840.1.101.3.4.2.4", "SHA224");
        DIGEST_NAMES.put(AOAlgorithmID.OID_SHA256, "SHA256");
        DIGEST_NAMES.put("2.16.840.1.101.3.4.2.2", AOSignConstants.DIGEST_ALGORITHM_SHA384);
        DIGEST_NAMES.put("2.16.840.1.101.3.4.2.3", "SHA512");
        DIGEST_NAMES.put("1.3.36.3.2.2", "RIPEMD128");
        DIGEST_NAMES.put("1.3.36.3.2.1", "RIPEMD160");
        DIGEST_NAMES.put("1.3.36.3.2.3", "RIPEMD256");
        DIGEST_NAMES.put("1.2.840.113549.1.1.4", MessageDigestAlgorithms.MD5);
        DIGEST_NAMES.put("1.2.840.113549.1.1.2", MessageDigestAlgorithms.MD2);
        DIGEST_NAMES.put(WinCrypt.szOID_RSA_SHA1RSA, AOSignConstants.DIGEST_ALGORITHM_SHA1);
        DIGEST_NAMES.put("1.2.840.113549.1.1.14", "SHA224");
        DIGEST_NAMES.put("1.2.840.113549.1.1.11", "SHA256");
        DIGEST_NAMES.put("1.2.840.113549.1.1.12", AOSignConstants.DIGEST_ALGORITHM_SHA384);
        DIGEST_NAMES.put("1.2.840.113549.1.1.13", "SHA512");
        DIGEST_NAMES.put("1.2.840.10040.4.3", AOSignConstants.DIGEST_ALGORITHM_SHA1);
        DIGEST_NAMES.put("2.16.840.1.101.3.4.3.1", "SHA224");
        DIGEST_NAMES.put("2.16.840.1.101.3.4.3.2", "SHA256");
        DIGEST_NAMES.put("2.16.840.1.101.3.4.3.3", AOSignConstants.DIGEST_ALGORITHM_SHA384);
        DIGEST_NAMES.put("2.16.840.1.101.3.4.3.4", "SHA512");
        DIGEST_NAMES.put("1.3.36.3.3.1.3", "RIPEMD128");
        DIGEST_NAMES.put("1.3.36.3.3.1.2", "RIPEMD160");
        DIGEST_NAMES.put("1.3.36.3.3.1.4", "RIPEMD256");
        ALGORITHM_NAMES.put(ID_RSA, "RSA");
        ALGORITHM_NAMES.put(ID_DSA, "DSA");
        ALGORITHM_NAMES.put("1.2.840.113549.1.1.2", "RSA");
        ALGORITHM_NAMES.put("1.2.840.113549.1.1.4", "RSA");
        ALGORITHM_NAMES.put(WinCrypt.szOID_RSA_SHA1RSA, "RSA");
        ALGORITHM_NAMES.put("1.2.840.113549.1.1.14", "RSA");
        ALGORITHM_NAMES.put("1.2.840.113549.1.1.11", "RSA");
        ALGORITHM_NAMES.put("1.2.840.113549.1.1.12", "RSA");
        ALGORITHM_NAMES.put("1.2.840.113549.1.1.13", "RSA");
        ALGORITHM_NAMES.put("1.2.840.10040.4.3", "DSA");
        ALGORITHM_NAMES.put("2.16.840.1.101.3.4.3.1", "DSA");
        ALGORITHM_NAMES.put("2.16.840.1.101.3.4.3.2", "DSA");
        ALGORITHM_NAMES.put("1.3.36.3.3.1.3", "RSA");
        ALGORITHM_NAMES.put("1.3.36.3.3.1.2", "RSA");
        ALGORITHM_NAMES.put("1.3.36.3.3.1.4", "RSA");
        ALLOWED_DIGESTS.put(MessageDigestAlgorithms.MD5, "1.2.840.113549.2.5");
        ALLOWED_DIGESTS.put(MessageDigestAlgorithms.MD2, "1.2.840.113549.2.2");
        ALLOWED_DIGESTS.put(AOSignConstants.DIGEST_ALGORITHM_SHA1, "1.3.14.3.2.26");
        ALLOWED_DIGESTS.put("SHA224", "2.16.840.1.101.3.4.2.4");
        ALLOWED_DIGESTS.put("SHA256", AOAlgorithmID.OID_SHA256);
        ALLOWED_DIGESTS.put(AOSignConstants.DIGEST_ALGORITHM_SHA384, "2.16.840.1.101.3.4.2.2");
        ALLOWED_DIGESTS.put("SHA512", "2.16.840.1.101.3.4.2.3");
        ALLOWED_DIGESTS.put("MD-5", "1.2.840.113549.2.5");
        ALLOWED_DIGESTS.put("MD-2", "1.2.840.113549.2.2");
        ALLOWED_DIGESTS.put("SHA-1", "1.3.14.3.2.26");
        ALLOWED_DIGESTS.put("SHA-224", "2.16.840.1.101.3.4.2.4");
        ALLOWED_DIGESTS.put("SHA-256", AOAlgorithmID.OID_SHA256);
        ALLOWED_DIGESTS.put("SHA-384", "2.16.840.1.101.3.4.2.2");
        ALLOWED_DIGESTS.put("SHA-512", "2.16.840.1.101.3.4.2.3");
        ALLOWED_DIGESTS.put("RIPEMD128", "1.3.36.3.2.2");
        ALLOWED_DIGESTS.put("RIPEMD-128", "1.3.36.3.2.2");
        ALLOWED_DIGESTS.put("RIPEMD160", "1.3.36.3.2.1");
        ALLOWED_DIGESTS.put("RIPEMD-160", "1.3.36.3.2.1");
        ALLOWED_DIGESTS.put("RIPEMD256", "1.3.36.3.2.3");
        ALLOWED_DIGESTS.put("RIPEMD-256", "1.3.36.3.2.3");
        OIDS = new Hashtable();
        OIDS.put(OID_ECDSA_SHA1, AOSignConstants.SIGN_ALGORITHM_SHA1WITHECDSA);
        OIDS.put(OID_ECDSA_SHA224, AOSignConstants.SIGN_ALGORITHM_SHA224WITHECDSA);
        OIDS.put(OID_ECDSA_SHA256, AOSignConstants.SIGN_ALGORITHM_SHA256WITHECDSA);
        OIDS.put(OID_ECDSA_SHA384, AOSignConstants.SIGN_ALGORITHM_SHA384WITHECDSA);
        OIDS.put(OID_ECDSA_SHA512, AOSignConstants.SIGN_ALGORITHM_SHA512WITHECDSA);
        OIDS.put(OID_RSA_SHA3_224, "SHA3-224withRSA");
        OIDS.put(OID_RSA_SHA3_256, "SHA3-256withRSA");
        OIDS.put(OID_RSA_SHA3_384, "SHA3-384withRSA");
        OIDS.put(OID_RSA_SHA3_512, "SHA3-512withRSA");
        OIDS.put(OID_ECDSA_SHA3_224, "SHA3-224withECDSA");
        OIDS.put(OID_ECDSA_SHA3_256, "SHA3-256withECDSA");
        OIDS.put(OID_ECDSA_SHA3_384, "SHA3-384withECDSA");
        OIDS.put(OID_ECDSA_SHA3_512, "SHA3-512withECDSA");
    }

    public byte[] getPkcs1() {
        if (this.digest != null) {
            return (byte[]) this.digest.clone();
        }
        return null;
    }

    private static String getDigest(String oid) {
        String ret = DIGEST_NAMES.get(oid);
        if (ret == null) {
            return oid;
        }
        return ret;
    }

    private static String getAlgorithm(String oid) {
        String ret = ALGORITHM_NAMES.get(oid);
        if (ret == null) {
            return oid;
        }
        return ret;
    }

    public TimeStampToken getTimeStampToken() {
        return this.timeStampToken;
    }

    public Calendar getTimeStampDate() {
        if (this.timeStampToken == null) {
            return null;
        }
        Calendar cal = new GregorianCalendar();
        Date date = this.timeStampToken.getTimeStampInfo().getGenTime();
        cal.setTime(date);
        return cal;
    }

    PdfPKCS7(byte[] contentsKey, byte[] certsKey, String provider) {
        try {
            this.provider = provider;
            CertificateFactory cf = CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID);
            this.certs = cf.generateCertificates(new ByteArrayInputStream(certsKey));
            this.signCerts = this.certs;
            this.signCert = (X509Certificate) this.certs.iterator().next();
            this.crls = new ArrayList();
            ASN1InputStream in = new ASN1InputStream(new ByteArrayInputStream(contentsKey));
            this.digest = ((DEROctetString) in.readObject()).getOctets();
            if (provider == null) {
                this.sig = Signature.getInstance(AOSignConstants.SIGN_ALGORITHM_SHA1WITHRSA);
            } else {
                this.sig = Signature.getInstance(AOSignConstants.SIGN_ALGORITHM_SHA1WITHRSA, provider);
            }
            PublicKey publicKey = this.signCert.getPublicKey();
            publicKey = publicKey == null ? CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID).generateCertificate(new ByteArrayInputStream(this.signCert.getEncoded())).getPublicKey() : publicKey;
            if (publicKey != null) {
                this.sig.initVerify(publicKey);
                return;
            }
            throw new CertificateEncodingException("El certificado no contiene una clave publica adecuada");
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public BasicOCSPResp getOcsp() {
        return this.basicResp;
    }

    private void findOcsp(ASN1Sequence seq) throws IOException {
        boolean ret;
        this.basicResp = null;
        do {
            if (!(seq.getObjectAt(0) instanceof ASN1ObjectIdentifier) || !((ASN1ObjectIdentifier) seq.getObjectAt(0)).getId().equals(OCSPObjectIdentifiers.id_pkix_ocsp_basic.getId())) {
                ret = true;
                for (int k = 0; k < seq.size(); k++) {
                    if (seq.getObjectAt(k) instanceof ASN1Sequence) {
                        seq = (ASN1Sequence) seq.getObjectAt(0);
                        ret = false;
                        break;
                    } else {
                        if (seq.getObjectAt(k) instanceof ASN1TaggedObject) {
                            ASN1TaggedObject tag = (ASN1TaggedObject) seq.getObjectAt(k);
                            if (tag.getObject() instanceof ASN1Sequence) {
                                seq = (ASN1Sequence) tag.getObject();
                                ret = false;
                                break;
                            }
                            return;
                        }
                    }
                }
            } else {
                DEROctetString os = (DEROctetString) seq.getObjectAt(1);
                ASN1InputStream inp = new ASN1InputStream(os.getOctets());
                BasicOCSPResponse resp = BasicOCSPResponse.getInstance(inp.readObject());
                this.basicResp = new BasicOCSPResp(resp);
                return;
            }
        } while (!ret);
    }

    PdfPKCS7(byte[] contentsKey, String provider) {
        ASN1OctetString rsaDataContent;
        try {
            this.provider = provider;
            ASN1InputStream din = new ASN1InputStream(new ByteArrayInputStream(contentsKey));
            try {
                ASN1Primitive pkcs = din.readObject();
                if (!(pkcs instanceof ASN1Sequence)) {
                    throw new IllegalArgumentException("Not a valid PKCS#7 object - not a sequence");
                }
                ASN1Sequence signedData = (ASN1Sequence) pkcs;
                ASN1ObjectIdentifier objId = (ASN1ObjectIdentifier) signedData.getObjectAt(0);
                if (!objId.getId().equals(ID_PKCS7_SIGNED_DATA)) {
                    throw new IllegalArgumentException("Not a valid PKCS#7 object - not signed data");
                }
                ASN1Sequence content = (ASN1Sequence) ((ASN1TaggedObject) signedData.getObjectAt(1)).getObject();
                this.version = ((ASN1Integer) content.getObjectAt(0)).getValue().intValue();
                this.digestalgos = new HashSet();
                Enumeration e = ((ASN1Set) content.getObjectAt(1)).getObjects();
                while (e.hasMoreElements()) {
                    ASN1Sequence s = (ASN1Sequence) e.nextElement();
                    ASN1ObjectIdentifier o = (ASN1ObjectIdentifier) s.getObjectAt(0);
                    this.digestalgos.add(o.getId());
                }
                X509CertParser cr = new X509CertParser();
                cr.engineInit(new ByteArrayInputStream(contentsKey));
                this.certs = cr.engineReadAll();
                X509CRLParser cl = new X509CRLParser();
                cl.engineInit(new ByteArrayInputStream(contentsKey));
                this.crls = cl.engineReadAll();
                ASN1Sequence rsaData = (ASN1Sequence) content.getObjectAt(2);
                if (rsaData.size() > 1) {
                    ASN1Encodable encodable = rsaData.getObjectAt(1);
                    if (encodable instanceof DERTaggedObject) {
                        rsaDataContent = (ASN1OctetString) ((DERTaggedObject) encodable).getObject();
                    } else if (encodable instanceof BERTaggedObject) {
                        rsaDataContent = (ASN1OctetString) ((BERTaggedObject) encodable).getObject();
                    } else {
                        throw new IllegalArgumentException("El objeto ASN.1 no es ni BER ni BER ni DER: " + encodable.getClass().getName());
                    }
                    this.RSAdata = rsaDataContent.getOctets();
                }
                int next = 3;
                while (content.getObjectAt(next) instanceof ASN1TaggedObject) {
                    next++;
                }
                ASN1Set signerInfos = (ASN1Set) content.getObjectAt(next);
                if (signerInfos.size() != 1) {
                    throw new IllegalArgumentException("This PKCS#7 object has multiple SignerInfos - only one is supported at this time");
                }
                ASN1Sequence signerInfo = (ASN1Sequence) signerInfos.getObjectAt(0);
                this.signerversion = ((ASN1Integer) signerInfo.getObjectAt(0)).getValue().intValue();
                ASN1Sequence issuerAndSerialNumber = (ASN1Sequence) signerInfo.getObjectAt(1);
                BigInteger serialNumber = ((ASN1Integer) issuerAndSerialNumber.getObjectAt(1)).getValue();
                for (Object element : this.certs) {
                    X509Certificate cert = (X509Certificate) element;
                    if (serialNumber.equals(cert.getSerialNumber())) {
                        this.signCert = cert;
                        break;
                    }
                }
                if (this.signCert == null) {
                    throw new IllegalArgumentException("Can't find signing certificate with serial " + serialNumber.toString(16));
                }
                signCertificateChain();
                this.digestAlgorithm = ((ASN1ObjectIdentifier) ((ASN1Sequence) signerInfo.getObjectAt(2)).getObjectAt(0)).getId();
                int next2 = 3;
                if (signerInfo.getObjectAt(3) instanceof ASN1TaggedObject) {
                    ASN1TaggedObject tagsig = (ASN1TaggedObject) signerInfo.getObjectAt(3);
                    ASN1Set sseq = ASN1Set.getInstance(tagsig, false);
                    this.sigAttr = sseq.getEncoded();
                    for (int k = 0; k < sseq.size(); k++) {
                        ASN1Sequence seq2 = (ASN1Sequence) sseq.getObjectAt(k);
                        if (((ASN1ObjectIdentifier) seq2.getObjectAt(0)).getId().equals(ID_MESSAGE_DIGEST)) {
                            ASN1Set set = (ASN1Set) seq2.getObjectAt(1);
                            this.digestAttr = ((DEROctetString) set.getObjectAt(0)).getOctets();
                        } else if (((ASN1ObjectIdentifier) seq2.getObjectAt(0)).getId().equals(ID_ADBE_REVOCATION)) {
                            ASN1Set setout = (ASN1Set) seq2.getObjectAt(1);
                            ASN1Sequence seqout = (ASN1Sequence) setout.getObjectAt(0);
                            for (int j = 0; j < seqout.size(); j++) {
                                ASN1TaggedObject tg = (ASN1TaggedObject) seqout.getObjectAt(j);
                                if (tg.getTagNo() == 1) {
                                    ASN1Sequence seqin = (ASN1Sequence) tg.getObject();
                                    findOcsp(seqin);
                                }
                            }
                        }
                    }
                    if (this.digestAttr == null) {
                        throw new IllegalArgumentException("Authenticated attribute is missing the digest.");
                    }
                    next2 = 3 + 1;
                }
                int i = next2;
                int next3 = next2 + 1;
                this.digestEncryptionAlgorithm = ((ASN1ObjectIdentifier) ((ASN1Sequence) signerInfo.getObjectAt(i)).getObjectAt(0)).getId();
                int next4 = next3 + 1;
                this.digest = ((DEROctetString) signerInfo.getObjectAt(next3)).getOctets();
                if (next4 < signerInfo.size() && (signerInfo.getObjectAt(next4) instanceof DERTaggedObject)) {
                    DERTaggedObject taggedObject = (DERTaggedObject) signerInfo.getObjectAt(next4);
                    ASN1Set unat = ASN1Set.getInstance(taggedObject, false);
                    AttributeTable attble = new AttributeTable(unat);
                    Attribute ts = attble.get(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken);
                    if (ts != null) {
                        ASN1Set attributeValues = ts.getAttrValues();
                        ASN1Sequence tokenSequence = ASN1Sequence.getInstance(attributeValues.getObjectAt(0));
                        ContentInfo contentInfo = ContentInfo.getInstance(tokenSequence);
                        this.timeStampToken = new TimeStampToken(contentInfo);
                    }
                }
                if (this.RSAdata != null || this.digestAttr != null) {
                    if (provider == null || provider.startsWith("SunPKCS11")) {
                        this.messageDigest = MessageDigest.getInstance(getDigestAlgorithmName(getHashAlgorithm()));
                    } else {
                        this.messageDigest = MessageDigest.getInstance(getDigestAlgorithmName(getHashAlgorithm()), provider);
                    }
                }
                if (provider == null) {
                    this.sig = Signature.getInstance(getDigestAlgorithm());
                } else {
                    this.sig = Signature.getInstance(getDigestAlgorithm(), provider);
                }
                PublicKey publicKey = this.signCert.getPublicKey();
                publicKey = publicKey == null ? CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID).generateCertificate(new ByteArrayInputStream(this.signCert.getEncoded())).getPublicKey() : publicKey;
                if (publicKey != null) {
                    this.sig.initVerify(publicKey);
                    return;
                }
                throw new CertificateEncodingException("El certificado no contiene una clave publica adecuada");
            } catch (IOException e2) {
                throw new IllegalArgumentException("can't decode PKCS7SignedData object", e2);
            }
        } catch (Exception e3) {
            throw new ExceptionConverter(e3);
        }
    }

    PdfPKCS7(PrivateKey privKey, Certificate[] certChain, CRL[] crlList, String hashAlgorithm, String provider, boolean hasRSAdata) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        this.provider = provider;
        this.digestAlgorithm = ALLOWED_DIGESTS.get(hashAlgorithm.toUpperCase());
        if (this.digestAlgorithm == null) {
            throw new NoSuchAlgorithmException("Unknown Hash Algorithm " + hashAlgorithm);
        }
        this.signerversion = 1;
        this.version = 1;
        this.certs = new ArrayList();
        this.crls = new ArrayList();
        this.digestalgos = new HashSet();
        this.digestalgos.add(this.digestAlgorithm);
        this.signCert = (X509Certificate) certChain[0];
        for (Certificate element : certChain) {
            this.certs.add(element);
        }
        if (crlList != null) {
            for (CRL element2 : crlList) {
                this.crls.add(element2);
            }
        }
        if (privKey != null) {
            this.digestEncryptionAlgorithm = privKey.getAlgorithm();
            if (this.digestEncryptionAlgorithm.equals("RSA")) {
                this.digestEncryptionAlgorithm = ID_RSA;
            } else if (this.digestEncryptionAlgorithm.equals("DSA")) {
                this.digestEncryptionAlgorithm = ID_DSA;
            } else {
                throw new NoSuchAlgorithmException("Unknown Key Algorithm " + this.digestEncryptionAlgorithm);
            }
        }
        if (hasRSAdata) {
            this.RSAdata = new byte[0];
            if (provider == null || provider.startsWith("SunPKCS11")) {
                this.messageDigest = MessageDigest.getInstance(getHashAlgorithm());
            } else {
                this.messageDigest = MessageDigest.getInstance(getHashAlgorithm(), provider);
            }
        }
        if (privKey != null) {
            if (provider == null) {
                this.sig = Signature.getInstance(getDigestAlgorithm());
            } else {
                this.sig = Signature.getInstance(getDigestAlgorithm(), provider);
            }
            this.sig.initSign(privKey);
        }
    }

    void update(byte[] buf, int off, int len) throws SignatureException {
        if (this.RSAdata != null || this.digestAttr != null) {
            this.messageDigest.update(buf, off, len);
        } else {
            this.sig.update(buf, off, len);
        }
    }

    public Certificate[] getCertificates() {
        return (Certificate[]) this.certs.toArray(new X509Certificate[this.certs.size()]);
    }

    public Certificate[] getSignCertificateChain() {
        return (Certificate[]) this.signCerts.toArray(new X509Certificate[this.signCerts.size()]);
    }

    private void signCertificateChain() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.signCert);
        ArrayList<Certificate> oc = new ArrayList<>(this.certs);
        int k = 0;
        while (k < oc.size()) {
            if (this.signCert.getSerialNumber().equals(((X509Certificate) oc.get(k)).getSerialNumber())) {
                oc.remove(k);
                k--;
            }
            k++;
        }
        boolean found = true;
        while (found) {
            X509Certificate v = (X509Certificate) arrayList.get(arrayList.size() - 1);
            found = false;
            for (int k2 = 0; k2 < oc.size(); k2++) {
                try {
                    if (this.provider == null) {
                        v.verify(((X509Certificate) oc.get(k2)).getPublicKey());
                    } else {
                        v.verify(((X509Certificate) oc.get(k2)).getPublicKey(), this.provider);
                    }
                    found = true;
                    arrayList.add(oc.get(k2));
                    oc.remove(k2);
                    break;
                } catch (Exception e) {
                }
            }
        }
        this.signCerts = arrayList;
    }

    public Collection<CRL> getCRLs() {
        return this.crls;
    }

    public X509Certificate getSigningCertificate() {
        return this.signCert;
    }

    public int getVersion() {
        return this.version;
    }

    public int getSigningInfoVersion() {
        return this.signerversion;
    }

    public String getDigestAlgorithm() {
        if (this.digestEncryptionAlgorithm == null) {
            return null;
        }
        String dea = getAlgorithm(this.digestEncryptionAlgorithm);
        if (dea == null) {
            dea = this.digestEncryptionAlgorithm;
        }
        String algo = dea != null ? OIDS.get(dea) : null;
        return algo != null ? algo : getHashAlgorithm() + "with" + dea;
    }

    public String getHashAlgorithm() {
        return getDigest(this.digestAlgorithm);
    }

    public String getStrictHashAlgorithm() {
        return STRICT_DIGEST_NAMES.get(this.digestAlgorithm);
    }

    public boolean isRevocationValid() {
        if (this.basicResp == null || this.signCerts.size() < 2) {
            return false;
        }
        try {
            X509Certificate[] cs = (X509Certificate[]) getSignCertificateChain();
            SingleResp sr = this.basicResp.getResponses()[0];
            CertificateID cid = sr.getCertID();
            X509Certificate sigcer = getSigningCertificate();
            X509Certificate isscer = cs[1];
            CertificateID tis = new CertificateID(new BcDigestCalculatorProvider().get(CertificateID.HASH_SHA1), new X509CertificateHolder(isscer.getEncoded()), sigcer.getSerialNumber());
            return tis.equals(cid);
        } catch (Exception e) {
            return false;
        }
    }

    private static ASN1Primitive getIssuer(byte[] enc) {
        try {
            ASN1InputStream in = new ASN1InputStream(new ByteArrayInputStream(enc));
            Throwable th = null;
            try {
                ASN1Sequence seq = (ASN1Sequence) in.readObject();
                ASN1Primitive aSN1Primitive = (ASN1Primitive) seq.getObjectAt(seq.getObjectAt(0) instanceof DERTaggedObject ? 3 : 2);
                if (in != null) {
                    if (0 != 0) {
                        try {
                            in.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        in.close();
                    }
                }
                return aSN1Primitive;
            } catch (Throwable th3) {
                if (in != null) {
                    if (0 != 0) {
                        try {
                            in.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        in.close();
                    }
                }
                throw th3;
            }
        } catch (IOException e) {
            throw new ExceptionConverter(e);
        }
    }

    private static ASN1Primitive getSubject(byte[] enc) {
        try {
            ASN1InputStream in = new ASN1InputStream(new ByteArrayInputStream(enc));
            ASN1Sequence seq = (ASN1Sequence) in.readObject();
            return (ASN1Primitive) seq.getObjectAt(seq.getObjectAt(0) instanceof DERTaggedObject ? 5 : 4);
        } catch (IOException e) {
            throw new ExceptionConverter(e);
        }
    }

    public static X509Name getSubjectFields(X509Certificate cert) {
        try {
            return new X509Name((ASN1Sequence) getSubject(cert.getTBSCertificate()));
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public byte[] getEncodedPKCS1() {
        try {
            if (this.externalDigest != null) {
                this.digest = this.externalDigest;
            } else {
                this.digest = this.sig.sign();
            }
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            ASN1OutputStream dout = new ASN1OutputStream(bOut);
            dout.writeObject(new DEROctetString(this.digest));
            dout.close();
            return bOut.toByteArray();
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public void setExternalDigest(byte[] digest, byte[] RSAdata, String digestEncryptionAlgorithm) {
        this.externalDigest = digest;
        this.externalRSAdata = RSAdata;
        if (digestEncryptionAlgorithm != null) {
            if (digestEncryptionAlgorithm.equals("RSA")) {
                this.digestEncryptionAlgorithm = ID_RSA;
            } else {
                if (digestEncryptionAlgorithm.equals("DSA")) {
                    this.digestEncryptionAlgorithm = ID_DSA;
                    return;
                }
                throw new ExceptionConverter(new NoSuchAlgorithmException("Unknown Key Algorithm " + digestEncryptionAlgorithm));
            }
        }
    }

    public byte[] getEncodedPKCS7() {
        return getEncodedPKCS7(null, null, null, null);
    }

    private byte[] getEncodedPKCS7(byte[] secondDigest, Calendar signingTime, TSAClient tsaClient, byte[] ocsp) {
        ASN1EncodableVector unauthAttributes;
        try {
            if (this.externalDigest != null) {
                this.digest = this.externalDigest;
                if (this.RSAdata != null) {
                    this.RSAdata = this.externalRSAdata;
                }
            } else if (this.externalRSAdata != null && this.RSAdata != null) {
                this.RSAdata = this.externalRSAdata;
                this.sig.update(this.RSAdata);
                try {
                    this.digest = this.sig.sign();
                } catch (Exception e) {
                }
            } else {
                if (this.RSAdata != null) {
                    this.RSAdata = this.messageDigest.digest();
                    this.sig.update(this.RSAdata);
                }
                try {
                    this.digest = this.sig.sign();
                } catch (Exception e2) {
                }
            }
            ASN1EncodableVector digestAlgorithms = new ASN1EncodableVector();
            for (String string : this.digestalgos) {
                ASN1EncodableVector algos = new ASN1EncodableVector();
                algos.add(new ASN1ObjectIdentifier(string));
                algos.add(DERNull.INSTANCE);
                digestAlgorithms.add(new DERSequence(algos));
            }
            ASN1EncodableVector v = new ASN1EncodableVector();
            v.add(new ASN1ObjectIdentifier("1.2.840.113549.1.7.1"));
            if (this.RSAdata != null) {
                v.add(new DERTaggedObject(0, new DEROctetString(this.RSAdata)));
            }
            DERSequence contentinfo = new DERSequence(v);
            ASN1EncodableVector v2 = new ASN1EncodableVector();
            for (Object element : this.certs) {
                ASN1InputStream tempstream = new ASN1InputStream(new ByteArrayInputStream(((X509Certificate) element).getEncoded()));
                v2.add(tempstream.readObject());
            }
            DERSet dercertificates = new DERSet(v2);
            ASN1EncodableVector signerinfo = new ASN1EncodableVector();
            signerinfo.add(new ASN1Integer(this.signerversion));
            ASN1EncodableVector v3 = new ASN1EncodableVector();
            v3.add(getIssuer(this.signCert.getTBSCertificate()));
            v3.add(new ASN1Integer(this.signCert.getSerialNumber()));
            signerinfo.add(new DERSequence(v3));
            ASN1EncodableVector v4 = new ASN1EncodableVector();
            v4.add(new ASN1ObjectIdentifier(this.digestAlgorithm));
            v4.add(DERNull.INSTANCE);
            signerinfo.add(new DERSequence(v4));
            if (secondDigest != null && signingTime != null) {
                signerinfo.add(new DERTaggedObject(false, 0, getAuthenticatedAttributeSet(secondDigest, signingTime, ocsp)));
            }
            ASN1EncodableVector v5 = new ASN1EncodableVector();
            v5.add(new ASN1ObjectIdentifier(this.digestEncryptionAlgorithm));
            v5.add(DERNull.INSTANCE);
            signerinfo.add(new DERSequence(v5));
            signerinfo.add(new DEROctetString(this.digest));
            if (tsaClient != null) {
                byte[] tsImprint = MessageDigest.getInstance("SHA-1").digest(this.digest);
                byte[] tsToken = tsaClient.getTimeStampToken(this, tsImprint);
                if (tsToken != null && (unauthAttributes = buildUnauthenticatedAttributes(tsToken)) != null) {
                    signerinfo.add(new DERTaggedObject(false, 1, new DERSet(unauthAttributes)));
                }
            }
            ASN1EncodableVector body = new ASN1EncodableVector();
            body.add(new ASN1Integer(this.version));
            body.add(new DERSet(digestAlgorithms));
            body.add(contentinfo);
            body.add(new DERTaggedObject(false, 0, dercertificates));
            if (!this.crls.isEmpty()) {
                ASN1EncodableVector v6 = new ASN1EncodableVector();
                for (Object element2 : this.crls) {
                    ASN1InputStream t = new ASN1InputStream(new ByteArrayInputStream(((X509CRL) element2).getEncoded()));
                    v6.add(t.readObject());
                }
                DERSet dercrls = new DERSet(v6);
                body.add(new DERTaggedObject(false, 1, dercrls));
            }
            body.add(new DERSet(new DERSequence(signerinfo)));
            ASN1EncodableVector whole = new ASN1EncodableVector();
            whole.add(new ASN1ObjectIdentifier(ID_PKCS7_SIGNED_DATA));
            whole.add(new DERTaggedObject(0, new DERSequence(body)));
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            ASN1OutputStream dout = new ASN1OutputStream(bOut);
            dout.writeObject(new DERSequence(whole));
            dout.close();
            return bOut.toByteArray();
        } catch (Exception e3) {
            throw new ExceptionConverter(e3);
        }
    }

    private static ASN1EncodableVector buildUnauthenticatedAttributes(byte[] timeStampToken) throws IOException {
        if (timeStampToken == null) {
            return null;
        }
        ASN1InputStream tempstream = new ASN1InputStream(new ByteArrayInputStream(timeStampToken));
        ASN1EncodableVector unauthAttributes = new ASN1EncodableVector();
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1ObjectIdentifier("1.2.840.113549.1.9.16.2.14"));
        ASN1Sequence seq = (ASN1Sequence) tempstream.readObject();
        v.add(new DERSet(seq));
        unauthAttributes.add(new DERSequence(v));
        return unauthAttributes;
    }

    private DERSet getAuthenticatedAttributeSet(byte[] secondDigest, Calendar signingTime, byte[] ocsp) {
        try {
            ASN1EncodableVector attribute = new ASN1EncodableVector();
            ASN1EncodableVector v = new ASN1EncodableVector();
            v.add(new ASN1ObjectIdentifier(ID_CONTENT_TYPE));
            v.add(new DERSet(new ASN1ObjectIdentifier("1.2.840.113549.1.7.1")));
            attribute.add(new DERSequence(v));
            ASN1EncodableVector v2 = new ASN1EncodableVector();
            v2.add(new ASN1ObjectIdentifier(ID_SIGNING_TIME));
            v2.add(new DERSet(new DERUTCTime(signingTime.getTime())));
            attribute.add(new DERSequence(v2));
            ASN1EncodableVector v3 = new ASN1EncodableVector();
            v3.add(new ASN1ObjectIdentifier(ID_MESSAGE_DIGEST));
            v3.add(new DERSet(new DEROctetString(secondDigest)));
            attribute.add(new DERSequence(v3));
            if (ocsp != null) {
                ASN1EncodableVector v4 = new ASN1EncodableVector();
                v4.add(new ASN1ObjectIdentifier(ID_ADBE_REVOCATION));
                DEROctetString doctet = new DEROctetString(ocsp);
                ASN1EncodableVector vo1 = new ASN1EncodableVector();
                ASN1EncodableVector v5 = new ASN1EncodableVector();
                v5.add(OCSPObjectIdentifiers.id_pkix_ocsp_basic);
                v5.add(doctet);
                ASN1Enumerated den = new ASN1Enumerated(0);
                ASN1EncodableVector v6 = new ASN1EncodableVector();
                v6.add(den);
                v6.add(new DERTaggedObject(true, 0, new DERSequence(v5)));
                vo1.add(new DERSequence(v6));
                v4.add(new DERSet(new DERSequence(new DERTaggedObject(true, 1, new DERSequence(vo1)))));
                attribute.add(new DERSequence(v4));
            } else if (!this.crls.isEmpty()) {
                ASN1EncodableVector v7 = new ASN1EncodableVector();
                v7.add(new ASN1ObjectIdentifier(ID_ADBE_REVOCATION));
                ASN1EncodableVector v8 = new ASN1EncodableVector();
                for (Object element : this.crls) {
                    ASN1InputStream t = new ASN1InputStream(new ByteArrayInputStream(((X509CRL) element).getEncoded()));
                    v8.add(t.readObject());
                }
                v7.add(new DERSet(new DERSequence(new DERTaggedObject(true, 0, new DERSequence(v8)))));
                attribute.add(new DERSequence(v7));
            }
            return new DERSet(attribute);
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactInfo() {
        return this.contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public Calendar getSignDate() {
        return this.signDate;
    }

    public void setSignDate(Calendar signDate) {
        this.signDate = signDate;
    }

    public String getSignName() {
        return this.signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfPKCS7$X509Name.class */
    public static class X509Name {
        private static final ASN1ObjectIdentifier C = new ASN1ObjectIdentifier("2.5.4.6");
        private static final ASN1ObjectIdentifier O = new ASN1ObjectIdentifier("2.5.4.10");
        private static final ASN1ObjectIdentifier OU = new ASN1ObjectIdentifier("2.5.4.11");
        private static final ASN1ObjectIdentifier T = new ASN1ObjectIdentifier("2.5.4.12");
        private static final ASN1ObjectIdentifier CN = new ASN1ObjectIdentifier("2.5.4.3");
        private static final ASN1ObjectIdentifier SN = new ASN1ObjectIdentifier("2.5.4.5");
        private static final ASN1ObjectIdentifier L = new ASN1ObjectIdentifier("2.5.4.7");
        private static final ASN1ObjectIdentifier ST = new ASN1ObjectIdentifier("2.5.4.8");
        private static final ASN1ObjectIdentifier SURNAME = new ASN1ObjectIdentifier("2.5.4.4");
        private static final ASN1ObjectIdentifier GIVENNAME = new ASN1ObjectIdentifier("2.5.4.42");
        private static final ASN1ObjectIdentifier INITIALS = new ASN1ObjectIdentifier("2.5.4.43");
        private static final ASN1ObjectIdentifier GENERATION = new ASN1ObjectIdentifier("2.5.4.44");
        private static final ASN1ObjectIdentifier EmailAddress = new ASN1ObjectIdentifier("1.2.840.113549.1.9.1");
        private static final ASN1ObjectIdentifier DC = new ASN1ObjectIdentifier("0.9.2342.19200300.100.1.25");
        private static final ASN1ObjectIdentifier UID = new ASN1ObjectIdentifier("0.9.2342.19200300.100.1.1");
        private static HashMap<ASN1ObjectIdentifier, String> DefaultSymbols = new LinkedHashMap();
        private final HashMap<String, ArrayList<String>> values = new LinkedHashMap();

        static {
            DefaultSymbols.put(C, "C");
            DefaultSymbols.put(O, PDAnnotationLink.HIGHLIGHT_MODE_OUTLINE);
            DefaultSymbols.put(T, PDNumberFormatDictionary.FRACTIONAL_DISPLAY_TRUNCATE);
            DefaultSymbols.put(OU, "OU");
            DefaultSymbols.put(CN, "CN");
            DefaultSymbols.put(L, "L");
            DefaultSymbols.put(ST, "ST");
            DefaultSymbols.put(SN, "SN");
            DefaultSymbols.put(EmailAddress, "E");
            DefaultSymbols.put(DC, "DC");
            DefaultSymbols.put(UID, "UID");
            DefaultSymbols.put(SURNAME, "SURNAME");
            DefaultSymbols.put(GIVENNAME, "GIVENNAME");
            DefaultSymbols.put(INITIALS, "INITIALS");
            DefaultSymbols.put(GENERATION, "GENERATION");
        }

        X509Name(ASN1Sequence seq) {
            Enumeration e = seq.getObjects();
            while (e.hasMoreElements()) {
                ASN1Set set = (ASN1Set) e.nextElement();
                for (int i = 0; i < set.size(); i++) {
                    ASN1Sequence s = (ASN1Sequence) set.getObjectAt(i);
                    String id = DefaultSymbols.get(s.getObjectAt(0));
                    if (id != null) {
                        ArrayList<String> vs = this.values.get(id);
                        if (vs == null) {
                            vs = new ArrayList<>();
                            this.values.put(id, vs);
                        }
                        vs.add(((ASN1String) s.getObjectAt(1)).getString());
                    }
                }
            }
        }

        public String getField(String name) {
            ArrayList<String> arrayList = this.values.get(name);
            if (arrayList == null) {
                return null;
            }
            return arrayList.get(0);
        }

        public HashMap<String, ArrayList<String>> getFields() {
            return this.values;
        }

        public String toString() {
            return this.values.toString();
        }
    }

    private static String getDigestAlgorithmName(String pseudoName) {
        String upperPseudoName = pseudoName.toUpperCase();
        if (upperPseudoName.equals("SHA") || upperPseudoName.equals("http://www.w3.org/2000/09/xmldsig#sha1".toUpperCase()) || upperPseudoName.equals("1.3.14.3.2.26") || upperPseudoName.startsWith(AOSignConstants.DIGEST_ALGORITHM_SHA1) || upperPseudoName.startsWith("SHA-1")) {
            return AOSignConstants.DIGEST_ALGORITHM_SHA1;
        }
        if (upperPseudoName.equals("http://www.w3.org/2001/04/xmlenc#sha256".toUpperCase()) || upperPseudoName.equals(AOAlgorithmID.OID_SHA256) || upperPseudoName.startsWith("SHA256") || upperPseudoName.startsWith("SHA-256")) {
            return "SHA-256";
        }
        if (upperPseudoName.startsWith(AOSignConstants.DIGEST_ALGORITHM_SHA384) || upperPseudoName.equals("2.16.840.1.101.3.4.2.2") || upperPseudoName.startsWith("SHA-384")) {
            return "SHA-384";
        }
        if (upperPseudoName.equals("http://www.w3.org/2001/04/xmlenc#sha512".toUpperCase()) || upperPseudoName.equals("2.16.840.1.101.3.4.2.3") || upperPseudoName.startsWith("SHA512") || upperPseudoName.startsWith("SHA-512")) {
            return "SHA-512";
        }
        if (upperPseudoName.equals("http://www.w3.org/2001/04/xmlenc#ripemd160".toUpperCase()) || upperPseudoName.startsWith("RIPEMD160") || upperPseudoName.startsWith("RIPEMD-160")) {
            return "RIPEMD160";
        }
        if (upperPseudoName.equals(MessageDigestAlgorithms.MD5) || upperPseudoName.equals("1.2.840.113549.2.5") || upperPseudoName.startsWith(MessageDigestAlgorithms.MD5)) {
            return MessageDigestAlgorithms.MD5;
        }
        if (upperPseudoName.equals(MessageDigestAlgorithms.MD2) || upperPseudoName.equals("1.2.840.113549.2.2") || upperPseudoName.startsWith(MessageDigestAlgorithms.MD2)) {
            return MessageDigestAlgorithms.MD2;
        }
        throw new IllegalArgumentException("Algoritmo de huella digital no soportado: " + pseudoName);
    }

    public boolean verify() throws SignatureException {
        if (this.verified) {
            return this.verifyResult;
        }
        if (this.sigAttr != null) {
            this.sig.update(this.sigAttr);
            if (this.RSAdata != null) {
                byte[] msd = this.messageDigest.digest();
                this.messageDigest.update(msd);
            }
            this.verifyResult = Arrays.equals(this.messageDigest.digest(), this.digestAttr) && this.sig.verify(this.digest);
        } else {
            if (this.RSAdata != null) {
                this.sig.update(this.messageDigest.digest());
            }
            this.verifyResult = this.sig.verify(this.digest);
        }
        this.verified = true;
        return this.verifyResult;
    }
}
