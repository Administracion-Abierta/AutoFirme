package org.spongycastle.pqc.crypto.gmss;

import java.util.Vector;
import org.spongycastle.crypto.Digest;
import org.spongycastle.pqc.crypto.gmss.util.GMSSRandom;
import org.spongycastle.pqc.crypto.gmss.util.WinternitzOTSignature;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/gmss/GMSSPrivateKeyParameters.class */
public class GMSSPrivateKeyParameters extends GMSSKeyParameters {
    private int[] index;
    private byte[][] currentSeeds;
    private byte[][] nextNextSeeds;
    private byte[][][] currentAuthPaths;
    private byte[][][] nextAuthPaths;
    private Treehash[][] currentTreehash;
    private Treehash[][] nextTreehash;
    private Vector[] currentStack;
    private Vector[] nextStack;
    private Vector[][] currentRetain;
    private Vector[][] nextRetain;
    private byte[][][] keep;
    private GMSSLeaf[] nextNextLeaf;
    private GMSSLeaf[] upperLeaf;
    private GMSSLeaf[] upperTreehashLeaf;
    private int[] minTreehash;
    private GMSSParameters gmssPS;
    private byte[][] nextRoot;
    private GMSSRootCalc[] nextNextRoot;
    private byte[][] currentRootSig;
    private GMSSRootSig[] nextRootSig;
    private GMSSDigestProvider digestProvider;
    private boolean used;
    private int[] heightOfTrees;
    private int[] otsIndex;
    private int[] K;
    private int numLayer;
    private Digest messDigestTrees;
    private int mdLength;
    private GMSSRandom gmssRandom;
    private int[] numLeafs;

    public GMSSPrivateKeyParameters(byte[][] currentSeed, byte[][] nextNextSeed, byte[][][] currentAuthPath, byte[][][] nextAuthPath, Treehash[][] currentTreehash, Treehash[][] nextTreehash, Vector[] currentStack, Vector[] nextStack, Vector[][] currentRetain, Vector[][] nextRetain, byte[][] nextRoot, byte[][] currentRootSig, GMSSParameters gmssParameterset, GMSSDigestProvider digestProvider) {
        this(null, currentSeed, nextNextSeed, currentAuthPath, nextAuthPath, (byte[][][]) null, currentTreehash, nextTreehash, currentStack, nextStack, currentRetain, nextRetain, null, null, null, null, nextRoot, null, currentRootSig, null, gmssParameterset, digestProvider);
    }

