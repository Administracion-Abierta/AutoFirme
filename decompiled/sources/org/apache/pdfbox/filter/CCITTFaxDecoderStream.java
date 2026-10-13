package org.apache.pdfbox.filter;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/filter/CCITTFaxDecoderStream.class */
final class CCITTFaxDecoderStream extends FilterInputStream {
    private final int columns;
    private final byte[] decodedRow;
    private final boolean optionG32D;
    private final boolean optionG3Fill;
    private final boolean optionUncompressed;
    private final boolean optionByteAligned;
    private final int type;
    private int decodedLength;
    private int decodedPos;
    private int[] changesReferenceRow;
    private int[] changesCurrentRow;
    private int changesReferenceRowCount;
    private int changesCurrentRowCount;
    private int lastChangingElement;
    int buffer;
    int bufferPos;
    static final short[][] BLACK_CODES = {new short[]{2, 3}, new short[]{2, 3}, new short[]{2, 3}, new short[]{3}, new short[]{4, 5}, new short[]{4, 5, 7}, new short[]{4, 7}, new short[]{24}, new short[]{23, 24, 55, 8, 15}, new short[]{23, 24, 40, 55, 103, 104, 108, 8, 12, 13}, new short[]{18, 19, 20, 21, 22, 23, 28, 29, 30, 31, 36, 39, 40, 43, 44, 51, 52, 53, 55, 56, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 200, 201, 202, 203, 204, 205, 210, 211, 212, 213, 214, 215, 218, 219}, new short[]{74, 75, 76, 77, 82, 83, 84, 85, 90, 91, 100, 101, 108, 109, 114, 115, 116, 117, 118, 119}};
    static final short[][] BLACK_RUN_LENGTHS = {new short[]{3, 2}, new short[]{1, 4}, new short[]{6, 5}, new short[]{7}, new short[]{9, 8}, new short[]{10, 11, 12}, new short[]{13, 14}, new short[]{15}, new short[]{16, 17, 0, 18, 64}, new short[]{24, 25, 23, 22, 19, 20, 21, 1792, 1856, 1920}, new short[]{1984, 2048, 2112, 2176, 2240, 2304, 2368, 2432, 2496, 2560, 52, 55, 56, 59, 60, 320, 384, 448, 53, 54, 50, 51, 44, 45, 46, 47, 57, 58, 61, 256, 48, 49, 62, 63, 30, 31, 32, 33, 40, 41, 128, 192, 26, 27, 28, 29, 34, 35, 36, 37, 38, 39, 42, 43}, new short[]{640, 704, 768, 832, 1280, 1344, 1408, 1472, 1536, 1600, 1664, 1728, 512, 576, 896, 960, 1024, 1088, 1152, 1216}};
    public static final short[][] WHITE_CODES = {new short[]{7, 8, 11, 12, 14, 15}, new short[]{18, 19, 20, 27, 7, 8}, new short[]{23, 24, 42, 43, 3, 52, 53, 7, 8}, new short[]{19, 23, 24, 36, 39, 40, 43, 3, 55, 4, 8, 12}, new short[]{18, 19, 20, 21, 22, 23, 26, 27, 2, 36, 37, 40, 41, 42, 43, 44, 45, 3, 50, 51, 52, 53, 54, 55, 4, 74, 75, 5, 82, 83, 84, 85, 88, 89, 90, 91, 100, 101, 103, 104, 10, 11}, new short[]{152, 153, 154, 155, 204, 205, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219}, new short[0], new short[]{8, 12, 13}, new short[]{18, 19, 20, 21, 22, 23, 28, 29, 30, 31}};
    public static final short[][] WHITE_RUN_LENGTHS = {new short[]{2, 3, 4, 5, 6, 7}, new short[]{128, 8, 9, 64, 10, 11}, new short[]{192, 1664, 16, 17, 13, 14, 15, 1, 12}, new short[]{26, 21, 28, 27, 18, 24, 25, 22, 256, 23, 20, 19}, new short[]{33, 34, 35, 36, 37, 38, 31, 32, 29, 53, 54, 39, 40, 41, 42, 43, 44, 30, 61, 62, 63, 0, 320, 384, 45, 59, 60, 46, 49, 50, 51, 52, 55, 56, 57, 58, 448, 512, 640, 576, 47, 48}, new short[]{1472, 1536, 1600, 1728, 704, 768, 832, 896, 960, 1024, 1088, 1152, 1216, 1280, 1344, 1408}, new short[0], new short[]{1792, 1856, 1920}, new short[]{1984, 2048, 2112, 2176, 2240, 2304, 2368, 2432, 2496, 2560}};
    static final Node EOL = new Node();
    static final Node FILL;
    static final Tree blackRunTree;
    static final Tree whiteRunTree;
    static final Tree eolOnlyTree;
    static final Tree codeTree;
    static final int VALUE_EOL = -2000;
    static final int VALUE_FILL = -1000;
    static final int VALUE_PASSMODE = -3000;
    static final int VALUE_HMODE = -4000;

