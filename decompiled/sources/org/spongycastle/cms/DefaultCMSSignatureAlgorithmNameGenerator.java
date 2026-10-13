package org.spongycastle.cms;

import es.gob.afirma.core.signers.AOSignConstants;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.bsi.BSIObjectIdentifiers;
import org.spongycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.spongycastle.asn1.eac.EACObjectIdentifiers;
import org.spongycastle.asn1.gm.GMObjectIdentifiers;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
import org.spongycastle.asn1.oiw.OIWObjectIdentifiers;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.spongycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.X509ObjectIdentifiers;
import org.spongycastle.asn1.x9.X9ObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/DefaultCMSSignatureAlgorithmNameGenerator.class */
public class DefaultCMSSignatureAlgorithmNameGenerator implements CMSSignatureAlgorithmNameGenerator {
    private final Map encryptionAlgs = new HashMap();
    private final Map digestAlgs = new HashMap();

    private void addEntries(ASN1ObjectIdentifier alias, String digest, String encryption) {
        this.digestAlgs.put(alias, digest);
        this.encryptionAlgs.put(alias, encryption);
    }

    public DefaultCMSSignatureAlgorithmNameGenerator() {
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
        addEntries(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128, "RIPEMD128", "RSA");
        addEntries(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160, "RIPEMD160", "RSA");
        addEntries(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256, "RIPEMD256", "RSA");
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
        addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA1, AOSignConstants.DIGEST_ALGORITHM_SHA1, "PLAIN-ECDSA");
        addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA224, "SHA224", "PLAIN-ECDSA");
        addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA256, "SHA256", "PLAIN-ECDSA");
        addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA384, AOSignConstants.DIGEST_ALGORITHM_SHA384, "PLAIN-ECDSA");
        addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA512, "SHA512", "PLAIN-ECDSA");
        addEntries(BSIObjectIdentifiers.ecdsa_plain_RIPEMD160, "RIPEMD160", "PLAIN-ECDSA");
        this.encryptionAlgs.put(X9ObjectIdentifiers.id_dsa, "DSA");
        this.encryptionAlgs.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
        this.encryptionAlgs.put(TeleTrusTObjectIdentifiers.teleTrusTRSAsignatureAlgorithm, "RSA");
        this.encryptionAlgs.put(X509ObjectIdentifiers.id_ea_rsa, "RSA");
        this.encryptionAlgs.put(PKCSObjectIdentifiers.id_RSASSA_PSS, "RSAandMGF1");
        this.encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3410_94, "GOST3410");
        this.encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410");
        this.encryptionAlgs.put(new ASN1ObjectIdentifier("1.3.6.1.4.1.5849.1.6.2"), "ECGOST3410");
        this.encryptionAlgs.put(new ASN1ObjectIdentifier("1.3.6.1.4.1.5849.1.1.5"), "GOST3410");
        this.encryptionAlgs.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, "ECGOST3410-2012-256");
        this.encryptionAlgs.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, "ECGOST3410-2012-512");
        this.encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, "ECGOST3410");
        this.encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, "GOST3410");
        this.encryptionAlgs.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, "ECGOST3410-2012-256");
        this.encryptionAlgs.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, "ECGOST3410-2012-512");
        this.encryptionAlgs.put(GMObjectIdentifiers.sm2sign_with_sm3, "SM2");
        this.digestAlgs.put(PKCSObjectIdentifiers.md2, MessageDigestAlgorithms.MD2);
        this.digestAlgs.put(PKCSObjectIdentifiers.md4, "MD4");
        this.digestAlgs.put(PKCSObjectIdentifiers.md5, MessageDigestAlgorithms.MD5);
        this.digestAlgs.put(OIWObjectIdentifiers.idSHA1, AOSignConstants.DIGEST_ALGORITHM_SHA1);
        this.digestAlgs.put(NISTObjectIdentifiers.id_sha224, "SHA224");
        this.digestAlgs.put(NISTObjectIdentifiers.id_sha256, "SHA256");
        this.digestAlgs.put(NISTObjectIdentifiers.id_sha384, AOSignConstants.DIGEST_ALGORITHM_SHA384);
        this.digestAlgs.put(NISTObjectIdentifiers.id_sha512, "SHA512");
        this.digestAlgs.put(TeleTrusTObjectIdentifiers.ripemd128, "RIPEMD128");
        this.digestAlgs.put(TeleTrusTObjectIdentifiers.ripemd160, "RIPEMD160");
        this.digestAlgs.put(TeleTrusTObjectIdentifiers.ripemd256, "RIPEMD256");
        this.digestAlgs.put(CryptoProObjectIdentifiers.gostR3411, "GOST3411");
        this.digestAlgs.put(new ASN1ObjectIdentifier("1.3.6.1.4.1.5849.1.2.1"), "GOST3411");
        this.digestAlgs.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256, "GOST3411-2012-256");
        this.digestAlgs.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512, "GOST3411-2012-512");
        this.digestAlgs.put(GMObjectIdentifiers.sm3, "SM3");
    }

    private String getDigestAlgName(ASN1ObjectIdentifier digestAlgOID) {
        String algName = (String) this.digestAlgs.get(digestAlgOID);
        if (algName != null) {
            return algName;
        }
        return digestAlgOID.getId();
    }

    private String getEncryptionAlgName(ASN1ObjectIdentifier encryptionAlgOID) {
        String algName = (String) this.encryptionAlgs.get(encryptionAlgOID);
        if (algName != null) {
            return algName;
        }
        return encryptionAlgOID.getId();
    }

    protected void setSigningEncryptionAlgorithmMapping(ASN1ObjectIdentifier oid, String algorithmName) {
        this.encryptionAlgs.put(oid, algorithmName);
    }

    protected void setSigningDigestAlgorithmMapping(ASN1ObjectIdentifier oid, String algorithmName) {
        this.digestAlgs.put(oid, algorithmName);
    }

    @Override // org.spongycastle.cms.CMSSignatureAlgorithmNameGenerator
    public String getSignatureName(AlgorithmIdentifier digestAlg, AlgorithmIdentifier encryptionAlg) {
        String digestName = getDigestAlgName(encryptionAlg.getAlgorithm());
        if (!digestName.equals(encryptionAlg.getAlgorithm().getId())) {
            return digestName + "with" + getEncryptionAlgName(encryptionAlg.getAlgorithm());
        }
        return getDigestAlgName(digestAlg.getAlgorithm()) + "with" + getEncryptionAlgName(encryptionAlg.getAlgorithm());
    }
}
