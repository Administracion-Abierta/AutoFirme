package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Longs;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/AsconBaseDigest.class */
abstract class AsconBaseDigest implements ExtendedDigest {
    protected long x0;
    protected long x1;
    protected long x2;
    protected long x3;
    protected long x4;
    protected final int CRYPTO_BYTES = 32;
    protected final int ASCON_HASH_RATE = 8;
    protected int ASCON_PB_ROUNDS = 12;
    protected final byte[] m_buf = new byte[8];
    protected int m_bufPos = 0;

    AsconBaseDigest() {
    }

    private void round(long j) {
        long j2 = ((((this.x0 ^ this.x1) ^ this.x2) ^ this.x3) ^ j) ^ (this.x1 & (((this.x0 ^ this.x2) ^ this.x4) ^ j));
        long j3 = ((((this.x0 ^ this.x2) ^ this.x3) ^ this.x4) ^ j) ^ (((this.x1 ^ this.x2) ^ j) & (this.x1 ^ this.x3));
        long j4 = (((this.x1 ^ this.x2) ^ this.x4) ^ j) ^ (this.x3 & this.x4);
        long j5 = (((this.x0 ^ this.x1) ^ this.x2) ^ j) ^ ((this.x0 ^ (-1)) & (this.x3 ^ this.x4));
        long j6 = ((this.x1 ^ this.x3) ^ this.x4) ^ ((this.x0 ^ this.x4) & this.x1);
        this.x0 = (j2 ^ Longs.rotateRight(j2, 19)) ^ Longs.rotateRight(j2, 28);
        this.x1 = (j3 ^ Longs.rotateRight(j3, 39)) ^ Longs.rotateRight(j3, 61);
        this.x2 = ((j4 ^ Longs.rotateRight(j4, 1)) ^ Longs.rotateRight(j4, 6)) ^ (-1);
        this.x3 = (j5 ^ Longs.rotateRight(j5, 10)) ^ Longs.rotateRight(j5, 17);
        this.x4 = (j6 ^ Longs.rotateRight(j6, 7)) ^ Longs.rotateRight(j6, 41);
    }

    protected void p(int i) {
        if (i == 12) {
            round(240L);
            round(225L);
            round(210L);
            round(195L);
        }
        if (i >= 8) {
            round(180L);
            round(165L);
        }
        round(150L);
        round(135L);
        round(120L);
        round(105L);
        round(90L);
        round(75L);
    }

    protected abstract long pad(int i);

    protected abstract long loadBytes(byte[] bArr, int i);

    protected abstract long loadBytes(byte[] bArr, int i, int i2);

    protected abstract void setBytes(long j, byte[] bArr, int i);

    protected abstract void setBytes(long j, byte[] bArr, int i, int i2);

    @Override // org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return 32;
    }

    @Override // org.bouncycastle.crypto.ExtendedDigest
    public int getByteLength() {
        return 8;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte b) {
        this.m_buf[this.m_bufPos] = b;
        int i = this.m_bufPos + 1;
        this.m_bufPos = i;
        if (i == 8) {
            this.x0 ^= loadBytes(this.m_buf, 0);
            p(this.ASCON_PB_ROUNDS);
            this.m_bufPos = 0;
        }
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte[] bArr, int i, int i2) {
        if (i + i2 > bArr.length) {
            throw new DataLengthException("input buffer too short");
        }
        int i3 = 8 - this.m_bufPos;
        if (i2 < i3) {
            System.arraycopy(bArr, i, this.m_buf, this.m_bufPos, i2);
            this.m_bufPos += i2;
            return;
        }
        int i4 = 0;
        if (this.m_bufPos > 0) {
            System.arraycopy(bArr, i, this.m_buf, this.m_bufPos, i3);
            i4 = 0 + i3;
            this.x0 ^= loadBytes(this.m_buf, 0);
            p(this.ASCON_PB_ROUNDS);
        }
        while (true) {
            int i5 = i2 - i4;
            if (i5 < 8) {
                System.arraycopy(bArr, i + i4, this.m_buf, 0, i5);
                this.m_bufPos = i5;
                return;
            } else {
                this.x0 ^= loadBytes(bArr, i + i4);
                p(this.ASCON_PB_ROUNDS);
                i4 += 8;
            }
        }
    }

    @Override // org.bouncycastle.crypto.Digest
    public int doFinal(byte[] bArr, int i) {
        return hash(bArr, i, 32);
    }

    protected void padAndAbsorb() {
        this.x0 ^= loadBytes(this.m_buf, 0, this.m_bufPos);
        this.x0 ^= pad(this.m_bufPos);
        p(12);
    }

    protected void squeeze(byte[] bArr, int i, int i2) {
        while (i2 > 8) {
            setBytes(this.x0, bArr, i);
            p(this.ASCON_PB_ROUNDS);
            i += 8;
            i2 -= 8;
        }
        setBytes(this.x0, bArr, i, i2);
        reset();
    }

    protected int hash(byte[] bArr, int i, int i2) {
        if (32 + i > bArr.length) {
            throw new OutputLengthException("output buffer is too short");
        }
        padAndAbsorb();
        squeeze(bArr, i, i2);
        return i2;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void reset() {
        Arrays.clear(this.m_buf);
        this.m_bufPos = 0;
    }
}
