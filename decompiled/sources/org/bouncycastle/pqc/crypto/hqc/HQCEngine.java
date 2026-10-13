package org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/hqc/HQCEngine.class */
class HQCEngine {
    private int n;
    private int n1;
    private int n2;
    private int k;
    private int delta;
    private int w;
    private int wr;
    private int we;
    private int g;
    private int rejectionThreshold;
    private int fft;
    private int mulParam;
    private int N_BYTE;
    private int n1n2;
    private int N_BYTE_64;
    private int K_BYTE;
    private int K_BYTE_64;
    private int N1_BYTE_64;
    private int N1N2_BYTE_64;
    private int N1N2_BYTE;
    private int N1_BYTE;
    private int[] generatorPoly;
    private long RED_MASK;
    private GF2PolynomialCalculator gfCalculator;
    private int SEED_SIZE = 40;
    private byte G_FCT_DOMAIN = 3;
    private byte K_FCT_DOMAIN = 4;
    private int GF_POLY_WT = 5;
    private int GF_POLY_M2 = 4;
    private int SALT_SIZE_BYTES = 16;
    private int SALT_SIZE_64 = 2;
    private int SHA512_BYTES = 64;

    public HQCEngine(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int[] iArr) {
        this.n = i;
        this.k = i4;
        this.delta = i6;
        this.w = i7;
        this.wr = i8;
        this.we = i9;
        this.n1 = i2;
        this.n2 = i3;
        this.n1n2 = i2 * i3;
        this.generatorPoly = iArr;
        this.g = i5;
        this.rejectionThreshold = i10;
        this.fft = i11;
        this.mulParam = (int) Math.ceil(i3 / 128);
        this.N_BYTE = Utils.getByteSizeFromBitSize(i);
        this.K_BYTE = i4;
        this.N_BYTE_64 = Utils.getByte64SizeFromBitSize(i);
        this.K_BYTE_64 = Utils.getByteSizeFromBitSize(i4);
        this.N1_BYTE_64 = Utils.getByteSizeFromBitSize(i2);
        this.N1N2_BYTE_64 = Utils.getByte64SizeFromBitSize(i2 * i3);
        this.N1N2_BYTE = Utils.getByteSizeFromBitSize(i2 * i3);
        this.N1_BYTE = Utils.getByteSizeFromBitSize(i2);
        this.RED_MASK = (1 << ((int) (((long) i) % 64))) - 1;
        this.gfCalculator = new GF2PolynomialCalculator(this.N_BYTE_64, i, this.RED_MASK);
    }

    public void genKeyPair(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        byte[] bArr4 = new byte[this.SEED_SIZE];
        byte[] bArr5 = new byte[this.K_BYTE];
        KeccakRandomGenerator keccakRandomGenerator = new KeccakRandomGenerator(256);
        keccakRandomGenerator.randomGeneratorInit(bArr3, null, bArr3.length, 0);
        keccakRandomGenerator.squeeze(bArr4, 40);
        keccakRandomGenerator.squeeze(bArr5, this.K_BYTE);
        KeccakRandomGenerator keccakRandomGenerator2 = new KeccakRandomGenerator(256);
        keccakRandomGenerator2.seedExpanderInit(bArr4, bArr4.length);
        long[] jArr = new long[this.N_BYTE_64];
        long[] jArr2 = new long[this.N_BYTE_64];
        generateRandomFixedWeight(jArr2, keccakRandomGenerator2, this.w);
        generateRandomFixedWeight(jArr, keccakRandomGenerator2, this.w);
        byte[] bArr6 = new byte[this.SEED_SIZE];
        keccakRandomGenerator.squeeze(bArr6, 40);
        KeccakRandomGenerator keccakRandomGenerator3 = new KeccakRandomGenerator(256);
        keccakRandomGenerator3.seedExpanderInit(bArr6, bArr6.length);
        long[] jArr3 = new long[this.N_BYTE_64];
        generatePublicKeyH(jArr3, keccakRandomGenerator3);
        long[] jArr4 = new long[this.N_BYTE_64];
        this.gfCalculator.multLongs(jArr4, jArr2, jArr3);
        GF2PolynomialCalculator.addLongs(jArr4, jArr4, jArr);
        byte[] bArr7 = new byte[this.N_BYTE];
        Utils.fromLongArrayToByteArray(bArr7, jArr4);
        byte[] bArrConcatenate = Arrays.concatenate(bArr6, bArr7);
        byte[] bArrConcatenate2 = Arrays.concatenate(bArr4, bArr5, bArrConcatenate);
        System.arraycopy(bArrConcatenate, 0, bArr, 0, bArrConcatenate.length);
        System.arraycopy(bArrConcatenate2, 0, bArr2, 0, bArrConcatenate2.length);
    }

