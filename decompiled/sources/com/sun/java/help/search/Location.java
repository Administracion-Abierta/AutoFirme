package com.sun.java.help.search;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/Location.class */
class Location {
    private final int _docID;
    private final int _begin;
    private final int _end;

    public Location(int i, int i2, int i3) {
        this._docID = i;
        this._begin = i2;
        this._end = i3;
    }

    public int getDocument() {
        return this._docID;
    }

    public int getBegin() {
        return this._begin;
    }

    public int getEnd() {
        return this._end;
    }
}
