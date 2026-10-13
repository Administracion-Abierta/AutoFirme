package org.apache.pdfbox.filter;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/filter/CCITTFaxEncoderStream.class */
final class CCITTFaxEncoderStream extends OutputStream {
    private final byte[] inputBuffer;
    private final int inputBufferLength;
    private final int columns;
    private final int rows;
    private int[] changesCurrentRow;
    private int[] changesReferenceRow;
    private final int fillOrder;
    private final OutputStream stream;
    private static final Code[] WHITE_TERMINATING_CODES = new Code[64];
    private static final Code[] WHITE_NONTERMINATING_CODES = new Code[40];
    private static final Code[] BLACK_TERMINATING_CODES;
    private static final Code[] BLACK_NONTERMINATING_CODES;
    private int currentBufferLength = 0;
    private int currentRow = 0;
    private int changesCurrentRowLength = 0;
    private int changesReferenceRowLength = 0;
    private byte outputBuffer = 0;
    private byte outputBufferBitLength = 0;

    CCITTFaxEncoderStream(OutputStream stream, int columns, int rows, int fillOrder) {
        this.stream = stream;
        this.columns = columns;
        this.rows = rows;
        this.fillOrder = fillOrder;
        this.changesReferenceRow = new int[columns];
        this.changesCurrentRow = new int[columns];
        this.inputBufferLength = (columns + 7) / 8;
        this.inputBuffer = new byte[this.inputBufferLength];
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        this.inputBuffer[this.currentBufferLength] = (byte) b;
        this.currentBufferLength++;
        if (this.currentBufferLength == this.inputBufferLength) {
            encodeRow();
            this.currentBufferLength = 0;
        }
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        this.stream.flush();
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.stream.close();
    }

    private void encodeRow() throws IOException {
        this.currentRow++;
        int[] tmp = this.changesReferenceRow;
        this.changesReferenceRow = this.changesCurrentRow;
        this.changesCurrentRow = tmp;
        this.changesReferenceRowLength = this.changesCurrentRowLength;
        this.changesCurrentRowLength = 0;
        boolean white = true;
        for (int index = 0; index < this.columns; index++) {
            int byteIndex = index / 8;
            int bit = index % 8;
            if ((((this.inputBuffer[byteIndex] >> (7 - bit)) & 1) == 1) == white) {
                this.changesCurrentRow[this.changesCurrentRowLength] = index;
                this.changesCurrentRowLength++;
                white = !white;
            }
        }
        encodeRowType6();
        if (this.currentRow == this.rows) {
            writeEOL();
            writeEOL();
            fill();
        }
    }

    private void encodeRowType6() throws IOException {
        encode2D();
    }

    private int[] getNextChanges(int pos, boolean white) {
        int[] result = {this.columns, this.columns};
        for (int i = 0; i < this.changesCurrentRowLength; i++) {
            if (pos < this.changesCurrentRow[i] || (pos == 0 && white)) {
                result[0] = this.changesCurrentRow[i];
                if (i + 1 >= this.changesCurrentRowLength) {
                    break;
                }
                result[1] = this.changesCurrentRow[i + 1];
                break;
            }
        }
        return result;
    }

    private void writeRun(int runLength, boolean white) throws IOException {
        int nonterm = runLength / 64;
        Code[] codes = white ? WHITE_NONTERMINATING_CODES : BLACK_NONTERMINATING_CODES;
        while (nonterm > 0) {
            if (nonterm >= codes.length) {
                write(codes[codes.length - 1].code, codes[codes.length - 1].length);
                nonterm -= codes.length;
            } else {
                write(codes[nonterm - 1].code, codes[nonterm - 1].length);
                nonterm = 0;
            }
        }
        Code c = white ? WHITE_TERMINATING_CODES[runLength % 64] : BLACK_TERMINATING_CODES[runLength % 64];
        write(c.code, c.length);
    }

