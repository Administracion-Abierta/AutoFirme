package org.bouncycastle.mime;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/mime/BoundaryLimitedInputStream.class */
public class BoundaryLimitedInputStream extends InputStream {
    private final InputStream src;
    private final byte[] boundary;
    private final byte[] buf;
    private int bufOff;
    private int index = 0;
    private boolean ended = false;
    private int lastI;

    public BoundaryLimitedInputStream(InputStream inputStream, String str) {
        this.bufOff = 0;
        this.src = inputStream;
        this.boundary = Strings.toByteArray(str);
        this.buf = new byte[str.length() + 3];
        this.bufOff = 0;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        int i;
        int i2;
        int i3;
        if (this.ended) {
            return -1;
        }
        if (this.index < this.bufOff) {
            byte[] bArr = this.buf;
            int i4 = this.index;
            this.index = i4 + 1;
            i = bArr[i4] & 255;
            if (this.index < this.bufOff) {
                return i;
            }
            this.bufOff = 0;
            this.index = 0;
        } else {
            i = this.src.read();
        }
        this.lastI = i;
        if (i < 0) {
            return -1;
        }
        if (i == 13 || i == 10) {
            this.index = 0;
            if (i == 13) {
                i2 = this.src.read();
                if (i2 == 10) {
                    byte[] bArr2 = this.buf;
                    int i5 = this.bufOff;
                    this.bufOff = i5 + 1;
                    bArr2[i5] = 10;
                    i2 = this.src.read();
                }
            } else {
                i2 = this.src.read();
            }
            if (i2 == 45) {
                byte[] bArr3 = this.buf;
                int i6 = this.bufOff;
                this.bufOff = i6 + 1;
                bArr3[i6] = 45;
                i2 = this.src.read();
            }
            if (i2 == 45) {
                byte[] bArr4 = this.buf;
                int i7 = this.bufOff;
                this.bufOff = i7 + 1;
                bArr4[i7] = 45;
                int i8 = this.bufOff;
                while (this.bufOff - i8 != this.boundary.length && (i3 = this.src.read()) >= 0) {
                    this.buf[this.bufOff] = (byte) i3;
                    if (this.buf[this.bufOff] != this.boundary[this.bufOff - i8]) {
                        this.bufOff++;
                        break;
                    }
                    this.bufOff++;
                }
                if (this.bufOff - i8 == this.boundary.length) {
                    this.ended = true;
                    return -1;
                }
            } else if (i2 >= 0) {
                byte[] bArr5 = this.buf;
                int i9 = this.bufOff;
                this.bufOff = i9 + 1;
                bArr5[i9] = (byte) i2;
            }
        }
        return i;
    }
}
