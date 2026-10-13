package com.aowagie.text.pdf.codec;

import com.github.markusbernhardt.proxy.search.wpad.dhcp.DHCPOptions;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/codec/TIFFFaxDecoder.class */
public class TIFFFaxDecoder {
    private byte[] data;
    private final int w;
    private final int h;
    private final int fillOrder;
    private int[] prevChangingElems;
    private int[] currChangingElems;
    private int oneD;
    private static int[] table1 = {0, 1, 3, 7, 15, 31, 63, 127, 255};
    private static int[] table2 = {0, 128, 192, 224, 240, 248, DHCPOptions.OPTION_PROXY_AUTODISCOVERY, 254, 255};
    private static byte[] flipTable = {0, -128, 64, -64, 32, -96, 96, -32, 16, -112, 80, -48, 48, -80, 112, -16, 8, -120, 72, -56, 40, -88, 104, -24, 24, -104, 88, -40, 56, -72, 120, -8, 4, -124, 68, -60, 36, -92, 100, -28, 20, -108, 84, -44, 52, -76, 116, -12, 12, -116, 76, -52, 44, -84, 108, -20, 28, -100, 92, -36, 60, -68, 124, -4, 2, -126, 66, -62, 34, -94, 98, -30, 18, -110, 82, -46, 50, -78, 114, -14, 10, -118, 74, -54, 42, -86, 106, -22, 26, -102, 90, -38, 58, -70, 122, -6, 6, -122, 70, -58, 38, -90, 102, -26, 22, -106, 86, -42, 54, -74, 118, -10, 14, -114, 78, -50, 46, -82, 110, -18, 30, -98, 94, -34, 62, -66, 126, -2, 1, -127, 65, -63, 33, -95, 97, -31, 17, -111, 81, -47, 49, -79, 113, -15, 9, -119, 73, -55, 41, -87, 105, -23, 25, -103, 89, -39, 57, -71, 121, -7, 5, -123, 69, -59, 37, -91, 101, -27, 21, -107, 85, -43, 53, -75, 117, -11, 13, -115, 77, -51, 45, -83, 109, -19, 29, -99, 93, -35, 61, -67, 125, -3, 3, -125, 67, -61, 35, -93, 99, -29, 19, -109, 83, -45, 51, -77, 115, -13, 11, -117, 75, -53, 43, -85, 107, -21, 27, -101, 91, -37, 59, -69, 123, -5, 7, -121, 71, -57, 39, -89, 103, -25, 23, -105, 87, -41, 55, -73, 119, -9, 15, -113, 79, -49, 47, -81, 111, -17, 31, -97, 95, -33, 63, -65, 127, -1};
    private static short[] white = {6430, 6400, 6400, 6400, 3225, 3225, 3225, 3225, 944, 944, 944, 944, 976, 976, 976, 976, 1456, 1456, 1456, 1456, 1488, 1488, 1488, 1488, 718, 718, 718, 718, 718, 718, 718, 718, 750, 750, 750, 750, 750, 750, 750, 750, 1520, 1520, 1520, 1520, 1552, 1552, 1552, 1552, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 654, 654, 654, 654, 654, 654, 654, 654, 1072, 1072, 1072, 1072, 1104, 1104, 1104, 1104, 1136, 1136, 1136, 1136, 1168, 1168, 1168, 1168, 1200, 1200, 1200, 1200, 1232, 1232, 1232, 1232, 622, 622, 622, 622, 622, 622, 622, 622, 1008, 1008, 1008, 1008, 1040, 1040, 1040, 1040, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 1712, 1712, 1712, 1712, 1744, 1744, 1744, 1744, 846, 846, 846, 846, 846, 846, 846, 846, 1264, 1264, 1264, 1264, 1296, 1296, 1296, 1296, 1328, 1328, 1328, 1328, 1360, 1360, 1360, 1360, 1392, 1392, 1392, 1392, 1424, 1424, 1424, 1424, 686, 686, 686, 686, 686, 686, 686, 686, 910, 910, 910, 910, 910, 910, 910, 910, 1968, 1968, 1968, 1968, 2000, 2000, 2000, 2000, 2032, 2032, 2032, 2032, 16, 16, 16, 16, 10257, 10257, 10257, 10257, 12305, 12305, 12305, 12305, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 878, 878, 878, 878, 878, 878, 878, 878, 1904, 1904, 1904, 1904, 1936, 1936, 1936, 1936, -18413, -18413, -16365, -16365, -14317, -14317, -10221, -10221, 590, 590, 590, 590, 590, 590, 590, 590, 782, 782, 782, 782, 782, 782, 782, 782, 1584, 1584, 1584, 1584, 1616, 1616, 1616, 1616, 1648, 1648, 1648, 1648, 1680, 1680, 1680, 1680, 814, 814, 814, 814, 814, 814, 814, 814, 1776, 1776, 1776, 1776, 1808, 1808, 1808, 1808, 1840, 1840, 1840, 1840, 1872, 1872, 1872, 1872, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, 14353, 14353, 14353, 14353, 16401, 16401, 16401, 16401, 22547, 22547, 24595, 24595, 20497, 20497, 20497, 20497, 18449, 18449, 18449, 18449, 26643, 26643, 28691, 28691, 30739, 30739, -32749, -32749, -30701, -30701, -28653, -28653, -26605, -26605, -24557, -24557, -22509, -22509, -20461, -20461, 8207, 8207, 8207, 8207, 8207, 8207, 8207, 8207, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 
    232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232};
    private static short[] additionalMakeup = {28679, 28679, 31752, -32759, -31735, -30711, -29687, -28663, 29703, 29703, 30727, 30727, -27639, -26615, -25591, -24567};
    private static short[] initBlack = {3226, 6412, 200, 168, 38, 38, 134, 134, 100, 100, 100, 100, 68, 68, 68, 68};
    private static short[] twoBitBlack = {292, 260, 226, 226};
    private static short[] black = {62, 62, 30, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 588, 588, 588, 588, 588, 588, 588, 588, 1680, 1680, 20499, 22547, 24595, 26643, 1776, 1776, 1808, 1808, -24557, -22509, -20461, -18413, 1904, 1904, 1936, 1936, -16365, -14317, 782, 782, 782, 782, 814, 814, 814, 814, -12269, -10221, 10257, 10257, 12305, 12305, 14353, 14353, 16403, 18451, 1712, 1712, 1744, 1744, 28691, 30739, -32749, -30701, -28653, -26605, 2061, 2061, 2061, 2061, 2061, 2061, 2061, 2061, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 750, 750, 750, 750, 1616, 1616, 1648, 1648, 1424, 1424, 1456, 1456, 1488, 1488, 1520, 1520, 1840, 1840, 1872, 1872, 1968, 1968, 8209, 8209, 524, 524, 524, 524, 524, 524, 524, 524, 556, 556, 556, 556, 556, 556, 556, 556, 1552, 1552, 1584, 1584, 2000, 2000, 2032, 2032, 976, 976, 1008, 1008, 1040, 1040, 1072, 1072, 1296, 1296, 1328, 1328, 718, 718, 718, 718, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 4113, 4113, 6161, 6161, 848, 848, 880, 880, 912, 912, 944, 944, 622, 622, 622, 622, 654, 654, 654, 654, 1104, 1104, 1136, 1136, 1168, 1168, 1200, 1200, 1232, 1232, 1264, 1264, 686, 686, 686, 686, 1360, 1360, 1392, 1392, 12, 12, 12, 12, 12, 12, 12, 12, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390};
    private static byte[] twoDCodes = {80, 88, 23, 71, 30, 30, 62, 62, 4, 4, 4, 4, 4, 4, 4, 4, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41};
    private int changingElemSize = 0;
    private int lastChangingElement = 0;
    private int compression = 2;
    private int uncompressedMode = 0;
    private int fillBits = 0;
    private int bitPointer = 0;
    private int bytePointer = 0;

