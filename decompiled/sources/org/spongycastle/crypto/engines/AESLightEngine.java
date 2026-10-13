package org.spongycastle.crypto.engines;

import com.sun.jna.platform.win32.WinError;
import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/AESLightEngine.class */
public class AESLightEngine implements BlockCipher {
    private static final byte[] S = {99, 124, 119, 123, -14, 107, 111, -59, 48, 1, 103, 43, -2, -41, -85, 118, -54, -126, -55, 125, -6, 89, 71, -16, -83, -44, -94, -81, -100, -92, 114, -64, -73, -3, -109, 38, 54, 63, -9, -52, 52, -91, -27, -15, 113, -40, 49, 21, 4, -57, 35, -61, 24, -106, 5, -102, 7, 18, -128, -30, -21, 39, -78, 117, 9, -125, 44, 26, 27, 110, 90, -96, 82, 59, -42, -77, 41, -29, 47, -124, 83, -47, 0, -19, 32, -4, -79, 91, 106, -53, -66, 57, 74, 76, 88, -49, -48, -17, -86, -5, 67, 77, 51, -123, 69, -7, 2, 127, 80, 60, -97, -88, 81, -93, 64, -113, -110, -99, 56, -11, -68, -74, -38, 33, 16, -1, -13, -46, -51, 12, 19, -20, 95, -105, 68, 23, -60, -89, 126, 61, 100, 93, 25, 115, 96, -127, 79, -36, 34, 42, -112, -120, 70, -18, -72, 20, -34, 94, 11, -37, -32, 50, 58, 10, 73, 6, 36, 92, -62, -45, -84, 98, -111, -107, -28, 121, -25, -56, 55, 109, -115, -43, 78, -87, 108, 86, -12, -22, 101, 122, -82, 8, -70, 120, 37, 46, 28, -90, -76, -58, -24, -35, 116, 31, 75, -67, -117, -118, 112, 62, -75, 102, 72, 3, -10, 14, 97, 53, 87, -71, -122, -63, 29, -98, -31, -8, -104, 17, 105, -39, -114, -108, -101, 30, -121, -23, -50, 85, 40, -33, -116, -95, -119, 13, -65, -26, 66, 104, 65, -103, 45, 15, -80, 84, -69, 22};
    private static final byte[] Si = {82, 9, 106, -43, 48, 54, -91, 56, -65, 64, -93, -98, -127, -13, -41, -5, 124, -29, 57, -126, -101, 47, -1, -121, 52, -114, 67, 68, -60, -34, -23, -53, 84, 123, -108, 50, -90, -62, 35, 61, -18, 76, -107, 11, 66, -6, -61, 78, 8, 46, -95, 102, 40, -39, 36, -78, 118, 91, -94, 73, 109, -117, -47, 37, 114, -8, -10, 100, -122, 104, -104, 22, -44, -92, 92, -52, 93, 101, -74, -110, 108, 112, 72, 80, -3, -19, -71, -38, 94, 21, 70, 87, -89, -115, -99, -124, -112, -40, -85, 0, -116, -68, -45, 10, -9, -28, 88, 5, -72, -77, 69, 6, -48, 44, 30, -113, -54, 63, 15, 2, -63, -81, -67, 3, 1, 19, -118, 107, 58, -111, 17, 65, 79, 103, -36, -22, -105, -14, -49, -50, -16, -76, -26, 115, -106, -84, 116, 34, -25, -83, 53, -123, -30, -7, 55, -24, 28, 117, -33, 110, 71, -15, 26, 113, 29, 41, -59, -119, 111, -73, 98, 14, -86, 24, -66, 27, -4, 86, 62, 75, -58, -46, 121, 32, -102, -37, -64, -2, 120, -51, 90, -12, 31, -35, -88, 51, -120, 7, -57, 49, -79, 18, 16, 89, 39, -128, -20, 95, 96, 81, 127, -87, 25, -75, 74, 13, 45, -27, 122, -97, -109, -55, -100, -17, -96, -32, 59, 77, -82, 42, -11, -80, -56, -21, -69, 60, -125, 83, -103, 97, 23, 43, 4, 126, -70, 119, -42, 38, -31, 105, 20, 99, 85, 33, 12, 125};
    private static final int[] rcon = {1, 2, 4, 8, 16, 32, 64, 128, 27, 54, 108, WinError.ERROR_EXE_MACHINE_TYPE_MISMATCH, 171, 77, 154, 47, 94, 188, 99, 198, 151, 53, 106, WinError.ERROR_LOCKED, 179, 125, 250, 239, 197, 145};
    private static final int m1 = -2139062144;
    private static final int m2 = 2139062143;
    private static final int m3 = 27;
    private static final int m4 = -1061109568;
    private static final int m5 = 1061109567;
    private int ROUNDS;
    private int[][] WorkingKey = (int[][]) null;
    private int C0;
    private int C1;
    private int C2;
    private int C3;
    private boolean forEncryption;
    private static final int BLOCK_SIZE = 16;

