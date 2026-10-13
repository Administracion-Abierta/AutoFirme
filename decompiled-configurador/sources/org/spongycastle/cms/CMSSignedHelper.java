package org.spongycastle.cms;

import es.gob.afirma.core.signers.AOSignConstants;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.cms.OtherRevocationInfoFormat;
import org.spongycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.spongycastle.asn1.eac.EACObjectIdentifiers;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
import org.spongycastle.asn1.oiw.OIWObjectIdentifiers;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.spongycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.AttributeCertificate;
import org.spongycastle.asn1.x509.Certificate;
import org.spongycastle.asn1.x509.CertificateList;
import org.spongycastle.asn1.x509.X509ObjectIdentifiers;
import org.spongycastle.asn1.x9.X9ObjectIdentifiers;
import org.spongycastle.cert.X509AttributeCertificateHolder;
import org.spongycastle.cert.X509CRLHolder;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.util.CollectionStore;
import org.spongycastle.util.Store;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/CMSSignedHelper.class */
class CMSSignedHelper {
    static final CMSSignedHelper INSTANCE = new CMSSignedHelper();
    private static final Map encryptionAlgs = new HashMap();
    private static final Map digestAlgs = new HashMap();
    private static final Map digestAliases = new HashMap();

    CMSSignedHelper() {
    }

