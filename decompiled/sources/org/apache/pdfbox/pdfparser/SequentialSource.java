package org.apache.pdfbox.pdfparser;

import java.io.Closeable;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdfparser/SequentialSource.class */
interface SequentialSource extends Closeable {
    int read() throws IOException;

    int read(byte[] bArr) throws IOException;

    int read(byte[] bArr, int i, int i2) throws IOException;

    long getPosition() throws IOException;

    int peek() throws IOException;

    void unread(int i) throws IOException;

    void unread(byte[] bArr) throws IOException;

    void unread(byte[] bArr, int i, int i2) throws IOException;

    byte[] readFully(int i) throws IOException;

    boolean isEOF() throws IOException;
}
