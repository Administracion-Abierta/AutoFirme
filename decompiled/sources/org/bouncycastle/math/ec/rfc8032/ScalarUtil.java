package org.bouncycastle.math.ec.rfc8032;

import org.bouncycastle.util.Integers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/rfc8032/ScalarUtil.class */
abstract class ScalarUtil {
    private static final long M = 4294967295L;

    ScalarUtil() {
    }

    static void addShifted_NP(int i, int i2, int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4) {
        long j = 0;
        long j2 = 0;
        if (i2 == 0) {
            for (int i3 = 0; i3 <= i; i3++) {
                int i4 = iArr3[i3];
                long j3 = j2 + (((long) iArr[i3]) & 4294967295L) + (((long) i4) & 4294967295L);
                long j4 = j + (((long) i4) & 4294967295L) + (((long) iArr2[i3]) & 4294967295L);
                int i5 = (int) j4;
                j = j4 >>> 32;
                iArr3[i3] = i5;
                long j5 = j3 + (((long) i5) & 4294967295L);
                iArr[i3] = (int) j5;
                j2 = j5 >>> 32;
            }
            return;
        }
        if (i2 < 32) {
            int i6 = 0;
            int i7 = 0;
            int i8 = 0;
            for (int i9 = 0; i9 <= i; i9++) {
                int i10 = iArr3[i9];
                int i11 = (i10 << i2) | (i6 >>> (-i2));
                i6 = i10;
                long j6 = j2 + (((long) iArr[i9]) & 4294967295L) + (((long) i11) & 4294967295L);
                int i12 = iArr2[i9];
                int i13 = (i12 << i2) | (i8 >>> (-i2));
                i8 = i12;
                long j7 = j + (((long) i10) & 4294967295L) + (((long) i13) & 4294967295L);
                int i14 = (int) j7;
                j = j7 >>> 32;
                iArr3[i9] = i14;
                int i15 = (i14 << i2) | (i7 >>> (-i2));
                i7 = i14;
                long j8 = j6 + (((long) i15) & 4294967295L);
                iArr[i9] = (int) j8;
                j2 = j8 >>> 32;
            }
            return;
        }
        System.arraycopy(iArr3, 0, iArr4, 0, i);
        int i16 = i2 >>> 5;
        int i17 = i2 & 31;
        if (i17 == 0) {
            for (int i18 = i16; i18 <= i; i18++) {
                long j9 = j2 + (((long) iArr[i18]) & 4294967295L) + (((long) iArr4[i18 - i16]) & 4294967295L);
                long j10 = j + (((long) iArr3[i18]) & 4294967295L) + (((long) iArr2[i18 - i16]) & 4294967295L);
                iArr3[i18] = (int) j10;
                j = j10 >>> 32;
                long j11 = j9 + (((long) iArr3[i18 - i16]) & 4294967295L);
                iArr[i18] = (int) j11;
                j2 = j11 >>> 32;
            }
            return;
        }
        int i19 = 0;
        int i20 = 0;
        int i21 = 0;
        for (int i22 = i16; i22 <= i; i22++) {
            int i23 = iArr4[i22 - i16];
            int i24 = (i23 << i17) | (i19 >>> (-i17));
            i19 = i23;
            long j12 = j2 + (((long) iArr[i22]) & 4294967295L) + (((long) i24) & 4294967295L);
            int i25 = iArr2[i22 - i16];
            int i26 = (i25 << i17) | (i21 >>> (-i17));
            i21 = i25;
            long j13 = j + (((long) iArr3[i22]) & 4294967295L) + (((long) i26) & 4294967295L);
            iArr3[i22] = (int) j13;
            j = j13 >>> 32;
            int i27 = iArr3[i22 - i16];
            int i28 = (i27 << i17) | (i20 >>> (-i17));
            i20 = i27;
            long j14 = j12 + (((long) i28) & 4294967295L);
            iArr[i22] = (int) j14;
            j2 = j14 >>> 32;
        }
    }

