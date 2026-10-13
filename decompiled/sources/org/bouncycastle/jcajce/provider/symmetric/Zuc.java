package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.engines.Zuc128Engine;
import org.bouncycastle.crypto.engines.Zuc256Engine;
import org.bouncycastle.crypto.macs.Zuc128Mac;
import org.bouncycastle.crypto.macs.Zuc256Mac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseStreamCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Zuc.class */
public class Zuc {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Zuc$AlgParams.class */
    public static class AlgParams extends IvAlgorithmParameters {
        @Override // org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters, java.security.AlgorithmParametersSpi
        protected String engineToString() {
            return "Zuc IV";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Zuc$KeyGen128.class */
    public static class KeyGen128 extends BaseKeyGenerator {
        public KeyGen128() {
            super("ZUC128", 128, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Zuc$KeyGen256.class */
    public static class KeyGen256 extends BaseKeyGenerator {
        public KeyGen256() {
            super("ZUC256", 256, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Zuc$Mappings.class */
    public static class Mappings extends SymmetricAlgorithmProvider {
        private static final String PREFIX = Zuc.class.getName();

        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.ZUC-128", PREFIX + "$Zuc128");
            configurableProvider.addAlgorithm("KeyGenerator.ZUC-128", PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("AlgorithmParameters.ZUC-128", PREFIX + "$AlgParams");
            configurableProvider.addAlgorithm("Cipher.ZUC-256", PREFIX + "$Zuc256");
            configurableProvider.addAlgorithm("KeyGenerator.ZUC-256", PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("AlgorithmParameters.ZUC-256", PREFIX + "$AlgParams");
            configurableProvider.addAlgorithm("Mac.ZUC-128", PREFIX + "$ZucMac128");
            configurableProvider.addAlgorithm("Mac.ZUC-256", PREFIX + "$ZucMac256");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.ZUC-256-128", "ZUC-256");
            configurableProvider.addAlgorithm("Mac.ZUC-256-64", PREFIX + "$ZucMac256_64");
            configurableProvider.addAlgorithm("Mac.ZUC-256-32", PREFIX + "$ZucMac256_32");
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Zuc$Zuc128.class */
    public static class Zuc128 extends BaseStreamCipher {
        public Zuc128() {
            super(new Zuc128Engine(), 16, 128);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Zuc$Zuc256.class */
    public static class Zuc256 extends BaseStreamCipher {
        public Zuc256() {
            super(new Zuc256Engine(), 25, 256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Zuc$ZucMac128.class */
    public static class ZucMac128 extends BaseMac {
        public ZucMac128() {
            super(new Zuc128Mac());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Zuc$ZucMac256.class */
    public static class ZucMac256 extends BaseMac {
        public ZucMac256() {
            super(new Zuc256Mac(128));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Zuc$ZucMac256_32.class */
    public static class ZucMac256_32 extends BaseMac {
        public ZucMac256_32() {
            super(new Zuc256Mac(32));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/Zuc$ZucMac256_64.class */
    public static class ZucMac256_64 extends BaseMac {
        public ZucMac256_64() {
            super(new Zuc256Mac(64));
        }
    }

    private Zuc() {
    }
}
