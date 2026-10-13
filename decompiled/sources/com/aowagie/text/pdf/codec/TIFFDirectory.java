package com.aowagie.text.pdf.codec;

import com.aowagie.text.pdf.RandomAccessFileOrArray;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/codec/TIFFDirectory.class */
class TIFFDirectory implements Serializable {
    private static final long serialVersionUID = -168636766193675380L;
    private boolean isBigEndian;
    private int numEntries;
    private TIFFField[] fields;
    private final Hashtable fieldIndex = new Hashtable();
    private long IFDOffset = 8;
    private long nextIFDOffset = 0;
    private static final int[] sizeOfType = {0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8};

    TIFFDirectory() {
    }

    private static boolean isValidEndianTag(int endian) {
        return endian == 18761 || endian == 19789;
    }

    TIFFDirectory(RandomAccessFileOrArray stream, int directory) throws IOException {
        long global_save_offset = stream.getFilePointer();
        stream.seek(0L);
        int endian = stream.readUnsignedShort();
        if (!isValidEndianTag(endian)) {
            throw new IllegalArgumentException("Bad endianness tag (not 0x4949 or 0x4d4d).");
        }
        this.isBigEndian = endian == 19789;
        int magic = readUnsignedShort(stream);
        if (magic != 42) {
            throw new IllegalArgumentException("Bad magic number, should be 42.");
        }
        long ifd_offset = readUnsignedInt(stream);
        for (int i = 0; i < directory; i++) {
            if (ifd_offset == 0) {
                throw new IllegalArgumentException("Directory number too large.");
            }
            stream.seek(ifd_offset);
            int entries = readUnsignedShort(stream);
            stream.skip(12 * entries);
            ifd_offset = readUnsignedInt(stream);
        }
        stream.seek(ifd_offset);
        initialize(stream);
        stream.seek(global_save_offset);
    }

