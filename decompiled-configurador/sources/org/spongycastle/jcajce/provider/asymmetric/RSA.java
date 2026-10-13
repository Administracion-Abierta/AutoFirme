package org.spongycastle.jcajce.provider.asymmetric;

import es.gob.afirma.core.signers.AOSignConstants;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
import org.spongycastle.asn1.oiw.OIWObjectIdentifiers;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.spongycastle.asn1.x509.X509ObjectIdentifiers;
import org.spongycastle.jcajce.provider.asymmetric.rsa.KeyFactorySpi;
import org.spongycastle.jcajce.provider.config.ConfigurableProvider;
import org.spongycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.spongycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/asymmetric/RSA.class */
public class RSA {
    private static final String PREFIX = "org.spongycastle.jcajce.provider.asymmetric.rsa.";
    private static final Map<String, String> generalRsaAttributes = new HashMap();

    static {
        generalRsaAttributes.put("SupportedKeyClasses", "javax.crypto.interfaces.RSAPublicKey|javax.crypto.interfaces.RSAPrivateKey");
        generalRsaAttributes.put("SupportedKeyFormats", "PKCS#8|X.509");
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/asymmetric/RSA$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.spongycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("AlgorithmParameters.OAEP", "org.spongycastle.jcajce.provider.asymmetric.rsa.AlgorithmParametersSpi$OAEP");
            provider.addAlgorithm("AlgorithmParameters.PSS", "org.spongycastle.jcajce.provider.asymmetric.rsa.AlgorithmParametersSpi$PSS");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.RSAPSS", "PSS");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.RSASSA-PSS", "PSS");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA224withRSA/PSS", "PSS");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA256withRSA/PSS", "PSS");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA384withRSA/PSS", "PSS");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA512withRSA/PSS", "PSS");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA224WITHRSAANDMGF1", "PSS");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA256WITHRSAANDMGF1", "PSS");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA384WITHRSAANDMGF1", "PSS");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA512WITHRSAANDMGF1", "PSS");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA3-224WITHRSAANDMGF1", "PSS");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA3-256WITHRSAANDMGF1", "PSS");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA3-384WITHRSAANDMGF1", "PSS");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA3-512WITHRSAANDMGF1", "PSS");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.RAWRSAPSS", "PSS");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.NONEWITHRSAPSS", "PSS");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.NONEWITHRSASSA-PSS", "PSS");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.NONEWITHRSAANDMGF1", "PSS");
            provider.addAttributes("Cipher.RSA", RSA.generalRsaAttributes);
            provider.addAlgorithm("Cipher.RSA", "org.spongycastle.jcajce.provider.asymmetric.rsa.CipherSpi$NoPadding");
            provider.addAlgorithm("Cipher.RSA/RAW", "org.spongycastle.jcajce.provider.asymmetric.rsa.CipherSpi$NoPadding");
            provider.addAlgorithm("Cipher.RSA/PKCS1", "org.spongycastle.jcajce.provider.asymmetric.rsa.CipherSpi$PKCS1v1_5Padding");
            provider.addAlgorithm("Cipher", PKCSObjectIdentifiers.rsaEncryption, "org.spongycastle.jcajce.provider.asymmetric.rsa.CipherSpi$PKCS1v1_5Padding");
            provider.addAlgorithm("Cipher", X509ObjectIdentifiers.id_ea_rsa, "org.spongycastle.jcajce.provider.asymmetric.rsa.CipherSpi$PKCS1v1_5Padding");
            provider.addAlgorithm("Cipher.RSA/1", "org.spongycastle.jcajce.provider.asymmetric.rsa.CipherSpi$PKCS1v1_5Padding_PrivateOnly");
            provider.addAlgorithm("Cipher.RSA/2", "org.spongycastle.jcajce.provider.asymmetric.rsa.CipherSpi$PKCS1v1_5Padding_PublicOnly");
            provider.addAlgorithm("Cipher.RSA/OAEP", "org.spongycastle.jcajce.provider.asymmetric.rsa.CipherSpi$OAEPPadding");
            provider.addAlgorithm("Cipher", PKCSObjectIdentifiers.id_RSAES_OAEP, "org.spongycastle.jcajce.provider.asymmetric.rsa.CipherSpi$OAEPPadding");
            provider.addAlgorithm("Cipher.RSA/ISO9796-1", "org.spongycastle.jcajce.provider.asymmetric.rsa.CipherSpi$ISO9796d1Padding");
            provider.addAlgorithm("Alg.Alias.Cipher.RSA//RAW", "RSA");
            provider.addAlgorithm("Alg.Alias.Cipher.RSA//NOPADDING", "RSA");
            provider.addAlgorithm("Alg.Alias.Cipher.RSA//PKCS1PADDING", "RSA/PKCS1");
            provider.addAlgorithm("Alg.Alias.Cipher.RSA//OAEPPADDING", "RSA/OAEP");
            provider.addAlgorithm("Alg.Alias.Cipher.RSA//ISO9796-1PADDING", "RSA/ISO9796-1");
            provider.addAlgorithm("KeyFactory.RSA", "org.spongycastle.jcajce.provider.asymmetric.rsa.KeyFactorySpi");
            provider.addAlgorithm("KeyPairGenerator.RSA", "org.spongycastle.jcajce.provider.asymmetric.rsa.KeyPairGeneratorSpi");
            AsymmetricKeyInfoConverter keyFact = new KeyFactorySpi();
            registerOid(provider, PKCSObjectIdentifiers.rsaEncryption, "RSA", keyFact);
            registerOid(provider, X509ObjectIdentifiers.id_ea_rsa, "RSA", keyFact);
            registerOid(provider, PKCSObjectIdentifiers.id_RSAES_OAEP, "RSA", keyFact);
            registerOid(provider, PKCSObjectIdentifiers.id_RSASSA_PSS, "RSA", keyFact);
            registerOidAlgorithmParameters(provider, PKCSObjectIdentifiers.rsaEncryption, "RSA");
            registerOidAlgorithmParameters(provider, X509ObjectIdentifiers.id_ea_rsa, "RSA");
            registerOidAlgorithmParameters(provider, PKCSObjectIdentifiers.id_RSAES_OAEP, "OAEP");
            registerOidAlgorithmParameters(provider, PKCSObjectIdentifiers.id_RSASSA_PSS, "PSS");
            provider.addAlgorithm("Signature.RSASSA-PSS", "org.spongycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$PSSwithRSA");
            provider.addAlgorithm("Signature." + PKCSObjectIdentifiers.id_RSASSA_PSS, "org.spongycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$PSSwithRSA");
            provider.addAlgorithm("Signature.OID." + PKCSObjectIdentifiers.id_RSASSA_PSS, "org.spongycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$PSSwithRSA");
            provider.addAlgorithm("Signature.RSA", "org.spongycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$noneRSA");
            provider.addAlgorithm("Signature.RAWRSASSA-PSS", "org.spongycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$nonePSS");
            provider.addAlgorithm("Alg.Alias.Signature.RAWRSA", "RSA");
            provider.addAlgorithm("Alg.Alias.Signature.NONEWITHRSA", "RSA");
            provider.addAlgorithm("Alg.Alias.Signature.RAWRSAPSS", "RAWRSASSA-PSS");
            provider.addAlgorithm("Alg.Alias.Signature.NONEWITHRSAPSS", "RAWRSASSA-PSS");
            provider.addAlgorithm("Alg.Alias.Signature.NONEWITHRSASSA-PSS", "RAWRSASSA-PSS");
            provider.addAlgorithm("Alg.Alias.Signature.NONEWITHRSAANDMGF1", "RAWRSASSA-PSS");
            provider.addAlgorithm("Alg.Alias.Signature.RSAPSS", "RSASSA-PSS");
            addPSSSignature(provider, "SHA224", "org.spongycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA224withRSA");
            addPSSSignature(provider, "SHA256", "org.spongycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA256withRSA");
            addPSSSignature(provider, AOSignConstants.DIGEST_ALGORITHM_SHA384, "org.spongycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA384withRSA");
            addPSSSignature(provider, "SHA512", "org.spongycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA512withRSA");
            addPSSSignature(provider, "SHA512(224)", "org.spongycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA512_224withRSA");
            addPSSSignature(provider, "SHA512(256)", "org.spongycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA512_256withRSA");
            addPSSSignature(provider, MessageDigestAlgorithms.SHA3_224, "org.spongycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA3_224withRSA");
            addPSSSignature(provider, "SHA3-256", "org.spongycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA3_256withRSA");
            addPSSSignature(provider, MessageDigestAlgorithms.SHA3_384, "org.spongycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA3_384withRSA");
            addPSSSignature(provider, MessageDigestAlgorithms.SHA3_512, "org.spongycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA3_512withRSA");
            if (provider.hasAlgorithm("MessageDigest", MessageDigestAlgorithms.MD2)) {
                addDigestSignature(provider, MessageDigestAlgorithms.MD2, "org.spongycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$MD2", PKCSObjectIdentifiers.md2WithRSAEncryption);
            }
            if (provider.hasAlgorithm("MessageDigest", "MD4")) {
                addDigestSignature(provider, "MD4", "org.spongycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$MD4", PKCSObjectIdentifiers.md4WithRSAEncryption);
            }
            if (provider.hasAlgorithm("MessageDigest", MessageDigestAlgorithms.MD5)) {
                addDigestSignature(provider, MessageDigestAlgorithms.MD5, "org.spongycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$MD5", PKCSObjectIdentifiers.md5WithRSAEncryption);
                addISO9796Signature(provider, MessageDigestAlgorithms.MD5, "org.spongycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$MD5WithRSAEncryption");
            }
            if (provider.hasAlgorithm("MessageDigest", AOSignConstants.DIGEST_ALGORITHM_SHA1)) {
                provider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA1withRSA/PSS", "PSS");
                provider.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA1WITHRSAANDMGF1", "PSS");
                addPSSSignature(provider, AOSignConstants.DIGEST_ALGORITHM_SHA1, "org.spongycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA1withRSA");
                addDigestSignature(provider, AOSignConstants.DIGEST_ALGORITHM_SHA1, "org.spongycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA1", PKCSObjectIdentifiers.sha1WithRSAEncryption);
                addISO9796Signature(provider, AOSignConstants.DIGEST_ALGORITHM_SHA1, "org.spongycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA1WithRSAEncryption");
                provider.addAlgorithm("Alg.Alias.Signature." + OIWObjectIdentifiers.sha1WithRSA, "SHA1WITHRSA");
                provider.addAlgorithm("Alg.Alias.Signature.OID." + OIWObjectIdentifiers.sha1WithRSA, "SHA1WITHRSA");
                addX931Signature(provider, AOSignConstants.DIGEST_ALGORITHM_SHA1, "org.spongycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA1WithRSAEncryption");
            }
            addDigestSignature(provider, "SHA224", "org.spongycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA224", PKCSObjectIdentifiers.sha224WithRSAEncryption);
            addDigestSignature(provider, "SHA256", "org.spongycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA256", PKCSObjectIdentifiers.sha256WithRSAEncryption);
            addDigestSignature(provider, AOSignConstants.DIGEST_ALGORITHM_SHA384, "org.spongycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA384", PKCSObjectIdentifiers.sha384WithRSAEncryption);
            addDigestSignature(provider, "SHA512", "org.spongycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA512", PKCSObjectIdentifiers.sha512WithRSAEncryption);
            addDigestSignature(provider, "SHA512(224)", "org.spongycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA512_224", PKCSObjectIdentifiers.sha512_224WithRSAEncryption);
            addDigestSignature(provider, "SHA512(256)", "org.spongycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA512_256", PKCSObjectIdentifiers.sha512_256WithRSAEncryption);
            addDigestSignature(provider, MessageDigestAlgorithms.SHA3_224, "org.spongycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA3_224", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_224);
            addDigestSignature(provider, "SHA3-256", "org.spongycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA3_256", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_256);
            addDigestSignature(provider, MessageDigestAlgorithms.SHA3_384, "org.spongycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA3_384", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_384);
            addDigestSignature(provider, MessageDigestAlgorithms.SHA3_512, "org.spongycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA3_512", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512);
            addISO9796Signature(provider, "SHA224", "org.spongycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA224WithRSAEncryption");
            addISO9796Signature(provider, "SHA256", "org.spongycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA256WithRSAEncryption");
            addISO9796Signature(provider, AOSignConstants.DIGEST_ALGORITHM_SHA384, "org.spongycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA384WithRSAEncryption");
            addISO9796Signature(provider, "SHA512", "org.spongycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA512WithRSAEncryption");
            addISO9796Signature(provider, "SHA512(224)", "org.spongycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA512_224WithRSAEncryption");
            addISO9796Signature(provider, "SHA512(256)", "org.spongycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA512_256WithRSAEncryption");
            addX931Signature(provider, "SHA224", "org.spongycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA224WithRSAEncryption");
            addX931Signature(provider, "SHA256", "org.spongycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA256WithRSAEncryption");
            addX931Signature(provider, AOSignConstants.DIGEST_ALGORITHM_SHA384, "org.spongycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA384WithRSAEncryption");
            addX931Signature(provider, "SHA512", "org.spongycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA512WithRSAEncryption");
            addX931Signature(provider, "SHA512(224)", "org.spongycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA512_224WithRSAEncryption");
            addX931Signature(provider, "SHA512(256)", "org.spongycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA512_256WithRSAEncryption");
            if (provider.hasAlgorithm("MessageDigest", "RIPEMD128")) {
                addDigestSignature(provider, "RIPEMD128", "org.spongycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$RIPEMD128", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
                addDigestSignature(provider, "RMD128", "org.spongycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$RIPEMD128", null);
                addX931Signature(provider, "RMD128", "org.spongycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$RIPEMD128WithRSAEncryption");
                addX931Signature(provider, "RIPEMD128", "org.spongycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$RIPEMD128WithRSAEncryption");
            }
            if (provider.hasAlgorithm("MessageDigest", "RIPEMD160")) {
                addDigestSignature(provider, "RIPEMD160", "org.spongycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$RIPEMD160", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
                addDigestSignature(provider, "RMD160", "org.spongycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$RIPEMD160", null);
                provider.addAlgorithm("Alg.Alias.Signature.RIPEMD160WithRSA/ISO9796-2", "RIPEMD160withRSA/ISO9796-2");
                provider.addAlgorithm("Signature.RIPEMD160withRSA/ISO9796-2", "org.spongycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$RIPEMD160WithRSAEncryption");
                addX931Signature(provider, "RMD160", "org.spongycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$RIPEMD160WithRSAEncryption");
                addX931Signature(provider, "RIPEMD160", "org.spongycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$RIPEMD160WithRSAEncryption");
            }
            if (provider.hasAlgorithm("MessageDigest", "RIPEMD256")) {
                addDigestSignature(provider, "RIPEMD256", "org.spongycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$RIPEMD256", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
                addDigestSignature(provider, "RMD256", "org.spongycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$RIPEMD256", null);
            }
            if (provider.hasAlgorithm("MessageDigest", "WHIRLPOOL")) {
                addISO9796Signature(provider, "Whirlpool", "org.spongycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$WhirlpoolWithRSAEncryption");
                addISO9796Signature(provider, "WHIRLPOOL", "org.spongycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$WhirlpoolWithRSAEncryption");
                addX931Signature(provider, "Whirlpool", "org.spongycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$WhirlpoolWithRSAEncryption");
                addX931Signature(provider, "WHIRLPOOL", "org.spongycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$WhirlpoolWithRSAEncryption");
            }
        }

        private void addDigestSignature(ConfigurableProvider provider, String digest, String className, ASN1ObjectIdentifier oid) {
            String mainName = digest + "WITHRSA";
            String jdk11Variation1 = digest + "withRSA";
            String jdk11Variation2 = digest + "WithRSA";
            String alias = digest + "/RSA";
            String longName = digest + "WITHRSAENCRYPTION";
            String longJdk11Variation1 = digest + "withRSAEncryption";
            String longJdk11Variation2 = digest + "WithRSAEncryption";
            provider.addAlgorithm("Signature." + mainName, className);
            provider.addAlgorithm("Alg.Alias.Signature." + jdk11Variation1, mainName);
            provider.addAlgorithm("Alg.Alias.Signature." + jdk11Variation2, mainName);
            provider.addAlgorithm("Alg.Alias.Signature." + longName, mainName);
            provider.addAlgorithm("Alg.Alias.Signature." + longJdk11Variation1, mainName);
            provider.addAlgorithm("Alg.Alias.Signature." + longJdk11Variation2, mainName);
            provider.addAlgorithm("Alg.Alias.Signature." + alias, mainName);
            if (oid != null) {
                provider.addAlgorithm("Alg.Alias.Signature." + oid, mainName);
                provider.addAlgorithm("Alg.Alias.Signature.OID." + oid, mainName);
            }
        }

        private void addISO9796Signature(ConfigurableProvider provider, String digest, String className) {
            provider.addAlgorithm("Alg.Alias.Signature." + digest + "withRSA/ISO9796-2", digest + "WITHRSA/ISO9796-2");
            provider.addAlgorithm("Alg.Alias.Signature." + digest + "WithRSA/ISO9796-2", digest + "WITHRSA/ISO9796-2");
            provider.addAlgorithm("Signature." + digest + "WITHRSA/ISO9796-2", className);
        }

        private void addPSSSignature(ConfigurableProvider provider, String digest, String className) {
            provider.addAlgorithm("Alg.Alias.Signature." + digest + "withRSA/PSS", digest + "WITHRSAANDMGF1");
            provider.addAlgorithm("Alg.Alias.Signature." + digest + "WithRSA/PSS", digest + "WITHRSAANDMGF1");
            provider.addAlgorithm("Alg.Alias.Signature." + digest + "withRSAandMGF1", digest + "WITHRSAANDMGF1");
            provider.addAlgorithm("Alg.Alias.Signature." + digest + "WithRSAAndMGF1", digest + "WITHRSAANDMGF1");
            provider.addAlgorithm("Signature." + digest + "WITHRSAANDMGF1", className);
        }

        private void addX931Signature(ConfigurableProvider provider, String digest, String className) {
            provider.addAlgorithm("Alg.Alias.Signature." + digest + "withRSA/X9.31", digest + "WITHRSA/X9.31");
            provider.addAlgorithm("Alg.Alias.Signature." + digest + "WithRSA/X9.31", digest + "WITHRSA/X9.31");
            provider.addAlgorithm("Signature." + digest + "WITHRSA/X9.31", className);
        }
    }
}
