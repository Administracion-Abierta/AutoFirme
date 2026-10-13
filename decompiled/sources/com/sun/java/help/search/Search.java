package com.sun.java.help.search;

import javax.help.search.SearchQuery;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/Search.class */
class Search {
    private static final int InitNConcepts = 128;
    private SearchEnvironment _env;
    private int _max;
    private int _nConcepts;
    private int _document;
    private ConceptGroupGenerator _firstGenerator = new ConceptGroupGenerator();
    private int[] _concepts = new int[16];
    private int _startingIndex = 0;
    private int _limit = 0;
    private GeneratorHeap _genHeap = new GeneratorHeap();
    private byte[] _data = null;
    private int _base = 0;
    private NextDocGeneratorHeap _nextDocGenHeap = new NextDocGeneratorHeap();
    private IntegerArray _kTable = new IntegerArray();
    private IntegerArray _offsets = new IntegerArray();
    private IntegerArray _maxConcepts = new IntegerArray();
    private IntegerArray _docConcepts = new IntegerArray();
    private IntegerArray _queryMasks = new IntegerArray();
    private int _maxHitsToShow = 100;
    private int _nQueries = 1;
    private Query[] _query = new Query[this._nQueries];
    private int _size2 = 128;
    private int _free2 = 0;
    private ConceptData[] _conceptData = new ConceptData[this._size2];

    public Search(SearchEnvironment searchEnvironment, int i) {
        this._env = searchEnvironment;
        this._query[0] = new Query(searchEnvironment, i, null);
    }

    public void addTerm(int i, int i2, double d, int i3) {
        if (this._env.occursInText(i2)) {
            if (this._free2 == this._size2) {
                int i4 = this._size2 * 2;
                this._size2 = i4;
                ConceptData[] conceptDataArr = new ConceptData[i4];
                System.arraycopy(this._conceptData, 0, conceptDataArr, 0, this._free2);
                this._conceptData = conceptDataArr;
            }
            ConceptData[] conceptDataArr2 = this._conceptData;
            int i5 = this._free2;
            this._free2 = i5 + 1;
            conceptDataArr2[i5] = new ConceptData(i2, i, d, i3, this._query[i3].getNColumns());
        }
    }

