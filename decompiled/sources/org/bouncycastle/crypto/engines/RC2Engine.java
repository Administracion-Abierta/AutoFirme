package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.RC2Parameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/RC2Engine.class */
public class RC2Engine implements BlockCipher {
    private static byte[] piTable = {-39, 120, -7, -60, 25, -35, -75, -19, 40, -23, -3, 121, 74, -96, -40, -99, -58, 126, 55, -125, 43, 118, 83, -114, 98, 76, 100, -120, 68, -117, -5, -94, 23, -102, 89, -11, -121, -77, 79, 19, 97, 69, 109, -115, 9, -127, 125, 50, -67, -113, 64, -21, -122, -73, 123, 11, -16, -107, 33, 34, 92, 107, 78, -126, 84, -42, 101, -109, -50, 96, -78, 28, 115, 86, -64, 20, -89, -116, -15, -36, 18, 117, -54, 31, 59, -66, -28, -47, 66, 61, -44, 48, -93, 60, -74, 38, 111, -65, 14, -38, 70, 105, 7, 87, 39, -14, 29, -101, -68, -108, 67, 3, -8, 17, -57, -10, -112, -17, 62, -25, 6, -61, -43, 47, -56, 102, 30, -41, 8, -24, -22, -34, -128, 82, -18, -9, -124, -86, 114, -84, 53, 77, 106, 42, -106, 26, -46, 113, 90, 21, 73, 116, 75, -97, -48, 94, 4, 24, -92, -20, -62, -32, 65, 110, 15, 81, -53, -52, 36, -111, -81, 80, -95, -12, 112, 57, -103, 124, 58, -123, 35, -72, -76, 122, -4, 2, 54, 91, 37, 85, -105, 49, 45, 93, -6, -104, -29, -118, -110, -82, 5, -33, 41, 16, 103, 108, -70, -55, -45, 0, -26, -49, -31, -98, -88, 44, 99, 22, 1, 63, 88, -30, -119, -87, 13, 56, 52, 27, -85, 51, -1, -80, -69, 72, 12, 95, -71, -79, -51, 46, -59, -13, -37, 71, -27, -91, -100, 119, 10, -90, 32, 104, -2, 127, -63, -83};
    private static final int BLOCK_SIZE = 8;
    private int[] workingKey;
    private boolean encrypting;

