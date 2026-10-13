package org.bouncycastle.pqc.crypto.bike;

import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/bike/BIKEEngine.class */
class BIKEEngine {
    private int r;
    private int w;
    private int hw;
    private int t;
    private int nbIter;
    private int tau;
    private final BIKERing bikeRing;
    private int L_BYTE;
    private int R_BYTE;
    private int R2_BYTE;

    public BIKEEngine(int i, int i2, int i3, int i4, int i5, int i6) {
        this.r = i;
        this.w = i2;
        this.t = i3;
        this.nbIter = i5;
        this.tau = i6;
        this.hw = this.w / 2;
        this.L_BYTE = i4 / 8;
        this.R_BYTE = (i + 7) >>> 3;
        this.R2_BYTE = ((2 * i) + 7) >>> 3;
        this.bikeRing = new BIKERing(i);
    }

    public int getSessionKeySize() {
        return this.L_BYTE;
    }

    private byte[] functionH(byte[] bArr) {
        byte[] bArr2 = new byte[2 * this.R_BYTE];
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        sHAKEDigest.update(bArr, 0, bArr.length);
        BIKEUtils.generateRandomByteArray(bArr2, 2 * this.r, this.t, sHAKEDigest);
        return bArr2;
    }

    private void functionL(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        byte[] bArr4 = new byte[48];
        SHA3Digest sHA3Digest = new SHA3Digest(384);
        sHA3Digest.update(bArr, 0, bArr.length);
        sHA3Digest.update(bArr2, 0, bArr2.length);
        sHA3Digest.doFinal(bArr4, 0);
        System.arraycopy(bArr4, 0, bArr3, 0, this.L_BYTE);
    }

    private void functionK(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4) {
        byte[] bArr5 = new byte[48];
        SHA3Digest sHA3Digest = new SHA3Digest(384);
        sHA3Digest.update(bArr, 0, bArr.length);
        sHA3Digest.update(bArr2, 0, bArr2.length);
        sHA3Digest.update(bArr3, 0, bArr3.length);
        sHA3Digest.doFinal(bArr5, 0);
        System.arraycopy(bArr5, 0, bArr4, 0, this.L_BYTE);
    }

    public void genKeyPair(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, SecureRandom secureRandom) {
        byte[] bArr5 = new byte[64];
        secureRandom.nextBytes(bArr5);
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        sHAKEDigest.update(bArr5, 0, this.L_BYTE);
        BIKEUtils.generateRandomByteArray(bArr, this.r, this.hw, sHAKEDigest);
        BIKEUtils.generateRandomByteArray(bArr2, this.r, this.hw, sHAKEDigest);
        long[] jArrCreate = this.bikeRing.create();
        long[] jArrCreate2 = this.bikeRing.create();
        this.bikeRing.decodeBytes(bArr, jArrCreate);
        this.bikeRing.decodeBytes(bArr2, jArrCreate2);
        long[] jArrCreate3 = this.bikeRing.create();
        this.bikeRing.inv(jArrCreate, jArrCreate3);
        this.bikeRing.multiply(jArrCreate3, jArrCreate2, jArrCreate3);
        this.bikeRing.encodeBytes(jArrCreate3, bArr4);
        System.arraycopy(bArr5, this.L_BYTE, bArr3, 0, bArr3.length);
    }

