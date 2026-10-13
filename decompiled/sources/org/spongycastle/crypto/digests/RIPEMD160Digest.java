package org.spongycastle.crypto.digests;

import org.spongycastle.util.Memoable;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/digests/RIPEMD160Digest.class */
public class RIPEMD160Digest extends GeneralDigest {
    private static final int DIGEST_LENGTH = 20;
    private int H0;
    private int H1;
    private int H2;
    private int H3;
    private int H4;
    private int[] X;
    private int xOff;

    public RIPEMD160Digest() {
        this.X = new int[16];
        reset();
    }

    public RIPEMD160Digest(RIPEMD160Digest t) {
        super(t);
        this.X = new int[16];
        copyIn(t);
    }

    private void copyIn(RIPEMD160Digest t) {
        super.copyIn((GeneralDigest) t);
        this.H0 = t.H0;
        this.H1 = t.H1;
        this.H2 = t.H2;
        this.H3 = t.H3;
        this.H4 = t.H4;
        System.arraycopy(t.X, 0, this.X, 0, t.X.length);
        this.xOff = t.xOff;
    }

    @Override // org.spongycastle.crypto.Digest
    public String getAlgorithmName() {
        return "RIPEMD160";
    }

    @Override // org.spongycastle.crypto.Digest
    public int getDigestSize() {
        return 20;
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
        unpackWord(this.H0, out, outOff);
        unpackWord(this.H1, out, outOff + 4);
        unpackWord(this.H2, out, outOff + 8);
        unpackWord(this.H3, out, outOff + 12);
        unpackWord(this.H4, out, outOff + 16);
        reset();
        return 20;
    }

    @Override // org.spongycastle.crypto.digests.GeneralDigest, org.spongycastle.crypto.Digest
    public void reset() {
        super.reset();
        this.H0 = 1732584193;
        this.H1 = -271733879;
        this.H2 = -1732584194;
        this.H3 = 271733878;
        this.H4 = -1009589776;
        this.xOff = 0;
        for (int i = 0; i != this.X.length; i++) {
            this.X[i] = 0;
        }
    }

    private int RL(int x, int n) {
        return (x << n) | (x >>> (32 - n));
    }

    private int f1(int x, int y, int z) {
        return (x ^ y) ^ z;
    }

    private int f2(int x, int y, int z) {
        return (x & y) | ((x ^ (-1)) & z);
    }

    private int f3(int x, int y, int z) {
        return (x | (y ^ (-1))) ^ z;
    }

    private int f4(int x, int y, int z) {
        return (x & z) | (y & (z ^ (-1)));
    }

    private int f5(int x, int y, int z) {
        return x ^ (y | (z ^ (-1)));
    }

