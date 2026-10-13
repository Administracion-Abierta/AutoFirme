package org.bouncycastle.math.ec.rfc7748;

import com.sun.jna.platform.win32.WinNT;
import org.bouncycastle.math.raw.Mod;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/rfc7748/X25519Field.class */
public abstract class X25519Field {
    public static final int SIZE = 10;
    private static final int M24 = 16777215;
    private static final int M25 = 33554431;
    private static final int M26 = 67108863;
    private static final int[] P32 = {-19, -1, -1, -1, -1, -1, -1, WinNT.MAXLONG};
    private static final int[] ROOT_NEG_ONE = {-32595792, -7943725, 4688975, 3500415, 6194736, 33281959, -12573105, -1002827, 163343, 5703241};

    protected X25519Field() {
    }

    public static void add(int[] iArr, int[] iArr2, int[] iArr3) {
        for (int i = 0; i < 10; i++) {
            iArr3[i] = iArr[i] + iArr2[i];
        }
    }

    public static void addOne(int[] iArr) {
        iArr[0] = iArr[0] + 1;
    }

    public static void addOne(int[] iArr, int i) {
        iArr[i] = iArr[i] + 1;
    }

    public static void apm(int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4) {
        for (int i = 0; i < 10; i++) {
            int i2 = iArr[i];
            int i3 = iArr2[i];
            iArr3[i] = i2 + i3;
            iArr4[i] = i2 - i3;
        }
    }

    public static int areEqual(int[] iArr, int[] iArr2) {
        int i = 0;
        for (int i2 = 0; i2 < 10; i2++) {
            i |= iArr[i2] ^ iArr2[i2];
        }
        return (((i >>> 1) | (i & 1)) - 1) >> 31;
    }

    public static boolean areEqualVar(int[] iArr, int[] iArr2) {
        return 0 != areEqual(iArr, iArr2);
    }

    public static void carry(int[] iArr) {
        int i = iArr[0];
        int i2 = iArr[1];
        int i3 = iArr[2];
        int i4 = iArr[3];
        int i5 = iArr[4];
        int i6 = iArr[5];
        int i7 = iArr[6];
        int i8 = iArr[7];
        int i9 = iArr[8];
        int i10 = iArr[9];
        int i11 = i3 + (i2 >> 26);
        int i12 = i2 & M26;
        int i13 = i5 + (i4 >> 26);
        int i14 = i4 & M26;
        int i15 = i8 + (i7 >> 26);
        int i16 = i7 & M26;
        int i17 = i10 + (i9 >> 26);
        int i18 = i9 & M26;
        int i19 = i14 + (i11 >> 25);
        int i20 = i11 & M25;
        int i21 = i6 + (i13 >> 25);
        int i22 = i13 & M25;
        int i23 = i18 + (i15 >> 25);
        int i24 = i15 & M25;
        int i25 = i + ((i17 >> 25) * 38);
        int i26 = i17 & M25;
        int i27 = i12 + (i25 >> 26);
        int i28 = i25 & M26;
        int i29 = i16 + (i21 >> 26);
        int i30 = i21 & M26;
        int i31 = i20 + (i27 >> 26);
        int i32 = i27 & M26;
        int i33 = i22 + (i19 >> 26);
        int i34 = i19 & M26;
        int i35 = i24 + (i29 >> 26);
        int i36 = i29 & M26;
        int i37 = i26 + (i23 >> 26);
        int i38 = i23 & M26;
        iArr[0] = i28;
        iArr[1] = i32;
        iArr[2] = i31;
        iArr[3] = i34;
        iArr[4] = i33;
        iArr[5] = i30;
        iArr[6] = i36;
        iArr[7] = i35;
        iArr[8] = i38;
        iArr[9] = i37;
    }

    public static void cmov(int i, int[] iArr, int i2, int[] iArr2, int i3) {
        for (int i4 = 0; i4 < 10; i4++) {
            int i5 = iArr2[i3 + i4];
            iArr2[i3 + i4] = i5 ^ ((i5 ^ iArr[i2 + i4]) & i);
        }
    }

    public static void cnegate(int i, int[] iArr) {
        int i2 = 0 - i;
        for (int i3 = 0; i3 < 10; i3++) {
            iArr[i3] = (iArr[i3] ^ i2) - i2;
        }
    }

