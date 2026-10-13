package org.spongycastle.util.encoders;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/util/encoders/Base64Encoder.class */
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

    @Override // org.spongycastle.util.encoders.Encoder
    public int encode(byte[] data, int off, int length, OutputStream out) throws IOException {
        int modulus = length % 3;
        int dataLength = length - modulus;
        for (int i = off; i < off + dataLength; i += 3) {
            int a1 = data[i] & 255;
            int a2 = data[i + 1] & 255;
            int a3 = data[i + 2] & 255;
            out.write(this.encodingTable[(a1 >>> 2) & 63]);
            out.write(this.encodingTable[((a1 << 4) | (a2 >>> 4)) & 63]);
            out.write(this.encodingTable[((a2 << 2) | (a3 >>> 6)) & 63]);
            out.write(this.encodingTable[a3 & 63]);
        }
        switch (modulus) {
            case 1:
                int d1 = data[off + dataLength] & 255;
                int b1 = (d1 >>> 2) & 63;
                int b2 = (d1 << 4) & 63;
                out.write(this.encodingTable[b1]);
                out.write(this.encodingTable[b2]);
                out.write(this.padding);
                out.write(this.padding);
                break;
            case 2:
                int d2 = data[off + dataLength] & 255;
                int d3 = data[off + dataLength + 1] & 255;
                int b3 = (d2 >>> 2) & 63;
                int b4 = ((d2 << 4) | (d3 >>> 4)) & 63;
                int b5 = (d3 << 2) & 63;
                out.write(this.encodingTable[b3]);
                out.write(this.encodingTable[b4]);
                out.write(this.encodingTable[b5]);
                out.write(this.padding);
                break;
        }
        return ((dataLength / 3) * 4) + (modulus == 0 ? 0 : 4);
    }

    private boolean ignore(char c) {
        return c == '\n' || c == '\r' || c == '\t' || c == ' ';
    }

    @Override // org.spongycastle.util.encoders.Encoder
    public int decode(byte[] data, int off, int length, OutputStream out) throws IOException {
        int outLen = 0;
        int end = off + length;
        while (end > off && ignore((char) data[end - 1])) {
            end--;
        }
        int finish = end - 4;
        int iNextI = nextI(data, off, finish);
        while (true) {
            int i = iNextI;
            if (i < finish) {
                byte b1 = this.decodingTable[data[i]];
                int i2 = nextI(data, i + 1, finish);
                int i3 = i2 + 1;
                byte b2 = this.decodingTable[data[i2]];
                int i4 = nextI(data, i3, finish);
                int i5 = i4 + 1;
                byte b3 = this.decodingTable[data[i4]];
                int i6 = nextI(data, i5, finish);
                int i7 = i6 + 1;
                byte b4 = this.decodingTable[data[i6]];
                if ((b1 | b2 | b3 | b4) < 0) {
                    throw new IOException("invalid characters encountered in base64 data");
                }
                out.write((b1 << 2) | (b2 >> 4));
                out.write((b2 << 4) | (b3 >> 2));
                out.write((b3 << 6) | b4);
                outLen += 3;
                iNextI = nextI(data, i7, finish);
            } else {
                return outLen + decodeLastBlock(out, (char) data[end - 4], (char) data[end - 3], (char) data[end - 2], (char) data[end - 1]);
            }
        }
    }

    private int nextI(byte[] data, int i, int finish) {
        while (i < finish && ignore((char) data[i])) {
            i++;
        }
        return i;
    }

    @Override // org.spongycastle.util.encoders.Encoder
    public int decode(String data, OutputStream out) throws IOException {
        int length = 0;
        int end = data.length();
        while (end > 0 && ignore(data.charAt(end - 1))) {
            end--;
        }
        int finish = end - 4;
        int iNextI = nextI(data, 0, finish);
        while (true) {
            int i = iNextI;
            if (i < finish) {
                byte b1 = this.decodingTable[data.charAt(i)];
                int i2 = nextI(data, i + 1, finish);
                int i3 = i2 + 1;
                byte b2 = this.decodingTable[data.charAt(i2)];
                int i4 = nextI(data, i3, finish);
                int i5 = i4 + 1;
                byte b3 = this.decodingTable[data.charAt(i4)];
                int i6 = nextI(data, i5, finish);
                int i7 = i6 + 1;
                byte b4 = this.decodingTable[data.charAt(i6)];
                if ((b1 | b2 | b3 | b4) < 0) {
                    throw new IOException("invalid characters encountered in base64 data");
                }
                out.write((b1 << 2) | (b2 >> 4));
                out.write((b2 << 4) | (b3 >> 2));
                out.write((b3 << 6) | b4);
                length += 3;
                iNextI = nextI(data, i7, finish);
            } else {
                return length + decodeLastBlock(out, data.charAt(end - 4), data.charAt(end - 3), data.charAt(end - 2), data.charAt(end - 1));
            }
        }
    }

    private int decodeLastBlock(OutputStream out, char c1, char c2, char c3, char c4) throws IOException {
        if (c3 == this.padding) {
            if (c4 != this.padding) {
                throw new IOException("invalid characters encountered at end of base64 data");
            }
            byte b1 = this.decodingTable[c1];
            byte b2 = this.decodingTable[c2];
            if ((b1 | b2) < 0) {
                throw new IOException("invalid characters encountered at end of base64 data");
            }
            out.write((b1 << 2) | (b2 >> 4));
            return 1;
        }
        if (c4 == this.padding) {
            byte b3 = this.decodingTable[c1];
            byte b4 = this.decodingTable[c2];
            byte b5 = this.decodingTable[c3];
            if ((b3 | b4 | b5) < 0) {
                throw new IOException("invalid characters encountered at end of base64 data");
            }
            out.write((b3 << 2) | (b4 >> 4));
            out.write((b4 << 4) | (b5 >> 2));
            return 2;
        }
        byte b6 = this.decodingTable[c1];
        byte b7 = this.decodingTable[c2];
        byte b8 = this.decodingTable[c3];
        byte b9 = this.decodingTable[c4];
        if ((b6 | b7 | b8 | b9) < 0) {
            throw new IOException("invalid characters encountered at end of base64 data");
        }
        out.write((b6 << 2) | (b7 >> 4));
        out.write((b7 << 4) | (b8 >> 2));
        out.write((b8 << 6) | b9);
        return 3;
    }

    private int nextI(String data, int i, int finish) {
        while (i < finish && ignore(data.charAt(i))) {
            i++;
        }
        return i;
    }
}
