package org.spongycastle.util.encoders;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/util/encoders/HexEncoder.class */
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

    @Override // org.spongycastle.util.encoders.Encoder
    public int encode(byte[] data, int off, int length, OutputStream out) throws IOException {
        for (int i = off; i < off + length; i++) {
            int v = data[i] & 255;
            out.write(this.encodingTable[v >>> 4]);
            out.write(this.encodingTable[v & 15]);
        }
        return length * 2;
    }

    private static boolean ignore(char c) {
        return c == '\n' || c == '\r' || c == '\t' || c == ' ';
    }

    @Override // org.spongycastle.util.encoders.Encoder
    public int decode(byte[] data, int off, int length, OutputStream out) throws IOException {
        int outLen = 0;
        int end = off + length;
        while (end > off && ignore((char) data[end - 1])) {
            end--;
        }
        int i = off;
        while (i < end) {
            while (i < end && ignore((char) data[i])) {
                i++;
            }
            int i2 = i;
            int i3 = i + 1;
            byte b1 = this.decodingTable[data[i2]];
            while (i3 < end && ignore((char) data[i3])) {
                i3++;
            }
            int i4 = i3;
            i = i3 + 1;
            byte b2 = this.decodingTable[data[i4]];
            if ((b1 | b2) < 0) {
                throw new IOException("invalid characters encountered in Hex data");
            }
            out.write((b1 << 4) | b2);
            outLen++;
        }
        return outLen;
    }

    @Override // org.spongycastle.util.encoders.Encoder
    public int decode(String data, OutputStream out) throws IOException {
        int length = 0;
        int end = data.length();
        while (end > 0 && ignore(data.charAt(end - 1))) {
            end--;
        }
        int i = 0;
        while (i < end) {
            while (i < end && ignore(data.charAt(i))) {
                i++;
            }
            int i2 = i;
            int i3 = i + 1;
            byte b1 = this.decodingTable[data.charAt(i2)];
            while (i3 < end && ignore(data.charAt(i3))) {
                i3++;
            }
            int i4 = i3;
            i = i3 + 1;
            byte b2 = this.decodingTable[data.charAt(i4)];
            if ((b1 | b2) < 0) {
                throw new IOException("invalid characters encountered in Hex string");
            }
            out.write((b1 << 4) | b2);
            length++;
        }
        return length;
    }
}
