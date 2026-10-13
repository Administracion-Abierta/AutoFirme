package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/GeneralDigest.class */
public abstract class GeneralDigest implements ExtendedDigest, Memoable {
    private static final int BYTE_LENGTH = 64;
    protected final CryptoServicePurpose purpose;
    private final byte[] xBuf;
    private int xBufOff;
    private long byteCount;

    protected GeneralDigest() {
        this(CryptoServicePurpose.ANY);
    }

    protected GeneralDigest(CryptoServicePurpose cryptoServicePurpose) {
        this.xBuf = new byte[4];
        this.purpose = cryptoServicePurpose;
        this.xBufOff = 0;
    }

    protected GeneralDigest(GeneralDigest generalDigest) {
        this.xBuf = new byte[4];
        this.purpose = generalDigest.purpose;
        copyIn(generalDigest);
    }

    protected GeneralDigest(byte[] bArr) {
        this.xBuf = new byte[4];
        this.purpose = CryptoServicePurpose.values()[bArr[bArr.length - 1]];
        System.arraycopy(bArr, 0, this.xBuf, 0, this.xBuf.length);
        this.xBufOff = Pack.bigEndianToInt(bArr, 4);
        this.byteCount = Pack.bigEndianToLong(bArr, 8);
    }

    protected void copyIn(GeneralDigest generalDigest) {
        System.arraycopy(generalDigest.xBuf, 0, this.xBuf, 0, generalDigest.xBuf.length);
        this.xBufOff = generalDigest.xBufOff;
        this.byteCount = generalDigest.byteCount;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte b) {
        byte[] bArr = this.xBuf;
        int i = this.xBufOff;
        this.xBufOff = i + 1;
        bArr[i] = b;
        if (this.xBufOff == this.xBuf.length) {
            processWord(this.xBuf, 0);
            this.xBufOff = 0;
        }
        this.byteCount++;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte[] bArr, int i, int i2) {
        int iMax = Math.max(0, i2);
        int i3 = 0;
        if (this.xBufOff != 0) {
            while (i3 < iMax) {
                byte[] bArr2 = this.xBuf;
                int i4 = this.xBufOff;
                this.xBufOff = i4 + 1;
                int i5 = i3;
                i3++;
                bArr2[i4] = bArr[i + i5];
                if (this.xBufOff == 4) {
                    processWord(this.xBuf, 0);
                    this.xBufOff = 0;
                    break;
                }
            }
        }
        int i6 = iMax - 3;
        while (i3 < i6) {
            processWord(bArr, i + i3);
            i3 += 4;
        }
        while (i3 < iMax) {
            byte[] bArr3 = this.xBuf;
            int i7 = this.xBufOff;
            this.xBufOff = i7 + 1;
            int i8 = i3;
            i3++;
            bArr3[i7] = bArr[i + i8];
        }
        this.byteCount += (long) iMax;
    }

    public void finish() {
        long j = this.byteCount << 3;
        update((byte) -128);
        while (this.xBufOff != 0) {
            update((byte) 0);
        }
        processLength(j);
        processBlock();
    }

    @Override // org.bouncycastle.crypto.Digest
    public void reset() {
        this.byteCount = 0L;
        this.xBufOff = 0;
        for (int i = 0; i < this.xBuf.length; i++) {
            this.xBuf[i] = 0;
        }
    }

    protected void populateState(byte[] bArr) {
        System.arraycopy(this.xBuf, 0, bArr, 0, this.xBufOff);
        Pack.intToBigEndian(this.xBufOff, bArr, 4);
        Pack.longToBigEndian(this.byteCount, bArr, 8);
    }

    @Override // org.bouncycastle.crypto.ExtendedDigest
    public int getByteLength() {
        return 64;
    }

    protected abstract void processWord(byte[] bArr, int i);

    protected abstract void processLength(long j);

    protected abstract void processBlock();

    protected abstract CryptoServiceProperties cryptoServiceProperties();
}