    public static void copy(int[] iArr, int i, int[] iArr2, int i2) {
        for (int i3 = 0; i3 < 10; i3++) {
            iArr2[i2 + i3] = iArr[i + i3];
        }
    }

    public static int[] create() {
        return new int[10];
    }

    public static int[] createTable(int i) {
        return new int[10 * i];
    }

    public static void cswap(int i, int[] iArr, int[] iArr2) {
        int i2 = 0 - i;
        for (int i3 = 0; i3 < 10; i3++) {
            int i4 = iArr[i3];
            int i5 = iArr2[i3];
            int i6 = i2 & (i4 ^ i5);
            iArr[i3] = i4 ^ i6;
            iArr2[i3] = i5 ^ i6;
        }
    }

    public static void decode(int[] iArr, int i, int[] iArr2) {
        decode128(iArr, i, iArr2, 0);
        decode128(iArr, i + 4, iArr2, 5);
        iArr2[9] = iArr2[9] & M24;
    }

    public static void decode(byte[] bArr, int[] iArr) {
        decode128(bArr, 0, iArr, 0);
        decode128(bArr, 16, iArr, 5);
        iArr[9] = iArr[9] & M24;
    }

    public static void decode(byte[] bArr, int i, int[] iArr) {
        decode128(bArr, i, iArr, 0);
        decode128(bArr, i + 16, iArr, 5);
        iArr[9] = iArr[9] & M24;
    }

    public static void decode(byte[] bArr, int i, int[] iArr, int i2) {
        decode128(bArr, i, iArr, i2);
        decode128(bArr, i + 16, iArr, i2 + 5);
        int i3 = i2 + 9;
        iArr[i3] = iArr[i3] & M24;
    }

    private static void decode128(int[] iArr, int i, int[] iArr2, int i2) {
        int i3 = iArr[i + 0];
        int i4 = iArr[i + 1];
        int i5 = iArr[i + 2];
        int i6 = iArr[i + 3];
        iArr2[i2 + 0] = i3 & M26;
        iArr2[i2 + 1] = ((i4 << 6) | (i3 >>> 26)) & M26;
        iArr2[i2 + 2] = ((i5 << 12) | (i4 >>> 20)) & M25;
        iArr2[i2 + 3] = ((i6 << 19) | (i5 >>> 13)) & M26;
        iArr2[i2 + 4] = i6 >>> 7;
    }

    private static void decode128(byte[] bArr, int i, int[] iArr, int i2) {
        int iDecode32 = decode32(bArr, i + 0);
        int iDecode33 = decode32(bArr, i + 4);
        int iDecode34 = decode32(bArr, i + 8);
        int iDecode35 = decode32(bArr, i + 12);
        iArr[i2 + 0] = iDecode32 & M26;
        iArr[i2 + 1] = ((iDecode33 << 6) | (iDecode32 >>> 26)) & M26;
        iArr[i2 + 2] = ((iDecode34 << 12) | (iDecode33 >>> 20)) & M25;
        iArr[i2 + 3] = ((iDecode35 << 19) | (iDecode34 >>> 13)) & M26;
        iArr[i2 + 4] = iDecode35 >>> 7;
    }

    private static int decode32(byte[] bArr, int i) {
        int i2 = bArr[i] & 255;
        int i3 = i + 1;
        int i4 = i2 | ((bArr[i3] & 255) << 8);
        int i5 = i3 + 1;
        return i4 | ((bArr[i5] & 255) << 16) | (bArr[i5 + 1] << 24);
    }

    public static void encode(int[] iArr, int[] iArr2, int i) {
        encode128(iArr, 0, iArr2, i);
        encode128(iArr, 5, iArr2, i + 4);
    }

    public static void encode(int[] iArr, byte[] bArr) {
        encode128(iArr, 0, bArr, 0);
        encode128(iArr, 5, bArr, 16);
    }

    public static void encode(int[] iArr, byte[] bArr, int i) {
        encode128(iArr, 0, bArr, i);
        encode128(iArr, 5, bArr, i + 16);
    }

    public static void encode(int[] iArr, int i, byte[] bArr, int i2) {
        encode128(iArr, i, bArr, i2);
        encode128(iArr, i + 5, bArr, i2 + 16);
    }

