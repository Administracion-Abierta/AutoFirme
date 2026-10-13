package org.spongycastle.jcajce.provider.keystore;

import org.spongycastle.jcajce.provider.config.ConfigurableProvider;
import org.spongycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/keystore/BCFKS.class */
public class BCFKS {
    private static final String PREFIX = "org.spongycastle.jcajce.provider.keystore.bcfks.";

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/keystore/BCFKS$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.spongycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("KeyStore.BCFKS", "org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi$Std");
            provider.addAlgorithm("KeyStore.BCFKS-DEF", "org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi$Def");
        }
    }
}
