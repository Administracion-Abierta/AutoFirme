package org.bouncycastle.util.encoders;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/encoders/Base32Encoder.class */
public class Base32Encoder implements Encoder {
    private static final byte[] DEAULT_ENCODING_TABLE = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 50, 51, 52, 53, 54, 55};
    private static final byte DEFAULT_PADDING = 61;
    private final byte[] encodingTable;
    private final byte padding;
    private final byte[] decodingTable;

    protected void initialiseDecodingTable() {
        for (int i = 0; i < this.decodingTable.length; i++) {
            this.decodingTable[i] = -1;
        }
        for (int i2 = 0; i2 < this.encodingTable.length; i2++) {
            this.decodingTable[this.encodingTable[i2]] = (byte) i2;
        }
    }

    public Base32Encoder() {
        this.decodingTable = new byte[128];
        this.encodingTable = DEAULT_ENCODING_TABLE;
        this.padding = (byte) 61;
        initialiseDecodingTable();
    }

    public Base32Encoder(byte[] bArr, byte b) {
        this.decodingTable = new byte[128];
        if (bArr.length != 32) {
            throw new IllegalArgumentException("encoding table needs to be length 32");
        }
        this.encodingTable = Arrays.clone(bArr);
        this.padding = b;
        initialiseDecodingTable();
    }

    public int encode(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws IOException {
        int i4 = i;
        int i5 = (i + i2) - 4;
        int i6 = i3;
        while (i4 < i5) {
            encodeBlock(bArr, i4, bArr2, i6);
            i4 += 5;
            i6 += 8;
        }
        int i7 = i2 - (i4 - i);
        if (i7 > 0) {
            byte[] bArr3 = new byte[5];
            System.arraycopy(bArr, i4, bArr3, 0, i7);
            encodeBlock(bArr3, 0, bArr2, i6);
            switch (i7) {
                case 1:
                    bArr2[i6 + 2] = this.padding;
                    bArr2[i6 + 3] = this.padding;
                    bArr2[i6 + 4] = this.padding;
                    bArr2[i6 + 5] = this.padding;
                    bArr2[i6 + 6] = this.padding;
                    bArr2[i6 + 7] = this.padding;
                    break;
                case 2:
                    bArr2[i6 + 4] = this.padding;
                    bArr2[i6 + 5] = this.padding;
                    bArr2[i6 + 6] = this.padding;
                    bArr2[i6 + 7] = this.padding;
                    break;
                case 3:
                    bArr2[i6 + 5] = this.padding;
                    bArr2[i6 + 6] = this.padding;
                    bArr2[i6 + 7] = this.padding;
                    break;
                case 4:
                    bArr2[i6 + 7] = this.padding;
                    break;
            }
            i6 += 8;
        }
        return i6 - i3;
    }

    private void encodeBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        int i3 = i + 1;
        byte b = bArr[i];
        int i4 = i3 + 1;
        int i5 = bArr[i3] & 255;
        int i6 = i4 + 1;
        int i7 = bArr[i4] & 255;
        int i8 = bArr[i6] & 255;
        int i9 = bArr[i6 + 1] & 255;
        int i10 = i2 + 1;
        bArr2[i2] = this.encodingTable[(b >>> 3) & 31];
        int i11 = i10 + 1;
        bArr2[i10] = this.encodingTable[((b << 2) | (i5 >>> 6)) & 31];
        int i12 = i11 + 1;
        bArr2[i11] = this.encodingTable[(i5 >>> 1) & 31];
        int i13 = i12 + 1;
        bArr2[i12] = this.encodingTable[((i5 << 4) | (i7 >>> 4)) & 31];
        int i14 = i13 + 1;
        bArr2[i13] = this.encodingTable[((i7 << 1) | (i8 >>> 7)) & 31];
        int i15 = i14 + 1;
        bArr2[i14] = this.encodingTable[(i8 >>> 2) & 31];
        bArr2[i15] = this.encodingTable[((i8 << 3) | (i9 >>> 5)) & 31];
        bArr2[i15 + 1] = this.encodingTable[i9 & 31];
    }

    @Override // org.bouncycastle.util.encoders.Encoder
    public int getEncodedLength(int i) {
        return ((i + 4) / 5) * 8;
    }

    @Override // org.bouncycastle.util.encoders.Encoder
    public int getMaxDecodedLength(int i) {
        return (i / 8) * 5;
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
            int iMin = Math.min(45, i4);
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
        byte[] bArr2 = new byte[55];
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
        while (i7 > i && i6 != 8) {
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
                int iNextI5 = nextI(bArr, iNextI4 + 1, i5);
                int iNextI6 = nextI(bArr, iNextI5 + 1, i5);
                int iNextI7 = nextI(bArr, iNextI6 + 1, i5);
                int iNextI8 = nextI(bArr, iNextI7 + 1, i5);
                return i4 + decodeLastBlock(outputStream, (char) bArr[iNextI2], (char) bArr[iNextI3], (char) bArr[iNextI4], (char) bArr[iNextI5], (char) bArr[iNextI6], (char) bArr[iNextI7], (char) bArr[iNextI8], (char) bArr[nextI(bArr, iNextI8 + 1, i5)]);
            }
            byte b = this.decodingTable[bArr[i8]];
            int iNextI9 = nextI(bArr, i8 + 1, i7);
            int i9 = iNextI9 + 1;
            byte b2 = this.decodingTable[bArr[iNextI9]];
            int iNextI10 = nextI(bArr, i9, i7);
            int i10 = iNextI10 + 1;
            byte b3 = this.decodingTable[bArr[iNextI10]];
            int iNextI11 = nextI(bArr, i10, i7);
            int i11 = iNextI11 + 1;
            byte b4 = this.decodingTable[bArr[iNextI11]];
            int iNextI12 = nextI(bArr, i11, i7);
            int i12 = iNextI12 + 1;
            byte b5 = this.decodingTable[bArr[iNextI12]];
            int iNextI13 = nextI(bArr, i12, i7);
            int i13 = iNextI13 + 1;
            byte b6 = this.decodingTable[bArr[iNextI13]];
            int iNextI14 = nextI(bArr, i13, i7);
            int i14 = iNextI14 + 1;
            byte b7 = this.decodingTable[bArr[iNextI14]];
            int iNextI15 = nextI(bArr, i14, i7);
            int i15 = iNextI15 + 1;
            byte b8 = this.decodingTable[bArr[iNextI15]];
            if ((b | b2 | b3 | b4 | b5 | b6 | b7 | b8) < 0) {
                throw new IOException("invalid characters encountered in base32 data");
            }
            int i16 = i3;
            int i17 = i3 + 1;
            bArr2[i16] = (byte) ((b << 3) | (b2 >> 2));
            int i18 = i17 + 1;
            bArr2[i17] = (byte) ((b2 << 6) | (b3 << 1) | (b4 >> 4));
            int i19 = i18 + 1;
            bArr2[i18] = (byte) ((b4 << 4) | (b5 >> 1));
            int i20 = i19 + 1;
            bArr2[i19] = (byte) ((b5 << 7) | (b6 << 2) | (b7 >> 3));
            i3 = i20 + 1;
            bArr2[i20] = (byte) ((b7 << 5) | b8);
            if (i3 == bArr2.length) {
                outputStream.write(bArr2);
                i3 = 0;
            }
            i4 += 5;
            iNextI = nextI(bArr, i15, i7);
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
        byte[] byteArray = Strings.toByteArray(str);
        return decode(byteArray, 0, byteArray.length, outputStream);
    }

    private int decodeLastBlock(OutputStream outputStream, char c, char c2, char c3, char c4, char c5, char c6, char c7, char c8) throws IOException {
        if (c8 != this.padding) {
            byte b = this.decodingTable[c];
            byte b2 = this.decodingTable[c2];
            byte b3 = this.decodingTable[c3];
            byte b4 = this.decodingTable[c4];
            byte b5 = this.decodingTable[c5];
            byte b6 = this.decodingTable[c6];
            byte b7 = this.decodingTable[c7];
            byte b8 = this.decodingTable[c8];
            if ((b | b2 | b3 | b4 | b5 | b6 | b7 | b8) < 0) {
                throw new IOException("invalid characters encountered at end of base32 data");
            }
            outputStream.write((b << 3) | (b2 >> 2));
            outputStream.write((b2 << 6) | (b3 << 1) | (b4 >> 4));
            outputStream.write((b4 << 4) | (b5 >> 1));
            outputStream.write((b5 << 7) | (b6 << 2) | (b7 >> 3));
            outputStream.write((b7 << 5) | b8);
            return 5;
        }
        if (c7 != this.padding) {
            byte b9 = this.decodingTable[c];
            byte b10 = this.decodingTable[c2];
            byte b11 = this.decodingTable[c3];
            byte b12 = this.decodingTable[c4];
            byte b13 = this.decodingTable[c5];
            byte b14 = this.decodingTable[c6];
            byte b15 = this.decodingTable[c7];
            if ((b9 | b10 | b11 | b12 | b13 | b14 | b15) < 0) {
                throw new IOException("invalid characters encountered at end of base32 data");
            }
            outputStream.write((b9 << 3) | (b10 >> 2));
            outputStream.write((b10 << 6) | (b11 << 1) | (b12 >> 4));
            outputStream.write((b12 << 4) | (b13 >> 1));
            outputStream.write((b13 << 7) | (b14 << 2) | (b15 >> 3));
            return 4;
        }
        if (c6 != this.padding) {
            throw new IOException("invalid characters encountered at end of base32 data");
        }
        if (c5 != this.padding) {
            byte b16 = this.decodingTable[c];
            byte b17 = this.decodingTable[c2];
            byte b18 = this.decodingTable[c3];
            byte b19 = this.decodingTable[c4];
            byte b20 = this.decodingTable[c5];
            if ((b16 | b17 | b18 | b19 | b20) < 0) {
                throw new IOException("invalid characters encountered at end of base32 data");
            }
            outputStream.write((b16 << 3) | (b17 >> 2));
            outputStream.write((b17 << 6) | (b18 << 1) | (b19 >> 4));
            outputStream.write((b19 << 4) | (b20 >> 1));
            return 3;
        }
        if (c4 == this.padding) {
            if (c3 != this.padding) {
                throw new IOException("invalid characters encountered at end of base32 data");
            }
            byte b21 = this.decodingTable[c];
            byte b22 = this.decodingTable[c2];
            if ((b21 | b22) < 0) {
                throw new IOException("invalid characters encountered at end of base32 data");
            }
            outputStream.write((b21 << 3) | (b22 >> 2));
            return 1;
        }
        byte b23 = this.decodingTable[c];
        byte b24 = this.decodingTable[c2];
        byte b25 = this.decodingTable[c3];
        byte b26 = this.decodingTable[c4];
        if ((b23 | b24 | b25 | b26) < 0) {
            throw new IOException("invalid characters encountered at end of base32 data");
        }
        outputStream.write((b23 << 3) | (b24 >> 2));
        outputStream.write((b24 << 6) | (b25 << 1) | (b26 >> 4));
        return 2;
    }
}
