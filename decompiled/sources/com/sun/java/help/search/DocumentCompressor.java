package com.sun.java.help.search;

import java.io.BufferedInputStream;
import java.io.DataOutput;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/DocumentCompressor.class */
class DocumentCompressor {
    public static final int NConceptsInGroup = 16;
    public static final int BitsInLabel = 4;
    public static final int DefaultSize = 32;
    private int _nGroups;
    private int _nExtents;
    private int _freeComp;
    private int _kk;
    private RandomAccessFile _posFile;
    private static boolean debug = false;
    private int _sizeComp = 32;
    private Compressor _currentCompressor = null;
    private Compressor[] _compressors = new Compressor[32];
    private Compressor _kCompr = new Compressor();
    private Compressor _lCompr = new Compressor();
    private Compressor _mCompr = new Compressor();
    private Compressor _posCompressor = new Compressor();
    private IntegerArray _kTable = new IntegerArray();
    private IntegerArray _lTable = new IntegerArray();
    private IntegerArray _maxConcepts = new IntegerArray();
    private IntegerArray _concepts = new IntegerArray();
    private IntegerArray _documents = new IntegerArray();
    private IntegerArray _offsets = new IntegerArray();
    private IntegerArray _titles = new IntegerArray();
    private IntegerArray _positions = new IntegerArray();
    private IntegerArray _labels = new IntegerArray();

    public DocumentCompressor(URL url) throws Exception {
        URL url2 = new URL(url, "POSITIONS");
        if (isFileURL(url2)) {
            try {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(new URL(url, "OFFSETS").openConnection().getInputStream());
                new StreamDecompressor(bufferedInputStream).ascDecode(bufferedInputStream.read(), this._documents);
                new StreamDecompressor(bufferedInputStream).ascDecode(bufferedInputStream.read(), this._offsets);
                new StreamDecompressor(bufferedInputStream).decode(bufferedInputStream.read(), this._titles);
                bufferedInputStream.close();
            } catch (FileNotFoundException e) {
            }
            this._posFile = new RandomAccessFile(Utilities.URLDecoder(url2.getFile()), "rw");
            return;
        }
        throw new IOException();
    }

    private boolean isFileURL(URL url) {
        return url.getProtocol().equalsIgnoreCase("file");
    }

    public void close(String str) throws IOException {
        this._posFile.close();
        writeOutOffsets(str);
    }

    public void compress(int i, int i2, ConceptLocation[] conceptLocationArr, int i3, ConceptLocation[] conceptLocationArr2, int i4) throws IOException {
        long jCurrentTimeMillis = System.currentTimeMillis();
        encode(conceptLocationArr, i3, 16);
        if (i4 > 0) {
            encodeExtents(conceptLocationArr2, i4);
        }
        finalizeEncoding();
        debug(new StringBuffer().append(System.currentTimeMillis() - jCurrentTimeMillis).append(" msec proc").toString());
        byteCount();
        long jCurrentTimeMillis2 = System.currentTimeMillis();
        long length = this._posFile.length();
        this._documents.add(i);
        this._offsets.add((int) length);
        this._titles.add(i2);
        this._posFile.seek(length);
        writeOut(this._posFile);
        debug(new StringBuffer().append(System.currentTimeMillis() - jCurrentTimeMillis2).append(" msec file").toString());
        debug(new StringBuffer().append("nGroups = ").append(this._nGroups).toString());
    }

    private void writeOutOffsets(String str) throws IOException {
        Compressor compressor = new Compressor();
        int iCompressAscending = compressor.compressAscending(this._documents);
        Compressor compressor2 = new Compressor();
        int iCompressAscending2 = compressor2.compressAscending(this._offsets);
        Compressor compressor3 = new Compressor();
        int iMinimize = compressor3.minimize(this._titles, 8);
        compressor.byteCount();
        RandomAccessFile randomAccessFile = new RandomAccessFile(str, "rw");
        randomAccessFile.seek(0L);
        randomAccessFile.write(iCompressAscending);
        compressor.write(randomAccessFile);
        randomAccessFile.write(iCompressAscending2);
        compressor2.write(randomAccessFile);
        randomAccessFile.write(iMinimize);
        compressor3.write(randomAccessFile);
        randomAccessFile.close();
    }

