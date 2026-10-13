package org.bouncycastle.pqc.crypto.crystals.dilithium;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/crystals/dilithium/Reduce.class */
class Reduce {
    Reduce() {
    }

    static int montgomeryReduce(long j) {
        return (int) ((j - (((long) ((int) (j * 58728449))) * 8380417)) >>> 32);
    }

    static int reduce32(int i) {
        return i - (((i + 4194304) >> 23) * 8380417);
    }

    static int conditionalAddQ(int i) {
        return i + ((i >> 31) & 8380417);
    }
}
