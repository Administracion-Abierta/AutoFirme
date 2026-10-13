package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/SHA224Digest.class */
public class SHA224Digest extends GeneralDigest implements EncodableDigest {
    private static final int DIGEST_LENGTH = 28;
    private int H1;
    private int H2;
    private int H3;
    private int H4;
    private int H5;
    private int H6;
    private int H7;
    private int H8;
    private int[] X;
    private int xOff;
    static final int[] K = {1116352408, 1899447441, -1245643825, -373957723, 961987163, 1508970993, -1841331548, -1424204075, -670586216, 310598401, 607225278, 1426881987, 1925078388, -2132889090, -1680079193, -1046744716, -459576895, -272742522, 264347078, 604807628, 770255983, 1249150122, 1555081692, 1996064986, -1740746414, -1473132947, -1341970488, -1084653625, -958395405, -710438585, 113926993, 338241895, 666307205, 773529912, 1294757372, 1396182291, 1695183700, 1986661051, -2117940946, -1838011259, -1564481375, -1474664885, -1035236496, -949202525, -778901479, -694614492, -200395387, 275423344, 430227734, 506948616, 659060556, 883997877, 958139571, 1322822218, 1537002063, 1747873779, 1955562222, 2024104815, -2067236844, -1933114872, -1866530822, -1538233109, -1090935817, -965641998};

    public SHA224Digest() {
        this(CryptoServicePurpose.ANY);
    }

    public SHA224Digest(CryptoServicePurpose cryptoServicePurpose) {
        super(cryptoServicePurpose);
        this.X = new int[64];
        CryptoServicesRegistrar.checkConstraints(cryptoServiceProperties());
        reset();
    }

    public SHA224Digest(SHA224Digest sHA224Digest) {
        super(sHA224Digest);
        this.X = new int[64];
        CryptoServicesRegistrar.checkConstraints(cryptoServiceProperties());
        doCopy(sHA224Digest);
    }

    private void doCopy(SHA224Digest sHA224Digest) {
        super.copyIn(sHA224Digest);
        this.H1 = sHA224Digest.H1;
        this.H2 = sHA224Digest.H2;
        this.H3 = sHA224Digest.H3;
        this.H4 = sHA224Digest.H4;
        this.H5 = sHA224Digest.H5;
        this.H6 = sHA224Digest.H6;
        this.H7 = sHA224Digest.H7;
        this.H8 = sHA224Digest.H8;
        System.arraycopy(sHA224Digest.X, 0, this.X, 0, sHA224Digest.X.length);
        this.xOff = sHA224Digest.xOff;
    }

    public SHA224Digest(byte[] bArr) {
        super(bArr);
        this.X = new int[64];
        CryptoServicesRegistrar.checkConstraints(cryptoServiceProperties());
        this.H1 = Pack.bigEndianToInt(bArr, 16);
        this.H2 = Pack.bigEndianToInt(bArr, 20);
        this.H3 = Pack.bigEndianToInt(bArr, 24);
        this.H4 = Pack.bigEndianToInt(bArr, 28);
        this.H5 = Pack.bigEndianToInt(bArr, 32);
        this.H6 = Pack.bigEndianToInt(bArr, 36);
        this.H7 = Pack.bigEndianToInt(bArr, 40);
        this.H8 = Pack.bigEndianToInt(bArr, 44);
        this.xOff = Pack.bigEndianToInt(bArr, 48);
        for (int i = 0; i != this.xOff; i++) {
            this.X[i] = Pack.bigEndianToInt(bArr, 52 + (i * 4));
        }
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "SHA-224";
    }

