package org.codehaus.stax2.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/io/Stax2ByteArraySource.class */
public class Stax2ByteArraySource extends Stax2BlockSource {
    private static final String DEFAULT_ENCODING = "UTF-8";
    final byte[] mBuffer;
    final int mStart;
    final int mLength;

    public Stax2ByteArraySource(byte[] buf, int start, int len) {
        this.mBuffer = buf;
        this.mStart = start;
        this.mLength = len;
    }

    @Override // org.codehaus.stax2.io.Stax2BlockSource, org.codehaus.stax2.io.Stax2Source
    public Reader constructReader() throws IOException {
        String enc = getEncoding();
        InputStream in = constructInputStream();
        if (enc == null || enc.length() == 0) {
            enc = "UTF-8";
        }
        return new InputStreamReader(in, enc);
    }

    @Override // org.codehaus.stax2.io.Stax2BlockSource, org.codehaus.stax2.io.Stax2Source
    public InputStream constructInputStream() throws IOException {
        return new ByteArrayInputStream(this.mBuffer, this.mStart, this.mLength);
    }

    public byte[] getBuffer() {
        return this.mBuffer;
    }

    public int getBufferStart() {
        return this.mStart;
    }

    public int getBufferLength() {
        return this.mLength;
    }

    public int getBufferEnd() {
        int end = this.mStart;
        if (this.mLength > 0) {
            end += this.mLength;
        }
        return end;
    }
}
