package com.sun.java.help.search;

import java.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HitStore.class */
class HitStore {
    private static final int ArraySize = 128;
    private static final int DefaultLimit = 300;
    private HitStoreNode _root;
    private int _limit;
    private double _standard;
    private Vector _leaves;
    private HitStoreNode _current;

    public HitStore(double d) {
        this(d, 300);
    }

    public HitStore(double d, int i) {
        this._root = new HitStoreNode(128);
        this._leaves = null;
        this._current = null;
        this._limit = i;
        this._standard = d;
    }

    public void addQueryHit(QueryHit queryHit) {
        if (this._root.add(queryHit)) {
            adapt();
        }
    }

    private HitStoreNode getNextNode() {
        if (this._leaves.size() > 0) {
            HitStoreNode hitStoreNode = (HitStoreNode) this._leaves.firstElement();
            this._leaves.removeElementAt(0);
            hitStoreNode.sort();
            return hitStoreNode;
        }
        return null;
    }

    public QueryHit firstBestQueryHit() {
        this._leaves = new Vector();
        this._root.gatherLeaves(this._leaves);
        this._current = getNextNode();
        return this._current.getNextHit();
    }

    public QueryHit nextBestQueryHit() {
        QueryHit nextHit = this._current.getNextHit();
        if (nextHit != null) {
            return nextHit;
        }
        HitStoreNode nextNode = getNextNode();
        this._current = nextNode;
        if (nextNode != null) {
            return this._current.getNextHit();
        }
        return null;
    }

    double getCurrentStandard() {
        return this._standard;
    }

    private void adapt() {
        HitStoreNode hitStoreNode;
        HitStoreNode hitStoreNode2;
        if (this._root.getCount() > this._limit && !this._root.isLeaf()) {
            HitStoreNode rChild = this._root;
            while (true) {
                hitStoreNode = rChild;
                if (hitStoreNode.getRChild().isLeaf()) {
                    break;
                } else {
                    rChild = hitStoreNode.getRChild();
                }
            }
            this._standard = hitStoreNode.getDivider();
            if (hitStoreNode == this._root) {
                this._root = hitStoreNode.getLChild();
            } else {
                int count = hitStoreNode.getRChild().getCount();
                HitStoreNode rChild2 = this._root;
                while (true) {
                    hitStoreNode2 = rChild2;
                    if (hitStoreNode2.getRChild() == hitStoreNode) {
                        break;
                    }
                    hitStoreNode2.decrementCount(count);
                    rChild2 = hitStoreNode2.getRChild();
                }
                hitStoreNode2.setRChild(hitStoreNode.getLChild());
            }
            hitStoreNode.setLChild(null);
        }
    }
}
