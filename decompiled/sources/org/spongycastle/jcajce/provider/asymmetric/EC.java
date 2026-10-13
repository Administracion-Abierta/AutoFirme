package org.spongycastle.jcajce.provider.asymmetric;

import es.gob.afirma.core.signers.AOSignConstants;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.spongycastle.asn1.bsi.BSIObjectIdentifiers;
import org.spongycastle.asn1.eac.EACObjectIdentifiers;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
import org.spongycastle.asn1.sec.SECObjectIdentifiers;
import org.spongycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.spongycastle.asn1.x9.X9ObjectIdentifiers;
import org.spongycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi;
import org.spongycastle.jcajce.provider.config.ConfigurableProvider;
import org.spongycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.spongycastle.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/EC.class */
public class EC {
    private static final String PREFIX = "org.spongycastle.jcajce.provider.asymmetric.ec.";
    private static final Map<String, String> generalEcAttributes = new HashMap();

    static {
        generalEcAttributes.put("SupportedKeyClasses", "java.security.interfaces.ECPublicKey|java.security.interfaces.ECPrivateKey");
        generalEcAttributes.put("SupportedKeyFormats", "PKCS#8|X.509");
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/EC$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.spongycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("AlgorithmParameters.EC", "org.spongycastle.jcajce.provider.asymmetric.ec.AlgorithmParametersSpi");
            provider.addAttributes("KeyAgreement.ECDH", EC.generalEcAttributes);
            provider.addAlgorithm("KeyAgreement.ECDH", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DH");
            provider.addAttributes("KeyAgreement.ECDHC", EC.generalEcAttributes);
            provider.addAlgorithm("KeyAgreement.ECDHC", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHC");
            provider.addAttributes("KeyAgreement.ECCDH", EC.generalEcAttributes);
            provider.addAlgorithm("KeyAgreement.ECCDH", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHC");
            provider.addAlgorithm("KeyAgreement." + X9ObjectIdentifiers.dhSinglePass_stdDH_sha1kdf_scheme, "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHwithSHA1KDFAndSharedInfo");
            provider.addAlgorithm("KeyAgreement." + X9ObjectIdentifiers.dhSinglePass_cofactorDH_sha1kdf_scheme, "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$CDHwithSHA1KDFAndSharedInfo");
            provider.addAlgorithm("KeyAgreement." + SECObjectIdentifiers.dhSinglePass_stdDH_sha224kdf_scheme, "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHwithSHA224KDFAndSharedInfo");
            provider.addAlgorithm("KeyAgreement." + SECObjectIdentifiers.dhSinglePass_cofactorDH_sha224kdf_scheme, "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$CDHwithSHA224KDFAndSharedInfo");
            provider.addAlgorithm("KeyAgreement." + SECObjectIdentifiers.dhSinglePass_stdDH_sha256kdf_scheme, "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHwithSHA256KDFAndSharedInfo");
            provider.addAlgorithm("KeyAgreement." + SECObjectIdentifiers.dhSinglePass_cofactorDH_sha256kdf_scheme, "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$CDHwithSHA256KDFAndSharedInfo");
            provider.addAlgorithm("KeyAgreement." + SECObjectIdentifiers.dhSinglePass_stdDH_sha384kdf_scheme, "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHwithSHA384KDFAndSharedInfo");
            provider.addAlgorithm("KeyAgreement." + SECObjectIdentifiers.dhSinglePass_cofactorDH_sha384kdf_scheme, "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$CDHwithSHA384KDFAndSharedInfo");
            provider.addAlgorithm("KeyAgreement." + SECObjectIdentifiers.dhSinglePass_stdDH_sha512kdf_scheme, "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHwithSHA512KDFAndSharedInfo");
            provider.addAlgorithm("KeyAgreement." + SECObjectIdentifiers.dhSinglePass_cofactorDH_sha512kdf_scheme, "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$CDHwithSHA512KDFAndSharedInfo");
            provider.addAlgorithm("KeyAgreement.ECDHWITHSHA1KDF", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHwithSHA1KDF");
            provider.addAlgorithm("KeyAgreement.ECCDHWITHSHA1CKDF", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHwithSHA1CKDF");
            provider.addAlgorithm("KeyAgreement.ECCDHWITHSHA256CKDF", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHwithSHA256CKDF");
            provider.addAlgorithm("KeyAgreement.ECCDHWITHSHA384CKDF", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHwithSHA384CKDF");
            provider.addAlgorithm("KeyAgreement.ECCDHWITHSHA512CKDF", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHwithSHA512CKDF");
            registerOid(provider, X9ObjectIdentifiers.id_ecPublicKey, "EC", new KeyFactorySpi.EC());
            registerOid(provider, X9ObjectIdentifiers.dhSinglePass_cofactorDH_sha1kdf_scheme, "EC", new KeyFactorySpi.EC());
            registerOid(provider, X9ObjectIdentifiers.mqvSinglePass_sha1kdf_scheme, "ECMQV", new KeyFactorySpi.ECMQV());
            registerOid(provider, SECObjectIdentifiers.dhSinglePass_stdDH_sha224kdf_scheme, "EC", new KeyFactorySpi.EC());
            registerOid(provider, SECObjectIdentifiers.dhSinglePass_cofactorDH_sha224kdf_scheme, "EC", new KeyFactorySpi.EC());
            registerOid(provider, SECObjectIdentifiers.dhSinglePass_stdDH_sha256kdf_scheme, "EC", new KeyFactorySpi.EC());
            registerOid(provider, SECObjectIdentifiers.dhSinglePass_cofactorDH_sha256kdf_scheme, "EC", new KeyFactorySpi.EC());
            registerOid(provider, SECObjectIdentifiers.dhSinglePass_stdDH_sha384kdf_scheme, "EC", new KeyFactorySpi.EC());
            registerOid(provider, SECObjectIdentifiers.dhSinglePass_cofactorDH_sha384kdf_scheme, "EC", new KeyFactorySpi.EC());
            registerOid(provider, SECObjectIdentifiers.dhSinglePass_stdDH_sha512kdf_scheme, "EC", new KeyFactorySpi.EC());
            registerOid(provider, SECObjectIdentifiers.dhSinglePass_cofactorDH_sha512kdf_scheme, "EC", new KeyFactorySpi.EC());
            registerOidAlgorithmParameters(provider, X9ObjectIdentifiers.id_ecPublicKey, "EC");
            registerOidAlgorithmParameters(provider, X9ObjectIdentifiers.dhSinglePass_stdDH_sha1kdf_scheme, "EC");
            registerOidAlgorithmParameters(provider, X9ObjectIdentifiers.dhSinglePass_cofactorDH_sha1kdf_scheme, "EC");
            registerOidAlgorithmParameters(provider, SECObjectIdentifiers.dhSinglePass_stdDH_sha224kdf_scheme, "EC");
            registerOidAlgorithmParameters(provider, SECObjectIdentifiers.dhSinglePass_cofactorDH_sha224kdf_scheme, "EC");
            registerOidAlgorithmParameters(provider, SECObjectIdentifiers.dhSinglePass_stdDH_sha256kdf_scheme, "EC");
            registerOidAlgorithmParameters(provider, SECObjectIdentifiers.dhSinglePass_cofactorDH_sha256kdf_scheme, "EC");
            registerOidAlgorithmParameters(provider, SECObjectIdentifiers.dhSinglePass_stdDH_sha384kdf_scheme, "EC");
            registerOidAlgorithmParameters(provider, SECObjectIdentifiers.dhSinglePass_cofactorDH_sha384kdf_scheme, "EC");
            registerOidAlgorithmParameters(provider, SECObjectIdentifiers.dhSinglePass_stdDH_sha512kdf_scheme, "EC");
            registerOidAlgorithmParameters(provider, SECObjectIdentifiers.dhSinglePass_cofactorDH_sha512kdf_scheme, "EC");
            if (!Properties.isOverrideSet("org.spongycastle.ec.disable_mqv")) {
                provider.addAlgorithm("KeyAgreement.ECMQV", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQV");
                provider.addAlgorithm("KeyAgreement.ECMQVWITHSHA1CKDF", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA1CKDF");
                provider.addAlgorithm("KeyAgreement.ECMQVWITHSHA224CKDF", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA224CKDF");
                provider.addAlgorithm("KeyAgreement.ECMQVWITHSHA256CKDF", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA256CKDF");
                provider.addAlgorithm("KeyAgreement.ECMQVWITHSHA384CKDF", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA384CKDF");
                provider.addAlgorithm("KeyAgreement.ECMQVWITHSHA512CKDF", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA512CKDF");
                provider.addAlgorithm("KeyAgreement." + X9ObjectIdentifiers.mqvSinglePass_sha1kdf_scheme, "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA1KDFAndSharedInfo");
                provider.addAlgorithm("KeyAgreement." + SECObjectIdentifiers.mqvSinglePass_sha224kdf_scheme, "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA224KDFAndSharedInfo");
                provider.addAlgorithm("KeyAgreement." + SECObjectIdentifiers.mqvSinglePass_sha256kdf_scheme, "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA256KDFAndSharedInfo");
                provider.addAlgorithm("KeyAgreement." + SECObjectIdentifiers.mqvSinglePass_sha384kdf_scheme, "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA384KDFAndSharedInfo");
                provider.addAlgorithm("KeyAgreement." + SECObjectIdentifiers.mqvSinglePass_sha512kdf_scheme, "org.spongycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA512KDFAndSharedInfo");
                registerOid(provider, X9ObjectIdentifiers.dhSinglePass_stdDH_sha1kdf_scheme, "EC", new KeyFactorySpi.EC());
                registerOidAlgorithmParameters(provider, X9ObjectIdentifiers.mqvSinglePass_sha1kdf_scheme, "EC");
                registerOid(provider, SECObjectIdentifiers.mqvSinglePass_sha224kdf_scheme, "ECMQV", new KeyFactorySpi.ECMQV());
                registerOidAlgorithmParameters(provider, SECObjectIdentifiers.mqvSinglePass_sha256kdf_scheme, "EC");
                registerOid(provider, SECObjectIdentifiers.mqvSinglePass_sha256kdf_scheme, "ECMQV", new KeyFactorySpi.ECMQV());
                registerOidAlgorithmParameters(provider, SECObjectIdentifiers.mqvSinglePass_sha224kdf_scheme, "EC");
                registerOid(provider, SECObjectIdentifiers.mqvSinglePass_sha384kdf_scheme, "ECMQV", new KeyFactorySpi.ECMQV());
                registerOidAlgorithmParameters(provider, SECObjectIdentifiers.mqvSinglePass_sha384kdf_scheme, "EC");
                registerOid(provider, SECObjectIdentifiers.mqvSinglePass_sha512kdf_scheme, "ECMQV", new KeyFactorySpi.ECMQV());
                registerOidAlgorithmParameters(provider, SECObjectIdentifiers.mqvSinglePass_sha512kdf_scheme, "EC");
                provider.addAlgorithm("KeyFactory.ECMQV", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi$ECMQV");
                provider.addAlgorithm("KeyPairGenerator.ECMQV", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi$ECMQV");
            }
            provider.addAlgorithm("KeyFactory.EC", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi$EC");
            provider.addAlgorithm("KeyFactory.ECDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi$ECDSA");
            provider.addAlgorithm("KeyFactory.ECDH", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi$ECDH");
            provider.addAlgorithm("KeyFactory.ECDHC", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi$ECDHC");
            provider.addAlgorithm("KeyPairGenerator.EC", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi$EC");
            provider.addAlgorithm("KeyPairGenerator.ECDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi$ECDSA");
            provider.addAlgorithm("KeyPairGenerator.ECDH", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi$ECDH");
            provider.addAlgorithm("KeyPairGenerator.ECDHWITHSHA1KDF", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi$ECDH");
            provider.addAlgorithm("KeyPairGenerator.ECDHC", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi$ECDHC");
            provider.addAlgorithm("KeyPairGenerator.ECIES", "org.spongycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi$ECDH");
            provider.addAlgorithm("Cipher.ECIES", "org.spongycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIES");
            provider.addAlgorithm("Cipher.ECIESwithAES-CBC", "org.spongycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithAESCBC");
            provider.addAlgorithm("Cipher.ECIESWITHAES-CBC", "org.spongycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithAESCBC");
            provider.addAlgorithm("Cipher.ECIESwithDESEDE-CBC", "org.spongycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithDESedeCBC");
            provider.addAlgorithm("Cipher.ECIESWITHDESEDE-CBC", "org.spongycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithDESedeCBC");
            provider.addAlgorithm("Signature.ECDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSA");
            provider.addAlgorithm("Signature.NONEwithECDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSAnone");
            provider.addAlgorithm("Alg.Alias.Signature.SHA1withECDSA", "ECDSA");
            provider.addAlgorithm("Alg.Alias.Signature.ECDSAwithSHA1", "ECDSA");
            provider.addAlgorithm("Alg.Alias.Signature.SHA1WITHECDSA", "ECDSA");
            provider.addAlgorithm("Alg.Alias.Signature.ECDSAWITHSHA1", "ECDSA");
            provider.addAlgorithm("Alg.Alias.Signature.SHA1WithECDSA", "ECDSA");
            provider.addAlgorithm("Alg.Alias.Signature.ECDSAWithSHA1", "ECDSA");
            provider.addAlgorithm("Alg.Alias.Signature.1.2.840.10045.4.1", "ECDSA");
            provider.addAlgorithm("Alg.Alias.Signature." + TeleTrusTObjectIdentifiers.ecSignWithSha1, "ECDSA");
            provider.addAlgorithm("Signature.ECDDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDetDSA");
            provider.addAlgorithm("Signature.SHA1WITHECDDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDetDSA");
            provider.addAlgorithm("Signature.SHA224WITHECDDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDetDSA224");
            provider.addAlgorithm("Signature.SHA256WITHECDDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDetDSA256");
            provider.addAlgorithm("Signature.SHA384WITHECDDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDetDSA384");
            provider.addAlgorithm("Signature.SHA512WITHECDDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDetDSA512");
            provider.addAlgorithm("Signature.SHA3-224WITHECDDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDetDSASha3_224");
            provider.addAlgorithm("Signature.SHA3-256WITHECDDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDetDSASha3_256");
            provider.addAlgorithm("Signature.SHA3-384WITHECDDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDetDSASha3_384");
            provider.addAlgorithm("Signature.SHA3-512WITHECDDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDetDSASha3_512");
            provider.addAlgorithm("Alg.Alias.Signature.DETECDSA", "ECDDSA");
            provider.addAlgorithm("Alg.Alias.Signature.SHA1WITHDETECDSA", "SHA1WITHECDDSA");
            provider.addAlgorithm("Alg.Alias.Signature.SHA224WITHDETECDSA", "SHA224WITHECDDSA");
            provider.addAlgorithm("Alg.Alias.Signature.SHA256WITHDETECDSA", "SHA256WITHECDDSA");
            provider.addAlgorithm("Alg.Alias.Signature.SHA384WITHDETECDSA", "SHA384WITHECDDSA");
            provider.addAlgorithm("Alg.Alias.Signature.SHA512WITHDETECDSA", "SHA512WITHECDDSA");
            addSignatureAlgorithm(provider, "SHA224", "ECDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSA224", X9ObjectIdentifiers.ecdsa_with_SHA224);
            addSignatureAlgorithm(provider, "SHA256", "ECDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSA256", X9ObjectIdentifiers.ecdsa_with_SHA256);
            addSignatureAlgorithm(provider, AOSignConstants.DIGEST_ALGORITHM_SHA384, "ECDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSA384", X9ObjectIdentifiers.ecdsa_with_SHA384);
            addSignatureAlgorithm(provider, "SHA512", "ECDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSA512", X9ObjectIdentifiers.ecdsa_with_SHA512);
            addSignatureAlgorithm(provider, MessageDigestAlgorithms.SHA3_224, "ECDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSASha3_224", NISTObjectIdentifiers.id_ecdsa_with_sha3_224);
            addSignatureAlgorithm(provider, "SHA3-256", "ECDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSASha3_256", NISTObjectIdentifiers.id_ecdsa_with_sha3_256);
            addSignatureAlgorithm(provider, MessageDigestAlgorithms.SHA3_384, "ECDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSASha3_384", NISTObjectIdentifiers.id_ecdsa_with_sha3_384);
            addSignatureAlgorithm(provider, MessageDigestAlgorithms.SHA3_512, "ECDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSASha3_512", NISTObjectIdentifiers.id_ecdsa_with_sha3_512);
            addSignatureAlgorithm(provider, "RIPEMD160", "ECDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSARipeMD160", TeleTrusTObjectIdentifiers.ecSignWithRipemd160);
            provider.addAlgorithm("Signature.SHA1WITHECNR", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecNR");
            provider.addAlgorithm("Signature.SHA224WITHECNR", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecNR224");
            provider.addAlgorithm("Signature.SHA256WITHECNR", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecNR256");
            provider.addAlgorithm("Signature.SHA384WITHECNR", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecNR384");
            provider.addAlgorithm("Signature.SHA512WITHECNR", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecNR512");
            addSignatureAlgorithm(provider, AOSignConstants.DIGEST_ALGORITHM_SHA1, "CVC-ECDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecCVCDSA", EACObjectIdentifiers.id_TA_ECDSA_SHA_1);
            addSignatureAlgorithm(provider, "SHA224", "CVC-ECDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecCVCDSA224", EACObjectIdentifiers.id_TA_ECDSA_SHA_224);
            addSignatureAlgorithm(provider, "SHA256", "CVC-ECDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecCVCDSA256", EACObjectIdentifiers.id_TA_ECDSA_SHA_256);
            addSignatureAlgorithm(provider, AOSignConstants.DIGEST_ALGORITHM_SHA384, "CVC-ECDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecCVCDSA384", EACObjectIdentifiers.id_TA_ECDSA_SHA_384);
            addSignatureAlgorithm(provider, "SHA512", "CVC-ECDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecCVCDSA512", EACObjectIdentifiers.id_TA_ECDSA_SHA_512);
            addSignatureAlgorithm(provider, AOSignConstants.DIGEST_ALGORITHM_SHA1, "PLAIN-ECDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecCVCDSA", BSIObjectIdentifiers.ecdsa_plain_SHA1);
            addSignatureAlgorithm(provider, "SHA224", "PLAIN-ECDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecCVCDSA224", BSIObjectIdentifiers.ecdsa_plain_SHA224);
            addSignatureAlgorithm(provider, "SHA256", "PLAIN-ECDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecCVCDSA256", BSIObjectIdentifiers.ecdsa_plain_SHA256);
            addSignatureAlgorithm(provider, AOSignConstants.DIGEST_ALGORITHM_SHA384, "PLAIN-ECDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecCVCDSA384", BSIObjectIdentifiers.ecdsa_plain_SHA384);
            addSignatureAlgorithm(provider, "SHA512", "PLAIN-ECDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecCVCDSA512", BSIObjectIdentifiers.ecdsa_plain_SHA512);
            addSignatureAlgorithm(provider, "RIPEMD160", "PLAIN-ECDSA", "org.spongycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecPlainDSARP160", BSIObjectIdentifiers.ecdsa_plain_RIPEMD160);
        }
    }
}
