package org.apache.pdfbox.filter;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/filter/Predictor.class */
public final class Predictor {
    private Predictor() {
    }

    static void decodePredictorRow(int predictor, int colors, int bitsPerComponent, int columns, byte[] actline, byte[] lastline) {
        int left;
        if (predictor == 1) {
        }
        int bitsPerPixel = colors * bitsPerComponent;
        int bytesPerPixel = (bitsPerPixel + 7) / 8;
        int rowlength = actline.length;
        switch (predictor) {
            case 2:
                if (bitsPerComponent == 8) {
                    for (int p = bytesPerPixel; p < rowlength; p++) {
                        int sub = actline[p] & 255;
                        int left2 = actline[p - bytesPerPixel] & 255;
                        actline[p] = (byte) (sub + left2);
                    }
                } else if (bitsPerComponent == 16) {
                    for (int p2 = bytesPerPixel; p2 < rowlength; p2 += 2) {
                        int sub2 = ((actline[p2] & 255) << 8) + (actline[p2 + 1] & 255);
                        int left3 = ((actline[p2 - bytesPerPixel] & 255) << 8) + (actline[(p2 - bytesPerPixel) + 1] & 255);
                        actline[p2] = (byte) (((sub2 + left3) >> 8) & 255);
                        actline[p2 + 1] = (byte) ((sub2 + left3) & 255);
                    }
                } else if (bitsPerComponent == 1 && colors == 1) {
                    for (int p3 = 0; p3 < rowlength; p3++) {
                        for (int bit = 7; bit >= 0; bit--) {
                            int sub3 = (actline[p3] >> bit) & 1;
                            if (p3 != 0 || bit != 7) {
                                if (bit == 7) {
                                    left = actline[p3 - 1] & 1;
                                } else {
                                    left = (actline[p3] >> (bit + 1)) & 1;
                                }
                                if (((sub3 + left) & 1) == 0) {
                                    int i = p3;
                                    actline[i] = (byte) (actline[i] & ((1 << bit) ^ (-1)));
                                } else {
                                    int i2 = p3;
                                    actline[i2] = (byte) (actline[i2] | (1 << bit));
                                }
                            }
                        }
                    }
                } else {
                    int elements = columns * colors;
                    for (int p4 = colors; p4 < elements; p4++) {
                        int bytePosSub = (p4 * bitsPerComponent) / 8;
                        int bitPosSub = (8 - ((p4 * bitsPerComponent) % 8)) - bitsPerComponent;
                        int bytePosLeft = ((p4 - colors) * bitsPerComponent) / 8;
                        int bitPosLeft = (8 - (((p4 - colors) * bitsPerComponent) % 8)) - bitsPerComponent;
                        int sub4 = getBitSeq(actline[bytePosSub], bitPosSub, bitsPerComponent);
                        int left4 = getBitSeq(actline[bytePosLeft], bitPosLeft, bitsPerComponent);
                        actline[bytePosSub] = (byte) calcSetBitSeq(actline[bytePosSub], bitPosSub, bitsPerComponent, sub4 + left4);
                    }
                }
                break;
            case 11:
                for (int p5 = bytesPerPixel; p5 < rowlength; p5++) {
                    actline[p5] = (byte) (actline[p5] + actline[p5 - bytesPerPixel]);
                }
                break;
            case 12:
                for (int p6 = 0; p6 < rowlength; p6++) {
                    int up = actline[p6] & 255;
                    int prior = lastline[p6] & 255;
                    actline[p6] = (byte) ((up + prior) & 255);
                }
                break;
            case 13:
                for (int p7 = 0; p7 < rowlength; p7++) {
                    int avg = actline[p7] & 255;
                    int left5 = p7 - bytesPerPixel >= 0 ? actline[p7 - bytesPerPixel] & 255 : 0;
                    int up2 = lastline[p7] & 255;
                    actline[p7] = (byte) ((avg + ((left5 + up2) / 2)) & 255);
                }
                break;
            case 14:
                for (int p8 = 0; p8 < rowlength; p8++) {
                    int paeth = actline[p8] & 255;
                    int a = p8 - bytesPerPixel >= 0 ? actline[p8 - bytesPerPixel] & 255 : 0;
                    int b = lastline[p8] & 255;
                    int c = p8 - bytesPerPixel >= 0 ? lastline[p8 - bytesPerPixel] & 255 : 0;
                    int value = (a + b) - c;
                    int absa = Math.abs(value - a);
                    int absb = Math.abs(value - b);
                    int absc = Math.abs(value - c);
                    if (absa <= absb && absa <= absc) {
                        actline[p8] = (byte) ((paeth + a) & 255);
                    } else if (absb <= absc) {
                        actline[p8] = (byte) ((paeth + b) & 255);
                    } else {
                        actline[p8] = (byte) ((paeth + c) & 255);
                    }
                }
                break;
        }
    }

