package org.bouncycastle.math.ec.rfc8032;

import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat256;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/rfc8032/Scalar25519.class */
abstract class Scalar25519 {
    static final int SIZE = 8;
    private static final int SCALAR_BYTES = 32;
    private static final long M08L = 255;
    private static final long M28L = 268435455;
    private static final long M32L = 4294967295L;
    private static final int TARGET_LENGTH = 254;
    private static final int[] L = {1559614445, 1477600026, -1560830762, 350157278, 0, 0, 0, 268435456};
    private static final int[] LSq = {-1424848535, -487721339, 580428573, 1745064566, -770181698, 1036971123, 461123738, -1582065343, 1268693629, -889041821, -731974758, 43769659, 0, 0, 0, 16777216};
    private static final int L0 = -50998291;
    private static final int L1 = 19280294;
    private static final int L2 = 127719000;
    private static final int L3 = -6428113;
    private static final int L4 = 5343;

    Scalar25519() {
    }

    static boolean checkVar(byte[] bArr, int[] iArr) {
        decode(bArr, iArr);
        return !Nat256.gte(iArr, L);
    }

    static void decode(byte[] bArr, int[] iArr) {
        Codec.decode32(bArr, 0, iArr, 0, 8);
    }

    static void getOrderWnafVar(int i, byte[] bArr) {
        Wnaf.getSignedVar(L, i, bArr);
    }

    static void multiply128Var(int[] iArr, int[] iArr2, int[] iArr3) {
        int[] iArr4 = new int[12];
        Nat256.mul128(iArr, iArr2, iArr4);
        if (iArr2[3] < 0) {
            Nat256.addTo(L, 0, iArr4, 4, 0);
            Nat256.subFrom(iArr, 0, iArr4, 4, 0);
        }
        byte[] bArr = new byte[48];
        Codec.encode32(iArr4, 0, 12, bArr, 0);
        decode(reduce384(bArr), iArr3);
    }

