package com.sun.java.help.search;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/QueryHit.class */
class QueryHit {
    private int _doc;
    private int _begin;
    private int _end;
    private double _score;
    private final int[] _concepts;

    public QueryHit(Location location, double d, int i) {
        this._score = d;
        this._doc = location.getDocument();
        this._begin = location.getBegin();
        this._end = location.getEnd();
        this._concepts = new int[i];
    }

    public String toString() {
        return new StringBuffer().append("[doc = ").append(this._doc).append(", ").append(this._begin).append(", ").append(this._end).append(", ").append(this._score).append("]").toString();
    }

    public int getDocument() {
        return this._doc;
    }

    public int getBegin() {
        return this._begin;
    }

    public int getEnd() {
        return this._end;
    }

    public double getScore() {
        return this._score;
    }

    public int[] getArray() {
        return this._concepts;
    }

    public boolean betterThan(QueryHit queryHit) {
        if (this._score < queryHit._score) {
            return true;
        }
        if (this._score > queryHit._score) {
            return false;
        }
        if (this._begin < queryHit._begin) {
            return true;
        }
        if (this._begin > queryHit._begin) {
            return false;
        }
        if (this._end < queryHit._end) {
            return true;
        }
        return this._end > queryHit._end ? false : false;
    }
}
