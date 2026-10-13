package org.bouncycastle.pqc.crypto.slhdsa;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/slhdsa/SK.class */
class SK {
    final byte[] seed;
    final byte[] prf;

    SK(byte[] bArr, byte[] bArr2) {
        this.seed = bArr;
        this.prf = bArr2;
    }
}
