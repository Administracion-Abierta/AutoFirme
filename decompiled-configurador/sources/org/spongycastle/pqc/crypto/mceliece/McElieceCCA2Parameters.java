package org.spongycastle.pqc.crypto.mceliece;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/mceliece/McElieceCCA2Parameters.class */
public class McElieceCCA2Parameters extends McElieceParameters {
    private final String digest;

    public McElieceCCA2Parameters() {
        this(11, 50, "SHA-256");
    }

    public McElieceCCA2Parameters(String digest) {
        this(11, 50, digest);
    }

    public McElieceCCA2Parameters(int keysize) {
        this(keysize, "SHA-256");
    }

    public McElieceCCA2Parameters(int keysize, String digest) {
        super(keysize);
        this.digest = digest;
    }

    public McElieceCCA2Parameters(int m, int t) {
        this(m, t, "SHA-256");
    }

    public McElieceCCA2Parameters(int m, int t, String digest) {
        super(m, t);
        this.digest = digest;
    }

    public McElieceCCA2Parameters(int m, int t, int poly) {
        this(m, t, poly, "SHA-256");
    }

    public McElieceCCA2Parameters(int m, int t, int poly, String digest) {
        super(m, t, poly);
        this.digest = digest;
    }

    public String getDigest() {
        return this.digest;
    }
}
