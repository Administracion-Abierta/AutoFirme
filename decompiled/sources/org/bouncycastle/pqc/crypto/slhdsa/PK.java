package org.bouncycastle.pqc.crypto.slhdsa;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/slhdsa/PK.class */
class PK {
    final byte[] seed;
    final byte[] root;

    PK(byte[] bArr, byte[] bArr2) {
        this.seed = bArr;
        this.root = bArr2;
    }
}
