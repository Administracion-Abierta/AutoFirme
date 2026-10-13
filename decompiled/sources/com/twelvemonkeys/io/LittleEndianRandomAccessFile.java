package com.twelvemonkeys.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UTFDataFormatException;
import java.nio.channels.FileChannel;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/LittleEndianRandomAccessFile.class */
public class LittleEndianRandomAccessFile implements DataInput, DataOutput {
    private RandomAccessFile file;

    public LittleEndianRandomAccessFile(String str, String str2) throws FileNotFoundException {
        this(FileUtil.resolve(str), str2);
    }

    public LittleEndianRandomAccessFile(File file, String str) throws FileNotFoundException {
        this.file = new RandomAccessFile(file, str);
    }

    public void close() throws IOException {
        this.file.close();
    }

    public FileChannel getChannel() {
        return this.file.getChannel();
    }

    public FileDescriptor getFD() throws IOException {
        return this.file.getFD();
    }

    public long getFilePointer() throws IOException {
        return this.file.getFilePointer();
    }

    public long length() throws IOException {
        return this.file.length();
    }

    public int read() throws IOException {
        return this.file.read();
    }

    public int read(byte[] bArr) throws IOException {
        return this.file.read(bArr);
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        return this.file.read(bArr, i, i2);
    }

    @Override // java.io.DataInput
    public void readFully(byte[] bArr) throws IOException {
        this.file.readFully(bArr);
    }

    @Override // java.io.DataInput
    public void readFully(byte[] bArr, int i, int i2) throws IOException {
        this.file.readFully(bArr, i, i2);
    }

    @Override // java.io.DataInput
    public String readLine() throws IOException {
        return this.file.readLine();
    }

    @Override // java.io.DataInput
    public boolean readBoolean() throws IOException {
        int i = this.file.read();
        if (i < 0) {
            throw new EOFException();
        }
        return i != 0;
    }

    @Override // java.io.DataInput
    public byte readByte() throws IOException {
        int i = this.file.read();
        if (i < 0) {
            throw new EOFException();
        }
        return (byte) i;
    }

    @Override // java.io.DataInput
    public int readUnsignedByte() throws IOException {
        int i = this.file.read();
        if (i < 0) {
            throw new EOFException();
        }
        return i;
    }

    @Override // java.io.DataInput
    public short readShort() throws IOException {
        int i = this.file.read();
        int i2 = this.file.read();
        if (i2 < 0) {
            throw new EOFException();
        }
        return (short) ((((i2 << 24) >>> 16) + (i << 24)) >>> 24);
    }

    @Override // java.io.DataInput
    public int readUnsignedShort() throws IOException {
        int i = this.file.read();
        int i2 = this.file.read();
        if (i2 < 0) {
            throw new EOFException();
        }
        return (i2 << 8) + i;
    }

    @Override // java.io.DataInput
    public char readChar() throws IOException {
        int i = this.file.read();
        int i2 = this.file.read();
        if (i2 < 0) {
            throw new EOFException();
        }
        return (char) (((i2 << 24) >>> 16) + ((i << 24) >>> 24));
    }

    @Override // java.io.DataInput
    public int readInt() throws IOException {
        int i = this.file.read();
        int i2 = this.file.read();
        int i3 = this.file.read();
        int i4 = this.file.read();
        if (i4 < 0) {
            throw new EOFException();
        }
        return (i4 << 24) + ((i3 << 24) >>> 8) + ((i2 << 24) >>> 16) + ((i << 24) >>> 24);
    }

    @Override // java.io.DataInput
    public long readLong() throws IOException {
        long j = this.file.read();
        long j2 = this.file.read();
        long j3 = this.file.read();
        long j4 = this.file.read();
        long j5 = this.file.read();
        long j6 = this.file.read();
        long j7 = this.file.read();
        long j8 = this.file.read();
        if (j8 < 0) {
            throw new EOFException();
        }
        return (j8 << 56) + ((j7 << 56) >>> 8) + ((j6 << 56) >>> 16) + ((j5 << 56) >>> 24) + ((j4 << 56) >>> 32) + ((j3 << 56) >>> 40) + ((j2 << 56) >>> 48) + ((j << 56) >>> 56);
    }

