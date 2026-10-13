package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.jcajce.provider.dilithium.DilithiumKeyFactorySpi;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/Dilithium.class */
public class Dilithium {
    private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.dilithium.";

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/Dilithium$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.DILITHIUM", "org.bouncycastle.pqc.jcajce.provider.dilithium.DilithiumKeyFactorySpi");
            addKeyFactoryAlgorithm(configurableProvider, "DILITHIUM2", "org.bouncycastle.pqc.jcajce.provider.dilithium.DilithiumKeyFactorySpi$Base2", BCObjectIdentifiers.dilithium2, new DilithiumKeyFactorySpi.Base2());
            addKeyFactoryAlgorithm(configurableProvider, "DILITHIUM3", "org.bouncycastle.pqc.jcajce.provider.dilithium.DilithiumKeyFactorySpi$Base3", BCObjectIdentifiers.dilithium3, new DilithiumKeyFactorySpi.Base3());
            addKeyFactoryAlgorithm(configurableProvider, "DILITHIUM5", "org.bouncycastle.pqc.jcajce.provider.dilithium.DilithiumKeyFactorySpi$Base5", BCObjectIdentifiers.dilithium5, new DilithiumKeyFactorySpi.Base5());
            configurableProvider.addAlgorithm("KeyPairGenerator.DILITHIUM", "org.bouncycastle.pqc.jcajce.provider.dilithium.DilithiumKeyPairGeneratorSpi");
            addKeyPairGeneratorAlgorithm(configurableProvider, "DILITHIUM2", "org.bouncycastle.pqc.jcajce.provider.dilithium.DilithiumKeyPairGeneratorSpi$Base2", BCObjectIdentifiers.dilithium2);
            addKeyPairGeneratorAlgorithm(configurableProvider, "DILITHIUM3", "org.bouncycastle.pqc.jcajce.provider.dilithium.DilithiumKeyPairGeneratorSpi$Base3", BCObjectIdentifiers.dilithium3);
            addKeyPairGeneratorAlgorithm(configurableProvider, "DILITHIUM5", "org.bouncycastle.pqc.jcajce.provider.dilithium.DilithiumKeyPairGeneratorSpi$Base5", BCObjectIdentifiers.dilithium5);
            addSignatureAlgorithm(configurableProvider, "DILITHIUM", "org.bouncycastle.pqc.jcajce.provider.dilithium.SignatureSpi$Base", BCObjectIdentifiers.dilithium);
            addSignatureAlgorithm(configurableProvider, "DILITHIUM2", "org.bouncycastle.pqc.jcajce.provider.dilithium.SignatureSpi$Base2", BCObjectIdentifiers.dilithium2);
            addSignatureAlgorithm(configurableProvider, "DILITHIUM3", "org.bouncycastle.pqc.jcajce.provider.dilithium.SignatureSpi$Base3", BCObjectIdentifiers.dilithium3);
            addSignatureAlgorithm(configurableProvider, "DILITHIUM5", "org.bouncycastle.pqc.jcajce.provider.dilithium.SignatureSpi$Base5", BCObjectIdentifiers.dilithium5);
        }
    }
}