    private static int shift(int r, int shift) {
        return (r >>> shift) | (r << (-shift));
    }

    private static int FFmulX(int x) {
        return ((x & m2) << 1) ^ (((x & m1) >>> 7) * 27);
    }

    private static int FFmulX2(int x) {
        int t0 = (x & m5) << 2;
        int t1 = x & m4;
        int t2 = t1 ^ (t1 >>> 1);
        return (t0 ^ (t2 >>> 2)) ^ (t2 >>> 5);
    }

    private static int mcol(int x) {
        int t0 = shift(x, 8);
        int t1 = x ^ t0;
        return (shift(t1, 16) ^ t0) ^ FFmulX(t1);
    }

    private static int inv_mcol(int x) {
        int t1 = x ^ shift(x, 8);
        int t0 = x ^ FFmulX(t1);
        int t2 = t1 ^ FFmulX2(t0);
        return t0 ^ (t2 ^ shift(t2, 16));
    }

    private static int subWord(int x) {
        return (S[x & 255] & 255) | ((S[(x >> 8) & 255] & 255) << 8) | ((S[(x >> 16) & 255] & 255) << 16) | (S[(x >> 24) & 255] << 24);
    }

    private int[][] generateWorkingKey(byte[] key, boolean forEncryption) {
        int keyLen = key.length;
        if (keyLen < 16 || keyLen > 32 || (keyLen & 7) != 0) {
            throw new IllegalArgumentException("Key length not 128/192/256 bits.");
        }
        int KC = keyLen >> 2;
        this.ROUNDS = KC + 6;
        int[][] W = new int[this.ROUNDS + 1][4];
        switch (KC) {
            case 4:
                int t0 = Pack.littleEndianToInt(key, 0);
                W[0][0] = t0;
                int t1 = Pack.littleEndianToInt(key, 4);
                W[0][1] = t1;
                int t2 = Pack.littleEndianToInt(key, 8);
                W[0][2] = t2;
                int t3 = Pack.littleEndianToInt(key, 12);
                W[0][3] = t3;
                for (int i = 1; i <= 10; i++) {
                    int u = subWord(shift(t3, 8)) ^ rcon[i - 1];
                    t0 ^= u;
                    W[i][0] = t0;
                    t1 ^= t0;
                    W[i][1] = t1;
                    t2 ^= t1;
                    W[i][2] = t2;
                    t3 ^= t2;
                    W[i][3] = t3;
                }
                break;
            case 5:
            case 7:
            default:
                throw new IllegalStateException("Should never get here");
            case 6:
                int t4 = Pack.littleEndianToInt(key, 0);
                W[0][0] = t4;
                int t5 = Pack.littleEndianToInt(key, 4);
                W[0][1] = t5;
                int t6 = Pack.littleEndianToInt(key, 8);
                W[0][2] = t6;
                int t7 = Pack.littleEndianToInt(key, 12);
                W[0][3] = t7;
                int t8 = Pack.littleEndianToInt(key, 16);
                W[1][0] = t8;
                int t9 = Pack.littleEndianToInt(key, 20);
                W[1][1] = t9;
                int u2 = subWord(shift(t9, 8)) ^ 1;
                int rcon2 = 1 << 1;
                int t10 = t4 ^ u2;
                W[1][2] = t10;
                int t11 = t5 ^ t10;
                W[1][3] = t11;
                int t12 = t6 ^ t11;
                W[2][0] = t12;
                int t13 = t7 ^ t12;
                W[2][1] = t13;
                int t14 = t8 ^ t13;
                W[2][2] = t14;
                int t15 = t9 ^ t14;
                W[2][3] = t15;
                for (int i2 = 3; i2 < 12; i2 += 3) {
                    int u3 = subWord(shift(t15, 8)) ^ rcon2;
                    int rcon3 = rcon2 << 1;
                    int t16 = t10 ^ u3;
                    W[i2][0] = t16;
                    int t17 = t11 ^ t16;
                    W[i2][1] = t17;
                    int t18 = t12 ^ t17;
                    W[i2][2] = t18;
                    int t19 = t13 ^ t18;
                    W[i2][3] = t19;
                    int t20 = t14 ^ t19;
                    W[i2 + 1][0] = t20;
                    int t21 = t15 ^ t20;
                    W[i2 + 1][1] = t21;
                    int u4 = subWord(shift(t21, 8)) ^ rcon3;
                    rcon2 = rcon3 << 1;
                    t10 = t16 ^ u4;
                    W[i2 + 1][2] = t10;
                    t11 = t17 ^ t10;
                    W[i2 + 1][3] = t11;
                    t12 = t18 ^ t11;
                    W[i2 + 2][0] = t12;
                    t13 = t19 ^ t12;
                    W[i2 + 2][1] = t13;
                    t14 = t20 ^ t13;
                    W[i2 + 2][2] = t14;
                    t15 = t21 ^ t14;
                    W[i2 + 2][3] = t15;
                }
                int u5 = subWord(shift(t15, 8)) ^ rcon2;
                int t22 = t10 ^ u5;
                W[12][0] = t22;
                int t23 = t11 ^ t22;
                W[12][1] = t23;
                int t24 = t12 ^ t23;
                W[12][2] = t24;
                W[12][3] = t13 ^ t24;
                break;
            case 8:
                int t25 = Pack.littleEndianToInt(key, 0);
                W[0][0] = t25;
                int t26 = Pack.littleEndianToInt(key, 4);
                W[0][1] = t26;
                int t27 = Pack.littleEndianToInt(key, 8);
                W[0][2] = t27;
                int t28 = Pack.littleEndianToInt(key, 12);
                W[0][3] = t28;
                int t29 = Pack.littleEndianToInt(key, 16);
                W[1][0] = t29;
                int t30 = Pack.littleEndianToInt(key, 20);
                W[1][1] = t30;
                int t31 = Pack.littleEndianToInt(key, 24);
                W[1][2] = t31;
                int t32 = Pack.littleEndianToInt(key, 28);
                W[1][3] = t32;
                int rcon4 = 1;
                for (int i3 = 2; i3 < 14; i3 += 2) {
                    int u6 = subWord(shift(t32, 8)) ^ rcon4;
                    rcon4 <<= 1;
                    t25 ^= u6;
                    W[i3][0] = t25;
                    t26 ^= t25;
                    W[i3][1] = t26;
                    t27 ^= t26;
                    W[i3][2] = t27;
                    t28 ^= t27;
                    W[i3][3] = t28;
                    int u7 = subWord(t28);
                    t29 ^= u7;
                    W[i3 + 1][0] = t29;
                    t30 ^= t29;
                    W[i3 + 1][1] = t30;
                    t31 ^= t30;
                    W[i3 + 1][2] = t31;
                    t32 ^= t31;
                    W[i3 + 1][3] = t32;
                }
                int u8 = subWord(shift(t32, 8)) ^ rcon4;
                int t33 = t25 ^ u8;
                W[14][0] = t33;
                int t34 = t26 ^ t33;
                W[14][1] = t34;
                int t35 = t27 ^ t34;
                W[14][2] = t35;
                W[14][3] = t28 ^ t35;
                break;
        }
        if (!forEncryption) {
            for (int j = 1; j < this.ROUNDS; j++) {
                for (int i4 = 0; i4 < 4; i4++) {
                    W[j][i4] = inv_mcol(W[j][i4]);
                }
            }
        }
        return W;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void init(boolean forEncryption, CipherParameters params) {
        if (params instanceof KeyParameter) {
            this.WorkingKey = generateWorkingKey(((KeyParameter) params).getKey(), forEncryption);
            this.forEncryption = forEncryption;
            return;
        }
        throw new IllegalArgumentException("invalid parameter passed to AES init - " + params.getClass().getName());
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "AES";
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 16;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) {
        if (this.WorkingKey == null) {
            throw new IllegalStateException("AES engine not initialised");
        }
        if (inOff + 16 > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + 16 > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.forEncryption) {
            unpackBlock(in, inOff);
            encryptBlock(this.WorkingKey);
            packBlock(out, outOff);
            return 16;
        }
        unpackBlock(in, inOff);
        decryptBlock(this.WorkingKey);
        packBlock(out, outOff);
        return 16;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void reset() {
    }

    private void unpackBlock(byte[] bytes, int off) {
        int index = off + 1;
        this.C0 = bytes[off] & 255;
        int index2 = index + 1;
        this.C0 |= (bytes[index] & 255) << 8;
        int index3 = index2 + 1;
        this.C0 |= (bytes[index2] & 255) << 16;
        int index4 = index3 + 1;
        this.C0 |= bytes[index3] << 24;
        int index5 = index4 + 1;
        this.C1 = bytes[index4] & 255;
        int index6 = index5 + 1;
        this.C1 |= (bytes[index5] & 255) << 8;
        int index7 = index6 + 1;
        this.C1 |= (bytes[index6] & 255) << 16;
        int index8 = index7 + 1;
        this.C1 |= bytes[index7] << 24;
        int index9 = index8 + 1;
        this.C2 = bytes[index8] & 255;
        int index10 = index9 + 1;
        this.C2 |= (bytes[index9] & 255) << 8;
        int index11 = index10 + 1;
        this.C2 |= (bytes[index10] & 255) << 16;
        int index12 = index11 + 1;
        this.C2 |= bytes[index11] << 24;
        int index13 = index12 + 1;
        this.C3 = bytes[index12] & 255;
        int index14 = index13 + 1;
        this.C3 |= (bytes[index13] & 255) << 8;
        int index15 = index14 + 1;
        this.C3 |= (bytes[index14] & 255) << 16;
        int i = index15 + 1;
        this.C3 |= bytes[index15] << 24;
    }

    private void packBlock(byte[] bytes, int off) {
        int index = off + 1;
        bytes[off] = (byte) this.C0;
        int index2 = index + 1;
        bytes[index] = (byte) (this.C0 >> 8);
        int index3 = index2 + 1;
        bytes[index2] = (byte) (this.C0 >> 16);
        int index4 = index3 + 1;
        bytes[index3] = (byte) (this.C0 >> 24);
        int index5 = index4 + 1;
        bytes[index4] = (byte) this.C1;
        int index6 = index5 + 1;
        bytes[index5] = (byte) (this.C1 >> 8);
        int index7 = index6 + 1;
        bytes[index6] = (byte) (this.C1 >> 16);
        int index8 = index7 + 1;
        bytes[index7] = (byte) (this.C1 >> 24);
        int index9 = index8 + 1;
        bytes[index8] = (byte) this.C2;
        int index10 = index9 + 1;
        bytes[index9] = (byte) (this.C2 >> 8);
        int index11 = index10 + 1;
        bytes[index10] = (byte) (this.C2 >> 16);
        int index12 = index11 + 1;
        bytes[index11] = (byte) (this.C2 >> 24);
        int index13 = index12 + 1;
        bytes[index12] = (byte) this.C3;
        int index14 = index13 + 1;
        bytes[index13] = (byte) (this.C3 >> 8);
        int index15 = index14 + 1;
        bytes[index14] = (byte) (this.C3 >> 16);
        int i = index15 + 1;
        bytes[index15] = (byte) (this.C3 >> 24);
    }

    private void encryptBlock(int[][] KW) {
        int t0 = this.C0 ^ KW[0][0];
        int t1 = this.C1 ^ KW[0][1];
        int t2 = this.C2 ^ KW[0][2];
        int r = 1;
        int iMcol = this.C3;
        int i = KW[0][3];
        while (true) {
            int r3 = iMcol ^ i;
            if (r < this.ROUNDS - 1) {
                int r0 = mcol((((S[t0 & 255] & 255) ^ ((S[(t1 >> 8) & 255] & 255) << 8)) ^ ((S[(t2 >> 16) & 255] & 255) << 16)) ^ (S[(r3 >> 24) & 255] << 24)) ^ KW[r][0];
                int r1 = mcol((((S[t1 & 255] & 255) ^ ((S[(t2 >> 8) & 255] & 255) << 8)) ^ ((S[(r3 >> 16) & 255] & 255) << 16)) ^ (S[(t0 >> 24) & 255] << 24)) ^ KW[r][1];
                int r2 = mcol((((S[t2 & 255] & 255) ^ ((S[(r3 >> 8) & 255] & 255) << 8)) ^ ((S[(t0 >> 16) & 255] & 255) << 16)) ^ (S[(t1 >> 24) & 255] << 24)) ^ KW[r][2];
                int i2 = r;
                int r4 = r + 1;
                int r5 = mcol((((S[r3 & 255] & 255) ^ ((S[(t0 >> 8) & 255] & 255) << 8)) ^ ((S[(t1 >> 16) & 255] & 255) << 16)) ^ (S[(t2 >> 24) & 255] << 24)) ^ KW[i2][3];
                t0 = mcol((((S[r0 & 255] & 255) ^ ((S[(r1 >> 8) & 255] & 255) << 8)) ^ ((S[(r2 >> 16) & 255] & 255) << 16)) ^ (S[(r5 >> 24) & 255] << 24)) ^ KW[r4][0];
                t1 = mcol((((S[r1 & 255] & 255) ^ ((S[(r2 >> 8) & 255] & 255) << 8)) ^ ((S[(r5 >> 16) & 255] & 255) << 16)) ^ (S[(r0 >> 24) & 255] << 24)) ^ KW[r4][1];
                t2 = mcol((((S[r2 & 255] & 255) ^ ((S[(r5 >> 8) & 255] & 255) << 8)) ^ ((S[(r0 >> 16) & 255] & 255) << 16)) ^ (S[(r1 >> 24) & 255] << 24)) ^ KW[r4][2];
                iMcol = mcol((((S[r5 & 255] & 255) ^ ((S[(r0 >> 8) & 255] & 255) << 8)) ^ ((S[(r1 >> 16) & 255] & 255) << 16)) ^ (S[(r2 >> 24) & 255] << 24));
                r = r4 + 1;
                i = KW[r4][3];
            } else {
                int r6 = mcol((((S[t0 & 255] & 255) ^ ((S[(t1 >> 8) & 255] & 255) << 8)) ^ ((S[(t2 >> 16) & 255] & 255) << 16)) ^ (S[(r3 >> 24) & 255] << 24)) ^ KW[r][0];
                int r7 = mcol((((S[t1 & 255] & 255) ^ ((S[(t2 >> 8) & 255] & 255) << 8)) ^ ((S[(r3 >> 16) & 255] & 255) << 16)) ^ (S[(t0 >> 24) & 255] << 24)) ^ KW[r][1];
                int r8 = mcol((((S[t2 & 255] & 255) ^ ((S[(r3 >> 8) & 255] & 255) << 8)) ^ ((S[(t0 >> 16) & 255] & 255) << 16)) ^ (S[(t1 >> 24) & 255] << 24)) ^ KW[r][2];
                int i3 = r;
                int r9 = r + 1;
                int r10 = mcol((((S[r3 & 255] & 255) ^ ((S[(t0 >> 8) & 255] & 255) << 8)) ^ ((S[(t1 >> 16) & 255] & 255) << 16)) ^ (S[(t2 >> 24) & 255] << 24)) ^ KW[i3][3];
                this.C0 = ((((S[r6 & 255] & 255) ^ ((S[(r7 >> 8) & 255] & 255) << 8)) ^ ((S[(r8 >> 16) & 255] & 255) << 16)) ^ (S[(r10 >> 24) & 255] << 24)) ^ KW[r9][0];
                this.C1 = ((((S[r7 & 255] & 255) ^ ((S[(r8 >> 8) & 255] & 255) << 8)) ^ ((S[(r10 >> 16) & 255] & 255) << 16)) ^ (S[(r6 >> 24) & 255] << 24)) ^ KW[r9][1];
                this.C2 = ((((S[r8 & 255] & 255) ^ ((S[(r10 >> 8) & 255] & 255) << 8)) ^ ((S[(r6 >> 16) & 255] & 255) << 16)) ^ (S[(r7 >> 24) & 255] << 24)) ^ KW[r9][2];
                this.C3 = ((((S[r10 & 255] & 255) ^ ((S[(r6 >> 8) & 255] & 255) << 8)) ^ ((S[(r7 >> 16) & 255] & 255) << 16)) ^ (S[(r8 >> 24) & 255] << 24)) ^ KW[r9][3];
                return;
            }
        }
    }

    private void decryptBlock(int[][] KW) {
        int t0 = this.C0 ^ KW[this.ROUNDS][0];
        int t1 = this.C1 ^ KW[this.ROUNDS][1];
        int t2 = this.C2 ^ KW[this.ROUNDS][2];
        int r = this.ROUNDS - 1;
        int iInv_mcol = this.C3;
        int i = KW[this.ROUNDS][3];
        while (true) {
            int r3 = iInv_mcol ^ i;
            if (r > 1) {
                int r0 = inv_mcol((((Si[t0 & 255] & 255) ^ ((Si[(r3 >> 8) & 255] & 255) << 8)) ^ ((Si[(t2 >> 16) & 255] & 255) << 16)) ^ (Si[(t1 >> 24) & 255] << 24)) ^ KW[r][0];
                int r1 = inv_mcol((((Si[t1 & 255] & 255) ^ ((Si[(t0 >> 8) & 255] & 255) << 8)) ^ ((Si[(r3 >> 16) & 255] & 255) << 16)) ^ (Si[(t2 >> 24) & 255] << 24)) ^ KW[r][1];
                int r2 = inv_mcol((((Si[t2 & 255] & 255) ^ ((Si[(t1 >> 8) & 255] & 255) << 8)) ^ ((Si[(t0 >> 16) & 255] & 255) << 16)) ^ (Si[(r3 >> 24) & 255] << 24)) ^ KW[r][2];
                int i2 = r;
                int r4 = r - 1;
                int r5 = inv_mcol((((Si[r3 & 255] & 255) ^ ((Si[(t2 >> 8) & 255] & 255) << 8)) ^ ((Si[(t1 >> 16) & 255] & 255) << 16)) ^ (Si[(t0 >> 24) & 255] << 24)) ^ KW[i2][3];
                t0 = inv_mcol((((Si[r0 & 255] & 255) ^ ((Si[(r5 >> 8) & 255] & 255) << 8)) ^ ((Si[(r2 >> 16) & 255] & 255) << 16)) ^ (Si[(r1 >> 24) & 255] << 24)) ^ KW[r4][0];
                t1 = inv_mcol((((Si[r1 & 255] & 255) ^ ((Si[(r0 >> 8) & 255] & 255) << 8)) ^ ((Si[(r5 >> 16) & 255] & 255) << 16)) ^ (Si[(r2 >> 24) & 255] << 24)) ^ KW[r4][1];
                t2 = inv_mcol((((Si[r2 & 255] & 255) ^ ((Si[(r1 >> 8) & 255] & 255) << 8)) ^ ((Si[(r0 >> 16) & 255] & 255) << 16)) ^ (Si[(r5 >> 24) & 255] << 24)) ^ KW[r4][2];
                iInv_mcol = inv_mcol((((Si[r5 & 255] & 255) ^ ((Si[(r2 >> 8) & 255] & 255) << 8)) ^ ((Si[(r1 >> 16) & 255] & 255) << 16)) ^ (Si[(r0 >> 24) & 255] << 24));
                r = r4 - 1;
                i = KW[r4][3];
            } else {
                int r6 = inv_mcol((((Si[t0 & 255] & 255) ^ ((Si[(r3 >> 8) & 255] & 255) << 8)) ^ ((Si[(t2 >> 16) & 255] & 255) << 16)) ^ (Si[(t1 >> 24) & 255] << 24)) ^ KW[r][0];
                int r7 = inv_mcol((((Si[t1 & 255] & 255) ^ ((Si[(t0 >> 8) & 255] & 255) << 8)) ^ ((Si[(r3 >> 16) & 255] & 255) << 16)) ^ (Si[(t2 >> 24) & 255] << 24)) ^ KW[r][1];
                int r8 = inv_mcol((((Si[t2 & 255] & 255) ^ ((Si[(t1 >> 8) & 255] & 255) << 8)) ^ ((Si[(t0 >> 16) & 255] & 255) << 16)) ^ (Si[(r3 >> 24) & 255] << 24)) ^ KW[r][2];
                int r9 = inv_mcol((((Si[r3 & 255] & 255) ^ ((Si[(t2 >> 8) & 255] & 255) << 8)) ^ ((Si[(t1 >> 16) & 255] & 255) << 16)) ^ (Si[(t0 >> 24) & 255] << 24)) ^ KW[r][3];
                this.C0 = ((((Si[r6 & 255] & 255) ^ ((Si[(r9 >> 8) & 255] & 255) << 8)) ^ ((Si[(r8 >> 16) & 255] & 255) << 16)) ^ (Si[(r7 >> 24) & 255] << 24)) ^ KW[0][0];
                this.C1 = ((((Si[r7 & 255] & 255) ^ ((Si[(r6 >> 8) & 255] & 255) << 8)) ^ ((Si[(r9 >> 16) & 255] & 255) << 16)) ^ (Si[(r8 >> 24) & 255] << 24)) ^ KW[0][1];
                this.C2 = ((((Si[r8 & 255] & 255) ^ ((Si[(r7 >> 8) & 255] & 255) << 8)) ^ ((Si[(r6 >> 16) & 255] & 255) << 16)) ^ (Si[(r9 >> 24) & 255] << 24)) ^ KW[0][2];
                this.C3 = ((((Si[r9 & 255] & 255) ^ ((Si[(r8 >> 8) & 255] & 255) << 8)) ^ ((Si[(r7 >> 16) & 255] & 255) << 16)) ^ (Si[(r6 >> 24) & 255] << 24)) ^ KW[0][3];
                return;
            }
        }
    }
}
