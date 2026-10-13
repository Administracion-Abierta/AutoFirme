package org.spongycastle.crypto.digests;

import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.spongycastle.util.Memoable;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/digests/MD5Digest.class */
public class MD5Digest extends GeneralDigest implements EncodableDigest {
    private static final int DIGEST_LENGTH = 16;
    private int H1;
    private int H2;
    private int H3;
    private int H4;
    private int[] X;
    private int xOff;
    private static final int S11 = 7;
    private static final int S12 = 12;
    private static final int S13 = 17;
    private static final int S14 = 22;
    private static final int S21 = 5;
    private static final int S22 = 9;
    private static final int S23 = 14;
    private static final int S24 = 20;
    private static final int S31 = 4;
    private static final int S32 = 11;
    private static final int S33 = 16;
    private static final int S34 = 23;
    private static final int S41 = 6;
    private static final int S42 = 10;
    private static final int S43 = 15;
    private static final int S44 = 21;

    public MD5Digest() {
        this.X = new int[16];
        reset();
    }

    public MD5Digest(byte[] encodedState) {
        super(encodedState);
        this.X = new int[16];
        this.H1 = Pack.bigEndianToInt(encodedState, 16);
        this.H2 = Pack.bigEndianToInt(encodedState, 20);
        this.H3 = Pack.bigEndianToInt(encodedState, 24);
        this.H4 = Pack.bigEndianToInt(encodedState, 28);
        this.xOff = Pack.bigEndianToInt(encodedState, 32);
        for (int i = 0; i != this.xOff; i++) {
            this.X[i] = Pack.bigEndianToInt(encodedState, 36 + (i * 4));
        }
    }

    public MD5Digest(MD5Digest t) {
        super(t);
        this.X = new int[16];
        copyIn(t);
    }

    private void copyIn(MD5Digest t) {
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
        return MessageDigestAlgorithms.MD5;
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
        return (u & w) | (v & (w ^ (-1)));
    }

    private int H(int u, int v, int w) {
        return (u ^ v) ^ w;
    }

    private int K(int u, int v, int w) {
        return v ^ (u | (w ^ (-1)));
    }

