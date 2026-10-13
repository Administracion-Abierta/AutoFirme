package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.jcajce.provider.cmce.CMCEKeyFactorySpi;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/CMCE.class */
public class CMCE {
    private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.cmce.";

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/CMCE$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.CMCE", "org.bouncycastle.pqc.jcajce.provider.cmce.CMCEKeyFactorySpi");
            configurableProvider.addAlgorithm("KeyPairGenerator.CMCE", "org.bouncycastle.pqc.jcajce.provider.cmce.CMCEKeyPairGeneratorSpi");
            configurableProvider.addAlgorithm("KeyGenerator.CMCE", "org.bouncycastle.pqc.jcajce.provider.cmce.CMCEKeyGeneratorSpi");
            CMCEKeyFactorySpi cMCEKeyFactorySpi = new CMCEKeyFactorySpi();
            configurableProvider.addAlgorithm("Cipher.CMCE", "org.bouncycastle.pqc.jcajce.provider.cmce.CMCECipherSpi$Base");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher." + BCObjectIdentifiers.pqc_kem_mceliece, "CMCE");
            addCipherAlgorithm(configurableProvider, "mceliece348864", "org.bouncycastle.pqc.jcajce.provider.cmce.CMCECipherSpi$MCE348864", BCObjectIdentifiers.mceliece348864_r3);
            addCipherAlgorithm(configurableProvider, "mceliece460896", "org.bouncycastle.pqc.jcajce.provider.cmce.CMCECipherSpi$MCE460896", BCObjectIdentifiers.mceliece460896_r3);
            addCipherAlgorithm(configurableProvider, "mceliece6688128", "org.bouncycastle.pqc.jcajce.provider.cmce.CMCECipherSpi$MCE6688128", BCObjectIdentifiers.mceliece6688128_r3);
            addCipherAlgorithm(configurableProvider, "mceliece6960119", "org.bouncycastle.pqc.jcajce.provider.cmce.CMCECipherSpi$MCE6960119", BCObjectIdentifiers.mceliece6960119_r3);
            addCipherAlgorithm(configurableProvider, "mceliece8192128", "org.bouncycastle.pqc.jcajce.provider.cmce.CMCECipherSpi$MCE8192128", BCObjectIdentifiers.mceliece8192128_r3);
            registerOid(configurableProvider, BCObjectIdentifiers.pqc_kem_mceliece, "CMCE", cMCEKeyFactorySpi);
            registerOidAlgorithmParameters(configurableProvider, BCObjectIdentifiers.pqc_kem_mceliece, "CMCE");
        }
    }
}
