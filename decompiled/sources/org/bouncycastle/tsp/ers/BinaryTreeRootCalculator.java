package org.bouncycastle.tsp.ers;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.tsp.PartialHashtree;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/tsp/ers/BinaryTreeRootCalculator.class */
public class BinaryTreeRootCalculator implements ERSRootNodeCalculator {
    private List<List<byte[]>> tree;

    @Override // org.bouncycastle.tsp.ers.ERSRootNodeCalculator
    public byte[] computeRootHash(DigestCalculator digestCalculator, PartialHashtree[] partialHashtreeArr) {
        SortedHashList sortedHashList = new SortedHashList();
        for (PartialHashtree partialHashtree : partialHashtreeArr) {
            sortedHashList.add(ERSUtil.computeNodeHash(digestCalculator, partialHashtree));
        }
        List list = sortedHashList.toList();
        this.tree = new ArrayList();
        this.tree.add((List<byte[]>) list);
        if (list.size() > 1) {
            do {
                ArrayList arrayList = new ArrayList((list.size() / 2) + 1);
                for (int i = 0; i <= list.size() - 2; i += 2) {
                    arrayList.add(ERSUtil.calculateBranchHash(digestCalculator, (byte[]) list.get(i), (byte[]) list.get(i + 1)));
                }
                if (list.size() % 2 == 1) {
                    arrayList.add(list.get(list.size() - 1));
                }
                this.tree.add(arrayList);
                list = arrayList;
            } while (list.size() > 1);
        }
        return (byte[]) list.get(0);
    }

    @Override // org.bouncycastle.tsp.ers.ERSRootNodeCalculator
    public PartialHashtree[] computePathToRoot(DigestCalculator digestCalculator, PartialHashtree partialHashtree, int i) {
        ArrayList arrayList = new ArrayList();
        byte[] bArrComputeNodeHash = ERSUtil.computeNodeHash(digestCalculator, partialHashtree);
        arrayList.add(partialHashtree);
        int i2 = 0;
        while (i2 < this.tree.size() - 1) {
            if (i == this.tree.get(i2).size() - 1) {
                while (true) {
                    List<byte[]> list = this.tree.get(i2 + 1);
                    if (!Arrays.areEqual(bArrComputeNodeHash, list.get(list.size() - 1))) {
                        break;
                    }
                    i2++;
                    i = this.tree.get(i2).size() - 1;
                }
            }
            byte[] bArr = (i & 1) == 0 ? this.tree.get(i2).get(i + 1) : this.tree.get(i2).get(i - 1);
            arrayList.add(new PartialHashtree(bArr));
            bArrComputeNodeHash = ERSUtil.calculateBranchHash(digestCalculator, bArrComputeNodeHash, bArr);
            i /= 2;
            i2++;
        }
        return (PartialHashtree[]) arrayList.toArray(new PartialHashtree[0]);
    }

    @Override // org.bouncycastle.tsp.ers.ERSRootNodeCalculator
    public byte[] recoverRootHash(DigestCalculator digestCalculator, PartialHashtree[] partialHashtreeArr) {
        byte[] bArrComputeNodeHash = ERSUtil.computeNodeHash(digestCalculator, partialHashtreeArr[0]);
        for (int i = 1; i < partialHashtreeArr.length; i++) {
            bArrComputeNodeHash = ERSUtil.calculateBranchHash(digestCalculator, bArrComputeNodeHash, ERSUtil.computeNodeHash(digestCalculator, partialHashtreeArr[i]));
        }
        return bArrComputeNodeHash;
    }
}