    @Override // org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return 28;
    }

    @Override // org.bouncycastle.crypto.digests.GeneralDigest
    protected void processWord(byte[] bArr, int i) {
        this.X[this.xOff] = Pack.bigEndianToInt(bArr, i);
        int i2 = this.xOff + 1;
        this.xOff = i2;
        if (i2 == 16) {
            processBlock();
        }
    }

    @Override // org.bouncycastle.crypto.digests.GeneralDigest
    protected void processLength(long j) {
        if (this.xOff > 14) {
            processBlock();
        }
        this.X[14] = (int) (j >>> 32);
        this.X[15] = (int) (j & (-1));
    }

    @Override // org.bouncycastle.crypto.Digest
    public int doFinal(byte[] bArr, int i) {
        finish();
        Pack.intToBigEndian(this.H1, bArr, i);
        Pack.intToBigEndian(this.H2, bArr, i + 4);
        Pack.intToBigEndian(this.H3, bArr, i + 8);
        Pack.intToBigEndian(this.H4, bArr, i + 12);
        Pack.intToBigEndian(this.H5, bArr, i + 16);
        Pack.intToBigEndian(this.H6, bArr, i + 20);
        Pack.intToBigEndian(this.H7, bArr, i + 24);
        reset();
        return 28;
    }

    @Override // org.bouncycastle.crypto.digests.GeneralDigest, org.bouncycastle.crypto.Digest
    public void reset() {
        super.reset();
        this.H1 = -1056596264;
        this.H2 = 914150663;
        this.H3 = 812702999;
        this.H4 = -150054599;
        this.H5 = -4191439;
        this.H6 = 1750603025;
        this.H7 = 1694076839;
        this.H8 = -1090891868;
        this.xOff = 0;
        for (int i = 0; i != this.X.length; i++) {
            this.X[i] = 0;
        }
    }

    @Override // org.bouncycastle.crypto.digests.GeneralDigest
    protected void processBlock() {
        for (int i = 16; i <= 63; i++) {
            this.X[i] = Theta1(this.X[i - 2]) + this.X[i - 7] + Theta0(this.X[i - 15]) + this.X[i - 16];
        }
        int iSum0 = this.H1;
        int iSum1 = this.H2;
        int iSum2 = this.H3;
        int iSum3 = this.H4;
        int i2 = this.H5;
        int i3 = this.H6;
        int i4 = this.H7;
        int i5 = this.H8;
        int i6 = 0;
        for (int i7 = 0; i7 < 8; i7++) {
            int iSum4 = i5 + Sum1(i2) + Ch(i2, i3, i4) + K[i6] + this.X[i6];
            int i8 = iSum3 + iSum4;
            int iSum5 = iSum4 + Sum0(iSum0) + Maj(iSum0, iSum1, iSum2);
            int i9 = i6 + 1;
            int iSum6 = i4 + Sum1(i8) + Ch(i8, i2, i3) + K[i9] + this.X[i9];
            int i10 = iSum2 + iSum6;
            int iSum7 = iSum6 + Sum0(iSum5) + Maj(iSum5, iSum0, iSum1);
            int i11 = i9 + 1;
            int iSum8 = i3 + Sum1(i10) + Ch(i10, i8, i2) + K[i11] + this.X[i11];
            int i12 = iSum1 + iSum8;
            int iSum9 = iSum8 + Sum0(iSum7) + Maj(iSum7, iSum5, iSum0);
            int i13 = i11 + 1;
            int iSum10 = i2 + Sum1(i12) + Ch(i12, i10, i8) + K[i13] + this.X[i13];
            int i14 = iSum0 + iSum10;
            int iSum11 = iSum10 + Sum0(iSum9) + Maj(iSum9, iSum7, iSum5);
            int i15 = i13 + 1;
            int iSum12 = i8 + Sum1(i14) + Ch(i14, i12, i10) + K[i15] + this.X[i15];
            i5 = iSum5 + iSum12;
            iSum3 = iSum12 + Sum0(iSum11) + Maj(iSum11, iSum9, iSum7);
            int i16 = i15 + 1;
            int iSum13 = i10 + Sum1(i5) + Ch(i5, i14, i12) + K[i16] + this.X[i16];
            i4 = iSum7 + iSum13;
            iSum2 = iSum13 + Sum0(iSum3) + Maj(iSum3, iSum11, iSum9);
            int i17 = i16 + 1;
            int iSum14 = i12 + Sum1(i4) + Ch(i4, i5, i14) + K[i17] + this.X[i17];
            i3 = iSum9 + iSum14;
            iSum1 = iSum14 + Sum0(iSum2) + Maj(iSum2, iSum3, iSum11);
            int i18 = i17 + 1;
            int iSum15 = i14 + Sum1(i3) + Ch(i3, i4, i5) + K[i18] + this.X[i18];
            i2 = iSum11 + iSum15;
            iSum0 = iSum15 + Sum0(iSum1) + Maj(iSum1, iSum2, iSum3);
            i6 = i18 + 1;
        }
        this.H1 += iSum0;
        this.H2 += iSum1;
        this.H3 += iSum2;
        this.H4 += iSum3;
        this.H5 += i2;
        this.H6 += i3;
        this.H7 += i4;
        this.H8 += i5;
        this.xOff = 0;
        for (int i19 = 0; i19 < 16; i19++) {
            this.X[i19] = 0;
        }
    }

    private int Ch(int i, int i2, int i3) {
        return (i & i2) ^ ((i ^ (-1)) & i3);
    }

    private int Maj(int i, int i2, int i3) {
        return ((i & i2) ^ (i & i3)) ^ (i2 & i3);
    }

    private int Sum0(int i) {
        return (((i >>> 2) | (i << 30)) ^ ((i >>> 13) | (i << 19))) ^ ((i >>> 22) | (i << 10));
    }

    private int Sum1(int i) {
        return (((i >>> 6) | (i << 26)) ^ ((i >>> 11) | (i << 21))) ^ ((i >>> 25) | (i << 7));
    }

    private int Theta0(int i) {
        return (((i >>> 7) | (i << 25)) ^ ((i >>> 18) | (i << 14))) ^ (i >>> 3);
    }

    private int Theta1(int i) {
        return (((i >>> 17) | (i << 15)) ^ ((i >>> 19) | (i << 13))) ^ (i >>> 10);
    }

    @Override // org.bouncycastle.util.Memoable
    public Memoable copy() {
        return new SHA224Digest(this);
    }

    @Override // org.bouncycastle.util.Memoable
    public void reset(Memoable memoable) {
        doCopy((SHA224Digest) memoable);
    }

    @Override // org.bouncycastle.crypto.digests.EncodableDigest
    public byte[] getEncodedState() {
        byte[] bArr = new byte[52 + (this.xOff * 4) + 1];
        super.populateState(bArr);
        Pack.intToBigEndian(this.H1, bArr, 16);
        Pack.intToBigEndian(this.H2, bArr, 20);
        Pack.intToBigEndian(this.H3, bArr, 24);
        Pack.intToBigEndian(this.H4, bArr, 28);
        Pack.intToBigEndian(this.H5, bArr, 32);
        Pack.intToBigEndian(this.H6, bArr, 36);
        Pack.intToBigEndian(this.H7, bArr, 40);
        Pack.intToBigEndian(this.H8, bArr, 44);
        Pack.intToBigEndian(this.xOff, bArr, 48);
        for (int i = 0; i != this.xOff; i++) {
            Pack.intToBigEndian(this.X[i], bArr, 52 + (i * 4));
        }
        bArr[bArr.length - 1] = (byte) this.purpose.ordinal();
        return bArr;
    }

    @Override // org.bouncycastle.crypto.digests.GeneralDigest
    protected CryptoServiceProperties cryptoServiceProperties() {
        return Utils.getDefaultProperties(this, 192, this.purpose);
    }
}
