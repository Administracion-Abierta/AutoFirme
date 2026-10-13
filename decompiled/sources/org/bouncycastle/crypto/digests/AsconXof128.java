package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.Xof;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/AsconXof128.class */
public class AsconXof128 extends AsconBaseDigest implements Xof {
    private boolean m_squeezing = false;

    public AsconXof128() {
        reset();
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest
    protected long pad(int i) {
        return 1 << (i << 3);
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest
    protected long loadBytes(byte[] bArr, int i) {
        return Pack.littleEndianToLong(bArr, i);
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest
    protected long loadBytes(byte[] bArr, int i, int i2) {
        return Pack.littleEndianToLong(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest
    protected void setBytes(long j, byte[] bArr, int i) {
        Pack.longToLittleEndian(j, bArr, i);
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest
    protected void setBytes(long j, byte[] bArr, int i, int i2) {
        Pack.longToLittleEndian(j, bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest
    protected void padAndAbsorb() {
        this.m_squeezing = true;
        super.padAndAbsorb();
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "Ascon-XOF-128";
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest, org.bouncycastle.crypto.Digest
    public void update(byte b) {
        if (this.m_squeezing) {
            throw new IllegalArgumentException("attempt to absorb while squeezing");
        }
        super.update(b);
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest, org.bouncycastle.crypto.Digest
    public void update(byte[] bArr, int i, int i2) {
        if (this.m_squeezing) {
            throw new IllegalArgumentException("attempt to absorb while squeezing");
        }
        super.update(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.Xof
    public int doOutput(byte[] bArr, int i, int i2) {
        return hash(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.Xof
    public int doFinal(byte[] bArr, int i, int i2) {
        int iDoOutput = doOutput(bArr, i, i2);
        reset();
        return iDoOutput;
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest, org.bouncycastle.crypto.ExtendedDigest
    public int getByteLength() {
        return 8;
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest, org.bouncycastle.crypto.Digest
    public void reset() {
        this.m_squeezing = false;
        super.reset();
        this.x0 = -2701369817892108309L;
        this.x1 = -3711838248891385495L;
        this.x2 = -1778763697082575311L;
        this.x3 = 1072114354614917324L;
        this.x4 = -2282070310009238562L;
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest, org.bouncycastle.crypto.Digest
    public /* bridge */ /* synthetic */ int doFinal(byte[] bArr, int i) {
        return super.doFinal(bArr, i);
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest, org.bouncycastle.crypto.Digest
    public /* bridge */ /* synthetic */ int getDigestSize() {
        return super.getDigestSize();
    }
}
