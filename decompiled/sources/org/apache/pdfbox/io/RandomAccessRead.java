package org.apache.pdfbox.io;

import java.io.Closeable;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/io/RandomAccessRead.class */
public interface RandomAccessRead extends Closeable {
    int read() throws IOException;

    int read(byte[] bArr) throws IOException;

    int read(byte[] bArr, int i, int i2) throws IOException;

    long getPosition() throws IOException;

    void seek(long j) throws IOException;

    long length() throws IOException;

    boolean isClosed();

    int peek() throws IOException;

    void rewind(int i) throws IOException;

    byte[] readFully(int i) throws IOException;

    boolean isEOF() throws IOException;

    int available() throws IOException;
}
