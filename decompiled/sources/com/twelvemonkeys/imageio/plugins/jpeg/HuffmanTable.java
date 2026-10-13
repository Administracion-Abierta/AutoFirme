package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.metadata.jpeg.JPEG;
import java.io.DataInput;
import java.io.IOException;
import javax.imageio.IIOException;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/HuffmanTable.class */
final class HuffmanTable extends Segment {
    private final short[][][] l;
    private final short[][][][] v;
    private final boolean[][] tc;
    private static final int MSB = Integer.MIN_VALUE;

    private HuffmanTable() {
        super(JPEG.DHT);
        this.l = new short[4][2][16];
        this.v = new short[4][2][16][200];
        this.tc = new boolean[4][2];
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    void buildHuffTables(int[][][] iArr) throws IIOException, IOException {
        for (int i = 0; i < 4; i++) {
            for (int i2 = 0; i2 < 2; i2++) {
                if (this.tc[i][i2]) {
                    buildHuffTable(iArr[i][i2], this.l[i][i2], this.v[i][i2]);
                }
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private void buildHuffTable(int[] iArr, short[] sArr, short[][] sArr2) throws IIOException, IOException {
        int i = 0;
        for (int i2 = 0; i2 < 8; i2++) {
            for (int i3 = 0; i3 < sArr[i2]; i3++) {
                for (int i4 = 0; i4 < (256 >> (i2 + 1)); i4++) {
                    iArr[i] = sArr2[i2][i3] | ((i2 + 1) << 8);
                    i++;
                }
            }
        }
        int i5 = 1;
        while (i < 256) {
            iArr[i] = i5 | Integer.MIN_VALUE;
            i5++;
            i++;
        }
        int i6 = 1;
        int i7 = 0;
        for (int i8 = 8; i8 < 16; i8++) {
            for (int i9 = 0; i9 < sArr[i8]; i9++) {
                for (int i10 = 0; i10 < (256 >> (i8 - 7)); i10++) {
                    iArr[(i6 * 256) + i7] = sArr2[i8][i9] | ((i8 + 1) << 8);
                    i7++;
                }
                if (i7 >= 256) {
                    if (i7 > 256) {
                        throw new IIOException("JPEG Huffman Table error");
                    }
                    i7 = 0;
                    i6++;
                }
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("DHT[");
        for (int i = 0; i < this.tc.length; i++) {
            for (int i2 = 0; i2 < this.tc[i].length; i2++) {
                if (this.tc[i][i2]) {
                    if (sb.length() > 4) {
                        sb.append(", ");
                    }
                    sb.append("id: ");
                    sb.append(i);
                    sb.append(", class: ");
                    sb.append(i2);
                }
            }
        }
        sb.append(']');
        return sb.toString();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    public static Segment read(DataInput dataInput, int i) throws IOException, IIOException {
        int i2 = 2;
        HuffmanTable huffmanTable = new HuffmanTable();
        while (i2 < i) {
            int unsignedByte = dataInput.readUnsignedByte();
            i2++;
            int i3 = unsignedByte & 15;
            if (i3 > 3) {
                throw new IIOException("Unexpected JPEG Huffman Table Id (> 3):" + i3);
            }
            int i4 = unsignedByte >> 4;
            if (i4 > 1) {
                throw new IIOException("Unexpected JPEG Huffman Table class (> 1): " + i4);
            }
            huffmanTable.tc[i3][i4] = true;
            for (int i5 = 0; i5 < 16; i5++) {
                huffmanTable.l[i3][i4][i5] = (short) dataInput.readUnsignedByte();
                i2++;
            }
            for (int i6 = 0; i6 < 16; i6++) {
                for (int i7 = 0; i7 < huffmanTable.l[i3][i4][i6]; i7++) {
                    if (i2 > i) {
                        throw new IIOException("JPEG Huffman Table format error");
                    }
                    huffmanTable.v[i3][i4][i6][i7] = (short) dataInput.readUnsignedByte();
                    i2++;
                }
            }
        }
        if (i2 != i) {
            throw new IIOException("JPEG Huffman Table format error, bad segment length: " + i);
        }
        return huffmanTable;
    }

    public boolean isPresent(int i, int i2) {
        return this.tc[i][i2];
    }

    private short[] lengths(int i, int i2) {
        return this.l[i][i2];
    }

    private short[] tables(int i, int i2) {
        short[] sArrLengths = lengths(i, i2);
        int i3 = 0;
        for (short s : sArrLengths) {
            i3 += s;
        }
        short[] sArr = new short[i3];
        int i4 = 0;
        for (int i5 = 0; i5 < 16; i5++) {
            short[] sArr2 = this.v[i][i2][i5];
            short s2 = sArrLengths[i5];
            System.arraycopy(sArr2, 0, sArr, i4, s2);
            i4 += s2;
        }
        return sArr;
    }

    JPEGHuffmanTable toNativeTable(int i, int i2) {
        return new JPEGHuffmanTable(lengths(i, i2), tables(i, i2));
    }
}
