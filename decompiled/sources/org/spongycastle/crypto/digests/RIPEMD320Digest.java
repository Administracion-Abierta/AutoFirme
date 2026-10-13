package org.spongycastle.crypto.digests;

import com.sun.jna.platform.unix.Reboot;
import org.spongycastle.util.Memoable;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/digests/RIPEMD320Digest.class */
public class RIPEMD320Digest extends GeneralDigest {
    private static final int DIGEST_LENGTH = 40;
    private int H0;
    private int H1;
    private int H2;
    private int H3;
    private int H4;
    private int H5;
    private int H6;
    private int H7;
    private int H8;
    private int H9;
    private int[] X;
    private int xOff;

    public RIPEMD320Digest() {
        this.X = new int[16];
        reset();
    }

    public RIPEMD320Digest(RIPEMD320Digest t) {
        super(t);
        this.X = new int[16];
        doCopy(t);
    }

    private void doCopy(RIPEMD320Digest t) {
        super.copyIn(t);
        this.H0 = t.H0;
        this.H1 = t.H1;
        this.H2 = t.H2;
        this.H3 = t.H3;
        this.H4 = t.H4;
        this.H5 = t.H5;
        this.H6 = t.H6;
        this.H7 = t.H7;
        this.H8 = t.H8;
        this.H9 = t.H9;
        System.arraycopy(t.X, 0, this.X, 0, t.X.length);
        this.xOff = t.xOff;
    }

    @Override // org.spongycastle.crypto.Digest
    public String getAlgorithmName() {
        return "RIPEMD320";
    }

    @Override // org.spongycastle.crypto.Digest
    public int getDigestSize() {
        return 40;
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
        unpackWord(this.H5, out, outOff + 20);
        unpackWord(this.H6, out, outOff + 24);
        unpackWord(this.H7, out, outOff + 28);
        unpackWord(this.H8, out, outOff + 32);
        unpackWord(this.H9, out, outOff + 36);
        reset();
        return 40;
    }