    static byte[] reduce384(byte[] bArr) {
        long jDecode32 = ((long) Codec.decode32(bArr, 0)) & 4294967295L;
        long jDecode24 = ((long) (Codec.decode24(bArr, 4) << 4)) & 4294967295L;
        long jDecode33 = ((long) Codec.decode32(bArr, 7)) & 4294967295L;
        long jDecode25 = ((long) (Codec.decode24(bArr, 11) << 4)) & 4294967295L;
        long jDecode34 = ((long) Codec.decode32(bArr, 14)) & 4294967295L;
        long jDecode26 = ((long) (Codec.decode24(bArr, 18) << 4)) & 4294967295L;
        long jDecode35 = ((long) Codec.decode32(bArr, 21)) & 4294967295L;
        long jDecode27 = ((long) (Codec.decode24(bArr, 25) << 4)) & 4294967295L;
        long jDecode36 = ((long) Codec.decode32(bArr, 28)) & 4294967295L;
        long jDecode28 = ((long) (Codec.decode24(bArr, 32) << 4)) & 4294967295L;
        long jDecode37 = ((long) Codec.decode32(bArr, 35)) & 4294967295L;
        long jDecode29 = ((long) (Codec.decode24(bArr, 39) << 4)) & 4294967295L;
        long jDecode38 = ((long) Codec.decode32(bArr, 42)) & 4294967295L;
        long jDecode16 = (((long) (Codec.decode16(bArr, 46) << 4)) & 4294967295L) + (jDecode38 >> 28);
        long j = jDecode38 & M28L;
        long j2 = jDecode34 - (jDecode16 * (-50998291));
        long j3 = jDecode26 - (jDecode16 * 19280294);
        long j4 = jDecode35 - (jDecode16 * 127719000);
        long j5 = jDecode27 - (jDecode16 * (-6428113));
        long j6 = jDecode36 - (jDecode16 * 5343);
        long j7 = j + (jDecode29 >> 28);
        long j8 = jDecode29 & M28L;
        long j9 = jDecode25 - (j7 * (-50998291));
        long j10 = j2 - (j7 * 19280294);
        long j11 = j3 - (j7 * 127719000);
        long j12 = j4 - (j7 * (-6428113));
        long j13 = j5 - (j7 * 5343);
        long j14 = j8 + (jDecode37 >> 28);
        long j15 = jDecode37 & M28L;
        long j16 = jDecode33 - (j14 * (-50998291));
        long j17 = j9 - (j14 * 19280294);
        long j18 = j10 - (j14 * 127719000);
        long j19 = j11 - (j14 * (-6428113));
        long j20 = j12 - (j14 * 5343);
        long j21 = j15 + (jDecode28 >> 28);
        long j22 = jDecode28 & M28L;
        long j23 = jDecode24 - (j21 * (-50998291));
        long j24 = j16 - (j21 * 19280294);
        long j25 = j17 - (j21 * 127719000);
        long j26 = j18 - (j21 * (-6428113));
        long j27 = j19 - (j21 * 5343);
        long j28 = j6 + (j13 >> 28);
        long j29 = j13 & M28L;
        long j30 = j22 + (j28 >> 28);
        long j31 = j28 & M28L;
        long j32 = j31 >>> 27;
        long j33 = j30 + j32;
        long j34 = jDecode32 - (j33 * (-50998291));
        long j35 = j23 - (j33 * 19280294);
        long j36 = j24 - (j33 * 127719000);
        long j37 = j25 - (j33 * (-6428113));
        long j38 = j26 - (j33 * 5343);
        long j39 = j35 + (j34 >> 28);
        long j40 = j34 & M28L;
        long j41 = j36 + (j39 >> 28);
        long j42 = j39 & M28L;
        long j43 = j37 + (j41 >> 28);
        long j44 = j41 & M28L;
        long j45 = j38 + (j43 >> 28);
        long j46 = j43 & M28L;
        long j47 = j27 + (j45 >> 28);
        long j48 = j45 & M28L;
        long j49 = j20 + (j47 >> 28);
        long j50 = j47 & M28L;
        long j51 = j29 + (j49 >> 28);
        long j52 = j49 & M28L;
        long j53 = j31 + (j51 >> 28);
        long j54 = j51 & M28L;
        long j55 = j53 >> 28;
        long j56 = j53 & M28L;
        long j57 = j55 - j32;
        long j58 = j40 + (j57 & (-50998291));
        long j59 = j42 + (j57 & 19280294);
        long j60 = j44 + (j57 & 127719000);
        long j61 = j46 + (j57 & (-6428113));
        long j62 = j48 + (j57 & 5343);
        long j63 = j59 + (j58 >> 28);
        long j64 = j58 & M28L;
        long j65 = j60 + (j63 >> 28);
        long j66 = j63 & M28L;
        long j67 = j61 + (j65 >> 28);
        long j68 = j65 & M28L;
        long j69 = j62 + (j67 >> 28);
        long j70 = j67 & M28L;
        long j71 = j50 + (j69 >> 28);
        long j72 = j69 & M28L;
        long j73 = j52 + (j71 >> 28);
        long j74 = j71 & M28L;
        long j75 = j54 + (j73 >> 28);
        long j76 = j73 & M28L;
        long j77 = j56 + (j75 >> 28);
        long j78 = j75 & M28L;
        byte[] bArr2 = new byte[64];
        Codec.encode56(j64 | (j66 << 28), bArr2, 0);
        Codec.encode56(j68 | (j70 << 28), bArr2, 7);
        Codec.encode56(j72 | (j74 << 28), bArr2, 14);
        Codec.encode56(j76 | (j78 << 28), bArr2, 21);
        Codec.encode32((int) j77, bArr2, 28);
        return bArr2;
    }

