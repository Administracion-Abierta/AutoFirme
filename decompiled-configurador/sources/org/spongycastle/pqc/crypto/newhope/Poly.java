package org.spongycastle.pqc.crypto.newhope;

import android.R;
import org.spongycastle.crypto.digests.SHAKEDigest;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/newhope/Poly.class */
class Poly {
    Poly() {
    }

    static void add(short[] x, short[] y, short[] z) {
        for (int i = 0; i < 1024; i++) {
            z[i] = Reduce.barrett((short) (x[i] + y[i]));
        }
    }

    static void fromBytes(short[] r, byte[] a) {
        for (int i = 0; i < 256; i++) {
            int j = 7 * i;
            int a0 = a[j + 0] & 255;
            int a1 = a[j + 1] & 255;
            int a2 = a[j + 2] & 255;
            int a3 = a[j + 3] & 255;
            int a4 = a[j + 4] & 255;
            int a5 = a[j + 5] & 255;
            int a6 = a[j + 6] & 255;
            int k = 4 * i;
            r[k + 0] = (short) (a0 | ((a1 & 63) << 8));
            r[k + 1] = (short) ((a1 >>> 6) | (a2 << 2) | ((a3 & 15) << 10));
            r[k + 2] = (short) ((a3 >>> 4) | (a4 << 4) | ((a5 & 3) << 12));
            r[k + 3] = (short) ((a5 >>> 2) | (a6 << 6));
        }
    }

    static void fromNTT(short[] r) {
        NTT.bitReverse(r);
        NTT.core(r, Precomp.OMEGAS_INV_MONTGOMERY);
        NTT.mulCoefficients(r, Precomp.PSIS_INV_MONTGOMERY);
    }

    static void getNoise(short[] r, byte[] seed, byte nonce) {
        byte[] iv = new byte[8];
        iv[0] = nonce;
        byte[] buf = new byte[4096];
        ChaCha20.process(seed, iv, buf, 0, buf.length);
        for (int i = 0; i < 1024; i++) {
            int t = Pack.bigEndianToInt(buf, i * 4);
            int d = 0;
            for (int j = 0; j < 8; j++) {
                d += (t >> j) & R.attr.cacheColorHint;
            }
            int a = ((d >>> 24) + (d >>> 0)) & 255;
            int b = ((d >>> 16) + (d >>> 8)) & 255;
            r[i] = (short) ((a + 12289) - b);
        }
    }

    static void pointWise(short[] x, short[] y, short[] z) {
        for (int i = 0; i < 1024; i++) {
            int xi = x[i] & 65535;
            int yi = y[i] & 65535;
            short t = Reduce.montgomery(3186 * yi);
            z[i] = Reduce.montgomery(xi * (t & 65535));
        }
    }

    static void toBytes(byte[] r, short[] p) {
        for (int i = 0; i < 256; i++) {
            int j = 4 * i;
            short t0 = normalize(p[j + 0]);
            short t1 = normalize(p[j + 1]);
            short t2 = normalize(p[j + 2]);
            short t3 = normalize(p[j + 3]);
            int k = 7 * i;
            r[k + 0] = (byte) t0;
            r[k + 1] = (byte) ((t0 >> 8) | (t1 << 6));
            r[k + 2] = (byte) (t1 >> 2);
            r[k + 3] = (byte) ((t1 >> 10) | (t2 << 4));
            r[k + 4] = (byte) (t2 >> 4);
            r[k + 5] = (byte) ((t2 >> 12) | (t3 << 2));
            r[k + 6] = (byte) (t3 >> 6);
        }
    }

    static void toNTT(short[] r) {
        NTT.mulCoefficients(r, Precomp.PSIS_BITREV_MONTGOMERY);
        NTT.core(r, Precomp.OMEGAS_MONTGOMERY);
    }

    static void uniform(short[] a, byte[] seed) {
        SHAKEDigest xof = new SHAKEDigest(128);
        xof.update(seed, 0, seed.length);
        int pos = 0;
        while (true) {
            byte[] output = new byte[256];
            xof.doOutput(output, 0, output.length);
            for (int i = 0; i < output.length; i += 2) {
                int val = ((output[i] & 255) | ((output[i + 1] & 255) << 8)) & 16383;
                if (val < 12289) {
                    int i2 = pos;
                    pos++;
                    a[i2] = (short) val;
                    if (pos == 1024) {
                        return;
                    }
                }
            }
        }
    }

    private static short normalize(short x) {
        int t = Reduce.barrett(x);
        int m = t - 12289;
        int c = m >> 31;
        return (short) (m ^ ((t ^ m) & c));
    }
}
