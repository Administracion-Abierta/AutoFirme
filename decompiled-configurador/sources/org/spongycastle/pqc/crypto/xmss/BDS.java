package org.spongycastle.pqc.crypto.xmss;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/xmss/BDS.class */
public final class BDS implements Serializable {
    private static final long serialVersionUID = 1;
    private transient WOTSPlus wotsPlus;
    private final int treeHeight;
    private final List<BDSTreeHash> treeHashInstances;
    private int k;
    private XMSSNode root;
    private List<XMSSNode> authenticationPath;
    private Map<Integer, LinkedList<XMSSNode>> retain;
    private Stack<XMSSNode> stack;
    private Map<Integer, XMSSNode> keep;
    private int index;
    private boolean used;

    BDS(XMSSParameters params, int index) {
        this(params.getWOTSPlus(), params.getHeight(), params.getK());
        this.index = index;
        this.used = true;
    }

    BDS(XMSSParameters params, byte[] publicSeed, byte[] secretKeySeed, OTSHashAddress otsHashAddress) {
        this(params.getWOTSPlus(), params.getHeight(), params.getK());
        initialize(publicSeed, secretKeySeed, otsHashAddress);
    }

    BDS(XMSSParameters params, byte[] publicSeed, byte[] secretKeySeed, OTSHashAddress otsHashAddress, int index) {
        this(params.getWOTSPlus(), params.getHeight(), params.getK());
        initialize(publicSeed, secretKeySeed, otsHashAddress);
        while (this.index < index) {
            nextAuthenticationPath(publicSeed, secretKeySeed, otsHashAddress);
            this.used = false;
        }
    }

    private BDS(WOTSPlus wotsPlus, int treeHeight, int k) {
        this.wotsPlus = wotsPlus;
        this.treeHeight = treeHeight;
        this.k = k;
        if (k > treeHeight || k < 2 || (treeHeight - k) % 2 != 0) {
            throw new IllegalArgumentException("illegal value for BDS parameter k");
        }
        this.authenticationPath = new ArrayList();
        this.retain = new TreeMap();
        this.stack = new Stack<>();
        this.treeHashInstances = new ArrayList();
        for (int height = 0; height < treeHeight - k; height++) {
            this.treeHashInstances.add(new BDSTreeHash(height));
        }
        this.keep = new TreeMap();
        this.index = 0;
        this.used = false;
    }

    private BDS(BDS last, byte[] publicSeed, byte[] secretKeySeed, OTSHashAddress otsHashAddress) {
        this.wotsPlus = last.wotsPlus;
        this.treeHeight = last.treeHeight;
        this.k = last.k;
        this.root = last.root;
        this.authenticationPath = new ArrayList(last.authenticationPath);
        this.retain = last.retain;
        this.stack = (Stack) last.stack.clone();
        this.treeHashInstances = last.treeHashInstances;
        this.keep = new TreeMap(last.keep);
        this.index = last.index;
        nextAuthenticationPath(publicSeed, secretKeySeed, otsHashAddress);
        last.used = true;
    }

    public BDS getNextState(byte[] publicSeed, byte[] secretKeySeed, OTSHashAddress otsHashAddress) {
        return new BDS(this, publicSeed, secretKeySeed, otsHashAddress);
    }

