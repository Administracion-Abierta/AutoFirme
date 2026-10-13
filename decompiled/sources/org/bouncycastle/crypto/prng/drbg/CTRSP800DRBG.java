package org.bouncycastle.crypto.prng.drbg;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/prng/drbg/CTRSP800DRBG.class */
public class CTRSP800DRBG implements SP80090DRBG {
    private static final long TDEA_RESEED_MAX = 2147483648L;
    private static final long AES_RESEED_MAX = 140737488355328L;
    private static final int TDEA_MAX_BITS_REQUEST = 4096;
    private static final int AES_MAX_BITS_REQUEST = 262144;
    private EntropySource _entropySource;
    private BlockCipher _engine;
    private int _keySizeInBits;
    private int _seedLength;
    private int _securityStrength;
    private byte[] _Key;
    private byte[] _V;
    private long _reseedCounter = 0;
    private boolean _isTDEA;
    private static final byte[] K_BITS = Hex.decodeStrict("000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F");

    public CTRSP800DRBG(BlockCipher blockCipher, int i, int i2, EntropySource entropySource, byte[] bArr, byte[] bArr2) {
        this._isTDEA = false;
        this._entropySource = entropySource;
        this._engine = blockCipher;
        this._keySizeInBits = i;
        this._securityStrength = i2;
        this._seedLength = i + (blockCipher.getBlockSize() * 8);
        this._isTDEA = isTDEA(blockCipher);
        if (i2 > 256) {
            throw new IllegalArgumentException("Requested security strength is not supported by the derivation function");
        }
        if (getMaxSecurityStrength(blockCipher, i) < i2) {
            throw new IllegalArgumentException("Requested security strength is not supported by block cipher and key size");
        }
        if (entropySource.entropySize() < i2) {
            throw new IllegalArgumentException("Not enough entropy for security strength required");
        }
        CTR_DRBG_Instantiate_algorithm(getEntropy(), bArr2, bArr);
    }

    private void CTR_DRBG_Instantiate_algorithm(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        byte[] bArrBlock_Cipher_df = Block_Cipher_df(Arrays.concatenate(bArr, bArr2, bArr3), this._seedLength);
        int blockSize = this._engine.getBlockSize();
        this._Key = new byte[(this._keySizeInBits + 7) / 8];
        this._V = new byte[blockSize];
        CTR_DRBG_Update(bArrBlock_Cipher_df, this._Key, this._V);
        this._reseedCounter = 1L;
    }

    private void CTR_DRBG_Update(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        byte[] bArr4 = new byte[bArr.length];
        byte[] bArr5 = new byte[this._engine.getBlockSize()];
        int blockSize = this._engine.getBlockSize();
        this._engine.init(true, new KeyParameter(expandKey(bArr2)));
        for (int i = 0; i * blockSize < bArr.length; i++) {
            addOneTo(bArr3);
            this._engine.processBlock(bArr3, 0, bArr5, 0);
            System.arraycopy(bArr5, 0, bArr4, i * blockSize, bArr4.length - (i * blockSize) > blockSize ? blockSize : bArr4.length - (i * blockSize));
        }
        Bytes.xorTo(bArr.length, bArr, bArr4);
        System.arraycopy(bArr4, 0, bArr2, 0, bArr2.length);
        System.arraycopy(bArr4, bArr2.length, bArr3, 0, bArr3.length);
    }

    private void CTR_DRBG_Reseed_algorithm(byte[] bArr) {
        CTR_DRBG_Update(Block_Cipher_df(Arrays.concatenate(getEntropy(), bArr), this._seedLength), this._Key, this._V);
        this._reseedCounter = 1L;
    }

    private void addOneTo(byte[] bArr) {
        int i = 1;
        for (int i2 = 1; i2 <= bArr.length; i2++) {
            int i3 = (bArr[bArr.length - i2] & 255) + i;
            i = i3 > 255 ? 1 : 0;
            bArr[bArr.length - i2] = (byte) i3;
        }
    }

