package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/MD4Digest.class */
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
        this(CryptoServicePurpose.ANY);
    }

    public MD4Digest(CryptoServicePurpose cryptoServicePurpose) {
        super(cryptoServicePurpose);
        this.X = new int[16];
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, 64, cryptoServicePurpose));
        reset();
    }

    public MD4Digest(MD4Digest mD4Digest) {
        super(mD4Digest.purpose);
        this.X = new int[16];
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, 64, this.purpose));
        copyIn(mD4Digest);
    }

    private void copyIn(MD4Digest mD4Digest) {
        super.copyIn((GeneralDigest) mD4Digest);
        this.H1 = mD4Digest.H1;
        this.H2 = mD4Digest.H2;
        this.H3 = mD4Digest.H3;
        this.H4 = mD4Digest.H4;
        System.arraycopy(mD4Digest.X, 0, this.X, 0, mD4Digest.X.length);
        this.xOff = mD4Digest.xOff;
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "MD4";
    }

    @Override // org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return 16;
    }

    @Override // org.bouncycastle.crypto.digests.GeneralDigest
    protected void processWord(byte[] bArr, int i) {
        int[] iArr = this.X;
        int i2 = this.xOff;
        this.xOff = i2 + 1;
        iArr[i2] = Pack.littleEndianToInt(bArr, i);
        if (this.xOff == 16) {
            processBlock();
        }
    }

    @Override // org.bouncycastle.crypto.digests.GeneralDigest
    protected void processLength(long j) {
        if (this.xOff > 14) {
            processBlock();
        }
        this.X[14] = (int) (j & (-1));
        this.X[15] = (int) (j >>> 32);
    }

    @Override // org.bouncycastle.crypto.Digest
    public int doFinal(byte[] bArr, int i) {
        finish();
        Pack.intToLittleEndian(this.H1, bArr, i);
        Pack.intToLittleEndian(this.H2, bArr, i + 4);
        Pack.intToLittleEndian(this.H3, bArr, i + 8);
        Pack.intToLittleEndian(this.H4, bArr, i + 12);
        reset();
        return 16;
    }

    @Override // org.bouncycastle.crypto.digests.GeneralDigest, org.bouncycastle.crypto.Digest
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

    private int rotateLeft(int i, int i2) {
        return (i << i2) | (i >>> (32 - i2));
    }

    private int F(int i, int i2, int i3) {
        return (i & i2) | ((i ^ (-1)) & i3);
    }

    private int G(int i, int i2, int i3) {
        return (i & i2) | (i & i3) | (i2 & i3);
    }

    private int H(int i, int i2, int i3) {
        return (i ^ i2) ^ i3;
    }

    @Override // org.bouncycastle.crypto.digests.GeneralDigest
    protected void processBlock() {
        int i = this.H1;
        int i2 = this.H2;
        int i3 = this.H3;
        int i4 = this.H4;
        int iRotateLeft = rotateLeft(i + F(i2, i3, i4) + this.X[0], 3);
        int iRotateLeft2 = rotateLeft(i4 + F(iRotateLeft, i2, i3) + this.X[1], 7);
        int iRotateLeft3 = rotateLeft(i3 + F(iRotateLeft2, iRotateLeft, i2) + this.X[2], 11);
        int iRotateLeft4 = rotateLeft(i2 + F(iRotateLeft3, iRotateLeft2, iRotateLeft) + this.X[3], 19);
        int iRotateLeft5 = rotateLeft(iRotateLeft + F(iRotateLeft4, iRotateLeft3, iRotateLeft2) + this.X[4], 3);
        int iRotateLeft6 = rotateLeft(iRotateLeft2 + F(iRotateLeft5, iRotateLeft4, iRotateLeft3) + this.X[5], 7);
        int iRotateLeft7 = rotateLeft(iRotateLeft3 + F(iRotateLeft6, iRotateLeft5, iRotateLeft4) + this.X[6], 11);
        int iRotateLeft8 = rotateLeft(iRotateLeft4 + F(iRotateLeft7, iRotateLeft6, iRotateLeft5) + this.X[7], 19);
        int iRotateLeft9 = rotateLeft(iRotateLeft5 + F(iRotateLeft8, iRotateLeft7, iRotateLeft6) + this.X[8], 3);
        int iRotateLeft10 = rotateLeft(iRotateLeft6 + F(iRotateLeft9, iRotateLeft8, iRotateLeft7) + this.X[9], 7);
        int iRotateLeft11 = rotateLeft(iRotateLeft7 + F(iRotateLeft10, iRotateLeft9, iRotateLeft8) + this.X[10], 11);
        int iRotateLeft12 = rotateLeft(iRotateLeft8 + F(iRotateLeft11, iRotateLeft10, iRotateLeft9) + this.X[11], 19);
        int iRotateLeft13 = rotateLeft(iRotateLeft9 + F(iRotateLeft12, iRotateLeft11, iRotateLeft10) + this.X[12], 3);
        int iRotateLeft14 = rotateLeft(iRotateLeft10 + F(iRotateLeft13, iRotateLeft12, iRotateLeft11) + this.X[13], 7);
        int iRotateLeft15 = rotateLeft(iRotateLeft11 + F(iRotateLeft14, iRotateLeft13, iRotateLeft12) + this.X[14], 11);
        int iRotateLeft16 = rotateLeft(iRotateLeft12 + F(iRotateLeft15, iRotateLeft14, iRotateLeft13) + this.X[15], 19);
        int iRotateLeft17 = rotateLeft(iRotateLeft13 + G(iRotateLeft16, iRotateLeft15, iRotateLeft14) + this.X[0] + 1518500249, 3);
        int iRotateLeft18 = rotateLeft(iRotateLeft14 + G(iRotateLeft17, iRotateLeft16, iRotateLeft15) + this.X[4] + 1518500249, 5);
        int iRotateLeft19 = rotateLeft(iRotateLeft15 + G(iRotateLeft18, iRotateLeft17, iRotateLeft16) + this.X[8] + 1518500249, 9);
        int iRotateLeft20 = rotateLeft(iRotateLeft16 + G(iRotateLeft19, iRotateLeft18, iRotateLeft17) + this.X[12] + 1518500249, 13);
        int iRotateLeft21 = rotateLeft(iRotateLeft17 + G(iRotateLeft20, iRotateLeft19, iRotateLeft18) + this.X[1] + 1518500249, 3);
        int iRotateLeft22 = rotateLeft(iRotateLeft18 + G(iRotateLeft21, iRotateLeft20, iRotateLeft19) + this.X[5] + 1518500249, 5);
        int iRotateLeft23 = rotateLeft(iRotateLeft19 + G(iRotateLeft22, iRotateLeft21, iRotateLeft20) + this.X[9] + 1518500249, 9);
        int iRotateLeft24 = rotateLeft(iRotateLeft20 + G(iRotateLeft23, iRotateLeft22, iRotateLeft21) + this.X[13] + 1518500249, 13);
        int iRotateLeft25 = rotateLeft(iRotateLeft21 + G(iRotateLeft24, iRotateLeft23, iRotateLeft22) + this.X[2] + 1518500249, 3);
        int iRotateLeft26 = rotateLeft(iRotateLeft22 + G(iRotateLeft25, iRotateLeft24, iRotateLeft23) + this.X[6] + 1518500249, 5);
        int iRotateLeft27 = rotateLeft(iRotateLeft23 + G(iRotateLeft26, iRotateLeft25, iRotateLeft24) + this.X[10] + 1518500249, 9);
        int iRotateLeft28 = rotateLeft(iRotateLeft24 + G(iRotateLeft27, iRotateLeft26, iRotateLeft25) + this.X[14] + 1518500249, 13);
        int iRotateLeft29 = rotateLeft(iRotateLeft25 + G(iRotateLeft28, iRotateLeft27, iRotateLeft26) + this.X[3] + 1518500249, 3);
        int iRotateLeft30 = rotateLeft(iRotateLeft26 + G(iRotateLeft29, iRotateLeft28, iRotateLeft27) + this.X[7] + 1518500249, 5);
        int iRotateLeft31 = rotateLeft(iRotateLeft27 + G(iRotateLeft30, iRotateLeft29, iRotateLeft28) + this.X[11] + 1518500249, 9);
        int iRotateLeft32 = rotateLeft(iRotateLeft28 + G(iRotateLeft31, iRotateLeft30, iRotateLeft29) + this.X[15] + 1518500249, 13);
        int iRotateLeft33 = rotateLeft(iRotateLeft29 + H(iRotateLeft32, iRotateLeft31, iRotateLeft30) + this.X[0] + 1859775393, 3);
        int iRotateLeft34 = rotateLeft(iRotateLeft30 + H(iRotateLeft33, iRotateLeft32, iRotateLeft31) + this.X[8] + 1859775393, 9);
        int iRotateLeft35 = rotateLeft(iRotateLeft31 + H(iRotateLeft34, iRotateLeft33, iRotateLeft32) + this.X[4] + 1859775393, 11);
        int iRotateLeft36 = rotateLeft(iRotateLeft32 + H(iRotateLeft35, iRotateLeft34, iRotateLeft33) + this.X[12] + 1859775393, 15);
        int iRotateLeft37 = rotateLeft(iRotateLeft33 + H(iRotateLeft36, iRotateLeft35, iRotateLeft34) + this.X[2] + 1859775393, 3);
        int iRotateLeft38 = rotateLeft(iRotateLeft34 + H(iRotateLeft37, iRotateLeft36, iRotateLeft35) + this.X[10] + 1859775393, 9);
        int iRotateLeft39 = rotateLeft(iRotateLeft35 + H(iRotateLeft38, iRotateLeft37, iRotateLeft36) + this.X[6] + 1859775393, 11);
        int iRotateLeft40 = rotateLeft(iRotateLeft36 + H(iRotateLeft39, iRotateLeft38, iRotateLeft37) + this.X[14] + 1859775393, 15);
        int iRotateLeft41 = rotateLeft(iRotateLeft37 + H(iRotateLeft40, iRotateLeft39, iRotateLeft38) + this.X[1] + 1859775393, 3);
        int iRotateLeft42 = rotateLeft(iRotateLeft38 + H(iRotateLeft41, iRotateLeft40, iRotateLeft39) + this.X[9] + 1859775393, 9);
        int iRotateLeft43 = rotateLeft(iRotateLeft39 + H(iRotateLeft42, iRotateLeft41, iRotateLeft40) + this.X[5] + 1859775393, 11);
        int iRotateLeft44 = rotateLeft(iRotateLeft40 + H(iRotateLeft43, iRotateLeft42, iRotateLeft41) + this.X[13] + 1859775393, 15);
        int iRotateLeft45 = rotateLeft(iRotateLeft41 + H(iRotateLeft44, iRotateLeft43, iRotateLeft42) + this.X[3] + 1859775393, 3);
        int iRotateLeft46 = rotateLeft(iRotateLeft42 + H(iRotateLeft45, iRotateLeft44, iRotateLeft43) + this.X[11] + 1859775393, 9);
        int iRotateLeft47 = rotateLeft(iRotateLeft43 + H(iRotateLeft46, iRotateLeft45, iRotateLeft44) + this.X[7] + 1859775393, 11);
        int iRotateLeft48 = rotateLeft(iRotateLeft44 + H(iRotateLeft47, iRotateLeft46, iRotateLeft45) + this.X[15] + 1859775393, 15);
        this.H1 += iRotateLeft45;
        this.H2 += iRotateLeft48;
        this.H3 += iRotateLeft47;
        this.H4 += iRotateLeft46;
        this.xOff = 0;
        for (int i5 = 0; i5 != this.X.length; i5++) {
            this.X[i5] = 0;
        }
    }

    @Override // org.bouncycastle.util.Memoable
    public Memoable copy() {
        return new MD4Digest(this);
    }

    @Override // org.bouncycastle.util.Memoable
    public void reset(Memoable memoable) {
        copyIn((MD4Digest) memoable);
    }

    @Override // org.bouncycastle.crypto.digests.GeneralDigest
    protected CryptoServiceProperties cryptoServiceProperties() {
        return Utils.getDefaultProperties(this, this.purpose);
    }
}