    private static void encode128(int[] iArr, int i, int[] iArr2, int i2) {
        int i3 = iArr[i + 0];
        int i4 = iArr[i + 1];
        int i5 = iArr[i + 2];
        int i6 = iArr[i + 3];
        int i7 = iArr[i + 4];
        iArr2[i2 + 0] = i3 | (i4 << 26);
        iArr2[i2 + 1] = (i4 >>> 6) | (i5 << 20);
        iArr2[i2 + 2] = (i5 >>> 12) | (i6 << 13);
        iArr2[i2 + 3] = (i6 >>> 19) | (i7 << 7);
    }

    private static void encode128(int[] iArr, int i, byte[] bArr, int i2) {
        int i3 = iArr[i + 0];
        int i4 = iArr[i + 1];
        int i5 = iArr[i + 2];
        int i6 = iArr[i + 3];
        int i7 = iArr[i + 4];
        encode32(i3 | (i4 << 26), bArr, i2 + 0);
        encode32((i4 >>> 6) | (i5 << 20), bArr, i2 + 4);
        encode32((i5 >>> 12) | (i6 << 13), bArr, i2 + 8);
        encode32((i6 >>> 19) | (i7 << 7), bArr, i2 + 12);
    }

    private static void encode32(int i, byte[] bArr, int i2) {
        bArr[i2] = (byte) i;
        int i3 = i2 + 1;
        bArr[i3] = (byte) (i >>> 8);
        int i4 = i3 + 1;
        bArr[i4] = (byte) (i >>> 16);
        bArr[i4 + 1] = (byte) (i >>> 24);
    }

    public static void inv(int[] iArr, int[] iArr2) {
        int[] iArrCreate = create();
        int[] iArr3 = new int[8];
        copy(iArr, 0, iArrCreate, 0);
        normalize(iArrCreate);
        encode(iArrCreate, iArr3, 0);
        Mod.modOddInverse(P32, iArr3, iArr3);
        decode(iArr3, 0, iArr2);
    }

    public static void invVar(int[] iArr, int[] iArr2) {
        int[] iArrCreate = create();
        int[] iArr3 = new int[8];
        copy(iArr, 0, iArrCreate, 0);
        normalize(iArrCreate);
        encode(iArrCreate, iArr3, 0);
        Mod.modOddInverseVar(P32, iArr3, iArr3);
        decode(iArr3, 0, iArr2);
    }

    public static int isOne(int[] iArr) {
        int i = iArr[0] ^ 1;
        for (int i2 = 1; i2 < 10; i2++) {
            i |= iArr[i2];
        }
        return (((i >>> 1) | (i & 1)) - 1) >> 31;
    }

    public static boolean isOneVar(int[] iArr) {
        return 0 != isOne(iArr);
    }

    public static int isZero(int[] iArr) {
        int i = 0;
        for (int i2 = 0; i2 < 10; i2++) {
            i |= iArr[i2];
        }
        return (((i >>> 1) | (i & 1)) - 1) >> 31;
    }

    public static boolean isZeroVar(int[] iArr) {
        return 0 != isZero(iArr);
    }

    public static void mul(int[] iArr, int i, int[] iArr2) {
        int i2 = iArr[0];
        int i3 = iArr[1];
        int i4 = iArr[2];
        int i5 = iArr[3];
        int i6 = iArr[4];
        int i7 = iArr[5];
        int i8 = iArr[6];
        int i9 = iArr[7];
        int i10 = iArr[8];
        int i11 = iArr[9];
        long j = ((long) i4) * ((long) i);
        int i12 = ((int) j) & M25;
        long j2 = j >> 25;
        long j3 = ((long) i6) * ((long) i);
        int i13 = ((int) j3) & M25;
        long j4 = j3 >> 25;
        long j5 = ((long) i9) * ((long) i);
        int i14 = ((int) j5) & M25;
        long j6 = j5 >> 25;
        long j7 = ((long) i11) * ((long) i);
        int i15 = ((int) j7) & M25;
        long j8 = ((j7 >> 25) * 38) + (((long) i2) * ((long) i));
        iArr2[0] = ((int) j8) & M26;
        long j9 = j8 >> 26;
        long j10 = j4 + (((long) i7) * ((long) i));
        iArr2[5] = ((int) j10) & M26;
        long j11 = j10 >> 26;
        long j12 = j9 + (((long) i3) * ((long) i));
        iArr2[1] = ((int) j12) & M26;
        long j13 = j12 >> 26;
        long j14 = j2 + (((long) i5) * ((long) i));
        iArr2[3] = ((int) j14) & M26;
        long j15 = j14 >> 26;
        long j16 = j11 + (((long) i8) * ((long) i));
        iArr2[6] = ((int) j16) & M26;
        long j17 = j16 >> 26;
        long j18 = j6 + (((long) i10) * ((long) i));
        iArr2[8] = ((int) j18) & M26;
        iArr2[2] = i12 + ((int) j13);
        iArr2[4] = i13 + ((int) j15);
        iArr2[7] = i14 + ((int) j17);
        iArr2[9] = i15 + ((int) (j18 >> 26));
    }