    private void encode(ConceptLocation[] conceptLocationArr, int i, int i2) {
        ConceptLocation.sortByConcept(conceptLocationArr, 0, i);
        clear();
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        int concept = conceptLocationArr[0].getConcept();
        nextCompressor();
        this._concepts.add(concept);
        int i6 = 0;
        while (true) {
            if (i6 < i && conceptLocationArr[i6].getConcept() == concept) {
                conceptLocationArr[i6].setConcept(i3);
                i6++;
            } else {
                if (i6 == i) {
                    break;
                }
                i3++;
                if (i3 == i2) {
                    this._nGroups++;
                    this._concepts.popLast();
                    this._maxConcepts.add(concept - i5);
                    i5 = concept;
                    this._kTable.add(this._currentCompressor.minimize(this._concepts, 4));
                    encodePositions(conceptLocationArr, i4, i6, 4);
                    i4 = i6;
                    nextCompressor();
                    this._concepts.clear();
                    i3 = 0;
                }
                this._concepts.add(conceptLocationArr[i6].getConcept() - concept);
                concept = conceptLocationArr[i6].getConcept();
            }
        }
        if (this._concepts.cardinality() > 0) {
            this._nGroups++;
            this._kTable.add(this._currentCompressor.minimize(this._concepts, 4));
        }
        encodePositions(conceptLocationArr, i4, i6, 4);
    }

    private void encodePositions(ConceptLocation[] conceptLocationArr, int i, int i2, int i3) {
        ConceptLocation.sortByPosition(conceptLocationArr, i, i2);
        this._positions.clear();
        this._labels.clear();
        IntegerArray integerArray = this._positions;
        int begin = conceptLocationArr[i].getBegin();
        int begin2 = begin;
        integerArray.add(begin);
        this._labels.add(conceptLocationArr[i].getConcept());
        int i4 = i;
        for (int i5 = i + 1; i5 < i2; i5++) {
            if (!conceptLocationArr[i4].equals(conceptLocationArr[i5])) {
                i4 = i5;
                this._positions.add(conceptLocationArr[i4].getBegin() - begin2);
                begin2 = conceptLocationArr[i4].getBegin();
                this._labels.add(conceptLocationArr[i4].getConcept());
            }
        }
        IntegerArray integerArray2 = this._kTable;
        int iMinimize = this._posCompressor.minimize(this._positions, 3);
        integerArray2.add(iMinimize);
        this._posCompressor.clear();
        this._posCompressor.encode(this._positions, this._labels, iMinimize, i3);
        this._currentCompressor.concatenate(this._posCompressor);
    }

    private void encodeExtents(ConceptLocation[] conceptLocationArr, int i) {
        int concept = 0;
        IntegerArray integerArray = new IntegerArray(i);
        IntegerArray integerArray2 = new IntegerArray();
        IntegerArray integerArray3 = new IntegerArray();
        IntegerArray integerArray4 = new IntegerArray();
        nextCompressor();
        Compressor compressor = this._currentCompressor;
        for (int i2 = 0; i2 < i; i2++) {
            if (conceptLocationArr[i2].getConcept() != concept) {
                if (concept != 0) {
                    this._nExtents++;
                    nextCompressor();
                    integerArray3.add(this._currentCompressor.minimize(integerArray2, 4));
                    integerArray4.add(this._currentCompressor.byteCount());
                }
                integerArray.add(conceptLocationArr[i2].getConcept() - concept);
                concept = conceptLocationArr[i2].getConcept();
                integerArray2.clear();
                integerArray2.add(conceptLocationArr[i2].getLength());
            } else {
                integerArray2.add(conceptLocationArr[i2].getLength());
            }
        }
        nextCompressor();
        integerArray3.add(this._currentCompressor.minimize(integerArray2, 4));
        integerArray4.add(this._currentCompressor.byteCount());
        Compressor compressor2 = new Compressor();
        integerArray3.add(compressor2.minimize(integerArray4, 4));
        Compressor compressor3 = new Compressor();
        integerArray3.add(compressor3.minimize(integerArray, 4));
        this._kTable.add(compressor.minimize(integerArray3, 4));
        compressor.concatenate(compressor2);
        compressor.concatenate(compressor3);
    }

