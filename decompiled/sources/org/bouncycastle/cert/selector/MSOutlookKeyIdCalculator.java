package org.bouncycastle.cert.selector;

import java.io.IOException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/selector/MSOutlookKeyIdCalculator.class */
class MSOutlookKeyIdCalculator {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/selector/MSOutlookKeyIdCalculator$GeneralDigest.class */
    private static abstract class GeneralDigest {
        private static final int BYTE_LENGTH = 64;
        private byte[] xBuf;
        private int xBufOff;
        private long byteCount;

        protected GeneralDigest() {
            this.xBuf = new byte[4];
            this.xBufOff = 0;
        }

        protected GeneralDigest(GeneralDigest generalDigest) {
            this.xBuf = new byte[generalDigest.xBuf.length];
            copyIn(generalDigest);
        }

        protected void copyIn(GeneralDigest generalDigest) {
            System.arraycopy(generalDigest.xBuf, 0, this.xBuf, 0, generalDigest.xBuf.length);
            this.xBufOff = generalDigest.xBufOff;
            this.byteCount = generalDigest.byteCount;
        }

        public void update(byte b) {
            byte[] bArr = this.xBuf;
            int i = this.xBufOff;
            this.xBufOff = i + 1;
            bArr[i] = b;
            if (this.xBufOff == this.xBuf.length) {
                processWord(this.xBuf, 0);
                this.xBufOff = 0;
            }
            this.byteCount++;
        }

        public void update(byte[] bArr, int i, int i2) {
            while (this.xBufOff != 0 && i2 > 0) {
                update(bArr[i]);
                i++;
                i2--;
            }
            while (i2 > this.xBuf.length) {
                processWord(bArr, i);
                i += this.xBuf.length;
                i2 -= this.xBuf.length;
                this.byteCount += (long) this.xBuf.length;
            }
            while (i2 > 0) {
                update(bArr[i]);
                i++;
                i2--;
            }
        }

        public void finish() {
            long j = this.byteCount << 3;
            update((byte) -128);
            while (this.xBufOff != 0) {
                update((byte) 0);
            }
            processLength(j);
            processBlock();
        }

        public void reset() {
            this.byteCount = 0L;
            this.xBufOff = 0;
            for (int i = 0; i < this.xBuf.length; i++) {
                this.xBuf[i] = 0;
            }
        }

        protected abstract void processWord(byte[] bArr, int i);

        protected abstract void processLength(long j);

        protected abstract void processBlock();
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/selector/MSOutlookKeyIdCalculator$SHA1Digest.class */
    private static class SHA1Digest extends GeneralDigest {
        private static final int DIGEST_LENGTH = 20;
        private int H1;
        private int H2;
        private int H3;
        private int H4;
        private int H5;
        private int[] X = new int[80];
        private int xOff;
        private static final int Y1 = 1518500249;
        private static final int Y2 = 1859775393;
        private static final int Y3 = -1894007588;
        private static final int Y4 = -899497514;

        public SHA1Digest() {
            reset();
        }

        public String getAlgorithmName() {
            return "SHA-1";
        }

        public int getDigestSize() {
            return 20;
        }

        @Override // org.bouncycastle.cert.selector.MSOutlookKeyIdCalculator.GeneralDigest
        protected void processWord(byte[] bArr, int i) {
            int i2 = bArr[i] << 24;
            int i3 = i + 1;
            int i4 = i2 | ((bArr[i3] & 255) << 16);
            int i5 = i3 + 1;
            this.X[this.xOff] = i4 | ((bArr[i5] & 255) << 8) | (bArr[i5 + 1] & 255);
            int i6 = this.xOff + 1;
            this.xOff = i6;
            if (i6 == 16) {
                processBlock();
            }
        }

