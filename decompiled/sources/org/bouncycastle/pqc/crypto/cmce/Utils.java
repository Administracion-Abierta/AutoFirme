package org.bouncycastle.pqc.crypto.cmce;

import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/cmce/Utils.class */
class Utils {
    Utils() {
    }

    static void store_gf(byte[] bArr, int i, short s) {
        bArr[i + 0] = (byte) (s & 255);
        bArr[i + 1] = (byte) (s >> 8);
    }

    static short load_gf(byte[] bArr, int i, int i2) {
        return (short) (Pack.littleEndianToShort(bArr, i) & i2);
    }

    static int load4(byte[] bArr, int i) {
        return Pack.littleEndianToInt(bArr, i);
    }

    static void store8(byte[] bArr, int i, long j) {
        bArr[i + 0] = (byte) ((j >> 0) & 255);
        bArr[i + 1] = (byte) ((j >> 8) & 255);
        bArr[i + 2] = (byte) ((j >> 16) & 255);
        bArr[i + 3] = (byte) ((j >> 24) & 255);
        bArr[i + 4] = (byte) ((j >> 32) & 255);
        bArr[i + 5] = (byte) ((j >> 40) & 255);
        bArr[i + 6] = (byte) ((j >> 48) & 255);
        bArr[i + 7] = (byte) ((j >> 56) & 255);
    }

    static long load8(byte[] bArr, int i) {
        return Pack.littleEndianToLong(bArr, i);
    }

    static short bitrev(short s, int i) {
        short s2 = (short) (((s & 255) << 8) | ((s & 65280) >> 8));
        short s3 = (short) (((s2 & 3855) << 4) | ((s2 & 61680) >> 4));
        short s4 = (short) (((s3 & 13107) << 2) | ((s3 & 52428) >> 2));
        short s5 = (short) (((s4 & 21845) << 1) | ((s4 & 43690) >> 1));
        return i == 12 ? (short) (s5 >> 4) : (short) (s5 >> 3);
    }
}
