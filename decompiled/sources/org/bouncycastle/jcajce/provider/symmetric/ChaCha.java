package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.engines.ChaCha7539Engine;
import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.crypto.modes.AEADCipher;
import org.bouncycastle.crypto.modes.ChaCha20Poly1305;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseStreamCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ChaCha.class */
public final class ChaCha {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ChaCha$AlgParams.class */
    public static class AlgParams extends IvAlgorithmParameters {
        @Override // org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters, java.security.AlgorithmParametersSpi
        protected String engineToString() {
            return "ChaCha7539 IV";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ChaCha$AlgParamsCC1305.class */
    public static class AlgParamsCC1305 extends IvAlgorithmParameters {
        @Override // org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters, java.security.AlgorithmParametersSpi
        protected String engineToString() {
            return "ChaCha20-Poly1305 IV";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ChaCha$Base.class */
    public static class Base extends BaseStreamCipher {
        public Base() {
            super(new ChaChaEngine(), 8);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ChaCha$Base7539.class */
    public static class Base7539 extends BaseStreamCipher {
        public Base7539() {
            super(new ChaCha7539Engine(), 12);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ChaCha$BaseCC20P1305.class */
    public static class BaseCC20P1305 extends BaseBlockCipher {
        public BaseCC20P1305() {
            super((AEADCipher) new ChaCha20Poly1305(), true, 12);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ChaCha$KeyGen.class */
    public static class KeyGen extends BaseKeyGenerator {
        public KeyGen() {
            super("ChaCha", 128, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ChaCha$KeyGen7539.class */
    public static class KeyGen7539 extends BaseKeyGenerator {
        public KeyGen7539() {
            super("ChaCha7539", 256, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ChaCha$Mappings.class */
    public static class Mappings extends AlgorithmProvider {
        private static final String PREFIX = ChaCha.class.getName();

        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.CHACHA", PREFIX + "$Base");
            configurableProvider.addAlgorithm("KeyGenerator.CHACHA", PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("Cipher.CHACHA7539", PREFIX + "$Base7539");
            configurableProvider.addAlgorithm("KeyGenerator.CHACHA7539", PREFIX + "$KeyGen7539");
            configurableProvider.addAlgorithm("AlgorithmParameters.CHACHA7539", PREFIX + "$AlgParams");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.CHACHA20", "CHACHA7539");
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.CHACHA20", "CHACHA7539");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.CHACHA20", "CHACHA7539");
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.CHACHA20-POLY1305", "CHACHA7539");
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator." + PKCSObjectIdentifiers.id_alg_AEADChaCha20Poly1305, "CHACHA7539");
            configurableProvider.addAlgorithm("Cipher.CHACHA20-POLY1305", PREFIX + "$BaseCC20P1305");
            configurableProvider.addAlgorithm("AlgorithmParameters.CHACHA20-POLY1305", PREFIX + "$AlgParamsCC1305");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher." + PKCSObjectIdentifiers.id_alg_AEADChaCha20Poly1305, "CHACHA20-POLY1305");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + PKCSObjectIdentifiers.id_alg_AEADChaCha20Poly1305, "CHACHA20-POLY1305");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.OID." + PKCSObjectIdentifiers.id_alg_AEADChaCha20Poly1305, "CHACHA20-POLY1305");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.OID." + PKCSObjectIdentifiers.id_alg_AEADChaCha20Poly1305, "CHACHA20-POLY1305");
        }
    }

    private ChaCha() {
    }
}
