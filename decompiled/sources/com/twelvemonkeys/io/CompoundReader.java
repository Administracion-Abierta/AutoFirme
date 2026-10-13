package com.twelvemonkeys.io;

import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/CompoundReader.class */
public class CompoundReader extends Reader {
    private Reader current;
    private List<Reader> readers;
    protected final Object finalLock;
    protected final boolean markSupported;
    private int currentReader;
    private int markedReader;
    private long mark;
    private long next;

    public CompoundReader(Iterator<Reader> it) {
        super(Validate.notNull(it, "readers"));
        this.finalLock = it;
        this.readers = new ArrayList();
        boolean z = true;
        while (true) {
            boolean z2 = z;
            if (it.hasNext()) {
                Reader next = it.next();
                if (next == null) {
                    throw new NullPointerException("readers cannot contain null-elements");
                }
                this.readers.add(next);
                z = z2 && next.markSupported();
            } else {
                this.markSupported = z2;
                this.current = nextReader();
                return;
            }
        }
    }

    protected final Reader nextReader() {
        if (this.currentReader >= this.readers.size()) {
            this.current = new EmptyReader();
        } else {
            List<Reader> list = this.readers;
            int i = this.currentReader;
            this.currentReader = i + 1;
            this.current = list.get(i);
        }
        this.next = 0L;
        return this.current;
    }

    protected final void ensureOpen() throws IOException {
        if (this.readers == null) {
            throw new IOException("Stream closed");
        }
    }

    @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        Iterator<Reader> it = this.readers.iterator();
        while (it.hasNext()) {
            it.next().close();
        }
        this.readers = null;
    }

    @Override // java.io.Reader
    public void mark(int i) throws IOException {
        if (i < 0) {
            throw new IllegalArgumentException("Read limit < 0");
        }
        synchronized (this.finalLock) {
            ensureOpen();
            this.mark = this.next;
            this.markedReader = this.currentReader;
            this.current.mark(i);
        }
    }

    @Override // java.io.Reader
    public void reset() throws IOException {
        synchronized (this.finalLock) {
            ensureOpen();
            if (this.currentReader != this.markedReader) {
                for (int i = this.currentReader; i >= this.markedReader; i--) {
                    this.readers.get(i).reset();
                }
                this.currentReader = this.markedReader - 1;
                nextReader();
            }
            this.current.reset();
            this.next = this.mark;
        }
    }

    @Override // java.io.Reader
    public boolean markSupported() {
        return this.markSupported;
    }

    @Override // java.io.Reader
    public int read() throws IOException {
        synchronized (this.finalLock) {
            int i = this.current.read();
            if (i < 0 && this.currentReader < this.readers.size()) {
                nextReader();
                return read();
            }
            this.next++;
            return i;
        }
    }

    @Override // java.io.Reader
    public int read(char[] cArr, int i, int i2) throws IOException {
        synchronized (this.finalLock) {
            int i3 = this.current.read(cArr, i, i2);
            if (i3 < 0 && this.currentReader < this.readers.size()) {
                nextReader();
                return read(cArr, i, i2);
            }
            this.next += (long) i3;
            return i3;
        }
    }

    @Override // java.io.Reader
    public boolean ready() throws IOException {
        return this.current.ready();
    }

    @Override // java.io.Reader
    public long skip(long j) throws IOException {
        synchronized (this.finalLock) {
            long jSkip = this.current.skip(j);
            if (jSkip == 0 && this.currentReader < this.readers.size()) {
                nextReader();
                return skip(j);
            }
            this.next += jSkip;
            return jSkip;
        }
    }
}