    private int[] generateWorkingKey(byte[] bArr, int i) {
        int[] iArr = new int[128];
        for (int i2 = 0; i2 != bArr.length; i2++) {
            iArr[i2] = bArr[i2] & 255;
        }
        int length = bArr.length;
        if (length < 128) {
            int i3 = 0;
            int i4 = iArr[length - 1];
            do {
                int i5 = i3;
                i3++;
                i4 = piTable[(i4 + iArr[i5]) & 255] & 255;
                int i6 = length;
                length++;
                iArr[i6] = i4;
            } while (length < 128);
        }
        int i7 = (i + 7) >> 3;
        int i8 = piTable[iArr[128 - i7] & (255 >> (7 & (-i)))] & 255;
        iArr[128 - i7] = i8;
        for (int i9 = (128 - i7) - 1; i9 >= 0; i9--) {
            i8 = piTable[i8 ^ iArr[i9 + i7]] & 255;
            iArr[i9] = i8;
        }
        int[] iArr2 = new int[64];
        for (int i10 = 0; i10 != iArr2.length; i10++) {
            iArr2[i10] = iArr[2 * i10] + (iArr[(2 * i10) + 1] << 8);
        }
        return iArr2;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void init(boolean z, CipherParameters cipherParameters) {
        byte[] key;
        this.encrypting = z;
        if (cipherParameters instanceof RC2Parameters) {
            RC2Parameters rC2Parameters = (RC2Parameters) cipherParameters;
            this.workingKey = generateWorkingKey(rC2Parameters.getKey(), rC2Parameters.getEffectiveKeyBits());
            key = rC2Parameters.getKey();
        } else {
            if (!(cipherParameters instanceof KeyParameter)) {
                throw new IllegalArgumentException("invalid parameter passed to RC2 init - " + cipherParameters.getClass().getName());
            }
            key = ((KeyParameter) cipherParameters).getKey();
            this.workingKey = generateWorkingKey(key, key.length * 8);
        }
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(getAlgorithmName(), key.length * 8, cipherParameters, Utils.getPurpose(z)));
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void reset() {
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "RC2";
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 8;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public final int processBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        if (this.workingKey == null) {
            throw new IllegalStateException("RC2 engine not initialised");
        }
        if (i + 8 > bArr.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (i2 + 8 > bArr2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.encrypting) {
            encryptBlock(bArr, i, bArr2, i2);
            return 8;
        }
        decryptBlock(bArr, i, bArr2, i2);
        return 8;
    }

    private int rotateWordLeft(int i, int i2) {
        int i3 = i & 65535;
        return (i3 << i2) | (i3 >> (16 - i2));
    }

    private void encryptBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        int iRotateWordLeft = ((bArr[i + 7] & 255) << 8) + (bArr[i + 6] & 255);
        int iRotateWordLeft2 = ((bArr[i + 5] & 255) << 8) + (bArr[i + 4] & 255);
        int iRotateWordLeft3 = ((bArr[i + 3] & 255) << 8) + (bArr[i + 2] & 255);
        int iRotateWordLeft4 = ((bArr[i + 1] & 255) << 8) + (bArr[i + 0] & 255);
        for (int i3 = 0; i3 <= 16; i3 += 4) {
            iRotateWordLeft4 = rotateWordLeft(iRotateWordLeft4 + (iRotateWordLeft3 & (iRotateWordLeft ^ (-1))) + (iRotateWordLeft2 & iRotateWordLeft) + this.workingKey[i3], 1);
            iRotateWordLeft3 = rotateWordLeft(iRotateWordLeft3 + (iRotateWordLeft2 & (iRotateWordLeft4 ^ (-1))) + (iRotateWordLeft & iRotateWordLeft4) + this.workingKey[i3 + 1], 2);
            iRotateWordLeft2 = rotateWordLeft(iRotateWordLeft2 + (iRotateWordLeft & (iRotateWordLeft3 ^ (-1))) + (iRotateWordLeft4 & iRotateWordLeft3) + this.workingKey[i3 + 2], 3);
            iRotateWordLeft = rotateWordLeft(iRotateWordLeft + (iRotateWordLeft4 & (iRotateWordLeft2 ^ (-1))) + (iRotateWordLeft3 & iRotateWordLeft2) + this.workingKey[i3 + 3], 5);
        }
        int iRotateWordLeft5 = iRotateWordLeft4 + this.workingKey[iRotateWordLeft & 63];
        int iRotateWordLeft6 = iRotateWordLeft3 + this.workingKey[iRotateWordLeft5 & 63];
        int iRotateWordLeft7 = iRotateWordLeft2 + this.workingKey[iRotateWordLeft6 & 63];
        int iRotateWordLeft8 = iRotateWordLeft + this.workingKey[iRotateWordLeft7 & 63];
        for (int i4 = 20; i4 <= 40; i4 += 4) {
            iRotateWordLeft5 = rotateWordLeft(iRotateWordLeft5 + (iRotateWordLeft6 & (iRotateWordLeft8 ^ (-1))) + (iRotateWordLeft7 & iRotateWordLeft8) + this.workingKey[i4], 1);
            iRotateWordLeft6 = rotateWordLeft(iRotateWordLeft6 + (iRotateWordLeft7 & (iRotateWordLeft5 ^ (-1))) + (iRotateWordLeft8 & iRotateWordLeft5) + this.workingKey[i4 + 1], 2);
            iRotateWordLeft7 = rotateWordLeft(iRotateWordLeft7 + (iRotateWordLeft8 & (iRotateWordLeft6 ^ (-1))) + (iRotateWordLeft5 & iRotateWordLeft6) + this.workingKey[i4 + 2], 3);
            iRotateWordLeft8 = rotateWordLeft(iRotateWordLeft8 + (iRotateWordLeft5 & (iRotateWordLeft7 ^ (-1))) + (iRotateWordLeft6 & iRotateWordLeft7) + this.workingKey[i4 + 3], 5);
        }
        int iRotateWordLeft9 = iRotateWordLeft5 + this.workingKey[iRotateWordLeft8 & 63];
        int iRotateWordLeft10 = iRotateWordLeft6 + this.workingKey[iRotateWordLeft9 & 63];
        int iRotateWordLeft11 = iRotateWordLeft7 + this.workingKey[iRotateWordLeft10 & 63];
        int iRotateWordLeft12 = iRotateWordLeft8 + this.workingKey[iRotateWordLeft11 & 63];
        for (int i5 = 44; i5 < 64; i5 += 4) {
            iRotateWordLeft9 = rotateWordLeft(iRotateWordLeft9 + (iRotateWordLeft10 & (iRotateWordLeft12 ^ (-1))) + (iRotateWordLeft11 & iRotateWordLeft12) + this.workingKey[i5], 1);
            iRotateWordLeft10 = rotateWordLeft(iRotateWordLeft10 + (iRotateWordLeft11 & (iRotateWordLeft9 ^ (-1))) + (iRotateWordLeft12 & iRotateWordLeft9) + this.workingKey[i5 + 1], 2);
            iRotateWordLeft11 = rotateWordLeft(iRotateWordLeft11 + (iRotateWordLeft12 & (iRotateWordLeft10 ^ (-1))) + (iRotateWordLeft9 & iRotateWordLeft10) + this.workingKey[i5 + 2], 3);
            iRotateWordLeft12 = rotateWordLeft(iRotateWordLeft12 + (iRotateWordLeft9 & (iRotateWordLeft11 ^ (-1))) + (iRotateWordLeft10 & iRotateWordLeft11) + this.workingKey[i5 + 3], 5);
        }
        bArr2[i2 + 0] = (byte) iRotateWordLeft9;
        bArr2[i2 + 1] = (byte) (iRotateWordLeft9 >> 8);
        bArr2[i2 + 2] = (byte) iRotateWordLeft10;
        bArr2[i2 + 3] = (byte) (iRotateWordLeft10 >> 8);
        bArr2[i2 + 4] = (byte) iRotateWordLeft11;
        bArr2[i2 + 5] = (byte) (iRotateWordLeft11 >> 8);
        bArr2[i2 + 6] = (byte) iRotateWordLeft12;
        bArr2[i2 + 7] = (byte) (iRotateWordLeft12 >> 8);
    }

    private void decryptBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        int iRotateWordLeft = ((bArr[i + 7] & 255) << 8) + (bArr[i + 6] & 255);
        int iRotateWordLeft2 = ((bArr[i + 5] & 255) << 8) + (bArr[i + 4] & 255);
        int iRotateWordLeft3 = ((bArr[i + 3] & 255) << 8) + (bArr[i + 2] & 255);
        int iRotateWordLeft4 = ((bArr[i + 1] & 255) << 8) + (bArr[i + 0] & 255);
        for (int i3 = 60; i3 >= 44; i3 -= 4) {
            iRotateWordLeft = rotateWordLeft(iRotateWordLeft, 11) - (((iRotateWordLeft4 & (iRotateWordLeft2 ^ (-1))) + (iRotateWordLeft3 & iRotateWordLeft2)) + this.workingKey[i3 + 3]);
            iRotateWordLeft2 = rotateWordLeft(iRotateWordLeft2, 13) - (((iRotateWordLeft & (iRotateWordLeft3 ^ (-1))) + (iRotateWordLeft4 & iRotateWordLeft3)) + this.workingKey[i3 + 2]);
            iRotateWordLeft3 = rotateWordLeft(iRotateWordLeft3, 14) - (((iRotateWordLeft2 & (iRotateWordLeft4 ^ (-1))) + (iRotateWordLeft & iRotateWordLeft4)) + this.workingKey[i3 + 1]);
            iRotateWordLeft4 = rotateWordLeft(iRotateWordLeft4, 15) - (((iRotateWordLeft3 & (iRotateWordLeft ^ (-1))) + (iRotateWordLeft2 & iRotateWordLeft)) + this.workingKey[i3]);
        }
        int iRotateWordLeft5 = iRotateWordLeft - this.workingKey[iRotateWordLeft2 & 63];
        int iRotateWordLeft6 = iRotateWordLeft2 - this.workingKey[iRotateWordLeft3 & 63];
        int iRotateWordLeft7 = iRotateWordLeft3 - this.workingKey[iRotateWordLeft4 & 63];
        int iRotateWordLeft8 = iRotateWordLeft4 - this.workingKey[iRotateWordLeft5 & 63];
        for (int i4 = 40; i4 >= 20; i4 -= 4) {
            iRotateWordLeft5 = rotateWordLeft(iRotateWordLeft5, 11) - (((iRotateWordLeft8 & (iRotateWordLeft6 ^ (-1))) + (iRotateWordLeft7 & iRotateWordLeft6)) + this.workingKey[i4 + 3]);
            iRotateWordLeft6 = rotateWordLeft(iRotateWordLeft6, 13) - (((iRotateWordLeft5 & (iRotateWordLeft7 ^ (-1))) + (iRotateWordLeft8 & iRotateWordLeft7)) + this.workingKey[i4 + 2]);
            iRotateWordLeft7 = rotateWordLeft(iRotateWordLeft7, 14) - (((iRotateWordLeft6 & (iRotateWordLeft8 ^ (-1))) + (iRotateWordLeft5 & iRotateWordLeft8)) + this.workingKey[i4 + 1]);
            iRotateWordLeft8 = rotateWordLeft(iRotateWordLeft8, 15) - (((iRotateWordLeft7 & (iRotateWordLeft5 ^ (-1))) + (iRotateWordLeft6 & iRotateWordLeft5)) + this.workingKey[i4]);
        }
        int iRotateWordLeft9 = iRotateWordLeft5 - this.workingKey[iRotateWordLeft6 & 63];
        int iRotateWordLeft10 = iRotateWordLeft6 - this.workingKey[iRotateWordLeft7 & 63];
        int iRotateWordLeft11 = iRotateWordLeft7 - this.workingKey[iRotateWordLeft8 & 63];
        int iRotateWordLeft12 = iRotateWordLeft8 - this.workingKey[iRotateWordLeft9 & 63];
        for (int i5 = 16; i5 >= 0; i5 -= 4) {
            iRotateWordLeft9 = rotateWordLeft(iRotateWordLeft9, 11) - (((iRotateWordLeft12 & (iRotateWordLeft10 ^ (-1))) + (iRotateWordLeft11 & iRotateWordLeft10)) + this.workingKey[i5 + 3]);
            iRotateWordLeft10 = rotateWordLeft(iRotateWordLeft10, 13) - (((iRotateWordLeft9 & (iRotateWordLeft11 ^ (-1))) + (iRotateWordLeft12 & iRotateWordLeft11)) + this.workingKey[i5 + 2]);
            iRotateWordLeft11 = rotateWordLeft(iRotateWordLeft11, 14) - (((iRotateWordLeft10 & (iRotateWordLeft12 ^ (-1))) + (iRotateWordLeft9 & iRotateWordLeft12)) + this.workingKey[i5 + 1]);
            iRotateWordLeft12 = rotateWordLeft(iRotateWordLeft12, 15) - (((iRotateWordLeft11 & (iRotateWordLeft9 ^ (-1))) + (iRotateWordLeft10 & iRotateWordLeft9)) + this.workingKey[i5]);
        }
        bArr2[i2 + 0] = (byte) iRotateWordLeft12;
        bArr2[i2 + 1] = (byte) (iRotateWordLeft12 >> 8);
        bArr2[i2 + 2] = (byte) iRotateWordLeft11;
        bArr2[i2 + 3] = (byte) (iRotateWordLeft11 >> 8);
        bArr2[i2 + 4] = (byte) iRotateWordLeft10;
        bArr2[i2 + 5] = (byte) (iRotateWordLeft10 >> 8);
        bArr2[i2 + 6] = (byte) iRotateWordLeft9;
        bArr2[i2 + 7] = (byte) (iRotateWordLeft9 >> 8);
    }
}