    public static void mul(int[] iArr, int[] iArr2, int[] iArr3) {
        int i = iArr[0];
        int i2 = iArr2[0];
        int i3 = iArr[1];
        int i4 = iArr2[1];
        int i5 = iArr[2];
        int i6 = iArr2[2];
        int i7 = iArr[3];
        int i8 = iArr2[3];
        int i9 = iArr[4];
        int i10 = iArr2[4];
        int i11 = iArr[5];
        int i12 = iArr2[5];
        int i13 = iArr[6];
        int i14 = iArr2[6];
        int i15 = iArr[7];
        int i16 = iArr2[7];
        int i17 = iArr[8];
        int i18 = iArr2[8];
        int i19 = iArr[9];
        int i20 = iArr2[9];
        long j = ((long) i) * ((long) i2);
        long j2 = (((long) i) * ((long) i4)) + (((long) i3) * ((long) i2));
        long j3 = (((long) i) * ((long) i6)) + (((long) i3) * ((long) i4)) + (((long) i5) * ((long) i2));
        long j4 = (((((long) i3) * ((long) i6)) + (((long) i5) * ((long) i4))) << 1) + (((long) i) * ((long) i8)) + (((long) i7) * ((long) i2));
        long j5 = ((((long) i5) * ((long) i6)) << 1) + (((long) i) * ((long) i10)) + (((long) i3) * ((long) i8)) + (((long) i7) * ((long) i4)) + (((long) i9) * ((long) i2));
        long j6 = ((((((long) i3) * ((long) i10)) + (((long) i5) * ((long) i8))) + (((long) i7) * ((long) i6))) + (((long) i9) * ((long) i4))) << 1;
        long j7 = (((((long) i5) * ((long) i10)) + (((long) i9) * ((long) i6))) << 1) + (((long) i7) * ((long) i8));
        long j8 = (((long) i7) * ((long) i10)) + (((long) i9) * ((long) i8));
        long j9 = (((long) i9) * ((long) i10)) << 1;
        long j10 = ((long) i11) * ((long) i12);
        long j11 = (((long) i11) * ((long) i14)) + (((long) i13) * ((long) i12));
        long j12 = (((long) i11) * ((long) i16)) + (((long) i13) * ((long) i14)) + (((long) i15) * ((long) i12));
        long j13 = (((((long) i13) * ((long) i16)) + (((long) i15) * ((long) i14))) << 1) + (((long) i11) * ((long) i18)) + (((long) i17) * ((long) i12));
        long j14 = ((((long) i15) * ((long) i16)) << 1) + (((long) i11) * ((long) i20)) + (((long) i13) * ((long) i18)) + (((long) i17) * ((long) i14)) + (((long) i19) * ((long) i12));
        long j15 = (((long) i13) * ((long) i20)) + (((long) i15) * ((long) i18)) + (((long) i17) * ((long) i16)) + (((long) i19) * ((long) i14));
        long j16 = (((((long) i15) * ((long) i20)) + (((long) i19) * ((long) i16))) << 1) + (((long) i17) * ((long) i18));
        long j17 = (((long) i17) * ((long) i20)) + (((long) i19) * ((long) i18));
        long j18 = ((long) i19) * ((long) i20);
        long j19 = j - (j15 * 76);
        long j20 = j2 - (j16 * 38);
        long j21 = j3 - (j17 * 38);
        long j22 = j4 - (j18 * 76);
        long j23 = j6 - j10;
        long j24 = j7 - j11;
        long j25 = j8 - j12;
        long j26 = j9 - j13;
        int i21 = i + i11;
        int i22 = i2 + i12;
        int i23 = i3 + i13;
        int i24 = i4 + i14;
        int i25 = i5 + i15;
        int i26 = i6 + i16;
        int i27 = i7 + i17;
        int i28 = i8 + i18;
        int i29 = i9 + i19;
        int i30 = i10 + i20;
        long j27 = ((long) i21) * ((long) i22);
        long j28 = (((long) i21) * ((long) i24)) + (((long) i23) * ((long) i22));
        long j29 = (((long) i21) * ((long) i26)) + (((long) i23) * ((long) i24)) + (((long) i25) * ((long) i22));
        long j30 = (((((long) i23) * ((long) i26)) + (((long) i25) * ((long) i24))) << 1) + (((long) i21) * ((long) i28)) + (((long) i27) * ((long) i22));
        long j31 = ((((long) i25) * ((long) i26)) << 1) + (((long) i21) * ((long) i30)) + (((long) i23) * ((long) i28)) + (((long) i27) * ((long) i24)) + (((long) i29) * ((long) i22));
        long j32 = ((((((long) i23) * ((long) i30)) + (((long) i25) * ((long) i28))) + (((long) i27) * ((long) i26))) + (((long) i29) * ((long) i24))) << 1;
        long j33 = (((((long) i25) * ((long) i30)) + (((long) i29) * ((long) i26))) << 1) + (((long) i27) * ((long) i28));
        long j34 = (((long) i27) * ((long) i30)) + (((long) i29) * ((long) i28));
        long j35 = (((long) i29) * ((long) i30)) << 1;
        long j36 = j26 + (j30 - j22);
        int i31 = ((int) j36) & M26;
        long j37 = (j36 >> 26) + ((j31 - j5) - j14);
        int i32 = ((int) j37) & M25;
        long j38 = j19 + ((((j37 >> 25) + j32) - j23) * 38);
        iArr3[0] = ((int) j38) & M26;
        long j39 = (j38 >> 26) + j20 + ((j33 - j24) * 38);
        iArr3[1] = ((int) j39) & M26;
        long j40 = (j39 >> 26) + j21 + ((j34 - j25) * 38);
        iArr3[2] = ((int) j40) & M25;
        long j41 = (j40 >> 25) + j22 + ((j35 - j26) * 38);
        iArr3[3] = ((int) j41) & M26;
        long j42 = (j41 >> 26) + j5 + (j14 * 38);
        iArr3[4] = ((int) j42) & M25;
        long j43 = (j42 >> 25) + j23 + (j27 - j19);
        iArr3[5] = ((int) j43) & M26;
        long j44 = (j43 >> 26) + j24 + (j28 - j20);
        iArr3[6] = ((int) j44) & M26;
        long j45 = (j44 >> 26) + j25 + (j29 - j21);
        iArr3[7] = ((int) j45) & M25;
        long j46 = (j45 >> 25) + ((long) i31);
        iArr3[8] = ((int) j46) & M26;
        iArr3[9] = i32 + ((int) (j46 >> 26));
    }