    private void encode2D() throws IOException {
        boolean white = true;
        int i = 0;
        while (true) {
            int index = i;
            if (index < this.columns) {
                int[] nextChanges = getNextChanges(index, white);
                int[] nextRefs = getNextRefChanges(index, white);
                int difference = nextChanges[0] - nextRefs[0];
                if (nextChanges[0] > nextRefs[1]) {
                    write(1, 4);
                    i = nextRefs[1];
                } else if (difference > 3 || difference < -3) {
                    write(1, 3);
                    writeRun(nextChanges[0] - index, white);
                    writeRun(nextChanges[1] - nextChanges[0], !white);
                    i = nextChanges[1];
                } else {
                    switch (difference) {
                        case -3:
                            write(2, 7);
                            break;
                        case -2:
                            write(2, 6);
                            break;
                        case -1:
                            write(2, 3);
                            break;
                        case 0:
                            write(1, 1);
                            break;
                        case 1:
                            write(3, 3);
                            break;
                        case 2:
                            write(3, 6);
                            break;
                        case 3:
                            write(3, 7);
                            break;
                    }
                    white = !white;
                    i = nextRefs[0] + difference;
                }
            } else {
                return;
            }
        }
    }

    private int[] getNextRefChanges(int a0, boolean white) {
        int[] result = {this.columns, this.columns};
        for (int i = white ? 0 : 1; i < this.changesReferenceRowLength; i += 2) {
            if (this.changesReferenceRow[i] > a0 || (a0 == 0 && i == 0)) {
                result[0] = this.changesReferenceRow[i];
                if (i + 1 >= this.changesReferenceRowLength) {
                    break;
                }
                result[1] = this.changesReferenceRow[i + 1];
                break;
            }
        }
        return result;
    }

    private void write(int code, int codeLength) throws IOException {
        for (int i = 0; i < codeLength; i++) {
            boolean codeBit = ((code >> ((codeLength - i) - 1)) & 1) == 1;
            if (this.fillOrder == 1) {
                this.outputBuffer = (byte) (this.outputBuffer | (codeBit ? 1 << (7 - (this.outputBufferBitLength % 8)) : 0));
            } else {
                this.outputBuffer = (byte) (this.outputBuffer | (codeBit ? 1 << (this.outputBufferBitLength % 8) : 0));
            }
            this.outputBufferBitLength = (byte) (this.outputBufferBitLength + 1);
            if (this.outputBufferBitLength == 8) {
                this.stream.write(this.outputBuffer);
                clearOutputBuffer();
            }
        }
    }

    private void writeEOL() throws IOException {
        write(1, 12);
    }

    private void fill() throws IOException {
        if (this.outputBufferBitLength != 0) {
            this.stream.write(this.outputBuffer);
        }
        clearOutputBuffer();
    }

    private void clearOutputBuffer() {
        this.outputBuffer = (byte) 0;
        this.outputBufferBitLength = (byte) 0;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/filter/CCITTFaxEncoderStream$Code.class */
    private static class Code {
        final int code;
        final int length;

        private Code(int code, int length) {
            this.code = code;
            this.length = length;
        }
    }

    static {
        for (int i = 0; i < CCITTFaxDecoderStream.WHITE_CODES.length; i++) {
            int bitLength = i + 4;
            for (int j = 0; j < CCITTFaxDecoderStream.WHITE_CODES[i].length; j++) {
                short s = CCITTFaxDecoderStream.WHITE_RUN_LENGTHS[i][j];
                short s2 = CCITTFaxDecoderStream.WHITE_CODES[i][j];
                if (s < 64) {
                    WHITE_TERMINATING_CODES[s] = new Code(s2, bitLength);
                } else {
                    WHITE_NONTERMINATING_CODES[(s / 64) - 1] = new Code(s2, bitLength);
                }
            }
        }
        BLACK_TERMINATING_CODES = new Code[64];
        BLACK_NONTERMINATING_CODES = new Code[40];
        for (int i2 = 0; i2 < CCITTFaxDecoderStream.BLACK_CODES.length; i2++) {
            int bitLength2 = i2 + 2;
            for (int j2 = 0; j2 < CCITTFaxDecoderStream.BLACK_CODES[i2].length; j2++) {
                short s3 = CCITTFaxDecoderStream.BLACK_RUN_LENGTHS[i2][j2];
                short s4 = CCITTFaxDecoderStream.BLACK_CODES[i2][j2];
                if (s3 < 64) {
                    BLACK_TERMINATING_CODES[s3] = new Code(s4, bitLength2);
                } else {
                    BLACK_NONTERMINATING_CODES[(s3 / 64) - 1] = new Code(s4, bitLength2);
                }
            }
        }
    }
}