    static byte[] reduce512(byte[] bArr) {
        long jDecode32 = ((long) Codec.decode32(bArr, 0)) & 4294967295L;
        long jDecode24 = ((long) (Codec.decode24(bArr, 4) << 4)) & 4294967295L;
        long jDecode33 = ((long) Codec.decode32(bArr, 7)) & 4294967295L;
        long jDecode25 = ((long) (Codec.decode24(bArr, 11) << 4)) & 4294967295L;
        long jDecode34 = ((long) Codec.decode32(bArr, 14)) & 4294967295L;
        long jDecode26 = ((long) (Codec.decode24(bArr, 18) << 4)) & 4294967295L;
        long jDecode35 = ((long) Codec.decode32(bArr, 21)) & 4294967295L;
        long jDecode27 = ((long) (Codec.decode24(bArr, 25) << 4)) & 4294967295L;
        long jDecode36 = ((long) Codec.decode32(bArr, 28)) & 4294967295L;
        long jDecode28 = ((long) (Codec.decode24(bArr, 32) << 4)) & 4294967295L;
        long jDecode37 = ((long) Codec.decode32(bArr, 35)) & 4294967295L;
        long jDecode29 = ((long) (Codec.decode24(bArr, 39) << 4)) & 4294967295L;
        long jDecode38 = ((long) Codec.decode32(bArr, 42)) & 4294967295L;
        long jDecode210 = ((long) (Codec.decode24(bArr, 46) << 4)) & 4294967295L;
        long jDecode39 = ((long) Codec.decode32(bArr, 49)) & 4294967295L;
        long jDecode211 = ((long) (Codec.decode24(bArr, 53) << 4)) & 4294967295L;
        long jDecode310 = ((long) Codec.decode32(bArr, 56)) & 4294967295L;
        long jDecode212 = ((long) (Codec.decode24(bArr, 60) << 4)) & 4294967295L;
        long j = ((long) bArr[63]) & M08L;
        long j2 = jDecode28 - (j * (-50998291));
        long j3 = jDecode37 - (j * 19280294);
        long j4 = jDecode29 - (j * 127719000);
        long j5 = jDecode38 - (j * (-6428113));
        long j6 = jDecode210 - (j * 5343);
        long j7 = jDecode212 + (jDecode310 >> 28);
        long j8 = jDecode310 & M28L;
        long j9 = jDecode36 - (j7 * (-50998291));
        long j10 = j2 - (j7 * 19280294);
        long j11 = j3 - (j7 * 127719000);
        long j12 = j4 - (j7 * (-6428113));
        long j13 = j5 - (j7 * 5343);
        long j14 = jDecode27 - (j8 * (-50998291));
        long j15 = j9 - (j8 * 19280294);
        long j16 = j10 - (j8 * 127719000);
        long j17 = j11 - (j8 * (-6428113));
        long j18 = j12 - (j8 * 5343);
        long j19 = jDecode211 + (jDecode39 >> 28);
        long j20 = jDecode39 & M28L;
        long j21 = jDecode35 - (j19 * (-50998291));
        long j22 = j14 - (j19 * 19280294);
        long j23 = j15 - (j19 * 127719000);
        long j24 = j16 - (j19 * (-6428113));
        long j25 = j17 - (j19 * 5343);
        long j26 = jDecode26 - (j20 * (-50998291));
        long j27 = j21 - (j20 * 19280294);
        long j28 = j22 - (j20 * 127719000);
        long j29 = j23 - (j20 * (-6428113));
        long j30 = j24 - (j20 * 5343);
        long j31 = j6 + (j13 >> 28);
        long j32 = j13 & M28L;
        long j33 = jDecode34 - (j31 * (-50998291));
        long j34 = j26 - (j31 * 19280294);
        long j35 = j27 - (j31 * 127719000);
        long j36 = j28 - (j31 * (-6428113));
        long j37 = j29 - (j31 * 5343);
        long j38 = j32 + (j18 >> 28);
        long j39 = j18 & M28L;
        long j40 = jDecode25 - (j38 * (-50998291));
        long j41 = j33 - (j38 * 19280294);
        long j42 = j34 - (j38 * 127719000);
        long j43 = j35 - (j38 * (-6428113));
        long j44 = j36 - (j38 * 5343);
        long j45 = j39 + (j25 >> 28);
        long j46 = j25 & M28L;
        long j47 = jDecode33 - (j45 * (-50998291));
        long j48 = j40 - (j45 * 19280294);
        long j49 = j41 - (j45 * 127719000);
        long j50 = j42 - (j45 * (-6428113));
        long j51 = j43 - (j45 * 5343);
        long j52 = j46 + (j30 >> 28);
        long j53 = j30 & M28L;
        long j54 = jDecode24 - (j52 * (-50998291));
        long j55 = j47 - (j52 * 19280294);
        long j56 = j48 - (j52 * 127719000);
        long j57 = j49 - (j52 * (-6428113));
        long j58 = j50 - (j52 * 5343);
        long j59 = j37 + (j44 >> 28);
        long j60 = j44 & M28L;
        long j61 = j53 + (j59 >> 28);
        long j62 = j59 & M28L;
        long j63 = j62 >>> 27;
        long j64 = j61 + j63;
        long j65 = jDecode32 - (j64 * (-50998291));
        long j66 = j54 - (j64 * 19280294);
        long j67 = j55 - (j64 * 127719000);
        long j68 = j56 - (j64 * (-6428113));
        long j69 = j57 - (j64 * 5343);
        long j70 = j66 + (j65 >> 28);
        long j71 = j65 & M28L;
        long j72 = j67 + (j70 >> 28);
        long j73 = j70 & M28L;
        long j74 = j68 + (j72 >> 28);
        long j75 = j72 & M28L;
        long j76 = j69 + (j74 >> 28);
        long j77 = j74 & M28L;
        long j78 = j58 + (j76 >> 28);
        long j79 = j76 & M28L;
        long j80 = j51 + (j78 >> 28);
        long j81 = j78 & M28L;
        long j82 = j60 + (j80 >> 28);
        long j83 = j80 & M28L;
        long j84 = j62 + (j82 >> 28);
        long j85 = j82 & M28L;
        long j86 = j84 >> 28;
        long j87 = j84 & M28L;
        long j88 = j86 - j63;
        long j89 = j71 + (j88 & (-50998291));
        long j90 = j73 + (j88 & 19280294);
        long j91 = j75 + (j88 & 127719000);
        long j92 = j77 + (j88 & (-6428113));
        long j93 = j79 + (j88 & 5343);
        long j94 = j90 + (j89 >> 28);
        long j95 = j89 & M28L;
        long j96 = j91 + (j94 >> 28);
        long j97 = j94 & M28L;
        long j98 = j92 + (j96 >> 28);
        long j99 = j96 & M28L;
        long j100 = j93 + (j98 >> 28);
        long j101 = j98 & M28L;
        long j102 = j81 + (j100 >> 28);
        long j103 = j100 & M28L;
        long j104 = j83 + (j102 >> 28);
        long j105 = j102 & M28L;
        long j106 = j85 + (j104 >> 28);
        long j107 = j104 & M28L;
        long j108 = j87 + (j106 >> 28);
        long j109 = j106 & M28L;
        byte[] bArr2 = new byte[32];
        Codec.encode56(j95 | (j97 << 28), bArr2, 0);
        Codec.encode56(j99 | (j101 << 28), bArr2, 7);
        Codec.encode56(j103 | (j105 << 28), bArr2, 14);
        Codec.encode56(j107 | (j109 << 28), bArr2, 21);
        Codec.encode32((int) j108, bArr2, 28);
        return bArr2;
    }

