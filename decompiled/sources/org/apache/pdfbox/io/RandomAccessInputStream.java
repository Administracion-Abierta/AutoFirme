package org.apache.pdfbox.io;

import com.sun.jna.platform.win32.WinNT;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/io/RandomAccessInputStream.class */
public class RandomAccessInputStream extends InputStream {
    private static final Log LOG = LogFactory.getLog(RandomAccessInputStream.class);
    private final RandomAccessRead input;
    private long position = 0;

    public RandomAccessInputStream(RandomAccessRead randomAccessRead) {
        this.input = randomAccessRead;
    }

    void restorePosition() throws IOException {
        this.input.seek(this.position);
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        restorePosition();
        long available = this.input.length() - this.input.getPosition();
        if (available > 2147483647L) {
            return WinNT.MAXLONG;
        }
        return (int) available;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        restorePosition();
        if (this.input.isEOF()) {
            return -1;
        }
        int b = this.input.read();
        if (b != -1) {
            this.position++;
        } else {
            LOG.error("read() returns -1, assumed position: " + this.position + ", actual position: " + this.input.getPosition());
        }
        return b;
    }

    @Override // java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        restorePosition();
        if (this.input.isEOF()) {
            return -1;
        }
        int n = this.input.read(b, off, len);
        if (n != -1) {
            this.position += (long) n;
        } else {
            LOG.error("read() returns -1, assumed position: " + this.position + ", actual position: " + this.input.getPosition());
        }
        return n;
    }

    @Override // java.io.InputStream
    public long skip(long n) throws IOException {
        restorePosition();
        this.input.seek(this.position + n);
        this.position += n;
        return n;
    }
}