    TIFFFaxDecoder(int fillOrder, int w, int h) {
        this.fillOrder = fillOrder;
        this.w = w;
        this.h = h;
        this.prevChangingElems = new int[w];
        this.currChangingElems = new int[w];
    }

    public static void reverseBits(byte[] b) {
        for (int k = 0; k < b.length; k++) {
            b[k] = flipTable[b[k] & 255];
        }
    }

    void decode1D(byte[] buffer, byte[] compData, int startX, int height) {
        this.data = compData;
        int lineOffset = 0;
        int scanlineStride = (this.w + 7) / 8;
        this.bitPointer = 0;
        this.bytePointer = 0;
        for (int i = 0; i < height; i++) {
            decodeNextScanline(buffer, lineOffset, startX);
            lineOffset += scanlineStride;
        }
    }

    private void decodeNextScanline(byte[] buffer, int lineOffset, int bitOffset) {
        boolean isWhite = true;
        this.changingElemSize = 0;
        while (bitOffset < this.w) {
            while (isWhite) {
                int current = nextNBits(10);
                short s = white[current];
                int isT = s & 1;
                int bits = (s >>> 1) & 15;
                if (bits == 12) {
                    int twoBits = nextLesserThan8Bits(2);
                    short s2 = additionalMakeup[((current << 2) & 12) | twoBits];
                    int bits2 = (s2 >>> 1) & 7;
                    bitOffset += (s2 >>> 4) & 4095;
                    updatePointer(4 - bits2);
                } else {
                    if (bits == 0) {
                        throw new RuntimeException("Invalid code encountered.");
                    }
                    if (bits == 15) {
                        throw new RuntimeException("EOL code word encountered in White run.");
                    }
                    bitOffset += (s >>> 5) & 2047;
                    updatePointer(10 - bits);
                    if (isT == 0) {
                        isWhite = false;
                        int[] iArr = this.currChangingElems;
                        int i = this.changingElemSize;
                        this.changingElemSize = i + 1;
                        iArr[i] = bitOffset;
                    }
                }
            }
            if (bitOffset == this.w) {
                if (this.compression != 2) {
                    break;
                }
                advancePointer();
                break;
            }
            while (!isWhite) {
                int current2 = nextLesserThan8Bits(4);
                short s3 = initBlack[current2];
                int i2 = s3 & 1;
                int bits3 = (s3 >>> 1) & 15;
                int code = (s3 >>> 5) & 2047;
                if (code == 100) {
                    int current3 = nextNBits(9);
                    short s4 = black[current3];
                    int isT2 = s4 & 1;
                    int bits4 = (s4 >>> 1) & 15;
                    int code2 = (s4 >>> 5) & 2047;
                    if (bits4 == 12) {
                        updatePointer(5);
                        int current4 = nextLesserThan8Bits(4);
                        short s5 = additionalMakeup[current4];
                        int bits5 = (s5 >>> 1) & 7;
                        int code3 = (s5 >>> 4) & 4095;
                        setToBlack(buffer, lineOffset, bitOffset, code3);
                        bitOffset += code3;
                        updatePointer(4 - bits5);
                    } else {
                        if (bits4 == 15) {
                            throw new RuntimeException("EOL code word encountered in Black run.");
                        }
                        setToBlack(buffer, lineOffset, bitOffset, code2);
                        bitOffset += code2;
                        updatePointer(9 - bits4);
                        if (isT2 == 0) {
                            isWhite = true;
                            int[] iArr2 = this.currChangingElems;
                            int i3 = this.changingElemSize;
                            this.changingElemSize = i3 + 1;
                            iArr2[i3] = bitOffset;
                        }
                    }
                } else if (code == 200) {
                    int current5 = nextLesserThan8Bits(2);
                    short s6 = twoBitBlack[current5];
                    int code4 = (s6 >>> 5) & 2047;
                    int bits6 = (s6 >>> 1) & 15;
                    setToBlack(buffer, lineOffset, bitOffset, code4);
                    bitOffset += code4;
                    updatePointer(2 - bits6);
                    isWhite = true;
                    int[] iArr3 = this.currChangingElems;
                    int i4 = this.changingElemSize;
                    this.changingElemSize = i4 + 1;
                    iArr3[i4] = bitOffset;
                } else {
                    setToBlack(buffer, lineOffset, bitOffset, code);
                    bitOffset += code;
                    updatePointer(4 - bits3);
                    isWhite = true;
                    int[] iArr4 = this.currChangingElems;
                    int i5 = this.changingElemSize;
                    this.changingElemSize = i5 + 1;
                    iArr4[i5] = bitOffset;
                }
            }
            if (bitOffset == this.w) {
                if (this.compression != 2) {
                    break;
                }
                advancePointer();
                break;
            }
        }
        int[] iArr5 = this.currChangingElems;
        int i6 = this.changingElemSize;
        this.changingElemSize = i6 + 1;
        iArr5[i6] = bitOffset;
    }

