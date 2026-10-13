package org.spongycastle.math.raw;

import org.spongycastle.crypto.tls.CipherSuite;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/raw/Interleave.class */
public class Interleave {
    private static final long M32 = 1431655765;
    private static final long M64 = 6148914691236517205L;

    public static int expand8to16(int x) {
        int x2 = x & 255;
        int x3 = (x2 | (x2 << 4)) & 3855;
        int x4 = (x3 | (x3 << 2)) & 13107;
        return (x4 | (x4 << 1)) & 21845;
    }

    public static int expand16to32(int x) {
        int x2 = x & 65535;
        int x3 = (x2 | (x2 << 8)) & 16711935;
        int x4 = (x3 | (x3 << 4)) & 252645135;
        int x5 = (x4 | (x4 << 2)) & 858993459;
        return (x5 | (x5 << 1)) & 1431655765;
    }

    public static long expand32to64(int x) {
        int t = (x ^ (x >>> 8)) & CipherSuite.DRAFT_TLS_DHE_RSA_WITH_AES_128_OCB;
        int x2 = x ^ (t ^ (t << 8));
        int t2 = (x2 ^ (x2 >>> 4)) & 15728880;
        int x3 = x2 ^ (t2 ^ (t2 << 4));
        int t3 = (x3 ^ (x3 >>> 2)) & 202116108;
        int x4 = x3 ^ (t3 ^ (t3 << 2));
        int t4 = (x4 ^ (x4 >>> 1)) & 572662306;
        int x5 = x4 ^ (t4 ^ (t4 << 1));
        return ((((long) (x5 >>> 1)) & M32) << 32) | (((long) x5) & M32);
    }

    public static void expand64To128(long x, long[] z, int zOff) {
        long t = (x ^ (x >>> 16)) & 4294901760L;
        long x2 = x ^ (t ^ (t << 16));
        long t2 = (x2 ^ (x2 >>> 8)) & 280375465148160L;
        long x3 = x2 ^ (t2 ^ (t2 << 8));
        long t3 = (x3 ^ (x3 >>> 4)) & 67555025218437360L;
        long x4 = x3 ^ (t3 ^ (t3 << 4));
        long t4 = (x4 ^ (x4 >>> 2)) & 868082074056920076L;
        long x5 = x4 ^ (t4 ^ (t4 << 2));
        long t5 = (x5 ^ (x5 >>> 1)) & 2459565876494606882L;
        long x6 = x5 ^ (t5 ^ (t5 << 1));
        z[zOff] = x6 & M64;
        z[zOff + 1] = (x6 >>> 1) & M64;
    }

    public static long unshuffle(long x) {
        long t = (x ^ (x >>> 1)) & 2459565876494606882L;
        long x2 = x ^ (t ^ (t << 1));
        long t2 = (x2 ^ (x2 >>> 2)) & 868082074056920076L;
        long x3 = x2 ^ (t2 ^ (t2 << 2));
        long t3 = (x3 ^ (x3 >>> 4)) & 67555025218437360L;
        long x4 = x3 ^ (t3 ^ (t3 << 4));
        long t4 = (x4 ^ (x4 >>> 8)) & 280375465148160L;
        long x5 = x4 ^ (t4 ^ (t4 << 8));
        long t5 = (x5 ^ (x5 >>> 16)) & 4294901760L;
        return x5 ^ (t5 ^ (t5 << 16));
    }
}
