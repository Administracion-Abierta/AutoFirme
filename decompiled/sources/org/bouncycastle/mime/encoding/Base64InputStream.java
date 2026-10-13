package org.bouncycastle.mime.encoding;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/mime/encoding/Base64InputStream.class */
public class Base64InputStream extends InputStream {
    private static final byte[] decodingTable = new byte[128];
    InputStream in;
    int[] outBuf = new int[3];
    int bufPtr = 3;

    private int decode(int i, int i2, int i3, int i4, int[] iArr) throws EOFException {
        if (i4 < 0) {
            throw new EOFException("unexpected end of file in armored stream.");
        }
        if (i3 == 61) {
            iArr[2] = (((decodingTable[i] & 255) << 2) | ((decodingTable[i2] & 255) >> 4)) & 255;
            return 2;
        }
        if (i4 == 61) {
            byte b = decodingTable[i];
            byte b2 = decodingTable[i2];
            byte b3 = decodingTable[i3];
            iArr[1] = ((b << 2) | (b2 >> 4)) & 255;
            iArr[2] = ((b2 << 4) | (b3 >> 2)) & 255;
            return 1;
        }
        byte b4 = decodingTable[i];
        byte b5 = decodingTable[i2];
        byte b6 = decodingTable[i3];
        byte b7 = decodingTable[i4];
        iArr[0] = ((b4 << 2) | (b5 >> 4)) & 255;
        iArr[1] = ((b5 << 4) | (b6 >> 2)) & 255;
        iArr[2] = ((b6 << 6) | b7) & 255;
        return 0;
    }

    public Base64InputStream(InputStream inputStream) {
        this.in = inputStream;
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        return 0;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        if (this.bufPtr > 2) {
            int ignoreSpaceFirst = readIgnoreSpaceFirst();
            if (ignoreSpaceFirst < 0) {
                return -1;
            }
            this.bufPtr = decode(ignoreSpaceFirst, readIgnoreSpace(), readIgnoreSpace(), readIgnoreSpace(), this.outBuf);
        }
        int[] iArr = this.outBuf;
        int i = this.bufPtr;
        this.bufPtr = i + 1;
        return iArr[i];
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.in.close();
    }

    private int readIgnoreSpace() throws IOException {
        while (true) {
            int i = this.in.read();
            switch (i) {
                case 9:
                case 32:
                    break;
                default:
                    return i;
            }
        }
    }

    private int readIgnoreSpaceFirst() throws IOException {
        while (true) {
            int i = this.in.read();
            switch (i) {
                case 9:
                case 10:
                case 13:
                case 32:
                    break;
                default:
                    return i;
            }
        }
    }

    static {
        for (int i = 65; i <= 90; i++) {
            decodingTable[i] = (byte) (i - 65);
        }
        for (int i2 = 97; i2 <= 122; i2++) {
            decodingTable[i2] = (byte) ((i2 - 97) + 26);
        }
        for (int i3 = 48; i3 <= 57; i3++) {
            decodingTable[i3] = (byte) ((i3 - 48) + 52);
        }
        decodingTable[43] = 62;
        decodingTable[47] = 63;
    }
}