    @Override // java.io.DataInput
    public String readUTF() throws IOException {
        int i = this.file.read();
        int i2 = this.file.read();
        if (i2 < 0) {
            throw new EOFException();
        }
        int i3 = (i << 8) + i2;
        char[] cArr = new char[i3];
        int i4 = 0;
        int i5 = 0;
        while (i4 < i3) {
            int unsignedByte = readUnsignedByte();
            int i6 = unsignedByte >> 4;
            if (i6 < 8) {
                i4++;
                int i7 = i5;
                i5++;
                cArr[i7] = (char) unsignedByte;
            } else if (i6 == 12 || i6 == 13) {
                i4 += 2;
                if (i4 > i3) {
                    throw new UTFDataFormatException();
                }
                int unsignedByte2 = readUnsignedByte();
                if ((unsignedByte2 & 192) != 128) {
                    throw new UTFDataFormatException();
                }
                int i8 = i5;
                i5++;
                cArr[i8] = (char) (((unsignedByte & 31) << 6) | (unsignedByte2 & 63));
            } else if (i6 == 14) {
                i4 += 3;
                if (i4 > i3) {
                    throw new UTFDataFormatException();
                }
                int unsignedByte3 = readUnsignedByte();
                int unsignedByte4 = readUnsignedByte();
                if ((unsignedByte3 & 192) != 128 || (unsignedByte4 & 192) != 128) {
                    throw new UTFDataFormatException();
                }
                int i9 = i5;
                i5++;
                cArr[i9] = (char) (((unsignedByte & 15) << 12) | ((unsignedByte3 & 63) << 6) | (unsignedByte4 & 63));
            } else {
                throw new UTFDataFormatException();
            }
        }
        return new String(cArr, 0, i5);
    }

    @Override // java.io.DataInput
    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    @Override // java.io.DataInput
    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    public void seek(long j) throws IOException {
        this.file.seek(j);
    }

    public void setLength(long j) throws IOException {
        this.file.setLength(j);
    }

    @Override // java.io.DataInput
    public int skipBytes(int i) throws IOException {
        return this.file.skipBytes(i);
    }

    @Override // java.io.DataOutput
    public void write(byte[] bArr) throws IOException {
        this.file.write(bArr);
    }

    @Override // java.io.DataOutput
    public void write(byte[] bArr, int i, int i2) throws IOException {
        this.file.write(bArr, i, i2);
    }

    @Override // java.io.DataOutput
    public void write(int i) throws IOException {
        this.file.write(i);
    }

    @Override // java.io.DataOutput
    public void writeBoolean(boolean z) throws IOException {
        if (z) {
            write(1);
        } else {
            write(0);
        }
    }

    @Override // java.io.DataOutput
    public void writeByte(int i) throws IOException {
        this.file.write(i);
    }

    @Override // java.io.DataOutput
    public void writeShort(int i) throws IOException {
        this.file.write(i & 255);
        this.file.write((i >>> 8) & 255);
    }

    @Override // java.io.DataOutput
    public void writeChar(int i) throws IOException {
        this.file.write(i & 255);
        this.file.write((i >>> 8) & 255);
    }

    @Override // java.io.DataOutput
    public void writeInt(int i) throws IOException {
        this.file.write(i & 255);
        this.file.write((i >>> 8) & 255);
        this.file.write((i >>> 16) & 255);
        this.file.write((i >>> 24) & 255);
    }

    @Override // java.io.DataOutput
    public void writeLong(long j) throws IOException {
        this.file.write(((int) j) & 255);
        this.file.write(((int) (j >>> 8)) & 255);
        this.file.write(((int) (j >>> 16)) & 255);
        this.file.write(((int) (j >>> 24)) & 255);
        this.file.write(((int) (j >>> 32)) & 255);
        this.file.write(((int) (j >>> 40)) & 255);
        this.file.write(((int) (j >>> 48)) & 255);
        this.file.write(((int) (j >>> 56)) & 255);
    }

    @Override // java.io.DataOutput
    public final void writeFloat(float f) throws IOException {
        writeInt(Float.floatToIntBits(f));
    }

    @Override // java.io.DataOutput
    public final void writeDouble(double d) throws IOException {
        writeLong(Double.doubleToLongBits(d));
    }

    @Override // java.io.DataOutput
    public void writeBytes(String str) throws IOException {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            this.file.write((byte) str.charAt(i));
        }
    }

    @Override // java.io.DataOutput
    public void writeChars(String str) throws IOException {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            this.file.write(cCharAt & 255);
            this.file.write((cCharAt >>> '\b') & 255);
        }
    }

    @Override // java.io.DataOutput
    public void writeUTF(String str) throws IOException {
        int length = str.length();
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            char cCharAt = str.charAt(i2);
            if (cCharAt >= 1 && cCharAt <= 127) {
                i++;
            } else if (cCharAt > 2047) {
                i += 3;
            } else {
                i += 2;
            }
        }
        if (i > 65535) {
            throw new UTFDataFormatException();
        }
        this.file.write((i >>> 8) & 255);
        this.file.write(i & 255);
        for (int i3 = 0; i3 < length; i3++) {
            char cCharAt2 = str.charAt(i3);
            if (cCharAt2 >= 1 && cCharAt2 <= 127) {
                this.file.write(cCharAt2);
            } else if (cCharAt2 > 2047) {
                this.file.write(224 | ((cCharAt2 >> '\f') & 15));
                this.file.write(128 | ((cCharAt2 >> 6) & 63));
                this.file.write(128 | (cCharAt2 & '?'));
            } else {
                this.file.write(192 | ((cCharAt2 >> 6) & 31));
                this.file.write(128 | (cCharAt2 & '?'));
            }
        }
    }
}
