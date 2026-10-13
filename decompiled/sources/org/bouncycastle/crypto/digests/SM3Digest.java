package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/SM3Digest.class */
public class SM3Digest extends GeneralDigest {
    private static final int DIGEST_LENGTH = 32;
    private static final int BLOCK_SIZE = 16;
    private int[] V;
    private int[] inwords;
    private int xOff;
    private int[] W;
    private static final int[] T = new int[64];

    public SM3Digest() {
        this(CryptoServicePurpose.ANY);
    }

    public SM3Digest(CryptoServicePurpose cryptoServicePurpose) {
        super(cryptoServicePurpose);
        this.V = new int[8];
        this.inwords = new int[16];
        this.W = new int[68];
        CryptoServicesRegistrar.checkConstraints(cryptoServiceProperties());
        reset();
    }

    public SM3Digest(SM3Digest sM3Digest) {
        super(sM3Digest);
        this.V = new int[8];
        this.inwords = new int[16];
        this.W = new int[68];
        CryptoServicesRegistrar.checkConstraints(cryptoServiceProperties());
        copyIn(sM3Digest);
    }

    private void copyIn(SM3Digest sM3Digest) {
        System.arraycopy(sM3Digest.V, 0, this.V, 0, this.V.length);
        System.arraycopy(sM3Digest.inwords, 0, this.inwords, 0, this.inwords.length);
        this.xOff = sM3Digest.xOff;
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "SM3";
    }

