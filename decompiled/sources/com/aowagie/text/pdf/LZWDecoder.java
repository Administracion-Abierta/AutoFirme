package com.aowagie.text.pdf;

import com.aowagie.text.ExceptionConverter;
import com.sun.jna.platform.win32.WinUser;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/LZWDecoder.class */
class LZWDecoder {
    private byte[][] stringTable;
    private OutputStream uncompData;
    private int tableIndex;
    private int bytePointer;
    private int bitPointer;
    private byte[] data = null;
    private int bitsToGet = 9;
    private int nextData = 0;
    private int nextBits = 0;
    private final int[] andTable = {511, WinUser.CF_GDIOBJLAST, 2047, 4095};

    public void decode(byte[] data, OutputStream uncompData) {
        if (data[0] == 0 && data[1] == 1) {
            throw new RuntimeException("LZW flavour not supported.");
        }
        initializeStringTable();
        this.data = data;
        this.uncompData = uncompData;
        this.bytePointer = 0;
        this.bitPointer = 0;
        this.nextData = 0;
        this.nextBits = 0;
        int i = 0;
        while (true) {
            int oldCode = i;
            int code = getNextCode();
            if (code != 257) {
                if (code == 256) {
                    initializeStringTable();
                    int code2 = getNextCode();
                    if (code2 != 257) {
                        writeString(this.stringTable[code2]);
                        i = code2;
                    } else {
                        return;
                    }
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
            } else {
                return;
            }
        }
    }

    private void initializeStringTable() {
        this.stringTable = new byte[8192][];
        for (int i = 0; i < 256; i++) {
            this.stringTable[i] = new byte[1];
            this.stringTable[i][0] = (byte) i;
        }
        this.tableIndex = 258;
        this.bitsToGet = 9;
    }

    private void writeString(byte[] string) {
        try {
            this.uncompData.write(string);
        } catch (IOException e) {
            throw new ExceptionConverter(e);
        }
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