    @Override // org.spongycastle.crypto.digests.GeneralDigest
    protected void processBlock() {
        int a = this.H0;
        int b = this.H1;
        int c = this.H2;
        int d = this.H3;
        int e = this.H4;
        int a2 = RL(a + f1(b, c, d) + this.X[0], 11) + e;
        int c2 = RL(c, 10);
        int e2 = RL(e + f1(a2, b, c2) + this.X[1], 14) + d;
        int b2 = RL(b, 10);
        int d2 = RL(d + f1(e2, a2, b2) + this.X[2], 15) + c2;
        int a3 = RL(a2, 10);
        int c3 = RL(c2 + f1(d2, e2, a3) + this.X[3], 12) + b2;
        int e3 = RL(e2, 10);
        int b3 = RL(b2 + f1(c3, d2, e3) + this.X[4], 5) + a3;
        int d3 = RL(d2, 10);
        int a4 = RL(a3 + f1(b3, c3, d3) + this.X[5], 8) + e3;
        int c4 = RL(c3, 10);
        int e4 = RL(e3 + f1(a4, b3, c4) + this.X[6], 7) + d3;
        int b4 = RL(b3, 10);
        int d4 = RL(d3 + f1(e4, a4, b4) + this.X[7], 9) + c4;
        int a5 = RL(a4, 10);
        int c5 = RL(c4 + f1(d4, e4, a5) + this.X[8], 11) + b4;
        int e5 = RL(e4, 10);
        int b5 = RL(b4 + f1(c5, d4, e5) + this.X[9], 13) + a5;
        int d5 = RL(d4, 10);
        int a6 = RL(a5 + f1(b5, c5, d5) + this.X[10], 14) + e5;
        int c6 = RL(c5, 10);
        int e6 = RL(e5 + f1(a6, b5, c6) + this.X[11], 15) + d5;
        int b6 = RL(b5, 10);
        int d6 = RL(d5 + f1(e6, a6, b6) + this.X[12], 6) + c6;
        int a7 = RL(a6, 10);
        int c7 = RL(c6 + f1(d6, e6, a7) + this.X[13], 7) + b6;
        int e7 = RL(e6, 10);
        int b7 = RL(b6 + f1(c7, d6, e7) + this.X[14], 9) + a7;
        int d7 = RL(d6, 10);
        int a8 = RL(a7 + f1(b7, c7, d7) + this.X[15], 8) + e7;
        int c8 = RL(c7, 10);
        int aa = RL(a + f5(b, c, d) + this.X[5] + 1352829926, 8) + e;
        int cc = RL(c, 10);
        int ee = RL(e + f5(aa, b, cc) + this.X[14] + 1352829926, 9) + d;
        int bb = RL(b, 10);
        int dd = RL(d + f5(ee, aa, bb) + this.X[7] + 1352829926, 9) + cc;
        int aa2 = RL(aa, 10);
        int cc2 = RL(cc + f5(dd, ee, aa2) + this.X[0] + 1352829926, 11) + bb;
        int ee2 = RL(ee, 10);
        int bb2 = RL(bb + f5(cc2, dd, ee2) + this.X[9] + 1352829926, 13) + aa2;
        int dd2 = RL(dd, 10);
        int aa3 = RL(aa2 + f5(bb2, cc2, dd2) + this.X[2] + 1352829926, 15) + ee2;
        int cc3 = RL(cc2, 10);
        int ee3 = RL(ee2 + f5(aa3, bb2, cc3) + this.X[11] + 1352829926, 15) + dd2;
        int bb3 = RL(bb2, 10);
        int dd3 = RL(dd2 + f5(ee3, aa3, bb3) + this.X[4] + 1352829926, 5) + cc3;
        int aa4 = RL(aa3, 10);
        int cc4 = RL(cc3 + f5(dd3, ee3, aa4) + this.X[13] + 1352829926, 7) + bb3;
        int ee4 = RL(ee3, 10);
        int bb4 = RL(bb3 + f5(cc4, dd3, ee4) + this.X[6] + 1352829926, 7) + aa4;
        int dd4 = RL(dd3, 10);
        int aa5 = RL(aa4 + f5(bb4, cc4, dd4) + this.X[15] + 1352829926, 8) + ee4;
        int cc5 = RL(cc4, 10);
        int ee5 = RL(ee4 + f5(aa5, bb4, cc5) + this.X[8] + 1352829926, 11) + dd4;
        int bb5 = RL(bb4, 10);
        int dd5 = RL(dd4 + f5(ee5, aa5, bb5) + this.X[1] + 1352829926, 14) + cc5;
        int aa6 = RL(aa5, 10);
        int cc6 = RL(cc5 + f5(dd5, ee5, aa6) + this.X[10] + 1352829926, 14) + bb5;
        int ee6 = RL(ee5, 10);
        int bb6 = RL(bb5 + f5(cc6, dd5, ee6) + this.X[3] + 1352829926, 12) + aa6;
        int dd6 = RL(dd5, 10);
        int aa7 = RL(aa6 + f5(bb6, cc6, dd6) + this.X[12] + 1352829926, 6) + ee6;
        int cc7 = RL(cc6, 10);
        int e8 = RL(e7 + f2(a8, b7, c8) + this.X[7] + 1518500249, 7) + d7;
        int b8 = RL(b7, 10);
        int d8 = RL(d7 + f2(e8, a8, b8) + this.X[4] + 1518500249, 6) + c8;
        int a9 = RL(a8, 10);
        int c9 = RL(c8 + f2(d8, e8, a9) + this.X[13] + 1518500249, 8) + b8;
        int e9 = RL(e8, 10);
        int b9 = RL(b8 + f2(c9, d8, e9) + this.X[1] + 1518500249, 13) + a9;
        int d9 = RL(d8, 10);
        int a10 = RL(a9 + f2(b9, c9, d9) + this.X[10] + 1518500249, 11) + e9;
        int c10 = RL(c9, 10);
        int e10 = RL(e9 + f2(a10, b9, c10) + this.X[6] + 1518500249, 9) + d9;
        int b10 = RL(b9, 10);
        int d10 = RL(d9 + f2(e10, a10, b10) + this.X[15] + 1518500249, 7) + c10;
        int a11 = RL(a10, 10);
        int c11 = RL(c10 + f2(d10, e10, a11) + this.X[3] + 1518500249, 15) + b10;
        int e11 = RL(e10, 10);
        int b11 = RL(b10 + f2(c11, d10, e11) + this.X[12] + 1518500249, 7) + a11;
        int d11 = RL(d10, 10);
        int a12 = RL(a11 + f2(b11, c11, d11) + this.X[0] + 1518500249, 12) + e11;
        int c12 = RL(c11, 10);
        int e12 = RL(e11 + f2(a12, b11, c12) + this.X[9] + 1518500249, 15) + d11;
        int b12 = RL(b11, 10);
        int d12 = RL(d11 + f2(e12, a12, b12) + this.X[5] + 1518500249, 9) + c12;
        int a13 = RL(a12, 10);
        int c13 = RL(c12 + f2(d12, e12, a13) + this.X[2] + 1518500249, 11) + b12;
        int e13 = RL(e12, 10);
        int b13 = RL(b12 + f2(c13, d12, e13) + this.X[14] + 1518500249, 7) + a13;
        int d13 = RL(d12, 10);
        int a14 = RL(a13 + f2(b13, c13, d13) + this.X[11] + 1518500249, 13) + e13;
        int c14 = RL(c13, 10);
        int e14 = RL(e13 + f2(a14, b13, c14) + this.X[8] + 1518500249, 12) + d13;
        int b14 = RL(b13, 10);
        int ee7 = RL(ee6 + f4(aa7, bb6, cc7) + this.X[6] + 1548603684, 9) + dd6;
        int bb7 = RL(bb6, 10);
        int dd7 = RL(dd6 + f4(ee7, aa7, bb7) + this.X[11] + 1548603684, 13) + cc7;
        int aa8 = RL(aa7, 10);
        int cc8 = RL(cc7 + f4(dd7, ee7, aa8) + this.X[3] + 1548603684, 15) + bb7;
        int ee8 = RL(ee7, 10);
        int bb8 = RL(bb7 + f4(cc8, dd7, ee8) + this.X[7] + 1548603684, 7) + aa8;
        int dd8 = RL(dd7, 10);
        int aa9 = RL(aa8 + f4(bb8, cc8, dd8) + this.X[0] + 1548603684, 12) + ee8;
        int cc9 = RL(cc8, 10);
        int ee9 = RL(ee8 + f4(aa9, bb8, cc9) + this.X[13] + 1548603684, 8) + dd8;
        int bb9 = RL(bb8, 10);
        int dd9 = RL(dd8 + f4(ee9, aa9, bb9) + this.X[5] + 1548603684, 9) + cc9;
        int aa10 = RL(aa9, 10);
        int cc10 = RL(cc9 + f4(dd9, ee9, aa10) + this.X[10] + 1548603684, 11) + bb9;
        int ee10 = RL(ee9, 10);
        int bb10 = RL(bb9 + f4(cc10, dd9, ee10) + this.X[14] + 1548603684, 7) + aa10;
        int dd10 = RL(dd9, 10);
        int aa11 = RL(aa10 + f4(bb10, cc10, dd10) + this.X[15] + 1548603684, 7) + ee10;
        int cc11 = RL(cc10, 10);
        int ee11 = RL(ee10 + f4(aa11, bb10, cc11) + this.X[8] + 1548603684, 12) + dd10;
        int bb11 = RL(bb10, 10);
        int dd11 = RL(dd10 + f4(ee11, aa11, bb11) + this.X[12] + 1548603684, 7) + cc11;
        int aa12 = RL(aa11, 10);
        int cc12 = RL(cc11 + f4(dd11, ee11, aa12) + this.X[4] + 1548603684, 6) + bb11;
        int ee12 = RL(ee11, 10);
        int bb12 = RL(bb11 + f4(cc12, dd11, ee12) + this.X[9] + 1548603684, 15) + aa12;
        int dd12 = RL(dd11, 10);
        int aa13 = RL(aa12 + f4(bb12, cc12, dd12) + this.X[1] + 1548603684, 13) + ee12;
        int cc13 = RL(cc12, 10);
        int ee13 = RL(ee12 + f4(aa13, bb12, cc13) + this.X[2] + 1548603684, 11) + dd12;
        int bb13 = RL(bb12, 10);
        int d14 = RL(d13 + f3(e14, a14, b14) + this.X[3] + 1859775393, 11) + c14;
        int a15 = RL(a14, 10);
        int c15 = RL(c14 + f3(d14, e14, a15) + this.X[10] + 1859775393, 13) + b14;
        int e15 = RL(e14, 10);
        int b15 = RL(b14 + f3(c15, d14, e15) + this.X[14] + 1859775393, 6) + a15;
        int d15 = RL(d14, 10);
        int a16 = RL(a15 + f3(b15, c15, d15) + this.X[4] + 1859775393, 7) + e15;
        int c16 = RL(c15, 10);
        int e16 = RL(e15 + f3(a16, b15, c16) + this.X[9] + 1859775393, 14) + d15;
        int b16 = RL(b15, 10);
        int d16 = RL(d15 + f3(e16, a16, b16) + this.X[15] + 1859775393, 9) + c16;
        int a17 = RL(a16, 10);
        int c17 = RL(c16 + f3(d16, e16, a17) + this.X[8] + 1859775393, 13) + b16;
        int e17 = RL(e16, 10);
        int b17 = RL(b16 + f3(c17, d16, e17) + this.X[1] + 1859775393, 15) + a17;
        int d17 = RL(d16, 10);
        int a18 = RL(a17 + f3(b17, c17, d17) + this.X[2] + 1859775393, 14) + e17;
        int c18 = RL(c17, 10);
        int e18 = RL(e17 + f3(a18, b17, c18) + this.X[7] + 1859775393, 8) + d17;
        int b18 = RL(b17, 10);
        int d18 = RL(d17 + f3(e18, a18, b18) + this.X[0] + 1859775393, 13) + c18;
        int a19 = RL(a18, 10);
        int c19 = RL(c18 + f3(d18, e18, a19) + this.X[6] + 1859775393, 6) + b18;
        int e19 = RL(e18, 10);
        int b19 = RL(b18 + f3(c19, d18, e19) + this.X[13] + 1859775393, 5) + a19;
        int d19 = RL(d18, 10);
        int a20 = RL(a19 + f3(b19, c19, d19) + this.X[11] + 1859775393, 12) + e19;
        int c20 = RL(c19, 10);
        int e20 = RL(e19 + f3(a20, b19, c20) + this.X[5] + 1859775393, 7) + d19;
        int b20 = RL(b19, 10);
        int d20 = RL(d19 + f3(e20, a20, b20) + this.X[12] + 1859775393, 5) + c20;
        int a21 = RL(a20, 10);
        int dd13 = RL(dd12 + f3(ee13, aa13, bb13) + this.X[15] + 1836072691, 9) + cc13;
        int aa14 = RL(aa13, 10);
        int cc14 = RL(cc13 + f3(dd13, ee13, aa14) + this.X[5] + 1836072691, 7) + bb13;
        int ee14 = RL(ee13, 10);
        int bb14 = RL(bb13 + f3(cc14, dd13, ee14) + this.X[1] + 1836072691, 15) + aa14;
        int dd14 = RL(dd13, 10);
        int aa15 = RL(aa14 + f3(bb14, cc14, dd14) + this.X[3] + 1836072691, 11) + ee14;
        int cc15 = RL(cc14, 10);
        int ee15 = RL(ee14 + f3(aa15, bb14, cc15) + this.X[7] + 1836072691, 8) + dd14;
        int bb15 = RL(bb14, 10);
        int dd15 = RL(dd14 + f3(ee15, aa15, bb15) + this.X[14] + 1836072691, 6) + cc15;
        int aa16 = RL(aa15, 10);
        int cc16 = RL(cc15 + f3(dd15, ee15, aa16) + this.X[6] + 1836072691, 6) + bb15;
        int ee16 = RL(ee15, 10);
        int bb16 = RL(bb15 + f3(cc16, dd15, ee16) + this.X[9] + 1836072691, 14) + aa16;
        int dd16 = RL(dd15, 10);
        int aa17 = RL(aa16 + f3(bb16, cc16, dd16) + this.X[11] + 1836072691, 12) + ee16;
        int cc17 = RL(cc16, 10);
        int ee17 = RL(ee16 + f3(aa17, bb16, cc17) + this.X[8] + 1836072691, 13) + dd16;
        int bb17 = RL(bb16, 10);
        int dd17 = RL(dd16 + f3(ee17, aa17, bb17) + this.X[12] + 1836072691, 5) + cc17;
        int aa18 = RL(aa17, 10);
        int cc18 = RL(cc17 + f3(dd17, ee17, aa18) + this.X[2] + 1836072691, 14) + bb17;
        int ee18 = RL(ee17, 10);
        int bb18 = RL(bb17 + f3(cc18, dd17, ee18) + this.X[10] + 1836072691, 13) + aa18;
        int dd18 = RL(dd17, 10);
        int aa19 = RL(aa18 + f3(bb18, cc18, dd18) + this.X[0] + 1836072691, 13) + ee18;
        int cc19 = RL(cc18, 10);
        int ee19 = RL(ee18 + f3(aa19, bb18, cc19) + this.X[4] + 1836072691, 7) + dd18;
        int bb19 = RL(bb18, 10);
        int dd19 = RL(dd18 + f3(ee19, aa19, bb19) + this.X[13] + 1836072691, 5) + cc19;
        int aa20 = RL(aa19, 10);
        int c21 = RL(((c20 + f4(d20, e20, a21)) + this.X[1]) - 1894007588, 11) + b20;
        int e21 = RL(e20, 10);
        int b21 = RL(((b20 + f4(c21, d20, e21)) + this.X[9]) - 1894007588, 12) + a21;
        int d21 = RL(d20, 10);
        int a22 = RL(((a21 + f4(b21, c21, d21)) + this.X[11]) - 1894007588, 14) + e21;
        int c22 = RL(c21, 10);
        int e22 = RL(((e21 + f4(a22, b21, c22)) + this.X[10]) - 1894007588, 15) + d21;
        int b22 = RL(b21, 10);
        int d22 = RL(((d21 + f4(e22, a22, b22)) + this.X[0]) - 1894007588, 14) + c22;
        int a23 = RL(a22, 10);
        int c23 = RL(((c22 + f4(d22, e22, a23)) + this.X[8]) - 1894007588, 15) + b22;
        int e23 = RL(e22, 10);
        int b23 = RL(((b22 + f4(c23, d22, e23)) + this.X[12]) - 1894007588, 9) + a23;
        int d23 = RL(d22, 10);
        int a24 = RL(((a23 + f4(b23, c23, d23)) + this.X[4]) - 1894007588, 8) + e23;
        int c24 = RL(c23, 10);
        int e24 = RL(((e23 + f4(a24, b23, c24)) + this.X[13]) - 1894007588, 9) + d23;
        int b24 = RL(b23, 10);
        int d24 = RL(((d23 + f4(e24, a24, b24)) + this.X[3]) - 1894007588, 14) + c24;
        int a25 = RL(a24, 10);
        int c25 = RL(((c24 + f4(d24, e24, a25)) + this.X[7]) - 1894007588, 5) + b24;
        int e25 = RL(e24, 10);
        int b25 = RL(((b24 + f4(c25, d24, e25)) + this.X[15]) - 1894007588, 6) + a25;
        int d25 = RL(d24, 10);
        int a26 = RL(((a25 + f4(b25, c25, d25)) + this.X[14]) - 1894007588, 8) + e25;
        int c26 = RL(c25, 10);
        int e26 = RL(((e25 + f4(a26, b25, c26)) + this.X[5]) - 1894007588, 6) + d25;
        int b26 = RL(b25, 10);
        int d26 = RL(((d25 + f4(e26, a26, b26)) + this.X[6]) - 1894007588, 5) + c26;
        int a27 = RL(a26, 10);
        int c27 = RL(((c26 + f4(d26, e26, a27)) + this.X[2]) - 1894007588, 12) + b26;
        int e27 = RL(e26, 10);
        int cc20 = RL(cc19 + f2(dd19, ee19, aa20) + this.X[8] + 2053994217, 15) + bb19;
        int ee20 = RL(ee19, 10);
        int bb20 = RL(bb19 + f2(cc20, dd19, ee20) + this.X[6] + 2053994217, 5) + aa20;
        int dd20 = RL(dd19, 10);
        int aa21 = RL(aa20 + f2(bb20, cc20, dd20) + this.X[4] + 2053994217, 8) + ee20;
        int cc21 = RL(cc20, 10);
        int ee21 = RL(ee20 + f2(aa21, bb20, cc21) + this.X[1] + 2053994217, 11) + dd20;
        int bb21 = RL(bb20, 10);
        int dd21 = RL(dd20 + f2(ee21, aa21, bb21) + this.X[3] + 2053994217, 14) + cc21;
        int aa22 = RL(aa21, 10);
        int cc22 = RL(cc21 + f2(dd21, ee21, aa22) + this.X[11] + 2053994217, 14) + bb21;
        int ee22 = RL(ee21, 10);
        int bb22 = RL(bb21 + f2(cc22, dd21, ee22) + this.X[15] + 2053994217, 6) + aa22;
        int dd22 = RL(dd21, 10);
        int aa23 = RL(aa22 + f2(bb22, cc22, dd22) + this.X[0] + 2053994217, 14) + ee22;
        int cc23 = RL(cc22, 10);
        int ee23 = RL(ee22 + f2(aa23, bb22, cc23) + this.X[5] + 2053994217, 6) + dd22;
        int bb23 = RL(bb22, 10);
        int dd23 = RL(dd22 + f2(ee23, aa23, bb23) + this.X[12] + 2053994217, 9) + cc23;
        int aa24 = RL(aa23, 10);
        int cc24 = RL(cc23 + f2(dd23, ee23, aa24) + this.X[2] + 2053994217, 12) + bb23;
        int ee24 = RL(ee23, 10);
        int bb24 = RL(bb23 + f2(cc24, dd23, ee24) + this.X[13] + 2053994217, 9) + aa24;
        int dd24 = RL(dd23, 10);
        int aa25 = RL(aa24 + f2(bb24, cc24, dd24) + this.X[9] + 2053994217, 12) + ee24;
        int cc25 = RL(cc24, 10);
        int ee25 = RL(ee24 + f2(aa25, bb24, cc25) + this.X[7] + 2053994217, 5) + dd24;
        int bb25 = RL(bb24, 10);
        int dd25 = RL(dd24 + f2(ee25, aa25, bb25) + this.X[10] + 2053994217, 15) + cc25;
        int aa26 = RL(aa25, 10);
        int cc26 = RL(cc25 + f2(dd25, ee25, aa26) + this.X[14] + 2053994217, 8) + bb25;
        int ee26 = RL(ee25, 10);
        int b27 = RL(((b26 + f5(c27, d26, e27)) + this.X[4]) - 1454113458, 9) + a27;
        int d27 = RL(d26, 10);
        int a28 = RL(((a27 + f5(b27, c27, d27)) + this.X[0]) - 1454113458, 15) + e27;
        int c28 = RL(c27, 10);
        int e28 = RL(((e27 + f5(a28, b27, c28)) + this.X[5]) - 1454113458, 5) + d27;
        int b28 = RL(b27, 10);
        int d28 = RL(((d27 + f5(e28, a28, b28)) + this.X[9]) - 1454113458, 11) + c28;
        int a29 = RL(a28, 10);
        int c29 = RL(((c28 + f5(d28, e28, a29)) + this.X[7]) - 1454113458, 6) + b28;
        int e29 = RL(e28, 10);
        int b29 = RL(((b28 + f5(c29, d28, e29)) + this.X[12]) - 1454113458, 8) + a29;
        int d29 = RL(d28, 10);
        int a30 = RL(((a29 + f5(b29, c29, d29)) + this.X[2]) - 1454113458, 13) + e29;
        int c30 = RL(c29, 10);
        int e30 = RL(((e29 + f5(a30, b29, c30)) + this.X[10]) - 1454113458, 12) + d29;
        int b30 = RL(b29, 10);
        int d30 = RL(((d29 + f5(e30, a30, b30)) + this.X[14]) - 1454113458, 5) + c30;
        int a31 = RL(a30, 10);
        int c31 = RL(((c30 + f5(d30, e30, a31)) + this.X[1]) - 1454113458, 12) + b30;
        int e31 = RL(e30, 10);
        int b31 = RL(((b30 + f5(c31, d30, e31)) + this.X[3]) - 1454113458, 13) + a31;
        int d31 = RL(d30, 10);
        int a32 = RL(((a31 + f5(b31, c31, d31)) + this.X[8]) - 1454113458, 14) + e31;
        int c32 = RL(c31, 10);
        int e32 = RL(((e31 + f5(a32, b31, c32)) + this.X[11]) - 1454113458, 11) + d31;
        int b32 = RL(b31, 10);
        int d32 = RL(((d31 + f5(e32, a32, b32)) + this.X[6]) - 1454113458, 8) + c32;
        int a33 = RL(a32, 10);
        int c33 = RL(((c32 + f5(d32, e32, a33)) + this.X[15]) - 1454113458, 5) + b32;
        int e33 = RL(e32, 10);
        int b33 = RL(((b32 + f5(c33, d32, e33)) + this.X[13]) - 1454113458, 6) + a33;
        int d33 = RL(d32, 10);
        int bb26 = RL(bb25 + f1(cc26, dd25, ee26) + this.X[12], 8) + aa26;
        int dd26 = RL(dd25, 10);
        int aa27 = RL(aa26 + f1(bb26, cc26, dd26) + this.X[15], 5) + ee26;
        int cc27 = RL(cc26, 10);
        int ee27 = RL(ee26 + f1(aa27, bb26, cc27) + this.X[10], 12) + dd26;
        int bb27 = RL(bb26, 10);
        int dd27 = RL(dd26 + f1(ee27, aa27, bb27) + this.X[4], 9) + cc27;
        int aa28 = RL(aa27, 10);
        int cc28 = RL(cc27 + f1(dd27, ee27, aa28) + this.X[1], 12) + bb27;
        int ee28 = RL(ee27, 10);
        int bb28 = RL(bb27 + f1(cc28, dd27, ee28) + this.X[5], 5) + aa28;
        int dd28 = RL(dd27, 10);
        int aa29 = RL(aa28 + f1(bb28, cc28, dd28) + this.X[8], 14) + ee28;
        int cc29 = RL(cc28, 10);
        int ee29 = RL(ee28 + f1(aa29, bb28, cc29) + this.X[7], 6) + dd28;
        int bb29 = RL(bb28, 10);
        int dd29 = RL(dd28 + f1(ee29, aa29, bb29) + this.X[6], 8) + cc29;
        int aa30 = RL(aa29, 10);
        int cc30 = RL(cc29 + f1(dd29, ee29, aa30) + this.X[2], 13) + bb29;
        int ee30 = RL(ee29, 10);
        int bb30 = RL(bb29 + f1(cc30, dd29, ee30) + this.X[13], 6) + aa30;
        int dd30 = RL(dd29, 10);
        int aa31 = RL(aa30 + f1(bb30, cc30, dd30) + this.X[14], 5) + ee30;
        int cc31 = RL(cc30, 10);
        int ee31 = RL(ee30 + f1(aa31, bb30, cc31) + this.X[0], 15) + dd30;
        int bb31 = RL(bb30, 10);
        int dd31 = RL(dd30 + f1(ee31, aa31, bb31) + this.X[3], 13) + cc31;
        int aa32 = RL(aa31, 10);
        int cc32 = RL(cc31 + f1(dd31, ee31, aa32) + this.X[9], 11) + bb31;
        int ee32 = RL(ee31, 10);
        int bb32 = RL(bb31 + f1(cc32, dd31, ee32) + this.X[11], 11) + aa32;
        int dd32 = RL(dd31, 10) + c33 + this.H1;
        this.H1 = this.H2 + d33 + ee32;
        this.H2 = this.H3 + e33 + aa32;
        this.H3 = this.H4 + a33 + bb32;
        this.H4 = this.H0 + b33 + cc32;
        this.H0 = dd32;
        this.xOff = 0;
        for (int i = 0; i != this.X.length; i++) {
            this.X[i] = 0;
        }
    }

    @Override // org.spongycastle.util.Memoable
    public Memoable copy() {
        return new RIPEMD160Digest(this);
    }

    @Override // org.spongycastle.util.Memoable
    public void reset(Memoable other) {
        RIPEMD160Digest d = (RIPEMD160Digest) other;
        copyIn(d);
    }
}
