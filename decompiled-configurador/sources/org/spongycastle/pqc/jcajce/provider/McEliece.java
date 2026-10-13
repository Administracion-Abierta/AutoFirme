package org.spongycastle.pqc.jcajce.provider;

import org.spongycastle.jcajce.provider.config.ConfigurableProvider;
import org.spongycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.spongycastle.pqc.asn1.PQCObjectIdentifiers;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/jcajce/provider/McEliece.class */
public class McEliece {
    private static final String PREFIX = "org.spongycastle.pqc.jcajce.provider.mceliece.";

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/jcajce/provider/McEliece$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.spongycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("KeyPairGenerator.McElieceKobaraImai", "org.spongycastle.pqc.jcajce.provider.mceliece.McElieceCCA2KeyPairGeneratorSpi");
            provider.addAlgorithm("KeyPairGenerator.McEliecePointcheval", "org.spongycastle.pqc.jcajce.provider.mceliece.McElieceCCA2KeyPairGeneratorSpi");
            provider.addAlgorithm("KeyPairGenerator.McElieceFujisaki", "org.spongycastle.pqc.jcajce.provider.mceliece.McElieceCCA2KeyPairGeneratorSpi");
            provider.addAlgorithm("KeyPairGenerator.McEliece", "org.spongycastle.pqc.jcajce.provider.mceliece.McElieceKeyPairGeneratorSpi");
            provider.addAlgorithm("KeyPairGenerator.McEliece-CCA2", "org.spongycastle.pqc.jcajce.provider.mceliece.McElieceCCA2KeyPairGeneratorSpi");
            provider.addAlgorithm("KeyFactory.McElieceKobaraImai", "org.spongycastle.pqc.jcajce.provider.mceliece.McElieceCCA2KeyFactorySpi");
            provider.addAlgorithm("KeyFactory.McEliecePointcheval", "org.spongycastle.pqc.jcajce.provider.mceliece.McElieceCCA2KeyFactorySpi");
            provider.addAlgorithm("KeyFactory.McElieceFujisaki", "org.spongycastle.pqc.jcajce.provider.mceliece.McElieceCCA2KeyFactorySpi");
            provider.addAlgorithm("KeyFactory.McEliece", "org.spongycastle.pqc.jcajce.provider.mceliece.McElieceKeyFactorySpi");
            provider.addAlgorithm("KeyFactory.McEliece-CCA2", "org.spongycastle.pqc.jcajce.provider.mceliece.McElieceCCA2KeyFactorySpi");
            provider.addAlgorithm("KeyFactory." + PQCObjectIdentifiers.mcElieceCca2, "org.spongycastle.pqc.jcajce.provider.mceliece.McElieceCCA2KeyFactorySpi");
            provider.addAlgorithm("KeyFactory." + PQCObjectIdentifiers.mcEliece, "org.spongycastle.pqc.jcajce.provider.mceliece.McElieceKeyFactorySpi");
            provider.addAlgorithm("Cipher.McEliece", "org.spongycastle.pqc.jcajce.provider.mceliece.McEliecePKCSCipherSpi$McEliecePKCS");
            provider.addAlgorithm("Cipher.McEliecePointcheval", "org.spongycastle.pqc.jcajce.provider.mceliece.McEliecePointchevalCipherSpi$McEliecePointcheval");
            provider.addAlgorithm("Cipher.McElieceKobaraImai", "org.spongycastle.pqc.jcajce.provider.mceliece.McElieceKobaraImaiCipherSpi$McElieceKobaraImai");
            provider.addAlgorithm("Cipher.McElieceFujisaki", "org.spongycastle.pqc.jcajce.provider.mceliece.McElieceFujisakiCipherSpi$McElieceFujisaki");
        }
    }
}