    static void addShifted_UV(int i, int i2, int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4) {
        int i3 = i2 >>> 5;
        int i4 = i2 & 31;
        long j = 0;
        long j2 = 0;
        if (i4 == 0) {
            for (int i5 = i3; i5 <= i; i5++) {
                long j3 = j + (((long) iArr[i5]) & 4294967295L);
                long j4 = j2 + (((long) iArr2[i5]) & 4294967295L);
                long j5 = j3 + (((long) iArr3[i5 - i3]) & 4294967295L);
                long j6 = j4 + (((long) iArr4[i5 - i3]) & 4294967295L);
                iArr[i5] = (int) j5;
                j = j5 >>> 32;
                iArr2[i5] = (int) j6;
                j2 = j6 >>> 32;
            }
            return;
        }
        int i6 = 0;
        int i7 = 0;
        for (int i8 = i3; i8 <= i; i8++) {
            int i9 = iArr3[i8 - i3];
            int i10 = iArr4[i8 - i3];
            int i11 = (i9 << i4) | (i6 >>> (-i4));
            int i12 = (i10 << i4) | (i7 >>> (-i4));
            i6 = i9;
            i7 = i10;
            long j7 = j + (((long) iArr[i8]) & 4294967295L) + (((long) i11) & 4294967295L);
            long j8 = j2 + (((long) iArr2[i8]) & 4294967295L) + (((long) i12) & 4294967295L);
            iArr[i8] = (int) j7;
            j = j7 >>> 32;
            iArr2[i8] = (int) j8;
            j2 = j8 >>> 32;
        }
    }

    static int getBitLength(int i, int[] iArr) {
        int i2 = i;
        int i3 = iArr[i2] >> 31;
        while (i2 > 0 && iArr[i2] == i3) {
            i2--;
        }
        return ((i2 * 32) + 32) - Integers.numberOfLeadingZeros(iArr[i2] ^ i3);
    }

    static int getBitLengthPositive(int i, int[] iArr) {
        int i2 = i;
        while (i2 > 0 && iArr[i2] == 0) {
            i2--;
        }
        return ((i2 * 32) + 32) - Integers.numberOfLeadingZeros(iArr[i2]);
    }

    static boolean lessThan(int i, int[] iArr, int[] iArr2) {
        int i2 = i;
        do {
            int i3 = iArr[i2] - Integer.MIN_VALUE;
            int i4 = iArr2[i2] - Integer.MIN_VALUE;
            if (i3 < i4) {
                return true;
            }
            if (i3 > i4) {
                return false;
            }
            i2--;
        } while (i2 >= 0);
        return false;
    }