    public static void negate(int[] iArr, int[] iArr2) {
        for (int i = 0; i < 10; i++) {
            iArr2[i] = -iArr[i];
        }
    }

    public static void normalize(int[] iArr) {
        int i = (iArr[9] >>> 23) & 1;
        reduce(iArr, i);
        reduce(iArr, -i);
    }

    public static void one(int[] iArr) {
        iArr[0] = 1;
        for (int i = 1; i < 10; i++) {
            iArr[i] = 0;
        }
    }

    private static void powPm5d8(int[] iArr, int[] iArr2, int[] iArr3) {
        sqr(iArr, iArr2);
        mul(iArr, iArr2, iArr2);
        int[] iArrCreate = create();
        sqr(iArr2, iArrCreate);
        mul(iArr, iArrCreate, iArrCreate);
        sqr(iArrCreate, 2, iArrCreate);
        mul(iArr2, iArrCreate, iArrCreate);
        int[] iArrCreate2 = create();
        sqr(iArrCreate, 5, iArrCreate2);
        mul(iArrCreate, iArrCreate2, iArrCreate2);
        int[] iArrCreate3 = create();
        sqr(iArrCreate2, 5, iArrCreate3);
        mul(iArrCreate, iArrCreate3, iArrCreate3);
        sqr(iArrCreate3, 10, iArrCreate);
        mul(iArrCreate2, iArrCreate, iArrCreate);
        sqr(iArrCreate, 25, iArrCreate2);
        mul(iArrCreate, iArrCreate2, iArrCreate2);
        sqr(iArrCreate2, 25, iArrCreate3);
        mul(iArrCreate, iArrCreate3, iArrCreate3);
        sqr(iArrCreate3, 50, iArrCreate);
        mul(iArrCreate2, iArrCreate, iArrCreate);
        sqr(iArrCreate, 125, iArrCreate2);
        mul(iArrCreate, iArrCreate2, iArrCreate2);
        sqr(iArrCreate2, 2, iArrCreate);
        mul(iArrCreate, iArr, iArr3);
    }