    static int calculateRowLength(int colors, int bitsPerComponent, int columns) {
        int bitsPerPixel = colors * bitsPerComponent;
        return ((columns * bitsPerPixel) + 7) / 8;
    }

    static int getBitSeq(int by, int startBit, int bitSize) {
        int mask = (1 << bitSize) - 1;
        return (by >>> startBit) & mask;
    }

    static int calcSetBitSeq(int by, int startBit, int bitSize, int val) {
        int mask = (1 << bitSize) - 1;
        int truncatedVal = val & mask;
        return (by & ((mask << startBit) ^ (-1))) | (truncatedVal << startBit);
    }

    static OutputStream wrapPredictor(OutputStream out, COSDictionary decodeParams) {
        int predictor = decodeParams.getInt(COSName.PREDICTOR);
        if (predictor > 1) {
            int colors = Math.min(decodeParams.getInt(COSName.COLORS, 1), 32);
            int bitsPerPixel = decodeParams.getInt(COSName.BITS_PER_COMPONENT, 8);
            int columns = decodeParams.getInt(COSName.COLUMNS, 1);
            return new PredictorOutputStream(out, predictor, colors, bitsPerPixel, columns);
        }
        return out;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/filter/Predictor$PredictorOutputStream.class */
    private static final class PredictorOutputStream extends FilterOutputStream {
        private int predictor;
        private final int colors;
        private final int bitsPerComponent;
        private final int columns;
        private final int rowLength;
        private final boolean predictorPerRow;
        private byte[] currentRow;
        private byte[] lastRow;
        private int currentRowData;
        private boolean predictorRead;

        PredictorOutputStream(OutputStream out, int predictor, int colors, int bitsPerComponent, int columns) {
            super(out);
            this.currentRowData = 0;
            this.predictorRead = false;
            this.predictor = predictor;
            this.colors = colors;
            this.bitsPerComponent = bitsPerComponent;
            this.columns = columns;
            this.rowLength = Predictor.calculateRowLength(colors, bitsPerComponent, columns);
            this.predictorPerRow = predictor >= 10;
            this.currentRow = new byte[this.rowLength];
            this.lastRow = new byte[this.rowLength];
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream
        public void write(byte[] bytes) throws IOException {
            write(bytes, 0, bytes.length);
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream
        public void write(byte[] bytes, int off, int len) throws IOException {
            int currentOffset = off;
            int maxOffset = currentOffset + len;
            while (currentOffset < maxOffset) {
                if (this.predictorPerRow && this.currentRowData == 0 && !this.predictorRead) {
                    this.predictor = bytes[currentOffset] + 10;
                    currentOffset++;
                    this.predictorRead = true;
                } else {
                    int toRead = Math.min(this.rowLength - this.currentRowData, maxOffset - currentOffset);
                    System.arraycopy(bytes, currentOffset, this.currentRow, this.currentRowData, toRead);
                    this.currentRowData += toRead;
                    currentOffset += toRead;
                    if (this.currentRowData == this.currentRow.length) {
                        decodeAndWriteRow();
                    }
                }
            }
        }

        private void decodeAndWriteRow() throws IOException {
            Predictor.decodePredictorRow(this.predictor, this.colors, this.bitsPerComponent, this.columns, this.currentRow, this.lastRow);
            this.out.write(this.currentRow);
            flipRows();
        }

        private void flipRows() {
            byte[] temp = this.lastRow;
            this.lastRow = this.currentRow;
            this.currentRow = temp;
            this.currentRowData = 0;
            this.predictorRead = false;
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
        public void flush() throws IOException {
            if (this.currentRowData > 0) {
                Arrays.fill(this.currentRow, this.currentRowData, this.rowLength, (byte) 0);
                decodeAndWriteRow();
            }
            super.flush();
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream
        public void write(int i) throws IOException {
            throw new UnsupportedOperationException("Not supported");
        }
    }
}