    public void encaps(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5, byte[] bArr6) {
        byte[] bArr7 = new byte[this.K_BYTE];
        byte[] bArr8 = new byte[this.SEED_SIZE];
        KeccakRandomGenerator keccakRandomGenerator = new KeccakRandomGenerator(256);
        keccakRandomGenerator.randomGeneratorInit(bArr5, null, bArr5.length, 0);
        keccakRandomGenerator.squeeze(bArr8, 40);
        keccakRandomGenerator.squeeze(new byte[this.K_BYTE], this.K_BYTE);
        keccakRandomGenerator.squeeze(new byte[this.SEED_SIZE], 40);
        keccakRandomGenerator.squeeze(bArr7, this.K_BYTE);
        byte[] bArr9 = new byte[this.SHA512_BYTES];
        byte[] bArr10 = new byte[this.K_BYTE + (this.SALT_SIZE_BYTES * 2) + this.SALT_SIZE_BYTES];
        keccakRandomGenerator.squeeze(bArr6, this.SALT_SIZE_BYTES);
        System.arraycopy(bArr7, 0, bArr10, 0, bArr7.length);
        System.arraycopy(bArr4, 0, bArr10, this.K_BYTE, this.SALT_SIZE_BYTES * 2);
        System.arraycopy(bArr6, 0, bArr10, this.K_BYTE + (this.SALT_SIZE_BYTES * 2), this.SALT_SIZE_BYTES);
        KeccakRandomGenerator keccakRandomGenerator2 = new KeccakRandomGenerator(256);
        keccakRandomGenerator2.SHAKE256_512_ds(bArr9, bArr10, bArr10.length, new byte[]{this.G_FCT_DOMAIN});
        long[] jArr = new long[this.N_BYTE_64];
        byte[] bArr11 = new byte[this.N_BYTE];
        extractPublicKeys(jArr, bArr11, bArr4);
        long[] jArr2 = new long[this.N1N2_BYTE_64];
        encrypt(bArr, jArr2, jArr, bArr11, bArr7, bArr9);
        Utils.fromLongArrayToByteArray(bArr2, jArr2);
        byte[] bArrConcatenate = Arrays.concatenate(bArr7, bArr, bArr2);
        keccakRandomGenerator2.SHAKE256_512_ds(bArr3, bArrConcatenate, bArrConcatenate.length, new byte[]{this.K_FCT_DOMAIN});
    }

    public int decaps(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        long[] jArr = new long[this.N_BYTE_64];
        byte[] bArr4 = new byte[40 + this.N_BYTE];
        byte[] bArr5 = new byte[this.K_BYTE];
        extractKeysFromSecretKeys(jArr, bArr5, bArr4, bArr3);
        byte[] bArr6 = new byte[this.N_BYTE];
        byte[] bArr7 = new byte[this.N1N2_BYTE];
        byte[] bArr8 = new byte[this.SALT_SIZE_BYTES];
        extractCiphertexts(bArr6, bArr7, bArr8, bArr2);
        byte[] bArr9 = new byte[this.k];
        int iDecrypt = decrypt(bArr9, bArr9, bArr5, bArr6, bArr7, jArr);
        byte[] bArr10 = new byte[this.SHA512_BYTES];
        byte[] bArr11 = new byte[this.K_BYTE + (this.SALT_SIZE_BYTES * 2) + this.SALT_SIZE_BYTES];
        System.arraycopy(bArr9, 0, bArr11, 0, bArr9.length);
        System.arraycopy(bArr4, 0, bArr11, this.K_BYTE, this.SALT_SIZE_BYTES * 2);
        System.arraycopy(bArr8, 0, bArr11, this.K_BYTE + (this.SALT_SIZE_BYTES * 2), this.SALT_SIZE_BYTES);
        KeccakRandomGenerator keccakRandomGenerator = new KeccakRandomGenerator(256);
        keccakRandomGenerator.SHAKE256_512_ds(bArr10, bArr11, bArr11.length, new byte[]{this.G_FCT_DOMAIN});
        long[] jArr2 = new long[this.N_BYTE_64];
        byte[] bArr12 = new byte[this.N_BYTE];
        extractPublicKeys(jArr2, bArr12, bArr4);
        byte[] bArr13 = new byte[this.N_BYTE];
        byte[] bArr14 = new byte[this.N1N2_BYTE];
        long[] jArr3 = new long[this.N1N2_BYTE_64];
        encrypt(bArr13, jArr3, jArr2, bArr12, bArr9, bArr10);
        Utils.fromLongArrayToByteArray(bArr14, jArr3);
        byte[] bArr15 = new byte[this.K_BYTE + this.N_BYTE + this.N1N2_BYTE];
        if (!Arrays.constantTimeAreEqual(bArr6, bArr13)) {
            iDecrypt = 1;
        }
        if (!Arrays.constantTimeAreEqual(bArr7, bArr14)) {
            iDecrypt = 1;
        }
        int i = iDecrypt - 1;
        for (int i2 = 0; i2 < this.K_BYTE; i2++) {
            bArr15[i2] = (byte) (((bArr9[i2] & i) ^ (bArr5[i2] & (i ^ (-1)))) & 255);
        }
        System.arraycopy(bArr6, 0, bArr15, this.K_BYTE, this.N_BYTE);
        System.arraycopy(bArr7, 0, bArr15, this.K_BYTE + this.N_BYTE, this.N1N2_BYTE);
        keccakRandomGenerator.SHAKE256_512_ds(bArr, bArr15, bArr15.length, new byte[]{this.K_FCT_DOMAIN});
        return -i;
    }