    @Override // org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return 32;
    }

    @Override // org.bouncycastle.util.Memoable
    public Memoable copy() {
        return new SM3Digest(this);
    }

    @Override // org.bouncycastle.util.Memoable
    public void reset(Memoable memoable) {
        SM3Digest sM3Digest = (SM3Digest) memoable;
        super.copyIn((GeneralDigest) sM3Digest);
        copyIn(sM3Digest);
    }

    @Override // org.bouncycastle.crypto.digests.GeneralDigest, org.bouncycastle.crypto.Digest
    public void reset() {
        super.reset();
        this.V[0] = 1937774191;
        this.V[1] = 1226093241;
        this.V[2] = 388252375;
        this.V[3] = -628488704;
        this.V[4] = -1452330820;
        this.V[5] = 372324522;
        this.V[6] = -477237683;
        this.V[7] = -1325724082;
        this.xOff = 0;
    }

    @Override // org.bouncycastle.crypto.Digest
    public int doFinal(byte[] bArr, int i) {
        finish();
        Pack.intToBigEndian(this.V, bArr, i);
        reset();
        return 32;
    }

    @Override // org.bouncycastle.crypto.digests.GeneralDigest
    protected void processWord(byte[] bArr, int i) {
        int[] iArr = this.inwords;
        int i2 = this.xOff;
        this.xOff = i2 + 1;
        iArr[i2] = Pack.bigEndianToInt(bArr, i);
        if (this.xOff >= 16) {
            processBlock();
        }
    }

    @Override // org.bouncycastle.crypto.digests.GeneralDigest
    protected void processLength(long j) {
        if (this.xOff > 14) {
            this.inwords[this.xOff] = 0;
            this.xOff++;
            processBlock();
        }
        while (this.xOff < 14) {
            this.inwords[this.xOff] = 0;
            this.xOff++;
        }
        int[] iArr = this.inwords;
        int i = this.xOff;
        this.xOff = i + 1;
        iArr[i] = (int) (j >>> 32);
        int[] iArr2 = this.inwords;
        int i2 = this.xOff;
        this.xOff = i2 + 1;
        iArr2[i2] = (int) j;
    }

    private int P0(int i) {
        return (i ^ ((i << 9) | (i >>> 23))) ^ ((i << 17) | (i >>> 15));
    }

    private int P1(int i) {
        return (i ^ ((i << 15) | (i >>> 17))) ^ ((i << 23) | (i >>> 9));
    }

    private int FF0(int i, int i2, int i3) {
        return (i ^ i2) ^ i3;
    }

    private int FF1(int i, int i2, int i3) {
        return (i & i2) | (i & i3) | (i2 & i3);
    }

    private int GG0(int i, int i2, int i3) {
        return (i ^ i2) ^ i3;
    }

    private int GG1(int i, int i2, int i3) {
        return (i & i2) | ((i ^ (-1)) & i3);
    }

    @Override // org.bouncycastle.crypto.digests.GeneralDigest
    protected void processBlock() {
        for (int i = 0; i < 16; i++) {
            this.W[i] = this.inwords[i];
        }
        for (int i2 = 16; i2 < 68; i2++) {
            int i3 = this.W[i2 - 3];
            int i4 = (i3 << 15) | (i3 >>> 17);
            int i5 = this.W[i2 - 13];
            this.W[i2] = (P1((this.W[i2 - 16] ^ this.W[i2 - 9]) ^ i4) ^ ((i5 << 7) | (i5 >>> 25))) ^ this.W[i2 - 6];
        }
        int i6 = this.V[0];
        int i7 = this.V[1];
        int i8 = this.V[2];
        int i9 = this.V[3];
        int iP0 = this.V[4];
        int i10 = this.V[5];
        int i11 = this.V[6];
        int i12 = this.V[7];
        for (int i13 = 0; i13 < 16; i13++) {
            int i14 = (i6 << 12) | (i6 >>> 20);
            int i15 = i14 + iP0 + T[i13];
            int i16 = (i15 << 7) | (i15 >>> 25);
            int i17 = i16 ^ i14;
            int i18 = this.W[i13];
            int iFF0 = FF0(i6, i7, i8) + i9 + i17 + (i18 ^ this.W[i13 + 4]);
            int iGG0 = GG0(iP0, i10, i11) + i12 + i16 + i18;
            i9 = i8;
            i8 = (i7 << 9) | (i7 >>> 23);
            i7 = i6;
            i6 = iFF0;
            i12 = i11;
            i11 = (i10 << 19) | (i10 >>> 13);
            i10 = iP0;
            iP0 = P0(iGG0);
        }
        for (int i19 = 16; i19 < 64; i19++) {
            int i20 = (i6 << 12) | (i6 >>> 20);
            int i21 = i20 + iP0 + T[i19];
            int i22 = (i21 << 7) | (i21 >>> 25);
            int i23 = i22 ^ i20;
            int i24 = this.W[i19];
            int iFF1 = FF1(i6, i7, i8) + i9 + i23 + (i24 ^ this.W[i19 + 4]);
            int iGG1 = GG1(iP0, i10, i11) + i12 + i22 + i24;
            i9 = i8;
            i8 = (i7 << 9) | (i7 >>> 23);
            i7 = i6;
            i6 = iFF1;
            i12 = i11;
            i11 = (i10 << 19) | (i10 >>> 13);
            i10 = iP0;
            iP0 = P0(iGG1);
        }
        int[] iArr = this.V;
        iArr[0] = iArr[0] ^ i6;
        int[] iArr2 = this.V;
        iArr2[1] = iArr2[1] ^ i7;
        int[] iArr3 = this.V;
        iArr3[2] = iArr3[2] ^ i8;
        int[] iArr4 = this.V;
        iArr4[3] = iArr4[3] ^ i9;
        int[] iArr5 = this.V;
        iArr5[4] = iArr5[4] ^ iP0;
        int[] iArr6 = this.V;
        iArr6[5] = iArr6[5] ^ i10;
        int[] iArr7 = this.V;
        iArr7[6] = iArr7[6] ^ i11;
        int[] iArr8 = this.V;
        iArr8[7] = iArr8[7] ^ i12;
        this.xOff = 0;
    }

    @Override // org.bouncycastle.crypto.digests.GeneralDigest
    protected CryptoServiceProperties cryptoServiceProperties() {
        return Utils.getDefaultProperties(this, 256, this.purpose);
    }

    static {
        for (int i = 0; i < 16; i++) {
            T[i] = (2043430169 << i) | (2043430169 >>> (32 - i));
        }
        for (int i2 = 16; i2 < 64; i2++) {
            int i3 = i2 % 32;
            T[i2] = (2055708042 << i3) | (2055708042 >>> (32 - i3));
        }
    }
}
