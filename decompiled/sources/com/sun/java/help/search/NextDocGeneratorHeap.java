package com.sun.java.help.search;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/NextDocGeneratorHeap.class */
class NextDocGeneratorHeap {
    private static final int InitSize = 1024;
    private int _heapSize = 0;
    private int _size = 1024;
    private NextDocGenerator[] _heap = new NextDocGenerator[1024];
    private int _free = 0;
    private boolean _nonEmpty = false;

    NextDocGeneratorHeap() {
    }

    public boolean isNonEmpty() {
        return this._nonEmpty;
    }

    public void addGenerator(NextDocGenerator nextDocGenerator) {
        if (this._free == this._size) {
            int i = this._size * 2;
            this._size = i;
            NextDocGenerator[] nextDocGeneratorArr = new NextDocGenerator[i];
            System.arraycopy(this._heap, 0, nextDocGeneratorArr, 0, this._free);
            this._heap = nextDocGeneratorArr;
        }
        NextDocGenerator[] nextDocGeneratorArr2 = this._heap;
        int i2 = this._free;
        this._free = i2 + 1;
        nextDocGeneratorArr2[i2] = nextDocGenerator;
    }

    public void start() {
        int i = this._free;
        this._heapSize = i;
        if (i > 0) {
            for (int i2 = this._heapSize / 2; i2 >= 0; i2--) {
                heapify(i2);
            }
            this._nonEmpty = true;
            return;
        }
        this._nonEmpty = false;
    }

    public void step() throws Exception {
        if (this._heap[0].next() != -1) {
            heapify(0);
            return;
        }
        if (this._heapSize > 1) {
            NextDocGenerator[] nextDocGeneratorArr = this._heap;
            NextDocGenerator[] nextDocGeneratorArr2 = this._heap;
            int i = this._heapSize - 1;
            this._heapSize = i;
            nextDocGeneratorArr[0] = nextDocGeneratorArr2[i];
            heapify(0);
            return;
        }
        this._nonEmpty = false;
    }

    public int getDocument() {
        return this._heap[0].getDocument();
    }

    public int getConcept() {
        return this._heap[0].getConcept();
    }

    public ConceptData getTerms() {
        return this._heap[0].getTerms();
    }

    public int getQueryMask() {
        return this._heap[0].getQueryMask();
    }

    public void reset() {
        this._nonEmpty = false;
        this._free = 0;
    }

    public boolean atDocument(int i) {
        return this._nonEmpty && this._heap[0].getDocument() == i;
    }

    private void heapify(int i) {
        int i2 = (i + 1) << 1;
        int i3 = i2 - 1;
        int i4 = (i3 >= this._heapSize || !this._heap[i3].smallerThan(this._heap[i])) ? i : i3;
        if (i2 < this._heapSize && this._heap[i2].smallerThan(this._heap[i4])) {
            i4 = i2;
        }
        if (i4 != i) {
            NextDocGenerator nextDocGenerator = this._heap[i4];
            this._heap[i4] = this._heap[i];
            this._heap[i] = nextDocGenerator;
            heapify(i4);
        }
    }
}
