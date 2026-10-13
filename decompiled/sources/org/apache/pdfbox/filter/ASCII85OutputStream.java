package org.apache.pdfbox.filter;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/filter/ASCII85OutputStream.class */
final class ASCII85OutputStream extends FilterOutputStream {
    private int lineBreak;
    private int count;
    private byte[] indata;
    private byte[] outdata;
    private int maxline;
    private boolean flushed;
    private char terminator;
    private static final char OFFSET = '!';
    private static final char NEWLINE = '\n';
    private static final char Z = 'z';

    ASCII85OutputStream(OutputStream out) {
        super(out);
        this.lineBreak = 72;
        this.maxline = 72;
        this.count = 0;
        this.indata = new byte[4];
        this.outdata = new byte[5];
        this.flushed = true;
        this.terminator = '~';
    }

    public void setTerminator(char term) {
        if (term < 'v' || term > '~' || term == 'z') {
            throw new IllegalArgumentException("Terminator must be 118-126 excluding z");
        }
        this.terminator = term;
    }

    public char getTerminator() {
        return this.terminator;
    }

    public void setLineLength(int l) {
        if (this.lineBreak > l) {
            this.lineBreak = l;
        }
        this.maxline = l;
    }

    public int getLineLength() {
        return this.maxline;
    }

    private void transformASCII85() {
        long word = ((long) ((((this.indata[0] << 8) | (this.indata[1] & 255)) << 16) | ((this.indata[2] & 255) << 8) | (this.indata[3] & 255))) & 4294967295L;
        if (word == 0) {
            this.outdata[0] = 122;
            this.outdata[1] = 0;
            return;
        }
        long x = word / 52200625;
        this.outdata[0] = (byte) (x + 33);
        long word2 = word - ((((x * 85) * 85) * 85) * 85);
        long x2 = word2 / 614125;
        this.outdata[1] = (byte) (x2 + 33);
        long word3 = word2 - (((x2 * 85) * 85) * 85);
        long x3 = word3 / 7225;
        this.outdata[2] = (byte) (x3 + 33);
        long word4 = word3 - ((x3 * 85) * 85);
        this.outdata[3] = (byte) ((word4 / 85) + 33);
        this.outdata[4] = (byte) ((word4 % 85) + 33);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int b) throws IOException {
        this.flushed = false;
        byte[] bArr = this.indata;
        int i = this.count;
        this.count = i + 1;
        bArr[i] = (byte) b;
        if (this.count < 4) {
            return;
        }
        transformASCII85();
        for (int i2 = 0; i2 < 5 && this.outdata[i2] != 0; i2++) {
            this.out.write(this.outdata[i2]);
            int i3 = this.lineBreak - 1;
            this.lineBreak = i3;
            if (i3 == 0) {
                this.out.write(10);
                this.lineBreak = this.maxline;
            }
        }
        this.count = 0;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        if (this.flushed) {
            return;
        }
        if (this.count > 0) {
            for (int i = this.count; i < 4; i++) {
                this.indata[i] = 0;
            }
            transformASCII85();
            if (this.outdata[0] == 122) {
                for (int i2 = 0; i2 < 5; i2++) {
                    this.outdata[i2] = 33;
                }
            }
            for (int i3 = 0; i3 < this.count + 1; i3++) {
                this.out.write(this.outdata[i3]);
                int i4 = this.lineBreak - 1;
                this.lineBreak = i4;
                if (i4 == 0) {
                    this.out.write(10);
                    this.lineBreak = this.maxline;
                }
            }
        }
        int i5 = this.lineBreak - 1;
        this.lineBreak = i5;
        if (i5 == 0) {
            this.out.write(10);
        }
        this.out.write(this.terminator);
        this.out.write(62);
        this.out.write(10);
        this.count = 0;
        this.lineBreak = this.maxline;
        this.flushed = true;
        super.flush();
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        try {
            flush();
            super.close();
        } finally {
            byte[] bArr = null;
            this.outdata = bArr;
            this.indata = bArr;
        }
    }
}
