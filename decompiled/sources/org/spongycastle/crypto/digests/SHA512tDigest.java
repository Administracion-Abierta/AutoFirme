package org.spongycastle.crypto.digests;

import org.spongycastle.util.Memoable;
import org.spongycastle.util.MemoableResetException;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/digests/SHA512tDigest.class */
public class SHA512tDigest extends LongDigest {
    private int digestLength;
    private long H1t;
    private long H2t;
    private long H3t;
    private long H4t;
    private long H5t;
    private long H6t;
    private long H7t;
    private long H8t;

    public SHA512tDigest(int bitLength) {
        if (bitLength >= 512) {
            throw new IllegalArgumentException("bitLength cannot be >= 512");
        }
        if (bitLength % 8 != 0) {
            throw new IllegalArgumentException("bitLength needs to be a multiple of 8");
        }
        if (bitLength == 384) {
            throw new IllegalArgumentException("bitLength cannot be 384 use SHA384 instead");
        }
        this.digestLength = bitLength / 8;
        tIvGenerate(this.digestLength * 8);
        reset();
    }

    public SHA512tDigest(SHA512tDigest t) {
        super(t);
        this.digestLength = t.digestLength;
        reset(t);
    }

    public SHA512tDigest(byte[] encodedState) {
        this(readDigestLength(encodedState));
        restoreState(encodedState);
    }

    private static int readDigestLength(byte[] encodedState) {
        return Pack.bigEndianToInt(encodedState, encodedState.length - 4);
    }

    @Override // org.spongycastle.crypto.Digest
    public String getAlgorithmName() {
        return "SHA-512/" + Integer.toString(this.digestLength * 8);
    }

    @Override // org.spongycastle.crypto.Digest
    public int getDigestSize() {
        return this.digestLength;
    }

    @Override // org.spongycastle.crypto.Digest
    public int doFinal(byte[] out, int outOff) {
        finish();
        longToBigEndian(this.H1, out, outOff, this.digestLength);
        longToBigEndian(this.H2, out, outOff + 8, this.digestLength - 8);
        longToBigEndian(this.H3, out, outOff + 16, this.digestLength - 16);
        longToBigEndian(this.H4, out, outOff + 24, this.digestLength - 24);
        longToBigEndian(this.H5, out, outOff + 32, this.digestLength - 32);
        longToBigEndian(this.H6, out, outOff + 40, this.digestLength - 40);
        longToBigEndian(this.H7, out, outOff + 48, this.digestLength - 48);
        longToBigEndian(this.H8, out, outOff + 56, this.digestLength - 56);
        reset();
        return this.digestLength;
    }

    @Override // org.spongycastle.crypto.digests.LongDigest, org.spongycastle.crypto.Digest
    public void reset() {
        super.reset();
        this.H1 = this.H1t;
        this.H2 = this.H2t;
        this.H3 = this.H3t;
        this.H4 = this.H4t;
        this.H5 = this.H5t;
        this.H6 = this.H6t;
        this.H7 = this.H7t;
        this.H8 = this.H8t;
    }

    private void tIvGenerate(int bitLength) {
        this.H1 = -3482333909917012819L;
        this.H2 = 2216346199247487646L;
        this.H3 = -7364697282686394994L;
        this.H4 = 65953792586715988L;
        this.H5 = -816286391624063116L;
        this.H6 = 4512832404995164602L;
        this.H7 = -5033199132376557362L;
        this.H8 = -124578254951840548L;
        update((byte) 83);
        update((byte) 72);
        update((byte) 65);
        update((byte) 45);
        update((byte) 53);
        update((byte) 49);
        update((byte) 50);
        update((byte) 47);
        if (bitLength > 100) {
            update((byte) ((bitLength / 100) + 48));
            int bitLength2 = bitLength % 100;
            update((byte) ((bitLength2 / 10) + 48));
            update((byte) ((bitLength2 % 10) + 48));
        } else if (bitLength > 10) {
            update((byte) ((bitLength / 10) + 48));
            update((byte) ((bitLength % 10) + 48));
        } else {
            update((byte) (bitLength + 48));
        }
        finish();
        this.H1t = this.H1;
        this.H2t = this.H2;
        this.H3t = this.H3;
        this.H4t = this.H4;
        this.H5t = this.H5;
        this.H6t = this.H6;
        this.H7t = this.H7;
        this.H8t = this.H8;
    }

    private static void longToBigEndian(long n, byte[] bs, int off, int max) {
        if (max > 0) {
            intToBigEndian((int) (n >>> 32), bs, off, max);
            if (max > 4) {
                intToBigEndian((int) (n & 4294967295L), bs, off + 4, max - 4);
            }
        }
    }

    private static void intToBigEndian(int n, byte[] bs, int off, int max) {
        int num = Math.min(4, max);
        while (true) {
            num--;
            if (num >= 0) {
                int shift = 8 * (3 - num);
                bs[off + num] = (byte) (n >>> shift);
            } else {
                return;
            }
        }
    }

    @Override // org.spongycastle.util.Memoable
    public Memoable copy() {
        return new SHA512tDigest(this);
    }

    @Override // org.spongycastle.util.Memoable
    public void reset(Memoable other) {
        SHA512tDigest t = (SHA512tDigest) other;
        if (this.digestLength != t.digestLength) {
            throw new MemoableResetException("digestLength inappropriate in other");
        }
        super.copyIn(t);
        this.H1t = t.H1t;
        this.H2t = t.H2t;
        this.H3t = t.H3t;
        this.H4t = t.H4t;
        this.H5t = t.H5t;
        this.H6t = t.H6t;
        this.H7t = t.H7t;
        this.H8t = t.H8t;
    }

    @Override // org.spongycastle.crypto.digests.EncodableDigest
    public byte[] getEncodedState() {
        int baseSize = getEncodedStateSize();
        byte[] encoded = new byte[baseSize + 4];
        populateState(encoded);
        Pack.intToBigEndian(this.digestLength * 8, encoded, baseSize);
        return encoded;
    }
}
