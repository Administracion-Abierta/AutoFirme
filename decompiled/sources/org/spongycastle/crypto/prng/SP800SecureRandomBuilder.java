package org.spongycastle.crypto.prng;

import java.security.SecureRandom;
import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.prng.drbg.CTRSP800DRBG;
import org.spongycastle.crypto.prng.drbg.HMacSP800DRBG;
import org.spongycastle.crypto.prng.drbg.HashSP800DRBG;
import org.spongycastle.crypto.prng.drbg.SP80090DRBG;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/prng/SP800SecureRandomBuilder.class */
public class SP800SecureRandomBuilder {
    private final SecureRandom random;
    private final EntropySourceProvider entropySourceProvider;
    private byte[] personalizationString;
    private int securityStrength;
    private int entropyBitsRequired;

    public SP800SecureRandomBuilder() {
        this(new SecureRandom(), false);
    }

    public SP800SecureRandomBuilder(SecureRandom entropySource, boolean predictionResistant) {
        this.securityStrength = 256;
        this.entropyBitsRequired = 256;
        this.random = entropySource;
        this.entropySourceProvider = new BasicEntropySourceProvider(this.random, predictionResistant);
    }

    public SP800SecureRandomBuilder(EntropySourceProvider entropySourceProvider) {
        this.securityStrength = 256;
        this.entropyBitsRequired = 256;
        this.random = null;
        this.entropySourceProvider = entropySourceProvider;
    }

    public SP800SecureRandomBuilder setPersonalizationString(byte[] personalizationString) {
        this.personalizationString = personalizationString;
        return this;
    }

    public SP800SecureRandomBuilder setSecurityStrength(int securityStrength) {
        this.securityStrength = securityStrength;
        return this;
    }

    public SP800SecureRandomBuilder setEntropyBitsRequired(int entropyBitsRequired) {
        this.entropyBitsRequired = entropyBitsRequired;
        return this;
    }

    public SP800SecureRandom buildHash(Digest digest, byte[] nonce, boolean predictionResistant) {
        return new SP800SecureRandom(this.random, this.entropySourceProvider.get(this.entropyBitsRequired), new HashDRBGProvider(digest, nonce, this.personalizationString, this.securityStrength), predictionResistant);
    }

    public SP800SecureRandom buildCTR(BlockCipher cipher, int keySizeInBits, byte[] nonce, boolean predictionResistant) {
        return new SP800SecureRandom(this.random, this.entropySourceProvider.get(this.entropyBitsRequired), new CTRDRBGProvider(cipher, keySizeInBits, nonce, this.personalizationString, this.securityStrength), predictionResistant);
    }

    public SP800SecureRandom buildHMAC(Mac hMac, byte[] nonce, boolean predictionResistant) {
        return new SP800SecureRandom(this.random, this.entropySourceProvider.get(this.entropyBitsRequired), new HMacDRBGProvider(hMac, nonce, this.personalizationString, this.securityStrength), predictionResistant);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/prng/SP800SecureRandomBuilder$HashDRBGProvider.class */
    private static class HashDRBGProvider implements DRBGProvider {
        private final Digest digest;
        private final byte[] nonce;
        private final byte[] personalizationString;
        private final int securityStrength;

        public HashDRBGProvider(Digest digest, byte[] nonce, byte[] personalizationString, int securityStrength) {
            this.digest = digest;
            this.nonce = nonce;
            this.personalizationString = personalizationString;
            this.securityStrength = securityStrength;
        }

        @Override // org.spongycastle.crypto.prng.DRBGProvider
        public SP80090DRBG get(EntropySource entropySource) {
            return new HashSP800DRBG(this.digest, this.securityStrength, entropySource, this.personalizationString, this.nonce);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/prng/SP800SecureRandomBuilder$HMacDRBGProvider.class */
    private static class HMacDRBGProvider implements DRBGProvider {
        private final Mac hMac;
        private final byte[] nonce;
        private final byte[] personalizationString;
        private final int securityStrength;

        public HMacDRBGProvider(Mac hMac, byte[] nonce, byte[] personalizationString, int securityStrength) {
            this.hMac = hMac;
            this.nonce = nonce;
            this.personalizationString = personalizationString;
            this.securityStrength = securityStrength;
        }

        @Override // org.spongycastle.crypto.prng.DRBGProvider
        public SP80090DRBG get(EntropySource entropySource) {
            return new HMacSP800DRBG(this.hMac, this.securityStrength, entropySource, this.personalizationString, this.nonce);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/prng/SP800SecureRandomBuilder$CTRDRBGProvider.class */
    private static class CTRDRBGProvider implements DRBGProvider {
        private final BlockCipher blockCipher;
        private final int keySizeInBits;
        private final byte[] nonce;
        private final byte[] personalizationString;
        private final int securityStrength;

        public CTRDRBGProvider(BlockCipher blockCipher, int keySizeInBits, byte[] nonce, byte[] personalizationString, int securityStrength) {
            this.blockCipher = blockCipher;
            this.keySizeInBits = keySizeInBits;
            this.nonce = nonce;
            this.personalizationString = personalizationString;
            this.securityStrength = securityStrength;
        }

        @Override // org.spongycastle.crypto.prng.DRBGProvider
        public SP80090DRBG get(EntropySource entropySource) {
            return new CTRSP800DRBG(this.blockCipher, this.keySizeInBits, this.securityStrength, entropySource, this.personalizationString, this.nonce);
        }
    }
}
