package com.twelvemonkeys.io;

import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/StringArrayReader.class */
public class StringArrayReader extends StringReader {
    private StringReader current;
    private String[] strings;
    protected final Object finalLock;
    private int currentSting;
    private int markedString;
    private long mark;
    private long next;

    public StringArrayReader(String[] strArr) {
        super("");
        Validate.notNull(strArr, "strings");
        this.lock = strArr;
        this.finalLock = strArr;
        this.strings = (String[]) strArr.clone();
        nextReader();
    }

    protected final Reader nextReader() {
        if (this.currentSting >= this.strings.length) {
            this.current = new EmptyReader();
        } else {
            String[] strArr = this.strings;
            int i = this.currentSting;
            this.currentSting = i + 1;
            this.current = new StringReader(strArr[i]);
        }
        this.next = 0L;
        return this.current;
    }

    protected final void ensureOpen() throws IOException {
        if (this.strings == null) {
            throw new IOException("Stream closed");
        }
    }

    @Override // java.io.StringReader, java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        super.close();
        this.strings = null;
        this.current.close();
    }

    @Override // java.io.StringReader, java.io.Reader
    public void mark(int i) throws IOException {
        if (i < 0) {
            throw new IllegalArgumentException("Read limit < 0");
        }
        synchronized (this.finalLock) {
            ensureOpen();
            this.mark = this.next;
            this.markedString = this.currentSting;
            this.current.mark(i);
        }
    }

    @Override // java.io.StringReader, java.io.Reader
    public void reset() throws IOException {
        synchronized (this.finalLock) {
            ensureOpen();
            if (this.currentSting != this.markedString) {
                this.currentSting = this.markedString - 1;
                nextReader();
                this.current.skip(this.mark);
            } else {
                this.current.reset();
            }
            this.next = this.mark;
        }
    }

    @Override // java.io.StringReader, java.io.Reader
    public boolean markSupported() {
        return true;
    }

    @Override // java.io.StringReader, java.io.Reader
    public int read() throws IOException {
        synchronized (this.finalLock) {
            int i = this.current.read();
            if (i < 0 && this.currentSting < this.strings.length) {
                nextReader();
                return read();
            }
            this.next++;
            return i;
        }
    }

    @Override // java.io.StringReader, java.io.Reader
    public int read(char[] cArr, int i, int i2) throws IOException {
        synchronized (this.finalLock) {
            int i3 = this.current.read(cArr, i, i2);
            if (i3 < 0 && this.currentSting < this.strings.length) {
                nextReader();
                return read(cArr, i, i2);
            }
            this.next += (long) i3;
            return i3;
        }
    }

    @Override // java.io.StringReader, java.io.Reader
    public boolean ready() throws IOException {
        return this.current.ready();
    }

    @Override // java.io.StringReader, java.io.Reader
    public long skip(long j) throws IOException {
        synchronized (this.finalLock) {
            long jSkip = this.current.skip(j);
            if (jSkip == 0 && this.currentSting < this.strings.length) {
                nextReader();
                return skip(j);
            }
            this.next += jSkip;
            return jSkip;
        }
    }
}
