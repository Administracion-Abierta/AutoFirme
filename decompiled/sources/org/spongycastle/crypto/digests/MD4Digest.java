package org.spongycastle.crypto.digests;

import org.spongycastle.util.Memoable;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/digests/MD4Digest.class */
public class MD4Digest extends GeneralDigest {
    private static final int DIGEST_LENGTH = 16;
    private int H1;
    private int H2;
    private int H3;
    private int H4;
    private int[] X;
    private int xOff;
    private static final int S11 = 3;
    private static final int S12 = 7;
    private static final int S13 = 11;
    private static final int S14 = 19;
    private static final int S21 = 3;
    private static final int S22 = 5;
    private static final int S23 = 9;
    private static final int S24 = 13;
    private static final int S31 = 3;
    private static final int S32 = 9;
    private static final int S33 = 11;
    private static final int S34 = 15;

    public MD4Digest() {
        this.X = new int[16];
        reset();
    }

    public MD4Digest(MD4Digest t) {
        super(t);
        this.X = new int[16];
        copyIn(t);
    }

    private void copyIn(MD4Digest t) {
        super.copyIn((GeneralDigest) t);
        this.H1 = t.H1;
        this.H2 = t.H2;
        this.H3 = t.H3;
        this.H4 = t.H4;
        System.arraycopy(t.X, 0, this.X, 0, t.X.length);
        this.xOff = t.xOff;
    }

    @Override // org.spongycastle.crypto.Digest
    public String getAlgorithmName() {
        return "MD4";
    }

    @Override // org.spongycastle.crypto.Digest
    public int getDigestSize() {
        return 16;
    }

    @Override // org.spongycastle.crypto.digests.GeneralDigest
    protected void processWord(byte[] in, int inOff) {
        int[] iArr = this.X;
        int i = this.xOff;
        this.xOff = i + 1;
        iArr[i] = (in[inOff] & 255) | ((in[inOff + 1] & 255) << 8) | ((in[inOff + 2] & 255) << 16) | ((in[inOff + 3] & 255) << 24);
        if (this.xOff == 16) {
            processBlock();
        }
    }

    @Override // org.spongycastle.crypto.digests.GeneralDigest
    protected void processLength(long bitLength) {
        if (this.xOff > 14) {
            processBlock();
        }
        this.X[14] = (int) (bitLength & (-1));
        this.X[15] = (int) (bitLength >>> 32);
    }

    private void unpackWord(int word, byte[] out, int outOff) {
        out[outOff] = (byte) word;
        out[outOff + 1] = (byte) (word >>> 8);
        out[outOff + 2] = (byte) (word >>> 16);
        out[outOff + 3] = (byte) (word >>> 24);
    }

    @Override // org.spongycastle.crypto.Digest
    public int doFinal(byte[] out, int outOff) {
        finish();
        unpackWord(this.H1, out, outOff);
        unpackWord(this.H2, out, outOff + 4);
        unpackWord(this.H3, out, outOff + 8);
        unpackWord(this.H4, out, outOff + 12);
        reset();
        return 16;
    }

    @Override // org.spongycastle.crypto.digests.GeneralDigest, org.spongycastle.crypto.Digest
    public void reset() {
        super.reset();
        this.H1 = 1732584193;
        this.H2 = -271733879;
        this.H3 = -1732584194;
        this.H4 = 271733878;
        this.xOff = 0;
        for (int i = 0; i != this.X.length; i++) {
            this.X[i] = 0;
        }
    }

    private int rotateLeft(int x, int n) {
        return (x << n) | (x >>> (32 - n));
    }

    private int F(int u, int v, int w) {
        return (u & v) | ((u ^ (-1)) & w);
    }

    private int G(int u, int v, int w) {
        return (u & v) | (u & w) | (v & w);
    }

    private int H(int u, int v, int w) {
        return (u ^ v) ^ w;
    }

