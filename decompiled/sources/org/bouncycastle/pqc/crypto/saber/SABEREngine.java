package org.bouncycastle.pqc.crypto.saber;

import java.security.SecureRandom;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/saber/SABEREngine.class */
class SABEREngine {
    public static final int SABER_EP = 10;
    public static final int SABER_N = 256;
    private static final int SABER_SEEDBYTES = 32;
    private static final int SABER_NOISE_SEEDBYTES = 32;
    private static final int SABER_KEYBYTES = 32;
    private static final int SABER_HASHBYTES = 32;
    private final int SABER_L;
    private final int SABER_MU;
    private final int SABER_ET;
    private final int SABER_POLYCOINBYTES;
    private final int SABER_EQ;
    private final int SABER_POLYBYTES;
    private final int SABER_POLYVECBYTES;
    private final int SABER_POLYCOMPRESSEDBYTES;
    private final int SABER_POLYVECCOMPRESSEDBYTES;
    private final int SABER_SCALEBYTES_KEM;
    private final int SABER_INDCPA_PUBLICKEYBYTES;
    private final int SABER_INDCPA_SECRETKEYBYTES;
    private final int SABER_PUBLICKEYBYTES;
    private final int SABER_SECRETKEYBYTES;
    private final int SABER_BYTES_CCA_DEC;
    private final int defaultKeySize;
    private final int h1;
    private final int h2;
    private final Utils utils;
    private final Poly poly;
    private final boolean usingAES;
    protected final boolean usingEffectiveMasking;
    protected final Symmetric symmetric;

    public int getSABER_N() {
        return 256;
    }

    public int getSABER_EP() {
        return 10;
    }

    public int getSABER_KEYBYTES() {
        return 32;
    }

    public int getSABER_L() {
        return this.SABER_L;
    }

    public int getSABER_ET() {
        return this.SABER_ET;
    }

    public int getSABER_POLYBYTES() {
        return this.SABER_POLYBYTES;
    }

    public int getSABER_POLYVECBYTES() {
        return this.SABER_POLYVECBYTES;
    }

    public int getSABER_SEEDBYTES() {
        return 32;
    }

    public int getSABER_POLYCOINBYTES() {
        return this.SABER_POLYCOINBYTES;
    }

    public int getSABER_NOISE_SEEDBYTES() {
        return 32;
    }

    public int getSABER_MU() {
        return this.SABER_MU;
    }

    public Utils getUtils() {
        return this.utils;
    }

    public int getSessionKeySize() {
        return this.defaultKeySize / 8;
    }

    public int getCipherTextSize() {
        return this.SABER_BYTES_CCA_DEC;
    }

    public int getPublicKeySize() {
        return this.SABER_PUBLICKEYBYTES;
    }

    public int getPrivateKeySize() {
        return this.SABER_SECRETKEYBYTES;
    }

    public SABEREngine(int i, int i2, boolean z, boolean z2) {
        this.defaultKeySize = i2;
        this.usingAES = z;
        this.usingEffectiveMasking = z2;
        this.SABER_L = i;
        if (i == 2) {
            this.SABER_MU = 10;
            this.SABER_ET = 3;
        } else if (i == 3) {
            this.SABER_MU = 8;
            this.SABER_ET = 4;
        } else {
            this.SABER_MU = 6;
            this.SABER_ET = 6;
        }
        if (z) {
            this.symmetric = new Symmetric.AesSymmetric();
        } else {
            this.symmetric = new Symmetric.ShakeSymmetric();
        }
        if (z2) {
            this.SABER_EQ = 12;
            this.SABER_POLYCOINBYTES = 64;
        } else {
            this.SABER_EQ = 13;
            this.SABER_POLYCOINBYTES = (this.SABER_MU * 256) / 8;
        }
        this.SABER_POLYBYTES = (this.SABER_EQ * 256) / 8;
        this.SABER_POLYVECBYTES = this.SABER_L * this.SABER_POLYBYTES;
        this.SABER_POLYCOMPRESSEDBYTES = 320;
        this.SABER_POLYVECCOMPRESSEDBYTES = this.SABER_L * this.SABER_POLYCOMPRESSEDBYTES;
        this.SABER_SCALEBYTES_KEM = (this.SABER_ET * 256) / 8;
        this.SABER_INDCPA_PUBLICKEYBYTES = this.SABER_POLYVECCOMPRESSEDBYTES + 32;
        this.SABER_INDCPA_SECRETKEYBYTES = this.SABER_POLYVECBYTES;
        this.SABER_PUBLICKEYBYTES = this.SABER_INDCPA_PUBLICKEYBYTES;
        this.SABER_SECRETKEYBYTES = this.SABER_INDCPA_SECRETKEYBYTES + this.SABER_INDCPA_PUBLICKEYBYTES + 32 + 32;
        this.SABER_BYTES_CCA_DEC = this.SABER_POLYVECCOMPRESSEDBYTES + this.SABER_SCALEBYTES_KEM;
        this.h1 = 1 << ((this.SABER_EQ - 10) - 1);
        this.h2 = (256 - (1 << ((10 - this.SABER_ET) - 1))) + (1 << ((this.SABER_EQ - 10) - 1));
        this.utils = new Utils(this);
        this.poly = new Poly(this);
    }

