package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/RIPEMD128Digest.class */
public class RIPEMD128Digest extends GeneralDigest {
    private static final int DIGEST_LENGTH = 16;
    private int H0;
    private int H1;
    private int H2;
    private int H3;
    private int[] X;
    private int xOff;

    public RIPEMD128Digest() {
        this(CryptoServicePurpose.ANY);
    }

    public RIPEMD128Digest(CryptoServicePurpose cryptoServicePurpose) {
        super(cryptoServicePurpose);
        this.X = new int[16];
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, 128, cryptoServicePurpose));
        reset();
    }

    public RIPEMD128Digest(RIPEMD128Digest rIPEMD128Digest) {
        super(rIPEMD128Digest.purpose);
        this.X = new int[16];
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, 128, this.purpose));
        copyIn(rIPEMD128Digest);
    }

    private void copyIn(RIPEMD128Digest rIPEMD128Digest) {
        super.copyIn((GeneralDigest) rIPEMD128Digest);
        this.H0 = rIPEMD128Digest.H0;
        this.H1 = rIPEMD128Digest.H1;
        this.H2 = rIPEMD128Digest.H2;
        this.H3 = rIPEMD128Digest.H3;
        System.arraycopy(rIPEMD128Digest.X, 0, this.X, 0, rIPEMD128Digest.X.length);
        this.xOff = rIPEMD128Digest.xOff;
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "RIPEMD128";
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
        Pack.intToLittleEndian(this.H0, bArr, i);
        Pack.intToLittleEndian(this.H1, bArr, i + 4);
        Pack.intToLittleEndian(this.H2, bArr, i + 8);
        Pack.intToLittleEndian(this.H3, bArr, i + 12);
        reset();
        return 16;
    }

    @Override // org.bouncycastle.crypto.digests.GeneralDigest, org.bouncycastle.crypto.Digest
    public void reset() {
        super.reset();
        this.H0 = 1732584193;
        this.H1 = -271733879;
        this.H2 = -1732584194;
        this.H3 = 271733878;
        this.xOff = 0;
        for (int i = 0; i != this.X.length; i++) {
            this.X[i] = 0;
        }
    }

    private int RL(int i, int i2) {
        return (i << i2) | (i >>> (32 - i2));
    }

    private int f1(int i, int i2, int i3) {
        return (i ^ i2) ^ i3;
    }

    private int f2(int i, int i2, int i3) {
        return (i & i2) | ((i ^ (-1)) & i3);
    }

    private int f3(int i, int i2, int i3) {
        return (i | (i2 ^ (-1))) ^ i3;
    }

    private int f4(int i, int i2, int i3) {
        return (i & i3) | (i2 & (i3 ^ (-1)));
    }

    private int F1(int i, int i2, int i3, int i4, int i5, int i6) {
        return RL(i + f1(i2, i3, i4) + i5, i6);
    }

    private int F2(int i, int i2, int i3, int i4, int i5, int i6) {
        return RL(i + f2(i2, i3, i4) + i5 + 1518500249, i6);
    }

    private int F3(int i, int i2, int i3, int i4, int i5, int i6) {
        return RL(i + f3(i2, i3, i4) + i5 + 1859775393, i6);
    }

    private int F4(int i, int i2, int i3, int i4, int i5, int i6) {
        return RL(((i + f4(i2, i3, i4)) + i5) - 1894007588, i6);
    }

    private int FF1(int i, int i2, int i3, int i4, int i5, int i6) {
        return RL(i + f1(i2, i3, i4) + i5, i6);
    }

    private int FF2(int i, int i2, int i3, int i4, int i5, int i6) {
        return RL(i + f2(i2, i3, i4) + i5 + 1836072691, i6);
    }

    private int FF3(int i, int i2, int i3, int i4, int i5, int i6) {
        return RL(i + f3(i2, i3, i4) + i5 + 1548603684, i6);
    }

    private int FF4(int i, int i2, int i3, int i4, int i5, int i6) {
        return RL(i + f4(i2, i3, i4) + i5 + 1352829926, i6);
    }

    @Override // org.bouncycastle.crypto.digests.GeneralDigest
    protected void processBlock() {
        int i = this.H0;
        int i2 = this.H1;
        int i3 = this.H2;
        int i4 = this.H3;
        int iF1 = F1(i, i2, i3, i4, this.X[0], 11);
        int iF2 = F1(i4, iF1, i2, i3, this.X[1], 14);
        int iF3 = F1(i3, iF2, iF1, i2, this.X[2], 15);
        int iF4 = F1(i2, iF3, iF2, iF1, this.X[3], 12);
        int iF5 = F1(iF1, iF4, iF3, iF2, this.X[4], 5);
        int iF6 = F1(iF2, iF5, iF4, iF3, this.X[5], 8);
        int iF7 = F1(iF3, iF6, iF5, iF4, this.X[6], 7);
        int iF8 = F1(iF4, iF7, iF6, iF5, this.X[7], 9);
        int iF9 = F1(iF5, iF8, iF7, iF6, this.X[8], 11);
        int iF10 = F1(iF6, iF9, iF8, iF7, this.X[9], 13);
        int iF11 = F1(iF7, iF10, iF9, iF8, this.X[10], 14);
        int iF12 = F1(iF8, iF11, iF10, iF9, this.X[11], 15);
        int iF13 = F1(iF9, iF12, iF11, iF10, this.X[12], 6);
        int iF14 = F1(iF10, iF13, iF12, iF11, this.X[13], 7);
        int iF15 = F1(iF11, iF14, iF13, iF12, this.X[14], 9);
        int iF16 = F1(iF12, iF15, iF14, iF13, this.X[15], 8);
        int iF17 = F2(iF13, iF16, iF15, iF14, this.X[7], 7);
        int iF18 = F2(iF14, iF17, iF16, iF15, this.X[4], 6);
        int iF19 = F2(iF15, iF18, iF17, iF16, this.X[13], 8);
        int iF20 = F2(iF16, iF19, iF18, iF17, this.X[1], 13);
        int iF21 = F2(iF17, iF20, iF19, iF18, this.X[10], 11);
        int iF22 = F2(iF18, iF21, iF20, iF19, this.X[6], 9);
        int iF23 = F2(iF19, iF22, iF21, iF20, this.X[15], 7);
        int iF24 = F2(iF20, iF23, iF22, iF21, this.X[3], 15);
        int iF25 = F2(iF21, iF24, iF23, iF22, this.X[12], 7);
        int iF26 = F2(iF22, iF25, iF24, iF23, this.X[0], 12);
        int iF27 = F2(iF23, iF26, iF25, iF24, this.X[9], 15);
        int iF28 = F2(iF24, iF27, iF26, iF25, this.X[5], 9);
        int iF29 = F2(iF25, iF28, iF27, iF26, this.X[2], 11);
        int iF30 = F2(iF26, iF29, iF28, iF27, this.X[14], 7);
        int iF31 = F2(iF27, iF30, iF29, iF28, this.X[11], 13);
        int iF32 = F2(iF28, iF31, iF30, iF29, this.X[8], 12);
        int iF33 = F3(iF29, iF32, iF31, iF30, this.X[3], 11);
        int iF34 = F3(iF30, iF33, iF32, iF31, this.X[10], 13);
        int iF35 = F3(iF31, iF34, iF33, iF32, this.X[14], 6);
        int iF36 = F3(iF32, iF35, iF34, iF33, this.X[4], 7);
        int iF37 = F3(iF33, iF36, iF35, iF34, this.X[9], 14);
        int iF38 = F3(iF34, iF37, iF36, iF35, this.X[15], 9);
        int iF39 = F3(iF35, iF38, iF37, iF36, this.X[8], 13);
        int iF40 = F3(iF36, iF39, iF38, iF37, this.X[1], 15);
        int iF41 = F3(iF37, iF40, iF39, iF38, this.X[2], 14);
        int iF42 = F3(iF38, iF41, iF40, iF39, this.X[7], 8);
        int iF43 = F3(iF39, iF42, iF41, iF40, this.X[0], 13);
        int iF44 = F3(iF40, iF43, iF42, iF41, this.X[6], 6);
        int iF45 = F3(iF41, iF44, iF43, iF42, this.X[13], 5);
        int iF46 = F3(iF42, iF45, iF44, iF43, this.X[11], 12);
        int iF47 = F3(iF43, iF46, iF45, iF44, this.X[5], 7);
        int iF48 = F3(iF44, iF47, iF46, iF45, this.X[12], 5);
        int iF49 = F4(iF45, iF48, iF47, iF46, this.X[1], 11);
        int iF50 = F4(iF46, iF49, iF48, iF47, this.X[9], 12);
        int iF51 = F4(iF47, iF50, iF49, iF48, this.X[11], 14);
        int iF52 = F4(iF48, iF51, iF50, iF49, this.X[10], 15);
        int iF53 = F4(iF49, iF52, iF51, iF50, this.X[0], 14);
        int iF54 = F4(iF50, iF53, iF52, iF51, this.X[8], 15);
        int iF55 = F4(iF51, iF54, iF53, iF52, this.X[12], 9);
        int iF56 = F4(iF52, iF55, iF54, iF53, this.X[4], 8);
        int iF57 = F4(iF53, iF56, iF55, iF54, this.X[13], 9);
        int iF58 = F4(iF54, iF57, iF56, iF55, this.X[3], 14);
        int iF59 = F4(iF55, iF58, iF57, iF56, this.X[7], 5);
        int iF60 = F4(iF56, iF59, iF58, iF57, this.X[15], 6);
        int iF61 = F4(iF57, iF60, iF59, iF58, this.X[14], 8);
        int iF62 = F4(iF58, iF61, iF60, iF59, this.X[5], 6);
        int iF63 = F4(iF59, iF62, iF61, iF60, this.X[6], 5);
        int iF64 = F4(iF60, iF63, iF62, iF61, this.X[2], 12);
        int iFF4 = FF4(i, i2, i3, i4, this.X[5], 8);
        int iFF5 = FF4(i4, iFF4, i2, i3, this.X[14], 9);
        int iFF6 = FF4(i3, iFF5, iFF4, i2, this.X[7], 9);
        int iFF7 = FF4(i2, iFF6, iFF5, iFF4, this.X[0], 11);
        int iFF8 = FF4(iFF4, iFF7, iFF6, iFF5, this.X[9], 13);
        int iFF9 = FF4(iFF5, iFF8, iFF7, iFF6, this.X[2], 15);
        int iFF10 = FF4(iFF6, iFF9, iFF8, iFF7, this.X[11], 15);
        int iFF11 = FF4(iFF7, iFF10, iFF9, iFF8, this.X[4], 5);
        int iFF12 = FF4(iFF8, iFF11, iFF10, iFF9, this.X[13], 7);
        int iFF13 = FF4(iFF9, iFF12, iFF11, iFF10, this.X[6], 7);
        int iFF14 = FF4(iFF10, iFF13, iFF12, iFF11, this.X[15], 8);
        int iFF15 = FF4(iFF11, iFF14, iFF13, iFF12, this.X[8], 11);
        int iFF16 = FF4(iFF12, iFF15, iFF14, iFF13, this.X[1], 14);
        int iFF17 = FF4(iFF13, iFF16, iFF15, iFF14, this.X[10], 14);
        int iFF18 = FF4(iFF14, iFF17, iFF16, iFF15, this.X[3], 12);
        int iFF19 = FF4(iFF15, iFF18, iFF17, iFF16, this.X[12], 6);
        int iFF3 = FF3(iFF16, iFF19, iFF18, iFF17, this.X[6], 9);
        int iFF20 = FF3(iFF17, iFF3, iFF19, iFF18, this.X[11], 13);
        int iFF21 = FF3(iFF18, iFF20, iFF3, iFF19, this.X[3], 15);
        int iFF22 = FF3(iFF19, iFF21, iFF20, iFF3, this.X[7], 7);
        int iFF23 = FF3(iFF3, iFF22, iFF21, iFF20, this.X[0], 12);
        int iFF24 = FF3(iFF20, iFF23, iFF22, iFF21, this.X[13], 8);
        int iFF25 = FF3(iFF21, iFF24, iFF23, iFF22, this.X[5], 9);
        int iFF26 = FF3(iFF22, iFF25, iFF24, iFF23, this.X[10], 11);
        int iFF27 = FF3(iFF23, iFF26, iFF25, iFF24, this.X[14], 7);
        int iFF28 = FF3(iFF24, iFF27, iFF26, iFF25, this.X[15], 7);
        int iFF29 = FF3(iFF25, iFF28, iFF27, iFF26, this.X[8], 12);
        int iFF30 = FF3(iFF26, iFF29, iFF28, iFF27, this.X[12], 7);
        int iFF31 = FF3(iFF27, iFF30, iFF29, iFF28, this.X[4], 6);
        int iFF32 = FF3(iFF28, iFF31, iFF30, iFF29, this.X[9], 15);
        int iFF33 = FF3(iFF29, iFF32, iFF31, iFF30, this.X[1], 13);
        int iFF34 = FF3(iFF30, iFF33, iFF32, iFF31, this.X[2], 11);
        int iFF2 = FF2(iFF31, iFF34, iFF33, iFF32, this.X[15], 9);
        int iFF35 = FF2(iFF32, iFF2, iFF34, iFF33, this.X[5], 7);
        int iFF36 = FF2(iFF33, iFF35, iFF2, iFF34, this.X[1], 15);
        int iFF37 = FF2(iFF34, iFF36, iFF35, iFF2, this.X[3], 11);
        int iFF38 = FF2(iFF2, iFF37, iFF36, iFF35, this.X[7], 8);
        int iFF39 = FF2(iFF35, iFF38, iFF37, iFF36, this.X[14], 6);
        int iFF40 = FF2(iFF36, iFF39, iFF38, iFF37, this.X[6], 6);
        int iFF41 = FF2(iFF37, iFF40, iFF39, iFF38, this.X[9], 14);
        int iFF42 = FF2(iFF38, iFF41, iFF40, iFF39, this.X[11], 12);
        int iFF43 = FF2(iFF39, iFF42, iFF41, iFF40, this.X[8], 13);
        int iFF44 = FF2(iFF40, iFF43, iFF42, iFF41, this.X[12], 5);
        int iFF45 = FF2(iFF41, iFF44, iFF43, iFF42, this.X[2], 14);
        int iFF46 = FF2(iFF42, iFF45, iFF44, iFF43, this.X[10], 13);
        int iFF47 = FF2(iFF43, iFF46, iFF45, iFF44, this.X[0], 13);
        int iFF48 = FF2(iFF44, iFF47, iFF46, iFF45, this.X[4], 7);
        int iFF49 = FF2(iFF45, iFF48, iFF47, iFF46, this.X[13], 5);
        int iFF1 = FF1(iFF46, iFF49, iFF48, iFF47, this.X[8], 15);
        int iFF50 = FF1(iFF47, iFF1, iFF49, iFF48, this.X[6], 5);
        int iFF51 = FF1(iFF48, iFF50, iFF1, iFF49, this.X[4], 8);
        int iFF52 = FF1(iFF49, iFF51, iFF50, iFF1, this.X[1], 11);
        int iFF53 = FF1(iFF1, iFF52, iFF51, iFF50, this.X[3], 14);
        int iFF54 = FF1(iFF50, iFF53, iFF52, iFF51, this.X[11], 14);
        int iFF55 = FF1(iFF51, iFF54, iFF53, iFF52, this.X[15], 6);
        int iFF56 = FF1(iFF52, iFF55, iFF54, iFF53, this.X[0], 14);
        int iFF57 = FF1(iFF53, iFF56, iFF55, iFF54, this.X[5], 6);
        int iFF58 = FF1(iFF54, iFF57, iFF56, iFF55, this.X[12], 9);
        int iFF59 = FF1(iFF55, iFF58, iFF57, iFF56, this.X[2], 12);
        int iFF60 = FF1(iFF56, iFF59, iFF58, iFF57, this.X[13], 9);
        int iFF61 = FF1(iFF57, iFF60, iFF59, iFF58, this.X[9], 12);
        int iFF62 = FF1(iFF58, iFF61, iFF60, iFF59, this.X[7], 5);
        int iFF63 = FF1(iFF59, iFF62, iFF61, iFF60, this.X[10], 15);
        int iFF64 = FF1(iFF60, iFF63, iFF62, iFF61, this.X[14], 8);
        int i5 = iFF62 + iF63 + this.H1;
        this.H1 = this.H2 + iF62 + iFF61;
        this.H2 = this.H3 + iF61 + iFF64;
        this.H3 = this.H0 + iF64 + iFF63;
        this.H0 = i5;
        this.xOff = 0;
        for (int i6 = 0; i6 != this.X.length; i6++) {
            this.X[i6] = 0;
        }
    }

    @Override // org.bouncycastle.util.Memoable
    public Memoable copy() {
        return new RIPEMD128Digest(this);
    }

    @Override // org.bouncycastle.util.Memoable
    public void reset(Memoable memoable) {
        copyIn((RIPEMD128Digest) memoable);
    }

    @Override // org.bouncycastle.crypto.digests.GeneralDigest
    protected CryptoServiceProperties cryptoServiceProperties() {
        return Utils.getDefaultProperties(this, this.purpose);
    }
}
