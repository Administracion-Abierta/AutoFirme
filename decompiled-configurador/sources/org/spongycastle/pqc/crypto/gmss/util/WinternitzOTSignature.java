package org.spongycastle.pqc.crypto.gmss.util;

import org.spongycastle.crypto.Digest;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/gmss/util/WinternitzOTSignature.class */
public class WinternitzOTSignature {
    private Digest messDigestOTS;
    private int mdsize;
    private int keysize;
    private byte[][] privateKeyOTS;
    private int w;
    private GMSSRandom gmssRandom;
    private int messagesize;
    private int checksumsize;

    public WinternitzOTSignature(byte[] seed0, Digest digest, int w) {
        this.w = w;
        this.messDigestOTS = digest;
        this.gmssRandom = new GMSSRandom(this.messDigestOTS);
        this.mdsize = this.messDigestOTS.getDigestSize();
        int mdsizeBit = this.mdsize << 3;
        this.messagesize = (int) Math.ceil(((double) mdsizeBit) / ((double) w));
        this.checksumsize = getLog((this.messagesize << w) + 1);
        this.keysize = this.messagesize + ((int) Math.ceil(((double) this.checksumsize) / ((double) w)));
        this.privateKeyOTS = new byte[this.keysize][this.mdsize];
        byte[] dummy = new byte[this.mdsize];
        System.arraycopy(seed0, 0, dummy, 0, dummy.length);
        for (int i = 0; i < this.keysize; i++) {
            this.privateKeyOTS[i] = this.gmssRandom.nextSeed(dummy);
        }
    }

    public byte[][] getPrivateKey() {
        return this.privateKeyOTS;
    }

    public byte[] getPublicKey() {
        byte[] helppubKey = new byte[this.keysize * this.mdsize];
        byte[] bArr = new byte[this.mdsize];
        int two_power_t = 1 << this.w;
        for (int i = 0; i < this.keysize; i++) {
            this.messDigestOTS.update(this.privateKeyOTS[i], 0, this.privateKeyOTS[i].length);
            byte[] help = new byte[this.messDigestOTS.getDigestSize()];
            this.messDigestOTS.doFinal(help, 0);
            for (int j = 2; j < two_power_t; j++) {
                this.messDigestOTS.update(help, 0, help.length);
                help = new byte[this.messDigestOTS.getDigestSize()];
                this.messDigestOTS.doFinal(help, 0);
            }
            System.arraycopy(help, 0, helppubKey, this.mdsize * i, this.mdsize);
        }
        this.messDigestOTS.update(helppubKey, 0, helppubKey.length);
        byte[] tmp = new byte[this.messDigestOTS.getDigestSize()];
        this.messDigestOTS.doFinal(tmp, 0);
        return tmp;
    }