    private void initialize(RandomAccessFileOrArray stream) throws IOException {
        long nextTagOffset = 0;
        long maxOffset = stream.length();
        this.IFDOffset = stream.getFilePointer();
        this.numEntries = readUnsignedShort(stream);
        this.fields = new TIFFField[this.numEntries];
        for (int i = 0; i < this.numEntries && nextTagOffset < maxOffset; i++) {
            int tag = readUnsignedShort(stream);
            int type = readUnsignedShort(stream);
            int count = (int) readUnsignedInt(stream);
            boolean processTag = true;
            nextTagOffset = stream.getFilePointer() + 4;
            try {
                if (count * sizeOfType[type] > 4) {
                    long valueOffset = readUnsignedInt(stream);
                    if (valueOffset < maxOffset) {
                        stream.seek(valueOffset);
                    } else {
                        processTag = false;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                processTag = false;
            }
            if (processTag) {
                this.fieldIndex.put(Integer.valueOf(tag), Integer.valueOf(i));
                Object obj = null;
                switch (type) {
                    case 1:
                    case 2:
                    case 6:
                    case 7:
                        byte[] bvalues = new byte[count];
                        stream.readFully(bvalues, 0, count);
                        if (type == 2) {
                            int index = 0;
                            int prevIndex = 0;
                            ArrayList v = new ArrayList();
                            while (index < count) {
                                while (index < count) {
                                    int i2 = index;
                                    index++;
                                    if (bvalues[i2] == 0) {
                                    }
                                    break;
                                }
                                v.add(new String(bvalues, prevIndex, index - prevIndex));
                                prevIndex = index;
                            }
                            count = v.size();
                            String[] strings = new String[count];
                            for (int c = 0; c < count; c++) {
                                strings[c] = (String) v.get(c);
                            }
                            obj = strings;
                        } else {
                            obj = bvalues;
                        }
                        break;
                    case 3:
                        char[] cvalues = new char[count];
                        for (int j = 0; j < count; j++) {
                            cvalues[j] = (char) readUnsignedShort(stream);
                        }
                        obj = cvalues;
                        break;
                    case 4:
                        long[] lvalues = new long[count];
                        for (int j2 = 0; j2 < count; j2++) {
                            lvalues[j2] = readUnsignedInt(stream);
                        }
                        obj = lvalues;
                        break;
                    case 5:
                        long[][] llvalues = new long[count][2];
                        for (int j3 = 0; j3 < count; j3++) {
                            llvalues[j3][0] = readUnsignedInt(stream);
                            llvalues[j3][1] = readUnsignedInt(stream);
                        }
                        obj = llvalues;
                        break;
                    case 8:
                        short[] svalues = new short[count];
                        for (int j4 = 0; j4 < count; j4++) {
                            svalues[j4] = readShort(stream);
                        }
                        obj = svalues;
                        break;
                    case 9:
                        int[] ivalues = new int[count];
                        for (int j5 = 0; j5 < count; j5++) {
                            ivalues[j5] = readInt(stream);
                        }
                        obj = ivalues;
                        break;
                    case 10:
                        int[][] iivalues = new int[count][2];
                        for (int j6 = 0; j6 < count; j6++) {
                            iivalues[j6][0] = readInt(stream);
                            iivalues[j6][1] = readInt(stream);
                        }
                        obj = iivalues;
                        break;
                    case 11:
                        float[] fvalues = new float[count];
                        for (int j7 = 0; j7 < count; j7++) {
                            fvalues[j7] = readFloat(stream);
                        }
                        obj = fvalues;
                        break;
                    case 12:
                        double[] dvalues = new double[count];
                        for (int j8 = 0; j8 < count; j8++) {
                            dvalues[j8] = readDouble(stream);
                        }
                        obj = dvalues;
                        break;
                }
                this.fields[i] = new TIFFField(tag, type, count, obj);
            }
            stream.seek(nextTagOffset);
        }
        try {
            this.nextIFDOffset = readUnsignedInt(stream);
        } catch (Exception e2) {
            this.nextIFDOffset = 0L;
        }
    }

    public int getNumEntries() {
        return this.numEntries;
    }

    TIFFField getField(int tag) {
        Integer i = (Integer) this.fieldIndex.get(Integer.valueOf(tag));
        if (i == null) {
            return null;
        }
        return this.fields[i.intValue()];
    }

    boolean isTagPresent(int tag) {
        return this.fieldIndex.containsKey(Integer.valueOf(tag));
    }

    public int[] getTags() {
        int[] tags = new int[this.fieldIndex.size()];
        Enumeration e = this.fieldIndex.keys();
        int i = 0;
        while (e.hasMoreElements()) {
            int i2 = i;
            i++;
            tags[i2] = ((Integer) e.nextElement()).intValue();
        }
        return tags;
    }

    public TIFFField[] getFields() {
        return this.fields;
    }

    private byte getFieldAsByte(int tag, int index) {
        Integer i = (Integer) this.fieldIndex.get(Integer.valueOf(tag));
        byte[] b = this.fields[i.intValue()].getAsBytes();
        return b[index];
    }

    private long getFieldAsLong(int tag, int index) {
        Integer i = (Integer) this.fieldIndex.get(Integer.valueOf(tag));
        return this.fields[i.intValue()].getAsLong(index);
    }

    long getFieldAsLong(int tag) {
        return getFieldAsLong(tag, 0);
    }

    private float getFieldAsFloat(int tag, int index) {
        Integer i = (Integer) this.fieldIndex.get(Integer.valueOf(tag));
        return this.fields[i.intValue()].getAsFloat(index);
    }

    private double getFieldAsDouble(int tag, int index) {
        Integer i = (Integer) this.fieldIndex.get(Integer.valueOf(tag));
        return this.fields[i.intValue()].getAsDouble(index);
    }

    private short readShort(RandomAccessFileOrArray stream) throws IOException {
        if (this.isBigEndian) {
            return stream.readShort();
        }
        return stream.readShortLE();
    }

    private int readUnsignedShort(RandomAccessFileOrArray stream) throws IOException {
        if (this.isBigEndian) {
            return stream.readUnsignedShort();
        }
        return stream.readUnsignedShortLE();
    }

    private int readInt(RandomAccessFileOrArray stream) throws IOException {
        if (this.isBigEndian) {
            return stream.readInt();
        }
        return stream.readIntLE();
    }

    private long readUnsignedInt(RandomAccessFileOrArray stream) throws IOException {
        if (this.isBigEndian) {
            return stream.readUnsignedInt();
        }
        return stream.readUnsignedIntLE();
    }

    private long readLong(RandomAccessFileOrArray stream) throws IOException {
        if (this.isBigEndian) {
            return stream.readLong();
        }
        return stream.readLongLE();
    }

    private float readFloat(RandomAccessFileOrArray stream) throws IOException {
        if (this.isBigEndian) {
            return stream.readFloat();
        }
        return stream.readFloatLE();
    }

    private double readDouble(RandomAccessFileOrArray stream) throws IOException {
        if (this.isBigEndian) {
            return stream.readDouble();
        }
        return stream.readDoubleLE();
    }

    private static int readUnsignedShort(RandomAccessFileOrArray stream, boolean isBigEndian) throws IOException {
        if (isBigEndian) {
            return stream.readUnsignedShort();
        }
        return stream.readUnsignedShortLE();
    }

    private static long readUnsignedInt(RandomAccessFileOrArray stream, boolean isBigEndian) throws IOException {
        if (isBigEndian) {
            return stream.readUnsignedInt();
        }
        return stream.readUnsignedIntLE();
    }

    public boolean isBigEndian() {
        return this.isBigEndian;
    }

    public long getIFDOffset() {
        return this.IFDOffset;
    }

    public long getNextIFDOffset() {
        return this.nextIFDOffset;
    }
}
