package org.bouncycastle.pqc.crypto.mlkem;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/mlkem/Reduce.class */
class Reduce {
    Reduce() {
    }

    public static short montgomeryReduce(int i) {
        return (short) ((i - (((short) (i * MLKEMEngine.KyberQinv)) * MLKEMEngine.KyberQ)) >> 16);
    }

    public static short barretReduce(short s) {
        return (short) (s - ((short) (((short) ((((short) ((67108864 + 1664) / 3329)) * s) >> 26)) * MLKEMEngine.KyberQ)));
    }

    public static short conditionalSubQ(short s) {
        short s2 = (short) (s - MLKEMEngine.KyberQ);
        return (short) (s2 + ((s2 >> 15) & MLKEMEngine.KyberQ));
    }
}