    public void encaps(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, SecureRandom secureRandom) {
        byte[] bArr5 = new byte[this.L_BYTE];
        secureRandom.nextBytes(bArr5);
        byte[] bArrFunctionH = functionH(bArr5);
        byte[] bArr6 = new byte[this.R_BYTE];
        byte[] bArr7 = new byte[this.R_BYTE];
        splitEBytes(bArrFunctionH, bArr6, bArr7);
        long[] jArrCreate = this.bikeRing.create();
        long[] jArrCreate2 = this.bikeRing.create();
        this.bikeRing.decodeBytes(bArr6, jArrCreate);
        this.bikeRing.decodeBytes(bArr7, jArrCreate2);
        long[] jArrCreate3 = this.bikeRing.create();
        this.bikeRing.decodeBytes(bArr4, jArrCreate3);
        this.bikeRing.multiply(jArrCreate3, jArrCreate2, jArrCreate3);
        this.bikeRing.add(jArrCreate3, jArrCreate, jArrCreate3);
        this.bikeRing.encodeBytes(jArrCreate3, bArr);
        functionL(bArr6, bArr7, bArr2);
        Bytes.xorTo(this.L_BYTE, bArr5, bArr2);
        functionK(bArr5, bArr, bArr2, bArr3);
    }

    public void decaps(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5, byte[] bArr6) {
        int[] iArr = new int[this.hw];
        int[] iArr2 = new int[this.hw];
        convertToCompact(iArr, bArr2);
        convertToCompact(iArr2, bArr3);
        byte[] bArrBGFDecoder = BGFDecoder(computeSyndrome(bArr5, bArr2), iArr, iArr2);
        byte[] bArr7 = new byte[2 * this.R_BYTE];
        BIKEUtils.fromBitArrayToByteArray(bArr7, bArrBGFDecoder, 0, 2 * this.r);
        byte[] bArr8 = new byte[this.R_BYTE];
        byte[] bArr9 = new byte[this.R_BYTE];
        splitEBytes(bArr7, bArr8, bArr9);
        byte[] bArr10 = new byte[this.L_BYTE];
        functionL(bArr8, bArr9, bArr10);
        Bytes.xorTo(this.L_BYTE, bArr6, bArr10);
        if (Arrays.areEqual(bArr7, 0, this.R2_BYTE, functionH(bArr10), 0, this.R2_BYTE)) {
            functionK(bArr10, bArr5, bArr6, bArr);
        } else {
            functionK(bArr4, bArr5, bArr6, bArr);
        }
    }

    private byte[] computeSyndrome(byte[] bArr, byte[] bArr2) {
        long[] jArrCreate = this.bikeRing.create();
        long[] jArrCreate2 = this.bikeRing.create();
        this.bikeRing.decodeBytes(bArr, jArrCreate);
        this.bikeRing.decodeBytes(bArr2, jArrCreate2);
        this.bikeRing.multiply(jArrCreate, jArrCreate2, jArrCreate);
        return this.bikeRing.encodeBitsTransposed(jArrCreate);
    }

    private byte[] BGFDecoder(byte[] bArr, int[] iArr, int[] iArr2) {
        byte[] bArr2 = new byte[2 * this.r];
        int[] columnFromCompactVersion = getColumnFromCompactVersion(iArr);
        int[] columnFromCompactVersion2 = getColumnFromCompactVersion(iArr2);
        byte[] bArr3 = new byte[2 * this.r];
        byte[] bArr4 = new byte[this.r];
        byte[] bArr5 = new byte[2 * this.r];
        BFIter(bArr, bArr2, threshold(BIKEUtils.getHammingWeight(bArr), this.r), iArr, iArr2, columnFromCompactVersion, columnFromCompactVersion2, bArr3, bArr5, bArr4);
        BFMaskedIter(bArr, bArr2, bArr3, ((this.hw + 1) / 2) + 1, iArr, iArr2, columnFromCompactVersion, columnFromCompactVersion2);
        BFMaskedIter(bArr, bArr2, bArr5, ((this.hw + 1) / 2) + 1, iArr, iArr2, columnFromCompactVersion, columnFromCompactVersion2);
        for (int i = 1; i < this.nbIter; i++) {
            Arrays.fill(bArr3, (byte) 0);
            BFIter2(bArr, bArr2, threshold(BIKEUtils.getHammingWeight(bArr), this.r), iArr, iArr2, columnFromCompactVersion, columnFromCompactVersion2, bArr4);
        }
        if (BIKEUtils.getHammingWeight(bArr) == 0) {
            return bArr2;
        }
        return null;
    }

