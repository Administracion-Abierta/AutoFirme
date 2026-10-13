package com.sun.java.help.search;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/DocumentLists.class */
class DocumentLists {
    private static final int RANGE = 1024;
    private static final int NConcepts = 16;
    private static final int K = 3;
    private DataOutputStream _mainFile;
    private int _heapSize;
    private MicroIndex[] _heap;
    private final IntegerArray[] _arrays = new IntegerArray[1024];
    private int _minConcept = 0;
    private int _limit = 1024;
    private final IntegerArray _concepts = new IntegerArray();
    private final IntegerArray _offsets = new IntegerArray();
    private final Compressor _compr = new Compressor();
    private final IntegerArray _diffs = new IntegerArray();
    private final ByteArrayDecompressor _decmp = new ByteArrayDecompressor(null, 0);
    private boolean debug = false;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/DocumentLists$MicroIndex.class */
    private class MicroIndex {
        private int _currentRange;
        private final int _documentNumber;
        private short _group;
        private short _ix;
        private byte[] _data;
        private int _base;
        private int _limit;
        private int _nc;
        private final DocumentLists this$0;
        private int[] _concepts = new int[17];
        private IntegerArray _kTable = new IntegerArray();
        private IntegerArray _offsets = new IntegerArray();
        private IntegerArray _maxConcepts = new IntegerArray();

        public MicroIndex(DocumentLists documentLists, int i, byte[] bArr, int i2) throws Exception {
            this.this$0 = documentLists;
            this._documentNumber = i;
            this._data = bArr;
            this._base = i2;
            openDocumentIndex();
        }

        public boolean smallerThan(MicroIndex microIndex) {
            return this._currentRange < microIndex._currentRange || (this._currentRange == microIndex._currentRange && this._documentNumber < microIndex._documentNumber);
        }

        private boolean next() throws Exception {
            int iAt;
            int iAt2;
            if (this._group <= this._limit) {
                if (this._group > 0) {
                    iAt = this._base + this._offsets.at(this._group - 1);
                    iAt2 = this._maxConcepts.at(this._group - 1);
                } else {
                    iAt = this._base;
                    iAt2 = 0;
                }
                this.this$0._decmp.initReading(this._data, iAt);
                this._nc = this.this$0._decmp.ascendingDecode(this._kTable.at(this._group * 2), iAt2, this._concepts);
                if (this._group < this._limit) {
                    int[] iArr = this._concepts;
                    int i = this._nc;
                    this._nc = i + 1;
                    iArr[i] = this._maxConcepts.at(this._group);
                }
                int[] iArr2 = this._concepts;
                this._ix = (short) 0;
                this._currentRange = iArr2[0] / 1024;
                this._group = (short) (this._group + 1);
                return true;
            }
            return false;
        }

        private void openDocumentIndex() throws Exception {
            int i = this._data[this._base] & 255;
            switch (i >> 6) {
                case 0:
                    ByteArrayDecompressor byteArrayDecompressor = this.this$0._decmp;
                    byte[] bArr = this._data;
                    int i2 = this._base + 2;
                    this._base = i2;
                    byteArrayDecompressor.initReading(bArr, i2);
                    this._nc = this.this$0._decmp.ascendingDecode(i & 63, 0, this._concepts);
                    int[] iArr = this._concepts;
                    this._ix = (short) 0;
                    this._currentRange = iArr[0] / 1024;
                    this._limit = 0;
                    this._group = (short) 1;
                    break;
                case 1:
                case 3:
                    System.err.println("extents not yet implemented\n");
                    break;
                case 2:
                    this.this$0._decmp.initReading(this._data, this._base + 1);
                    this.this$0._decmp.decode(i & 63, this._kTable);
                    this.this$0._decmp.ascDecode(this._kTable.popLast(), this._offsets);
                    this.this$0._decmp.ascDecode(this._kTable.popLast(), this._maxConcepts);
                    this._base += 1 + this.this$0._decmp.bytesRead();
                    this._limit = this._maxConcepts.cardinality();
                    this._group = (short) 0;
                    next();
                    break;
            }
        }

        public boolean process(DocumentLists documentLists) throws Exception {
            boolean z = true;
            while (true) {
                short sProcess = documentLists.process(this._documentNumber, this._concepts, this._nc, this._ix, z);
                if (sProcess < this._nc) {
                    int[] iArr = this._concepts;
                    this._ix = sProcess;
                    this._currentRange = iArr[sProcess] / 1024;
                    return true;
                }
                if (next()) {
                    z = false;
                } else {
                    return false;
                }
            }
        }
    }