    public byte[] getSignature(byte[] message) {
        byte[] sign = new byte[this.keysize * this.mdsize];
        byte[] bArr = new byte[this.mdsize];
        int counter = 0;
        int c = 0;
        this.messDigestOTS.update(message, 0, message.length);
        byte[] hash = new byte[this.messDigestOTS.getDigestSize()];
        this.messDigestOTS.doFinal(hash, 0);
        if (8 % this.w == 0) {
            int d = 8 / this.w;
            int k = (1 << this.w) - 1;
            byte[] hlp = new byte[this.mdsize];
            for (int i = 0; i < hash.length; i++) {
                for (int j = 0; j < d; j++) {
                    int test = hash[i] & k;
                    c += test;
                    System.arraycopy(this.privateKeyOTS[counter], 0, hlp, 0, this.mdsize);
                    while (test > 0) {
                        this.messDigestOTS.update(hlp, 0, hlp.length);
                        hlp = new byte[this.messDigestOTS.getDigestSize()];
                        this.messDigestOTS.doFinal(hlp, 0);
                        test--;
                    }
                    System.arraycopy(hlp, 0, sign, counter * this.mdsize, this.mdsize);
                    hash[i] = (byte) (hash[i] >>> this.w);
                    counter++;
                }
            }
            int c2 = (this.messagesize << this.w) - c;
            int i2 = 0;
            while (true) {
                int i3 = i2;
                if (i3 >= this.checksumsize) {
                    break;
                }
                System.arraycopy(this.privateKeyOTS[counter], 0, hlp, 0, this.mdsize);
                for (int test2 = c2 & k; test2 > 0; test2--) {
                    this.messDigestOTS.update(hlp, 0, hlp.length);
                    hlp = new byte[this.messDigestOTS.getDigestSize()];
                    this.messDigestOTS.doFinal(hlp, 0);
                }
                System.arraycopy(hlp, 0, sign, counter * this.mdsize, this.mdsize);
                c2 >>>= this.w;
                counter++;
                i2 = i3 + this.w;
            }
        } else if (this.w < 8) {
            int d2 = this.mdsize / this.w;
            int k2 = (1 << this.w) - 1;
            byte[] hlp2 = new byte[this.mdsize];
            int ii = 0;
            for (int i4 = 0; i4 < d2; i4++) {
                long big8 = 0;
                for (int j2 = 0; j2 < this.w; j2++) {
                    big8 ^= (long) ((hash[ii] & 255) << (j2 << 3));
                    ii++;
                }
                for (int j3 = 0; j3 < 8; j3++) {
                    int test3 = (int) (big8 & ((long) k2));
                    c += test3;
                    System.arraycopy(this.privateKeyOTS[counter], 0, hlp2, 0, this.mdsize);
                    while (test3 > 0) {
                        this.messDigestOTS.update(hlp2, 0, hlp2.length);
                        hlp2 = new byte[this.messDigestOTS.getDigestSize()];
                        this.messDigestOTS.doFinal(hlp2, 0);
                        test3--;
                    }
                    System.arraycopy(hlp2, 0, sign, counter * this.mdsize, this.mdsize);
                    big8 >>>= this.w;
                    counter++;
                }
            }
            int d3 = this.mdsize % this.w;
            long big9 = 0;
            for (int j4 = 0; j4 < d3; j4++) {
                big9 ^= (long) ((hash[ii] & 255) << (j4 << 3));
                ii++;
            }
            int d4 = d3 << 3;
            int i5 = 0;
            while (true) {
                int j5 = i5;
                if (j5 >= d4) {
                    break;
                }
                int test4 = (int) (big9 & ((long) k2));
                c += test4;
                System.arraycopy(this.privateKeyOTS[counter], 0, hlp2, 0, this.mdsize);
                while (test4 > 0) {
                    this.messDigestOTS.update(hlp2, 0, hlp2.length);
                    hlp2 = new byte[this.messDigestOTS.getDigestSize()];
                    this.messDigestOTS.doFinal(hlp2, 0);
                    test4--;
                }
                System.arraycopy(hlp2, 0, sign, counter * this.mdsize, this.mdsize);
                big9 >>>= this.w;
                counter++;
                i5 = j5 + this.w;
            }
            int c3 = (this.messagesize << this.w) - c;
            int i6 = 0;
            while (true) {
                int i7 = i6;
                if (i7 >= this.checksumsize) {
                    break;
                }
                System.arraycopy(this.privateKeyOTS[counter], 0, hlp2, 0, this.mdsize);
                for (int test5 = c3 & k2; test5 > 0; test5--) {
                    this.messDigestOTS.update(hlp2, 0, hlp2.length);
                    hlp2 = new byte[this.messDigestOTS.getDigestSize()];
                    this.messDigestOTS.doFinal(hlp2, 0);
                }
                System.arraycopy(hlp2, 0, sign, counter * this.mdsize, this.mdsize);
                c3 >>>= this.w;
                counter++;
                i6 = i7 + this.w;
            }
        } else if (this.w < 57) {
            int d5 = (this.mdsize << 3) - this.w;
            int k3 = (1 << this.w) - 1;
            byte[] hlp3 = new byte[this.mdsize];
            int r = 0;
            while (r <= d5) {
                int s = r >>> 3;
                int rest = r % 8;
                r += this.w;
                int f = (r + 7) >>> 3;
                long big10 = 0;
                int ii2 = 0;
                for (int j6 = s; j6 < f; j6++) {
                    big10 ^= (long) ((hash[j6] & 255) << (ii2 << 3));
                    ii2++;
                }
                long test8 = (big10 >>> rest) & ((long) k3);
                c = (int) (((long) c) + test8);
                System.arraycopy(this.privateKeyOTS[counter], 0, hlp3, 0, this.mdsize);
                while (test8 > 0) {
                    this.messDigestOTS.update(hlp3, 0, hlp3.length);
                    hlp3 = new byte[this.messDigestOTS.getDigestSize()];
                    this.messDigestOTS.doFinal(hlp3, 0);
                    test8--;
                }
                System.arraycopy(hlp3, 0, sign, counter * this.mdsize, this.mdsize);
                counter++;
            }
            int s2 = r >>> 3;
            if (s2 < this.mdsize) {
                int rest2 = r % 8;
                long big11 = 0;
                int ii3 = 0;
                for (int j7 = s2; j7 < this.mdsize; j7++) {
                    big11 ^= (long) ((hash[j7] & 255) << (ii3 << 3));
                    ii3++;
                }
                long test9 = (big11 >>> rest2) & ((long) k3);
                c = (int) (((long) c) + test9);
                System.arraycopy(this.privateKeyOTS[counter], 0, hlp3, 0, this.mdsize);
                while (test9 > 0) {
                    this.messDigestOTS.update(hlp3, 0, hlp3.length);
                    hlp3 = new byte[this.messDigestOTS.getDigestSize()];
                    this.messDigestOTS.doFinal(hlp3, 0);
                    test9--;
                }
                System.arraycopy(hlp3, 0, sign, counter * this.mdsize, this.mdsize);
                counter++;
            }
            int c4 = (this.messagesize << this.w) - c;
            int i8 = 0;
            while (true) {
                int i9 = i8;
                if (i9 >= this.checksumsize) {
                    break;
                }
                System.arraycopy(this.privateKeyOTS[counter], 0, hlp3, 0, this.mdsize);
                for (long test10 = c4 & k3; test10 > 0; test10--) {
                    this.messDigestOTS.update(hlp3, 0, hlp3.length);
                    hlp3 = new byte[this.messDigestOTS.getDigestSize()];
                    this.messDigestOTS.doFinal(hlp3, 0);
                }
                System.arraycopy(hlp3, 0, sign, counter * this.mdsize, this.mdsize);
                c4 >>>= this.w;
                counter++;
                i8 = i9 + this.w;
            }
        }
        return sign;
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
}
