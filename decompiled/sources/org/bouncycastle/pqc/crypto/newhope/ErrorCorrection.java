package org.bouncycastle.pqc.crypto.newhope;

import org.bouncycastle.util.Arrays;
import org.spongycastle.crypto.tls.CipherSuite;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/newhope/ErrorCorrection.class */
class ErrorCorrection {
    ErrorCorrection() {
    }

    static int abs(int i) {
        int i2 = i >> 31;
        return (i ^ i2) - i2;
    }

    static int f(int[] iArr, int i, int i2, int i3) {
        int i4 = (i3 * 2730) >> 25;
        int i5 = i4 - ((12288 - (i3 - (i4 * 12289))) >> 31);
        iArr[i] = (i5 >> 1) + (i5 & 1);
        int i6 = i5 - 1;
        iArr[i2] = (i6 >> 1) + (i6 & 1);
        return abs(i3 - ((iArr[i] * 2) * 12289));
    }

    static int g(int i) {
        int i2 = (i * 2730) >> 27;
        int i3 = i2 - ((CipherSuite.TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA - (i - (i2 * CipherSuite.TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA))) >> 31);
        return abs((((i3 >> 1) + (i3 & 1)) * 98312) - i);
    }

    static void helpRec(short[] sArr, short[] sArr2, byte[] bArr, byte b) {
        byte[] bArr2 = new byte[8];
        bArr2[0] = b;
        byte[] bArr3 = new byte[32];
        ChaCha20.process(bArr, bArr2, bArr3, 0, bArr3.length);
        int[] iArr = new int[8];
        int[] iArr2 = new int[4];
        for (int i = 0; i < 256; i++) {
            int i2 = (bArr3[i >>> 3] >>> (i & 7)) & 1;
            int iF = (24577 - (((f(iArr, 0, 4, (8 * sArr2[0 + i]) + (4 * i2)) + f(iArr, 1, 5, (8 * sArr2[256 + i]) + (4 * i2))) + f(iArr, 2, 6, (8 * sArr2[512 + i]) + (4 * i2))) + f(iArr, 3, 7, (8 * sArr2[768 + i]) + (4 * i2)))) >> 31;
            iArr2[0] = ((iF ^ (-1)) & iArr[0]) ^ (iF & iArr[4]);
            iArr2[1] = ((iF ^ (-1)) & iArr[1]) ^ (iF & iArr[5]);
            iArr2[2] = ((iF ^ (-1)) & iArr[2]) ^ (iF & iArr[6]);
            iArr2[3] = ((iF ^ (-1)) & iArr[3]) ^ (iF & iArr[7]);
            sArr[0 + i] = (short) ((iArr2[0] - iArr2[3]) & 3);
            sArr[256 + i] = (short) ((iArr2[1] - iArr2[3]) & 3);
            sArr[512 + i] = (short) ((iArr2[2] - iArr2[3]) & 3);
            sArr[768 + i] = (short) (((-iF) + (2 * iArr2[3])) & 3);
        }
    }

    static short LDDecode(int i, int i2, int i3, int i4) {
        return (short) (((((g(i) + g(i2)) + g(i3)) + g(i4)) - 98312) >>> 31);
    }

    static void rec(byte[] bArr, short[] sArr, short[] sArr2) {
        Arrays.fill(bArr, (byte) 0);
        int[] iArr = new int[4];
        for (int i = 0; i < 256; i++) {
            iArr[0] = (196624 + (8 * sArr[0 + i])) - (12289 * ((2 * sArr2[0 + i]) + sArr2[768 + i]));
            iArr[1] = (196624 + (8 * sArr[256 + i])) - (12289 * ((2 * sArr2[256 + i]) + sArr2[768 + i]));
            iArr[2] = (196624 + (8 * sArr[512 + i])) - (12289 * ((2 * sArr2[512 + i]) + sArr2[768 + i]));
            iArr[3] = (196624 + (8 * sArr[768 + i])) - (12289 * sArr2[768 + i]);
            int i2 = i >>> 3;
            bArr[i2] = (byte) (bArr[i2] | (LDDecode(iArr[0], iArr[1], iArr[2], iArr[3]) << (i & 7)));
        }
    }
}