    private DocumentLists(String str) throws Exception {
        this._heapSize = 0;
        for (int i = 0; i < 1024; i++) {
            this._arrays[i] = new IntegerArray();
        }
        this._mainFile = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new StringBuffer().append(str).append("DOCS").toString())));
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(new StringBuffer().append(str).append("OFFSETS").toString()));
        int i2 = bufferedInputStream.read();
        IntegerArray integerArray = new IntegerArray(4096);
        new StreamDecompressor(bufferedInputStream).ascDecode(i2, integerArray);
        int i3 = bufferedInputStream.read();
        IntegerArray integerArray2 = new IntegerArray(integerArray.cardinality() + 1);
        new StreamDecompressor(bufferedInputStream).ascDecode(i3, integerArray2);
        bufferedInputStream.close();
        File file = new File(new StringBuffer().append(str).append("POSITIONS").toString());
        byte[] bArr = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(bArr);
        fileInputStream.close();
        int iCardinality = integerArray.cardinality();
        this._heapSize = iCardinality;
        this._heap = new MicroIndex[iCardinality];
        for (int i4 = 0; i4 < integerArray.cardinality(); i4++) {
            this._heap[i4] = new MicroIndex(this, i4, bArr, integerArray2.at(i4));
        }
        debug(new StringBuffer().append(integerArray.cardinality()).append(" documents").toString());
        for (int i5 = this._heapSize / 2; i5 >= 0; i5--) {
            heapify(i5);
        }
        while (true) {
            if (this._heap[0].process(this)) {
                heapify(0);
            } else if (this._heapSize > 1) {
                MicroIndex[] microIndexArr = this._heap;
                MicroIndex[] microIndexArr2 = this._heap;
                int i6 = this._heapSize - 1;
                this._heapSize = i6;
                microIndexArr[0] = microIndexArr2[i6];
                heapify(0);
            } else {
                flush();
                this._mainFile.close();
                DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new StringBuffer().append(str).append("DOCS.TAB").toString())));
                dataOutputStream.write(this._compr.compressAscending(this._concepts));
                this._compr.write(dataOutputStream);
                this._compr.clear();
                dataOutputStream.write(this._compr.minimize(this._offsets, 3));
                this._compr.write(dataOutputStream);
                dataOutputStream.close();
                return;
            }
        }
    }

    public short process(int i, int[] iArr, int i2, short s, boolean z) throws IOException {
        if (z && iArr[s] >= this._limit) {
            flush();
        }
        iArr[i2] = this._limit;
        while (iArr[s] < this._limit) {
            short s2 = s;
            s = (short) (s + 1);
            this._arrays[iArr[s2] - this._minConcept].add(i);
        }
        return s;
    }

    private void heapify(int i) {
        int i2 = (i + 1) << 1;
        int i3 = i2 - 1;
        int i4 = (i3 >= this._heapSize || !this._heap[i3].smallerThan(this._heap[i])) ? i : i3;
        if (i2 < this._heapSize && this._heap[i2].smallerThan(this._heap[i4])) {
            i4 = i2;
        }
        if (i4 != i) {
            MicroIndex microIndex = this._heap[i4];
            this._heap[i4] = this._heap[i];
            this._heap[i] = microIndex;
            heapify(i4);
        }
    }

    private void flush() throws IOException {
        for (int i = 0; i < 1024; i++) {
            if (this._arrays[i].cardinality() > 0) {
                this._arrays[i].toDifferences(this._diffs);
                this._mainFile.write(this._compr.minimize(this._diffs, 3));
                this._offsets.add(this._compr.byteCount() + 1);
                this._compr.write(this._mainFile);
                this._concepts.add(this._minConcept + i);
                this._arrays[i].clear();
                this._diffs.clear();
                this._compr.clear();
            }
        }
        this._limit += 1024;
        this._minConcept += 1024;
    }

    public static void invert(String str) throws Exception {
        new DocumentLists(str);
    }

    public static void main(String[] strArr) {
        try {
            new DocumentLists(strArr[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void debug(String str) {
        if (this.debug) {
            System.err.println(new StringBuffer().append("DocumentLists: ").append(str).toString());
        }
    }
}
