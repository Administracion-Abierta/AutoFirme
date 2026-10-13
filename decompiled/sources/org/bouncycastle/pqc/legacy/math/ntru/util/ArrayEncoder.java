package org.bouncycastle.pqc.legacy.math.ntru.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/ntru/util/ArrayEncoder.class */
public class ArrayEncoder {
    private static final int[] COEFF1_TABLE = {0, 0, 0, 1, 1, 1, -1, -1};
    private static final int[] COEFF2_TABLE = {0, 1, -1, 0, 1, -1, 0, 1};
    private static final int[] BIT1_TABLE = {1, 1, 1, 0, 0, 0, 1, 0, 1};
    private static final int[] BIT2_TABLE = {1, 1, 1, 1, 0, 0, 0, 1, 0};
    private static final int[] BIT3_TABLE = {1, 0, 1, 0, 0, 1, 1, 1, 0};

    public static byte[] encodeModQ(int[] iArr, int i) {
        int iNumberOfLeadingZeros = 31 - Integer.numberOfLeadingZeros(i);
        byte[] bArr = new byte[((iArr.length * iNumberOfLeadingZeros) + 7) / 8];
        int i2 = 0;
        int i3 = 0;
        for (int i4 : iArr) {
            for (int i5 = 0; i5 < iNumberOfLeadingZeros; i5++) {
                int i6 = i3;
                bArr[i6] = (byte) (bArr[i6] | (((i4 >> i5) & 1) << i2));
                if (i2 == 7) {
                    i2 = 0;
                    i3++;
                } else {
                    i2++;
                }
            }
        }
        return bArr;
    }

    public static int[] decodeModQ(byte[] bArr, int i, int i2) {
        int[] iArr = new int[i];
        int iNumberOfLeadingZeros = 31 - Integer.numberOfLeadingZeros(i2);
        int i3 = i * iNumberOfLeadingZeros;
        int i4 = 0;
        for (int i5 = 0; i5 < i3; i5++) {
            if (i5 > 0 && i5 % iNumberOfLeadingZeros == 0) {
                i4++;
            }
            int i6 = i4;
            iArr[i6] = iArr[i6] + (getBit(bArr, i5) << (i5 % iNumberOfLeadingZeros));
        }
        return iArr;
    }

    public static int[] decodeModQ(InputStream inputStream, int i, int i2) throws IOException {
        return decodeModQ(Util.readFullLength(inputStream, ((i * (31 - Integer.numberOfLeadingZeros(i2))) + 7) / 8), i, i2);
    }

    public static int[] decodeMod3Sves(byte[] bArr, int i) {
        int[] iArr = new int[i];
        int i2 = 0;
        int i3 = 0;
        while (i3 < bArr.length * 8) {
            int i4 = i3;
            int i5 = i3 + 1;
            int bit = getBit(bArr, i4);
            int i6 = i5 + 1;
            int bit2 = getBit(bArr, i5);
            i3 = i6 + 1;
            int bit3 = (bit * 4) + (bit2 * 2) + getBit(bArr, i6);
            int i7 = i2;
            int i8 = i2 + 1;
            iArr[i7] = COEFF1_TABLE[bit3];
            i2 = i8 + 1;
            iArr[i8] = COEFF2_TABLE[bit3];
            if (i2 > i - 2) {
                break;
            }
        }
        return iArr;
    }

    public static byte[] encodeMod3Sves(int[] iArr) {
        byte[] bArr = new byte[((((iArr.length * 3) + 1) / 2) + 7) / 8];
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        while (i3 < (iArr.length / 2) * 2) {
            int i4 = i3;
            int i5 = i3 + 1;
            int i6 = iArr[i4] + 1;
            i3 = i5 + 1;
            int i7 = iArr[i5] + 1;
            if (i6 == 0 && i7 == 0) {
                throw new IllegalStateException("Illegal encoding!");
            }
            int i8 = (i6 * 3) + i7;
            int[] iArr2 = {BIT1_TABLE[i8], BIT2_TABLE[i8], BIT3_TABLE[i8]};
            for (int i9 = 0; i9 < 3; i9++) {
                int i10 = i2;
                bArr[i10] = (byte) (bArr[i10] | (iArr2[i9] << i));
                if (i == 7) {
                    i = 0;
                    i2++;
                } else {
                    i++;
                }
            }
        }
        return bArr;
    }

    public static byte[] encodeMod3Tight(int[] iArr) {
        BigInteger bigIntegerAdd = BigInteger.ZERO;
        for (int length = iArr.length - 1; length >= 0; length--) {
            bigIntegerAdd = bigIntegerAdd.multiply(BigInteger.valueOf(3L)).add(BigInteger.valueOf(iArr[length] + 1));
        }
        int iBitLength = (BigInteger.valueOf(3L).pow(iArr.length).bitLength() + 7) / 8;
        byte[] byteArray = bigIntegerAdd.toByteArray();
        if (byteArray.length < iBitLength) {
            byte[] bArr = new byte[iBitLength];
            System.arraycopy(byteArray, 0, bArr, iBitLength - byteArray.length, byteArray.length);
            return bArr;
        }
        if (byteArray.length > iBitLength) {
            byteArray = Arrays.copyOfRange(byteArray, 1, byteArray.length);
        }
        return byteArray;
    }

    public static int[] decodeMod3Tight(byte[] bArr, int i) {
        BigInteger bigInteger = new BigInteger(1, bArr);
        int[] iArr = new int[i];
        for (int i2 = 0; i2 < i; i2++) {
            iArr[i2] = bigInteger.mod(BigInteger.valueOf(3L)).intValue() - 1;
            if (iArr[i2] > 1) {
                int i3 = i2;
                iArr[i3] = iArr[i3] - 3;
            }
            bigInteger = bigInteger.divide(BigInteger.valueOf(3L));
        }
        return iArr;
    }

    public static int[] decodeMod3Tight(InputStream inputStream, int i) throws IOException {
        return decodeMod3Tight(Util.readFullLength(inputStream, (int) Math.ceil(((((double) i) * Math.log(3.0d)) / Math.log(2.0d)) / 8.0d)), i);
    }

    private static int getBit(byte[] bArr, int i) {
        return ((bArr[i / 8] & 255) >> (i % 8)) & 1;
    }
}
