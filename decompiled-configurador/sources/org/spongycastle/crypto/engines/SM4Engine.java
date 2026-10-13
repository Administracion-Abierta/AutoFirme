package org.spongycastle.crypto.engines;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/engines/SM4Engine.class */
public class SM4Engine implements BlockCipher {
    private static final int BLOCK_SIZE = 16;
    private static final byte[] Sbox = {-42, -112, -23, -2, -52, -31, 61, -73, 22, -74, 20, -62, 40, -5, 44, 5, 43, 103, -102, 118, 42, -66, 4, -61, -86, 68, 19, 38, 73, -122, 6, -103, -100, 66, 80, -12, -111, -17, -104, 122, 51, 84, 11, 67, -19, -49, -84, 98, -28, -77, 28, -87, -55, 8, -24, -107, -128, -33, -108, -6, 117, -113, 63, -90, 71, 7, -89, -4, -13, 115, 23, -70, -125, 89, 60, 25, -26, -123, 79, -88, 104, 107, -127, -78, 113, 100, -38, -117, -8, -21, 15, 75, 112, 86, -99, 53, 30, 36, 14, 94, 99, 88, -47, -94, 37, 34, 124, 59, 1, 33, 120, -121, -44, 0, 70, 87, -97, -45, 39, 82, 76, 54, 2, -25, -96, -60, -56, -98, -22, -65, -118, -46, 64, -57, 56, -75, -93, -9, -14, -50, -7, 97, 21, -95, -32, -82, 93, -92, -101, 52, 26, 85, -83, -109, 50, 48, -11, -116, -79, -29, 29, -10, -30, 46, -126, 102, -54, 96, -64, 41, 35, -85, 13, 83, 78, 111, -43, -37, 55, 69, -34, -3, -114, 47, 3, -1, 106, 114, 109, 108, 91, 81, -115, 27, -81, -110, -69, -35, -68, 127, 17, -39, 92, 65, 31, 16, 90, -40, 10, -63, 49, -120, -91, -51, 123, -67, 45, 116, -48, 18, -72, -27, -76, -80, -119, 105, -105, 74, 12, -106, 119, 126, 101, -71, -15, 9, -59, 110, -58, -124, 24, -16, 125, -20, 58, -36, 77, 32, 121, -18, 95, 62, -41, -53, 57, 72};
    private static final int[] CK = {462357, 472066609, 943670861, 1415275113, 1886879365, -1936483679, -1464879427, -993275175, -521670923, -66909679, 404694573, 876298825, 1347903077, 1819507329, -2003855715, -1532251463, -1060647211, -589042959, -117504499, 337322537, 808926789, 1280531041, 1752135293, -2071227751, -1599623499, -1128019247, -656414995, -184876535, 269950501, 741554753, 1213159005, 1684763257};
    private static final int[] FK = {-1548633402, 1453994832, 1736282519, -1301273892};
    private final int[] X = new int[4];
    private int[] rk;

    private int rotateLeft(int x, int bits) {
        return (x << bits) | (x >>> (-bits));
    }

    private int tau(int A) {
        int b0 = Sbox[(A >> 24) & 255] & 255;
        int b1 = Sbox[(A >> 16) & 255] & 255;
        int b2 = Sbox[(A >> 8) & 255] & 255;
        int b3 = Sbox[A & 255] & 255;
        return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
    }

    private int L_ap(int B) {
        return (B ^ rotateLeft(B, 13)) ^ rotateLeft(B, 23);
    }

    private int T_ap(int Z) {
        return L_ap(tau(Z));
    }

