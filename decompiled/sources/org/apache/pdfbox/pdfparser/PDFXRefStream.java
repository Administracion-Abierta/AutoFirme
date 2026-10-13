package org.apache.pdfbox.pdfparser;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdfwriter.COSWriterXRefEntry;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdfparser/PDFXRefStream.class */
public class PDFXRefStream implements PDFXRef {
    private static final int ENTRY_OBJSTREAM = 2;
    private static final int ENTRY_NORMAL = 1;
    private static final int ENTRY_FREE = 0;
    private final Map<Long, Object> streamData;
    private final Set<Long> objectNumbers;
    private final COSStream stream;
    private long size;

    @Deprecated
    public PDFXRefStream() {
        this.size = -1L;
        this.stream = new COSStream();
        this.streamData = new TreeMap();
        this.objectNumbers = new TreeSet();
    }

    public PDFXRefStream(COSDocument cosDocument) {
        this.size = -1L;
        this.stream = cosDocument.createCOSStream();
        this.streamData = new TreeMap();
        this.objectNumbers = new TreeSet();
    }

    public COSStream getStream() throws IOException {
        this.stream.setItem(COSName.TYPE, (COSBase) COSName.XREF);
        if (this.size == -1) {
            throw new IllegalArgumentException("size is not set in xrefstream");
        }
        this.stream.setLong(COSName.SIZE, this.size);
        List<Long> indexEntry = getIndexEntry();
        COSArray indexAsArray = new COSArray();
        for (Long i : indexEntry) {
            indexAsArray.add((COSBase) COSInteger.get(i.longValue()));
        }
        this.stream.setItem(COSName.INDEX, (COSBase) indexAsArray);
        int[] wEntry = getWEntry();
        COSArray wAsArray = new COSArray();
        for (int j : wEntry) {
            wAsArray.add((COSBase) COSInteger.get(j));
        }
        this.stream.setItem(COSName.W, (COSBase) wAsArray);
        OutputStream outputStream = this.stream.createOutputStream(COSName.FLATE_DECODE);
        writeStreamData(outputStream, wEntry);
        outputStream.flush();
        outputStream.close();
        Set<COSName> keySet = this.stream.keySet();
        for (COSName cosName : keySet) {
            if (!COSName.ROOT.equals(cosName) && !COSName.INFO.equals(cosName) && !COSName.PREV.equals(cosName) && !COSName.ENCRYPT.equals(cosName)) {
                COSBase dictionaryObject = this.stream.getDictionaryObject(cosName);
                dictionaryObject.setDirect(true);
            }
        }
        return this.stream;
    }

    public void addTrailerInfo(COSDictionary trailerDict) {
        Set<Map.Entry<COSName, COSBase>> entrySet = trailerDict.entrySet();
        for (Map.Entry<COSName, COSBase> entry : entrySet) {
            COSName key = entry.getKey();
            if (COSName.INFO.equals(key) || COSName.ROOT.equals(key) || COSName.ENCRYPT.equals(key) || COSName.ID.equals(key) || COSName.PREV.equals(key)) {
                this.stream.setItem(key, entry.getValue());
            }
        }
    }

    public void addEntry(COSWriterXRefEntry entry) {
        this.objectNumbers.add(Long.valueOf(entry.getKey().getNumber()));
        if (entry.isFree()) {
            FreeReference value = new FreeReference();
            value.nextGenNumber = entry.getKey().getGeneration();
            value.nextFree = entry.getKey().getNumber();
            this.streamData.put(Long.valueOf(value.nextFree), value);
            return;
        }
        NormalReference value2 = new NormalReference();
        value2.genNumber = entry.getKey().getGeneration();
        value2.offset = entry.getOffset();
        this.streamData.put(Long.valueOf(entry.getKey().getNumber()), value2);
    }

