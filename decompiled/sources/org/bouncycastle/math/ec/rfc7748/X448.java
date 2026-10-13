package org.bouncycastle.math.ec.rfc7748;

import java.security.SecureRandom;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.bouncycastle.math.ec.rfc8032.Ed448;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/rfc7748/X448.class */
public abstract class X448 {
    public static final int POINT_SIZE = 56;
    public static final int SCALAR_SIZE = 56;
    private static final int C_A = 156326;
    private static final int C_A24 = 39082;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/rfc7748/X448$F.class */
    private static class F extends X448Field {
        private F() {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/rfc7748/X448$Friend.class */
    public static class Friend {
        private static final Friend INSTANCE = new Friend();

        private Friend() {
        }
    }

    public static boolean calculateAgreement(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, int i3) {
        scalarMult(bArr, i, bArr2, i2, bArr3, i3);
        return !Arrays.areAllZeroes(bArr3, i3, 56);
    }

    private static int decode32(byte[] bArr, int i) {
        int i2 = bArr[i] & 255;
        int i3 = i + 1;
        int i4 = i2 | ((bArr[i3] & 255) << 8);
        int i5 = i3 + 1;
        return i4 | ((bArr[i5] & 255) << 16) | (bArr[i5 + 1] << 24);
    }

    private static void decodeScalar(byte[] bArr, int i, int[] iArr) {
        for (int i2 = 0; i2 < 14; i2++) {
            iArr[i2] = decode32(bArr, i + (i2 * 4));
        }
        iArr[0] = iArr[0] & (-4);
        iArr[13] = iArr[13] | Integer.MIN_VALUE;
    }

    public static void generatePrivateKey(SecureRandom secureRandom, byte[] bArr) {
        if (bArr.length != 56) {
            throw new IllegalArgumentException(OperatorName.NON_STROKING_CMYK);
        }
        secureRandom.nextBytes(bArr);
        bArr[0] = (byte) (bArr[0] & 252);
        bArr[55] = (byte) (bArr[55] | 128);
    }

    public static void generatePublicKey(byte[] bArr, int i, byte[] bArr2, int i2) {
        scalarMultBase(bArr, i, bArr2, i2);
    }

    private static void pointDouble(int[] iArr, int[] iArr2) {
        int[] iArrCreate = F.create();
        int[] iArrCreate2 = F.create();
        F.add(iArr, iArr2, iArrCreate);
        F.sub(iArr, iArr2, iArrCreate2);
        F.sqr(iArrCreate, iArrCreate);
        F.sqr(iArrCreate2, iArrCreate2);
        F.mul(iArrCreate, iArrCreate2, iArr);
        F.sub(iArrCreate, iArrCreate2, iArrCreate);
        F.mul(iArrCreate, C_A24, iArr2);
        F.add(iArr2, iArrCreate2, iArr2);
        F.mul(iArr2, iArrCreate, iArr2);
    }

    public static void precompute() {
        Ed448.precompute();
    }

    public static void scalarMult(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, int i3) {
        int[] iArr = new int[14];
        decodeScalar(bArr, i, iArr);
        int[] iArrCreate = F.create();
        F.decode(bArr2, i2, iArrCreate);
        int[] iArrCreate2 = F.create();
        F.copy(iArrCreate, 0, iArrCreate2, 0);
        int[] iArrCreate3 = F.create();
        iArrCreate3[0] = 1;
        int[] iArrCreate4 = F.create();
        iArrCreate4[0] = 1;
        int[] iArrCreate5 = F.create();
        int[] iArrCreate6 = F.create();
        int[] iArrCreate7 = F.create();
        int i4 = 447;
        int i5 = 1;
        do {
            F.add(iArrCreate4, iArrCreate5, iArrCreate6);
            F.sub(iArrCreate4, iArrCreate5, iArrCreate4);
            F.add(iArrCreate2, iArrCreate3, iArrCreate5);
            F.sub(iArrCreate2, iArrCreate3, iArrCreate2);
            F.mul(iArrCreate6, iArrCreate2, iArrCreate6);
            F.mul(iArrCreate4, iArrCreate5, iArrCreate4);
            F.sqr(iArrCreate5, iArrCreate5);
            F.sqr(iArrCreate2, iArrCreate2);
            F.sub(iArrCreate5, iArrCreate2, iArrCreate7);
            F.mul(iArrCreate7, C_A24, iArrCreate3);
            F.add(iArrCreate3, iArrCreate2, iArrCreate3);
            F.mul(iArrCreate3, iArrCreate7, iArrCreate3);
            F.mul(iArrCreate2, iArrCreate5, iArrCreate2);
            F.sub(iArrCreate6, iArrCreate4, iArrCreate5);
            F.add(iArrCreate6, iArrCreate4, iArrCreate4);
            F.sqr(iArrCreate4, iArrCreate4);
            F.sqr(iArrCreate5, iArrCreate5);
            F.mul(iArrCreate5, iArrCreate, iArrCreate5);
            i4--;
            int i6 = (iArr[i4 >>> 5] >>> (i4 & 31)) & 1;
            int i7 = i5 ^ i6;
            F.cswap(i7, iArrCreate2, iArrCreate4);
            F.cswap(i7, iArrCreate3, iArrCreate5);
            i5 = i6;
        } while (i4 >= 2);
        for (int i8 = 0; i8 < 2; i8++) {
            pointDouble(iArrCreate2, iArrCreate3);
        }
        F.inv(iArrCreate3, iArrCreate3);
        F.mul(iArrCreate2, iArrCreate3, iArrCreate2);
        F.normalize(iArrCreate2);
        F.encode(iArrCreate2, bArr3, i3);
    }

    public static void scalarMultBase(byte[] bArr, int i, byte[] bArr2, int i2) {
        int[] iArrCreate = F.create();
        int[] iArrCreate2 = F.create();
        Ed448.scalarMultBaseXY(Friend.INSTANCE, bArr, i, iArrCreate, iArrCreate2);
        F.inv(iArrCreate, iArrCreate);
        F.mul(iArrCreate, iArrCreate2, iArrCreate);
        F.sqr(iArrCreate, iArrCreate);
        F.normalize(iArrCreate);
        F.encode(iArrCreate, bArr2, i2);
    }
}
