package com.twelvemonkeys.io;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/Seekable.class */
public interface Seekable {
    long getStreamPosition() throws IOException;

    void seek(long j) throws IOException;

    void mark();

    void reset() throws IOException;

    void flushBefore(long j) throws IOException;

    void flush() throws IOException;

    long getFlushedPosition() throws IOException;

    boolean isCached();

    boolean isCachedMemory();

    boolean isCachedFile();

    void close() throws IOException;
}
