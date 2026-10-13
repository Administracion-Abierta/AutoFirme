package org.spongycastle.jcajce.provider.drbg;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.SecureRandomSpi;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.spongycastle.crypto.digests.SHA512Digest;
import org.spongycastle.crypto.macs.HMac;
import org.spongycastle.crypto.prng.EntropySource;
import org.spongycastle.crypto.prng.EntropySourceProvider;
import org.spongycastle.crypto.prng.SP800SecureRandom;
import org.spongycastle.crypto.prng.SP800SecureRandomBuilder;
import org.spongycastle.jcajce.provider.config.ConfigurableProvider;
import org.spongycastle.jcajce.provider.symmetric.util.ClassUtil;
import org.spongycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Pack;
import org.spongycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/drbg/DRBG.class */
public class DRBG {
    private static final String PREFIX = DRBG.class.getName();
    private static final String[][] initialEntropySourceNames = {new String[]{"sun.security.provider.Sun", "sun.security.provider.SecureRandom"}, new String[]{"org.apache.harmony.security.provider.crypto.CryptoProvider", "org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl"}, new String[]{"com.android.org.conscrypt.OpenSSLProvider", "com.android.org.conscrypt.OpenSSLRandom"}, new String[]{"org.conscrypt.OpenSSLProvider", "org.conscrypt.OpenSSLRandom"}};
    private static final Object[] initialEntropySourceAndSpi = findSource();

