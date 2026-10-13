package org.bouncycastle.pqc.crypto.rainbow;

import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/rainbow/GF2Field.class */
class GF2Field {
    static final byte[][] gfMulTable = new byte[256][256];
    static final byte[] gfInvTable = new byte[256];
    public static final int MASK = 255;

    GF2Field() {
    }

    private static short gf4Mul2(short s) {
        return (short) (((s << 1) ^ ((s >>> 1) * 7)) & 255);
    }

    private static short gf4Mul3(short s) {
        int i = (s - 2) >>> 1;
        return (short) (((i & (s * 3)) | ((i ^ (-1)) & (s - 1))) & 255);
    }

    private static short gf4Mul(short s, short s2) {
        return (short) (((s * (s2 & 1)) ^ (gf4Mul2(s) * (s2 >>> 1))) & 255);
    }

    private static short gf4Squ(short s) {
        return (short) ((s ^ (s >>> 1)) & 255);
    }

    private static short gf16Mul(short s, short s2) {
        short s3 = (short) (s & 3 & 255);
        short s4 = (short) ((s >>> 2) & 255);
        short s5 = (short) (s2 & 3 & 255);
        short s6 = (short) ((s2 >>> 2) & 255);
        short sGf4Mul = gf4Mul(s3, s5);
        short sGf4Mul2 = gf4Mul(s4, s6);
        short sGf4Mul3 = (short) (gf4Mul((short) (s3 ^ s4), (short) (s5 ^ s6)) ^ sGf4Mul);
        return (short) ((((sGf4Mul3 << 2) ^ sGf4Mul) ^ gf4Mul2(sGf4Mul2)) & 255);
    }

    private static short gf16Squ(short s) {
        short s2 = (short) (s & 3 & 255);
        short sGf4Squ = gf4Squ((short) ((s >>> 2) & 255));
        return (short) ((((sGf4Squ << 2) ^ gf4Mul2(sGf4Squ)) ^ gf4Squ(s2)) & 255);
    }

    private static short gf16Mul8(short s) {
        short s2 = (short) ((s >>> 2) & 255);
        return (short) (((gf4Mul2((short) (((short) ((s & 3) & 255)) ^ s2)) << 2) | gf4Mul3(s2)) & 255);
    }

    private static short gf256Mul(short s, short s2) {
        short s3 = (short) (s & 15 & 255);
        short s4 = (short) ((s >>> 4) & 255);
        short s5 = (short) (s2 & 15 & 255);
        short s6 = (short) ((s2 >>> 4) & 255);
        short sGf16Mul = gf16Mul(s3, s5);
        short sGf16Mul2 = gf16Mul(s4, s6);
        short sGf16Mul3 = (short) (gf16Mul((short) (s3 ^ s4), (short) (s5 ^ s6)) ^ sGf16Mul);
        return (short) ((((sGf16Mul3 << 4) ^ sGf16Mul) ^ gf16Mul8(sGf16Mul2)) & 255);
    }

    private static short gf256Squ(short s) {
        short s2 = (short) (s & 15 & 255);
        short sGf16Squ = gf16Squ((short) ((s >>> 4) & 255));
        return (short) ((((sGf16Squ << 4) ^ gf16Mul8(sGf16Squ)) ^ gf16Squ(s2)) & 255);
    }

    private static short gf256Inv(short s) {
        short sGf256Squ = gf256Squ(s);
        short sGf256Squ2 = gf256Squ(sGf256Squ);
        short sGf256Mul = gf256Mul(gf256Mul(sGf256Squ2, sGf256Squ), gf256Squ(sGf256Squ2));
        return gf256Mul(sGf256Squ, gf256Squ(gf256Mul(gf256Squ(gf256Squ(gf256Squ(sGf256Mul))), sGf256Mul)));
    }

    public static short addElem(short s, short s2) {
        return (short) (s ^ s2);
    }

    public static long addElem_64(long j, long j2) {
        return j ^ j2;
    }

    public static short invElem(short s) {
        return (short) (gfInvTable[s] & 255);
    }

