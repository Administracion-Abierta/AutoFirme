package org.spongycastle.jcajce.provider.asymmetric;

import org.spongycastle.asn1.oiw.OIWObjectIdentifiers;
import org.spongycastle.jcajce.provider.asymmetric.elgamal.KeyFactorySpi;
import org.spongycastle.jcajce.provider.config.ConfigurableProvider;
import org.spongycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.spongycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/ElGamal.class */
public class ElGamal {
    private static final String PREFIX = "org.spongycastle.jcajce.provider.asymmetric.elgamal.";

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/ElGamal$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.spongycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("AlgorithmParameterGenerator.ELGAMAL", "org.spongycastle.jcajce.provider.asymmetric.elgamal.AlgorithmParameterGeneratorSpi");
            provider.addAlgorithm("AlgorithmParameterGenerator.ElGamal", "org.spongycastle.jcajce.provider.asymmetric.elgamal.AlgorithmParameterGeneratorSpi");
            provider.addAlgorithm("AlgorithmParameters.ELGAMAL", "org.spongycastle.jcajce.provider.asymmetric.elgamal.AlgorithmParametersSpi");
            provider.addAlgorithm("AlgorithmParameters.ElGamal", "org.spongycastle.jcajce.provider.asymmetric.elgamal.AlgorithmParametersSpi");
            provider.addAlgorithm("Cipher.ELGAMAL", "org.spongycastle.jcajce.provider.asymmetric.elgamal.CipherSpi$NoPadding");
            provider.addAlgorithm("Cipher.ElGamal", "org.spongycastle.jcajce.provider.asymmetric.elgamal.CipherSpi$NoPadding");
            provider.addAlgorithm("Alg.Alias.Cipher.ELGAMAL/ECB/PKCS1PADDING", "ELGAMAL/PKCS1");
            provider.addAlgorithm("Alg.Alias.Cipher.ELGAMAL/NONE/PKCS1PADDING", "ELGAMAL/PKCS1");
            provider.addAlgorithm("Alg.Alias.Cipher.ELGAMAL/NONE/NOPADDING", "ELGAMAL");
            provider.addAlgorithm("Cipher.ELGAMAL/PKCS1", "org.spongycastle.jcajce.provider.asymmetric.elgamal.CipherSpi$PKCS1v1_5Padding");
            provider.addAlgorithm("KeyFactory.ELGAMAL", "org.spongycastle.jcajce.provider.asymmetric.elgamal.KeyFactorySpi");
            provider.addAlgorithm("KeyFactory.ElGamal", "org.spongycastle.jcajce.provider.asymmetric.elgamal.KeyFactorySpi");
            provider.addAlgorithm("KeyPairGenerator.ELGAMAL", "org.spongycastle.jcajce.provider.asymmetric.elgamal.KeyPairGeneratorSpi");
            provider.addAlgorithm("KeyPairGenerator.ElGamal", "org.spongycastle.jcajce.provider.asymmetric.elgamal.KeyPairGeneratorSpi");
            AsymmetricKeyInfoConverter keyFact = new KeyFactorySpi();
            registerOid(provider, OIWObjectIdentifiers.elGamalAlgorithm, "ELGAMAL", keyFact);
            registerOidAlgorithmParameterGenerator(provider, OIWObjectIdentifiers.elGamalAlgorithm, "ELGAMAL");
        }
    }
}