    void decode2D(byte[] buffer, byte[] compData, int startX, int height, long tiffT4Options) {
        this.data = compData;
        this.compression = 3;
        this.bitPointer = 0;
        this.bytePointer = 0;
        int scanlineStride = (this.w + 7) / 8;
        int[] b = new int[2];
        this.oneD = (int) (tiffT4Options & 1);
        this.uncompressedMode = (int) ((tiffT4Options & 2) >> 1);
        this.fillBits = (int) ((tiffT4Options & 4) >> 2);
        if (readEOL(true) != 1) {
            throw new RuntimeException("First scanline must be 1D encoded.");
        }
        decodeNextScanline(buffer, 0, startX);
        int lineOffset = 0 + scanlineStride;
        for (int lines = 1; lines < height; lines++) {
            if (readEOL(false) == 0) {
                int[] temp = this.prevChangingElems;
                this.prevChangingElems = this.currChangingElems;
                this.currChangingElems = temp;
                int currIndex = 0;
                int a0 = -1;
                boolean isWhite = true;
                int bitOffset = startX;
                this.lastChangingElement = 0;
                while (bitOffset < this.w) {
                    getNextChangingElement(a0, isWhite, b);
                    int b1 = b[0];
                    int b2 = b[1];
                    int entry = twoDCodes[nextLesserThan8Bits(7)] & 255;
                    int code = (entry & 120) >>> 3;
                    int bits = entry & 7;
                    if (code == 0) {
                        if (!isWhite) {
                            setToBlack(buffer, lineOffset, bitOffset, b2 - bitOffset);
                        }
                        a0 = b2;
                        bitOffset = b2;
                        updatePointer(7 - bits);
                    } else if (code == 1) {
                        updatePointer(7 - bits);
                        if (isWhite) {
                            int bitOffset2 = bitOffset + decodeWhiteCodeWord();
                            int i = currIndex;
                            int currIndex2 = currIndex + 1;
                            this.currChangingElems[i] = bitOffset2;
                            int number = decodeBlackCodeWord();
                            setToBlack(buffer, lineOffset, bitOffset2, number);
                            bitOffset = bitOffset2 + number;
                            currIndex = currIndex2 + 1;
                            this.currChangingElems[currIndex2] = bitOffset;
                        } else {
                            int number2 = decodeBlackCodeWord();
                            setToBlack(buffer, lineOffset, bitOffset, number2);
                            int bitOffset3 = bitOffset + number2;
                            int i2 = currIndex;
                            int currIndex3 = currIndex + 1;
                            this.currChangingElems[i2] = bitOffset3;
                            bitOffset = bitOffset3 + decodeWhiteCodeWord();
                            currIndex = currIndex3 + 1;
                            this.currChangingElems[currIndex3] = bitOffset;
                        }
                        a0 = bitOffset;
                    } else if (code <= 8) {
                        int a1 = (b1 + code) - 5;
                        int i3 = currIndex;
                        currIndex++;
                        this.currChangingElems[i3] = a1;
                        if (!isWhite) {
                            setToBlack(buffer, lineOffset, bitOffset, a1 - bitOffset);
                        }
                        a0 = a1;
                        bitOffset = a1;
                        isWhite = !isWhite;
                        updatePointer(7 - bits);
                    } else {
                        throw new RuntimeException("Invalid code encountered while decoding 2D group 3 compressed data.");
                    }
                }
                this.currChangingElems[currIndex] = bitOffset;
                this.changingElemSize = currIndex + 1;
            } else {
                decodeNextScanline(buffer, lineOffset, startX);
            }
            lineOffset += scanlineStride;
        }
    }