    int getSessionKeySize() {
        return this.SHA512_BYTES;
    }

    private void encrypt(byte[] bArr, long[] jArr, long[] jArr2, byte[] bArr2, byte[] bArr3, byte[] bArr4) {
        KeccakRandomGenerator keccakRandomGenerator = new KeccakRandomGenerator(256);
        keccakRandomGenerator.seedExpanderInit(bArr4, this.SEED_SIZE);
        long[] jArr3 = new long[this.N_BYTE_64];
        long[] jArr4 = new long[this.N_BYTE_64];
        long[] jArr5 = new long[this.N_BYTE_64];
        generateRandomFixedWeight(jArr5, keccakRandomGenerator, this.wr);
        generateRandomFixedWeight(jArr3, keccakRandomGenerator, this.we);
        generateRandomFixedWeight(jArr4, keccakRandomGenerator, this.wr);
        long[] jArr6 = new long[this.N_BYTE_64];
        this.gfCalculator.multLongs(jArr6, jArr5, jArr2);
        GF2PolynomialCalculator.addLongs(jArr6, jArr6, jArr4);
        Utils.fromLongArrayToByteArray(bArr, jArr6);
        byte[] bArr5 = new byte[this.n1];
        long[] jArr7 = new long[this.N1N2_BYTE_64];
        long[] jArr8 = new long[this.N_BYTE_64];
        ReedSolomon.encode(bArr5, bArr3, this.K_BYTE * 8, this.n1, this.k, this.g, this.generatorPoly);
        ReedMuller.encode(jArr7, bArr5, this.n1, this.mulParam);
        System.arraycopy(jArr7, 0, jArr8, 0, jArr7.length);
        long[] jArr9 = new long[this.N_BYTE_64];
        Utils.fromByteArrayToLongArray(jArr9, bArr2);
        long[] jArr10 = new long[this.N_BYTE_64];
        this.gfCalculator.multLongs(jArr10, jArr5, jArr9);
        GF2PolynomialCalculator.addLongs(jArr10, jArr10, jArr8);
        GF2PolynomialCalculator.addLongs(jArr10, jArr10, jArr3);
        Utils.resizeArray(jArr, this.n1n2, jArr10, this.n, this.N1N2_BYTE_64, this.N1N2_BYTE_64);
    }

    private int decrypt(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5, long[] jArr) {
        long[] jArr2 = new long[this.N_BYTE_64];
        Utils.fromByteArrayToLongArray(jArr2, bArr4);
        long[] jArr3 = new long[this.N1N2_BYTE_64];
        Utils.fromByteArrayToLongArray(jArr3, bArr5);
        long[] jArr4 = new long[this.N_BYTE_64];
        System.arraycopy(jArr3, 0, jArr4, 0, jArr3.length);
        long[] jArr5 = new long[this.N_BYTE_64];
        this.gfCalculator.multLongs(jArr5, jArr, jArr2);
        GF2PolynomialCalculator.addLongs(jArr5, jArr5, jArr4);
        byte[] bArr6 = new byte[this.n1];
        ReedMuller.decode(bArr6, jArr5, this.n1, this.mulParam);
        ReedSolomon.decode(bArr2, bArr6, this.n1, this.fft, this.delta, this.k, this.g);
        System.arraycopy(bArr2, 0, bArr, 0, bArr.length);
        return 0;
    }

