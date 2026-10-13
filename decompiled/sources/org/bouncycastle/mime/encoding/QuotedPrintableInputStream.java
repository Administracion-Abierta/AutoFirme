package org.bouncycastle.mime.encoding;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/mime/encoding/QuotedPrintableInputStream.class */
public class QuotedPrintableInputStream extends FilterInputStream {
    public QuotedPrintableInputStream(InputStream inputStream) {
        super(inputStream);
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        int i3;
        int i4 = 0;
        while (i4 != i2 && (i3 = read()) >= 0) {
            bArr[i4 + i] = (byte) i3;
            i4++;
        }
        if (i4 == 0) {
            return -1;
        }
        return i4;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        int i;
        int i2;
        int i3 = this.in.read();
        if (i3 == -1) {
            return -1;
        }
        while (i3 == 61) {
            int i4 = this.in.read();
            if (i4 == -1) {
                throw new IllegalStateException("Quoted '=' at end of stream");
            }
            if (i4 == 13) {
                int i5 = this.in.read();
                if (i5 == 10) {
                    i5 = this.in.read();
                }
                i3 = i5;
            } else {
                if (i4 != 10) {
                    if (i4 >= 48 && i4 <= 57) {
                        i = i4 - 48;
                    } else {
                        if (i4 < 65 || i4 > 70) {
                            throw new IllegalStateException("Expecting '0123456789ABCDEF after quote that was not immediately followed by LF or CRLF");
                        }
                        i = 10 + (i4 - 65);
                    }
                    int i6 = i << 4;
                    int i7 = this.in.read();
                    if (i7 >= 48 && i7 <= 57) {
                        i2 = i6 | (i7 - 48);
                    } else {
                        if (i7 < 65 || i7 > 70) {
                            throw new IllegalStateException("Expecting second '0123456789ABCDEF after quote that was not immediately followed by LF or CRLF");
                        }
                        i2 = i6 | (10 + (i7 - 65));
                    }
                    return i2;
                }
                i3 = this.in.read();
            }
        }
        return i3;
    }
}
