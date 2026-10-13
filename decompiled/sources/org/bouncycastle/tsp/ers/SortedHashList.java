package org.bouncycastle.tsp.ers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/tsp/ers/SortedHashList.class */
public class SortedHashList {
    private static final Comparator<byte[]> hashComp = new ByteArrayComparator();
    private final LinkedList<byte[]> baseList = new LinkedList<>();

    public byte[] getFirst() {
        return this.baseList.getFirst();
    }

    public void add(byte[] bArr) {
        if (this.baseList.size() == 0) {
            this.baseList.addFirst(bArr);
            return;
        }
        if (hashComp.compare(bArr, this.baseList.get(0)) < 0) {
            this.baseList.addFirst(bArr);
            return;
        }
        int i = 1;
        while (i < this.baseList.size() && hashComp.compare(this.baseList.get(i), bArr) <= 0) {
            i++;
        }
        if (i == this.baseList.size()) {
            this.baseList.add(bArr);
        } else {
            this.baseList.add(i, bArr);
        }
    }

    public int size() {
        return this.baseList.size();
    }

    public List<byte[]> toList() {
        return new ArrayList(this.baseList);
    }
}
