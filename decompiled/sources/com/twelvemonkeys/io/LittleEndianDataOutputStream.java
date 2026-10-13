package com.twelvemonkeys.io;

import com.twelvemonkeys.lang.Validate;
import java.io.DataOutput;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UTFDataFormatException;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/LittleEndianDataOutputStream.class */
public class LittleEndianDataOutputStream extends FilterOutputStream implements DataOutput {
    protected int bytesWritten;

    public LittleEndianDataOutputStream(OutputStream outputStream) {
        super((OutputStream) Validate.notNull(outputStream, "stream"));
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.DataOutput
    public synchronized void write(int i) throws IOException {
        this.out.write(i);
        this.bytesWritten++;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.DataOutput
    public synchronized void write(byte[] bArr, int i, int i2) throws IOException {
        this.out.write(bArr, i, i2);
        this.bytesWritten += i2;
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
        this.out.write(i);
        this.bytesWritten++;
    }

    @Override // java.io.DataOutput
    public void writeShort(int i) throws IOException {
        this.out.write(i & 255);
        this.out.write((i >>> 8) & 255);
        this.bytesWritten += 2;
    }

    @Override // java.io.DataOutput
    public void writeChar(int i) throws IOException {
        this.out.write(i & 255);
        this.out.write((i >>> 8) & 255);
        this.bytesWritten += 2;
    }

    @Override // java.io.DataOutput
    public void writeInt(int i) throws IOException {
        this.out.write(i & 255);
        this.out.write((i >>> 8) & 255);
        this.out.write((i >>> 16) & 255);
        this.out.write((i >>> 24) & 255);
        this.bytesWritten += 4;
    }

    @Override // java.io.DataOutput
    public void writeLong(long j) throws IOException {
        this.out.write(((int) j) & 255);
        this.out.write(((int) (j >>> 8)) & 255);
        this.out.write(((int) (j >>> 16)) & 255);
        this.out.write(((int) (j >>> 24)) & 255);
        this.out.write(((int) (j >>> 32)) & 255);
        this.out.write(((int) (j >>> 40)) & 255);
        this.out.write(((int) (j >>> 48)) & 255);
        this.out.write(((int) (j >>> 56)) & 255);
        this.bytesWritten += 8;
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
            this.out.write((byte) str.charAt(i));
        }
        this.bytesWritten += length;
    }

    @Override // java.io.DataOutput
    public void writeChars(String str) throws IOException {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            this.out.write(cCharAt & 255);
            this.out.write((cCharAt >>> '\b') & 255);
        }
        this.bytesWritten += length * 2;
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
        this.out.write((i >>> 8) & 255);
        this.out.write(i & 255);
        for (int i3 = 0; i3 < length; i3++) {
            char cCharAt2 = str.charAt(i3);
            if (cCharAt2 >= 1 && cCharAt2 <= 127) {
                this.out.write(cCharAt2);
            } else if (cCharAt2 > 2047) {
                this.out.write(224 | ((cCharAt2 >> '\f') & 15));
                this.out.write(128 | ((cCharAt2 >> 6) & 63));
                this.out.write(128 | (cCharAt2 & '?'));
                this.bytesWritten += 2;
            } else {
                this.out.write(192 | ((cCharAt2 >> 6) & 31));
                this.out.write(128 | (cCharAt2 & '?'));
                this.bytesWritten++;
            }
        }
        this.bytesWritten += length + 2;
    }

    public int size() {
        return this.bytesWritten;
    }
}
