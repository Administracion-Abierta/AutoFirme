package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.metadata.jpeg.JPEG;
import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/JPEGLosslessDecoder.class */
final class JPEGLosslessDecoder {
    private final ImageInputStream input;
    private final JPEGImageReader listenerDelegate;
    private final Frame frame;
    private final List<HuffmanTable> huffTables;
    private final QuantizationTable quantTable;
    private Scan scan;
    private final int[][][] HuffTab = new int[4][2][12800];
    private final int[] IDCT_Source = new int[64];
    private final int[] nBlock = new int[10];
    private final int[][] acTab = new int[10][];
    private final int[][] dcTab = new int[10][];
    private final int[][] qTab = new int[10][];
    private boolean restarting;
    private int marker;
    private int markerIndex;
    private int numComp;
    private int restartInterval;
    private int selection;
    private int xDim;
    private int yDim;
    private int xLoc;
    private int yLoc;
    private int mask;
    private int[][] outputData;
    private static final int[] IDCT_P = {0, 5, 40, 16, 45, 2, 7, 42, 21, 56, 8, 61, 18, 47, 1, 4, 41, 23, 58, 13, 32, 24, 37, 10, 63, 17, 44, 3, 6, 43, 20, 57, 15, 34, 29, 48, 53, 26, 39, 9, 60, 19, 46, 22, 59, 12, 33, 31, 50, 55, 25, 36, 11, 62, 14, 35, 28, 49, 52, 27, 38, 30, 51, 54};
    private static final int RESTART_MARKER_BEGIN = 65488;
    private static final int RESTART_MARKER_END = 65495;
    private static final int MAX_HUFFMAN_SUBTREE = 50;
    private static final int MSB = Integer.MIN_VALUE;

    int getDimX() {
        return this.xDim;
    }

    int getDimY() {
        return this.yDim;
    }

    JPEGLosslessDecoder(List<Segment> list, ImageInputStream imageInputStream, JPEGImageReader jPEGImageReader) {
        Validate.notNull(list);
        this.frame = (Frame) get(list, Frame.class);
        this.scan = (Scan) get(list, Scan.class);
        QuantizationTable quantizationTable = (QuantizationTable) get(list, QuantizationTable.class);
        this.quantTable = quantizationTable != null ? quantizationTable : new QuantizationTable();
        this.huffTables = getAll(list, HuffmanTable.class);
        RestartInterval restartInterval = (RestartInterval) get(list, RestartInterval.class);
        this.restartInterval = restartInterval != null ? restartInterval.interval : 0;
        this.input = imageInputStream;
        this.listenerDelegate = jPEGImageReader;
    }

    private <T> List<T> getAll(List<Segment> list, Class<T> cls) {
        ArrayList arrayList = new ArrayList();
        for (Segment segment : list) {
            if (cls.isInstance(segment)) {
                arrayList.add(cls.cast(segment));
            }
        }
        return arrayList;
    }

    private <T> T get(List<Segment> list, Class<T> cls) {
        for (Segment segment : list) {
            if (cls.isInstance(segment)) {
                return cls.cast(segment);
            }
        }
        return null;
    }