    private void finalizeEncoding() {
        if (this._nGroups > 1) {
            int i = this._nExtents > 0 ? this._freeComp : this._freeComp - 1;
            for (int i2 = 0; i2 < i; i2++) {
                this._lTable.add(this._compressors[i2].byteCount());
            }
            this._kTable.add(this._mCompr.minimize(this._maxConcepts, 3));
            this._kTable.add(this._lCompr.minimize(this._lTable, 3));
            this._kk = this._kCompr.minimize(this._kTable, 3);
            this._kCompr.concatenate(this._lCompr);
            this._kCompr.concatenate(this._mCompr);
        } else if (this._nGroups == 1 && this._nExtents > 0) {
            this._kTable.add(this._compressors[0].byteCount());
            this._kk = this._kCompr.minimize(this._kTable, 3);
        }
        debug(new StringBuffer().append("compr: ").append(byteCount()).append(" bytes").toString());
    }

    private void writeOut(DataOutput dataOutput) throws IOException {
        if (this._nExtents == 0) {
            if (this._nGroups > 1) {
                dataOutput.write(128 | this._kk);
                this._kCompr.write(dataOutput);
                for (int i = 0; i < this._freeComp; i++) {
                    this._compressors[i].write(dataOutput);
                }
                return;
            }
            dataOutput.write(this._kTable.at(0));
            dataOutput.write(this._kTable.at(1));
            this._compressors[0].write(dataOutput);
            return;
        }
        dataOutput.write((this._nGroups > 1 ? 192 : 64) | this._kk);
        this._kCompr.write(dataOutput);
        for (int i2 = 0; i2 < this._freeComp; i2++) {
            this._compressors[i2].write(dataOutput);
        }
    }

    private Compressor nextCompressor() {
        if (this._freeComp == this._sizeComp) {
            int i = this._sizeComp * 2;
            this._sizeComp = i;
            Compressor[] compressorArr = new Compressor[i];
            System.arraycopy(this._compressors, 0, compressorArr, 0, this._freeComp);
            this._compressors = compressorArr;
        }
        if (this._compressors[this._freeComp] == null) {
            this._compressors[this._freeComp] = new Compressor();
        }
        Compressor[] compressorArr2 = this._compressors;
        int i2 = this._freeComp;
        this._freeComp = i2 + 1;
        Compressor compressor = compressorArr2[i2];
        this._currentCompressor = compressor;
        return compressor;
    }

    private int byteCount() {
        if (this._nGroups == 1 && this._nExtents == 0) {
            return 2 + this._compressors[0].byteCount();
        }
        int iByteCount = 1 + this._kCompr.byteCount();
        for (int i = 0; i < this._freeComp; i++) {
            iByteCount += this._compressors[i].byteCount();
        }
        return iByteCount;
    }

    private void clear() {
        this._nGroups = 0;
        this._nExtents = 0;
        this._kTable.clear();
        this._lTable.clear();
        this._concepts.clear();
        this._maxConcepts.clear();
        this._kCompr.clear();
        this._lCompr.clear();
        this._mCompr.clear();
        for (int i = 0; i < this._sizeComp; i++) {
            if (this._compressors[i] != null) {
                this._compressors[i].clear();
            }
        }
        this._freeComp = 0;
        this._currentCompressor = null;
    }

    private static void debug(String str) {
        if (debug) {
            System.err.println(new StringBuffer().append("DocumentCompressor: ").append(str).toString());
        }
    }
}
