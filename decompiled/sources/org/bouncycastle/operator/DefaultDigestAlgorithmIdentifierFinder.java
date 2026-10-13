package org.bouncycastle.operator;

import es.gob.afirma.core.signers.AOSignConstants;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.bsi.BSIObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/DefaultDigestAlgorithmIdentifierFinder.class */
public class DefaultDigestAlgorithmIdentifierFinder implements DigestAlgorithmIdentifierFinder {
    private static Map digestOids = new HashMap();
    private static Map digestNameToOids = new HashMap();
    private static Map digestOidToAlgIds = new HashMap();
    private static Set shake256oids = new HashSet();
    public static DigestAlgorithmIdentifierFinder INSTANCE;

    private static void addDigestAlgId(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean z) {
        digestOidToAlgIds.put(aSN1ObjectIdentifier, z ? new AlgorithmIdentifier(aSN1ObjectIdentifier, DERNull.INSTANCE) : new AlgorithmIdentifier(aSN1ObjectIdentifier));
    }

    @Override // org.bouncycastle.operator.DigestAlgorithmIdentifierFinder
    public AlgorithmIdentifier find(AlgorithmIdentifier algorithmIdentifier) {
        ASN1ObjectIdentifier algorithm = algorithmIdentifier.getAlgorithm();
        if (shake256oids.contains(algorithm)) {
            return !algorithm.equals((ASN1Primitive) EdECObjectIdentifiers.id_Ed448) ? new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256) : new AlgorithmIdentifier(NISTObjectIdentifiers.id_shake256_len, new ASN1Integer(512L));
        }
        ASN1ObjectIdentifier algorithm2 = algorithm.equals((ASN1Primitive) PKCSObjectIdentifiers.id_RSASSA_PSS) ? RSASSAPSSparams.getInstance(algorithmIdentifier.getParameters()).getHashAlgorithm().getAlgorithm() : (ASN1ObjectIdentifier) digestOids.get(algorithm);
        if (algorithm2 == null) {
            return null;
        }
        return find(algorithm2);
    }

    @Override // org.bouncycastle.operator.DigestAlgorithmIdentifierFinder
    public AlgorithmIdentifier find(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (aSN1ObjectIdentifier == null) {
            throw new NullPointerException("digest OID is null");
        }
        AlgorithmIdentifier algorithmIdentifier = (AlgorithmIdentifier) digestOidToAlgIds.get(aSN1ObjectIdentifier);
        return algorithmIdentifier == null ? new AlgorithmIdentifier(aSN1ObjectIdentifier) : algorithmIdentifier;
    }

    @Override // org.bouncycastle.operator.DigestAlgorithmIdentifierFinder
    public AlgorithmIdentifier find(String str) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier) digestNameToOids.get(str);
        if (aSN1ObjectIdentifier != null) {
            return find(aSN1ObjectIdentifier);
        }
        try {
            return find(new ASN1ObjectIdentifier(str));
        } catch (RuntimeException e) {
            return null;
        }
    }

    static {
        digestOids.put(OIWObjectIdentifiers.dsaWithSHA1, OIWObjectIdentifiers.idSHA1);
        digestOids.put(OIWObjectIdentifiers.md4WithRSAEncryption, PKCSObjectIdentifiers.md4);
        digestOids.put(OIWObjectIdentifiers.md4WithRSA, PKCSObjectIdentifiers.md4);
        digestOids.put(OIWObjectIdentifiers.md5WithRSA, PKCSObjectIdentifiers.md5);
        digestOids.put(OIWObjectIdentifiers.sha1WithRSA, OIWObjectIdentifiers.idSHA1);
        digestOids.put(PKCSObjectIdentifiers.sha224WithRSAEncryption, NISTObjectIdentifiers.id_sha224);
        digestOids.put(PKCSObjectIdentifiers.sha256WithRSAEncryption, NISTObjectIdentifiers.id_sha256);
        digestOids.put(PKCSObjectIdentifiers.sha384WithRSAEncryption, NISTObjectIdentifiers.id_sha384);
        digestOids.put(PKCSObjectIdentifiers.sha512WithRSAEncryption, NISTObjectIdentifiers.id_sha512);
        digestOids.put(PKCSObjectIdentifiers.sha512_224WithRSAEncryption, NISTObjectIdentifiers.id_sha512_224);
        digestOids.put(PKCSObjectIdentifiers.sha512_256WithRSAEncryption, NISTObjectIdentifiers.id_sha512_256);
        digestOids.put(PKCSObjectIdentifiers.md2WithRSAEncryption, PKCSObjectIdentifiers.md2);
        digestOids.put(PKCSObjectIdentifiers.md4WithRSAEncryption, PKCSObjectIdentifiers.md4);
        digestOids.put(PKCSObjectIdentifiers.md5WithRSAEncryption, PKCSObjectIdentifiers.md5);
        digestOids.put(PKCSObjectIdentifiers.sha1WithRSAEncryption, OIWObjectIdentifiers.idSHA1);
        digestOids.put(X9ObjectIdentifiers.ecdsa_with_SHA1, OIWObjectIdentifiers.idSHA1);
        digestOids.put(X9ObjectIdentifiers.ecdsa_with_SHA224, NISTObjectIdentifiers.id_sha224);
        digestOids.put(X9ObjectIdentifiers.ecdsa_with_SHA256, NISTObjectIdentifiers.id_sha256);
        digestOids.put(X9ObjectIdentifiers.ecdsa_with_SHA384, NISTObjectIdentifiers.id_sha384);
        digestOids.put(X9ObjectIdentifiers.ecdsa_with_SHA512, NISTObjectIdentifiers.id_sha512);
        digestOids.put(X9ObjectIdentifiers.id_dsa_with_sha1, OIWObjectIdentifiers.idSHA1);
        digestOids.put(BSIObjectIdentifiers.ecdsa_plain_SHA1, OIWObjectIdentifiers.idSHA1);
        digestOids.put(BSIObjectIdentifiers.ecdsa_plain_SHA224, NISTObjectIdentifiers.id_sha224);
        digestOids.put(BSIObjectIdentifiers.ecdsa_plain_SHA256, NISTObjectIdentifiers.id_sha256);
        digestOids.put(BSIObjectIdentifiers.ecdsa_plain_SHA384, NISTObjectIdentifiers.id_sha384);
        digestOids.put(BSIObjectIdentifiers.ecdsa_plain_SHA512, NISTObjectIdentifiers.id_sha512);
        digestOids.put(BSIObjectIdentifiers.ecdsa_plain_SHA3_224, NISTObjectIdentifiers.id_sha3_224);
        digestOids.put(BSIObjectIdentifiers.ecdsa_plain_SHA3_256, NISTObjectIdentifiers.id_sha3_256);
        digestOids.put(BSIObjectIdentifiers.ecdsa_plain_SHA3_384, NISTObjectIdentifiers.id_sha3_384);
        digestOids.put(BSIObjectIdentifiers.ecdsa_plain_SHA3_512, NISTObjectIdentifiers.id_sha3_512);
        digestOids.put(BSIObjectIdentifiers.ecdsa_plain_RIPEMD160, TeleTrusTObjectIdentifiers.ripemd160);
        digestOids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_1, OIWObjectIdentifiers.idSHA1);
        digestOids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_224, NISTObjectIdentifiers.id_sha224);
        digestOids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_256, NISTObjectIdentifiers.id_sha256);
        digestOids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_384, NISTObjectIdentifiers.id_sha384);
        digestOids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_512, NISTObjectIdentifiers.id_sha512);
        digestOids.put(NISTObjectIdentifiers.dsa_with_sha224, NISTObjectIdentifiers.id_sha224);
        digestOids.put(NISTObjectIdentifiers.dsa_with_sha256, NISTObjectIdentifiers.id_sha256);
        digestOids.put(NISTObjectIdentifiers.dsa_with_sha384, NISTObjectIdentifiers.id_sha384);
        digestOids.put(NISTObjectIdentifiers.dsa_with_sha512, NISTObjectIdentifiers.id_sha512);
        digestOids.put(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_224, NISTObjectIdentifiers.id_sha3_224);
        digestOids.put(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_256, NISTObjectIdentifiers.id_sha3_256);
        digestOids.put(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_384, NISTObjectIdentifiers.id_sha3_384);
        digestOids.put(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512, NISTObjectIdentifiers.id_sha3_512);
        digestOids.put(NISTObjectIdentifiers.id_dsa_with_sha3_224, NISTObjectIdentifiers.id_sha3_224);
        digestOids.put(NISTObjectIdentifiers.id_dsa_with_sha3_256, NISTObjectIdentifiers.id_sha3_256);
        digestOids.put(NISTObjectIdentifiers.id_dsa_with_sha3_384, NISTObjectIdentifiers.id_sha3_384);
        digestOids.put(NISTObjectIdentifiers.id_dsa_with_sha3_512, NISTObjectIdentifiers.id_sha3_512);
        digestOids.put(NISTObjectIdentifiers.id_ecdsa_with_sha3_224, NISTObjectIdentifiers.id_sha3_224);
        digestOids.put(NISTObjectIdentifiers.id_ecdsa_with_sha3_256, NISTObjectIdentifiers.id_sha3_256);
        digestOids.put(NISTObjectIdentifiers.id_ecdsa_with_sha3_384, NISTObjectIdentifiers.id_sha3_384);
        digestOids.put(NISTObjectIdentifiers.id_ecdsa_with_sha3_512, NISTObjectIdentifiers.id_sha3_512);
        digestOids.put(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128, TeleTrusTObjectIdentifiers.ripemd128);
        digestOids.put(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160, TeleTrusTObjectIdentifiers.ripemd160);
        digestOids.put(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256, TeleTrusTObjectIdentifiers.ripemd256);
        digestOids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, CryptoProObjectIdentifiers.gostR3411);
        digestOids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, CryptoProObjectIdentifiers.gostR3411);
        digestOids.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256);
        digestOids.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512);
        digestOids.put(BCObjectIdentifiers.sphincs256_with_SHA3_512, NISTObjectIdentifiers.id_sha3_512);
        digestOids.put(BCObjectIdentifiers.sphincs256_with_SHA512, NISTObjectIdentifiers.id_sha512);
        digestOids.put(BCObjectIdentifiers.sphincsPlus_sha2_128s_r3, NISTObjectIdentifiers.id_sha256);
        digestOids.put(BCObjectIdentifiers.sphincsPlus_sha2_128f_r3, NISTObjectIdentifiers.id_sha256);
        digestOids.put(BCObjectIdentifiers.sphincsPlus_shake_128s_r3, NISTObjectIdentifiers.id_shake256);
        digestOids.put(BCObjectIdentifiers.sphincsPlus_shake_128f_r3, NISTObjectIdentifiers.id_shake256);
        digestOids.put(BCObjectIdentifiers.sphincsPlus_sha2_192s_r3, NISTObjectIdentifiers.id_sha256);
        digestOids.put(BCObjectIdentifiers.sphincsPlus_sha2_192f_r3, NISTObjectIdentifiers.id_sha256);
        digestOids.put(BCObjectIdentifiers.sphincsPlus_shake_192s_r3, NISTObjectIdentifiers.id_shake256);
        digestOids.put(BCObjectIdentifiers.sphincsPlus_shake_192f_r3, NISTObjectIdentifiers.id_shake256);
        digestOids.put(BCObjectIdentifiers.sphincsPlus_sha2_256s_r3, NISTObjectIdentifiers.id_sha256);
        digestOids.put(BCObjectIdentifiers.sphincsPlus_sha2_256f_r3, NISTObjectIdentifiers.id_sha256);
        digestOids.put(BCObjectIdentifiers.sphincsPlus_shake_256s_r3, NISTObjectIdentifiers.id_shake256);
        digestOids.put(BCObjectIdentifiers.sphincsPlus_shake_256f_r3, NISTObjectIdentifiers.id_shake256);
        digestOids.put(BCObjectIdentifiers.sphincsPlus_sha2_128s_r3_simple, NISTObjectIdentifiers.id_sha256);
        digestOids.put(BCObjectIdentifiers.sphincsPlus_sha2_128f_r3_simple, NISTObjectIdentifiers.id_sha256);
        digestOids.put(BCObjectIdentifiers.sphincsPlus_shake_128s_r3_simple, NISTObjectIdentifiers.id_shake256);
        digestOids.put(BCObjectIdentifiers.sphincsPlus_shake_128f_r3_simple, NISTObjectIdentifiers.id_shake256);
        digestOids.put(BCObjectIdentifiers.sphincsPlus_sha2_192s_r3_simple, NISTObjectIdentifiers.id_sha256);
        digestOids.put(BCObjectIdentifiers.sphincsPlus_sha2_192f_r3_simple, NISTObjectIdentifiers.id_sha256);
        digestOids.put(BCObjectIdentifiers.sphincsPlus_shake_192s_r3_simple, NISTObjectIdentifiers.id_shake256);
        digestOids.put(BCObjectIdentifiers.sphincsPlus_shake_192f_r3_simple, NISTObjectIdentifiers.id_shake256);
        digestOids.put(BCObjectIdentifiers.sphincsPlus_sha2_256s_r3_simple, NISTObjectIdentifiers.id_sha256);
        digestOids.put(BCObjectIdentifiers.sphincsPlus_sha2_256f_r3_simple, NISTObjectIdentifiers.id_sha256);
        digestOids.put(BCObjectIdentifiers.sphincsPlus_shake_256s_r3_simple, NISTObjectIdentifiers.id_shake256);
        digestOids.put(BCObjectIdentifiers.sphincsPlus_shake_256f_r3_simple, NISTObjectIdentifiers.id_shake256);
        digestOids.put(NISTObjectIdentifiers.id_slh_dsa_sha2_128s, NISTObjectIdentifiers.id_sha256);
        digestOids.put(NISTObjectIdentifiers.id_slh_dsa_sha2_128f, NISTObjectIdentifiers.id_sha256);
        digestOids.put(NISTObjectIdentifiers.id_slh_dsa_sha2_192s, NISTObjectIdentifiers.id_sha512);
        digestOids.put(NISTObjectIdentifiers.id_slh_dsa_sha2_192f, NISTObjectIdentifiers.id_sha512);
        digestOids.put(NISTObjectIdentifiers.id_slh_dsa_sha2_256s, NISTObjectIdentifiers.id_sha512);
        digestOids.put(NISTObjectIdentifiers.id_slh_dsa_sha2_256f, NISTObjectIdentifiers.id_sha512);
        digestOids.put(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_128s_with_sha256, NISTObjectIdentifiers.id_sha256);
        digestOids.put(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_128f_with_sha256, NISTObjectIdentifiers.id_sha256);
        digestOids.put(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_192s_with_sha512, NISTObjectIdentifiers.id_sha512);
        digestOids.put(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_192f_with_sha512, NISTObjectIdentifiers.id_sha512);
        digestOids.put(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_256s_with_sha512, NISTObjectIdentifiers.id_sha512);
        digestOids.put(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_256f_with_sha512, NISTObjectIdentifiers.id_sha512);
        digestOids.put(NISTObjectIdentifiers.id_slh_dsa_shake_128s, NISTObjectIdentifiers.id_shake128);
        digestOids.put(NISTObjectIdentifiers.id_slh_dsa_shake_128f, NISTObjectIdentifiers.id_shake128);
        digestOids.put(NISTObjectIdentifiers.id_slh_dsa_shake_192s, NISTObjectIdentifiers.id_shake256);
        digestOids.put(NISTObjectIdentifiers.id_slh_dsa_shake_192f, NISTObjectIdentifiers.id_shake256);
        digestOids.put(NISTObjectIdentifiers.id_slh_dsa_shake_256s, NISTObjectIdentifiers.id_shake256);
        digestOids.put(NISTObjectIdentifiers.id_slh_dsa_shake_256f, NISTObjectIdentifiers.id_shake256);
        digestOids.put(NISTObjectIdentifiers.id_hash_slh_dsa_shake_128s_with_shake128, NISTObjectIdentifiers.id_shake128);
        digestOids.put(NISTObjectIdentifiers.id_hash_slh_dsa_shake_128f_with_shake128, NISTObjectIdentifiers.id_shake128);
        digestOids.put(NISTObjectIdentifiers.id_hash_slh_dsa_shake_192s_with_shake256, NISTObjectIdentifiers.id_shake256);
        digestOids.put(NISTObjectIdentifiers.id_hash_slh_dsa_shake_192f_with_shake256, NISTObjectIdentifiers.id_shake256);
        digestOids.put(NISTObjectIdentifiers.id_hash_slh_dsa_shake_256s_with_shake256, NISTObjectIdentifiers.id_shake256);
        digestOids.put(NISTObjectIdentifiers.id_hash_slh_dsa_shake_256f_with_shake256, NISTObjectIdentifiers.id_shake256);
        digestOids.put(NISTObjectIdentifiers.id_ml_dsa_44, NISTObjectIdentifiers.id_shake256);
        digestOids.put(NISTObjectIdentifiers.id_ml_dsa_65, NISTObjectIdentifiers.id_shake256);
        digestOids.put(NISTObjectIdentifiers.id_ml_dsa_87, NISTObjectIdentifiers.id_shake256);
        digestOids.put(NISTObjectIdentifiers.id_hash_ml_dsa_44_with_sha512, NISTObjectIdentifiers.id_sha512);
        digestOids.put(NISTObjectIdentifiers.id_hash_ml_dsa_65_with_sha512, NISTObjectIdentifiers.id_sha512);
        digestOids.put(NISTObjectIdentifiers.id_hash_ml_dsa_87_with_sha512, NISTObjectIdentifiers.id_sha512);
        digestOids.put(BCObjectIdentifiers.falcon, NISTObjectIdentifiers.id_shake256);
        digestOids.put(BCObjectIdentifiers.falcon_512, NISTObjectIdentifiers.id_shake256);
        digestOids.put(BCObjectIdentifiers.falcon_1024, NISTObjectIdentifiers.id_shake256);
        digestOids.put(BCObjectIdentifiers.picnic_signature, NISTObjectIdentifiers.id_shake256);
        digestOids.put(BCObjectIdentifiers.picnic_with_sha512, NISTObjectIdentifiers.id_sha512);
        digestOids.put(BCObjectIdentifiers.picnic_with_sha3_512, NISTObjectIdentifiers.id_sha3_512);
        digestOids.put(BCObjectIdentifiers.picnic_with_shake256, NISTObjectIdentifiers.id_shake256);
        digestOids.put(GMObjectIdentifiers.sm2sign_with_sha256, NISTObjectIdentifiers.id_sha256);
        digestOids.put(GMObjectIdentifiers.sm2sign_with_sm3, GMObjectIdentifiers.sm3);
        digestOids.put(X509ObjectIdentifiers.id_rsassa_pss_shake128, NISTObjectIdentifiers.id_shake128);
        digestOids.put(X509ObjectIdentifiers.id_rsassa_pss_shake256, NISTObjectIdentifiers.id_shake256);
        digestOids.put(X509ObjectIdentifiers.id_ecdsa_with_shake128, NISTObjectIdentifiers.id_shake128);
        digestOids.put(X509ObjectIdentifiers.id_ecdsa_with_shake256, NISTObjectIdentifiers.id_shake256);
        digestOids.put(EdECObjectIdentifiers.id_Ed25519, NISTObjectIdentifiers.id_sha512);
        digestNameToOids.put("SHA-1", OIWObjectIdentifiers.idSHA1);
        digestNameToOids.put("SHA-224", NISTObjectIdentifiers.id_sha224);
        digestNameToOids.put("SHA-256", NISTObjectIdentifiers.id_sha256);
        digestNameToOids.put("SHA-384", NISTObjectIdentifiers.id_sha384);
        digestNameToOids.put("SHA-512", NISTObjectIdentifiers.id_sha512);
        digestNameToOids.put("SHA-512-224", NISTObjectIdentifiers.id_sha512_224);
        digestNameToOids.put("SHA-512-256", NISTObjectIdentifiers.id_sha512_256);
        digestNameToOids.put(AOSignConstants.DIGEST_ALGORITHM_SHA1, OIWObjectIdentifiers.idSHA1);
        digestNameToOids.put("SHA224", NISTObjectIdentifiers.id_sha224);
        digestNameToOids.put("SHA256", NISTObjectIdentifiers.id_sha256);
        digestNameToOids.put(AOSignConstants.DIGEST_ALGORITHM_SHA384, NISTObjectIdentifiers.id_sha384);
        digestNameToOids.put("SHA512", NISTObjectIdentifiers.id_sha512);
        digestNameToOids.put("SHA512-224", NISTObjectIdentifiers.id_sha512_224);
        digestNameToOids.put("SHA512-256", NISTObjectIdentifiers.id_sha512_256);
        digestNameToOids.put(MessageDigestAlgorithms.SHA3_224, NISTObjectIdentifiers.id_sha3_224);
        digestNameToOids.put("SHA3-256", NISTObjectIdentifiers.id_sha3_256);
        digestNameToOids.put(MessageDigestAlgorithms.SHA3_384, NISTObjectIdentifiers.id_sha3_384);
        digestNameToOids.put(MessageDigestAlgorithms.SHA3_512, NISTObjectIdentifiers.id_sha3_512);
        digestNameToOids.put("SHAKE128", NISTObjectIdentifiers.id_shake128);
        digestNameToOids.put("SHAKE256", NISTObjectIdentifiers.id_shake256);
        digestNameToOids.put("SHAKE-128", NISTObjectIdentifiers.id_shake128);
        digestNameToOids.put("SHAKE-256", NISTObjectIdentifiers.id_shake256);
        digestNameToOids.put("GOST3411", CryptoProObjectIdentifiers.gostR3411);
        digestNameToOids.put("GOST3411-2012-256", RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256);
        digestNameToOids.put("GOST3411-2012-512", RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512);
        digestNameToOids.put(MessageDigestAlgorithms.MD2, PKCSObjectIdentifiers.md2);
        digestNameToOids.put("MD4", PKCSObjectIdentifiers.md4);
        digestNameToOids.put(MessageDigestAlgorithms.MD5, PKCSObjectIdentifiers.md5);
        digestNameToOids.put("RIPEMD128", TeleTrusTObjectIdentifiers.ripemd128);
        digestNameToOids.put("RIPEMD160", TeleTrusTObjectIdentifiers.ripemd160);
        digestNameToOids.put("RIPEMD256", TeleTrusTObjectIdentifiers.ripemd256);
        digestNameToOids.put("SM3", GMObjectIdentifiers.sm3);
        addDigestAlgId(OIWObjectIdentifiers.idSHA1, true);
        addDigestAlgId(NISTObjectIdentifiers.id_sha224, false);
        addDigestAlgId(NISTObjectIdentifiers.id_sha256, false);
        addDigestAlgId(NISTObjectIdentifiers.id_sha384, false);
        addDigestAlgId(NISTObjectIdentifiers.id_sha512, false);
        addDigestAlgId(NISTObjectIdentifiers.id_sha512_224, false);
        addDigestAlgId(NISTObjectIdentifiers.id_sha512_256, false);
        addDigestAlgId(NISTObjectIdentifiers.id_sha3_224, false);
        addDigestAlgId(NISTObjectIdentifiers.id_sha3_256, false);
        addDigestAlgId(NISTObjectIdentifiers.id_sha3_384, false);
        addDigestAlgId(NISTObjectIdentifiers.id_sha3_512, false);
        addDigestAlgId(NISTObjectIdentifiers.id_shake128, false);
        addDigestAlgId(NISTObjectIdentifiers.id_shake256, false);
        addDigestAlgId(CryptoProObjectIdentifiers.gostR3411, true);
        addDigestAlgId(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256, false);
        addDigestAlgId(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512, false);
        addDigestAlgId(PKCSObjectIdentifiers.md2, true);
        addDigestAlgId(PKCSObjectIdentifiers.md4, true);
        addDigestAlgId(PKCSObjectIdentifiers.md5, true);
        addDigestAlgId(TeleTrusTObjectIdentifiers.ripemd128, true);
        addDigestAlgId(TeleTrusTObjectIdentifiers.ripemd160, true);
        addDigestAlgId(TeleTrusTObjectIdentifiers.ripemd256, true);
        shake256oids.add(EdECObjectIdentifiers.id_Ed448);
        shake256oids.add(BCObjectIdentifiers.dilithium2);
        shake256oids.add(BCObjectIdentifiers.dilithium3);
        shake256oids.add(BCObjectIdentifiers.dilithium5);
        shake256oids.add(BCObjectIdentifiers.dilithium2_aes);
        shake256oids.add(BCObjectIdentifiers.dilithium3_aes);
        shake256oids.add(BCObjectIdentifiers.dilithium5_aes);
        shake256oids.add(BCObjectIdentifiers.falcon_512);
        shake256oids.add(BCObjectIdentifiers.falcon_1024);
        INSTANCE = new DefaultDigestAlgorithmIdentifierFinder();
    }
}
