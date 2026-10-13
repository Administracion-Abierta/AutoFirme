package com.twelvemonkeys.imageio.metadata.tiff;

import com.ctc.wstx.shaded.msv_core.grammar.util.PossibleNamesCollector;
import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.MetadataReader;
import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.lang.Validate;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/tiff/TIFFReader.class */
public final class TIFFReader extends MetadataReader {
    static final boolean DEBUG = "true".equalsIgnoreCase(System.getProperty("com.twelvemonkeys.imageio.metadata.tiff.debug"));
    private static final Collection<Integer> VALID_TOP_LEVEL_IFDS = Collections.unmodifiableCollection(Arrays.asList(Integer.valueOf(TIFF.TAG_SUB_IFD), Integer.valueOf(TIFF.TAG_EXIF_IFD), Integer.valueOf(TIFF.TAG_GPS_IFD)));
    private static final Map<Integer, Collection<Integer>> VALID_SUB_IFDS = createSubIFDMap();
    private final Set<Long> parsedIFDs = new TreeSet();
    private long inputLength;
    private boolean longOffsets;
    private int offsetSize;

    private static Map<Integer, Collection<Integer>> createSubIFDMap() {
        HashMap<Integer, Collection<Integer>> map = new HashMap<Integer, Collection<Integer>>() { // from class: com.twelvemonkeys.imageio.metadata.tiff.TIFFReader.1
            @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
            public Collection<Integer> get(Object obj) {
                Collection<Integer> collection = (Collection) super.get(obj);
                return collection != null ? collection : Collections.emptySet();
            }
        };
        map.put(Integer.valueOf(TIFF.TAG_SUB_IFD), Collections.singleton(Integer.valueOf(TIFF.TAG_SUB_IFD)));
        map.put(Integer.valueOf(TIFF.TAG_EXIF_IFD), Collections.singleton(Integer.valueOf(TIFF.TAG_INTEROP_IFD)));
        return Collections.unmodifiableMap(map);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    @Override // com.twelvemonkeys.imageio.metadata.MetadataReader
    public Directory read(ImageInputStream imageInputStream) throws IIOException, IOException {
        Validate.notNull(imageInputStream, "input");
        byte[] bArr = new byte[2];
        imageInputStream.readFully(bArr);
        if (bArr[0] == 73 && bArr[1] == 73) {
            imageInputStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        } else if (bArr[0] == 77 && bArr[1] == 77) {
            imageInputStream.setByteOrder(ByteOrder.BIG_ENDIAN);
        } else {
            throw new IIOException(String.format("Invalid TIFF byte order mark '%s', expected: 'II' or 'MM'", StringUtil.decode(bArr, 0, bArr.length, "ASCII")));
        }
        int unsignedShort = imageInputStream.readUnsignedShort();
        if (unsignedShort == 42) {
            this.longOffsets = false;
            this.offsetSize = 4;
        } else if (unsignedShort == 43) {
            this.longOffsets = true;
            this.offsetSize = 8;
            int unsignedShort2 = imageInputStream.readUnsignedShort();
            if (unsignedShort2 != 8) {
                throw new IIOException(String.format("Unexpected BigTIFF offset size: %04x, expected: %04x", Integer.valueOf(unsignedShort2), 8));
            }
            int unsignedShort3 = imageInputStream.readUnsignedShort();
            if (unsignedShort3 != 0) {
                throw new IIOException(String.format("Unexpected BigTIFF padding: %04x, expected: %04x", Integer.valueOf(unsignedShort3), 0));
            }
        } else {
            throw new IIOException(String.format("Wrong TIFF magic in input data: %04x, expected: %04x", Integer.valueOf(unsignedShort), 42));
        }
        this.inputLength = imageInputStream.length();
        return readLinkedIFDs(imageInputStream);
    }

    private TIFFDirectory readLinkedIFDs(ImageInputStream imageInputStream) throws IOException {
        long offset = readOffset(imageInputStream);
        ArrayList arrayList = new ArrayList();
        while (offset != 0) {
            try {
                if ((this.inputLength > 0 && offset >= this.inputLength) || !isValidOffset(imageInputStream, offset) || !this.parsedIFDs.add(Long.valueOf(offset))) {
                    if (DEBUG) {
                        System.err.println("Bad IFD offset: " + offset);
                    }
                    break;
                }
                arrayList.add(readIFD(imageInputStream, offset, VALID_TOP_LEVEL_IFDS));
                offset = readOffset(imageInputStream);
            } catch (EOFException e) {
                offset = 0;
            }
        }
        return new TIFFDirectory(arrayList);
    }

    private long readOffset(ImageInputStream imageInputStream) throws IOException {
        return this.longOffsets ? imageInputStream.readLong() : imageInputStream.readUnsignedInt();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private IFD readIFD(ImageInputStream imageInputStream, long j, Collection<Integer> collection) throws IOException {
        imageInputStream.seek(j);
        long entryCount = readEntryCount(imageInputStream);
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < entryCount; i++) {
            try {
                TIFFEntry entry = readEntry(imageInputStream);
                if (entry != null) {
                    arrayList.add(entry);
                }
            } catch (IIOException e) {
                if (DEBUG) {
                    e.printStackTrace();
                }
            }
        }
        readSubIFDs(imageInputStream, arrayList, collection);
        return new IFD(arrayList);
    }

    private long readEntryCount(ImageInputStream imageInputStream) throws IOException {
        return this.longOffsets ? imageInputStream.readLong() : imageInputStream.readUnsignedShort();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private void readSubIFDs(ImageInputStream imageInputStream, List<TIFFEntry> list, Collection<Integer> collection) throws IOException {
        if (collection == null || collection.isEmpty()) {
            return;
        }
        long streamPosition = imageInputStream.getStreamPosition();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            TIFFEntry tIFFEntry = list.get(i);
            int iIntValue = ((Integer) tIFFEntry.getIdentifier()).intValue();
            if (collection.contains(Integer.valueOf(iIntValue))) {
                try {
                    long[] pointerOffsets = getPointerOffsets(tIFFEntry);
                    ArrayList arrayList = new ArrayList(pointerOffsets.length);
                    int length = pointerOffsets.length;
                    int i2 = 0;
                    while (true) {
                        if (i2 < length) {
                            long j = pointerOffsets[i2];
                            try {
                                if ((this.inputLength > 0 && j >= this.inputLength) || !isValidOffset(imageInputStream, j) || !this.parsedIFDs.add(Long.valueOf(j))) {
                                    if (DEBUG) {
                                        System.err.println("Bad IFD offset: " + j);
                                    }
                                } else {
                                    arrayList.add(readIFD(imageInputStream, j, VALID_SUB_IFDS.get(Integer.valueOf(iIntValue))));
                                    i2++;
                                }
                            } catch (EOFException e) {
                                if (DEBUG) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (arrayList.size() == 1) {
                            list.set(i, new TIFFEntry(iIntValue, tIFFEntry.getType(), arrayList.get(0)));
                        } else if (!arrayList.isEmpty()) {
                            list.set(i, new TIFFEntry(iIntValue, tIFFEntry.getType(), arrayList.toArray(new IFD[0])));
                        }
                    }
                } catch (IIOException e2) {
                    if (DEBUG) {
                        System.err.println("Error parsing sub-IFD: " + iIntValue);
                        e2.printStackTrace();
                    }
                }
            }
        }
        imageInputStream.seek(streamPosition);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private long[] getPointerOffsets(Entry entry) throws IIOException {
        long[] jArr;
        Object value = entry.getValue();
        if (value instanceof Byte) {
            jArr = new long[]{((Byte) value).byteValue() & 255};
        } else if (value instanceof Short) {
            jArr = new long[]{((Short) value).shortValue() & 65535};
        } else if (value instanceof Integer) {
            jArr = new long[]{((long) ((Integer) value).intValue()) & 4294967295L};
        } else if (value instanceof Long) {
            jArr = new long[]{((Long) value).longValue()};
        } else if (value instanceof long[]) {
            jArr = (long[]) value;
        } else {
            Object[] objArr = new Object[1];
            objArr[0] = value != null ? value.getClass() : null;
            throw new IIOException(String.format("Unknown pointer type: %s", objArr));
        }
        return jArr;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private TIFFEntry readEntry(ImageInputStream imageInputStream) throws IOException, IIOException {
        Object valueAt;
        int unsignedShort = imageInputStream.readUnsignedShort();
        short s = imageInputStream.readShort();
        int valueCount = readValueCount(imageInputStream);
        if (valueCount < 0) {
            throw new IIOException(String.format("Illegal count %d for tag %s type %s @%08x", Integer.valueOf(valueCount), Integer.valueOf(unsignedShort), Short.valueOf(s), Long.valueOf(imageInputStream.getStreamPosition())));
        }
        if (!isValidType(s)) {
            imageInputStream.skipBytes(4);
            if (DEBUG) {
                long streamPosition = imageInputStream.getStreamPosition() - 12;
                System.err.printf("Bad TIFF data @%08x\n", Long.valueOf(imageInputStream.getStreamPosition()));
                System.err.println("tagId: " + unsignedShort + (unsignedShort <= 0 ? " (INVALID)" : ""));
                System.err.println("type: " + ((int) s) + " (INVALID)");
                System.err.println("count: " + valueCount);
                imageInputStream.mark();
                try {
                    imageInputStream.seek(streamPosition);
                    byte[] bArr = new byte[8 + Math.min(120, Math.max(24, valueCount))];
                    int i = imageInputStream.read(bArr);
                    System.err.print(HexDump.dump(streamPosition, bArr, 0, i));
                    System.err.println(i < valueCount ? "[...]" : "");
                    return null;
                } finally {
                    imageInputStream.reset();
                }
            }
            return null;
        }
        long valueLength = TIFFEntry.getValueLength(s, valueCount);
        if (valueLength > 0 && valueLength <= this.offsetSize) {
            valueAt = readValueInLine(imageInputStream, s, valueCount);
            imageInputStream.skipBytes(((long) this.offsetSize) - valueLength);
        } else {
            valueAt = readValueAt(imageInputStream, readOffset(imageInputStream), valueLength, s, valueCount);
        }
        return new TIFFEntry(unsignedShort, s, valueAt);
    }

    private boolean isValidType(short s) {
        return s > 0 && s < TIFF.TYPE_LENGTHS.length && TIFF.TYPE_LENGTHS[s] > 0;
    }

    private int readValueCount(ImageInputStream imageInputStream) throws IOException {
        return assertIntCount(this.longOffsets ? imageInputStream.readLong() : imageInputStream.readUnsignedInt());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private int assertIntCount(long j) throws IIOException, IOException {
        if (j > 2147483647L) {
            throw new IIOException(String.format("Unsupported TIFF value count value: %s > Integer.MAX_VALUE", Long.valueOf(j)));
        }
        return (int) j;
    }

    private boolean isValidOffset(ImageInputStream imageInputStream, long j) throws IOException {
        try {
            imageInputStream.mark();
            imageInputStream.seek(j);
            return imageInputStream.read() >= 0;
        } catch (IOException e) {
            return false;
        } finally {
            imageInputStream.reset();
        }
    }

    private boolean isValidLengthAtOffset(ImageInputStream imageInputStream, long j, long j2) throws IOException {
        return (this.inputLength < 0 || this.inputLength >= j + j2) && (j2 < 32767 || isValidOffset(imageInputStream, (j + j2) - 1));
    }

    private Object readValueAt(ImageInputStream imageInputStream, long j, long j2, short s, int i) throws IOException {
        long streamPosition = imageInputStream.getStreamPosition();
        try {
            try {
                imageInputStream.seek(j);
                if (i < Integer.MAX_VALUE && isValidLengthAtOffset(imageInputStream, j, j2)) {
                    Object value = readValue(imageInputStream, s, i, this.longOffsets);
                    imageInputStream.seek(streamPosition);
                    return value;
                }
                Object[] objArr = new Object[3];
                objArr[0] = Long.valueOf(j);
                objArr[1] = Long.valueOf(j2);
                objArr[2] = this.inputLength >= 0 ? this.inputLength + " bytes" : "unknown";
                throw new EOFException(String.format("TIFF value offset or size too large: @%08x/%d bytes (input length: %s)", objArr));
            } catch (EOFException e) {
                if (DEBUG) {
                    System.err.println(e);
                }
                imageInputStream.seek(streamPosition);
                return e;
            }
        } catch (Throwable th) {
            imageInputStream.seek(streamPosition);
            throw th;
        }
    }

    private Object readValueInLine(ImageInputStream imageInputStream, short s, int i) throws IOException {
        return readValue(imageInputStream, s, i, this.longOffsets);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:23:0x00c7  */
    /* JADX WARN: Code duplicated, block: B:33:0x00f5  */
    /* JADX WARN: Code duplicated, block: B:35:0x00ff  */
    /* JADX WARN: Code duplicated, block: B:37:0x0115  */
    /* JADX WARN: Code duplicated, block: B:40:0x0123 A[LOOP:0: B:38:0x011d->B:40:0x0123, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:43:0x0139  */
    /* JADX WARN: Code duplicated, block: B:51:0x0150  */
    /* JADX WARN: Code duplicated, block: B:53:0x015a  */
    /* JADX WARN: Code duplicated, block: B:60:0x0184 A[LOOP:1: B:58:0x017e->B:60:0x0184, LOOP_END] */
    private static Object readValue(ImageInputStream imageInputStream, short s, int i, boolean z) throws IIOException, IOException {
        int[] iArr;
        long[] jArr;
        int i2;
        short[] sArr;
        int[] iArr2;
        int i3;
        long streamPosition = imageInputStream.getStreamPosition();
        switch (s) {
            case 1:
                if (i == 1) {
                    return Integer.valueOf(imageInputStream.readUnsignedByte());
                }
                if (i == 1) {
                    return Byte.valueOf(imageInputStream.readByte());
                }
                byte[] bArr = new byte[i];
                imageInputStream.readFully(bArr);
                return bArr;
            case 2:
                if (i == 0) {
                    return "";
                }
                byte[] bArr2 = new byte[i];
                imageInputStream.readFully(bArr2);
                String[] strArrSplit = new String(bArr2, 0, bArr2[bArr2.length - 1] == 0 ? bArr2.length - 1 : bArr2.length, StandardCharsets.UTF_8).split(PossibleNamesCollector.MAGIC);
                return strArrSplit.length == 1 ? strArrSplit[0] : strArrSplit;
            case 3:
                if (i == 1) {
                    return Integer.valueOf(imageInputStream.readUnsignedShort());
                }
                if (i == 1) {
                    return Short.valueOf(imageInputStream.readShort());
                }
                sArr = new short[i];
                imageInputStream.readFully(sArr, 0, sArr.length);
                if (s == 3) {
                    iArr2 = new int[i];
                    for (i3 = 0; i3 < i; i3++) {
                        iArr2[i3] = sArr[i3] & 65535;
                    }
                    return iArr2;
                }
                return sArr;
            case 4:
            case 13:
                if (i == 1) {
                    return Long.valueOf(imageInputStream.readUnsignedInt());
                }
                if (i == 1) {
                    return Integer.valueOf(imageInputStream.readInt());
                }
                iArr = new int[i];
                imageInputStream.readFully(iArr, 0, iArr.length);
                if (s != 4 || s == 13) {
                    jArr = new long[i];
                    for (i2 = 0; i2 < i; i2++) {
                        jArr[i2] = ((long) iArr[i2]) & 4294967295L;
                    }
                    return jArr;
                }
                return iArr;
            case 5:
                if (i == 1) {
                    return createSafeRational(imageInputStream.readUnsignedInt(), imageInputStream.readUnsignedInt());
                }
                Rational[] rationalArr = new Rational[i];
                for (int i4 = 0; i4 < rationalArr.length; i4++) {
                    rationalArr[i4] = createSafeRational(imageInputStream.readUnsignedInt(), imageInputStream.readUnsignedInt());
                }
                return rationalArr;
            case 6:
                if (i == 1) {
                    return Byte.valueOf(imageInputStream.readByte());
                }
                byte[] bArr3 = new byte[i];
                imageInputStream.readFully(bArr3);
                return bArr3;
            case 7:
                byte[] bArr4 = new byte[i];
                imageInputStream.readFully(bArr4);
                return bArr4;
            case 8:
                if (i == 1) {
                    return Short.valueOf(imageInputStream.readShort());
                }
                sArr = new short[i];
                imageInputStream.readFully(sArr, 0, sArr.length);
                if (s == 3) {
                    iArr2 = new int[i];
                    while (i3 < i) {
                        iArr2[i3] = sArr[i3] & 65535;
                    }
                    return iArr2;
                }
                return sArr;
            case 9:
                if (i == 1) {
                    return Integer.valueOf(imageInputStream.readInt());
                }
                iArr = new int[i];
                imageInputStream.readFully(iArr, 0, iArr.length);
                if (s != 4) {
                    break;
                }
                jArr = new long[i];
                while (i2 < i) {
                    jArr[i2] = ((long) iArr[i2]) & 4294967295L;
                }
                return jArr;
            case 10:
                if (i == 1) {
                    return createSafeRational(imageInputStream.readInt(), imageInputStream.readInt());
                }
                Rational[] rationalArr2 = new Rational[i];
                for (int i5 = 0; i5 < rationalArr2.length; i5++) {
                    rationalArr2[i5] = createSafeRational(imageInputStream.readInt(), imageInputStream.readInt());
                }
                return rationalArr2;
            case 11:
                if (i == 1) {
                    return Float.valueOf(imageInputStream.readFloat());
                }
                float[] fArr = new float[i];
                imageInputStream.readFully(fArr, 0, fArr.length);
                return fArr;
            case 12:
                if (i == 1) {
                    return Double.valueOf(imageInputStream.readDouble());
                }
                double[] dArr = new double[i];
                imageInputStream.readFully(dArr, 0, dArr.length);
                return dArr;
            case 14:
            case 15:
            default:
                return new Unknown(s, i, streamPosition);
            case 16:
            case 17:
            case 18:
                if (z) {
                    if (i == 1) {
                        long j = imageInputStream.readLong();
                        if (s != 17 && j < 0) {
                            throw new IIOException(String.format("Value > %s", Long.MAX_VALUE));
                        }
                        return Long.valueOf(j);
                    }
                    long[] jArr2 = new long[i];
                    for (int i6 = 0; i6 < i; i6++) {
                        jArr2[i6] = imageInputStream.readLong();
                    }
                    return jArr2;
                }
                return new Unknown(s, i, streamPosition);
        }
    }

    private static Rational createSafeRational(long j, long j2) {
        if (j2 == 0) {
            return Rational.NaN;
        }
        return new Rational(j, j2);
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x010d */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void main(java.lang.String[] r8) throws java.io.IOException {
        /*
            Method dump skipped, instruction units count: 309
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.twelvemonkeys.imageio.metadata.tiff.TIFFReader.main(java.lang.String[]):void");
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/tiff/TIFFReader$HexDump.class */
    public static class HexDump {
        private static final int WIDTH = 32;

        private HexDump() {
        }

        public static String dump(byte[] bArr) {
            return dump(0L, bArr, 0, bArr.length);
        }

        public static String dump(long j, byte[] bArr, int i, int i2) {
            StringBuilder sb = new StringBuilder();
            for (int i3 = 0; i3 < i2; i3++) {
                if (i3 % 32 == 0) {
                    if (i3 > 0) {
                        sb.append("\n");
                    }
                    sb.append(String.format("%08x: ", Long.valueOf(((long) (i3 + i)) + j)));
                } else if (i3 > 0 && i3 % 2 == 0) {
                    sb.append(" ");
                }
                sb.append(String.format("%02x", Byte.valueOf(bArr[i3 + i])));
                int i4 = i3 + 1;
                if (i4 % 32 == 0 || i4 == i2) {
                    int i5 = (32 - (i4 % 32)) % 32;
                    if (i5 != 0) {
                        int i6 = i5 / 2;
                        if (i2 % 2 != 0) {
                            sb.append("  ");
                        }
                        for (int i7 = 0; i7 < i6; i7++) {
                            sb.append("     ");
                        }
                    }
                    sb.append("  ");
                    sb.append(toAsciiString(bArr, (i4 - (32 - i5)) + i, i4 + i));
                }
            }
            return sb.toString();
        }

        private static String toAsciiString(byte[] bArr, int i, int i2) {
            byte[] bArrCopyOfRange = Arrays.copyOfRange(bArr, i, i2);
            for (int i3 = 0; i3 < bArrCopyOfRange.length; i3++) {
                if (bArrCopyOfRange[i3] < 32 || bArrCopyOfRange[i3] > 126) {
                    bArrCopyOfRange[i3] = 46;
                }
            }
            return new String(bArrCopyOfRange, StandardCharsets.US_ASCII);
        }
    }
}
