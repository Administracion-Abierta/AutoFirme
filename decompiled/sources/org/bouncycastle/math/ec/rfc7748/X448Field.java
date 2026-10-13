package org.bouncycastle.math.ec.rfc7748;

import com.sun.jna.platform.win32.WinError;
import org.bouncycastle.math.raw.Mod;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/rfc7748/X448Field.class */
public abstract class X448Field {
    public static final int SIZE = 16;
    private static final int M28 = 268435455;
    private static final long U32 = 4294967295L;
    private static final int[] P32 = {-1, -1, -1, -1, -1, -1, -1, -2, -1, -1, -1, -1, -1, -1};

    protected X448Field() {
    }

    public static void add(int[] iArr, int[] iArr2, int[] iArr3) {
        for (int i = 0; i < 16; i++) {
            iArr3[i] = iArr[i] + iArr2[i];
        }
    }

    public static void addOne(int[] iArr) {
        iArr[0] = iArr[0] + 1;
    }

    public static void addOne(int[] iArr, int i) {
        iArr[i] = iArr[i] + 1;
    }

    public static int areEqual(int[] iArr, int[] iArr2) {
        int i = 0;
        for (int i2 = 0; i2 < 16; i2++) {
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
        int i11 = iArr[10];
        int i12 = iArr[11];
        int i13 = iArr[12];
        int i14 = iArr[13];
        int i15 = iArr[14];
        int i16 = iArr[15];
        int i17 = i2 + (i >>> 28);
        int i18 = i & M28;
        int i19 = i6 + (i5 >>> 28);
        int i20 = i5 & M28;
        int i21 = i10 + (i9 >>> 28);
        int i22 = i9 & M28;
        int i23 = i14 + (i13 >>> 28);
        int i24 = i13 & M28;
        int i25 = i3 + (i17 >>> 28);
        int i26 = i17 & M28;
        int i27 = i7 + (i19 >>> 28);
        int i28 = i19 & M28;
        int i29 = i11 + (i21 >>> 28);
        int i30 = i21 & M28;
        int i31 = i15 + (i23 >>> 28);
        int i32 = i23 & M28;
        int i33 = i4 + (i25 >>> 28);
        int i34 = i25 & M28;
        int i35 = i8 + (i27 >>> 28);
        int i36 = i27 & M28;
        int i37 = i12 + (i29 >>> 28);
        int i38 = i29 & M28;
        int i39 = i16 + (i31 >>> 28);
        int i40 = i31 & M28;
        int i41 = i39 >>> 28;
        int i42 = i39 & M28;
        int i43 = i18 + i41;
        int i44 = i22 + i41;
        int i45 = i20 + (i33 >>> 28);
        int i46 = i33 & M28;
        int i47 = i44 + (i35 >>> 28);
        int i48 = i35 & M28;
        int i49 = i24 + (i37 >>> 28);
        int i50 = i37 & M28;
        int i51 = i26 + (i43 >>> 28);
        int i52 = i43 & M28;
        int i53 = i28 + (i45 >>> 28);
        int i54 = i45 & M28;
        int i55 = i30 + (i47 >>> 28);
        int i56 = i47 & M28;
        int i57 = i32 + (i49 >>> 28);
        int i58 = i49 & M28;
        iArr[0] = i52;
        iArr[1] = i51;
        iArr[2] = i34;
        iArr[3] = i46;
        iArr[4] = i54;
        iArr[5] = i53;
        iArr[6] = i36;
        iArr[7] = i48;
        iArr[8] = i56;
        iArr[9] = i55;
        iArr[10] = i38;
        iArr[11] = i50;
        iArr[12] = i58;
        iArr[13] = i57;
        iArr[14] = i40;
        iArr[15] = i42;
    }

    public static void cmov(int i, int[] iArr, int i2, int[] iArr2, int i3) {
        for (int i4 = 0; i4 < 16; i4++) {
            int i5 = iArr2[i3 + i4];
            iArr2[i3 + i4] = i5 ^ ((i5 ^ iArr[i2 + i4]) & i);
        }
    }

    public static void cnegate(int i, int[] iArr) {
        int[] iArrCreate = create();
        sub(iArrCreate, iArr, iArrCreate);
        cmov(-i, iArrCreate, 0, iArr, 0);
    }

    public static void copy(int[] iArr, int i, int[] iArr2, int i2) {
        for (int i3 = 0; i3 < 16; i3++) {
            iArr2[i2 + i3] = iArr[i + i3];
        }
    }

    public static int[] create() {
        return new int[16];
    }

    public static int[] createTable(int i) {
        return new int[16 * i];
    }

    public static void cswap(int i, int[] iArr, int[] iArr2) {
        int i2 = 0 - i;
        for (int i3 = 0; i3 < 16; i3++) {
            int i4 = iArr[i3];
            int i5 = iArr2[i3];
            int i6 = i2 & (i4 ^ i5);
            iArr[i3] = i4 ^ i6;
            iArr2[i3] = i5 ^ i6;
        }
    }

    public static void decode(int[] iArr, int i, int[] iArr2) {
        decode224(iArr, i, iArr2, 0);
        decode224(iArr, i + 7, iArr2, 8);
    }

    public static void decode(byte[] bArr, int[] iArr) {
        decode56(bArr, 0, iArr, 0);
        decode56(bArr, 7, iArr, 2);
        decode56(bArr, 14, iArr, 4);
        decode56(bArr, 21, iArr, 6);
        decode56(bArr, 28, iArr, 8);
        decode56(bArr, 35, iArr, 10);
        decode56(bArr, 42, iArr, 12);
        decode56(bArr, 49, iArr, 14);
    }

    public static void decode(byte[] bArr, int i, int[] iArr) {
        decode56(bArr, i, iArr, 0);
        decode56(bArr, i + 7, iArr, 2);
        decode56(bArr, i + 14, iArr, 4);
        decode56(bArr, i + 21, iArr, 6);
        decode56(bArr, i + 28, iArr, 8);
        decode56(bArr, i + 35, iArr, 10);
        decode56(bArr, i + 42, iArr, 12);
        decode56(bArr, i + 49, iArr, 14);
    }

    public static void decode(byte[] bArr, int i, int[] iArr, int i2) {
        decode56(bArr, i, iArr, i2);
        decode56(bArr, i + 7, iArr, i2 + 2);
        decode56(bArr, i + 14, iArr, i2 + 4);
        decode56(bArr, i + 21, iArr, i2 + 6);
        decode56(bArr, i + 28, iArr, i2 + 8);
        decode56(bArr, i + 35, iArr, i2 + 10);
        decode56(bArr, i + 42, iArr, i2 + 12);
        decode56(bArr, i + 49, iArr, i2 + 14);
    }

    private static void decode224(int[] iArr, int i, int[] iArr2, int i2) {
        int i3 = iArr[i + 0];
        int i4 = iArr[i + 1];
        int i5 = iArr[i + 2];
        int i6 = iArr[i + 3];
        int i7 = iArr[i + 4];
        int i8 = iArr[i + 5];
        int i9 = iArr[i + 6];
        iArr2[i2 + 0] = i3 & M28;
        iArr2[i2 + 1] = ((i3 >>> 28) | (i4 << 4)) & M28;
        iArr2[i2 + 2] = ((i4 >>> 24) | (i5 << 8)) & M28;
        iArr2[i2 + 3] = ((i5 >>> 20) | (i6 << 12)) & M28;
        iArr2[i2 + 4] = ((i6 >>> 16) | (i7 << 16)) & M28;
        iArr2[i2 + 5] = ((i7 >>> 12) | (i8 << 20)) & M28;
        iArr2[i2 + 6] = ((i8 >>> 8) | (i9 << 24)) & M28;
        iArr2[i2 + 7] = i9 >>> 4;
    }

    private static int decode24(byte[] bArr, int i) {
        int i2 = bArr[i] & 255;
        int i3 = i + 1;
        return i2 | ((bArr[i3] & 255) << 8) | ((bArr[i3 + 1] & 255) << 16);
    }

    private static int decode32(byte[] bArr, int i) {
        int i2 = bArr[i] & 255;
        int i3 = i + 1;
        int i4 = i2 | ((bArr[i3] & 255) << 8);
        int i5 = i3 + 1;
        return i4 | ((bArr[i5] & 255) << 16) | (bArr[i5 + 1] << 24);
    }

    private static void decode56(byte[] bArr, int i, int[] iArr, int i2) {
        int iDecode32 = decode32(bArr, i);
        int iDecode24 = decode24(bArr, i + 4);
        iArr[i2] = iDecode32 & M28;
        iArr[i2 + 1] = (iDecode32 >>> 28) | (iDecode24 << 4);
    }

    public static void encode(int[] iArr, int[] iArr2, int i) {
        encode224(iArr, 0, iArr2, i);
        encode224(iArr, 8, iArr2, i + 7);
    }

    public static void encode(int[] iArr, byte[] bArr) {
        encode56(iArr, 0, bArr, 0);
        encode56(iArr, 2, bArr, 7);
        encode56(iArr, 4, bArr, 14);
        encode56(iArr, 6, bArr, 21);
        encode56(iArr, 8, bArr, 28);
        encode56(iArr, 10, bArr, 35);
        encode56(iArr, 12, bArr, 42);
        encode56(iArr, 14, bArr, 49);
    }

    public static void encode(int[] iArr, byte[] bArr, int i) {
        encode56(iArr, 0, bArr, i);
        encode56(iArr, 2, bArr, i + 7);
        encode56(iArr, 4, bArr, i + 14);
        encode56(iArr, 6, bArr, i + 21);
        encode56(iArr, 8, bArr, i + 28);
        encode56(iArr, 10, bArr, i + 35);
        encode56(iArr, 12, bArr, i + 42);
        encode56(iArr, 14, bArr, i + 49);
    }

    public static void encode(int[] iArr, int i, byte[] bArr, int i2) {
        encode56(iArr, i, bArr, i2);
        encode56(iArr, i + 2, bArr, i2 + 7);
        encode56(iArr, i + 4, bArr, i2 + 14);
        encode56(iArr, i + 6, bArr, i2 + 21);
        encode56(iArr, i + 8, bArr, i2 + 28);
        encode56(iArr, i + 10, bArr, i2 + 35);
        encode56(iArr, i + 12, bArr, i2 + 42);
        encode56(iArr, i + 14, bArr, i2 + 49);
    }

    private static void encode224(int[] iArr, int i, int[] iArr2, int i2) {
        int i3 = iArr[i + 0];
        int i4 = iArr[i + 1];
        int i5 = iArr[i + 2];
        int i6 = iArr[i + 3];
        int i7 = iArr[i + 4];
        int i8 = iArr[i + 5];
        int i9 = iArr[i + 6];
        int i10 = iArr[i + 7];
        iArr2[i2 + 0] = i3 | (i4 << 28);
        iArr2[i2 + 1] = (i4 >>> 4) | (i5 << 24);
        iArr2[i2 + 2] = (i5 >>> 8) | (i6 << 20);
        iArr2[i2 + 3] = (i6 >>> 12) | (i7 << 16);
        iArr2[i2 + 4] = (i7 >>> 16) | (i8 << 12);
        iArr2[i2 + 5] = (i8 >>> 20) | (i9 << 8);
        iArr2[i2 + 6] = (i9 >>> 24) | (i10 << 4);
    }

    private static void encode24(int i, byte[] bArr, int i2) {
        bArr[i2] = (byte) i;
        int i3 = i2 + 1;
        bArr[i3] = (byte) (i >>> 8);
        bArr[i3 + 1] = (byte) (i >>> 16);
    }

    private static void encode32(int i, byte[] bArr, int i2) {
        bArr[i2] = (byte) i;
        int i3 = i2 + 1;
        bArr[i3] = (byte) (i >>> 8);
        int i4 = i3 + 1;
        bArr[i4] = (byte) (i >>> 16);
        bArr[i4 + 1] = (byte) (i >>> 24);
    }

    private static void encode56(int[] iArr, int i, byte[] bArr, int i2) {
        int i3 = iArr[i];
        int i4 = iArr[i + 1];
        encode32(i3 | (i4 << 28), bArr, i2);
        encode24(i4 >>> 4, bArr, i2 + 4);
    }

    public static void inv(int[] iArr, int[] iArr2) {
        int[] iArrCreate = create();
        int[] iArr3 = new int[14];
        copy(iArr, 0, iArrCreate, 0);
        normalize(iArrCreate);
        encode(iArrCreate, iArr3, 0);
        Mod.modOddInverse(P32, iArr3, iArr3);
        decode(iArr3, 0, iArr2);
    }

    public static void invVar(int[] iArr, int[] iArr2) {
        int[] iArrCreate = create();
        int[] iArr3 = new int[14];
        copy(iArr, 0, iArrCreate, 0);
        normalize(iArrCreate);
        encode(iArrCreate, iArr3, 0);
        Mod.modOddInverseVar(P32, iArr3, iArr3);
        decode(iArr3, 0, iArr2);
    }

    public static int isOne(int[] iArr) {
        int i = iArr[0] ^ 1;
        for (int i2 = 1; i2 < 16; i2++) {
            i |= iArr[i2];
        }
        return (((i >>> 1) | (i & 1)) - 1) >> 31;
    }

    public static boolean isOneVar(int[] iArr) {
        return 0 != isOne(iArr);
    }

    public static int isZero(int[] iArr) {
        int i = 0;
        for (int i2 = 0; i2 < 16; i2++) {
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
        int i12 = iArr[10];
        int i13 = iArr[11];
        int i14 = iArr[12];
        int i15 = iArr[13];
        int i16 = iArr[14];
        int i17 = iArr[15];
        long j = ((long) i3) * ((long) i);
        int i18 = ((int) j) & M28;
        long j2 = j >>> 28;
        long j3 = ((long) i7) * ((long) i);
        int i19 = ((int) j3) & M28;
        long j4 = j3 >>> 28;
        long j5 = ((long) i11) * ((long) i);
        int i20 = ((int) j5) & M28;
        long j6 = j5 >>> 28;
        long j7 = ((long) i15) * ((long) i);
        int i21 = ((int) j7) & M28;
        long j8 = j7 >>> 28;
        long j9 = j2 + (((long) i4) * ((long) i));
        iArr2[2] = ((int) j9) & M28;
        long j10 = j9 >>> 28;
        long j11 = j4 + (((long) i8) * ((long) i));
        iArr2[6] = ((int) j11) & M28;
        long j12 = j11 >>> 28;
        long j13 = j6 + (((long) i12) * ((long) i));
        iArr2[10] = ((int) j13) & M28;
        long j14 = j13 >>> 28;
        long j15 = j8 + (((long) i16) * ((long) i));
        iArr2[14] = ((int) j15) & M28;
        long j16 = j15 >>> 28;
        long j17 = j10 + (((long) i5) * ((long) i));
        iArr2[3] = ((int) j17) & M28;
        long j18 = j17 >>> 28;
        long j19 = j12 + (((long) i9) * ((long) i));
        iArr2[7] = ((int) j19) & M28;
        long j20 = j19 >>> 28;
        long j21 = j14 + (((long) i13) * ((long) i));
        iArr2[11] = ((int) j21) & M28;
        long j22 = j21 >>> 28;
        long j23 = j16 + (((long) i17) * ((long) i));
        iArr2[15] = ((int) j23) & M28;
        long j24 = j23 >>> 28;
        long j25 = j20 + j24;
        long j26 = j18 + (((long) i6) * ((long) i));
        iArr2[4] = ((int) j26) & M28;
        long j27 = j26 >>> 28;
        long j28 = j25 + (((long) i10) * ((long) i));
        iArr2[8] = ((int) j28) & M28;
        long j29 = j28 >>> 28;
        long j30 = j22 + (((long) i14) * ((long) i));
        iArr2[12] = ((int) j30) & M28;
        long j31 = j30 >>> 28;
        long j32 = j24 + (((long) i2) * ((long) i));
        iArr2[0] = ((int) j32) & M28;
        iArr2[1] = i18 + ((int) (j32 >>> 28));
        iArr2[5] = i19 + ((int) j27);
        iArr2[9] = i20 + ((int) j29);
        iArr2[13] = i21 + ((int) j31);
    }

    public static void mul(int[] iArr, int[] iArr2, int[] iArr3) {
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
        int i11 = iArr[10];
        int i12 = iArr[11];
        int i13 = iArr[12];
        int i14 = iArr[13];
        int i15 = iArr[14];
        int i16 = iArr[15];
        int i17 = iArr2[0];
        int i18 = iArr2[1];
        int i19 = iArr2[2];
        int i20 = iArr2[3];
        int i21 = iArr2[4];
        int i22 = iArr2[5];
        int i23 = iArr2[6];
        int i24 = iArr2[7];
        int i25 = iArr2[8];
        int i26 = iArr2[9];
        int i27 = iArr2[10];
        int i28 = iArr2[11];
        int i29 = iArr2[12];
        int i30 = iArr2[13];
        int i31 = iArr2[14];
        int i32 = iArr2[15];
        int i33 = i + i9;
        int i34 = i2 + i10;
        int i35 = i3 + i11;
        int i36 = i4 + i12;
        int i37 = i5 + i13;
        int i38 = i6 + i14;
        int i39 = i7 + i15;
        int i40 = i8 + i16;
        int i41 = i17 + i25;
        int i42 = i18 + i26;
        int i43 = i19 + i27;
        int i44 = i20 + i28;
        int i45 = i21 + i29;
        int i46 = i22 + i30;
        int i47 = i23 + i31;
        int i48 = i24 + i32;
        long j = ((long) i) * ((long) i17);
        long j2 = (((long) i8) * ((long) i18)) + (((long) i7) * ((long) i19)) + (((long) i6) * ((long) i20)) + (((long) i5) * ((long) i21)) + (((long) i4) * ((long) i22)) + (((long) i3) * ((long) i23)) + (((long) i2) * ((long) i24));
        long j3 = ((long) i9) * ((long) i25);
        long j4 = (((long) i16) * ((long) i26)) + (((long) i15) * ((long) i27)) + (((long) i14) * ((long) i28)) + (((long) i13) * ((long) i29)) + (((long) i12) * ((long) i30)) + (((long) i11) * ((long) i31)) + (((long) i10) * ((long) i32));
        long j5 = ((long) i33) * ((long) i41);
        long j6 = (((long) i40) * ((long) i42)) + (((long) i39) * ((long) i43)) + (((long) i38) * ((long) i44)) + (((long) i37) * ((long) i45)) + (((long) i36) * ((long) i46)) + (((long) i35) * ((long) i47)) + (((long) i34) * ((long) i48));
        long j7 = ((j + j3) + j6) - j2;
        int i49 = ((int) j7) & M28;
        long j8 = j7 >>> 28;
        long j9 = ((j4 + j5) - j) + j6;
        int i50 = ((int) j9) & M28;
        long j10 = j9 >>> 28;
        long j11 = (((long) i2) * ((long) i17)) + (((long) i) * ((long) i18));
        long j12 = (((long) i8) * ((long) i19)) + (((long) i7) * ((long) i20)) + (((long) i6) * ((long) i21)) + (((long) i5) * ((long) i22)) + (((long) i4) * ((long) i23)) + (((long) i3) * ((long) i24));
        long j13 = (((long) i10) * ((long) i25)) + (((long) i9) * ((long) i26));
        long j14 = (((long) i16) * ((long) i27)) + (((long) i15) * ((long) i28)) + (((long) i14) * ((long) i29)) + (((long) i13) * ((long) i30)) + (((long) i12) * ((long) i31)) + (((long) i11) * ((long) i32));
        long j15 = (((long) i34) * ((long) i41)) + (((long) i33) * ((long) i42));
        long j16 = (((long) i40) * ((long) i43)) + (((long) i39) * ((long) i44)) + (((long) i38) * ((long) i45)) + (((long) i37) * ((long) i46)) + (((long) i36) * ((long) i47)) + (((long) i35) * ((long) i48));
        long j17 = j8 + (((j11 + j13) + j16) - j12);
        int i51 = ((int) j17) & M28;
        long j18 = j17 >>> 28;
        long j19 = j10 + ((j14 + j15) - j11) + j16;
        int i52 = ((int) j19) & M28;
        long j20 = j19 >>> 28;
        long j21 = (((long) i3) * ((long) i17)) + (((long) i2) * ((long) i18)) + (((long) i) * ((long) i19));
        long j22 = (((long) i8) * ((long) i20)) + (((long) i7) * ((long) i21)) + (((long) i6) * ((long) i22)) + (((long) i5) * ((long) i23)) + (((long) i4) * ((long) i24));
        long j23 = (((long) i11) * ((long) i25)) + (((long) i10) * ((long) i26)) + (((long) i9) * ((long) i27));
        long j24 = (((long) i16) * ((long) i28)) + (((long) i15) * ((long) i29)) + (((long) i14) * ((long) i30)) + (((long) i13) * ((long) i31)) + (((long) i12) * ((long) i32));
        long j25 = (((long) i35) * ((long) i41)) + (((long) i34) * ((long) i42)) + (((long) i33) * ((long) i43));
        long j26 = (((long) i40) * ((long) i44)) + (((long) i39) * ((long) i45)) + (((long) i38) * ((long) i46)) + (((long) i37) * ((long) i47)) + (((long) i36) * ((long) i48));
        long j27 = j18 + (((j21 + j23) + j26) - j22);
        int i53 = ((int) j27) & M28;
        long j28 = j27 >>> 28;
        long j29 = j20 + ((j24 + j25) - j21) + j26;
        int i54 = ((int) j29) & M28;
        long j30 = j29 >>> 28;
        long j31 = (((long) i4) * ((long) i17)) + (((long) i3) * ((long) i18)) + (((long) i2) * ((long) i19)) + (((long) i) * ((long) i20));
        long j32 = (((long) i8) * ((long) i21)) + (((long) i7) * ((long) i22)) + (((long) i6) * ((long) i23)) + (((long) i5) * ((long) i24));
        long j33 = (((long) i12) * ((long) i25)) + (((long) i11) * ((long) i26)) + (((long) i10) * ((long) i27)) + (((long) i9) * ((long) i28));
        long j34 = (((long) i16) * ((long) i29)) + (((long) i15) * ((long) i30)) + (((long) i14) * ((long) i31)) + (((long) i13) * ((long) i32));
        long j35 = (((long) i36) * ((long) i41)) + (((long) i35) * ((long) i42)) + (((long) i34) * ((long) i43)) + (((long) i33) * ((long) i44));
        long j36 = (((long) i40) * ((long) i45)) + (((long) i39) * ((long) i46)) + (((long) i38) * ((long) i47)) + (((long) i37) * ((long) i48));
        long j37 = j28 + (((j31 + j33) + j36) - j32);
        int i55 = ((int) j37) & M28;
        long j38 = j37 >>> 28;
        long j39 = j30 + ((j34 + j35) - j31) + j36;
        int i56 = ((int) j39) & M28;
        long j40 = j39 >>> 28;
        long j41 = (((long) i5) * ((long) i17)) + (((long) i4) * ((long) i18)) + (((long) i3) * ((long) i19)) + (((long) i2) * ((long) i20)) + (((long) i) * ((long) i21));
        long j42 = (((long) i8) * ((long) i22)) + (((long) i7) * ((long) i23)) + (((long) i6) * ((long) i24));
        long j43 = (((long) i13) * ((long) i25)) + (((long) i12) * ((long) i26)) + (((long) i11) * ((long) i27)) + (((long) i10) * ((long) i28)) + (((long) i9) * ((long) i29));
        long j44 = (((long) i16) * ((long) i30)) + (((long) i15) * ((long) i31)) + (((long) i14) * ((long) i32));
        long j45 = (((long) i37) * ((long) i41)) + (((long) i36) * ((long) i42)) + (((long) i35) * ((long) i43)) + (((long) i34) * ((long) i44)) + (((long) i33) * ((long) i45));
        long j46 = (((long) i40) * ((long) i46)) + (((long) i39) * ((long) i47)) + (((long) i38) * ((long) i48));
        long j47 = j38 + (((j41 + j43) + j46) - j42);
        int i57 = ((int) j47) & M28;
        long j48 = j47 >>> 28;
        long j49 = j40 + ((j44 + j45) - j41) + j46;
        int i58 = ((int) j49) & M28;
        long j50 = j49 >>> 28;
        long j51 = (((long) i6) * ((long) i17)) + (((long) i5) * ((long) i18)) + (((long) i4) * ((long) i19)) + (((long) i3) * ((long) i20)) + (((long) i2) * ((long) i21)) + (((long) i) * ((long) i22));
        long j52 = (((long) i8) * ((long) i23)) + (((long) i7) * ((long) i24));
        long j53 = (((long) i14) * ((long) i25)) + (((long) i13) * ((long) i26)) + (((long) i12) * ((long) i27)) + (((long) i11) * ((long) i28)) + (((long) i10) * ((long) i29)) + (((long) i9) * ((long) i30));
        long j54 = (((long) i16) * ((long) i31)) + (((long) i15) * ((long) i32));
        long j55 = (((long) i38) * ((long) i41)) + (((long) i37) * ((long) i42)) + (((long) i36) * ((long) i43)) + (((long) i35) * ((long) i44)) + (((long) i34) * ((long) i45)) + (((long) i33) * ((long) i46));
        long j56 = (((long) i40) * ((long) i47)) + (((long) i39) * ((long) i48));
        long j57 = j48 + (((j51 + j53) + j56) - j52);
        int i59 = ((int) j57) & M28;
        long j58 = j57 >>> 28;
        long j59 = j50 + ((j54 + j55) - j51) + j56;
        int i60 = ((int) j59) & M28;
        long j60 = j59 >>> 28;
        long j61 = (((long) i7) * ((long) i17)) + (((long) i6) * ((long) i18)) + (((long) i5) * ((long) i19)) + (((long) i4) * ((long) i20)) + (((long) i3) * ((long) i21)) + (((long) i2) * ((long) i22)) + (((long) i) * ((long) i23));
        long j62 = ((long) i8) * ((long) i24);
        long j63 = (((long) i15) * ((long) i25)) + (((long) i14) * ((long) i26)) + (((long) i13) * ((long) i27)) + (((long) i12) * ((long) i28)) + (((long) i11) * ((long) i29)) + (((long) i10) * ((long) i30)) + (((long) i9) * ((long) i31));
        long j64 = ((long) i16) * ((long) i32);
        long j65 = (((long) i39) * ((long) i41)) + (((long) i38) * ((long) i42)) + (((long) i37) * ((long) i43)) + (((long) i36) * ((long) i44)) + (((long) i35) * ((long) i45)) + (((long) i34) * ((long) i46)) + (((long) i33) * ((long) i47));
        long j66 = ((long) i40) * ((long) i48);
        long j67 = j58 + (((j61 + j63) + j66) - j62);
        int i61 = ((int) j67) & M28;
        long j68 = j67 >>> 28;
        long j69 = j60 + ((j64 + j65) - j61) + j66;
        int i62 = ((int) j69) & M28;
        long j70 = j69 >>> 28;
        long j71 = (((long) i8) * ((long) i17)) + (((long) i7) * ((long) i18)) + (((long) i6) * ((long) i19)) + (((long) i5) * ((long) i20)) + (((long) i4) * ((long) i21)) + (((long) i3) * ((long) i22)) + (((long) i2) * ((long) i23)) + (((long) i) * ((long) i24));
        long j72 = (((long) i16) * ((long) i25)) + (((long) i15) * ((long) i26)) + (((long) i14) * ((long) i27)) + (((long) i13) * ((long) i28)) + (((long) i12) * ((long) i29)) + (((long) i11) * ((long) i30)) + (((long) i10) * ((long) i31)) + (((long) i9) * ((long) i32));
        long j73 = (((long) i40) * ((long) i41)) + (((long) i39) * ((long) i42)) + (((long) i38) * ((long) i43)) + (((long) i37) * ((long) i44)) + (((long) i36) * ((long) i45)) + (((long) i35) * ((long) i46)) + (((long) i34) * ((long) i47)) + (((long) i33) * ((long) i48));
        long j74 = j68 + j71 + j72;
        int i63 = ((int) j74) & M28;
        long j75 = j74 >>> 28;
        long j76 = j70 + (j73 - j71);
        int i64 = ((int) j76) & M28;
        long j77 = j76 >>> 28;
        long j78 = j75 + j77 + ((long) i50);
        int i65 = ((int) j78) & M28;
        long j79 = j78 >>> 28;
        long j80 = j77 + ((long) i49);
        int i66 = ((int) j80) & M28;
        long j81 = j80 >>> 28;
        iArr3[0] = i66;
        iArr3[1] = i51 + ((int) j81);
        iArr3[2] = i53;
        iArr3[3] = i55;
        iArr3[4] = i57;
        iArr3[5] = i59;
        iArr3[6] = i61;
        iArr3[7] = i63;
        iArr3[8] = i65;
        iArr3[9] = i52 + ((int) j79);
        iArr3[10] = i54;
        iArr3[11] = i56;
        iArr3[12] = i58;
        iArr3[13] = i60;
        iArr3[14] = i62;
        iArr3[15] = i64;
    }

    public static void negate(int[] iArr, int[] iArr2) {
        sub(create(), iArr, iArr2);
    }

    public static void normalize(int[] iArr) {
        reduce(iArr, 1);
        reduce(iArr, -1);
    }

    public static void one(int[] iArr) {
        iArr[0] = 1;
        for (int i = 1; i < 16; i++) {
            iArr[i] = 0;
        }
    }

    private static void powPm3d4(int[] iArr, int[] iArr2) {
        int[] iArrCreate = create();
        sqr(iArr, iArrCreate);
        mul(iArr, iArrCreate, iArrCreate);
        int[] iArrCreate2 = create();
        sqr(iArrCreate, iArrCreate2);
        mul(iArr, iArrCreate2, iArrCreate2);
        int[] iArrCreate3 = create();
        sqr(iArrCreate2, 3, iArrCreate3);
        mul(iArrCreate2, iArrCreate3, iArrCreate3);
        int[] iArrCreate4 = create();
        sqr(iArrCreate3, 3, iArrCreate4);
        mul(iArrCreate2, iArrCreate4, iArrCreate4);
        int[] iArrCreate5 = create();
        sqr(iArrCreate4, 9, iArrCreate5);
        mul(iArrCreate4, iArrCreate5, iArrCreate5);
        int[] iArrCreate6 = create();
        sqr(iArrCreate5, iArrCreate6);
        mul(iArr, iArrCreate6, iArrCreate6);
        int[] iArrCreate7 = create();
        sqr(iArrCreate6, 18, iArrCreate7);
        mul(iArrCreate5, iArrCreate7, iArrCreate7);
        int[] iArrCreate8 = create();
        sqr(iArrCreate7, 37, iArrCreate8);
        mul(iArrCreate7, iArrCreate8, iArrCreate8);
        int[] iArrCreate9 = create();
        sqr(iArrCreate8, 37, iArrCreate9);
        mul(iArrCreate7, iArrCreate9, iArrCreate9);
        int[] iArrCreate10 = create();
        sqr(iArrCreate9, 111, iArrCreate10);
        mul(iArrCreate9, iArrCreate10, iArrCreate10);
        int[] iArrCreate11 = create();
        sqr(iArrCreate10, iArrCreate11);
        mul(iArr, iArrCreate11, iArrCreate11);
        int[] iArrCreate12 = create();
        sqr(iArrCreate11, WinError.ERROR_FILE_TOO_LARGE, iArrCreate12);
        mul(iArrCreate12, iArrCreate10, iArr2);
    }

    private static void reduce(int[] iArr, int i) {
        int i2 = iArr[15];
        int i3 = i2 & M28;
        int i4 = (i2 >>> 28) + i;
        long j = i4;
        for (int i5 = 0; i5 < 8; i5++) {
            long j2 = j + (((long) iArr[i5]) & 4294967295L);
            iArr[i5] = ((int) j2) & M28;
            j = j2 >> 28;
        }
        long j3 = j + ((long) i4);
        for (int i6 = 8; i6 < 15; i6++) {
            long j4 = j3 + (((long) iArr[i6]) & 4294967295L);
            iArr[i6] = ((int) j4) & M28;
            j3 = j4 >> 28;
        }
        iArr[15] = i3 + ((int) j3);
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
        int i11 = iArr[10];
        int i12 = iArr[11];
        int i13 = iArr[12];
        int i14 = iArr[13];
        int i15 = iArr[14];
        int i16 = iArr[15];
        int i17 = i * 2;
        int i18 = i2 * 2;
        int i19 = i3 * 2;
        int i20 = i4 * 2;
        int i21 = i5 * 2;
        int i22 = i6 * 2;
        int i23 = i7 * 2;
        int i24 = i9 * 2;
        int i25 = i10 * 2;
        int i26 = i11 * 2;
        int i27 = i12 * 2;
        int i28 = i13 * 2;
        int i29 = i14 * 2;
        int i30 = i15 * 2;
        int i31 = i + i9;
        int i32 = i2 + i10;
        int i33 = i3 + i11;
        int i34 = i4 + i12;
        int i35 = i5 + i13;
        int i36 = i6 + i14;
        int i37 = i7 + i15;
        int i38 = i8 + i16;
        int i39 = i31 * 2;
        int i40 = i32 * 2;
        int i41 = i33 * 2;
        int i42 = i34 * 2;
        int i43 = i35 * 2;
        int i44 = i36 * 2;
        int i45 = i37 * 2;
        long j = ((long) i) * ((long) i);
        long j2 = (((long) i8) * ((long) i18)) + (((long) i7) * ((long) i19)) + (((long) i6) * ((long) i20)) + (((long) i5) * ((long) i5));
        long j3 = ((long) i9) * ((long) i9);
        long j4 = (((long) i16) * ((long) i25)) + (((long) i15) * ((long) i26)) + (((long) i14) * ((long) i27)) + (((long) i13) * ((long) i13));
        long j5 = ((long) i31) * ((long) i31);
        long j6 = (((long) i38) * (((long) i40) & 4294967295L)) + (((long) i37) * (((long) i41) & 4294967295L)) + (((long) i36) * (((long) i42) & 4294967295L)) + (((long) i35) * ((long) i35));
        long j7 = ((j + j3) + j6) - j2;
        int i46 = ((int) j7) & M28;
        long j8 = j7 >>> 28;
        long j9 = ((j4 + j5) - j) + j6;
        int i47 = ((int) j9) & M28;
        long j10 = j9 >>> 28;
        long j11 = ((long) i2) * ((long) i17);
        long j12 = (((long) i8) * ((long) i19)) + (((long) i7) * ((long) i20)) + (((long) i6) * ((long) i21));
        long j13 = ((long) i10) * ((long) i24);
        long j14 = (((long) i16) * ((long) i26)) + (((long) i15) * ((long) i27)) + (((long) i14) * ((long) i28));
        long j15 = ((long) i32) * (((long) i39) & 4294967295L);
        long j16 = (((long) i38) * (((long) i41) & 4294967295L)) + (((long) i37) * (((long) i42) & 4294967295L)) + (((long) i36) * (((long) i43) & 4294967295L));
        long j17 = j8 + (((j11 + j13) + j16) - j12);
        int i48 = ((int) j17) & M28;
        long j18 = j17 >>> 28;
        long j19 = j10 + ((j14 + j15) - j11) + j16;
        int i49 = ((int) j19) & M28;
        long j20 = j19 >>> 28;
        long j21 = (((long) i3) * ((long) i17)) + (((long) i2) * ((long) i2));
        long j22 = (((long) i8) * ((long) i20)) + (((long) i7) * ((long) i21)) + (((long) i6) * ((long) i6));
        long j23 = (((long) i11) * ((long) i24)) + (((long) i10) * ((long) i10));
        long j24 = (((long) i16) * ((long) i27)) + (((long) i15) * ((long) i28)) + (((long) i14) * ((long) i14));
        long j25 = (((long) i33) * (((long) i39) & 4294967295L)) + (((long) i32) * ((long) i32));
        long j26 = (((long) i38) * (((long) i42) & 4294967295L)) + (((long) i37) * (((long) i43) & 4294967295L)) + (((long) i36) * ((long) i36));
        long j27 = j18 + (((j21 + j23) + j26) - j22);
        int i50 = ((int) j27) & M28;
        long j28 = j27 >>> 28;
        long j29 = j20 + ((j24 + j25) - j21) + j26;
        int i51 = ((int) j29) & M28;
        long j30 = j29 >>> 28;
        long j31 = (((long) i4) * ((long) i17)) + (((long) i3) * ((long) i18));
        long j32 = (((long) i8) * ((long) i21)) + (((long) i7) * ((long) i22));
        long j33 = (((long) i12) * ((long) i24)) + (((long) i11) * ((long) i25));
        long j34 = (((long) i16) * ((long) i28)) + (((long) i15) * ((long) i29));
        long j35 = (((long) i34) * (((long) i39) & 4294967295L)) + (((long) i33) * (((long) i40) & 4294967295L));
        long j36 = (((long) i38) * (((long) i43) & 4294967295L)) + (((long) i37) * (((long) i44) & 4294967295L));
        long j37 = j28 + (((j31 + j33) + j36) - j32);
        int i52 = ((int) j37) & M28;
        long j38 = j37 >>> 28;
        long j39 = j30 + ((j34 + j35) - j31) + j36;
        int i53 = ((int) j39) & M28;
        long j40 = j39 >>> 28;
        long j41 = (((long) i5) * ((long) i17)) + (((long) i4) * ((long) i18)) + (((long) i3) * ((long) i3));
        long j42 = (((long) i8) * ((long) i22)) + (((long) i7) * ((long) i7));
        long j43 = (((long) i13) * ((long) i24)) + (((long) i12) * ((long) i25)) + (((long) i11) * ((long) i11));
        long j44 = (((long) i16) * ((long) i29)) + (((long) i15) * ((long) i15));
        long j45 = (((long) i35) * (((long) i39) & 4294967295L)) + (((long) i34) * (((long) i40) & 4294967295L)) + (((long) i33) * ((long) i33));
        long j46 = (((long) i38) * (((long) i44) & 4294967295L)) + (((long) i37) * ((long) i37));
        long j47 = j38 + (((j41 + j43) + j46) - j42);
        int i54 = ((int) j47) & M28;
        long j48 = j47 >>> 28;
        long j49 = j40 + ((j44 + j45) - j41) + j46;
        int i55 = ((int) j49) & M28;
        long j50 = j49 >>> 28;
        long j51 = (((long) i6) * ((long) i17)) + (((long) i5) * ((long) i18)) + (((long) i4) * ((long) i19));
        long j52 = ((long) i8) * ((long) i23);
        long j53 = (((long) i14) * ((long) i24)) + (((long) i13) * ((long) i25)) + (((long) i12) * ((long) i26));
        long j54 = ((long) i16) * ((long) i30);
        long j55 = (((long) i36) * (((long) i39) & 4294967295L)) + (((long) i35) * (((long) i40) & 4294967295L)) + (((long) i34) * (((long) i41) & 4294967295L));
        long j56 = ((long) i38) * (((long) i45) & 4294967295L);
        long j57 = j48 + (((j51 + j53) + j56) - j52);
        int i56 = ((int) j57) & M28;
        long j58 = j57 >>> 28;
        long j59 = j50 + ((j54 + j55) - j51) + j56;
        int i57 = ((int) j59) & M28;
        long j60 = j59 >>> 28;
        long j61 = (((long) i7) * ((long) i17)) + (((long) i6) * ((long) i18)) + (((long) i5) * ((long) i19)) + (((long) i4) * ((long) i4));
        long j62 = ((long) i8) * ((long) i8);
        long j63 = (((long) i15) * ((long) i24)) + (((long) i14) * ((long) i25)) + (((long) i13) * ((long) i26)) + (((long) i12) * ((long) i12));
        long j64 = ((long) i16) * ((long) i16);
        long j65 = (((long) i37) * (((long) i39) & 4294967295L)) + (((long) i36) * (((long) i40) & 4294967295L)) + (((long) i35) * (((long) i41) & 4294967295L)) + (((long) i34) * ((long) i34));
        long j66 = ((long) i38) * ((long) i38);
        long j67 = j58 + (((j61 + j63) + j66) - j62);
        int i58 = ((int) j67) & M28;
        long j68 = j67 >>> 28;
        long j69 = j60 + ((j64 + j65) - j61) + j66;
        int i59 = ((int) j69) & M28;
        long j70 = j69 >>> 28;
        long j71 = (((long) i8) * ((long) i17)) + (((long) i7) * ((long) i18)) + (((long) i6) * ((long) i19)) + (((long) i5) * ((long) i20));
        long j72 = (((long) i16) * ((long) i24)) + (((long) i15) * ((long) i25)) + (((long) i14) * ((long) i26)) + (((long) i13) * ((long) i27));
        long j73 = (((long) i38) * (((long) i39) & 4294967295L)) + (((long) i37) * (((long) i40) & 4294967295L)) + (((long) i36) * (((long) i41) & 4294967295L)) + (((long) i35) * (((long) i42) & 4294967295L));
        long j74 = j68 + j71 + j72;
        int i60 = ((int) j74) & M28;
        long j75 = j74 >>> 28;
        long j76 = j70 + (j73 - j71);
        int i61 = ((int) j76) & M28;
        long j77 = j76 >>> 28;
        long j78 = j75 + j77 + ((long) i47);
        int i62 = ((int) j78) & M28;
        long j79 = j78 >>> 28;
        long j80 = j77 + ((long) i46);
        int i63 = ((int) j80) & M28;
        long j81 = j80 >>> 28;
        iArr2[0] = i63;
        iArr2[1] = i48 + ((int) j81);
        iArr2[2] = i50;
        iArr2[3] = i52;
        iArr2[4] = i54;
        iArr2[5] = i56;
        iArr2[6] = i58;
        iArr2[7] = i60;
        iArr2[8] = i62;
        iArr2[9] = i49 + ((int) j79);
        iArr2[10] = i51;
        iArr2[11] = i53;
        iArr2[12] = i55;
        iArr2[13] = i57;
        iArr2[14] = i59;
        iArr2[15] = i61;
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
        sqr(iArr, iArrCreate);
        mul(iArrCreate, iArr2, iArrCreate);
        sqr(iArrCreate, iArrCreate2);
        mul(iArrCreate, iArr, iArrCreate);
        mul(iArrCreate2, iArr, iArrCreate2);
        mul(iArrCreate2, iArr2, iArrCreate2);
        int[] iArrCreate3 = create();
        powPm3d4(iArrCreate2, iArrCreate3);
        mul(iArrCreate3, iArrCreate, iArrCreate3);
        int[] iArrCreate4 = create();
        sqr(iArrCreate3, iArrCreate4);
        mul(iArrCreate4, iArr2, iArrCreate4);
        sub(iArr, iArrCreate4, iArrCreate4);
        normalize(iArrCreate4);
        if (!isZeroVar(iArrCreate4)) {
            return false;
        }
        copy(iArrCreate3, 0, iArr3, 0);
        return true;
    }

    public static void sub(int[] iArr, int[] iArr2, int[] iArr3) {
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
        int i11 = iArr[10];
        int i12 = iArr[11];
        int i13 = iArr[12];
        int i14 = iArr[13];
        int i15 = iArr[14];
        int i16 = iArr[15];
        int i17 = iArr2[0];
        int i18 = iArr2[1];
        int i19 = iArr2[2];
        int i20 = iArr2[3];
        int i21 = iArr2[4];
        int i22 = iArr2[5];
        int i23 = iArr2[6];
        int i24 = iArr2[7];
        int i25 = iArr2[8];
        int i26 = iArr2[9];
        int i27 = iArr2[10];
        int i28 = iArr2[11];
        int i29 = iArr2[12];
        int i30 = iArr2[13];
        int i31 = (i + 536870910) - i17;
        int i32 = (i2 + 536870910) - i18;
        int i33 = (i3 + 536870910) - i19;
        int i34 = (i4 + 536870910) - i20;
        int i35 = (i5 + 536870910) - i21;
        int i36 = (i6 + 536870910) - i22;
        int i37 = (i7 + 536870910) - i23;
        int i38 = (i8 + 536870910) - i24;
        int i39 = (i9 + 536870908) - i25;
        int i40 = (i10 + 536870910) - i26;
        int i41 = (i11 + 536870910) - i27;
        int i42 = (i12 + 536870910) - i28;
        int i43 = (i13 + 536870910) - i29;
        int i44 = (i14 + 536870910) - i30;
        int i45 = (i15 + 536870910) - iArr2[14];
        int i46 = (i16 + 536870910) - iArr2[15];
        int i47 = i33 + (i32 >>> 28);
        int i48 = i32 & M28;
        int i49 = i37 + (i36 >>> 28);
        int i50 = i36 & M28;
        int i51 = i41 + (i40 >>> 28);
        int i52 = i40 & M28;
        int i53 = i45 + (i44 >>> 28);
        int i54 = i44 & M28;
        int i55 = i34 + (i47 >>> 28);
        int i56 = i47 & M28;
        int i57 = i38 + (i49 >>> 28);
        int i58 = i49 & M28;
        int i59 = i42 + (i51 >>> 28);
        int i60 = i51 & M28;
        int i61 = i46 + (i53 >>> 28);
        int i62 = i53 & M28;
        int i63 = i61 >>> 28;
        int i64 = i61 & M28;
        int i65 = i31 + i63;
        int i66 = i39 + i63;
        int i67 = i35 + (i55 >>> 28);
        int i68 = i55 & M28;
        int i69 = i66 + (i57 >>> 28);
        int i70 = i57 & M28;
        int i71 = i43 + (i59 >>> 28);
        int i72 = i59 & M28;
        int i73 = i48 + (i65 >>> 28);
        int i74 = i65 & M28;
        int i75 = i50 + (i67 >>> 28);
        int i76 = i67 & M28;
        int i77 = i52 + (i69 >>> 28);
        int i78 = i69 & M28;
        int i79 = i54 + (i71 >>> 28);
        int i80 = i71 & M28;
        iArr3[0] = i74;
        iArr3[1] = i73;
        iArr3[2] = i56;
        iArr3[3] = i68;
        iArr3[4] = i76;
        iArr3[5] = i75;
        iArr3[6] = i58;
        iArr3[7] = i70;
        iArr3[8] = i78;
        iArr3[9] = i77;
        iArr3[10] = i60;
        iArr3[11] = i72;
        iArr3[12] = i80;
        iArr3[13] = i79;
        iArr3[14] = i62;
        iArr3[15] = i64;
    }

    public static void subOne(int[] iArr) {
        int[] iArrCreate = create();
        iArrCreate[0] = 1;
        sub(iArr, iArrCreate, iArr);
    }

    public static void zero(int[] iArr) {
        for (int i = 0; i < 16; i++) {
            iArr[i] = 0;
        }
    }
}
