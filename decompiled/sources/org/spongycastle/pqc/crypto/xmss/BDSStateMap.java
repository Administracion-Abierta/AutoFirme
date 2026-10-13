package org.spongycastle.pqc.crypto.xmss;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import org.spongycastle.util.Integers;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/xmss/BDSStateMap.class */
public class BDSStateMap implements Serializable {
    private final Map<Integer, BDS> bdsState = new TreeMap();

    BDSStateMap() {
    }

    BDSStateMap(XMSSMTParameters params, long globalIndex, byte[] publicSeed, byte[] secretKeySeed) {
        long j = 0;
        while (true) {
            long index = j;
            if (index < globalIndex) {
                updateState(params, index, publicSeed, secretKeySeed);
                j = index + 1;
            } else {
                return;
            }
        }
    }

    BDSStateMap(BDSStateMap stateMap, XMSSMTParameters params, long globalIndex, byte[] publicSeed, byte[] secretKeySeed) {
        for (Integer key : stateMap.bdsState.keySet()) {
            this.bdsState.put(key, stateMap.bdsState.get(key));
        }
        updateState(params, globalIndex, publicSeed, secretKeySeed);
    }

    private void updateState(XMSSMTParameters params, long globalIndex, byte[] publicSeed, byte[] secretKeySeed) {
        XMSSParameters xmssParams = params.getXMSSParameters();
        int xmssHeight = xmssParams.getHeight();
        long indexTree = XMSSUtil.getTreeIndex(globalIndex, xmssHeight);
        int indexLeaf = XMSSUtil.getLeafIndex(globalIndex, xmssHeight);
        OTSHashAddress otsHashAddress = (OTSHashAddress) new OTSHashAddress.Builder().withTreeAddress(indexTree).withOTSAddress(indexLeaf).build();
        if (indexLeaf < (1 << xmssHeight) - 1) {
            if (get(0) == null || indexLeaf == 0) {
                put(0, new BDS(xmssParams, publicSeed, secretKeySeed, otsHashAddress));
            }
            update(0, publicSeed, secretKeySeed, otsHashAddress);
        }
        for (int layer = 1; layer < params.getLayers(); layer++) {
            int indexLeaf2 = XMSSUtil.getLeafIndex(indexTree, xmssHeight);
            indexTree = XMSSUtil.getTreeIndex(indexTree, xmssHeight);
            OTSHashAddress otsHashAddress2 = (OTSHashAddress) new OTSHashAddress.Builder().withLayerAddress(layer).withTreeAddress(indexTree).withOTSAddress(indexLeaf2).build();
            if (indexLeaf2 < (1 << xmssHeight) - 1 && XMSSUtil.isNewAuthenticationPathNeeded(globalIndex, xmssHeight, layer)) {
                if (get(layer) == null) {
                    put(layer, new BDS(params.getXMSSParameters(), publicSeed, secretKeySeed, otsHashAddress2));
                }
                update(layer, publicSeed, secretKeySeed, otsHashAddress2);
            }
        }
    }

    void setXMSS(XMSSParameters xmss) {
        for (Integer key : this.bdsState.keySet()) {
            BDS bds = this.bdsState.get(key);
            bds.setXMSS(xmss);
            bds.validate();
        }
    }

    public boolean isEmpty() {
        return this.bdsState.isEmpty();
    }

    public BDS get(int index) {
        return this.bdsState.get(Integers.valueOf(index));
    }

    public BDS update(int index, byte[] publicSeed, byte[] secretKeySeed, OTSHashAddress otsHashAddress) {
        return this.bdsState.put(Integers.valueOf(index), this.bdsState.get(Integers.valueOf(index)).getNextState(publicSeed, secretKeySeed, otsHashAddress));
    }

    public void put(int index, BDS bds) {
        this.bdsState.put(Integers.valueOf(index), bds);
    }
}
