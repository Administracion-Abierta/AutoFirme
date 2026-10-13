package org.spongycastle.crypto.digests;

import org.spongycastle.util.Memoable;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/digests/SHA1Digest.class */
public class SHA1Digest extends GeneralDigest implements EncodableDigest {
    private static final int DIGEST_LENGTH = 20;
    private int H1;
    private int H2;
    private int H3;
    private int H4;
    private int H5;
    private int[] X;
    private int xOff;
    private static final int Y1 = 1518500249;
    private static final int Y2 = 1859775393;
    private static final int Y3 = -1894007588;
    private static final int Y4 = -899497514;

    public SHA1Digest() {
        this.X = new int[80];
        reset();
    }

    public SHA1Digest(SHA1Digest t) {
        super(t);
        this.X = new int[80];
        copyIn(t);
    }

    public SHA1Digest(byte[] encodedState) {
        super(encodedState);
        this.X = new int[80];
        this.H1 = Pack.bigEndianToInt(encodedState, 16);
        this.H2 = Pack.bigEndianToInt(encodedState, 20);
        this.H3 = Pack.bigEndianToInt(encodedState, 24);
        this.H4 = Pack.bigEndianToInt(encodedState, 28);
        this.H5 = Pack.bigEndianToInt(encodedState, 32);
        this.xOff = Pack.bigEndianToInt(encodedState, 36);
        for (int i = 0; i != this.xOff; i++) {
            this.X[i] = Pack.bigEndianToInt(encodedState, 40 + (i * 4));
        }
    }

    private void copyIn(SHA1Digest t) {
        this.H1 = t.H1;
        this.H2 = t.H2;
        this.H3 = t.H3;
        this.H4 = t.H4;
        this.H5 = t.H5;
        System.arraycopy(t.X, 0, this.X, 0, t.X.length);
        this.xOff = t.xOff;
    }

    @Override // org.spongycastle.crypto.Digest
    public String getAlgorithmName() {
        return "SHA-1";
    }

    @Override // org.spongycastle.crypto.Digest
    public int getDigestSize() {
        return 20;
    }

    @Override // org.spongycastle.crypto.digests.GeneralDigest
    protected void processWord(byte[] in, int inOff) {
        int n = in[inOff] << 24;
        int inOff2 = inOff + 1;
        int n2 = n | ((in[inOff2] & 255) << 16);
        int inOff3 = inOff2 + 1;
        this.X[this.xOff] = n2 | ((in[inOff3] & 255) << 8) | (in[inOff3 + 1] & 255);
        int i = this.xOff + 1;
        this.xOff = i;
        if (i == 16) {
            processBlock();
        }
    }

    @Override // org.spongycastle.crypto.digests.GeneralDigest
    protected void processLength(long bitLength) {
        if (this.xOff > 14) {
            processBlock();
        }
        this.X[14] = (int) (bitLength >>> 32);
        this.X[15] = (int) (bitLength & (-1));
    }

    @Override // org.spongycastle.crypto.Digest
    public int doFinal(byte[] out, int outOff) {
        finish();
        Pack.intToBigEndian(this.H1, out, outOff);
        Pack.intToBigEndian(this.H2, out, outOff + 4);
        Pack.intToBigEndian(this.H3, out, outOff + 8);
        Pack.intToBigEndian(this.H4, out, outOff + 12);
        Pack.intToBigEndian(this.H5, out, outOff + 16);
        reset();
        return 20;
    }

    @Override // org.spongycastle.crypto.digests.GeneralDigest, org.spongycastle.crypto.Digest
    public void reset() {
        super.reset();
        this.H1 = 1732584193;
        this.H2 = -271733879;
        this.H3 = -1732584194;
        this.H4 = 271733878;
        this.H5 = -1009589776;
        this.xOff = 0;
        for (int i = 0; i != this.X.length; i++) {
            this.X[i] = 0;
        }
    }

    private int f(int u, int v, int w) {
        return (u & v) | ((u ^ (-1)) & w);
    }

    private int h(int u, int v, int w) {
        return (u ^ v) ^ w;
    }

    private int g(int u, int v, int w) {
        return (u & v) | (u & w) | (v & w);
    }