    @Override // org.spongycastle.crypto.digests.GeneralDigest, org.spongycastle.crypto.Digest
    public void reset() {
        super.reset();
        this.H0 = 1732584193;
        this.H1 = -271733879;
        this.H2 = -1732584194;
        this.H3 = 271733878;
        this.H4 = -1009589776;
        this.H5 = 1985229328;
        this.H6 = -19088744;
        this.H7 = Reboot.RB_ENABLE_CAD;
        this.H8 = Reboot.RB_AUTOBOOT;
        this.H9 = 1009589775;
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
        int aa = this.H5;
        int bb = this.H6;
        int cc = this.H7;
        int dd = this.H8;
        int ee = this.H9;
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
        int aa2 = RL(aa + f5(bb, cc, dd) + this.X[5] + 1352829926, 8) + ee;
        int cc2 = RL(cc, 10);
        int ee2 = RL(ee + f5(aa2, bb, cc2) + this.X[14] + 1352829926, 9) + dd;
        int bb2 = RL(bb, 10);
        int dd2 = RL(dd + f5(ee2, aa2, bb2) + this.X[7] + 1352829926, 9) + cc2;
        int aa3 = RL(aa2, 10);
        int cc3 = RL(cc2 + f5(dd2, ee2, aa3) + this.X[0] + 1352829926, 11) + bb2;
        int ee3 = RL(ee2, 10);
        int bb3 = RL(bb2 + f5(cc3, dd2, ee3) + this.X[9] + 1352829926, 13) + aa3;
        int dd3 = RL(dd2, 10);
        int aa4 = RL(aa3 + f5(bb3, cc3, dd3) + this.X[2] + 1352829926, 15) + ee3;
        int cc4 = RL(cc3, 10);
        int ee4 = RL(ee3 + f5(aa4, bb3, cc4) + this.X[11] + 1352829926, 15) + dd3;
        int bb4 = RL(bb3, 10);
        int dd4 = RL(dd3 + f5(ee4, aa4, bb4) + this.X[4] + 1352829926, 5) + cc4;
        int aa5 = RL(aa4, 10);
        int cc5 = RL(cc4 + f5(dd4, ee4, aa5) + this.X[13] + 1352829926, 7) + bb4;
        int ee5 = RL(ee4, 10);
        int bb5 = RL(bb4 + f5(cc5, dd4, ee5) + this.X[6] + 1352829926, 7) + aa5;
        int dd5 = RL(dd4, 10);
        int aa6 = RL(aa5 + f5(bb5, cc5, dd5) + this.X[15] + 1352829926, 8) + ee5;
        int cc6 = RL(cc5, 10);
        int ee6 = RL(ee5 + f5(aa6, bb5, cc6) + this.X[8] + 1352829926, 11) + dd5;
        int bb6 = RL(bb5, 10);
        int dd6 = RL(dd5 + f5(ee6, aa6, bb6) + this.X[1] + 1352829926, 14) + cc6;
        int aa7 = RL(aa6, 10);
        int cc7 = RL(cc6 + f5(dd6, ee6, aa7) + this.X[10] + 1352829926, 14) + bb6;
        int ee7 = RL(ee6, 10);
        int bb7 = RL(bb6 + f5(cc7, dd6, ee7) + this.X[3] + 1352829926, 12) + aa7;
        int dd7 = RL(dd6, 10);
        int aa8 = RL(aa7 + f5(bb7, cc7, dd7) + this.X[12] + 1352829926, 6) + ee7;
        int cc8 = RL(cc7, 10);
        int e8 = RL(e7 + f2(aa8, b7, c8) + this.X[7] + 1518500249, 7) + d7;
        int b8 = RL(b7, 10);
        int d8 = RL(d7 + f2(e8, aa8, b8) + this.X[4] + 1518500249, 6) + c8;
        int a9 = RL(aa8, 10);
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
        int ee8 = RL(ee7 + f4(a8, bb7, cc8) + this.X[6] + 1548603684, 9) + dd7;
        int bb8 = RL(bb7, 10);
        int dd8 = RL(dd7 + f4(ee8, a8, bb8) + this.X[11] + 1548603684, 13) + cc8;
        int aa9 = RL(a8, 10);
        int cc9 = RL(cc8 + f4(dd8, ee8, aa9) + this.X[3] + 1548603684, 15) + bb8;
        int ee9 = RL(ee8, 10);
        int bb9 = RL(bb8 + f4(cc9, dd8, ee9) + this.X[7] + 1548603684, 7) + aa9;
        int dd9 = RL(dd8, 10);
        int aa10 = RL(aa9 + f4(bb9, cc9, dd9) + this.X[0] + 1548603684, 12) + ee9;
        int cc10 = RL(cc9, 10);
        int ee10 = RL(ee9 + f4(aa10, bb9, cc10) + this.X[13] + 1548603684, 8) + dd9;
        int bb10 = RL(bb9, 10);
        int dd10 = RL(dd9 + f4(ee10, aa10, bb10) + this.X[5] + 1548603684, 9) + cc10;
        int aa11 = RL(aa10, 10);
        int cc11 = RL(cc10 + f4(dd10, ee10, aa11) + this.X[10] + 1548603684, 11) + bb10;
        int ee11 = RL(ee10, 10);
        int bb11 = RL(bb10 + f4(cc11, dd10, ee11) + this.X[14] + 1548603684, 7) + aa11;
        int dd11 = RL(dd10, 10);
        int aa12 = RL(aa11 + f4(bb11, cc11, dd11) + this.X[15] + 1548603684, 7) + ee11;
        int cc12 = RL(cc11, 10);
        int ee12 = RL(ee11 + f4(aa12, bb11, cc12) + this.X[8] + 1548603684, 12) + dd11;
        int bb12 = RL(bb11, 10);
        int dd12 = RL(dd11 + f4(ee12, aa12, bb12) + this.X[12] + 1548603684, 7) + cc12;
        int aa13 = RL(aa12, 10);
        int cc13 = RL(cc12 + f4(dd12, ee12, aa13) + this.X[4] + 1548603684, 6) + bb12;
        int ee13 = RL(ee12, 10);
        int bb13 = RL(bb12 + f4(cc13, dd12, ee13) + this.X[9] + 1548603684, 15) + aa13;
        int dd13 = RL(dd12, 10);
        int aa14 = RL(aa13 + f4(bb13, cc13, dd13) + this.X[1] + 1548603684, 13) + ee13;
        int cc14 = RL(cc13, 10);
        int ee14 = RL(ee13 + f4(aa14, bb13, cc14) + this.X[2] + 1548603684, 11) + dd13;
        int bb14 = RL(bb13, 10);
        int d14 = RL(d13 + f3(e14, a14, bb14) + this.X[3] + 1859775393, 11) + c14;
        int a15 = RL(a14, 10);
        int c15 = RL(c14 + f3(d14, e14, a15) + this.X[10] + 1859775393, 13) + bb14;
        int e15 = RL(e14, 10);
        int b15 = RL(bb14 + f3(c15, d14, e15) + this.X[14] + 1859775393, 6) + a15;
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
        int dd14 = RL(dd13 + f3(ee14, aa14, b14) + this.X[15] + 1836072691, 9) + cc14;
        int aa15 = RL(aa14, 10);
        int cc15 = RL(cc14 + f3(dd14, ee14, aa15) + this.X[5] + 1836072691, 7) + b14;
        int ee15 = RL(ee14, 10);
        int bb15 = RL(b14 + f3(cc15, dd14, ee15) + this.X[1] + 1836072691, 15) + aa15;
        int dd15 = RL(dd14, 10);
        int aa16 = RL(aa15 + f3(bb15, cc15, dd15) + this.X[3] + 1836072691, 11) + ee15;
        int cc16 = RL(cc15, 10);
        int ee16 = RL(ee15 + f3(aa16, bb15, cc16) + this.X[7] + 1836072691, 8) + dd15;
        int bb16 = RL(bb15, 10);
        int dd16 = RL(dd15 + f3(ee16, aa16, bb16) + this.X[14] + 1836072691, 6) + cc16;
        int aa17 = RL(aa16, 10);
        int cc17 = RL(cc16 + f3(dd16, ee16, aa17) + this.X[6] + 1836072691, 6) + bb16;
        int ee17 = RL(ee16, 10);
        int bb17 = RL(bb16 + f3(cc17, dd16, ee17) + this.X[9] + 1836072691, 14) + aa17;
        int dd17 = RL(dd16, 10);
        int aa18 = RL(aa17 + f3(bb17, cc17, dd17) + this.X[11] + 1836072691, 12) + ee17;
        int cc18 = RL(cc17, 10);
        int ee18 = RL(ee17 + f3(aa18, bb17, cc18) + this.X[8] + 1836072691, 13) + dd17;
        int bb18 = RL(bb17, 10);
        int dd18 = RL(dd17 + f3(ee18, aa18, bb18) + this.X[12] + 1836072691, 5) + cc18;
        int aa19 = RL(aa18, 10);
        int cc19 = RL(cc18 + f3(dd18, ee18, aa19) + this.X[2] + 1836072691, 14) + bb18;
        int ee19 = RL(ee18, 10);
        int bb19 = RL(bb18 + f3(cc19, dd18, ee19) + this.X[10] + 1836072691, 13) + aa19;
        int dd19 = RL(dd18, 10);
        int aa20 = RL(aa19 + f3(bb19, cc19, dd19) + this.X[0] + 1836072691, 13) + ee19;
        int cc20 = RL(cc19, 10);
        int ee20 = RL(ee19 + f3(aa20, bb19, cc20) + this.X[4] + 1836072691, 7) + dd19;
        int bb20 = RL(bb19, 10);
        int dd20 = RL(dd19 + f3(ee20, aa20, bb20) + this.X[13] + 1836072691, 5) + cc20;
        int aa21 = RL(aa20, 10);
        int c21 = RL(((cc20 + f4(d20, e20, a21)) + this.X[1]) - 1894007588, 11) + b20;
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
        int cc21 = RL(c20 + f2(dd20, ee20, aa21) + this.X[8] + 2053994217, 15) + bb20;
        int ee21 = RL(ee20, 10);
        int bb21 = RL(bb20 + f2(cc21, dd20, ee21) + this.X[6] + 2053994217, 5) + aa21;
        int dd21 = RL(dd20, 10);
        int aa22 = RL(aa21 + f2(bb21, cc21, dd21) + this.X[4] + 2053994217, 8) + ee21;
        int cc22 = RL(cc21, 10);
        int ee22 = RL(ee21 + f2(aa22, bb21, cc22) + this.X[1] + 2053994217, 11) + dd21;
        int bb22 = RL(bb21, 10);
        int dd22 = RL(dd21 + f2(ee22, aa22, bb22) + this.X[3] + 2053994217, 14) + cc22;
        int aa23 = RL(aa22, 10);
        int cc23 = RL(cc22 + f2(dd22, ee22, aa23) + this.X[11] + 2053994217, 14) + bb22;
        int ee23 = RL(ee22, 10);
        int bb23 = RL(bb22 + f2(cc23, dd22, ee23) + this.X[15] + 2053994217, 6) + aa23;
        int dd23 = RL(dd22, 10);
        int aa24 = RL(aa23 + f2(bb23, cc23, dd23) + this.X[0] + 2053994217, 14) + ee23;
        int cc24 = RL(cc23, 10);
        int ee24 = RL(ee23 + f2(aa24, bb23, cc24) + this.X[5] + 2053994217, 6) + dd23;
        int bb24 = RL(bb23, 10);
        int dd24 = RL(dd23 + f2(ee24, aa24, bb24) + this.X[12] + 2053994217, 9) + cc24;
        int aa25 = RL(aa24, 10);
        int cc25 = RL(cc24 + f2(dd24, ee24, aa25) + this.X[2] + 2053994217, 12) + bb24;
        int ee25 = RL(ee24, 10);
        int bb25 = RL(bb24 + f2(cc25, dd24, ee25) + this.X[13] + 2053994217, 9) + aa25;
        int dd25 = RL(dd24, 10);
        int aa26 = RL(aa25 + f2(bb25, cc25, dd25) + this.X[9] + 2053994217, 12) + ee25;
        int cc26 = RL(cc25, 10);
        int ee26 = RL(ee25 + f2(aa26, bb25, cc26) + this.X[7] + 2053994217, 5) + dd25;
        int bb26 = RL(bb25, 10);
        int dd26 = RL(dd25 + f2(ee26, aa26, bb26) + this.X[10] + 2053994217, 15) + cc26;
        int aa27 = RL(aa26, 10);
        int cc27 = RL(cc26 + f2(dd26, ee26, aa27) + this.X[14] + 2053994217, 8) + bb26;
        int ee27 = RL(ee26, 10);
        int b27 = RL(((b26 + f5(c27, dd26, e27)) + this.X[4]) - 1454113458, 9) + a27;
        int d27 = RL(dd26, 10);
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
        int bb27 = RL(bb26 + f1(cc27, d26, ee27) + this.X[12], 8) + aa27;
        int dd27 = RL(d26, 10);
        int aa28 = RL(aa27 + f1(bb27, cc27, dd27) + this.X[15], 5) + ee27;
        int cc28 = RL(cc27, 10);
        int ee28 = RL(ee27 + f1(aa28, bb27, cc28) + this.X[10], 12) + dd27;
        int bb28 = RL(bb27, 10);
        int dd28 = RL(dd27 + f1(ee28, aa28, bb28) + this.X[4], 9) + cc28;
        int aa29 = RL(aa28, 10);
        int cc29 = RL(cc28 + f1(dd28, ee28, aa29) + this.X[1], 12) + bb28;
        int ee29 = RL(ee28, 10);
        int bb29 = RL(bb28 + f1(cc29, dd28, ee29) + this.X[5], 5) + aa29;
        int dd29 = RL(dd28, 10);
        int aa30 = RL(aa29 + f1(bb29, cc29, dd29) + this.X[8], 14) + ee29;
        int cc30 = RL(cc29, 10);
        int ee30 = RL(ee29 + f1(aa30, bb29, cc30) + this.X[7], 6) + dd29;
        int bb30 = RL(bb29, 10);
        int dd30 = RL(dd29 + f1(ee30, aa30, bb30) + this.X[6], 8) + cc30;
        int aa31 = RL(aa30, 10);
        int cc31 = RL(cc30 + f1(dd30, ee30, aa31) + this.X[2], 13) + bb30;
        int ee31 = RL(ee30, 10);
        int bb31 = RL(bb30 + f1(cc31, dd30, ee31) + this.X[13], 6) + aa31;
        int dd31 = RL(dd30, 10);
        int aa32 = RL(aa31 + f1(bb31, cc31, dd31) + this.X[14], 5) + ee31;
        int cc32 = RL(cc31, 10);
        int ee32 = RL(ee31 + f1(aa32, bb31, cc32) + this.X[0], 15) + dd31;
        int bb32 = RL(bb31, 10);
        int dd32 = RL(dd31 + f1(ee32, aa32, bb32) + this.X[3], 13) + cc32;
        int aa33 = RL(aa32, 10);
        int cc33 = RL(cc32 + f1(dd32, ee32, aa33) + this.X[9], 11) + bb32;
        int ee33 = RL(ee32, 10);
        int bb33 = RL(bb32 + f1(cc33, dd32, ee33) + this.X[11], 11) + aa33;
        int dd33 = RL(dd32, 10);
        this.H0 += a33;
        this.H1 += b33;
        this.H2 += c33;
        this.H3 += d33;
        this.H4 += ee33;
        this.H5 += aa33;
        this.H6 += bb33;
        this.H7 += cc33;
        this.H8 += dd33;
        this.H9 += e33;
        this.xOff = 0;
        for (int i = 0; i != this.X.length; i++) {
            this.X[i] = 0;
        }
    }

    @Override // org.spongycastle.util.Memoable
    public Memoable copy() {
        return new RIPEMD320Digest(this);
    }

    @Override // org.spongycastle.util.Memoable
    public void reset(Memoable other) {
        RIPEMD320Digest d = (RIPEMD320Digest) other;
        doCopy(d);
    }
}