    @Override // org.spongycastle.crypto.digests.GeneralDigest
    protected void processBlock() {
        int a = this.H1;
        int b = this.H2;
        int c = this.H3;
        int d = this.H4;
        int a2 = rotateLeft(((a + F(b, c, d)) + this.X[0]) - 680876936, 7) + b;
        int d2 = rotateLeft(((d + F(a2, b, c)) + this.X[1]) - 389564586, 12) + a2;
        int c2 = rotateLeft(c + F(d2, a2, b) + this.X[2] + 606105819, 17) + d2;
        int b2 = rotateLeft(((b + F(c2, d2, a2)) + this.X[3]) - 1044525330, 22) + c2;
        int a3 = rotateLeft(((a2 + F(b2, c2, d2)) + this.X[4]) - 176418897, 7) + b2;
        int d3 = rotateLeft(d2 + F(a3, b2, c2) + this.X[5] + 1200080426, 12) + a3;
        int c3 = rotateLeft(((c2 + F(d3, a3, b2)) + this.X[6]) - 1473231341, 17) + d3;
        int b3 = rotateLeft(((b2 + F(c3, d3, a3)) + this.X[7]) - 45705983, 22) + c3;
        int a4 = rotateLeft(a3 + F(b3, c3, d3) + this.X[8] + 1770035416, 7) + b3;
        int d4 = rotateLeft(((d3 + F(a4, b3, c3)) + this.X[9]) - 1958414417, 12) + a4;
        int c4 = rotateLeft(((c3 + F(d4, a4, b3)) + this.X[10]) - 42063, 17) + d4;
        int b4 = rotateLeft(((b3 + F(c4, d4, a4)) + this.X[11]) - 1990404162, 22) + c4;
        int a5 = rotateLeft(a4 + F(b4, c4, d4) + this.X[12] + 1804603682, 7) + b4;
        int d5 = rotateLeft(((d4 + F(a5, b4, c4)) + this.X[13]) - 40341101, 12) + a5;
        int c5 = rotateLeft(((c4 + F(d5, a5, b4)) + this.X[14]) - 1502002290, 17) + d5;
        int b5 = rotateLeft(b4 + F(c5, d5, a5) + this.X[15] + 1236535329, 22) + c5;
        int a6 = rotateLeft(((a5 + G(b5, c5, d5)) + this.X[1]) - 165796510, 5) + b5;
        int d6 = rotateLeft(((d5 + G(a6, b5, c5)) + this.X[6]) - 1069501632, 9) + a6;
        int c6 = rotateLeft(c5 + G(d6, a6, b5) + this.X[11] + 643717713, 14) + d6;
        int b6 = rotateLeft(((b5 + G(c6, d6, a6)) + this.X[0]) - 373897302, 20) + c6;
        int a7 = rotateLeft(((a6 + G(b6, c6, d6)) + this.X[5]) - 701558691, 5) + b6;
        int d7 = rotateLeft(d6 + G(a7, b6, c6) + this.X[10] + 38016083, 9) + a7;
        int c7 = rotateLeft(((c6 + G(d7, a7, b6)) + this.X[15]) - 660478335, 14) + d7;
        int b7 = rotateLeft(((b6 + G(c7, d7, a7)) + this.X[4]) - 405537848, 20) + c7;
        int a8 = rotateLeft(a7 + G(b7, c7, d7) + this.X[9] + 568446438, 5) + b7;
        int d8 = rotateLeft(((d7 + G(a8, b7, c7)) + this.X[14]) - 1019803690, 9) + a8;
        int c8 = rotateLeft(((c7 + G(d8, a8, b7)) + this.X[3]) - 187363961, 14) + d8;
        int b8 = rotateLeft(b7 + G(c8, d8, a8) + this.X[8] + 1163531501, 20) + c8;
        int a9 = rotateLeft(((a8 + G(b8, c8, d8)) + this.X[13]) - 1444681467, 5) + b8;
        int d9 = rotateLeft(((d8 + G(a9, b8, c8)) + this.X[2]) - 51403784, 9) + a9;
        int c9 = rotateLeft(c8 + G(d9, a9, b8) + this.X[7] + 1735328473, 14) + d9;
        int b9 = rotateLeft(((b8 + G(c9, d9, a9)) + this.X[12]) - 1926607734, 20) + c9;
        int a10 = rotateLeft(((a9 + H(b9, c9, d9)) + this.X[5]) - 378558, 4) + b9;
        int d10 = rotateLeft(((d9 + H(a10, b9, c9)) + this.X[8]) - 2022574463, 11) + a10;
        int c10 = rotateLeft(c9 + H(d10, a10, b9) + this.X[11] + 1839030562, 16) + d10;
        int b10 = rotateLeft(((b9 + H(c10, d10, a10)) + this.X[14]) - 35309556, 23) + c10;
        int a11 = rotateLeft(((a10 + H(b10, c10, d10)) + this.X[1]) - 1530992060, 4) + b10;
        int d11 = rotateLeft(d10 + H(a11, b10, c10) + this.X[4] + 1272893353, 11) + a11;
        int c11 = rotateLeft(((c10 + H(d11, a11, b10)) + this.X[7]) - 155497632, 16) + d11;
        int b11 = rotateLeft(((b10 + H(c11, d11, a11)) + this.X[10]) - 1094730640, 23) + c11;
        int a12 = rotateLeft(a11 + H(b11, c11, d11) + this.X[13] + 681279174, 4) + b11;
        int d12 = rotateLeft(((d11 + H(a12, b11, c11)) + this.X[0]) - 358537222, 11) + a12;
        int c12 = rotateLeft(((c11 + H(d12, a12, b11)) + this.X[3]) - 722521979, 16) + d12;
        int b12 = rotateLeft(b11 + H(c12, d12, a12) + this.X[6] + 76029189, 23) + c12;
        int a13 = rotateLeft(((a12 + H(b12, c12, d12)) + this.X[9]) - 640364487, 4) + b12;
        int d13 = rotateLeft(((d12 + H(a13, b12, c12)) + this.X[12]) - 421815835, 11) + a13;
        int c13 = rotateLeft(c12 + H(d13, a13, b12) + this.X[15] + 530742520, 16) + d13;
        int b13 = rotateLeft(((b12 + H(c13, d13, a13)) + this.X[2]) - 995338651, 23) + c13;
        int a14 = rotateLeft(((a13 + K(b13, c13, d13)) + this.X[0]) - 198630844, 6) + b13;
        int d14 = rotateLeft(d13 + K(a14, b13, c13) + this.X[7] + 1126891415, 10) + a14;
        int c14 = rotateLeft(((c13 + K(d14, a14, b13)) + this.X[14]) - 1416354905, 15) + d14;
        int b14 = rotateLeft(((b13 + K(c14, d14, a14)) + this.X[5]) - 57434055, 21) + c14;
        int a15 = rotateLeft(a14 + K(b14, c14, d14) + this.X[12] + 1700485571, 6) + b14;
        int d15 = rotateLeft(((d14 + K(a15, b14, c14)) + this.X[3]) - 1894986606, 10) + a15;
        int c15 = rotateLeft(((c14 + K(d15, a15, b14)) + this.X[10]) - 1051523, 15) + d15;
        int b15 = rotateLeft(((b14 + K(c15, d15, a15)) + this.X[1]) - 2054922799, 21) + c15;
        int a16 = rotateLeft(a15 + K(b15, c15, d15) + this.X[8] + 1873313359, 6) + b15;
        int d16 = rotateLeft(((d15 + K(a16, b15, c15)) + this.X[15]) - 30611744, 10) + a16;
        int c16 = rotateLeft(((c15 + K(d16, a16, b15)) + this.X[6]) - 1560198380, 15) + d16;
        int b16 = rotateLeft(b15 + K(c16, d16, a16) + this.X[13] + 1309151649, 21) + c16;
        int a17 = rotateLeft(((a16 + K(b16, c16, d16)) + this.X[4]) - 145523070, 6) + b16;
        int d17 = rotateLeft(((d16 + K(a17, b16, c16)) + this.X[11]) - 1120210379, 10) + a17;
        int c17 = rotateLeft(c16 + K(d17, a17, b16) + this.X[2] + 718787259, 15) + d17;
        int b17 = rotateLeft(((b16 + K(c17, d17, a17)) + this.X[9]) - 343485551, 21) + c17;
        this.H1 += a17;
        this.H2 += b17;
        this.H3 += c17;
        this.H4 += d17;
        this.xOff = 0;
        for (int i = 0; i != this.X.length; i++) {
            this.X[i] = 0;
        }
    }

    @Override // org.spongycastle.util.Memoable
    public Memoable copy() {
        return new MD5Digest(this);
    }

    @Override // org.spongycastle.util.Memoable
    public void reset(Memoable other) {
        MD5Digest d = (MD5Digest) other;
        copyIn(d);
    }

    @Override // org.spongycastle.crypto.digests.EncodableDigest
    public byte[] getEncodedState() {
        byte[] state = new byte[36 + (this.xOff * 4)];
        super.populateState(state);
        Pack.intToBigEndian(this.H1, state, 16);
        Pack.intToBigEndian(this.H2, state, 20);
        Pack.intToBigEndian(this.H3, state, 24);
        Pack.intToBigEndian(this.H4, state, 28);
        Pack.intToBigEndian(this.xOff, state, 32);
        for (int i = 0; i != this.xOff; i++) {
            Pack.intToBigEndian(this.X[i], state, 36 + (i * 4));
        }
        return state;
    }
}