    private static final Object[] findSource() {
        for (int t = 0; t < initialEntropySourceNames.length; t++) {
            String[] pair = initialEntropySourceNames[t];
            try {
                Object[] r = {Class.forName(pair[0]).newInstance(), Class.forName(pair[1]).newInstance()};
                return r;
            } catch (Throwable th) {
            }
        }
        return null;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/drbg/DRBG$CoreSecureRandom.class */
    private static class CoreSecureRandom extends SecureRandom {
        CoreSecureRandom() {
            super((SecureRandomSpi) DRBG.initialEntropySourceAndSpi[1], (Provider) DRBG.initialEntropySourceAndSpi[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static SecureRandom createInitialEntropySource() {
        if (initialEntropySourceAndSpi != null) {
            return new CoreSecureRandom();
        }
        return new SecureRandom();
    }

    private static EntropySourceProvider createEntropySource() {
        final String sourceClass = System.getProperty("org.spongycastle.drbg.entropysource");
        return (EntropySourceProvider) AccessController.doPrivileged(new PrivilegedAction<EntropySourceProvider>() { // from class: org.spongycastle.jcajce.provider.drbg.DRBG.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.security.PrivilegedAction
            public EntropySourceProvider run() {
                try {
                    Class clazz = ClassUtil.loadClass(DRBG.class, sourceClass);
                    return (EntropySourceProvider) clazz.newInstance();
                } catch (Exception e) {
                    throw new IllegalStateException("entropy source " + sourceClass + " not created: " + e.getMessage(), e);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static SecureRandom createBaseRandom(boolean isPredictionResistant) {
        if (System.getProperty("org.spongycastle.drbg.entropysource") != null) {
            EntropySourceProvider entropyProvider = createEntropySource();
            EntropySource initSource = entropyProvider.get(128);
            byte[] personalisationString = isPredictionResistant ? generateDefaultPersonalizationString(initSource.getEntropy()) : generateNonceIVPersonalizationString(initSource.getEntropy());
            return new SP800SecureRandomBuilder(entropyProvider).setPersonalizationString(personalisationString).buildHash(new SHA512Digest(), Arrays.concatenate(initSource.getEntropy(), initSource.getEntropy()), isPredictionResistant);
        }
        SecureRandom randomSource = new HybridSecureRandom();
        byte[] personalisationString2 = isPredictionResistant ? generateDefaultPersonalizationString(randomSource.generateSeed(16)) : generateNonceIVPersonalizationString(randomSource.generateSeed(16));
        return new SP800SecureRandomBuilder(randomSource, true).setPersonalizationString(personalisationString2).buildHash(new SHA512Digest(), randomSource.generateSeed(32), isPredictionResistant);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/drbg/DRBG$Default.class */
    public static class Default extends SecureRandomSpi {
        private static final SecureRandom random = DRBG.createBaseRandom(true);

        @Override // java.security.SecureRandomSpi
        protected void engineSetSeed(byte[] bytes) {
            random.setSeed(bytes);
        }

        @Override // java.security.SecureRandomSpi
        protected void engineNextBytes(byte[] bytes) {
            random.nextBytes(bytes);
        }

        @Override // java.security.SecureRandomSpi
        protected byte[] engineGenerateSeed(int numBytes) {
            return random.generateSeed(numBytes);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/drbg/DRBG$NonceAndIV.class */
    public static class NonceAndIV extends SecureRandomSpi {
        private static final SecureRandom random = DRBG.createBaseRandom(false);

        @Override // java.security.SecureRandomSpi
        protected void engineSetSeed(byte[] bytes) {
            random.setSeed(bytes);
        }

        @Override // java.security.SecureRandomSpi
        protected void engineNextBytes(byte[] bytes) {
            random.nextBytes(bytes);
        }

        @Override // java.security.SecureRandomSpi
        protected byte[] engineGenerateSeed(int numBytes) {
            return random.generateSeed(numBytes);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/drbg/DRBG$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.spongycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("SecureRandom.DEFAULT", DRBG.PREFIX + "$Default");
            provider.addAlgorithm("SecureRandom.NONCEANDIV", DRBG.PREFIX + "$NonceAndIV");
        }
    }

    private static byte[] generateDefaultPersonalizationString(byte[] seed) {
        return Arrays.concatenate(Strings.toByteArray("Default"), seed, Pack.longToBigEndian(Thread.currentThread().getId()), Pack.longToBigEndian(System.currentTimeMillis()));
    }

    private static byte[] generateNonceIVPersonalizationString(byte[] seed) {
        return Arrays.concatenate(Strings.toByteArray("Nonce"), seed, Pack.longToLittleEndian(Thread.currentThread().getId()), Pack.longToLittleEndian(System.currentTimeMillis()));
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/drbg/DRBG$HybridSecureRandom.class */
    private static class HybridSecureRandom extends SecureRandom {
        private final AtomicBoolean seedAvailable;
        private final AtomicInteger samples;
        private final SecureRandom baseRandom;
        private final SP800SecureRandom drbg;

        HybridSecureRandom() {
            super(null, null);
            this.seedAvailable = new AtomicBoolean(false);
            this.samples = new AtomicInteger(0);
            this.baseRandom = DRBG.createInitialEntropySource();
            this.drbg = new SP800SecureRandomBuilder(new EntropySourceProvider() { // from class: org.spongycastle.jcajce.provider.drbg.DRBG.HybridSecureRandom.1
                @Override // org.spongycastle.crypto.prng.EntropySourceProvider
                public EntropySource get(int bitsRequired) {
                    return HybridSecureRandom.this.new SignallingEntropySource(bitsRequired);
                }
            }).setPersonalizationString(Strings.toByteArray("Bouncy Castle Hybrid Entropy Source")).buildHMAC(new HMac(new SHA512Digest()), this.baseRandom.generateSeed(32), false);
        }

        @Override // java.security.SecureRandom
        public void setSeed(byte[] seed) {
            if (this.drbg != null) {
                this.drbg.setSeed(seed);
            }
        }

        @Override // java.security.SecureRandom, java.util.Random
        public void setSeed(long seed) {
            if (this.drbg != null) {
                this.drbg.setSeed(seed);
            }
        }

        @Override // java.security.SecureRandom
        public byte[] generateSeed(int numBytes) {
            byte[] data = new byte[numBytes];
            if (this.samples.getAndIncrement() > 20 && this.seedAvailable.getAndSet(false)) {
                this.samples.set(0);
                this.drbg.reseed(null);
            }
            this.drbg.nextBytes(data);
            return data;
        }

        /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/drbg/DRBG$HybridSecureRandom$SignallingEntropySource.class */
        private class SignallingEntropySource implements EntropySource {
            private final int byteLength;
            private final AtomicReference entropy = new AtomicReference();
            private final AtomicBoolean scheduled = new AtomicBoolean(false);

            SignallingEntropySource(int bitsRequired) {
                this.byteLength = (bitsRequired + 7) / 8;
            }

            @Override // org.spongycastle.crypto.prng.EntropySource
            public boolean isPredictionResistant() {
                return true;
            }

            @Override // org.spongycastle.crypto.prng.EntropySource
            public byte[] getEntropy() {
                byte[] seed = (byte[]) this.entropy.getAndSet(null);
                if (seed == null || seed.length != this.byteLength) {
                    seed = HybridSecureRandom.this.baseRandom.generateSeed(this.byteLength);
                } else {
                    this.scheduled.set(false);
                }
                if (!this.scheduled.getAndSet(true)) {
                    new Thread(new EntropyGatherer(this.byteLength)).start();
                }
                return seed;
            }

            @Override // org.spongycastle.crypto.prng.EntropySource
            public int entropySize() {
                return this.byteLength * 8;
            }

            /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/drbg/DRBG$HybridSecureRandom$SignallingEntropySource$EntropyGatherer.class */
            private class EntropyGatherer implements Runnable {
                private final int numBytes;

                EntropyGatherer(int numBytes) {
                    this.numBytes = numBytes;
                }

                @Override // java.lang.Runnable
                public void run() {
                    SignallingEntropySource.this.entropy.set(HybridSecureRandom.this.baseRandom.generateSeed(this.numBytes));
                    HybridSecureRandom.this.seedAvailable.set(true);
                }
            }
        }
    }
}