    private byte[] getEntropy() {
        byte[] entropy = this._entropySource.getEntropy();
        if (entropy.length < (this._securityStrength + 7) / 8) {
            throw new IllegalStateException("Insufficient entropy provided by entropy source");
        }
        return entropy;
    }

    private byte[] Block_Cipher_df(byte[] bArr, int i) {
        int blockSize = this._engine.getBlockSize();
        int length = bArr.length;
        byte[] bArr2 = new byte[(((((8 + length) + 1) + blockSize) - 1) / blockSize) * blockSize];
        copyIntToByteArray(bArr2, length, 0);
        copyIntToByteArray(bArr2, i / 8, 4);
        System.arraycopy(bArr, 0, bArr2, 8, length);
        bArr2[8 + length] = -128;
        byte[] bArr3 = new byte[(this._keySizeInBits / 8) + blockSize];
        byte[] bArr4 = new byte[blockSize];
        byte[] bArr5 = new byte[blockSize];
        byte[] bArr6 = new byte[this._keySizeInBits / 8];
        System.arraycopy(K_BITS, 0, bArr6, 0, bArr6.length);
        for (int i2 = 0; i2 * blockSize * 8 < this._keySizeInBits + (blockSize * 8); i2++) {
            copyIntToByteArray(bArr5, i2, 0);
            BCC(bArr4, bArr6, bArr5, bArr2);
            System.arraycopy(bArr4, 0, bArr3, i2 * blockSize, bArr3.length - (i2 * blockSize) > blockSize ? blockSize : bArr3.length - (i2 * blockSize));
        }
        byte[] bArr7 = new byte[blockSize];
        System.arraycopy(bArr3, 0, bArr6, 0, bArr6.length);
        System.arraycopy(bArr3, bArr6.length, bArr7, 0, bArr7.length);
        byte[] bArr8 = new byte[i / 8];
        this._engine.init(true, new KeyParameter(expandKey(bArr6)));
        for (int i3 = 0; i3 * blockSize < bArr8.length; i3++) {
            this._engine.processBlock(bArr7, 0, bArr7, 0);
            System.arraycopy(bArr7, 0, bArr8, i3 * blockSize, bArr8.length - (i3 * blockSize) > blockSize ? blockSize : bArr8.length - (i3 * blockSize));
        }
        return bArr8;
    }

    private void BCC(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4) {
        int blockSize = this._engine.getBlockSize();
        byte[] bArr5 = new byte[blockSize];
        int length = bArr4.length / blockSize;
        byte[] bArr6 = new byte[blockSize];
        this._engine.init(true, new KeyParameter(expandKey(bArr2)));
        this._engine.processBlock(bArr3, 0, bArr5, 0);
        for (int i = 0; i < length; i++) {
            Bytes.xor(blockSize, bArr5, 0, bArr4, i * blockSize, bArr6, 0);
            this._engine.processBlock(bArr6, 0, bArr5, 0);
        }
        System.arraycopy(bArr5, 0, bArr, 0, bArr.length);
    }

    private void copyIntToByteArray(byte[] bArr, int i, int i2) {
        bArr[i2 + 0] = (byte) (i >> 24);
        bArr[i2 + 1] = (byte) (i >> 16);
        bArr[i2 + 2] = (byte) (i >> 8);
        bArr[i2 + 3] = (byte) i;
    }

    @Override // org.bouncycastle.crypto.prng.drbg.SP80090DRBG
    public int getBlockSize() {
        return this._V.length * 8;
    }

