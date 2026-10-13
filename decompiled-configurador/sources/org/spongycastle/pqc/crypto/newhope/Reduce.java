package org.spongycastle.pqc.crypto.newhope;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/newhope/Reduce.class */
class Reduce {
    static final int QInv = 12287;
    static final int RLog = 18;
    static final int RMask = 262143;

    Reduce() {
    }

    static short montgomery(int a) {
        int u = a * QInv;
        return (short) ((((u & RMask) * 12289) + a) >>> 18);
    }

    static short barrett(short a) {
        int t = a & 65535;
        int u = (t * 5) >>> 16;
        return (short) (t - (u * 12289));
    }
}
