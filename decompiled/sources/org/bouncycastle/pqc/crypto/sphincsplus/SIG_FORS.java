package org.bouncycastle.pqc.crypto.sphincsplus;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/sphincsplus/SIG_FORS.class */
class SIG_FORS {
    final byte[][] authPath;
    final byte[] sk;

    SIG_FORS(byte[] bArr, byte[][] bArr2) {
        this.authPath = bArr2;
        this.sk = bArr;
    }

    byte[] getSK() {
        return this.sk;
    }

    public byte[][] getAuthPath() {
        return this.authPath;
    }
}