    void decodeT6(byte[] buffer, byte[] compData, int startX, int height, long tiffT6Options) {
        this.data = compData;
        this.compression = 4;
        this.bitPointer = 0;
        this.bytePointer = 0;
        int scanlineStride = (this.w + 7) / 8;
        int[] b = new int[2];
        this.uncompressedMode = (int) ((tiffT6Options & 2) >> 1);
        int[] cce = this.currChangingElems;
        this.changingElemSize = 0;
        int i = this.changingElemSize;
        this.changingElemSize = i + 1;
        cce[i] = this.w;
        int i2 = this.changingElemSize;
        this.changingElemSize = i2 + 1;
        cce[i2] = this.w;
        int lineOffset = 0;
        for (int lines = 0; lines < height; lines++) {
            int a0 = -1;
            boolean isWhite = true;
            int[] temp = this.prevChangingElems;
            this.prevChangingElems = this.currChangingElems;
            this.currChangingElems = temp;
            int currIndex = 0;
            int bitOffset = startX;
            this.lastChangingElement = 0;
            while (bitOffset < this.w) {
                getNextChangingElement(a0, isWhite, b);
                int b1 = b[0];
                int b2 = b[1];
                int entry = twoDCodes[nextLesserThan8Bits(7)] & 255;
                int code = (entry & 120) >>> 3;
                int bits = entry & 7;
                if (code == 0) {
                    if (!isWhite) {
                        setToBlack(buffer, lineOffset, bitOffset, b2 - bitOffset);
                    }
                    a0 = b2;
                    bitOffset = b2;
                    updatePointer(7 - bits);
                } else if (code == 1) {
                    updatePointer(7 - bits);
                    if (isWhite) {
                        int bitOffset2 = bitOffset + decodeWhiteCodeWord();
                        int i3 = currIndex;
                        int currIndex2 = currIndex + 1;
                        temp[i3] = bitOffset2;
                        int number = decodeBlackCodeWord();
                        setToBlack(buffer, lineOffset, bitOffset2, number);
                        bitOffset = bitOffset2 + number;
                        currIndex = currIndex2 + 1;
                        temp[currIndex2] = bitOffset;
                    } else {
                        int number2 = decodeBlackCodeWord();
                        setToBlack(buffer, lineOffset, bitOffset, number2);
                        int bitOffset3 = bitOffset + number2;
                        int i4 = currIndex;
                        int currIndex3 = currIndex + 1;
                        temp[i4] = bitOffset3;
                        bitOffset = bitOffset3 + decodeWhiteCodeWord();
                        currIndex = currIndex3 + 1;
                        temp[currIndex3] = bitOffset;
                    }
                    a0 = bitOffset;
                } else if (code <= 8) {
                    int a1 = (b1 + code) - 5;
                    int i5 = currIndex;
                    currIndex++;
                    temp[i5] = a1;
                    if (!isWhite) {
                        setToBlack(buffer, lineOffset, bitOffset, a1 - bitOffset);
                    }
                    a0 = a1;
                    bitOffset = a1;
                    isWhite = !isWhite;
                    updatePointer(7 - bits);
                } else if (code == 11) {
                    if (nextLesserThan8Bits(3) != 7) {
                        throw new RuntimeException("Invalid code encountered while decoding 2D group 4 compressed data.");
                    }
                    int zeros = 0;
                    boolean exit = false;
                    while (!exit) {
                        while (nextLesserThan8Bits(1) != 1) {
                            zeros++;
                        }
                        if (zeros > 5) {
                            zeros -= 6;
                            if (!isWhite && zeros > 0) {
                                int i6 = currIndex;
                                currIndex++;
                                temp[i6] = bitOffset;
                            }
                            bitOffset += zeros;
                            if (zeros > 0) {
                                isWhite = true;
                            }
                            if (nextLesserThan8Bits(1) == 0) {
                                if (!isWhite) {
                                    int i7 = currIndex;
                                    currIndex++;
                                    temp[i7] = bitOffset;
                                }
                                isWhite = true;
                            } else {
                                if (isWhite) {
                                    int i8 = currIndex;
                                    currIndex++;
                                    temp[i8] = bitOffset;
                                }
                                isWhite = false;
                            }
                            exit = true;
                        }
                        if (zeros == 5) {
                            if (!isWhite) {
                                int i9 = currIndex;
                                currIndex++;
                                temp[i9] = bitOffset;
                            }
                            bitOffset += zeros;
                            isWhite = true;
                        } else {
                            int bitOffset4 = bitOffset + zeros;
                            int i10 = currIndex;
                            currIndex++;
                            temp[i10] = bitOffset4;
                            setToBlack(buffer, lineOffset, bitOffset4, 1);
                            bitOffset = bitOffset4 + 1;
                            isWhite = false;
                        }
                    }
                } else {
                    bitOffset = this.w;
                    updatePointer(7 - bits);
                }
            }
            if (currIndex < temp.length) {
                int i11 = currIndex;
                currIndex++;
                temp[i11] = bitOffset;
            }
            this.changingElemSize = currIndex;
            lineOffset += scanlineStride;
        }
    }

