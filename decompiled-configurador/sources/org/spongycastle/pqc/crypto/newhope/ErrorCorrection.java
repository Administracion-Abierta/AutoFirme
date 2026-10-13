package org.spongycastle.pqc.crypto.newhope;

import org.spongycastle.crypto.tls.CipherSuite;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/newhope/ErrorCorrection.class */
class ErrorCorrection {
    ErrorCorrection() {
    }

    static int abs(int v) {
        int mask = v >> 31;
        return (v ^ mask) - mask;
    }

    static int f(int[] v, int off0, int off1, int x) {
        int b = x * 2730;
        int t = b >> 25;
        int b2 = x - (t * 12289);
        int t2 = t - ((12288 - b2) >> 31);
        int r = t2 & 1;
        int xit = t2 >> 1;
        v[off0] = xit + r;
        int t3 = t2 - 1;
        int r2 = t3 & 1;
        v[off1] = (t3 >> 1) + r2;
        return abs(x - ((v[off0] * 2) * 12289));
    }

    static int g(int x) {
        int b = x * 2730;
        int t = b >> 27;
        int b2 = x - (t * CipherSuite.TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA);
        int t2 = t - ((CipherSuite.TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA - b2) >> 31);
        int c = t2 & 1;
        return abs((((t2 >> 1) + c) * 98312) - x);
    }

    static void helpRec(short[] c, short[] v, byte[] seed, byte nonce) {
        byte[] iv = new byte[8];
        iv[0] = nonce;
        byte[] rand = new byte[32];
        ChaCha20.process(seed, iv, rand, 0, rand.length);
        int[] vs = new int[8];
        int[] vTmp = new int[4];
        for (int i = 0; i < 256; i++) {
            int rBit = (rand[i >>> 3] >>> (i & 7)) & 1;
            int k = (24577 - (((f(vs, 0, 4, (8 * v[0 + i]) + (4 * rBit)) + f(vs, 1, 5, (8 * v[256 + i]) + (4 * rBit))) + f(vs, 2, 6, (8 * v[512 + i]) + (4 * rBit))) + f(vs, 3, 7, (8 * v[768 + i]) + (4 * rBit)))) >> 31;
            vTmp[0] = ((k ^ (-1)) & vs[0]) ^ (k & vs[4]);
            vTmp[1] = ((k ^ (-1)) & vs[1]) ^ (k & vs[5]);
            vTmp[2] = ((k ^ (-1)) & vs[2]) ^ (k & vs[6]);
            vTmp[3] = ((k ^ (-1)) & vs[3]) ^ (k & vs[7]);
            c[0 + i] = (short) ((vTmp[0] - vTmp[3]) & 3);
            c[256 + i] = (short) ((vTmp[1] - vTmp[3]) & 3);
            c[512 + i] = (short) ((vTmp[2] - vTmp[3]) & 3);
            c[768 + i] = (short) (((-k) + (2 * vTmp[3])) & 3);
        }
    }

    static short LDDecode(int xi0, int xi1, int xi2, int xi3) {
        int t = g(xi0);
        return (short) (((((t + g(xi1)) + g(xi2)) + g(xi3)) - 98312) >>> 31);
    }

    static void rec(byte[] key, short[] v, short[] c) {
        Arrays.fill(key, (byte) 0);
        int[] tmp = new int[4];
        for (int i = 0; i < 256; i++) {
            tmp[0] = (196624 + (8 * v[0 + i])) - (12289 * ((2 * c[0 + i]) + c[768 + i]));
            tmp[1] = (196624 + (8 * v[256 + i])) - (12289 * ((2 * c[256 + i]) + c[768 + i]));
            tmp[2] = (196624 + (8 * v[512 + i])) - (12289 * ((2 * c[512 + i]) + c[768 + i]));
            tmp[3] = (196624 + (8 * v[768 + i])) - (12289 * c[768 + i]);
            int i2 = i >>> 3;
            key[i2] = (byte) (key[i2] | (LDDecode(tmp[0], tmp[1], tmp[2], tmp[3]) << (i & 7)));
        }
    }
}