    private void generateRandomFixedWeight(long[] jArr, KeccakRandomGenerator keccakRandomGenerator, int i) {
        int[] iArr = new int[this.wr];
        byte[] bArr = new byte[this.wr * 4];
        int[] iArr2 = new int[this.wr];
        int[] iArr3 = new int[this.wr];
        long[] jArr2 = new long[this.wr];
        keccakRandomGenerator.expandSeed(bArr, 4 * i);
        Pack.littleEndianToInt(bArr, 0, iArr, 0, iArr.length);
        for (int i2 = 0; i2 < i; i2++) {
            iArr2[i2] = (int) (((long) i2) + ((((long) iArr[i2]) & 4294967295L) % ((long) (this.n - i2))));
        }
        for (int i3 = i - 1; i3 >= 0; i3--) {
            int i4 = 0;
            for (int i5 = i3 + 1; i5 < i; i5++) {
                if (iArr2[i5] == iArr2[i3]) {
                    i4 |= 1;
                }
            }
            int i6 = -i4;
            iArr2[i3] = (i6 & i3) ^ ((i6 ^ (-1)) & iArr2[i3]);
        }
        for (int i7 = 0; i7 < i; i7++) {
            iArr3[i7] = iArr2[i7] >>> 6;
            jArr2[i7] = 1 << (iArr2[i7] & 63);
        }
        for (int i8 = 0; i8 < this.N_BYTE_64; i8++) {
            long j = 0;
            for (int i9 = 0; i9 < i; i9++) {
                int i10 = i8 - iArr3[i9];
                j |= jArr2[i9] & ((long) (-(1 ^ ((i10 | (-i10)) >>> 31))));
            }
            int i11 = i8;
            jArr[i11] = jArr[i11] | j;
        }
    }

    void generatePublicKeyH(long[] jArr, KeccakRandomGenerator keccakRandomGenerator) {
        byte[] bArr = new byte[this.N_BYTE];
        keccakRandomGenerator.expandSeed(bArr, this.N_BYTE);
        long[] jArr2 = new long[this.N_BYTE_64];
        Utils.fromByteArrayToLongArray(jArr2, bArr);
        int i = this.N_BYTE_64 - 1;
        jArr2[i] = jArr2[i] & Utils.bitMask(this.n, 64L);
        System.arraycopy(jArr2, 0, jArr, 0, jArr.length);
    }

    private void extractPublicKeys(long[] jArr, byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[this.SEED_SIZE];
        System.arraycopy(bArr2, 0, bArr3, 0, bArr3.length);
        KeccakRandomGenerator keccakRandomGenerator = new KeccakRandomGenerator(256);
        keccakRandomGenerator.seedExpanderInit(bArr3, bArr3.length);
        long[] jArr2 = new long[this.N_BYTE_64];
        generatePublicKeyH(jArr2, keccakRandomGenerator);
        System.arraycopy(jArr2, 0, jArr, 0, jArr.length);
        System.arraycopy(bArr2, 40, bArr, 0, bArr.length);
    }

    private void extractKeysFromSecretKeys(long[] jArr, byte[] bArr, byte[] bArr2, byte[] bArr3) {
        byte[] bArr4 = new byte[this.SEED_SIZE];
        System.arraycopy(bArr3, 0, bArr4, 0, bArr4.length);
        System.arraycopy(bArr3, this.SEED_SIZE, bArr, 0, this.K_BYTE);
        KeccakRandomGenerator keccakRandomGenerator = new KeccakRandomGenerator(256);
        keccakRandomGenerator.seedExpanderInit(bArr4, bArr4.length);
        generateRandomFixedWeight(jArr, keccakRandomGenerator, this.w);
        System.arraycopy(bArr3, this.SEED_SIZE + this.K_BYTE, bArr2, 0, bArr2.length);
    }

    private void extractCiphertexts(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4) {
        System.arraycopy(bArr4, 0, bArr, 0, bArr.length);
        System.arraycopy(bArr4, bArr.length, bArr2, 0, bArr2.length);
        System.arraycopy(bArr4, bArr.length + bArr2.length, bArr3, 0, bArr3.length);
    }
}
