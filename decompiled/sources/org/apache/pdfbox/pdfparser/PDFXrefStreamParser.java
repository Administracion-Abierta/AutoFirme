package org.apache.pdfbox.pdfparser;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObjectKey;
import org.apache.pdfbox.cos.COSStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdfparser/PDFXrefStreamParser.class */
public class PDFXrefStreamParser extends BaseParser {
    private final XrefTrailerResolver xrefTrailerResolver;
    private final int[] w;
    private ObjectNumbers objectNumbers;

    public PDFXrefStreamParser(COSStream stream, COSDocument document, XrefTrailerResolver resolver) throws IOException {
        super(new InputStreamSource(stream.createInputStream()));
        this.w = new int[3];
        this.objectNumbers = null;
        this.document = document;
        this.xrefTrailerResolver = resolver;
        try {
            initParserValues(stream);
        } catch (IOException e) {
            close();
        }
    }

    private void initParserValues(COSStream stream) throws IOException {
        COSArray wArray = stream.getCOSArray(COSName.W);
        if (wArray == null) {
            throw new IOException("/W array is missing in Xref stream");
        }
        if (wArray.size() != 3) {
            throw new IOException("Wrong number of values for /W array in XRef: " + Arrays.toString(this.w));
        }
        for (int i = 0; i < 3; i++) {
            this.w[i] = wArray.getInt(i, 0);
        }
        if (this.w[0] < 0 || this.w[1] < 0 || this.w[2] < 0) {
            throw new IOException("Incorrect /W array in XRef: " + Arrays.toString(this.w));
        }
        COSArray indexArray = stream.getCOSArray(COSName.INDEX);
        if (indexArray == null) {
            indexArray = new COSArray();
            indexArray.add((COSBase) COSInteger.ZERO);
            indexArray.add((COSBase) COSInteger.get(stream.getInt(COSName.SIZE, 0)));
        }
        if (indexArray.size() % 2 == 1) {
            throw new IOException("Wrong number of values for /Index array in XRef: " + Arrays.toString(this.w));
        }
        this.objectNumbers = new ObjectNumbers(indexArray);
    }

    private void close() throws IOException {
        if (this.seqSource != null) {
            this.seqSource.close();
        }
        this.document = null;
    }

    public void parse() throws IOException {
        byte[] currLine = new byte[this.w[0] + this.w[1] + this.w[2]];
        while (!this.seqSource.isEOF() && this.objectNumbers.hasNext()) {
            this.seqSource.read(currLine);
            long objID = this.objectNumbers.next().longValue();
            int type = this.w[0] == 0 ? 1 : (int) parseValue(currLine, 0, this.w[0]);
            if (type != 0) {
                long offset = parseValue(currLine, this.w[0], this.w[1]);
                int genNum = type == 1 ? (int) parseValue(currLine, this.w[0] + this.w[1], this.w[2]) : 0;
                COSObjectKey objKey = new COSObjectKey(objID, genNum);
                if (type == 1) {
                    this.xrefTrailerResolver.setXRef(objKey, offset);
                } else {
                    this.xrefTrailerResolver.setXRef(objKey, -offset);
                }
            }
        }
        close();
    }

    private long parseValue(byte[] data, int start, int length) {
        long value = 0;
        for (int i = 0; i < length; i++) {
            value += (((long) data[i + start]) & 255) << (((length - i) - 1) * 8);
        }
        return value;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdfparser/PDFXrefStreamParser$ObjectNumbers.class */
    private static class ObjectNumbers implements Iterator<Long> {
        private final long[] start;
        private final long[] end;
        private int currentRange;
        private long currentEnd;
        private long currentNumber;
        private long maxValue;

        private ObjectNumbers(COSArray indexArray) throws IOException {
            this.currentRange = 0;
            this.currentEnd = 0L;
            this.currentNumber = 0L;
            this.maxValue = 0L;
            this.start = new long[indexArray.size() / 2];
            this.end = new long[this.start.length];
            int counter = 0;
            Iterator<COSBase> indexIter = indexArray.iterator();
            while (indexIter.hasNext()) {
                COSBase base = indexIter.next();
                if (!(base instanceof COSInteger)) {
                    throw new IOException("Xref stream must have integer in /Index array");
                }
                long startValue = ((COSInteger) base).longValue();
                if (!indexIter.hasNext()) {
                    break;
                }
                COSBase base2 = indexIter.next();
                if (!(base2 instanceof COSInteger)) {
                    throw new IOException("Xref stream must have integer in /Index array");
                }
                long sizeValue = ((COSInteger) base2).longValue();
                this.start[counter] = startValue;
                int i = counter;
                counter++;
                this.end[i] = startValue + sizeValue;
            }
            this.currentNumber = this.start[0];
            this.currentEnd = this.end[0];
            this.maxValue = this.end[counter - 1];
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.currentNumber < this.maxValue;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Long next() {
            if (this.currentNumber >= this.maxValue) {
                throw new NoSuchElementException();
            }
            if (this.currentNumber < this.currentEnd) {
                long j = this.currentNumber;
                this.currentNumber = j + 1;
                return Long.valueOf(j);
            }
            long[] jArr = this.start;
            int i = this.currentRange + 1;
            this.currentRange = i;
            this.currentNumber = jArr[i];
            this.currentEnd = this.end[this.currentRange];
            long j2 = this.currentNumber;
            this.currentNumber = j2 + 1;
            return Long.valueOf(j2);
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
