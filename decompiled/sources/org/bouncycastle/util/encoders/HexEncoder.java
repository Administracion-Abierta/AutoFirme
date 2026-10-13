package org.bouncycastle.util.encoders;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/encoders/HexEncoder.class */
public class HexEncoder implements Encoder {
    protected final byte[] encodingTable = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
    protected final byte[] decodingTable = new byte[128];

    protected void initialiseDecodingTable() {
        for (int i = 0; i < this.decodingTable.length; i++) {
            this.decodingTable[i] = -1;
        }
        for (int i2 = 0; i2 < this.encodingTable.length; i2++) {
            this.decodingTable[this.encodingTable[i2]] = (byte) i2;
        }
        this.decodingTable[65] = this.decodingTable[97];
        this.decodingTable[66] = this.decodingTable[98];
        this.decodingTable[67] = this.decodingTable[99];
        this.decodingTable[68] = this.decodingTable[100];
        this.decodingTable[69] = this.decodingTable[101];
        this.decodingTable[70] = this.decodingTable[102];
    }

    public HexEncoder() {
        initialiseDecodingTable();
    }

    public int encode(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws IOException {
        int i4 = i;
        int i5 = i + i2;
        int i6 = i3;
        while (i4 < i5) {
            int i7 = i4;
            i4++;
            int i8 = bArr[i7] & 255;
            int i9 = i6;
            int i10 = i6 + 1;
            bArr2[i9] = this.encodingTable[i8 >>> 4];
            i6 = i10 + 1;
            bArr2[i10] = this.encodingTable[i8 & 15];
        }
        return i6 - i3;
    }

    @Override // org.bouncycastle.util.encoders.Encoder
    public int getEncodedLength(int i) {
        return i * 2;
    }

    @Override // org.bouncycastle.util.encoders.Encoder
    public int getMaxDecodedLength(int i) {
        return i / 2;
    }

    @Override // org.bouncycastle.util.encoders.Encoder
    public int encode(byte[] bArr, int i, int i2, OutputStream outputStream) throws IOException {
        if (i2 < 0) {
            return 0;
        }
        byte[] bArr2 = new byte[72];
        int i3 = i2;
        while (true) {
            int i4 = i3;
            if (i4 <= 0) {
                return i2 * 2;
            }
            int iMin = Math.min(36, i4);
            outputStream.write(bArr2, 0, encode(bArr, i, iMin, bArr2, 0));
            i += iMin;
            i3 = i4 - iMin;
        }
    }

    private static boolean ignore(char c) {
        return c == '\n' || c == '\r' || c == '\t' || c == ' ';
    }

    @Override // org.bouncycastle.util.encoders.Encoder
    public int decode(byte[] bArr, int i, int i2, OutputStream outputStream) throws IOException {
        int i3 = 0;
        byte[] bArr2 = new byte[36];
        int i4 = 0;
        int i5 = i + i2;
        while (i5 > i && ignore((char) bArr[i5 - 1])) {
            i5--;
        }
        int i6 = i;
        while (i6 < i5) {
            while (i6 < i5 && ignore((char) bArr[i6])) {
                i6++;
            }
            int i7 = i6;
            int i8 = i6 + 1;
            byte b = this.decodingTable[bArr[i7]];
            while (i8 < i5 && ignore((char) bArr[i8])) {
                i8++;
            }
            int i9 = i8;
            i6 = i8 + 1;
            byte b2 = this.decodingTable[bArr[i9]];
            if ((b | b2) < 0) {
                throw new IOException("invalid characters encountered in Hex data");
            }
            int i10 = i4;
            i4++;
            bArr2[i10] = (byte) ((b << 4) | b2);
            if (i4 == bArr2.length) {
                outputStream.write(bArr2);
                i4 = 0;
            }
            i3++;
        }
        if (i4 > 0) {
            outputStream.write(bArr2, 0, i4);
        }
        return i3;
    }

    @Override // org.bouncycastle.util.encoders.Encoder
    public int decode(String str, OutputStream outputStream) throws IOException {
        int i = 0;
        byte[] bArr = new byte[36];
        int i2 = 0;
        int length = str.length();
        while (length > 0 && ignore(str.charAt(length - 1))) {
            length--;
        }
        int i3 = 0;
        while (i3 < length) {
            while (i3 < length && ignore(str.charAt(i3))) {
                i3++;
            }
            int i4 = i3;
            int i5 = i3 + 1;
            byte b = this.decodingTable[str.charAt(i4)];
            while (i5 < length && ignore(str.charAt(i5))) {
                i5++;
            }
            int i6 = i5;
            i3 = i5 + 1;
            byte b2 = this.decodingTable[str.charAt(i6)];
            if ((b | b2) < 0) {
                throw new IOException("invalid characters encountered in Hex string");
            }
            int i7 = i2;
            i2++;
            bArr[i7] = (byte) ((b << 4) | b2);
            if (i2 == bArr.length) {
                outputStream.write(bArr);
                i2 = 0;
            }
            i++;
        }
        if (i2 > 0) {
            outputStream.write(bArr, 0, i2);
        }
        return i;
    }

    byte[] decodeStrict(String str, int i, int i2) throws IOException {
        if (null == str) {
            throw new NullPointerException("'str' cannot be null");
        }
        if (i < 0 || i2 < 0 || i > str.length() - i2) {
            throw new IndexOutOfBoundsException("invalid offset and/or length specified");
        }
        if (0 != (i2 & 1)) {
            throw new IOException("a hexadecimal encoding must have an even number of characters");
        }
        int i3 = i2 >>> 1;
        byte[] bArr = new byte[i3];
        int i4 = i;
        for (int i5 = 0; i5 < i3; i5++) {
            int i6 = i4;
            int i7 = i4 + 1;
            i4 = i7 + 1;
            int i8 = (this.decodingTable[str.charAt(i6)] << 4) | this.decodingTable[str.charAt(i7)];
            if (i8 < 0) {
                throw new IOException("invalid characters encountered in Hex string");
            }
            bArr[i5] = (byte) i8;
        }
        return bArr;
    }
}