    static {
        addEntries(NISTObjectIdentifiers.dsa_with_sha224, "SHA224", "DSA");
        addEntries(NISTObjectIdentifiers.dsa_with_sha256, "SHA256", "DSA");
        addEntries(NISTObjectIdentifiers.dsa_with_sha384, AOSignConstants.DIGEST_ALGORITHM_SHA384, "DSA");
        addEntries(NISTObjectIdentifiers.dsa_with_sha512, "SHA512", "DSA");
        addEntries(OIWObjectIdentifiers.dsaWithSHA1, AOSignConstants.DIGEST_ALGORITHM_SHA1, "DSA");
        addEntries(OIWObjectIdentifiers.md4WithRSA, "MD4", "RSA");
        addEntries(OIWObjectIdentifiers.md4WithRSAEncryption, "MD4", "RSA");
        addEntries(OIWObjectIdentifiers.md5WithRSA, MessageDigestAlgorithms.MD5, "RSA");
        addEntries(OIWObjectIdentifiers.sha1WithRSA, AOSignConstants.DIGEST_ALGORITHM_SHA1, "RSA");
        addEntries(PKCSObjectIdentifiers.md2WithRSAEncryption, MessageDigestAlgorithms.MD2, "RSA");
        addEntries(PKCSObjectIdentifiers.md4WithRSAEncryption, "MD4", "RSA");
        addEntries(PKCSObjectIdentifiers.md5WithRSAEncryption, MessageDigestAlgorithms.MD5, "RSA");
        addEntries(PKCSObjectIdentifiers.sha1WithRSAEncryption, AOSignConstants.DIGEST_ALGORITHM_SHA1, "RSA");
        addEntries(PKCSObjectIdentifiers.sha224WithRSAEncryption, "SHA224", "RSA");
        addEntries(PKCSObjectIdentifiers.sha256WithRSAEncryption, "SHA256", "RSA");
        addEntries(PKCSObjectIdentifiers.sha384WithRSAEncryption, AOSignConstants.DIGEST_ALGORITHM_SHA384, "RSA");
        addEntries(PKCSObjectIdentifiers.sha512WithRSAEncryption, "SHA512", "RSA");
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA1, AOSignConstants.DIGEST_ALGORITHM_SHA1, "ECDSA");
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA224, "SHA224", "ECDSA");
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA256, "SHA256", "ECDSA");
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA384, AOSignConstants.DIGEST_ALGORITHM_SHA384, "ECDSA");
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA512, "SHA512", "ECDSA");
        addEntries(X9ObjectIdentifiers.id_dsa_with_sha1, AOSignConstants.DIGEST_ALGORITHM_SHA1, "DSA");
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_1, AOSignConstants.DIGEST_ALGORITHM_SHA1, "ECDSA");
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_224, "SHA224", "ECDSA");
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_256, "SHA256", "ECDSA");
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_384, AOSignConstants.DIGEST_ALGORITHM_SHA384, "ECDSA");
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_512, "SHA512", "ECDSA");
        addEntries(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_1, AOSignConstants.DIGEST_ALGORITHM_SHA1, "RSA");
        addEntries(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_256, "SHA256", "RSA");
        addEntries(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_1, AOSignConstants.DIGEST_ALGORITHM_SHA1, "RSAandMGF1");
        addEntries(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_256, "SHA256", "RSAandMGF1");
        encryptionAlgs.put(X9ObjectIdentifiers.id_dsa.getId(), "DSA");
        encryptionAlgs.put(PKCSObjectIdentifiers.rsaEncryption.getId(), "RSA");
        encryptionAlgs.put(TeleTrusTObjectIdentifiers.teleTrusTRSAsignatureAlgorithm, "RSA");
        encryptionAlgs.put(X509ObjectIdentifiers.id_ea_rsa.getId(), "RSA");
        encryptionAlgs.put(CMSSignedDataGenerator.ENCRYPTION_RSA_PSS, "RSAandMGF1");
        encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3410_94.getId(), "GOST3410");
        encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3410_2001.getId(), "ECGOST3410");
        encryptionAlgs.put("1.3.6.1.4.1.5849.1.6.2", "ECGOST3410");
        encryptionAlgs.put("1.3.6.1.4.1.5849.1.1.5", "GOST3410");
        encryptionAlgs.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, "ECGOST3410-2012-256");
        encryptionAlgs.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, "ECGOST3410-2012-512");
        encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001.getId(), "ECGOST3410");
        encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94.getId(), "GOST3410");
        encryptionAlgs.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, "ECGOST3410-2012-256");
        encryptionAlgs.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, "ECGOST3410-2012-512");
        digestAlgs.put(PKCSObjectIdentifiers.md2.getId(), MessageDigestAlgorithms.MD2);
        digestAlgs.put(PKCSObjectIdentifiers.md4.getId(), "MD4");
        digestAlgs.put(PKCSObjectIdentifiers.md5.getId(), MessageDigestAlgorithms.MD5);
        digestAlgs.put(OIWObjectIdentifiers.idSHA1.getId(), AOSignConstants.DIGEST_ALGORITHM_SHA1);
        digestAlgs.put(NISTObjectIdentifiers.id_sha224.getId(), "SHA224");
        digestAlgs.put(NISTObjectIdentifiers.id_sha256.getId(), "SHA256");
        digestAlgs.put(NISTObjectIdentifiers.id_sha384.getId(), AOSignConstants.DIGEST_ALGORITHM_SHA384);
        digestAlgs.put(NISTObjectIdentifiers.id_sha512.getId(), "SHA512");
        digestAlgs.put(TeleTrusTObjectIdentifiers.ripemd128.getId(), "RIPEMD128");
        digestAlgs.put(TeleTrusTObjectIdentifiers.ripemd160.getId(), "RIPEMD160");
        digestAlgs.put(TeleTrusTObjectIdentifiers.ripemd256.getId(), "RIPEMD256");
        digestAlgs.put(CryptoProObjectIdentifiers.gostR3411.getId(), "GOST3411");
        digestAlgs.put("1.3.6.1.4.1.5849.1.2.1", "GOST3411");
        digestAlgs.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256, "GOST3411-2012-256");
        digestAlgs.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512, "GOST3411-2012-512");
        digestAliases.put(AOSignConstants.DIGEST_ALGORITHM_SHA1, new String[]{"SHA-1"});
        digestAliases.put("SHA224", new String[]{"SHA-224"});
        digestAliases.put("SHA256", new String[]{"SHA-256"});
        digestAliases.put(AOSignConstants.DIGEST_ALGORITHM_SHA384, new String[]{"SHA-384"});
        digestAliases.put("SHA512", new String[]{"SHA-512"});
    }

    private static void addEntries(ASN1ObjectIdentifier alias, String digest, String encryption) {
        digestAlgs.put(alias.getId(), digest);
        encryptionAlgs.put(alias.getId(), encryption);
    }

    String getEncryptionAlgName(String encryptionAlgOID) {
        String algName = (String) encryptionAlgs.get(encryptionAlgOID);
        if (algName != null) {
            return algName;
        }
        return encryptionAlgOID;
    }

    AlgorithmIdentifier fixAlgID(AlgorithmIdentifier algId) {
        if (algId.getParameters() == null) {
            return new AlgorithmIdentifier(algId.getAlgorithm(), DERNull.INSTANCE);
        }
        return algId;
    }

    void setSigningEncryptionAlgorithmMapping(ASN1ObjectIdentifier oid, String algorithmName) {
        encryptionAlgs.put(oid.getId(), algorithmName);
    }

    void setSigningDigestAlgorithmMapping(ASN1ObjectIdentifier oid, String algorithmName) {
        digestAlgs.put(oid.getId(), algorithmName);
    }

    Store getCertificates(ASN1Set certSet) {
        if (certSet != null) {
            List certList = new ArrayList(certSet.size());
            Enumeration en = certSet.getObjects();
            while (en.hasMoreElements()) {
                ASN1Primitive obj = ((ASN1Encodable) en.nextElement()).toASN1Primitive();
                if (obj instanceof ASN1Sequence) {
                    certList.add(new X509CertificateHolder(Certificate.getInstance(obj)));
                }
            }
            return new CollectionStore(certList);
        }
        return new CollectionStore(new ArrayList());
    }

    Store getAttributeCertificates(ASN1Set certSet) {
        if (certSet != null) {
            List certList = new ArrayList(certSet.size());
            Enumeration en = certSet.getObjects();
            while (en.hasMoreElements()) {
                ASN1Primitive obj = ((ASN1Encodable) en.nextElement()).toASN1Primitive();
                if (obj instanceof ASN1TaggedObject) {
                    certList.add(new X509AttributeCertificateHolder(AttributeCertificate.getInstance(((ASN1TaggedObject) obj).getObject())));
                }
            }
            return new CollectionStore(certList);
        }
        return new CollectionStore(new ArrayList());
    }

    Store getCRLs(ASN1Set crlSet) {
        if (crlSet != null) {
            List crlList = new ArrayList(crlSet.size());
            Enumeration en = crlSet.getObjects();
            while (en.hasMoreElements()) {
                ASN1Primitive obj = ((ASN1Encodable) en.nextElement()).toASN1Primitive();
                if (obj instanceof ASN1Sequence) {
                    crlList.add(new X509CRLHolder(CertificateList.getInstance(obj)));
                }
            }
            return new CollectionStore(crlList);
        }
        return new CollectionStore(new ArrayList());
    }

    Store getOtherRevocationInfo(ASN1ObjectIdentifier otherRevocationInfoFormat, ASN1Set crlSet) {
        if (crlSet != null) {
            List crlList = new ArrayList(crlSet.size());
            Enumeration en = crlSet.getObjects();
            while (en.hasMoreElements()) {
                ASN1Primitive obj = ((ASN1Encodable) en.nextElement()).toASN1Primitive();
                if (obj instanceof ASN1TaggedObject) {
                    ASN1TaggedObject tObj = ASN1TaggedObject.getInstance(obj);
                    if (tObj.getTagNo() == 1) {
                        OtherRevocationInfoFormat other = OtherRevocationInfoFormat.getInstance(tObj, false);
                        if (otherRevocationInfoFormat.equals(other.getInfoFormat())) {
                            crlList.add(other.getInfo());
                        }
                    }
                }
            }
            return new CollectionStore(crlList);
        }
        return new CollectionStore(new ArrayList());
    }
}
