package org.bouncycastle.pqc.crypto.ntru;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/ntru/OWCPADecryptResult.class */
class OWCPADecryptResult {
    final byte[] rm;
    final int fail;

    public OWCPADecryptResult(byte[] bArr, int i) {
        this.rm = bArr;
        this.fail = i;
    }
}
