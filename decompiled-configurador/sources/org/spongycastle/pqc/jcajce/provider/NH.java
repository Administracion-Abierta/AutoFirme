package org.spongycastle.pqc.jcajce.provider;

import org.spongycastle.jcajce.provider.config.ConfigurableProvider;
import org.spongycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.spongycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.spongycastle.pqc.asn1.PQCObjectIdentifiers;
import org.spongycastle.pqc.jcajce.provider.newhope.NHKeyFactorySpi;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/jcajce/provider/NH.class */
public class NH {
    private static final String PREFIX = "org.spongycastle.pqc.jcajce.provider.newhope.";

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/jcajce/provider/NH$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.spongycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("KeyFactory.NH", "org.spongycastle.pqc.jcajce.provider.newhope.NHKeyFactorySpi");
            provider.addAlgorithm("KeyPairGenerator.NH", "org.spongycastle.pqc.jcajce.provider.newhope.NHKeyPairGeneratorSpi");
            provider.addAlgorithm("KeyAgreement.NH", "org.spongycastle.pqc.jcajce.provider.newhope.KeyAgreementSpi");
            AsymmetricKeyInfoConverter keyFact = new NHKeyFactorySpi();
            registerOid(provider, PQCObjectIdentifiers.newHope, "NH", keyFact);
        }
    }
}
