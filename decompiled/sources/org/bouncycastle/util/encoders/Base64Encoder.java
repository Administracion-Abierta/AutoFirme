package org.bouncycastle.util.encoders;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/encoders/Base64Encoder.class */
public class Base64Encoder implements Encoder {
    protected final byte[] encodingTable = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
    protected byte padding = 61;
    protected final byte[] decodingTable = new byte[128];

    protected void initialiseDecodingTable() {
        for (int i = 0; i < this.decodingTable.length; i++) {
            this.decodingTable[i] = -1;
        }
        for (int i2 = 0; i2 < this.encodingTable.length; i2++) {
            this.decodingTable[this.encodingTable[i2]] = (byte) i2;
        }
    }

    public Base64Encoder() {
        initialiseDecodingTable();
    }

    public int encode(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws IOException {
        int i4 = i;
        int i5 = (i + i2) - 2;
        int i6 = i3;
        while (i4 < i5) {
            int i7 = i4;
            int i8 = i4 + 1;
            byte b = bArr[i7];
            int i9 = i8 + 1;
            int i10 = bArr[i8] & 255;
            i4 = i9 + 1;
            int i11 = bArr[i9] & 255;
            int i12 = i6;
            int i13 = i6 + 1;
            bArr2[i12] = this.encodingTable[(b >>> 2) & 63];
            int i14 = i13 + 1;
            bArr2[i13] = this.encodingTable[((b << 4) | (i10 >>> 4)) & 63];
            int i15 = i14 + 1;
            bArr2[i14] = this.encodingTable[((i10 << 2) | (i11 >>> 6)) & 63];
            i6 = i15 + 1;
            bArr2[i15] = this.encodingTable[i11 & 63];
        }
        switch (i2 - (i4 - i)) {
            case 1:
                int i16 = i4;
                int i17 = i4 + 1;
                int i18 = bArr[i16] & 255;
                int i19 = i6;
                int i20 = i6 + 1;
                bArr2[i19] = this.encodingTable[(i18 >>> 2) & 63];
                int i21 = i20 + 1;
                bArr2[i20] = this.encodingTable[(i18 << 4) & 63];
                int i22 = i21 + 1;
                bArr2[i21] = this.padding;
                i6 = i22 + 1;
                bArr2[i22] = this.padding;
                break;
            case 2:
                int i23 = i4;
                int i24 = i4 + 1;
                int i25 = bArr[i23] & 255;
                int i26 = i24 + 1;
                int i27 = bArr[i24] & 255;
                int i28 = i6;
                int i29 = i6 + 1;
                bArr2[i28] = this.encodingTable[(i25 >>> 2) & 63];
                int i30 = i29 + 1;
                bArr2[i29] = this.encodingTable[((i25 << 4) | (i27 >>> 4)) & 63];
                int i31 = i30 + 1;
                bArr2[i30] = this.encodingTable[(i27 << 2) & 63];
                i6 = i31 + 1;
                bArr2[i31] = this.padding;
                break;
        }
        return i6 - i3;
    }

    @Override // org.bouncycastle.util.encoders.Encoder
    public int getEncodedLength(int i) {
        return ((i + 2) / 3) * 4;
    }

    @Override // org.bouncycastle.util.encoders.Encoder
    public int getMaxDecodedLength(int i) {
        return (i / 4) * 3;
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
                return ((i2 + 2) / 3) * 4;
            }
            int iMin = Math.min(54, i4);
            outputStream.write(bArr2, 0, encode(bArr, i, iMin, bArr2, 0));
            i += iMin;
            i3 = i4 - iMin;
        }
    }

    private boolean ignore(char c) {
        return c == '\n' || c == '\r' || c == '\t' || c == ' ';
    }

    @Override // org.bouncycastle.util.encoders.Encoder
    public int decode(byte[] bArr, int i, int i2, OutputStream outputStream) throws IOException {
        byte[] bArr2 = new byte[54];
        int i3 = 0;
        int i4 = 0;
        int i5 = i + i2;
        while (i5 > i && ignore((char) bArr[i5 - 1])) {
            i5--;
        }
        if (i5 == 0) {
            return 0;
        }
        int i6 = 0;
        int i7 = i5;
        while (i7 > i && i6 != 4) {
            if (!ignore((char) bArr[i7 - 1])) {
                i6++;
            }
            i7--;
        }
        int iNextI = nextI(bArr, i, i7);
        while (true) {
            int i8 = iNextI;
            if (i8 >= i7) {
                if (i3 > 0) {
                    outputStream.write(bArr2, 0, i3);
                }
                int iNextI2 = nextI(bArr, i8, i5);
                int iNextI3 = nextI(bArr, iNextI2 + 1, i5);
                int iNextI4 = nextI(bArr, iNextI3 + 1, i5);
                return i4 + decodeLastBlock(outputStream, (char) bArr[iNextI2], (char) bArr[iNextI3], (char) bArr[iNextI4], (char) bArr[nextI(bArr, iNextI4 + 1, i5)]);
            }
            byte b = this.decodingTable[bArr[i8]];
            int iNextI5 = nextI(bArr, i8 + 1, i7);
            int i9 = iNextI5 + 1;
            byte b2 = this.decodingTable[bArr[iNextI5]];
            int iNextI6 = nextI(bArr, i9, i7);
            int i10 = iNextI6 + 1;
            byte b3 = this.decodingTable[bArr[iNextI6]];
            int iNextI7 = nextI(bArr, i10, i7);
            int i11 = iNextI7 + 1;
            byte b4 = this.decodingTable[bArr[iNextI7]];
            if ((b | b2 | b3 | b4) < 0) {
                throw new IOException("invalid characters encountered in base64 data");
            }
            int i12 = i3;
            int i13 = i3 + 1;
            bArr2[i12] = (byte) ((b << 2) | (b2 >> 4));
            int i14 = i13 + 1;
            bArr2[i13] = (byte) ((b2 << 4) | (b3 >> 2));
            i3 = i14 + 1;
            bArr2[i14] = (byte) ((b3 << 6) | b4);
            if (i3 == bArr2.length) {
                outputStream.write(bArr2);
                i3 = 0;
            }
            i4 += 3;
            iNextI = nextI(bArr, i11, i7);
        }
    }

    private int nextI(byte[] bArr, int i, int i2) {
        while (i < i2 && ignore((char) bArr[i])) {
            i++;
        }
        return i;
    }

    @Override // org.bouncycastle.util.encoders.Encoder
    public int decode(String str, OutputStream outputStream) throws IOException {
        byte[] bArr = new byte[54];
        int i = 0;
        int i2 = 0;
        int length = str.length();
        while (length > 0 && ignore(str.charAt(length - 1))) {
            length--;
        }
        if (length == 0) {
            return 0;
        }
        int i3 = 0;
        int i4 = length;
        while (i4 > 0 && i3 != 4) {
            if (!ignore(str.charAt(i4 - 1))) {
                i3++;
            }
            i4--;
        }
        int iNextI = nextI(str, 0, i4);
        while (true) {
            int i5 = iNextI;
            if (i5 >= i4) {
                if (i > 0) {
                    outputStream.write(bArr, 0, i);
                }
                int iNextI2 = nextI(str, i5, length);
                int iNextI3 = nextI(str, iNextI2 + 1, length);
                int iNextI4 = nextI(str, iNextI3 + 1, length);
                return i2 + decodeLastBlock(outputStream, str.charAt(iNextI2), str.charAt(iNextI3), str.charAt(iNextI4), str.charAt(nextI(str, iNextI4 + 1, length)));
            }
            byte b = this.decodingTable[str.charAt(i5)];
            int iNextI5 = nextI(str, i5 + 1, i4);
            int i6 = iNextI5 + 1;
            byte b2 = this.decodingTable[str.charAt(iNextI5)];
            int iNextI6 = nextI(str, i6, i4);
            int i7 = iNextI6 + 1;
            byte b3 = this.decodingTable[str.charAt(iNextI6)];
            int iNextI7 = nextI(str, i7, i4);
            int i8 = iNextI7 + 1;
            byte b4 = this.decodingTable[str.charAt(iNextI7)];
            if ((b | b2 | b3 | b4) < 0) {
                throw new IOException("invalid characters encountered in base64 data");
            }
            int i9 = i;
            int i10 = i + 1;
            bArr[i9] = (byte) ((b << 2) | (b2 >> 4));
            int i11 = i10 + 1;
            bArr[i10] = (byte) ((b2 << 4) | (b3 >> 2));
            i = i11 + 1;
            bArr[i11] = (byte) ((b3 << 6) | b4);
            i2 += 3;
            if (i == bArr.length) {
                outputStream.write(bArr);
                i = 0;
            }
            iNextI = nextI(str, i8, i4);
        }
    }

    private int decodeLastBlock(OutputStream outputStream, char c, char c2, char c3, char c4) throws IOException {
        if (c3 == this.padding) {
            if (c4 != this.padding) {
                throw new IOException("invalid characters encountered at end of base64 data");
            }
            byte b = this.decodingTable[c];
            byte b2 = this.decodingTable[c2];
            if ((b | b2) < 0) {
                throw new IOException("invalid characters encountered at end of base64 data");
            }
            outputStream.write((b << 2) | (b2 >> 4));
            return 1;
        }
        if (c4 == this.padding) {
            byte b3 = this.decodingTable[c];
            byte b4 = this.decodingTable[c2];
            byte b5 = this.decodingTable[c3];
            if ((b3 | b4 | b5) < 0) {
                throw new IOException("invalid characters encountered at end of base64 data");
            }
            outputStream.write((b3 << 2) | (b4 >> 4));
            outputStream.write((b4 << 4) | (b5 >> 2));
            return 2;
        }
        byte b6 = this.decodingTable[c];
        byte b7 = this.decodingTable[c2];
        byte b8 = this.decodingTable[c3];
        byte b9 = this.decodingTable[c4];
        if ((b6 | b7 | b8 | b9) < 0) {
            throw new IOException("invalid characters encountered at end of base64 data");
        }
        outputStream.write((b6 << 2) | (b7 >> 4));
        outputStream.write((b7 << 4) | (b8 >> 2));
        outputStream.write((b8 << 6) | b9);
        return 3;
    }

    private int nextI(String str, int i, int i2) {
        while (i < i2 && ignore(str.charAt(i))) {
            i++;
        }
        return i;
    }
}
