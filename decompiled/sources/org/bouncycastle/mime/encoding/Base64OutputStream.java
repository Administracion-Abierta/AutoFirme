package org.bouncycastle.mime.encoding;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.util.encoders.Base64Encoder;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/mime/encoding/Base64OutputStream.class */
public class Base64OutputStream extends FilterOutputStream {
    private static final Base64Encoder ENCODER = new Base64Encoder();
    private static final int INBUF_SIZE = 54;
    private static final int OUTBUF_SIZE = 74;
    private final byte[] inBuf;
    private final byte[] outBuf;
    private int inPos;

    public Base64OutputStream(OutputStream outputStream) {
        super(outputStream);
        this.inBuf = new byte[54];
        this.outBuf = new byte[74];
        this.inPos = 0;
        this.outBuf[72] = 13;
        this.outBuf[73] = 10;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int i) throws IOException {
        byte[] bArr = this.inBuf;
        int i2 = this.inPos;
        this.inPos = i2 + 1;
        bArr[i2] = (byte) i;
        if (this.inPos == 54) {
            encodeBlock(this.inBuf, 0);
            this.inPos = 0;
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] bArr, int i, int i2) throws IOException {
        int i3 = 54 - this.inPos;
        if (i2 < i3) {
            System.arraycopy(bArr, i, this.inBuf, this.inPos, i2);
            this.inPos += i2;
            return;
        }
        int i4 = 0;
        if (this.inPos > 0) {
            System.arraycopy(bArr, i, this.inBuf, this.inPos, i3);
            i4 = 0 + i3;
            encodeBlock(this.inBuf, 0);
        }
        while (true) {
            int i5 = i2 - i4;
            if (i5 < 54) {
                System.arraycopy(bArr, i + i4, this.inBuf, 0, i5);
                this.inPos = i5;
                return;
            } else {
                encodeBlock(bArr, i + i4);
                i4 += 54;
            }
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] bArr) throws IOException {
        write(bArr, 0, bArr.length);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.inPos > 0) {
            int iEncode = ENCODER.encode(this.inBuf, 0, this.inPos, this.outBuf, 0);
            this.inPos = 0;
            int i = iEncode + 1;
            this.outBuf[iEncode] = 13;
            this.outBuf[i] = 10;
            this.out.write(this.outBuf, 0, i + 1);
        }
        this.out.close();
    }

    private void encodeBlock(byte[] bArr, int i) throws IOException {
        ENCODER.encode(bArr, i, 54, this.outBuf, 0);
        this.out.write(this.outBuf, 0, 74);
    }
}
