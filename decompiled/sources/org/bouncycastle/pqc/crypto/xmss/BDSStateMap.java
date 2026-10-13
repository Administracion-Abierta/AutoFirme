package org.bouncycastle.pqc.crypto.xmss;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.util.Integers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/xmss/BDSStateMap.class */
public class BDSStateMap implements Serializable {
    private static final long serialVersionUID = -3464451825208522308L;
    private final Map<Integer, BDS> bdsState = new TreeMap();
    private transient long maxIndex;

    BDSStateMap(long j) {
        this.maxIndex = j;
    }

    BDSStateMap(BDSStateMap bDSStateMap, long j) {
        for (Integer num : bDSStateMap.bdsState.keySet()) {
            this.bdsState.put(num, new BDS(bDSStateMap.bdsState.get(num)));
        }
        this.maxIndex = j;
    }

    BDSStateMap(XMSSMTParameters xMSSMTParameters, long j, byte[] bArr, byte[] bArr2) {
        this.maxIndex = (1 << xMSSMTParameters.getHeight()) - 1;
        long j2 = 0;
        while (true) {
            long j3 = j2;
            if (j3 >= j) {
                return;
            }
            updateState(xMSSMTParameters, j3, bArr, bArr2);
            j2 = j3 + 1;
        }
    }

    public long getMaxIndex() {
        return this.maxIndex;
    }

    void updateState(XMSSMTParameters xMSSMTParameters, long j, byte[] bArr, byte[] bArr2) {
        XMSSParameters xMSSParameters = xMSSMTParameters.getXMSSParameters();
        int height = xMSSParameters.getHeight();
        long treeIndex = XMSSUtil.getTreeIndex(j, height);
        int leafIndex = XMSSUtil.getLeafIndex(j, height);
        OTSHashAddress oTSHashAddress = (OTSHashAddress) new OTSHashAddress.Builder().withTreeAddress(treeIndex).withOTSAddress(leafIndex).build();
        if (leafIndex < (1 << height) - 1) {
            if (get(0) == null || leafIndex == 0) {
                put(0, new BDS(xMSSParameters, bArr, bArr2, oTSHashAddress));
            }
            update(0, bArr, bArr2, oTSHashAddress);
        }
        for (int i = 1; i < xMSSMTParameters.getLayers(); i++) {
            int leafIndex2 = XMSSUtil.getLeafIndex(treeIndex, height);
            treeIndex = XMSSUtil.getTreeIndex(treeIndex, height);
            OTSHashAddress oTSHashAddress2 = (OTSHashAddress) new OTSHashAddress.Builder().withLayerAddress(i).withTreeAddress(treeIndex).withOTSAddress(leafIndex2).build();
            if (this.bdsState.get(Integer.valueOf(i)) == null || XMSSUtil.isNewBDSInitNeeded(j, height, i)) {
                this.bdsState.put(Integer.valueOf(i), new BDS(xMSSParameters, bArr, bArr2, oTSHashAddress2));
            }
            if (leafIndex2 < (1 << height) - 1 && XMSSUtil.isNewAuthenticationPathNeeded(j, height, i)) {
                update(i, bArr, bArr2, oTSHashAddress2);
            }
        }
    }

    public boolean isEmpty() {
        return this.bdsState.isEmpty();
    }

    BDS get(int i) {
        return this.bdsState.get(Integers.valueOf(i));
    }

    BDS update(int i, byte[] bArr, byte[] bArr2, OTSHashAddress oTSHashAddress) {
        return this.bdsState.put(Integers.valueOf(i), this.bdsState.get(Integers.valueOf(i)).getNextState(bArr, bArr2, oTSHashAddress));
    }

    void put(int i, BDS bds) {
        this.bdsState.put(Integers.valueOf(i), bds);
    }

    public BDSStateMap withWOTSDigest(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        BDSStateMap bDSStateMap = new BDSStateMap(this.maxIndex);
        for (Integer num : this.bdsState.keySet()) {
            bDSStateMap.bdsState.put(num, this.bdsState.get(num).withWOTSDigest(aSN1ObjectIdentifier));
        }
        return bDSStateMap;
    }

    private void readObject(ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        if (objectInputStream.available() != 0) {
            this.maxIndex = objectInputStream.readLong();
        } else {
            this.maxIndex = 0L;
        }
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeLong(this.maxIndex);
    }
}
