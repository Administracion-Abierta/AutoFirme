package org.bouncycastle.pqc.crypto.picnic;

import java.util.logging.Logger;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/picnic/Tree.class */
class Tree {
    private static final Logger LOG = Logger.getLogger(Tree.class.getName());
    private static final int MAX_SEED_SIZE_BYTES = 32;
    private int depth;
    byte[][] nodes;
    private int dataSize;
    private boolean[] haveNode;
    private boolean[] exists;
    private int numNodes;
    private int numLeaves;
    private PicnicEngine engine;

    protected byte[][] getLeaves() {
        return this.nodes;
    }

    protected int getLeavesOffset() {
        return this.numNodes - this.numLeaves;
    }

    public Tree(PicnicEngine picnicEngine, int i, int i2) {
        this.engine = picnicEngine;
        this.depth = Utils.ceil_log2(i) + 1;
        this.numNodes = ((1 << this.depth) - 1) - ((1 << (this.depth - 1)) - i);
        this.numLeaves = i;
        this.dataSize = i2;
        this.nodes = new byte[this.numNodes][i2];
        for (int i3 = 0; i3 < this.numNodes; i3++) {
            this.nodes[i3] = new byte[i2];
        }
        this.haveNode = new boolean[this.numNodes];
        this.exists = new boolean[this.numNodes];
        Arrays.fill(this.exists, this.numNodes - this.numLeaves, this.numNodes, true);
        for (int i4 = this.numNodes - this.numLeaves; i4 > 0; i4--) {
            if (exists((2 * i4) + 1) || exists((2 * i4) + 2)) {
                this.exists[i4] = true;
            }
        }
        this.exists[0] = true;
    }

    protected void buildMerkleTree(byte[][] bArr, byte[] bArr2) {
        int i = this.numNodes - this.numLeaves;
        for (int i2 = 0; i2 < this.numLeaves; i2++) {
            if (bArr[i2] != null) {
                System.arraycopy(bArr[i2], 0, this.nodes[i + i2], 0, this.dataSize);
                this.haveNode[i + i2] = true;
            }
        }
        for (int i3 = this.numNodes; i3 > 0; i3--) {
            computeParentHash(i3, bArr2);
        }
    }

    protected int verifyMerkleTree(byte[][] bArr, byte[] bArr2) {
        int i = this.numNodes - this.numLeaves;
        for (int i2 = 0; i2 < this.numLeaves; i2++) {
            if (bArr[i2] != null) {
                if (this.haveNode[i + i2]) {
                    return -1;
                }
                if (bArr[i2] != null) {
                    System.arraycopy(bArr[i2], 0, this.nodes[i + i2], 0, this.dataSize);
                    this.haveNode[i + i2] = true;
                }
            }
        }
        for (int i3 = this.numNodes; i3 > 0; i3--) {
            computeParentHash(i3, bArr2);
        }
        return !this.haveNode[0] ? -1 : 0;
    }

    protected int reconstructSeeds(int[] iArr, int i, byte[] bArr, int i2, byte[] bArr2, int i3) {
        int i4 = i2;
        int[] iArr2 = {0};
        int[] revealedNodes = getRevealedNodes(iArr, i, iArr2);
        for (int i5 = 0; i5 < iArr2[0]; i5++) {
            i4 -= this.engine.seedSizeBytes;
            if (i4 < 0) {
                return -1;
            }
            System.arraycopy(bArr, i5 * this.engine.seedSizeBytes, this.nodes[revealedNodes[i5]], 0, this.engine.seedSizeBytes);
            this.haveNode[revealedNodes[i5]] = true;
        }
        expandSeeds(bArr2, i3);
        return 0;
    }

    protected byte[] openMerkleTree(int[] iArr, int i, int[] iArr2) {
        int[] iArr3 = new int[1];
        int[] revealedMerkleNodes = getRevealedMerkleNodes(iArr, i, iArr3);
        iArr2[0] = iArr3[0] * this.dataSize;
        byte[] bArr = new byte[iArr2[0]];
        for (int i2 = 0; i2 < iArr3[0]; i2++) {
            System.arraycopy(this.nodes[revealedMerkleNodes[i2]], 0, bArr, i2 * this.dataSize, this.dataSize);
        }
        return bArr;
    }

