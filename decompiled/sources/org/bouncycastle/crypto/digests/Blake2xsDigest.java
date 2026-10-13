package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/Blake2xsDigest.class */
public class Blake2xsDigest implements Xof {
    public static final int UNKNOWN_DIGEST_LENGTH = 65535;
    private static final int DIGEST_LENGTH = 32;
    private static final long MAX_NUMBER_BLOCKS = 4294967296L;
    private int digestLength;
    private Blake2sDigest hash;
    private byte[] h0;
    private byte[] buf;
    private int bufPos;
    private int digestPos;
    private long blockPos;
    private long nodeOffset;
    private final CryptoServicePurpose purpose;

    public Blake2xsDigest() {
        this(65535, CryptoServicePurpose.ANY);
    }

    public Blake2xsDigest(int i, CryptoServicePurpose cryptoServicePurpose) {
        this(i, null, null, null, cryptoServicePurpose);
    }

    public Blake2xsDigest(int i) {
        this(i, CryptoServicePurpose.ANY);
    }

    public Blake2xsDigest(int i, byte[] bArr) {
        this(i, bArr, null, null, CryptoServicePurpose.ANY);
    }

    public Blake2xsDigest(int i, byte[] bArr, byte[] bArr2, byte[] bArr3, CryptoServicePurpose cryptoServicePurpose) {
        this.h0 = null;
        this.buf = new byte[32];
        this.bufPos = 32;
        this.digestPos = 0;
        this.blockPos = 0L;
        if (i < 1 || i > 65535) {
            throw new IllegalArgumentException("BLAKE2xs digest length must be between 1 and 2^16-1");
        }
        this.digestLength = i;
        this.nodeOffset = computeNodeOffset();
        this.purpose = cryptoServicePurpose;
        this.hash = new Blake2sDigest(32, bArr, bArr2, bArr3, this.nodeOffset, cryptoServicePurpose);
    }

    public Blake2xsDigest(Blake2xsDigest blake2xsDigest) {
        this.h0 = null;
        this.buf = new byte[32];
        this.bufPos = 32;
        this.digestPos = 0;
        this.blockPos = 0L;
        this.digestLength = blake2xsDigest.digestLength;
        this.hash = new Blake2sDigest(blake2xsDigest.hash);
        this.h0 = Arrays.clone(blake2xsDigest.h0);
        this.buf = Arrays.clone(blake2xsDigest.buf);
        this.bufPos = blake2xsDigest.bufPos;
        this.digestPos = blake2xsDigest.digestPos;
        this.blockPos = blake2xsDigest.blockPos;
        this.nodeOffset = blake2xsDigest.nodeOffset;
        this.purpose = blake2xsDigest.purpose;
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "BLAKE2xs";
    }

    @Override // org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return this.digestLength;
    }

    @Override // org.bouncycastle.crypto.ExtendedDigest
    public int getByteLength() {
        return this.hash.getByteLength();
    }

    public long getUnknownMaxLength() {
        return 137438953472L;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte b) {
        this.hash.update(b);
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte[] bArr, int i, int i2) {
        this.hash.update(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.Digest
    public void reset() {
        this.hash.reset();
        this.h0 = null;
        this.bufPos = 32;
        this.digestPos = 0;
        this.blockPos = 0L;
        this.nodeOffset = computeNodeOffset();
    }

    @Override // org.bouncycastle.crypto.Digest
    public int doFinal(byte[] bArr, int i) {
        return doFinal(bArr, i, this.digestLength);
    }

    @Override // org.bouncycastle.crypto.Xof
    public int doFinal(byte[] bArr, int i, int i2) {
        int iDoOutput = doOutput(bArr, i, i2);
        reset();
        return iDoOutput;
    }

    @Override // org.bouncycastle.crypto.Xof
    public int doOutput(byte[] bArr, int i, int i2) {
        if (i > bArr.length - i2) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.h0 == null) {
            this.h0 = new byte[this.hash.getDigestSize()];
            this.hash.doFinal(this.h0, 0);
        }
        if (this.digestLength != 65535) {
            if (this.digestPos + i2 > this.digestLength) {
                throw new IllegalArgumentException("Output length is above the digest length");
            }
        } else if ((this.blockPos << 5) >= getUnknownMaxLength()) {
            throw new IllegalArgumentException("Maximum length is 2^32 blocks of 32 bytes");
        }
        for (int i3 = 0; i3 < i2; i3++) {
            if (this.bufPos >= 32) {
                Blake2sDigest blake2sDigest = new Blake2sDigest(computeStepLength(), 32, this.nodeOffset);
                blake2sDigest.update(this.h0, 0, this.h0.length);
                Arrays.fill(this.buf, (byte) 0);
                blake2sDigest.doFinal(this.buf, 0);
                this.bufPos = 0;
                this.nodeOffset++;
                this.blockPos++;
            }
            bArr[i + i3] = this.buf[this.bufPos];
            this.bufPos++;
            this.digestPos++;
        }
        return i2;
    }

    private int computeStepLength() {
        if (this.digestLength == 65535) {
            return 32;
        }
        return Math.min(32, this.digestLength - this.digestPos);
    }

    private long computeNodeOffset() {
        return ((long) this.digestLength) * MAX_NUMBER_BLOCKS;
    }
}
