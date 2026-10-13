package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyFactorySpi;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/Kyber.class */
public class Kyber {
    private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.kyber.";

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/Kyber$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.KYBER", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyFactorySpi");
            addKeyFactoryAlgorithm(configurableProvider, "ML-KEM-512", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyFactorySpi$Kyber512", NISTObjectIdentifiers.id_alg_ml_kem_512, new KyberKeyFactorySpi.Kyber512());
            addKeyFactoryAlgorithm(configurableProvider, "ML-KEM-768", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyFactorySpi$Kyber768", NISTObjectIdentifiers.id_alg_ml_kem_768, new KyberKeyFactorySpi.Kyber768());
            addKeyFactoryAlgorithm(configurableProvider, "ML-KEM-1024", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyFactorySpi$Kyber1024", NISTObjectIdentifiers.id_alg_ml_kem_1024, new KyberKeyFactorySpi.Kyber1024());
            configurableProvider.addAlgorithm("Alg.Alias.KeyFactory.KYBER512", "ML-KEM-512");
            configurableProvider.addAlgorithm("Alg.Alias.KeyFactory.KYBER768", "ML-KEM-768");
            configurableProvider.addAlgorithm("Alg.Alias.KeyFactory.KYBER1024", "ML-KEM-1024");
            configurableProvider.addAlgorithm("KeyPairGenerator.ML-KEM", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyPairGeneratorSpi");
            configurableProvider.addAlgorithm("KeyPairGenerator.ML-KEM-512", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyPairGeneratorSpi$Kyber512");
            configurableProvider.addAlgorithm("KeyPairGenerator.ML-KEM-768", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyPairGeneratorSpi$Kyber768");
            configurableProvider.addAlgorithm("KeyPairGenerator.ML-KEM-1024", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyPairGeneratorSpi$Kyber1024");
            configurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.KYBER", "ML-KEM");
            configurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.KYBER512", "ML-KEM-512");
            configurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.KYBER768", "ML-KEM-768");
            configurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.KYBER1024", "ML-KEM-1024");
            configurableProvider.addAlgorithm("KeyGenerator.KYBER", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyGeneratorSpi");
            addKeyGeneratorAlgorithm(configurableProvider, "ML-KEM-512", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyGeneratorSpi$Kyber512", NISTObjectIdentifiers.id_alg_ml_kem_512);
            addKeyGeneratorAlgorithm(configurableProvider, "ML-KEM-768", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyGeneratorSpi$Kyber768", NISTObjectIdentifiers.id_alg_ml_kem_768);
            addKeyGeneratorAlgorithm(configurableProvider, "ML-KEM-1024", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyGeneratorSpi$Kyber1024", NISTObjectIdentifiers.id_alg_ml_kem_1024);
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.KYBER512", "ML-KEM-512");
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.KYBER768", "ML-KEM-768");
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.KYBER1024", "ML-KEM-1024");
            KyberKeyFactorySpi kyberKeyFactorySpi = new KyberKeyFactorySpi();
            configurableProvider.addAlgorithm("Cipher.KYBER", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberCipherSpi$Base");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher." + BCObjectIdentifiers.pqc_kem_kyber, "KYBER");
            addCipherAlgorithm(configurableProvider, "ML-KEM-512", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberCipherSpi$Kyber512", NISTObjectIdentifiers.id_alg_ml_kem_512);
            addCipherAlgorithm(configurableProvider, "ML-KEM-768", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberCipherSpi$Kyber768", NISTObjectIdentifiers.id_alg_ml_kem_768);
            addCipherAlgorithm(configurableProvider, "ML-KEM-1024", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberCipherSpi$Kyber1024", NISTObjectIdentifiers.id_alg_ml_kem_1024);
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.KYBER512", "ML-KEM-512");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.KYBER768", "ML-KEM-768");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.KYBER1024", "ML-KEM-1024");
            registerOid(configurableProvider, BCObjectIdentifiers.pqc_kem_kyber, "KYBER", kyberKeyFactorySpi);
        }
    }
}
