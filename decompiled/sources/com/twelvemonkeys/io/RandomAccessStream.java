package com.twelvemonkeys.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/RandomAccessStream.class */
public abstract class RandomAccessStream implements Seekable, DataInput, DataOutput {
    SeekableInputStream inputView = null;
    SeekableOutputStream outputView = null;

    public int read() throws IOException {
        try {
            return readByte() & 255;
        } catch (EOFException e) {
            return -1;
        }
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        if (bArr == null) {
            throw new NullPointerException("bytes == null");
        }
        if (i < 0 || i > bArr.length || i2 < 0 || i + i2 > bArr.length || i + i2 < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (i2 == 0) {
            return 0;
        }
        int i3 = read();
        if (i3 == -1) {
            return -1;
        }
        bArr[i] = (byte) i3;
        int i4 = 1;
        while (i4 < i2) {
            try {
                int i5 = read();
                if (i5 == -1) {
                    break;
                }
                bArr[i + i4] = (byte) i5;
                i4++;
            } catch (IOException e) {
            }
        }
        return i4;
    }

    public final int read(byte[] bArr) throws IOException {
        return read(bArr, 0, bArr != null ? bArr.length : 1);
    }

    public final SeekableInputStream asInputStream() {
        if (this.inputView == null) {
            this.inputView = new InputStreamView(this);
        }
        return this.inputView;
    }

    public final SeekableOutputStream asOutputStream() {
        if (this.outputView == null) {
            this.outputView = new OutputStreamView(this);
        }
        return this.outputView;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/RandomAccessStream$InputStreamView.class */
    static final class InputStreamView extends SeekableInputStream {
        private final RandomAccessStream mStream;

        public InputStreamView(RandomAccessStream randomAccessStream) {
            if (randomAccessStream == null) {
                throw new IllegalArgumentException("stream == null");
            }
            this.mStream = randomAccessStream;
        }

        @Override // com.twelvemonkeys.io.Seekable
        public boolean isCached() {
            return this.mStream.isCached();
        }

        @Override // com.twelvemonkeys.io.Seekable
        public boolean isCachedFile() {
            return this.mStream.isCachedFile();
        }

        @Override // com.twelvemonkeys.io.Seekable
        public boolean isCachedMemory() {
            return this.mStream.isCachedMemory();
        }

        @Override // com.twelvemonkeys.io.SeekableInputStream
        protected void closeImpl() throws IOException {
            this.mStream.close();
        }

        @Override // com.twelvemonkeys.io.SeekableInputStream
        protected void flushBeforeImpl(long j) throws IOException {
            this.mStream.flushBefore(j);
        }

        @Override // com.twelvemonkeys.io.SeekableInputStream
        protected void seekImpl(long j) throws IOException {
            this.mStream.seek(j);
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            return this.mStream.read();
        }

        @Override // java.io.InputStream
        public int read(byte[] bArr, int i, int i2) throws IOException {
            return this.mStream.read(bArr, i, i2);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/RandomAccessStream$OutputStreamView.class */
    static final class OutputStreamView extends SeekableOutputStream {
        private final RandomAccessStream mStream;

        public OutputStreamView(RandomAccessStream randomAccessStream) {
            if (randomAccessStream == null) {
                throw new IllegalArgumentException("stream == null");
            }
            this.mStream = randomAccessStream;
        }

        @Override // com.twelvemonkeys.io.Seekable
        public boolean isCached() {
            return this.mStream.isCached();
        }

        @Override // com.twelvemonkeys.io.Seekable
        public boolean isCachedFile() {
            return this.mStream.isCachedFile();
        }

        @Override // com.twelvemonkeys.io.Seekable
        public boolean isCachedMemory() {
            return this.mStream.isCachedMemory();
        }

        @Override // com.twelvemonkeys.io.SeekableOutputStream
        protected void closeImpl() throws IOException {
            this.mStream.close();
        }

        @Override // com.twelvemonkeys.io.SeekableOutputStream
        protected void flushBeforeImpl(long j) throws IOException {
            this.mStream.flushBefore(j);
        }

        @Override // com.twelvemonkeys.io.SeekableOutputStream
        protected void seekImpl(long j) throws IOException {
            this.mStream.seek(j);
        }

        @Override // java.io.OutputStream
        public void write(int i) throws IOException {
            this.mStream.write(i);
        }

        @Override // java.io.OutputStream
        public void write(byte[] bArr, int i, int i2) throws IOException {
            this.mStream.write(bArr, i, i2);
        }
    }
}
