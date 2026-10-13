package com.twelvemonkeys.imageio.metadata.tiff;

import com.twelvemonkeys.imageio.metadata.CompoundDirectory;
import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.MetadataWriter;
import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageOutputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/tiff/TIFFWriter.class */
public final class TIFFWriter extends MetadataWriter {
    private static final int WORD_LENGTH = 2;
    private static final int LONGWORD_LENGTH = 4;
    private final boolean longOffsets;
    private final int offsetSize;
    private final long entryLength;
    private final int directoryCountLength;

    public TIFFWriter() {
        this(4);
    }

    public TIFFWriter(int i) {
        this.offsetSize = ((Integer) Validate.isTrue(i == 4 || i == 8, Integer.valueOf(i), "offsetSize must be 4 for TIFF or 8 for BigTIFF")).intValue();
        this.longOffsets = i == 8;
        this.directoryCountLength = this.longOffsets ? 8 : 2;
        this.entryLength = 4 + (2 * i);
    }

    public boolean write(Collection<? extends Entry> collection, ImageOutputStream imageOutputStream) throws IOException {
        return write(new IFD(collection), imageOutputStream);
    }

    @Override // com.twelvemonkeys.imageio.metadata.MetadataWriter
    public boolean write(Directory directory, ImageOutputStream imageOutputStream) throws IOException {
        Validate.notNull(directory);
        Validate.notNull(imageOutputStream);
        writeTIFFHeader(imageOutputStream);
        if (directory instanceof CompoundDirectory) {
            CompoundDirectory compoundDirectory = (CompoundDirectory) directory;
            for (int i = 0; i < compoundDirectory.directoryCount(); i++) {
                writeIFD(compoundDirectory.getDirectory(i), imageOutputStream, false);
            }
        } else {
            writeIFD(directory, imageOutputStream, false);
        }
        writeOffset(imageOutputStream, 0L);
        return true;
    }

    public void writeTIFFHeader(ImageOutputStream imageOutputStream) throws IOException {
        imageOutputStream.writeShort(imageOutputStream.getByteOrder() == ByteOrder.BIG_ENDIAN ? TIFF.BYTE_ORDER_MARK_BIG_ENDIAN : TIFF.BYTE_ORDER_MARK_LITTLE_ENDIAN);
        imageOutputStream.writeShort(this.longOffsets ? 43 : 42);
        if (this.longOffsets) {
            imageOutputStream.writeShort(this.offsetSize);
            imageOutputStream.writeShort(0);
        }
    }

    public long writeIFD(Collection<Entry> collection, ImageOutputStream imageOutputStream) throws IOException {
        Validate.notNull(collection);
        Validate.notNull(imageOutputStream);
        return writeIFD(new IFD(collection), imageOutputStream, false);
    }

    private long writeIFD(Directory directory, ImageOutputStream imageOutputStream, boolean z) throws IOException {
        long size;
        Directory directoryEnsureOrderedDirectory = ensureOrderedDirectory(directory);
        long streamPosition = imageOutputStream.getStreamPosition();
        long streamPosition2 = imageOutputStream.getStreamPosition() + computeDataSize(directoryEnsureOrderedDirectory) + ((long) this.offsetSize);
        if (!z) {
            writeOffset(imageOutputStream, streamPosition2);
            size = streamPosition + ((long) this.offsetSize);
            imageOutputStream.seek(streamPosition2);
        } else {
            size = streamPosition + ((long) this.directoryCountLength) + (((long) directoryEnsureOrderedDirectory.size()) * this.entryLength);
        }
        writeDirectoryCount(imageOutputStream, directoryEnsureOrderedDirectory.size());
        for (Entry entry : directoryEnsureOrderedDirectory) {
            imageOutputStream.writeShort(((Integer) entry.getIdentifier()).intValue());
            imageOutputStream.writeShort(TIFFEntry.getType(entry));
            writeValueCount(imageOutputStream, getCount(entry));
            Object value = entry.getValue();
            if (value instanceof Directory) {
                if (value instanceof CompoundDirectory) {
                    throw new AssertionError("SubIFD cannot contain linked IFDs");
                }
                long streamPosition3 = imageOutputStream.getStreamPosition() + ((long) this.offsetSize);
                writeValueInline(Long.valueOf(size), TIFFEntry.getType(entry), imageOutputStream);
                imageOutputStream.seek(size);
                Directory directory2 = (Directory) value;
                writeIFD(directory2, imageOutputStream, true);
                size += computeDataSize(directory2);
                imageOutputStream.seek(streamPosition3);
            } else {
                size += writeValue(entry, size, imageOutputStream);
            }
        }
        return streamPosition2;
    }

