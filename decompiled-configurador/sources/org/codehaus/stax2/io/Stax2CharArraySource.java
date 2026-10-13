package org.codehaus.stax2.io;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/io/Stax2CharArraySource.class */
public class Stax2CharArraySource extends Stax2BlockSource {
    final char[] mBuffer;
    final int mStart;
    final int mLength;

    public Stax2CharArraySource(char[] buf, int start, int len) {
        this.mBuffer = buf;
        this.mStart = start;
        this.mLength = len;
    }

    @Override // org.codehaus.stax2.io.Stax2BlockSource, org.codehaus.stax2.io.Stax2Source
    public Reader constructReader() throws IOException {
        return new CharArrayReader(this.mBuffer, this.mStart, this.mLength);
    }

    @Override // org.codehaus.stax2.io.Stax2BlockSource, org.codehaus.stax2.io.Stax2Source
    public InputStream constructInputStream() throws IOException {
        return null;
    }

    public char[] getBuffer() {
        return this.mBuffer;
    }

    public int getBufferStart() {
        return this.mStart;
    }

    public int getBufferLength() {
        return this.mLength;
    }
}
