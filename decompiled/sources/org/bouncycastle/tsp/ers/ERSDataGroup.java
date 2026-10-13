package org.bouncycastle.tsp.ers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.operator.DigestCalculator;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/tsp/ers/ERSDataGroup.class */
public class ERSDataGroup extends ERSCachingData {
    protected List<ERSData> dataObjects;

    public ERSDataGroup(ERSData... eRSDataArr) {
        this.dataObjects = new ArrayList(eRSDataArr.length);
        this.dataObjects.addAll(Arrays.asList(eRSDataArr));
    }

    public ERSDataGroup(List<ERSData> list) {
        this.dataObjects = new ArrayList(list.size());
        this.dataObjects.addAll(list);
    }

    public ERSDataGroup(ERSData eRSData) {
        this.dataObjects = Collections.singletonList(eRSData);
    }

    public List<byte[]> getHashes(DigestCalculator digestCalculator, byte[] bArr) {
        return ERSUtil.buildHashList(digestCalculator, this.dataObjects, bArr);
    }

    @Override // org.bouncycastle.tsp.ers.ERSCachingData, org.bouncycastle.tsp.ers.ERSData
    public byte[] getHash(DigestCalculator digestCalculator, byte[] bArr) {
        List<byte[]> hashes = getHashes(digestCalculator, bArr);
        return hashes.size() > 1 ? ERSUtil.calculateDigest(digestCalculator, hashes.iterator()) : hashes.get(0);
    }

    @Override // org.bouncycastle.tsp.ers.ERSCachingData
    protected byte[] calculateHash(DigestCalculator digestCalculator, byte[] bArr) {
        List<byte[]> hashes = getHashes(digestCalculator, bArr);
        if (hashes.size() <= 1) {
            return hashes.get(0);
        }
        ArrayList arrayList = new ArrayList(hashes.size());
        for (int i = 0; i != arrayList.size(); i++) {
            arrayList.add(hashes.get(i));
        }
        return ERSUtil.calculateDigest(digestCalculator, (Iterator<byte[]>) arrayList.iterator());
    }

    public int size() {
        return this.dataObjects.size();
    }
}