    private void writeDirectoryCount(ImageOutputStream imageOutputStream, int i) throws IOException {
        if (this.longOffsets) {
            imageOutputStream.writeLong(i);
        } else {
            imageOutputStream.writeShort(i);
        }
    }

    private void writeValueCount(ImageOutputStream imageOutputStream, int i) throws IOException {
        if (this.longOffsets) {
            imageOutputStream.writeLong(i);
        } else {
            imageOutputStream.writeInt(i);
        }
    }

    public long computeIFDSize(Collection<? extends Entry> collection) {
        return ((long) this.directoryCountLength) + computeDataSize(new IFD(collection)) + (((long) collection.size()) * this.entryLength);
    }

    private long computeDataSize(Directory directory) {
        long jComputeDataSize = 0;
        for (Entry entry : directory) {
            long valueLength = TIFFEntry.getValueLength(TIFFEntry.getType(entry), getCount(entry));
            if (valueLength < 0) {
                throw new IllegalArgumentException(String.format("Unknown size for entry %s", entry));
            }
            if (valueLength > this.offsetSize) {
                jComputeDataSize += valueLength;
            }
            if (entry.getValue() instanceof Directory) {
                Directory directory2 = (Directory) entry.getValue();
                jComputeDataSize += ((long) this.directoryCountLength) + computeDataSize(directory2) + (((long) directory2.size()) * this.entryLength);
            }
        }
        return jComputeDataSize;
    }

    private Directory ensureOrderedDirectory(Directory directory) {
        if (!isSorted(directory)) {
            ArrayList arrayList = new ArrayList(directory.size());
            Iterator<Entry> it = directory.iterator();
            while (it.hasNext()) {
                arrayList.add(it.next());
            }
            Collections.sort(arrayList, new Comparator<Entry>() { // from class: com.twelvemonkeys.imageio.metadata.tiff.TIFFWriter.1
                @Override // java.util.Comparator
                public int compare(Entry entry, Entry entry2) {
                    return ((Integer) entry.getIdentifier()).intValue() - ((Integer) entry2.getIdentifier()).intValue();
                }
            });
            return new IFD(arrayList);
        }
        return directory;
    }

    private boolean isSorted(Directory directory) {
        int i = 0;
        Iterator<Entry> it = directory.iterator();
        while (it.hasNext()) {
            int iIntValue = ((Integer) it.next().getIdentifier()).intValue() & 65535;
            if (iIntValue < i) {
                return false;
            }
            i = iIntValue;
        }
        return true;
    }

    private long writeValue(Entry entry, long j, ImageOutputStream imageOutputStream) throws IOException {
        short type = TIFFEntry.getType(entry);
        long valueLength = TIFFEntry.getValueLength(type, getCount(entry));
        if (valueLength <= this.offsetSize) {
            writeValueInline(entry.getValue(), type, imageOutputStream);
            long j2 = valueLength;
            while (true) {
                long j3 = j2;
                if (j3 < this.offsetSize) {
                    imageOutputStream.write(0);
                    j2 = j3 + 1;
                } else {
                    return 0L;
                }
            }
        } else {
            writeValueAt(j, entry.getValue(), type, imageOutputStream);
            return valueLength;
        }
    }

    private int getCount(Entry entry) {
        Object value = entry.getValue();
        if (value instanceof String) {
            return computeStringLength((String) value);
        }
        if (value instanceof String[]) {
            return computeStringLength((String[]) value);
        }
        return entry.valueCount();
    }

    private int computeStringLength(String... strArr) {
        int length = 0;
        for (String str : strArr) {
            length += str.getBytes(StandardCharsets.UTF_8).length + 1;
        }
        return length;
    }

