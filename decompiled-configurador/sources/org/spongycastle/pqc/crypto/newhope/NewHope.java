package org.spongycastle.pqc.crypto.newhope;

import java.security.SecureRandom;
import org.spongycastle.crypto.digests.SHA3Digest;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/newhope/NewHope.class */
class NewHope {
    private static final boolean STATISTICAL_TEST = false;
    public static final int AGREEMENT_SIZE = 32;
    public static final int POLY_SIZE = 1024;
    public static final int SENDA_BYTES = 1824;
    public static final int SENDB_BYTES = 2048;

    NewHope() {
    }

    public static void keygen(SecureRandom rand, byte[] send, short[] sk) {
        byte[] seed = new byte[32];
        rand.nextBytes(seed);
        short[] a = new short[1024];
        generateA(a, seed);
        byte[] noiseSeed = new byte[32];
        rand.nextBytes(noiseSeed);
        Poly.getNoise(sk, noiseSeed, (byte) 0);
        Poly.toNTT(sk);
        short[] e = new short[1024];
        Poly.getNoise(e, noiseSeed, (byte) 1);
        Poly.toNTT(e);
        short[] r = new short[1024];
        Poly.pointWise(a, sk, r);
        short[] pk = new short[1024];
        Poly.add(r, e, pk);
        encodeA(send, pk, seed);
    }

    public static void sharedB(SecureRandom rand, byte[] sharedKey, byte[] send, byte[] received) {
        short[] pkA = new short[1024];
        byte[] seed = new byte[32];
        decodeA(pkA, seed, received);
        short[] a = new short[1024];
        generateA(a, seed);
        byte[] noiseSeed = new byte[32];
        rand.nextBytes(noiseSeed);
        short[] sp = new short[1024];
        Poly.getNoise(sp, noiseSeed, (byte) 0);
        Poly.toNTT(sp);
        short[] ep = new short[1024];
        Poly.getNoise(ep, noiseSeed, (byte) 1);
        Poly.toNTT(ep);
        short[] bp = new short[1024];
        Poly.pointWise(a, sp, bp);
        Poly.add(bp, ep, bp);
        short[] v = new short[1024];
        Poly.pointWise(pkA, sp, v);
        Poly.fromNTT(v);
        short[] epp = new short[1024];
        Poly.getNoise(epp, noiseSeed, (byte) 2);
        Poly.add(v, epp, v);
        short[] c = new short[1024];
        ErrorCorrection.helpRec(c, v, noiseSeed, (byte) 3);
        encodeB(send, bp, c);
        ErrorCorrection.rec(sharedKey, v, c);
        sha3(sharedKey);
    }

    public static void sharedA(byte[] sharedKey, short[] sk, byte[] received) {
        short[] bp = new short[1024];
        short[] c = new short[1024];
        decodeB(bp, c, received);
        short[] v = new short[1024];
        Poly.pointWise(sk, bp, v);
        Poly.fromNTT(v);
        ErrorCorrection.rec(sharedKey, v, c);
        sha3(sharedKey);
    }

    static void decodeA(short[] pk, byte[] seed, byte[] r) {
        Poly.fromBytes(pk, r);
        System.arraycopy(r, 1792, seed, 0, 32);
    }

    static void decodeB(short[] b, short[] c, byte[] r) {
        Poly.fromBytes(b, r);
        for (int i = 0; i < 256; i++) {
            int j = 4 * i;
            int ri = r[1792 + i] & 255;
            c[j + 0] = (short) (ri & 3);
            c[j + 1] = (short) ((ri >>> 2) & 3);
            c[j + 2] = (short) ((ri >>> 4) & 3);
            c[j + 3] = (short) (ri >>> 6);
        }
    }

    static void encodeA(byte[] r, short[] pk, byte[] seed) {
        Poly.toBytes(r, pk);
        System.arraycopy(seed, 0, r, 1792, 32);
    }

    static void encodeB(byte[] r, short[] b, short[] c) {
        Poly.toBytes(r, b);
        for (int i = 0; i < 256; i++) {
            int j = 4 * i;
            r[1792 + i] = (byte) (c[j] | (c[j + 1] << 2) | (c[j + 2] << 4) | (c[j + 3] << 6));
        }
    }

    static void generateA(short[] a, byte[] seed) {
        Poly.uniform(a, seed);
    }

    static void sha3(byte[] sharedKey) {
        SHA3Digest d = new SHA3Digest(256);
        d.update(sharedKey, 0, 32);
        d.doFinal(sharedKey, 0);
    }
}
