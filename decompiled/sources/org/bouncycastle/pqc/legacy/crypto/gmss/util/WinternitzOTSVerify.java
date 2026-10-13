package org.bouncycastle.pqc.legacy.crypto.gmss.util;

import org.bouncycastle.crypto.Digest;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/crypto/gmss/util/WinternitzOTSVerify.class */
public class WinternitzOTSVerify {
    private Digest messDigestOTS;
    private int mdsize;
    private int w;

    public WinternitzOTSVerify(Digest digest, int i) {
        this.w = i;
        this.messDigestOTS = digest;
        this.mdsize = this.messDigestOTS.getDigestSize();
    }

    public int getSignatureLength() {
        int digestSize = this.messDigestOTS.getDigestSize();
        int i = ((digestSize << 3) + (this.w - 1)) / this.w;
        return digestSize * (i + (((getLog((i << this.w) + 1) + this.w) - 1) / this.w));
    }

    public byte[] Verify(byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[this.mdsize];
        this.messDigestOTS.update(bArr, 0, bArr.length);
        this.messDigestOTS.doFinal(bArr3, 0);
        int i = ((this.mdsize << 3) + (this.w - 1)) / this.w;
        int log = getLog((i << this.w) + 1);
        int i2 = this.mdsize * (i + (((log + this.w) - 1) / this.w));
        if (i2 != bArr2.length) {
            return null;
        }
        byte[] bArr4 = new byte[i2];
        int i3 = 0;
        int i4 = 0;
        if (8 % this.w == 0) {
            int i5 = 8 / this.w;
            int i6 = (1 << this.w) - 1;
            for (int i7 = 0; i7 < bArr3.length; i7++) {
                for (int i8 = 0; i8 < i5; i8++) {
                    int i9 = bArr3[i7] & i6;
                    i3 += i9;
                    hashSignatureBlock(bArr2, i4 * this.mdsize, i6 - i9, bArr4, i4 * this.mdsize);
                    bArr3[i7] = (byte) (bArr3[i7] >>> this.w);
                    i4++;
                }
            }
            int i10 = (i << this.w) - i3;
            int i11 = 0;
            while (true) {
                int i12 = i11;
                if (i12 >= log) {
                    break;
                }
                hashSignatureBlock(bArr2, i4 * this.mdsize, i6 - (i10 & i6), bArr4, i4 * this.mdsize);
                i10 >>>= this.w;
                i4++;
                i11 = i12 + this.w;
            }
        } else if (this.w < 8) {
            int i13 = this.mdsize / this.w;
            int i14 = (1 << this.w) - 1;
            int i15 = 0;
            for (int i16 = 0; i16 < i13; i16++) {
                long j = 0;
                for (int i17 = 0; i17 < this.w; i17++) {
                    j ^= (long) ((bArr3[i15] & 255) << (i17 << 3));
                    i15++;
                }
                for (int i18 = 0; i18 < 8; i18++) {
                    int i19 = (int) (j & ((long) i14));
                    i3 += i19;
                    hashSignatureBlock(bArr2, i4 * this.mdsize, i14 - i19, bArr4, i4 * this.mdsize);
                    j >>>= this.w;
                    i4++;
                }
            }
            int i20 = this.mdsize % this.w;
            long j2 = 0;
            for (int i21 = 0; i21 < i20; i21++) {
                j2 ^= (long) ((bArr3[i15] & 255) << (i21 << 3));
                i15++;
            }
            int i22 = i20 << 3;
            int i23 = 0;
            while (true) {
                int i24 = i23;
                if (i24 >= i22) {
                    break;
                }
                int i25 = (int) (j2 & ((long) i14));
                i3 += i25;
                hashSignatureBlock(bArr2, i4 * this.mdsize, i14 - i25, bArr4, i4 * this.mdsize);
                j2 >>>= this.w;
                i4++;
                i23 = i24 + this.w;
            }
            int i26 = (i << this.w) - i3;
            int i27 = 0;
            while (true) {
                int i28 = i27;
                if (i28 >= log) {
                    break;
                }
                hashSignatureBlock(bArr2, i4 * this.mdsize, i14 - (i26 & i14), bArr4, i4 * this.mdsize);
                i26 >>>= this.w;
                i4++;
                i27 = i28 + this.w;
            }
        } else if (this.w < 57) {
            int i29 = (this.mdsize << 3) - this.w;
            int i30 = (1 << this.w) - 1;
            byte[] bArr5 = new byte[this.mdsize];
            int i31 = 0;
            while (i31 <= i29) {
                int i32 = i31 >>> 3;
                int i33 = i31 % 8;
                i31 += this.w;
                int i34 = (i31 + 7) >>> 3;
                long j3 = 0;
                int i35 = 0;
                for (int i36 = i32; i36 < i34; i36++) {
                    j3 ^= (long) ((bArr3[i36] & 255) << (i35 << 3));
                    i35++;
                }
                long j4 = (j3 >>> i33) & ((long) i30);
                i3 = (int) (((long) i3) + j4);
                System.arraycopy(bArr2, i4 * this.mdsize, bArr5, 0, this.mdsize);
                while (j4 < i30) {
                    this.messDigestOTS.update(bArr5, 0, bArr5.length);
                    this.messDigestOTS.doFinal(bArr5, 0);
                    j4++;
                }
                System.arraycopy(bArr5, 0, bArr4, i4 * this.mdsize, this.mdsize);
                i4++;
            }
            int i37 = i31 >>> 3;
            if (i37 < this.mdsize) {
                int i38 = i31 % 8;
                long j5 = 0;
                int i39 = 0;
                for (int i40 = i37; i40 < this.mdsize; i40++) {
                    j5 ^= (long) ((bArr3[i40] & 255) << (i39 << 3));
                    i39++;
                }
                long j6 = (j5 >>> i38) & ((long) i30);
                i3 = (int) (((long) i3) + j6);
                System.arraycopy(bArr2, i4 * this.mdsize, bArr5, 0, this.mdsize);
                while (j6 < i30) {
                    this.messDigestOTS.update(bArr5, 0, bArr5.length);
                    this.messDigestOTS.doFinal(bArr5, 0);
                    j6++;
                }
                System.arraycopy(bArr5, 0, bArr4, i4 * this.mdsize, this.mdsize);
                i4++;
            }
            int i41 = (i << this.w) - i3;
            int i42 = 0;
            while (true) {
                int i43 = i42;
                if (i43 >= log) {
                    break;
                }
                System.arraycopy(bArr2, i4 * this.mdsize, bArr5, 0, this.mdsize);
                for (long j7 = i41 & i30; j7 < i30; j7++) {
                    this.messDigestOTS.update(bArr5, 0, bArr5.length);
                    this.messDigestOTS.doFinal(bArr5, 0);
                }
                System.arraycopy(bArr5, 0, bArr4, i4 * this.mdsize, this.mdsize);
                i41 >>>= this.w;
                i4++;
                i42 = i43 + this.w;
            }
        }
        this.messDigestOTS.update(bArr4, 0, bArr4.length);
        byte[] bArr6 = new byte[this.mdsize];
        this.messDigestOTS.doFinal(bArr6, 0);
        return bArr6;
    }

    public int getLog(int i) {
        int i2 = 1;
        int i3 = 2;
        while (i3 < i) {
            i3 <<= 1;
            i2++;
        }
        return i2;
    }

    private void hashSignatureBlock(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        if (i2 < 1) {
            System.arraycopy(bArr, i, bArr2, i3, this.mdsize);
            return;
        }
        this.messDigestOTS.update(bArr, i, this.mdsize);
        this.messDigestOTS.doFinal(bArr2, i3);
        while (true) {
            i2--;
            if (i2 <= 0) {
                return;
            }
            this.messDigestOTS.update(bArr2, i3, this.mdsize);
            this.messDigestOTS.doFinal(bArr2, i3);
        }
    }
}
