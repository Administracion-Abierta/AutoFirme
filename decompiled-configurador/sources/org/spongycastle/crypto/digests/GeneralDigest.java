package org.spongycastle.crypto.digests;

import org.spongycastle.crypto.ExtendedDigest;
import org.spongycastle.util.Memoable;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/digests/GeneralDigest.class */
public abstract class GeneralDigest implements ExtendedDigest, Memoable {
    private static final int BYTE_LENGTH = 64;
    private final byte[] xBuf;
    private int xBufOff;
    private long byteCount;

    protected abstract void processWord(byte[] bArr, int i);

    protected abstract void processLength(long j);

    protected abstract void processBlock();

    protected GeneralDigest() {
        this.xBuf = new byte[4];
        this.xBufOff = 0;
    }

    protected GeneralDigest(GeneralDigest t) {
        this.xBuf = new byte[4];
        copyIn(t);
    }

    protected GeneralDigest(byte[] encodedState) {
        this.xBuf = new byte[4];
        System.arraycopy(encodedState, 0, this.xBuf, 0, this.xBuf.length);
        this.xBufOff = Pack.bigEndianToInt(encodedState, 4);
        this.byteCount = Pack.bigEndianToLong(encodedState, 8);
    }

    protected void copyIn(GeneralDigest t) {
        System.arraycopy(t.xBuf, 0, this.xBuf, 0, t.xBuf.length);
        this.xBufOff = t.xBufOff;
        this.byteCount = t.byteCount;
    }

    @Override // org.spongycastle.crypto.Digest
    public void update(byte in) {
        byte[] bArr = this.xBuf;
        int i = this.xBufOff;
        this.xBufOff = i + 1;
        bArr[i] = in;
        if (this.xBufOff == this.xBuf.length) {
            processWord(this.xBuf, 0);
            this.xBufOff = 0;
        }
        this.byteCount++;
    }

    @Override // org.spongycastle.crypto.Digest
    public void update(byte[] in, int inOff, int len) {
        int len2 = Math.max(0, len);
        int i = 0;
        if (this.xBufOff != 0) {
            while (i < len2) {
                byte[] bArr = this.xBuf;
                int i2 = this.xBufOff;
                this.xBufOff = i2 + 1;
                int i3 = i;
                i++;
                bArr[i2] = in[inOff + i3];
                if (this.xBufOff == 4) {
                    processWord(this.xBuf, 0);
                    this.xBufOff = 0;
                    break;
                }
            }
        }
        int limit = ((len2 - i) & (-4)) + i;
        while (i < limit) {
            processWord(in, inOff + i);
            i += 4;
        }
        while (i < len2) {
            byte[] bArr2 = this.xBuf;
            int i4 = this.xBufOff;
            this.xBufOff = i4 + 1;
            int i5 = i;
            i++;
            bArr2[i4] = in[inOff + i5];
        }
        this.byteCount += (long) len2;
    }

    public void finish() {
        long bitLength = this.byteCount << 3;
        update((byte) -128);
        while (this.xBufOff != 0) {
            update((byte) 0);
        }
        processLength(bitLength);
        processBlock();
    }

    @Override // org.spongycastle.crypto.Digest
    public void reset() {
        this.byteCount = 0L;
        this.xBufOff = 0;
        for (int i = 0; i < this.xBuf.length; i++) {
            this.xBuf[i] = 0;
        }
    }

    protected void populateState(byte[] state) {
        System.arraycopy(this.xBuf, 0, state, 0, this.xBufOff);
        Pack.intToBigEndian(this.xBufOff, state, 4);
        Pack.longToBigEndian(this.byteCount, state, 8);
    }

    @Override // org.spongycastle.crypto.ExtendedDigest
    public int getByteLength() {
        return 64;
    }
}
