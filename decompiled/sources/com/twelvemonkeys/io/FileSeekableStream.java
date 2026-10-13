package com.twelvemonkeys.io;

import com.sun.jna.platform.win32.WinNT;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/FileSeekableStream.class */
public final class FileSeekableStream extends SeekableInputStream {
    final RandomAccessFile mRandomAccess;

    public FileSeekableStream(File file) throws FileNotFoundException {
        this(new RandomAccessFile(file, PDPageLabelRange.STYLE_ROMAN_LOWER));
    }

    public FileSeekableStream(RandomAccessFile randomAccessFile) {
        this.mRandomAccess = randomAccessFile;
    }

    @Override // com.twelvemonkeys.io.Seekable
    public boolean isCached() {
        return false;
    }

    @Override // com.twelvemonkeys.io.Seekable
    public boolean isCachedFile() {
        return false;
    }

    @Override // com.twelvemonkeys.io.Seekable
    public boolean isCachedMemory() {
        return false;
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        long length = this.mRandomAccess.length() - this.position;
        return length > 2147483647L ? WinNT.MAXLONG : (int) length;
    }

    @Override // com.twelvemonkeys.io.SeekableInputStream
    public void closeImpl() throws IOException {
        this.mRandomAccess.close();
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        checkOpen();
        int i = this.mRandomAccess.read();
        if (i >= 0) {
            this.position++;
        }
        return i;
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        checkOpen();
        int i3 = this.mRandomAccess.read(bArr, i, i2);
        if (i3 > 0) {
            this.position += (long) i3;
        }
        return i3;
    }

    @Override // com.twelvemonkeys.io.SeekableInputStream
    protected void flushBeforeImpl(long j) {
    }

    @Override // com.twelvemonkeys.io.SeekableInputStream
    protected void seekImpl(long j) throws IOException {
        this.mRandomAccess.seek(j);
    }
}
