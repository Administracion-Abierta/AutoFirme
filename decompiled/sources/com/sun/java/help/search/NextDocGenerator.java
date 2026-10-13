package com.sun.java.help.search;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/NextDocGenerator.class */
class NextDocGenerator {
    private int _document = 0;
    private final int _concept;
    private final int _queryMask;
    private final ConceptData _terms;
    private final NonnegativeIntegerGenerator _iterator;

    public NextDocGenerator(ConceptData conceptData, SearchEnvironment searchEnvironment) {
        this._concept = conceptData.getConcept();
        this._queryMask = conceptData.getQueryMask();
        this._terms = conceptData;
        this._iterator = searchEnvironment.getDocumentIterator(this._concept);
    }

    public int first() throws Exception {
        int iFirst = this._iterator != null ? this._iterator.first() : -1;
        this._document = iFirst;
        return iFirst;
    }

    public int next() throws Exception {
        int next = this._iterator.next();
        this._document = next;
        return next;
    }

    public int getDocument() {
        return this._document;
    }

    public int getConcept() {
        return this._concept;
    }

    public ConceptData getTerms() {
        return this._terms;
    }

    public int getQueryMask() {
        return this._queryMask;
    }

    public boolean compareWith(NextDocGenerator nextDocGenerator) {
        return this._document > nextDocGenerator._document || (this._document == nextDocGenerator._document && this._concept > nextDocGenerator._concept);
    }

    public boolean smallerThan(NextDocGenerator nextDocGenerator) {
        return this._document < nextDocGenerator._document || (this._document == nextDocGenerator._document && this._concept < nextDocGenerator._concept);
    }
}