    private void writeValueInline(Object obj, short s, ImageOutputStream imageOutputStream) throws IOException {
        int[] iArr;
        short[] sArr;
        if (obj.getClass().isArray()) {
            switch (s) {
                case 1:
                case 6:
                case 7:
                    imageOutputStream.write((byte[]) obj);
                    return;
                case 2:
                    break;
                case 3:
                case 8:
                    if (obj instanceof short[]) {
                        sArr = (short[]) obj;
                    } else if (obj instanceof int[]) {
                        int[] iArr2 = (int[]) obj;
                        sArr = new short[iArr2.length];
                        for (int i = 0; i < iArr2.length; i++) {
                            sArr[i] = (short) iArr2[i];
                        }
                    } else if (obj instanceof long[]) {
                        long[] jArr = (long[]) obj;
                        sArr = new short[jArr.length];
                        for (int i2 = 0; i2 < jArr.length; i2++) {
                            sArr[i2] = (short) jArr[i2];
                        }
                    } else {
                        throw new IllegalArgumentException("Unsupported type for TIFF SHORT: " + obj.getClass());
                    }
                    imageOutputStream.writeShorts(sArr, 0, sArr.length);
                    return;
                case 4:
                case 9:
                    if (obj instanceof int[]) {
                        iArr = (int[]) obj;
                    } else if (obj instanceof long[]) {
                        long[] jArr2 = (long[]) obj;
                        iArr = new int[jArr2.length];
                        for (int i3 = 0; i3 < jArr2.length; i3++) {
                            iArr[i3] = (int) jArr2[i3];
                        }
                    } else {
                        throw new IllegalArgumentException("Unsupported type for TIFF LONG: " + obj.getClass());
                    }
                    imageOutputStream.writeInts(iArr, 0, iArr.length);
                    return;
                case 5:
                case 10:
                    for (Rational rational : (Rational[]) obj) {
                        imageOutputStream.writeInt((int) rational.numerator());
                        imageOutputStream.writeInt((int) rational.denominator());
                    }
                    return;
                case 11:
                    if (obj instanceof float[]) {
                        float[] fArr = (float[]) obj;
                        imageOutputStream.writeFloats(fArr, 0, fArr.length);
                        return;
                    }
                    throw new IllegalArgumentException("Unsupported type for TIFF FLOAT: " + obj.getClass());
                case 12:
                    if (obj instanceof double[]) {
                        double[] dArr = (double[]) obj;
                        imageOutputStream.writeDoubles(dArr, 0, dArr.length);
                        return;
                    }
                    throw new IllegalArgumentException("Unsupported type for TIFF DOUBLE: " + obj.getClass());
                case 13:
                case 14:
                case 15:
                default:
                    throw new IllegalArgumentException("Unsupported TIFF type: " + ((int) s));
                case 16:
                case 17:
                    if (this.longOffsets) {
                        if (obj instanceof long[]) {
                            long[] jArr3 = (long[]) obj;
                            imageOutputStream.writeLongs(jArr3, 0, jArr3.length);
                            return;
                        }
                        throw new IllegalArgumentException("Unsupported type for TIFF LONG8: " + obj.getClass());
                    }
                    break;
            }
            writeStrings(imageOutputStream, (String[]) obj);
            return;
        }
        switch (s) {
            case 1:
            case 6:
            case 7:
                imageOutputStream.writeByte(((Number) obj).intValue());
                return;
            case 2:
                writeStrings(imageOutputStream, (String) obj);
                return;
            case 3:
            case 8:
                imageOutputStream.writeShort(((Number) obj).intValue());
                return;
            case 4:
            case 9:
            case 13:
                imageOutputStream.writeInt(((Number) obj).intValue());
                return;
            case 5:
            case 10:
                Rational rational2 = (Rational) obj;
                imageOutputStream.writeInt((int) rational2.numerator());
                imageOutputStream.writeInt((int) rational2.denominator());
                return;
            case 11:
                imageOutputStream.writeFloat(((Number) obj).floatValue());
                return;
            case 12:
                imageOutputStream.writeDouble(((Number) obj).doubleValue());
                return;
            case 16:
            case 17:
            case 18:
                if (this.longOffsets) {
                    imageOutputStream.writeLong(((Number) obj).longValue());
                    return;
                }
                break;
        }
        throw new IllegalArgumentException("Unsupported TIFF type: " + ((int) s));
    }

    private void writeStrings(ImageOutputStream imageOutputStream, String... strArr) throws IOException {
        for (String str : strArr) {
            imageOutputStream.write(str.getBytes(StandardCharsets.UTF_8));
            imageOutputStream.write(0);
        }
    }

    private void writeValueAt(long j, Object obj, short s, ImageOutputStream imageOutputStream) throws IOException {
        writeOffset(imageOutputStream, j);
        long streamPosition = imageOutputStream.getStreamPosition();
        imageOutputStream.seek(j);
        writeValueInline(obj, s, imageOutputStream);
        imageOutputStream.seek(streamPosition);
    }

    public void writeOffset(ImageOutputStream imageOutputStream, long j) throws IOException {
        if (this.longOffsets) {
            imageOutputStream.writeLong(assertLongOffset(j));
        } else {
            imageOutputStream.writeInt(assertIntegerOffset(j));
        }
    }

    public int offsetSize() {
        return this.offsetSize;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private int assertIntegerOffset(long j) throws IIOException {
        if (j < 0 || j > 4294967295L) {
            throw new IIOException("Integer overflow for TIFF stream");
        }
        return (int) j;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private long assertLongOffset(long j) throws IIOException {
        if (j < 0) {
            throw new IIOException("Long overflow for BigTIFF stream");
        }
        return j;
    }
}
