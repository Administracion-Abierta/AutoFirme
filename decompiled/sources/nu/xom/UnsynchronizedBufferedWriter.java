package nu.xom;

import java.io.IOException;
import java.io.Writer;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/UnsynchronizedBufferedWriter.class */
final class UnsynchronizedBufferedWriter extends Writer {
    private static final int CAPACITY = 8192;
    private char[] buffer = new char[8192];
    private int position = 0;
    private Writer out;

    public UnsynchronizedBufferedWriter(Writer writer) {
        this.out = writer;
    }

    @Override // java.io.Writer
    public void write(char[] cArr, int i, int i2) throws IOException {
        throw new UnsupportedOperationException("XOM bug: this statement shouldn't be reachable.");
    }

    @Override // java.io.Writer
    public void write(String str) throws IOException {
        write(str, 0, str.length());
    }

    @Override // java.io.Writer
    public void write(String str, int i, int i2) throws IOException {
        while (i2 > 0) {
            int i3 = 8192 - this.position;
            if (i2 < i3) {
                i3 = i2;
            }
            str.getChars(i, i + i3, this.buffer, this.position);
            this.position += i3;
            i += i3;
            i2 -= i3;
            if (this.position >= 8192) {
                flushInternal();
            }
        }
    }

    @Override // java.io.Writer
    public void write(int i) throws IOException {
        if (this.position >= 8192) {
            flushInternal();
        }
        this.buffer[this.position] = (char) i;
        this.position++;
    }

    @Override // java.io.Writer, java.io.Flushable
    public void flush() throws IOException {
        flushInternal();
        this.out.flush();
    }

    private void flushInternal() throws IOException {
        if (this.position != 0) {
            this.out.write(this.buffer, 0, this.position);
            this.position = 0;
        }
    }

    @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        throw new UnsupportedOperationException("How'd we get here?");
    }
}