    private int[] expandKey(boolean forEncryption, byte[] key) {
        int[] rk = new int[32];
        int[] MK = {Pack.bigEndianToInt(key, 0), Pack.bigEndianToInt(key, 4), Pack.bigEndianToInt(key, 8), Pack.bigEndianToInt(key, 12)};
        int[] K = {MK[0] ^ FK[0], MK[1] ^ FK[1], MK[2] ^ FK[2], MK[3] ^ FK[3]};
        if (forEncryption) {
            rk[0] = K[0] ^ T_ap(((K[1] ^ K[2]) ^ K[3]) ^ CK[0]);
            rk[1] = K[1] ^ T_ap(((K[2] ^ K[3]) ^ rk[0]) ^ CK[1]);
            rk[2] = K[2] ^ T_ap(((K[3] ^ rk[0]) ^ rk[1]) ^ CK[2]);
            rk[3] = K[3] ^ T_ap(((rk[0] ^ rk[1]) ^ rk[2]) ^ CK[3]);
            for (int i = 4; i < 32; i++) {
                rk[i] = rk[i - 4] ^ T_ap(((rk[i - 3] ^ rk[i - 2]) ^ rk[i - 1]) ^ CK[i]);
            }
        } else {
            rk[31] = K[0] ^ T_ap(((K[1] ^ K[2]) ^ K[3]) ^ CK[0]);
            rk[30] = K[1] ^ T_ap(((K[2] ^ K[3]) ^ rk[31]) ^ CK[1]);
            rk[29] = K[2] ^ T_ap(((K[3] ^ rk[31]) ^ rk[30]) ^ CK[2]);
            rk[28] = K[3] ^ T_ap(((rk[31] ^ rk[30]) ^ rk[29]) ^ CK[3]);
            for (int i2 = 27; i2 >= 0; i2--) {
                rk[i2] = rk[i2 + 4] ^ T_ap(((rk[i2 + 3] ^ rk[i2 + 2]) ^ rk[i2 + 1]) ^ CK[31 - i2]);
            }
        }
        return rk;
    }

    private int L(int B) {
        int C = (((B ^ rotateLeft(B, 2)) ^ rotateLeft(B, 10)) ^ rotateLeft(B, 18)) ^ rotateLeft(B, 24);
        return C;
    }

    private int T(int Z) {
        return L(tau(Z));
    }

    private void R(int[] A, int off) {
        int off1 = off + 1;
        int off2 = off + 2;
        int off3 = off + 3;
        A[off] = A[off] ^ A[off3];
        A[off3] = A[off] ^ A[off3];
        A[off] = A[off] ^ A[off3];
        A[off1] = A[off1] ^ A[off2];
        A[off2] = A[off1] ^ A[off2];
        A[off1] = A[off1] ^ A[off2];
    }

    private int F0(int[] X, int rk) {
        return X[0] ^ T(((X[1] ^ X[2]) ^ X[3]) ^ rk);
    }

    private int F1(int[] X, int rk) {
        return X[1] ^ T(((X[2] ^ X[3]) ^ X[0]) ^ rk);
    }

    private int F2(int[] X, int rk) {
        return X[2] ^ T(((X[3] ^ X[0]) ^ X[1]) ^ rk);
    }

    private int F3(int[] X, int rk) {
        return X[3] ^ T(((X[0] ^ X[1]) ^ X[2]) ^ rk);
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        if (params instanceof KeyParameter) {
            byte[] key = ((KeyParameter) params).getKey();
            if (key.length != 16) {
                throw new IllegalArgumentException("SM4 requires a 128 bit key");
            }
            this.rk = expandKey(forEncryption, key);
            return;
        }
        throw new IllegalArgumentException("invalid parameter passed to SM4 init - " + params.getClass().getName());
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "SM4";
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 16;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws IllegalStateException, DataLengthException {
        if (this.rk == null) {
            throw new IllegalStateException("SM4 not initialised");
        }
        if (inOff + 16 > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + 16 > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        this.X[0] = Pack.bigEndianToInt(in, inOff);
        this.X[1] = Pack.bigEndianToInt(in, inOff + 4);
        this.X[2] = Pack.bigEndianToInt(in, inOff + 8);
        this.X[3] = Pack.bigEndianToInt(in, inOff + 12);
        for (int i = 0; i < 32; i += 4) {
            this.X[0] = F0(this.X, this.rk[i]);
            this.X[1] = F1(this.X, this.rk[i + 1]);
            this.X[2] = F2(this.X, this.rk[i + 2]);
            this.X[3] = F3(this.X, this.rk[i + 3]);
        }
        R(this.X, 0);
        Pack.intToBigEndian(this.X[0], out, outOff);
        Pack.intToBigEndian(this.X[1], out, outOff + 4);
        Pack.intToBigEndian(this.X[2], out, outOff + 8);
        Pack.intToBigEndian(this.X[3], out, outOff + 12);
        return 16;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void reset() {
    }
}
