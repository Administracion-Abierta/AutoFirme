package org.spongycastle.pqc.crypto.gmss;

import org.spongycastle.crypto.Digest;
import org.spongycastle.pqc.crypto.gmss.util.GMSSRandom;
import org.spongycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/gmss/GMSSRootSig.class */
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

    public GMSSRootSig(Digest digest, byte[][] statByte, int[] statInt) {
        this.messDigestOTS = digest;
        this.gmssRandom = new GMSSRandom(this.messDigestOTS);
        this.counter = statInt[0];
        this.test = statInt[1];
        this.ii = statInt[2];
        this.r = statInt[3];
        this.steps = statInt[4];
        this.keysize = statInt[5];
        this.height = statInt[6];
        this.w = statInt[7];
        this.checksum = statInt[8];
        this.mdsize = this.messDigestOTS.getDigestSize();
        this.k = (1 << this.w) - 1;
        int mdsizeBit = this.mdsize << 3;
        this.messagesize = (int) Math.ceil(((double) mdsizeBit) / ((double) this.w));
        this.privateKeyOTS = statByte[0];
        this.seed = statByte[1];
        this.hash = statByte[2];
        this.sign = statByte[3];
        this.test8 = ((long) (statByte[4][0] & 255)) | (((long) (statByte[4][1] & 255)) << 8) | (((long) (statByte[4][2] & 255)) << 16) | (((long) (statByte[4][3] & 255)) << 24) | (((long) (statByte[4][4] & 255)) << 32) | (((long) (statByte[4][5] & 255)) << 40) | (((long) (statByte[4][6] & 255)) << 48) | (((long) (statByte[4][7] & 255)) << 56);
        this.big8 = ((long) (statByte[4][8] & 255)) | (((long) (statByte[4][9] & 255)) << 8) | (((long) (statByte[4][10] & 255)) << 16) | (((long) (statByte[4][11] & 255)) << 24) | (((long) (statByte[4][12] & 255)) << 32) | (((long) (statByte[4][13] & 255)) << 40) | (((long) (statByte[4][14] & 255)) << 48) | (((long) (statByte[4][15] & 255)) << 56);
    }

    public GMSSRootSig(Digest digest, int w, int height) {
        this.messDigestOTS = digest;
        this.gmssRandom = new GMSSRandom(this.messDigestOTS);
        this.mdsize = this.messDigestOTS.getDigestSize();
        this.w = w;
        this.height = height;
        this.k = (1 << w) - 1;
        int mdsizeBit = this.mdsize << 3;
        this.messagesize = (int) Math.ceil(((double) mdsizeBit) / ((double) w));
    }

    public void initSign(byte[] seed0, byte[] message) {
        this.hash = new byte[this.mdsize];
        this.messDigestOTS.update(message, 0, message.length);
        this.hash = new byte[this.messDigestOTS.getDigestSize()];
        this.messDigestOTS.doFinal(this.hash, 0);
        byte[] messPart = new byte[this.mdsize];
        System.arraycopy(this.hash, 0, messPart, 0, this.mdsize);
        int sumH = 0;
        int checksumsize = getLog((this.messagesize << this.w) + 1);
        if (8 % this.w == 0) {
            int dt = 8 / this.w;
            for (int a = 0; a < this.mdsize; a++) {
                for (int b = 0; b < dt; b++) {
                    sumH += messPart[a] & this.k;
                    messPart[a] = (byte) (messPart[a] >>> this.w);
                }
            }
            this.checksum = (this.messagesize << this.w) - sumH;
            int checkPart = this.checksum;
            int i = 0;
            while (true) {
                int b2 = i;
                if (b2 >= checksumsize) {
                    break;
                }
                sumH += checkPart & this.k;
                checkPart >>>= this.w;
                i = b2 + this.w;
            }
        } else if (this.w < 8) {
            int ii = 0;
            int dt2 = this.mdsize / this.w;
            for (int i2 = 0; i2 < dt2; i2++) {
                long big8 = 0;
                for (int j = 0; j < this.w; j++) {
                    big8 ^= (long) ((messPart[ii] & 255) << (j << 3));
                    ii++;
                }
                for (int j2 = 0; j2 < 8; j2++) {
                    sumH += (int) (big8 & ((long) this.k));
                    big8 >>>= this.w;
                }
            }
            int dt3 = this.mdsize % this.w;
            long big9 = 0;
            for (int j3 = 0; j3 < dt3; j3++) {
                big9 ^= (long) ((messPart[ii] & 255) << (j3 << 3));
                ii++;
            }
            int dt4 = dt3 << 3;
            int i3 = 0;
            while (true) {
                int j4 = i3;
                if (j4 >= dt4) {
                    break;
                }
                sumH += (int) (big9 & ((long) this.k));
                big9 >>>= this.w;
                i3 = j4 + this.w;
            }
            this.checksum = (this.messagesize << this.w) - sumH;
            int checkPart2 = this.checksum;
            int i4 = 0;
            while (true) {
                int i5 = i4;
                if (i5 >= checksumsize) {
                    break;
                }
                sumH += checkPart2 & this.k;
                checkPart2 >>>= this.w;
                i4 = i5 + this.w;
            }
        } else if (this.w < 57) {
            int r = 0;
            while (r <= (this.mdsize << 3) - this.w) {
                int s = r >>> 3;
                int rest = r % 8;
                r += this.w;
                int f = (r + 7) >>> 3;
                long big10 = 0;
                int ii2 = 0;
                for (int j5 = s; j5 < f; j5++) {
                    big10 ^= (long) ((messPart[j5] & 255) << (ii2 << 3));
                    ii2++;
                }
                sumH = (int) (((long) sumH) + ((big10 >>> rest) & ((long) this.k)));
            }
            int s2 = r >>> 3;
            if (s2 < this.mdsize) {
                int rest2 = r % 8;
                long big11 = 0;
                int ii3 = 0;
                for (int j6 = s2; j6 < this.mdsize; j6++) {
                    big11 ^= (long) ((messPart[j6] & 255) << (ii3 << 3));
                    ii3++;
                }
                sumH = (int) (((long) sumH) + ((big11 >>> rest2) & ((long) this.k)));
            }
            this.checksum = (this.messagesize << this.w) - sumH;
            int checkPart3 = this.checksum;
            int i6 = 0;
            while (true) {
                int i7 = i6;
                if (i7 >= checksumsize) {
                    break;
                }
                sumH += checkPart3 & this.k;
                checkPart3 >>>= this.w;
                i6 = i7 + this.w;
            }
        }
        this.keysize = this.messagesize + ((int) Math.ceil(((double) checksumsize) / ((double) this.w)));
        this.steps = (int) Math.ceil(((double) (this.keysize + sumH)) / ((double) (1 << this.height)));
        this.sign = new byte[this.keysize * this.mdsize];
        this.counter = 0;
        this.test = 0;
        this.ii = 0;
        this.test8 = 0L;
        this.r = 0;
        this.privateKeyOTS = new byte[this.mdsize];
        this.seed = new byte[this.mdsize];
        System.arraycopy(seed0, 0, this.seed, 0, this.mdsize);
    }

    public boolean updateSign() {
        for (int s = 0; s < this.steps; s++) {
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
        int f;
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
                        for (int j = 0; j < this.w; j++) {
                            this.big8 ^= (long) ((this.hash[this.ii] & 255) << (j << 3));
                            this.ii++;
                        }
                    } else {
                        for (int j2 = 0; j2 < this.mdsize % this.w; j2++) {
                            this.big8 ^= (long) ((this.hash[this.ii] & 255) << (j2 << 3));
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
                int rest = this.r % 8;
                int s = this.r >>> 3;
                if (s < this.mdsize) {
                    if (this.r <= (this.mdsize << 3) - this.w) {
                        this.r += this.w;
                        f = (this.r + 7) >>> 3;
                    } else {
                        f = this.mdsize;
                        this.r += this.w;
                    }
                    for (int i = s; i < f; i++) {
                        this.big8 ^= (long) ((this.hash[i] & 255) << (this.ii << 3));
                        this.ii++;
                    }
                    this.big8 >>>= rest;
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

    public int getLog(int intValue) {
        int log = 1;
        int i = 2;
        while (i < intValue) {
            i <<= 1;
            log++;
        }
        return log;
    }

    public byte[][] getStatByte() {
        byte[][] statByte = {this.privateKeyOTS, this.seed, this.hash, this.sign, getStatLong()};
        return statByte;
    }

    public int[] getStatInt() {
        int[] statInt = {this.counter, this.test, this.ii, this.r, this.steps, this.keysize, this.height, this.w, this.checksum};
        return statInt;
    }

    public byte[] getStatLong() {
        byte[] bytes = {(byte) (this.test8 & 255), (byte) ((this.test8 >> 8) & 255), (byte) ((this.test8 >> 16) & 255), (byte) ((this.test8 >> 24) & 255), (byte) ((this.test8 >> 32) & 255), (byte) ((this.test8 >> 40) & 255), (byte) ((this.test8 >> 48) & 255), (byte) ((this.test8 >> 56) & 255), (byte) (this.big8 & 255), (byte) ((this.big8 >> 8) & 255), (byte) ((this.big8 >> 16) & 255), (byte) ((this.big8 >> 24) & 255), (byte) ((this.big8 >> 32) & 255), (byte) ((this.big8 >> 40) & 255), (byte) ((this.big8 >> 48) & 255), (byte) ((this.big8 >> 56) & 255)};
        return bytes;
    }

    public String toString() {
        String out = "" + this.big8 + "  ";
        int[] iArr = new int[9];
        int[] statInt = getStatInt();
        byte[][] bArr = new byte[5][this.mdsize];
        byte[][] statByte = getStatByte();
        for (int i = 0; i < 9; i++) {
            out = out + statInt[i] + " ";
        }
        for (int i2 = 0; i2 < 5; i2++) {
            out = out + new String(Hex.encode(statByte[i2])) + " ";
        }
        return out;
    }
}