    static void subShifted_NP(int i, int i2, int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4) {
        long j = 0;
        long j2 = 0;
        if (i2 == 0) {
            for (int i3 = 0; i3 <= i; i3++) {
                int i4 = iArr3[i3];
                long j3 = (j2 + (((long) iArr[i3]) & 4294967295L)) - (((long) i4) & 4294967295L);
                long j4 = (j + (((long) i4) & 4294967295L)) - (((long) iArr2[i3]) & 4294967295L);
                int i5 = (int) j4;
                j = j4 >> 32;
                iArr3[i3] = i5;
                long j5 = j3 - (((long) i5) & 4294967295L);
                iArr[i3] = (int) j5;
                j2 = j5 >> 32;
            }
            return;
        }
        if (i2 < 32) {
            int i6 = 0;
            int i7 = 0;
            int i8 = 0;
            for (int i9 = 0; i9 <= i; i9++) {
                int i10 = iArr3[i9];
                int i11 = (i10 << i2) | (i6 >>> (-i2));
                i6 = i10;
                long j6 = (j2 + (((long) iArr[i9]) & 4294967295L)) - (((long) i11) & 4294967295L);
                int i12 = iArr2[i9];
                int i13 = (i12 << i2) | (i8 >>> (-i2));
                i8 = i12;
                long j7 = (j + (((long) i10) & 4294967295L)) - (((long) i13) & 4294967295L);
                int i14 = (int) j7;
                j = j7 >> 32;
                iArr3[i9] = i14;
                int i15 = (i14 << i2) | (i7 >>> (-i2));
                i7 = i14;
                long j8 = j6 - (((long) i15) & 4294967295L);
                iArr[i9] = (int) j8;
                j2 = j8 >> 32;
            }
            return;
        }
        System.arraycopy(iArr3, 0, iArr4, 0, i);
        int i16 = i2 >>> 5;
        int i17 = i2 & 31;
        if (i17 == 0) {
            for (int i18 = i16; i18 <= i; i18++) {
                long j9 = (j2 + (((long) iArr[i18]) & 4294967295L)) - (((long) iArr4[i18 - i16]) & 4294967295L);
                long j10 = (j + (((long) iArr3[i18]) & 4294967295L)) - (((long) iArr2[i18 - i16]) & 4294967295L);
                iArr3[i18] = (int) j10;
                j = j10 >> 32;
                long j11 = j9 - (((long) iArr3[i18 - i16]) & 4294967295L);
                iArr[i18] = (int) j11;
                j2 = j11 >> 32;
            }
            return;
        }
        int i19 = 0;
        int i20 = 0;
        int i21 = 0;
        for (int i22 = i16; i22 <= i; i22++) {
            int i23 = iArr4[i22 - i16];
            int i24 = (i23 << i17) | (i19 >>> (-i17));
            i19 = i23;
            long j12 = (j2 + (((long) iArr[i22]) & 4294967295L)) - (((long) i24) & 4294967295L);
            int i25 = iArr2[i22 - i16];
            int i26 = (i25 << i17) | (i21 >>> (-i17));
            i21 = i25;
            long j13 = (j + (((long) iArr3[i22]) & 4294967295L)) - (((long) i26) & 4294967295L);
            iArr3[i22] = (int) j13;
            j = j13 >> 32;
            int i27 = iArr3[i22 - i16];
            int i28 = (i27 << i17) | (i20 >>> (-i17));
            i20 = i27;
            long j14 = j12 - (((long) i28) & 4294967295L);
            iArr[i22] = (int) j14;
            j2 = j14 >> 32;
        }
    }

    static void subShifted_UV(int i, int i2, int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4) {
        int i3 = i2 >>> 5;
        int i4 = i2 & 31;
        long j = 0;
        long j2 = 0;
        if (i4 == 0) {
            for (int i5 = i3; i5 <= i; i5++) {
                long j3 = j + (((long) iArr[i5]) & 4294967295L);
                long j4 = j2 + (((long) iArr2[i5]) & 4294967295L);
                long j5 = j3 - (((long) iArr3[i5 - i3]) & 4294967295L);
                long j6 = j4 - (((long) iArr4[i5 - i3]) & 4294967295L);
                iArr[i5] = (int) j5;
                j = j5 >> 32;
                iArr2[i5] = (int) j6;
                j2 = j6 >> 32;
            }
            return;
        }
        int i6 = 0;
        int i7 = 0;
        for (int i8 = i3; i8 <= i; i8++) {
            int i9 = iArr3[i8 - i3];
            int i10 = iArr4[i8 - i3];
            int i11 = (i9 << i4) | (i6 >>> (-i4));
            int i12 = (i10 << i4) | (i7 >>> (-i4));
            i6 = i9;
            i7 = i10;
            long j7 = (j + (((long) iArr[i8]) & 4294967295L)) - (((long) i11) & 4294967295L);
            long j8 = (j2 + (((long) iArr2[i8]) & 4294967295L)) - (((long) i12) & 4294967295L);
            iArr[i8] = (int) j7;
            j = j7 >> 32;
            iArr2[i8] = (int) j8;
            j2 = j8 >> 32;
        }
    }
}
