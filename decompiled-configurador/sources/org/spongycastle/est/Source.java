package org.spongycastle.est;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/est/Source.class */
public interface Source<T> {
    InputStream getInputStream() throws IOException;

    OutputStream getOutputStream() throws IOException;

    T getSession();

    void close() throws IOException;
}
