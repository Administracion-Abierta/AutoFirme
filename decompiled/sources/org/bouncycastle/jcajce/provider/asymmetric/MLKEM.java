package org.bouncycastle.jcajce.provider.asymmetric;

import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/MLKEM.class */
public class MLKEM {
    private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.mlkem.";

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/MLKEM$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.ML-KEM", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyFactorySpi");
            configurableProvider.addAlgorithm("Alg.Alias.KeyFactory.MLKEM", "ML-KEM");
            addKeyFactoryAlgorithm(configurableProvider, "ML-KEM-512", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyFactorySpi$MLKEM512", NISTObjectIdentifiers.id_alg_ml_kem_512, new MLKEMKeyFactorySpi.MLKEM512());
            addKeyFactoryAlgorithm(configurableProvider, "ML-KEM-768", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyFactorySpi$MLKEM768", NISTObjectIdentifiers.id_alg_ml_kem_768, new MLKEMKeyFactorySpi.MLKEM768());
            addKeyFactoryAlgorithm(configurableProvider, "ML-KEM-1024", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyFactorySpi$MLKEM1024", NISTObjectIdentifiers.id_alg_ml_kem_1024, new MLKEMKeyFactorySpi.MLKEM1024());
            configurableProvider.addAlgorithm("KeyPairGenerator.ML-KEM", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyPairGeneratorSpi");
            configurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.MLKEM", "ML-KEM");
            addKeyPairGeneratorAlgorithm(configurableProvider, "ML-KEM-512", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyPairGeneratorSpi$MLKEM512", NISTObjectIdentifiers.id_alg_ml_kem_512);
            addKeyPairGeneratorAlgorithm(configurableProvider, "ML-KEM-768", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyPairGeneratorSpi$MLKEM768", NISTObjectIdentifiers.id_alg_ml_kem_768);
            addKeyPairGeneratorAlgorithm(configurableProvider, "ML-KEM-1024", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyPairGeneratorSpi$MLKEM1024", NISTObjectIdentifiers.id_alg_ml_kem_1024);
            configurableProvider.addAlgorithm("KeyGenerator.ML-KEM", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyGeneratorSpi");
            addKeyGeneratorAlgorithm(configurableProvider, "ML-KEM-512", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyGeneratorSpi$MLKEM512", NISTObjectIdentifiers.id_alg_ml_kem_512);
            addKeyGeneratorAlgorithm(configurableProvider, "ML-KEM-768", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyGeneratorSpi$MLKEM768", NISTObjectIdentifiers.id_alg_ml_kem_768);
            addKeyGeneratorAlgorithm(configurableProvider, "ML-KEM-1024", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyGeneratorSpi$MLKEM1024", NISTObjectIdentifiers.id_alg_ml_kem_1024);
            MLKEMKeyFactorySpi mLKEMKeyFactorySpi = new MLKEMKeyFactorySpi();
            configurableProvider.addAlgorithm("Cipher.ML-KEM", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMCipherSpi$Base");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.MLKEM", "ML-KEM");
            addCipherAlgorithm(configurableProvider, "ML-KEM-512", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMCipherSpi$MLKEM512", NISTObjectIdentifiers.id_alg_ml_kem_512);
            addCipherAlgorithm(configurableProvider, "ML-KEM-768", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMCipherSpi$MLKEM768", NISTObjectIdentifiers.id_alg_ml_kem_768);
            addCipherAlgorithm(configurableProvider, "ML-KEM-1024", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMCipherSpi$MLKEM1024", NISTObjectIdentifiers.id_alg_ml_kem_1024);
            configurableProvider.addKeyInfoConverter(NISTObjectIdentifiers.id_alg_ml_kem_512, mLKEMKeyFactorySpi);
            configurableProvider.addKeyInfoConverter(NISTObjectIdentifiers.id_alg_ml_kem_768, mLKEMKeyFactorySpi);
            configurableProvider.addKeyInfoConverter(NISTObjectIdentifiers.id_alg_ml_kem_1024, mLKEMKeyFactorySpi);
        }
    }
}