    private void indcpa_kem_keypair(byte[] bArr, byte[] bArr2, SecureRandom secureRandom) {
        short[][][] sArr = new short[this.SABER_L][this.SABER_L][256];
        short[][] sArr2 = new short[this.SABER_L][256];
        short[][] sArr3 = new short[this.SABER_L][256];
        byte[] bArr3 = new byte[32];
        byte[] bArr4 = new byte[32];
        secureRandom.nextBytes(bArr3);
        this.symmetric.prf(bArr3, bArr3, 32, 32);
        secureRandom.nextBytes(bArr4);
        this.poly.GenMatrix(sArr, bArr3);
        this.poly.GenSecret(sArr2, bArr4);
        this.poly.MatrixVectorMul(sArr, sArr2, sArr3, 1);
        for (int i = 0; i < this.SABER_L; i++) {
            for (int i2 = 0; i2 < 256; i2++) {
                sArr3[i][i2] = (short) (((sArr3[i][i2] + this.h1) & 65535) >>> (this.SABER_EQ - 10));
            }
        }
        this.utils.POLVECq2BS(bArr2, sArr2);
        this.utils.POLVECp2BS(bArr, sArr3);
        System.arraycopy(bArr3, 0, bArr, this.SABER_POLYVECCOMPRESSEDBYTES, bArr3.length);
    }

    public int crypto_kem_keypair(byte[] bArr, byte[] bArr2, SecureRandom secureRandom) {
        indcpa_kem_keypair(bArr, bArr2, secureRandom);
        for (int i = 0; i < this.SABER_INDCPA_PUBLICKEYBYTES; i++) {
            bArr2[i + this.SABER_INDCPA_SECRETKEYBYTES] = bArr[i];
        }
        this.symmetric.hash_h(bArr2, bArr, this.SABER_SECRETKEYBYTES - 64);
        byte[] bArr3 = new byte[32];
        secureRandom.nextBytes(bArr3);
        System.arraycopy(bArr3, 0, bArr2, this.SABER_SECRETKEYBYTES - 32, bArr3.length);
        return 0;
    }

    private void indcpa_kem_enc(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4) {
        short[][][] sArr = new short[this.SABER_L][this.SABER_L][256];
        short[][] sArr2 = new short[this.SABER_L][256];
        short[][] sArr3 = new short[this.SABER_L][256];
        short[][] sArr4 = new short[this.SABER_L][256];
        short[] sArr5 = new short[256];
        short[] sArr6 = new short[256];
        this.poly.GenMatrix(sArr, Arrays.copyOfRange(bArr3, this.SABER_POLYVECCOMPRESSEDBYTES, bArr3.length));
        this.poly.GenSecret(sArr2, bArr2);
        this.poly.MatrixVectorMul(sArr, sArr2, sArr3, 0);
        for (int i = 0; i < this.SABER_L; i++) {
            for (int i2 = 0; i2 < 256; i2++) {
                sArr3[i][i2] = (short) (((sArr3[i][i2] + this.h1) & 65535) >>> (this.SABER_EQ - 10));
            }
        }
        this.utils.POLVECp2BS(bArr4, sArr3);
        this.utils.BS2POLVECp(bArr3, sArr4);
        this.poly.InnerProd(sArr4, sArr2, sArr6);
        this.utils.BS2POLmsg(bArr, sArr5);
        for (int i3 = 0; i3 < 256; i3++) {
            sArr6[i3] = (short) ((((sArr6[i3] - (sArr5[i3] << 9)) + this.h1) & 65535) >>> (10 - this.SABER_ET));
        }
        this.utils.POLT2BS(bArr4, this.SABER_POLYVECCOMPRESSEDBYTES, sArr6);
    }

