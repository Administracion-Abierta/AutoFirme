package org.spongycastle.pqc.crypto.sphincs;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/sphincs/Wots.class */
class Wots {
    static final int WOTS_LOGW = 4;
    static final int WOTS_W = 16;
    static final int WOTS_L1 = 64;
    static final int WOTS_L = 67;
    static final int WOTS_LOG_L = 7;
    static final int WOTS_SIGBYTES = 2144;

    Wots() {
    }

    static void expand_seed(byte[] outseeds, int outOff, byte[] inseed, int inOff) {
        clear(outseeds, outOff, 2144);
        Seed.prg(outseeds, outOff, 2144L, inseed, inOff);
    }

    private static void clear(byte[] bytes, int offSet, int length) {
        for (int i = 0; i != length; i++) {
            bytes[i + offSet] = 0;
        }
    }

    static void gen_chain(HashFunctions hs, byte[] out, int outOff, byte[] seed, int seedOff, byte[] masks, int masksOff, int chainlen) {
        for (int j = 0; j < 32; j++) {
            out[j + outOff] = seed[j + seedOff];
        }
        for (int i = 0; i < chainlen && i < 16; i++) {
            hs.hash_n_n_mask(out, outOff, out, outOff, masks, masksOff + (i * 32));
        }
    }

    void wots_pkgen(HashFunctions hs, byte[] pk, int pkOff, byte[] sk, int skOff, byte[] masks, int masksOff) {
        expand_seed(pk, pkOff, sk, skOff);
        for (int i = 0; i < 67; i++) {
            gen_chain(hs, pk, pkOff + (i * 32), pk, pkOff + (i * 32), masks, masksOff, 15);
        }
    }

    void wots_sign(HashFunctions hs, byte[] sig, int sigOff, byte[] msg, byte[] sk, byte[] masks) {
        int[] basew = new int[67];
        int c = 0;
        int i = 0;
        while (i < 64) {
            basew[i] = msg[i / 2] & 15;
            basew[i + 1] = (msg[i / 2] & 255) >>> 4;
            c = c + (15 - basew[i]) + (15 - basew[i + 1]);
            i += 2;
        }
        while (i < 67) {
            basew[i] = c & 15;
            c >>>= 4;
            i++;
        }
        expand_seed(sig, sigOff, sk, 0);
        for (int i2 = 0; i2 < 67; i2++) {
            gen_chain(hs, sig, sigOff + (i2 * 32), sig, sigOff + (i2 * 32), masks, 0, basew[i2]);
        }
    }

    void wots_verify(HashFunctions hs, byte[] pk, byte[] sig, int sigOff, byte[] msg, byte[] masks) {
        int[] basew = new int[67];
        int c = 0;
        int i = 0;
        while (i < 64) {
            basew[i] = msg[i / 2] & 15;
            basew[i + 1] = (msg[i / 2] & 255) >>> 4;
            c = c + (15 - basew[i]) + (15 - basew[i + 1]);
            i += 2;
        }
        while (i < 67) {
            basew[i] = c & 15;
            c >>>= 4;
            i++;
        }
        for (int i2 = 0; i2 < 67; i2++) {
            gen_chain(hs, pk, i2 * 32, sig, sigOff + (i2 * 32), masks, basew[i2] * 32, 15 - basew[i2]);
        }
    }
}