    static boolean reduceBasisVar(int[] iArr, int[] iArr2, int[] iArr3) {
        int[] iArr4 = new int[16];
        System.arraycopy(LSq, 0, iArr4, 0, 16);
        int[] iArr5 = new int[16];
        Nat256.square(iArr, iArr5);
        iArr5[0] = iArr5[0] + 1;
        int[] iArr6 = new int[16];
        Nat256.mul(L, iArr, iArr6);
        int[] iArr7 = new int[16];
        int[] iArr8 = new int[4];
        System.arraycopy(L, 0, iArr8, 0, 4);
        int[] iArr9 = new int[4];
        int[] iArr10 = new int[4];
        System.arraycopy(iArr, 0, iArr10, 0, 4);
        int[] iArr11 = new int[4];
        iArr11[0] = 1;
        int i = 1016;
        int i2 = 15;
        int bitLengthPositive = ScalarUtil.getBitLengthPositive(15, iArr5);
        while (bitLengthPositive > 254) {
            i--;
            if (i < 0) {
                return false;
            }
            int bitLength = ScalarUtil.getBitLength(i2, iArr6) - bitLengthPositive;
            int i3 = bitLength & ((bitLength >> 31) ^ (-1));
            if (iArr6[i2] < 0) {
                ScalarUtil.addShifted_NP(i2, i3, iArr4, iArr5, iArr6, iArr7);
                ScalarUtil.addShifted_UV(3, i3, iArr8, iArr9, iArr10, iArr11);
            } else {
                ScalarUtil.subShifted_NP(i2, i3, iArr4, iArr5, iArr6, iArr7);
                ScalarUtil.subShifted_UV(3, i3, iArr8, iArr9, iArr10, iArr11);
            }
            if (ScalarUtil.lessThan(i2, iArr4, iArr5)) {
                int[] iArr12 = iArr8;
                iArr8 = iArr10;
                iArr10 = iArr12;
                int[] iArr13 = iArr9;
                iArr9 = iArr11;
                iArr11 = iArr13;
                int[] iArr14 = iArr4;
                iArr4 = iArr5;
                iArr5 = iArr14;
                i2 = bitLengthPositive >>> 5;
                bitLengthPositive = ScalarUtil.getBitLengthPositive(i2, iArr5);
            }
        }
        System.arraycopy(iArr10, 0, iArr2, 0, 4);
        System.arraycopy(iArr11, 0, iArr3, 0, 4);
        return true;
    }

    static void toSignedDigits(int i, int[] iArr) {
        Nat.caddTo(8, (iArr[0] ^ (-1)) & 1, L, iArr);
        Nat.shiftDownBit(8, iArr, 1);
    }
}
