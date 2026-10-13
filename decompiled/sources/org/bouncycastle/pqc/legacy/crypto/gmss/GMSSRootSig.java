package org.bouncycastle.pqc.legacy.crypto.gmss;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.pqc.legacy.crypto.gmss.util.GMSSRandom;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/crypto/gmss/GMSSRootSig.class */
public class GMSSRootSig {
    private Digest messDigestOTS;
    private int mdsize;
    private int keysize;
    private byte[] privateKeyOTS;
    private byte[] hash;
    private byte[] sign;
    private int w;
    private GMSSRandom gmssRandom;
    private int messagesize;
    private int k;
    private int r;
    private int test;
    private int counter;
    private int ii;
    private long test8;
    private long big8;
    private int steps;
    private int checksum;
    private int height;
    private byte[] seed;

    public GMSSRootSig(Digest digest, byte[][] bArr, int[] iArr) {
        this.messDigestOTS = digest;
        this.gmssRandom = new GMSSRandom(this.messDigestOTS);
        this.counter = iArr[0];
        this.test = iArr[1];
        this.ii = iArr[2];
        this.r = iArr[3];
        this.steps = iArr[4];
        this.keysize = iArr[5];
        this.height = iArr[6];
        this.w = iArr[7];
        this.checksum = iArr[8];
        this.mdsize = this.messDigestOTS.getDigestSize();
        this.k = (1 << this.w) - 1;
        this.messagesize = (int) Math.ceil(((double) (this.mdsize << 3)) / ((double) this.w));
        this.privateKeyOTS = bArr[0];
        this.seed = bArr[1];
        this.hash = bArr[2];
        this.sign = bArr[3];
        this.test8 = ((long) (bArr[4][0] & 255)) | (((long) (bArr[4][1] & 255)) << 8) | (((long) (bArr[4][2] & 255)) << 16) | (((long) (bArr[4][3] & 255)) << 24) | (((long) (bArr[4][4] & 255)) << 32) | (((long) (bArr[4][5] & 255)) << 40) | (((long) (bArr[4][6] & 255)) << 48) | (((long) (bArr[4][7] & 255)) << 56);
        this.big8 = ((long) (bArr[4][8] & 255)) | (((long) (bArr[4][9] & 255)) << 8) | (((long) (bArr[4][10] & 255)) << 16) | (((long) (bArr[4][11] & 255)) << 24) | (((long) (bArr[4][12] & 255)) << 32) | (((long) (bArr[4][13] & 255)) << 40) | (((long) (bArr[4][14] & 255)) << 48) | (((long) (bArr[4][15] & 255)) << 56);
    }

    public GMSSRootSig(Digest digest, int i, int i2) {
        this.messDigestOTS = digest;
        this.gmssRandom = new GMSSRandom(this.messDigestOTS);
        this.mdsize = this.messDigestOTS.getDigestSize();
        this.w = i;
        this.height = i2;
        this.k = (1 << i) - 1;
        this.messagesize = (int) Math.ceil(((double) (this.mdsize << 3)) / ((double) i));
    }