    public static long invElem_64(long j) {
        return gf256Inv_64(j);
    }

    public static short multElem(short s, short s2) {
        return (short) (gfMulTable[s][s2] & 255);
    }

    public static long multElem_64(long j, long j2) {
        return gf256Mul_64(j, j2);
    }

    private static long gf4Mul2_64(long j) {
        long j2 = j & (-6148914691236517206L);
        return (j2 ^ ((j & 6148914691236517205L) << 1)) ^ (j2 >>> 1);
    }

    private static long gf4Mul_64(long j, long j2) {
        long j3 = j & j2;
        return (j3 ^ ((((j << 1) & j2) ^ ((j2 << 1) & j)) & (-6148914691236517206L))) ^ ((j3 & (-6148914691236517206L)) >>> 1);
    }

    private static long gf4Squ_64(long j) {
        return j ^ ((j & (-6148914691236517206L)) >>> 1);
    }

    private static long gf16Mul_64(long j, long j2) {
        long jGf4Mul_64 = gf4Mul_64(j, j2);
        long j3 = jGf4Mul_64 & 3689348814741910323L;
        return (gf4Mul_64((((j << 2) ^ j) & (-3689348814741910324L)) ^ ((jGf4Mul_64 & (-3689348814741910324L)) >>> 2), (((j2 << 2) ^ j2) & (-3689348814741910324L)) ^ 2459565876494606882L) ^ (j3 << 2)) ^ j3;
    }

    private static long gf16Squ_64(long j) {
        long jGf4Squ_64 = gf4Squ_64(j);
        return jGf4Squ_64 ^ (gf4Mul2_64(jGf4Squ_64 & (-3689348814741910324L)) >>> 2);
    }

    private static long gf16Mul8_64(long j) {
        long j2 = j & (-3689348814741910324L);
        return gf4Mul2_64((((j & 3689348814741910323L) << 2) ^ j2) ^ (j2 >>> 2)) ^ (j2 >>> 2);
    }

    private static long gf256Mul_64(long j, long j2) {
        long jGf16Mul_64 = gf16Mul_64(j, j2);
        long j3 = jGf16Mul_64 & 1085102592571150095L;
        return (gf16Mul_64((((j << 4) ^ j) & (-1085102592571150096L)) ^ ((jGf16Mul_64 & (-1085102592571150096L)) >>> 4), (((j2 << 4) ^ j2) & (-1085102592571150096L)) ^ 578721382704613384L) ^ (j3 << 4)) ^ j3;
    }

    private static long gf256Squ_64(long j) {
        long jGf16Squ_64 = gf16Squ_64(j);
        return jGf16Squ_64 ^ (gf16Mul8_64(jGf16Squ_64 & (-1085102592571150096L)) >>> 4);
    }

    private static long gf256Inv_64(long j) {
        long jGf256Squ_64 = gf256Squ_64(j);
        long jGf256Squ_65 = gf256Squ_64(jGf256Squ_64);
        long jGf256Mul_64 = gf256Mul_64(gf256Mul_64(jGf256Squ_65, jGf256Squ_64), gf256Squ_64(jGf256Squ_65));
        return gf256Mul_64(jGf256Squ_64, gf256Squ_64(gf256Mul_64(gf256Squ_64(gf256Squ_64(gf256Squ_64(jGf256Mul_64))), jGf256Mul_64)));
    }

    static {
        long j = 72340172838076673L;
        for (int i = 1; i <= 255; i++) {
            long j2 = 506097522914230528L;
            for (int i2 = 0; i2 < 256; i2 += 8) {
                Pack.longToLittleEndian(gf256Mul_64(j, j2), gfMulTable[i], i2);
                j2 += 578721382704613384L;
            }
            j += 72340172838076673L;
        }
        long j3 = 506097522914230528L;
        for (int i3 = 0; i3 < 256; i3 += 8) {
            Pack.longToLittleEndian(gf256Inv_64(j3), gfInvTable, i3);
            j3 += 578721382704613384L;
        }
    }
}