    public GMSSPrivateKeyParameters(int[] index, byte[][] currentSeeds, byte[][] nextNextSeeds, byte[][][] currentAuthPaths, byte[][][] nextAuthPaths, byte[][][] keep, Treehash[][] currentTreehash, Treehash[][] nextTreehash, Vector[] currentStack, Vector[] nextStack, Vector[][] currentRetain, Vector[][] nextRetain, GMSSLeaf[] nextNextLeaf, GMSSLeaf[] upperLeaf, GMSSLeaf[] upperTreehashLeaf, int[] minTreehash, byte[][] nextRoot, GMSSRootCalc[] nextNextRoot, byte[][] currentRootSig, GMSSRootSig[] nextRootSig, GMSSParameters gmssParameterset, GMSSDigestProvider digestProvider) {
        super(true, gmssParameterset);
        this.used = false;
        this.messDigestTrees = digestProvider.get();
        this.mdLength = this.messDigestTrees.getDigestSize();
        this.gmssPS = gmssParameterset;
        this.otsIndex = gmssParameterset.getWinternitzParameter();
        this.K = gmssParameterset.getK();
        this.heightOfTrees = gmssParameterset.getHeightOfTrees();
        this.numLayer = this.gmssPS.getNumOfLayers();
        if (index == null) {
            this.index = new int[this.numLayer];
            for (int i = 0; i < this.numLayer; i++) {
                this.index[i] = 0;
            }
        } else {
            this.index = index;
        }
        this.currentSeeds = currentSeeds;
        this.nextNextSeeds = nextNextSeeds;
        this.currentAuthPaths = currentAuthPaths;
        this.nextAuthPaths = nextAuthPaths;
        if (keep == null) {
            this.keep = new byte[this.numLayer][][];
            for (int i2 = 0; i2 < this.numLayer; i2++) {
                this.keep[i2] = new byte[(int) Math.floor(this.heightOfTrees[i2] / 2)][this.mdLength];
            }
        } else {
            this.keep = keep;
        }
        if (currentStack == null) {
            this.currentStack = new Vector[this.numLayer];
            for (int i3 = 0; i3 < this.numLayer; i3++) {
                this.currentStack[i3] = new Vector();
            }
        } else {
            this.currentStack = currentStack;
        }
        if (nextStack == null) {
            this.nextStack = new Vector[this.numLayer - 1];
            for (int i4 = 0; i4 < this.numLayer - 1; i4++) {
                this.nextStack[i4] = new Vector();
            }
        } else {
            this.nextStack = nextStack;
        }
        this.currentTreehash = currentTreehash;
        this.nextTreehash = nextTreehash;
        this.currentRetain = currentRetain;
        this.nextRetain = nextRetain;
        this.nextRoot = nextRoot;
        this.digestProvider = digestProvider;
        if (nextNextRoot == null) {
            this.nextNextRoot = new GMSSRootCalc[this.numLayer - 1];
            for (int i5 = 0; i5 < this.numLayer - 1; i5++) {
                this.nextNextRoot[i5] = new GMSSRootCalc(this.heightOfTrees[i5 + 1], this.K[i5 + 1], this.digestProvider);
            }
        } else {
            this.nextNextRoot = nextNextRoot;
        }
        this.currentRootSig = currentRootSig;
        this.numLeafs = new int[this.numLayer];
        for (int i6 = 0; i6 < this.numLayer; i6++) {
            this.numLeafs[i6] = 1 << this.heightOfTrees[i6];
        }
        this.gmssRandom = new GMSSRandom(this.messDigestTrees);
        if (this.numLayer > 1) {
            if (nextNextLeaf == null) {
                this.nextNextLeaf = new GMSSLeaf[this.numLayer - 2];
                for (int i7 = 0; i7 < this.numLayer - 2; i7++) {
                    this.nextNextLeaf[i7] = new GMSSLeaf(digestProvider.get(), this.otsIndex[i7 + 1], this.numLeafs[i7 + 2], this.nextNextSeeds[i7]);
                }
            } else {
                this.nextNextLeaf = nextNextLeaf;
            }
        } else {
            this.nextNextLeaf = new GMSSLeaf[0];
        }
        if (upperLeaf == null) {
            this.upperLeaf = new GMSSLeaf[this.numLayer - 1];
            for (int i8 = 0; i8 < this.numLayer - 1; i8++) {
                this.upperLeaf[i8] = new GMSSLeaf(digestProvider.get(), this.otsIndex[i8], this.numLeafs[i8 + 1], this.currentSeeds[i8]);
            }
        } else {
            this.upperLeaf = upperLeaf;
        }
        if (upperTreehashLeaf == null) {
            this.upperTreehashLeaf = new GMSSLeaf[this.numLayer - 1];
            for (int i9 = 0; i9 < this.numLayer - 1; i9++) {
                this.upperTreehashLeaf[i9] = new GMSSLeaf(digestProvider.get(), this.otsIndex[i9], this.numLeafs[i9 + 1]);
            }
        } else {
            this.upperTreehashLeaf = upperTreehashLeaf;
        }
        if (minTreehash == null) {
            this.minTreehash = new int[this.numLayer - 1];
            for (int i10 = 0; i10 < this.numLayer - 1; i10++) {
                this.minTreehash[i10] = -1;
            }
        } else {
            this.minTreehash = minTreehash;
        }
        byte[] dummy = new byte[this.mdLength];
        byte[] bArr = new byte[this.mdLength];
        if (nextRootSig == null) {
            this.nextRootSig = new GMSSRootSig[this.numLayer - 1];
            for (int i11 = 0; i11 < this.numLayer - 1; i11++) {
                System.arraycopy(currentSeeds[i11], 0, dummy, 0, this.mdLength);
                this.gmssRandom.nextSeed(dummy);
                byte[] OTSseed = this.gmssRandom.nextSeed(dummy);
                this.nextRootSig[i11] = new GMSSRootSig(digestProvider.get(), this.otsIndex[i11], this.heightOfTrees[i11 + 1]);
                this.nextRootSig[i11].initSign(OTSseed, nextRoot[i11]);
            }
            return;
        }
        this.nextRootSig = nextRootSig;
    }

