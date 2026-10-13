package org.spongycastle.pqc.crypto.gmss;

import org.spongycastle.crypto.Digest;
import org.spongycastle.pqc.crypto.gmss.util.GMSSRandom;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/gmss/GMSSLeaf.class */
public class GMSSLeaf {
    private Digest messDigestOTS;
    private int mdsize;
    private int keysize;
    private GMSSRandom gmssRandom;
    private byte[] leaf;
    private byte[] concHashs;
    private int i;
    private int j;
    private int two_power_w;
    private int w;
    private int steps;
    private byte[] seed;
    byte[] privateKeyOTS;

    public GMSSLeaf(Digest digest, byte[][] otsIndex, int[] numLeafs) {
        this.i = numLeafs[0];
        this.j = numLeafs[1];
        this.steps = numLeafs[2];
        this.w = numLeafs[3];
        this.messDigestOTS = digest;
        this.gmssRandom = new GMSSRandom(this.messDigestOTS);
        this.mdsize = this.messDigestOTS.getDigestSize();
        int mdsizeBit = this.mdsize << 3;
        int messagesize = (int) Math.ceil(((double) mdsizeBit) / ((double) this.w));
        int checksumsize = getLog((messagesize << this.w) + 1);
        this.keysize = messagesize + ((int) Math.ceil(((double) checksumsize) / ((double) this.w)));
        this.two_power_w = 1 << this.w;
        this.privateKeyOTS = otsIndex[0];
        this.seed = otsIndex[1];
        this.concHashs = otsIndex[2];
        this.leaf = otsIndex[3];
    }

    GMSSLeaf(Digest digest, int w, int numLeafs) {
        this.w = w;
        this.messDigestOTS = digest;
        this.gmssRandom = new GMSSRandom(this.messDigestOTS);
        this.mdsize = this.messDigestOTS.getDigestSize();
        int mdsizeBit = this.mdsize << 3;
        int messagesize = (int) Math.ceil(((double) mdsizeBit) / ((double) w));
        int checksumsize = getLog((messagesize << w) + 1);
        this.keysize = messagesize + ((int) Math.ceil(((double) checksumsize) / ((double) w)));
        this.two_power_w = 1 << w;
        this.steps = (int) Math.ceil(((double) (((((1 << w) - 1) * this.keysize) + 1) + this.keysize)) / ((double) numLeafs));
        this.seed = new byte[this.mdsize];
        this.leaf = new byte[this.mdsize];
        this.privateKeyOTS = new byte[this.mdsize];
        this.concHashs = new byte[this.mdsize * this.keysize];
    }

    public GMSSLeaf(Digest digest, int w, int numLeafs, byte[] seed0) {
        this.w = w;
        this.messDigestOTS = digest;
        this.gmssRandom = new GMSSRandom(this.messDigestOTS);
        this.mdsize = this.messDigestOTS.getDigestSize();
        int mdsizeBit = this.mdsize << 3;
        int messagesize = (int) Math.ceil(((double) mdsizeBit) / ((double) w));
        int checksumsize = getLog((messagesize << w) + 1);
        this.keysize = messagesize + ((int) Math.ceil(((double) checksumsize) / ((double) w)));
        this.two_power_w = 1 << w;
        this.steps = (int) Math.ceil(((double) (((((1 << w) - 1) * this.keysize) + 1) + this.keysize)) / ((double) numLeafs));
        this.seed = new byte[this.mdsize];
        this.leaf = new byte[this.mdsize];
        this.privateKeyOTS = new byte[this.mdsize];
        this.concHashs = new byte[this.mdsize * this.keysize];
        initLeafCalc(seed0);
    }

    private GMSSLeaf(GMSSLeaf original) {
        this.messDigestOTS = original.messDigestOTS;
        this.mdsize = original.mdsize;
        this.keysize = original.keysize;
        this.gmssRandom = original.gmssRandom;
        this.leaf = Arrays.clone(original.leaf);
        this.concHashs = Arrays.clone(original.concHashs);
        this.i = original.i;
        this.j = original.j;
        this.two_power_w = original.two_power_w;
        this.w = original.w;
        this.steps = original.steps;
        this.seed = Arrays.clone(original.seed);
        this.privateKeyOTS = Arrays.clone(original.privateKeyOTS);
    }

    void initLeafCalc(byte[] seed0) {
        this.i = 0;
        this.j = 0;
        byte[] dummy = new byte[this.mdsize];
        System.arraycopy(seed0, 0, dummy, 0, this.seed.length);
        this.seed = this.gmssRandom.nextSeed(dummy);
    }

    GMSSLeaf nextLeaf() {
        GMSSLeaf nextLeaf = new GMSSLeaf(this);
        nextLeaf.updateLeafCalc();
        return nextLeaf;
    }

    private void updateLeafCalc() {
        byte[] buf = new byte[this.messDigestOTS.getDigestSize()];
        for (int s = 0; s < this.steps + 10000; s++) {
            if (this.i == this.keysize && this.j == this.two_power_w - 1) {
                this.messDigestOTS.update(this.concHashs, 0, this.concHashs.length);
                this.leaf = new byte[this.messDigestOTS.getDigestSize()];
                this.messDigestOTS.doFinal(this.leaf, 0);
                return;
            }
            if (this.i == 0 || this.j == this.two_power_w - 1) {
                this.i++;
                this.j = 0;
                this.privateKeyOTS = this.gmssRandom.nextSeed(this.seed);
            } else {
                this.messDigestOTS.update(this.privateKeyOTS, 0, this.privateKeyOTS.length);
                this.privateKeyOTS = buf;
                this.messDigestOTS.doFinal(this.privateKeyOTS, 0);
                this.j++;
                if (this.j == this.two_power_w - 1) {
                    System.arraycopy(this.privateKeyOTS, 0, this.concHashs, this.mdsize * (this.i - 1), this.mdsize);
                }
            }
        }
        throw new IllegalStateException("unable to updateLeaf in steps: " + this.steps + " " + this.i + " " + this.j);
    }

    public byte[] getLeaf() {
        return Arrays.clone(this.leaf);
    }

    private int getLog(int intValue) {
        int log = 1;
        int i = 2;
        while (i < intValue) {
            i <<= 1;
            log++;
        }
        return log;
    }

    public byte[][] getStatByte() {
        byte[][] statByte = {new byte[this.mdsize], new byte[this.mdsize], new byte[this.mdsize * this.keysize], new byte[this.mdsize]};
        statByte[0] = this.privateKeyOTS;
        statByte[1] = this.seed;
        statByte[2] = this.concHashs;
        statByte[3] = this.leaf;
        return statByte;
    }

    public int[] getStatInt() {
        int[] statInt = {this.i, this.j, this.steps, this.w};
        return statInt;
    }

    public String toString() {
        String str;
        String out = "";
        for (int i = 0; i < 4; i++) {
            out = out + getStatInt()[i] + " ";
        }
        String out2 = out + " " + this.mdsize + " " + this.keysize + " " + this.two_power_w + " ";
        byte[][] temp = getStatByte();
        for (int i2 = 0; i2 < 4; i2++) {
            if (temp[i2] != null) {
                str = out2 + new String(Hex.encode(temp[i2])) + " ";
            } else {
                str = out2 + "null ";
            }
            out2 = str;
        }
        return out2;
    }
}