    private void setToBlack(byte[] buffer, int lineOffset, int bitOffset, int numBits) {
        int bitNum = (8 * lineOffset) + bitOffset;
        int lastBit = bitNum + numBits;
        int byteNum = bitNum >> 3;
        int shift = bitNum & 7;
        if (shift > 0) {
            int maskVal = 1 << (7 - shift);
            byte val = buffer[byteNum];
            while (maskVal > 0 && bitNum < lastBit) {
                val = (byte) (val | maskVal);
                maskVal >>= 1;
                bitNum++;
            }
            buffer[byteNum] = val;
        }
        int byteNum2 = bitNum >> 3;
        while (bitNum < lastBit - 7) {
            int i = byteNum2;
            byteNum2++;
            buffer[i] = -1;
            bitNum += 8;
        }
        while (bitNum < lastBit) {
            int byteNum3 = bitNum >> 3;
            buffer[byteNum3] = (byte) (buffer[byteNum3] | (1 << (7 - (bitNum & 7))));
            bitNum++;
        }
    }

    private int decodeWhiteCodeWord() {
        int runLength = 0;
        boolean isWhite = true;
        while (isWhite) {
            int current = nextNBits(10);
            short s = white[current];
            int isT = s & 1;
            int bits = (s >>> 1) & 15;
            if (bits == 12) {
                int twoBits = nextLesserThan8Bits(2);
                short s2 = additionalMakeup[((current << 2) & 12) | twoBits];
                int bits2 = (s2 >>> 1) & 7;
                int code = (s2 >>> 4) & 4095;
                runLength += code;
                updatePointer(4 - bits2);
            } else {
                if (bits == 0) {
                    throw new RuntimeException("Invalid code encountered.");
                }
                if (bits == 15) {
                    throw new RuntimeException("EOL code word encountered in White run.");
                }
                int code2 = (s >>> 5) & 2047;
                runLength += code2;
                updatePointer(10 - bits);
                if (isT == 0) {
                    isWhite = false;
                }
            }
        }
        return runLength;
    }