    private int[] getRevealedNodes(int[] iArr, int i, int[] iArr2) {
        int i2 = this.depth - 1;
        int[][] iArr3 = new int[i2][i];
        for (int i3 = 0; i3 < i; i3++) {
            int i4 = 0;
            int i5 = iArr[i3] + (this.numNodes - this.numLeaves);
            iArr3[0][i3] = i5;
            while (true) {
                i4++;
                int parent = getParent(i5);
                i5 = parent;
                if (parent != 0) {
                    iArr3[i4][i3] = i5;
                }
            }
        }
        int[] iArr4 = new int[this.numLeaves];
        int i6 = 0;
        for (int i7 = 0; i7 < i2; i7++) {
            for (int i8 = 0; i8 < i; i8++) {
                if (hasSibling(iArr3[i7][i8])) {
                    int sibling = getSibling(iArr3[i7][i8]);
                    if (!contains(iArr3[i7], i, sibling)) {
                        while (!hasRightChild(sibling) && !isLeafNode(sibling)) {
                            sibling = (2 * sibling) + 1;
                        }
                        if (!contains(iArr4, i6, sibling)) {
                            iArr4[i6] = sibling;
                            i6++;
                        }
                    }
                }
            }
        }
        iArr2[0] = i6;
        return iArr4;
    }

    private int getSibling(int i) {
        if (!isLeftChild(i)) {
            return i - 1;
        }
        if (i + 1 < this.numNodes) {
            return i + 1;
        }
        LOG.fine("getSibling: request for node with not sibling");
        return 0;
    }

    private boolean isLeafNode(int i) {
        return (2 * i) + 1 >= this.numNodes;
    }

    private boolean hasSibling(int i) {
        if (exists(i)) {
            return !isLeftChild(i) || exists(i + 1);
        }
        return false;
    }

    protected int revealSeedsSize(int[] iArr, int i) {
        int[] iArr2 = {0};
        getRevealedNodes(iArr, i, iArr2);
        return iArr2[0] * this.engine.seedSizeBytes;
    }

    protected int revealSeeds(int[] iArr, int i, byte[] bArr, int i2) {
        int[] iArr2 = {0};
        int i3 = i2;
        int[] revealedNodes = getRevealedNodes(iArr, i, iArr2);
        for (int i4 = 0; i4 < iArr2[0]; i4++) {
            i3 -= this.engine.seedSizeBytes;
            if (i3 < 0) {
                LOG.fine("Insufficient sized buffer provided to revealSeeds");
                return 0;
            }
            System.arraycopy(this.nodes[revealedNodes[i4]], 0, bArr, i4 * this.engine.seedSizeBytes, this.engine.seedSizeBytes);
        }
        return bArr.length - i3;
    }

    protected int openMerkleTreeSize(int[] iArr, int i) {
        int[] iArr2 = new int[1];
        getRevealedMerkleNodes(iArr, i, iArr2);
        return iArr2[0] * this.engine.digestSizeBytes;
    }

    private int[] getRevealedMerkleNodes(int[] iArr, int i, int[] iArr2) {
        int parent;
        int i2 = this.numNodes - this.numLeaves;
        boolean[] zArr = new boolean[this.numNodes];
        for (int i3 = 0; i3 < i; i3++) {
            zArr[i2 + iArr[i3]] = true;
        }
        for (int parent2 = getParent(this.numNodes - 1); parent2 > 0; parent2--) {
            if (exists(parent2)) {
                if (exists((2 * parent2) + 2)) {
                    if (zArr[(2 * parent2) + 1] && zArr[(2 * parent2) + 2]) {
                        zArr[parent2] = true;
                    }
                } else if (zArr[(2 * parent2) + 1]) {
                    zArr[parent2] = true;
                }
            }
        }
        int[] iArr3 = new int[this.numLeaves];
        int i4 = 0;
        for (int i5 = 0; i5 < i; i5++) {
            int i6 = iArr[i5] + i2;
            do {
                if (!zArr[getParent(i6)]) {
                    if (!contains(iArr3, i4, i6)) {
                        iArr3[i4] = i6;
                        i4++;
                        break;
                    }
                    break;
                }
                parent = getParent(i6);
                i6 = parent;
            } while (parent != 0);
        }
        iArr2[0] = i4;
        return iArr3;
    }

