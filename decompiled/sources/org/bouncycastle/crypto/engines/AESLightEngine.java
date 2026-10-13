package org.bouncycastle.crypto.engines;

import com.sun.jna.platform.win32.WinError;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/AESLightEngine.class */
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
    private int[][] WorkingKey = null;
    private boolean forEncryption;
    private static final int BLOCK_SIZE = 16;

    private static int shift(int i, int i2) {
        return (i >>> i2) | (i << (-i2));
    }

    private static int FFmulX(int i) {
        return ((i & m2) << 1) ^ (((i & m1) >>> 7) * 27);
    }

    private static int FFmulX2(int i) {
        int i2 = (i & m5) << 2;
        int i3 = i & m4;
        int i4 = i3 ^ (i3 >>> 1);
        return (i2 ^ (i4 >>> 2)) ^ (i4 >>> 5);
    }

    private static int mcol(int i) {
        int iShift = shift(i, 8);
        int i2 = i ^ iShift;
        return (shift(i2, 16) ^ iShift) ^ FFmulX(i2);
    }

    private static int inv_mcol(int i) {
        int iShift = i ^ shift(i, 8);
        int iFFmulX = i ^ FFmulX(iShift);
        int iFFmulX2 = iShift ^ FFmulX2(iFFmulX);
        return iFFmulX ^ (iFFmulX2 ^ shift(iFFmulX2, 16));
    }

    private static int subWord(int i) {
        return (S[i & 255] & 255) | ((S[(i >> 8) & 255] & 255) << 8) | ((S[(i >> 16) & 255] & 255) << 16) | (S[(i >> 24) & 255] << 24);
    }

    private int[][] generateWorkingKey(byte[] bArr, boolean z) {
        int length = bArr.length;
        if (length < 16 || length > 32 || (length & 7) != 0) {
            throw new IllegalArgumentException("Key length not 128/192/256 bits.");
        }
        int i = length >>> 2;
        this.ROUNDS = i + 6;
        int[][] iArr = new int[this.ROUNDS + 1][4];
        switch (i) {
            case 4:
                int iLittleEndianToInt = Pack.littleEndianToInt(bArr, 0);
                iArr[0][0] = iLittleEndianToInt;
                int iLittleEndianToInt2 = Pack.littleEndianToInt(bArr, 4);
                iArr[0][1] = iLittleEndianToInt2;
                int iLittleEndianToInt3 = Pack.littleEndianToInt(bArr, 8);
                iArr[0][2] = iLittleEndianToInt3;
                int iLittleEndianToInt4 = Pack.littleEndianToInt(bArr, 12);
                iArr[0][3] = iLittleEndianToInt4;
                for (int i2 = 1; i2 <= 10; i2++) {
                    iLittleEndianToInt ^= subWord(shift(iLittleEndianToInt4, 8)) ^ rcon[i2 - 1];
                    iArr[i2][0] = iLittleEndianToInt;
                    iLittleEndianToInt2 ^= iLittleEndianToInt;
                    iArr[i2][1] = iLittleEndianToInt2;
                    iLittleEndianToInt3 ^= iLittleEndianToInt2;
                    iArr[i2][2] = iLittleEndianToInt3;
                    iLittleEndianToInt4 ^= iLittleEndianToInt3;
                    iArr[i2][3] = iLittleEndianToInt4;
                }
                break;
            case 5:
            case 7:
            default:
                throw new IllegalStateException("Should never get here");
            case 6:
                int iLittleEndianToInt5 = Pack.littleEndianToInt(bArr, 0);
                iArr[0][0] = iLittleEndianToInt5;
                int iLittleEndianToInt6 = Pack.littleEndianToInt(bArr, 4);
                iArr[0][1] = iLittleEndianToInt6;
                int iLittleEndianToInt7 = Pack.littleEndianToInt(bArr, 8);
                iArr[0][2] = iLittleEndianToInt7;
                int iLittleEndianToInt8 = Pack.littleEndianToInt(bArr, 12);
                iArr[0][3] = iLittleEndianToInt8;
                int iLittleEndianToInt9 = Pack.littleEndianToInt(bArr, 16);
                int iLittleEndianToInt10 = Pack.littleEndianToInt(bArr, 20);
                int i3 = 1;
                int i4 = 1;
                while (true) {
                    iArr[i3][0] = iLittleEndianToInt9;
                    iArr[i3][1] = iLittleEndianToInt10;
                    int i5 = i4 << 1;
                    int iSubWord = iLittleEndianToInt5 ^ (subWord(shift(iLittleEndianToInt10, 8)) ^ i4);
                    iArr[i3][2] = iSubWord;
                    int i6 = iLittleEndianToInt6 ^ iSubWord;
                    iArr[i3][3] = i6;
                    int i7 = iLittleEndianToInt7 ^ i6;
                    iArr[i3 + 1][0] = i7;
                    int i8 = iLittleEndianToInt8 ^ i7;
                    iArr[i3 + 1][1] = i8;
                    int i9 = iLittleEndianToInt9 ^ i8;
                    iArr[i3 + 1][2] = i9;
                    int i10 = iLittleEndianToInt10 ^ i9;
                    iArr[i3 + 1][3] = i10;
                    i4 = i5 << 1;
                    iLittleEndianToInt5 = iSubWord ^ (subWord(shift(i10, 8)) ^ i5);
                    iArr[i3 + 2][0] = iLittleEndianToInt5;
                    iLittleEndianToInt6 = i6 ^ iLittleEndianToInt5;
                    iArr[i3 + 2][1] = iLittleEndianToInt6;
                    iLittleEndianToInt7 = i7 ^ iLittleEndianToInt6;
                    iArr[i3 + 2][2] = iLittleEndianToInt7;
                    iLittleEndianToInt8 = i8 ^ iLittleEndianToInt7;
                    iArr[i3 + 2][3] = iLittleEndianToInt8;
                    i3 += 3;
                    if (i3 < 13) {
                        iLittleEndianToInt9 = i9 ^ iLittleEndianToInt8;
                        iLittleEndianToInt10 = i10 ^ iLittleEndianToInt9;
                    }
                    break;
                }
                break;
            case 8:
                int iLittleEndianToInt11 = Pack.littleEndianToInt(bArr, 0);
                iArr[0][0] = iLittleEndianToInt11;
                int iLittleEndianToInt12 = Pack.littleEndianToInt(bArr, 4);
                iArr[0][1] = iLittleEndianToInt12;
                int iLittleEndianToInt13 = Pack.littleEndianToInt(bArr, 8);
                iArr[0][2] = iLittleEndianToInt13;
                int iLittleEndianToInt14 = Pack.littleEndianToInt(bArr, 12);
                iArr[0][3] = iLittleEndianToInt14;
                int iLittleEndianToInt15 = Pack.littleEndianToInt(bArr, 16);
                iArr[1][0] = iLittleEndianToInt15;
                int iLittleEndianToInt16 = Pack.littleEndianToInt(bArr, 20);
                iArr[1][1] = iLittleEndianToInt16;
                int iLittleEndianToInt17 = Pack.littleEndianToInt(bArr, 24);
                iArr[1][2] = iLittleEndianToInt17;
                int iLittleEndianToInt18 = Pack.littleEndianToInt(bArr, 28);
                iArr[1][3] = iLittleEndianToInt18;
                int i11 = 2;
                int i12 = 1;
                while (true) {
                    int iSubWord2 = subWord(shift(iLittleEndianToInt18, 8)) ^ i12;
                    i12 <<= 1;
                    iLittleEndianToInt11 ^= iSubWord2;
                    iArr[i11][0] = iLittleEndianToInt11;
                    iLittleEndianToInt12 ^= iLittleEndianToInt11;
                    iArr[i11][1] = iLittleEndianToInt12;
                    iLittleEndianToInt13 ^= iLittleEndianToInt12;
                    iArr[i11][2] = iLittleEndianToInt13;
                    iLittleEndianToInt14 ^= iLittleEndianToInt13;
                    iArr[i11][3] = iLittleEndianToInt14;
                    int i13 = i11 + 1;
                    if (i13 < 15) {
                        iLittleEndianToInt15 ^= subWord(iLittleEndianToInt14);
                        iArr[i13][0] = iLittleEndianToInt15;
                        iLittleEndianToInt16 ^= iLittleEndianToInt15;
                        iArr[i13][1] = iLittleEndianToInt16;
                        iLittleEndianToInt17 ^= iLittleEndianToInt16;
                        iArr[i13][2] = iLittleEndianToInt17;
                        iLittleEndianToInt18 ^= iLittleEndianToInt17;
                        iArr[i13][3] = iLittleEndianToInt18;
                        i11 = i13 + 1;
                    }
                    break;
                }
                break;
        }
        if (!z) {
            for (int i14 = 1; i14 < this.ROUNDS; i14++) {
                for (int i15 = 0; i15 < 4; i15++) {
                    iArr[i14][i15] = inv_mcol(iArr[i14][i15]);
                }
            }
        }
        return iArr;
    }

    public AESLightEngine() {
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(getAlgorithmName(), bitsOfSecurity()));
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void init(boolean z, CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to AES init - " + cipherParameters.getClass().getName());
        }
        this.WorkingKey = generateWorkingKey(((KeyParameter) cipherParameters).getKey(), z);
        this.forEncryption = z;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(getAlgorithmName(), bitsOfSecurity(), cipherParameters, Utils.getPurpose(z)));
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "AES";
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 16;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int processBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        if (this.WorkingKey == null) {
            throw new IllegalStateException("AES engine not initialised");
        }
        if (i > bArr.length - 16) {
            throw new DataLengthException("input buffer too short");
        }
        if (i2 > bArr2.length - 16) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.forEncryption) {
            encryptBlock(bArr, i, bArr2, i2, this.WorkingKey);
            return 16;
        }
        decryptBlock(bArr, i, bArr2, i2, this.WorkingKey);
        return 16;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void reset() {
    }

    private void encryptBlock(byte[] bArr, int i, byte[] bArr2, int i2, int[][] iArr) {
        int iLittleEndianToInt = Pack.littleEndianToInt(bArr, i + 0);
        int iLittleEndianToInt2 = Pack.littleEndianToInt(bArr, i + 4);
        int iLittleEndianToInt3 = Pack.littleEndianToInt(bArr, i + 8);
        int iLittleEndianToInt4 = Pack.littleEndianToInt(bArr, i + 12);
        int iMcol = iLittleEndianToInt ^ iArr[0][0];
        int iMcol2 = iLittleEndianToInt2 ^ iArr[0][1];
        int iMcol3 = iLittleEndianToInt3 ^ iArr[0][2];
        int i3 = 1;
        int iMcol4 = iLittleEndianToInt4;
        int i4 = iArr[0][3];
        while (true) {
            int i5 = iMcol4 ^ i4;
            if (i3 >= this.ROUNDS - 1) {
                int iMcol5 = mcol((((S[iMcol & 255] & 255) ^ ((S[(iMcol2 >> 8) & 255] & 255) << 8)) ^ ((S[(iMcol3 >> 16) & 255] & 255) << 16)) ^ (S[(i5 >> 24) & 255] << 24)) ^ iArr[i3][0];
                int iMcol6 = mcol((((S[iMcol2 & 255] & 255) ^ ((S[(iMcol3 >> 8) & 255] & 255) << 8)) ^ ((S[(i5 >> 16) & 255] & 255) << 16)) ^ (S[(iMcol >> 24) & 255] << 24)) ^ iArr[i3][1];
                int iMcol7 = mcol((((S[iMcol3 & 255] & 255) ^ ((S[(i5 >> 8) & 255] & 255) << 8)) ^ ((S[(iMcol >> 16) & 255] & 255) << 16)) ^ (S[(iMcol2 >> 24) & 255] << 24)) ^ iArr[i3][2];
                int i6 = i3;
                int i7 = i3 + 1;
                int iMcol8 = mcol((((S[i5 & 255] & 255) ^ ((S[(iMcol >> 8) & 255] & 255) << 8)) ^ ((S[(iMcol2 >> 16) & 255] & 255) << 16)) ^ (S[(iMcol3 >> 24) & 255] << 24)) ^ iArr[i6][3];
                int i8 = ((((S[iMcol5 & 255] & 255) ^ ((S[(iMcol6 >> 8) & 255] & 255) << 8)) ^ ((S[(iMcol7 >> 16) & 255] & 255) << 16)) ^ (S[(iMcol8 >> 24) & 255] << 24)) ^ iArr[i7][0];
                int i9 = ((((S[iMcol6 & 255] & 255) ^ ((S[(iMcol7 >> 8) & 255] & 255) << 8)) ^ ((S[(iMcol8 >> 16) & 255] & 255) << 16)) ^ (S[(iMcol5 >> 24) & 255] << 24)) ^ iArr[i7][1];
                int i10 = ((((S[iMcol7 & 255] & 255) ^ ((S[(iMcol8 >> 8) & 255] & 255) << 8)) ^ ((S[(iMcol5 >> 16) & 255] & 255) << 16)) ^ (S[(iMcol6 >> 24) & 255] << 24)) ^ iArr[i7][2];
                int i11 = ((((S[iMcol8 & 255] & 255) ^ ((S[(iMcol5 >> 8) & 255] & 255) << 8)) ^ ((S[(iMcol6 >> 16) & 255] & 255) << 16)) ^ (S[(iMcol7 >> 24) & 255] << 24)) ^ iArr[i7][3];
                Pack.intToLittleEndian(i8, bArr2, i2 + 0);
                Pack.intToLittleEndian(i9, bArr2, i2 + 4);
                Pack.intToLittleEndian(i10, bArr2, i2 + 8);
                Pack.intToLittleEndian(i11, bArr2, i2 + 12);
                return;
            }
            int iMcol9 = mcol((((S[iMcol & 255] & 255) ^ ((S[(iMcol2 >> 8) & 255] & 255) << 8)) ^ ((S[(iMcol3 >> 16) & 255] & 255) << 16)) ^ (S[(i5 >> 24) & 255] << 24)) ^ iArr[i3][0];
            int iMcol10 = mcol((((S[iMcol2 & 255] & 255) ^ ((S[(iMcol3 >> 8) & 255] & 255) << 8)) ^ ((S[(i5 >> 16) & 255] & 255) << 16)) ^ (S[(iMcol >> 24) & 255] << 24)) ^ iArr[i3][1];
            int iMcol11 = mcol((((S[iMcol3 & 255] & 255) ^ ((S[(i5 >> 8) & 255] & 255) << 8)) ^ ((S[(iMcol >> 16) & 255] & 255) << 16)) ^ (S[(iMcol2 >> 24) & 255] << 24)) ^ iArr[i3][2];
            int i12 = i3;
            int i13 = i3 + 1;
            int iMcol12 = mcol((((S[i5 & 255] & 255) ^ ((S[(iMcol >> 8) & 255] & 255) << 8)) ^ ((S[(iMcol2 >> 16) & 255] & 255) << 16)) ^ (S[(iMcol3 >> 24) & 255] << 24)) ^ iArr[i12][3];
            iMcol = mcol((((S[iMcol9 & 255] & 255) ^ ((S[(iMcol10 >> 8) & 255] & 255) << 8)) ^ ((S[(iMcol11 >> 16) & 255] & 255) << 16)) ^ (S[(iMcol12 >> 24) & 255] << 24)) ^ iArr[i13][0];
            iMcol2 = mcol((((S[iMcol10 & 255] & 255) ^ ((S[(iMcol11 >> 8) & 255] & 255) << 8)) ^ ((S[(iMcol12 >> 16) & 255] & 255) << 16)) ^ (S[(iMcol9 >> 24) & 255] << 24)) ^ iArr[i13][1];
            iMcol3 = mcol((((S[iMcol11 & 255] & 255) ^ ((S[(iMcol12 >> 8) & 255] & 255) << 8)) ^ ((S[(iMcol9 >> 16) & 255] & 255) << 16)) ^ (S[(iMcol10 >> 24) & 255] << 24)) ^ iArr[i13][2];
            iMcol4 = mcol((((S[iMcol12 & 255] & 255) ^ ((S[(iMcol9 >> 8) & 255] & 255) << 8)) ^ ((S[(iMcol10 >> 16) & 255] & 255) << 16)) ^ (S[(iMcol11 >> 24) & 255] << 24));
            i3 = i13 + 1;
            i4 = iArr[i13][3];
        }
    }

    private void decryptBlock(byte[] bArr, int i, byte[] bArr2, int i2, int[][] iArr) {
        int iLittleEndianToInt = Pack.littleEndianToInt(bArr, i + 0);
        int iLittleEndianToInt2 = Pack.littleEndianToInt(bArr, i + 4);
        int iLittleEndianToInt3 = Pack.littleEndianToInt(bArr, i + 8);
        int iLittleEndianToInt4 = Pack.littleEndianToInt(bArr, i + 12);
        int iInv_mcol = iLittleEndianToInt ^ iArr[this.ROUNDS][0];
        int iInv_mcol2 = iLittleEndianToInt2 ^ iArr[this.ROUNDS][1];
        int iInv_mcol3 = iLittleEndianToInt3 ^ iArr[this.ROUNDS][2];
        int i3 = this.ROUNDS - 1;
        int iInv_mcol4 = iLittleEndianToInt4;
        int i4 = iArr[this.ROUNDS][3];
        while (true) {
            int i5 = iInv_mcol4 ^ i4;
            if (i3 <= 1) {
                int iInv_mcol5 = inv_mcol((((Si[iInv_mcol & 255] & 255) ^ ((Si[(i5 >> 8) & 255] & 255) << 8)) ^ ((Si[(iInv_mcol3 >> 16) & 255] & 255) << 16)) ^ (Si[(iInv_mcol2 >> 24) & 255] << 24)) ^ iArr[i3][0];
                int iInv_mcol6 = inv_mcol((((Si[iInv_mcol2 & 255] & 255) ^ ((Si[(iInv_mcol >> 8) & 255] & 255) << 8)) ^ ((Si[(i5 >> 16) & 255] & 255) << 16)) ^ (Si[(iInv_mcol3 >> 24) & 255] << 24)) ^ iArr[i3][1];
                int iInv_mcol7 = inv_mcol((((Si[iInv_mcol3 & 255] & 255) ^ ((Si[(iInv_mcol2 >> 8) & 255] & 255) << 8)) ^ ((Si[(iInv_mcol >> 16) & 255] & 255) << 16)) ^ (Si[(i5 >> 24) & 255] << 24)) ^ iArr[i3][2];
                int iInv_mcol8 = inv_mcol((((Si[i5 & 255] & 255) ^ ((Si[(iInv_mcol3 >> 8) & 255] & 255) << 8)) ^ ((Si[(iInv_mcol2 >> 16) & 255] & 255) << 16)) ^ (Si[(iInv_mcol >> 24) & 255] << 24)) ^ iArr[i3][3];
                int i6 = ((((Si[iInv_mcol5 & 255] & 255) ^ ((Si[(iInv_mcol8 >> 8) & 255] & 255) << 8)) ^ ((Si[(iInv_mcol7 >> 16) & 255] & 255) << 16)) ^ (Si[(iInv_mcol6 >> 24) & 255] << 24)) ^ iArr[0][0];
                int i7 = ((((Si[iInv_mcol6 & 255] & 255) ^ ((Si[(iInv_mcol5 >> 8) & 255] & 255) << 8)) ^ ((Si[(iInv_mcol8 >> 16) & 255] & 255) << 16)) ^ (Si[(iInv_mcol7 >> 24) & 255] << 24)) ^ iArr[0][1];
                int i8 = ((((Si[iInv_mcol7 & 255] & 255) ^ ((Si[(iInv_mcol6 >> 8) & 255] & 255) << 8)) ^ ((Si[(iInv_mcol5 >> 16) & 255] & 255) << 16)) ^ (Si[(iInv_mcol8 >> 24) & 255] << 24)) ^ iArr[0][2];
                int i9 = ((((Si[iInv_mcol8 & 255] & 255) ^ ((Si[(iInv_mcol7 >> 8) & 255] & 255) << 8)) ^ ((Si[(iInv_mcol6 >> 16) & 255] & 255) << 16)) ^ (Si[(iInv_mcol5 >> 24) & 255] << 24)) ^ iArr[0][3];
                Pack.intToLittleEndian(i6, bArr2, i2 + 0);
                Pack.intToLittleEndian(i7, bArr2, i2 + 4);
                Pack.intToLittleEndian(i8, bArr2, i2 + 8);
                Pack.intToLittleEndian(i9, bArr2, i2 + 12);
                return;
            }
            int iInv_mcol9 = inv_mcol((((Si[iInv_mcol & 255] & 255) ^ ((Si[(i5 >> 8) & 255] & 255) << 8)) ^ ((Si[(iInv_mcol3 >> 16) & 255] & 255) << 16)) ^ (Si[(iInv_mcol2 >> 24) & 255] << 24)) ^ iArr[i3][0];
            int iInv_mcol10 = inv_mcol((((Si[iInv_mcol2 & 255] & 255) ^ ((Si[(iInv_mcol >> 8) & 255] & 255) << 8)) ^ ((Si[(i5 >> 16) & 255] & 255) << 16)) ^ (Si[(iInv_mcol3 >> 24) & 255] << 24)) ^ iArr[i3][1];
            int iInv_mcol11 = inv_mcol((((Si[iInv_mcol3 & 255] & 255) ^ ((Si[(iInv_mcol2 >> 8) & 255] & 255) << 8)) ^ ((Si[(iInv_mcol >> 16) & 255] & 255) << 16)) ^ (Si[(i5 >> 24) & 255] << 24)) ^ iArr[i3][2];
            int i10 = i3;
            int i11 = i3 - 1;
            int iInv_mcol12 = inv_mcol((((Si[i5 & 255] & 255) ^ ((Si[(iInv_mcol3 >> 8) & 255] & 255) << 8)) ^ ((Si[(iInv_mcol2 >> 16) & 255] & 255) << 16)) ^ (Si[(iInv_mcol >> 24) & 255] << 24)) ^ iArr[i10][3];
            iInv_mcol = inv_mcol((((Si[iInv_mcol9 & 255] & 255) ^ ((Si[(iInv_mcol12 >> 8) & 255] & 255) << 8)) ^ ((Si[(iInv_mcol11 >> 16) & 255] & 255) << 16)) ^ (Si[(iInv_mcol10 >> 24) & 255] << 24)) ^ iArr[i11][0];
            iInv_mcol2 = inv_mcol((((Si[iInv_mcol10 & 255] & 255) ^ ((Si[(iInv_mcol9 >> 8) & 255] & 255) << 8)) ^ ((Si[(iInv_mcol12 >> 16) & 255] & 255) << 16)) ^ (Si[(iInv_mcol11 >> 24) & 255] << 24)) ^ iArr[i11][1];
            iInv_mcol3 = inv_mcol((((Si[iInv_mcol11 & 255] & 255) ^ ((Si[(iInv_mcol10 >> 8) & 255] & 255) << 8)) ^ ((Si[(iInv_mcol9 >> 16) & 255] & 255) << 16)) ^ (Si[(iInv_mcol12 >> 24) & 255] << 24)) ^ iArr[i11][2];
            iInv_mcol4 = inv_mcol((((Si[iInv_mcol12 & 255] & 255) ^ ((Si[(iInv_mcol11 >> 8) & 255] & 255) << 8)) ^ ((Si[(iInv_mcol10 >> 16) & 255] & 255) << 16)) ^ (Si[(iInv_mcol9 >> 24) & 255] << 24));
            i3 = i11 - 1;
            i4 = iArr[i11][3];
        }
    }

    private int bitsOfSecurity() {
        if (this.WorkingKey == null) {
            return 256;
        }
        return (this.WorkingKey.length - 7) << 5;
    }
}