    @Override // org.spongycastle.crypto.digests.GeneralDigest
    protected void processBlock() {
        for (int i = 16; i < 80; i++) {
            int t = ((this.X[i - 3] ^ this.X[i - 8]) ^ this.X[i - 14]) ^ this.X[i - 16];
            this.X[i] = (t << 1) | (t >>> 31);
        }
        int A = this.H1;
        int B = this.H2;
        int C = this.H3;
        int D = this.H4;
        int E = this.H5;
        int idx = 0;
        for (int j = 0; j < 4; j++) {
            int i2 = idx;
            int idx2 = idx + 1;
            int E2 = E + ((A << 5) | (A >>> 27)) + f(B, C, D) + this.X[i2] + Y1;
            int B2 = (B << 30) | (B >>> 2);
            int idx3 = idx2 + 1;
            int D2 = D + ((E2 << 5) | (E2 >>> 27)) + f(A, B2, C) + this.X[idx2] + Y1;
            int A2 = (A << 30) | (A >>> 2);
            int idx4 = idx3 + 1;
            int C2 = C + ((D2 << 5) | (D2 >>> 27)) + f(E2, A2, B2) + this.X[idx3] + Y1;
            E = (E2 << 30) | (E2 >>> 2);
            int idx5 = idx4 + 1;
            B = B2 + ((C2 << 5) | (C2 >>> 27)) + f(D2, E, A2) + this.X[idx4] + Y1;
            D = (D2 << 30) | (D2 >>> 2);
            idx = idx5 + 1;
            A = A2 + ((B << 5) | (B >>> 27)) + f(C2, D, E) + this.X[idx5] + Y1;
            C = (C2 << 30) | (C2 >>> 2);
        }
        for (int j2 = 0; j2 < 4; j2++) {
            int i3 = idx;
            int idx6 = idx + 1;
            int E3 = E + ((A << 5) | (A >>> 27)) + h(B, C, D) + this.X[i3] + Y2;
            int B3 = (B << 30) | (B >>> 2);
            int idx7 = idx6 + 1;
            int D3 = D + ((E3 << 5) | (E3 >>> 27)) + h(A, B3, C) + this.X[idx6] + Y2;
            int A3 = (A << 30) | (A >>> 2);
            int idx8 = idx7 + 1;
            int C3 = C + ((D3 << 5) | (D3 >>> 27)) + h(E3, A3, B3) + this.X[idx7] + Y2;
            E = (E3 << 30) | (E3 >>> 2);
            int idx9 = idx8 + 1;
            B = B3 + ((C3 << 5) | (C3 >>> 27)) + h(D3, E, A3) + this.X[idx8] + Y2;
            D = (D3 << 30) | (D3 >>> 2);
            idx = idx9 + 1;
            A = A3 + ((B << 5) | (B >>> 27)) + h(C3, D, E) + this.X[idx9] + Y2;
            C = (C3 << 30) | (C3 >>> 2);
        }
        for (int j3 = 0; j3 < 4; j3++) {
            int i4 = idx;
            int idx10 = idx + 1;
            int E4 = E + ((A << 5) | (A >>> 27)) + g(B, C, D) + this.X[i4] + Y3;
            int B4 = (B << 30) | (B >>> 2);
            int idx11 = idx10 + 1;
            int D4 = D + ((E4 << 5) | (E4 >>> 27)) + g(A, B4, C) + this.X[idx10] + Y3;
            int A4 = (A << 30) | (A >>> 2);
            int idx12 = idx11 + 1;
            int C4 = C + ((D4 << 5) | (D4 >>> 27)) + g(E4, A4, B4) + this.X[idx11] + Y3;
            E = (E4 << 30) | (E4 >>> 2);
            int idx13 = idx12 + 1;
            B = B4 + ((C4 << 5) | (C4 >>> 27)) + g(D4, E, A4) + this.X[idx12] + Y3;
            D = (D4 << 30) | (D4 >>> 2);
            idx = idx13 + 1;
            A = A4 + ((B << 5) | (B >>> 27)) + g(C4, D, E) + this.X[idx13] + Y3;
            C = (C4 << 30) | (C4 >>> 2);
        }
        for (int j4 = 0; j4 <= 3; j4++) {
            int i5 = idx;
            int idx14 = idx + 1;
            int E5 = E + ((A << 5) | (A >>> 27)) + h(B, C, D) + this.X[i5] + Y4;
            int B5 = (B << 30) | (B >>> 2);
            int idx15 = idx14 + 1;
            int D5 = D + ((E5 << 5) | (E5 >>> 27)) + h(A, B5, C) + this.X[idx14] + Y4;
            int A5 = (A << 30) | (A >>> 2);
            int idx16 = idx15 + 1;
            int C5 = C + ((D5 << 5) | (D5 >>> 27)) + h(E5, A5, B5) + this.X[idx15] + Y4;
            E = (E5 << 30) | (E5 >>> 2);
            int idx17 = idx16 + 1;
            B = B5 + ((C5 << 5) | (C5 >>> 27)) + h(D5, E, A5) + this.X[idx16] + Y4;
            D = (D5 << 30) | (D5 >>> 2);
            idx = idx17 + 1;
            A = A5 + ((B << 5) | (B >>> 27)) + h(C5, D, E) + this.X[idx17] + Y4;
            C = (C5 << 30) | (C5 >>> 2);
        }
        this.H1 += A;
        this.H2 += B;
        this.H3 += C;
        this.H4 += D;
        this.H5 += E;
        this.xOff = 0;
        for (int i6 = 0; i6 < 16; i6++) {
            this.X[i6] = 0;
        }
    }

    @Override // org.spongycastle.util.Memoable
    public Memoable copy() {
        return new SHA1Digest(this);
    }

    @Override // org.spongycastle.util.Memoable
    public void reset(Memoable other) {
        SHA1Digest d = (SHA1Digest) other;
        super.copyIn((GeneralDigest) d);
        copyIn(d);
    }

    @Override // org.spongycastle.crypto.digests.EncodableDigest
    public byte[] getEncodedState() {
        byte[] state = new byte[40 + (this.xOff * 4)];
        super.populateState(state);
        Pack.intToBigEndian(this.H1, state, 16);
        Pack.intToBigEndian(this.H2, state, 20);
        Pack.intToBigEndian(this.H3, state, 24);
        Pack.intToBigEndian(this.H4, state, 28);
        Pack.intToBigEndian(this.H5, state, 32);
        Pack.intToBigEndian(this.xOff, state, 36);
        for (int i = 0; i != this.xOff; i++) {
            Pack.intToBigEndian(this.X[i], state, 40 + (i * 4));
        }
        return state;
    }
}
