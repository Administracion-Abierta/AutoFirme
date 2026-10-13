package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/AsconDigest.class */
public class AsconDigest extends AsconBaseDigest {
    AsconParameters asconParameters;
    private final String algorithmName;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/AsconDigest$AsconParameters.class */
    public enum AsconParameters {
        AsconHash,
        AsconHashA
    }

    public AsconDigest(AsconParameters asconParameters) {
        this.asconParameters = asconParameters;
        switch (asconParameters) {
            case AsconHash:
                this.ASCON_PB_ROUNDS = 12;
                this.algorithmName = "Ascon-Hash";
                break;
            case AsconHashA:
                this.ASCON_PB_ROUNDS = 8;
                this.algorithmName = "Ascon-HashA";
                break;
            default:
                throw new IllegalArgumentException("Invalid parameter settings for Ascon Hash");
        }
        reset();
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest
    protected long pad(int i) {
        return 128 << (56 - (i << 3));
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest
    protected long loadBytes(byte[] bArr, int i) {
        return Pack.bigEndianToLong(bArr, i);
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest
    protected long loadBytes(byte[] bArr, int i, int i2) {
        return Pack.bigEndianToLong(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest
    protected void setBytes(long j, byte[] bArr, int i) {
        Pack.longToBigEndian(j, bArr, i);
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest
    protected void setBytes(long j, byte[] bArr, int i, int i2) {
        Pack.longToBigEndian(j, bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return this.algorithmName;
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest, org.bouncycastle.crypto.Digest
    public void reset() {
        super.reset();
        switch (this.asconParameters) {
            case AsconHash:
                this.x0 = -1255492011513352131L;
                this.x1 = -8380609354527731710L;
                this.x2 = -5437372128236807582L;
                this.x3 = 4834782570098516968L;
                this.x4 = 3787428097924915520L;
                break;
            case AsconHashA:
                this.x0 = 92044056785660070L;
                this.x1 = 8326807761760157607L;
                this.x2 = 3371194088139667532L;
                this.x3 = -2956994353054992515L;
                this.x4 = -6828509670848688761L;
                break;
        }
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest, org.bouncycastle.crypto.Digest
    public /* bridge */ /* synthetic */ int doFinal(byte[] bArr, int i) {
        return super.doFinal(bArr, i);
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest, org.bouncycastle.crypto.Digest
    public /* bridge */ /* synthetic */ void update(byte[] bArr, int i, int i2) {
        super.update(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest, org.bouncycastle.crypto.Digest
    public /* bridge */ /* synthetic */ void update(byte b) {
        super.update(b);
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest, org.bouncycastle.crypto.ExtendedDigest
    public /* bridge */ /* synthetic */ int getByteLength() {
        return super.getByteLength();
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest, org.bouncycastle.crypto.Digest
    public /* bridge */ /* synthetic */ int getDigestSize() {
        return super.getDigestSize();
    }
}