    int[][] decode() throws IOException {
        int i = 0;
        this.xLoc = 0;
        this.yLoc = 0;
        if (this.input.readUnsignedShort() != 65496) {
            throw new IIOException("Not a JPEG file, does not start with 0xFFD8");
        }
        Iterator<HuffmanTable> it = this.huffTables.iterator();
        while (it.hasNext()) {
            it.next().buildHuffTables(this.HuffTab);
        }
        this.quantTable.enhanceTables();
        int unsignedShort = this.input.readUnsignedShort();
        while (true) {
            if (unsignedShort != 65498) {
                this.input.skipBytes(this.input.readUnsignedShort() - 2);
                unsignedShort = this.input.readUnsignedShort();
            } else {
                int i2 = this.frame.samplePrecision;
                if (i2 == 8) {
                    this.mask = 255;
                } else {
                    this.mask = 65535;
                }
                Frame.Component[] componentArr = this.frame.components;
                this.scan = readScan();
                this.numComp = this.scan.components.length;
                this.selection = this.scan.spectralSelStart;
                Scan.Component[] componentArr2 = this.scan.components;
                for (int i3 = 0; i3 < this.numComp; i3++) {
                    Frame.Component componentSpec = getComponentSpec(componentArr, componentArr2[i3].scanCompSel);
                    this.qTab[i3] = this.quantTable.qTable(componentSpec.qtSel);
                    this.nBlock[i3] = componentSpec.vSub * componentSpec.hSub;
                    int i4 = componentArr2[i3].dcTabSel;
                    int i5 = componentArr2[i3].acTabSel;
                    if (useACForDC(i4)) {
                        processWarningOccured("Lossless JPEG with no DC tables encountered. Assuming only tables present to be DC tables.");
                        this.dcTab[i3] = this.HuffTab[i4][1];
                        this.acTab[i3] = this.HuffTab[i5][0];
                    } else {
                        this.dcTab[i3] = this.HuffTab[i4][0];
                        this.acTab[i3] = this.HuffTab[i5][1];
                    }
                }
                this.xDim = this.frame.samplesPerLine;
                this.yDim = this.frame.lines;
                this.outputData = new int[this.numComp][];
                for (int i6 = 0; i6 < this.numComp; i6++) {
                    this.outputData[i6] = new int[this.xDim * this.yDim];
                }
                int[] iArr = new int[this.numComp];
                for (int i7 = 0; i7 < this.numComp; i7++) {
                    iArr[i7] = 1 << (i2 - 1);
                }
                int[] iArr2 = new int[this.numComp];
                i++;
                do {
                    int[] iArr3 = new int[1];
                    int[] iArr4 = new int[1];
                    System.arraycopy(iArr, 0, iArr2, 0, this.numComp);
                    if (this.restartInterval == 0) {
                        int iDecode = decode(iArr2, iArr3, iArr4);
                        while (true) {
                            unsignedShort = iDecode;
                            if (unsignedShort != 0 || this.xLoc >= this.xDim || this.yLoc >= this.yDim) {
                                break;
                            }
                            output(iArr2);
                            iDecode = decode(iArr2, iArr3, iArr4);
                        }
                    } else {
                        int i8 = 0;
                        while (i8 < this.restartInterval) {
                            this.restarting = i8 == 0;
                            unsignedShort = decode(iArr2, iArr3, iArr4);
                            output(iArr2);
                            if (unsignedShort != 0) {
                                break;
                            }
                            i8++;
                        }
                        if (unsignedShort == 0) {
                            if (this.markerIndex != 0) {
                                unsignedShort = 65280 | this.marker;
                                this.markerIndex = 0;
                            } else {
                                unsignedShort = this.input.readUnsignedShort();
                            }
                        }
                        if (unsignedShort < RESTART_MARKER_BEGIN) {
                            break;
                        }
                    }
                } while (unsignedShort <= RESTART_MARKER_END);
                if (unsignedShort == 65500 && i == 1) {
                    readNumber();
                    unsignedShort = this.input.readUnsignedShort();
                }
                if (unsignedShort == 65497 || this.xLoc >= this.xDim || this.yLoc >= this.yDim || i != 0) {
                    break;
                }
            }
        }
        return this.outputData;
    }

    private void processWarningOccured(String str) {
        this.listenerDelegate.processWarningOccurred(str);
    }

