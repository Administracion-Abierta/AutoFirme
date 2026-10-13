package com.sun.java.help.search;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/GeneratorHeap.class */
class GeneratorHeap {
    private static final int InitSize = 128;
    private int _heapSize = 0;
    private int _size = 128;
    private int _free = 0;
    private ConceptGroupGenerator[] _heap = new ConceptGroupGenerator[128];

    public void reset() {
        this._free = 0;
    }

    public void addGenerator(ConceptGroupGenerator conceptGroupGenerator) {
        if (this._free == this._size) {
            int i = this._size * 2;
            this._size = i;
            ConceptGroupGenerator[] conceptGroupGeneratorArr = new ConceptGroupGenerator[i];
            System.arraycopy(this._heap, 0, conceptGroupGeneratorArr, 0, this._free);
            this._heap = conceptGroupGeneratorArr;
        }
        ConceptGroupGenerator[] conceptGroupGeneratorArr2 = this._heap;
        int i2 = this._free;
        this._free = i2 + 1;
        conceptGroupGeneratorArr2[i2] = conceptGroupGenerator;
    }

    private void buildHeap() {
        for (int i = this._heapSize / 2; i >= 0; i--) {
            heapify(i);
        }
    }

    private void heapify(int i) {
        int i2 = (i + 1) << 1;
        int i3 = i2 - 1;
        int i4 = (i3 >= this._heapSize || this._heap[i3].position() >= this._heap[i].position()) ? i : i3;
        if (i2 < this._heapSize && this._heap[i2].position() < this._heap[i4].position()) {
            i4 = i2;
        }
        if (i4 != i) {
            ConceptGroupGenerator conceptGroupGenerator = this._heap[i4];
            this._heap[i4] = this._heap[i];
            this._heap[i] = conceptGroupGenerator;
            heapify(i4);
        }
    }

    public boolean start(RoleFiller[] roleFillerArr) throws Exception {
        int i = this._free;
        this._heapSize = i;
        if (i > 0) {
            for (int i2 = 0; i2 < this._free; i2++) {
                this._heap[i2].next();
            }
            buildHeap();
            this._heap[0].generateFillers(roleFillerArr);
            return true;
        }
        return false;
    }

    public boolean next(RoleFiller[] roleFillerArr) throws Exception {
        if (this._heapSize > 0) {
            if (!this._heap[0].next()) {
                if (this._heapSize > 1) {
                    ConceptGroupGenerator[] conceptGroupGeneratorArr = this._heap;
                    ConceptGroupGenerator[] conceptGroupGeneratorArr2 = this._heap;
                    int i = this._heapSize - 1;
                    this._heapSize = i;
                    conceptGroupGeneratorArr[0] = conceptGroupGeneratorArr2[i];
                } else {
                    this._heapSize = 0;
                    return false;
                }
            }
            heapify(0);
            this._heap[0].generateFillers(roleFillerArr);
            return true;
        }
        return false;
    }
}
