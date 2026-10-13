package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/AsconCXof128.class */
public class AsconCXof128 extends AsconBaseDigest implements Xof {
    private boolean m_squeezing;
    private final long z0;
    private final long z1;
    private final long z2;
    private final long z3;
    private final long z4;

    public AsconCXof128() {
        this(new byte[0], 0, 0);
    }

    public AsconCXof128(byte[] bArr) {
        this(bArr, 0, bArr.length);
    }

    public AsconCXof128(byte[] bArr, int i, int i2) {
        this.m_squeezing = false;
        if (i + i2 > bArr.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (i2 > 256) {
            throw new DataLengthException("customized string is too long");
        }
        initState(bArr, i, i2);
        this.z0 = this.x0;
        this.z1 = this.x1;
        this.z2 = this.x2;
        this.z3 = this.x3;
        this.z4 = this.x4;
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
        return "Ascon-CXOF128";
    }

    @Override // org.bouncycastle.crypto.Xof
    public int doOutput(byte[] bArr, int i, int i2) {
        if (32 + i > bArr.length) {
            throw new OutputLengthException("output buffer is too short");
        }
        padAndAbsorb();
        squeeze(bArr, i, i2);
        return i2;
    }

    @Override // org.bouncycastle.crypto.Xof
    public int doFinal(byte[] bArr, int i, int i2) {
        int iDoOutput = doOutput(bArr, i, i2);
        reset();
        return iDoOutput;
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest, org.bouncycastle.crypto.Digest
    public void reset() {
        super.reset();
        this.m_squeezing = false;
        this.x0 = this.z0;
        this.x1 = this.z1;
        this.x2 = this.z2;
        this.x3 = this.z3;
        this.x4 = this.z4;
    }

    private void initState(byte[] bArr, int i, int i2) {
        this.x0 = 7445901275803737603L;
        this.x1 = 4886737088792722364L;
        this.x2 = -1616759365661982283L;
        this.x3 = 3076320316797452470L;
        this.x4 = -8124743304765850554L;
        Pack.longToLittleEndian(((long) i2) << 3, this.m_buf, 0);
        p(12);
        update(bArr, i, i2);
        padAndAbsorb();
        this.m_squeezing = false;
    }

    @Override // org.bouncycastle.crypto.digests.AsconBaseDigest, org.bouncycastle.crypto.Digest
    public /* bridge */ /* synthetic */ int doFinal(byte[] bArr, int i) {
        return super.doFinal(bArr, i);
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