    private static void reduce(int[] iArr, int i) {
        int i2 = iArr[9];
        int i3 = i2 & M24;
        long j = ((long) (((i2 >> 24) + i) * 19)) + ((long) iArr[0]);
        iArr[0] = ((int) j) & M26;
        long j2 = (j >> 26) + ((long) iArr[1]);
        iArr[1] = ((int) j2) & M26;
        long j3 = (j2 >> 26) + ((long) iArr[2]);
        iArr[2] = ((int) j3) & M25;
        long j4 = (j3 >> 25) + ((long) iArr[3]);
        iArr[3] = ((int) j4) & M26;
        long j5 = (j4 >> 26) + ((long) iArr[4]);
        iArr[4] = ((int) j5) & M25;
        long j6 = (j5 >> 25) + ((long) iArr[5]);
        iArr[5] = ((int) j6) & M26;
        long j7 = (j6 >> 26) + ((long) iArr[6]);
        iArr[6] = ((int) j7) & M26;
        long j8 = (j7 >> 26) + ((long) iArr[7]);
        iArr[7] = ((int) j8) & M25;
        long j9 = (j8 >> 25) + ((long) iArr[8]);
        iArr[8] = ((int) j9) & M26;
        iArr[9] = i3 + ((int) (j9 >> 26));
    }

    public static void sqr(int[] iArr, int[] iArr2) {
        int i = iArr[0];
        int i2 = iArr[1];
        int i3 = iArr[2];
        int i4 = iArr[3];
        int i5 = iArr[4];
        int i6 = iArr[5];
        int i7 = iArr[6];
        int i8 = iArr[7];
        int i9 = iArr[8];
        int i10 = iArr[9];
        int i11 = i2 * 2;
        int i12 = i3 * 2;
        int i13 = i4 * 2;
        int i14 = i5 * 2;
        long j = ((long) i) * ((long) i);
        long j2 = ((long) i) * ((long) i11);
        long j3 = (((long) i) * ((long) i12)) + (((long) i2) * ((long) i2));
        long j4 = (((long) i11) * ((long) i12)) + (((long) i) * ((long) i13));
        long j5 = (((long) i3) * ((long) i12)) + (((long) i) * ((long) i14)) + (((long) i2) * ((long) i13));
        long j6 = (((long) i11) * ((long) i14)) + (((long) i12) * ((long) i13));
        long j7 = (((long) i12) * ((long) i14)) + (((long) i4) * ((long) i4));
        long j8 = ((long) i4) * ((long) i14);
        long j9 = ((long) i5) * ((long) i14);
        int i15 = i7 * 2;
        int i16 = i8 * 2;
        int i17 = i9 * 2;
        int i18 = i10 * 2;
        long j10 = ((long) i6) * ((long) i6);
        long j11 = ((long) i6) * ((long) i15);
        long j12 = (((long) i6) * ((long) i16)) + (((long) i7) * ((long) i7));
        long j13 = (((long) i15) * ((long) i16)) + (((long) i6) * ((long) i17));
        long j14 = (((long) i8) * ((long) i16)) + (((long) i6) * ((long) i18)) + (((long) i7) * ((long) i17));
        long j15 = (((long) i15) * ((long) i18)) + (((long) i16) * ((long) i17));
        long j16 = (((long) i16) * ((long) i18)) + (((long) i9) * ((long) i9));
        long j17 = ((long) i9) * ((long) i18);
        long j18 = ((long) i10) * ((long) i18);
        long j19 = j - (j15 * 38);
        long j20 = j2 - (j16 * 38);
        long j21 = j3 - (j17 * 38);
        long j22 = j4 - (j18 * 38);
        long j23 = j6 - j10;
        long j24 = j7 - j11;
        long j25 = j8 - j12;
        long j26 = j9 - j13;
        int i19 = i + i6;
        int i20 = i2 + i7;
        int i21 = i3 + i8;
        int i22 = i4 + i9;
        int i23 = i5 + i10;
        int i24 = i20 * 2;
        int i25 = i21 * 2;
        int i26 = i22 * 2;
        int i27 = i23 * 2;
        long j27 = ((long) i19) * ((long) i19);
        long j28 = ((long) i19) * ((long) i24);
        long j29 = (((long) i19) * ((long) i25)) + (((long) i20) * ((long) i20));
        long j30 = (((long) i24) * ((long) i25)) + (((long) i19) * ((long) i26));
        long j31 = (((long) i21) * ((long) i25)) + (((long) i19) * ((long) i27)) + (((long) i20) * ((long) i26));
        long j32 = (((long) i24) * ((long) i27)) + (((long) i25) * ((long) i26));
        long j33 = (((long) i25) * ((long) i27)) + (((long) i22) * ((long) i22));
        long j34 = ((long) i22) * ((long) i27);
        long j35 = ((long) i23) * ((long) i27);
        long j36 = j26 + (j30 - j22);
        int i28 = ((int) j36) & M26;
        long j37 = (j36 >> 26) + ((j31 - j5) - j14);
        int i29 = ((int) j37) & M25;
        long j38 = j19 + ((((j37 >> 25) + j32) - j23) * 38);
        iArr2[0] = ((int) j38) & M26;
        long j39 = (j38 >> 26) + j20 + ((j33 - j24) * 38);
        iArr2[1] = ((int) j39) & M26;
        long j40 = (j39 >> 26) + j21 + ((j34 - j25) * 38);
        iArr2[2] = ((int) j40) & M25;
        long j41 = (j40 >> 25) + j22 + ((j35 - j26) * 38);
        iArr2[3] = ((int) j41) & M26;
        long j42 = (j41 >> 26) + j5 + (j14 * 38);
        iArr2[4] = ((int) j42) & M25;
        long j43 = (j42 >> 25) + j23 + (j27 - j19);
        iArr2[5] = ((int) j43) & M26;
        long j44 = (j43 >> 26) + j24 + (j28 - j20);
        iArr2[6] = ((int) j44) & M26;
        long j45 = (j44 >> 26) + j25 + (j29 - j21);
        iArr2[7] = ((int) j45) & M25;
        long j46 = (j45 >> 25) + ((long) i28);
        iArr2[8] = ((int) j46) & M26;
        iArr2[9] = i29 + ((int) (j46 >> 26));
    }