    private GMSSPrivateKeyParameters(GMSSPrivateKeyParameters original) {
        super(true, original.getParameters());
        this.used = false;
        this.index = Arrays.clone(original.index);
        this.currentSeeds = Arrays.clone(original.currentSeeds);
        this.nextNextSeeds = Arrays.clone(original.nextNextSeeds);
        this.currentAuthPaths = Arrays.clone(original.currentAuthPaths);
        this.nextAuthPaths = Arrays.clone(original.nextAuthPaths);
        this.currentTreehash = original.currentTreehash;
        this.nextTreehash = original.nextTreehash;
        this.currentStack = original.currentStack;
        this.nextStack = original.nextStack;
        this.currentRetain = original.currentRetain;
        this.nextRetain = original.nextRetain;
        this.keep = Arrays.clone(original.keep);
        this.nextNextLeaf = original.nextNextLeaf;
        this.upperLeaf = original.upperLeaf;
        this.upperTreehashLeaf = original.upperTreehashLeaf;
        this.minTreehash = original.minTreehash;
        this.gmssPS = original.gmssPS;
        this.nextRoot = Arrays.clone(original.nextRoot);
        this.nextNextRoot = original.nextNextRoot;
        this.currentRootSig = original.currentRootSig;
        this.nextRootSig = original.nextRootSig;
        this.digestProvider = original.digestProvider;
        this.heightOfTrees = original.heightOfTrees;
        this.otsIndex = original.otsIndex;
        this.K = original.K;
        this.numLayer = original.numLayer;
        this.messDigestTrees = original.messDigestTrees;
        this.mdLength = original.mdLength;
        this.gmssRandom = original.gmssRandom;
        this.numLeafs = original.numLeafs;
    }

    public boolean isUsed() {
        return this.used;
    }

    public void markUsed() {
        this.used = true;
    }

    public GMSSPrivateKeyParameters nextKey() {
        GMSSPrivateKeyParameters nKey = new GMSSPrivateKeyParameters(this);
        nKey.nextKey(this.gmssPS.getNumOfLayers() - 1);
        return nKey;
    }

    private void nextKey(int layer) {
        if (layer == this.numLayer - 1) {
            int[] iArr = this.index;
            iArr[layer] = iArr[layer] + 1;
        }
        if (this.index[layer] == this.numLeafs[layer]) {
            if (this.numLayer != 1) {
                nextTree(layer);
                this.index[layer] = 0;
                return;
            }
            return;
        }
        updateKey(layer);
    }