        @Override // org.bouncycastle.cert.selector.MSOutlookKeyIdCalculator.GeneralDigest
        protected void processLength(long j) {
            if (this.xOff > 14) {
                processBlock();
            }
            this.X[14] = (int) (j >>> 32);
            this.X[15] = (int) (j & (-1));
        }

        public int doFinal(byte[] bArr, int i) {
            finish();
            Pack.intToBigEndian(this.H1, bArr, i);
            Pack.intToBigEndian(this.H2, bArr, i + 4);
            Pack.intToBigEndian(this.H3, bArr, i + 8);
            Pack.intToBigEndian(this.H4, bArr, i + 12);
            Pack.intToBigEndian(this.H5, bArr, i + 16);
            reset();
            return 20;
        }

        @Override // org.bouncycastle.cert.selector.MSOutlookKeyIdCalculator.GeneralDigest
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

        private int f(int i, int i2, int i3) {
            return (i & i2) | ((i ^ (-1)) & i3);
        }

        private int h(int i, int i2, int i3) {
            return (i ^ i2) ^ i3;
        }

        private int g(int i, int i2, int i3) {
            return (i & i2) | (i & i3) | (i2 & i3);
        }

        @Override // org.bouncycastle.cert.selector.MSOutlookKeyIdCalculator.GeneralDigest
        protected void processBlock() {
            for (int i = 16; i < 80; i++) {
                int i2 = ((this.X[i - 3] ^ this.X[i - 8]) ^ this.X[i - 14]) ^ this.X[i - 16];
                this.X[i] = (i2 << 1) | (i2 >>> 31);
            }
            int iH = this.H1;
            int iH2 = this.H2;
            int i3 = this.H3;
            int i4 = this.H4;
            int i5 = this.H5;
            int i6 = 0;
            for (int i7 = 0; i7 < 4; i7++) {
                int i8 = i6;
                int i9 = i6 + 1;
                int iF = i5 + ((iH << 5) | (iH >>> 27)) + f(iH2, i3, i4) + this.X[i8] + Y1;
                int i10 = (iH2 << 30) | (iH2 >>> 2);
                int i11 = i9 + 1;
                int iF2 = i4 + ((iF << 5) | (iF >>> 27)) + f(iH, i10, i3) + this.X[i9] + Y1;
                int i12 = (iH << 30) | (iH >>> 2);
                int i13 = i11 + 1;
                int iF3 = i3 + ((iF2 << 5) | (iF2 >>> 27)) + f(iF, i12, i10) + this.X[i11] + Y1;
                i5 = (iF << 30) | (iF >>> 2);
                int i14 = i13 + 1;
                iH2 = i10 + ((iF3 << 5) | (iF3 >>> 27)) + f(iF2, i5, i12) + this.X[i13] + Y1;
                i4 = (iF2 << 30) | (iF2 >>> 2);
                i6 = i14 + 1;
                iH = i12 + ((iH2 << 5) | (iH2 >>> 27)) + f(iF3, i4, i5) + this.X[i14] + Y1;
                i3 = (iF3 << 30) | (iF3 >>> 2);
            }
            for (int i15 = 0; i15 < 4; i15++) {
                int i16 = i6;
                int i17 = i6 + 1;
                int iH3 = i5 + ((iH << 5) | (iH >>> 27)) + h(iH2, i3, i4) + this.X[i16] + Y2;
                int i18 = (iH2 << 30) | (iH2 >>> 2);
                int i19 = i17 + 1;
                int iH4 = i4 + ((iH3 << 5) | (iH3 >>> 27)) + h(iH, i18, i3) + this.X[i17] + Y2;
                int i20 = (iH << 30) | (iH >>> 2);
                int i21 = i19 + 1;
                int iH5 = i3 + ((iH4 << 5) | (iH4 >>> 27)) + h(iH3, i20, i18) + this.X[i19] + Y2;
                i5 = (iH3 << 30) | (iH3 >>> 2);
                int i22 = i21 + 1;
                iH2 = i18 + ((iH5 << 5) | (iH5 >>> 27)) + h(iH4, i5, i20) + this.X[i21] + Y2;
                i4 = (iH4 << 30) | (iH4 >>> 2);
                i6 = i22 + 1;
                iH = i20 + ((iH2 << 5) | (iH2 >>> 27)) + h(iH5, i4, i5) + this.X[i22] + Y2;
                i3 = (iH5 << 30) | (iH5 >>> 2);
            }
            for (int i23 = 0; i23 < 4; i23++) {
                int i24 = i6;
                int i25 = i6 + 1;
                int iG = i5 + ((iH << 5) | (iH >>> 27)) + g(iH2, i3, i4) + this.X[i24] + Y3;
                int i26 = (iH2 << 30) | (iH2 >>> 2);
                int i27 = i25 + 1;
                int iG2 = i4 + ((iG << 5) | (iG >>> 27)) + g(iH, i26, i3) + this.X[i25] + Y3;
                int i28 = (iH << 30) | (iH >>> 2);
                int i29 = i27 + 1;
                int iG3 = i3 + ((iG2 << 5) | (iG2 >>> 27)) + g(iG, i28, i26) + this.X[i27] + Y3;
                i5 = (iG << 30) | (iG >>> 2);
                int i30 = i29 + 1;
                iH2 = i26 + ((iG3 << 5) | (iG3 >>> 27)) + g(iG2, i5, i28) + this.X[i29] + Y3;
                i4 = (iG2 << 30) | (iG2 >>> 2);
                i6 = i30 + 1;
                iH = i28 + ((iH2 << 5) | (iH2 >>> 27)) + g(iG3, i4, i5) + this.X[i30] + Y3;
                i3 = (iG3 << 30) | (iG3 >>> 2);
            }
            for (int i31 = 0; i31 <= 3; i31++) {
                int i32 = i6;
                int i33 = i6 + 1;
                int iH6 = i5 + ((iH << 5) | (iH >>> 27)) + h(iH2, i3, i4) + this.X[i32] + Y4;
                int i34 = (iH2 << 30) | (iH2 >>> 2);
                int i35 = i33 + 1;
                int iH7 = i4 + ((iH6 << 5) | (iH6 >>> 27)) + h(iH, i34, i3) + this.X[i33] + Y4;
                int i36 = (iH << 30) | (iH >>> 2);
                int i37 = i35 + 1;
                int iH8 = i3 + ((iH7 << 5) | (iH7 >>> 27)) + h(iH6, i36, i34) + this.X[i35] + Y4;
                i5 = (iH6 << 30) | (iH6 >>> 2);
                int i38 = i37 + 1;
                iH2 = i34 + ((iH8 << 5) | (iH8 >>> 27)) + h(iH7, i5, i36) + this.X[i37] + Y4;
                i4 = (iH7 << 30) | (iH7 >>> 2);
                i6 = i38 + 1;
                iH = i36 + ((iH2 << 5) | (iH2 >>> 27)) + h(iH8, i4, i5) + this.X[i38] + Y4;
                i3 = (iH8 << 30) | (iH8 >>> 2);
            }
            this.H1 += iH;
            this.H2 += iH2;
            this.H3 += i3;
            this.H4 += i4;
            this.H5 += i5;
            this.xOff = 0;
            for (int i39 = 0; i39 < 16; i39++) {
                this.X[i39] = 0;
            }
        }
    }

    MSOutlookKeyIdCalculator() {
    }

    static byte[] calculateKeyId(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        SHA1Digest sHA1Digest = new SHA1Digest();
        byte[] bArr = new byte[sHA1Digest.getDigestSize()];
        byte[] bArr2 = new byte[0];
        try {
            byte[] encoded = subjectPublicKeyInfo.getEncoded("DER");
            sHA1Digest.update(encoded, 0, encoded.length);
            sHA1Digest.doFinal(bArr, 0);
            return bArr;
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