    public static void sqr(int[] iArr, int i, int[] iArr2) {
        sqr(iArr, iArr2);
        while (true) {
            i--;
            if (i <= 0) {
                return;
            } else {
                sqr(iArr2, iArr2);
            }
        }
    }

    public static boolean sqrtRatioVar(int[] iArr, int[] iArr2, int[] iArr3) {
        int[] iArrCreate = create();
        int[] iArrCreate2 = create();
        mul(iArr, iArr2, iArrCreate);
        sqr(iArr2, iArrCreate2);
        mul(iArrCreate, iArrCreate2, iArrCreate);
        sqr(iArrCreate2, iArrCreate2);
        mul(iArrCreate2, iArrCreate, iArrCreate2);
        int[] iArrCreate3 = create();
        int[] iArrCreate4 = create();
        powPm5d8(iArrCreate2, iArrCreate3, iArrCreate4);
        mul(iArrCreate4, iArrCreate, iArrCreate4);
        int[] iArrCreate5 = create();
        sqr(iArrCreate4, iArrCreate5);
        mul(iArrCreate5, iArr2, iArrCreate5);
        sub(iArrCreate5, iArr, iArrCreate3);
        normalize(iArrCreate3);
        if (isZeroVar(iArrCreate3)) {
            copy(iArrCreate4, 0, iArr3, 0);
            return true;
        }
        add(iArrCreate5, iArr, iArrCreate3);
        normalize(iArrCreate3);
        if (!isZeroVar(iArrCreate3)) {
            return false;
        }
        mul(iArrCreate4, ROOT_NEG_ONE, iArr3);
        return true;
    }

    public static void sub(int[] iArr, int[] iArr2, int[] iArr3) {
        for (int i = 0; i < 10; i++) {
            iArr3[i] = iArr[i] - iArr2[i];
        }
    }

    public static void subOne(int[] iArr) {
        iArr[0] = iArr[0] - 1;
    }

    public static void zero(int[] iArr) {
        for (int i = 0; i < 10; i++) {
            iArr[i] = 0;
        }
    }
}
