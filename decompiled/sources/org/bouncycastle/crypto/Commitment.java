package org.bouncycastle.crypto;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/Commitment.class */
public class Commitment {
    private final byte[] secret;
    private final byte[] commitment;

    public Commitment(byte[] bArr, byte[] bArr2) {
        this.secret = bArr;
        this.commitment = bArr2;
    }

    public byte[] getSecret() {
        return this.secret;
    }

    public byte[] getCommitment() {
        return this.commitment;
    }
}