    public int crypto_kem_enc(byte[] bArr, byte[] bArr2, byte[] bArr3, SecureRandom secureRandom) {
        byte[] bArr4 = new byte[64];
        byte[] bArr5 = new byte[64];
        byte[] bArr6 = new byte[32];
        secureRandom.nextBytes(bArr6);
        this.symmetric.hash_h(bArr6, bArr6, 0);
        System.arraycopy(bArr6, 0, bArr5, 0, 32);
        this.symmetric.hash_h(bArr5, bArr3, 32);
        this.symmetric.hash_g(bArr4, bArr5);
        indcpa_kem_enc(bArr5, Arrays.copyOfRange(bArr4, 32, bArr4.length), bArr3, bArr);
        this.symmetric.hash_h(bArr4, bArr, 32);
        byte[] bArr7 = new byte[32];
        this.symmetric.hash_h(bArr7, bArr4, 0);
        System.arraycopy(bArr7, 0, bArr2, 0, this.defaultKeySize / 8);
        return 0;
    }

    private void indcpa_kem_dec(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        short[][] sArr = new short[this.SABER_L][256];
        short[][] sArr2 = new short[this.SABER_L][256];
        short[] sArr3 = new short[256];
        short[] sArr4 = new short[256];
        this.utils.BS2POLVECq(bArr, 0, sArr);
        this.utils.BS2POLVECp(bArr2, sArr2);
        this.poly.InnerProd(sArr2, sArr, sArr3);
        this.utils.BS2POLT(bArr2, this.SABER_POLYVECCOMPRESSEDBYTES, sArr4);
        for (int i = 0; i < 256; i++) {
            sArr3[i] = (short) ((((sArr3[i] + this.h2) - (sArr4[i] << (10 - this.SABER_ET))) & 65535) >> 9);
        }
        this.utils.POLmsg2BS(bArr3, sArr3);
    }

    public int crypto_kem_dec(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        byte[] bArr4 = new byte[this.SABER_BYTES_CCA_DEC];
        byte[] bArr5 = new byte[64];
        byte[] bArr6 = new byte[64];
        byte[] bArrCopyOfRange = Arrays.copyOfRange(bArr3, this.SABER_INDCPA_SECRETKEYBYTES, bArr3.length);
        indcpa_kem_dec(bArr3, bArr2, bArr5);
        for (int i = 0; i < 32; i++) {
            bArr5[32 + i] = bArr3[(this.SABER_SECRETKEYBYTES - 64) + i];
        }
        this.symmetric.hash_g(bArr6, bArr5);
        indcpa_kem_enc(bArr5, Arrays.copyOfRange(bArr6, 32, bArr6.length), bArrCopyOfRange, bArr4);
        int iVerify = verify(bArr2, bArr4, this.SABER_BYTES_CCA_DEC);
        this.symmetric.hash_h(bArr6, bArr2, 32);
        cmov(bArr6, bArr3, this.SABER_SECRETKEYBYTES - 32, 32, (byte) iVerify);
        byte[] bArr7 = new byte[32];
        this.symmetric.hash_h(bArr7, bArr6, 0);
        System.arraycopy(bArr7, 0, bArr, 0, this.defaultKeySize / 8);
        return 0;
    }

    static int verify(byte[] bArr, byte[] bArr2, int i) {
        long j = 0;
        for (int i2 = 0; i2 < i; i2++) {
            j |= (long) (bArr[i2] ^ bArr2[i2]);
        }
        return (int) ((-j) >>> 63);
    }

    static void cmov(byte[] bArr, byte[] bArr2, int i, int i2, byte b) {
        byte b2 = (byte) (-b);
        for (int i3 = 0; i3 < i2; i3++) {
            int i4 = i3;
            bArr[i4] = (byte) (bArr[i4] ^ (b2 & (bArr2[i3 + i] ^ bArr[i3])));
        }
    }
}