    private int decodeBlackCodeWord() {
        int runLength = 0;
        boolean isWhite = false;
        while (!isWhite) {
            int current = nextLesserThan8Bits(4);
            short s = initBlack[current];
            int i = s & 1;
            int bits = (s >>> 1) & 15;
            int code = (s >>> 5) & 2047;
            if (code == 100) {
                int current2 = nextNBits(9);
                short s2 = black[current2];
                int isT = s2 & 1;
                int bits2 = (s2 >>> 1) & 15;
                int code2 = (s2 >>> 5) & 2047;
                if (bits2 == 12) {
                    updatePointer(5);
                    int current3 = nextLesserThan8Bits(4);
                    short s3 = additionalMakeup[current3];
                    int bits3 = (s3 >>> 1) & 7;
                    runLength += (s3 >>> 4) & 4095;
                    updatePointer(4 - bits3);
                } else {
                    if (bits2 == 15) {
                        throw new RuntimeException("EOL code word encountered in Black run.");
                    }
                    runLength += code2;
                    updatePointer(9 - bits2);
                    if (isT == 0) {
                        isWhite = true;
                    }
                }
            } else if (code == 200) {
                int current4 = nextLesserThan8Bits(2);
                short s4 = twoBitBlack[current4];
                runLength += (s4 >>> 5) & 2047;
                int bits4 = (s4 >>> 1) & 15;
                updatePointer(2 - bits4);
                isWhite = true;
            } else {
                runLength += code;
                updatePointer(4 - bits);
                isWhite = true;
            }
        }
        return runLength;
    }