    private void initialize(byte[] publicSeed, byte[] secretSeed, OTSHashAddress otsHashAddress) {
        if (otsHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        LTreeAddress lTreeAddress = (LTreeAddress) new LTreeAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).build();
        HashTreeAddress hashTreeAddress = (HashTreeAddress) new HashTreeAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).build();
        for (int indexLeaf = 0; indexLeaf < (1 << this.treeHeight); indexLeaf++) {
            otsHashAddress = (OTSHashAddress) new OTSHashAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).withOTSAddress(indexLeaf).withChainAddress(otsHashAddress.getChainAddress()).withHashAddress(otsHashAddress.getHashAddress()).withKeyAndMask(otsHashAddress.getKeyAndMask()).build();
            this.wotsPlus.importKeys(this.wotsPlus.getWOTSPlusSecretKey(secretSeed, otsHashAddress), publicSeed);
            WOTSPlusPublicKeyParameters wotsPlusPublicKey = this.wotsPlus.getPublicKey(otsHashAddress);
            lTreeAddress = (LTreeAddress) new LTreeAddress.Builder().withLayerAddress(lTreeAddress.getLayerAddress()).withTreeAddress(lTreeAddress.getTreeAddress()).withLTreeAddress(indexLeaf).withTreeHeight(lTreeAddress.getTreeHeight()).withTreeIndex(lTreeAddress.getTreeIndex()).withKeyAndMask(lTreeAddress.getKeyAndMask()).build();
            XMSSNode node = XMSSNodeUtil.lTree(this.wotsPlus, wotsPlusPublicKey, lTreeAddress);
            XMSSAddress xMSSAddressBuild = new HashTreeAddress.Builder().withLayerAddress(hashTreeAddress.getLayerAddress()).withTreeAddress(hashTreeAddress.getTreeAddress()).withTreeIndex(indexLeaf).withKeyAndMask(hashTreeAddress.getKeyAndMask()).build();
            while (true) {
                hashTreeAddress = (HashTreeAddress) xMSSAddressBuild;
                if (this.stack.isEmpty() || this.stack.peek().getHeight() != node.getHeight()) {
                    break;
                }
                int indexOnHeight = (int) Math.floor(indexLeaf / (1 << node.getHeight()));
                if (indexOnHeight == 1) {
                    this.authenticationPath.add(node.m989clone());
                }
                if (indexOnHeight == 3 && node.getHeight() < this.treeHeight - this.k) {
                    this.treeHashInstances.get(node.getHeight()).setNode(node.m989clone());
                }
                if (indexOnHeight >= 3 && (indexOnHeight & 1) == 1 && node.getHeight() >= this.treeHeight - this.k && node.getHeight() <= this.treeHeight - 2) {
                    if (this.retain.get(Integer.valueOf(node.getHeight())) == null) {
                        LinkedList<XMSSNode> queue = new LinkedList<>();
                        queue.add(node.m989clone());
                        this.retain.put(Integer.valueOf(node.getHeight()), queue);
                    } else {
                        this.retain.get(Integer.valueOf(node.getHeight())).add(node.m989clone());
                    }
                }
                HashTreeAddress hashTreeAddress2 = (HashTreeAddress) new HashTreeAddress.Builder().withLayerAddress(hashTreeAddress.getLayerAddress()).withTreeAddress(hashTreeAddress.getTreeAddress()).withTreeHeight(hashTreeAddress.getTreeHeight()).withTreeIndex((hashTreeAddress.getTreeIndex() - 1) / 2).withKeyAndMask(hashTreeAddress.getKeyAndMask()).build();
                XMSSNode node2 = XMSSNodeUtil.randomizeHash(this.wotsPlus, this.stack.pop(), node, hashTreeAddress2);
                node = new XMSSNode(node2.getHeight() + 1, node2.getValue());
                xMSSAddressBuild = new HashTreeAddress.Builder().withLayerAddress(hashTreeAddress2.getLayerAddress()).withTreeAddress(hashTreeAddress2.getTreeAddress()).withTreeHeight(hashTreeAddress2.getTreeHeight() + 1).withTreeIndex(hashTreeAddress2.getTreeIndex()).withKeyAndMask(hashTreeAddress2.getKeyAndMask()).build();
            }
            this.stack.push(node);
        }
        this.root = this.stack.pop();
    }

    private void nextAuthenticationPath(byte[] publicSeed, byte[] secretSeed, OTSHashAddress otsHashAddress) {
        if (otsHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        if (this.used) {
            throw new IllegalStateException("index already used");
        }
        if (this.index > (1 << this.treeHeight) - 2) {
            throw new IllegalStateException("index out of bounds");
        }
        LTreeAddress lTreeAddress = (LTreeAddress) new LTreeAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).build();
        HashTreeAddress hashTreeAddress = (HashTreeAddress) new HashTreeAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).build();
        int tau = XMSSUtil.calculateTau(this.index, this.treeHeight);
        if (((this.index >> (tau + 1)) & 1) == 0 && tau < this.treeHeight - 1) {
            this.keep.put(Integer.valueOf(tau), this.authenticationPath.get(tau).m989clone());
        }
        if (tau == 0) {
            otsHashAddress = (OTSHashAddress) new OTSHashAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).withOTSAddress(this.index).withChainAddress(otsHashAddress.getChainAddress()).withHashAddress(otsHashAddress.getHashAddress()).withKeyAndMask(otsHashAddress.getKeyAndMask()).build();
            this.wotsPlus.importKeys(this.wotsPlus.getWOTSPlusSecretKey(secretSeed, otsHashAddress), publicSeed);
            WOTSPlusPublicKeyParameters wotsPlusPublicKey = this.wotsPlus.getPublicKey(otsHashAddress);
            this.authenticationPath.set(0, XMSSNodeUtil.lTree(this.wotsPlus, wotsPlusPublicKey, (LTreeAddress) new LTreeAddress.Builder().withLayerAddress(lTreeAddress.getLayerAddress()).withTreeAddress(lTreeAddress.getTreeAddress()).withLTreeAddress(this.index).withTreeHeight(lTreeAddress.getTreeHeight()).withTreeIndex(lTreeAddress.getTreeIndex()).withKeyAndMask(lTreeAddress.getKeyAndMask()).build()));
        } else {
            XMSSNode node = XMSSNodeUtil.randomizeHash(this.wotsPlus, this.authenticationPath.get(tau - 1), this.keep.get(Integer.valueOf(tau - 1)), (HashTreeAddress) new HashTreeAddress.Builder().withLayerAddress(hashTreeAddress.getLayerAddress()).withTreeAddress(hashTreeAddress.getTreeAddress()).withTreeHeight(tau - 1).withTreeIndex(this.index >> tau).withKeyAndMask(hashTreeAddress.getKeyAndMask()).build());
            this.authenticationPath.set(tau, new XMSSNode(node.getHeight() + 1, node.getValue()));
            this.keep.remove(Integer.valueOf(tau - 1));
            for (int height = 0; height < tau; height++) {
                if (height < this.treeHeight - this.k) {
                    this.authenticationPath.set(height, this.treeHashInstances.get(height).getTailNode());
                } else {
                    this.authenticationPath.set(height, this.retain.get(Integer.valueOf(height)).removeFirst());
                }
            }
            int minHeight = Math.min(tau, this.treeHeight - this.k);
            for (int height2 = 0; height2 < minHeight; height2++) {
                int startIndex = this.index + 1 + (3 * (1 << height2));
                if (startIndex < (1 << this.treeHeight)) {
                    this.treeHashInstances.get(height2).initialize(startIndex);
                }
            }
        }
        for (int i = 0; i < ((this.treeHeight - this.k) >> 1); i++) {
            BDSTreeHash treeHash = getBDSTreeHashInstanceForUpdate();
            if (treeHash != null) {
                treeHash.update(this.stack, this.wotsPlus, publicSeed, secretSeed, otsHashAddress);
            }
        }
        this.index++;
    }

    boolean isUsed() {
        return this.used;
    }

    private BDSTreeHash getBDSTreeHashInstanceForUpdate() {
        BDSTreeHash ret = null;
        for (BDSTreeHash treeHash : this.treeHashInstances) {
            if (!treeHash.isFinished() && treeHash.isInitialized()) {
                if (ret == null) {
                    ret = treeHash;
                } else if (treeHash.getHeight() < ret.getHeight()) {
                    ret = treeHash;
                } else if (treeHash.getHeight() == ret.getHeight() && treeHash.getIndexLeaf() < ret.getIndexLeaf()) {
                    ret = treeHash;
                }
            }
        }
        return ret;
    }

    protected void validate() {
        if (this.authenticationPath == null) {
            throw new IllegalStateException("authenticationPath == null");
        }
        if (this.retain == null) {
            throw new IllegalStateException("retain == null");
        }
        if (this.stack == null) {
            throw new IllegalStateException("stack == null");
        }
        if (this.treeHashInstances == null) {
            throw new IllegalStateException("treeHashInstances == null");
        }
        if (this.keep == null) {
            throw new IllegalStateException("keep == null");
        }
        if (!XMSSUtil.isIndexValid(this.treeHeight, this.index)) {
            throw new IllegalStateException("index in BDS state out of bounds");
        }
    }

    protected int getTreeHeight() {
        return this.treeHeight;
    }

    protected XMSSNode getRoot() {
        return this.root.m989clone();
    }

    protected List<XMSSNode> getAuthenticationPath() {
        List<XMSSNode> authenticationPath = new ArrayList<>();
        for (XMSSNode node : this.authenticationPath) {
            authenticationPath.add(node.m989clone());
        }
        return authenticationPath;
    }

    protected void setXMSS(XMSSParameters xmss) {
        if (this.treeHeight != xmss.getHeight()) {
            throw new IllegalStateException("wrong height");
        }
        this.wotsPlus = xmss.getWOTSPlus();
    }

    protected int getIndex() {
        return this.index;
    }
}
