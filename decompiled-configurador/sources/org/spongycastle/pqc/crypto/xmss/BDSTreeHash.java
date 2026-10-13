package org.spongycastle.pqc.crypto.xmss;

import java.io.Serializable;
import java.util.Stack;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/xmss/BDSTreeHash.class */
class BDSTreeHash implements Serializable {
    private static final long serialVersionUID = 1;
    private XMSSNode tailNode;
    private final int initialHeight;
    private int height;
    private int nextIndex;
    private boolean initialized = false;
    private boolean finished = false;

    BDSTreeHash(int initialHeight) {
        this.initialHeight = initialHeight;
    }

    void initialize(int nextIndex) {
        this.tailNode = null;
        this.height = this.initialHeight;
        this.nextIndex = nextIndex;
        this.initialized = true;
        this.finished = false;
    }

    void update(Stack<XMSSNode> stack, WOTSPlus wotsPlus, byte[] publicSeed, byte[] secretSeed, OTSHashAddress otsHashAddress) {
        if (otsHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        if (this.finished || !this.initialized) {
            throw new IllegalStateException("finished or not initialized");
        }
        OTSHashAddress otsHashAddress2 = (OTSHashAddress) new OTSHashAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).withOTSAddress(this.nextIndex).withChainAddress(otsHashAddress.getChainAddress()).withHashAddress(otsHashAddress.getHashAddress()).withKeyAndMask(otsHashAddress.getKeyAndMask()).build();
        LTreeAddress lTreeAddress = (LTreeAddress) new LTreeAddress.Builder().withLayerAddress(otsHashAddress2.getLayerAddress()).withTreeAddress(otsHashAddress2.getTreeAddress()).withLTreeAddress(this.nextIndex).build();
        HashTreeAddress hashTreeAddress = (HashTreeAddress) new HashTreeAddress.Builder().withLayerAddress(otsHashAddress2.getLayerAddress()).withTreeAddress(otsHashAddress2.getTreeAddress()).withTreeIndex(this.nextIndex).build();
        wotsPlus.importKeys(wotsPlus.getWOTSPlusSecretKey(secretSeed, otsHashAddress2), publicSeed);
        WOTSPlusPublicKeyParameters wotsPlusPublicKey = wotsPlus.getPublicKey(otsHashAddress2);
        XMSSNode node = XMSSNodeUtil.lTree(wotsPlus, wotsPlusPublicKey, lTreeAddress);
        while (!stack.isEmpty() && stack.peek().getHeight() == node.getHeight() && stack.peek().getHeight() != this.initialHeight) {
            HashTreeAddress hashTreeAddress2 = (HashTreeAddress) new HashTreeAddress.Builder().withLayerAddress(hashTreeAddress.getLayerAddress()).withTreeAddress(hashTreeAddress.getTreeAddress()).withTreeHeight(hashTreeAddress.getTreeHeight()).withTreeIndex((hashTreeAddress.getTreeIndex() - 1) / 2).withKeyAndMask(hashTreeAddress.getKeyAndMask()).build();
            XMSSNode node2 = XMSSNodeUtil.randomizeHash(wotsPlus, stack.pop(), node, hashTreeAddress2);
            node = new XMSSNode(node2.getHeight() + 1, node2.getValue());
            hashTreeAddress = (HashTreeAddress) new HashTreeAddress.Builder().withLayerAddress(hashTreeAddress2.getLayerAddress()).withTreeAddress(hashTreeAddress2.getTreeAddress()).withTreeHeight(hashTreeAddress2.getTreeHeight() + 1).withTreeIndex(hashTreeAddress2.getTreeIndex()).withKeyAndMask(hashTreeAddress2.getKeyAndMask()).build();
        }
        if (this.tailNode == null) {
            this.tailNode = node;
        } else if (this.tailNode.getHeight() == node.getHeight()) {
            HashTreeAddress hashTreeAddress3 = (HashTreeAddress) new HashTreeAddress.Builder().withLayerAddress(hashTreeAddress.getLayerAddress()).withTreeAddress(hashTreeAddress.getTreeAddress()).withTreeHeight(hashTreeAddress.getTreeHeight()).withTreeIndex((hashTreeAddress.getTreeIndex() - 1) / 2).withKeyAndMask(hashTreeAddress.getKeyAndMask()).build();
            node = new XMSSNode(this.tailNode.getHeight() + 1, XMSSNodeUtil.randomizeHash(wotsPlus, this.tailNode, node, hashTreeAddress3).getValue());
            this.tailNode = node;
        } else {
            stack.push(node);
        }
        if (this.tailNode.getHeight() == this.initialHeight) {
            this.finished = true;
        } else {
            this.height = node.getHeight();
            this.nextIndex++;
        }
    }

    int getHeight() {
        if (!this.initialized || this.finished) {
            return Integer.MAX_VALUE;
        }
        return this.height;
    }

    int getIndexLeaf() {
        return this.nextIndex;
    }

    void setNode(XMSSNode node) {
        this.tailNode = node;
        this.height = node.getHeight();
        if (this.height == this.initialHeight) {
            this.finished = true;
        }
    }

    boolean isFinished() {
        return this.finished;
    }

    boolean isInitialized() {
        return this.initialized;
    }

    public XMSSNode getTailNode() {
        return this.tailNode.m989clone();
    }
}
