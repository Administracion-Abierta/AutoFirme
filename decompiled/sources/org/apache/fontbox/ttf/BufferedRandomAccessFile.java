package org.apache.fontbox.ttf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/BufferedRandomAccessFile.class */
public class BufferedRandomAccessFile extends RandomAccessFile {
    private final byte[] buffer;
    private int bufend;
    private int bufpos;
    private long realpos;

    public BufferedRandomAccessFile(String filename, String mode, int bufsize) throws FileNotFoundException {
        super(filename, mode);
        this.bufend = 0;
        this.bufpos = 0;
        this.realpos = 0L;
        this.buffer = new byte[bufsize];
    }

    public BufferedRandomAccessFile(File file, String mode, int bufsize) throws FileNotFoundException {
        super(file, mode);
        this.bufend = 0;
        this.bufpos = 0;
        this.realpos = 0L;
        this.buffer = new byte[bufsize];
    }

    @Override // java.io.RandomAccessFile
    public final int read() throws IOException {
        if ((this.bufpos >= this.bufend && fillBuffer() < 0) || this.bufend == 0) {
            return -1;
        }
        byte[] bArr = this.buffer;
        int i = this.bufpos;
        this.bufpos = i + 1;
        return (bArr[i] + 256) & 255;
    }

    private int fillBuffer() throws IOException {
        int n = super.read(this.buffer);
        if (n >= 0) {
            this.realpos += (long) n;
            this.bufend = n;
            this.bufpos = 0;
        }
        return n;
    }

    private void invalidate() throws IOException {
        this.bufend = 0;
        this.bufpos = 0;
        this.realpos = super.getFilePointer();
    }

    @Override // java.io.RandomAccessFile
    public int read(byte[] b, int off, int len) throws IOException {
        int curLen = len;
        int curOff = off;
        int totalRead = 0;
        while (true) {
            int leftover = this.bufend - this.bufpos;
            if (curLen <= leftover) {
                System.arraycopy(this.buffer, this.bufpos, b, curOff, curLen);
                this.bufpos += curLen;
                return totalRead + curLen;
            }
            System.arraycopy(this.buffer, this.bufpos, b, curOff, leftover);
            totalRead += leftover;
            this.bufpos += leftover;
            if (fillBuffer() > 0) {
                curOff += leftover;
                curLen -= leftover;
            } else {
                if (totalRead == 0) {
                    return -1;
                }
                return totalRead;
            }
        }
    }

    @Override // java.io.RandomAccessFile
    public long getFilePointer() throws IOException {
        return (this.realpos - ((long) this.bufend)) + ((long) this.bufpos);
    }

    @Override // java.io.RandomAccessFile
    public void seek(long pos) throws IOException {
        int n = (int) (this.realpos - pos);
        if (n >= 0 && n <= this.bufend) {
            this.bufpos = this.bufend - n;
        } else {
            super.seek(pos);
            invalidate();
        }
    }
}
