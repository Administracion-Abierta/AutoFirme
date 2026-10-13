package org.spongycastle.crypto;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/Commitment.class */
public class Commitment {
    private final byte[] secret;
    private final byte[] commitment;

    public Commitment(byte[] secret, byte[] commitment) {
        this.secret = secret;
        this.commitment = commitment;
    }

    public byte[] getSecret() {
        return this.secret;
    }

    public byte[] getCommitment() {
        return this.commitment;
    }
}
