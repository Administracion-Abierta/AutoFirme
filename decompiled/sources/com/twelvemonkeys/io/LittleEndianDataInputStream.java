package com.twelvemonkeys.io;

import com.twelvemonkeys.lang.Validate;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/LittleEndianDataInputStream.class */
public class LittleEndianDataInputStream extends FilterInputStream implements DataInput {
    public LittleEndianDataInputStream(InputStream inputStream) {
        super((InputStream) Validate.notNull(inputStream, "stream"));
    }

    @Override // java.io.DataInput
    public boolean readBoolean() throws IOException {
        int i = this.in.read();
        if (i < 0) {
            throw new EOFException();
        }
        return i != 0;
    }

    @Override // java.io.DataInput
    public byte readByte() throws IOException {
        int i = this.in.read();
        if (i < 0) {
            throw new EOFException();
        }
        return (byte) i;
    }

    @Override // java.io.DataInput
    public int readUnsignedByte() throws IOException {
        int i = this.in.read();
        if (i < 0) {
            throw new EOFException();
        }
        return i;
    }

    @Override // java.io.DataInput
    public short readShort() throws IOException {
        int i = this.in.read();
        int i2 = this.in.read();
        if (i2 < 0) {
            throw new EOFException();
        }
        return (short) (((i2 << 24) >>> 16) | ((i << 24) >>> 24));
    }

    @Override // java.io.DataInput
    public int readUnsignedShort() throws IOException {
        int i = this.in.read();
        int i2 = this.in.read();
        if (i2 < 0) {
            throw new EOFException();
        }
        return (i2 << 8) + i;
    }

    @Override // java.io.DataInput
    public char readChar() throws IOException {
        int i = this.in.read();
        int i2 = this.in.read();
        if (i2 < 0) {
            throw new EOFException();
        }
        return (char) (((i2 << 24) >>> 16) | ((i << 24) >>> 24));
    }

    @Override // java.io.DataInput
    public int readInt() throws IOException {
        int i = this.in.read();
        int i2 = this.in.read();
        int i3 = this.in.read();
        int i4 = this.in.read();
        if (i4 < 0) {
            throw new EOFException();
        }
        return (i4 << 24) | ((i3 << 24) >>> 8) | ((i2 << 24) >>> 16) | ((i << 24) >>> 24);
    }

    @Override // java.io.DataInput
    public long readLong() throws IOException {
        long j = this.in.read();
        long j2 = this.in.read();
        long j3 = this.in.read();
        long j4 = this.in.read();
        long j5 = this.in.read();
        long j6 = this.in.read();
        long j7 = this.in.read();
        long j8 = this.in.read();
        if (j8 < 0) {
            throw new EOFException();
        }
        return (j8 << 56) | ((j7 << 56) >>> 8) | ((j6 << 56) >>> 16) | ((j5 << 56) >>> 24) | ((j4 << 56) >>> 32) | ((j3 << 56) >>> 40) | ((j2 << 56) >>> 48) | ((j << 56) >>> 56);
    }

    @Override // java.io.DataInput
    public String readUTF() throws IOException {
        int i = this.in.read();
        int i2 = this.in.read();
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

    @Override // java.io.DataInput
    public final int skipBytes(int i) throws IOException {
        int i2;
        int iSkip;
        int i3 = 0;
        while (true) {
            i2 = i3;
            if (i2 >= i || (iSkip = (int) this.in.skip(i - i2)) <= 0) {
                break;
            }
            i3 = i2 + iSkip;
        }
        return i2;
    }

    @Override // java.io.DataInput
    public final void readFully(byte[] bArr) throws IOException {
        readFully(bArr, 0, bArr.length);
    }

    @Override // java.io.DataInput
    public final void readFully(byte[] bArr, int i, int i2) throws IOException {
        if (i2 < 0) {
            throw new IndexOutOfBoundsException();
        }
        int i3 = 0;
        while (true) {
            int i4 = i3;
            if (i4 < i2) {
                int i5 = this.in.read(bArr, i + i4, i2 - i4);
                if (i5 < 0) {
                    throw new EOFException();
                }
                i3 = i4 + i5;
            } else {
                return;
            }
        }
    }

    @Override // java.io.DataInput
    @Deprecated
    public String readLine() throws IOException {
        return new DataInputStream(this.in).readLine();
    }
}