    @Override // org.bouncycastle.crypto.prng.drbg.SP80090DRBG
    public int generate(byte[] bArr, byte[] bArr2, boolean z) {
        byte[] bArrBlock_Cipher_df;
        if (this._isTDEA) {
            if (this._reseedCounter > TDEA_RESEED_MAX) {
                return -1;
            }
            if (Utils.isTooLarge(bArr, 512)) {
                throw new IllegalArgumentException("Number of bits per request limited to 4096");
            }
        } else {
            if (this._reseedCounter > AES_RESEED_MAX) {
                return -1;
            }
            if (Utils.isTooLarge(bArr, 32768)) {
                throw new IllegalArgumentException("Number of bits per request limited to 262144");
            }
        }
        if (z) {
            CTR_DRBG_Reseed_algorithm(bArr2);
            bArr2 = null;
        }
        if (bArr2 != null) {
            bArrBlock_Cipher_df = Block_Cipher_df(bArr2, this._seedLength);
            CTR_DRBG_Update(bArrBlock_Cipher_df, this._Key, this._V);
        } else {
            bArrBlock_Cipher_df = new byte[this._seedLength / 8];
        }
        byte[] bArr3 = new byte[this._V.length];
        this._engine.init(true, new KeyParameter(expandKey(this._Key)));
        for (int i = 0; i <= bArr.length / bArr3.length; i++) {
            int length = bArr.length - (i * bArr3.length) > bArr3.length ? bArr3.length : bArr.length - (i * this._V.length);
            if (length != 0) {
                addOneTo(this._V);
                this._engine.processBlock(this._V, 0, bArr3, 0);
                System.arraycopy(bArr3, 0, bArr, i * bArr3.length, length);
            }
        }
        CTR_DRBG_Update(bArrBlock_Cipher_df, this._Key, this._V);
        this._reseedCounter++;
        return bArr.length * 8;
    }

    @Override // org.bouncycastle.crypto.prng.drbg.SP80090DRBG
    public void reseed(byte[] bArr) {
        CTR_DRBG_Reseed_algorithm(bArr);
    }

    private boolean isTDEA(BlockCipher blockCipher) {
        return blockCipher.getAlgorithmName().equals("DESede") || blockCipher.getAlgorithmName().equals("TDEA");
    }

    private int getMaxSecurityStrength(BlockCipher blockCipher, int i) {
        if (isTDEA(blockCipher) && i == 168) {
            return 112;
        }
        if (blockCipher.getAlgorithmName().equals("AES")) {
            return i;
        }
        return -1;
    }

    byte[] expandKey(byte[] bArr) {
        if (!this._isTDEA) {
            return bArr;
        }
        byte[] bArr2 = new byte[24];
        padKey(bArr, 0, bArr2, 0);
        padKey(bArr, 7, bArr2, 8);
        padKey(bArr, 14, bArr2, 16);
        return bArr2;
    }

    private void padKey(byte[] bArr, int i, byte[] bArr2, int i2) {
        bArr2[i2 + 0] = (byte) (bArr[i + 0] & 254);
        bArr2[i2 + 1] = (byte) ((bArr[i + 0] << 7) | ((bArr[i + 1] & 252) >>> 1));
        bArr2[i2 + 2] = (byte) ((bArr[i + 1] << 6) | ((bArr[i + 2] & 248) >>> 2));
        bArr2[i2 + 3] = (byte) ((bArr[i + 2] << 5) | ((bArr[i + 3] & 240) >>> 3));
        bArr2[i2 + 4] = (byte) ((bArr[i + 3] << 4) | ((bArr[i + 4] & 224) >>> 4));
        bArr2[i2 + 5] = (byte) ((bArr[i + 4] << 3) | ((bArr[i + 5] & 192) >>> 5));
        bArr2[i2 + 6] = (byte) ((bArr[i + 5] << 2) | ((bArr[i + 6] & 128) >>> 6));
        bArr2[i2 + 7] = (byte) (bArr[i + 6] << 1);
        for (int i3 = i2; i3 <= i2 + 7; i3++) {
            byte b = bArr2[i3];
            bArr2[i3] = (byte) ((b & 254) | (((((((((b >> 1) ^ (b >> 2)) ^ (b >> 3)) ^ (b >> 4)) ^ (b >> 5)) ^ (b >> 6)) ^ (b >> 7)) ^ 1) & 1));
        }
    }
}