    public CCITTFaxDecoderStream(InputStream stream, int columns, int type, long options, boolean byteAligned) {
        super(stream);
        this.lastChangingElement = 0;
        this.buffer = -1;
        this.bufferPos = -1;
        this.columns = columns;
        this.type = type;
        this.decodedRow = new byte[(columns + 7) / 8];
        this.changesReferenceRow = new int[columns + 2];
        this.changesCurrentRow = new int[columns + 2];
        switch (type) {
            case 2:
                this.optionByteAligned = byteAligned;
                this.optionG32D = false;
                this.optionG3Fill = false;
                this.optionUncompressed = false;
                return;
            case 3:
                this.optionByteAligned = byteAligned;
                this.optionG32D = (options & 1) != 0;
                this.optionG3Fill = (options & 4) != 0;
                this.optionUncompressed = (options & 2) != 0;
                return;
            case 4:
                this.optionByteAligned = byteAligned;
                this.optionG32D = false;
                this.optionG3Fill = false;
                this.optionUncompressed = (options & 2) != 0;
                return;
            default:
                throw new IllegalArgumentException("Illegal parameter: " + type);
        }
    }

    private void fetch() throws IOException {
        if (this.decodedPos >= this.decodedLength) {
            this.decodedLength = 0;
            try {
                decodeRow();
            } catch (EOFException e) {
                if (this.decodedLength != 0) {
                    throw e;
                }
                this.decodedLength = -1;
            } catch (ArrayIndexOutOfBoundsException e2) {
                throw new IOException("Malformed CCITT stream", e2);
            }
            this.decodedPos = 0;
        }
    }

    private void decode1D() throws IOException {
        int completeRun;
        int index = 0;
        boolean white = true;
        this.changesCurrentRowCount = 0;
        do {
            if (white) {
                completeRun = decodeRun(whiteRunTree);
            } else {
                completeRun = decodeRun(blackRunTree);
            }
            index += completeRun;
            int[] iArr = this.changesCurrentRow;
            int i = this.changesCurrentRowCount;
            this.changesCurrentRowCount = i + 1;
            iArr[i] = index;
            white = !white;
        } while (index < this.columns);
    }

    private void decode2D() throws IOException {
        this.changesReferenceRowCount = this.changesCurrentRowCount;
        int[] tmp = this.changesCurrentRow;
        this.changesCurrentRow = this.changesReferenceRow;
        this.changesReferenceRow = tmp;
        boolean white = true;
        int index = 0;
        this.changesCurrentRowCount = 0;
        while (index < this.columns) {
            Node n = codeTree.root;
            while (true) {
                n = n.walk(readBit());
                if (n == null) {
                    break;
                }
                if (n.isLeaf) {
                    switch (n.value) {
                        case VALUE_HMODE /* -4000 */:
                            int runLength = decodeRun(white ? whiteRunTree : blackRunTree);
                            int index2 = index + runLength;
                            int[] iArr = this.changesCurrentRow;
                            int i = this.changesCurrentRowCount;
                            this.changesCurrentRowCount = i + 1;
                            iArr[i] = index2;
                            int runLength2 = decodeRun(white ? blackRunTree : whiteRunTree);
                            index = index2 + runLength2;
                            int[] iArr2 = this.changesCurrentRow;
                            int i2 = this.changesCurrentRowCount;
                            this.changesCurrentRowCount = i2 + 1;
                            iArr2[i2] = index;
                            break;
                        case VALUE_PASSMODE /* -3000 */:
                            int pChangingElement = getNextChangingElement(index, white) + 1;
                            if (pChangingElement >= this.changesReferenceRowCount) {
                                index = this.columns;
                            } else {
                                index = this.changesReferenceRow[pChangingElement];
                            }
                            break;
                        default:
                            int vChangingElement = getNextChangingElement(index, white);
                            if (vChangingElement >= this.changesReferenceRowCount || vChangingElement == -1) {
                                index = this.columns + n.value;
                            } else {
                                index = this.changesReferenceRow[vChangingElement] + n.value;
                            }
                            this.changesCurrentRow[this.changesCurrentRowCount] = index;
                            this.changesCurrentRowCount++;
                            white = !white;
                            break;
                    }
                }
            }
        }
    }

