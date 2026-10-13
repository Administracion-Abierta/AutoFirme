package org.bouncycastle.jcajce.provider.drbg;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.SecureRandomSpi;
import java.security.Security;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropySourceProvider;
import org.bouncycastle.crypto.prng.SP800SecureRandom;
import org.bouncycastle.crypto.prng.SP800SecureRandomBuilder;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.ClassUtil;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Properties;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/drbg/DRBG.class */
public class DRBG {
    private static EntropyDaemon entropyDaemon;
    private static final String PREFIX = DRBG.class.getName();
    private static final String[][] initialEntropySourceNames = {new String[]{"sun.security.provider.Sun", "sun.security.provider.SecureRandom"}, new String[]{"org.apache.harmony.security.provider.crypto.CryptoProvider", "org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl"}, new String[]{"com.android.org.conscrypt.OpenSSLProvider", "com.android.org.conscrypt.OpenSSLRandom"}, new String[]{"org.conscrypt.OpenSSLProvider", "org.conscrypt.OpenSSLRandom"}};
    private static Thread entropyThread = null;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/drbg/DRBG$CoreSecureRandom.class */
    private static class CoreSecureRandom extends SecureRandom {
        CoreSecureRandom(Object[] objArr) {
            super((SecureRandomSpi) objArr[1], (Provider) objArr[0]);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/drbg/DRBG$Default.class */
    public static class Default extends SecureRandomSpi {
        private static final SecureRandom random = DRBG.createBaseRandom(true);

        @Override // java.security.SecureRandomSpi
        protected void engineSetSeed(byte[] bArr) {
            random.setSeed(bArr);
        }

        @Override // java.security.SecureRandomSpi
        protected void engineNextBytes(byte[] bArr) {
            random.nextBytes(bArr);
        }

        @Override // java.security.SecureRandomSpi
        protected byte[] engineGenerateSeed(int i) {
            return random.generateSeed(i);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/drbg/DRBG$HybridEntropySource.class */
    private static class HybridEntropySource implements EntropySource {
        private final SP800SecureRandom drbg;
        private final SignallingEntropySource entropySource;
        private final int bytesRequired;
        private final AtomicBoolean seedAvailable = new AtomicBoolean(false);
        private final AtomicInteger samples = new AtomicInteger(0);
        private final byte[] additionalInput = Pack.longToBigEndian(System.currentTimeMillis());

        /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/drbg/DRBG$HybridEntropySource$SignallingEntropySource.class */
        private static class SignallingEntropySource implements IncrementalEntropySource {
            private final EntropyDaemon entropyDaemon;
            private final AtomicBoolean seedAvailable;
            private final IncrementalEntropySource entropySource;
            private final int byteLength;
            private final AtomicReference entropy = new AtomicReference();
            private final AtomicBoolean scheduled = new AtomicBoolean(false);

            SignallingEntropySource(EntropyDaemon entropyDaemon, AtomicBoolean atomicBoolean, EntropySourceProvider entropySourceProvider, int i) {
                this.entropyDaemon = entropyDaemon;
                this.seedAvailable = atomicBoolean;
                this.entropySource = (IncrementalEntropySource) entropySourceProvider.get(i);
                this.byteLength = (i + 7) / 8;
            }

            @Override // org.bouncycastle.crypto.prng.EntropySource
            public boolean isPredictionResistant() {
                return true;
            }

            @Override // org.bouncycastle.crypto.prng.EntropySource
            public byte[] getEntropy() {
                try {
                    return getEntropy(0L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("initial entropy fetch interrupted");
                }
            }

            @Override // org.bouncycastle.jcajce.provider.drbg.IncrementalEntropySource
            public byte[] getEntropy(long j) throws InterruptedException {
                byte[] entropy = (byte[]) this.entropy.getAndSet(null);
                if (entropy == null || entropy.length != this.byteLength) {
                    entropy = this.entropySource.getEntropy(j);
                } else {
                    this.scheduled.set(false);
                }
                return entropy;
            }

            void schedule() {
                if (this.scheduled.getAndSet(true)) {
                    return;
                }
                this.entropyDaemon.addTask(new EntropyGatherer(this.entropySource, this.seedAvailable, this.entropy));
            }

            @Override // org.bouncycastle.crypto.prng.EntropySource
            public int entropySize() {
                return this.byteLength * 8;
            }
        }

        HybridEntropySource(EntropyDaemon entropyDaemon, int i) {
            EntropySourceProvider entropySourceProviderCreateCoreEntropySourceProvider = DRBG.createCoreEntropySourceProvider();
            this.bytesRequired = (i + 7) / 8;
            this.entropySource = new SignallingEntropySource(entropyDaemon, this.seedAvailable, entropySourceProviderCreateCoreEntropySourceProvider, 256);
            this.drbg = new SP800SecureRandomBuilder(new EntropySourceProvider() { // from class: org.bouncycastle.jcajce.provider.drbg.DRBG.HybridEntropySource.1
                @Override // org.bouncycastle.crypto.prng.EntropySourceProvider
                public EntropySource get(int i2) {
                    return HybridEntropySource.this.entropySource;
                }
            }).setPersonalizationString(Strings.toByteArray("Bouncy Castle Hybrid Entropy Source")).buildHMAC(new HMac(new SHA512Digest()), this.entropySource.getEntropy(), false);
        }

        @Override // org.bouncycastle.crypto.prng.EntropySource
        public boolean isPredictionResistant() {
            return true;
        }

        @Override // org.bouncycastle.crypto.prng.EntropySource
        public byte[] getEntropy() {
            byte[] bArr = new byte[this.bytesRequired];
            if (this.samples.getAndIncrement() > 128) {
                if (this.seedAvailable.getAndSet(false)) {
                    this.samples.set(0);
                    this.drbg.reseed(this.additionalInput);
                } else {
                    this.entropySource.schedule();
                }
            }
            this.drbg.nextBytes(bArr);
            return bArr;
        }

        @Override // org.bouncycastle.crypto.prng.EntropySource
        public int entropySize() {
            return this.bytesRequired * 8;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/drbg/DRBG$Mappings.class */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("SecureRandom.DEFAULT", DRBG.PREFIX + "$Default");
            configurableProvider.addAlgorithm("SecureRandom.NONCEANDIV", DRBG.PREFIX + "$NonceAndIV");
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/drbg/DRBG$NonceAndIV.class */
    public static class NonceAndIV extends SecureRandomSpi {
        private static final SecureRandom random = DRBG.createBaseRandom(false);

        @Override // java.security.SecureRandomSpi
        protected void engineSetSeed(byte[] bArr) {
            random.setSeed(bArr);
        }

        @Override // java.security.SecureRandomSpi
        protected void engineNextBytes(byte[] bArr) {
            random.nextBytes(bArr);
        }

        @Override // java.security.SecureRandomSpi
        protected byte[] engineGenerateSeed(int i) {
            return random.generateSeed(i);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/drbg/DRBG$OneShotHybridEntropySource.class */
    private static class OneShotHybridEntropySource implements EntropySource {
        private final SP800SecureRandom drbg;
        private final OneShotSignallingEntropySource entropySource;
        private final int bytesRequired;
        private final AtomicBoolean seedAvailable = new AtomicBoolean(false);
        private final AtomicInteger samples = new AtomicInteger(0);
        private final byte[] additionalInput = Pack.longToBigEndian(System.currentTimeMillis());

        /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/drbg/DRBG$OneShotHybridEntropySource$OneShotSignallingEntropySource.class */
        private static class OneShotSignallingEntropySource implements IncrementalEntropySource {
            private final AtomicBoolean seedAvailable;
            private final IncrementalEntropySource entropySource;
            private final int byteLength;
            private final AtomicReference entropy = new AtomicReference();
            private final AtomicBoolean scheduled = new AtomicBoolean(false);

            OneShotSignallingEntropySource(AtomicBoolean atomicBoolean, EntropySourceProvider entropySourceProvider, int i) {
                this.seedAvailable = atomicBoolean;
                this.entropySource = (IncrementalEntropySource) entropySourceProvider.get(i);
                this.byteLength = (i + 7) / 8;
            }

            @Override // org.bouncycastle.crypto.prng.EntropySource
            public boolean isPredictionResistant() {
                return true;
            }

            @Override // org.bouncycastle.crypto.prng.EntropySource
            public byte[] getEntropy() {
                try {
                    return getEntropy(0L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("initial entropy fetch interrupted");
                }
            }

            @Override // org.bouncycastle.jcajce.provider.drbg.IncrementalEntropySource
            public byte[] getEntropy(long j) throws InterruptedException {
                byte[] entropy = (byte[]) this.entropy.getAndSet(null);
                if (entropy == null || entropy.length != this.byteLength) {
                    entropy = this.entropySource.getEntropy(j);
                } else {
                    this.scheduled.set(false);
                }
                return entropy;
            }

            void schedule() {
                if (this.scheduled.getAndSet(true)) {
                    return;
                }
                Thread thread = new Thread(new EntropyGatherer(this.entropySource, this.seedAvailable, this.entropy));
                thread.setDaemon(true);
                thread.start();
            }

            @Override // org.bouncycastle.crypto.prng.EntropySource
            public int entropySize() {
                return this.byteLength * 8;
            }
        }

        OneShotHybridEntropySource(int i) {
            EntropySourceProvider entropySourceProviderCreateCoreEntropySourceProvider = DRBG.createCoreEntropySourceProvider();
            this.bytesRequired = (i + 7) / 8;
            this.entropySource = new OneShotSignallingEntropySource(this.seedAvailable, entropySourceProviderCreateCoreEntropySourceProvider, 256);
            this.drbg = new SP800SecureRandomBuilder(new EntropySourceProvider() { // from class: org.bouncycastle.jcajce.provider.drbg.DRBG.OneShotHybridEntropySource.1
                @Override // org.bouncycastle.crypto.prng.EntropySourceProvider
                public EntropySource get(int i2) {
                    return OneShotHybridEntropySource.this.entropySource;
                }
            }).setPersonalizationString(Strings.toByteArray("Bouncy Castle Hybrid Entropy Source")).buildHMAC(new HMac(new SHA512Digest()), this.entropySource.getEntropy(), false);
        }

        @Override // org.bouncycastle.crypto.prng.EntropySource
        public boolean isPredictionResistant() {
            return true;
        }

        @Override // org.bouncycastle.crypto.prng.EntropySource
        public byte[] getEntropy() {
            byte[] bArr = new byte[this.bytesRequired];
            if (this.samples.getAndIncrement() > 1024) {
                if (this.seedAvailable.getAndSet(false)) {
                    this.samples.set(0);
                    this.drbg.reseed(this.additionalInput);
                } else {
                    this.entropySource.schedule();
                }
            }
            this.drbg.nextBytes(bArr);
            return bArr;
        }

        @Override // org.bouncycastle.crypto.prng.EntropySource
        public int entropySize() {
            return this.bytesRequired * 8;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/drbg/DRBG$URLSeededEntropySourceProvider.class */
    private static class URLSeededEntropySourceProvider implements EntropySourceProvider {
        private final InputStream seedStream;

        URLSeededEntropySourceProvider(final URL url) {
            this.seedStream = (InputStream) AccessController.doPrivileged(new PrivilegedAction<InputStream>() { // from class: org.bouncycastle.jcajce.provider.drbg.DRBG.URLSeededEntropySourceProvider.1
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.security.PrivilegedAction
                public InputStream run() {
                    try {
                        return url.openStream();
                    } catch (IOException e) {
                        throw new IllegalStateException("unable to open random source");
                    }
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int privilegedRead(final byte[] bArr, final int i, final int i2) {
            return ((Integer) AccessController.doPrivileged(new PrivilegedAction<Integer>() { // from class: org.bouncycastle.jcajce.provider.drbg.DRBG.URLSeededEntropySourceProvider.2
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.security.PrivilegedAction
                public Integer run() {
                    try {
                        return Integer.valueOf(URLSeededEntropySourceProvider.this.seedStream.read(bArr, i, i2));
                    } catch (IOException e) {
                        throw new InternalError("unable to read random source");
                    }
                }
            })).intValue();
        }

        @Override // org.bouncycastle.crypto.prng.EntropySourceProvider
        public EntropySource get(final int i) {
            return new IncrementalEntropySource() { // from class: org.bouncycastle.jcajce.provider.drbg.DRBG.URLSeededEntropySourceProvider.3
                private final int numBytes;

                {
                    this.numBytes = (i + 7) / 8;
                }

                @Override // org.bouncycastle.crypto.prng.EntropySource
                public boolean isPredictionResistant() {
                    return true;
                }

                @Override // org.bouncycastle.crypto.prng.EntropySource
                public byte[] getEntropy() {
                    try {
                        return getEntropy(0L);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException("initial entropy fetch interrupted");
                    }
                }

                @Override // org.bouncycastle.jcajce.provider.drbg.IncrementalEntropySource
                public byte[] getEntropy(long j) throws InterruptedException {
                    int iPrivilegedRead;
                    byte[] bArr = new byte[this.numBytes];
                    int i2 = 0;
                    while (i2 != bArr.length && (iPrivilegedRead = URLSeededEntropySourceProvider.this.privilegedRead(bArr, i2, bArr.length - i2)) > -1) {
                        i2 += iPrivilegedRead;
                        DRBG.sleep(j);
                    }
                    if (i2 != bArr.length) {
                        throw new InternalError("unable to fully read random source");
                    }
                    return bArr;
                }

                @Override // org.bouncycastle.crypto.prng.EntropySource
                public int entropySize() {
                    return i;
                }
            };
        }
    }

    private static final Object[] findSource() {
        for (int i = 0; i < initialEntropySourceNames.length; i++) {
            String[] strArr = initialEntropySourceNames[i];
            try {
                return new Object[]{Class.forName(strArr[0]).newInstance(), Class.forName(strArr[1]).newInstance()};
            } catch (Throwable th) {
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static SecureRandom createBaseRandom(boolean z) {
        if (Properties.getPropertyValue("org.bouncycastle.drbg.entropysource") != null) {
            EntropySourceProvider entropySourceProviderCreateEntropySource = createEntropySource();
            EntropySource entropySource = entropySourceProviderCreateEntropySource.get(128);
            return new SP800SecureRandomBuilder(entropySourceProviderCreateEntropySource).setPersonalizationString(z ? generateDefaultPersonalizationString(entropySource.getEntropy()) : generateNonceIVPersonalizationString(entropySource.getEntropy())).buildHash(new SHA512Digest(), entropySource.getEntropy(), z);
        }
        if (!Properties.isOverrideSet("org.bouncycastle.drbg.entropy_thread")) {
            OneShotHybridEntropySource oneShotHybridEntropySource = new OneShotHybridEntropySource(256);
            return new SP800SecureRandomBuilder(new EntropySourceProvider() { // from class: org.bouncycastle.jcajce.provider.drbg.DRBG.2
                @Override // org.bouncycastle.crypto.prng.EntropySourceProvider
                public EntropySource get(int i) {
                    return new OneShotHybridEntropySource(i);
                }
            }).setPersonalizationString(z ? generateDefaultPersonalizationString(oneShotHybridEntropySource.getEntropy()) : generateNonceIVPersonalizationString(oneShotHybridEntropySource.getEntropy())).buildHash(new SHA512Digest(), oneShotHybridEntropySource.getEntropy(), z);
        }
        synchronized (entropyDaemon) {
            if (entropyThread == null) {
                entropyThread = new Thread(entropyDaemon, "BC Entropy Daemon");
                entropyThread.setDaemon(true);
                entropyThread.start();
            }
        }
        HybridEntropySource hybridEntropySource = new HybridEntropySource(entropyDaemon, 256);
        return new SP800SecureRandomBuilder(new EntropySourceProvider() { // from class: org.bouncycastle.jcajce.provider.drbg.DRBG.1
            @Override // org.bouncycastle.crypto.prng.EntropySourceProvider
            public EntropySource get(int i) {
                return new HybridEntropySource(DRBG.entropyDaemon, i);
            }
        }).setPersonalizationString(z ? generateDefaultPersonalizationString(hybridEntropySource.getEntropy()) : generateNonceIVPersonalizationString(hybridEntropySource.getEntropy())).buildHash(new SHA512Digest(), hybridEntropySource.getEntropy(), z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static EntropySourceProvider createCoreEntropySourceProvider() {
        SecureRandom secureRandom;
        if (((Boolean) AccessController.doPrivileged(new PrivilegedAction<Boolean>() { // from class: org.bouncycastle.jcajce.provider.drbg.DRBG.3
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.security.PrivilegedAction
            public Boolean run() {
                try {
                    return Boolean.valueOf(SecureRandom.class.getMethod("getInstanceStrong", new Class[0]) != null);
                } catch (Exception e) {
                    return false;
                }
            }
        })).booleanValue() && (secureRandom = (SecureRandom) AccessController.doPrivileged(new PrivilegedAction<SecureRandom>() { // from class: org.bouncycastle.jcajce.provider.drbg.DRBG.4
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.security.PrivilegedAction
            public SecureRandom run() {
                try {
                    return (SecureRandom) SecureRandom.class.getMethod("getInstanceStrong", new Class[0]).invoke(null, new Object[0]);
                } catch (Exception e) {
                    return null;
                }
            }
        })) != null) {
            return new IncrementalEntropySourceProvider(secureRandom, true);
        }
        return createInitialEntropySource();
    }

    private static EntropySourceProvider createInitialEntropySource() {
        String str = (String) AccessController.doPrivileged(new PrivilegedAction<String>() { // from class: org.bouncycastle.jcajce.provider.drbg.DRBG.5
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.security.PrivilegedAction
            public String run() {
                return Security.getProperty("securerandom.source");
            }
        });
        if (str == null) {
            return new IncrementalEntropySourceProvider(new CoreSecureRandom(findSource()), true);
        }
        try {
            return new URLSeededEntropySourceProvider(new URL(str));
        } catch (Exception e) {
            return new IncrementalEntropySourceProvider(new CoreSecureRandom(findSource()), true);
        }
    }

    private static EntropySourceProvider createEntropySource() {
        final String propertyValue = Properties.getPropertyValue("org.bouncycastle.drbg.entropysource");
        return (EntropySourceProvider) AccessController.doPrivileged(new PrivilegedAction<EntropySourceProvider>() { // from class: org.bouncycastle.jcajce.provider.drbg.DRBG.6
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.security.PrivilegedAction
            public EntropySourceProvider run() {
                try {
                    return (EntropySourceProvider) ClassUtil.loadClass(DRBG.class, propertyValue).newInstance();
                } catch (Exception e) {
                    throw new IllegalStateException("entropy source " + propertyValue + " not created: " + e.getMessage(), e);
                }
            }
        });
    }

    private static byte[] generateDefaultPersonalizationString(byte[] bArr) {
        return Arrays.concatenate(Strings.toByteArray("Default"), bArr, Pack.longToBigEndian(Thread.currentThread().getId()), Pack.longToBigEndian(System.currentTimeMillis()));
    }

    private static byte[] generateNonceIVPersonalizationString(byte[] bArr) {
        return Arrays.concatenate(Strings.toByteArray("Nonce"), bArr, Pack.longToLittleEndian(Thread.currentThread().getId()), Pack.longToLittleEndian(System.currentTimeMillis()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void sleep(long j) throws InterruptedException {
        if (j != 0) {
            Thread.sleep(j);
        }
    }

    static {
        entropyDaemon = null;
        entropyDaemon = new EntropyDaemon();
    }
}
