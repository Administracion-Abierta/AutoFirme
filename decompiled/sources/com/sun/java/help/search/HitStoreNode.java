package com.sun.java.help.search;

import java.util.Vector;

/* JADX INFO: compiled from: HitStore.java */
/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HitStoreNode.class */
class HitStoreNode {
    private int _size;
    private QueryHit[] _array;
    private int _free = 0;
    private int _hitCount = 0;
    private double _divider = 0.0d;
    private double _min = 1.0E7d;
    private double _max = 0.0d;
    private HitStoreNode[] _children = new HitStoreNode[2];
    private int _index = 0;

    public HitStoreNode(int i) {
        this._size = i;
        this._array = new QueryHit[i];
    }

    public QueryHit getNextHit() {
        if (this._index >= this._free) {
            return null;
        }
        QueryHit[] queryHitArr = this._array;
        int i = this._index;
        this._index = i + 1;
        return queryHitArr[i];
    }

    private void fastAdd(QueryHit queryHit) {
        this._hitCount++;
        this._divider += queryHit.getScore();
        if (queryHit.getScore() > this._max) {
            this._max = queryHit.getScore();
        }
        if (queryHit.getScore() < this._min) {
            this._min = queryHit.getScore();
        }
        QueryHit[] queryHitArr = this._array;
        int i = this._free;
        this._free = i + 1;
        queryHitArr[i] = queryHit;
    }

    public boolean add(QueryHit queryHit) {
        if (this._array != null) {
            if (this._free < this._size) {
                fastAdd(queryHit);
                return false;
            }
            if (this._min != this._max) {
                split();
                this._hitCount++;
                this._children[queryHit.getScore() > this._divider ? (char) 1 : (char) 0].fastAdd(queryHit);
                return true;
            }
            int i = this._size * 2;
            this._size = i;
            QueryHit[] queryHitArr = new QueryHit[i];
            System.arraycopy(this._array, 0, queryHitArr, 0, this._free);
            this._array = queryHitArr;
            fastAdd(queryHit);
            return true;
        }
        this._hitCount++;
        return this._children[queryHit.getScore() > this._divider ? (char) 1 : (char) 0].add(queryHit);
    }

    private void split() {
        this._children[0] = new HitStoreNode(this._size);
        this._children[1] = new HitStoreNode(this._size);
        this._divider /= (double) this._hitCount;
        for (int i = 0; i < this._free; i++) {
            this._children[this._array[i].getScore() > this._divider ? (char) 1 : (char) 0].fastAdd(this._array[i]);
        }
        this._array = null;
    }

    public int getCount() {
        return this._hitCount;
    }

    public double getDivider() {
        return this._divider;
    }

    public HitStoreNode getLChild() {
        return this._children[0];
    }

    public HitStoreNode getRChild() {
        return this._children[1];
    }

    public void setLChild(HitStoreNode hitStoreNode) {
        this._children[0] = hitStoreNode;
    }

    public void setRChild(HitStoreNode hitStoreNode) {
        this._children[1] = hitStoreNode;
    }

    public void decrementCount(int i) {
        this._hitCount -= i;
    }

    public boolean isLeaf() {
        return this._array != null;
    }

    public void sort() {
        quicksort(0, this._free - 1);
    }

    public void gatherLeaves(Vector vector) {
        if (isLeaf()) {
            vector.addElement(this);
        } else {
            getLChild().gatherLeaves(vector);
            getRChild().gatherLeaves(vector);
        }
    }

    private int partition(int i, int i2) {
        QueryHit queryHit = this._array[i];
        int i3 = i - 1;
        int i4 = i2 + 1;
        while (true) {
            i4--;
            if (!queryHit.betterThan(this._array[i4])) {
                do {
                    i3++;
                } while (this._array[i3].betterThan(queryHit));
                if (i3 < i4) {
                    QueryHit queryHit2 = this._array[i3];
                    this._array[i3] = this._array[i4];
                    this._array[i4] = queryHit2;
                } else {
                    return i4;
                }
            }
        }
    }

    private void quicksort(int i, int i2) {
        if (i < i2) {
            int iPartition = partition(i, i2);
            quicksort(i, iPartition);
            quicksort(iPartition + 1, i2);
        }
    }
}