    private int readEOL(boolean isFirstEOL) {
        if (this.fillBits == 0) {
            int next12Bits = nextNBits(12);
            if (isFirstEOL && next12Bits == 0 && nextNBits(4) == 1) {
                this.fillBits = 1;
                return 1;
            }
            if (next12Bits != 1) {
                throw new RuntimeException("Scanline must begin with EOL code word.");
            }
        } else if (this.fillBits == 1) {
            int bitsLeft = 8 - this.bitPointer;
            if (nextNBits(bitsLeft) != 0) {
                throw new RuntimeException("All fill bits preceding EOL code must be 0.");
            }
            if (bitsLeft < 4 && nextNBits(8) != 0) {
                throw new RuntimeException("All fill bits preceding EOL code must be 0.");
            }
            while (true) {
                int n = nextNBits(8);
                if (n == 1) {
                    break;
                }
                if (n != 0) {
                    throw new RuntimeException("All fill bits preceding EOL code must be 0.");
                }
            }
        }
        if (this.oneD == 0) {
            return 1;
        }
        return nextLesserThan8Bits(1);
    }

    private void getNextChangingElement(int a0, boolean isWhite, int[] ret) {
        int start;
        int[] pce = this.prevChangingElems;
        int ces = this.changingElemSize;
        int start2 = this.lastChangingElement > 0 ? this.lastChangingElement - 1 : 0;
        if (isWhite) {
            start = start2 & (-2);
        } else {
            start = start2 | 1;
        }
        int i = start;
        while (i < ces) {
            int temp = pce[i];
            if (temp <= a0) {
                i += 2;
            } else {
                this.lastChangingElement = i;
                ret[0] = temp;
                break;
            }
        }
        if (i + 1 < ces) {
            ret[1] = pce[i + 1];
        }
    }