    public void startSearch(SearchQuery searchQuery) {
        quicksort(0, this._free2 - 1);
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= this._free2 - 1) {
                break;
            }
            int i3 = i2 + 1;
            while (i3 < this._free2) {
                if (this._conceptData[i2].crqEquals(this._conceptData[i3])) {
                    this._conceptData[i3] = null;
                } else {
                    i2 = i3;
                }
                i3++;
            }
            i = i3;
        }
        int i4 = 0;
        while (true) {
            int i5 = i4;
            if (i5 >= this._free2 - 1) {
                break;
            }
            int i6 = i5 + 1;
            while (i6 < this._free2) {
                if (this._conceptData[i6] != null) {
                    if (this._conceptData[i5].cEquals(this._conceptData[i6])) {
                        this._conceptData[i5].addLast(this._conceptData[i6]);
                        this._conceptData[i6] = null;
                    } else {
                        i5 = i6;
                    }
                }
                i6++;
            }
            i4 = i6;
        }
        for (int i7 = 0; i7 < this._free2 - 1; i7++) {
            if (this._conceptData[i7] == null) {
                for (int i8 = i7 + 1; i8 < this._free2; i8++) {
                    if (this._conceptData[i8] != null) {
                        this._conceptData[i7] = this._conceptData[i8];
                        this._conceptData[i8] = null;
                        break;
                    }
                }
            }
        }
        this._nextDocGenHeap.reset();
        for (int i9 = 0; i9 < this._free2 && this._conceptData[i9] != null; i9++) {
            NextDocGenerator nextDocGenerator = new NextDocGenerator(this._conceptData[i9], this._env);
            try {
                nextDocGenerator.first();
                if (nextDocGenerator.getDocument() != -1) {
                    this._conceptData[i9].setConceptLength(this._env.getConceptLength(this._conceptData[i9].getConcept()));
                    this._nextDocGenHeap.addGenerator(nextDocGenerator);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this._nextDocGenHeap.start();
        searchDocument();
        if (searchQuery == null) {
            printResults(this._maxHitsToShow);
        } else {
            this._query[0].makeEvent(this._maxHitsToShow, searchQuery);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:22:0x007f  */
    /* JADX WARN: Code duplicated, block: B:24:0x0087  */
    /* JADX WARN: Code duplicated, block: B:41:0x009c A[ADDED_TO_REGION, REMOVE, SYNTHETIC] */
    private void searchDocument() {
        int i;
        RoleFiller roleFiller;
        RoleFiller[] roleFillerArr = new RoleFiller[this._nQueries];
        do {
            try {
                switch (nextDocument(roleFillerArr)) {
                    case 0:
                        this._genHeap.start(roleFillerArr);
                        while (this._genHeap.next(roleFillerArr)) {
                        }
                        for (i = 0; i < this._nQueries; i++) {
                            roleFiller = roleFillerArr[i];
                            if (roleFiller == null && roleFiller != RoleFiller.STOP) {
                                roleFiller.scoreList(this._query[i], this._document);
                            }
                        }
                        this._genHeap.reset();
                        break;
                    case 1:
                        if (this._firstGenerator.next()) {
                            this._firstGenerator.generateFillers(roleFillerArr);
                            while (this._firstGenerator.next()) {
                                this._firstGenerator.generateFillers(roleFillerArr);
                            }
                        }
                        while (i < this._nQueries) {
                            roleFiller = roleFillerArr[i];
                            if (roleFiller == null) {
                            }
                        }
                        this._genHeap.reset();
                        break;
                    case 2:
                        break;
                    default:
                        while (i < this._nQueries) {
                            roleFiller = roleFillerArr[i];
                            if (roleFiller == null) {
                            }
                        }
                        this._genHeap.reset();
                        break;
                }
                return;
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        } while (this._nextDocGenHeap.isNonEmpty());
    }

    private int indexOf(int i) throws Exception {
        int i2 = this._startingIndex;
        int i3 = this._nConcepts;
        while (i2 <= i3) {
            int i4 = (i2 + i3) / 2;
            if (this._concepts[i4] < i) {
                i2 = i4 + 1;
            } else if (i < this._concepts[i4]) {
                i3 = i4 - 1;
            } else {
                this._startingIndex = i4 + 1;
                return i4;
            }
        }
        throw new Exception(new StringBuffer().append("indexOf ").append(i).append(" not found").toString());
    }

    private void printResults(int i) {
        for (int i2 = 0; i2 < this._nQueries; i2++) {
            System.out.println(new StringBuffer().append("query ").append(i2).toString());
            if (this._query[i2] != null) {
                this._query[i2].printHits(i);
            }
        }
    }

    private ConceptGroupGenerator makeGenerator(int i) throws Exception {
        int iAt;
        int iAt2;
        if (i > 0) {
            iAt = this._base + this._offsets.at(i - 1);
            iAt2 = this._maxConcepts.at(i - 1);
        } else {
            iAt = this._base;
            iAt2 = 0;
        }
        ConceptGroupGenerator conceptGroupGenerator = new ConceptGroupGenerator(this._data, iAt, this._kTable.at((2 * i) + 1));
        this._nConcepts = conceptGroupGenerator.decodeConcepts(this._kTable.at(2 * i), iAt2, this._concepts);
        if (i < this._limit) {
            int[] iArr = this._concepts;
            int i2 = this._nConcepts;
            int iAt3 = this._maxConcepts.at(i);
            iArr[i2] = iAt3;
            this._max = iAt3;
        } else {
            this._max = this._concepts[this._nConcepts - 1];
        }
        this._genHeap.addGenerator(conceptGroupGenerator);
        this._startingIndex = 0;
        return conceptGroupGenerator;
    }

    private boolean openDocumentIndex(int i) throws Exception {
        this._data = this._env.getPositions(i);
        this._base = 0;
        this._startingIndex = 0;
        int i2 = this._data[this._base] & 255;
        switch (i2 >> 6) {
            case 0:
                byte b = this._data[this._base + 1];
                ConceptGroupGenerator conceptGroupGenerator = this._firstGenerator;
                byte[] bArr = this._data;
                int i3 = this._base + 2;
                this._base = i3;
                conceptGroupGenerator.init(bArr, i3, b);
                this._nConcepts = this._firstGenerator.decodeConcepts(i2 & 63, 0, this._concepts);
                return false;
            case 1:
            case 3:
                throw new Exception("extents not yet implemented\n");
            case 2:
                this._kTable.clear();
                this._offsets.clear();
                this._maxConcepts.clear();
                ByteArrayDecompressor byteArrayDecompressor = new ByteArrayDecompressor(this._data, this._base + 1);
                byteArrayDecompressor.decode(i2 & 63, this._kTable);
                byteArrayDecompressor.ascDecode(this._kTable.popLast(), this._offsets);
                byteArrayDecompressor.ascDecode(this._kTable.popLast(), this._maxConcepts);
                this._base += 1 + byteArrayDecompressor.bytesRead();
                this._limit = this._maxConcepts.cardinality();
                return true;
            default:
                return false;
        }
    }

    private int nextDocument(RoleFiller[] roleFillerArr) throws Exception {
        while (this._nextDocGenHeap.isNonEmpty()) {
            for (int i = 0; i < this._nQueries; i++) {
                if (this._query[i] != null) {
                    this._query[i].resetForNextDocument();
                }
            }
            int i2 = 0;
            this._document = this._nextDocGenHeap.getDocument();
            this._docConcepts.clear();
            this._queryMasks.clear();
            do {
                this._docConcepts.add(this._nextDocGenHeap.getConcept());
                this._queryMasks.add(this._nextDocGenHeap.getQueryMask());
                ConceptData[] conceptDataArr = this._conceptData;
                int i3 = i2;
                i2++;
                ConceptData terms = this._nextDocGenHeap.getTerms();
                conceptDataArr[i3] = terms;
                terms.runBy(this._query);
                this._nextDocGenHeap.step();
            } while (this._nextDocGenHeap.atDocument(this._document));
            int i4 = 0;
            for (int i5 = 0; i5 < this._nQueries; i5++) {
                if (this._query[i5] != null) {
                    if (this._query[i5].vote()) {
                        roleFillerArr[i5] = null;
                        i4 |= 1 << i5;
                    } else {
                        roleFillerArr[i5] = RoleFiller.STOP;
                    }
                }
            }
            if (i4 != 0) {
                if (openDocumentIndex(this._document)) {
                    int i6 = 0;
                    while ((this._queryMasks.at(i6) & i4) == 0) {
                        i6++;
                    }
                    int iAt = this._docConcepts.at(i6);
                    int i7 = 0;
                    while (iAt > this._maxConcepts.at(i7)) {
                        i7++;
                        if (i7 >= this._limit) {
                            break;
                        }
                    }
                    ConceptGroupGenerator conceptGroupGeneratorMakeGenerator = makeGenerator(i7);
                    conceptGroupGeneratorMakeGenerator.addTerms(indexOf(iAt), this._conceptData[i6]);
                    while (true) {
                        i6++;
                        if (i6 < i2) {
                            if ((this._queryMasks.at(i6) & i4) > 0) {
                                int iAt2 = this._docConcepts.at(i6);
                                if (iAt2 > this._max) {
                                    while (iAt2 > this._maxConcepts.at(i7)) {
                                        i7++;
                                        if (i7 >= this._limit) {
                                            break;
                                        }
                                    }
                                    conceptGroupGeneratorMakeGenerator = makeGenerator(i7);
                                }
                                conceptGroupGeneratorMakeGenerator.addTerms(indexOf(iAt2), this._conceptData[i6]);
                            }
                        } else {
                            return 0;
                        }
                    }
                } else {
                    for (int i8 = 0; i8 < i2; i8++) {
                        if ((this._queryMasks.at(i8) & i4) != 0) {
                            this._firstGenerator.addTerms(indexOf(this._docConcepts.at(i8)), this._conceptData[i8]);
                        }
                    }
                    return 1;
                }
            }
        }
        return 2;
    }

    private int partition(int i, int i2) {
        ConceptData conceptData = this._conceptData[i];
        int i3 = i - 1;
        int i4 = i2 + 1;
        while (true) {
            i4--;
            if (!conceptData.compareWith(this._conceptData[i4])) {
                do {
                    i3++;
                } while (this._conceptData[i3].compareWith(conceptData));
                if (i3 < i4) {
                    ConceptData conceptData2 = this._conceptData[i3];
                    this._conceptData[i3] = this._conceptData[i4];
                    this._conceptData[i4] = conceptData2;
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
