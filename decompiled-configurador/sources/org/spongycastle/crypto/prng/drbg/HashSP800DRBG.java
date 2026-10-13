package org.spongycastle.crypto.prng.drbg;

import java.util.Hashtable;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.prng.EntropySource;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Integers;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/prng/drbg/HashSP800DRBG.class */
public class HashSP800DRBG implements SP80090DRBG {
    private static final long RESEED_MAX = 140737488355328L;
    private static final int MAX_BITS_REQUEST = 262144;
    private Digest _digest;
    private byte[] _V;
    private byte[] _C;
    private long _reseedCounter;
    private EntropySource _entropySource;
    private int _securityStrength;
    private int _seedLength;
    private static final byte[] ONE = {1};
    private static final Hashtable seedlens = new Hashtable();

    static {
        seedlens.put("SHA-1", Integers.valueOf(440));
        seedlens.put("SHA-224", Integers.valueOf(440));
        seedlens.put("SHA-256", Integers.valueOf(440));
        seedlens.put(MessageDigestAlgorithms.SHA_512_256, Integers.valueOf(440));
        seedlens.put(MessageDigestAlgorithms.SHA_512_224, Integers.valueOf(440));
        seedlens.put("SHA-384", Integers.valueOf(888));
        seedlens.put("SHA-512", Integers.valueOf(888));
    }

    public HashSP800DRBG(Digest digest, int securityStrength, EntropySource entropySource, byte[] personalizationString, byte[] nonce) {
        if (securityStrength > Utils.getMaxSecurityStrength(digest)) {
            throw new IllegalArgumentException("Requested security strength is not supported by the derivation function");
        }
        if (entropySource.entropySize() < securityStrength) {
            throw new IllegalArgumentException("Not enough entropy for security strength required");
        }
        this._digest = digest;
        this._entropySource = entropySource;
        this._securityStrength = securityStrength;
        this._seedLength = ((Integer) seedlens.get(digest.getAlgorithmName())).intValue();
        byte[] entropy = getEntropy();
        byte[] seedMaterial = Arrays.concatenate(entropy, nonce, personalizationString);
        byte[] seed = Utils.hash_df(this._digest, seedMaterial, this._seedLength);
        this._V = seed;
        byte[] subV = new byte[this._V.length + 1];
        System.arraycopy(this._V, 0, subV, 1, this._V.length);
        this._C = Utils.hash_df(this._digest, subV, this._seedLength);
        this._reseedCounter = 1L;
    }

    @Override // org.spongycastle.crypto.prng.drbg.SP80090DRBG
    public int getBlockSize() {
        return this._digest.getDigestSize() * 8;
    }

    @Override // org.spongycastle.crypto.prng.drbg.SP80090DRBG
    public int generate(byte[] output, byte[] additionalInput, boolean predictionResistant) {
        int numberOfBits = output.length * 8;
        if (numberOfBits > 262144) {
            throw new IllegalArgumentException("Number of bits per request limited to 262144");
        }
        if (this._reseedCounter > RESEED_MAX) {
            return -1;
        }
        if (predictionResistant) {
            reseed(additionalInput);
            additionalInput = null;
        }
        if (additionalInput != null) {
            byte[] newInput = new byte[1 + this._V.length + additionalInput.length];
            newInput[0] = 2;
            System.arraycopy(this._V, 0, newInput, 1, this._V.length);
            System.arraycopy(additionalInput, 0, newInput, 1 + this._V.length, additionalInput.length);
            byte[] w = hash(newInput);
            addTo(this._V, w);
        }
        byte[] rv = hashgen(this._V, numberOfBits);
        byte[] subH = new byte[this._V.length + 1];
        System.arraycopy(this._V, 0, subH, 1, this._V.length);
        subH[0] = 3;
        byte[] H = hash(subH);
        addTo(this._V, H);
        addTo(this._V, this._C);
        byte[] c = {(byte) (this._reseedCounter >> 24), (byte) (this._reseedCounter >> 16), (byte) (this._reseedCounter >> 8), (byte) this._reseedCounter};
        addTo(this._V, c);
        this._reseedCounter++;
        System.arraycopy(rv, 0, output, 0, output.length);
        return numberOfBits;
    }

    private byte[] getEntropy() {
        byte[] entropy = this._entropySource.getEntropy();
        if (entropy.length < (this._securityStrength + 7) / 8) {
            throw new IllegalStateException("Insufficient entropy provided by entropy source");
        }
        return entropy;
    }

    private void addTo(byte[] longer, byte[] shorter) {
        int carry = 0;
        for (int i = 1; i <= shorter.length; i++) {
            int res = (longer[longer.length - i] & 255) + (shorter[shorter.length - i] & 255) + carry;
            carry = res > 255 ? 1 : 0;
            longer[longer.length - i] = (byte) res;
        }
        for (int i2 = shorter.length + 1; i2 <= longer.length; i2++) {
            int res2 = (longer[longer.length - i2] & 255) + carry;
            carry = res2 > 255 ? 1 : 0;
            longer[longer.length - i2] = (byte) res2;
        }
    }

    @Override // org.spongycastle.crypto.prng.drbg.SP80090DRBG
    public void reseed(byte[] additionalInput) {
        byte[] entropy = getEntropy();
        byte[] seedMaterial = Arrays.concatenate(ONE, this._V, entropy, additionalInput);
        byte[] seed = Utils.hash_df(this._digest, seedMaterial, this._seedLength);
        this._V = seed;
        byte[] subV = new byte[this._V.length + 1];
        subV[0] = 0;
        System.arraycopy(this._V, 0, subV, 1, this._V.length);
        this._C = Utils.hash_df(this._digest, subV, this._seedLength);
        this._reseedCounter = 1L;
    }

    private byte[] hash(byte[] input) {
        byte[] hash = new byte[this._digest.getDigestSize()];
        doHash(input, hash);
        return hash;
    }

    private void doHash(byte[] input, byte[] output) {
        this._digest.update(input, 0, input.length);
        this._digest.doFinal(output, 0);
    }

    private byte[] hashgen(byte[] input, int lengthInBits) {
        int digestSize = this._digest.getDigestSize();
        int m = (lengthInBits / 8) / digestSize;
        byte[] data = new byte[input.length];
        System.arraycopy(input, 0, data, 0, input.length);
        byte[] W = new byte[lengthInBits / 8];
        byte[] dig = new byte[this._digest.getDigestSize()];
        for (int i = 0; i <= m; i++) {
            doHash(data, dig);
            int bytesToCopy = W.length - (i * dig.length) > dig.length ? dig.length : W.length - (i * dig.length);
            System.arraycopy(dig, 0, W, i * dig.length, bytesToCopy);
            addTo(data, ONE);
        }
        return W;
    }
}
