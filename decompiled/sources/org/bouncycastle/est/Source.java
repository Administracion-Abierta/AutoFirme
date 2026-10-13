package org.bouncycastle.est;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/est/Source.class */
public interface Source<T> {
    InputStream getInputStream() throws IOException;

    OutputStream getOutputStream() throws IOException;

    T getSession();

    void close() throws IOException;
}