    private int getNextChangingElement(int a0, boolean white) {
        int start = (this.lastChangingElement & (-2)) + (white ? 0 : 1);
        if (start > 2) {
            start -= 2;
        }
        if (a0 == 0) {
            return start;
        }
        for (int i = start; i < this.changesReferenceRowCount; i += 2) {
            if (a0 < this.changesReferenceRow[i]) {
                this.lastChangingElement = i;
                return i;
            }
        }
        return -1;
    }

    private void decodeRowType2() throws IOException {
        if (this.optionByteAligned) {
            resetBuffer();
        }
        decode1D();
    }

    private void decodeRowType4() throws IOException {
        if (this.optionByteAligned) {
            resetBuffer();
        }
        loop0: while (true) {
            Node n = eolOnlyTree.root;
            while (true) {
                n = n.walk(readBit());
                if (n == null) {
                    break;
                } else if (n.isLeaf) {
                    break loop0;
                }
            }
        }
        if (!this.optionG32D || readBit()) {
            decode1D();
        } else {
            decode2D();
        }
    }

    private void decodeRowType6() throws IOException {
        if (this.optionByteAligned) {
            resetBuffer();
        }
        decode2D();
    }

    private void decodeRow() throws IOException {
        switch (this.type) {
            case 2:
                decodeRowType2();
                break;
            case 3:
                decodeRowType4();
                break;
            case 4:
                decodeRowType6();
                break;
            default:
                throw new IllegalArgumentException("Illegal parameter: " + this.type);
        }
        int index = 0;
        boolean white = true;
        this.lastChangingElement = 0;
        for (int i = 0; i <= this.changesCurrentRowCount; i++) {
            int nextChange = this.columns;
            if (i != this.changesCurrentRowCount) {
                nextChange = this.changesCurrentRow[i];
            }
            if (nextChange > this.columns) {
                nextChange = this.columns;
            }
            int byteIndex = index / 8;
            while (index % 8 != 0 && nextChange - index > 0) {
                byte[] bArr = this.decodedRow;
                bArr[byteIndex] = (byte) (bArr[byteIndex] | (white ? 0 : 1 << (7 - (index % 8))));
                index++;
            }
            if (index % 8 == 0) {
                byteIndex = index / 8;
                byte value = (byte) (white ? 0 : 255);
                while (nextChange - index > 7) {
                    this.decodedRow[byteIndex] = value;
                    index += 8;
                    byteIndex++;
                }
            }
            while (nextChange - index > 0) {
                if (index % 8 == 0) {
                    this.decodedRow[byteIndex] = 0;
                }
                byte[] bArr2 = this.decodedRow;
                int i2 = byteIndex;
                bArr2[i2] = (byte) (bArr2[i2] | (white ? 0 : 1 << (7 - (index % 8))));
                index++;
            }
            white = !white;
        }
        if (index != this.columns) {
            throw new IOException("Sum of run-lengths does not equal scan line width: " + index + " > " + this.columns);
        }
        this.decodedLength = (index + 7) / 8;
    }

    private int decodeRun(Tree tree) throws IOException {
        int total = 0;
        Node n = tree.root;
        while (true) {
            boolean bit = readBit();
            n = n.walk(bit);
            if (n == null) {
                throw new IOException("Unknown code in Huffman RLE stream");
            }
            if (n.isLeaf) {
                total += n.value;
                if (n.value >= 64) {
                    n = tree.root;
                } else {
                    if (n.value >= 0) {
                        return total;
                    }
                    return this.columns;
                }
            }
        }
    }

    private void resetBuffer() {
        this.bufferPos = -1;
    }