    private int nextNBits(int bitsToGet) {
        byte b;
        byte next;
        byte next2next;
        int l = this.data.length - 1;
        int bp = this.bytePointer;
        if (this.fillOrder == 1) {
            b = this.data[bp];
            if (bp == l) {
                next = 0;
                next2next = 0;
            } else if (bp + 1 == l) {
                next = this.data[bp + 1];
                next2next = 0;
            } else {
                next = this.data[bp + 1];
                next2next = this.data[bp + 2];
            }
        } else if (this.fillOrder == 2) {
            b = flipTable[this.data[bp] & 255];
            if (bp == l) {
                next = 0;
                next2next = 0;
            } else if (bp + 1 == l) {
                next = flipTable[this.data[bp + 1] & 255];
                next2next = 0;
            } else {
                next = flipTable[this.data[bp + 1] & 255];
                next2next = flipTable[this.data[bp + 2] & 255];
            }
        } else {
            throw new RuntimeException("TIFF_FILL_ORDER tag must be either 1 or 2.");
        }
        int bitsLeft = 8 - this.bitPointer;
        int bitsFromNextByte = bitsToGet - bitsLeft;
        int bitsFromNext2NextByte = 0;
        if (bitsFromNextByte > 8) {
            bitsFromNext2NextByte = bitsFromNextByte - 8;
            bitsFromNextByte = 8;
        }
        this.bytePointer++;
        int i1 = (b & table1[bitsLeft]) << (bitsToGet - bitsLeft);
        int i2 = (next & table2[bitsFromNextByte]) >>> (8 - bitsFromNextByte);
        if (bitsFromNext2NextByte != 0) {
            int i3 = (next2next & table2[bitsFromNext2NextByte]) >>> (8 - bitsFromNext2NextByte);
            i2 = (i2 << bitsFromNext2NextByte) | i3;
            this.bytePointer++;
            this.bitPointer = bitsFromNext2NextByte;
        } else if (bitsFromNextByte == 8) {
            this.bitPointer = 0;
            this.bytePointer++;
        } else {
            this.bitPointer = bitsFromNextByte;
        }
        int i = i1 | i2;
        return i;
    }

    private int nextLesserThan8Bits(int bitsToGet) {
        byte b;
        byte next;
        int i1;
        int l = this.data.length - 1;
        int bp = this.bytePointer;
        if (this.fillOrder == 1) {
            b = this.data[bp];
            if (bp == l) {
                next = 0;
            } else {
                next = this.data[bp + 1];
            }
        } else if (this.fillOrder == 2) {
            b = flipTable[this.data[bp] & 255];
            if (bp == l) {
                next = 0;
            } else {
                next = flipTable[this.data[bp + 1] & 255];
            }
        } else {
            throw new RuntimeException("TIFF_FILL_ORDER tag must be either 1 or 2.");
        }
        int bitsLeft = 8 - this.bitPointer;
        int bitsFromNextByte = bitsToGet - bitsLeft;
        int shift = bitsLeft - bitsToGet;
        if (shift >= 0) {
            i1 = (b & table1[bitsLeft]) >>> shift;
            this.bitPointer += bitsToGet;
            if (this.bitPointer == 8) {
                this.bitPointer = 0;
                this.bytePointer++;
            }
        } else {
            int i2 = (b & table1[bitsLeft]) << (-shift);
            int i3 = (next & table2[bitsFromNextByte]) >>> (8 - bitsFromNextByte);
            i1 = i2 | i3;
            this.bytePointer++;
            this.bitPointer = bitsFromNextByte;
        }
        return i1;
    }

    private void updatePointer(int bitsToMoveBack) {
        int i = this.bitPointer - bitsToMoveBack;
        if (i < 0) {
            this.bytePointer--;
            this.bitPointer = 8 + i;
        } else {
            this.bitPointer = i;
        }
    }

    private boolean advancePointer() {
        if (this.bitPointer != 0) {
            this.bytePointer++;
            this.bitPointer = 0;
            return true;
        }
        return true;
    }
}