    private void BFIter(byte[] bArr, byte[] bArr2, int i, int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4, byte[] bArr3, byte[] bArr4, byte[] bArr5) {
        ctrAll(iArr3, bArr, bArr5);
        int i2 = bArr5[0] & 255;
        int i3 = ((i2 - i) >> 31) + 1;
        int i4 = ((i2 - (i - this.tau)) >> 31) + 1;
        bArr2[0] = (byte) (bArr2[0] ^ ((byte) i3));
        bArr3[0] = (byte) i3;
        bArr4[0] = (byte) i4;
        for (int i5 = 1; i5 < this.r; i5++) {
            int i6 = bArr5[i5] & 255;
            int i7 = ((i6 - i) >> 31) + 1;
            int i8 = ((i6 - (i - this.tau)) >> 31) + 1;
            int i9 = this.r - i5;
            bArr2[i9] = (byte) (bArr2[i9] ^ ((byte) i7));
            bArr3[i5] = (byte) i7;
            bArr4[i5] = (byte) i8;
        }
        ctrAll(iArr4, bArr, bArr5);
        int i10 = bArr5[0] & 255;
        int i11 = ((i10 - i) >> 31) + 1;
        int i12 = ((i10 - (i - this.tau)) >> 31) + 1;
        int i13 = this.r;
        bArr2[i13] = (byte) (bArr2[i13] ^ ((byte) i11));
        bArr3[this.r] = (byte) i11;
        bArr4[this.r] = (byte) i12;
        for (int i14 = 1; i14 < this.r; i14++) {
            int i15 = bArr5[i14] & 255;
            int i16 = ((i15 - i) >> 31) + 1;
            int i17 = ((i15 - (i - this.tau)) >> 31) + 1;
            int i18 = (this.r + this.r) - i14;
            bArr2[i18] = (byte) (bArr2[i18] ^ ((byte) i16));
            bArr3[this.r + i14] = (byte) i16;
            bArr4[this.r + i14] = (byte) i17;
        }
        for (int i19 = 0; i19 < 2 * this.r; i19++) {
            recomputeSyndrome(bArr, i19, iArr, iArr2, bArr3[i19] != 0);
        }
    }

    private void BFIter2(byte[] bArr, byte[] bArr2, int i, int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4, byte[] bArr3) {
        int[] iArr5 = new int[2 * this.r];
        ctrAll(iArr3, bArr, bArr3);
        int i2 = (((bArr3[0] & 255) - i) >> 31) + 1;
        bArr2[0] = (byte) (bArr2[0] ^ ((byte) i2));
        iArr5[0] = i2;
        for (int i3 = 1; i3 < this.r; i3++) {
            int i4 = (((bArr3[i3] & 255) - i) >> 31) + 1;
            int i5 = this.r - i3;
            bArr2[i5] = (byte) (bArr2[i5] ^ ((byte) i4));
            iArr5[i3] = i4;
        }
        ctrAll(iArr4, bArr, bArr3);
        int i6 = (((bArr3[0] & 255) - i) >> 31) + 1;
        int i7 = this.r;
        bArr2[i7] = (byte) (bArr2[i7] ^ ((byte) i6));
        iArr5[this.r] = i6;
        for (int i8 = 1; i8 < this.r; i8++) {
            int i9 = (((bArr3[i8] & 255) - i) >> 31) + 1;
            int i10 = (this.r + this.r) - i8;
            bArr2[i10] = (byte) (bArr2[i10] ^ ((byte) i9));
            iArr5[this.r + i8] = i9;
        }
        for (int i11 = 0; i11 < 2 * this.r; i11++) {
            recomputeSyndrome(bArr, i11, iArr, iArr2, iArr5[i11] == 1);
        }
    }