    private boolean contains(int[] iArr, int i, int i2) {
        for (int i3 = 0; i3 < i; i3++) {
            if (iArr[i3] == i2) {
                return true;
            }
        }
        return false;
    }

    private void computeParentHash(int i, byte[] bArr) {
        if (exists(i)) {
            int parent = getParent(i);
            if (!this.haveNode[parent] && this.haveNode[(2 * parent) + 1]) {
                if (!exists((2 * parent) + 2) || this.haveNode[(2 * parent) + 2]) {
                    this.engine.digest.update((byte) 3);
                    this.engine.digest.update(this.nodes[(2 * parent) + 1], 0, this.engine.digestSizeBytes);
                    if (hasRightChild(parent)) {
                        this.engine.digest.update(this.nodes[(2 * parent) + 2], 0, this.engine.digestSizeBytes);
                    }
                    this.engine.digest.update(bArr, 0, 32);
                    this.engine.digest.update(Pack.intToLittleEndian(parent), 0, 2);
                    this.engine.digest.doFinal(this.nodes[parent], 0, this.engine.digestSizeBytes);
                    this.haveNode[parent] = true;
                }
            }
        }
    }

    protected byte[] getLeaf(int i) {
        return this.nodes[(this.numNodes - this.numLeaves) + i];
    }

    protected int addMerkleNodes(int[] iArr, int i, byte[] bArr, int i2) {
        int i3 = i2;
        int[] iArr2 = {0};
        int[] revealedMerkleNodes = getRevealedMerkleNodes(iArr, i, iArr2);
        for (int i4 = 0; i4 < iArr2[0]; i4++) {
            i3 -= this.dataSize;
            if (i3 < 0) {
                return -1;
            }
            System.arraycopy(bArr, i4 * this.dataSize, this.nodes[revealedMerkleNodes[i4]], 0, this.dataSize);
            this.haveNode[revealedMerkleNodes[i4]] = true;
        }
        return i3 != 0 ? -1 : 0;
    }

    protected void generateSeeds(byte[] bArr, byte[] bArr2, int i) {
        this.nodes[0] = bArr;
        this.haveNode[0] = true;
        expandSeeds(bArr2, i);
    }

    private void expandSeeds(byte[] bArr, int i) {
        byte[] bArr2 = new byte[64];
        int parent = getParent(this.numNodes - 1);
        for (int i2 = 0; i2 <= parent; i2++) {
            if (this.haveNode[i2]) {
                hashSeed(bArr2, this.nodes[i2], bArr, (byte) 1, i, i2);
                if (!this.haveNode[(2 * i2) + 1]) {
                    System.arraycopy(bArr2, 0, this.nodes[(2 * i2) + 1], 0, this.engine.seedSizeBytes);
                    this.haveNode[(2 * i2) + 1] = true;
                }
                if (exists((2 * i2) + 2) && !this.haveNode[(2 * i2) + 2]) {
                    System.arraycopy(bArr2, this.engine.seedSizeBytes, this.nodes[(2 * i2) + 2], 0, this.engine.seedSizeBytes);
                    this.haveNode[(2 * i2) + 2] = true;
                }
            }
        }
    }

    private void hashSeed(byte[] bArr, byte[] bArr2, byte[] bArr3, byte b, int i, int i2) {
        this.engine.digest.update(b);
        this.engine.digest.update(bArr2, 0, this.engine.seedSizeBytes);
        this.engine.digest.update(bArr3, 0, 32);
        this.engine.digest.update(Pack.shortToLittleEndian((short) (i & 65535)), 0, 2);
        this.engine.digest.update(Pack.shortToLittleEndian((short) (i2 & 65535)), 0, 2);
        this.engine.digest.doFinal(bArr, 0, 2 * this.engine.seedSizeBytes);
    }

    private boolean isLeftChild(int i) {
        return i % 2 == 1;
    }

    private boolean hasRightChild(int i) {
        return (2 * i) + 2 < this.numNodes && exists(i);
    }

    boolean hasLeftChild(Tree tree, int i) {
        return (2 * i) + 1 < this.numNodes;
    }

    private int getParent(int i) {
        return isLeftChild(i) ? (i - 1) / 2 : (i - 2) / 2;
    }

    private boolean exists(int i) {
        if (i >= this.numNodes) {
            return false;
        }
        return this.exists[i];
    }
}