    private void nextTree(int layer) {
        if (layer > 0) {
            int[] iArr = this.index;
            int i = layer - 1;
            iArr[i] = iArr[i] + 1;
            boolean lastTree = true;
            int z = layer;
            do {
                z--;
                if (this.index[z] < this.numLeafs[z]) {
                    lastTree = false;
                }
                if (!lastTree) {
                    break;
                }
            } while (z > 0);
            if (!lastTree) {
                this.gmssRandom.nextSeed(this.currentSeeds[layer]);
                this.nextRootSig[layer - 1].updateSign();
                if (layer > 1) {
                    this.nextNextLeaf[(layer - 1) - 1] = this.nextNextLeaf[(layer - 1) - 1].nextLeaf();
                }
                this.upperLeaf[layer - 1] = this.upperLeaf[layer - 1].nextLeaf();
                if (this.minTreehash[layer - 1] >= 0) {
                    this.upperTreehashLeaf[layer - 1] = this.upperTreehashLeaf[layer - 1].nextLeaf();
                    byte[] leaf = this.upperTreehashLeaf[layer - 1].getLeaf();
                    try {
                        this.currentTreehash[layer - 1][this.minTreehash[layer - 1]].update(this.gmssRandom, leaf);
                        if (this.currentTreehash[layer - 1][this.minTreehash[layer - 1]].wasFinished()) {
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
                updateNextNextAuthRoot(layer);
                this.currentRootSig[layer - 1] = this.nextRootSig[layer - 1].getSig();
                for (int i2 = 0; i2 < this.heightOfTrees[layer] - this.K[layer]; i2++) {
                    this.currentTreehash[layer][i2] = this.nextTreehash[layer - 1][i2];
                    this.nextTreehash[layer - 1][i2] = this.nextNextRoot[layer - 1].getTreehash()[i2];
                }
                for (int i3 = 0; i3 < this.heightOfTrees[layer]; i3++) {
                    System.arraycopy(this.nextAuthPaths[layer - 1][i3], 0, this.currentAuthPaths[layer][i3], 0, this.mdLength);
                    System.arraycopy(this.nextNextRoot[layer - 1].getAuthPath()[i3], 0, this.nextAuthPaths[layer - 1][i3], 0, this.mdLength);
                }
                for (int i4 = 0; i4 < this.K[layer] - 1; i4++) {
                    this.currentRetain[layer][i4] = this.nextRetain[layer - 1][i4];
                    this.nextRetain[layer - 1][i4] = this.nextNextRoot[layer - 1].getRetain()[i4];
                }
                this.currentStack[layer] = this.nextStack[layer - 1];
                this.nextStack[layer - 1] = this.nextNextRoot[layer - 1].getStack();
                this.nextRoot[layer - 1] = this.nextNextRoot[layer - 1].getRoot();
                byte[] bArr = new byte[this.mdLength];
                byte[] dummy = new byte[this.mdLength];
                System.arraycopy(this.currentSeeds[layer - 1], 0, dummy, 0, this.mdLength);
                this.gmssRandom.nextSeed(dummy);
                this.gmssRandom.nextSeed(dummy);
                byte[] OTSseed = this.gmssRandom.nextSeed(dummy);
                this.nextRootSig[layer - 1].initSign(OTSseed, this.nextRoot[layer - 1]);
                nextKey(layer - 1);
            }
        }
    }

    private void updateKey(int layer) {
        computeAuthPaths(layer);
        if (layer > 0) {
            if (layer > 1) {
                this.nextNextLeaf[(layer - 1) - 1] = this.nextNextLeaf[(layer - 1) - 1].nextLeaf();
            }
            this.upperLeaf[layer - 1] = this.upperLeaf[layer - 1].nextLeaf();
            int t = (int) Math.floor(((double) (getNumLeafs(layer) * 2)) / ((double) (this.heightOfTrees[layer - 1] - this.K[layer - 1])));
            if (this.index[layer] % t == 1) {
                if (this.index[layer] > 1 && this.minTreehash[layer - 1] >= 0) {
                    byte[] leaf = this.upperTreehashLeaf[layer - 1].getLeaf();
                    try {
                        this.currentTreehash[layer - 1][this.minTreehash[layer - 1]].update(this.gmssRandom, leaf);
                        if (this.currentTreehash[layer - 1][this.minTreehash[layer - 1]].wasFinished()) {
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
                this.minTreehash[layer - 1] = getMinTreehashIndex(layer - 1);
                if (this.minTreehash[layer - 1] >= 0) {
                    byte[] seed = this.currentTreehash[layer - 1][this.minTreehash[layer - 1]].getSeedActive();
                    this.upperTreehashLeaf[layer - 1] = new GMSSLeaf(this.digestProvider.get(), this.otsIndex[layer - 1], t, seed);
                    this.upperTreehashLeaf[layer - 1] = this.upperTreehashLeaf[layer - 1].nextLeaf();
                }
            } else if (this.minTreehash[layer - 1] >= 0) {
                this.upperTreehashLeaf[layer - 1] = this.upperTreehashLeaf[layer - 1].nextLeaf();
            }
            this.nextRootSig[layer - 1].updateSign();
            if (this.index[layer] == 1) {
                this.nextNextRoot[layer - 1].initialize(new Vector());
            }
            updateNextNextAuthRoot(layer);
        }
    }

    private int getMinTreehashIndex(int layer) {
        int minTreehash = -1;
        for (int h = 0; h < this.heightOfTrees[layer] - this.K[layer]; h++) {
            if (this.currentTreehash[layer][h].wasInitialized() && !this.currentTreehash[layer][h].wasFinished()) {
                if (minTreehash == -1) {
                    minTreehash = h;
                } else if (this.currentTreehash[layer][h].getLowestNodeHeight() < this.currentTreehash[layer][minTreehash].getLowestNodeHeight()) {
                    minTreehash = h;
                }
            }
        }
        return minTreehash;
    }

    private void computeAuthPaths(int layer) {
        byte[] help;
        int Phi = this.index[layer];
        int H = this.heightOfTrees[layer];
        int K = this.K[layer];
        for (int i = 0; i < H - K; i++) {
            this.currentTreehash[layer][i].updateNextSeed(this.gmssRandom);
        }
        int Tau = heightOfPhi(Phi);
        byte[] bArr = new byte[this.mdLength];
        byte[] OTSseed = this.gmssRandom.nextSeed(this.currentSeeds[layer]);
        int L = (Phi >>> (Tau + 1)) & 1;
        byte[] tempKeep = new byte[this.mdLength];
        if (Tau < H - 1 && L == 0) {
            System.arraycopy(this.currentAuthPaths[layer][Tau], 0, tempKeep, 0, this.mdLength);
        }
        byte[] bArr2 = new byte[this.mdLength];
        if (Tau == 0) {
            if (layer == this.numLayer - 1) {
                WinternitzOTSignature ots = new WinternitzOTSignature(OTSseed, this.digestProvider.get(), this.otsIndex[layer]);
                help = ots.getPublicKey();
            } else {
                byte[] dummy = new byte[this.mdLength];
                System.arraycopy(this.currentSeeds[layer], 0, dummy, 0, this.mdLength);
                this.gmssRandom.nextSeed(dummy);
                help = this.upperLeaf[layer].getLeaf();
                this.upperLeaf[layer].initLeafCalc(dummy);
            }
            System.arraycopy(help, 0, this.currentAuthPaths[layer][0], 0, this.mdLength);
        } else {
            byte[] toBeHashed = new byte[this.mdLength << 1];
            System.arraycopy(this.currentAuthPaths[layer][Tau - 1], 0, toBeHashed, 0, this.mdLength);
            System.arraycopy(this.keep[layer][(int) Math.floor((Tau - 1) / 2)], 0, toBeHashed, this.mdLength, this.mdLength);
            this.messDigestTrees.update(toBeHashed, 0, toBeHashed.length);
            this.currentAuthPaths[layer][Tau] = new byte[this.messDigestTrees.getDigestSize()];
            this.messDigestTrees.doFinal(this.currentAuthPaths[layer][Tau], 0);
            for (int i2 = 0; i2 < Tau; i2++) {
                if (i2 < H - K) {
                    if (this.currentTreehash[layer][i2].wasFinished()) {
                        System.arraycopy(this.currentTreehash[layer][i2].getFirstNode(), 0, this.currentAuthPaths[layer][i2], 0, this.mdLength);
                        this.currentTreehash[layer][i2].destroy();
                    } else {
                        System.err.println("Treehash (" + layer + "," + i2 + ") not finished when needed in AuthPathComputation");
                    }
                }
                if (i2 < H - 1 && i2 >= H - K && this.currentRetain[layer][i2 - (H - K)].size() > 0) {
                    System.arraycopy(this.currentRetain[layer][i2 - (H - K)].lastElement(), 0, this.currentAuthPaths[layer][i2], 0, this.mdLength);
                    this.currentRetain[layer][i2 - (H - K)].removeElementAt(this.currentRetain[layer][i2 - (H - K)].size() - 1);
                }
                if (i2 < H - K) {
                    int startPoint = Phi + (3 * (1 << i2));
                    if (startPoint < this.numLeafs[layer]) {
                        this.currentTreehash[layer][i2].initialize();
                    }
                }
            }
        }
        if (Tau < H - 1 && L == 0) {
            System.arraycopy(tempKeep, 0, this.keep[layer][(int) Math.floor(Tau / 2)], 0, this.mdLength);
        }
        if (layer == this.numLayer - 1) {
            for (int tmp = 1; tmp <= (H - K) / 2; tmp++) {
                int minTreehash = getMinTreehashIndex(layer);
                if (minTreehash >= 0) {
                    try {
                        byte[] seed = new byte[this.mdLength];
                        System.arraycopy(this.currentTreehash[layer][minTreehash].getSeedActive(), 0, seed, 0, this.mdLength);
                        byte[] seed2 = this.gmssRandom.nextSeed(seed);
                        WinternitzOTSignature ots2 = new WinternitzOTSignature(seed2, this.digestProvider.get(), this.otsIndex[layer]);
                        byte[] leaf = ots2.getPublicKey();
                        this.currentTreehash[layer][minTreehash].update(this.gmssRandom, leaf);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
            return;
        }
        this.minTreehash[layer] = getMinTreehashIndex(layer);
    }

    private int heightOfPhi(int Phi) {
        if (Phi == 0) {
            return -1;
        }
        int Tau = 0;
        int modul = 1;
        while (Phi % modul == 0) {
            modul *= 2;
            Tau++;
        }
        return Tau - 1;
    }

    private void updateNextNextAuthRoot(int layer) {
        byte[] bArr = new byte[this.mdLength];
        byte[] OTSseed = this.gmssRandom.nextSeed(this.nextNextSeeds[layer - 1]);
        if (layer == this.numLayer - 1) {
            WinternitzOTSignature ots = new WinternitzOTSignature(OTSseed, this.digestProvider.get(), this.otsIndex[layer]);
            this.nextNextRoot[layer - 1].update(this.nextNextSeeds[layer - 1], ots.getPublicKey());
        } else {
            this.nextNextRoot[layer - 1].update(this.nextNextSeeds[layer - 1], this.nextNextLeaf[layer - 1].getLeaf());
            this.nextNextLeaf[layer - 1].initLeafCalc(this.nextNextSeeds[layer - 1]);
        }
    }

    public int[] getIndex() {
        return this.index;
    }

    public int getIndex(int i) {
        return this.index[i];
    }

    public byte[][] getCurrentSeeds() {
        return Arrays.clone(this.currentSeeds);
    }

    public byte[][][] getCurrentAuthPaths() {
        return Arrays.clone(this.currentAuthPaths);
    }

    public byte[] getSubtreeRootSig(int i) {
        return this.currentRootSig[i];
    }

    public GMSSDigestProvider getName() {
        return this.digestProvider;
    }

    public int getNumLeafs(int i) {
        return this.numLeafs[i];
    }
}