    private void BFMaskedIter(byte[] bArr, byte[] bArr2, byte[] bArr3, int i, int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4) {
        int[] iArr5 = new int[2 * this.r];
        for (int i2 = 0; i2 < this.r; i2++) {
            if (bArr3[i2] == 1) {
                boolean z = ctr(iArr3, bArr, i2) >= i;
                updateNewErrorIndex(bArr2, i2, z);
                iArr5[i2] = z ? 1 : 0;
            }
        }
        for (int i3 = 0; i3 < this.r; i3++) {
            if (bArr3[this.r + i3] == 1) {
                boolean z2 = ctr(iArr4, bArr, i3) >= i;
                updateNewErrorIndex(bArr2, this.r + i3, z2);
                iArr5[this.r + i3] = z2 ? 1 : 0;
            }
        }
        for (int i4 = 0; i4 < 2 * this.r; i4++) {
            recomputeSyndrome(bArr, i4, iArr, iArr2, iArr5[i4] == 1);
        }
    }

    private int threshold(int i, int i2) {
        switch (i2) {
            case 12323:
                return thresholdFromParameters(i, 0.0069722d, 13.53d, 36);
            case 24659:
                return thresholdFromParameters(i, 0.005265d, 15.2588d, 52);
            case 40973:
                return thresholdFromParameters(i, 0.00402312d, 17.8785d, 69);
            default:
                throw new IllegalArgumentException();
        }
    }

    private static int thresholdFromParameters(int i, double d, double d2, int i2) {
        return Math.max(i2, (int) Math.floor((d * ((double) i)) + d2));
    }

    private int ctr(int[] iArr, byte[] bArr, int i) {
        int i2 = 0;
        int i3 = 0;
        int i4 = this.hw - 4;
        while (i3 <= i4) {
            int i5 = (iArr[i3 + 0] + i) - this.r;
            int i6 = (iArr[i3 + 1] + i) - this.r;
            int i7 = (iArr[i3 + 2] + i) - this.r;
            int i8 = (iArr[i3 + 3] + i) - this.r;
            i2 = i2 + (bArr[i5 + ((i5 >> 31) & this.r)] & 255) + (bArr[i6 + ((i6 >> 31) & this.r)] & 255) + (bArr[i7 + ((i7 >> 31) & this.r)] & 255) + (bArr[i8 + ((i8 >> 31) & this.r)] & 255);
            i3 += 4;
        }
        while (i3 < this.hw) {
            int i9 = (iArr[i3] + i) - this.r;
            i2 += bArr[i9 + ((i9 >> 31) & this.r)] & 255;
            i3++;
        }
        return i2;
    }

    private void ctrAll(int[] iArr, byte[] bArr, byte[] bArr2) {
        int i = iArr[0];
        int i2 = this.r - i;
        System.arraycopy(bArr, i, bArr2, 0, i2);
        System.arraycopy(bArr, 0, bArr2, i2, i);
        for (int i3 = 1; i3 < this.hw; i3++) {
            int i4 = iArr[i3];
            int i5 = this.r - i4;
            int i6 = 0;
            int i7 = i5 - 4;
            while (i6 <= i7) {
                int i8 = i6 + 0;
                bArr2[i8] = (byte) (bArr2[i8] + (bArr[i4 + i6 + 0] & 255));
                int i9 = i6 + 1;
                bArr2[i9] = (byte) (bArr2[i9] + (bArr[i4 + i6 + 1] & 255));
                int i10 = i6 + 2;
                bArr2[i10] = (byte) (bArr2[i10] + (bArr[i4 + i6 + 2] & 255));
                int i11 = i6 + 3;
                bArr2[i11] = (byte) (bArr2[i11] + (bArr[i4 + i6 + 3] & 255));
                i6 += 4;
            }
            while (i6 < i5) {
                int i12 = i6;
                bArr2[i12] = (byte) (bArr2[i12] + (bArr[i4 + i6] & 255));
                i6++;
            }
            int i13 = i5;
            int i14 = this.r - 4;
            while (i13 <= i14) {
                int i15 = i13 + 0;
                bArr2[i15] = (byte) (bArr2[i15] + (bArr[(i13 + 0) - i5] & 255));
                int i16 = i13 + 1;
                bArr2[i16] = (byte) (bArr2[i16] + (bArr[(i13 + 1) - i5] & 255));
                int i17 = i13 + 2;
                bArr2[i17] = (byte) (bArr2[i17] + (bArr[(i13 + 2) - i5] & 255));
                int i18 = i13 + 3;
                bArr2[i18] = (byte) (bArr2[i18] + (bArr[(i13 + 3) - i5] & 255));
                i13 += 4;
            }
            while (i13 < this.r) {
                int i19 = i13;
                bArr2[i19] = (byte) (bArr2[i19] + (bArr[i13 - i5] & 255));
                i13++;
            }
        }
    }