    private int[] getWEntry() {
        long[] wMax = new long[3];
        for (Object entry : this.streamData.values()) {
            if (entry instanceof FreeReference) {
                FreeReference free = (FreeReference) entry;
                wMax[0] = Math.max(wMax[0], 0L);
                wMax[1] = Math.max(wMax[1], free.nextFree);
                wMax[2] = Math.max(wMax[2], free.nextGenNumber);
            } else if (entry instanceof NormalReference) {
                NormalReference ref = (NormalReference) entry;
                wMax[0] = Math.max(wMax[0], 1L);
                wMax[1] = Math.max(wMax[1], ref.offset);
                wMax[2] = Math.max(wMax[2], ref.genNumber);
            } else if (entry instanceof ObjectStreamReference) {
                ObjectStreamReference objStream = (ObjectStreamReference) entry;
                wMax[0] = Math.max(wMax[0], 2L);
                wMax[1] = Math.max(wMax[1], objStream.offset);
                wMax[2] = Math.max(wMax[2], objStream.objectNumberOfObjectStream);
            } else {
                throw new RuntimeException("unexpected reference type");
            }
        }
        int[] w = new int[3];
        for (int i = 0; i < w.length; i++) {
            while (wMax[i] > 0) {
                int i2 = i;
                w[i2] = w[i2] + 1;
                int i3 = i;
                wMax[i3] = wMax[i3] >> 8;
            }
        }
        return w;
    }

    public void setSize(long streamSize) {
        this.size = streamSize;
    }

    private List<Long> getIndexEntry() {
        LinkedList<Long> linkedList = new LinkedList<>();
        Long first = null;
        Long length = null;
        Set<Long> objNumbers = new TreeSet<>();
        objNumbers.add(0L);
        objNumbers.addAll(this.objectNumbers);
        for (Long objNumber : objNumbers) {
            if (first == null) {
                first = objNumber;
                length = 1L;
            }
            if (first.longValue() + length.longValue() == objNumber.longValue()) {
                length = Long.valueOf(length.longValue() + 1);
            }
            if (first.longValue() + length.longValue() < objNumber.longValue()) {
                linkedList.add(first);
                linkedList.add(length);
                first = objNumber;
                length = 1L;
            }
        }
        linkedList.add(first);
        linkedList.add(length);
        return linkedList;
    }

    private void writeNumber(OutputStream os, long number, int bytes) throws IOException {
        byte[] buffer = new byte[bytes];
        for (int i = 0; i < bytes; i++) {
            buffer[i] = (byte) (number & 255);
            number >>= 8;
        }
        for (int i2 = 0; i2 < bytes; i2++) {
            os.write(buffer[(bytes - i2) - 1]);
        }
    }

    private void writeStreamData(OutputStream os, int[] w) throws IOException {
        writeNumber(os, 0L, w[0]);
        writeNumber(os, 0L, w[1]);
        writeNumber(os, 65535L, w[2]);
        for (Object entry : this.streamData.values()) {
            if (entry instanceof FreeReference) {
                FreeReference free = (FreeReference) entry;
                writeNumber(os, 0L, w[0]);
                writeNumber(os, free.nextFree, w[1]);
                writeNumber(os, free.nextGenNumber, w[2]);
            } else if (entry instanceof NormalReference) {
                NormalReference ref = (NormalReference) entry;
                writeNumber(os, 1L, w[0]);
                writeNumber(os, ref.offset, w[1]);
                writeNumber(os, ref.genNumber, w[2]);
            } else if (entry instanceof ObjectStreamReference) {
                ObjectStreamReference objStream = (ObjectStreamReference) entry;
                writeNumber(os, 2L, w[0]);
                writeNumber(os, objStream.offset, w[1]);
                writeNumber(os, objStream.objectNumberOfObjectStream, w[2]);
            } else {
                throw new RuntimeException("unexpected reference type");
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdfparser/PDFXRefStream$ObjectStreamReference.class */
    static class ObjectStreamReference {
        long objectNumberOfObjectStream;
        long offset;

        ObjectStreamReference() {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdfparser/PDFXRefStream$NormalReference.class */
    static class NormalReference {
        int genNumber;
        long offset;

        NormalReference() {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdfparser/PDFXRefStream$FreeReference.class */
    static class FreeReference {
        int nextGenNumber;
        long nextFree;

        FreeReference() {
        }
    }

    @Override // org.apache.pdfbox.pdfparser.PDFXRef
    public COSObject getObject(int objectNumber) {
        return null;
    }
}