    public void initSign(byte[] bArr, byte[] bArr2) {
        this.hash = new byte[this.mdsize];
        this.messDigestOTS.update(bArr2, 0, bArr2.length);
        this.hash = new byte[this.messDigestOTS.getDigestSize()];
        this.messDigestOTS.doFinal(this.hash, 0);
        byte[] bArr3 = new byte[this.mdsize];
        System.arraycopy(this.hash, 0, bArr3, 0, this.mdsize);
        int i = 0;
        int log = getLog((this.messagesize << this.w) + 1);
        if (8 % this.w == 0) {
            int i2 = 8 / this.w;
            for (int i3 = 0; i3 < this.mdsize; i3++) {
                for (int i4 = 0; i4 < i2; i4++) {
                    i += bArr3[i3] & this.k;
                    bArr3[i3] = (byte) (bArr3[i3] >>> this.w);
                }
            }
            this.checksum = (this.messagesize << this.w) - i;
            int i5 = this.checksum;
            int i6 = 0;
            while (true) {
                int i7 = i6;
                if (i7 >= log) {
                    break;
                }
                i += i5 & this.k;
                i5 >>>= this.w;
                i6 = i7 + this.w;
            }
        } else if (this.w < 8) {
            int i8 = 0;
            int i9 = this.mdsize / this.w;
            for (int i10 = 0; i10 < i9; i10++) {
                long j = 0;
                for (int i11 = 0; i11 < this.w; i11++) {
                    j ^= (long) ((bArr3[i8] & 255) << (i11 << 3));
                    i8++;
                }
                for (int i12 = 0; i12 < 8; i12++) {
                    i += (int) (j & ((long) this.k));
                    j >>>= this.w;
                }
            }
            int i13 = this.mdsize % this.w;
            long j2 = 0;
            for (int i14 = 0; i14 < i13; i14++) {
                j2 ^= (long) ((bArr3[i8] & 255) << (i14 << 3));
                i8++;
            }
            int i15 = i13 << 3;
            int i16 = 0;
            while (true) {
                int i17 = i16;
                if (i17 >= i15) {
                    break;
                }
                i += (int) (j2 & ((long) this.k));
                j2 >>>= this.w;
                i16 = i17 + this.w;
            }
            this.checksum = (this.messagesize << this.w) - i;
            int i18 = this.checksum;
            int i19 = 0;
            while (true) {
                int i20 = i19;
                if (i20 >= log) {
                    break;
                }
                i += i18 & this.k;
                i18 >>>= this.w;
                i19 = i20 + this.w;
            }
        } else if (this.w < 57) {
            int i21 = 0;
            while (i21 <= (this.mdsize << 3) - this.w) {
                int i22 = i21 >>> 3;
                int i23 = i21 % 8;
                i21 += this.w;
                int i24 = (i21 + 7) >>> 3;
                long j3 = 0;
                int i25 = 0;
                for (int i26 = i22; i26 < i24; i26++) {
                    j3 ^= (long) ((bArr3[i26] & 255) << (i25 << 3));
                    i25++;
                }
                i = (int) (((long) i) + ((j3 >>> i23) & ((long) this.k)));
            }
            int i27 = i21 >>> 3;
            if (i27 < this.mdsize) {
                int i28 = i21 % 8;
                long j4 = 0;
                int i29 = 0;
                for (int i30 = i27; i30 < this.mdsize; i30++) {
                    j4 ^= (long) ((bArr3[i30] & 255) << (i29 << 3));
                    i29++;
                }
                i = (int) (((long) i) + ((j4 >>> i28) & ((long) this.k)));
            }
            this.checksum = (this.messagesize << this.w) - i;
            int i31 = this.checksum;
            int i32 = 0;
            while (true) {
                int i33 = i32;
                if (i33 >= log) {
                    break;
                }
                i += i31 & this.k;
                i31 >>>= this.w;
                i32 = i33 + this.w;
            }
        }
        this.keysize = this.messagesize + ((int) Math.ceil(((double) log) / ((double) this.w)));
        this.steps = (int) Math.ceil(((double) (this.keysize + i)) / ((double) (1 << this.height)));
        this.sign = new byte[this.keysize * this.mdsize];
        this.counter = 0;
        this.test = 0;
        this.ii = 0;
        this.test8 = 0L;
        this.r = 0;
        this.privateKeyOTS = new byte[this.mdsize];
        this.seed = new byte[this.mdsize];
        System.arraycopy(bArr, 0, this.seed, 0, this.mdsize);
    }

    public boolean updateSign() {
        for (int i = 0; i < this.steps; i++) {
            if (this.counter < this.keysize) {
                oneStep();
            }
            if (this.counter == this.keysize) {
                return true;
            }
        }
        return false;
    }

    public byte[] getSig() {
        return this.sign;
    }

