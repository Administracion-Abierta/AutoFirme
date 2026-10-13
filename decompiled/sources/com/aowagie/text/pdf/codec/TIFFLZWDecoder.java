package com.aowagie.text.pdf.codec;

import com.sun.jna.platform.win32.WinUser;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/codec/TIFFLZWDecoder.class */
class TIFFLZWDecoder {
    private byte[][] stringTable;
    private byte[] uncompData;
    private int tableIndex;
    private int bytePointer;
    private int bitPointer;
    private int dstIndex;
    private final int w;
    private int h;
    private final int predictor;
    private final int samplesPerPixel;
    private byte[] data = null;
    private int bitsToGet = 9;
    private int nextData = 0;
    private int nextBits = 0;
    private final int[] andTable = {511, WinUser.CF_GDIOBJLAST, 2047, 4095};

    TIFFLZWDecoder(int w, int predictor, int samplesPerPixel) {
        this.w = w;
        this.predictor = predictor;
        this.samplesPerPixel = samplesPerPixel;
    }

    byte[] decode(byte[] data, byte[] uncompData, int h) {
        if (data[0] == 0 && data[1] == 1) {
            throw new UnsupportedOperationException("TIFF 5.0-style LZW codes are not supported.");
        }
        initializeStringTable();
        this.data = data;
        this.h = h;
        this.uncompData = uncompData;
        this.bytePointer = 0;
        this.bitPointer = 0;
        this.dstIndex = 0;
        this.nextData = 0;
        this.nextBits = 0;
        int i = 0;
        while (true) {
            int oldCode = i;
            int code = getNextCode();
            if (code == 257 || this.dstIndex >= uncompData.length) {
                break;
            }
            if (code == 256) {
                initializeStringTable();
                int code2 = getNextCode();
                if (code2 == 257) {
                    break;
                }
                writeString(this.stringTable[code2]);
                i = code2;
            } else if (code < this.tableIndex) {
                byte[] string = this.stringTable[code];
                writeString(string);
                addStringToTable(this.stringTable[oldCode], string[0]);
                i = code;
            } else {
                byte[] string2 = this.stringTable[oldCode];
                byte[] string3 = composeString(string2, string2[0]);
                writeString(string3);
                addStringToTable(string3);
                i = code;
            }
        }
        if (this.predictor == 2) {
            for (int j = 0; j < h; j++) {
                int count = this.samplesPerPixel * ((j * this.w) + 1);
                for (int i2 = this.samplesPerPixel; i2 < this.w * this.samplesPerPixel; i2++) {
                    int i3 = count;
                    uncompData[i3] = (byte) (uncompData[i3] + uncompData[count - this.samplesPerPixel]);
                    count++;
                }
            }
        }
        return uncompData;
    }

    private void initializeStringTable() {
        this.stringTable = new byte[4096][];
        for (int i = 0; i < 256; i++) {
            this.stringTable[i] = new byte[1];
            this.stringTable[i][0] = (byte) i;
        }
        this.tableIndex = 258;
        this.bitsToGet = 9;
    }

    private void writeString(byte[] string) {
        int max = this.uncompData.length - this.dstIndex;
        if (string.length < max) {
            max = string.length;
        }
        System.arraycopy(string, 0, this.uncompData, this.dstIndex, max);
        this.dstIndex += max;
    }

    private void addStringToTable(byte[] oldString, byte newString) {
        int length = oldString.length;
        byte[] string = new byte[length + 1];
        System.arraycopy(oldString, 0, string, 0, length);
        string[length] = newString;
        byte[][] bArr = this.stringTable;
        int i = this.tableIndex;
        this.tableIndex = i + 1;
        bArr[i] = string;
        if (this.tableIndex == 511) {
            this.bitsToGet = 10;
        } else if (this.tableIndex == 1023) {
            this.bitsToGet = 11;
        } else if (this.tableIndex == 2047) {
            this.bitsToGet = 12;
        }
    }

    private void addStringToTable(byte[] string) {
        byte[][] bArr = this.stringTable;
        int i = this.tableIndex;
        this.tableIndex = i + 1;
        bArr[i] = string;
        if (this.tableIndex == 511) {
            this.bitsToGet = 10;
        } else if (this.tableIndex == 1023) {
            this.bitsToGet = 11;
        } else if (this.tableIndex == 2047) {
            this.bitsToGet = 12;
        }
    }

    private byte[] composeString(byte[] oldString, byte newString) {
        int length = oldString.length;
        byte[] string = new byte[length + 1];
        System.arraycopy(oldString, 0, string, 0, length);
        string[length] = newString;
        return string;
    }

    public int getNextCode() {
        try {
            int i = this.nextData << 8;
            byte[] bArr = this.data;
            int i2 = this.bytePointer;
            this.bytePointer = i2 + 1;
            this.nextData = i | (bArr[i2] & 255);
            this.nextBits += 8;
            if (this.nextBits < this.bitsToGet) {
                int i3 = this.nextData << 8;
                byte[] bArr2 = this.data;
                int i4 = this.bytePointer;
                this.bytePointer = i4 + 1;
                this.nextData = i3 | (bArr2[i4] & 255);
                this.nextBits += 8;
            }
            int code = (this.nextData >> (this.nextBits - this.bitsToGet)) & this.andTable[this.bitsToGet - 9];
            this.nextBits -= this.bitsToGet;
            return code;
        } catch (ArrayIndexOutOfBoundsException e) {
            return 257;
        }
    }
}
