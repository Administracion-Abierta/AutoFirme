package org.spongycastle.jcajce.provider.asymmetric;

import es.gob.afirma.core.signers.AOSignConstants;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
import org.spongycastle.jcajce.provider.asymmetric.dsa.DSAUtil;
import org.spongycastle.jcajce.provider.asymmetric.dsa.KeyFactorySpi;
import org.spongycastle.jcajce.provider.config.ConfigurableProvider;
import org.spongycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.spongycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/DSA.class */
public class DSA {
    private static final String PREFIX = "org.spongycastle.jcajce.provider.asymmetric.dsa.";

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/DSA$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.spongycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("AlgorithmParameters.DSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.AlgorithmParametersSpi");
            provider.addAlgorithm("AlgorithmParameterGenerator.DSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.AlgorithmParameterGeneratorSpi");
            provider.addAlgorithm("KeyPairGenerator.DSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.KeyPairGeneratorSpi");
            provider.addAlgorithm("KeyFactory.DSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.KeyFactorySpi");
            provider.addAlgorithm("Signature.DSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$stdDSA");
            provider.addAlgorithm("Signature.NONEWITHDSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$noneDSA");
            provider.addAlgorithm("Alg.Alias.Signature.RAWDSA", "NONEWITHDSA");
            provider.addAlgorithm("Signature.DETDSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA");
            provider.addAlgorithm("Signature.SHA1WITHDETDSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA");
            provider.addAlgorithm("Signature.SHA224WITHDETDSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA224");
            provider.addAlgorithm("Signature.SHA256WITHDETDSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA256");
            provider.addAlgorithm("Signature.SHA384WITHDETDSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA384");
            provider.addAlgorithm("Signature.SHA512WITHDETDSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA512");
            provider.addAlgorithm("Signature.DDSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA");
            provider.addAlgorithm("Signature.SHA1WITHDDSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA");
            provider.addAlgorithm("Signature.SHA224WITHDDSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA224");
            provider.addAlgorithm("Signature.SHA256WITHDDSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA256");
            provider.addAlgorithm("Signature.SHA384WITHDDSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA384");
            provider.addAlgorithm("Signature.SHA512WITHDDSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA512");
            provider.addAlgorithm("Signature.SHA3-224WITHDDSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSASha3_224");
            provider.addAlgorithm("Signature.SHA3-256WITHDDSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSASha3_256");
            provider.addAlgorithm("Signature.SHA3-384WITHDDSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSASha3_384");
            provider.addAlgorithm("Signature.SHA3-512WITHDDSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSASha3_512");
            addSignatureAlgorithm(provider, "SHA224", "DSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsa224", NISTObjectIdentifiers.dsa_with_sha224);
            addSignatureAlgorithm(provider, "SHA256", "DSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsa256", NISTObjectIdentifiers.dsa_with_sha256);
            addSignatureAlgorithm(provider, AOSignConstants.DIGEST_ALGORITHM_SHA384, "DSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsa384", NISTObjectIdentifiers.dsa_with_sha384);
            addSignatureAlgorithm(provider, "SHA512", "DSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsa512", NISTObjectIdentifiers.dsa_with_sha512);
            addSignatureAlgorithm(provider, MessageDigestAlgorithms.SHA3_224, "DSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsaSha3_224", NISTObjectIdentifiers.id_dsa_with_sha3_224);
            addSignatureAlgorithm(provider, "SHA3-256", "DSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsaSha3_256", NISTObjectIdentifiers.id_dsa_with_sha3_256);
            addSignatureAlgorithm(provider, MessageDigestAlgorithms.SHA3_384, "DSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsaSha3_384", NISTObjectIdentifiers.id_dsa_with_sha3_384);
            addSignatureAlgorithm(provider, MessageDigestAlgorithms.SHA3_512, "DSA", "org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsaSha3_512", NISTObjectIdentifiers.id_dsa_with_sha3_512);
            provider.addAlgorithm("Alg.Alias.Signature.SHA/DSA", "DSA");
            provider.addAlgorithm("Alg.Alias.Signature.SHA1withDSA", "DSA");
            provider.addAlgorithm("Alg.Alias.Signature.SHA1WITHDSA", "DSA");
            provider.addAlgorithm("Alg.Alias.Signature.1.3.14.3.2.26with1.2.840.10040.4.1", "DSA");
            provider.addAlgorithm("Alg.Alias.Signature.1.3.14.3.2.26with1.2.840.10040.4.3", "DSA");
            provider.addAlgorithm("Alg.Alias.Signature.DSAwithSHA1", "DSA");
            provider.addAlgorithm("Alg.Alias.Signature.DSAWITHSHA1", "DSA");
            provider.addAlgorithm("Alg.Alias.Signature.SHA1WithDSA", "DSA");
            provider.addAlgorithm("Alg.Alias.Signature.DSAWithSHA1", "DSA");
            provider.addAlgorithm("Alg.Alias.Signature.1.2.840.10040.4.3", "DSA");
            AsymmetricKeyInfoConverter keyFact = new KeyFactorySpi();
            for (int i = 0; i != DSAUtil.dsaOids.length; i++) {
                provider.addAlgorithm("Alg.Alias.Signature." + DSAUtil.dsaOids[i], "DSA");
                registerOid(provider, DSAUtil.dsaOids[i], "DSA", keyFact);
                registerOidAlgorithmParameterGenerator(provider, DSAUtil.dsaOids[i], "DSA");
            }
        }
    }
}