    private void oneStep() {
        int i;
        if (8 % this.w == 0) {
            if (this.test == 0) {
                this.privateKeyOTS = this.gmssRandom.nextSeed(this.seed);
                if (this.ii < this.mdsize) {
                    this.test = this.hash[this.ii] & this.k;
                    this.hash[this.ii] = (byte) (this.hash[this.ii] >>> this.w);
                } else {
                    this.test = this.checksum & this.k;
                    this.checksum >>>= this.w;
                }
            } else if (this.test > 0) {
                this.messDigestOTS.update(this.privateKeyOTS, 0, this.privateKeyOTS.length);
                this.privateKeyOTS = new byte[this.messDigestOTS.getDigestSize()];
                this.messDigestOTS.doFinal(this.privateKeyOTS, 0);
                this.test--;
            }
            if (this.test == 0) {
                System.arraycopy(this.privateKeyOTS, 0, this.sign, this.counter * this.mdsize, this.mdsize);
                this.counter++;
                if (this.counter % (8 / this.w) == 0) {
                    this.ii++;
                    return;
                }
                return;
            }
            return;
        }
        if (this.w < 8) {
            if (this.test == 0) {
                if (this.counter % 8 == 0 && this.ii < this.mdsize) {
                    this.big8 = 0L;
                    if (this.counter < ((this.mdsize / this.w) << 3)) {
                        for (int i2 = 0; i2 < this.w; i2++) {
                            this.big8 ^= (long) ((this.hash[this.ii] & 255) << (i2 << 3));
                            this.ii++;
                        }
                    } else {
                        for (int i3 = 0; i3 < this.mdsize % this.w; i3++) {
                            this.big8 ^= (long) ((this.hash[this.ii] & 255) << (i3 << 3));
                            this.ii++;
                        }
                    }
                }
                if (this.counter == this.messagesize) {
                    this.big8 = this.checksum;
                }
                this.test = (int) (this.big8 & ((long) this.k));
                this.privateKeyOTS = this.gmssRandom.nextSeed(this.seed);
            } else if (this.test > 0) {
                this.messDigestOTS.update(this.privateKeyOTS, 0, this.privateKeyOTS.length);
                this.privateKeyOTS = new byte[this.messDigestOTS.getDigestSize()];
                this.messDigestOTS.doFinal(this.privateKeyOTS, 0);
                this.test--;
            }
            if (this.test == 0) {
                System.arraycopy(this.privateKeyOTS, 0, this.sign, this.counter * this.mdsize, this.mdsize);
                this.big8 >>>= this.w;
                this.counter++;
                return;
            }
            return;
        }
        if (this.w < 57) {
            if (this.test8 == 0) {
                this.big8 = 0L;
                this.ii = 0;
                int i4 = this.r % 8;
                int i5 = this.r >>> 3;
                if (i5 < this.mdsize) {
                    if (this.r <= (this.mdsize << 3) - this.w) {
                        this.r += this.w;
                        i = (this.r + 7) >>> 3;
                    } else {
                        i = this.mdsize;
                        this.r += this.w;
                    }
                    for (int i6 = i5; i6 < i; i6++) {
                        this.big8 ^= (long) ((this.hash[i6] & 255) << (this.ii << 3));
                        this.ii++;
                    }
                    this.big8 >>>= i4;
                    this.test8 = this.big8 & ((long) this.k);
                } else {
                    this.test8 = this.checksum & this.k;
                    this.checksum >>>= this.w;
                }
                this.privateKeyOTS = this.gmssRandom.nextSeed(this.seed);
            } else if (this.test8 > 0) {
                this.messDigestOTS.update(this.privateKeyOTS, 0, this.privateKeyOTS.length);
                this.privateKeyOTS = new byte[this.messDigestOTS.getDigestSize()];
                this.messDigestOTS.doFinal(this.privateKeyOTS, 0);
                this.test8--;
            }
            if (this.test8 == 0) {
                System.arraycopy(this.privateKeyOTS, 0, this.sign, this.counter * this.mdsize, this.mdsize);
                this.counter++;
            }
        }
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

    public byte[][] getStatByte() {
        return new byte[][]{this.privateKeyOTS, this.seed, this.hash, this.sign, getStatLong()};
    }

    public int[] getStatInt() {
        return new int[]{this.counter, this.test, this.ii, this.r, this.steps, this.keysize, this.height, this.w, this.checksum};
    }

    public byte[] getStatLong() {
        return new byte[]{(byte) (this.test8 & 255), (byte) ((this.test8 >> 8) & 255), (byte) ((this.test8 >> 16) & 255), (byte) ((this.test8 >> 24) & 255), (byte) ((this.test8 >> 32) & 255), (byte) ((this.test8 >> 40) & 255), (byte) ((this.test8 >> 48) & 255), (byte) ((this.test8 >> 56) & 255), (byte) (this.big8 & 255), (byte) ((this.big8 >> 8) & 255), (byte) ((this.big8 >> 16) & 255), (byte) ((this.big8 >> 24) & 255), (byte) ((this.big8 >> 32) & 255), (byte) ((this.big8 >> 40) & 255), (byte) ((this.big8 >> 48) & 255), (byte) ((this.big8 >> 56) & 255)};
    }

    public String toString() {
        String str = "" + this.big8 + "  ";
        int[] iArr = new int[9];
        int[] statInt = getStatInt();
        byte[][] bArr = new byte[5][this.mdsize];
        byte[][] statByte = getStatByte();
        for (int i = 0; i < 9; i++) {
            str = str + statInt[i] + " ";
        }
        for (int i2 = 0; i2 < 5; i2++) {
            str = str + new String(Hex.encode(statByte[i2])) + " ";
        }
        return str;
    }
}