    @Override // org.spongycastle.crypto.digests.GeneralDigest
    protected void processBlock() {
        int a = this.H1;
        int b = this.H2;
        int c = this.H3;
        int d = this.H4;
        int a2 = rotateLeft(a + F(b, c, d) + this.X[0], 3);
        int d2 = rotateLeft(d + F(a2, b, c) + this.X[1], 7);
        int c2 = rotateLeft(c + F(d2, a2, b) + this.X[2], 11);
        int b2 = rotateLeft(b + F(c2, d2, a2) + this.X[3], 19);
        int a3 = rotateLeft(a2 + F(b2, c2, d2) + this.X[4], 3);
        int d3 = rotateLeft(d2 + F(a3, b2, c2) + this.X[5], 7);
        int c3 = rotateLeft(c2 + F(d3, a3, b2) + this.X[6], 11);
        int b3 = rotateLeft(b2 + F(c3, d3, a3) + this.X[7], 19);
        int a4 = rotateLeft(a3 + F(b3, c3, d3) + this.X[8], 3);
        int d4 = rotateLeft(d3 + F(a4, b3, c3) + this.X[9], 7);
        int c4 = rotateLeft(c3 + F(d4, a4, b3) + this.X[10], 11);
        int b4 = rotateLeft(b3 + F(c4, d4, a4) + this.X[11], 19);
        int a5 = rotateLeft(a4 + F(b4, c4, d4) + this.X[12], 3);
        int d5 = rotateLeft(d4 + F(a5, b4, c4) + this.X[13], 7);
        int c5 = rotateLeft(c4 + F(d5, a5, b4) + this.X[14], 11);
        int b5 = rotateLeft(b4 + F(c5, d5, a5) + this.X[15], 19);
        int a6 = rotateLeft(a5 + G(b5, c5, d5) + this.X[0] + 1518500249, 3);
        int d6 = rotateLeft(d5 + G(a6, b5, c5) + this.X[4] + 1518500249, 5);
        int c6 = rotateLeft(c5 + G(d6, a6, b5) + this.X[8] + 1518500249, 9);
        int b6 = rotateLeft(b5 + G(c6, d6, a6) + this.X[12] + 1518500249, 13);
        int a7 = rotateLeft(a6 + G(b6, c6, d6) + this.X[1] + 1518500249, 3);
        int d7 = rotateLeft(d6 + G(a7, b6, c6) + this.X[5] + 1518500249, 5);
        int c7 = rotateLeft(c6 + G(d7, a7, b6) + this.X[9] + 1518500249, 9);
        int b7 = rotateLeft(b6 + G(c7, d7, a7) + this.X[13] + 1518500249, 13);
        int a8 = rotateLeft(a7 + G(b7, c7, d7) + this.X[2] + 1518500249, 3);
        int d8 = rotateLeft(d7 + G(a8, b7, c7) + this.X[6] + 1518500249, 5);
        int c8 = rotateLeft(c7 + G(d8, a8, b7) + this.X[10] + 1518500249, 9);
        int b8 = rotateLeft(b7 + G(c8, d8, a8) + this.X[14] + 1518500249, 13);
        int a9 = rotateLeft(a8 + G(b8, c8, d8) + this.X[3] + 1518500249, 3);
        int d9 = rotateLeft(d8 + G(a9, b8, c8) + this.X[7] + 1518500249, 5);
        int c9 = rotateLeft(c8 + G(d9, a9, b8) + this.X[11] + 1518500249, 9);
        int b9 = rotateLeft(b8 + G(c9, d9, a9) + this.X[15] + 1518500249, 13);
        int a10 = rotateLeft(a9 + H(b9, c9, d9) + this.X[0] + 1859775393, 3);
        int d10 = rotateLeft(d9 + H(a10, b9, c9) + this.X[8] + 1859775393, 9);
        int c10 = rotateLeft(c9 + H(d10, a10, b9) + this.X[4] + 1859775393, 11);
        int b10 = rotateLeft(b9 + H(c10, d10, a10) + this.X[12] + 1859775393, 15);
        int a11 = rotateLeft(a10 + H(b10, c10, d10) + this.X[2] + 1859775393, 3);
        int d11 = rotateLeft(d10 + H(a11, b10, c10) + this.X[10] + 1859775393, 9);
        int c11 = rotateLeft(c10 + H(d11, a11, b10) + this.X[6] + 1859775393, 11);
        int b11 = rotateLeft(b10 + H(c11, d11, a11) + this.X[14] + 1859775393, 15);
        int a12 = rotateLeft(a11 + H(b11, c11, d11) + this.X[1] + 1859775393, 3);
        int d12 = rotateLeft(d11 + H(a12, b11, c11) + this.X[9] + 1859775393, 9);
        int c12 = rotateLeft(c11 + H(d12, a12, b11) + this.X[5] + 1859775393, 11);
        int b12 = rotateLeft(b11 + H(c12, d12, a12) + this.X[13] + 1859775393, 15);
        int a13 = rotateLeft(a12 + H(b12, c12, d12) + this.X[3] + 1859775393, 3);
        int d13 = rotateLeft(d12 + H(a13, b12, c12) + this.X[11] + 1859775393, 9);
        int c13 = rotateLeft(c12 + H(d13, a13, b12) + this.X[7] + 1859775393, 11);
        int b13 = rotateLeft(b12 + H(c13, d13, a13) + this.X[15] + 1859775393, 15);
        this.H1 += a13;
        this.H2 += b13;
        this.H3 += c13;
        this.H4 += d13;
        this.xOff = 0;
        for (int i = 0; i != this.X.length; i++) {
            this.X[i] = 0;
        }
    }

    @Override // org.spongycastle.util.Memoable
    public Memoable copy() {
        return new MD4Digest(this);
    }

    @Override // org.spongycastle.util.Memoable
    public void reset(Memoable other) {
        MD4Digest d = (MD4Digest) other;
        copyIn(d);
    }
}
