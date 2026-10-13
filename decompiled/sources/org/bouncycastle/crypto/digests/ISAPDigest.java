package org.bouncycastle.crypto.digests;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/ISAPDigest.class */
public class ISAPDigest implements Digest {
    private long x0;
    private long x1;
    private long x2;
    private long x3;
    private long x4;
    private long t0;
    private long t1;
    private long t2;
    private long t3;
    private long t4;
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    private void ROUND(long j) {
        this.t0 = ((((this.x0 ^ this.x1) ^ this.x2) ^ this.x3) ^ j) ^ (this.x1 & (((this.x0 ^ this.x2) ^ this.x4) ^ j));
        this.t1 = ((((this.x0 ^ this.x2) ^ this.x3) ^ this.x4) ^ j) ^ (((this.x1 ^ this.x2) ^ j) & (this.x1 ^ this.x3));
        this.t2 = (((this.x1 ^ this.x2) ^ this.x4) ^ j) ^ (this.x3 & this.x4);
        this.t3 = (((this.x0 ^ this.x1) ^ this.x2) ^ j) ^ ((this.x0 ^ (-1)) & (this.x3 ^ this.x4));
        this.t4 = ((this.x1 ^ this.x3) ^ this.x4) ^ ((this.x0 ^ this.x4) & this.x1);
        this.x0 = (this.t0 ^ ROTR(this.t0, 19L)) ^ ROTR(this.t0, 28L);
        this.x1 = (this.t1 ^ ROTR(this.t1, 39L)) ^ ROTR(this.t1, 61L);
        this.x2 = ((this.t2 ^ ROTR(this.t2, 1L)) ^ ROTR(this.t2, 6L)) ^ (-1);
        this.x3 = (this.t3 ^ ROTR(this.t3, 10L)) ^ ROTR(this.t3, 17L);
        this.x4 = (this.t4 ^ ROTR(this.t4, 7L)) ^ ROTR(this.t4, 41L);
    }

    private void P12() {
        ROUND(240L);
        ROUND(225L);
        ROUND(210L);
        ROUND(195L);
        ROUND(180L);
        ROUND(165L);
        ROUND(150L);
        ROUND(135L);
        ROUND(120L);
        ROUND(105L);
        ROUND(90L);
        ROUND(75L);
    }

    private long ROTR(long j, long j2) {
        return (j >>> ((int) j2)) | (j << ((int) (64 - j2)));
    }

    protected long U64BIG(long j) {
        return (ROTR(j, 8L) & (-72057589759737856L)) | (ROTR(j, 24L) & 71776119077928960L) | (ROTR(j, 40L) & 280375465148160L) | (ROTR(j, 56L) & 1095216660735L);
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "ISAP Hash";
    }

    @Override // org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return 32;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte b) {
        this.buffer.write(b);
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte[] bArr, int i, int i2) {
        if (i + i2 > bArr.length) {
            throw new DataLengthException("input buffer too short");
        }
        this.buffer.write(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.Digest
    public int doFinal(byte[] bArr, int i) {
        if (32 + i > bArr.length) {
            throw new OutputLengthException("output buffer is too short");
        }
        this.t4 = 0L;
        this.t3 = 0L;
        this.t2 = 0L;
        this.t1 = 0L;
        this.t0 = 0L;
        this.x0 = -1255492011513352131L;
        this.x1 = -8380609354527731710L;
        this.x2 = -5437372128236807582L;
        this.x3 = 4834782570098516968L;
        this.x4 = 3787428097924915520L;
        byte[] byteArray = this.buffer.toByteArray();
        int length = byteArray.length;
        long[] jArr = new long[length >> 3];
        Pack.littleEndianToLong(byteArray, 0, jArr, 0, jArr.length);
        int i2 = 0;
        while (length >= 8) {
            int i3 = i2;
            i2++;
            this.x0 ^= U64BIG(jArr[i3]);
            P12();
            length -= 8;
        }
        this.x0 ^= 128 << ((7 - length) << 3);
        while (length > 0) {
            length--;
            this.x0 ^= (((long) byteArray[(i2 << 3) + length]) & 255) << ((7 - length) << 3);
        }
        P12();
        long[] jArr2 = new long[4];
        int i4 = 0;
        while (i4 < 3) {
            jArr2[i4] = U64BIG(this.x0);
            P12();
            i4++;
        }
        jArr2[i4] = U64BIG(this.x0);
        Pack.longToLittleEndian(jArr2, bArr, i);
        this.buffer.reset();
        return 32;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void reset() {
        this.buffer.reset();
    }
}
