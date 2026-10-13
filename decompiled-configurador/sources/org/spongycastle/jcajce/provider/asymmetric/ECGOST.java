package org.spongycastle.jcajce.provider.asymmetric;

import org.spongycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.spongycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.spongycastle.jcajce.provider.asymmetric.ecgost.KeyFactorySpi;
import org.spongycastle.jcajce.provider.config.ConfigurableProvider;
import org.spongycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/asymmetric/ECGOST.class */
public class ECGOST {
    private static final String PREFIX = "org.spongycastle.jcajce.provider.asymmetric.ecgost.";
    private static final String PREFIX_GOST_2012 = "org.spongycastle.jcajce.provider.asymmetric.ecgost12.";

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/asymmetric/ECGOST$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.spongycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("KeyFactory.ECGOST3410", "org.spongycastle.jcajce.provider.asymmetric.ecgost.KeyFactorySpi");
            provider.addAlgorithm("Alg.Alias.KeyFactory.GOST-3410-2001", "ECGOST3410");
            provider.addAlgorithm("Alg.Alias.KeyFactory.ECGOST-3410", "ECGOST3410");
            registerOid(provider, CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410", new KeyFactorySpi());
            registerOid(provider, CryptoProObjectIdentifiers.gostR3410_2001DH, "ECGOST3410", new KeyFactorySpi());
            registerOidAlgorithmParameters(provider, CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410");
            provider.addAlgorithm("KeyPairGenerator.ECGOST3410", "org.spongycastle.jcajce.provider.asymmetric.ecgost.KeyPairGeneratorSpi");
            provider.addAlgorithm("Alg.Alias.KeyPairGenerator.ECGOST-3410", "ECGOST3410");
            provider.addAlgorithm("Alg.Alias.KeyPairGenerator.GOST-3410-2001", "ECGOST3410");
            provider.addAlgorithm("Signature.ECGOST3410", "org.spongycastle.jcajce.provider.asymmetric.ecgost.SignatureSpi");
            provider.addAlgorithm("Alg.Alias.Signature.ECGOST-3410", "ECGOST3410");
            provider.addAlgorithm("Alg.Alias.Signature.GOST-3410-2001", "ECGOST3410");
            provider.addAlgorithm("KeyAgreement.ECGOST3410", "org.spongycastle.jcajce.provider.asymmetric.ecgost.KeyAgreementSpi$ECVKO");
            provider.addAlgorithm("Alg.Alias.KeyAgreement." + CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410");
            provider.addAlgorithm("Alg.Alias.KeyAgreement.GOST-3410-2001", "ECGOST3410");
            provider.addAlgorithm("Alg.Alias.KeyAgreement." + CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_ESDH, "ECGOST3410");
            provider.addAlgorithm("AlgorithmParameters.ECGOST3410", "org.spongycastle.jcajce.provider.asymmetric.ecgost.AlgorithmParametersSpi");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.GOST-3410-2001", "ECGOST3410");
            addSignatureAlgorithm(provider, "GOST3411", "ECGOST3410", "org.spongycastle.jcajce.provider.asymmetric.ecgost.SignatureSpi", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
            provider.addAlgorithm("KeyFactory.ECGOST3410-2012", "org.spongycastle.jcajce.provider.asymmetric.ecgost12.KeyFactorySpi");
            provider.addAlgorithm("Alg.Alias.KeyFactory.GOST-3410-2012", "ECGOST3410-2012");
            provider.addAlgorithm("Alg.Alias.KeyFactory.ECGOST-3410-2012", "ECGOST3410-2012");
            registerOid(provider, RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, "ECGOST3410-2012", new org.spongycastle.jcajce.provider.asymmetric.ecgost12.KeyFactorySpi());
            registerOid(provider, RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_256, "ECGOST3410-2012", new org.spongycastle.jcajce.provider.asymmetric.ecgost12.KeyFactorySpi());
            registerOidAlgorithmParameters(provider, RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, "ECGOST3410-2012");
            registerOid(provider, RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, "ECGOST3410-2012", new org.spongycastle.jcajce.provider.asymmetric.ecgost12.KeyFactorySpi());
            registerOid(provider, RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_512, "ECGOST3410-2012", new org.spongycastle.jcajce.provider.asymmetric.ecgost12.KeyFactorySpi());
            registerOidAlgorithmParameters(provider, RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, "ECGOST3410-2012");
            provider.addAlgorithm("KeyPairGenerator.ECGOST3410-2012", "org.spongycastle.jcajce.provider.asymmetric.ecgost12.KeyPairGeneratorSpi");
            provider.addAlgorithm("Alg.Alias.KeyPairGenerator.ECGOST3410-2012", "ECGOST3410-2012");
            provider.addAlgorithm("Alg.Alias.KeyPairGenerator.GOST-3410-2012", "ECGOST3410-2012");
            provider.addAlgorithm("Signature.ECGOST3410-2012-256", "org.spongycastle.jcajce.provider.asymmetric.ecgost12.ECGOST2012SignatureSpi256");
            provider.addAlgorithm("Alg.Alias.Signature.ECGOST3410-2012-256", "ECGOST3410-2012-256");
            provider.addAlgorithm("Alg.Alias.Signature.GOST-3410-2012-256", "ECGOST3410-2012-256");
            addSignatureAlgorithm(provider, "GOST3411-2012-256", "ECGOST3410-2012-256", "org.spongycastle.jcajce.provider.asymmetric.ecgost12.ECGOST2012SignatureSpi256", RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256);
            provider.addAlgorithm("Signature.ECGOST3410-2012-512", "org.spongycastle.jcajce.provider.asymmetric.ecgost12.ECGOST2012SignatureSpi512");
            provider.addAlgorithm("Alg.Alias.Signature.ECGOST3410-2012-512", "ECGOST3410-2012-512");
            provider.addAlgorithm("Alg.Alias.Signature.GOST-3410-2012-512", "ECGOST3410-2012-512");
            addSignatureAlgorithm(provider, "GOST3411-2012-512", "ECGOST3410-2012-512", "org.spongycastle.jcajce.provider.asymmetric.ecgost12.ECGOST2012SignatureSpi512", RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512);
            provider.addAlgorithm("KeyAgreement.ECGOST3410-2012-256", "org.spongycastle.jcajce.provider.asymmetric.ecgost12.KeyAgreementSpi$ECVKO256");
            provider.addAlgorithm("KeyAgreement.ECGOST3410-2012-512", "org.spongycastle.jcajce.provider.asymmetric.ecgost12.KeyAgreementSpi$ECVKO512");
            provider.addAlgorithm("Alg.Alias.KeyAgreement." + RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_256, "ECGOST3410-2012-256");
            provider.addAlgorithm("Alg.Alias.KeyAgreement." + RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_512, "ECGOST3410-2012-512");
            provider.addAlgorithm("Alg.Alias.KeyAgreement." + RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, "ECGOST3410-2012-256");
            provider.addAlgorithm("Alg.Alias.KeyAgreement." + RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, "ECGOST3410-2012-512");
        }
    }
}
