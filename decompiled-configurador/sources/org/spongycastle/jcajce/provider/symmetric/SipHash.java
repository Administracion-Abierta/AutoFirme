package org.spongycastle.jcajce.provider.symmetric;

import org.spongycastle.crypto.CipherKeyGenerator;
import org.spongycastle.jcajce.provider.config.ConfigurableProvider;
import org.spongycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.spongycastle.jcajce.provider.symmetric.util.BaseMac;
import org.spongycastle.jcajce.provider.util.AlgorithmProvider;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/symmetric/SipHash.class */
public final class SipHash {
    private SipHash() {
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/symmetric/SipHash$Mac24.class */
    public static class Mac24 extends BaseMac {
        public Mac24() {
            super(new org.spongycastle.crypto.macs.SipHash());
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/symmetric/SipHash$Mac48.class */
    public static class Mac48 extends BaseMac {
        public Mac48() {
            super(new org.spongycastle.crypto.macs.SipHash(4, 8));
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/symmetric/SipHash$KeyGen.class */
    public static class KeyGen extends BaseKeyGenerator {
        public KeyGen() {
            super("SipHash", 128, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/symmetric/SipHash$Mappings.class */
    public static class Mappings extends AlgorithmProvider {
        private static final String PREFIX = SipHash.class.getName();

        @Override // org.spongycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("Mac.SIPHASH-2-4", PREFIX + "$Mac24");
            provider.addAlgorithm("Alg.Alias.Mac.SIPHASH", "SIPHASH-2-4");
            provider.addAlgorithm("Mac.SIPHASH-4-8", PREFIX + "$Mac48");
            provider.addAlgorithm("KeyGenerator.SIPHASH", PREFIX + "$KeyGen");
            provider.addAlgorithm("Alg.Alias.KeyGenerator.SIPHASH-2-4", "SIPHASH");
            provider.addAlgorithm("Alg.Alias.KeyGenerator.SIPHASH-4-8", "SIPHASH");
        }
    }
}
