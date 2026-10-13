package com.sun.java.help.search;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/ConceptData.class */
class ConceptData {
    private final byte _query;
    private final byte _nColumns;
    private final byte _role;
    private final int _concept;
    private int _conceptLength;
    private final int _proximity;
    private final double _penalty;
    private ConceptData _next = null;
    private static final int ProxPerTerm = 100;

    public ConceptData(int i, int i2, double d, int i3, int i4) {
        this._query = (byte) i3;
        this._nColumns = (byte) i4;
        this._concept = i;
        this._proximity = i4 * 100;
        this._role = (byte) i2;
        this._penalty = d;
    }

    public int getConcept() {
        return this._concept;
    }

    public double getPenalty() {
        return this._penalty;
    }

    public int getConceptLength() {
        return this._conceptLength;
    }

    public byte getRole() {
        return this._role;
    }

    public byte getQuery() {
        return this._query;
    }

    public byte getNColumns() {
        return this._nColumns;
    }

    public double getScore() {
        return this._penalty;
    }

    public ConceptData getNext() {
        return this._next;
    }

    public int getQueryMask() {
        return (this._next != null ? this._next.getQueryMask() : 0) | (1 << this._query);
    }

    public void setConceptLength(int i) {
        this._conceptLength = i;
        if (this._next != null) {
            this._next.setConceptLength(i);
        }
    }

    public void setNext(ConceptData conceptData) {
        this._next = conceptData;
    }

    boolean cEquals(ConceptData conceptData) {
        return this._concept == conceptData._concept;
    }

    boolean crEquals(ConceptData conceptData) {
        return this._concept == conceptData._concept && this._role == conceptData._role;
    }

    boolean crqEquals(ConceptData conceptData) {
        return this._concept == conceptData._concept && this._role == conceptData._role && this._query == conceptData._query;
    }

    void addLast(ConceptData conceptData) {
        if (this._next != null) {
            this._next.addLast(conceptData);
        } else {
            this._next = conceptData;
        }
    }

    boolean compareWith(ConceptData conceptData) {
        return this._concept < conceptData._concept || (cEquals(conceptData) && this._role < conceptData._role) || (crEquals(conceptData) && this._penalty < conceptData._penalty);
    }

    public void runBy(Query[] queryArr) {
        ConceptData conceptData;
        ConceptData conceptData2 = this;
        do {
            queryArr[conceptData2._query].updateEstimate(conceptData2._role, conceptData2._penalty);
            conceptData = conceptData2._next;
            conceptData2 = conceptData;
        } while (conceptData != null);
    }

    public void generateFillers(RoleFiller[] roleFillerArr, int i) {
        if (roleFillerArr[this._query] != RoleFiller.STOP) {
            new RoleFiller(this._nColumns, this, this._role, i, i + this._proximity).use(roleFillerArr, this._query);
        }
        if (this._next != null) {
            this._next.generateFillers(roleFillerArr, i);
        }
    }
}