    private void convertToCompact(int[] iArr, byte[] bArr) {
        int i = 0;
        for (int i2 = 0; i2 < this.R_BYTE; i2++) {
            for (int i3 = 0; i3 < 8 && (i2 * 8) + i3 != this.r; i3++) {
                int i4 = (bArr[i2] >> i3) & 1;
                iArr[i] = (((i2 * 8) + i3) & (-i4)) | (iArr[i] & ((-i4) ^ (-1)));
                i = (i + i4) % this.hw;
            }
        }
    }

    private int[] getColumnFromCompactVersion(int[] iArr) {
        int[] iArr2 = new int[this.hw];
        if (iArr[0] == 0) {
            iArr2[0] = 0;
            for (int i = 1; i < this.hw; i++) {
                iArr2[i] = this.r - iArr[this.hw - i];
            }
        } else {
            for (int i2 = 0; i2 < this.hw; i2++) {
                iArr2[i2] = this.r - iArr[(this.hw - 1) - i2];
            }
        }
        return iArr2;
    }

    private void recomputeSyndrome(byte[] bArr, int i, int[] iArr, int[] iArr2, boolean z) {
        byte b = z ? (byte) 1 : (byte) 0;
        if (i < this.r) {
            for (int i2 = 0; i2 < this.hw; i2++) {
                if (iArr[i2] <= i) {
                    int i3 = i - iArr[i2];
                    bArr[i3] = (byte) (bArr[i3] ^ b);
                } else {
                    int i4 = (this.r + i) - iArr[i2];
                    bArr[i4] = (byte) (bArr[i4] ^ b);
                }
            }
            return;
        }
        for (int i5 = 0; i5 < this.hw; i5++) {
            if (iArr2[i5] <= i - this.r) {
                int i6 = (i - this.r) - iArr2[i5];
                bArr[i6] = (byte) (bArr[i6] ^ b);
            } else {
                int i7 = (this.r - iArr2[i5]) + (i - this.r);
                bArr[i7] = (byte) (bArr[i7] ^ b);
            }
        }
    }

    private void splitEBytes(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        int i = this.r & 7;
        System.arraycopy(bArr, 0, bArr2, 0, this.R_BYTE - 1);
        byte b = bArr[this.R_BYTE - 1];
        byte b2 = (byte) ((-1) << i);
        bArr2[this.R_BYTE - 1] = (byte) (b & (b2 ^ (-1)));
        byte b3 = (byte) (b & b2);
        for (int i2 = 0; i2 < this.R_BYTE; i2++) {
            byte b4 = bArr[this.R_BYTE + i2];
            bArr3[i2] = (byte) ((b4 << (8 - i)) | ((b3 & 255) >>> i));
            b3 = b4;
        }
    }

    private void updateNewErrorIndex(byte[] bArr, int i, boolean z) {
        int i2 = i;
        if (i != 0 && i != this.r) {
            i2 = i > this.r ? ((2 * this.r) - i) + this.r : this.r - i;
        }
        int i3 = i2;
        bArr[i3] = (byte) (bArr[i3] ^ (z ? (byte) 1 : (byte) 0));
    }
}