    private boolean useACForDC(int i) {
        if (isLossless()) {
            for (HuffmanTable huffmanTable : this.huffTables) {
                if (!huffmanTable.isPresent(i, 0) && huffmanTable.isPresent(i, 1)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private boolean isLossless() {
        switch (this.frame.marker) {
            case JPEG.SOF3 /* 65475 */:
            case JPEG.SOF7 /* 65479 */:
            case JPEG.SOF11 /* 65483 */:
            case JPEG.SOF15 /* 65487 */:
                return true;
            default:
                return false;
        }
    }

    private Frame.Component getComponentSpec(Frame.Component[] componentArr, int i) {
        for (Frame.Component component : componentArr) {
            if (component.id == i) {
                return component;
            }
        }
        throw new IllegalArgumentException("No such component id: " + i);
    }

    private Scan readScan() throws IOException {
        return Scan.read(this.input, this.input.readUnsignedShort());
    }

    private int decode(int[] iArr, int[] iArr2, int[] iArr3) throws IOException {
        if (this.numComp == 1) {
            return decodeSingle(iArr, iArr2, iArr3);
        }
        if (this.numComp == 3) {
            return decodeRGB(iArr, iArr2, iArr3);
        }
        return decodeAny(iArr, iArr2, iArr3);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private int decodeSingle(int[] iArr, int[] iArr2, int[] iArr3) throws IIOException, IOException {
        if (this.restarting) {
            this.restarting = false;
            iArr[0] = 1 << (this.frame.samplePrecision - 1);
        } else {
            int[] iArr4 = this.outputData[0];
            switch (this.selection) {
                case 2:
                    iArr[0] = getPreviousY(iArr4);
                    break;
                case 3:
                    iArr[0] = getPreviousXY(iArr4);
                    break;
                case 4:
                    iArr[0] = (getPreviousX(iArr4) + getPreviousY(iArr4)) - getPreviousXY(iArr4);
                    break;
                case 5:
                    iArr[0] = getPreviousX(iArr4) + ((getPreviousY(iArr4) - getPreviousXY(iArr4)) >> 1);
                    break;
                case 6:
                    iArr[0] = getPreviousY(iArr4) + ((getPreviousX(iArr4) - getPreviousXY(iArr4)) >> 1);
                    break;
                case 7:
                    iArr[0] = (int) ((((long) getPreviousX(iArr4)) + ((long) getPreviousY(iArr4))) / 2);
                    break;
                default:
                    iArr[0] = getPreviousX(iArr4);
                    break;
            }
        }
        for (int i = 0; i < this.nBlock[0]; i++) {
            int huffmanValue = getHuffmanValue(this.dcTab[0], iArr2, iArr3);
            if (huffmanValue >= 65280) {
                return huffmanValue;
            }
            int nVar = getn(iArr, huffmanValue, iArr2, iArr3);
            int i2 = nVar >> 8;
            if (i2 >= RESTART_MARKER_BEGIN && i2 <= RESTART_MARKER_END) {
                return i2;
            }
            iArr[0] = iArr[0] + nVar;
        }
        return 0;
    }

    private int decodeRGB(int[] iArr, int[] iArr2, int[] iArr3) throws IOException {
        int[] iArr4 = this.outputData[0];
        int[] iArr5 = this.outputData[1];
        int[] iArr6 = this.outputData[2];
        switch (this.selection) {
            case 2:
                iArr[0] = getPreviousY(iArr4);
                iArr[1] = getPreviousY(iArr5);
                iArr[2] = getPreviousY(iArr6);
                break;
            case 3:
                iArr[0] = getPreviousXY(iArr4);
                iArr[1] = getPreviousXY(iArr5);
                iArr[2] = getPreviousXY(iArr6);
                break;
            case 4:
                iArr[0] = (getPreviousX(iArr4) + getPreviousY(iArr4)) - getPreviousXY(iArr4);
                iArr[1] = (getPreviousX(iArr5) + getPreviousY(iArr5)) - getPreviousXY(iArr5);
                iArr[2] = (getPreviousX(iArr6) + getPreviousY(iArr6)) - getPreviousXY(iArr6);
                break;
            case 5:
                iArr[0] = getPreviousX(iArr4) + ((getPreviousY(iArr4) - getPreviousXY(iArr4)) >> 1);
                iArr[1] = getPreviousX(iArr5) + ((getPreviousY(iArr5) - getPreviousXY(iArr5)) >> 1);
                iArr[2] = getPreviousX(iArr6) + ((getPreviousY(iArr6) - getPreviousXY(iArr6)) >> 1);
                break;
            case 6:
                iArr[0] = getPreviousY(iArr4) + ((getPreviousX(iArr4) - getPreviousXY(iArr4)) >> 1);
                iArr[1] = getPreviousY(iArr5) + ((getPreviousX(iArr5) - getPreviousXY(iArr5)) >> 1);
                iArr[2] = getPreviousY(iArr6) + ((getPreviousX(iArr6) - getPreviousXY(iArr6)) >> 1);
                break;
            case 7:
                iArr[0] = (int) ((((long) getPreviousX(iArr4)) + ((long) getPreviousY(iArr4))) / 2);
                iArr[1] = (int) ((((long) getPreviousX(iArr5)) + ((long) getPreviousY(iArr5))) / 2);
                iArr[2] = (int) ((((long) getPreviousX(iArr6)) + ((long) getPreviousY(iArr6))) / 2);
                break;
            default:
                iArr[0] = getPreviousX(iArr4);
                iArr[1] = getPreviousX(iArr5);
                iArr[2] = getPreviousX(iArr6);
                break;
        }
        return decode0(iArr, iArr2, iArr3);
    }

    private int decodeAny(int[] iArr, int[] iArr2, int[] iArr3) throws IOException {
        int previousX;
        for (int i = 0; i < this.outputData.length; i++) {
            int[] iArr4 = this.outputData[i];
            switch (this.selection) {
                case 2:
                    previousX = getPreviousY(iArr4);
                    break;
                case 3:
                    previousX = getPreviousXY(iArr4);
                    break;
                case 4:
                    previousX = (getPreviousX(iArr4) + getPreviousY(iArr4)) - getPreviousXY(iArr4);
                    break;
                case 5:
                    previousX = getPreviousX(iArr4) + ((getPreviousY(iArr4) - getPreviousXY(iArr4)) >> 1);
                    break;
                case 6:
                    previousX = getPreviousY(iArr4) + ((getPreviousX(iArr4) - getPreviousXY(iArr4)) >> 1);
                    break;
                case 7:
                    previousX = (int) ((((long) getPreviousX(iArr4)) + ((long) getPreviousY(iArr4))) / 2);
                    break;
                default:
                    previousX = getPreviousX(iArr4);
                    break;
            }
            iArr[i] = previousX;
        }
        return decode0(iArr, iArr2, iArr3);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private int decode0(int[] iArr, int[] iArr2, int[] iArr3) throws IIOException, IOException {
        for (int i = 0; i < this.numComp; i++) {
            int[] iArr4 = this.qTab[i];
            int[] iArr5 = this.acTab[i];
            int[] iArr6 = this.dcTab[i];
            for (int i2 = 0; i2 < this.nBlock[i]; i2++) {
                Arrays.fill(this.IDCT_Source, 0);
                int huffmanValue = getHuffmanValue(iArr6, iArr2, iArr3);
                if (huffmanValue >= 65280) {
                    return huffmanValue;
                }
                int[] iArr7 = this.IDCT_Source;
                int nVar = iArr[i] + getn(iArr3, huffmanValue, iArr2, iArr3);
                iArr7[0] = nVar;
                iArr[i] = nVar;
                int[] iArr8 = this.IDCT_Source;
                iArr8[0] = iArr8[0] * iArr4[0];
                int i3 = 1;
                while (i3 < 64) {
                    int huffmanValue2 = getHuffmanValue(iArr5, iArr2, iArr3);
                    if (huffmanValue2 >= 65280) {
                        return huffmanValue2;
                    }
                    int i4 = i3 + (huffmanValue2 >> 4);
                    if ((huffmanValue2 & 15) == 0) {
                        if ((huffmanValue2 >> 4) == 0) {
                            break;
                        }
                    } else {
                        this.IDCT_Source[IDCT_P[i4]] = getn(iArr3, huffmanValue2 & 15, iArr2, iArr3) * iArr4[i4];
                    }
                    i3 = i4 + 1;
                }
            }
        }
        return 0;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private int getHuffmanValue(int[] iArr, int[] iArr2, int[] iArr3) throws IIOException, IOException {
        if (iArr3[0] < 8) {
            iArr2[0] = iArr2[0] << 8;
            int unsignedByte = this.input.readUnsignedByte();
            if (unsignedByte == 255) {
                this.marker = this.input.readUnsignedByte();
                if (this.marker != 0) {
                    this.markerIndex = 9;
                }
            }
            iArr2[0] = iArr2[0] | unsignedByte;
        } else {
            iArr3[0] = iArr3[0] - 8;
        }
        int i = iArr[iArr2[0] >> iArr3[0]];
        if ((i & Integer.MIN_VALUE) != 0) {
            if (this.markerIndex != 0) {
                this.markerIndex = 0;
                return 65280 | this.marker;
            }
            iArr2[0] = iArr2[0] & (65535 >> (16 - iArr3[0]));
            iArr2[0] = iArr2[0] << 8;
            int unsignedByte2 = this.input.readUnsignedByte();
            if (unsignedByte2 == 255) {
                this.marker = this.input.readUnsignedByte();
                if (this.marker != 0) {
                    this.markerIndex = 9;
                }
            }
            iArr2[0] = iArr2[0] | unsignedByte2;
            i = iArr[((i & 255) * 256) + (iArr2[0] >> iArr3[0])];
            iArr3[0] = iArr3[0] + 8;
        }
        iArr3[0] = iArr3[0] + (8 - (i >> 8));
        if (iArr3[0] < 0) {
            throw new IIOException("index=" + iArr3[0] + " temp=" + iArr2[0] + " code=" + i + " in HuffmanValue()");
        }
        if (iArr3[0] < this.markerIndex) {
            this.markerIndex = 0;
            return 65280 | this.marker;
        }
        iArr2[0] = iArr2[0] & (65535 >> (16 - iArr3[0]));
        return i & 255;
    }

    private int getn(int[] iArr, int i, int[] iArr2, int[] iArr3) throws IOException {
        int i2;
        if (i == 0) {
            return 0;
        }
        if (i == 16) {
            if (iArr[0] >= 0) {
                return -32768;
            }
            return 32768;
        }
        iArr3[0] = iArr3[0] - i;
        if (iArr3[0] >= 0) {
            if (iArr3[0] < this.markerIndex && !isLastPixel()) {
                this.markerIndex = 0;
                return (65280 | this.marker) << 8;
            }
            i2 = iArr2[0] >> iArr3[0];
            iArr2[0] = iArr2[0] & (65535 >> (16 - iArr3[0]));
        } else {
            iArr2[0] = iArr2[0] << 8;
            int unsignedByte = this.input.readUnsignedByte();
            if (unsignedByte == 255) {
                this.marker = this.input.readUnsignedByte();
                if (this.marker != 0) {
                    this.markerIndex = 9;
                }
            }
            iArr2[0] = iArr2[0] | unsignedByte;
            iArr3[0] = iArr3[0] + 8;
            if (iArr3[0] < 0) {
                if (this.markerIndex != 0) {
                    this.markerIndex = 0;
                    return (65280 | this.marker) << 8;
                }
                iArr2[0] = iArr2[0] << 8;
                int unsignedByte2 = this.input.readUnsignedByte();
                if (unsignedByte2 == 255) {
                    this.marker = this.input.readUnsignedByte();
                    if (this.marker != 0) {
                        this.markerIndex = 9;
                    }
                }
                iArr2[0] = iArr2[0] | unsignedByte2;
                iArr3[0] = iArr3[0] + 8;
            }
            if (iArr3[0] < 0) {
                throw new IOException("index=" + iArr3[0] + " in getn()");
            }
            if (iArr3[0] < this.markerIndex) {
                this.markerIndex = 0;
                return (65280 | this.marker) << 8;
            }
            i2 = iArr2[0] >> iArr3[0];
            iArr2[0] = iArr2[0] & (65535 >> (16 - iArr3[0]));
        }
        if (i2 < (1 << (i - 1))) {
            i2 += ((-1) << i) + 1;
        }
        return i2;
    }

    private int getPreviousX(int[] iArr) {
        if (this.xLoc > 0) {
            return iArr[((this.yLoc * this.xDim) + this.xLoc) - 1];
        }
        if (this.yLoc > 0) {
            return getPreviousY(iArr);
        }
        return 1 << (this.frame.samplePrecision - 1);
    }

    private int getPreviousXY(int[] iArr) {
        if (this.xLoc > 0 && this.yLoc > 0) {
            return iArr[(((this.yLoc - 1) * this.xDim) + this.xLoc) - 1];
        }
        return getPreviousY(iArr);
    }

    private int getPreviousY(int[] iArr) {
        if (this.yLoc > 0) {
            return iArr[((this.yLoc - 1) * this.xDim) + this.xLoc];
        }
        return getPreviousX(iArr);
    }

    private boolean isLastPixel() {
        return this.xLoc == this.xDim - 1 && this.yLoc == this.yDim - 1;
    }

    private void output(int[] iArr) {
        if (this.numComp == 1) {
            outputSingle(iArr);
        } else if (this.numComp == 3) {
            outputRGB(iArr);
        } else {
            outputAny(iArr);
        }
    }

    private void outputSingle(int[] iArr) {
        if (this.xLoc < this.xDim && this.yLoc < this.yDim) {
            this.outputData[0][(this.yLoc * this.xDim) + this.xLoc] = this.mask & iArr[0];
            this.xLoc++;
            if (this.xLoc >= this.xDim) {
                this.yLoc++;
                this.xLoc = 0;
            }
        }
    }

    private void outputRGB(int[] iArr) {
        if (this.xLoc < this.xDim && this.yLoc < this.yDim) {
            int i = (this.yLoc * this.xDim) + this.xLoc;
            this.outputData[0][i] = iArr[0];
            this.outputData[1][i] = iArr[1];
            this.outputData[2][i] = iArr[2];
            this.xLoc++;
            if (this.xLoc >= this.xDim) {
                this.yLoc++;
                this.xLoc = 0;
            }
        }
    }

    private void outputAny(int[] iArr) {
        if (this.xLoc < this.xDim && this.yLoc < this.yDim) {
            int i = (this.yLoc * this.xDim) + this.xLoc;
            for (int i2 = 0; i2 < this.outputData.length; i2++) {
                this.outputData[i2][i] = iArr[i2];
            }
            this.xLoc++;
            if (this.xLoc >= this.xDim) {
                this.yLoc++;
                this.xLoc = 0;
            }
        }
    }

    private int readNumber() throws IOException {
        if (this.input.readUnsignedShort() != 4) {
            throw new IOException("ERROR: Define number format throw new IOException [Ld!=4]");
        }
        return this.input.readUnsignedShort();
    }

    int getNumComponents() {
        return this.numComp;
    }

    int getPrecision() {
        return this.frame.samplePrecision;
    }
}