    private boolean readBit() throws IOException {
        if (this.bufferPos < 0 || this.bufferPos > 7) {
            this.buffer = this.in.read();
            if (this.buffer == -1) {
                throw new EOFException("Unexpected end of Huffman RLE stream");
            }
            this.bufferPos = 0;
        }
        boolean isSet = (this.buffer & 128) != 0;
        this.buffer <<= 1;
        this.bufferPos++;
        return isSet;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        if (this.decodedLength < 0) {
            return 0;
        }
        if (this.decodedPos >= this.decodedLength) {
            fetch();
            if (this.decodedLength < 0) {
                return 0;
            }
        }
        byte[] bArr = this.decodedRow;
        int i = this.decodedPos;
        this.decodedPos = i + 1;
        return bArr[i] & 255;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.decodedLength < 0) {
            Arrays.fill(b, off, off + len, (byte) 0);
            return len;
        }
        if (this.decodedPos >= this.decodedLength) {
            fetch();
            if (this.decodedLength < 0) {
                Arrays.fill(b, off, off + len, (byte) 0);
                return len;
            }
        }
        int read = Math.min(this.decodedLength - this.decodedPos, len);
        System.arraycopy(this.decodedRow, this.decodedPos, b, off, read);
        this.decodedPos += read;
        return read;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public long skip(long n) throws IOException {
        if (this.decodedLength < 0) {
            return -1L;
        }
        if (this.decodedPos >= this.decodedLength) {
            fetch();
            if (this.decodedLength < 0) {
                return -1L;
            }
        }
        int skipped = (int) Math.min(this.decodedLength - this.decodedPos, n);
        this.decodedPos += skipped;
        return skipped;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public boolean markSupported() {
        return false;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public synchronized void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/filter/CCITTFaxDecoderStream$Node.class */
    private static final class Node {
        Node left;
        Node right;
        int value;
        boolean canBeFill;
        boolean isLeaf;

        private Node() {
            this.canBeFill = false;
            this.isLeaf = false;
        }

        void set(boolean next, Node node) {
            if (!next) {
                this.left = node;
            } else {
                this.right = node;
            }
        }

        Node walk(boolean next) {
            return next ? this.right : this.left;
        }

        public String toString() {
            return "[leaf=" + this.isLeaf + ", value=" + this.value + ", canBeFill=" + this.canBeFill + "]";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/filter/CCITTFaxDecoderStream$Tree.class */
    private static final class Tree {
        final Node root;

        private Tree() {
            this.root = new Node();
        }

        void fill(int depth, int path, int value) throws IOException {
            Node current = this.root;
            for (int i = 0; i < depth; i++) {
                int bitPos = (depth - 1) - i;
                boolean isSet = ((path >> bitPos) & 1) == 1;
                Node next = current.walk(isSet);
                if (next == null) {
                    next = new Node();
                    if (i == depth - 1) {
                        next.value = value;
                        next.isLeaf = true;
                    }
                    if (path == 0) {
                        next.canBeFill = true;
                    }
                    current.set(isSet, next);
                } else if (next.isLeaf) {
                    throw new IOException("node is leaf, no other following");
                }
                current = next;
            }
        }

        void fill(int depth, int path, Node node) throws IOException {
            Node current = this.root;
            for (int i = 0; i < depth; i++) {
                int bitPos = (depth - 1) - i;
                boolean isSet = ((path >> bitPos) & 1) == 1;
                Node next = current.walk(isSet);
                if (next == null) {
                    if (i == depth - 1) {
                        next = node;
                    } else {
                        next = new Node();
                    }
                    if (path == 0) {
                        next.canBeFill = true;
                    }
                    current.set(isSet, next);
                } else if (next.isLeaf) {
                    throw new IOException("node is leaf, no other following");
                }
                current = next;
            }
        }
    }

    static {
        EOL.isLeaf = true;
        EOL.value = VALUE_EOL;
        FILL = new Node();
        FILL.value = VALUE_FILL;
        FILL.left = FILL;
        FILL.right = EOL;
        eolOnlyTree = new Tree();
        try {
            eolOnlyTree.fill(12, 0, FILL);
            eolOnlyTree.fill(12, 1, EOL);
            blackRunTree = new Tree();
            for (int i = 0; i < BLACK_CODES.length; i++) {
                try {
                    for (int j = 0; j < BLACK_CODES[i].length; j++) {
                        blackRunTree.fill(i + 2, BLACK_CODES[i][j], BLACK_RUN_LENGTHS[i][j]);
                    }
                } catch (IOException e) {
                    throw new AssertionError(e);
                }
            }
            blackRunTree.fill(12, 0, FILL);
            blackRunTree.fill(12, 1, EOL);
            whiteRunTree = new Tree();
            for (int i2 = 0; i2 < WHITE_CODES.length; i2++) {
                try {
                    for (int j2 = 0; j2 < WHITE_CODES[i2].length; j2++) {
                        whiteRunTree.fill(i2 + 4, WHITE_CODES[i2][j2], WHITE_RUN_LENGTHS[i2][j2]);
                    }
                } catch (IOException e2) {
                    throw new AssertionError(e2);
                }
            }
            whiteRunTree.fill(12, 0, FILL);
            whiteRunTree.fill(12, 1, EOL);
            codeTree = new Tree();
            try {
                codeTree.fill(4, 1, VALUE_PASSMODE);
                codeTree.fill(3, 1, VALUE_HMODE);
                codeTree.fill(1, 1, 0);
                codeTree.fill(3, 3, 1);
                codeTree.fill(6, 3, 2);
                codeTree.fill(7, 3, 3);
                codeTree.fill(3, 2, -1);
                codeTree.fill(6, 2, -2);
                codeTree.fill(7, 2, -3);
            } catch (IOException e3) {
                throw new AssertionError(e3);
            }
        } catch (IOException e4) {
            throw new AssertionError(e4);
        }
    }
}
